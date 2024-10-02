package com.dastan.profscore

sealed class Screens(val route:String) {
    object SingleProfessor: Screens("SingleProfessor/{profId}"){
        fun createRoute(profId:String):String="SingleProfessor/${profId}"
    }
    object Professor: Screens("Professor")
    object AddProfessor: Screens("AddProfessor")
    object AddComment: Screens("AddComment/{profId}"){
        fun createRoute(profId:String):String="AddComment/${profId}"
    }
    object AllComments: Screens("AllComments/{profId}"){
        fun createRoute(profId:String):String="AllComments/${profId}"
    }
    object AllSimilarProfessor: Screens("AllSimilarProfessor")
    object Syllabuses: Screens("Syllabuses")
}