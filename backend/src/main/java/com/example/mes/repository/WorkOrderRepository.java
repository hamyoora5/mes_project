package com.example.mes.repository;

import com.example.mes.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작업지시 엔티티의 조회와 저장을 담당하는 리포지토리입니다.
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    /**
     * 작업지시번호의 중복 여부를 확인합니다.
     *
     * @param workOrderNo 작업지시 번호
     * @return 중복 여부
     */
    boolean existsByWorkOrderNo(String workOrderNo);

    /**
     * 작업지시번호로 작업지시를 조회합니다.
     *
     * @param workOrderNo 작업지시 번호
     * @return 조회 결과
     */
    Optional<WorkOrder> findByWorkOrderNo(String workOrderNo);
}
