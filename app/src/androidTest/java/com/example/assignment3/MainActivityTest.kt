package com.example.assignment3

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testTotalBalanceIsDisplayed() {
        onView(withId(R.id.tv_total_balance))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddButtonClick() {
        onView(withId(R.id.add_button))
            .perform(click())
        // You can assert what happens next after clicking if you have a dialog or new screen
    }

    @Test
    fun testViewAllButtonIsVisible() {
        onView(withId(R.id.view_all_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNoTransactionImageHiddenInitially() {
        onView(withId(R.id.img_no_transaction))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
