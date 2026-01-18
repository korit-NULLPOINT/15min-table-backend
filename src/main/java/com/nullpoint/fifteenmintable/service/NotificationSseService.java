package com.nullpoint.fifteenmintable.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSseService {

    // userId -> emitter
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Integer userId) {
        // 0 = 무제한, 보통 30분~1시간 추천. (프론트에서 재연결)
        long timeoutMs = 60L * 60L * 1000L;

        SseEmitter emitter = new SseEmitter(timeoutMs);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // 연결 확인용 더미 이벤트(옵션)
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void pushToUser(Integer userId, Object payload) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name("notification").data(payload));
        } catch (IOException e) {
            emitters.remove(userId);
        }
    }

    // (옵션) 프론트가 오래 idle이면 끊기는 경우가 있어서 heartbeat 주기적으로 보내기도 함
    public void heartbeatAll() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        });
    }
}
