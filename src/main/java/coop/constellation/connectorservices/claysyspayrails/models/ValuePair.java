package coop.constellation.connectorservices.claysyspayrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValuePair {
    public String name;
    public String value;
}   
    
