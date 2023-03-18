package com.inventory.mgt.sytem.inventory.mgt.system.Pdf;

import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/api/auth")
public class PdfController {
     @Autowired

    private  PdfService service;

      // PDF FOR ONE PRODUCT SOLD
     @GetMapping("/pdf/{id}")
public  void getPdf(HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
         response.setContentType("application/json");
         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");

         String currentTime = dateFormat.format(new Date());

         String headerKey = "Content-Disposition";
         String  headerValue = "attachment; filename=pdf_  " + currentTime + ".pdf";
           response.setHeader(headerKey, headerValue);
           service.getPdf(response, id);

     }

     // PDF FOR LIST OF PRODUCT SOLD
    @GetMapping("list/pdf/{id}")
    public  void  generatePdf(HttpServletResponse response, @PathVariable("id")Long id, SalesOrder salesOrder
                              ) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDatetime = dateFormat.format(new Date());
        String headerKey = "Content-Disposition";

        String headerValue = "attachment; filename=pdf_" + currentDatetime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.service.export(response, salesOrder);


    }

}
