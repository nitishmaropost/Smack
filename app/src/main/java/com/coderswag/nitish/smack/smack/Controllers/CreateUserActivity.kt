package com.coderswag.nitish.smack.smack.Controllers

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.coderswag.nitish.smack.R
import com.coderswag.nitish.smack.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatarImageClicked(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatarNumber = random.nextInt(28)
        if(color == 0) {
            userAvatar = "light$avatarNumber"
        }
        else {
            userAvatar = "dark$avatarNumber"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    fun backgroundColorButtonClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
        println(avatarColor)
    }

    fun createUserButtonClicked(view: View) {
        val email = createEmailText.text
        val password = createPasswordText.text

        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
        }
        else {
            AuthService.registerUser(this, email.toString(), password.toString()) { registerSuccess ->
                if(registerSuccess) {
                    AuthService.loginUser(this, email.toString(), password.toString()) { loginSuccess ->
                        if(loginSuccess) {
                            println("${AuthService.authToken}\n${AuthService.userEmail}")
                        }
                    }
                }
                else{

                }
            }
        }
    }
}
