package coop.constellation.connectorservices.claysyspayrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
     private String transactionId;
    private String accountId;
    private String type;
    private Amount amount;
    private String description;
    private String dateTimePosted;
    private String status;
    private String source;
    private CustomData customData;
    private Amount interestAmount;
    private Amount principalAmount;
}