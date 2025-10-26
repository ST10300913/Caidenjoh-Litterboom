package com.example.litterboom.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * System Camera version:
 * - Requests CAMERA (+ WRITE_EXTERNAL_STORAGE for API <= 28)
 * - Pre-creates a MediaStore Uri
 * - Launches the phone's camera app via TakePicture()
 * - On success: returns the final Uri via onCaptured
 * - On cancel/failure: cleans up and calls onClose
 */
@Composable
fun CameraCaptureScreen(
    onCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    // === Runtime permissions ===
    val needsWrite = Build.VERSION.SDK_INT <= 28
    var hasCamera by remember { mutableStateOf(false) }
    var hasWrite by remember { mutableStateOf(!needsWrite) }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        hasCamera = res[Manifest.permission.CAMERA] == true ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (needsWrite) {
            hasWrite = res[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (needsWrite) {
            hasWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        if (!hasCamera || !hasWrite) {
            val perms = mutableListOf(Manifest.permission.CAMERA)
            if (needsWrite) perms += Manifest.permission.WRITE_EXTERNAL_STORAGE
            permLauncher.launch(perms.toTypedArray())
        }
    }

    val permissionsGranted = hasCamera && hasWrite

    // === TakePicture launcher ===
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingUri
        pendingUri = null
        if (success && uri != null) {
            // Mark pending done on Q+ just in case
            if (Build.VERSION.SDK_INT >= 29) {
                runCatching {
                    context.contentResolver.update(
                        uri,
                        ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) },
                        null,
                        null
                    )
                }
            }
            onCaptured(uri)
        } else {
            // User cancelled or capture failed -> clean up
            if (uri != null) {
                runCatching { context.contentResolver.delete(uri, null, null) }
            }
            onClose()
        }
    }

    // Fire camera once permissions are granted
    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            val (created, uri) = createMediaStoreImageUri(context)
            if (!created || uri == null) {
                Toast.makeText(context, "Cannot create output file", Toast.LENGTH_SHORT).show()
                onClose()
                return@LaunchedEffect
            }
            pendingUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    // Minimal UI while we request permission/launch camera
    if (!permissionsGranted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission required")
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true)
                        ) {
                            val perms = mutableListOf(Manifest.permission.CAMERA)
                            if (needsWrite) perms += Manifest.permission.WRITE_EXTERNAL_STORAGE
                            permLauncher.launch(perms.toTypedArray())
                        }
                ) {
                    Text("Grant")
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true)
                        ) { onClose() }
                ) { Text("Cancel") }
            }
        }
        return
    }

    // Show a lightweight "opening camera" screen with a back button
    Box(Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                // If user backs out before result, cancel and cleanup
                pendingUri?.let { runCatching { context.contentResolver.delete(it, null, null) } }
                pendingUri = null
                onClose()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Opening camera…")
        }
    }
}

private fun createMediaStoreImageUri(
    context: Context,
    directory: String = "LitterBoom"
): Pair<Boolean, Uri?> {
    val isQPlus = Build.VERSION.SDK_INT >= 29
    val name = "waste_${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (isQPlus) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$directory")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        put(MediaStore.Images.Media.TITLE, name)
    }
    val resolver = context.contentResolver
    val collection: Uri =
        if (isQPlus) MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val uri = runCatching { resolver.insert(collection, values) }.getOrNull()
    return (uri != null) to uri
}
