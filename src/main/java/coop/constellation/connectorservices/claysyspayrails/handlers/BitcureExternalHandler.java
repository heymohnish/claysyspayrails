package coop.constellation.connectorservices.claysyspayrails.handlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorResponse;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.claysyspayrails.models.TokenApiResponse;
import coop.constellation.connectorservices.claysyspayrails.models.TokenRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BitcureExternalHandler extends HandlerBase implements ClaysysPayrailsHandlerLogic {

    private final ConnectorLogging logger;

    @Override
    public String generateResponse(final Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException {
        List<ConnectorResponse> connectorResponseList = connectorState.getConnectorResponseList().getResponses();

        // This is how you capture the response
        String resp = "{\"response\": 1}";
        for (ConnectorResponse connectorResponse : connectorResponseList) {

            // This is how you retrieve the name of the connector
            String name = connectorResponse.getConnectorRequestData().getConnectorName();
            logger.info(connectorState.getConnectorMessage(), name);

            // This is how you capture the response
            String data = connectorResponse.getResponse();

            // Parse the response how ever you see fit
            resp = "{\"response\": " + data + "}";
            logger.info(connectorState.getConnectorMessage(), resp);
        }

        // This is required, and is how you set the response for a workflow method
        connectorState.setResponse(resp);
        return resp;
    }

    @Override
    public String generateResponse(Map<String, String> parms, String userId, ConnectorMessage connectorMessage)
            throws IOException, ParseException {

        try {

            String url = "https://devapi.bitcure.com/api/Access/v1/ValidateUser";

            RestTemplate restTemplate = new RestTemplate();

            // 🔹 Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getToken());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("accept", "*/*");

            // 🔹 Form Data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("email", parms.get("email"));
            body.add("source", "PatientApp");

            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while generating response", e);
        }
    }

    public String getToken() {

        String url = "https://devapi.bitcure.com/api/Token/GetToken";

        RestTemplate restTemplate = new RestTemplate();

        // Request body
        TokenRequest request = new TokenRequest();
        request.setProjectKey("bitcure360");
        request.setProjectSecret("bitcure360");

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "*/*");

        HttpEntity<TokenRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TokenApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                TokenApiResponse.class
        );

        return response.getBody().getData().getAccessToken();
    }
}
