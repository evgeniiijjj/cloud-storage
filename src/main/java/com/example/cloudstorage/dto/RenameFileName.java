package com.example.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RenameFileName {

    @JsonProperty("filename")
    String fileName;
}
