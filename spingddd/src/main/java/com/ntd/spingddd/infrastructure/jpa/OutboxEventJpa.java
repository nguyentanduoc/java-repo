package com.ntd.spingddd.infrastructure.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO;

public interface OutboxEventJpa extends JpaRepository<OutboxEventDO, Long> {

  @Query("SELECT e FROM OutboxEventDO e WHERE e.status = 0 ORDER BY e.createdAt ASC")
  List<OutboxEventDO> findPending(Pageable pageable);

  @Modifying // Bắt buộc phải có đối với câu lệnh UPDATE/DELETE
  @Query("UPDATE OutboxEventDO o SET o.status = 1, o.publishedAt = :publishedAt WHERE o.id = :id")
  int update(LocalDateTime publishedAt, Long id);
}
