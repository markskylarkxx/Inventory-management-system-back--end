package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import com.inventory.mgt.sytem.inventory.mgt.system.enums.OrderType;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderRequestDto {

    private OrderType orderType;

    private Date dateOrdered;
    private User user;




}
