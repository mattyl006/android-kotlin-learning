package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    val domainPasswordHashed = "d8vQEBpCRmydByUT+R7Zjg==";
    var myPassword = domainPasswordHashed;
    var isValid = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val password = findViewById<EditText>(R.id.txtPassword);
        val button = findViewById<Button>(R.id.btnSubmit);

        myPassword = loadPassword();
        var access = true;
        
        val stringToHash = "dbsmHashTest";
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHash = String(messageDigest.digest());
        println("Test hasha: $stringHash");

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

    fun validate(password: String) : Boolean {
        val aes = HomePageActivity.ChCrypto;
        if(password == aes.aesDecrypt(myPassword, "n2r5u8x/A?D(G+KbPdSgVkYp3s6v9y&B")) {
            return true;
        }
        return false;
    }

    fun loadPassword() : String {
        val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
        val newPassword = sharedPreference.getString("password", domainPasswordHashed);
        return newPassword.toString();
    }
}