package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.security.MessageDigest

class SetFirstPass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_first_pass)

        val backButton = findViewById<Button>(R.id.btnBack);
        val setPassword = findViewById<EditText>(R.id.txtPassword);
        val savePasswordButton =  findViewById<Button>(R.id.btnSavePassword);

        savePasswordButton.setOnClickListener(View.OnClickListener {
            if(setPassword.text.toString().length < 16) {
                Toast.makeText(this, "Typed password is too short, password minimal length is 16", Toast.LENGTH_LONG).show();
            } else {
                val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
                val editor = sharedPreference.edit();
                editor.putString("password", hashString(setPassword.text.toString()));
                editor.apply();
                Toast.makeText(this, "Setting password success", Toast.LENGTH_LONG).show();

            }
        })

        backButton.setOnClickListener(View.OnClickListener {
            if(setPassword.text.toString() == "") {
                Toast.makeText(this, "You have to set your first password, before go to main view!", Toast.LENGTH_LONG).show();
                println("You have to set your first password, before go to main view!");

            } else {
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
            }
        })
    }

    fun hashString(stringToHash : String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHashed = String(messageDigest.digest());
        return stringHashed;
    }
}