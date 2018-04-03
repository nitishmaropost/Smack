package com.coderswag.nitish.smack.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.coderswag.nitish.smack.smack.Controllers.App
import com.coderswag.nitish.smack.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, REGISTER_URL, Response.Listener { response ->
            println(response)
            complete(true)

        }, Response.ErrorListener { error ->
            println(error.message)
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, LOGIN_URL, null, Response.Listener { response ->

            try {
                App.prefs.authToken = response.getString("token")
                App.prefs.userEmail = response.getString("user")
                App.prefs.isLoggedIn = true
                complete(true)
            } catch (exception: JSONException) {
                Log.d("Login JsonException : ", "${exception.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, ADD_USER_URL, null, Response.Listener { response ->

            try {
                UserDataService.name = response.getString("name")
                UserDataService.id = response.getString("_id")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                complete(true)
            } catch (exception: JSONException) {
                Log.d("Add User JsonException : ", "${exception.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun findUserByEmail(context: Context, complete : (Boolean) -> Unit) {

        val findRequest = object : JsonObjectRequest(Method.GET, "$FIND_USER_URL${App.prefs.userEmail}", null, Response.Listener {response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.id = response.getString("_id")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                complete(true)
            } catch (exception: JSONException) {
                Log.d("Find user json exception : ", exception.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("Find user api error : ", error.localizedMessage)
            complete(false)
        })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(findRequest)
    }
}