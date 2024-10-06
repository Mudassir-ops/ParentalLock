package com.example.parentallock.presentation.customlock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parentallock.data.model.AllAppsEntity
import com.example.parentallock.databinding.AllDeviceCustomAppsItemLayoutBinding


class MyDeviceAppsAdapter(
    private val callbackSelection: (AllAppsEntity) -> Unit,
    var dataList: ArrayList<AllAppsEntity?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun updateVersionsList(mProducts: ArrayList<AllAppsEntity?>) {
        dataList = mProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AllDeviceCustomAppsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VersionsItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val headerHolder = holder as VersionsItemsViewHolder
        dataList[position]?.let { headerHolder.bindData(it) }
    }

    override fun getItemCount(): Int = dataList.size
    inner class VersionsItemsViewHolder(private val binding: AllDeviceCustomAppsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AllAppsEntity) {
            binding.allEntity = item
            binding.tvVersion.isSelected = true
            itemView.setOnClickListener {
                item.isSelected = !item.isSelected
                notifyItemChanged(adapterPosition)
                callbackSelection.invoke(item)
            }
        }
    }
}