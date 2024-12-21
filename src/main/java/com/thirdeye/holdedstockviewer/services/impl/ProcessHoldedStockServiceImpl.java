package com.thirdeye.holdedstockviewer.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.holdedstockviewer.pojos.HoldedStockPayload;
import com.thirdeye.holdedstockviewer.pojos.PriceTimestampPojo;
import com.thirdeye.holdedstockviewer.pojos.ResponsePayload;
import com.thirdeye.holdedstockviewer.services.ProcessHoldedStockService;
import com.thirdeye.holdedstockviewer.utils.PropertyLoader;
import com.thirdeye.holdedstockviewer.utils.StocksPriceChangesCalculator;
import com.thirdeye.holdedstockviewer.utils.TimeManagementUtil;

@Service
public class ProcessHoldedStockServiceImpl implements ProcessHoldedStockService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessHoldedStockServiceImpl.class);
//	private Boolean updateMachine = false;
//	private Set<String> machineSet = new HashSet<>();
	
	private Map<Long, List<PriceTimestampPojo>> dataStoringMap = new HashMap<>();
	private Boolean updateMachine = true;
	private Map<String, Boolean> updateMachineStatus = new HashMap<>();
	
	@Autowired
    TimeManagementUtil timeManagementUtil;
	
	@Autowired
	PropertyLoader propertyLoader;
	
	@Autowired
	StocksPriceChangesCalculator stocksPriceChangesCalculator;
	
	@Autowired
	HoldedStockServiveImpl holdedStockServiveImpl;
	 
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
//	@Override
//	public ResponsePayload processHoldedStockData(List<HoldedStockPayload> holdedStockData, String uniqueMachineCode)
//	{
//		lock.writeLock().lock();
//	    try {
//	        logger.info("DataProcessing Starting time is : {}", timeManagementUtil.getCurrentTime());
//	        
//	        List<CompletableFuture<Void>> futures = new ArrayList<>();
//	        
//	        for(HoldedStockPayload holdedStockPayload : holdedStockData) {
//	            if(holdedStockPayload.getStockId() != null && holdedStockPayload.getStockId() > 0 
//	                    && holdedStockPayload.getPrice() != null && holdedStockPayload.getPrice() > 0) {
//	                
//	                CompletableFuture<Void> future = stocksPriceChangesCalculator.calculateChanges(holdedStockPayload);
//	                futures.add(future);
//	            }
//	        }
//
//	        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//	        logger.info("DataProcessing Ending time is : {}", timeManagementUtil.getCurrentTime());
//	        
//	        if (updateMachine) {
//	            if (machineSet.size() >= propertyLoader.noOfMachine) {
//	                machineSet.clear();
//	                updateMachine = false;
//	                logger.info("All machines are reset.");
//	            } else {
//	                machineSet.add(uniqueMachineCode);
//	                logger.info("Going to reset machine with uniqueMachineCode : {}", uniqueMachineCode);
//	            }
//	        }
//	    } finally {
//	        lock.writeLock().unlock();
//	    }
//	    return new ResponsePayload(null,updateMachine,holdedStockServiveImpl.getAllUnsoldStockIds());
//	}
	
	@Override
	public ResponsePayload processHoldedStockData(List<HoldedStockPayload> holdedStockData, String uniqueMachineCode, Integer firstTime) {
	    lock.writeLock().lock();
	    Boolean check = false;
	    List<Long> ids = null;
	    try {
	        logger.info("DataProcessing Starting time is : {}", timeManagementUtil.getCurrentTime());

	        // Launch async tasks without waiting for them to complete
	        for (HoldedStockPayload holdedStockPayload : holdedStockData) {
	            if (holdedStockPayload.getStockId() != null && holdedStockPayload.getStockId() > 0
	                    && holdedStockPayload.getPrice() != null && holdedStockPayload.getPrice() > 0) {
	                
	                stocksPriceChangesCalculator.calculateChanges(holdedStockPayload);
	            }
	        }

	        logger.info("DataProcessing Ending time is : {}", timeManagementUtil.getCurrentTime());

//	        if (updateMachine) {
//	            if (machineSet.size() >= propertyLoader.noOfMachine) {
//	                machineSet.clear();
//	                updateMachine = false;
//	                logger.info("All machines are reset.");
//	            } else {
//	                machineSet.add(uniqueMachineCode);
//	                logger.info("Going to reset machine with uniqueMachineCode : {}", uniqueMachineCode);
//	            }
//	        }
	        
	        if (updateMachine) {
                if (updateMachineStatus.size() >= propertyLoader.noOfMachine) {
                    updateMachineStatus.clear();
                    updateMachine = false;
                    logger.info("All machines are reset.");
                } else {
                    if (updateMachineStatus.get(uniqueMachineCode) == null) {
                        check = true;
                        updateMachineStatus.put(uniqueMachineCode, true);
                        ids = holdedStockServiveImpl.getAllUnsoldStockIds();
                        logger.info("Going to reset machine with uniqueMachineCode : {}", uniqueMachineCode);
                    } else {
                        logger.info("Machine with uniqueMachineCode {} is already updated.", uniqueMachineCode);
                    }
                }
            }
	    } finally {
	        lock.writeLock().unlock();
	    }
	    if(firstTime.equals(1))
	    {
        	logger.info("Sending data first time.");
		    ids = holdedStockServiveImpl.getAllUnsoldStockIds();
		    return new ResponsePayload(null, true, ids);
	    }
	    return new ResponsePayload(null, check, ids);
	}
	
	@Override
	public void refreshData()
	{
		lock.writeLock().lock();
		try {
			logger.info("Refreshing all data.");
			dataStoringMap.clear();
			updateMachine = true;
			updateMachineStatus.clear(); 
		} finally {
            lock.writeLock().unlock();
        }
	}
}
