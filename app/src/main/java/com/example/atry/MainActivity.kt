package com.example.atry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.atry.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengganti tampilan dengan fragmen Home saat aktivitas pertama kali dibuat
        replaceFragment(DashboardFragment())

        // Mengatur listener untuk perubahan item yang dipilih pada BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                //R.id.layanan -> replaceFragment(Home()) // Pilihan "Home" dipilih, ganti tampilan dengan fragmen Home
                R.id.dashboard -> replaceFragment(DashboardFragment()) // Pilihan "Profile" dipilih, ganti tampilan dengan fragmen Profile
                //R.id.profile -> replaceFragment(Favorite()) // Pilihan "Favorite" dipilih, ganti tampilan dengan fragmen Favorite
                else -> {
                    // Handle pilihan lainnya (jika ada)
                }
            }
            true // Mengembalikan nilai true untuk menunjukkan bahwa perubahan item telah ditangani
        }
    }

    // Fungsi untuk mengganti fragmen dalam layout
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment) // Ganti fragmen dalam FrameLayout dengan yang baru
        fragmentTransaction.commit() // Terapkan perubahan fragmen
    }
}
