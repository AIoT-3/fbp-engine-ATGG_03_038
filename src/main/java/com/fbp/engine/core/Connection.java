package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    private final String id;
    private final BlockingQueue<Message> buffer; // 1. BlockingQueue로 변경
    private InputPort target;

    // 2. 기본 생성자: 기본 버퍼 크기를 100으로 설정
    public Connection() {
        this(100);
    }

    // 3. 생성자 오버로딩: 버퍼 크기를 직접 지정 가능
    public Connection(int capacity) {
        this.id = UUID.randomUUID().toString();
        this.buffer = new LinkedBlockingQueue<>(capacity);
    }

    public void deliver(Message message) {
        try {
            // 더 이상 target.receive()를 직접 호출하지 않습니다.
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드 중단 상태 유지
        }
    }

    public Message poll() {
        try {
            return buffer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void setTarget(InputPort target) {
        this.target = target;
    }

    public int getBufferSize() {
        return buffer.size();
    }

    public String getId() {
        return id;
    }
}