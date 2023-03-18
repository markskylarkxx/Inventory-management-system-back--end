package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import com.inventory.mgt.sytem.inventory.mgt.system.model.SalesTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SalesTransactionDto {
    private List<SalesTransaction> list;

}
