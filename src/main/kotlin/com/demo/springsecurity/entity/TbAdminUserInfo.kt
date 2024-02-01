package com.demo.springsecurity.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_admin_user_info")
class TbAdminUserInfo(
    @Id val adminUserNo : Long,
    val adminId: String,
    val password: String,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val department: String,
    val position: String,
    @Column(updatable = false, insertable = false) val authGroupId: Long? = null,
    lastLoginDt: LocalDateTime,
    @Column(updatable = false, insertable = false) val regDt: LocalDateTime,
    @Column(insertable = false) val updDt: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "authGroupId") val tbAuthGroup: TbAuthGroup
) {

    var lastLoginDt: LocalDateTime = lastLoginDt
        protected set
    fun updateLastLoginDt() {
        this.lastLoginDt = LocalDateTime.now()
    }

}
