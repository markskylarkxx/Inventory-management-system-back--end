package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SalesRepository extends JpaRepository<SalesOrder, Long> {


     @Query("select  p from  SalesOrder  p where p.dateOrdered =?1")
     List<SalesOrder> findByDateOrdered(LocalDate date);

     @Query("SELECT  s from SalesOrder  s where  s.dateOrdered between ?1 and ?2 ")
     List<SalesOrder> findByDateSold(LocalDate startDate, LocalDate endDate);

     @Query("select  s from  SalesOrder  s where  s.user = ?1")
     List<SalesOrder> findByUser(User user);
}
