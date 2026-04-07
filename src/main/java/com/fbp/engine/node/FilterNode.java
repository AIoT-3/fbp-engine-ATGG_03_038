package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class FilterNode extends AbstractNode {
    private final String key;
    private final double threshold;

    public FilterNode(String id, String key, double threshold) {
        // 1. 부모 클래스 생성자 호출
        super(id);
        this.key = key;
        this.threshold = threshold;

        // 2. 입력 포트 "in"과 출력 포트 "out" 등록
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        Object value = message.get(key);

        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();

            // 조건 만족 시 AbstractNode의 send 메서드 활용
            if (val >= threshold) {
                send("out", message);
            }
        }
    }
}