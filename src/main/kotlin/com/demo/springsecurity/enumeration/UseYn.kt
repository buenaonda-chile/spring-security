package com.demo.springsecurity.enumeration

enum class UseYn(val flag: Boolean) {
    N(false), Y(true);

    fun isUsable(): Boolean {
        return flag
    }

    fun reverse(): UseYn {
        if (isUsable()) {
            return N
        }

        return Y
    }
}