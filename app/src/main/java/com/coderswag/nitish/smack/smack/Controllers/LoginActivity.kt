package com.coderswag.nitish.smack.smack.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.coderswag.nitish.smack.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginButtonClicked(view: View) {

    }

    fun loginCreateButtonClicked(view: View) {
        val signUpIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(signUpIntent)
    }
}
