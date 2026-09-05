package com.ntd.spingddd.infrastructure.repository.outboxEvent;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.ntd.spingddd.domain.model.OutboxEvent;
import com.ntd.spingddd.domain.repository.OutboxEventRepository;
import com.ntd.spingddd.infrastructure.jpa.OutboxEventJpa;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

  private final OutboxEventJpa eventJpa;

  @Override
  public void save(OutboxEvent event) {
    eventJpa.save(OutboxEventConverter.toOutboxEventDO(event));
  }

  @Override
  public List<OutboxEventDO> findPending(int limit) {
    return eventJpa.findPending(PageRequest.of(0, limit));
  }

  @Override
  public void markPublished(Long id, LocalDateTime now) {
    eventJpa.update(now, id);
  }

}
