package com.example.mes.repository;

import com.example.mes.entity.DefectHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 불량 이력 엔티티의 조회와 저장을 담당하는 리포지토리입니다.
 */
public interface DefectHistoryRepository extends JpaRepository<DefectHistory, Long> {
}
