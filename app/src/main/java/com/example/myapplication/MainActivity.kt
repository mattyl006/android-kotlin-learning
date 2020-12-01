package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var myPassword = "dbsm";
    var isValid = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val password = findViewById<EditText>(R.id.txtPassword);
        val button = findViewById<Button>(R.id.btnSubmit);

        myPassword = loadPassword();
        var access = true;

        button.setOnClickListener() {
            if(access) {
                val inputPassword = password.text.toString();
                if (inputPassword.isEmpty()) {
                    Toast.makeText(this, "You have to enter password.", Toast.LENGTH_SHORT).show();
                } else {
                    isValid = validate(inputPassword);
                    if (!isValid) {
                        Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                        access = false;
                    } else {
                        Toast.makeText(this, "Login succesful.", Toast.LENGTH_SHORT).show();

                        val intent = Intent(this, HomePageActivity::class.java);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(this, "You must wait some time to try press password again.", Toast.LENGTH_SHORT).show();
                Handler().postDelayed({
                    access = true;
                }, 10000)
            }
        }
    }

    fun validate(password : String) : Boolean {
        val aes = HomePageActivity.ChCrypto;
        if(password == aes.aesDecrypt(myPassword, "n2r5u8x/A?D(G+KbPdSgVkYp3s6v9y&B")) {
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