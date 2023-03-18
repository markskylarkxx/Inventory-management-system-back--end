package com.inventory.mgt.sytem.inventory.mgt.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


       @ExceptionHandler(ResourceNotFoundException.class)
       @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        HttpStatus notFound = HttpStatus.NOT_FOUND;
           String description = request.getDescription(false);
           // 1) Create a payload
           // 2) Return response entity
        final ApiError apiError = new ApiError(ex.getMessage(),  
                notFound, ZonedDateTime.now(ZoneId.of  ("Z")));
        return  new ResponseEntity<>(apiError, notFound);
       }

      @ExceptionHandler(AppException.class)
      @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public  ResponseEntity<?> handleAppException(AppException ex){
        HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;
        final  ApiError apiError = new ApiError(ex.getMessage(),
                serverError,ZonedDateTime.now(ZoneId.of("Z")) );
        return  new ResponseEntity<>(apiError, serverError);

    }
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleInvalidArgException (MethodArgumentNotValidException ex ){
        // create an object  of the map;
            Map<String, String> errorMap = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error->errorMap.
                    put(error.getField(), error.getDefaultMessage()));
            return  errorMap;
     }
}
