package com.example.assignment3

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewTransactionActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(new_transaction::class.java)

    @Test
    fun testAddTransactionUIFlow() {
        // Select "Income" radio button
        onView(withId(R.id.rb_income)).perform(click())

        // Enter amount
        onView(withId(R.id.et_amount))
            .perform(clearText(), typeText("250.50"), closeSoftKeyboard())

        // Enter description
        onView(withId(R.id.et_description))
            .perform(clearText(), typeText("Salary bonus"), closeSoftKeyboard())

        // Click "Add Expense" button (can rename the button text dynamically based on radio)
        onView(withId(R.id.btn_add_transaction)).perform(click())

    }
}
