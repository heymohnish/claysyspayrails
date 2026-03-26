package coop.constellation.connectorservices.claysyspayrails.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.ConnectorParametersResponse;
import com.xtensifi.dspco.ExternalServicePayload;
import com.xtensifi.dspco.ResponseStatusMessage;

import coop.constellation.connectorservices.claysyspayrails.handlers.ClaysysPayrailsHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.HandlerLogic;
import coop.constellation.connectorservices.claysyspayrails.handlers.ClaysysPayrailsHandlerLogic;
import lombok.NonNull;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    /**
     * Boilerplate method for handling the connector message
     * 
     * @param logPrefix     A prefix for log messages and stats reasons
     * @param connectorJson The raw JSON for the request connector message
     * @param handlerLogic  The custom logic for generating a response
     * @return a response connector message
     */
    ConnectorMessage handleConnectorMessage(final String logPrefix,
            final String connectorJson,
            final HandlerLogic handlerLogic) {
        ConnectorMessage connectorMessage = null;
        ResponseStatusMessage responseStatusMessage = null;
        try {
            connectorMessage = objectMapper.readValue(connectorJson, ConnectorMessage.class);

            final Map<String, String> allParams = getAllParams(connectorMessage);

            final String userId = connectorMessage.getExternalServicePayload().getUserData().getUserId();

            final String response = handlerLogic.generateResponse(allParams, userId, connectorMessage);

            connectorMessage.setResponse("{\"response\": " + response + "}");

            responseStatusMessage = new ResponseStatusMessage() {
                {
                    setStatus("OK");
                    setStatusCode("200");
                    setStatusDescription("Success");
                    setStatusReason(logPrefix + "Has responded.");
                }
            };
        } catch (Exception ex) {
            clog.error(connectorMessage, logPrefix + ex.getMessage());
            responseStatusMessage = new ResponseStatusMessage() {
                {
                    setStatus("ERROR");
                    setStatusCode("500");
                    setStatusDescription("Failed");
                    setStatusReason(logPrefix + ": " + ex.toString());
                }
            };
            connectorMessage.setResponse("{\"response\":{\"success\":false}}");

        } finally {
            if (connectorMessage == null) {
                clog.warn(connectorMessage,
                        "Failed to create a connector message from the request, creating a new one for the response.");
                connectorMessage = new ConnectorMessage();
            }
            connectorMessage.setResponseStatus(responseStatusMessage);
        }
        return connectorMessage;
    }

    public Function<ConnectorState, ConnectorState> handleResponseEntity(ClaysysPayrailsHandlerLogic handler) {
        return connectorState -> {
            ConnectorMessage connectorMessage = connectorState.getConnectorMessage();
            clog.info(connectorMessage, "inside handle response entity");

            final Map<String, String> allParams = getAllParams(connectorMessage);

            String response = "{}";
            try {
                response = handler.generateResponse(allParams, connectorState);
                clog.info(connectorMessage, "this is the final response " + response);

            } catch (Exception e) {
                clog.error(connectorState.getConnectorMessage(), e.getMessage());
            }

            connectorState.setResponse("{\"response\": " + response + "}");
            return connectorState;
        };
    }

    ConnectorMessage getErrorResponse(@NonNull final String connectorJson, @NonNull final String message)
            throws IOException {
        ConnectorMessage connectorMessage = objectMapper.readValue(connectorJson, ConnectorMessage.class);
        connectorMessage.setResponseStatus(new ResponseStatusMessage() {
            {
                setStatus("ERROR");
                setStatusCode("500");
                setStatusDescription("Failed");
                setStatusReason(message);
            }
        });
        connectorMessage.setResponse("{\"response\":{\"success\":false}}");
        return connectorMessage;
    }

    /**
     * Get all the value pairs out of the connector message.
     * NOTE: if a name occurs more than once, only the first occurrance is returned.
     * 
     * @param connectorMessage the request connector message
     * @return a Map of the value pairs
     */
    public static Map<String, String> getAllParams(final ConnectorMessage connectorMessage) {
        final Map<String, String> allParams = new HashMap<>();
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
