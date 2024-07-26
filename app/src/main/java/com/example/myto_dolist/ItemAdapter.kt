package com.example.myto_dolist

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myto_dolist.databinding.ItemsRowBinding
import java.util.Collections

class ItemAdapter(
    private var items: ArrayList<TaskEntity>,
    private val updateListener: (TaskEntity) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llMain = binding.llMain
        val tvTask = binding.tvTask
        val radioButton = binding.radioButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvTask.text = item.task

        holder.tvTask.paintFlags = if (item.isCompleted) {
            holder.tvTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.radioButton.isChecked = item.isCompleted

        holder.radioButton.setOnClickListener {
            item.isCompleted = !item.isCompleted
            updateListener.invoke(item)
        }
    }

    fun getItemAtPosition(position: Int): TaskEntity {
        return items[position]
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(newItems: List<TaskEntity>) {
        items = ArrayList(newItems)
        notifyDataSetChanged()
    }
}
