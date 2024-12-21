package com.thirdeye.holdedstockviewer.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdeye.holdedstockviewer.services.HoldedStockService;
import com.thirdeye.holdedstockviewer.services.ProcessHoldedStockService;
import com.thirdeye.holdedstockviewer.services.impl.HoldedStockServiveImpl;
import com.thirdeye.holdedstockviewer.services.impl.ProcessHoldedStockServiceImpl;
import com.thirdeye.holdedstockviewer.utils.AllMicroservicesData;

@RestController
@RequestMapping("/api/updateholdedstock")
public class HoldedStockController {

    @Autowired
    AllMicroservicesData allMicroservicesData;

    @Value("${uniqueMachineName}")
    private String uniqueMachineName;

    @Autowired
    HoldedStockService holdedStockService;
    
	@Autowired
	ProcessHoldedStockService processHoldedStockService;

    private static final Logger logger = LoggerFactory.getLogger(HoldedStockController.class);

    @PostMapping("/{uniqueId}/{uniqueMachineCode}")
    public ResponseEntity<Boolean> updateHoldedStockData(@PathVariable("uniqueId") Integer pathUniqueId, 
                                                @PathVariable("uniqueMachineCode") Integer pathUniqueMachineCode) {
        if (pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId()) &&
            pathUniqueMachineCode.equals(allMicroservicesData.allMicroservices.get(uniqueMachineName).getMicroserviceUniqueId())) {
            logger.info("Update all unsold stock");
            try {
				holdedStockService.updateAllUnsoldStocks();
				processHoldedStockService.refreshData();
				logger.info("Updated all unsold stock successfull");
			} catch (Exception e) {
				logger.info("Failed Update all unsold stock successfull");
				return ResponseEntity.ok(false);
			}
            return ResponseEntity.ok(true);
        } else {
            logger.warn("Status check for uniqueId {} or uniqueMachineCode {}: Not Found", 
                        pathUniqueId, pathUniqueMachineCode);
            return ResponseEntity.ok(false);
        }
    }
}
