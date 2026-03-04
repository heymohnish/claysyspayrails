package coop.constellation.connectorservices.claysyspayrails;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;


import coop.constellation.connectorservices.claysyspayrails.controller.BaseParamsSupplier;
import coop.constellation.connectorservices.claysyspayrails.helpers.EnhancedConnectorLogging;
import coop.constellation.connectorservices.claysyspayrails.helpers.StdoutConnectorLogging;


@Configuration
public class BeansConfig {

    @Bean
    @Profile("!local")
    ConnectorLogging connectorLogging() {
        return new EnhancedConnectorLogging();
    }
 @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
    @Bean
    @Profile("local")
    ConnectorLogging localConnectorLogging() {
        return new StdoutConnectorLogging();
    }

    /**
     * Set up extra params that this connector should use as a base for every
     * request.
     */
    @Bean
    BaseParamsSupplier baseParamsSupplier() {
        return () -> Map.of("localCpConnectionInitSql", "SET TIME ZONE 'UTC';");
    }

    @Bean
    ConnectorConfig connectorConfig() {
        return new ConnectorConfig();
    }

}