package com.demo.springsecurity.config

import org.springframework.security.core.GrantedAuthority

class RequestGrantedAuthority (
    private val grantedDomain : String,
    private val grant : HashMap<GrantType, Boolean>
) : GrantedAuthority {
    override fun getAuthority(): String {
        return grantedDomain;
    }
}