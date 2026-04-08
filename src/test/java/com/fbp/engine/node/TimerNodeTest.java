package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class TimerNodeTest {
    @Test
    @DisplayName("TimerNode: 전체 기능 검증 (메시지 생성, tick 증가, shutdown, 주기 확인)")
    void testTimerNodeFull() throws InterruptedException {
        // 500ms 주기로 설정
        TimerNode timer = new TimerNode("timer", 500);
        Connection conn = new Connection(10);
        timer.getOutputPort("out").connect(conn);

        // 1. initialize 후 메시지 생성 확인
        timer.initialize();

        // 첫 번째 메시지 (tick 0) 대기
        Message m1 = conn.poll();
        assertNotNull(m1, "initialize() 후 메시지가 생성되어야 합니다.");

        // 2. tick 증가 확인
        assertEquals(0, (Integer) m1.get("tick"), "첫 번째 tick은 0이어야 합니다.");
        Message m2 = conn.poll();
        assertEquals(1, (Integer) m2.get("tick"), "두 번째 tick은 1이어야 합니다.");

        // 4. 주기 확인 (2초간 대략 4개 메시지 생성 확인)
        // 이미 2개를 받았으므로 2초 더 대기하면 약 4개가 더 쌓여야 함
        Thread.sleep(2000);
        int count = conn.getBufferSize();
        // 오차 고려하여 3~5개 사이면 통과
        assertTrue(count >= 3 && count <= 5, "2초 동안 약 4개의 메시지가 생성되어야 합니다. (현재: " + count + ")");

        // 3. shutdown 후 정지 확인
        timer.shutdown();
        // 남은 메시지 다 비우기
        while(conn.getBufferSize() > 0) conn.poll();

        // 1초 더 대기 후에도 메시지가 생성되지 않는지 확인
        Thread.sleep(1000);
        assertEquals(0, conn.getBufferSize(), "shutdown() 후에는 메시지가 생성되지 않아야 합니다.");
    }
}