package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var database: DatabaseReference
    private lateinit var insightBtn: LinearLayout
    private lateinit var view_all_btn: TextView

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
        getFinanceData()
        setupRecyclerView()
    }

    private fun init(){
        rvRecycleView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.add_button)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
        tvTotalIncome = findViewById(R.id.tv_total_income)
        tvTotalExpense = findViewById(R.id.tv_total_expense)
        insightBtn = findViewById(R.id.nav_insights)
        view_all_btn = findViewById(R.id.view_all_button)
    }

    private fun buttonClicked(){
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
                    val income = financeSnapshot.child("income").getValue(Double::class.java)?: 0.0
                    val expense = financeSnapshot.child("expense").getValue(Double::class.java)?: 0.0
                    tvTotalIncome.text = String.format("$%.1f", income)
                    tvTotalExpense.text = String.format("$%.1f", expense)
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

    private fun deleteTransaction(transaction: Transaction) {
        val id = transaction.id
        if (id != null) {
            database.child(id).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error: No ID found", Toast.LENGTH_SHORT).show()
        }
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
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load data: ${error.message}")
            }
        })
    }

}