package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import com.inventory.mgt.sytem.inventory.mgt.system.enums.OrderType;
//import com.inventory.mgt.sytem.inventory.mgt.system.model.Transaction;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private OrderType  orderType;
    private Date dateOrdered;
    private User user;

}
