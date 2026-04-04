package com.carelink.global.config;

import com.carelink.global.util.SessionCookieManager;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carelinkOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Carelink API")
                        .description("Carelink 백엔드 API 문서")
                        .version("v1")
                        .contact(new Contact().name("Carelink")))
                .components(new Components()
                        .addSecuritySchemes("sessionCookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name(SessionCookieManager.SESSION_COOKIE_NAME)
                                .description("로그인 API가 발급한 세션 쿠키로 인증이 필요한 엔드포인트를 호출합니다.")));
    }
}
