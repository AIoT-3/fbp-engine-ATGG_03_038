//package com.fbp.engine.core;
//
//import com.fbp.engine.message.Message;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ConnectionTest {
//    private Connection connection;
//
//    @BeforeEach
//    void setUp() {
//        connection = new Connection();
//    }
//
//    @Test
//    @DisplayName("1. deliver 후 target 수신")
//    void testDeliverToTarget() {
//        // 메시지가 잘 들어왔는지 확인하기 위한 리스트
//        List<Message> receivedMessages = new ArrayList<>();
//
//        // 익명 클래스로 테스트용 InputPort 생성
//        InputPort mockTarget = new InputPort() {
//            @Override public String getName() { return "test-in"; }
//            @Override public void receive(Message message) { receivedMessages.add(message); }
//        };
//
//        connection.setTarget(mockTarget);
//        Message msg = new Message(Map.of("data", "hello"));
//
//        // 실행
//        connection.deliver(msg);
//
//        // 검증: 리스트에 메시지가 하나 들어있어야 함
//        assertEquals(1, receivedMessages.size());
//        assertEquals(msg.getId(), receivedMessages.get(0).getId());
//    }
//
//    @Test
//    @DisplayName("2. target 미설정 시 동작 확인 (예외 미발생)")
//    void testNoTargetNoException() {
//        Message msg = new Message(Map.of("data", "no-target"));
//
//        // target이 null인 상태에서 deliver를 호출해도 에러가 나지 않아야 함
//        assertDoesNotThrow(() -> connection.deliver(msg));
//        // target이 없으므로 버퍼에 1개가 쌓여있어야 함
//        assertEquals(1, connection.getBufferSize());
//    }
//
//    @Test
//    @DisplayName("3. 버퍼 크기 확인")
//    void testBufferSize() {
//        Message msg1 = new Message(Map.of("val", 1));
//        Message msg2 = new Message(Map.of("val", 2));
//
//        // target이 없을 때 메시지를 넣으면 버퍼 크기가 증가함
//        connection.deliver(msg1);
//        assertEquals(1, connection.getBufferSize());
//
//        connection.deliver(msg2);
//        assertEquals(2, connection.getBufferSize());
//    }
//
//    @Test
//    @DisplayName("4. 다수 메시지 순서 보장 (FIFO)")
//    void testMessageOrder() {
//        List<Message> receivedMessages = new ArrayList<>();
//        InputPort mockTarget = new InputPort() {
//            @Override public String getName() { return "in"; }
//            @Override public void receive(Message message) { receivedMessages.add(message); }
//        };
//
//        Message msg1 = new Message(Map.of("seq", 1));
//        Message msg2 = new Message(Map.of("seq", 2));
//
//        // 1. 먼저 메시지 두 개를 target 없이 보내서 버퍼에 쌓음
//        connection.deliver(msg1);
//        connection.deliver(msg2);
//
//        // 2. 이제 target을 설정하고, 버퍼에 쌓인 것을 순서대로 보낼 준비 (현재 Connection 구조 기준)
//        // 주의: 현재 Connection.java 구조는 deliver() 시점에만 target 확인 후 배달함.
//        // 따라서 이미 쌓인 것을 보내려면 로직 확인이 필요하지만, 여기서는 순차적 deliver로 검증.
//
//        connection.setTarget(mockTarget);
//        Message msg3 = new Message(Map.of("seq", 3));
//        connection.deliver(msg3);
//
//        // FIFO 원리에 따라 가장 먼저 들어갔던 msg1(seq=1)이 나오는 것이 맞습니다.
//        assertEquals("seq=1", receivedMessages.get(0).getPayload().toString().replace("{", "").replace("}", ""));
//    }
//
//}

// BlockingQueue 버전

package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {
    private Connection connection;

    @BeforeEach
    void setUp() {
        // 기본 버퍼 크기 10으로 설정
        connection = new Connection(10);
    }

    @Test
    @DisplayName("1. deliver-poll 기본 동작 확인")
    void testDeliverPoll() {
        Message msg = new Message(Map.of("key", "value"));
        connection.deliver(msg);

        Message polled = connection.poll();
        assertNotNull(polled);
        assertEquals(msg.getId(), polled.getId());
    }

    @Test
    @DisplayName("2. 메시지 순서 보장 (FIFO)")
    void testMessageOrder() {
        connection.deliver(new Message(Map.of("seq", 1)));
        connection.deliver(new Message(Map.of("seq", 2)));
        connection.deliver(new Message(Map.of("seq", 3)));

        assertEquals(1, (Integer) connection.poll().get("seq"));
        assertEquals(2, (Integer) connection.poll().get("seq"));
        assertEquals(3, (Integer) connection.poll().get("seq"));
    }

    @Test
    @DisplayName("3. 멀티스레드 deliver-poll 테스트")
    void testMultithreadedCommunication() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Message> received = new AtomicReference<>();
        Message sentMsg = new Message(Map.of("data", "async"));

        // 소비자 스레드 시작
        Thread consumer = new Thread(() -> {
            received.set(connection.poll()); // 데이터 올 때까지 대기
            latch.countDown();
        });
        consumer.start();

        // 약간의 시간차를 두고 생산자가 데이터 전송
        Thread.sleep(100);
        connection.deliver(sentMsg);

        // 테스트 스레드에서 소비자 작업 완료 대기 (최대 1초)
        boolean completed = latch.await(1, TimeUnit.SECONDS);

        assertTrue(completed);
        assertEquals(sentMsg.getId(), received.get().getId());
    }

    @Test
    @DisplayName("4. poll 대기 동작 확인 (Blocking)")
    void testPollBlocking() {
        long startTime = System.currentTimeMillis();

        // 0.5초 후에 메시지를 넣어주는 스레드
        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            connection.deliver(new Message(Map.of("status", "late")));
        }).start();

        // poll()이 최소 0.5초 이상 대기했다가 값을 가져오는지 확인
        Message msg = connection.poll();
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(msg);
        assertTrue(duration >= 500, "poll()은 데이터가 올 때까지 블로킹되어야 합니다.");
    }

    @Test
    @DisplayName("5. 버퍼 크기 제한 및 deliver 블로킹 확인")
    void testBufferLimitBlocking() throws InterruptedException {
        Connection smallConn = new Connection(2);
        smallConn.deliver(new Message(Map.of("n", 1)));
        smallConn.deliver(new Message(Map.of("n", 2)));

        CountDownLatch latch = new CountDownLatch(1);
        Thread producer = new Thread(() -> {
            smallConn.deliver(new Message(Map.of("n", 3))); // 여기서 대기해야 함
            latch.countDown();
        });
        producer.start();

        // 0.5초 동안은 세 번째 deliver가 완료되지 않아야 함 (latch가 0이 되면 안 됨)
        assertFalse(latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals(2, smallConn.getBufferSize());

        // 소비자가 하나 꺼내면 비로소 세 번째 deliver가 완료됨
        smallConn.poll();
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("6. 버퍼 크기 조회 확인")
    void testGetBufferSize() {
        connection.deliver(new Message(Map.of("a", 1)));
        connection.deliver(new Message(Map.of("b", 2)));
        assertEquals(2, connection.getBufferSize());

        connection.poll();
        assertEquals(1, connection.getBufferSize());
    }
}