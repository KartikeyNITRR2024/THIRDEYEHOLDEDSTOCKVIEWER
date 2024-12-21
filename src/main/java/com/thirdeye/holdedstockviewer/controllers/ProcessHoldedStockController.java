package com.thirdeye.holdedstockviewer.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdeye.holdedstockviewer.pojos.HoldedStockPayload;
import com.thirdeye.holdedstockviewer.pojos.ResponsePayload;
import com.thirdeye.holdedstockviewer.services.impl.ProcessHoldedStockServiceImpl;
import com.thirdeye.holdedstockviewer.utils.AllMicroservicesData;
import com.thirdeye.holdedstockviewer.utils.TimeManagementUtil;

@RestController
@RequestMapping("/api/holdedstock")
public class ProcessHoldedStockController {

	@Autowired
	AllMicroservicesData allMicroservicesData;
	
	@Value("${uniqueMachineCode}")
	private String uniqueMachineCode;
	
	@Autowired
    TimeManagementUtil timeManagementUtil;
	
	@Autowired
	ProcessHoldedStockServiceImpl processHoldedStockServiceImpl;
	
    private static final Logger logger = LoggerFactory.getLogger(ProcessHoldedStockController.class);

    @PostMapping("/{uniqueId}/{uniqueMachineCode}/{firstTime}")
    public ResponseEntity<ResponsePayload> holdedStockData(@PathVariable("uniqueId") Integer pathUniqueId, @PathVariable("uniqueMachineCode") String pathUniqueMachineCode, @PathVariable("firstTime") Integer firstTime, @RequestBody List<HoldedStockPayload> holdedStockData) {
    	if (pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId()) && (firstTime == 0 || firstTime == 1)) {
        	logger.info("Status check for uniqueId {}: Found and uniqueMachineCode {}: Found and firstTime {}: ", allMicroservicesData.current.getMicroserviceUniqueId(), pathUniqueMachineCode, firstTime);
        	Timestamp currenttime = timeManagementUtil.getCurrentTime();
        	logger.info("Current Iteration time : {}", currenttime);
        	
        	ResponsePayload responsePayload = new ResponsePayload();
            if(timeManagementUtil.giveAccess(currenttime))
            {
            	responsePayload = processHoldedStockServiceImpl.processHoldedStockData(holdedStockData, pathUniqueMachineCode, firstTime);
            }
            else
            {
            	logger.info("Invalid Time");
            }
            
        	responsePayload.setNextIterationTime(timeManagementUtil.getNextIterationTime(currenttime));
            logger.info("Next Iteration time : {}", responsePayload.getNextIterationTime());
            return ResponseEntity.ok(responsePayload);
        } else {
            logger.warn("Status check for uniqueId {}: Not Found", allMicroservicesData.current.getMicroserviceUniqueId());
            return ResponseEntity.notFound().build();
        }
    }
}
