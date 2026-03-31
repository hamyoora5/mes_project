package com.example.mes.repository;

import com.example.mes.entity.QualityTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 품질 검사 엔티티의 조회와 저장을 담당하는 리포지토리입니다.
 */
public interface QualityTestRepository extends JpaRepository<QualityTest, Long> {

    /**
     * 품질 검사 번호 중복 여부를 확인합니다.
     *
     * @param testNo 품질 검사 번호
     * @return 중복 여부
     */
    boolean existsByTestNo(String testNo);

    /**
     * 품질 검사 번호로 품질 검사를 조회합니다.
     *
     * @param testNo 품질 검사 번호
     * @return 조회 결과
     */
    Optional<QualityTest> findByTestNo(String testNo);
}
