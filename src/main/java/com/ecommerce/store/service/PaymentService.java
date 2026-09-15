package com.ecommerce.store.service;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service 
public class PaymentService {

    @Value ("${razorpay.api.key}")
    private String apiKey;

    @Value ("${razorpay.api.secret}")
    private String apiSecret;

    public String createRazorpayOrder(BigDecimal amount, String receiptId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
        JSONObject orderRequest = new JSONObject();

        int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        return razorpayOrder.get("id");
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();

            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, apiSecret);

        }   catch (RazorpayException e) {
            return false;
        }
    }

    public void issueRefund(String paymentId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("payment_id", paymentId);

        razorpayClient.refunds.create(refundRequest);
    }
}