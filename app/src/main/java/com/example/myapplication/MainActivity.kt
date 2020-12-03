package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    var isValid = false;
    var typedPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val password = findViewById<EditText>(R.id.txtPassword);
        val button = findViewById<Button>(R.id.btnSubmit);

        try {
            typedPassword = loadPassword();
        } catch (e : IOException) {
            setFirstPass();
        } catch (e: NullPointerException) {
            setFirstPass();
        } catch (e: Exception) {
            setFirstPass();
        } catch (e: FileNotFoundException) {
            setFirstPass();
        } catch (e: NumberFormatException) {
            setFirstPass();
        }; if(typedPassword == "accident") {
            setFirstPass();
        }

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
        if(hashString(password) == typedPassword) {
            return true;
        }
        return false;
    }

    fun loadPassword() : String {
        val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
        val newPassword = sharedPreference.getString("password", "accident");
        return newPassword.toString();
    }

    fun setFirstPass() {
        val intent = Intent(this, SetFirstPass::class.java);
        startActivity(intent);
    }

    fun hashString(stringToHash : String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHashed = String(messageDigest.digest());
        return stringHashed;
    }
}