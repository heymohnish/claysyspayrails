package coop.constellation.connectorservices.claysyspayrails.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@AllArgsConstructor
public class JacksonConfiguration {

    private final ObjectMapper objectMapper;

    /**
     * Further configures the object mapper that spring boot configures.
     */
    @PostConstruct
    public ObjectMapper configureObjectMapper() {
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); // don't use IEEE floats for
                                                                                // deserialization
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN); // don't use scientific notation for
                                                                              // serializing BigDecimals
        return objectMapper;
    }
}
