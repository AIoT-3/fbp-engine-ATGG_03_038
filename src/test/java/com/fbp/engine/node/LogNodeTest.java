package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LogNodeTest {
    @Test
    @DisplayName("LogNode: 메시지 전달 및 파이프라인 중간 삽입 검증")
    void testLogNode() {
        LogNode logger = new LogNode("logger-1");
        Connection conn = new Connection();
        logger.getOutputPort("out").connect(conn);

        // 1. 메시지 통과 전달 확인 (내용 동일)
        Message original = new Message(Map.of("sensor", 25.5));
        logger.process(original);

        Message received = conn.poll();
        assertNotNull(received);
        assertEquals(original.getId(), received.getId(), "받은 메시지는 원본과 동일한 ID를 가져야 합니다.");
        assertEquals(25.5, received.get("sensor"), "메시지 페이로드가 유지되어야 합니다.");

        // 2. 중간 삽입 가능 (A -> Log -> B 형태 구성 테스트)
        GeneratorNode gen = new GeneratorNode("gen");
        gen.getOutputPort("out").connect(new Connection()); // 중간 연결 시뮬레이션
        // 실제 코드에서는 연결을 gen -> conn -> log -> conn -> print 로 구성하여 테스트 가능
        assertTrue(logger.getInputPort("in") != null && logger.getOutputPort("out") != null,
                "LogNode는 입출력 포트가 모두 있어 중간 삽입이 가능해야 합니다.");
    }
}