package com.example.mes.repository;

import com.example.mes.entity.QualityTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QualityTestRepository extends JpaRepository<QualityTest, Long> {

    boolean existsByTestNo(String testNo);

    Optional<QualityTest> findByTestNo(String testNo);
}
