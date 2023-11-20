package com.example.atry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atry.databinding.ActivityLoginBinding
import java.net.URLEncoder

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegis.setOnClickListener {
            val intent = Intent(this, RegisterActvity::class.java)
            startActivity(intent)
        }

        val url = "http://192.168.1.12/api-mysql-main/api-login.php" //

        binding.buttonLogin.setOnClickListener {
            val request: RequestQueue = Volley.newRequestQueue(applicationContext)

            val stringRequest = StringRequest(
                Request.Method.GET,
                "$url?email=${binding.editTextEmail.text}&password=${binding.editTextPassword.text}",
                { response ->
                    if (response == "welcome") {
                        val intent = Intent(this, MainActivity::class.java)
                        binding.editTextEmail.text.toString()
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Gagal login", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Log.d("errorApp", error.toString())
                }
            )
            request.add(stringRequest)
        }
    }
}