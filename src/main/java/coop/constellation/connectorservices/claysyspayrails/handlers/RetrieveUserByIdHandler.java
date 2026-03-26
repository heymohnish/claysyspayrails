package coop.constellation.connectorservices.claysyspayrails.handlers;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorResponse;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetrieveUserByIdHandler extends HandlerBase implements ClaysysPayrailsHandlerLogic {

    // private final ConnectorLogging logger;

    @Override
    public String generateResponse(final Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException {
        // Retrieve and log userId
        UserData userData = connectorState.getConnectorMessage().getExternalServicePayload().getUserData();
        String userId = userData.getUserId();
        // logger.info(connectorState.getConnectorMessage(), userId);

        List<ConnectorResponse> connectorResponseList = connectorState.getConnectorResponseList().getResponses();

        // This is how you capture the response
        String resp = "{\"response\": 1}";
        for (ConnectorResponse connectorResponse : connectorResponseList) {

            // This is how you retrieve the name of the connector
            String name = connectorResponse.getConnectorRequestData().getConnectorName();
            // logger.info(connectorState.getConnectorMessage(), name);

            // This is how you capture the response
            String data = connectorResponse.getResponse();

            // Parse the response how ever you see fit
            resp = "{\"response\": " + data + "}";
            // logger.info(connectorState.getConnectorMessage(), resp);
        }

        // This is required, and is how you set the response for a workflow method
        connectorState.setResponse(resp);
        return resp;
    }

    @Override
    public String generateResponse(Map<String, String> parms, String userId, ConnectorMessage connectorMessage)
            throws IOException, ParseException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateResponse'");
    }
}