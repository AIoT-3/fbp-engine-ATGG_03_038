package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TransformNodeTest {
    @Test
    @DisplayName("TransformNode: 변환 및 null 처리 검증")
    void testTransform() {
        // 1. 변환 정상 동작 확인
        TransformNode upperNode = new TransformNode("upper", msg -> {
            String val = (String) msg.get("text");
            return msg.withEntry("text", val.toUpperCase());
        });
        Connection conn = new Connection();
        upperNode.getOutputPort("out").connect(conn);

        upperNode.process(new Message(Map.of("text", "hello")));
        Message result = conn.poll();
        assertEquals("HELLO", result.get("text"), "텍스트가 대문자로 변환되어야 합니다.");

        // 2. null 반환 시 미전달 확인 (필터링 효과)
        TransformNode filterNode = new TransformNode("filter", msg -> null);
        Connection conn2 = new Connection();
        filterNode.getOutputPort("out").connect(conn2);

        filterNode.process(new Message(Map.of("data", 1)));
        assertEquals(0, conn2.getBufferSize(), "null을 반환하면 메시지가 전송되지 않아야 합니다.");
    }
}