package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var rvRecycleView:RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var tvTotalBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        buttonClicked()
    }

    private fun init(){
        rvRecycleView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.add_button)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
    }

    private fun buttonClicked(){
        addButton.setOnClickListener{addExpense()}
    }

    private fun addExpense(){
        val intent = Intent(this, add_expense::class.java)
        startActivity(intent)
    }

    private fun recycleView(){
        rvRecycleView.layoutManager = LinearLayoutManager(this)
        rvRecycleView.adapter = myAdapter()
//        val newTransaction = Transaction(
//            category = "Adidas Store",
//            detail = "Credit Card",
//            price = "$180",
//            date = "Tue, 11 June 2025"
//        )
//        myAdapter.addTransaction(newTransaction)
    }
}