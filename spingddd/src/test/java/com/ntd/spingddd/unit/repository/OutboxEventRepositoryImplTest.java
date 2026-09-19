package com.ntd.spingddd.unit.repository;

import com.ntd.spingddd.domain.model.OutboxEvent;
import com.ntd.spingddd.infrastructure.jpa.OutboxEventJpa;
import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventRepositoryImplTest {

    @Mock
    private OutboxEventJpa eventJpa;

    @InjectMocks
    private OutboxEventRepositoryImpl repository;

    @Test
    void save_callsJpa() {
        OutboxEvent event = OutboxEvent.builder().id(1L).build();
        repository.save(event);
        verify(eventJpa, times(1)).save(any());
    }

    @Test
    void findPending_callsJpa() {
        repository.findPending(10);
        verify(eventJpa).findPending(PageRequest.of(0, 10));
    }

    @Test
    void markPublished_callsJpa() {
        LocalDateTime now = LocalDateTime.now();
        repository.markPublished(1L, now);
        verify(eventJpa).update(now, 1L);
    }
}
