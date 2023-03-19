package com.inventory.mgt.sytem.inventory.mgt.system.controller;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.OrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.ResponseOrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.SalesOrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesTransactionRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.response.ApiResponse;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesTransaction;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import com.inventory.mgt.sytem.inventory.mgt.system.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class OrderController {
    @Autowired
    private OrderServiceImpl service;

    @Autowired
    UserRepository repository;

    @Autowired
    SalesTransactionRepo salesTransactionRepo;

    //todo; place an order, get list of orders made by a user, update the order, get order by their id;
      @PreAuthorize("hasRole('USER')")  //only users can place order
     @PostMapping("/placeOrder") // can only be accessed by User with (User_Role)
     public ResponseEntity<ResponseOrderDto> placeOrder(@RequestBody OrderDto orderDto ){

       //  return  new ResponseEntity<>(new ApiResponse(true, "Your order has been placed"), HttpStatus.CREATED);
        return new ResponseEntity(service.placeOrder(orderDto), HttpStatus.CREATED);
     }

    // sales_order
    @PostMapping("/sale")
    @PreAuthorize("hasRole('ROLE_ADMIN')")// only admins can make sales
    public ResponseEntity<ApiResponse> makesSales(@RequestBody SalesOrderDto salesOrderDto, Principal principal){
        service.saleTransaction(salesOrderDto, principal);
        return  new ResponseEntity<>(new ApiResponse(true, "Sales(s) successfully  made!"), HttpStatus.CREATED);

    }

    // find the least sale;
    //ALL SALES DONE IN A DAY
    @GetMapping("/specific_date")
    public ResponseEntity<List<SalesOrder>> salesInAParticularDay(@RequestParam(value = "date", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return  new ResponseEntity<>(service. salesInAParticularDay(date), HttpStatus.OK);
    }

    // ALL SALES FOR INTERVAL(WEEKLY, MONTHLY, YEARLY)
      @GetMapping("/interval_date")
    public ResponseEntity<List<SalesOrder>> salesMadeInAnInterval(@RequestParam(value = "start", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate start,
                                                            @RequestParam(value = "end", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate end){
          return  new ResponseEntity<>(service.salesMadeInAnInterval(start, end), HttpStatus.OK);


    }

    // ALL SALES TRANSACTION CARRIED OUT BY A PARTICULAR ADMIN(SPECIFIC DATE)
    @GetMapping("/all_transaction/{id}")
    public ResponseEntity<List<SalesTransaction>> transactionsByAnAdmin(@RequestParam(value = "date",
                                                                          required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                        LocalDate date,
                                                                        @PathVariable("id") Long userId){
          return new ResponseEntity<>(service. transactionsByAnAdmin(date, userId), HttpStatus.OK);
    }


    // SALES TRANSACTION WHERE A PARTICULAR PRODUCT IS MOST SOLD(Interval)
    @GetMapping("/transaction/most_sold")
    public  ResponseEntity<SalesTransaction> mostSoldProduct(@RequestParam(value = "start", required = false)
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                   LocalDate start,
                                                                   @RequestParam(value = "end", required = false)
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                   LocalDate end){
          return  new ResponseEntity<>(service.mostSoldProduct(start, end), HttpStatus.OK);
    }
    //  GET FIVE MOST SOLD PRODUCT BY INTERVAL
    @GetMapping("/transaction/quantity/sold/reversed")
    public ResponseEntity<List<SalesTransaction>> SoldProductByQuantity(@RequestParam(value = "start", required = false)
                                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                  LocalDate start,
                                                                  @RequestParam(value = "end", required = false)
                                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                  LocalDate end){
          return new ResponseEntity<>(service.fiveMostSoldProduct(start, end), HttpStatus.OK);



    }

    //return all products  sold;
     @GetMapping("/all/{id}")
    public void salesOrder(@PathVariable("id") Long id, SalesOrder salesOrder) {

         List<Product>products = salesTransactionRepo.findBySalesOrder(salesOrder.getId());
         System.out.println("=> " + products);



         List<Long> productId= products.stream().map(m -> m.getId()).collect(Collectors.toList());
         System.out.println(productId);

         System.out.println(products.size());
            //  quantity sold
         List<Long> byQuantitySold = salesTransactionRepo.findByQuantitySold(salesOrder.getId());
         System.out.println("quantity " + byQuantitySold);

         List<String> transform = Lists.transform(byQuantitySold, Functions.toStringFunction());
         String join = String.join("\n", transform);
         System.out.println(join);

         // total amount

         List<Long> byTotalAmount = salesTransactionRepo.findByTotalAmount(salesOrder.getId());
         System.out.println("amount" + byTotalAmount);


         List<String> transform1 = Lists.transform(byTotalAmount, Functions.toStringFunction());
         String join1 = String.join("\n", transform1);
         System.out.println(join1);


         // product price;

         List<Long>product_price = products.stream().map(m -> m.getProductPrice()).collect(Collectors.toList());

         System.out.println("price " +product_price);

         List<String> transform2 = Lists.transform(product_price, Functions.toStringFunction());
         String join2 = String.join("\n", transform2);
         System.out.println(join2);




         // product name;
         List<String> product_name = products.stream().map(m -> m.getBrandName()).collect(Collectors.toList());
         System.out.println("product name => " + product_name);
         String join3 = String.join("\n", product_name);
         System.out.println(join3);

         List<SalesOrder> SalesOrder =
                 salesTransactionRepo.findSalesTransactionBySalesOrder(salesOrder.getId());

         String s = SalesOrder.stream().map(m -> m.getUsername()).findFirst().get();
         System.out.println("username " + s);
     }
} 
