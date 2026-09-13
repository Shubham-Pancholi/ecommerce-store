package com.ecommerce.store.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ecommerce.store.entity.OrderPlacedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class NotificationService {

    private final ObjectProvider<MessageBuilder> messageBuilderProvider;
    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @KafkaListener (topics = "order-notifications", groupId = "ecommerce-notification-group")
    public void handleOrderNotification(String message) {
        try {
            OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);

            MessageBuilder emailBuilder = messageBuilderProvider.getObject();

            emailBuilder.to(event.userEmail())
                        .subject("Order Confirmation: " + event.orderNumber())
                        .buildOrderConfirmation(event.orderNumber());
            
            System.out.println("Ready to send mail to: " + emailBuilder.getRecipient());

            RestClient restClient = RestClient.create();

            try {
                String response = restClient.post()
                                            .uri("https://jsonplaceholder.typicode.com/posts")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(emailBuilder)
                                            .retrieve()
                                            .body(String.class);

            System.out.println("Third Party API response: \n" + response);

            }   catch (Exception exception) {
                System.err.println("Failed to reach Third-Party API: " + exception.getMessage());
            }

            sseService.pushNotificationToUser(
                event.userEmail(),
                "Your Order, numbered, " +  event.orderNumber() + ", has been confirmed and processed."
            );

        }   catch (Exception exception) {
            System.err.println("Failed to parse Kafka message: " + exception.getMessage());
        }
    }
}