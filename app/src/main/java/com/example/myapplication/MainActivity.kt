package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myText = findViewById<TextView>(R.id.myText)
        var myButton = findViewById<Button>(R.id.myButton)

        myButton.setOnClickListener() {
            myText.text = "Text Changed"
            Toast.makeText(this, "You've clicked a button", Toast.LENGTH_SHORT).show()
        }
    }
}