package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SplitNodeTest {
    @Test
    @DisplayName("SplitNode: 조건 분기 및 경계값 검증")
    void testSplit() {
        SplitNode splitter = new SplitNode("split", "val", 10.0);
        Connection matchConn = new Connection();
        Connection mismatchConn = new Connection();

        splitter.getOutputPort("match").connect(matchConn);
        splitter.getOutputPort("mismatch").connect(mismatchConn);

        // 1. 조건 만족 (15.0 >= 10.0) -> match
        splitter.process(new Message(Map.of("val", 15.0)));
        assertNotNull(matchConn.poll());
        assertEquals(0, mismatchConn.getBufferSize());

        // 2. 조건 미달 (5.0 < 10.0) -> mismatch
        splitter.process(new Message(Map.of("val", 5.0)));
        assertNotNull(mismatchConn.poll());

        // 3. 경계값 처리 (10.0 == 10.0) -> match
        splitter.process(new Message(Map.of("val", 10.0)));
        assertNotNull(matchConn.poll());
    }
}