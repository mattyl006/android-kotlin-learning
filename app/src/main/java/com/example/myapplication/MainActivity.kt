package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var myPassword = "dbsm";
    var isValid = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var password = findViewById<EditText>(R.id.txtPassword);
        var button = findViewById<Button>(R.id.btnSubmit);


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
}