package com.example.customviewtutorial

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<CropViewCustom>(R.id.cropView).setBitmapImage(BitmapFactory.decodeFile("/data/data/com.example.customviewtutorial/files/IMG_5385.jpg"))
    }
}