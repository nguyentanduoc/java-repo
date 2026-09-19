package com.ntd.spingddd.unit.controller;

import com.ntd.spingddd.application.service.OrderService;
import com.ntd.spingddd.e2e.AbstractE2ETest;
import com.ntd.spingddd.interfaces.ov.order.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;

class OrderControllerTest extends AbstractE2ETest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

        @Test
    void createOrder_success() {
        OrderRequest request = new OrderRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/order/createOrder")
        .then()
            .statusCode(200);
    }
}

