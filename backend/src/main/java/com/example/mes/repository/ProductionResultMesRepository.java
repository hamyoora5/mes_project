package com.example.mes.repository;

import com.example.mes.entity.ProductionResultMes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * MES 생산 집계 결과 엔티티의 조회와 저장을 담당하는 리포지토리입니다.
 */
public interface ProductionResultMesRepository extends JpaRepository<ProductionResultMes, Long> {

    /**
     * 일일생산코드로 생산 결과를 조회합니다.
     *
     * @param dailyProdCode 일일생산코드
     * @return 생산 결과 조회 결과
     */
    Optional<ProductionResultMes> findByDailyProdCode(String dailyProdCode);

    /**
     * 완료 시각 역순으로 전체 생산 결과를 조회합니다.
     *
     * @return 생산 결과 목록
     */
    java.util.List<ProductionResultMes> findAllByOrderByCompletedAtDesc();
}
