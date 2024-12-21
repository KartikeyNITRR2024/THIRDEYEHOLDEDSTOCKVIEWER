package com.thirdeye.holdedstockviewer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thirdeye.holdedstockviewer.services.impl.HoldedStockServiveImpl;
import com.thirdeye.holdedstockviewer.services.impl.ProcessHoldedStockServiceImpl;

import jakarta.annotation.PostConstruct;

@Component 
public class Initiatier {
	
	@Autowired
	AllMicroservicesData allMicroservicesData;
	
	@Autowired
	PropertyLoader propertyLoader;
	
	@Autowired
	HoldedStockServiveImpl holdedStockServiveImpl;
	
	@Autowired
	ProcessHoldedStockServiceImpl processHoldedStockServiceImpl;
	
	private static final Logger logger = LoggerFactory.getLogger(Initiatier.class);
	
	@PostConstruct
    public void init() throws Exception{
        logger.info("Initializing Initiatier...");
        allMicroservicesData.getAllMicroservicesData();
        propertyLoader.updatePropertyLoader();
        holdedStockServiveImpl.updateAllUnsoldStocks();
        processHoldedStockServiceImpl.refreshData();
        logger.info("Initiatier initialized.");
    }
}
