package com.example.assignment3

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class myAdapter: RecyclerView.Adapter<myAdapter.ViewHolder>() {
    private val transactions = mutableListOf<Transaction>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.category)
        val detail: TextView = itemView.findViewById(R.id.detail)
        val price: TextView = itemView.findViewById(R.id.price)
        val date: TextView = itemView.findViewById(R.id.date)
        val logo: ImageView = itemView.findViewById(R.id.logo)
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
        holder.price.text= transaction.price.toString()
        holder.date.text = transaction.date
        // Set the logo based on the category
        val logoResId = when (transaction.category?.lowercase()) {
            "food" -> R.drawable.food_icons
            "transport" -> R.drawable.transportation_icons
            "shopping" -> R.drawable.shopping_icons
            "entertainment" -> R.drawable.entertaiment_icons
            else -> R.drawable.income_icon // fallback logo
        }

        holder.logo.setImageResource(logoResId)
    }

    override fun getItemCount(): Int = transactions.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }


}
