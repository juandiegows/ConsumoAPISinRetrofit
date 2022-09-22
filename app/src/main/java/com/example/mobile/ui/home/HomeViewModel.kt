package com.example.mobile.ui.home

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobile.Adapter.AdapterDay
import com.example.mobile.Adapter.IAdapterDay
import com.example.mobile.Network.CallAPIJD
import com.example.mobile.Network.IServicesJD
import com.example.mobile.helper.*
import com.example.mobile.model.DayCalendar
import com.example.mobile.model.PrivateLesson
import com.example.mobile.model.ResponseJD
import org.json.JSONArray
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class HomeViewModel : ViewModel() {

    //get day of month
    private val _result = MutableLiveData<ResponseJD>().apply {
        value = ResponseJD().apply {
            status = 1
            response = ""
            data = arrayListOf<DayCalendar>()
        }
    }
    val result: LiveData<ResponseJD> = _result

    fun UpdateCaledar(calendar: Calendar) {
        var lista = arrayListOf(
            DayCalendar().apply {
                day = "S"
                activo = false
            },
            DayCalendar().apply {
                day = "M"
                activo = false
            },

            DayCalendar().apply {
                day = "T"
                activo = false
            },

            DayCalendar().apply {
                day = "W"
                activo = false
            },

            DayCalendar().apply {
                day = "T"
                activo = false
            },

            DayCalendar().apply {
                day = "F"
                activo = false
            },

            DayCalendar().apply {
                day = "S"
                activo = false
            },


            )
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)
        var dayWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
        for (i in 1..dayWeek - 2) {
            lista.add(DayCalendar().apply {
                day = ""
                activo = false
            })
        }
        val maxDate: Int = calendar.get(Calendar.DATE)


        for (i in 1..maxDate) {

            lista.add(DayCalendar().apply {
                day = i.toString()
                activo = false
            })
        }
        _result.value?.apply {
            status = HTTP.OK
            response = ""
            data = lista

        }


    }

    //add private lesson to caledar

    private val _resultPrivateLesson = MutableLiveData<ResponseJD>().apply {
        value = ResponseJD().apply {

            status = 1
            response = ""
            data = kotlin.collections.ArrayList<PrivateLesson>()
        }
    }
    val resultPrivateLesson: LiveData<ResponseJD> = _resultPrivateLesson

    fun AddPrivateLessonToCaledar(calendar: Calendar) {
        CallAPIJD.StartQuery("api/privatelesson/${Singleton.User.Id}/${calendar.get(Calendar.YEAR)}/${
            calendar.get(
                Calendar.MONTH
            ) + 1
        }",
            CallAPIJD.Companion.method.GET,
            "",
            object : IServicesJD {
                override fun Finish(response: String, status: Int) {

                    if (status == HTTP.OK) {
                        var list: java.util.ArrayList<PrivateLesson> =
                            JSONArray(response).toList(PrivateLesson::class.java.name).Cast()

                        _resultPrivateLesson.postValue( ResponseJD().apply{
                            this.status = status
                            this.data = list
                            this.response = response


                        })


                    }


                }

                override fun Error(response: String, status: Int) {

                }
            }
        )

    }

    //update info user login
    private val _userName =
        MutableLiveData("${Singleton.User.FirstName} ${Singleton.User.LastName}")
    val userName: LiveData<String> = _userName

    private val _lastConection = MutableLiveData("Loading...")
    val lastConection: LiveData<String> = _lastConection

    private val _imagen = MutableLiveData<Bitmap>(Singleton.User.Photo.ToBitmap())
    val imagen: LiveData<Bitmap> = _imagen

    fun UpdateData() {
        _userName.value = "${Singleton.User.FirstName} ${Singleton.User.LastName}"
        try {
            Singleton.User.Photo.ToBitmap()
        } catch (e: Exception) {
        }
        _imagen.value = Singleton.User.Photo.ToBitmap()
        Handler(Looper.getMainLooper()).postDelayed({

            try {
                _lastConection.value = "Last Login ${Singleton.LoginHistory.DateTime} :)"
            } catch (e: Exception) {
            }

        }, 3000)
    }


    //date time now
    private val _dateNow = MutableLiveData<Calendar>(Calendar.getInstance())
    val dateNow: LiveData<Calendar> = _dateNow

    fun ChangeDateNow(calendar: Calendar) {
        _dateNow.postValue(calendar)
    }

}