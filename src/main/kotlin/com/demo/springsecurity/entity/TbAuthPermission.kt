package com.demo.springsecurity.entity

import com.demo.springsecurity.config.GrantType
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "tb_admin_auth_permission")
class TbAuthPermission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val permissionId: Long,
    @Enumerated(EnumType.STRING) var permissionName: GrantType? = null,
    @Column(updatable = false, insertable = false) val regDt: LocalDateTime,
    @Column(updatable = false, insertable = false) val updDt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "menuId") private val tbMenuInfo: TbMenuInfo? = null
): Serializable {
}