package com.demo.springsecurity.config

import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable

class AdminUser private constructor(
    private val password : String,
    private val adminId : String,
    private val adminUserNo : Long,
    private val authorities : List<RequestGrantedAuthority>
): UserDetails, Serializable {
    override fun getAuthorities(): List<RequestGrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return adminId
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getAdminId(): String {
        return adminId
    }

    fun getAdminUserNo() : Long {
        return adminUserNo;
    }

}
