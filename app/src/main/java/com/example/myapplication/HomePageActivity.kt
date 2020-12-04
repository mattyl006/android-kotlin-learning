package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class HomePageActivity : AppCompatActivity() {

    var keyFragment = "";

    object ChCrypto{
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic fun aesEncrypt(v:String, secretKey:String) = AES256.encrypt(v, secretKey)
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic fun aesDecrypt(v:String, secretKey:String) = AES256.decrypt(v, secretKey)
    }

    private object AES256{
        @RequiresApi(Build.VERSION_CODES.O)
        private val encorder = Base64.getEncoder()
        @RequiresApi(Build.VERSION_CODES.O)
        private val decorder = Base64.getDecoder()
        private fun cipher(opmode:Int, secretKey:String):Cipher{
            if(secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars");
            val c = Cipher.getInstance("AES/OFB32/PKCS5Padding");
            val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES");
            val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8));
            // Secure random, aby iv był zawsze losowy, zupełnie losowy nie związany z kluczem
            // IV trzeba bedzie gdzies przechowac i zabezpieczyc, bo przy decrypt bedzie  trzeba uzyc go jeszcze raz
            c.init(opmode, sk, iv);
            return c
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun encrypt(str:String, secretKey:String):String{
            val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8));
            return String(encorder.encode(encrypted));
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun decrypt(str:String, secretKey:String):String{
            val byteStr = decorder.decode(str.toByteArray(Charsets.UTF_8));
            return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr));
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        keyFragment = intent.getStringExtra("correctPassword").toString();

        val notes = findViewById<EditText>(R.id.txtNotes);
        val notesStorage = "notesStorage.txt";

        notes.imeOptions = EditorInfo.IME_ACTION_DONE;
        notes.setRawInputType(InputType.TYPE_CLASS_TEXT);

        val saveNotesButton = findViewById<Button>(R.id.btnSaveMessage);
        val loadNotesButton = findViewById<Button>(R.id.btnLoadMessage);

        val backButton = findViewById<Button>(R.id.btnBack);

        val changePassword = findViewById<EditText>(R.id.txtPassword);
        val savePasswordButton = findViewById<Button>(R.id.btnSavePassword);

        loadData(notes, notesStorage);

        saveNotesButton.setOnClickListener(View.OnClickListener {
            saveData(notes, notesStorage);
        })

        loadNotesButton.setOnClickListener(View.OnClickListener {
            loadData(notes, notesStorage);
        })

        savePasswordButton.setOnClickListener(View.OnClickListener {
            if(changePassword.text.toString().length < 16) {
                Toast.makeText(this, "Typed password is too short, password minimal length is 16", Toast.LENGTH_LONG).show();
            } else {
                changePassword(changePassword.text.toString(), notes, notesStorage);
            }
        })

        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        })
    }

    fun saveData(notes: EditText, fileName : String) {
        val aes = ChCrypto;
        val data:String = aes.aesEncrypt(notes.text.toString(), (keyFragment+keyFragment).substring(0, 32));
        // key stretching to improve
        val fileOutputStream:FileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(data.toByteArray());
            Toast.makeText(this, "Save note success.", Toast.LENGTH_LONG).show();
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
            println("ERROR: Something went wrong with notes saving.");
        } catch (e: NumberFormatException) {
            e.printStackTrace();
            println("ERROR: Something went wrong with notes saving.");
        } catch (e: IOException) {
            e.printStackTrace();
            println("ERROR: Something went wrong with notes saving.");
        } catch (e: Exception) {
            e.printStackTrace();
            println("ERROR: Something went wrong with notes saving.");
        }
    }

    fun loadData(notes: EditText, fileName: String) {
        try {
            val aes = ChCrypto;
            if (fileName.trim() != "") {
                var fileInputStream: FileInputStream? = null;
                fileInputStream = openFileInput(fileName);
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream);
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader);
                val stringBuilder: StringBuilder = StringBuilder();
                var text: String? = null;
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text);
                }
                notes.setText(aes.aesDecrypt(stringBuilder.toString(), (keyFragment+keyFragment).substring(0, 32))).toString();
            } else {
                Toast.makeText(this, "file name cannot be blank", Toast.LENGTH_LONG).show();
            }
        } catch (e : IOException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(this, "Failed load data, file not exist. Try save some data first.", Toast.LENGTH_LONG).show();
        } catch (e: NullPointerException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(this, "Failed load data, file not exist. Try save some data first.", Toast.LENGTH_LONG).show();
        } catch (e: Exception) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(this, "Failed load data, file not exist. Try save some data first.", Toast.LENGTH_LONG).show();
        } catch (e: FileNotFoundException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(this, "Failed load data, file not exist. Try save some data first.", Toast.LENGTH_LONG).show();
        } catch (e: NumberFormatException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(this, "Failed load data, file not exist. Try save some data first.", Toast.LENGTH_LONG).show();
        }
    }

    fun hashString(stringToHash : String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHashed = String(messageDigest.digest());
        return stringHashed;
    }

    fun changePassword(typedPassword : String, notes : EditText, notesStorage : String) {
        try {
            val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
            val editor = sharedPreference.edit();
            editor.putString("password", hashString(typedPassword));
            editor.apply();
            loadData(notes, notesStorage);
            keyFragment = typedPassword;
            saveData(notes, notesStorage);
            Toast.makeText(this, "Typed password successfully changed", Toast.LENGTH_LONG).show();
        } catch (e : IOException) {
            println("Something went wrong with password changing");
            Toast.makeText(this, "Something went wrong with password changing", Toast.LENGTH_LONG).show();
        } catch (e: NullPointerException) {
            println("Something went wrong with password changing");
            Toast.makeText(this, "Something went wrong with password changing", Toast.LENGTH_LONG).show();
        } catch (e: Exception) {
            println("Something went wrong with password changing");
            Toast.makeText(this, "Something went wrong with password changing", Toast.LENGTH_LONG).show();
        } catch (e: FileNotFoundException) {
            println("Something went wrong with password changing");
            Toast.makeText(this, "Something went wrong with password changing", Toast.LENGTH_LONG).show();
        } catch (e: NumberFormatException) {
            println("Something went wrong with password changing");
            Toast.makeText(this, "Something went wrong with password changing", Toast.LENGTH_LONG).show();
        }
    }
}

