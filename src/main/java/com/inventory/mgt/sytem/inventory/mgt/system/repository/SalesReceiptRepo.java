package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesReceiptRepo extends JpaRepository<SalesReceipt, Long> {
}
