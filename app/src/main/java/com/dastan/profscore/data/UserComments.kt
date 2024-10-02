package com.dastan.profscore.data

data class UserComments(

    val id:String?=null,
    val profId:String?=null,
    val gpa:String?=null,
    val wouldTakeAgain:Boolean?=true,
    val difficulty:Int?=0,
    val quality:Int?=0,
    val tags:List<Tag>?= listOf(),
    val attendance:Boolean?=true, //mandatory or not
    val helpful:Int?=0,
    val notHelpful:Int?=0,
    val date:String?=null,
    val course:String?=null, //what course he/she provided
    val comments: String?=null
)


