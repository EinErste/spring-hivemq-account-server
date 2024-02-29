package org.ein.erste.iot.account.settings;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.ein.erste.iot.account.settings.auth.AuthConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "ApiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = AuthConstants.HEADER_STRING
)
public class OpenApiConfig {
    public static final String USER_CONTROLLER_TAG = "users";


    @Bean
    public OpenAPI customOpenAPI(@Value("${server.swagger.api.host}") String apiHost) {
        final Info info = new Info()
                .title("My API")
                .description("My API description.")
                .version("1.0.0");

        return new OpenAPI()
                .addServersItem(new Server().url(apiHost))
                .components(new Components())
                .addTagsItem(createTag(USER_CONTROLLER_TAG, "Users controller"))
                .info(info);
    }

    private Tag createTag(String name, String description) {
        final Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        return tag;
    }
}
