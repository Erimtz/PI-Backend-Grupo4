package com.gym.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {
//    @ExceptionHandler({ResourceNotFoundException.class})
//    public ResponseEntity<String> procesarResourceNotFoundException (ResourceNotFoundException rnfe){
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rnfe.getMessage());
//    }
}
