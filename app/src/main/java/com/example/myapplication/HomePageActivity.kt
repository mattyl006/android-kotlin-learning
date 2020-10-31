package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.lang.StringBuilder

class HomePageActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        println("Realizuje się kod home page activity");

        val notes = findViewById<EditText>(R.id.txtNotes);
        val storageFile = "data.txt";
        val saveNotesButton = findViewById<Button>(R.id.btnSaveMessage);
        val loadNotesButton = findViewById<Button>(R.id.btnLoadMessage);

        saveNotesButton.setOnClickListener(View.OnClickListener {
            val data:String = notes.text.toString();
            val fileOutputStream:FileOutputStream;
            try {
                fileOutputStream = openFileOutput(storageFile, Context.MODE_PRIVATE);
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
            println("Note save niby się wykonuje");
            Toast.makeText(this, "note save", Toast.LENGTH_LONG).show();
        })

        loadNotesButton.setOnClickListener(View.OnClickListener {
            if(storageFile.trim() != "") {
                var fileInputStream: FileInputStream? = null;
                fileInputStream = openFileInput(storageFile)
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }
                notes.setText(stringBuilder.toString()).toString();
            } else {
                Toast.makeText(this,"file name cannot be blank",Toast.LENGTH_LONG).show();
            }
        })
    }
}