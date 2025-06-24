package com.example.assignment3

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InsightsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(insight::class.java)

    @Test
    fun testHeaderIsDisplayed() {
        onView(withText("Spending Insights"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCategoryBreakdownTextVisible() {
        onView(withText("Category Breakdown"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPieChartVisible() {
        onView(withId(R.id.pieChart))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBottomNavigationButtonsVisible() {
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()))
        onView(withId(R.id.add_button)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_insights)).check(matches(isDisplayed()))
    }

}
