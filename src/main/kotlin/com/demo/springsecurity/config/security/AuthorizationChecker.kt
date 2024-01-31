package com.demo.springsecurity.config.security

import io.micrometer.common.util.StringUtils
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthorizationChecker {
    val log = LoggerFactory.getLogger(this.javaClass)!!

    fun checker(requestContext: RequestAuthorizationContext, authentication: Authentication): Boolean {

        try {
            val request: HttpServletRequest = requestContext.request
            if ("anonymousUser" != authentication.principal.toString()) {
                // header에 OptionalGrant 있고 정상적인 경우 권한체크를 AOP(GrantAspect)로 넘긴다.
                try {
                    val optionalGrant = request.getHeader("OptionalGrant")
                    if (StringUtils.isNotEmpty(optionalGrant)) {
                        val grantType = GrantType.valueOf(optionalGrant.uppercase(Locale.getDefault()))
                        log.info("Optional GrantType : {}", grantType)
                        request.setAttribute("OptionalGrant", grantType)
                        return true
                    }
                } catch (ignored: Exception) {
                    return false
                }

                var isGranted: Boolean? = false
                val requestMethod: String = request.method
                val requestURI: String = request.requestURI
                val requestGrantType: GrantType = GrantType.valueOf(requestMethod)
                for (authority in authentication.authorities) {
                    val requestGrantedAuthority = authority as RequestGrantedAuthority
                    if (isGranted(requestURI, requestGrantType, requestGrantedAuthority)) {
                        log.info("{} - [{}:{}] IS GRANTED", authentication.name, requestMethod, requestURI)
                        isGranted = true
                        break
                    }
                }
                if (!isGranted!!) {
                    log.info("{} - [{}:{}] IS NOT GRANTED", authentication.name, requestMethod, requestURI)
                }
                return isGranted
            }
        } catch (e: Exception) {
            log.error("", e)
        }
        return false;
    }

    fun isGranted(requestURI: String, requestGrantType: GrantType, authority: RequestGrantedAuthority): Boolean {
        return requestURI.startsWith(authority.authority) && authority.getGrant().getOrDefault(requestGrantType, false)
    }
}