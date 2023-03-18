package com.inventory.mgt.sytem.inventory.mgt.system.repository;


import com.inventory.mgt.sytem.inventory.mgt.system.model.ProductCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductCountRepo extends JpaRepository<ProductCount, Long> {

    @Query(value = "select  p from  ProductCount  p where p.dateSold = ?1" )
    List<ProductCount> findByDateSold(LocalDate dateTime);
   @Query("select  p from  ProductCount  p where  p.productSerialNo = ?1")
    Optional<ProductCount> findByProductSerialNo(String serialNo);

  @Query("SELECT  p from  ProductCount  p where  p.dateSold between ?1 and ?2 ")
  List<ProductCount> findByDateSold(LocalDate startDate, LocalDate endDate);
}
