package com.sae.wavetime.ui.item.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.R
import com.sae.wavetime.data.model.Item

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var items: List<Item> = emptyList()

    fun submitList(newList : List<Item>) {
        items = newList
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvDescription.text = item.description
    }

    override fun getItemCount(): Int {
        return items.size
    }
}