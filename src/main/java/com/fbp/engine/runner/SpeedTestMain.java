package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.message.Message;

public class SpeedTestMain {
    public static void main(String[] args) throws InterruptedException {
        // 버퍼 크기를 작게(2) 설정하여 병목 현상을 뚜렷하게 확인합니다.
        Connection connection = new Connection(2);
        GeneratorNode generator = new GeneratorNode("gen");
        PrintNode printer = new PrintNode("printer");
        generator.getOutputPort("out").connect(connection);

        // 시나리오 A: 생산은 광속(0.1초), 소비는 거북이(1초)
        System.out.println("=== 시나리오 A 시작: 생산 > 소비 ===");

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                generator.generate("val", i);
                System.out.println("[GEN] 전송 완료: " + i + " (버퍼 크기: " + connection.getBufferSize() + ")");
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; } // 느린 처리
                Message msg = connection.poll();
                printer.process(msg);
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}