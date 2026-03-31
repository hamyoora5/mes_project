package com.example.mes.repository;

import com.example.mes.entity.ProductionRunHistory;
import com.example.mes.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 생산 실행 이력 엔티티의 조회와 저장을 담당하는 리포지토리입니다.
 */
public interface ProductionRunHistoryRepository extends JpaRepository<ProductionRunHistory, Long> {

    /**
     * 특정 작업지시의 실행 이력 개수를 조회합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 실행 이력 개수
     */
    long countByWorkOrder(WorkOrder workOrder);

    /**
     * 특정 작업지시의 가장 최근 실행 이력을 조회합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 가장 최근 실행 이력
     */
    Optional<ProductionRunHistory> findTopByWorkOrderOrderByRunSeqDesc(WorkOrder workOrder);

    /**
     * 특정 작업지시의 전체 실행 이력을 순번 오름차순으로 조회합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 실행 이력 목록
     */
    List<ProductionRunHistory> findAllByWorkOrderOrderByRunSeqAsc(WorkOrder workOrder);
}
