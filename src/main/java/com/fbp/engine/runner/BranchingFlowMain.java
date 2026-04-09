package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.SplitNode;
import com.fbp.engine.node.TimerNode;

public class BranchingFlowMain { // 6-4 SplitNode 확인
    public static void main(String[] args) throws InterruptedException {
        // 1. 노드 생성
        // 0.5초마다 메시지 생성
        TimerNode timer = new TimerNode("timer-1", 500);

        // tick 값이 3.0 이상이면 match, 미만이면 mismatch로 분기
        SplitNode splitter = new SplitNode("split-logic", "tick", 3.0);

        // 두 갈래 길 끝에 대기하는 프린터들
        PrintNode warningPrinter = new PrintNode("ALARM");
        PrintNode normalPrinter = new PrintNode("NORMAL");

        // 2. 선로 생성 (총 3개의 선로 필요)
        Connection timerToSplit = new Connection(10);
        Connection matchToAlarm = new Connection(10);
        Connection mismatchToNormal = new Connection(10);

        // 3. 연결 (이름표를 정확히 매칭!)
        timer.getOutputPort("out").connect(timerToSplit);

        // SplitNode의 두 출구를 각각 다른 프린터에 연결
        splitter.getOutputPort("match").connect(matchToAlarm);
        splitter.getOutputPort("mismatch").connect(mismatchToNormal);

        // 4. 노드 초기화
        timer.initialize();
        splitter.initialize();
        warningPrinter.initialize();
        normalPrinter.initialize();

        // 5. 소비자 스레드 가동 (메시지 전달 루프)
        // Splitter 스레드
        Thread splitThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = timerToSplit.poll();
                if (msg != null) splitter.process(msg);
            }
        });

        // Warning Printer 스레드
        Thread alarmThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = matchToAlarm.poll();
                if (msg != null) warningPrinter.process(msg);
            }
        });

        // Normal Printer 스레드
        Thread normalThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = mismatchToNormal.poll();
                if (msg != null) normalPrinter.process(msg);
            }
        });

        splitThread.start();
        alarmThread.start();
        normalThread.start();

        // 6. 3.5초간 관찰 (0~6번까지 약 7개의 틱 발생 예상)
        System.out.println("=== 분기 시스템 가동 시작 ===");
        Thread.sleep(3500);

        // 7. 종료
        System.out.println("=== 시스템 종료 ===");
        timer.shutdown();
        splitThread.interrupt();
        alarmThread.interrupt();
        normalThread.interrupt();
    }
}