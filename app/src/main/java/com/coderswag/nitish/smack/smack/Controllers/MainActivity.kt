package com.coderswag.nitish.smack.smack.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.coderswag.nitish.smack.R
import com.coderswag.nitish.smack.smack.Model.Channel
import com.coderswag.nitish.smack.smack.Model.Message
import com.coderswag.nitish.smack.smack.Services.AuthService
import com.coderswag.nitish.smack.smack.Services.MessageService
import com.coderswag.nitish.smack.smack.Services.UserDataService
import com.coderswag.nitish.smack.smack.Services.UserDataService.avatarColor
import com.coderswag.nitish.smack.smack.Services.UserDataService.avatarName
import com.coderswag.nitish.smack.smack.Services.UserDataService.id
import com.coderswag.nitish.smack.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.coderswag.nitish.smack.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setAdapters()
        hideKeyboard()
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        channel_list.setOnItemClickListener { adapterView, view, i, l ->
            selectedChannel = MessageService.channels[i]
            mainChannelName.text = "#${selectedChannel?.name}"
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        if(App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {

            }
        }
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReciever, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReciever)
    }

    private val userDataChangeReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.prefs.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Logout"

                MessageService.findChannels() {complete ->
                    if(complete) {
                        if(MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        // download messages for channel
        if(selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) {complete ->
                if(complete) {
                    for(message in MessageService.messages) {
                        println(message.message)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View){
        if(App.prefs.isLoggedIn) {
            UserDataService.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            loginBtnNavHeader.text = "Login"
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View){
        if(App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val alertAddChannel = layoutInflater.inflate(R.layout.add_channel_layout, null)
            builder.setView(alertAddChannel)
                    .setPositiveButton("Add") { dialogInterface, i ->
                        val nameTextField = alertAddChannel.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descTextField = alertAddChannel.findViewById<EditText>(R.id.addChannelDescTxt)
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descTextField.text.toString()

                        // Create channel
                        socket.emit("newChannel", channelName, channelDesc)
                    }
                    .setNegativeButton("Cancel") {dialogInterface, i ->

                    }
                    .show()
        }

    }

    private val onNewChannel = Emitter.Listener {args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDesc = args[1] as String
                val channelId = args[2] as String
                val channel = Channel(channelName, channelDesc, channelId)
                MessageService.channels.add(channel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id) {
                    val messageBody = args[0] as String
                    val userName = args[3] as String
                    val avatarName = args[4] as String
                    val avatarColor = args[5] as String
                    val id = args[6] as String
                    val timestamp = args[7] as String
                    val newMessage = Message(messageBody, userName, channelId, avatarName, avatarColor, id, timestamp)
                    MessageService.messages.add(newMessage)
                    println(newMessage.message)
                }
            }
        }
    }

    fun sendMessageButtonClick(view: View){
        if(App.prefs.isLoggedIn && !messageTextField.text.isNullOrEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", messageTextField.text, userId, channelId, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
