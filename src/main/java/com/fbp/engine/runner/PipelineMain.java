package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.message.Message;

public class PipelineMain {
    // 모든 스레드가 공유하는 종료 플래그
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        // 1. 노드 및 선로 설정
        GeneratorNode generator = new GeneratorNode("gen");
        FilterNode filter = new FilterNode("filter", "temp", 30.0);
        PrintNode printer = new PrintNode("printer");

        Connection conn1 = new Connection(5); // Gen -> Filter
        Connection conn2 = new Connection(5); // Filter -> Print

        // 2. 물리적 연결 (Wiring)
        generator.getOutputPort("out").connect(conn1);
        filter.getOutputPort("out").connect(conn2);

        // 3. Thread-1: 생산자 (Generator)
        Thread genThread = new Thread(() -> {
            double[] temps = {25.5, 32.0, 28.0, 40.5, 22.0};
            for (double t : temps) {
                if (!running) break;
                System.out.println("[GEN] 온도 발생: " + t);
                generator.generate("temp", t);
                try { Thread.sleep(500); } catch (InterruptedException e) { break; }
            }
            running = false; // 데이터 발생 완료 시 종료 신호
            System.out.println("[GEN] 스레드 종료");
        });

        // 4. Thread-2: 중간 처리자 (Filter)
        Thread filterThread = new Thread(() -> {
            while (running || conn1.getBufferSize() > 0) {
                // Connection-1에서 데이터가 올 때까지 대기(poll)
                Message msg = conn1.poll();
                if (msg != null) {
                    System.out.println("[FILTER] 메시지 검사 중...");
                    // FilterNode 내부에서 조건을 만족하면 자동으로 conn2.deliver() 호출됨
                    filter.process(msg);
                }
            }
            System.out.println("[FILTER] 스레드 종료");
        });

        // 5. Thread-3: 최종 소비자 (Printer)
        Thread printThread = new Thread(() -> {
            while (running || conn2.getBufferSize() > 0) {
                // Connection-2에서 데이터가 올 때까지 대기(poll)
                Message msg = conn2.poll();
                if (msg != null) {
                    printer.process(msg);
                }
            }
            System.out.println("[PRINTER] 스레드 종료");
        });

        // 6. 모든 엔진 가동!
        genThread.start();
        filterThread.start();
        printThread.start();

        // 메인 스레드는 작업이 끝날 때까지 대기
        genThread.join();
        filterThread.join();
        printThread.join();
        System.out.println("=== 전체 시스템 종료 ===");
    }
}