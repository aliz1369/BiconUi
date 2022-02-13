package com.tivasoft.biconui.ui.patient.mood_level.adapter


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.local.ui.MoodLevel
import com.tivasoft.biconui.databinding.ItemMoodLevelBinding


class MoodLevelAdapter(private var moods: ArrayList<MoodLevel>):RecyclerView.Adapter<MoodLevelAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ItemMoodLevelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(mood: MoodLevel) {
            binding.apply {
                tvTitle.text = mood.title
                tvMoodName.text = mood.name
                tvDate.text = mood.date
                if(mood.percent.toInt()<0){
                    tvPercent.setTextColor(Color.RED)
                    tvPercent.text = mood.percent+"%"
                }else{
                    tvPercent.setTextColor(Color.WHITE)
                    tvPercent.text = mood.percent+"%"
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMoodLevelBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return moods.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(moods[position])
    }
}