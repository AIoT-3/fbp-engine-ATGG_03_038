package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class FilterNodeTest {

    @Test
    @DisplayName("1. 조건 만족 시 메시지 전달 (threshold 이상)")
    void testConditionMet() {
        // 기준값 10.0 설정
        FilterNode filter = new FilterNode("f-1", "val", 10.0);
        Connection conn = new Connection();
        filter.getOutputPort("out").connect(conn);

        // 15.0 전송 (기준치 이상)
        filter.process(new Message(Map.of("val", 15.0)));

        // OutputPort로 전달되었는지 확인
        assertEquals(1, conn.getBufferSize(), "기준치 이상인 메시지는 'out' 포트로 전달되어야 합니다.");
    }

    @Test
    @DisplayName("2. 조건 미달 시 메시지 차단 (threshold 미만)")
    void testConditionNotMet() {
        FilterNode filter = new FilterNode("f-2", "val", 10.0);
        Connection conn = new Connection();
        filter.getOutputPort("out").connect(conn);

        // 5.0 전송 (기준치 미만)
        filter.process(new Message(Map.of("val", 5.0)));

        // OutputPort에 메시지가 전달되지 않았는지 확인
        assertEquals(0, conn.getBufferSize(), "기준치 미만인 메시지는 차단되어 전달되지 않아야 합니다.");
    }

    @Test
    @DisplayName("3. 포트 구성 확인: 'in'과 'out' 포트가 존재해야 함")
    void testPortConfiguration() {
        FilterNode filter = new FilterNode("f-3", "val", 10.0);

        // 입력 포트 "in"과 출력 포트 "out"이 null이 아님을 확인
        assertNotNull(filter.getInputPort("in"), "입력 포트 'in'이 존재해야 합니다.");
        assertNotNull(filter.getOutputPort("out"), "출력 포트 'out'이 존재해야 합니다.");
    }
}