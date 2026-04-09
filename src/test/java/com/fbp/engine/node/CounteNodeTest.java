package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CounterNodeTest {
    @Test
    @DisplayName("CounterNode: 카운트 누적 및 원본 유지 검증")
    void testCounter() {
        com.fbp.engine.node.CounterNode counter = new com.fbp.engine.node.CounterNode("counter");
        Connection conn = new Connection();
        counter.getOutputPort("out").connect(conn);

        // 메시지 3개 전송
        counter.process(new Message(Map.of("data", "A")));
        counter.process(new Message(Map.of("data", "B")));
        counter.process(new Message(Map.of("data", "C")));

        // 마지막 메시지 확인
        conn.poll(); // 1번 버림
        conn.poll(); // 2번 버림
        Message third = conn.poll();

        assertEquals(3, (Integer) third.get("count"), "카운트가 3으로 누적되어야 합니다.");
        assertEquals("C", third.get("data"), "원본 데이터 'data' 키가 유지되어야 합니다.");
    }
}