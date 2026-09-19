package com.ntd.spingddd.e2e;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.TimeZone;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(TestKafkaConfig.class)
public abstract class AbstractE2ETest {

    static {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Trỏ trực tiếp vào các container đang chạy qua docker-compose
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:15432/postgres?options=-c%20timezone=UTC");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
                                        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.password", () -> "123456a");

        registry.add("spring.data.redis.port", () -> "16379");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:19094");
        // The E2E tests simulate the async flow manually (no real Kafka broker).
        // Disable Kafka listener container auto-startup so OrderConsummer's
        // @KafkaListener does not attempt to connect to a broker at startup.
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }
}

