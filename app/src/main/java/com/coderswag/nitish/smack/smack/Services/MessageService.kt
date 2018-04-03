package com.coderswag.nitish.smack.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.coderswag.nitish.smack.smack.Controllers.App
import com.coderswag.nitish.smack.smack.Model.Channel
import com.coderswag.nitish.smack.smack.Model.Message
import com.coderswag.nitish.smack.smack.Utilities.FIND_CHANNELS_URL
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun findChannels(complete : (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, FIND_CHANNELS_URL, null, Response.Listener { response ->
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
}