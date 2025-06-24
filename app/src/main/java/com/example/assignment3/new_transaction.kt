package com.example.assignment3

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class new_transaction : AppCompatActivity() {
    private lateinit var transactionType: RadioGroup
    private lateinit var closeButton:ImageButton
    private lateinit var amount:TextInputEditText
    private lateinit var description: EditText
    private lateinit var btnAddTransaction: Button
    private lateinit var llCategory: LinearLayout
    private lateinit var tvCategory:TextView
    private lateinit var ivCategoryIcon: ImageView
    private lateinit var llDate:LinearLayout
    private lateinit var tvDate:TextView
    private lateinit var ivCalendar:ImageView
    private lateinit var layoutCategorySection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_transaction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        buttonClickedListener()
        categoryList()
        transactionType()
        // Set current date (you can format it as you prefer)
        val currentDate = Calendar.getInstance()
        updateDateDisplay(currentDate)
    }
    private fun init(){
        closeButton = findViewById(R.id.btn_close)
        amount = findViewById(R.id.et_amount)
        description = findViewById(R.id.et_description)
        btnAddTransaction = findViewById(R.id.btn_add_transaction)
        llCategory = findViewById(R.id.ll_category)
        tvCategory = findViewById(R.id.tv_category)
        ivCategoryIcon = findViewById(R.id.iv_category_icon)
        llDate = findViewById(R.id.ll_date)
        tvDate = findViewById(R.id.tv_date)
        ivCalendar = findViewById(R.id.iv_calendar)
        transactionType = findViewById(R.id.rg_transaction_type)
        layoutCategorySection = findViewById(R.id.layout_category_section)
    }

    private fun buttonClickedListener(){
        closeButton.setOnClickListener{finish()}
        btnAddTransaction.setOnClickListener{addExpense()}
        llDate.setOnClickListener { showDatePicker() }
    }

    private fun transactionType(){
        transactionType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_expense -> {
                    layoutCategorySection.visibility = View.VISIBLE
                    btnAddTransaction.text = "Add Expense"
                }
                R.id.rb_income -> {
                    layoutCategorySection.visibility = View.GONE
                    btnAddTransaction.text = "Add Income"
                }
            }
        }

    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // User selected a date
                val newDate = Calendar.getInstance()
                newDate.set(selectedYear, selectedMonth, selectedDay)
                updateDateDisplay(newDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun updateDateDisplay(calendar: Calendar) {
        // Format the date as "Oct 5th, 2023"
        val dateFormat = SimpleDateFormat("MMM d', 'yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

    }



    private fun categoryList(){
        // Define your categories with their icons
        data class CategoryItem(val name: String, val iconRes: Int)

        val categories = listOf(
            CategoryItem("Food", R.drawable.food_icons),
            CategoryItem("Transport", R.drawable.transportation_icons),
            CategoryItem("Shopping", R.drawable.shopping_icons),
            CategoryItem("Entertainment", R.drawable.entertaiment_icons)
        )

        llCategory.setOnClickListener { view ->
            // Create a popup menu
            val popup = PopupMenu(this, view)

            // Add menu items
            categories.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item.name)
            }

            // Set click listener
            popup.setOnMenuItemClickListener { item ->
                val selected = categories[item.itemId]

                // Update the displayed category and icon
                tvCategory.text = selected.name
                ivCategoryIcon.setImageResource(selected.iconRes)

                true
            }

            popup.show()
        }
    }

    private fun addExpense(){
        val db = FirebaseDatabase.getInstance().getReference("transaction")

        val price = amount.text.toString().toDoubleOrNull() ?: 0.0
        val categoryName = tvCategory.text.toString().trim()
        val detail = description.text.toString().trim()
        val date = tvDate.text.toString().trim()

        val selectedTypeId = transactionType.checkedRadioButtonId
        val selectedRadio = findViewById<RadioButton>(selectedTypeId)
        val type = selectedRadio.text.toString().uppercase()


        Log.d("dataCheck", "${price},${categoryName},${detail},${date},${type}")

        if (price.isNaN() || categoryName.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }


        btnAddTransaction.isEnabled = false
        //make sure is not duplicate data store inside database
        //if not user might keep press the button until the toast message is coming out
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Saving...")
            setCancelable(false)
            show()
        }

        if(type == "INCOME"){
            val transactionId = db.push().key!!
            val transaction = Transaction(transactionId, type, "Income", detail, price, date)
            db.child(transactionId).setValue(transaction)
                .addOnCompleteListener {
                    updateFinanceSummary(transaction, progressDialog)
                    Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1)
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    btnAddTransaction.isEnabled = false
                    Toast.makeText(
                        this,
                        "Failed to save Transaction: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        else if(type == "EXPENSE"){
            val transactionId = db.push().key!!
            val transaction = Transaction(transactionId, type, categoryName, detail, price, date)
            db.child(transactionId).setValue(transaction)
                .addOnCompleteListener {
                    updateFinanceSummary(transaction, progressDialog)
                    Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1)
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    btnAddTransaction.isEnabled = false
                    Toast.makeText(
                        this,
                        "Failed to save Transaction: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
    private fun updateFinanceSummary(transaction: Transaction, progressDialog: ProgressDialog) {
        val financeRef = FirebaseDatabase.getInstance().getReference("finance")
        financeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.children.firstOrNull()?.getValue(FinanceSummary::class.java)
                val summaryId = current?.id ?: financeRef.push().key!!

                var balance = current?.balance ?: 0.0
                var income = current?.income ?: 0.0
                var expense = current?.expense ?: 0.0

                if (transaction.type == "INCOME") {
                    balance += transaction.price ?: 0.0
                    income += transaction.price ?: 0.0
                } else if (transaction.type == "EXPENSE") {
                    balance -= transaction.price ?: 0.0
                    expense += transaction.price ?: 0.0
                }

                val updatedSummary = FinanceSummary(summaryId, balance, income, expense)
                financeRef.child(summaryId).setValue(updatedSummary)
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@new_transaction, "Could not read finance summary", Toast.LENGTH_SHORT).show()
            }
        })
    }

}