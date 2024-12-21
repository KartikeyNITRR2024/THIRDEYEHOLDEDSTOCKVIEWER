package com.thirdeye.holdedstockviewer.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.holdedstockviewer.entity.HoldedStock;
import com.thirdeye.holdedstockviewer.entity.HoldedStockStatus;
import com.thirdeye.holdedstockviewer.repositories.HoldedStockRepo;
import com.thirdeye.holdedstockviewer.services.HoldedStockService;

@Service
public class HoldedStockServiveImpl implements HoldedStockService {

	@Autowired
	HoldedStockRepo holdedStockRepo;
	
	private List<HoldedStock> holdedStockList = new ArrayList<>();
	
	private List<Long> holdedStockIdList = new ArrayList<>();
	
    private static final Logger logger = LoggerFactory.getLogger(HoldedStockServiveImpl.class);
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Override
    public HoldedStock updateHoldStock(HoldedStock holdedStock)
    {
    	HoldedStock savedHoldedStock = holdedStockRepo.save(holdedStock);
    	for(HoldedStock holdedStock1 : holdedStockList)
    	{
    		if(holdedStock1.getHoldedStockId() == savedHoldedStock.getHoldedStockId())
    		{
    			holdedStock1.setCurrent(savedHoldedStock.getCurrent());
    		}
    	}
    	return savedHoldedStock;
    }
	
    @Override
	public void updateAllUnsoldStocks() throws Exception
	{
		List<HoldedStock> holdedStockList1 = holdedStockRepo.findByHolding(1);
		for(HoldedStock holdedStock : holdedStockList1)
		{
			List<HoldedStockStatus> statuses = holdedStock.getAllStatus();
		    if (statuses != null) {
		        statuses.sort(Comparator.comparing(HoldedStockStatus::getStatusPrice));
		    }
		}
		List<Long> holdedStockIdList1 = new ArrayList<>();
		for(HoldedStock holdedStock : holdedStockList1)
		{
			holdedStockIdList1.add(holdedStock.getStockId());
		}
		lock.writeLock().lock();
		try {
			holdedStockList.clear();
			holdedStockIdList.clear();
			holdedStockList = new ArrayList<>(holdedStockList1);
			holdedStockIdList = new ArrayList<>(holdedStockIdList1);
            logger.info("Successfully updated all unsold stock data : {}", holdedStockList1.size());
        } catch (Exception e) {
            logger.error("Error occurred while updating all unsold stock data: {}", e.getMessage(), e);
            throw new Exception("Failed to retrieve all unsold stock data", e);
        } finally {
            lock.writeLock().unlock();
        }
	}
    
    @Override
	public Integer getAllUnsoldStocksSize()
	{
		lock.readLock().lock();
        try {
        	return holdedStockList.size();
        } finally {
            lock.readLock().unlock();
        }	
	}
	
	@Override
	public List<HoldedStock> getAllUnsoldStocks()
	{
		lock.readLock().lock();
        try {
        	return holdedStockList;
        } finally {
            lock.readLock().unlock();
        }
	}
	
	@Override
	public List<Long> getAllUnsoldStockIds()
	{
		lock.readLock().lock();
        try {
        	return holdedStockIdList;
        } finally {
            lock.readLock().unlock();
        }
	}
}
