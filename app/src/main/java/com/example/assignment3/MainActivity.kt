package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var rvRecycleView:RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var tvTotalBalance: TextView
//    private lateinit var database: DatabaseReference

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

//    private fun getBalanceData() {
//        database.child("transactions").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                val specificUser = database.child("").getValue(Transaction::class.java)
//                specificUser?.let {
//                    Log.d("FirebaseExample", "Specific User: ${it.price}")
//                }
//
//            }
//
//        })
//    }
    private fun addExpense(){
        val intent = Intent(this, new_transaction::class.java)
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