package pt.ipmaia.cm2024.appmultiecra

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object Dashboard : Destino(route = "dashboard", icon = R.drawable.baseline_home_24, title = "Dashboard")
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.baseline_add_24, title = "Adicionar")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.baseline_history_24, title = "Histótico")
    object Login : Destino(route = "login_screen", icon = R.drawable.baseline_lock_clock_24, title = "Login")

    companion object {
        val toList = listOf(Dashboard,Ecra01, Ecra02)
    }
}


