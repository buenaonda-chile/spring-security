package com.demo.springsecurity.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {
    @GetMapping("/")
    fun welcome(): String {
        return "WELCOME TO MY WORLD"
    }
}