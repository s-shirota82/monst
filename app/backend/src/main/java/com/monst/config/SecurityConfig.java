package com.monst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // APIサーバ前提：CSRF無効（ブラウザPOSTが403にならない）
            .csrf(AbstractHttpConfigurer::disable)

            // 余計な認証UIやBasicを無効化（APIとして扱う）
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth
                // ---- 公開（認証不要）----
                .requestMatchers("/health").permitAll()
                .requestMatchers("/user/login", "/user/register").permitAll()

                // モンスター閲覧は公開
                .requestMatchers(HttpMethod.GET, "/monster/select/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/monster/select/*").permitAll()

                // ---- それ以外は admin 必須 ----
                .requestMatchers("/monster/**").hasRole("ADMIN")
                .requestMatchers("/master/**").hasRole("ADMIN")

                // その他は認証必須（将来用）
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
