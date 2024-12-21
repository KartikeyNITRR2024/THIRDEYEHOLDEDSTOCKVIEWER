package com.thirdeye.holdedstockviewer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.holdedstockviewer.entity.HoldedStock;


@Repository
public interface HoldedStockRepo extends JpaRepository<HoldedStock, Long> {
	List<HoldedStock> findByHolding(Integer holding);
}

