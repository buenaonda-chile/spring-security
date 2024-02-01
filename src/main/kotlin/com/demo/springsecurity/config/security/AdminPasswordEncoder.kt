package com.demo.springsecurity.config.security

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminPasswordEncoder {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}