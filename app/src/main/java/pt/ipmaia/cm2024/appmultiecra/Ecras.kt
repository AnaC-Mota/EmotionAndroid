package pt.ipmaia.cm2024.appmultiecra

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import pt.ipmaia.cm2024.appmultiecra.ui.screens.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val registros = remember { mutableStateListOf<String>() }

    NavHost(navController, startDestination = Destino.Login.route) {  // Inicia com a tela de login
        composable(Destino.Login.route) {
            LoginScreen(navController = navController)  // Tela de login/cadastro
        }
        composable(Destino.Ecra01.route) {
            Ecra01(registros = registros, navController = navController)
        }
        composable(Destino.Ecra02.route) {
            Ecra02(registros = registros, navController = navController)
        }
        composable(
            "detalhes_screen/{registro}",
            arguments = listOf(navArgument("registro") { type = NavType.StringType })
        ) { backStackEntry ->
            val registro = backStackEntry.arguments?.getString("registro") ?: ""
            EcraDetalhes(registro = registro)
        }
    }
}




@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = false,
                onClick = {
                    navController.navigate(item.route){
                        navController.graph.startDestinationRoute?.let{route -> popUpTo(route) {saveState = true}
                        }
                    launchSingleTop = true
                    restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Ecra01( registros: MutableList<String>, navController: NavController,modifier: Modifier = Modifier) {
    var selectedEmoji by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Layout do formulário
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        // Emoji
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = selectedEmoji,
                onValueChange = { newValue -> selectedEmoji = newValue },
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(2.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 50.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions.Default,
                singleLine = true,
                shape = CircleShape,
            )
        }

        // Espaçamento entre o campo de emoji e o próximo campo
        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Título:")
            }
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    value = title,
                    onValueChange = { newValue -> title = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    placeholder = { Text(text = "Digite o título") }
                )
            }
        }

        // Espaçamento entre o campo de título e o próximo campo
        Spacer(modifier = Modifier.height(16.dp))

        // Emoção
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Emoção:")
            }
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    value = emotion,  // Corrigido para usar a variável 'emotion'
                    onValueChange = { newValue -> emotion = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    placeholder = { Text(text = "Escreva sua emoção") }
                )
            }
        }

        // Espaçamento entre o campo de emoção e o próximo campo
        Spacer(modifier = Modifier.height(16.dp))

        // Descrição
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Descrição:")
            }
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    value = description,
                    onValueChange = { newValue -> description = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                    placeholder = { Text(text = "Escreva uma descrição") }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (title.isNotEmpty() && emotion.isNotEmpty()) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val registro = mapOf(
                        "emoji" to selectedEmoji,
                        "title" to title,
                        "emotion" to emotion,
                        "description" to description
                    )
                    val database = FirebaseDatabase.getInstance()
                    val registroRef = database.getReference("registros/$userId").push()
                    registroRef.setValue(registro).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            selectedEmoji = ""
                            title = ""
                            emotion = ""
                            description = ""
                            navController.navigate(Destino.Ecra02.route)
                        } else {
                            Log.e("FirebaseError", "Erro ao salvar o registro", task.exception)
                        }
                    }
                } else {
                    Log.e("AuthError", "Usuário não autenticado")
                }
            }
        }) {
            Text("Salvar")
        }
    }
}


@Composable
fun Ecra02(registros: List<String>, navController: NavController, modifier: Modifier = Modifier) {
    val registros = remember { mutableStateListOf<Map<String, String>>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val registroRef = database.getReference("registros/$userId")

            registroRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    registros.clear() // Limpar registros anteriores
                    for (child in snapshot.children) {
                        val registro = child.getValue<Map<String, String>>()
                        if (registro != null) {
                            registros.add(registro)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Erro ao carregar registros", error.toException())
                }
            })
        }
    }

    // Layout da tela com a lista de registros
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(registros) { registro ->
            val emoji = registro["emoji"] ?: ""
            val title = registro["title"] ?: ""

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = emoji, fontSize = 29.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    val registroCompleto = "$emoji - Título: ${registro["title"]} - Emoção: ${registro["emotion"]} - Descrição: ${registro["description"]}"
                    navController.navigate("detalhes_screen/$registroCompleto")
                }) {
                    Text("Detalhes")
                }
            }
        }
    }
}



@Composable
fun EcraDetalhes(registro: String, modifier: Modifier = Modifier) {
    // Split the registro string to extract the emoji, title, emotion, and description
    val details = registro.split(" - ")

    // Garanta que o registro tenha pelo menos 4 partes
    if (details.size >= 4) {
        val emoji = details[0]
        val title = details[1].substringAfter("Título: ").trim()
        val emotion = details[2].substringAfter("Emoção: ").trim()
        val description = details[3].substringAfter("Descrição: ").trim()

        // Layout for details screen
        Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
            Text(text = "Detalhes do Registro", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            // Emoji
            Text(
                text = "Emoji: $emoji",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp) // Aumentando o tamanho da fonte
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "Título: $title",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp) // Aumentando o tamanho da fonte
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Emotion
            Text(text = "Emoção: $emotion",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp) // Aumentando o tamanho da fonte
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(text = "Descrição: $description",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp) // Aumentando o tamanho da fonte
            )
        }
    } else {
        // Caso o registro não tenha a estrutura esperada
        Text("Registro inválido.", style = MaterialTheme.typography.bodyLarge)
    }
}
