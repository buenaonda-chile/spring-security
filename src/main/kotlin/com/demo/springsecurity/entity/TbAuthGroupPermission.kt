package com.demo.springsecurity.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "tb_admin_auth_group_permission")
class TbAuthGroupPermission(
    @EmbeddedId val tbAuthGroupPermissionId: TbAuthGroupPermissionId,
    @Column(updatable = false, insertable = false) val regDt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "permissionId") @MapsId("permissionId") val tbAuthPermission: TbAuthPermission
): Serializable {
}