package com.abnamro.nl.models;

import lombok.Data;

import java.util.List;

@Data
public class Ingredients {
    private List<String> includes;
    private List<String> excludes;
}
