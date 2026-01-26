package com.monst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // @PreAuthorize を有効化
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 既存テストで csrf() を使っているなら有効のままでOK
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // ---- 公開（認証不要） ----
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/user/login", "/user/register").permitAll()

                        // 重要：モンスター閲覧系は公開
                        .requestMatchers(HttpMethod.GET, "/monster/select/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/monster/select/*").permitAll()

                        // ---- それ以外は admin 必須 ----
                        .requestMatchers("/monster/**").hasRole("ADMIN")
                        .requestMatchers("/master/**").hasRole("ADMIN")

                        // その他（将来用）
                        .anyRequest().authenticated());

        return http.build();
    }
}
