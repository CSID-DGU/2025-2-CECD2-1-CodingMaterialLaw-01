package com.monorama.iot_server.config;

import com.monorama.iot_server.constant.Constant;
import com.monorama.iot_server.security.JwtAuthEntryPoint;
import com.monorama.iot_server.security.JwtAuthenticationProvider;
import com.monorama.iot_server.security.apple.CustomParameterConverter;
import com.monorama.iot_server.security.filter.JwtAuthenticationFilter;
import com.monorama.iot_server.security.filter.JwtExceptionFilter;
import com.monorama.iot_server.security.handler.*;
import com.monorama.iot_server.security.service.CustomOAuth2UserService;
import com.monorama.iot_server.security.service.CustomUserDetailService;
import com.monorama.iot_server.type.ERole;
import com.monorama.iot_server.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtUtil jwtUtil;

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final CustomLogOutProcessHandler customLogOutProcessHandler;
    private final CustomLogOutResultHandler customLogOutResultHandler;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private final CustomParameterConverter customParameterConverter;

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        RestClientAuthorizationCodeTokenResponseClient accessTokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setParametersConverter(customParameterConverter);
        return accessTokenResponseClient;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/metadata/**").hasAnyRole((ERole.PM.toString()),ERole.BOTH_USER.toString(), ERole.AQD_USER.toString())
                        .requestMatchers("/api/v1/pm/**").hasAnyRole((ERole.PM.toString()))
                        .requestMatchers("/api/v1/health-data/**").hasAnyRole((ERole.BOTH_USER.toString()),ERole.HD_USER.toString())
                        .requestMatchers("/api/v1/air-quality-data/**").hasAnyRole((ERole.BOTH_USER.toString()), ERole.AQD_USER.toString())
                        .anyRequest().authenticated())

                .oauth2Login(
                        configurer ->
                                configurer
                                        .tokenEndpoint(token ->
                                                token.accessTokenResponseClient(accessTokenResponseClient()))
                                        .successHandler(oAuth2LoginSuccessHandler)
                                        .failureHandler(oAuth2LoginFailureHandler)
                                        .userInfoEndpoint(userInfoEndpoint ->
                                                userInfoEndpoint.userService(customOAuth2UserService))

                )
                .logout(configurer ->
                        configurer
                                .logoutUrl("/api/auth/logout")
                                .addLogoutHandler(customLogOutProcessHandler)
                                .logoutSuccessHandler(customLogOutResultHandler)
                                .deleteCookies(Constant.AUTHORIZATION,Constant.REAUTHORIZATION)
                )
                .exceptionHandling(configurer ->
                        configurer
                                .authenticationEntryPoint(jwtAuthEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, new JwtAuthenticationProvider(customUserDetailService)), LogoutFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
                .getOrBuild();
    }
}
