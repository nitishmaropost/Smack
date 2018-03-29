package com.coderswag.nitish.smack.smack.Controllers

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.coderswag.nitish.smack.R
import com.coderswag.nitish.smack.smack.Services.AuthService
import com.coderswag.nitish.smack.smack.Services.UserDataService
import com.coderswag.nitish.smack.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        progressBarCreateUser.visibility = View.INVISIBLE
    }

    fun generateUserAvatarImageClicked(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatarNumber = random.nextInt(28)
        if (color == 0) {
            userAvatar = "light$avatarNumber"
        } else {
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

        val userName = createUserNameText.text
        val email = createEmailText.text
        val password = createPasswordText.text

        if (email.isNullOrEmpty() || userName.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
        } else {
            enableSpinner(true)
            AuthService.registerUser(this, email.toString(), password.toString()) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email.toString(), password.toString()) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, userName.toString(), email.toString(), userAvatar.toString(), avatarColor.toString()) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        }
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            progressBarCreateUser.visibility = View.VISIBLE
        } else {
            progressBarCreateUser.visibility = View.INVISIBLE
        }

        createUserButton.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        backgroundColorBtn.isEnabled = !enable
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong !!", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }
}
