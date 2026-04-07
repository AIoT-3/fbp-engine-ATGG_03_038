package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.InputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilterNodeTest {
    private FilterNode filterNode;
    private List<Message> receivedMessages;
    private final String KEY = "temp";
    private final double THRESHOLD = 30.0;

    @BeforeEach
    void setUp() {
        // 1. 테스트할 필터 노드 생성 (기준: temp가 30.0 이상일 것)
        filterNode = new FilterNode("test-filter", KEY, THRESHOLD);
        receivedMessages = new ArrayList<>();

        // 2. 필터의 출구(OutputPort)에 테스트용 선로와 포트를 연결
        Connection conn = new Connection();
        InputPort mockTarget = new InputPort() {
            @Override public String getName() { return "mock-in"; }
            @Override public void receive(Message message) { receivedMessages.add(message); }
        };

        conn.setTarget(mockTarget);
        filterNode.getOutputPort().connect(conn);
    }

    @Test
    @DisplayName("1. 조건 만족 시 통과 (Value >= Threshold)")
    void testConditionSatisfied() {
        Message msg = new Message(Map.of(KEY, 35.0));
        filterNode.process(msg);

        // 검증: 메시지가 필터를 통과하여 리스트에 들어있어야 함
        assertEquals(1, receivedMessages.size());
        assertEquals(35.0, (Double) receivedMessages.get(0).get(KEY));
    }

    @Test
    @DisplayName("2. 조건 미달 시 차단 (Value < Threshold)")
    void testConditionNotSatisfied() {
        Message msg = new Message(Map.of(KEY, 25.0));
        filterNode.process(msg);

        // 검증: 메시지가 차단되어 리스트가 비어있어야 함
        assertTrue(receivedMessages.isEmpty());
    }

    @Test
    @DisplayName("3. 경계값 처리 (Value == Threshold)")
    void testBoundaryValue() {
        Message msg = new Message(Map.of(KEY, 30.0));
        filterNode.process(msg);

        // 검증: '이상' 조건이므로 30.0은 통과해야 함
        assertEquals(1, receivedMessages.size());
    }

    @Test
    @DisplayName("4. 키가 없는 메시지 처리")
    void testMissingKey() {
        Message msg = new Message(Map.of("other_key", 100));

        // 실행 시 예외가 발생하지 않아야 하며, 메시지는 차단되어야 함
        assertDoesNotThrow(() -> filterNode.process(msg));
        assertTrue(receivedMessages.isEmpty());
    }
}