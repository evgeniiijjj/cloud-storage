package com.example.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private int id;

    public String toString() {
        return "{\"message\": \"" + message
                + "\", \"id\": " + id
                + "}";
    }
}
