package com.ecommerce.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

import com.ecommerce.store.service.SseService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/v1/notifications")
@RequiredArgsConstructor 
public class NotificationController {
    private final SseService sseService;

    @GetMapping (value = "/subscribe/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
        @PathVariable String email
    ) {
        return sseService.subscribe(email);
    }
}