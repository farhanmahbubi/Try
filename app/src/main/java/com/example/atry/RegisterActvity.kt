package com.example.atry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atry.databinding.ActivityRegisterBinding

class RegisterActvity : ComponentActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registerUrl = "http://192.168.1.12/api-mysql-main/api-register.php"

        binding.buttonRegister.setOnClickListener {
            if (binding.editTextUsername.text.toString().isEmpty() || binding.editTextEmail.text.toString().isEmpty() || binding.editTextPassword.text.toString().isEmpty() || binding.editTextNoHp.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Lengkapi data terlebih dahulu", Toast.LENGTH_LONG).show()
            } else {
                val request: RequestQueue = Volley.newRequestQueue(applicationContext)
                val strRequest = object : StringRequest(
                    Request.Method.POST,
                    registerUrl,
                    { response ->
                        if (response == "Daftar Berhasil") {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        }
                    },
                    { error ->
                        Log.d("ErrorApps", error.toString())
                        Toast.makeText(applicationContext, "An error occurred", Toast.LENGTH_LONG).show()
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["nama"] = binding.editTextUsername.text.toString()
                        params["email"] = binding.editTextEmail.text.toString()
                        params["password"] = binding.editTextPassword.text.toString()
                        params["no_hp"] = binding.editTextNoHp.text.toString()
                        return params
                    }
                }
                request.add(strRequest)
            }
        }
    }
}
