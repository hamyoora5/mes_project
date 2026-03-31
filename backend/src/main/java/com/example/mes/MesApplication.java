package com.example.mes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MES 백엔드 애플리케이션의 진입점입니다.
 *
 * <p>Spring Boot 자동 구성을 활성화하고, 작업지시 관리, 생산 이력 처리,
 * 품질 검사 연계, ERP 결과 조회까지 포함한 전체 서버를 부팅합니다.
 */
@SpringBootApplication
public class MesApplication {

    /**
     * Spring Boot 애플리케이션을 시작합니다.
     *
     * @param args 실행 시 전달되는 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(MesApplication.class, args);
    }
}
