package com.app.gmao_machines.repository

import android.util.Log
import com.app.gmao_machines.data.Intervention
import com.app.gmao_machines.data.InterventionDetail
import com.app.gmao_machines.data.InterventionStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import org.json.JSONArray
import org.json.JSONObject

class InterventionRepository {
    private val TAG = "InterventionRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val interventionsCollection = firestore.collection("interventions")
    private val interventionDetailsCollection = firestore.collection("intervention_details")

    suspend fun getInterventions(): List<Intervention> {
        return try {
            Log.d(TAG, "Fetching interventions from Firestore...")
            val snapshot = interventionsCollection
                .orderBy("dateIntervention", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Found ${snapshot.documents.size} interventions")
            
            val interventions = snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data == null) {
                    Log.w(TAG, "Document ${doc.id} has no data")
                    return@mapNotNull null
                }
                
                Log.d(TAG, "Processing intervention document: ${doc.id}")
                Log.d(TAG, "Document data: $data")
                
                Intervention(
                    id = doc.id.toIntOrNull() ?: 0,
                    intervenantId = data["intervenantId"] as? Int ?: 0,
                    intervenantName = data["intervenantName"] as? String ?: "",
                    dateIntervention = (data["dateIntervention"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                    datePrevue = (data["datePrevue"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                    dateRealisation = (data["dateRealisation"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                    planningId = data["planningId"] as? Int,
                    constatId = data["constatId"] as? Int,
                    status = try {
                        InterventionStatus.valueOf(data["status"] as? String ?: InterventionStatus.PENDING.name)
                    } catch (e: IllegalArgumentException) {
                        Log.w(TAG, "Invalid status for document ${doc.id}, defaulting to PENDING")
                        InterventionStatus.PENDING
                    },
                    description = data["description"] as? String
                )
            }
            
            Log.d(TAG, "Successfully processed ${interventions.size} interventions")
            interventions
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching interventions", e)
            emptyList()
        }
    }

    suspend fun getInterventionDetails(interventionId: Int): List<InterventionDetail> {
        return try {
            val snapshot = interventionDetailsCollection
                .whereEqualTo("interventionId", interventionId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                InterventionDetail(
                    id = doc.id.toIntOrNull() ?: 0,
                    interventionId = data["interventionId"] as? Int ?: 0,
                    operationId = data["operationId"] as? Int ?: 0,
                    operationName = data["operationName"] as? String ?: "",
                    note = data["note"] as? Int ?: 0
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addIntervention(intervention: Intervention): Boolean {
        return try {
            val interventionData = hashMapOf(
                "intervenantId" to intervention.intervenantId,
                "intervenantName" to intervention.intervenantName,
                "dateIntervention" to intervention.dateIntervention,
                "datePrevue" to intervention.datePrevue,
                "dateRealisation" to intervention.dateRealisation,
                "planningId" to intervention.planningId,
                "constatId" to intervention.constatId,
                "status" to intervention.status.name
            )

            interventionsCollection
                .document(intervention.id.toString())
                .set(interventionData)
                .await()

            // Add intervention details if any
            intervention.details.forEach { detail ->
                val detailData = hashMapOf(
                    "interventionId" to detail.interventionId,
                    "operationId" to detail.operationId,
                    "operationName" to detail.operationName,
                    "note" to detail.note
                )

                interventionDetailsCollection
                    .document(detail.id.toString())
                    .set(detailData)
                    .await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateInterventionStatus(interventionId: Int, status: InterventionStatus): Boolean {
        return try {
            interventionsCollection
                .document(interventionId.toString())
                .update("status", status.name)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addSampleInterventions() {
        try {
            Log.d(TAG, "Adding sample interventions...")
            
            // Sample intervention 1
            val intervention1 = hashMapOf(
                "intervenantId" to 1,
                "intervenantName" to "John Doe",
                "dateIntervention" to com.google.firebase.Timestamp(Date()),
                "datePrevue" to com.google.firebase.Timestamp(Date()),
                "dateRealisation" to com.google.firebase.Timestamp(Date()),
                "planningId" to 1,
                "constatId" to null,
                "status" to InterventionStatus.COMPLETED.name
            )
            
            // Sample intervention 2
            val intervention2 = hashMapOf(
                "intervenantId" to 2,
                "intervenantName" to "Jane Smith",
                "dateIntervention" to com.google.firebase.Timestamp(Date()),
                "datePrevue" to com.google.firebase.Timestamp(Date()),
                "dateRealisation" to com.google.firebase.Timestamp(Date()),
                "planningId" to 2,
                "constatId" to null,
                "status" to InterventionStatus.IN_PROGRESS.name
            )
            
            // Add interventions to Firestore
            interventionsCollection.document("1").set(intervention1).await()
            interventionsCollection.document("2").set(intervention2).await()
            
            // Add sample details for intervention 1
            val detail1 = hashMapOf(
                "interventionId" to 1,
                "operationId" to 1,
                "operationName" to "Regular Maintenance",
                "note" to 5
            )
            
            val detail2 = hashMapOf(
                "interventionId" to 1,
                "operationId" to 2,
                "operationName" to "System Check",
                "note" to 4
            )
            
            interventionDetailsCollection.document("1").set(detail1).await()
            interventionDetailsCollection.document("2").set(detail2).await()
            
            Log.d(TAG, "Sample interventions added successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding sample interventions", e)
        }
    }

    suspend fun importInterventionsFromJson() {
        val jsonString = """
        [
            {"id": 5001, "description_intervention": "Réparation standard", "date_intervention": "2025-04-10"},
            {"id": 5002, "description_intervention": "Maintenance préventive", "date_intervention": "2025-04-15"},
            {"id": 5003, "description_intervention": "Remplacement de pièces", "date_intervention": "2025-04-18"},
            {"id": 5004, "description_intervention": "Mise à jour logicielle", "date_intervention": "2025-04-20"},
            {"id": 5005, "description_intervention": "Calibration", "date_intervention": "2025-04-22"},
            {"id": 5006, "description_intervention": "Inspection visuelle", "date_intervention": "2025-04-25"},
            {"id": 5007, "description_intervention": "Test fonctionnel", "date_intervention": "2025-04-28"},
            {"id": 5008, "description_intervention": "Installation", "date_intervention": "2025-04-30"},
            {"id": 5009, "description_intervention": "Réparation d'urgence", "date_intervention": "2025-05-01"},
            {"id": 5010, "description_intervention": "Formation utilisateur", "date_intervention": "2025-05-02"}
        ]
        """.trimIndent()

        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getInt("id")
            val description = obj.getString("description_intervention")
            val dateStr = obj.getString("date_intervention")
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
            val date = sdf.parse(dateStr) ?: java.util.Date()
            val interventionData = hashMapOf(
                "intervenantId" to 0,
                "intervenantName" to "",
                "dateIntervention" to com.google.firebase.Timestamp(date),
                "datePrevue" to com.google.firebase.Timestamp(date),
                "dateRealisation" to com.google.firebase.Timestamp(date),
                "planningId" to null,
                "constatId" to null,
                "status" to com.app.gmao_machines.data.InterventionStatus.PENDING.name,
                "description" to description
            )
            interventionsCollection.document(id.toString()).set(interventionData).await()
        }
    }
} 