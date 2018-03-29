package com.coderswag.nitish.smack.smack.Services

import android.graphics.Color
import java.util.*

object UserDataService {

    var id = ""
    var avatarName = ""
    var avatarColor = ""
    var name = ""
    var email = ""

    fun returnAvatarColor(components: String) : Int {
        val strippedColor = components.replace("[", "").replace("]", "").replace(",", "")
        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(strippedColor)
        if(scanner.hasNext()) {
            r = ((scanner.nextDouble() * 255).toInt())
            g = ((scanner.nextDouble() * 255).toInt())
            b = ((scanner.nextDouble() * 255).toInt())
        }

        return Color.rgb(r, g, b)
    }

    fun logout() {
         id = ""
         avatarName = ""
         avatarColor = ""
         name = ""
         email = ""
        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }
}