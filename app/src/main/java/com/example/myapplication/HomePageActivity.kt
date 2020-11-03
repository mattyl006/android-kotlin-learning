package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.lang.StringBuilder

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val notes = findViewById<EditText>(R.id.txtNotes);
        val notesStorage = "notesStorage.txt";

        notes.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
            val sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
            val editor = sharedPreference.edit();
            editor.putString("password", changePassword.text.toString());
            editor.apply();
        })

        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        })
    }

    fun saveData(notes: EditText, fileName : String) {
        val data:String = notes.text.toString();
        val fileOutputStream:FileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(data.toByteArray());
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: NumberFormatException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace();
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    fun loadData(notes: EditText, fileName: String) {
        try {
            if (fileName.trim() != "") {
                var fileInputStream: FileInputStream? = null;
                fileInputStream = openFileInput(fileName)
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }
                notes.setText(stringBuilder.toString()).toString();
            } else {
                Toast.makeText(this, "file name cannot be blank", Toast.LENGTH_LONG).show();
            }
        } catch (e : IOException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
        } catch (e: NullPointerException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
        } catch (e: Exception) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
        } catch (e: FileNotFoundException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
        } catch (e: NumberFormatException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
        }
    }
}