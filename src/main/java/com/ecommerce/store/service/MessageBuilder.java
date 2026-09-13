package com.ecommerce.store.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Scope ("prototype") 
@Getter 
public class MessageBuilder {
    
    private String recipient;
    private String subject;
    private String body;

    public MessageBuilder to(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public MessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageBuilder buildOrderConfirmation(String orderNumber) {
        this.body = "<h1>Thank you for your order!</h1><p>Your order number is: " + orderNumber + "</p>";
        return this;
    }
}