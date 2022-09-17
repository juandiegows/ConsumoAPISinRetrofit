package com.example.mobile.Network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

class CallAPIJD {

    companion object {

        var URL2 = "http://10.0.2.2:3132"

        enum class method {
            GET,
            POST,
            PUT,
            DELETE
        }

        fun StartQuery(addres: String, method: method, data: String, Iservices: IServicesJD) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var client = java.net.URL("$URL2/$addres").openConnection() as HttpURLConnection
                    Log.e("Data", "$data")
                    Log.e("Data", "$URL2/$addres")
                    client.requestMethod = method.name
                    if (method == Companion.method.POST || method == Companion.method.PUT) {
                        client.setRequestProperty("content-type", "application/json")
                        client.doOutput = true
                        client.useCaches = true
                        client.outputStream.write(data.encodeToByteArray())
                    }

                    if (client.errorStream != null) {
                        client.errorStream.bufferedReader().apply {
                            var text = this.readText()
                            Log.e("Error", "${client.responseCode} $text")
                            Iservices.Error(text, client.responseCode)

                        }
                        return@launch
                    }

                    if (client.inputStream != null) {
                        client.inputStream.bufferedReader().apply {
                            var text = this.readText()
                            Log.e("Finish", "${client.responseCode} $text")
                            Iservices.Finish(text, client.responseCode)


                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    Iservices.Finish("Error en el API", -100)

                }
            }

        }
    }
}