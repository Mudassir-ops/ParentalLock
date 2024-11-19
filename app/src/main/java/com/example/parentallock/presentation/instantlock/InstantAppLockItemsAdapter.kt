package com.example.parentallock.presentation.instantlock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parental.control.displaytime.kids.safety.data.model.InstantLockAdapterItem
import com.parental.control.displaytime.kids.safety.databinding.InstantAppCallLayoutItemBinding
import com.parental.control.displaytime.kids.safety.databinding.InstantAppLayoutItemBinding

@Suppress("unused")
class InstantAppLockItemsAdapter(
    private val callback: (Int, Int,Int) -> Unit,
    private var instantAppEntityList: List<InstantLockAdapterItem?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_CALL = 1
    }

    override fun getItemViewType(position: Int): Int {
        val item =
            requireNotNull(instantAppEntityList[position]) { "Item at position $position is null" }
        return when (item) {
            is InstantLockAdapterItem.NORMAL -> TYPE_NORMAL
            is InstantLockAdapterItem.CALL -> TYPE_CALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NORMAL -> {
                val binding = InstantAppLayoutItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                InstantNormalAnimationViewHolder(binding)
            }

            TYPE_CALL -> {
                val binding = InstantAppCallLayoutItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                InstantCallAnimationViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateVersionsList(instantAppEntities: List<InstantLockAdapterItem?>) {
        instantAppEntityList = instantAppEntities
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = instantAppEntityList[position] ?: return
        when (item) {
            is InstantLockAdapterItem.NORMAL -> (holder as InstantNormalAnimationViewHolder).bindData(
                item,position
            )

            is InstantLockAdapterItem.CALL -> (holder as InstantCallAnimationViewHolder).bindData(
                item,position
            )
        }
    }

    override fun getItemCount(): Int = instantAppEntityList.size

    inner class InstantNormalAnimationViewHolder(private val binding: InstantAppLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: InstantLockAdapterItem.NORMAL, position: Int) {
            binding.instantAppDataModel = item
            binding.executePendingBindings()
            itemView.setOnClickListener {
                selectItem(adapterPosition)
                callback.invoke(item.lottieRawRes, item.soundRawRes,position)
            }
        }
    }

    inner class InstantCallAnimationViewHolder(private val binding: InstantAppCallLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: InstantLockAdapterItem.CALL, position: Int) {
            binding.instantAppDataModel = item
            binding.executePendingBindings()
            itemView.setOnClickListener {
                selectItem(adapterPosition)
                callback.invoke(item.lottieRawRes, item.soundRawRes,position)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectItem(position: Int) {
        instantAppEntityList.forEachIndexed { index, item ->
            when (item) {
                is InstantLockAdapterItem.NORMAL -> {
                    item.isSelected = index == position
                }

                is InstantLockAdapterItem.CALL -> {
                    item.isSelected = index == position
                }

                null -> TODO()
            }
        }
        notifyDataSetChanged()
    }
}
