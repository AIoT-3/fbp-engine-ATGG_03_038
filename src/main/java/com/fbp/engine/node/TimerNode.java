package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerNode extends AbstractNode {
    private final long intervalMs;
    private int tickCount = 0;
    private ScheduledExecutorService scheduler;

    public TimerNode(String id, long intervalMs) {
        super(id);
        this.intervalMs = intervalMs;

        // 출력 포트 "out" 등록
        addOutputPort("out");
    }

    @Override
    public void initialize() {
        // 단일 스레드 스케줄러 생성
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // 주기적인 작업 예약
        scheduler.scheduleAtFixedRate(() -> {
            // 메시지 생성 및 전송
            Message msg = new Message(Map.of(
                    "tick", tickCount++,
                    "timestamp", System.currentTimeMillis()
            ));

            send("out", msg);
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onProcess(Message message) {
        // No-op
    }

    @Override
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // 종료될 때까지 최대 1초 대기
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}