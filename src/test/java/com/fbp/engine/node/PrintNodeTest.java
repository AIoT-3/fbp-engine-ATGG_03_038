package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PrintNodeTest {

    @Test
    @DisplayName("1. 포트 구성 확인: 'in' 포트가 존재해야 함")
    void testPortConfiguration() {
        PrintNode printer = new PrintNode("p-1");
        // getInputPort("in")이 null이 아님을 확인
        assertNotNull(printer.getInputPort("in"), "PrintNode는 'in'이라는 이름의 입력 포트를 가져야 합니다.");
    }

    @Test
    @DisplayName("2. process 정상 동작: 메시지 처리 시 예외가 발생하지 않아야 함")
    void testProcessExecution() {
        PrintNode printer = new PrintNode("p-2");
        Message msg = new Message(Map.of("data", "hello"));

        // process() 호출 시 에러 없이 실행되는지 확인
        assertDoesNotThrow(() -> printer.process(msg));
    }

    @Test
    @DisplayName("3. AbstractNode 상속 확인: instanceof 검증")
    void testInheritance() {
        PrintNode printer = new PrintNode("p-3");

        // AbstractNode의 인스턴스인지 확인
        assertTrue(printer instanceof AbstractNode, "PrintNode는 AbstractNode를 상속받은 상태여야 합니다.");
    }
}