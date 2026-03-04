package coop.constellation.connectorservices.claysyspayrails.controller;

import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import coop.constellation.connectorservices.claysyspayrails.handlers.BusinessLogicMethodHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.ExternalCallMethodHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.GetLocalTextFileHandler;
import lombok.AllArgsConstructor;

// NOTE: Format for "@RequestMapping"
// RequestMapping("/externalConnector/[Connector Name]/[Connector Version Number]")
@RestController
@Controller
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/externalConnector/ClaysysPayrails/1.0")
public class ClaysysPayrailsController extends ConnectorControllerBase {

    // Following method is required in order for your controller to pass health
    // checks.
    // If the server cannot call awsping and get the expected response yur app will
    // not be active.
     @CrossOrigin
    @GetMapping("/awsping")
    public String getAWSPing() {
        return "{ping: 'pong'}";
    }
    @CrossOrigin
    @PostMapping("/awsping1")
    public String PostPing(@RequestBody String connectorJson) {
        return "{ping: 'pong'}";
    }
    // Logger for this object
    private ConnectorLogging logger = new ConnectorLogging();

    // ORIGINAL BUSINESS LOGIC METHOD
    @PostMapping(path = "/businessLogicMethod", consumes = "application/json", produces = "application/json")
    public ConnectorMessage BusinessLogicMethod(@RequestBody String connectorJson) {
        final String logPrefix = "BasicSampleConnector.businessLogicMethod: ";

        BusinessLogicMethodHandler handler = new BusinessLogicMethodHandler();

        final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, handler);
        logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
        return connectorMessage;

    }

    // EXTERNAL CALL METHOD
    @PostMapping(path = "/externalCallMethod", consumes = "application/json", produces = "application/json")
    public ConnectorMessage ExternalCallMethod(@RequestBody String connectorJson) {
        final String logPrefix = "BasicSampleConnector.ExternalCallMethod: ";

        ExternalCallMethodHandler handler = new ExternalCallMethodHandler();

        final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, handler);
        logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
        return connectorMessage;

    }

    // GET LOCAL TEXT FILE METHOD
    @PostMapping(path = "/getLocalTextFile", consumes = "application/json", produces = "application/json")
    public ConnectorMessage GetLocalTextFile(@RequestBody String connectorJson) {
        final String logPrefix = "BasicSampleConnector.GetLocalTextFile: ";

        GetLocalTextFileHandler handler = new GetLocalTextFileHandler();

        final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, handler);
        logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
        return connectorMessage;

    }
}
