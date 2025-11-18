package com.paxtech.mobileapp.core.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun rememberImagePickerLauncher(
    onImageSelected: (File) -> Unit
): androidx.activity.result.ActivityResultLauncher<String> {
    val context = LocalContext.current
    
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = createTempImageFile(context, it)
            file?.let(onImageSelected)
        }
    }
}

@Composable
fun rememberCameraLauncher(
    onImageCaptured: (File) -> Unit
): Pair<androidx.activity.result.ActivityResultLauncher<Uri>, File> {
    val context = LocalContext.current
    
    val tempFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "temp_profile_${System.currentTimeMillis()}.jpg"
    )
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFile.exists()) {
            onImageCaptured(tempFile)
        }
    }
    
    return Pair(launcher, tempFile)
}

private fun createTempImageFile(context: Context, uri: Uri): File? {
    return try {
        println("🔍 ImagePickerUtils: Creating temp file from URI: $uri")
        
        // Intentar obtener el tipo MIME del archivo original
        val mimeType = context.contentResolver.getType(uri)
        println("🔍 ImagePickerUtils: Original MIME type: $mimeType")
        
        // Determinar la extensión basándose en el tipo MIME o en el nombre del archivo
        val extension = getFileExtensionFromMimeType(mimeType) ?: getFileExtensionFromUri(context, uri) ?: "jpg"
        println("🔍 ImagePickerUtils: Determined extension: $extension")
        
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(
            context.cacheDir,
            "temp_profile_${System.currentTimeMillis()}.$extension"
        )
        
        println("🔍 ImagePickerUtils: Temp file path: ${tempFile.absolutePath}")
        
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                val bytesCopied = input.copyTo(output)
                println("🔍 ImagePickerUtils: Copied $bytesCopied bytes to temp file")
            }
        }
        
        println("🔍 ImagePickerUtils: Temp file created successfully: ${tempFile.exists()}, size: ${tempFile.length()} bytes")
        tempFile
    } catch (e: Exception) {
        println("🔍 ImagePickerUtils: Error creating temp file: ${e.message}")
        e.printStackTrace()
        null
    }
}

/**
 * Obtiene la extensión del archivo basándose en el tipo MIME
 */
private fun getFileExtensionFromMimeType(mimeType: String?): String? {
    return when (mimeType?.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/gif" -> "gif"
        else -> null
    }
}

/**
 * Intenta obtener la extensión del archivo desde el URI
 */
private fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                val fileName = it.getString(nameIndex)
                val extension = fileName.substringAfterLast('.', "").lowercase()
                if (extension.isNotEmpty()) extension else null
            } else null
        }
    } catch (e: Exception) {
        println("🔍 ImagePickerUtils: Error getting extension from URI: ${e.message}")
        null
    }
}

