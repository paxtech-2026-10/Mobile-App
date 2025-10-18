package com.paxtech.mobileapp.features.authentication.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenido superior
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                
                // Título
                Text(
                    text = "¡Bienvenido de nuevo!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtítulo
                Text(
                    text = "¿Listo para tu próximo servicio de belleza? Inicia sesión.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Campo Usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { 
                        Text(
                            "Usuario",
                            color = TextSecondary
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = DividerGray,
                        focusedLabelColor = PrimaryPurple,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { 
                        Text(
                            "Contraseña",
                            color = TextSecondary
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = TextSecondary
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = DividerGray,
                        focusedLabelColor = PrimaryPurple,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botón Iniciar Sesión
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = DividerGray
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(DividerGray, CircleShape)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = DividerGray
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botones sociales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Apple
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFF3F4F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍎", style = MaterialTheme.typography.headlineSmall)
                    }
                    
                    // Google
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFF3F4F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", style = MaterialTheme.typography.headlineSmall)
                    }
                    
                    // Facebook
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFF3F4F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("f", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
            
            // Enlace de registro
            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Regístrate aquí",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
