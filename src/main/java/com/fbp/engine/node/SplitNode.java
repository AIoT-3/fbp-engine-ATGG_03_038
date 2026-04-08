package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class SplitNode extends AbstractNode {
    private final String key;
    private final double threshold;

    public SplitNode(String id, String key, double threshold) {
        super(id);
        this.key = key;
        this.threshold = threshold;

        // 입구 1개와 출구 2개를 등록합니다.
        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    @Override
    protected void onProcess(Message message) {
        Object value = message.get(key);

        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();

            // 조건($val \ge threshold$) 확인 후 포트 분기
            if (val >= threshold) {
                send("match", message);
            } else {
                send("mismatch", message);
            }
        }
    }
}