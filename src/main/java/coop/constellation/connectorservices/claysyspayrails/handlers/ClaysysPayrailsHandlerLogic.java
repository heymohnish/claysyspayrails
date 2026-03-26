package coop.constellation.connectorservices.claysyspayrails.handlers;

import com.xtensifi.connectorservices.common.workflow.ConnectorState;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * Interface for the custom logic to generate a response for a workflow method
 */
@FunctionalInterface
public interface ClaysysPayrailsHandlerLogic {

    String generateResponse(final Map<String, String> parms, final ConnectorState connectorState)
            throws IOException, ParseException;

}
