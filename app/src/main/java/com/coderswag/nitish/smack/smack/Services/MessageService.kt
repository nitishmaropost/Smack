package com.coderswag.nitish.smack.smack.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.coderswag.nitish.smack.smack.Controllers.App
import com.coderswag.nitish.smack.smack.Model.Channel
import com.coderswag.nitish.smack.smack.Model.Message
import com.coderswag.nitish.smack.smack.Services.UserDataService.name
import com.coderswag.nitish.smack.smack.Utilities.FIND_CHANNELS_URL
import com.coderswag.nitish.smack.smack.Utilities.GET_MESSAGES_BY_CHANNEL
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun findChannels(complete : (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, FIND_CHANNELS_URL, null, Response.Listener { response ->
            clearChannels()
            try {
                for(x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val channelId = channel.getString("_id")
                    val desc = channel.getString("description")

                    val newChannel = Channel(name, desc, channelId)
                    this.channels.add(newChannel)
                }

                complete(true)
            } catch (exception: JSONException) {
                Log.d("Json exception : ", exception.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR : ", error.localizedMessage)
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

        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        val url = "$GET_MESSAGES_BY_CHANNEL$channelId"
        val messageRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
            try {
                for(x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val id = message.getString("_id")
                    val messageBody = message.getString("messageBody")
                    val userName = message.getString("userName")
                    val channelId = message.getString("channelId")
                    val avatarName = message.getString("userAvatar")
                    val avatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")
                    val newMessage = Message(messageBody, userName, channelId, avatarName, avatarColor, id, timeStamp)
                    this.messages.add(newMessage)
                }

                complete(true)
            } catch (exception: JSONException) {
                Log.d("Json exception : ", exception.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR : ", error.localizedMessage)
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

        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages() {
        this.messages.clear()
    }

    fun clearChannels() {
        this.channels.clear()
    }
}