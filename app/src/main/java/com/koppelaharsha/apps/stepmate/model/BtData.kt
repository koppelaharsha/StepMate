package com.koppelaharsha.apps.stepmate.model

data class BtData(
    val stepCount: Int = 0,
    val dataPoints: List<Int> = emptyList(),
    val currActivity: String = "IDLE"
)
