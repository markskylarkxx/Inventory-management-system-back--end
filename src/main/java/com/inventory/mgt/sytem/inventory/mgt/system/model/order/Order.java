package com.inventory.mgt.sytem.inventory.mgt.system.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
//import com.inventory.mgt.sytem.inventory.mgt.system.model.Transaction;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private  String orderDescription;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Transient
    @JsonIgnore
    private List<Product> productList;

    public Order(String orderDescription, User user, List<Product> productList) {
    }
}
