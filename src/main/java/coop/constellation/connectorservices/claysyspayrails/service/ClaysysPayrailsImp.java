package coop.constellation.connectorservices.claysyspayrails.service;

import javax.inject.Singleton;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;
import com.xtensifi.connectorservices.common.accesslayer.HttpRequest;
import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.claysyspayrails.handlers.ClaysysPayrailsHandler;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;

@Singleton
public class ClaysysPayrailsImp implements ClaysysPayrailsHandler {

    ConnectorMessage connectorMessage = null;

    /* Generic logger for the class */
    private ConnectorLogging logger = new ConnectorLogging();

    /* Init() - Initialize BasicTemplateImp */
    public void init() {
        logger.info(connectorMessage, "BasicTemplateImp.Init() - Ran");

    }

    /*
     * requestExternalData() - Simple Method to grab data from external(to
     * Consetellation) backend end.
     */
    public String requestExternalData(String url, String ParamName, String ParamValue,
            ConnectorMessage connectorMessage) {
        logger.info(connectorMessage, "BasicTemplateImp.Method1() - Ran");
        String extResponse = null;

        // CREATE HTTP REQUEST - HELPER IS HERE TO MAKE EXTERNAL CALLS
        HttpRequest dspHttpRequest = new HttpRequest();

        try {

            // HTTP REQUEST - BUILD & SET HEADER - For External call
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            dspHttpRequest.setHeaders(headers);

            // HTTP REQUEST - SET IS HTTP
            dspHttpRequest.setIsHttp(true);

            // HTTP REQUEST - SET URL
            dspHttpRequest.setUrl(url);

            // HTTP REQUEST - SET METHOD - GET OR POST
            dspHttpRequest.setMethod("GET");

            // HTTP REQUEST - SET URL Parameters
            String urlParameters = "";
            if (ParamValue != "" && ParamValue != null) {
                urlParameters = ParamName.toLowerCase() + "=" + URLEncoder.encode(ParamValue, "UTF-8");
                dspHttpRequest.setUrlParameters(urlParameters);
            }
            // EXECUTE HTTP REQUEST - NOTE: Response is in the format needed {\"response\":
            // {RESPONSE IS HERE}}"
            extResponse = dspHttpRequest.executeRequest();

        } catch (Exception ex) {
            logger.error(connectorMessage, "BasicTemplateImp.requestExternalDate - Exception:" + ex.getMessage());
            extResponse = "{\"data\": {}, \"success\": false}";
        }
        // NOTE External Response format is:
        return extResponse;
    }
}
