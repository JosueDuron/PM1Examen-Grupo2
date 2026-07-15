package com.uth.cloudcontacts.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.ContentScale
import android.util.Base64
import coil.compose.AsyncImage
import com.uth.cloudcontacts.data.network.model.ContactoResponse
import com.uth.cloudcontacts.ui.viewmodels.ListaContactosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContactosScreen(
    usuarioId: Int,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: ListaContactosViewModel = viewModel()
) {
    // Cargar contactos al iniciar
    LaunchedEffect(Unit) {
        viewModel.fetchContactos(usuarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Contactos", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CafeOscuro)
            )
        },
        containerColor = CafeArena,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = CafeOscuro,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = CafeOscuro)
            } else if (viewModel.contactos.isEmpty()) {
                Text(
                    "No hay contactos registrados",
                    modifier = Modifier.align(Alignment.Center),
                    color = CafeIntermedio
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.contactos) { contacto ->
                        ContactoCard(contacto, onClick = { onNavigateToDetail(contacto.id) })
                    }
                }
            }

            viewModel.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoCard(contacto: ContactoResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre ?: "Sin nombre",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CafeOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = CafeIntermedio)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(contacto.telefono ?: "Sin teléfono", fontSize = 13.sp, color = Color.DarkGray)
                }
                Text(
                    text = contacto.direccion ?: "Sin dirección",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            val imageBytes = remember(contacto.imagenBase64) {
                if (!contacto.imagenBase64.isNullOrEmpty()) {
                    try {
                        Base64.decode(contacto.imagenBase64, Base64.DEFAULT)
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            if (imageBytes != null) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(60.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CafeIntermedio)
                ) {
                    AsyncImage(
                        model = imageBytes,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
