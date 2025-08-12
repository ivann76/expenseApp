package com.example.assignment3

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.PopupMenu

class myAdapter(
    private val showMenu: Boolean = false,
    private val onDeleteClick: ((Transaction) -> Unit)? = null,
    private val onEditClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<myAdapter.ViewHolder>() {

    private val transactions = mutableListOf<Transaction>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.category)
        val detail: TextView = itemView.findViewById(R.id.detail)
        val price: TextView = itemView.findViewById(R.id.price)
        val date: TextView = itemView.findViewById(R.id.date)
        val logo: ImageView = itemView.findViewById(R.id.logo)
        val menuBtn: ImageButton = itemView.findViewById(R.id.btn_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.category.text = transaction.category
        holder.detail.text = transaction.detail
        holder.price.text = "$${transaction.price}"
        holder.date.text = transaction.date

        val logoResId = when (transaction.category?.lowercase()) {
            "food" -> R.drawable.food_icons
            "transport" -> R.drawable.transportation_icons
            "shopping" -> R.drawable.shopping_icons
            "entertainment" -> R.drawable.entertaiment_icons
            "sport" -> R.drawable.social_logo
            else -> R.drawable.income_icon
        }

        holder.logo.setImageResource(logoResId)

        val context = holder.itemView.context
        val colorRes = if (transaction.type == "INCOME") R.color.green_expense else R.color.red_expense
        holder.price.setTextColor(context.getColor(colorRes))

        // Menu button
        if (showMenu) {
            holder.menuBtn.visibility = View.VISIBLE
        } else {
            holder.menuBtn.visibility = View.GONE
        }

        holder.menuBtn.setOnClickListener {
            val popup = PopupMenu(context, holder.menuBtn)
            popup.inflate(R.menu.transaction_menu)

            popup.menu.findItem(R.id.menu_delete).isVisible = showMenu

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        onDeleteClick?.invoke(transaction)
                        true
                    }
                    R.id.menu_edit -> {
                        onEditClick?.invoke(transaction)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }
}
