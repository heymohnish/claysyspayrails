package coop.constellation.connectorservices.claysyspayrails.handlers;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * Interface for the custom logic to generate a response
 */
@FunctionalInterface
public interface HandlerLogic {
    String generateResponse(final Map<String, String> parms, final String userId,
            final ConnectorMessage connectorMessage) throws IOException, ParseException;
}