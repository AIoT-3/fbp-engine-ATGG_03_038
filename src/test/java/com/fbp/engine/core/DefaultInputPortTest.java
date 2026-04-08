package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DefaultInputPortTest {

    @Test
    @DisplayName("1. receive 시 owner의 process 호출 확인")
    void testReceiveCallsOwnerProcess() {
        // 1. process 호출 여부를 안전하게 체크하기 위한 플래그
        AtomicBoolean isProcessCalled = new AtomicBoolean(false);

        // 2. 테스트용 익명 노드(Mock) 생성
        // 익명 클래스 생성 시 missing method 에러 해결
        Node mockNode = new Node() {
            @Override public String getId() { return "test"; }
            @Override public void initialize() { } // 추가
            @Override public void process(Message m) { isProcessCalled.set(true); }
            @Override public void shutdown() { } // 추가
        };

        DefaultInputPort inputPort = new DefaultInputPort("in", mockNode);
        Message msg = new Message(Map.of("data", "hello"));

        // 3. 실행: 포트가 메시지를 수신함
        inputPort.receive(msg);

        // 4. 검증: 노드의 process 메서드가 실제로 실행되었는가?
        assertTrue(isProcessCalled.get(), "포트가 메시지를 받았을 때 노드의 process가 호출되어야 합니다.");
    }

    @Test
    @DisplayName("2. 포트 이름 확인")
    void testGetName() {
        // 실행 및 검증: 생성 시 지정한 이름이 잘 반환되는지 확인
        DefaultInputPort inputPort = new DefaultInputPort("trigger-port", null);
        assertEquals("trigger-port", inputPort.getName());
    }
}