package com.example.mobile.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobile.Adapter.AdapterDay
import com.example.mobile.Adapter.IAdapterDay
import com.example.mobile.Network.CallAPIJD
import com.example.mobile.Network.IServicesJD
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.helper.*
import com.example.mobile.model.DayCalendar
import com.example.mobile.model.PrivateLesson
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var mesIndex: Int = 0
    var dateActual: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        try {
            binding.img.setImageBitmap(Singleton.User.Photo.ToBitmap())
        } catch (e: Exception) {
        }
        var calendar = Calendar.getInstance()
        binding.txtMes.text = SimpleDateFormat("MMMM").format(
            Date(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )
        mesIndex = calendar.get(Calendar.MONTH)

        binding.txtMes.setOnClickListener {
            var calendar = dateActual
            var alert = DatePickerDialog(
                this.requireContext(), AlertDialog.THEME_HOLO_LIGHT, { v, y, m, d ->
                    run {
                        Log.e("TAG", "onCreateView: $m")
                        mesIndex = m
                        txtMes.text = SimpleDateFormat("MMMM").format(Date(y, m, d).apply {
                            month = m
                        })
                        dateActual = calendar.apply {
                            this.set(Calendar.YEAR, txtYear.text.toString().toInt())
                            this.set(Calendar.MONTH, mesIndex)
                            this.set(Calendar.DAY_OF_MONTH, 1)

                        }
                        UpdateCaledar(dateActual)
                    }
                },
                dateActual.get(Calendar.YEAR),
                dateActual.get(Calendar.MONTH),
                dateActual.get(Calendar.DAY_OF_MONTH)
            )
            var idDay = getResources().getIdentifier("day", "id", "android")
            var viewDay: View = alert.getDatePicker().findViewById(idDay)
            viewDay.visibility = View.GONE

            var idYear = getResources().getIdentifier("year", "id", "android")
            var viewYear: View = alert.getDatePicker().findViewById(idYear)
            viewYear.visibility = View.GONE
            alert.datePicker.minDate = Calendar.getInstance().apply {
                this.set(Calendar.YEAR, 2022)
                this.set(Calendar.DAY_OF_MONTH, 0)
                this.set(Calendar.MONTH, 1)

            }.timeInMillis
            alert.datePicker.maxDate = Calendar.getInstance().apply {
                this.set(Calendar.YEAR, 2022)
                this.set(Calendar.DAY_OF_MONTH, 12)
                this.set(Calendar.MONTH, 11)

            }.timeInMillis
            alert.show()
        }
        binding.txtYear.setOnClickListener {
            var calendar = Calendar.getInstance()
            var alert = DatePickerDialog(
                this.requireContext(), AlertDialog.THEME_HOLO_LIGHT, { v, y, m, d ->
                    run {
                        Log.e("TAG", "onCreateView: $m")
                        txtYear.text = y.toString()
                        UpdateCaledar(calendar.apply {
                            this.set(Calendar.YEAR, txtYear.text.toString().toInt())
                            this.set(Calendar.MONTH, mesIndex)
                            this.set(Calendar.DAY_OF_MONTH, 1)
                        })
                    }
                },
                dateActual.get(Calendar.YEAR),
                dateActual.get(Calendar.MONTH),
                dateActual.get(Calendar.DAY_OF_MONTH)
            )
            var idDay = getResources().getIdentifier("day", "id", "android")
            var viewDay: View = alert.getDatePicker().findViewById(idDay)
            viewDay.visibility = View.GONE

            var idYear = getResources().getIdentifier("month", "id", "android")
            var viewYear: View = alert.getDatePicker().findViewById(idYear)
            viewYear.visibility = View.GONE
            alert.datePicker.minDate = Calendar.getInstance().apply {
                this.set(Calendar.YEAR, 2000)
                this.set(Calendar.DAY_OF_MONTH, 0)
                this.set(Calendar.MONTH, 1)

            }.timeInMillis
            alert.datePicker.maxDate = Calendar.getInstance().apply {
                this.set(Calendar.YEAR, 2032)
                this.set(Calendar.DAY_OF_MONTH, 12)
                this.set(Calendar.MONTH, 11)

            }.timeInMillis
            alert.show()
        }
        UpdateCaledar(calendar)
        binding.txtUserName.text = "${Singleton.User.FirstName} ${Singleton.User.LastName}"
        Handler(Looper.getMainLooper()).postDelayed({

            try {
                binding.txtLastCOn.text = "Last Login ${Singleton.LoginHistory.DateTime} :)"
            } catch (e: Exception) {
            }

        }, 3000)


        return root
    }

    private fun UpdateCaledar(calendar: Calendar) {
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
        var dayWeek2: Int = calendar.get(Calendar.DAY_OF_WEEK)
        Log.e("TAG", "UpdateCaledar: $dayWeek $dayWeek2 ")
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
        binding.recyCaledar.layoutManager = GridLayoutManager(this@HomeFragment.requireContext(), 7)
        binding.recyCaledar.adapter = AdapterDay(lista, object : IAdapterDay {
            override fun ObtenerDay(day: String) {
                Toast.makeText(this@HomeFragment.requireContext(), day, Toast.LENGTH_SHORT).show()
            }
        })

        CallAPIJD.StartQuery("api/privatelesson/${Singleton.User.Id}/${dateActual.get(Calendar.YEAR)}/${
            dateActual.get(
                Calendar.MONTH
            ) + 1
        }",
            CallAPIJD.Companion.method.GET,
            "",
            object : IServicesJD {
                override fun Finish(response: String, status: Int) {
                    this@HomeFragment.requireActivity().runOnUiThread {
                        if (status == HTTP.OK) {
                            var list: ArrayList<PrivateLesson> =
                                JSONArray(response).toList(PrivateLesson::class.java.name).Cast()
                            println(list.size)

                            var listday = binding.recyCaledar.adapter as AdapterDay
                            listday.lista.forEach {

                                if (list.stream().filter { x ->
                                        x.DateTime.get(Calendar.DAY_OF_MONTH).toString() == it.day
                                    }.collect(Collectors.toList()).size > 0) {
                                    it.activo = true
                                }
                            }
                            listday.notifyDataSetChanged()
                        }
                    }


                }

                override fun Error(response: String, status: Int) {

                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}