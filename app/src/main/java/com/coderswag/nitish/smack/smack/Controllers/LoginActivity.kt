package com.coderswag.nitish.smack.smack.Controllers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.coderswag.nitish.smack.R
import com.coderswag.nitish.smack.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableSpinner(false)
    }

    fun loginLoginButtonClicked(view: View) {
        if (loginEmailText.text.isNullOrEmpty() || loginPasswordText.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
        } else {
            enableSpinner(true)
            AuthService.loginUser(this,loginEmailText.text.toString(), loginPasswordText.text.toString()) {loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) {findUserSuccess ->
                        if (findUserSuccess) {
                            finish()
                            enableSpinner(false)
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

    fun loginCreateButtonClicked(view: View) {
        val signUpIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(signUpIntent)
        finish()
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            progressBarLogin.visibility = View.VISIBLE
        } else {
            progressBarLogin.visibility = View.INVISIBLE
        }

        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
        loginEmailText.isEnabled = !enable
        loginPasswordText.isEnabled = !enable
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong !!", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }
}
