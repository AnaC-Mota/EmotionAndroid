package pt.ipmaia.cm2024.appmultiecra

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavType
import androidx.navigation.navArgument
import pt.ipmaia.cm2024.appmultiecra.ui.screens.LoginScreen


import pt.ipmaia.cm2024.appmultiecra.ui.screens.LoginScreen  // Importa a tela de login

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
                val registro = "$selectedEmoji - Título: $title - Emoção: $emotion - Descrição: $description"
                registros.add(registro)
                selectedEmoji = ""
                title = ""
                emotion = ""
                description = ""
                navController.navigate(Destino.Ecra02.route)
            }
        }) {
            Text("Salvar")
        }

    }
}


@Composable
fun Ecra02(registros: List<String>, navController: NavController, modifier: Modifier = Modifier) {
    // Container para a barra de pesquisa e os registros
    Column(modifier = modifier.fillMaxSize()) {
        // Barra de pesquisa
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
            Spacer(Modifier.size(7.dp))
            Text(
                "Pesquisa...",
                color = Color.White,
                fontSize = 18.sp // Aumentando o tamanho da fonte da pesquisa
            )
        }

        // Espaçamento entre a barra de pesquisa e a lista de registros
        Spacer(modifier = Modifier.height(8.dp))

        // Exibição dos registros
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp) // Aumenta o espaçamento entre os registros
        ) {
            items(registros) { registro ->
                // Divide o registro para obter o emoji e o título
                val details = registro.split(" - ")
                val emoji = details[0]
                val title = details.getOrNull(1)?.substringAfter("Título: ")?.trim() ?: ""

                // Row com alinhamento dos itens
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp) // Aumenta o espaçamento interno
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start // Garante que o conteúdo do Row seja alinhado à esquerda
                ) {
                    // Exibe texto (título) à esquerda
                    Column(
                        modifier = Modifier.weight(0.3f)  // Deixa o texto ocupar mais espaço
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 29.sp, // Aumenta o tamanho do emoji
                            modifier = Modifier.align(Alignment.Start) // Alinha o emoji à direita
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Espaço entre texto e emoji
                    }

                    // Exibe emoji à direita
                    Column(
                        modifier = Modifier.weight(1f)  // Deixa o emoji menor
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp, // Aumenta o tamanho do título
                            fontWeight = FontWeight.Bold, // Deixa o título em negrito
                            modifier = Modifier.align(Alignment.Start) // Alinha o título à esquerda

                        )
                    }

                    // Botão "Detalhes" alinhado à direita
                    Button(
                        onClick = {
                            // Navegar para a tela de detalhes passando o registro completo
                            navController.navigate("detalhes_screen/$registro")
                        },
                        modifier = Modifier.align(Alignment.CenterVertically) // Alinha o botão verticalmente
                    ) {
                        Text("Detalhes")
                    }
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
