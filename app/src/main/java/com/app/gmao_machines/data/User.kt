package com.app.gmao_machines.data

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val id: String = "" // In a real app, this would come from your backend
)