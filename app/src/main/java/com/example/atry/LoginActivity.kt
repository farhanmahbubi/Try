package com.example.atry

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atry.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject
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

        val url = "http://192.168.1.19/api-mysql-main/api-login.php"

        binding.buttonLogin.setOnClickListener {
            val request: RequestQueue = Volley.newRequestQueue(applicationContext)

            val stringRequest = StringRequest(
                Request.Method.GET,
                "$url?email=${
                    URLEncoder.encode(
                        binding.editTextEmail.text.toString(),
                        "UTF-8"
                    )
                }&password=${URLEncoder.encode(binding.editTextPassword.text.toString(), "UTF-8")}",
                { response ->
                    try {
                        Log.d("JSONResponse", response)

                        val jsonResponse = JSONObject(response)
                        val status = jsonResponse.getString("status")
                        if (status == "success") {
                            val idDonaturObject = jsonResponse.getJSONObject("id_donatur")

                            // Periksa apakah kunci "nama" ada dalam objek "id_donatur"
                            val namaDonatur = if (idDonaturObject.has("nama")) {
                                idDonaturObject.getString("nama")
                            } else {
                                // Handle jika kunci "nama" tidak ada
                                Log.e("JSONError", "Kunci 'nama' tidak ditemukan dalam objek 'id_donatur'")
                                "NamaDefault"
                            }

                            // Periksa apakah kunci "email" ada dalam objek "id_donatur"
                            val emailDonatur = if (idDonaturObject.has("email")) {
                                idDonaturObject.getString("email")
                            } else {
                                // Handle jika kunci "email" tidak ada
                                Log.e("JSONError", "Kunci 'email' tidak ditemukan dalam objek 'id_donatur'")
                                "EmailDefault"
                            }

                            val idDonatur = idDonaturObject.getString("id_donatur")
                            val noHpDonatur = idDonaturObject.getString("no_hp")

                            saveID(idDonatur, namaDonatur, emailDonatur, noHpDonatur)

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Gagal login", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Error parsing JSON", e)
                    }
                },
                { error ->
                    Log.d("errorApp", error.toString())
                }
            )
            request.add(stringRequest)
        }

    }

    private fun saveID(
        idDonatur: String,
        namaDonatur: String,
        emailDonatur: String,
        noHpDonatur: String
    ) {
        val preferences: SharedPreferences = getSharedPreferences("donatur_prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()

        editor.putString("id_donatur", idDonatur)
        editor.putString("nama", namaDonatur)
        editor.putString("email", emailDonatur)
        editor.putString("no_hp", noHpDonatur)

        editor.apply()
    }
}
