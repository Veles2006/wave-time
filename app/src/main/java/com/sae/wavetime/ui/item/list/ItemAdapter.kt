package com.sae.wavetime.ui.item.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.InventoryUiModel

class ItemAdapter(
    private val useItem: (itemId: String, amount: Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var items: List<InventoryUiModel> = emptyList()

    fun submitList(newList : List<InventoryUiModel>) {
        items = newList
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTier: TextView = itemView.findViewById(R.id.tvTier)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        val btnUseItem: MaterialButton = itemView.findViewById(R.id.btnUseItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvTier.text = item.tier
        holder.tvQuantity.text = "X${item.quantity}"
        holder.btnUseItem.setOnClickListener {
            useItem(item.id, 1)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}