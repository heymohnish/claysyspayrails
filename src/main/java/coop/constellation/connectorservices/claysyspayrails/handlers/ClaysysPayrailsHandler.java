package coop.constellation.connectorservices.claysyspayrails.handlers;

import com.xtensifi.dspco.ConnectorMessage;

public interface ClaysysPayrailsHandler {
    public void init();

    public String requestExternalData(String tileParameter1, String ParamName, String ParamValue,
            ConnectorMessage connectorMessage);
}