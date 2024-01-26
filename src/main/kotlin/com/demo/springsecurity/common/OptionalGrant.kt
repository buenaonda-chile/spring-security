package com.demo.springsecurity.common

import com.demo.springsecurity.config.GrantType
import java.lang.annotation.ElementType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OptionalGrant (val requiredGrant: GrantType)
