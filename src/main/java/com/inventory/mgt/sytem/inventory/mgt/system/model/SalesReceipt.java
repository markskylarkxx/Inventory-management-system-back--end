package com.inventory.mgt.sytem.inventory.mgt.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class SalesReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Long quantity;
    @Column(name = "total_amount")
    private Long totalAmount;
    @Column(name = "seller_name")
    private  String sellerName;
    private Long price;


}
