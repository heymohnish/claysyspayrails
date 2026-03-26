package coop.constellation.connectorservices.claysyspayrails.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xtensifi.connectorservices.common.events.RealtimeEventException;
import com.xtensifi.connectorservices.common.events.RealtimeEventService;
import com.xtensifi.connectorservices.common.events.model.EventDetail;
import com.xtensifi.connectorservices.common.events.model.EventDetailTopicData;
import com.xtensifi.connectorservices.common.events.model.EventItem;
import com.xtensifi.connectorservices.common.events.model.RealtimeEventData;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestData;
import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.claysyspayrails.helpers.RealtimeEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealtimeEventsImpl implements RealtimeEvents {

    @Override
    public void send(String source, String eventName, List<String> affectedItems, ConnectorMessage connectorMessage,
            ConnectorLogging clog, RealtimeEventService realtimeEventService) {
        RealtimeEventData eventData = new RealtimeEventData();

        eventData.setSource(source);
        eventData.setDetailType(eventName);

        EventDetail eventDetail = new EventDetail();
        String orgId = ConnectorRequestData.getConnectorParam("org", connectorMessage);
        eventDetail.setOrganizationId(orgId);
        eventDetail.setEndUserNameKey(""); // required field, can be blank
        EventDetailTopicData eventDetailTopicData = new EventDetailTopicData();

        for (String item : affectedItems) {
            EventItem eventItem = new EventItem();
            eventItem.setId(item);
            eventDetailTopicData.getAffectedItems().add(eventItem);
        }

        eventDetail.setTopicData(eventDetailTopicData);
        eventData.setDetail(eventDetail);

        try {
            clog.info(connectorMessage, "Event being sent to Real Time Event Bus: " + eventData.toJson());

            if (realtimeEventService != null) {
                realtimeEventService.sendEvent(eventData);
            } else {
                clog.error(connectorMessage, "Realtime event service is null");
            }
        } catch (JsonProcessingException jpe) {
            clog.info(connectorMessage, "Caught exception trying to transform event data to JSON!!!");
        } catch (RealtimeEventException ree) {
            clog.error(connectorMessage,
                    "Caught RealtimeEventException trying to send event that transaction was added" + ree.getMessage());
        }

    }
}
