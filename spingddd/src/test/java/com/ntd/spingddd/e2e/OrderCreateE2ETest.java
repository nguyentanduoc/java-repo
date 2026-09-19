package com.ntd.spingddd.e2e;

import com.ntd.spingddd.infrastructure.jpa.InventoryJpa;
import com.ntd.spingddd.infrastructure.jpa.ProductJpa;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryDO;
import com.ntd.spingddd.infrastructure.repository.product.ProductDO;
import com.ntd.spingddd.interfaces.ov.order.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderCreateE2ETest extends AbstractE2ETest {

    @LocalServerPort private int port;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private ProductJpa productJpa;
    @Autowired private com.ntd.spingddd.application.service.order.OrderConsummer orderConsummer;
    @Autowired private com.ntd.spingddd.infrastructure.jpa.OutboxEventJpa outboxJpa;
        // Mock the scheduled publisher so it never races with the manual Awaitility bridge
    // or tries to connect a dummy KafkaTemplate ("dummy-bootstrap-server").
    @MockitoBean
    private com.ntd.spingddd.application.cronjob.OutboxPublisherJob outboxPublisher;

    @Autowired private InventoryJpa inventoryJpa;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        jdbcTemplate.execute("TRUNCATE TABLE orders, outbox_event, inventory, product RESTART IDENTITY CASCADE");
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }


    @Test
    void productNotFound_returns404() {
        OrderRequest request = new OrderRequest();
        request.setProductId(9999L);
        request.setQuantity(1);

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/order/createOrder")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Happy Path: Tạo đơn hàng thành công và cập nhật tồn kho")
    void happyPath_createOrder_success() {
        ProductDO product = productJpa.save(ProductDO.builder().name("Laptop").price(new BigDecimal("1000.00")).build());
        Long productId = product.getId();
        inventoryJpa.save(InventoryDO.builder().productId(productId).availableQuantity(100).build());
        redisTemplate.opsForValue().set("inventory:product:" + productId, "100");

        OrderRequest request = new OrderRequest();
        request.setProductId(productId);
        request.setQuantity(5);

        given().contentType(ContentType.JSON).body(request).post("/order/createOrder")
               .then().statusCode(HttpStatus.OK.value());

        Awaitility.await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            List<com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO> pendingEvents = outboxJpa.findPending(org.springframework.data.domain.PageRequest.of(0, 1));
            if (!pendingEvents.isEmpty()) {
                com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO event = pendingEvents.get(0);
                com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage msg = com.alibaba.fastjson2.JSON.parseObject(event.getPayload(), com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage.class);
                orderConsummer.processOrder(msg);
                outboxJpa.update(java.time.LocalDateTime.now(), event.getId());
            }

            Integer status = jdbcTemplate.queryForObject("SELECT status FROM orders LIMIT 1", Integer.class);
            assertThat(status).isEqualTo(1);
            Integer qty = inventoryJpa.findByProductId(productId).get().getAvailableQuantity();
            assertThat(qty).isEqualTo(95);
        });
    }

    @Test
    @DisplayName("Concurrency: Đảm bảo không bán vượt số lượng tồn kho")
    void concurrentOrders_noOversell() throws Exception {
        int initialStock = 10;
        ProductDO product = productJpa.save(ProductDO.builder().name("Book").price(new BigDecimal("10.00")).build());
        Long productId = product.getId();
        inventoryJpa.save(InventoryDO.builder().productId(productId).availableQuantity(initialStock).build());
        redisTemplate.opsForValue().set("inventory:product:" + productId, String.valueOf(initialStock));

        int concurrentRequests = 20;
        var executor = Executors.newFixedThreadPool(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < concurrentRequests; i++) {
            executor.submit(() -> {
                OrderRequest request = new OrderRequest();
                request.setProductId(productId);
                request.setQuantity(1);
                int status = given().contentType(ContentType.JSON)
                    .body(request)
                    .post("/order/createOrder").getStatusCode();
                if (status == 200) successCount.incrementAndGet();
            });
        }
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        assertThat(successCount.get()).isEqualTo(initialStock);
    }
}
