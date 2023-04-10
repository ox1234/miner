package org.example.constant;

import java.util.regex.Pattern;

public class MyBatis {
    public static String mybatisMapperAnnotation = "Lorg/apache/ibatis/annotations/Mapper;";
    public static String mybatisSelectAnnotation = "Lorg/apache/ibatis/annotations/Select;";
    public static String mybatisParamAnnotation = "Lorg/apache/ibatis/annotations/Param;";

    public static Pattern mybatisPlaceHolderPattern = Pattern.compile("\\$\\{([a-z0-9_]*)\\}");
    ;
}
