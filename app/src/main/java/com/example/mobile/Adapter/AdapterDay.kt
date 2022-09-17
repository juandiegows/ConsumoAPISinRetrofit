package com.example.mobile.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.databinding.ItemDayBinding
import com.example.mobile.model.DayCalendar

class AdapterDay(var lista: ArrayList<DayCalendar>, val IAdapter:IAdapterDay) :
    RecyclerView.Adapter<AdapterDay.AdapterDayHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDayHolder {

        var binding = ItemDayBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        )

        return AdapterDayHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterDayHolder, position: Int) {
        with(holder.binding) {

            txtDay.text = lista.get(position).day
            imgDay.isVisible = lista.get(position).activo
            if (txtDay.text.isEmpty() || (position in 0..6)) {
                this.root.background = null
                this.txtDay.text = "  ${this.txtDay.text}"
            }else{
                this.root.setOnClickListener {
                    IAdapter.ObtenerDay(lista.get(position).day)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class AdapterDayHolder(var binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root)
}