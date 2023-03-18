package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.Product;
import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesTransaction;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesTransactionRepo extends JpaRepository<SalesTransaction, Long> {


//    SELECT sales_transaction.id, sales_order.date_ordered,
//    sales_transaction.quantity_sold FROM sales_transaction
//    INNER JOIN sales_order ON sales_transaction.order_id = sales_order.id
@Query(value = "select st from " +
        " SalesTransaction st inner join SalesOrder  " +
        "so on st.salesOrder = so.user where st.salesOrder =?1 and so.dateOrdered = ?2")
    List<SalesTransaction> findSalesTransactionBySalesOrderAndSalesOrderDateOrdered(SalesOrder  salesOrder,
                                                                              LocalDate date);


  @Query("select  st.product from  SalesTransaction st where st.salesOrder.id =?1")
  List<Product> findBySalesOrder(Long salesOrderId);

 @Query("select  st.quantitySold from  SalesTransaction  st where  st.salesOrder.id =?1")
  List<Long> findByQuantitySold(Long salesOrderId);

 @Query("select st.totalAmount from  SalesTransaction  st where st.salesOrder.id =?1")
    List<Long> findByTotalAmount(Long salesOrderId);
 @Query("select st.salesOrder from  SalesTransaction st where st.salesOrder.id = ?1")
 List<SalesOrder> findSalesTransactionBySalesOrder(Long salesOrder);

 @Query("select  st from  SalesTransaction " +
         " st inner  join SalesOrder so on  st.salesOrder.id = so.id where so.user.id =?1 and so.dateOrdered =?2")
List<SalesTransaction> findSalesTransactionBySalesOrder_User(Long userId, LocalDate date);

 @Query("select  st from  SalesTransaction  st inner  join  SalesOrder" +
         "  so on st.salesOrder.id = so.id where so.dateOrdered between ?1 and ?2")
    List<SalesTransaction> findSalesTransactionByQuantitySold(LocalDate start, LocalDate end);
}
