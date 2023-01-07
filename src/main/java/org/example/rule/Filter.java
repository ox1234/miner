package org.example.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Filter {
    public ArrayList<String> controllerClassTags;
    @JsonProperty("SpringBootApplicationTag")
    public ArrayList<String> springBootApplicationTag;
    @JsonProperty("SpringBootConfigTags")
    public ArrayList<String> springBootConfigTags;
    public ArrayList<String> requestMethodTags;
    public ArrayList<String> sources;
}
