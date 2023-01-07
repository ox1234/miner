package org.example.rule;

 import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
 import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

public class RuleUtil {
    public static Root getRule(String jsonStr) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonStr, Root.class);
    }
}
