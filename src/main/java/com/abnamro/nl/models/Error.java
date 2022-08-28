package com.abnamro.nl.models;

import lombok.Data;

import java.util.List;


@Data
public class Error {
    
    List<ErrorAttributes> errors;

}
