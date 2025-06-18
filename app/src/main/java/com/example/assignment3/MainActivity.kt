package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var rvRecycleView:RecyclerView
    private lateinit var adapter: myAdapter
    private lateinit var addButton: FloatingActionButton
    private lateinit var tvTotalBalance: TextView
    private lateinit var database: DatabaseReference
    private lateinit var insightBtn: LinearLayout
    private lateinit var view_all_btn: TextView
    private lateinit var noTransactionImage: ImageView
    private lateinit var noTransactionText: TextView


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
        buttonClickedListener()
        getFinanceData()
        setupRecyclerView()
    }

    private fun init(){
        rvRecycleView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.add_button)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
        insightBtn = findViewById(R.id.nav_insights)
        view_all_btn = findViewById(R.id.view_all_button)
        noTransactionImage = findViewById(R.id.img_no_transaction)
        noTransactionText = findViewById(R.id.text_no_transaction)
    }

    private fun buttonClickedListener(){
        addButton.setOnClickListener{addExpense()}
        insightBtn.setOnClickListener{navigateInsight()}
        view_all_btn.setOnClickListener{navigateAllTransaction()}
    }

    private fun navigateAllTransaction(){
        val intent = Intent(this,AllTransactionList::class.java)
        startActivity(intent)
    }
    private fun navigateInsight(){
        val intent = Intent(this, insight::class.java)
        startActivity(intent)
    }

    private fun getFinanceData() {
        database = FirebaseDatabase.getInstance().reference
        val financeRef = database.child("finance")
        financeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (financeSnapshot in snapshot.children) {
                    val balance = financeSnapshot.child("balance").getValue(Double::class.java)?: 0.0
                    tvTotalBalance.text = String.format("$%.2f", balance)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to read value.", error.toException())
            }
        })
    }

    private fun addExpense(){
        val intent = Intent(this, new_transaction::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        rvRecycleView = findViewById(R.id.recyclerView)

        adapter = myAdapter(
            showMenu = false
        )


        rvRecycleView.layoutManager = LinearLayoutManager(this)
        rvRecycleView.adapter = adapter

        loadTransactionsFromFirebase()
    }


    private fun loadTransactionsFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("transaction")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Transaction>()
                for (item in snapshot.children) {
                    val transaction = item.getValue(Transaction::class.java)
                    if (transaction?.type == "EXPENSE") {
                        tempList.add(transaction)
                    }
                    else if(transaction?.type == "INCOME"){
                        tempList.add(transaction)
                    }
                }
                tempList.reverse()
                // Only show the latest 3 (or change to 5 if you want)
                val latestTransactions = tempList.take(4)
                adapter.updateData(latestTransactions)
                if (tempList.isEmpty()) {
                    noTransactionImage.visibility = View.VISIBLE
                    noTransactionText.visibility = View.VISIBLE
                    rvRecycleView.visibility = View.GONE
                } else {
                    noTransactionImage.visibility = View.GONE
                    noTransactionText.visibility = View.GONE
                    rvRecycleView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load data: ${error.message}")
            }
        })
    }

}