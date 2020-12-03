package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SetFirstPass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_first_pass)

        val backButton = findViewById<Button>(R.id.btnBack);

        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        })
    }
}