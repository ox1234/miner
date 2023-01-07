package org.example.config;

import java.util.HashSet;
import java.util.Set;

public class SpringController {
    public static Set<String> controllerAnnotations = new HashSet<>();

    static{
        controllerAnnotations.add("Lorg/springframework/web/bind/annotation/RestController;");
        controllerAnnotations.add("");
    }
}
