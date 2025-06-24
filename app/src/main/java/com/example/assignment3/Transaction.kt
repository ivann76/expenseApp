package com.example.assignment3
import java.io.Serializable


data class Transaction(
    var id: String? = null,
    var type: String? = null,
    var category: String? = null,
    var detail: String? = null,
    var price: Double? = null,
    var date: String? = null
) : Serializable

