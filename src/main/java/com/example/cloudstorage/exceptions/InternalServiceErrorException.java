package com.example.cloudstorage.exceptions;

import com.example.cloudstorage.dto.ErrorResponse;

public class InternalServiceErrorException extends RuntimeException {
    public InternalServiceErrorException(ErrorResponse response) {
        super(response.toString());
    }
}
