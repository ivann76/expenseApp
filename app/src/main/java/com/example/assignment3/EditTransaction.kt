package com.example.assignment3

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class EditTransaction : AppCompatActivity() {

    private var isEditMode = false
    private var transactionToEdit: Transaction? = null

    private lateinit var btnAddTransaction: Button
    private lateinit var amount: EditText
    private lateinit var description: EditText
    private lateinit var tvDate: TextView
    private lateinit var tvCategory: TextView
    private lateinit var ivCategoryIcon: ImageView
    private lateinit var transactionType: RadioGroup
    private lateinit var layoutCategorySection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        buttonClickedListener()

        transactionToEdit = intent.getSerializableExtra("transaction") as? Transaction
        if (transactionToEdit != null) {
            isEditMode = true
            populateFieldsForEdit(transactionToEdit!!)
            btnAddTransaction.text = "Update Transaction"
        }
    }

    private fun init() {
        btnAddTransaction = findViewById(R.id.btn_edit_transaction)
        amount = findViewById(R.id.edit_amount)
        description = findViewById(R.id.edit_description)
        tvDate = findViewById(R.id.tv_date_edit)
        tvCategory = findViewById(R.id.tv_category)
        ivCategoryIcon = findViewById(R.id.iv_category_icon)
        transactionType = findViewById(R.id.rg_transaction_type)
        layoutCategorySection = findViewById(R.id.layout_category_section)
    }

    private fun buttonClickedListener() {
        btnAddTransaction.setOnClickListener { saveTransaction() }
    }

    private fun populateFieldsForEdit(transaction: Transaction) {
        amount.setText(transaction.price?.toString() ?: "")
        description.setText(transaction.detail ?: "")
        tvDate.text = transaction.date ?: ""

        tvCategory.text = transaction.category
        ivCategoryIcon.setImageResource(
            when (transaction.category?.lowercase()) {
                "food" -> R.drawable.food_icons
                "transport" -> R.drawable.transportation_icons
                "shopping" -> R.drawable.shopping_icons
                "entertainment" -> R.drawable.entertaiment_icons
                else -> R.drawable.income_icon
            }
        )

        if (transaction.type == "INCOME") {
            transactionType.check(R.id.rb_income)
            layoutCategorySection.visibility = View.GONE
        } else {
            transactionType.check(R.id.rb_expense)
            layoutCategorySection.visibility = View.VISIBLE
        }
    }

    private fun saveTransaction() {
        val db = FirebaseDatabase.getInstance().getReference("transaction")

        val price = amount.text.toString().toDoubleOrNull() ?: 0.0
        val category = tvCategory.text.toString().trim()
        val detail = description.text.toString().trim()
        val date = tvDate.text.toString().trim()

        val selectedId = transactionType.checkedRadioButtonId
        val selectedRadio = findViewById<RadioButton>(selectedId)
        val type = selectedRadio.text.toString().uppercase()

        if (price.isNaN() || detail.isEmpty() || (type == "EXPENSE" && category.isEmpty())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage(if (isEditMode) "Updating..." else "Saving...")
            setCancelable(false)
            show()
        }

        val id = transactionToEdit?.id ?: db.push().key!!
        val categoryFinal = if (type == "INCOME") "Income" else category
        val updatedTransaction = Transaction(id, type, categoryFinal, detail, price, date)

        db.child(id).setValue(updatedTransaction)
            .addOnSuccessListener {
                updateFinanceSummaryAfterEdit(transactionToEdit!!, updatedTransaction)
                progressDialog.dismiss()
                Toast.makeText(this, "Transaction updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFinanceSummaryAfterEdit(oldTransaction: Transaction, newTransaction: Transaction) {
        val financeRef = FirebaseDatabase.getInstance().getReference("finance")

        financeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.children.firstOrNull()?.getValue(FinanceSummary::class.java)
                val summaryId = current?.id ?: return

                var balance = current.balance
                var income = current.income
                var expense = current.expense

                // Reverse old transaction effect
                if (oldTransaction.type == "INCOME") {
                    balance -= oldTransaction.price ?: 0.0
                    income -= oldTransaction.price ?: 0.0
                } else if (oldTransaction.type == "EXPENSE") {
                    balance += oldTransaction.price ?: 0.0
                    expense -= oldTransaction.price ?: 0.0
                }

                // Apply new transaction effect
                if (newTransaction.type == "INCOME") {
                    balance += newTransaction.price ?: 0.0
                    income += newTransaction.price ?: 0.0
                } else if (newTransaction.type == "EXPENSE") {
                    balance -= newTransaction.price ?: 0.0
                    expense += newTransaction.price ?: 0.0
                }

                // Prevent negative values
                if (balance < 0) balance = 0.0
                if (income < 0) income = 0.0
                if (expense < 0) expense = 0.0

                val updated = FinanceSummary(summaryId, balance, income, expense)
                financeRef.child(summaryId).setValue(updated)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditTransaction, "Failed to update finance summary", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
