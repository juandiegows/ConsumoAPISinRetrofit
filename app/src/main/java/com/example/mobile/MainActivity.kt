package com.example.mobile

import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mobile.Network.CallAPIJD
import com.example.mobile.Network.IServicesJD
import com.example.mobile.helper.*
import com.example.mobile.model.LoginHistory
import com.example.mobile.model.User
import com.example.mobile.model.UserRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtUser.Requerido(txtSUser)
        txtPass.Requerido(txtSPass)
        var userl = Config(this)
        CallAPIJD.StartQuery(

            "api/register2/${userl.getID()}",
            CallAPIJD.Companion.method.GET,"",
            object : IServicesJD {
                override fun Finish(response: String, status: Int) {
                    this@MainActivity.runOnUiThread {
                        if (status == HTTP.OK) {
                            var user:User = JSONObject(userl.getUserLogin()).Cast()
                            Toast.makeText(this@MainActivity,"welcome ${user.FirstName} ${user.LastName}",Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    HomeActivity::class.java
                                )
                            )
                        }
                    }
                }
                override fun Error(response: String, status: Int) {

                }
            })
        Eventos()
    }

    private fun Eventos() {
        btnLogin.setOnClickListener {
            var list = arrayListOf<View>(txtSUser, txtSPass)
            if (list.IsValido()) {
                CallAPIJD.StartQuery("api/auth", CallAPIJD.Companion.method.POST,
                    UserRequest(
                        txtUser.text.toString(),
                        txtPass.text.toString()
                    ).toJson(),
                    object : IServicesJD {
                        override fun Finish(response: String, status: Int) {
                            this@MainActivity.runOnUiThread {
                                if (status == HTTP.OK) {
                                    if (response == "null") {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setMessage("User or password invalid")
                                            .setPositiveButton("OK") { _, _ ->
                                            }.create().show()

                                        return@runOnUiThread
                                    }

                                    Singleton.User =
                                        JSONObject(response).toClass(User::class.java.name).Cast()
                                    var user = Singleton.User
                                    CallAPIJD.StartQuery(
                                        "api/register/${user.Id}",
                                        CallAPIJD.Companion.method.GET, "",
                                        object : IServicesJD {
                                            override fun Finish(response: String, status: Int) {
                                                this@MainActivity.runOnUiThread {
                                                    if (status == HTTP.OK) {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "welcome ${user.FirstName} ${user.LastName}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    }
                                                }
                                            }

                                            override fun Error(response: String, status: Int) {

                                            }
                                        })
                                    Config(this@MainActivity).SetId(Singleton.User.Id)
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                } else {
                                    AlertDialog.Builder(this@MainActivity)
                                        .setMessage("Error in the API")
                                        .setPositiveButton("OK") { _, _ ->
                                        }.create().show()
                                }
                            }
                        }

                        override fun Error(response: String, status: Int) {
                            this@MainActivity.runOnUiThread {
                                this@MainActivity.runOnUiThread {

                                    AlertDialog.Builder(this@MainActivity)
                                        .setMessage("critical Error in the API :(")
                                        .setPositiveButton("OK") { _, _ ->
                                        }.create().show()
                                }
                            }
                        }
                    }
                )
            } else {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage("all field are required")
                    .setPositiveButton("OK") { _, _ ->
                    }.create().show()
            }
        }
    }
}