package com.example.mobile.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.javaType
import kotlin.reflect.typeOf


@OptIn(ExperimentalStdlibApi::class)
fun JSONObject.toClass(nameClass: String): Any {
    var onClas = Class.forName(nameClass)
    var instance = onClas.newInstance()

    onClas.declaredFields.forEach {
        it.isAccessible = true
        try {
            it.set(
                instance, when (it.type) {
                    typeOf<Int>().javaType -> this.getInt(it.name)
                    typeOf<Double>().javaType -> this.getDouble(it.name)
                    typeOf<String>().javaType -> this.getString(it.name)
                    typeOf<Boolean>().javaType -> this.getBoolean(it.name)
                    typeOf<Long>().javaType -> this.getLong(it.name)
                    typeOf<Calendar?>().javaType -> {
                        var date = Singleton.formatDate.parse(this@toClass.getString(it.name))
                        Calendar.getInstance().apply {
                            this.time = date
                        }
                    }
                    else -> {
                        try {
                            this.getJSONObject(it.name).toClass(it.type.name).Cast()
                        } catch (e: Exception) {

                        }
                    }
                }
            )
        } catch (e: Exception) {
        }
    }

    return instance
}

fun JSONArray.toList(nameClass: String): ArrayList<Any> {
    var list = ArrayList<Any>()

    for (i in 0 until this.length()) {
        list.add(this.getJSONObject(i).toClass(nameClass))
    }
    return list
}

inline fun <reified T> Any.Cast(): T {
    return this as T
}

fun Any.toJson(): String {
    var onClass = this::class.java
    var json = JSONObject()
    onClass.declaredFields.forEach {
        it.isAccessible = true
        json.put(it.name, it.get(this))
    }
    return json.toString()
}

fun ArrayList<View>.IsValido(): Boolean {
    var valido = true
    this.forEach {
        it.requestFocus()
        it.clearFocus()
        if(it is TextInputLayout){
            Log.e("TAG", "IsValido: '${it.error.toString()}  ${it.error.toString()!="null"}'" )
            if(!it.error.isNullOrEmpty()){
                valido = false
            }
        }
    }
return  valido;
}
fun String.ToBitmap():Bitmap{
    var byte = Base64.decode(this,Base64.DEFAULT)
    return  BitmapFactory.decodeByteArray(byte,0,byte.size)
}
fun TextInputEditText.Requerido(layout: TextInputLayout) {
    this.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            layout.error = ""
        } else {
            if (this.text.toString().trim().isEmpty()) {
                layout.error = "this field required"
            }
        }
    }
}