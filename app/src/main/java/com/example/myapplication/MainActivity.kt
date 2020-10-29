package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var password = findViewById<EditText>(R.id.txtPassword);
        var button = findViewById<Button>(R.id.btnSubmit);

        button.setOnClickListener() {
            Toast.makeText(this, password.text, Toast.LENGTH_SHORT).show()
        }
    }
}