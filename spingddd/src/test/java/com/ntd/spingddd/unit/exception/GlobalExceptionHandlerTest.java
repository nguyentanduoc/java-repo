package com.ntd.spingddd.unit.exception;

import com.ntd.spingddd.application.exception.BadRequestException;
import com.ntd.spingddd.application.exception.NotfoundException;
import com.ntd.spingddd.infrastructure.config.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest() {
        ResponseEntity<String> response = handler.handleBadRequest(new BadRequestException("error"));
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handleNotFound() {
        ResponseEntity<String> response = handler.handleNotFound(new NotfoundException("error"));
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}