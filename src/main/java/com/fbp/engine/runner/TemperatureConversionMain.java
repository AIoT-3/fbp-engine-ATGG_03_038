package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TransformNode;
import java.util.Map;

public class TemperatureConversionMain {
    public static void main(String[] args) throws InterruptedException {
        // 1. 노드 생성
        GeneratorNode fahrenheitGen = new GeneratorNode("fahrenheit-gen");
        PrintNode celsiusPrinter = new PrintNode("celsius-printer");

        // 2. TransformNode 생성: F -> C 변환 람다 주입
        TransformNode f2cTransformer = new TransformNode("f2c-transformer", msg -> {
            Object tempObj = msg.get("temp");

            if (tempObj instanceof Number) {
                double fahrenheit = ((Number) tempObj).doubleValue();
                // 화씨를 섭씨로 변환하는 공식 적용
                double celsius = (fahrenheit - 32) * 5.0 / 9.0;

                // 변환된 결과를 새로운 메시지에 담아 반환
                return new Message(Map.of(
                        "temp", celsius,
                        "unit", "Celsius",
                        "original", fahrenheit + "F"
                ));
            }
            return null; // 숫자 데이터가 아니면 무시
        });

        // 3. 선로 생성 및 연결
        Connection conn1 = new Connection(10); // Generator -> Transformer
        Connection conn2 = new Connection(10); // Transformer -> Printer

        fahrenheitGen.getOutputPort("out").connect(conn1);
        f2cTransformer.getOutputPort("out").connect(conn2);

        // 4. 노드 초기화
        fahrenheitGen.initialize();
        f2cTransformer.initialize();
        celsiusPrinter.initialize();

        // 5. 소비자 스레드 가동 (메시지 전달 루프)
        Thread transformerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = conn1.poll();
                if (msg != null) f2cTransformer.process(msg);
            }
        });

        Thread printerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = conn2.poll();
                if (msg != null) celsiusPrinter.process(msg);
            }
        });

        transformerThread.start();
        printerThread.start();

        // 6. 데이터 생성 테스트
        System.out.println("=== 온도 변환 시스템 가동 ===");

        // 화씨 100도 전송 (약 37.7도 예상)
        fahrenheitGen.generate("temp", 100.0);

        // 화씨 32도 전송 (0도 예상)
        fahrenheitGen.generate("temp", 32.0);

        // 결과 확인을 위해 잠시 대기 후 종료
        Thread.sleep(1000);

        transformerThread.interrupt();
        printerThread.interrupt();
        fahrenheitGen.shutdown();
        f2cTransformer.shutdown();
        celsiusPrinter.shutdown();

        System.out.println("=== 시스템 종료 ===");
    }
}