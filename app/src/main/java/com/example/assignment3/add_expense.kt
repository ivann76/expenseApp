package com.example.assignment3

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class add_expense : AppCompatActivity() {
    private lateinit var closeButton:ImageButton
    private lateinit var amount:TextInputEditText
    private lateinit var description: EditText
    private lateinit var addExpenseBtn: Button
    private lateinit var llCategory: LinearLayout
    private lateinit var tvCategory:TextView
    private lateinit var ivCategoryIcon: ImageView
    private lateinit var llDate:LinearLayout
    private lateinit var tvDate:TextView
    private lateinit var ivCalendar:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        buttonClicked()
        categoryList()
        // Set current date (you can format it as you prefer)
        val currentDate = Calendar.getInstance()
        updateDateDisplay(currentDate)
    }
    private fun init(){
        closeButton = findViewById(R.id.btn_close)
        amount = findViewById(R.id.et_amount)
        description = findViewById(R.id.et_description)
        addExpenseBtn = findViewById(R.id.btn_add_expense)
        llCategory = findViewById(R.id.ll_category)
        tvCategory = findViewById(R.id.tv_category)
        ivCategoryIcon = findViewById(R.id.iv_category_icon)
        llDate = findViewById(R.id.ll_date)
        tvDate = findViewById(R.id.tv_date)
        ivCalendar = findViewById(R.id.iv_calendar)
    }

    private fun buttonClicked(){
        closeButton.setOnClickListener{finish()}
        addExpenseBtn.setOnClickListener{addExpense()}
        llDate.setOnClickListener { showDatePicker() }
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

        // Alternative format with ordinal indicator (1st, 2nd, 3rd, etc.)
        // tvDate.text = getDateWithOrdinal(calendar)
    }

//    // Optional: For ordinal dates (1st, 2nd, 3rd, etc.)
//    private fun getDateWithOrdinal(calendar: Calendar): String {
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val suffix = when (day % 10) {
//            1 -> if (day != 11) "st" else "th"
//            2 -> if (day != 12) "nd" else "th"
//            3 -> if (day != 13) "rd" else "th"
//            else -> "th"
//        }
//
//        val dateFormat = SimpleDateFormat("MMM d'$suffix, 'yyyy", Locale.getDefault())
//        return dateFormat.format(calendar.time)
//    }

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

    private fun addExpense() {
        val db = FirebaseDatabase.getInstance().getReference("Transaction")

        val price = amount.text.toString().trim()
        val categoryName = tvCategory.text.toString().trim()
        val detail = description.text.toString().trim()
        val date = tvDate.text.toString().trim()

        Log.d("dataCheck", "${price},${categoryName},${detail},${date}")

        if (price.isEmpty() || categoryName.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionId = db.push().key ?: "default_id"
        val transaction = Transaction(transactionId, categoryName, detail, price, date)

        db.child(transactionId).setValue(transaction)
            .addOnCompleteListener {
                Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 100)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save Transaction: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}