package com.guidohuisman.wearmeditation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionEngine.attach(applicationContext)
        val settings = SessionSettings(applicationContext)
        setContent {
            MeditationApp(settings)
        }
    }
}
