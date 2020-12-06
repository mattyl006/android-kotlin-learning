package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.random.Random


class HomePageActivity : AppCompatActivity() {

    var keyFragment = "";

    object ChCrypto{
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic fun aesEncrypt(v: String, secretKey: String, ivValue: String) = AES256.encrypt(
            v,
            secretKey,
            ivValue
        )
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic fun aesDecrypt(v: String, secretKey: String, ivValue: String) = AES256.decrypt(
            v,
            secretKey,
            ivValue
        )
    }

    private object AES256{
        @RequiresApi(Build.VERSION_CODES.O)
        private val encorder = Base64.getEncoder()
        @RequiresApi(Build.VERSION_CODES.O)
        private val decorder = Base64.getDecoder()
        private fun cipher(opmode: Int, secretKey: String, ivValue: String):Cipher{
            if(secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars");
            val c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES");
            val iv = IvParameterSpec(ivValue.substring(0, 16).toByteArray(Charsets.UTF_8));
            c.init(opmode, sk, iv);
            return c
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun encrypt(str: String, secretKey: String, ivValue: String):String{
            val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey, ivValue).doFinal(
                str.toByteArray(
                    Charsets.UTF_8
                )
            );
            return String(encorder.encode(encrypted));
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun decrypt(str: String, secretKey: String, ivValue: String):String{
            val byteStr = decorder.decode(str.toByteArray(Charsets.UTF_8));
            return String(cipher(Cipher.DECRYPT_MODE, secretKey, ivValue).doFinal(byteStr));
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
            if (changePassword.text.toString().length < 16) {
                Toast.makeText(
                    this,
                    "Typed password is too short, password minimal length is 16",
                    Toast.LENGTH_LONG
                ).show();
            } else {
                changePassword(changePassword.text.toString(), notes, notesStorage);
            }
        })

        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        })
    }

    fun saveData(notes: EditText, fileName: String) {
        val aes = ChCrypto;
        val ivValue = generateIvValue();
        saveIvValue(encryptECB(ivValue, keyStretching(keyFragment) + keyFragment));
        val data:String = aes.aesEncrypt(
            notes.text.toString(),
            (keyStretching(keyFragment) + keyFragment).substring(0, 32),
            ivValue
        );
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
                notes.setText(
                    aes.aesDecrypt(
                        stringBuilder.toString(),
                        (keyStretching(keyFragment) + keyFragment).substring(0, 32),
                        decryptECB(loadIvValue(), keyStretching(keyFragment) + keyFragment)
                    )
                ).toString();
            } else {
                Toast.makeText(this, "file name cannot be blank", Toast.LENGTH_LONG).show();
            }
        } catch (e: IOException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(
                this,
                "Failed load data, file not exist. Try save some data first.",
                Toast.LENGTH_LONG
            ).show();
        } catch (e: NullPointerException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(
                this,
                "Failed load data, file not exist. Try save some data first.",
                Toast.LENGTH_LONG
            ).show();
        } catch (e: Exception) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(
                this,
                "Failed load data, file not exist. Try save some data first.",
                Toast.LENGTH_LONG
            ).show();
        } catch (e: FileNotFoundException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(
                this,
                "Failed load data, file not exist. Try save some data first.",
                Toast.LENGTH_LONG
            ).show();
        } catch (e: NumberFormatException) {
            println("Failed load data, file not exist.");
            println("Try save some data first.");
            Toast.makeText(
                this,
                "Failed load data, file not exist. Try save some data first.",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    fun hashString(stringToHash: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.toByteArray());
        val stringHashed = String(messageDigest.digest());
        return stringHashed;
    }

    fun changePassword(typedPassword: String, notes: EditText, notesStorage: String) {
        try {
            var sharedPreference = getSharedPreferences("passwordStorage", Context.MODE_PRIVATE);
            var editor = sharedPreference.edit();
            val salt = generateSalt();
            editor.putString("password", hashString(salt + typedPassword));
            editor.apply();
            sharedPreference = getSharedPreferences("saltStorage", Context.MODE_PRIVATE);
            editor = sharedPreference.edit();
            editor.putString("salt", salt);
            editor.apply();
            loadData(notes, notesStorage);
            keyFragment = typedPassword;
            saveData(notes, notesStorage);
            Toast.makeText(this, "Typed password successfully changed", Toast.LENGTH_LONG).show();
        } catch (e: IOException) {
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

    private fun generateSalt() : String {
        return Random.nextInt(10000000, 99999999).toString();
    }

    private fun generateIvValue() : String {
        return Random.nextLong(1000000000000000, 9999999999999999).toString();
    }

    private fun loadIvValue() : String {
        val defaultValue = Random.nextLong(1000000000000000, 9999999999999999).toString();
        val sharedPreference = getSharedPreferences("ivStorage", Context.MODE_PRIVATE);
        val salt = sharedPreference.getString("ivValue", defaultValue);
        return salt.toString();
    }

    private fun saveIvValue(ivValue: String) {
        val sharedPreference = getSharedPreferences("ivStorage", Context.MODE_PRIVATE);
        val editor = sharedPreference.edit();
        editor.putString("ivValue", ivValue);
        editor.apply();
    }

    private fun keyStretching(passwordToHash: String): String {
        var generatedPassword: String? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(passwordToHash.toByteArray())
            val bytes = md.digest()
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(((bytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
            }
            generatedPassword = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return generatedPassword.toString();
    }

    fun setKey(myKey: String): SecretKeySpec? {
        var sha: MessageDigest? = null
        var secretKey: SecretKeySpec? = null
        try {
            var key = myKey.toByteArray(charset("UTF-8"));
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = key.copyOf(16);
            secretKey = SecretKeySpec(key, "AES");
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace();
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace();
        }
        return secretKey;
    }

    @SuppressLint("GetInstance")
    private fun encryptECB(strToEncrypt: String, secret: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, setKey(secret))
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
    }

    @SuppressLint("GetInstance")
    private fun decryptECB(strToDecrypt: String?, secret: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, setKey(secret))
        return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
    }
}

