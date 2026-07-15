package com.uth.cloudcontacts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
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
import com.uth.cloudcontacts.ui.theme.CafeArena
import com.uth.cloudcontacts.ui.theme.CafeIntermedio
import com.uth.cloudcontacts.ui.theme.CafeOscuro
import com.uth.cloudcontacts.domain.model.Contacto
import com.uth.cloudcontacts.ui.viewmodels.ListaContactosViewModel
import com.uth.cloudcontacts.ui.viewmodels.Ordenamiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContactosScreen(
    usuarioId: Int,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ListaContactosViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchContactos(usuarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis Contactos", color = Color.White)
                        Text(
                            "${viewModel.contactos.size} contactos",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchContactos(usuarioId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Ordenar", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Nombre (A-Z)") },
                                onClick = {
                                    viewModel.ordenarContactos(Ordenamiento.NOMBRE_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Nombre (Z-A)") },
                                onClick = {
                                    viewModel.ordenarContactos(Ordenamiento.NOMBRE_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Más reciente") },
                                onClick = {
                                    viewModel.ordenarContactos(Ordenamiento.FECHA_RECENTE)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Más antiguo") },
                                onClick = {
                                    viewModel.ordenarContactos(Ordenamiento.FECHA_ANTIGUA)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
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
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.buscarContactos(it, usuarioId)
                    },
                    label = { Text("Buscar contacto...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CafeOscuro) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.buscarContactos("", usuarioId)
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = CafeOscuro)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CafeOscuro,
                        unfocusedBorderColor = CafeIntermedio,
                        focusedLabelColor = CafeOscuro
                    ),
                    singleLine = true
                )

                if (viewModel.isLoading && viewModel.contactos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CafeOscuro)
                    }
                } else if (viewModel.contactos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (searchQuery.isNotEmpty()) "No se encontraron resultados" else "No hay contactos registrados",
                            color = CafeIntermedio
                        )
                    }
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
            }

            viewModel.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoCard(contacto: Contacto, onClick: () -> Unit) {
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
                    text = contacto.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CafeOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = CafeIntermedio)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(contacto.telefono, fontSize = 13.sp, color = Color.DarkGray)
                }
                Text(
                    text = contacto.direccion,
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
