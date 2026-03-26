// package coop.constellation.connectorservices.claysyspayrails.handlers;

// import java.io.IOException;
// import java.text.ParseException;
// import java.util.Map;

// import org.springframework.stereotype.Service;

// import com.xtensifi.connectorservices.common.workflow.ConnectorState;
// import com.xtensifi.dspco.ConnectorMessage;
// import com.xtensifi.dspco.UserData;

// import coop.constellation.connectorservices.claysyspayrails.service.ClaysysPayrailsImp;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public abstract Aclass ExternalCallMethodHandler extends HandlerBase {

//     private ClaysysPayrailsImp BasicTemplateSvc;
//     private String extResponse;

//     @Override
//     public void generateResponse(Map<String, String> parms, UserData userData, ConnectorMessage connectorMessage)
//             throws Exception {
//         if (parms.get("url") != null) {
//             // REQUIRED FORMAT - Is handled by httpRequest;
//             BasicTemplateSvc = new ClaysysPayrailsImp();
//             BasicTemplateSvc.init();
//             String extResponse = null;
//             String response = BasicTemplateSvc.requestExternalData(parms.get("url"), "sort",
//                     parms.get("sort"), connectorMessage);

//             extResponse = "{\"response\": " + response + "}";
//             connectorMessage.setResponse(response);
//         } else {
//             // REQUIRED FORMAT - "{\"response\": {YOUR OBJECT HERE}}" as JSON string;
//             extResponse = "{\"response\": {\"data\": {}, \"success\": false}}";
//         }
//         // ==========================================================================================================
//         // END - FUNCTION TO CALL EXTERNAL WEB SERVER
//         // BEGIN - SET RESPONSE - Your Code here.
//         // ==========================================================================================================
//         connectorMessage.setResponse(extResponse);
//     }

//     @Override
//     public String generateResponse(Map<String, String> parms, ConnectorState connectorState)
//             throws IOException, ParseException {
//         // TODO Auto-generated method stub
//         return null;
//     }

// }
