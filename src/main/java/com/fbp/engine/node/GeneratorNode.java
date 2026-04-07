package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import java.util.Map;

public class GeneratorNode extends AbstractNode {

    public GeneratorNode(String id) {
        // 1. 부모 클래스 생성자 호출
        super(id);
        // 2. 출력 포트 "out" 등록
        addOutputPort("out");
    }

    // 3. 메시지 생성 로직
    public void generate(String key, Object value) {
        Message message = new Message(Map.of(key, value));
        // AbstractNode의 send 메서드 활용
        send("out", message);
    }

    @Override
    protected void onProcess(Message message) {
        // GeneratorNode는 보통 입력을 받지 않으므로 비워둡니다.
    }
}