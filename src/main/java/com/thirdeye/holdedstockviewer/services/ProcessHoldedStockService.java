package com.thirdeye.holdedstockviewer.services;

import java.util.List;

import com.thirdeye.holdedstockviewer.pojos.HoldedStockPayload;
import com.thirdeye.holdedstockviewer.pojos.ResponsePayload;

public interface ProcessHoldedStockService {

	ResponsePayload processHoldedStockData(List<HoldedStockPayload> holdedStockData, String uniqueMachineCode, Integer firstTime);
	void refreshData();

}
