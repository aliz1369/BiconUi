package com.tivasoft.biconui.ui.doctor.packages.add_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tivasoft.biconui.data.model.network.responses.doctor.ItemData
import com.tivasoft.biconui.R

class SpinnerItemAdapter(
    context: Context, textViewResourceId: Int, private val values: ArrayList<ItemData>
) : ArrayAdapter<ItemData>(
    context,
    textViewResourceId,
    values
) {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): ItemData? {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    @SuppressLint("ViewHolder")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val inflater  = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_item,parent,false)
        val tvName = view.findViewById<TextView>(R.id.tv_spinner)
        tvName.text = values[position].title
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater  = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_item,parent,false)
        val tvName = view.findViewById<TextView>(R.id.tv_spinner)
        tvName.text = values[position].title
        return view
    }


}