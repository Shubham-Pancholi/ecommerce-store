package com.ecommerce.store.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service 
public class SseService {
    
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userEmail) {
        SseEmitter emitter = new SseEmitter(3600000L);
        emitters.put(userEmail, emitter);

        emitter.onCompletion(() -> emitters.remove(userEmail));
        emitter.onTimeout(() -> emitters.remove(userEmail));
        emitter.onError(error -> emitters.remove(userEmail));

        return emitter;
    }

    public void pushNotificationToUser(String userEmail, String message) {
        SseEmitter emitter = emitters.get(userEmail);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("order-update").data(message));
            }   catch (IOException exception) {
                emitters.remove(userEmail);
            }
        }
    }
}