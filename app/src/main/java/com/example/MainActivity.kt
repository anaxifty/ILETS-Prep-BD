package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.ui.Modifier
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.IELTSPrepTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        FirebaseApp.initializeApp(this)
      }
    } catch (e: Exception) {
      Log.d("MainActivity", "FirebaseApp initialization note: ${e.message}")
    }

    enableEdgeToEdge()
    setContent {
      IELTSPrepTheme {
        LumenSurface(modifier = Modifier.fillMaxSize()) {
          AppNavigation()
        }
      }
    }
  }
}


