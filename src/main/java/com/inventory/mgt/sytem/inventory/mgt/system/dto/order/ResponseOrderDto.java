package com.inventory.mgt.sytem.inventory.mgt.system.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDto {
   private  float amount;
   private Date orderedDate;
   private  String  orderDescription;
   private Long order;
   private  String message;

}
