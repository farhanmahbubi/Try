package com.example.atry.donasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.atry.databinding.ActivityDonasiBinding
import com.example.atry.databinding.ActivityItemBinding
import com.example.atry.payments.PaymentsMidtrans

class DonasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonasiBinding
    private val itemList = generateDonasiList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun generateDonasiList(): List<DonasiItem> {
        val santunanItem = DonasiItem("Santunan", "Deskripsi Santunan")
        val pembangunanItem = DonasiItem("Pembangunan", "Deskripsi Pembangunan")
        return listOf(santunanItem, pembangunanItem)
    }

    private fun setupRecyclerView() {
        val adapter = DonasiAdapter(itemList, onItemClickListener = { item ->
            // Implementasi logika ketika item diklik
            val intent = Intent(this, PaymentsMidtrans::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("description", item.description)
            startActivity(intent)
        })
        binding.recycleViewFav.adapter = adapter
    }

    data class DonasiItem(val title: String, val description: String)

    class DonasiViewHolder(private val binding: ActivityItemBinding, private val onItemClickListener: (DonasiItem) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DonasiItem) {
            binding.itemContent.text = item.title
            binding.root.setOnClickListener {
                // Memanggil fungsi onItemClick ketika item diklik
                onItemClickListener.invoke(item)
            }
        }
    }

    class DonasiAdapter(
        private val itemList: List<DonasiItem>,
        private val onItemClickListener: (DonasiItem) -> Unit
    ) : RecyclerView.Adapter<DonasiViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonasiViewHolder {
            val binding =
                ActivityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DonasiViewHolder(binding, onItemClickListener)
        }

        override fun onBindViewHolder(holder: DonasiViewHolder, position: Int) {
            val currentItem = itemList[position]
            holder.bind(currentItem)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }
}
