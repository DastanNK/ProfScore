package com.dastan.profscore.data

data class ProfData(
    val id: String? = null,
    val profName: String? = null,
    val profNameLowercase: List<String>? = null,
    val department: String? = null,
    val departmentLowercase: List<String>? = null,
    val averageDifficulty: Double = 0.0,
    val averageQuality: Double = 0.0,
    val averageWouldTakeAgain: Double = 0.0,
    val quantity: Int = 0,
    val oneDifficulty: Int = 0,
    val twoDifficulty: Int = 0,
    val threeDifficulty: Int = 0,
    val fourDifficulty: Int = 0,
    val fiveDifficulty: Int = 0,
    val courses: List<String>? = null,
    val syllabus: List<SyllabusList>?= null
)

