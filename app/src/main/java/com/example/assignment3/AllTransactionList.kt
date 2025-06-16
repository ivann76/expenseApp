package com.example.assignment3

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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

class AllTransactionList : AppCompatActivity() {
    private lateinit var rvRecycleView: RecyclerView
    private lateinit var adapter: myAdapter
    private lateinit var closeBtn:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_transaction_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recycleView()
        closeBtnClicked()
    }

    private fun closeBtnClicked(){
        closeBtn = findViewById(R.id.btn_close)
        closeBtn.setOnClickListener{finish()}
    }
    private fun recycleView(){
        rvRecycleView = findViewById(R.id.recyclerView)
        adapter = myAdapter()


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
                adapter.updateData(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load data: ${error.message}")
            }
        })
    }
}