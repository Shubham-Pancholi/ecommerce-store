package com.ecommerce.store.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service 
public class NotificationService {
    
    @KafkaListener (topics = "order-notifications", groupId = "ecommerce-notification-group")
    public void handleOrderNotification(String message) {
        System.out.println("\n=======================================================");
        System.out.println("KAFKA CONSUMER TRIGGERED!");
        System.out.println("Processing background task...");
        System.out.println("Message received: " + message);
        System.out.println("Simulating 3-second email send...");
        
        try {
            Thread.sleep(3000); // Simulate a slow email API like SendGrid
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Email sent successfully!");
        System.out.println("=======================================================\n");
    }
}