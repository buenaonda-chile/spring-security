package com.demo.springsecurity.config.jwt

import com.demo.springsecurity.common.exception.ExceptionCode
import com.demo.springsecurity.dto.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    val objectMapper: ObjectMapper = ObjectMapper()
    val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token: String? = jwtTokenProvider.resolveToken(request)
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token!!)) {
                val auth: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            }
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            log.error("만료된 JWT 토큰입니다.", e)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            objectMapper.writeValue(response.writer, ErrorResponse(ExceptionCode.UNAUTHORIZED, "토큰이 만료되었습니다."))
        } catch (e: Exception) {
            log.error("토큰 오류", e)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            objectMapper.writeValue(response.writer, ErrorResponse(ExceptionCode.UNAUTHORIZED))
        }

    }
}