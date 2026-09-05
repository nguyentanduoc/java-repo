package com.ntd.spingddd.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.ntd.spingddd.domain.model.OutboxEvent;
import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO;

public interface OutboxEventRepository {
  public void save(OutboxEvent event);

  public List<OutboxEventDO> findPending(int limit);

  public void markPublished(Long id, LocalDateTime now);
}
