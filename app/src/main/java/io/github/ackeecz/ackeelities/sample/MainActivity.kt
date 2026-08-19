package io.github.ackeecz.ackeelities.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.ackeecz.ackeelities.compose.CollectLifecycleAware
import io.github.ackeecz.ackeelities.core.navigateToAppSettings
import io.github.ackeecz.ackeelities.core.openGooglePlay
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
private fun MainScreen() {
    val context = LocalContext.current
    val messages = remember { MutableSharedFlow<String>(extraBufferCapacity = 1) }
    messages.CollectLifecycleAware { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    Column {
        Button(onClick = { context.navigateToAppSettings(true) }) {
            Text(text = "Navigate to app settings")
        }
        Button(onClick = { context.openGooglePlay() }) {
            Text(text = "Open Google Play")
        }
        Button(onClick = { messages.tryEmit("Hello from CollectLifecycleAware") }) {
            Text(text = "Emit event")
        }
    }
}
