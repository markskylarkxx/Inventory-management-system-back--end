package com.inventory.mgt.sytem.inventory.mgt.system.controller;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.ProductDto;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.ProductCount;
import com.inventory.mgt.sytem.inventory.mgt.system.response.ApiResponse;
import com.inventory.mgt.sytem.inventory.mgt.system.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class ProductController {
    @Autowired
    ProductService service;


    // GET ALL PRODUCTS;
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam("number") Integer pageNumber,
                                                        @RequestParam("size") Integer pageSize){
        Page<Product> products = service.productList(pageNumber, pageSize);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }
    // add a product to the inventory;
     @PostMapping("/add/product")
    public  ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody ProductDto productDto){
        service.createProduct(productDto);
        return  new ResponseEntity<>(new ApiResponse(true,
                "product has been added"), HttpStatus.CREATED);

    }
    // update product
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") Long id,
                                                     @RequestBody ProductDto productDto ){
       service.updateProduct(id, productDto);

        return  new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);

    }

    //get product by id;
    @GetMapping("/product/{id}")
    public ResponseEntity<Product>  getProductById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.getProductById(id));

    }

    // get all product that have expired
      @GetMapping("/expired")
    public ResponseEntity<Page<Product>> expiredProducts(@RequestParam("number") Integer pageNumber,
                                                         @RequestParam("size") Integer pageSize){

        return new ResponseEntity<>( service.expiredProducts(pageNumber, pageSize), HttpStatus.OK);
    }

    // get the product with most sales;
    @GetMapping("/most_sales")
    public ResponseEntity<Product> productsWithMostSales(@RequestBody ProductCount productCount){
        return new ResponseEntity<>(service.productWithMostSales(productCount), HttpStatus.OK);

    }


    // get product with  the least sales
     @GetMapping("/least_sales")
    public ResponseEntity<Product> productWithLeastSales(@RequestBody ProductCount productCount){
        return  new ResponseEntity<>(service.productWithLeastSales(productCount), HttpStatus.OK);
    }


    // get all products/product that are almost exhausted;
    @GetMapping("/little")
    public  ResponseEntity<List<Product>> exhaustingProduct(){
        return  new ResponseEntity<>(service.exhaustingProduct(), HttpStatus.OK);
    }

// find the least sold product  by weekly, monthly and yearly
    //
   @GetMapping("least/interval_sale")
public  ResponseEntity<Product> leastSoldProductByInterval(@RequestParam("start")
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate start,
                                                       @RequestParam("end")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate end){
        return  new ResponseEntity<>(service.leastSoldProductByInterval(start, end), HttpStatus.OK);
}
@GetMapping("most/interval_sale")
public  ResponseEntity<Product> mostSoldProductByInterval(@RequestParam("start")
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate start,
                                                       @RequestParam("end")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate end){
    return  new ResponseEntity<>(service.mostSoldProductByInterval(start, end), HttpStatus.OK);
}



 @Scheduled(cron = "0 */20 23 * * *")
    public  void setReminder(){
    service.setReminder();
 }



}