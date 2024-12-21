package com.thirdeye.holdedstockviewer.services;

import java.util.List;

import com.thirdeye.holdedstockviewer.entity.HoldedStock;

public interface HoldedStockService {
	void updateAllUnsoldStocks() throws Exception;
	Integer getAllUnsoldStocksSize();
	List<HoldedStock> getAllUnsoldStocks();
	List<Long> getAllUnsoldStockIds();
	HoldedStock updateHoldStock(HoldedStock holdedStock);
}
