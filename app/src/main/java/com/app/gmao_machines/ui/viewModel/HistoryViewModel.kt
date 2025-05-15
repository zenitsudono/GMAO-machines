package com.app.gmao_machines.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gmao_machines.data.Intervention
import com.app.gmao_machines.data.InterventionStatus
import com.app.gmao_machines.repository.InterventionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val TAG = "HistoryViewModel"
    private val repository = InterventionRepository()

    private val _interventions = MutableStateFlow<List<Intervention>>(emptyList())
    val interventions: StateFlow<List<Intervention>> = _interventions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInterventions()
    }

    private fun loadInterventions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Loading interventions...")
                val interventions = repository.getInterventions()
                
                if (interventions.isEmpty()) {
                    Log.d(TAG, "No interventions found, adding sample data...")
                    repository.addSampleInterventions()
                    // Reload after adding sample data
                    _interventions.value = repository.getInterventions()
                } else {
                    Log.d(TAG, "Found ${interventions.size} interventions")
                    _interventions.value = interventions
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading interventions", e)
                _error.value = "Failed to load interventions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshInterventions() {
        loadInterventions()
    }

    fun updateInterventionStatus(interventionId: Int, status: InterventionStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = repository.updateInterventionStatus(interventionId, status)
                if (success) {
                    // Refresh the list to show updated status
                    loadInterventions()
                } else {
                    _error.value = "Failed to update intervention status"
                }
            } catch (e: Exception) {
                _error.value = "Error updating status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 