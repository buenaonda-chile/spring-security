package com.demo.springsecurity.dto

import com.demo.springsecurity.enumeration.UseYn

class MenuAuthManageDto() {
    data class Request(
        val menuId: Long,
        val showYn: UseYn,
        val subMenuAuthInfoList: MutableList<SubMenuAuthInfoDto.Request>
    )

    data class Response(
        var menuId: Long?,
        var menuName: String?,
        var showYn: UseYn,
        var subMenuAuthInfoList: MutableList<SubMenuAuthInfoDto.Response>?
    ) {
        constructor() : this(null, null, UseYn.N, null)
    }
}
