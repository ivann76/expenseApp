package com.example.assignment3

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
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
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("transaction")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transaction_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        closeBtn = findViewById(R.id.btn_close)
        closeBtn.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        rvRecycleView = findViewById(R.id.recyclerView)

        adapter = myAdapter(
            showMenu = true,
            onDeleteClick = { transaction -> deleteTransaction(transaction) }
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
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error: No ID found", Toast.LENGTH_SHORT).show()
        }
    }
}
