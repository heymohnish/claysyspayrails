package coop.constellation.connectorservices.claysyspayrails.models;

import lombok.Data;

@Data
public class TokenRequest {
    private String projectKey;
    private String projectSecret;
}