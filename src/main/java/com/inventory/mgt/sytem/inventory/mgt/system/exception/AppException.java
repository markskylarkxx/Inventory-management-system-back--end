package com.inventory.mgt.sytem.inventory.mgt.system.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@Data
@AllArgsConstructor


public class AppException extends  RuntimeException{
    public AppException(String message) {
        super(message);
    }

}
