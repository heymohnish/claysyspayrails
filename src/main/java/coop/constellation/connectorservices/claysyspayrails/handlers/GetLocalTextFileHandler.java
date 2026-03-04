package coop.constellation.connectorservices.claysyspayrails.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.text.ParseException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetLocalTextFileHandler extends HandlerBase {

    @Override
    public void generateResponse(Map<String, String> parms, UserData userData, ConnectorMessage connectorMessage)
            throws Exception {
        String fileText = null;
        if (parms.get("fileName") != null) {
            // // Get the file data.
            String filePath = "classpath:" + parms.get("fileName");
            File file = ResourceUtils.getFile(filePath);
            // // Check make sure has value
            if (file.exists()) {
                // // File exist read content
                String content = new String(Files.readAllBytes(file.toPath()));
                fileText = "{\"response\": {\"data\": \"" + content + "\", \"success\": false}}";
            } else {
                // // No file was found - return empty content
                fileText = "{\"response\": {\"data\": {}, \"success\": false}}";
            }

        } else {
            // // REQUIRED FORMAT - "{\"response\": {YOUR OBJECT HERE}}" as JSON string;
            fileText = "{\"response\": {\"data\": {}, \"success\": false}}";
        }

        // //
        // ==========================================================================================================
        // // END (YOUR LOGIC HERE) - FUNCTION TO GET LOCAL TEXT FILE
        // // BEGIN - SET RESPONSE - Your Code here.
        // //
        // ==========================================================================================================
        connectorMessage.setResponse(fileText);
    }

    @Override
    public String generateResponse(Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException {
        return null;
    }

}
