package com.ecommerce.store.dto;

public record PaymentVerificationRequest(
    String razorpayOrderId,
    String razorpayPaymentId,
    String razorpaySignature
) {}