package com.fbp.engine.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {
    private final String id;
    private final Map<String, Object> payload;
    private final long timestamp;

    public Message(Map<String, Object> payload) {
        this.id = UUID.randomUUID().toString();
        // 1. 방어적 복사 및 불변 맵 생성 (외부에서의 수정을 차단)
        this.payload = Collections.unmodifiableMap(new HashMap<>(payload));
        this.timestamp = System.currentTimeMillis();
    }

    private Message(String id, Map<String, Object> payload, long timestamp) {
        this.id = id;
        this.payload = Collections.unmodifiableMap(new HashMap<>(payload));
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    // 2. 제네릭 메서드: 꺼낼 때 자동으로 형변환 (Casting) 해줌
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) payload.get(key);
    }
    // 과제 2-3: 추가 메서드
    public Message withEntry(String key, Object value) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.put(key, value);
        return new Message(newPayload);
    }

    public boolean hasKey(String key) {
        return payload.containsKey(key);
    }

    public Message withoutKey(String key) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.remove(key);
        return new Message(newPayload);
    }

    @Override
    public String toString() {
        return "Message{id='" + id + "', payload=" + payload + ", timestamp=" + timestamp + "}";
    }
}