package pt.ipmaia.cm2024.appmultiecra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import pt.ipmaia.cm2024.appmultiecra.ui.theme.AppMultiEcraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        setContent {
            AppMultiEcraTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProgramaPrincipal()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramaPrincipal() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    var currentUser by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(Destino.Dashboard.route) {
                popUpTo(0)
            }
        } else {
            navController.navigate(Destino.Login.route) {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        topBar ={
            TopAppBar(title = {
                Text("Emotions")
            }, actions = {
                Row(
                    Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentUser != null) {
                        Button(onClick = {
                            auth.signOut()
                            currentUser = null
                            navController.navigate(Destino.Login.route) {
                                popUpTo(0)
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        }
                    }

                }
            })
        },
        bottomBar = {
            if (currentUser != null) {
                BottomNavigationBar(navController = navController, appItems = Destino.toList)
            }
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNavigation(navController = navController)
            }
        }
    )
}
