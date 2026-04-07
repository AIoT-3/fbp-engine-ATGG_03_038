package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TimerNode;
import com.fbp.engine.message.Message;

public class LifecyclePipelineMain {
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        // 1. 노드 생성 (0.5초 주기 타이머, 3번 틱 이상만 통과하는 필터)
        TimerNode timer = new TimerNode("timer-1", 500);
        FilterNode filter = new FilterNode("filter-1", "tick", 3.0);
        PrintNode printer = new PrintNode("print-1");

        // 2. 선로 생성 및 연결
        Connection conn1 = new Connection(10); // Timer -> Filter
        Connection conn2 = new Connection(10); // Filter -> Print

        timer.getOutputPort("out").connect(conn1);
        filter.getOutputPort("out").connect(conn2);

        // 3. 모든 노드 초기화 (심장박동 시작!)
        timer.initialize();
        filter.initialize();
        printer.initialize();

        // 4. 비동기 처리를 위한 소비자 스레드 가동
        // Filter 스레드
        Thread filterThread = new Thread(() -> {
            while (running) {
                Message msg = conn1.poll();
                if (msg != null) filter.process(msg);
            }
        });

        // Printer 스레드
        Thread printThread = new Thread(() -> {
            while (running) {
                Message msg = conn2.poll();
                if (msg != null) printer.process(msg);
            }
        });

        filterThread.start();
        printThread.start();

        // 5. 3초 동안 공장 가동 (약 6번의 틱 발생 예상)
        System.out.println("=== 시스템 가동 (3초간) ===");
        Thread.sleep(3000);

        // 6. 시스템 종료 (자원 해제)
        System.out.println("=== 시스템 종료 시작 ===");
        running = false;

        timer.shutdown();   // 스케줄러 정지
        filter.shutdown();
        printer.shutdown();

        filterThread.interrupt();
        printThread.interrupt();

        System.out.println("=== 전체 시스템 안전 종료 완료 ===");
    }
}