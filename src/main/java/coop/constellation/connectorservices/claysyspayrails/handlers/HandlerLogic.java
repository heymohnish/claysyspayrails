package coop.constellation.connectorservices.claysyspayrails.handlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

/**
 * Interface for the custom logic to generate a response
 */

public interface HandlerLogic {
    String generateResponse(final Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException;

    void generateResponse(final Map<String, String> parms, UserData userData, ConnectorMessage cm) throws Exception;
}