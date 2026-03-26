package coop.constellation.connectorservices.claysyspayrails.helpers;

import java.util.concurrent.CompletableFuture;

import com.xtensifi.connectorservices.common.workflow.ConnectorRequestData;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestParams;
import com.xtensifi.connectorservices.common.workflow.ConnectorResponse;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;

public class ClaysysPayrailsHelpers {
    public static CompletableFuture<ConnectorState> createNewConnectorStateFuture(ConnectorMessage connectorMessage) {
        return CompletableFuture.supplyAsync(() -> {
            return createNewConnectorState(connectorMessage);
        });
    }

    public static ConnectorState createNewConnectorState(ConnectorMessage connectorMessage) {
        ConnectorRequestData thisConnectorData = ConnectorRequestData.fromConnectorMessage(connectorMessage);
        ConnectorRequestParams connectorRequestParams = new ConnectorRequestParams(connectorMessage, thisConnectorData);
        ConnectorState connectorState = new ConnectorState(connectorRequestParams);
        connectorState.setConnectorRequestParams(connectorRequestParams);
        String firstConnectorDetailsJson = "{  }";
        ConnectorResponse connectorResponse = new ConnectorResponse();
        connectorResponse.setResponse(firstConnectorDetailsJson);
        connectorState.addResponse(connectorResponse);
        return connectorState;
    }
}
