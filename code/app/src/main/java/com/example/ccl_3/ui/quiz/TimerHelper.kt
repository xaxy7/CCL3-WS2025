package com.example.ccl_3.ui.quiz

fun formatTime(ms: Long): String{
    val totalSec = ms/1000
    val min = totalSec /60
    val sec = totalSec % 60
    return  "%02d:%02d".format(min,sec)
}