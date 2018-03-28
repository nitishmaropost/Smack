package com.coderswag.nitish.smack.smack.Services

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.coderswag.nitish.smack.smack.Utilities.REGISTER_URL
import org.json.JSONObject
import java.lang.reflect.Method

object AuthService {

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, REGISTER_URL, Response.Listener { response ->
            println(response)
            complete(true)

        }, Response.ErrorListener {error ->
            println(error.message)
            complete(false)
        })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }
}