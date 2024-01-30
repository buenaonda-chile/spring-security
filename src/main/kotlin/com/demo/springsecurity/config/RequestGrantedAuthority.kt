package com.demo.springsecurity.config

import org.springframework.security.core.GrantedAuthority

data class RequestGrantedAuthority(
    private val grantedDomain: String,
    private val grant: HashMap<GrantType, Boolean>
) : GrantedAuthority {
    override fun getAuthority(): String {
        return grantedDomain;
    }

    fun getGrant(): HashMap<GrantType, Boolean> {
        return grant;
    }

    companion object {
        fun of(grantedDomain: String, grantTypeNames: List<GrantType>) : RequestGrantedAuthority {
            val grant = HashMap<GrantType, Boolean>()
            grantTypeNames.forEach { i -> grant.put(GrantType.valueOf(i.name), true) }

            return RequestGrantedAuthority(grantedDomain, grant)
        }

    }
}