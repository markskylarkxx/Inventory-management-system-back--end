package com.inventory.mgt.sytem.inventory.mgt.system.dto.order;

import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SalesOrderDto {
    private  Long id;
//    private OrderType orderType;
//    private  String username;
//    private  Date dateOrdered;
    private List<Product> productList;

}
