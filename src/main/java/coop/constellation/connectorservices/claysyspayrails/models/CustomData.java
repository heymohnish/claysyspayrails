package coop.constellation.connectorservices.claysyspayrails.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomData {
     public  List<ValuePair> valuePair;
}
