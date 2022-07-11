package com.example.customviewtutorial

import android.graphics.Bitmap

data class WeatherModel(
    val time: String,
    val temp: String,
    val humidity: String,
    val bitmap: Bitmap?,
)
