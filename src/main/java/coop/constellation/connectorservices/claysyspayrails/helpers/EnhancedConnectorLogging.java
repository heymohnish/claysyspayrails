package coop.constellation.connectorservices.claysyspayrails.helpers;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;
import com.xtensifi.dspco.ConnectorMessage;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * A simple wrapper around ConnectorLogging that looks for global params and
 * application.properties settings: EnableConnectorLogging = true in the global
 * params will
 * turn logging on without needing to redeploy connector connector.local=TRUE
 * will send messages to slf4j otherwise they will go to the original
 * ConnectorLogging
 * Error and Fatal always log
 */
@Slf4j
public class EnhancedConnectorLogging extends ConnectorLogging {
    /**
     * This method enables the connector logging to be turned on or off with a
     * global connector param, 'EnableConnectorLogging' EnableConnectorLogging =
     * true --> logging is turned
     * on. EnableConnectorLogging = false (or any other value) --> logging is turned
     * off. If the
     * value pair is missing, logging is turned on by default
     * 
     * @param connectorMessage
     */
    public boolean isEclEnabled(ConnectorMessage connectorMessage) {
        Boolean eclEnabled = false;
        try {
            CustomData params = connectorMessage.getConnectorParametersResponse().getParameters();

            List<ValuePair> list = params.getValuePair();
            ValuePair eclEnabledPair = list.stream()
                    .filter(val -> val.getName().equalsIgnoreCase("EnableConnectorLogging")).findFirst().orElse(null);
            if (eclEnabledPair != null) {
                eclEnabled = eclEnabledPair.getValue().equalsIgnoreCase("true");
            }
        } catch (Exception e) {
            eclEnabled = false;
        }

        return eclEnabled;

    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void debug(ConnectorMessage connectorMessage, String message) {
        if (isEclEnabled(connectorMessage)) {
            super.debug(connectorMessage, message);
        } else {
            log.debug(message);
        }
    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void error(ConnectorMessage connectorMessage, String message) {

        if (isEclEnabled(connectorMessage)) {
            super.error(connectorMessage, message);
        } else {
            log.error(message);
        }

    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void fatal(ConnectorMessage connectorMessage, String message) {

        if (isEclEnabled(connectorMessage)) {
            super.fatal(connectorMessage, message);
        } else {
            log.error(message);
        }

    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void info(ConnectorMessage connectorMessage, String message) {
        if (isEclEnabled(connectorMessage)) {
            super.info(connectorMessage, message);
        } else {
            log.info(message);
        }
    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void trace(ConnectorMessage connectorMessage, String message) {
        if (isEclEnabled(connectorMessage)) {
            super.trace(connectorMessage, message);
        } else {
            log.trace(message);
        }
    }

    /**
     *
     * @param connectorMessage
     * @param message
     */
    @Override
    public void warn(ConnectorMessage connectorMessage, String message) {
        if (isEclEnabled(connectorMessage)) {
            super.warn(connectorMessage, message);
        } else {
            log.warn(message);
        }
    }
}