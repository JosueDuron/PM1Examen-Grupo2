package com.uth.cloudcontacts.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uth.cloudcontacts.ui.screens.*
import androidx.compose.ui.platform.LocalContext
import com.uth.cloudcontacts.data.local.SessionManager
import com.uth.cloudcontacts.data.network.RetrofitClient

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()

    var userId by remember { mutableIntStateOf(sessionManager.getActiveUserId()) }
    val startDestination = if (userId != 0) "lista_contactos" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { id ->
                    userId = id
                    navController.navigate("lista_contactos") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login")
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("lista_contactos") {
            ListaContactosScreen(
                usuarioId = userId,
                onNavigateToAdd = {
                    navController.navigate("agregar_contacto")
                },
                onNavigateToDetail = { contactoId ->
                    navController.navigate("detalle_contacto/$contactoId")
                },
                onNavigateToProfile = {
                    navController.navigate("perfil")
                },
                onLogout = {
                    sessionManager.clearSession()
                    RetrofitClient.reset()
                    userId = 0
                    navController.navigate("login") {
                        popUpTo("lista_contactos") { inclusive = true }
                    }
                }
            )
        }

        composable("agregar_contacto") {
            AgregarContactoScreen(
                usuarioId = userId
            )
        }

        composable("perfil") {
            UserProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onCuentaEliminada = {
                    userId = 0
                    navController.navigate("login") {
                        popUpTo("lista_contactos") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "detalle_contacto/{contactoId}",
            arguments = listOf(navArgument("contactoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val contactoId = backStackEntry.arguments?.getInt("contactoId") ?: 0
            DetalleContactoScreen(
                contactoId = contactoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
