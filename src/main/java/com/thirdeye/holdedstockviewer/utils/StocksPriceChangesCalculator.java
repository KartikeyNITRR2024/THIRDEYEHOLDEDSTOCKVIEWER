package com.thirdeye.holdedstockviewer.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.thirdeye.holdedstockviewer.entity.HoldedStock;
import com.thirdeye.holdedstockviewer.entity.HoldedStockStatus;
import com.thirdeye.holdedstockviewer.externalcontrollers.Thirdeye_Messenger_Connection;
import com.thirdeye.holdedstockviewer.pojos.ChangeDetails;
import com.thirdeye.holdedstockviewer.pojos.ChangeStatusDetails;
import com.thirdeye.holdedstockviewer.pojos.HoldedStockPayload;
import com.thirdeye.holdedstockviewer.pojos.PriceTimestampPojo;
import com.thirdeye.holdedstockviewer.services.impl.HoldedStockServiveImpl;


@Service
public class StocksPriceChangesCalculator {
private static final Logger logger = LoggerFactory.getLogger(StocksPriceChangesCalculator.class);
	
	@Autowired
	HoldedStockServiveImpl holdedStockServiveImpl;
	
	@Autowired
	Thirdeye_Messenger_Connection thirdeye_Messenger_Connection;
	
	public HoldedStockStatus findCurrentStatus(List<HoldedStockStatus> holdedStockStatusList, Double currentPrice) {
        int low = 0;
        int high = holdedStockStatusList.size() - 1;
        HoldedStockStatus result = null;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (holdedStockStatusList.get(mid).getStatusPrice() < currentPrice) {
                result = holdedStockStatusList.get(mid);
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }
    
    @Async("HoldedStockAsynchThread")
    public CompletableFuture<Void> calculateChanges(HoldedStockPayload holdedStockPayload) {
        logger.info("In function calculateChanges");

        List<HoldedStock> allHoldedStock = new ArrayList<>(holdedStockServiveImpl.getAllUnsoldStocks());
        for (HoldedStock holdedStock : allHoldedStock) {
        	Double currentPrice = null;
        	if(holdedStock.getType() == 0)
        	{
        		currentPrice = (holdedStockPayload.getPrice() / holdedStock.getBuyingPriceOfSingleStock() * 100) - 100;
        	}
        	else
        	{
        		currentPrice = holdedStockPayload.getPrice();
        	}
        	HoldedStockStatus newStatus = findCurrentStatus(holdedStock.getAllStatus(), currentPrice);
        	if(newStatus == null)
        	{
        		ChangeDetails changeDetails = new ChangeDetails();
        		changeDetails.setUserId(holdedStock.getUserId());
        		changeDetails.setChangeType(holdedStock.getType());
        		for(HoldedStockStatus holdedStockStatus : holdedStock.getAllStatus())
        		{
        			ChangeStatusDetails changeStatusDetails = new ChangeStatusDetails();
        			changeStatusDetails.setStatusId(holdedStockStatus.getStatusId());
        			changeStatusDetails.setStatusPrice(holdedStockStatus.getStatusPrice());
        			if(holdedStockStatus.getHoldedStockStatusId() == holdedStock.getCurrent().getHoldedStockStatusId())
        			{
        				changeStatusDetails.setStatus(-1);
        			}
        			changeDetails.getStatusList().add(changeStatusDetails);
        		}
        		holdedStockPayload.getChangeDetailsList().add(changeDetails);
        		holdedStock.setCurrent(holdedStock.getAllStatus().get(0));
        		holdedStockServiveImpl.updateHoldStock(holdedStock);
        	}
        	else if(!newStatus.getHoldedStockStatusId().equals(holdedStock.getCurrent().getHoldedStockStatusId()))
        	{
        		ChangeDetails changeDetails = new ChangeDetails();
        		changeDetails.setUserId(holdedStock.getUserId());
        		changeDetails.setChangeType(holdedStock.getType());
        		for(HoldedStockStatus holdedStockStatus : holdedStock.getAllStatus())
        		{
        			ChangeStatusDetails changeStatusDetails = new ChangeStatusDetails();
        			changeStatusDetails.setStatusId(holdedStockStatus.getStatusId());
        			changeStatusDetails.setStatusPrice(holdedStockStatus.getStatusPrice());
        			if(holdedStockStatus.getHoldedStockStatusId() == holdedStock.getCurrent().getHoldedStockStatusId())
        			{
        				changeStatusDetails.setStatus(-1);
        			}
        			if(holdedStockStatus.getHoldedStockStatusId() == newStatus.getHoldedStockStatusId())
        			{
        				changeStatusDetails.setStatus(1);
        			}
        			changeDetails.getStatusList().add(changeStatusDetails);
        		}
        		holdedStockPayload.getChangeDetailsList().add(changeDetails);
        		holdedStock.setCurrent(newStatus);
        		holdedStockServiveImpl.updateHoldStock(holdedStock);
        	}
        }
        if (holdedStockPayload.getChangeDetailsList() != null && holdedStockPayload.getChangeDetailsList().size() > 0) {
            logger.info("Calling Messenger Services");
        	thirdeye_Messenger_Connection.sendHoldedStockPayload(holdedStockPayload);   	
        }
        return CompletableFuture.completedFuture(null);
    }
}
