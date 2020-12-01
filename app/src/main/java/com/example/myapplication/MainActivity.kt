package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

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

                    val intent = Intent(this, HomePageActivity::class.java);
                    startActivity(intent);
                }
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