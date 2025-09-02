package com.example.temucafe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Langsung redirect ke LandingActivity (Java)
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish() // Tutup MainActivity supaya tidak bisa kembali dengan tombol back
    }
}
