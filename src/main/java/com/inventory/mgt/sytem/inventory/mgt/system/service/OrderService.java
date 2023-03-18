package com.inventory.mgt.sytem.inventory.mgt.system.service;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.OrderRequestDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.OrderResponse;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.OrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.order.ResponseOrderDto;
import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesTransaction;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.Order;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service

public interface OrderService {

    OrderResponse findById(Long id);
    Order insert(OrderRequestDto request);
    void insertAll(List<OrderRequestDto> request);
    void delete(Long id);
    void Update(OrderRequestDto request, Long id);


     ResponseOrderDto placeOrder(OrderDto orderDto);


  List<SalesOrder> salesInAParticularDay(LocalDate date);

    List<SalesOrder> salesMadeInAnInterval(LocalDate start, LocalDate end);


 List<SalesTransaction> transactionsByAnAdmin(LocalDate date, Long userId);

    SalesTransaction mostSoldProduct(LocalDate start, LocalDate end);


    List<SalesTransaction> fiveMostSoldProduct(LocalDate start, LocalDate end);
}
