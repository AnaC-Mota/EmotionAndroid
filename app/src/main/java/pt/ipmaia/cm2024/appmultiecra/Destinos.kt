package pt.ipmaia.cm2024.appmultiecra

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.baseline_add_shopping_cart_24, title = "Ecra01")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.baseline_lock_clock_24, title = "Ecra02")
    object Login : Destino(route = "login_screen", icon = R.drawable.baseline_lock_clock_24, title = "Login")

    companion object {
        val toList = listOf(Ecra01, Ecra02)
    }
}


