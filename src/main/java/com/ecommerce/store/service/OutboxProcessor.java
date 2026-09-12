package com.ecommerce.store.service;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.entity.OutboxMessage;
import com.ecommerce.store.repository.OutboxMessageRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class OutboxProcessor {
    
    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled (fixedDelay = 5000)
    @Transactional 
    public void processOutboxMessages() {
        
        List<OutboxMessage> pendingMessages = outboxMessageRepository.findByStatus("PENDING");

        for (OutboxMessage message : pendingMessages) {
            try {
                kafkaTemplate.send(message.getTopic(), message.getPayload());

                message.setStatus("PROCESSED");
                outboxMessageRepository.save(message);

            }   catch (Exception e) {
                System.err.println("Failed to send message ID:" + message.getId() + ". Will retry.");
            }
        }
    }
}