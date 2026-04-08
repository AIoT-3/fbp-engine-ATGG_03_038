package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AbstractNodeTest {
    // 테스트를 위한 임시 하위 클래스
    static class TestNode extends AbstractNode {
        boolean onProcessCalled = false;
        TestNode(String id) { super(id); }
        @Override
        protected void onProcess(Message message) { onProcessCalled = true; }
    }

    @Test
    @DisplayName("1. getId 및 포트 등록 확인")
    void testNodeBasics() {
        TestNode node = new TestNode("test-id");
        assertEquals("test-id", node.getId());

        node.addInputPort("in");
        node.addOutputPort("out");

        assertNotNull(node.getInputPort("in"));
        assertNotNull(node.getOutputPort("out"));
        assertNull(node.getInputPort("none"));
    }

    @Test
    @DisplayName("2. process 호출 시 onProcess 실행 확인")
    void testProcessFlow() {
        TestNode node = new TestNode("test-node");
        node.process(new Message(Map.of()));
        assertTrue(node.onProcessCalled);
    }

    @Test
    @DisplayName("3. send 메서드 동작 확인")
    void testSend() {
        TestNode node = new TestNode("sender");
        node.addOutputPort("out");
        Connection conn = new Connection();
        node.getOutputPort("out").connect(conn);

        node.send("out", new Message(Map.of("data", 1)));
        assertNotNull(conn.poll());
    }
}