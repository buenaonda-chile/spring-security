package com.demo.springsecurity.dto

import com.demo.springsecurity.config.GrantType
import com.demo.springsecurity.enumeration.UseYn
import com.fasterxml.jackson.annotation.JsonInclude

class SubMenuAuthInfoDto {
    data class Request(
        val menuId: Long,
        val permissionNames: List<GrantType>
    )

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    data class Response(
        @com.fasterxml.jackson.annotation.JsonIgnore val parentMenuId: Long?,
        @com.fasterxml.jackson.annotation.JsonIgnore val parentMenuName: String,
        val menuId: Long,
        val menuName: String,
        val menuUrl: String,
        var showYn: UseYn,
        val permissionNames: List<GrantType>
    ) {
        fun getShowYn(): UseYn {
            if (this.permissionNames.contains(GrantType.GET)) {
                return UseYn.Y
            }

            return UseYn.N
        }
    }
}