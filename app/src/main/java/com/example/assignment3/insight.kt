package com.example.assignment3

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*

class insight : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var database: DatabaseReference
    private lateinit var navHome: LinearLayout

    // Custom color palette for better aesthetics
    private val customColors = intArrayOf(
        Color.parseColor("#FF6B6B"), // Red
        Color.parseColor("#4ECDC4"), // Teal
        Color.parseColor("#45B7D1"), // Blue
        Color.parseColor("#96CEB4"), // Green
        Color.parseColor("#FFEAA7"), // Yellow
        Color.parseColor("#DDA0DD"), // Plum
        Color.parseColor("#98D8C8"), // Mint
        Color.parseColor("#F7DC6F"), // Light Yellow
        Color.parseColor("#BB8FCE"), // Light Purple
        Color.parseColor("#85C1E9")  // Light Blue
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight)

        pieChart = findViewById(R.id.pieChart)
        database = FirebaseDatabase.getInstance().getReference("transaction")
        navHome = findViewById(R.id.nav_home)
        navHome.setOnClickListener{finish()}

        setupPieChart()
        loadSpendingBreakdown()
    }


    private fun setupPieChart() {
        pieChart.apply {
            // Basic setup
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(20f, 10f, 20f, 10f)

            // Disable rotation and touch
            setDragDecelerationFrictionCoef(0.95f)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)

            // Hole styling
            holeRadius = 35f
            transparentCircleRadius = 40f
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(50)

            // Center text
            setDrawCenterText(true)
            centerText = "Spending\nBreakdown"
            setCenterTextSize(16f)
            setCenterTextColor(Color.DKGRAY)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)

            // Entry labels
            setEntryLabelColor(Color.DKGRAY)
            setEntryLabelTextSize(11f)
            setEntryLabelTypeface(Typeface.DEFAULT_BOLD)

            // Legend
            legend.apply {
                isEnabled = true
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textSize = 12f
                textColor = Color.DKGRAY
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                formSize = 12f
                xEntrySpace = 15f
                yEntrySpace = 5f
            }
        }
    }

    private fun loadSpendingBreakdown() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryTotals = mutableMapOf<String, Double>()
                var totalAmount = 0.0

                // Calculate totals
                for (data in snapshot.children) {
                    val transaction = data.getValue(Transaction::class.java)
                    if (transaction?.type == "EXPENSE") {
                        val category = transaction.category ?: "Unknown"
                        val price = transaction.price ?: 0.0
                        categoryTotals[category] = categoryTotals.getOrDefault(category, 0.0) + price
                        totalAmount += price
                    }
                }

                if (categoryTotals.isEmpty()) {
                    Toast.makeText(this@insight, "No expense data found", Toast.LENGTH_SHORT).show()
                    return
                }

                // Create pie entries
                val entries = categoryTotals.map {
                    PieEntry(it.value.toFloat(), it.key)
                }.sortedByDescending { it.value }

                // Create dataset with enhanced styling
                val dataSet = PieDataSet(entries, "").apply {
                    colors = customColors.toList()

                    // Value styling
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                    valueTypeface = Typeface.DEFAULT_BOLD

                    // Slice styling
                    sliceSpace = 2f
                    selectionShift = 8f

                    // Value positioning
                    setDrawValues(true)
                    yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                    xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

                    // Use custom value formatter to show currency
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return if (value < 5f) "" // Hide values for small slices
                            else String.format("%.1f%%", value)
                        }
                    }
                }

                val pieData = PieData(dataSet)

                pieChart.apply {
                    data = pieData

                    // Update center text with total
                    centerText = "Total Expenses\n$${String.format("%.2f", totalAmount)}"

                    // Animation
                    animateY(1200, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

                    // Refresh
                    invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PieChart", "Database error: ${error.message}")
                Toast.makeText(this@insight, "Failed to load spending data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}