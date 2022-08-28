package com.abnamro.nl.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAttributes {

    private String code;
    private String message;
    private int status;
    private List<String> params;
}
