package com.inventory.mgt.sytem.inventory.mgt.system.excel;


import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class ExcelController {
    @Autowired
    ExcelService excelService;
    @GetMapping("write/excel/{id}")
    public  ResponseEntity<?> writeToExcel(@PathVariable("id") SalesOrder salesOrder) throws IOException {
        return  new ResponseEntity<>(excelService.writeToExcel(salesOrder), HttpStatus.OK);

    }

}
