package coop.constellation.connectorservices.claysyspayrails.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenApiResponse {
    private int statusCode;
    private String message;
    private BitcureTokenData data;
    private Object error;

    // getters & setters
}
