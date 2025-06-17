package com.example.assignment3


data class Transaction(
    var id: String? = null,
    val type: String? = null,
    val category: String? = null,
    val detail: String? = null,
    val price: Double? = 0.0,
    val date: String? = null
)
