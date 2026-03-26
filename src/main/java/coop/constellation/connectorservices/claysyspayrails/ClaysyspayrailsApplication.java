package coop.constellation.connectorservices.claysyspayrails;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// @SpringBootApplication
// public class ClaysyspayrailsApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(ClaysyspayrailsApplication.class, args);
// 	}

// }

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ClaysyspayrailsApplication {

    public static void main(String[] args) {
         SpringApplication.run(ClaysyspayrailsApplication.class, args);
        // SpringApplication app = new SpringApplication(ClaysyspayrailsApplication.class);
        // app.setDefaultProperties(Collections.singletonMap("server.port", "9000"));
		// app.run(args);  
    }
}