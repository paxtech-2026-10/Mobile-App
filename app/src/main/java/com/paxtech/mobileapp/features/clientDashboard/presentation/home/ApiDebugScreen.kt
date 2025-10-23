package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Composable
fun ApiDebugScreen() {
    var result by remember { mutableStateOf("Presiona 'Test API' para probar...") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🔍 API Debug Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                isLoading = true
                result = "Probando API..."
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Probando..." else "Test API Directo")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resultado:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Test directo con OkHttp
        LaunchedEffect(isLoading) {
            if (isLoading) {
                result = try {
                    testApiDirectly()
                } catch (e: Exception) {
                    "❌ Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }
}

private suspend fun testApiDirectly(): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://utime-web-service.onrender.com/api/v1/provider-profiles")
            .addHeader("Accept", "application/json")
            .build()

        val response = client.newCall(request).execute()
        
        if (response.isSuccessful) {
            val body = response.body?.string() ?: "Empty body"
            "✅ Status: ${response.code}\n" +
            "✅ Message: ${response.message}\n" +
            "✅ Body: $body"
        } else {
            "❌ Status: ${response.code}\n" +
            "❌ Message: ${response.message}\n" +
            "❌ Error Body: ${response.body?.string()}"
        }
    } catch (e: IOException) {
        "❌ Network Error: ${e.message}"
    } catch (e: Exception) {
        "❌ General Error: ${e.message}"
    }
}
