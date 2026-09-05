package com.ntd.spingddd.application.cronjob;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.ntd.spingddd.domain.repository.OutboxEventRepository;
import com.ntd.spingddd.infrastructure.mq.KafkaOrderProducer;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;
import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisherJob {
  private static final int BATCH_SIZE = 500;

  private final OutboxEventRepository eventRepository;
  private final KafkaOrderProducer kafkaOrderProducer;

  @Scheduled(fixedDelay = 1000)
  public void publish() {
    List<OutboxEventDO> outboxEvents = eventRepository.findPending(BATCH_SIZE);
    for (OutboxEventDO outboxEventDO : outboxEvents) {
      try {
        PlaceOrderMQMessage message = JSON.parseObject(outboxEventDO.getPayload(), PlaceOrderMQMessage.class);
        kafkaOrderProducer.sendAndAwaitAck(message);
        eventRepository.markPublished(outboxEventDO.getId(), LocalDateTime.now());

      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }

    }
  }
}
