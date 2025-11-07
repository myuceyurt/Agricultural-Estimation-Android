package com.agrowise.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.agrowise.app.data.model.Analysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnalyzesViewModel : ViewModel() {

    private val _analyzes = MutableStateFlow<List<Analysis>>(emptyList())
    val analyzes: StateFlow<List<Analysis>> = _analyzes

    init {
        loadAnalyzes()
    }

    private fun loadAnalyzes() {
        _analyzes.value = listOf(
            Analysis(1, "Analysis 1", "8.3 ha", "Complete", 0.73f, 0xFF4CAF50),
            Analysis(2, "Analysis 2", "3.7 ha", "Pending", 0.10f, 0xFF8D6E63),
            Analysis(3, "Analysis 3", "5.2 ha", "Complete", 0.85f, 0xFF66BB6A),
            Analysis(4, "Analysis 4", "2.1 ha", "In Progress", 0.45f, 0xFFFFB74D)
        )
    }
}