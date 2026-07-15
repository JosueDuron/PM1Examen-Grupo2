package com.uth.cloudcontacts.ui.screens

import android.annotation.SuppressLint
import android.Manifest
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
import androidx.activity.result.launch
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.FileProvider
import java.io.File
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.uth.cloudcontacts.ui.theme.CafeArena
import com.uth.cloudcontacts.ui.theme.CafeIntermedio
import com.uth.cloudcontacts.ui.theme.CafeOscuro
import com.uth.cloudcontacts.ui.viewmodels.AgregarContactoViewModel
import java.io.ByteArrayOutputStream

// Paleta de Colores Café

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarContactoScreen(
    usuarioId: Int,
    viewModel: AgregarContactoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Manejo de Cámara con FileProvider para evitar cierres
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                viewModel.onImageSelected(context, inputStream)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = File(context.getExternalFilesDir("Pictures"), "foto_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "com.uth.cloudcontacts.fileprovider", photoFile)
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            viewModel.onImageSelected(context, inputStream)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            capturarUbicacion(context, fusedLocationClient) { location ->
                viewModel.latitud = location.latitude
                viewModel.longitud = location.longitude
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Visita", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CafeOscuro)
            )
        },
        containerColor = CafeArena
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campos de Texto
            CustomTextField(value = viewModel.nombre, onValueChange = { viewModel.nombre = it }, label = "Nombre", icon = Icons.Default.Person)
            CustomTextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = "Teléfono (1234-1234)",
                icon = Icons.Default.Phone
            )
            CustomTextField(value = viewModel.direccion, onValueChange = { viewModel.direccion = it }, label = "Dirección", icon = Icons.Default.LocationOn)

            // Ubicación
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Coordenadas GPS", fontWeight = FontWeight.Bold, color = CafeOscuro)
                    Text("Lat: ${viewModel.latitud}", fontSize = 14.sp)
                    Text("Long: ${viewModel.longitud}", fontSize = 14.sp)
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
                        Text("Capturar Ubicación")
                    }
                }
            }

            // Imagen
            Text("Foto del Contacto", fontWeight = FontWeight.Bold, color = CafeOscuro, modifier = Modifier.align(Alignment.Start))
            if (viewModel.fotoBase64.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(200.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CafeIntermedio)
                ) {
                    val decodedBytes = Base64.decode(viewModel.fotoBase64, Base64.DEFAULT)
                    AsyncImage(
                        model = decodedBytes,
                        contentDescription = "Previsualización",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
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

            // Firma Digital
            Text("Firma Digital", fontWeight = FontWeight.Bold, color = CafeOscuro, modifier = Modifier.align(Alignment.Start))
            SignatureCanvas(
                onSignatureCaptured = { viewModel.firmaBase64 = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
                    .border(2.dp, CafeOscuro, RoundedCornerShape(8.dp))
            )

            // Botón Guardar
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = CafeOscuro)
            } else {
                Button(
                    onClick = { viewModel.guardarContacto(usuarioId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CafeOscuro),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("GUARDAR VISITA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            viewModel.errorMessage?.let { Text(it, color = Color.Red) }
            if (viewModel.isSuccess) {
                Text("¡Visita guardada con éxito!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
fun SignatureCanvas(onSignatureCaptured: (String) -> Unit, modifier: Modifier = Modifier) {
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
                                    captureSignature(strokes, canvasSize.width, canvasSize.height, onSignatureCaptured)
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

private fun captureSignature(strokes: List<List<Offset>>, width: Int, height: Int, onCaptured: (String) -> Unit) {
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
private fun capturarUbicacion(context: Context, client: com.google.android.gms.location.FusedLocationProviderClient, onLocation: (Location) -> Unit) {
    try {
        client.lastLocation.addOnSuccessListener { location ->
            location?.let { onLocation(it) }
        }
    } catch (e: SecurityException) {
        // Manejar falta de permisos
    }
}
