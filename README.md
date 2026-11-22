# AppMaster Martini – Grupo 1

App Android para Master Martini Chile SPA: entrega recetas, videos técnicos y cursos, y permite
registrar perfil, intereses, favoritos y solicitudes de contacto con un asesor.

- **Asignatura:** DSY1105 Desarrollo de Aplicaciones Móviles (Duoc UC)
- **Evaluaciones:** Parcial 4 (encargo en parejas) y Parcial 5 (defensa individual)

## Integrantes

| Integrante | Rol | Capa principal |
|---|---|---|
| Zeus Chavez | UI | `ui/`, `platform/`, `res/`, README y `docs/` |
| Jhoan Sebastian Fernandez | DATOS | `data/`, `domain/`, `viewmodel/`, pruebas, Gradle y APK firmado |

### Mapeo de commits
En el historial de Git aparecen más de un alias por persona (máquinas distintas). El mapeo es:

- `ZeusCG` = Zeus Chavez
- `Harko` y `SebastianFernandez-dev` = Jhoan "Sebastian" Fernandez

## Funcionalidades

- **Autenticación:** registro e inicio de sesión con contraseña hasheada (PBKDF2 + sal), correo único.
- **Catálogo:** recetas, videos técnicos y cursos con búsqueda por texto y filtro por categoría.
- **Detalle:** información del contenido, favoritos y foto de referencia con la cámara.
- **Gestión (CRUD):** crear, editar y eliminar contenido desde la app, con validación por campo y confirmación al borrar.
- **Favoritos:** marcar/desmarcar contenido; al eliminar un contenido se limpian sus favoritos.
- **Perfil e intereses:** datos del cliente y selección de intereses.
- **Clima del taller:** tarjeta con temperatura, humedad y un consejo; usa el último dato guardado si no hay internet.
- **Contacto:** solicitud a un asesor con historial local y estado enviada/pendiente.

## API externa (Retrofit)

Sin backend propio (indicación del docente): se consumen dos APIs públicas, sin clave.

### GET – Clima del taller (Open-Meteo)
`GET https://api.open-meteo.com/v1/forecast?latitude=-33.45&longitude=-70.66&current=temperature_2m,relative_humidity_2m`

Ejemplo:
```bash
curl "https://api.open-meteo.com/v1/forecast?latitude=-33.45&longitude=-70.66&current=temperature_2m,relative_humidity_2m"
```
Respuesta esperada: `{"current":{"time":"...","temperature_2m":18.0,"relative_humidity_2m":50}}`

> Crédito obligatorio mostrado en la app: **Weather data by Open-Meteo.com**

### POST – Solicitud de contacto (JSONPlaceholder)
`POST https://jsonplaceholder.typicode.com/posts`

Ejemplo:
```bash
curl -X POST https://jsonplaceholder.typicode.com/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Consulta","body":"Hola","userId":1}'
```
Responde `201` con `{"id":101,...}`. La API solo simula el guardado; la solicitud real se respalda en
la base de datos local (Room) para conservar el historial.

## Cómo ejecutar

1. Requisitos: Android Studio (o Android SDK + JDK 21), minSdk 24.
2. Clonar el repositorio:
   ```bash
   git clone https://github.com/ZeusCG/AppMaster_Martini_Grupo1.git
   ```
3. Definir el SDK (`ANDROID_HOME` o `local.properties` con `sdk.dir`).
4. Compilar/instalar la versión de depuración:
   ```bash
   ./gradlew assembleDebug
   ```
5. Ejecutar las pruebas y la cobertura:
   ```bash
   ./gradlew testDebugUnitTest jacocoTestReport
   ```
   Reporte en `app/build/reports/jacoco/jacocoTestReport/html/index.html`.

## APK firmado y llave

- Llave: `release/appmaster.jks` (alias `appmaster`).
- APK firmado: `release/app-release.apk`.
- Para generarlo:
  ```bash
  ./gradlew assembleRelease
  apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
  cp app/build/outputs/apk/release/app-release.apk release/
  ```
- Las credenciales de firma viven en `keystore.properties` (no se sube al repositorio).
- Captura del APK firmado y del `.jks`: ver `docs/capturas/`.

## Evidencia

- `docs/` – capturas del tablero Trello, del reporte de cobertura y del APK firmado + `.jks`.
- Tablero Trello del proyecto: [agregar enlace]
- Indicación del docente (sin backend Spring Boot): `docs/indicacion-docente.md`
