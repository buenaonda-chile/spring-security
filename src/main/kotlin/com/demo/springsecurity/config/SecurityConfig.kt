package com.demo.springsecurity.config

import com.demo.springsecurity.common.exception.ExceptionCode
import com.demo.springsecurity.dto.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.function.Supplier


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customAuthenticationHandler: CustomAuthenticationHandler,
    private val authorizationChecker: AuthorizationChecker,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) {
    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { httpConfig -> httpConfig.disable() }
            .csrf { csrfConfig -> csrfConfig.disable() }
            .headers { headerConfig -> headerConfig.frameOptions { frameOptionConfig -> frameOptionConfig.disable() } }

        http.cors { corsConfig -> corsConfig.configurationSource(corsConfigurationSource()) }

        http.sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }


        http.authorizeHttpRequests { request ->
            request.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().access(this::isGranted)
        }

        http.formLogin { login ->
            login.successHandler(customAuthenticationHandler)
                .failureHandler(customAuthenticationHandler)
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("uid")
                .passwordParameter("password")
        }

        http.logout { logout ->
            logout.logoutUrl("/logout")
                .invalidateHttpSession(true)
                .addLogoutHandler(customLogoutHandler())
        }

        http.exceptionHandling { exception ->
            exception.authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        }

        http.addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), BasicAuthenticationFilter::class.java)

        return http.build();
    }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { request: HttpServletRequest?, response: HttpServletResponse, e: AccessDeniedException? ->
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            objectMapper.writeValue(response.writer, ErrorResponse.from(ExceptionCode.FORBIDDEN))
        }
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { request: HttpServletRequest?, response: HttpServletResponse, e: AuthenticationException? ->
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            objectMapper.writeValue(response.writer, ErrorResponse.from(ExceptionCode.UNAUTHORIZED))
        }
    }

    @Bean
    fun customLogoutHandler(): LogoutHandler {
        return LogoutHandler { request: HttpServletRequest, response: HttpServletResponse?, authentication: Authentication? ->
            log.info("[BEG] customLogoutHandler")
            log.info(request.getHeader("Authorization"))
            log.info("[END] customLogoutHandler")
        }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()

        corsConfiguration.setAllowedOriginPatterns(listOf("*"))
        //TODO: Header 선별해서 리스트 정하기.
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowedMethods = listOf("*")

        corsConfiguration.exposedHeaders = listOf("*")

        corsConfiguration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

    private fun isGranted(authentication: Supplier<Authentication>, `object`: RequestAuthorizationContext): AuthorizationDecision {
        return AuthorizationDecision(authorizationChecker.checker(`object`, authentication.get()))
    }
}