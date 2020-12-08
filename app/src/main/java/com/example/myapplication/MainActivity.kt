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
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    var isValid = false;
    var password = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val typedPassword = findViewById<EditText>(R.id.txtPassword);
        val button = findViewById<Button>(R.id.btnSubmit);

        try {
            password = loadPassword();
        } catch (e: IOException) {
            setFirstPass();
        } catch (e: NullPointerException) {
            setFirstPass();
        } catch (e: Exception) {
            setFirstPass();
        } catch (e: FileNotFoundException) {
            setFirstPass();
        } catch (e: NumberFormatException) {
            setFirstPass();
        }; if(password == "accident") {
            setFirstPass();
        }

        // key-stretching test
        println("TEST_1: " + keyStretching("pass123321123321", "10001000"));
        println("TEST_2: " + keyStretching("pass123321123321", "10001000"));
//        println("TEST_3: " + keyStretchingFailed("pass123321123321", "10001000", 10000, 16));
//        println("TEST_4: " + keyStretchingFailed("pass123321123321", "10001000", 10000, 16));
        // end testing

        var access = true;

        button.setOnClickListener() {
            if(access) {
                val inputPassword = typedPassword.text.toString();
                if (inputPassword.isEmpty()) {
                    Toast.makeText(this, "You have to enter password.", Toast.LENGTH_SHORT).show();
                } else {
                    isValid = validate(inputPassword);
                    if (!isValid) {
                        Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                        access = false;
                    } else {
                        Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                        val intent = Intent(this, HomePageActivity::class.java);
                        intent.putExtra("correctPassword", inputPassword);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(
                        this,
                        "You must wait some time to try press password again.",
                        Toast.LENGTH_SHORT
                ).show();
                Handler().postDelayed({
                    access = true;
                }, 10000)
            }
        }
    }

    fun validate(typedPassword: String) : Boolean {
        if(hashString(loadSalt() + typedPassword) == this.password) {
            return true;
        }
        return false;
    }

    fun loadPassword() : String {
        val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
        val newPassword = sharedPreference.getString("password", "accident");
        return newPassword.toString();
    }

    fun loadSalt() : String {
        val sharedPreference = getSharedPreferences("saltStorage", Context.MODE_PRIVATE);
        val salt = sharedPreference.getString("salt", "accident");
        return salt.toString();
    }

    fun setFirstPass() {
        val intent = Intent(this, SetFirstPass::class.java);
        startActivity(intent);
    }

    fun hashString(stringToHash: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHashed = String(messageDigest.digest());
        return stringHashed;
    }

    fun keyStretching(password: String,  salt: String): String {
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 10000, 24*8);
        val key =  skf.generateSecret(spec);
        return key.encoded.toHex();
    }

    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }
}