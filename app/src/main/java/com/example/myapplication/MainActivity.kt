package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NullPointerException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    var myPassword = "dbsm";
    var isValid = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var password = findViewById<EditText>(R.id.txtPassword);
        var button = findViewById<Button>(R.id.btnSubmit);

        myPassword = loadPassword();

        button.setOnClickListener() {
            var inputPassword = password.text.toString();

            if (inputPassword.isEmpty()) {
                Toast.makeText(this,"You have to enter password.", Toast.LENGTH_SHORT).show();
            } else {
                isValid = validate(inputPassword);

                if(!isValid) {
                    Toast.makeText(this,"Incorrect password.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"Login succesful.", Toast.LENGTH_SHORT).show();
                    // Add the code to go to new activity

                    val intent = Intent(this, HomePageActivity::class.java);
                    startActivity(intent);
                }
            }
        }
    }

    fun validate(password : String) : Boolean {
        if(password.equals(myPassword)) {
            return true;
        }
        return false;
    }


    fun loadPassword() : String {
        val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
        val newPassword = sharedPreference.getString("password", "dbsm");
        return newPassword.toString();
    }
}