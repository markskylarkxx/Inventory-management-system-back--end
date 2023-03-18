package com.inventory.mgt.sytem.inventory.mgt.system.excel;

import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesTransactionRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExcelService {
    @Autowired
    SalesTransactionRepo salesTransactionRepo;
    public String writeToExcel(SalesOrder salesOrder) throws IOException {
        List<Product> products = salesTransactionRepo.findBySalesOrder(salesOrder.getId());
                  // amount
        List<Long> byTotalAmount = salesTransactionRepo.findByTotalAmount(salesOrder.getId());

        List<String> amount = byTotalAmount.stream().map(Objects::toString).collect(Collectors.toList());

        //quantity
        List<Long> byQuantitySold = salesTransactionRepo.findByQuantitySold(salesOrder.getId());
        List<String> quantity = byQuantitySold.stream().map(Objects::toString).collect(Collectors.toList());

        //product name
        List<String> productName = products.stream().map(Product::getBrandName).collect(Collectors.toList());

        // product price;
        List<Long> collect = products.stream().map(Product::getProductPrice).collect(Collectors.toList());
        List<String> productPrice = collect.stream().map(Objects::toString).collect(Collectors.toList());

            // workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();

        // spreadsheet object
        XSSFSheet sheet = workbook.createSheet("Customer");

        // create first row corresponding to the header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Quantity");
        header.createCell(2).setCellValue("Price");
        header.createCell(3).setCellValue("Total Amount");

        // iterate through the list and create rows;

        for (int i =0; i<amount.size(); i++){
            XSSFRow row = sheet.createRow((short) i + 1);
            // increase the width of the column
            sheet.setColumnWidth(0,6000);
            sheet.setColumnWidth(1,6000);
            sheet.setColumnWidth(2,6000);
            sheet.setColumnWidth(3,6000);
            // cell for name;
            row.createCell(0).setCellValue(productName.get(i));
            // cell for quantity;
            row.createCell(1).setCellValue(quantity.get(i));
            //cell for price;
            row.createCell(2).setCellValue(productPrice.get(i));
            // cell for amount;
            row.createCell(3).setCellValue(amount.get(i));
        }

        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\USER\\Documents\\customer.xlsx");
        workbook.write(outputStream);
        outputStream.close();

  return  " File has been successfully added to directory";
    }
}
