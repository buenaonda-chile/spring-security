package com.demo.springsecurity.common

import com.demo.springsecurity.config.security.GrantType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OptionalGrant (val requiredGrant: GrantType)
