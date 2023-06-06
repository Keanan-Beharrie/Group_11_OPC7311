package com.example.Part_2_OPC7311

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.Part_2_OPC7311.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var logtxt : EditText
    lateinit var ed3: EditText
    lateinit var btnlogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnlogin.setOnClickListener(View.OnClickListener {
            if (binding.logtxt.text.toString() == "user" && binding.ed3.text.toString() == "1234"){
                Toast.makeText(this, "Double Click the Login Button!", Toast.LENGTH_SHORT).show()

                val buttonClick = findViewById<Button>(R.id.btnlogin)
                buttonClick.setOnClickListener {
                    val intent = Intent(this, OnBoardingScreen::class.java)
                    startActivity(intent)
                }

            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

