package com.example.atry.alokasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.atry.databinding.ActivityAlokasiBinding
import org.json.JSONException

class AlokasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlokasiBinding
    private lateinit var requestQueue: RequestQueue
    private val alokasiList = mutableListOf<AlokasiItem>()

    data class AlokasiItem(
        val nama_alokasi: String,
        val img_alokasi: ByteArray
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlokasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        requestQueue = Volley.newRequestQueue(this)
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        val url = "http://192.168.1.19/api-mysql-main/api-alokasi.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val alokasiObject = response.getJSONObject(i)
                        val namaAlokasi = alokasiObject.getString("nama_alokasi")
                        val imgAlokasi = alokasiObject.getString("img_alokasi").toByteArray()

                        val alokasiItem = AlokasiItem(namaAlokasi, imgAlokasi)
                        alokasiList.add(alokasiItem)
                    }

                    val adapter = AlokasiAdapter(alokasiList) { item ->
                        val intent = Intent(this, DetailAlokasiActiviity::class.java)
                        intent.putExtra("title", item.nama_alokasi)
                        intent.putExtra("image", item.img_alokasi)
                        startActivity(intent)
                    }
                    binding.recycleViewAlokasi.adapter = adapter

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun setupRecyclerView() {
        binding.recycleViewAlokasi.layoutManager = LinearLayoutManager(this)
    }
}
