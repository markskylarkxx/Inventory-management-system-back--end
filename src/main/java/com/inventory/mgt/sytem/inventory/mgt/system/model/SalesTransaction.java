package com.inventory.mgt.sytem.inventory.mgt.system.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SalesTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private Long quantitySold;
    private Long totalAmount;
    @OneToOne(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private SalesOrder  salesOrder;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)

    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
