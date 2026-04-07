package com.fbp.engine.node;

import com.fbp.engine.core.InputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PrintNodeTest {

    @Test
    @DisplayName("1. InputPort 조회 확인")
    void testGetInputPort() {
        PrintNode printNode = new PrintNode("test-printer");

        // 검증: 노드가 생성될 때 InputPort도 함께 생성되어야 함
        assertNotNull(printNode.getInputPort(), "PrintNode는 입력을 받기 위한 InputPort를 가지고 있어야 합니다.");
        assertEquals("in", printNode.getInputPort().getName());
    }

    @Test
    @DisplayName("2. InputPort를 통한 수신 및 process 실행 확인")
    void testReceiveThroughPort() {
        PrintNode printNode = new PrintNode("test-printer");
        InputPort inputPort = printNode.getInputPort();
        Message msg = new Message(Map.of("data", "test-print"));

        // 실행: 노드를 직접 호출하는 것이 아니라, 포트의 receive를 호출합니다.
        // 내부 흐름: Port.receive() -> Node.process()
        // 검증: 이 과정에서 예외가 발생하지 않고 정상 실행되는지 확인합니다.
        assertDoesNotThrow(() -> inputPort.receive(msg));
    }
}