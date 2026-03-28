package com.example.mes.repository;

import com.example.mes.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    boolean existsByWorkOrderNo(String workOrderNo);

    Optional<WorkOrder> findByWorkOrderNo(String workOrderNo);
}
