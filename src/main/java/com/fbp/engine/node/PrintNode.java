package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class PrintNode extends AbstractNode {

    public PrintNode(String id) {
        // 1. 부모 클래스(AbstractNode)의 생성자를 호출하여 ID를 설정합니다.
        super(id);
        // 2. AbstractNode가 제공하는 편의 메서드를 사용해 포트를 등록합니다.
        addInputPort("in");
    }

    @Override
    protected void onProcess(Message message) {
        // [노드ID] 메시지내용 형식으로 출력합니다.
        System.out.println("[" + getId() + "] " + message.getPayload());
    }
}