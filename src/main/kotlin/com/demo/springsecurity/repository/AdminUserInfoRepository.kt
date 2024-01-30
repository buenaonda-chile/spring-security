package com.demo.springsecurity.repository

import com.demo.springsecurity.entity.TbAdminUserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminUserInfoRepository: JpaRepository<TbAdminUserInfo, Long> {
    fun findByAdminId(id: String) : TbAdminUserInfo
}