package com.kottland.blockcoins

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform