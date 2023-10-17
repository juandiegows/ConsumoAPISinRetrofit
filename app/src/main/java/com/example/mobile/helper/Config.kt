package com.example.mobile.helper

import android.content.Context
import android.content.SharedPreferences

class Config(val context: Context) {

    val name = "Config"
    val id = "ID"
    val user = "user"
    fun getID(): Int {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(id, 0)

    }

    fun getUserLogin(): String {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(user, "").toString()
    }
    fun SetId(id_: Int) {
        var config = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
        config.putInt(id, id_)
        config.putString(user,Singleton.User.toJson())
        config.apply()
    }
}