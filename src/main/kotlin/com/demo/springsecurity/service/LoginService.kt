package com.demo.springsecurity.service

import com.demo.springsecurity.config.AdminUser
import com.demo.springsecurity.config.RequestGrantedAuthority
import com.demo.springsecurity.dto.MenuAuthManageDto
import com.demo.springsecurity.entity.TbAdminUserInfo
import com.demo.springsecurity.repository.AdminUserInfoRepository
import com.demo.springsecurity.repository.UserMenuInfoCustomRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

class LoginService(
    val adminUserInfoRepository: AdminUserInfoRepository,
    val userMenuInfoCustomRepository: UserMenuInfoCustomRepository,
    val passwordEncoder: PasswordEncoder,
    @Value("\${init.password}") val initPassword: String
) : UserDetailsService{
    override fun loadUserByUsername(adminId: String?): AdminUser {
        val tbAdminUserInfo: TbAdminUserInfo = adminUserInfoRepository.findByAdminId(adminId!!)
        val authorityList: List<RequestGrantedAuthority> = getAuthorityList(adminId)
        return AdminUser.of(tbAdminUserInfo, authorityList)
    }

    fun getAuthorityList(adminId: String?): List<RequestGrantedAuthority> {
        val authoritySet: MutableSet<RequestGrantedAuthority> = HashSet<RequestGrantedAuthority>()
        val menuAuthInfoDtoList: List<MenuAuthManageDto.Response> =
            userMenuInfoCustomRepository.selectMenuAuthByAdminId(adminId)
        if (menuAuthInfoDtoList != null && !menuAuthInfoDtoList.isEmpty()) {
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