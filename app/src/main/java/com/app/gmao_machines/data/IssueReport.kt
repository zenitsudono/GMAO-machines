package com.app.gmao_machines.data

import java.util.Date

data class IssueReport(
    val userId: String = "",
    val userEmail: String = "",
    val issueType: String = "",
    val severity: String = "",
    val description: String = "",
    val contactEmail: String = "",
    val attachmentCount: Int = 0,
    val status: String = "New",
    val createdAt: Date = Date(),
    val updatedAt: Date? = null
)