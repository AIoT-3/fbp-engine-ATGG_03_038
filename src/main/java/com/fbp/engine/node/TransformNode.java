package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import java.util.function.Function;

public class TransformNode extends AbstractNode {
    // 메시지를 입력받아 변환된 메시지를 반환하는 함수형 인터페이스
    private final Function<Message, Message> transformer;

    public TransformNode(String id, Function<Message, Message> transformer) {
        super(id);
        this.transformer = transformer;

        // 입구 "in"과 출구 "out" 포트 등록
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        // 주입된 람다식(transformer)을 실행하여 결과 메시지를 얻음
        Message result = transformer.apply(message);

        // 결과가 null이 아닌 경우에만 "out" 포트로 전송 (null이면 메시지 드랍/필터링)
        if (result != null) {
            send("out", result);
        }
    }
}