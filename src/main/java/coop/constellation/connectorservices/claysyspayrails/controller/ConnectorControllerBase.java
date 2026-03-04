    package coop.constellation.connectorservices.claysyspayrails.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;
import com.xtensifi.dspco.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import coop.constellation.connectorservices.claysyspayrails.handlers.HandlerLogic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

@Component
public class ConnectorControllerBase {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.objectMapper = om;
    }

    private ConnectorLogging clog;

    @Autowired
    public void setConnectorLogging(ConnectorLogging cl) {
        this.clog = cl;
    }

    @Autowired
    public ConnectorControllerBase() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    private BaseParamsSupplier baseParamsSupplier;

    @Autowired
    public void setBaseParamsSupplier(BaseParamsSupplier supplier) {
        this.baseParamsSupplier = supplier;
    }
    /**
     * Boilerplate method for handling the connector message
     * 
     * @param logPrefix     A prefix for log messages and stats reasons
     * @param connectorJson The raw JSON for the request connector message
     * @param handlerLogic  The custom logic for generating a response
     * @return a response connector message
     */
    ConnectorMessage handleConnectorMessage(final String logPrefix, final String connectorJson,
            final HandlerLogic handlerLogic) {

        ConnectorMessage connectorMessage = null;
        ResponseStatusMessage responseStatusMessage = new ResponseStatusMessage();
        try {

            connectorMessage = objectMapper.readValue(connectorJson, ConnectorMessage.class);
            clog.info(connectorMessage, "this is the incoming json: " + connectorJson);

            UserData userData = connectorMessage.getExternalServicePayload().getUserData();

            final Map<String, String> allParams = getAllParams(connectorMessage, baseParamsSupplier.get());

            // set default success message.
            responseStatusMessage.setStatus("Success");
            responseStatusMessage.setStatusCode("200");
            responseStatusMessage.setStatusDescription("Success");
            responseStatusMessage.setStatusReason(logPrefix + "Has responded.");

            handlerLogic.generateResponse(allParams, userData, connectorMessage);

        } catch (Exception ex) {
            clog.fatal(connectorMessage, "caught exception in controller base " + ex.getMessage());
            connectorMessage.setResponse("{}");

            responseStatusMessage = new ResponseStatusMessage() {
                {
                    setStatus("ERROR");
                    setStatusCode("500");
                    setStatusDescription("Failed");
                    setStatusReason(logPrefix + "Has Failed.");
                }
            };
        } finally {
            if (connectorMessage == null) {
                clog.warn(connectorMessage,
                        "Failed to create a connector message from the request, creating a new one for the response.");
                connectorMessage = new ConnectorMessage();
            }
            clog.info(connectorMessage, "setting final response status: " + responseStatusMessage.getStatus());
            connectorMessage.setResponseStatus(responseStatusMessage);
        }

        return connectorMessage;
    }

    /**
     * Get all the value pairs out of the connector message. NOTE: if a name occurs
     * more than once, only the first occurrance is returned.
     * 
     * @param connectorMessage the request connector message
     * @return a Map of the value pairs
     */
    public static Map<String, String> getAllParams(final ConnectorMessage connectorMessage,
            Map<String, String> baseParams) {
        final Map<String, String> allParams = new HashMap<>(baseParams);
        final ExternalServicePayload externalServicePayload = connectorMessage.getExternalServicePayload();
        final ConnectorParametersResponse connectorParametersResponse = connectorMessage
                .getConnectorParametersResponse();

        if (externalServicePayload != null) {
            final CustomData methodParams = externalServicePayload.getPayload();
            if (methodParams != null)
                for (ValuePair valuePair : methodParams.getValuePair()) {
                    allParams.putIfAbsent(valuePair.getName(), StringEscapeUtils.unescapeHtml4(valuePair.getValue()));
                }
        }
        if (connectorParametersResponse != null) {
            final CustomData otherParams = connectorParametersResponse.getParameters();
            if (otherParams != null) {
                for (ValuePair valuePair : otherParams.getValuePair()) {
                    allParams.putIfAbsent(valuePair.getName(), StringEscapeUtils.unescapeHtml4(valuePair.getValue()));
                }
            }
        }
        return allParams;
    }
}
