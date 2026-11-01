package com.example.appmaster_martini_grupo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmaster_martini_grupo1.ui.navigation.AppNavGraph
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMaster_Martini_Grupo1Theme {
                AppNavGraph()
            }
        }
    }
}