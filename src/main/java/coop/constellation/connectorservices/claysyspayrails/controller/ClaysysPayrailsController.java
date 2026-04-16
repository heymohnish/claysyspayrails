package coop.constellation.connectorservices.claysyspayrails.controller;

import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorHubService;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestData;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestParams;
import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.claysyspayrails.handlers.BitcureExternalHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.ExternalCallMethodHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveAccountListHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveUserByIdHandler;
import lombok.RequiredArgsConstructor;
// NOTE: Format for "@RequestMapping"
// RequestMapping("/externalConnector/[Connector Name]/[Connector Version Number]")

@RestController
@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/externalConnector/ClaysysPayrails/1.0")
public class ClaysysPayrailsController extends ConnectorControllerBase {

    // Following method is required in order for your controller to pass health
    // checks.
    // If the server cannot call awsping and get the expected response yur app will
    // not be active.
    @Autowired
    // ConnectorHubService is required for workflow methods
    private ConnectorHubService connectorHubService;
    private final RetrieveUserByIdHandler retrieveUserByIdHandler;
    private final RetrieveAccountListHandler retrieveAccountListHandler;
    private final ExternalCallMethodHandler externalCallMethodHandler;
    private final BitcureExternalHandler bitcureExternalHandler;

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
@CrossOrigin
    // ORIGINAL BUSINESS LOGIC METHOD
    @PostMapping(path = "/transaction", consumes = "application/json", produces = "application/json")
    public ConnectorMessage BusinessLogicMethod(@RequestBody String connectorMessageRequest)  {
        final String logPrefix = "BasicSampleConnector.businessLogicMethod: ";
        // logger.info(null, "BasicSampleConnector.businessLogicMethod Initial: ");
        final ConnectorMessage connectorMessage = this.handleConnectorMessage(logPrefix, connectorMessageRequest, externalCallMethodHandler);
        logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
        return connectorMessage;
    }
    @CrossOrigin
    @PostMapping(path = "/getToken", consumes = "application/json", produces = "application/json")
    public ConnectorMessage getToken(@RequestBody String connectorMessageRequest) {

        final String logPrefix = "BasicSampleConnector.businessLogicMethod: ";
        // logger.info(null, "BasicSampleConnector.businessLogicMethod Initial: ");
        final ConnectorMessage connectorMessage = this.handleConnectorMessage(logPrefix, connectorMessageRequest, bitcureExternalHandler);
        logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
        return connectorMessage;

    }
    @CrossOrigin
    @PostMapping(path = "/getPartyById", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getPartyById(@RequestBody final ConnectorMessage connectorMessage) {

        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(HttpStatus.OK);
        logger.info(connectorMessage, "Initial: ");
        try {
            connectorHubService
                    .executeConnector(connectorMessage, new ConnectorRequestData("kivapublic", "1.0", "getPartyById"))
                    .thenApply(this.handleResponseEntity(retrieveUserByIdHandler))
                    .thenApplyAsync(connectorHubService.completeAsync())
                    .exceptionally(exception -> connectorHubService.handleAsyncFlowError(exception, connectorMessage,
                    "Error running retrieveUserById: " + exception.getMessage()));
            logger.info(connectorMessage, "Final: " + responseEntity.build());
        } catch (Exception e) {
            logger.error(connectorMessage, "Error in getPartyById: " + e.getMessage());
        }

        return responseEntity.build();

    }
    
    // @CrossOrigin
    // @PostMapping(path = "/getAccountDetails", consumes = "application/json", produces = "application/json")
    // public ResponseEntity<String> getAccountDetails(@RequestBody final ConnectorMessage connectorMessage) {
    //     logger.info(connectorMessage, connectorMessage.toString());
    //     ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(HttpStatus.OK);
    //     connectorHubService
    //             .initAsyncConnectorRequest(connectorMessage,
    //                     new ConnectorRequestData("kivapublic", "1.0", "getAccounts"))
    //             .thenApply(this.retrieveFilterAcctParams(connectorMessage))
    //             .thenApply(connectorHubService.callConnectorAsync())
    //             .thenApplyAsync(connectorHubService.waitForConnectorResponse())
    //             .thenApply(this.handleResponseEntity(retrieveAccountListHandler))
    //             .thenApplyAsync(connectorHubService.completeAsync())
    //             .exceptionally(exception -> connectorHubService.handleAsyncFlowError(exception, connectorMessage,
    //             "Error running retrieveAccountList: " + exception.getMessage()));

    //     return responseEntity.build();

    // }

    private Function<ConnectorRequestParams, ConnectorRequestParams> retrieveFilterAcctParams(
            ConnectorMessage connectorMessage) {

        return connectorRequestParams -> {
            // Gets a list of all paramters passed into your connector call
            final Map<String, String> allParams = getAllParams(connectorMessage);

            logger.info(connectorMessage, "all params GC: " + allParams);

            // Finding the value of the filters parameter passed from the tile
            String strFilter = allParams.getOrDefault("filters", "");

            if (!strFilter.equals("")) {
                connectorRequestParams.addNameValue("accountFilter", strFilter);
            }

            // Returns our list of parameters to pass into the kivapublic call
            return connectorRequestParams;
        };
    }
}
