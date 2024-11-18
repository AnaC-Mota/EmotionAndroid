package pt.ipmaia.cm2024.appmultiecra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import pt.ipmaia.cm2024.appmultiecra.ui.theme.AppMultiEcraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMultiEcraTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProgramaPrincipal()
                }
            }
        }
    }
}

@Composable
fun ProgramaPrincipal() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Inicializa o estado com o valor atual do usuário
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    // Detecta alterações no estado de autenticação de forma assíncrona
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            isLoggedIn = firebaseAuth.currentUser != null
        }
    }

    // Realiza a navegação inicial com base no estado de login
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Destino.Login.route) {
                popUpTo(0) // Limpa a pilha de navegação
            }
        } else {
            navController.navigate(Destino.Login.route) {
                popUpTo(0) // Limpa a pilha de navegação
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
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
