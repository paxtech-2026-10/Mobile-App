# MobileApp - Firebase App Distribution

Guía para automatizar la subida de APK a Firebase App Distribution.

## 📋 Requisitos Previos

- Node.js y npm instalados
- Cuenta de Google con acceso al proyecto Firebase
- Proyecto Firebase configurado con `google-services.json`

## 🚀 Configuración Inicial

### Paso 1: Instalar Firebase CLI

Abre PowerShell o Terminal y ejecuta:

```bash
npm install -g firebase-tools
```

Verifica la instalación:

```bash
firebase --version
```

### Paso 2: Autenticarse con Firebase

Ejecuta el siguiente comando para iniciar sesión:

```bash
firebase login
```

Esto abrirá tu navegador para que inicies sesión con tu cuenta de Google asociada a Firebase. Acepta los permisos necesarios.

**Nota:** Si trabajas en un entorno CI/CD, puedes usar un token:

```bash
firebase login:ci
```

### Comandos Útiles de Firebase CLI

#### Verificar autenticación

Ver qué cuentas están autenticadas:

```bash
firebase login:list
```

#### Cerrar sesión

Cerrar sesión de Firebase CLI (útil cuando trabajas en otra PC):

```bash
firebase logout
```

Cerrar sesión de una cuenta específica:

```bash
firebase logout --account correo@ejemplo.com
```

#### Verificar versión

Verificar la versión de Firebase CLI instalada:

```bash
firebase --version
```

#### Ver ayuda

Ver todos los comandos disponibles:

```bash
firebase help
```

**Nota importante:** Si usaste `firebase login:ci` y generaste un token para CI/CD, ese token seguirá siendo válido aunque hagas logout. Para revocarlo, ve a [Google Cloud Console](https://console.cloud.google.com) → **IAM & Admin** → **Service Accounts** o **API Credentials** y revócalo manualmente.

### Paso 3: Verificar Configuración del Proyecto

El proyecto ya está configurado con:

- ✅ Plugin de Firebase App Distribution en `gradle/libs.versions.toml`
- ✅ Plugin aplicado en `build.gradle.kts` (nivel raíz)
- ✅ Plugin aplicado en `app/build.gradle.kts`
- ✅ Configuración de `firebaseAppDistribution` con el App ID

**App ID del proyecto:** `1:926361378609:android:a84ac4062d1a098abfda10`

> **¿Dónde encontrar el App ID?**
> - Abre `app/google-services.json`
> - Busca `mobilesdk_app_id` dentro de `client_info`
> - O ve a Firebase Console → Configuración del proyecto → Tu app Android

## 📦 Generar y Subir APK

### Opción 1: Comando Único (Recomendado)

Este comando genera la APK de release y la sube automáticamente:

**Windows (PowerShell):**
```bash
.\gradlew.bat assembleRelease appDistributionUploadRelease
```

**Linux/Mac:**
```bash
./gradlew assembleRelease appDistributionUploadRelease
```

### Opción 2: Pasos Separados

Si prefieres hacerlo en pasos:

1. **Generar la APK:**
   ```bash
   .\gradlew.bat assembleRelease
   ```

2. **Subir la APK:**
   ```bash
   .\gradlew.bat appDistributionUploadRelease
   ```

La APK se generará en: `app/build/outputs/apk/release/app-release.apk`

## 🔧 Configuración de Testers

Puedes configurar testers de dos formas:

### Opción 1: Desde `app/build.gradle.kts`

Edita la sección `firebaseAppDistribution` en `app/build.gradle.kts`:

```kotlin
firebaseAppDistribution {
    appId = "1:926361378609:android:a84ac4062d1a098abfda10"
    releaseNotes = "Versión de prueba para el profesor"
    
    // Opción A: Grupos de testers (creados en Firebase Console)
    groups = "testers,profesores"
    
    // Opción B: Testers individuales
    testers = "profesor@ejemplo.com,otro@ejemplo.com"
    
    // Opción C: Ambos
    // groups = "testers"
    // testers = "profesor@ejemplo.com"
}
```

### Opción 2: Desde Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto: `paxtechandroid`
3. Ve a **App Distribution** en el menú lateral
4. Haz clic en **"Testers y grupos"**
5. Crea grupos o agrega testers individuales
6. Al subir una nueva release, selecciona los testers

## 📱 Verificar la Subida

Después de ejecutar el comando de subida:

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto: `paxtechandroid`
3. Ve a **App Distribution** en el menú lateral
4. Deberías ver tu release recién subida con:
   - Versión de la app
   - Notas de la versión
   - Fecha de subida
   - Estado de distribución

## 🐛 Solución de Problemas

### Error: "Firebase CLI no encontrado"

**Solución:** Asegúrate de tener Firebase CLI instalado:
```bash
npm install -g firebase-tools
```

### Error: "No autenticado" o "Permission denied"

**Solución:** Vuelve a autenticarte:
```bash
firebase login
```

### Error: "App ID no válido"

**Solución:** Verifica que el App ID en `app/build.gradle.kts` coincida con el de `app/google-services.json`:
- Abre `app/google-services.json`
- Busca `mobilesdk_app_id`
- Copia el valor y actualízalo en `app/build.gradle.kts`

### Error: "Plugin no encontrado"

**Solución:** Sincroniza el proyecto Gradle:
- En Android Studio: **File → Sync Project with Gradle Files**
- O ejecuta: `.\gradlew.bat --refresh-dependencies`

### Error: "No se puede encontrar la APK"

**Solución:** Asegúrate de generar la APK primero:
```bash
.\gradlew.bat assembleRelease
```

Luego verifica que existe en: `app/build/outputs/apk/release/app-release.apk`

## 📝 Notas Adicionales

- **Versión del Plugin:** Firebase App Distribution v4.0.1
- **Proyecto Firebase:** `paxtechandroid`
- **Package Name:** `com.paxtech.mobileapp`
- **App ID:** `1:926361378609:android:a84ac4062d1a098abfda10`

## 🔗 Enlaces Útiles

- [Documentación de Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Firebase Console](https://console.firebase.google.com)
- [Documentación del Plugin de Gradle](https://firebase.google.com/docs/app-distribution/android/distribute-gradle)

## 📞 Soporte

Si encuentras algún problema, verifica:
1. Que Firebase CLI esté instalado y autenticado
2. Que el App ID sea correcto
3. Que tengas permisos en el proyecto Firebase
4. Que la APK se haya generado correctamente

