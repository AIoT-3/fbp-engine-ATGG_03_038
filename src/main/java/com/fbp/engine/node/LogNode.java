package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogNode extends AbstractNode {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public LogNode(String id) {
        super(id);
        // 1. 입구와 출구를 모두 만듭니다.
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        // 2. 현재 시간과 함께 로그 출력
        String now = LocalTime.now().format(formatter);
        System.out.println("[" + now + "][" + getId() + "] " + message.getPayload());

        // 3. 받은 메시지를 다음 노드로 그대로 전달 (핵심!)
        send("out", message);
    }
}