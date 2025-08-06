package io.github.ackeecz.ackeelities.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.ackeecz.ackeelities.core.navigateToAppSettings
import io.github.ackeecz.ackeelities.core.openGooglePlay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
private fun MainScreen() {
    val context = LocalContext.current
    Column {
        Button(onClick = { context.navigateToAppSettings(true) }) {
            Text(text = "Navigate to app settings")
        }
        Button(onClick = { context.openGooglePlay() }) {
            Text(text = "Open Google Play")
        }
    }
}
