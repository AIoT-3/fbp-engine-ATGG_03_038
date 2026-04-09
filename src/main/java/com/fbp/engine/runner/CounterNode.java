package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class CounterNode extends AbstractNode {
    // 수신한 메시지 누적 카운트
    private int count = 0;

    public CounterNode(String id) {
        super(id);
        // 입구와 출구를 각각 하나씩 등록합니다.
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        // 1. 카운트 증가
        count++;

        // 2. 원본 메시지에 "count" 정보를 추가한 '새로운 메시지' 생성
        // (원본을 수정하지 않고 새로운 객체를 만드는 것이 포인트!)
        Message newMessage = message.withEntry("count", count);

        // 3. 새 메시지를 출력 포트로 전송
        send("out", newMessage);
    }

    @Override
    public void shutdown() {
        // 종료 시 최종 처리 건수를 콘솔에 출력합니다.
        System.out.println("[" + getId() + "] 총 처리 메시지: " + count + "건");
    }
}