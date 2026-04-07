package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultOutputPortTest {
    private DefaultOutputPort outputPort;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        outputPort = new DefaultOutputPort("out");
        testMessage = new Message(Map.of("data", "test-payload"));
    }

    @Test
    @DisplayName("1. 단일 Connection 전달 확인")
    void testSingleConnectionDelivery() {
        Connection conn = new Connection();
        outputPort.connect(conn);

        // 실행
        outputPort.send(testMessage);

        // 검증: 연결된 Connection의 버퍼에 메시지가 1개 쌓여있어야 함
        assertEquals(1, conn.getBufferSize());
    }

    @Test
    @DisplayName("2. 다중 Connection 전달 확인 (1:N)")
    void testMultipleConnectionDelivery() {
        // 2개의 선로 준비
        Connection conn1 = new Connection();
        Connection conn2 = new Connection();

        outputPort.connect(conn1);
        outputPort.connect(conn2);

        // 실행
        outputPort.send(testMessage);

        // 검증: 두 선로 모두에 메시지가 전달되었는지 확인
        assertEquals(1, conn1.getBufferSize());
        assertEquals(1, conn2.getBufferSize());
    }

    @Test
    @DisplayName("3. Connection 미연결 시 예외 미발생 확인")
    void testNoConnectionNoException() {
        // 선로를 하나도 연결하지 않은 상태에서 전송해도 에러가 나지 않아야 함
        assertDoesNotThrow(() -> outputPort.send(testMessage));
    }
}