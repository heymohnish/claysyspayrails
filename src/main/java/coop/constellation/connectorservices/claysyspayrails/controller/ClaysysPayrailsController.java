package coop.constellation.connectorservices.claysyspayrails.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.events.RealtimeEventService;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorHubService;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestData;
import com.xtensifi.dspco.ConnectorMessage;

// impo     rt coop.constellation.connectorservices.claysyspayrails.handlers.EditTransactionHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.MultiCallHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.P2pTransferHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveAccountListHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveAccountListRefreshHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveTransactionCategoriesHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveTransactionListHandler;
import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveUserByIdHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.RetrieveUserBySocialHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.StartTransferHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.StopPaymentHandler;
// import coop.constellation.connectorservices.claysyspayrails.handlers.ValidateMemberAccountInfoHandler;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
    // @PostMapping(path = "/businessLogicMethod", consumes = "application/json", produces = "application/json")
    // public ConnectorMessage BusinessLogicMethod(@RequestBody String connectorJson) {
    //     final String logPrefix = "BasicSampleConnector.businessLogicMethod: ";

    //     BusinessLogicMethodHandler handler = new BusinessLogicMethodHandler();

    //     final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, handler);
    //     logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
    //     return connectorMessage;

    // }

    // EXTERNAL CALL METHOD
    // @PostMapping(path = "/externalCallMethod", consumes = "application/json", produces = "application/json")
    // public ConnectorMessage ExternalCallMethod(@RequestBody String connectorJson) {
    //     final String logPrefix = "BasicSampleConnector.ExternalCallMethod: ";
    //     ExternalCallMethodHandler handler = new ExternalCallMethodHandler();
    //     final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, handler);
    //     logger.info(connectorMessage, "Final: " + connectorMessage.getResponse());
    //     return connectorMessage;
    // }
    @CrossOrigin
    @PostMapping(path = "/getPartyById", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getPartyById(@RequestBody final ConnectorMessage connectorMessage) {

        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(HttpStatus.OK);
        logger.info(connectorMessage, "Initial: ");
        connectorHubService
                .executeConnector(connectorMessage, new ConnectorRequestData("kivapublic", "1.0", "getPartyById"))
                .thenApply(this.handleResponseEntity(retrieveUserByIdHandler))
                .thenApplyAsync(connectorHubService.completeAsync())
                .exceptionally(exception -> connectorHubService.handleAsyncFlowError(exception, connectorMessage,
                        "Error running retrieveUserById: " + exception.getMessage()));
        logger.info(connectorMessage, "Final: " + responseEntity.build());
        return responseEntity.build();

    }
}
