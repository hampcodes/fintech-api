# 🌐 CORS - Cross-Origin Resource Sharing

---

## ¿Qué es CORS?

**CORS** (Cross-Origin Resource Sharing) es un mecanismo de seguridad implementado por los navegadores web que controla cómo los recursos de un servidor pueden ser solicitados desde un dominio diferente.

### Problema que Resuelve

Por defecto, los navegadores bloquean peticiones HTTP entre diferentes orígenes por seguridad (política **Same-Origin Policy**).

**Ejemplo del problema:**
```
Frontend en:      http://localhost:3000  (React/Angular)
Backend API en:   http://localhost:8080  (Spring Boot)

❌ Sin CORS: El navegador BLOQUEA la petición
✅ Con CORS: El navegador PERMITE la petición
```

### Componentes de un Origen

Un origen se compone de tres partes:

```
https://example.com:443/api/users
└─┬──┘ └────┬─────┘ └┬┘
Protocolo  Dominio  Puerto
```

**Orígenes diferentes:**
- `http://localhost:3000` ≠ `http://localhost:8080` → Puerto diferente ❌
- `http://example.com` ≠ `https://example.com` → Protocolo diferente ❌
- `http://api.example.com` ≠ `http://example.com` → Subdominio diferente ❌

**Mismo origen:**
- `http://localhost:8080/api/users` = `http://localhost:8080/api/accounts` ✅

---

## Flujo CORS

### 1. Preflight Request (Petición de Verificación)

Para peticiones complejas (POST, PUT, DELETE con headers personalizados), el navegador envía primero una petición **OPTIONS**:

```
NAVEGADOR                                    SERVIDOR
    │                                            │
    │  OPTIONS /api/accounts                     │
    │  Origin: http://localhost:3000             │
    ├───────────────────────────────────────────►│
    │                                            │
    │  Access-Control-Allow-Origin: http://...   │
    │  Access-Control-Allow-Methods: GET,POST... │
    │  Access-Control-Allow-Headers: *           │
    │◄───────────────────────────────────────────┤
    │                                            │
    │  POST /api/accounts                        │
    │  Authorization: Bearer eyJhbG...           │
    ├───────────────────────────────────────────►│
    │                                            │
    │  200 OK                                    │
    │  {data: ...}                               │
    │◄───────────────────────────────────────────┤
    │                                            │
```

### 2. Simple Request (Sin Preflight)

Para peticiones simples (GET, HEAD), el navegador hace la petición directamente:

```
NAVEGADOR                                    SERVIDOR
    │                                            │
    │  GET /api/accounts                         │
    │  Origin: http://localhost:3000             │
    ├───────────────────────────────────────────►│
    │                                            │
    │  Access-Control-Allow-Origin: http://...   │
    │  200 OK                                    │
    │  {accounts: [...]}                         │
    │◄───────────────────────────────────────────┤
    │                                            │
```

---

## Configuración CORS en Fintech API

### Ubicación
`src/main/java/com/fintech/config/SecurityConfig.java`

### Configuración Actual

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 1. Orígenes permitidos
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",  // React dev server
        "http://localhost:4200"   // Angular dev server
    ));

    // 2. Métodos HTTP permitidos
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));

    // 3. Headers permitidos (todos)
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // 4. Permitir credenciales (cookies, JWT en Authorization header)
    configuration.setAllowCredentials(true);

    // 5. Headers expuestos al cliente
    configuration.setExposedHeaders(Arrays.asList("Authorization"));

    // 6. Tiempo de cache de la configuración CORS (1 hora)
    configuration.setMaxAge(3600L);

    // Aplicar a todas las rutas
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
}
```

### En SecurityFilterChain

```java
http
    .csrf(csrf -> csrf.disable())
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ⭐ CORS habilitado
    .authorizeHttpRequests(...)
```

---

## Headers CORS Importantes

| Header | Descripción | Valor en Fintech API |
|--------|-------------|----------------------|
| **Access-Control-Allow-Origin** | Orígenes permitidos | `http://localhost:3000` |
| **Access-Control-Allow-Methods** | Métodos HTTP permitidos | `GET, POST, PUT, PATCH, DELETE, OPTIONS` |
| **Access-Control-Allow-Headers** | Headers de petición permitidos | `*` (todos) |
| **Access-Control-Allow-Credentials** | Permite enviar cookies/tokens | `true` |
| **Access-Control-Expose-Headers** | Headers de respuesta visibles al JS | `Authorization` |
| **Access-Control-Max-Age** | Cache de preflight (segundos) | `3600` (1 hora) |

