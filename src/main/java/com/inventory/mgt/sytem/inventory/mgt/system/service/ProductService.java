package com.inventory.mgt.sytem.inventory.mgt.system.service;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.ProductDto;

import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;

import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.ProductCount;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.ProductCountRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.ProductRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.SalesTransactionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    @Autowired
    ProductRepository repository;
    @Autowired
    private ProductCountRepo productCountRepo;

    @Autowired
    SalesTransactionRepo salesTransactionRepo;

    public Page<Product> productList(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("brandName"));
        Page<Product> pages = repository.findAll(pageRequest);
        return   pages;

    }


    public Product createProduct(ProductDto productDto) {
        Optional<Product> optionalProduct = repository.findBySerialNumber(productDto.getSerialNumber());
        if (!optionalProduct.isPresent()) {
            // CREATE NEW PRODUCT
            Product product = Product.builder().
            productName(productDto.getName()).
            serialNumber(productDto.getSerialNumber())
            .productDescription(productDto.getDescription()).
            quantity(productDto.getQuantity()).
            dateOfManufacture(LocalDate.now()).
            expirationDate(productDto.getExpirationDate()).
            brandName(productDto.getBrand()).
            productPrice(productDto.getPrice()).category(productDto.getCategory()).build();


            product.setAvailable(Boolean.TRUE);
            repository.save(product);
        } else {
            // IF PRESENT, UPDATE THE QUANTITY;
            Product product = optionalProduct.get();
            long availableQuantity = product.getQuantity() + productDto.getQuantity();
            product.setQuantity(availableQuantity);
            repository.save(product);

        }
        return null;
    }

    public Product updateProduct(Long id, ProductDto productDto) {

        // CHECK PRODUCT TO BE UPDATED, BY PRODUCT ID;
        Optional<Product> product = repository.findById(id);
        if (!product.isPresent()){
            throw new ResourceNotFoundException("product not found");
        }
        Product p = product.get();
        p.setProductName(productDto.getName());
        p.setProductDescription(productDto.getDescription());
        p.setProductPrice(productDto.getPrice());
        p.setCategory(productDto.getCategory());
        p.setBrandName(productDto.getBrand());
        p.setAvailable(productDto.isAvailable());
        p.setDateOfManufacture(productDto.getDateOfManufacture());
        p.setExpirationDate(productDto.getExpirationDate());

       return   repository.save(p);
    }

    public Product getProductById(Long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        if (!optionalProduct.isPresent()){
            throw  new AppException("No product withe the given Id: " + id);
        }
        Product product = optionalProduct.get();
         return  product;
    }

    public Page<Product> expiredProducts(Integer pageNumber, Integer pageSize) {
       // List<Product> productList = repository.findAll();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = repository.findAll(pageRequest);
        // loop through the products
         for (Product p: page){
             // get the current time & check if the current time is greater or equals the expired date;
             if (this.checkIfProductIsExpired(p)) {
                 return  page;
             }
         }
         return  null;
    }
    public  boolean checkIfProductIsExpired(Product product){
     return    LocalDate.now().isAfter(product.getExpirationDate());

    }



    public Product productWithLeastSales(ProductCount productCount) {

        List<ProductCount>list = productCountRepo.findByDateSold(productCount.getDateSold());
            // create an empty map
        Map<String, Long> map = new HashMap<>();

        // iterate through the list
        for (ProductCount pc : list){
            String productSerialNo = pc.getProductSerialNo();
            Long quantity = pc.getQuantity();
            if (map.containsKey(productSerialNo)) {
                map.put(productSerialNo, map.get(productSerialNo) + quantity);
            }else {
                map.put(productSerialNo, quantity);
            }
        }
            System.out.println("map= " + map);
               //todo; get the product_serial_no with the lowest value;
           String serialNo = map.entrySet().stream().min((e1, e2) ->
                    e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();
           // System.out.println(serialNo);
            // todo; check if this product serial_no exist in the product table, if it exist return the product;
           Optional<Product> product = repository.findBySerialNumber(serialNo);
            System.out.println("product with the least sale " + product);
           return product.orElse(null);




    }




    public Product productWithMostSales(ProductCount productCount) {

        List<ProductCount> list  = productCountRepo.findByDateSold(productCount.getDateSold());
        System.out.println(list);

        // todo; create an empty  map;
        Map<String, Long> map = new HashMap<>();

        //todo; iterate through the product count

        for (ProductCount p : list){
            Long quantity = p.getQuantity();
            String productSerialNo = p.getProductSerialNo();
            if (map.containsKey(productSerialNo)){
                map.put(productSerialNo, map.get(productSerialNo) + quantity);
            }
            else {
                map.put(productSerialNo, quantity);
            }
        }

            //todo; get the product_serial_number with the highest value;
            String product_Serial_number = map.entrySet().stream().max((e1, e2)
                    -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();

            Optional<Product> product = repository.findBySerialNumber(product_Serial_number);
        System.out.println("product with the most sale " + product);
            return product.orElse( null);



    }
    public List<Product>  exhaustingProduct() {

        List<Product> all = repository.findAll();
        List<Product> collect = all.stream().
                filter(product -> product.getQuantity() <=500).collect(Collectors.toList());

        System.out.println(collect.size() + "\n" +  collect);

      List<String>  list  = collect.stream().map(Product::getBrandName).collect(Collectors.toList());
      log.info("Exhausting product are " + list);
       return collect;
    }


    public Product leastSoldProductByInterval(LocalDate start, LocalDate end) {

        List<ProductCount> dateSold = productCountRepo.findByDateSold(start, end);

        // empty map;
        Map<String, Long>newMap = new TreeMap<>();
           for (ProductCount pc : dateSold){
               Long quantity = pc.getQuantity();
               String productSerialNo = pc.getProductSerialNo();
               if (newMap.containsKey(productSerialNo)){
                   newMap.put(productSerialNo, newMap.get(productSerialNo) + quantity);
               }else {
                   newMap.put(productSerialNo, quantity);
               }
           }

        String key = newMap.entrySet().stream().
                min((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();

        Optional<Product> bySerialNumber = repository.findBySerialNumber(key);
        return bySerialNumber.orElse(null);
    }

    public Product mostSoldProductByInterval(LocalDate start, LocalDate end) {
        List<ProductCount> dateSold = productCountRepo.findByDateSold(start, end);

        // empty map
        Map<String, Long> map = new TreeMap<>();
        for (ProductCount pc : dateSold){
            Long qty = pc.getQuantity();
            String psn= pc.getProductSerialNo();
            if (map.containsKey(psn)){
                map.put(psn, map.get(psn) + qty);
            }else {
                map.put(psn, qty);
            }
        }

        String key = map.entrySet().stream().
                max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();

        Optional<Product> serialNumber = repository.findBySerialNumber(key);
        return serialNumber.orElse(null);

    }

    public void setReminder() {
        List<Product> products = exhaustingProduct();
        System.out.println(exhaustingProduct());

        List<String> collect = products.stream().map(Product::getBrandName).collect(Collectors.toList());
        log.info("The Exhausting/exhausted product are " + collect);;
   }



}
