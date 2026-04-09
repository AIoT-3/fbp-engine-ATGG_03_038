package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.LogNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TimerNode;

public class FullPipelineMain { // 6-5 노드파이트라인 구성
    public static void main(String[] args) throws InterruptedException {
        // 1. 노드 생성 (Timer -> Log -> Filter -> Print)
        TimerNode timer = new TimerNode("timer", 1000); // 1초 주기
        LogNode logger = new LogNode("logger");
        FilterNode filter = new FilterNode("filter", "tick", 3.0);
        PrintNode printer = new PrintNode("printer");

        // 2. 선로 생성 (3개의 연결 고리)
        Connection conn1 = new Connection(5);
        Connection conn2 = new Connection(5);
        Connection conn3 = new Connection(5);

        // 3. 연결
        timer.getOutputPort("out").connect(conn1);
        logger.getOutputPort("out").connect(conn2);
        filter.getOutputPort("out").connect(conn3);

        // 4. 노드 초기화 (전체 시작)
        timer.initialize();
        logger.initialize();
        filter.initialize();
        printer.initialize();

        // 5. 파이프라인 구동 스레드들
        Thread tLogger = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = conn1.poll();
                if (msg != null) logger.process(msg);
            }
        });

        Thread tFilter = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = conn2.poll();
                if (msg != null) filter.process(msg);
            }
        });

        Thread tPrinter = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = conn3.poll();
                if (msg != null) printer.process(msg);
            }
        });

        tLogger.start();
        tFilter.start();
        tPrinter.start();

        // 6. 7초간 동작 확인
        System.out.println("=== 4노드 파이프라인 가동 (7초) ===");
        Thread.sleep(7000);

        // 7. 종료 처리
        System.out.println("=== 시스템 종료 프로세스 시작 ===");
        timer.shutdown();
        tLogger.interrupt();
        tFilter.interrupt();
        tPrinter.interrupt();

        logger.shutdown();
        filter.shutdown();
        printer.shutdown();
    }
}