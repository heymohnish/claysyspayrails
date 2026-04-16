package coop.constellation.connectorservices.claysyspayrails.models;

import lombok.Data;

@Data
public class BitcureTokenData {
    private String accessToken;
    private String refreshToken;
    private String expiration;
}
