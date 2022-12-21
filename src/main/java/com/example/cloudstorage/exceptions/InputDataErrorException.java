package com.example.cloudstorage.exceptions;

import com.example.cloudstorage.dto.ErrorResponse;

public class InputDataErrorException extends RuntimeException {
    public InputDataErrorException(ErrorResponse response) {
        super(response.toString());
    }
}
