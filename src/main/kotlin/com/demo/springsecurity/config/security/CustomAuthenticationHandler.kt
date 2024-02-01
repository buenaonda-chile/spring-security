package com.demo.springsecurity.config.security

import com.demo.springsecurity.common.exception.ExceptionCode
import com.demo.springsecurity.config.jwt.JwtTokenProvider
import com.demo.springsecurity.dto.ErrorResponse
import com.demo.springsecurity.dto.LoginResponse
import com.demo.springsecurity.entity.TbAdminUserInfo
import com.demo.springsecurity.enumeration.UseYn
import com.demo.springsecurity.repository.AdminUserInfoRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder,
    private val adminUserInfoRepository: AdminUserInfoRepository
) : AuthenticationSuccessHandler, AuthenticationFailureHandler {
    @Value("\${init.password}") private lateinit var initPassword: String

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val adminUser: AdminUser = authentication.principal as AdminUser
        val token: String = jwtTokenProvider.generateToken(adminUser)

        val tbUserInfo: TbAdminUserInfo? = adminUserInfoRepository.findByAdminId(adminUser.getAdminId())

        if (tbUserInfo != null) {
            tbUserInfo.updateLastLoginDt()
            val isInitPassword: UseYn = if (passwordEncoder.matches(initPassword, adminUser.password)) UseYn.Y else UseYn.N
            val longinResponse = LoginResponse(adminUser.getAdminId(), isInitPassword, token)
            objectMapper.writeValue(response.writer, longinResponse)
        } else {
            val loginResponse = LoginResponse(adminUser.getAdminId())
            objectMapper.writeValue(response.writer, loginResponse)
        }
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val errorResponse = if ("LOGIN_ID_FAIL".equals(exception.message)) ErrorResponse(ExceptionCode.LOGIN_ID_FAIL) else ErrorResponse(ExceptionCode.LOGIN_PW_FAIL)
        objectMapper.writeValue(response.writer, errorResponse)
    }
}