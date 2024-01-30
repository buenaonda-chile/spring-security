package com.demo.springsecurity.repository

import com.demo.springsecurity.dto.MenuAuthManageDto
import com.demo.springsecurity.dto.SubMenuAuthInfoDto
import com.demo.springsecurity.entity.QTbAdminUserInfo.tbAdminUserInfo
import com.demo.springsecurity.entity.QTbAuthGroupPermission.tbAuthGroupPermission
import com.demo.springsecurity.entity.QTbAuthPermission.tbAuthPermission
import com.demo.springsecurity.entity.QTbMenuInfo
import com.demo.springsecurity.entity.QTbMenuInfo.tbMenuInfo
import com.demo.springsecurity.enumeration.UseYn
import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.util.function.Predicate

@Repository
class UserMenuInfoCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun selectMenuAuthByAdminId(adminId: String?): List<MenuAuthManageDto.Response> {
        val parentMenuInfo: QTbMenuInfo = QTbMenuInfo("parentMenuInfo")

        val subMenuAuthInfoDtoList: List<SubMenuAuthInfoDto.Response> = jpaQueryFactory
            .from(tbAdminUserInfo)
            .join(tbAuthGroupPermission)
            .on(
                tbAdminUserInfo.authGroupId.eq(tbAuthGroupPermission.tbAuthGroupPermissionId.authGroupId)
                    .and(tbAdminUserInfo.adminId.eq(adminId))
            )
            .join(tbAuthGroupPermission.tbAuthPermission, tbAuthPermission)
            .rightJoin(tbMenuInfo)
            .on(tbAuthPermission.tbMenuInfo.menuId.eq(tbMenuInfo.menuId))
            .leftJoin(parentMenuInfo)
            .on(parentMenuInfo.menuId.eq(tbMenuInfo.parentMenuId))
            .where(tbMenuInfo.isMenuUse.eq(UseYn.Y))
            .orderBy(parentMenuInfo.displaySort.asc(), tbMenuInfo.displaySort.asc())
            .transform(
                groupBy<Long>(tbMenuInfo.menuId)
                    .list(
                        Projections.fields(
                            SubMenuAuthInfoDto.Response::class.java,
                            tbMenuInfo.menuId.`as`("menuId"),
                            tbMenuInfo.parentMenuId.`as`("parentMenuId"),
                            parentMenuInfo.menuName.`as`("parentMenuName"),
                            tbMenuInfo.menuName.`as`("menuName"),
                            Expressions.asString("/").concat(parentMenuInfo.menuUrl).concat("/")
                                .concat(tbMenuInfo.menuUrl).coalesce(tbMenuInfo.menuUrl).`as`("menuUrl"),
                            GroupBy.list(tbAuthGroupPermission.tbAuthPermission.permissionName)
                                .`as`("permissionNames")
                        )
                    )
            )
        return getMenuAuthInfoDtoList(subMenuAuthInfoDtoList)
    }

    fun getMenuAuthInfoDtoList(subMenuAuthInfoDtoList: List<SubMenuAuthInfoDto.Response>): List<MenuAuthManageDto.Response> {
        val meLongListHashMap: LinkedHashMap<Long, MenuAuthManageDto.Response> =
            LinkedHashMap<Long, MenuAuthManageDto.Response>()
        for (subMenuAuthInfoResponse in subMenuAuthInfoDtoList) {
            if (subMenuAuthInfoResponse.parentMenuId == null) {
                val menuAuthInfoDto: MenuAuthManageDto.Response =
                    meLongListHashMap.getOrDefault(subMenuAuthInfoResponse.menuId, MenuAuthManageDto.Response())
                menuAuthInfoDto.menuId = subMenuAuthInfoResponse.menuId
                menuAuthInfoDto.menuName = subMenuAuthInfoResponse.menuName
                menuAuthInfoDto.showYn = subMenuAuthInfoResponse.getShowYn()
                meLongListHashMap[subMenuAuthInfoResponse.menuId] = menuAuthInfoDto
            } else {
                val temp: MenuAuthManageDto.Response =
                    meLongListHashMap.getOrDefault(subMenuAuthInfoResponse.parentMenuId, MenuAuthManageDto.Response())
                temp.subMenuAuthInfoList?.add(subMenuAuthInfoResponse)
                subMenuAuthInfoResponse.showYn = subMenuAuthInfoResponse.getShowYn()
                meLongListHashMap[subMenuAuthInfoResponse.menuId] = temp
            }
        }
        return meLongListHashMap.values.stream()
            .filter(Predicate<MenuAuthManageDto.Response> { e: MenuAuthManageDto.Response -> e.menuId != null })
            .toList()
    }
}