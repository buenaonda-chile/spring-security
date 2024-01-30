package com.demo.springsecurity.entity


import com.demo.springsecurity.common.converter.UseYnConverter
import com.demo.springsecurity.enumeration.UseYn
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_admin_menu_info")
class TbMenuInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val menuId: Long,
    val parentMenuId: Long,
    val menuName: String,
    val menuUrl: String,
    val displaySort: Int,
    @Convert(converter = UseYnConverter::class) val isMenuUse: UseYn,
    @Column(updatable = false, insertable = false) val regDt: LocalDateTime,
    @Column(updatable = false, insertable = false) val updDt: LocalDateTime
) {
}