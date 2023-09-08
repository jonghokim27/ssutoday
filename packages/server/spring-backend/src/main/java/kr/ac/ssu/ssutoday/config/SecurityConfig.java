/**
 * @filename    : SecurityConfig.java
 * @description : Config for Spring Security
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    /**
     * DI
     */
    private final JWTAuthFilter jwtAuthFilter;
    private final ClientKeyFilter clientKeyFilter;
    private final StatusCode statusCode;

    /**
     * Security filter chain config
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/student/login", "/error", "/device/checkVersion", "/sso/validateToken").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(getUnauthorizedEntryPoint())
                        .accessDeniedHandler(getAccessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(clientKeyFilter, JWTAuthFilter.class);

        return http.build();

    }

    private AccessDeniedHandler accessDeniedHandler = null;

    /**
     * Access Denied Handler (Lazy)
     */
    private AccessDeniedHandler getAccessDeniedHandler() {
        if (accessDeniedHandler == null) {
            return accessDeniedHandler = (request, response, accessDeniedException) -> {
                log.debug(accessDeniedException.getMessage(), accessDeniedException);

                response.setStatus(400);
                response.setContentType("application/json");
                CommonResponse res = new CommonResponse(statusCode.SSU4001, null, statusCode.SSU4001_MSG);
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(res));
            };
        }
        return accessDeniedHandler;
    }

    private AuthenticationEntryPoint authenticationEntryPoint = null;

    /**
     * Unauthorized entry point (Lazy)
     */
    private AuthenticationEntryPoint getUnauthorizedEntryPoint() {
        if (authenticationEntryPoint == null) {
            return authenticationEntryPoint = (request, response, authException) -> {
                log.debug(authException.getMessage(), authException);

                response.setStatus(400);
                response.setContentType("application/json");
                CommonResponse res = new CommonResponse(statusCode.SSU4001, null, statusCode.SSU4001_MSG);
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(res));
            };
        }
        return authenticationEntryPoint;
    }

}