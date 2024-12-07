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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import pt.ipmaia.cm2024.appmultiecra.ui.screens.LoginScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.TextStyle
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EmotionData(val Emocao: String, val Quantidade: Int)

@Composable
fun AppNavigation(navController: NavHostController) {
    val registros = remember { mutableStateListOf<String>() }

    NavHost(navController, startDestination = Destino.Login.route) {
        composable(Destino.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Destino.Dashboard.route) {
            Dashboard(navController = navController)
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
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
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
fun Dashboard(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val registros = remember { mutableStateListOf<String>() }
    val daysStatus = remember { mutableStateMapOf<String, Boolean>() } // Usar o dia completo como chave (DD/MM)
    val emocaoCount = remember { mutableStateMapOf<String, Int>() } // Contador de emoções

    LaunchedEffect(userId) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val registroRef = database.getReference("registros/$userId")

            Log.d("FirebaseSetup", "Listener configurado para userId: $userId")

            registroRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    registros.clear()
                    daysStatus.clear()
                    emocaoCount.clear()

                    for (child in snapshot.children) {
                        val registro = child.child("date").value?.toString()
                        val emotion = child.child("emotion").value?.toString()
                        Log.d("FirebaseData", "Emotion: $emotion")

                        if (registro != null) {
                            registros.add(registro)

                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val date = LocalDate.parse(registro, formatter)
                            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM")) // Formato para data (DD/MM)

                            daysStatus[formattedDate] = true
                        }
                        if (emotion != null) {
                            emocaoCount[emotion] = (emocaoCount[emotion] ?: 0) + 1
                        }
                    }
                    Log.d("FirebaseData", "Emotion Count: $emocaoCount") // Verifique o resultado final
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Erro ao carregar registros", error.toException())
                }
            })
        }
    }

    // Gerar todos os dias do ano (2024 como exemplo)
    val daysOfLastYear = List(365) { index ->
        LocalDate.now().minusDays(index.toLong()).format(DateTimeFormatter.ofPattern("dd/MM"))
    }.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👋 Bem-vindo!",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(50.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📅 Frequência de Registros",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Adiciona o LazyRow para rolar todos os dias do ano
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                            reverseLayout = true
                ) {
                    itemsIndexed(daysOfLastYear) { index, day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(
                                    id = if (daysStatus[day] == true) R.drawable.ic_filled_circle else R.drawable.ic_empty_circle
                                ),
                                contentDescription = day,
                                modifier = Modifier.size(40.dp),
                                tint = if (daysStatus[day] == true) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(onClick = { navController.navigate(Destino.Ecra01.route) }) {
                    Text("Adicionar Registro")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📊 Distribuição de Emoções",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient() // Garante carregamento interno
                    loadUrl("file:///android_asset/chart.html")
                }
            },
            update = { webView ->
                val dataArray = mutableListOf<EmotionData>()
                emocaoCount.forEach { (emotion, count) ->
                    dataArray.add(EmotionData(emotion, count))
                }

                val jsonData = Gson().toJson(dataArray)
                webView.evaluateJavascript("google.charts.setOnLoadCallback(() => drawChart($jsonData))") { result ->
                    Log.d("WebView", "Gráfico atualizado com: $result")
                }
            }
        )
    }
}


@Composable
fun Ecra01( registros: MutableList<String>, navController: NavController,modifier: Modifier = Modifier) {
    var selectedEmoji by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                        "description" to description,
                        "date" to LocalDate.now().toString()
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

    // Estados para o filtro e o dropdown
    var orderBy by remember { mutableStateOf("Filtros") } // Texto padrão do botão
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(userId, orderBy) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val registroRef = database.getReference("registros/$userId")

            registroRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempList = mutableListOf<Map<String, String>>()

                    for (child in snapshot.children) {
                        val registro = child.getValue<Map<String, String>>()
                        if (registro != null) {
                            tempList.add(registro)
                        }
                    }

                    // Ordenar com base na data
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    tempList.sortWith { a, b ->
                        val dateA = a["date"]?.let { parseDate(it, dateFormat) }
                        val dateB = b["date"]?.let { parseDate(it, dateFormat) }

                        when (orderBy) {
                            "Mais Recentes" -> (dateB ?: Date()).compareTo(dateA ?: Date())
                            "Mais Antigos" -> (dateA ?: Date()).compareTo(dateB ?: Date())
                            else -> 0
                        }
                    }

                    registros.clear()
                    registros.addAll(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Erro ao carregar registros", error.toException())
                }
            })
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Dropdown Menu para filtro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.TopStart // Posiciona no topo esquerdo
        ) {
            Button(onClick = { dropdownExpanded = !dropdownExpanded }) {
                Text(orderBy)
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier
                    .wrapContentWidth()
                    .offset(y = 10.dp) // Move o menu para logo abaixo do botão
            ) {
                DropdownMenuItem(
                    text = { Text("Mais Recentes") },
                    onClick = {
                        orderBy = "Mais Recentes"
                        dropdownExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Mais Antigos") },
                    onClick = {
                        orderBy = "Mais Antigos"
                        dropdownExpanded = false
                    }
                )
            }
        }

        // Lista de registros
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(registros) { registro ->
                val emoji = registro["emoji"] ?: ""
                val title = registro["title"] ?: ""
                val date = registro["date"] ?: "Data não disponível"

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
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        val registroCompleto = "$emoji - Título: ${registro["title"]} - Emoção: ${registro["emotion"]} - Descrição: ${registro["description"]} - Data: $date"
                        navController.navigate("detalhes_screen/$registroCompleto")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }
    }
}

// Função auxiliar para converter string em Date
fun parseDate(dateString: String, dateFormat: SimpleDateFormat): Date? {
    return try {
        dateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun EcraDetalhes(registro: String, modifier: Modifier = Modifier) {
    val details = registro.split(" - ")

    // Verifica se há pelo menos 5 elementos, já que estamos incluindo a data
    if (details.size >= 5) {
        val emoji = details[0]
        val title = details[1].substringAfter("Título: ").trim()
        val emotion = details[2].substringAfter("Emoção: ").trim()
        val description = details[3].substringAfter("Descrição: ").trim()
        val date = details[4].substringAfter("Data: ").trim()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Detalhes do Registro",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Emoji
            Text(
                text = "Emoji: $emoji",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "Título: $title",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Emotion
            Text(
                text = "Emoção: $emotion",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Descrição: $description",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = "Data: $date",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
            )
        }
    } else {
        Text(
            text = "Registro inválido.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

