package com.inventory.mgt.sytem.inventory.mgt.system.Pdf;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesReceipt;
import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesTransaction;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesReceiptRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesTransactionRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdfService {
    @Autowired
    SalesRepository salesRepository;

    @Autowired
    SalesTransactionRepo salesTransactionRepo;
    @Autowired
    SalesReceiptRepo salesReceiptRepo;


    public  void export(HttpServletResponse response, SalesOrder salesOrder) throws IOException {

        List<Product> products = salesTransactionRepo.findBySalesOrder(salesOrder.getId());
                   // STREAM TO GET THE PRODUCT IDs ASSOCIATED WITH THIS ORDER
        List<Long> productId= products.stream().map(Product::getId).collect(Collectors.toList());
        System.out.println(productId);
       System.out.println(products.size());

        //  quantity sold
        List<Long> byQuantitySold = salesTransactionRepo.findByQuantitySold(salesOrder.getId());
        System.out.println("quantity: " + byQuantitySold);

        // Convert to string
        List<String> transform = Lists.transform(byQuantitySold, Functions.toStringFunction());
        String joinQuantity = String.join("\n", transform);
        System.out.println(joinQuantity);

        // Total amount

        List<Long> byTotalAmount = salesTransactionRepo.findByTotalAmount(salesOrder.getId());
        System.out.println("amount" + byTotalAmount);

          // Convert to string
        List<String> transform1 = Lists.transform(byTotalAmount, Functions.toStringFunction());
        String joinAmount = String.join("\n", transform1);
        System.out.println(joinAmount);


        // product price;

        List<Long>product_price = products.stream().map(m -> m.getProductPrice()).collect(Collectors.toList());
        System.out.println("price " +product_price);
        // convert to string
        List<String> transform2 = Lists.transform(product_price, Functions.toStringFunction());
        String joinPrice = String.join("\n", transform2);
        System.out.println(joinPrice);

        // product name;
        List<String> product_name = products.stream().map(m -> m.getBrandName()).collect(Collectors.toList());
        System.out.println("product name => " + product_name);
        String joinName = String.join("\n", product_name);
        System.out.println(joinName);

        // GET THE NAME OF THE ADMIN WHO SOLD THE PRODUCT (firstname & lastname)
        List<SalesOrder> order =
                salesTransactionRepo.findSalesTransactionBySalesOrder(salesOrder.getId());
        User user = order.stream().map(m -> m.getUser()).findFirst().get();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        // GET THE DOCUMENT INSTANCE
        Document document = new Document(PageSize.A4);
              // INSTANCE OF PDF WRITER
        PdfWriter.getInstance(document, response.getOutputStream());
            //OPEN DOCUMENT
            document.open();

            //ADD A FONT
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA);
            fontTitle.setSize(18);
            fontTitle.setColor(Color.blue);

            //CREATE A PARAGRAPH
            Paragraph paragraph = new Paragraph("SALES RECEIPT ", fontTitle);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraph);


            // add spaces
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(12);
            font.setColor(Color.BLACK);
            Paragraph paragraph1 = new Paragraph("Admin :" + firstName +  " " + lastName,  font );
            paragraph1.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(paragraph1);

        // add spaces
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));




        //todo; add table

            PdfPTable table = new PdfPTable(4);

            PdfPCell cell = new PdfPCell(new Phrase("Name"));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Quantity"));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Price($)"));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Total Amount($)"));
            table.addCell(cell);

            //set header row
            table.setHeaderRows(1);


            // add cells
            table.addCell(joinName);
            table.addCell(joinQuantity);
            table.addCell(joinPrice);
            table.addCell(joinAmount);

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


            //todo; add Image;

           // document.add(Image.getInstance("C:\\Uploaded_files\\banner.png"));

            document.close();

            log.info("Receipt has been generated!");
        }


    // name of product bought;
    // quantity of product bought;
    // amount
    // person who sold the product;
    // total price

    public void getPdf(HttpServletResponse response, Long id) throws IOException {
        SalesTransaction salesTransaction = salesTransactionRepo.findById(id).orElse(null);
            // name of products
        String brandName = salesTransaction.getProduct().getBrandName();
            // quantity bought
        Long quantitySold = salesTransaction.getQuantitySold();
          // amount
        Long totalAmount = salesTransaction.getTotalAmount();
        // seller
        String username = salesTransaction.getSalesOrder().getUsername();
        // price
        Long productPrice = salesTransaction.getProduct().getProductPrice();


        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        // open the document

        document.open();

         Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
         fontTitle.setSize(18);
         fontTitle.setColor(Color.BLACK);

         // Create a paragraph

        Paragraph paragraph = new Paragraph("SALES RECEIPT", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        // add space
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

           // add table

        PdfPTable table = new PdfPTable(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setSize(12);
        font.setColor(Color.BLACK);

        PdfPCell cell = new PdfPCell(new Phrase("Brand name",font));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Quantity sold", font));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Price", font));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total amount", font));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Seller", font));

        table.addCell(cell);

        table.setHeaderRows(1);

           // add cells

        table.addCell(brandName);
        table.addCell(String.valueOf(quantitySold));
        table.addCell(String.valueOf(productPrice));
        table.addCell(String.valueOf(totalAmount));
        table.addCell(String.valueOf(username));
        document.add(table);

           document.close();
                    SalesReceipt salesReceipt = SalesReceipt.builder().
                    totalAmount(totalAmount).sellerName(username).
                    quantity(quantitySold).name(brandName).price(productPrice).build();

            salesReceiptRepo.save(salesReceipt);


    }
}
