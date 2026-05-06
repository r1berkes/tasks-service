package ie.rberkes.tasks.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addParameters("IdempotencyKey",
                                new Parameter()
                                        .in("header")
                                        .name("Idempotency-Key")
                                        .required(true))
                        .addParameters("TraceId",
                                new Parameter()
                                        .in("header")
                                        .name("X-Trace-Id")
                                        .required(true))
                );
    }
}