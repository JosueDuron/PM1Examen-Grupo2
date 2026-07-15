package com.uth.cloudcontacts.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.location.Location
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.uth.cloudcontacts.ui.theme.CafeArena
import com.uth.cloudcontacts.ui.theme.CafeIntermedio
import com.uth.cloudcontacts.ui.theme.CafeOscuro
import com.uth.cloudcontacts.ui.viewmodels.DetalleContactoViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContactoScreen(
    contactoId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetalleContactoViewModel = viewModel()
) {
    val contacto = viewModel.contacto
    val scrollState = rememberScrollState()
    var isEditing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(contactoId) {
        viewModel.fetchContacto(contactoId)
    }

    // Launchers para edición
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                viewModel.onImageSelected(inputStream)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = File(context.getExternalFilesDir("Pictures"), "update_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "com.uth.cloudcontacts.fileprovider", photoFile)
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            viewModel.onImageSelected(inputStream)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            capturarUbicacionInterna(fusedLocationClient) { location ->
                viewModel.latitud = location.latitude
                viewModel.longitud = location.longitude
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Visita", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    if (contacto != null) {
                        IconButton(onClick = { 
                            if (isEditing) {
                                viewModel.actualizarContacto()
                                isEditing = false
                            } else {
                                isEditing = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Guardar" else "Editar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CafeOscuro)
            )
        },
        containerColor = CafeArena
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CafeOscuro)
            }
        } else if (contacto == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage ?: "No se encontró la información.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Foto
                val imageToDisplay = if (isEditing) viewModel.imagenBase64 else contacto.imagenBase64
                val imageBytes = remember(imageToDisplay) {
                    if (!imageToDisplay.isNullOrEmpty()) {
                        try {
                            Base64.decode(imageToDisplay, Base64.DEFAULT)
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }

                if (imageBytes != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(200.dp),
                        border = androidx.compose.foundation.BorderStroke(3.dp, CafeOscuro),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        AsyncImage(
                            model = imageBytes,
                            contentDescription = "Foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }, colors = ButtonDefaults.buttonColors(containerColor = CafeIntermedio)) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null)
                            Text(" Cámara")
                        }
                        Button(onClick = { galleryLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = CafeIntermedio)) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Text(" Galería")
                        }
                    }
                }

                if (isEditing) {
                    CustomTextFieldPrivate(value = viewModel.nombre, onValueChange = { viewModel.nombre = it }, label = "Nombre", icon = Icons.Default.Person)
                    CustomTextFieldPrivate(
                        value = viewModel.telefono,
                        onValueChange = { viewModel.onTelefonoChange(it) },
                        label = "Teléfono (####-####)",
                        icon = Icons.Default.Phone
                    )
                    CustomTextFieldPrivate(value = viewModel.direccion, onValueChange = { viewModel.direccion = it }, label = "Dirección", icon = Icons.Default.LocationOn)
                } else {
                    Text(
                        text = contacto.nombre ?: "Sin nombre",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeOscuro
                    )
                    InfoRowPrivate(icon = Icons.Default.Phone, text = contacto.telefono ?: "Sin teléfono")
                    InfoRowPrivate(icon = Icons.Default.LocationOn, text = contacto.direccion ?: "Sin dirección")
                }

                // Coordenadas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Geolocalización", fontWeight = FontWeight.Bold, color = CafeOscuro)
                        Text("Latitud: ${if (isEditing) viewModel.latitud else contacto.latitud}")
                        Text("Longitud: ${if (isEditing) viewModel.longitud else contacto.longitud}")
                        
                        if (isEditing) {
                            Button(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CafeIntermedio),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Recalcular Ubicación")
                            }
                        }
                    }
                }

                // Firma
                val firmaToDisplay = if (isEditing) viewModel.firmaBase64 else contacto.firmaBase64
                val firmaBytes = remember(firmaToDisplay) {
                    if (!firmaToDisplay.isNullOrEmpty()) {
                        try {
                            Base64.decode(firmaToDisplay, Base64.DEFAULT)
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }

                if (isEditing) {
                    Text("Actualizar Firma", fontWeight = FontWeight.Bold, color = CafeOscuro, modifier = Modifier.align(Alignment.Start))
                    SignatureCanvasPrivate(
                        onSignatureCaptured = { viewModel.firmaBase64 = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.White)
                            .border(2.dp, CafeOscuro, RoundedCornerShape(8.dp))
                    )
                } else if (firmaBytes != null) {
                    Text("Firma del Contacto", fontWeight = FontWeight.Bold, color = CafeOscuro, modifier = Modifier.align(Alignment.Start))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(2.dp, CafeOscuro, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = firmaBytes,
                            contentDescription = "Firma",
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    }
                }
                
                if (viewModel.isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = CafeOscuro)
                }
                
                viewModel.errorMessage?.let {
                    Text(it, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                
                if (viewModel.updateSuccess) {
                    Text("¡Actualizado correctamente!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoRowPrivate(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, tint = CafeIntermedio)
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Composable
private fun CustomTextFieldPrivate(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = CafeOscuro) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CafeOscuro,
            unfocusedBorderColor = CafeIntermedio,
            focusedLabelColor = CafeOscuro
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun SignatureCanvasPrivate(onSignatureCaptured: (String) -> Unit, modifier: Modifier = Modifier) {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf<List<Offset>?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .onGloballyPositioned { canvasSize = it.size }
                .clipToBounds()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentStroke = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            val position = change.position
                            if (position.x in 0f..canvasSize.width.toFloat() &&
                                position.y in 0f..canvasSize.height.toFloat()
                            ) {
                                currentStroke = currentStroke?.plus(position)
                            } else {
                                currentStroke?.let { stroke ->
                                    strokes = strokes + listOf(stroke)
                                }
                                currentStroke = null
                            }
                        },
                        onDragEnd = {
                            currentStroke?.let { stroke ->
                                strokes = strokes + listOf(stroke)
                                if (canvasSize.width > 0 && canvasSize.height > 0) {
                                    captureSignatureInternal(strokes, canvasSize.width, canvasSize.height, onSignatureCaptured)
                                }
                            }
                            currentStroke = null
                        },
                        onDragCancel = {
                            currentStroke?.let { stroke ->
                                strokes = strokes + listOf(stroke)
                            }
                            currentStroke = null
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                strokes.forEach { stroke ->
                    if (stroke.size > 1) {
                        for (i in 0 until stroke.size - 1) {
                            drawLine(
                                color = Color.Black,
                                start = stroke[i],
                                end = stroke[i + 1],
                                strokeWidth = 5f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
                currentStroke?.let { stroke ->
                    if (stroke.size > 1) {
                        for (i in 0 until stroke.size - 1) {
                            drawLine(
                                color = Color.Black,
                                start = stroke[i],
                                end = stroke[i + 1],
                                strokeWidth = 5f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
        TextButton(onClick = { 
            strokes = emptyList()
            onSignatureCaptured("") 
        }) {
            Text("Limpiar Firma", color = CafeOscuro)
        }
    }
}

private fun captureSignatureInternal(strokes: List<List<Offset>>, width: Int, height: Int, onCaptured: (String) -> Unit) {
    if (width <= 0 || height <= 0) return
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.WHITE)
    val paint = Paint().apply {
        color = AndroidColor.BLACK
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }
    strokes.forEach { stroke ->
        if (stroke.size > 1) {
            val path = android.graphics.Path()
            path.moveTo(stroke[0].x, stroke[0].y)
            for (i in 1 until stroke.size) {
                path.lineTo(stroke[i].x, stroke[i].y)
            }
            canvas.drawPath(path, paint)
        } else if (stroke.size == 1) {
            canvas.drawPoint(stroke[0].x, stroke[0].y, paint)
        }
    }
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    onCaptured(Base64.encodeToString(byteArray, Base64.NO_WRAP))
}

@SuppressLint("MissingPermission")
private fun capturarUbicacionInterna(client: com.google.android.gms.location.FusedLocationProviderClient, onLocation: (Location) -> Unit) {
    try {
        client.lastLocation.addOnSuccessListener { location ->
            location?.let { onLocation(it) }
        }
    } catch (e: Exception) {}
}
