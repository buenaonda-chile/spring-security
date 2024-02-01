package com.demo.springsecurity.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class TbAuthGroupPermissionId (
    val permissionId: Long,
    val authGroupId: Long
): Serializable {

}