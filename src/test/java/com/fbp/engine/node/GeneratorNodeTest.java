package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.InputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorNodeTest {
    private GeneratorNode generatorNode;
    private List<Message> capturedMessages;

    @BeforeEach
    void setUp() {
        // 1. 테스트할 제너레이터 노드 생성
        generatorNode = new GeneratorNode("test-gen");
        capturedMessages = new ArrayList<>();

        // 2. 출력을 가로채기 위해 Mock 연결 구성
        Connection conn = new Connection();
        InputPort mockTarget = new InputPort() {
            @Override public String getName() { return "mock-in"; }
            @Override public void receive(Message message) { capturedMessages.add(message); }
        };

        conn.setTarget(mockTarget);
        generatorNode.getOutputPort().connect(conn);
    }

    @Test
    @DisplayName("1. generate 메시지 생성 확인")
    void testGenerateCreatesMessage() {
        // 실행
        generatorNode.generate("sensor", "active");

        // 검증: OutputPort를 통해 메시지가 1개 전달되었어야 함
        assertEquals(1, capturedMessages.size());
    }

    @Test
    @DisplayName("2. 메시지 내용(페이로드) 검증")
    void testMessageContent() {
        // 실행
        generatorNode.generate("temp", 25.5);

        // 검증: 페이로드에 넣은 값이 정확히 들어있는지 확인
        Message msg = capturedMessages.get(0);
        assertEquals(25.5, (Double) msg.get("temp"));
    }

    @Test
    @DisplayName("3. OutputPort 조회 확인")
    void testGetOutputPort() {
        // 검증: 포트가 null이 아니고 정상적으로 가져와지는지 확인
        assertNotNull(generatorNode.getOutputPort());
    }

    @Test
    @DisplayName("4. 다수 generate 호출 시 순서 보장")
    void testMultipleGenerate() {
        // 실행: 3번 호출
        generatorNode.generate("seq", 1);
        generatorNode.generate("seq", 2);
        generatorNode.generate("seq", 3);

        // 검증: 3개의 메시지가 순서대로 들어왔는지 확인
        assertEquals(3, capturedMessages.size());
        assertEquals(1, (Integer) capturedMessages.get(0).get("seq"));
        assertEquals(2, (Integer) capturedMessages.get(1).get("seq"));
        assertEquals(3, (Integer) capturedMessages.get(2).get("seq"));
    }
}