---

## Configuración por Entorno

### Desarrollo (application.properties)

```properties
# CORS ya configurado en SecurityConfig
# No requiere configuración adicional
```

### Producción (application-prod.properties)

```java
// Cambiar en SecurityConfig.java para producción:
configuration.setAllowedOrigins(Arrays.asList(
    "https://myapp.com",           // Frontend producción
    "https://www.myapp.com"        // Con www
));
```

**⚠️ NUNCA usar `*` (todos los orígenes) en producción con `allowCredentials(true)`**

---

## Testing CORS

### Con cURL

```bash
# Simular preflight request
curl -X OPTIONS http://localhost:8080/api/v1/accounts \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v

# Debe retornar:
# Access-Control-Allow-Origin: http://localhost:3000
# Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
# Access-Control-Allow-Headers: *
```

### Con JavaScript (Fetch API)

```javascript
// Desde http://localhost:3000
fetch('http://localhost:8080/api/v1/accounts', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer eyJhbGci...'
  },
  credentials: 'include'  // Enviar cookies/credenciales
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('CORS error:', error));
```

---

## Errores Comunes CORS

### Error 1: Origin no permitido

```
Access to fetch at 'http://localhost:8080/api/v1/accounts' from origin
'http://localhost:5173' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present
```

**Solución:** Agregar `http://localhost:5173` a `allowedOrigins`

### Error 2: Credentials con wildcard

```
The value of the 'Access-Control-Allow-Origin' header in the response
must not be the wildcard '*' when the request's credentials mode is 'include'
```

**Solución:** No usar `*` en `allowedOrigins` cuando `allowCredentials` es `true`

### Error 3: Método no permitido

```
Method DELETE is not allowed by Access-Control-Allow-Methods
```

**Solución:** Agregar `DELETE` a `allowedMethods`

---

## Mejores Prácticas

1. **✅ Especificar orígenes exactos en producción**
   ```java
   // ❌ Malo (inseguro)
   configuration.setAllowedOrigins(Arrays.asList("*"));

   // ✅ Bueno (seguro)
   configuration.setAllowedOrigins(Arrays.asList("https://myapp.com"));
   ```

2. **✅ Limitar métodos necesarios**
   ```java
   // Solo permitir lo que realmente usas
   configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
   ```

3. **✅ Usar variables de entorno para orígenes**
   ```java
   @Value("${cors.allowed-origins}")
   private String allowedOrigins;

   configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
   ```

4. **✅ Cache adecuado de preflight**
   ```java
   // 1 hora es suficiente para desarrollo
   configuration.setMaxAge(3600L);
   ```

5. **❌ NO exponer headers sensibles**
   ```java
   // Solo exponer lo necesario
   configuration.setExposedHeaders(Arrays.asList("Authorization"));
   ```

---

## CORS vs CSRF

| Aspecto | CORS | CSRF |
|---------|------|------|
| **Qué protege** | Bloquea peticiones cross-origin no autorizadas | Protege contra peticiones maliciosas desde sitios de terceros |
| **Dónde se aplica** | Navegador web | Servidor |
| **Tipo de ataque** | Acceso no autorizado a recursos | Ejecución de acciones no deseadas |
| **Solución en API REST** | Configurar CORS correctamente | Usar JWT (stateless), no cookies de sesión |

**En Fintech API:**
- ✅ CORS: Habilitado para permitir frontend en diferentes puertos
- ❌ CSRF: Deshabilitado (usamos JWT stateless, no cookies de sesión)

---

## Resumen

✅ **CORS configurado en**: `SecurityConfig.java`

✅ **Orígenes permitidos**:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)

✅ **Métodos permitidos**: GET, POST, PUT, PATCH, DELETE, OPTIONS

✅ **Credenciales permitidas**: Sí (para JWT en Authorization header)

✅ **Preflight cache**: 1 hora (3600 segundos)

**⚠️ Recordar:** En producción, cambiar `allowedOrigins` a tu dominio real.

---

**Última actualización**: 26 de Octubre, 2025
**Versión**: 1.0.0
