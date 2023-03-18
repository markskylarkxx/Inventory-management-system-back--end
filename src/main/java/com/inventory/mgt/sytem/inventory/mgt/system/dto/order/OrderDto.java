package com.inventory.mgt.sytem.inventory.mgt.system.dto.order;

import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {
    private  String orderDescription;
    @Transient
    private List<Product> productList;
    private String userFirstName;

}
