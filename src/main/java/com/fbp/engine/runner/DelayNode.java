package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class DelayNode extends AbstractNode {
    private final long delayMs;

    public DelayNode(String id, long delayMs) {
        super(id);
        this.delayMs = delayMs;

        // 입구와 출구를 각각 하나씩 등록합니다.
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        try {
            // 1. 지정된 시간만큼 현재 스레드를 대기시킵니다.
            Thread.sleep(delayMs);

            // 2. 대기 후 메시지를 그대로 다음 노드로 전달합니다.
            send("out", message);

        } catch (InterruptedException e) {
            // 3. 자고 있는 동안 인터럽트가 발생하면 상태를 복구하고 전송을 포기합니다.
            Thread.currentThread().interrupt();
            System.err.println("[" + getId() + "] 지연 중 중단됨: " + message.getId());
        }
    }
}