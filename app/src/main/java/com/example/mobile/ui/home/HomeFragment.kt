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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobile.AdapterDay
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.helper.Singleton
import com.example.mobile.helper.ToBitmap
import com.example.mobile.model.DayCalendar
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        binding.txtMes.setOnClickListener {
            var calendar = Calendar.getInstance()
            var alert = DatePickerDialog(this.requireContext(),AlertDialog.THEME_HOLO_LIGHT, {
                    v, y, m, d ->
                run {
                    Log.e("TAG", "onCreateView: $m" )
                    txtMes.text = SimpleDateFormat("MMMM").format(Date(y, m, d).apply {
                        month = m
                    })

                }
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

           alert.show()
        }

        var lista = arrayListOf<DayCalendar>()
        for (i in 1..31) {
            if (i % 2 != 0)
                lista.add(DayCalendar().apply {
                    day = i.toString()
                    activo = true
                })
            else
                lista.add(DayCalendar().apply {
                    day = i.toString()
                    activo = false
                })
        }
        binding.recyCaledar.layoutManager = GridLayoutManager(this@HomeFragment.requireContext(), 7)
        binding.recyCaledar.adapter = AdapterDay(lista)
        binding.txtUserName.text = "${Singleton.User.FirstName} ${Singleton.User.LastName}"
        Handler(Looper.getMainLooper()).postDelayed({

            binding.txtLastCOn.text = "Last Login ${Singleton.LoginHistory.DateTime} :)"

        }, 8000)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}