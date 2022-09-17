package com.example.mobile.helper

import com.example.mobile.model.LoginHistory
import com.example.mobile.model.User
import java.text.SimpleDateFormat

object Singleton {
    var User:User = User()
    var Id:Int = 1
    var LoginHistory:LoginHistory =LoginHistory()
    val formatDate = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
}