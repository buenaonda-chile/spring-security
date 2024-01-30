package com.demo.springsecurity.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_admin_auth_group")
class TbAuthGroup (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val authGroupId: Long,
    val authGroupName: String,
    val regDt: LocalDateTime,
    val updDt: LocalDateTime? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tbAuthGroup") val tbAdminUserInfoList: List<TbAdminUserInfo>
){
}