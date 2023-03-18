package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long > {
}
