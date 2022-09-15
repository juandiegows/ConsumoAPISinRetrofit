package com.example.mobile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ItemDayBinding
import com.example.mobile.model.DayCalendar

class AdapterDay (var lista :ArrayList<DayCalendar>): RecyclerView.Adapter<AdapterDay.AdapterDayHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDayHolder {

        var binding= ItemDayBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false))

        return  AdapterDayHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterDayHolder, position: Int) {
        with(holder.binding){
            txtDay.text = lista.get(position).day
            imgDay.isVisible = lista.get(position).activo

        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class AdapterDayHolder(var binding:ItemDayBinding): RecyclerView.ViewHolder(binding.root)
}