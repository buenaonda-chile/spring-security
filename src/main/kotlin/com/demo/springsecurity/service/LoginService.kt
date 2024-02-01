package com.demo.springsecurity.service

import com.demo.springsecurity.config.security.AdminUser
import com.demo.springsecurity.config.security.RequestGrantedAuthority
import com.demo.springsecurity.dto.MenuAuthManageDto
import com.demo.springsecurity.entity.TbAdminUserInfo
import com.demo.springsecurity.repository.AdminUserInfoRepository
import com.demo.springsecurity.repository.UserMenuInfoCustomRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val adminUserInfoRepository: AdminUserInfoRepository,
    private val userMenuInfoCustomRepository: UserMenuInfoCustomRepository,
) : UserDetailsService{
    override fun loadUserByUsername(adminId: String?): AdminUser {
        val tbAdminUserInfo: TbAdminUserInfo? = adminUserInfoRepository.findByAdminId(adminId!!)
        val authorityList: List<RequestGrantedAuthority> = getAuthorityList(adminId)
        return AdminUser.of(tbAdminUserInfo!!, authorityList)
    }

    fun getAuthorityList(adminId: String?): List<RequestGrantedAuthority> {
        val authoritySet: MutableSet<RequestGrantedAuthority> = HashSet<RequestGrantedAuthority>()
        val menuAuthInfoDtoList: List<MenuAuthManageDto.Response> =
            userMenuInfoCustomRepository.selectMenuAuthByAdminId(adminId)
        if (menuAuthInfoDtoList.isNotEmpty()) {
            for (menuAuthInfoDto in menuAuthInfoDtoList) {
                for (subMenuAuthInfoDto in menuAuthInfoDto.subMenuAuthInfoList!!) {
                    val requestGrantedAuthority: RequestGrantedAuthority = RequestGrantedAuthority.of(
                        subMenuAuthInfoDto.menuUrl,
                        subMenuAuthInfoDto.permissionNames
                    )
                    authoritySet.add(requestGrantedAuthority)
                }
            }
        }
        return ArrayList<RequestGrantedAuthority>(authoritySet)
    }
}