package com.app.gmao_machines.data

import java.util.Date

data class Intervention(
    val id: Int = 0,
    val intervenantId: Int = 0,
    val intervenantName: String = "",
    val dateIntervention: Date = Date(),
    val datePrevue: Date = Date(),
    val dateRealisation: Date = Date(),
    val planningId: Int? = null,
    val constatId: Int? = null,
    val status: InterventionStatus = InterventionStatus.PENDING,
    val details: List<InterventionDetail> = emptyList(),
    val description: String? = null
)

data class InterventionDetail(
    val id: Int = 0,
    val interventionId: Int = 0,
    val operationId: Int = 0,
    val operationName: String = "",
    val note: Int = 0
)

enum class InterventionStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
} 