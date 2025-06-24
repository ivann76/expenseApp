package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AllTransactionList : AppCompatActivity() {

    private lateinit var rvRecycleView: RecyclerView
    private lateinit var adapter: myAdapter
    private lateinit var closeBtn: ImageButton
    private lateinit var noTransactionImage: ImageView
    private lateinit var btnNoTransaction: Button
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("transaction")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transaction_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        setupRecyclerView()
        buttonClickedListener()
    }

    private fun init(){
        closeBtn = findViewById(R.id.btn_close)
        noTransactionImage = findViewById(R.id.img_no_transaction)
        btnNoTransaction = findViewById(R.id.btn_no_transaction)
    }

    private fun buttonClickedListener(){
        closeBtn.setOnClickListener { finish() }
        btnNoTransaction.setOnClickListener{ finish()}
    }

    private fun setupRecyclerView() {
        rvRecycleView = findViewById(R.id.recyclerView)

        adapter = myAdapter(
            showMenu = true,
            onDeleteClick = { transaction -> deleteTransaction(transaction) },
            onEditClick = { transaction ->
                val intent = Intent(this, EditTransaction::class.java)
                intent.putExtra("transaction", transaction)
                startActivity(intent)
            }
        )



        rvRecycleView.layoutManager = LinearLayoutManager(this)
        rvRecycleView.adapter = adapter

        loadTransactionsFromFirebase()
    }

    private fun loadTransactionsFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Transaction>()
                for (item in snapshot.children) {
                    val transaction = item.getValue(Transaction::class.java)
                    if (transaction != null) {
                        transaction.id = item.key.toString() // Make sure ID is saved
                        tempList.add(transaction)
                    }
                }
                tempList.reverse()
                adapter.updateData(tempList)

                if (tempList.isEmpty()) {
                    noTransactionImage.visibility = View.VISIBLE
                    btnNoTransaction.visibility = View.VISIBLE
                    rvRecycleView.visibility = View.GONE
                } else {
                    noTransactionImage.visibility = View.GONE
                    btnNoTransaction.visibility = View.GONE
                    rvRecycleView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load data: ${error.message}")
            }
        })
    }

    private fun deleteTransaction(transaction: Transaction) {
        val id = transaction.id
        if (id != null) {
            databaseRef.child(id).removeValue().addOnSuccessListener {
                updateFinanceAfterDelete(transaction)  // <-- Call it here
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error: No ID found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFinanceAfterDelete(transaction: Transaction) {
        val financeRef = FirebaseDatabase.getInstance().getReference("finance")

        financeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.children.firstOrNull()?.getValue(FinanceSummary::class.java)
                val summaryId = current?.id ?: return

                var balance = current.balance
                var income = current.income
                var expense = current.expense

                val amount = transaction.price ?: 0.0

                if (transaction.type == "INCOME") {
                    balance -= amount
                    income -= amount
                } else if (transaction.type == "EXPENSE") {
                    balance += amount
                    expense -= amount
                }

                // Clamp to avoid negatives
                if (balance < 0) balance = 0.0
                if (income < 0) income = 0.0
                if (expense < 0) expense = 0.0

                val updatedSummary = FinanceSummary(summaryId, balance, income, expense)
                financeRef.child(summaryId).setValue(updatedSummary)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FinanceUpdate", "Error updating after delete: ${error.message}")
            }
        })
    }

}
