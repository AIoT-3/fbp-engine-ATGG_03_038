package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DelayNodeTest {
    @Test
    @DisplayName("DelayNode: 지연 시간 측정 검증")
    void testDelay() {
        long delayTime = 500;
        com.fbp.engine.node.DelayNode delayer = new com.fbp.engine.node.DelayNode("delay", delayTime);
        Connection conn = new Connection();
        delayer.getOutputPort("out").connect(conn);

        long startTime = System.currentTimeMillis();
        delayer.process(new Message(Map.of("msg", "slow")));
        conn.poll(); // 메시지 수신 대기
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        // 500ms 이상 걸렸는지 확인 (시스템 오차 감안하여 450ms 이상으로 체크)
        assertTrue(duration >= 450, "지연 시간이 " + delayTime + "ms 이상 발생해야 합니다. (실제: " + duration + "ms)");
    }
}