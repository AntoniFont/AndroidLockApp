package com.example.applock

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.applock.ui.theme.ApplockTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    public var yaSeHanPedidoLosPermisos: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplockTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
        val rootView = findViewById<View>(android.R.id.content)

        rootView.post {
            // Your method to run after onCreate
            startFunctionality(this)
        }


    }
}

private fun startFunctionality(esto:MainActivity){

    val serviceIntent = Intent(esto, MyService::class.java)
    esto.startForegroundService(serviceIntent)

    GlobalScope.launch(Dispatchers.IO) {
            /*for (i in 1..99999999999999999) {
                val currentForegroundApp = ForegroundAppSearcher.getCurrentForegroundApp(esto)
                println(currentForegroundApp);
                if(currentForegroundApp == "com.google.android.dialer"){
                        // Start a new activity to show the lock screen
                        println("INSIDE COM.GOOGLE.ANDROID.DIALER")
                        // Start a new activity to show the lock screen
                        val lockScreenActivityIntent = Intent(esto, LockScreenActivity::class.java)
                        lockScreenActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        esto.startActivity(lockScreenActivityIntent)
                    }

                }*/
        }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApplockTheme {
        Greeting("Android")
    }
}