package com.example.mes.repository;

import com.example.mes.entity.ProductionRunHistory;
import com.example.mes.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionRunHistoryRepository extends JpaRepository<ProductionRunHistory, Long> {

    long countByWorkOrder(WorkOrder workOrder);

    Optional<ProductionRunHistory> findTopByWorkOrderOrderByRunSeqDesc(WorkOrder workOrder);

    List<ProductionRunHistory> findAllByWorkOrderOrderByRunSeqAsc(WorkOrder workOrder);
}
