# Skill: Testing (Java + Spring Boot)

> Quy trình kiểm thử (Testing) cho dự án `spingddd`.

## 1. Tech Stack Thực tế
- **Framework Test:** JUnit 5 (`useJUnitPlatform()`).
- **Assertion:** `spring-boot-starter-test` (AssertJ).
- **REST API Testing:** REST Assured (`given().post(...).then()`).
- **Async Wait:** Awaitility (`Awaitility.await().atMost(...)`).
- **Mocking Bean:** `@MockitoBean` (Spring Boot Test).
- **E2E Infrastructure:** Kết nối trực tiếp tới PostgreSQL & Redis qua `localhost` (container `docker-compose` đang chạy); Kafka listener được **disable** trong test.

## 2. Chiến lược Test
Dự án áp dụng **kiểu test E2E thực sự (integration-style)** được tổ chức trong package `com.ntd.spingddd.e2e`:

- **`AbstractE2ETest` (Base class):**
    - Dùng `@SpringBootTest(webEnvironment = RANDOM_PORT)` để khởi động toàn bộ context.
    - Dùng `@ActiveProfiles("test")` để áp dụng `application-test.yml`.
    - `@DynamicPropertySource` để override các cấu hình DB/Redis/Kafka về địa chỉ `localhost` (15432/16379/19094).
    - **Disable Kafka listener:** `spring.kafka.listener.auto-startup=false` để tránh `@KafkaListener` kết nối tới broker ảo `dummy`.
- **Test flow đặt hàng thành công (`OrderCreateE2ETest`):**
    - Mock `OutboxPublisherJob` bằng `@MockitoBean` để tránh kết nối tới Kafka.
    - Sử dụng `Awaitility` để **giả lập** luồng bất đồng bộ: đón bắt sự kiện Outbox, gọi `OrderConsummer.processOrder(msg)` thủ công, sau đó đánh dấu sự kiện đã gửi.
    - Chứng minh trạng thái `orders.status` thay đổi từ 0 → 1 và `inventory.available_quantity` được giảm đúng.
- **Test đồng thời (Concurrency / No Oversell):**
    - Gửi 20 đơn hàng đồng thời (`executor`) cho một sản phẩm có `initialStock = 10`.
    - Chứng minh **chính xác** số lượng đơn hàng thành công bằng `initialStock` (10).
- **Test Idempotency (`OrderConsumerIdempotencyTest`):**
    - Gửi cùng một message Kafka (`PlaceOrderMQMessage`) 2 lần liên tiếp tới `OrderConsummer.processOrder()`.
    - Chứng minh rằng tồn kho **chỉ bị trừ 1 lần** (không oversell).

## 3. Cấu trúc Test
```
src/test/java/com/ntd/spingddd/
 ├── SpingdddApplicationTests.java         # Smoke test
 └── e2e/
     ├── AbstractE2ETest.java              # Base class, dynamic properties
     ├── OrderCreateE2ETest.java           # Test flow + concurrency
     ├── OrderConsumerIdempotencyTest.java # Test consumer idempotent
     └── TestKafkaConfig.java              # Override Kafka producer cho test
src/test/resources/application-test.yml     # Test config (create-drop, dummy kafka)
```

## 4. Cách Chạy
- **Toàn bộ:** `./gradlew test`
- **Một test cụ thể:** `./gradlew --tests OrderCreateE2ETest`
- **Báo coverage:** `./gradlew jacocoTestReport`

## 5. Quy Tắc Ràng buộc
- **Bắt buộc:** Coverage **100%** instructions (`minimum = 1.0`).
- Không `@Disabled`, không skip coverage để vượt qua CI/CD.
