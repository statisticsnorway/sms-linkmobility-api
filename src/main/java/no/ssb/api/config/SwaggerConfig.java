package no.ssb.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by rsa on 01.06.2016.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                ;
    }

    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                "sms-linkmobility-api-klientId",
                "sms-linkmobility-api-secret",
                "sms-linkmobility-api-realm",
                "sms-linkmobility-api",
                "Legg inn SSB API Key her",
                ApiKeyVehicle.HEADER,
                "X-SSB-APIKEY",
                "," /*scope separator*/);
    }
}

