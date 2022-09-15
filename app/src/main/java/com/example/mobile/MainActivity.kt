package com.example.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.mobile.Network.CallAPIJD
import com.example.mobile.Network.IServicesJD
import com.example.mobile.helper.*
import com.example.mobile.model.LoginHistory
import com.example.mobile.model.User
import com.example.mobile.model.UserRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        barLoad.visibility = View.VISIBLE
        txtInfo.visibility = View.VISIBLE
        txtUser.Requerido(txtSUser)
        txtPass.Requerido(txtSPass)
        var userl = Config(this)
        LoadInit(userl)
        Eventos()
    }

    private fun LoadInit(userl: Config) {
        CallAPIJD.StartQuery(

            "api/registerexists/${userl.getID()}",
            CallAPIJD.Companion.method.GET, "",
            object : IServicesJD {
                override fun Finish(response: String, status: Int) {

                    this@MainActivity.runOnUiThread {

                        if (status == HTTP.OK) {
                            var user: User = JSONObject(userl.getUserLogin()).toClass(User::class.java.name).Cast()
                            Singleton.User = user
                            try {
                                Singleton.LoginHistory = JSONObject(response).toClass(LoginHistory::class.java.name).Cast()
                            } catch (e: Exception) {
                                Log.e("TAG", "Finish: Eror" )
                            }
                            Toast.makeText(
                                this@MainActivity,
                                "welcome ${user.FirstName} ${user.LastName}",
                                Toast.LENGTH_SHORT
                            ).show()
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
                  barLoad.isVisible = false
                  txtInfo.isVisible = false
                }
            })
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
                                                        try {
                                                            Singleton.LoginHistory = JSONObject(response).toClass(LoginHistory::class.java.name).Cast()
                                                        } catch (e: Exception) {
                                                        }
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