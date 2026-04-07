package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.message.Message;

public class ThreadTestMain {
    public static void main(String[] args) {
        // 1. 노드 및 선로 준비
        GeneratorNode generator = new GeneratorNode("gen-1");
        PrintNode printer = new PrintNode("print-1");
        Connection connection = new Connection(10); // 버퍼 크기 10

        // 2. 연결 (Generator -> Connection)
        generator.getOutputPort("out").connect(connection);

        // 3. 생산자 스레드 (Thread-1)
        Thread producerThread = new Thread(() -> {
            System.out.println("[생산자] 시작");
            for (int i = 1; i <= 5; i++) {
                generator.generate("data", "Msg-" + i);
                System.out.println("[생산자] 메시지 전송 완료: " + i);
                try {
                    Thread.sleep(1000); // 1초 간격으로 생성
                } catch (InterruptedException e) { break; }
            }
            System.out.println("[생산자] 종료");
        });

        // 4. 소비자 스레드 (Thread-2)
        Thread consumerThread = new Thread(() -> {
            System.out.println("[소비자] 대기 시작");
            // 5번의 메시지를 수신하기 위한 루프
            for (int i = 0; i < 5; i++) {
                // Connection에서 메시지가 올 때까지 Blocking(대기)함
                Message msg = connection.poll();
                if (msg != null) {
                    printer.process(msg);
                }
            }
            System.out.println("[소비자] 종료");
        });

        // 5. 실행 시작
        producerThread.start();
        consumerThread.start();
    }
}