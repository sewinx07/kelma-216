package com.example.kelma216

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kelma216.ui.navigation.KelmaApp
import com.example.kelma216.ui.theme.Kelma216Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kelma216Theme {
                KelmaApp()
            }
        }
    }
}
