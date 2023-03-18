package com.inventory.mgt.sytem.inventory.mgt.system.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private  boolean success;
    private  String message;
}
