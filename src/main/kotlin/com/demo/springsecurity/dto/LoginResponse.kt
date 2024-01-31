package com.demo.springsecurity.dto

import com.demo.springsecurity.enumeration.UseYn

data class LoginResponse constructor (
    var adminId: String?,
    var isInitPassword: UseYn?,
    var token: String?
) {
    constructor(adminId: String) : this(adminId, null, null)
}
