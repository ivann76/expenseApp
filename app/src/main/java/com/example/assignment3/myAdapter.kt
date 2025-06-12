package com.example.assignment3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class myAdapter: RecyclerView.Adapter<myAdapter.ViewHolder>() {
    private val transactions = mutableListOf<Transaction>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.category)
        val detail: TextView = itemView.findViewById(R.id.detail)
        val price: TextView = itemView.findViewById(R.id.price)
        val date: TextView = itemView.findViewById(R.id.date)
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
        holder.price.text = transaction.price
//        holder.date.text = transaction.date
    }

    override fun getItemCount(): Int = transactions.size

//    // Add a method to insert a new transaction
//    fun addTransaction(transaction: Transaction) {
//        transactions.add(transaction)
//        notifyItemInserted(transactions.size - 1)
//    }
}
