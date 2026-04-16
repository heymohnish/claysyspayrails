package coop.constellation.connectorservices.claysyspayrails.models;

import lombok.Data;

 @Data
public class Amount {
    public String value;
    public String currencyCode;
    public String exchangeRate;
}
