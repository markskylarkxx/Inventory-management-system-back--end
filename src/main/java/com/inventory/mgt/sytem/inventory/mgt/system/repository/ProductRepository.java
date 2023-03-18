package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductName(String name);
    Optional<Product> findById(Long id);
    @Query(value = "select p from  Product  p where p.serialNumber = ?1")
    Optional<Product> findBySerialNumber(String serialNumber);




}
