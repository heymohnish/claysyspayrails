package coop.constellation.connectorservices.claysyspayrails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorHubService;
import com.xtensifi.connectorservices.common.workflow.ConnectorResponse;
import coop.constellation.connectorservices.claysyspayrails.helpers.MockConnectorHubService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.charset.StandardCharsets;

@Configuration
public class LocalConnectorResponseLoaderConfig {
    @Bean
    @Profile("local")
    ConnectorHubService localConnectorHubService(@Qualifier("localConnectorLogging") ConnectorLogging clog,
            ObjectMapper mapper) {
        return new MockConnectorHubService(connectorState -> {

            String method = connectorState.getConnectorRequestParams().getConnectorRequestData().getMethod();
            String connectorName = connectorState.getConnectorRequestParams().getConnectorRequestData()
                    .getConnectorName();
            // load response
            try {
                String response = "";

                response = IOUtils.resourceToString(String.format("/%s-%s.json", connectorName, method),
                        StandardCharsets.UTF_8);

                connectorState.addResponse(new ConnectorResponse(
                        connectorState.getConnectorRequestParams().getConnectorRequestData(), response));
                return connectorState;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, clog);
    }

}
