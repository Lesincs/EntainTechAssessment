package com.lesincs.entaintechassessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lesincs.entaintechassessment.nextraces.ui.RacingRoute
import com.lesincs.entaintechassessment.ui.theme.EntainTechAssessmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EntainTechAssessmentTheme {
                RacingRoute(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
