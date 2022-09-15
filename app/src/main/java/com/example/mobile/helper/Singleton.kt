package com.example.mobile.helper

import com.example.mobile.model.LoginHistory
import com.example.mobile.model.User

object Singleton {
    var User:User = User()
    var Id:Int = 1
    var LoginHistory:LoginHistory =LoginHistory()
}