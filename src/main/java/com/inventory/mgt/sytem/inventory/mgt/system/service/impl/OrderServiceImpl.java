package com.inventory.mgt.sytem.inventory.mgt.system.service.impl;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.OrderRequestDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.OrderResponse;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.OrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.ResponseOrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.SalesOrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.OrderType;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.model.*;
;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.Order;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.*;
import com.inventory.mgt.sytem.inventory.mgt.system.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableTransactionManagement
public class OrderServiceImpl implements OrderService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SalesRepository salesRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    SalesTransactionRepo salesTransactionRepo;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductCountRepo productCountRepo;



    @Override
    public OrderResponse findById(Long id) {
        return null;
    }

    @Override
    public Order insert(OrderRequestDto request) {

        return null;
    }

    @Override
    public void insertAll(List<OrderRequestDto> request) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void Update(OrderRequestDto request, Long id) {

    }

    @Override
    public ResponseOrderDto placeOrder(OrderDto orderDto) {
        ResponseOrderDto response = new ResponseOrderDto();
        float amount = this.getProductAmount(orderDto); // get the product amount;

        // get the first name passed;
        Optional<User> optionalUser = userRepository.findByFirstName(orderDto.getUserFirstName());

        // check if the user is present in the db /to  place an order;

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("No user found, cannot place an order!");
        } else {
            // get the user instance
            User u = optionalUser.get();
            Order order1 = Order.builder().orderDescription(orderDto.getOrderDescription()).user(u).build();
            orderRepository.save(order1);


            response.setOrderDescription(orderDto.getOrderDescription());
            response.setOrderedDate(new Date());
            response.setAmount(amount);
            response.setOrder(order1.getId());
            response.setMessage("Your order has been placed");


            return response;
        }

    }



    public Float getProductAmount(OrderDto orderDto) {

        float amount = 0f;


        //todo;  iterate through the product list

        for (Product p : orderDto.getProductList()) {
            // get the product id;
            Long productId = p.getId();

            // check if this id is present in the product table

            Optional<Product> optional = productRepository.findById(productId);
            if (!optional.isPresent()) {
                throw new ResourceNotFoundException(" Requested product not found");
            } else {
                // get the product instance
                Product product = optional.get();

                //check if the quantity requested is greater than the available quantity
                if (p.getQuantity() > product.getQuantity()) {
                    throw new AppException(" cannot process order, available quantity is less than the requested quantity");

                } else {
                    // get the amount;

                    amount = p.getQuantity() * product.getProductPrice();

                }

            }

        }

        return amount;
    }

    @Transactional // roll_backs - to avoid data inconsistency in the sales order & sales transaction table
    public  void saleTransaction(SalesOrderDto salesDto, Principal principal) {
        User user = userRepository.findById(salesDto.getId())
                .orElseThrow(()-> new ResourceNotFoundException("No user  with the specified  id"));



        //todo; get the authenticated user;
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        System.out.println(principal.getName());

        SalesOrder sales = SalesOrder.builder().username(principal.getName()).
                orderType(OrderType.SALES).
                dateOrdered(LocalDate.now()).
                user(user).build();




        SalesOrder savedOrder = salesRepository.save(sales);
              salesRepository.save(savedOrder);


        // iterate through the list of product
        for (Product p : salesDto.getProductList()) {
            // get the id from the list of product passed in post man
            Long id = p.getId();
            Optional<Product> productId= productRepository.findById(id);
            if (!productId.isPresent()) {
                continue;
            }
            Product product = productId.get();

            //CHECK IF THE QUANTITY SOLD IS GREATER THAN THE QUANTITY IN THE PRODUCT TABLE
                if (verifyQuantity(p.getQuantity(), product.getQuantity())) {
                    log.warn("Available quantity is less than the sold quantity");
                    throw new AppException("Available quantity is less than the sold quantity");
                }

                // CHECK IF THE SELLING PRICE CORRELATE WITH THE PRICE OF THE PRODUCT;
                if (!verifyPrice(p.getProductPrice(), product.getProductPrice())) {
                    log.warn("Price mismatch error");
                    throw new AppException("Price mismatch error");
                }


                // AMOUNT = QUANTITY ORDERED X PRODUCT PRICE
                Long amount = p.getQuantity() * product.getProductPrice();

                // GET THE SALES ORDER
                SalesOrder salesOrder = salesRepository.findById(savedOrder.getId()).get();

                //SAVE THE TRANSACTION
                SalesTransaction salesTransaction =
                        SalesTransaction.builder().salesOrder(salesOrder).
                                product(product).quantitySold(p.getQuantity()).totalAmount(amount).build();

                salesTransactionRepo.save(salesTransaction);

                // REDUCE THE QUANTITY IN THE PRODUCT TABLE;

                Long quantityRemaining = product.getQuantity() - p.getQuantity();

                product.setQuantity(quantityRemaining);
                productRepository.save(product);

                ProductCount productCount = new ProductCount();
                productCount.setProductSerialNo(product.getSerialNumber());
                productCount.setQuantity(p.getQuantity());
                productCount.setDateSold(LocalDate.now());
                productCountRepo.save(productCount);
            }


    }

    @Override
    public List<SalesOrder> salesInAParticularDay(LocalDate date) {

        if (validateDateEntry(date)){
            log.error("Invalid date entry");
            throw  new ResourceNotFoundException("Error! Cannot process date...");
        }
        List<SalesOrder> byDateOrdered = salesRepository.findByDateOrdered(date);

        List<LocalDate> datesOrderedOnly =
                byDateOrdered.stream().map(m -> m.getDateOrdered()).collect(Collectors.toList());

        if(!datesOrderedOnly.contains(date)){
            throw  new ResourceNotFoundException("No sale(s) made on this day");
        }
        if (!ObjectUtils.isEmpty(byDateOrdered)){
            for (SalesOrder sc : byDateOrdered){

                LocalDate dateOrdered = sc.getDateOrdered();

                if (dateOrdered.equals(date)){
                    return  byDateOrdered;
                }

            }

        }return  null;

    }

    @Override
    public List<SalesOrder> salesMadeInAnInterval(LocalDate start, LocalDate end) {
        if (!startDateMustBeLessThanEndDate(start, end)){
            log.error("Invalid date interval");
            throw  new AppException("Invalid date interval");
        }
        List<SalesOrder> byDateSold = salesRepository.findByDateSold(start, end);

        List<LocalDate> date = byDateSold.stream().map(m -> m.getDateOrdered()).collect(Collectors.toList());
       if (!date.contains(start) || !date.contains(end)){
           throw  new ResourceNotFoundException("Date entered not found");
       }
            if (!ObjectUtils.isEmpty(byDateSold)) {
                System.out.println(byDateSold.size());
                        return byDateSold;

                }
        return null;

    }

    @Override
    public List<SalesTransaction> transactionsByAnAdmin(LocalDate date, Long userId) {
       // find admin by id;
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));

        Set<Role> userRole = user.getRoles();

        Role role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN).get();
        if (!userRole.contains(role)){
            throw  new ResourceNotFoundException("No admin with the specified id "  + userId);
        }

        List<SalesTransaction> transaction =
                salesTransactionRepo.findSalesTransactionBySalesOrder_User(user.getId(), date);

        if (transaction.isEmpty()){
            throw  new ResourceNotFoundException("No sales transaction found");
        }
        System.out.println(transaction.size() + "\n" + transaction );
        return  transaction;
    }

    @Override
    public SalesTransaction mostSoldProduct(LocalDate start, LocalDate end) {
        List<SalesTransaction> quantitySold =
                salesTransactionRepo.findSalesTransactionByQuantitySold(start, end);
        System.out.println(quantitySold.size());

        System.out.println(quantitySold);

        // stream to get the most sold product
        SalesTransaction salesTransaction =
                quantitySold.stream().max((e1, e2) -> e1.getQuantitySold() > e2.getQuantitySold() ? 1 : -1).get();
        System.out.println(">>>>>>>" + salesTransaction);
       log.info("product name " + salesTransaction.getProduct().getBrandName()) ;


        return salesTransaction;

    }

    @Override
    public List<SalesTransaction> fiveMostSoldProduct(LocalDate start, LocalDate end) {

        List<SalesTransaction> salesTransactionByQuantitySold =
                salesTransactionRepo.findSalesTransactionByQuantitySold(start, end);
        System.out.println(salesTransactionByQuantitySold.size() + " " + salesTransactionByQuantitySold);


        // get 5 most sold product;
        List<SalesTransaction> collect = salesTransactionByQuantitySold.stream().sorted(Comparator.
                comparingLong(SalesTransaction::getQuantitySold).reversed()
                ).limit(5).collect(Collectors.toList());

        System.out.println(collect.size() + ": " + collect);
        return collect;
    }


    private  static  Boolean startDateMustBeLessThanEndDate(LocalDate start, LocalDate end){
        Assert.hasText("start date must be before end date");

        return  start.isBefore(end);
    }

    private static  boolean verifyPrice(Long sellingPrice, Long productPrice){
        return Objects.equals(sellingPrice, productPrice);
    }
     private  static  boolean verifyQuantity(Long quantitySold, Long quantityAvailable) {
         return quantitySold > quantityAvailable;
     }
     private  static boolean validateDateEntry(LocalDate date){
        return  date.isAfter(LocalDate.now());
     }

}










