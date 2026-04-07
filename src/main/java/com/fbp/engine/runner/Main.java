package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;

public class Main {
    public static void main(String[] args) {
        // 1. 노드 생성 (작업대 배치)
        GeneratorNode generator = new GeneratorNode("temp-sensor");
        FilterNode filter = new FilterNode("high-temp-filter", "temperature", 30.0);
        PrintNode printer = new PrintNode("alert-printer");

        // 2. Connection 생성 (두 개의 선로 준비)
        Connection conn1 = new Connection(); // Generator -> Filter 연결용
        Connection conn2 = new Connection(); // Filter -> Print 연결용

        // 3. 조립 (Wiring)
        // [선로 1] Generator의 출구와 Filter의 입구 연결
        conn1.setTarget(filter.getInputPort("out"));
        generator.getOutputPort("out").connect(conn1);

        // [선로 2] Filter의 출구와 Print의 입구 연결
        conn2.setTarget(printer.getInputPort("out"));
        filter.getOutputPort("out").connect(conn2);

        // 4. 테스트 데이터 발생
        System.out.println("=== 3단 파이프라인 테스트 시작 (기준: 30.0도 이상) ===");

        System.out.println("> 25.0도 전송 중...");
        generator.generate("temperature", 25.0); // 출력되지 않아야 함

        System.out.println("> 35.0도 전송 중...");
        generator.generate("temperature", 35.0); // [alert-printer]에 출력되어야 함
    }
}