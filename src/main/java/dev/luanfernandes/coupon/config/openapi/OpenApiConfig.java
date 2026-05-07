package dev.luanfernandes.coupon.config.openapi;

import static java.util.stream.Stream.of;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "Coupon API",
                        version = "0.0.1",
                        description = "API para gerenciamento de cupons de desconto",
                        contact =
                                @Contact(
                                        name = "Luan Fernandes",
                                        email = "souluanf@icloud.com",
                                        url = "https://linkedin.com/in/souluanf")),
        externalDocs =
                @ExternalDocumentation(
                        description = "Git repository",
                        url = "https://github.com/souluanf/coupon-api"))
@Configuration
public class OpenApiConfig {

    @Value(value = "${openapi-servers-urls:http://localhost:8080}")
    private String[] openApiServersUrls;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openApi = new OpenAPI();
        of(openApiServersUrls).forEach(url -> openApi.addServersItem(new Server().url(url)));
        return openApi;
    }
}