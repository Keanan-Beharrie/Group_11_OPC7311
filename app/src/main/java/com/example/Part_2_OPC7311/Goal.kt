package com.example.Part_2_OPC7311

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.Part_2_OPC7311.databinding.ActivityLoginBinding

class Goal : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

lateinit var editNum1: EditText
lateinit var editNum2:EditText
lateinit var textResult: TextView
lateinit var button: Button

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    title = "Hours Worked"

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnlogin.setOnClickListener(View.OnClickListener {

        val buttonClick = findViewById<Button>(R.id.btnlogin)
        buttonClick.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)

        }
    })
}
fun addNumbers(view: View?) {
    editNum1 = findViewById(R.id.editNum1)
    editNum2 = findViewById(R.id.editNum2)
    textResult = findViewById(R.id.textResult)
    val num1 = editNum1.text.toString().toDouble()
    val num2 = editNum2.text.toString().toDouble()
    val result = num1 + num2
    textResult.text = java.lang.Double.toString(result)
}

}


/*
    val buttonClick = findViewById<Button>(R.id.goalC)
    buttonClick.setOnClickListener {
        val intent = Intent(this, HomeFragment::class.java)
        startActivity(intent)
    }
* */

/*
    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
 */