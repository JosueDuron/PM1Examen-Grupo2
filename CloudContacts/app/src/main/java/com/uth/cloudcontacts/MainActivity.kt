package com.uth.cloudcontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uth.cloudcontacts.ui.navigation.AppNavigation
import com.uth.cloudcontacts.ui.theme.CloudContactsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudContactsTheme {
                AppNavigation()
            }
        }
    }
}
