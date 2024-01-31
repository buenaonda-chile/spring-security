package com.demo.springsecurity.dto

import com.demo.springsecurity.config.security.GrantType
import com.demo.springsecurity.enumeration.UseYn
import com.fasterxml.jackson.annotation.JsonInclude

class SubMenuAuthInfoDto {
    data class Request(
        val menuId: Long,
        val permissionNames: List<GrantType>
    )

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    data class Response(
        @com.fasterxml.jackson.annotation.JsonIgnore var parentMenuId: Long?,
        @com.fasterxml.jackson.annotation.JsonIgnore var parentMenuName: String,
        var menuId: Long,
        var menuName: String,
        var menuUrl: String,
        var showYn: UseYn,
        var permissionNames: List<GrantType>
    ) {
        fun getShowYnByPermissionNames(): UseYn {
            if (this.permissionNames.contains(GrantType.GET)) {
                return UseYn.Y
            }

            return UseYn.N
        }
    }
}