package com.example.taskflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class Knife4jConfig {

    /**
     * JWT 认证方案名称
     *
     * 这个名称会显示在 Knife4j / Swagger 的授权配置中。
     */
    private static final String JWT_SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * OpenAPI 文档配置
     *
     * 作用：
     * 1. 配置接口文档标题、版本、描述
     * 2. 配置 JWT Bearer Token 认证方式
     * 3. 让 Knife4j 页面出现 Authorize 授权按钮
     *
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI taskFlowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow 智能任务协作平台接口文档")
                        .version("1.0.0")
                        .description("包含任务管理、状态流转、SSE 实时推送、AI 辅助处理、JWT 登录认证等接口"))

                // 给接口文档添加全局安全要求
                .addSecurityItem(new SecurityRequirement()
                        .addList(JWT_SECURITY_SCHEME_NAME))

                // 配置 JWT Bearer Token 认证方案
                .components(new Components()
                        .addSecuritySchemes(
                                JWT_SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        // 使用 API Key 请求头模式，确保 Knife4j 将认证信息写入 Authorization
                                        .type(SecurityScheme.Type.APIKEY)

                                        // 认证信息放在请求头中
                                        .in(SecurityScheme.In.HEADER)

                                        // 请求头名称
                                        .name(HttpHeaders.AUTHORIZATION)

                                        // 认证说明
                                        .description("请输入：Bearer + 空格 + 登录接口返回的 JWT Token")
                        ));
    }
}
