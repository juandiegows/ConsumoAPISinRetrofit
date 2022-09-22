package com.example.mobile.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
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
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.helper.Cast
import com.example.mobile.helper.HTTP
import com.example.mobile.helper.Singleton
import com.example.mobile.helper.toList
import com.example.mobile.model.DayCalendar
import com.example.mobile.model.PrivateLesson
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        Load()
        Init()
        Eventos()



        return root
    }

    private fun Load() {

        with(homeViewModel) {
            result.observe(viewLifecycleOwner) {

                binding.recyCaledar.layoutManager =
                    GridLayoutManager(this@HomeFragment.requireContext(), 7)

                var list: ArrayList<DayCalendar> = it.data as ArrayList<DayCalendar>
                binding.recyCaledar.adapter = AdapterDay(list,
                    object : IAdapterDay {
                        override fun ObtenerDay(day: String) {
                            Toast.makeText(
                                this@HomeFragment.requireContext(),
                                day,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                AddPrivateLessonToCaledar(this.dateNow.value!!)

            }
            imagen.observe(viewLifecycleOwner) {
                binding.img.setImageBitmap(it)
            }
            userName.observe(viewLifecycleOwner) {
                binding.txtUserName.text = it
            }
            lastConection.observe(viewLifecycleOwner) {
                binding.txtLastCOn.text = it
            }
            resultPrivateLesson.observe(viewLifecycleOwner) { r ->


                var listday = binding.recyCaledar.adapter as AdapterDay

                var listprivate: ArrayList<PrivateLesson> =
                    r.data.Cast()

                listday.lista.forEach { d ->

                    if (listprivate.stream().filter { x ->
                            x.DateTime.get(Calendar.DAY_OF_MONTH).toString() == d.day
                        }.collect(Collectors.toList()).size > 0) {
                        d.activo = true
                    }


                }
                listday.notifyDataSetChanged()


            }
            dateNow.observe(viewLifecycleOwner) {
                UpdateCaledar(it)
                binding.txtMes.text = SimpleDateFormat("MMMM").format(
                    Date(
                        it.get(Calendar.YEAR),
                        it.get(Calendar.MONTH),
                        it.get(Calendar.DAY_OF_MONTH)
                    )
                )
            }
        }


    }

    private fun Eventos() {
        binding.txtMes.setOnClickListener {
            var calendar = homeViewModel.dateNow.value!!
            var alert = DatePickerDialog(
                this.requireContext(), AlertDialog.THEME_HOLO_LIGHT, { v, y, m, d ->
                    run {

                        txtMes.text = SimpleDateFormat("MMMM").format(Date(y, m, d).apply {
                            month = m
                        })
                        UpdateCaledar(calendar.apply {
                            this.set(Calendar.YEAR, txtYear.text.toString().toInt())
                            this.set(Calendar.MONTH, m)
                            this.set(Calendar.DAY_OF_MONTH, 1)
                        })
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
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
            var calendar = homeViewModel.dateNow.value!!
            var alert = DatePickerDialog(
                this.requireContext(), AlertDialog.THEME_HOLO_LIGHT, { v, y, m, d ->
                    run {
                        txtYear.text = y.toString()
                        UpdateCaledar(calendar.apply {
                            this.set(Calendar.YEAR, txtYear.text.toString().toInt())
                            this.set(Calendar.MONTH, m)
                            this.set(Calendar.DAY_OF_MONTH, 1)
                        })

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
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
    }

    private fun Init() {

        homeViewModel.UpdateData()
        UpdateCaledar(homeViewModel.dateNow.value!!)


    }

    private fun UpdateCaledar(calendar: Calendar) {

        with(homeViewModel) {
            UpdateCaledar(calendar)
            ChangeDateNow(calendar)
            AddPrivateLessonToCaledar(calendar)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}