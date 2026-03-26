package coop.constellation.connectorservices.claysyspayrails.helpers;

import com.xtensifi.connectorservices.common.events.RealtimeEventService;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.dspco.ConnectorMessage;

import java.util.List;

public interface RealtimeEvents {
    void send(String source, String eventName, List<String> affectedItems, ConnectorMessage connectorMessage,
            ConnectorLogging clog, RealtimeEventService realtimeEventService);
}
