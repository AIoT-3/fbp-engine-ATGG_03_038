package com.fbp.engine.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test @DisplayName("1. 생성 시 ID 자동 할당")
    void testIdGeneration() {
        Message msg = new Message(Map.of());
        assertNotNull(msg.getId());
        assertFalse(msg.getId().isEmpty());
    }

    @Test @DisplayName("2. 생성 시 timestamp 자동 기록")
    void testTimestamp() {
        Message msg = new Message(Map.of());
        assertTrue(msg.getTimestamp() > 0);
    }

    @Test @DisplayName("3, 4, 5. 페이로드 조회 및 제네릭 타입 캐스팅")
    void testPayloadGet() {
        Message msg = new Message(Map.of("temperature", 25.5));
        Double temp = msg.get("temperature"); // 캐스팅 필요 없음
        assertEquals(25.5, temp);
        assertNull(msg.get("non-existent"));
    }

    @Test @DisplayName("6. 페이로드 불변 - 외부 수정 차단")
    void testImmutabilityPut() {
        Message msg = new Message(Map.of("key", "value"));
        assertThrows(UnsupportedOperationException.class, () -> {
            msg.getPayload().put("newKey", "newValue");
        });
    }

    @Test @DisplayName("7. 페이로드 불변 - 원본 Map 수정 무영향")
    void testImmutabilityOriginalMap() {
        Map<String, Object> original = new HashMap<>();
        original.put("key", "value");
        Message msg = new Message(original);
        original.put("key", "hacked");
        assertEquals("value", msg.get("key"));
    }

    @Test @DisplayName("8, 9, 10. withEntry 검증")
    void testWithEntry() {
        Message msg = new Message(Map.of("a", 1));
        Message newMsg = msg.withEntry("b", 2);

        assertNotSame(msg, newMsg);
        assertFalse(msg.hasKey("b"));
        assertEquals(2, (Integer) newMsg.get("b"));
    }

    @Test @DisplayName("11, 12. hasKey 검증")
    void testHasKey() {
        Message msg = new Message(Map.of("a", 1));
        assertTrue(msg.hasKey("a"));
        assertFalse(msg.hasKey("b"));
    }

    @Test @DisplayName("13, 14. withoutKey 검증")
    void testWithoutKey() {
        Message msg = new Message(Map.of("a", 1, "b", 2));
        Message newMsg = msg.withoutKey("a");

        assertFalse(newMsg.hasKey("a"));
        assertTrue(msg.hasKey("a"));
    }

    @Test @DisplayName("15. toString 포맷 확인")
    void testToString() {
        Message msg = new Message(Map.of("a", 1));
        assertNotNull(msg.toString());
        assertTrue(msg.toString().contains("a=1"));
    }
}