package com.example.mes.repository;

import com.example.mes.entity.ProductionResultMes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductionResultMesRepository extends JpaRepository<ProductionResultMes, Long> {

    Optional<ProductionResultMes> findByDailyProdCode(String dailyProdCode);

    java.util.List<ProductionResultMes> findAllByOrderByCompletedAtDesc();
}
