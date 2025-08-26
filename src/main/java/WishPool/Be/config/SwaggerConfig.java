package WishPool.Be.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("WishPool API GUIDE")
                .version("v1.0.0")
                .description("WishPool API 명세서입니다.");

        // --- 기존 JWT 인증 설정 (그대로 유지) ---
        String jwtSchemeName = "AccessToken";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        // ✅ 1. 소셜 로그인 경로를 수동으로 추가하는 부분
        String kakaoLoginPath = "/oauth2/authorization/kakao";

        Operation kakaoLoginOperation = new Operation()
                .summary("카카오 소셜 로그인 시작")
                .description("클라이언트를 카카오 로그인 페이지로 리디렉션합니다. 브라우저에서 직접 이 링크로 이동해야 합니다.")
                .tags(Collections.singletonList("Auth"))
                .responses(new ApiResponses().addApiResponse("302", new ApiResponse().description("카카오 로그인 페이지로 리디렉션")));

        PathItem kakaoLoginPathItem = new PathItem().get(kakaoLoginOperation);


        // ✅ 2. 기존 OpenAPI 객체에 경로를 추가하여 반환
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components)
                .path(kakaoLoginPath, kakaoLoginPathItem); // ⬅️ 이 부분을 추가
    }
}