package com.inventory.mgt.sytem.inventory.mgt.system.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
@Data

@AllArgsConstructor
public class ApiError {
    private final String message;
    private  final HttpStatus httpStatus;
    private  final ZonedDateTime timeStamp;
}
