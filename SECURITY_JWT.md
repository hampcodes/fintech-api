# 🔐 Spring Security y JWT - Fintech API

---

## 📋 Tabla de Contenidos

1. [¿Qué es Spring Security?](#qué-es-spring-security)
2. [¿Qué es JWT?](#qué-es-jwt)
3. [¿Cómo Trabajan Juntos Spring Security y JWT?](#cómo-trabajan-juntos-spring-security-y-jwt)
4. [Clases de Seguridad](#clases-de-seguridad)
5. [Implementación Paso a Paso](#implementación-paso-a-paso)
   - [Paso 1: Dependencias Maven](#paso-1-dependencias-maven)
   - [Paso 2: Configuración JWT](#paso-2-configuración-jwt)
   - [Paso 3: Crear Modelo de Datos](#paso-3-crear-modelo-de-datos)
   - [Paso 4: Crear JwtUtil](#paso-4-crear-jwtutil)
   - [Paso 5: Crear JwtAuthenticationFilter](#paso-5-crear-jwtauthenticationfilter)
   - [Paso 6: Crear UserDetailsServiceImpl](#paso-6-crear-userdetailsserviceimpl)
   - [Paso 7: Configurar SecurityConfig](#paso-7-configurar-securityconfig)
   - [Paso 8: Configurar CORS](#paso-8-configurar-cors)
   - [Paso 9: Crear AuthService](#paso-9-crear-authservice)
   - [Paso 10: Crear AuthController](#paso-10-crear-authcontroller)
   - [Paso 11: Inicializar Roles](#paso-11-inicializar-roles)
   - [Paso 12: Autorización con @PreAuthorize](#paso-12-autorización-con-preauthorize)
6. [Resumen](#resumen)

---

## 🛡️ ¿Qué es Spring Security?

Spring Security es un módulo del framework Spring que proporciona seguridad para aplicaciones. Ofrece:

- **Autenticación**: Verificar la identidad del usuario (login)
- **Autorización**: Controlar qué puede hacer cada usuario (roles y permisos)
- **Protección**: Contra ataques comunes (CSRF, XSS, etc.)

**En Fintech API:**
- Rutas públicas: `/auth/register`, `/auth/login`
- Rutas protegidas: `/accounts`, `/transactions`, `/admin/**`
- Autenticación: JWT (tokens stateless)
- Roles: `ROLE_USER`, `ROLE_ADMIN`

---

## 🎫 ¿Qué es JWT?

**JWT (JSON Web Token)** es un estándar para transmitir información de forma segura entre el cliente y el servidor.

### Estructura

```
eyJhbGci...  .  eyJzdWIi...  .  SflKxwRJ...
  HEADER         PAYLOAD        SIGNATURE
```

- **Header**: Algoritmo de firma (HS256)
- **Payload**: Datos del usuario (email, nombre, customerId, expiración)
- **Signature**: Firma criptográfica para validar autenticidad

**Claims incluidos en el token:**
- `sub`: Email del usuario
- `name`: Nombre del usuario
- `customerId`: ID del Customer asociado
- `iat`: Fecha de emisión (Issued At)
- `exp`: Fecha de expiración

### ¿Por qué JWT?

- ✅ **Stateless**: No requiere sesiones en servidor
- ✅ **Seguro**: Firmado con clave secreta
- ✅ **Escalable**: Funciona en microservicios
- ✅ **Self-contained**: Contiene toda la info necesaria

**En Fintech API:**
- Expiración: 24 horas
- Algoritmo: HMAC-SHA256
- Claims incluidos: `sub` (email), `name` (nombre), `customerId` (ID del Customer)

---

## 🔗 ¿Cómo Trabajan Juntos Spring Security y JWT?

Spring Security y JWT se complementan para crear un sistema de autenticación y autorización stateless:

### División de Responsabilidades

**Spring Security se encarga de:**
- Gestionar el ciclo de autenticación y autorización
- Definir qué rutas son públicas y cuáles requieren autenticación
- Mantener el contexto de seguridad (quién está autenticado)
- Validar roles y permisos con `@PreAuthorize`
- Encriptar contraseñas con BCrypt
- Gestionar la cadena de filtros de seguridad

**JWT se encarga de:**
- Almacenar la información del usuario autenticado (email, nombre, customerId)
- Viajar entre cliente y servidor en cada request
- Ser validado en cada petición sin consultar la base de datos
- Tener una fecha de expiración automática
- No requerir almacenamiento en servidor (stateless)

### Integración en Fintech API

1. **Al hacer login:**
   - Spring Security valida email y password contra la base de datos
   - Si es válido, JWT genera un token con los datos del usuario
   - El token se retorna al cliente

2. **En cada request protegido:**
   - El cliente envía el JWT en el header `Authorization: Bearer <token>`
   - Spring Security usa `JwtAuthenticationFilter` para interceptar el request
   - JWT valida el token (firma y expiración)
   - Si es válido, extrae el email del token
   - Spring Security carga el usuario completo con sus roles desde la BD (vía `UserDetailsService`)
   - Spring Security establece la autenticación en el `SecurityContext`
   - `@PreAuthorize` verifica que el usuario tenga el rol necesario
   - Si todo es válido, el request llega al controller

3. **Ventajas de esta combinación:**
   - **Stateless**: El servidor no mantiene sesiones, solo valida tokens
   - **Escalable**: Funciona en múltiples servidores sin sincronización
   - **Seguro**: Spring Security maneja la seguridad, JWT solo transporta datos
   - **Rápido**: No se consulta la BD en cada request para validar el token, solo para cargar roles
   - **Flexible**: JWT puede incluir claims personalizados (customerId)

### Flujo Simplificado

**Login:**
```
Cliente → Spring Security (valida credenciales) → JWT (genera token) → Cliente recibe token
```

**Request Protegido:**
```
Cliente (envía JWT) → JwtAuthenticationFilter (valida JWT) → UserDetailsService (carga roles)
→ SecurityContext (establece autenticación) → @PreAuthorize (verifica rol) → Controller
```

**Resultado:**
- Spring Security controla **quién** puede acceder y **qué** puede hacer
- JWT transporta **la identidad** del usuario de forma segura y eficiente

---

## 📦 Clases de Seguridad

### Clases Principales

| Clase | Ubicación | Responsabilidad |
|-------|-----------|-----------------|
| **JwtUtil** | `security/JwtUtil.java` | Genera, valida y extrae información de tokens JWT |
| **JwtAuthenticationFilter** | `security/JwtAuthenticationFilter.java` | Intercepta requests, valida JWT y establece autenticación |
| **UserDetailsServiceImpl** | `security/UserDetailsServiceImpl.java` | Carga usuarios desde BD con sus roles |
| **SecurityConfig** | `config/SecurityConfig.java` | Configuración central de seguridad (rutas, filtros) |
| **CorsConfig** | `config/CorsConfig.java` | Configuración CORS para permitir frontend |
| **AuthService** | `service/AuthService.java` | Lógica de negocio para registro y login |
| **AuthController** | `controller/AuthController.java` | Endpoints REST `/auth/register` y `/auth/login` |
| **DataInitializer** | `config/DataInitializer.java` | Inicializa roles y usuario admin al arrancar |

### Modelo de Datos

| Entidad | Ubicación | Propósito |
|---------|-----------|-----------|
| **User** | `model/User.java` | Email, password (BCrypt) y rol |
| **Role** | `model/Role.java` | Roles del sistema |
| **RoleType** | `model/RoleType.java` | Enum: `ROLE_USER`, `ROLE_ADMIN` |
| **Customer** | `model/Customer.java` | Datos personales del usuario (nombre, DNI, etc.) |

**Relación User-Customer**: Un User (1) tiene un Customer (1). El customerId se incluye en el JWT.

---

## 🚀 Implementación Paso a Paso

### Paso 1: Dependencias Maven

Agregar en `pom.xml`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

### Paso 2: Configuración JWT

En `application.properties`:

```properties
# JWT Configuration
jwt.secret=MiClaveSecretaSuperSeguraParaJWT123456789012345678901234567890
jwt.expiration=86400000  # 24 horas en milisegundos
```

⚠️ **Producción**: Usar variables de entorno, no hardcodear.

---

### Paso 3: Crear Modelo de Datos

#### RoleType.java
```java
public enum RoleType {
    ROLE_USER,
    ROLE_ADMIN
}
```

#### Role.java
```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;
}
```

#### User.java
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;  // BCrypt hash

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean active = true;
}
```

---

### Paso 4: Crear JwtUtil

**Ubicación**: `src/main/java/com/fintech/security/JwtUtil.java`

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Generar token con email, nombre y customerId
    public String generateToken(String email, String name, String customerId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)              // Email del usuario
                .claim("name", name)            // Nombre del usuario
                .claim("customerId", customerId) // ID del Customer
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer email del token
    public String getEmailFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    // Extraer nombre del token
    public String getNameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("name", String.class);
    }

    // Extraer customerId del token
    public String getCustomerIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("customerId", String.class);
    }

    // Obtener todos los claims
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

### Paso 5: Crear JwtAuthenticationFilter

**Ubicación**: `src/main/java/com/fintech/security/JwtAuthenticationFilter.java`

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.getEmailFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/") ||
               path.contains("/swagger-ui") ||
               path.contains("/v3/api-docs");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

### Paso 6: Crear UserDetailsServiceImpl

**Ubicación**: `src/main/java/com/fintech/security/UserDetailsServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getActive(),
                true, true, true,
                authorities
        );
    }
}
```

---

### Paso 7: Configurar SecurityConfig

**Ubicación**: `src/main/java/com/fintech/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
```

---

### Paso 8: Configurar CORS

**Ubicación**: `src/main/java/com/fintech/config/CorsConfig.java`

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",    // React
            "http://localhost:4200"     // Angular
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```

---

### Paso 9: Crear AuthService

**Ubicación**: `src/main/java/com/fintech/service/AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already registered");
        }

        // Crear User
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Role ROLE_USER not found"));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        // Crear Customer asociado
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setDni(request.dni());
        customer.setAddress(request.address());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setNationality(request.nationality());
        customer.setOccupation(request.occupation());
        Customer savedCustomer = customerRepository.save(customer);

        // Generar JWT con email, nombre y customerId
        String token = jwtUtil.generateToken(
            savedUser.getEmail(),
            savedCustomer.getName(),
            savedCustomer.getId()
        );

        return new AuthResponse(token, savedUser.getEmail(), savedCustomer.getName());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for user"));

        // Generar JWT con email, nombre y customerId
        String token = jwtUtil.generateToken(
            user.getEmail(),
            customer.getName(),
            customer.getId()
        );

        return new AuthResponse(token, user.getEmail(), customer.getName());
    }
}
```

---

### Paso 10: Crear AuthController

**Ubicación**: `src/main/java/com/fintech/controller/AuthController.java`

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

---

### Paso 11: Inicializar Roles

**Ubicación**: `src/main/java/com/fintech/config/DataInitializer.java`

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with default data...");

        // Crear roles
        Role userRole = createRoleIfNotExists(RoleType.ROLE_USER);
        Role adminRole = createRoleIfNotExists(RoleType.ROLE_ADMIN);

        // Crear usuario admin
        createAdminUserIfNotExists(adminRole);

        log.info("Database initialization completed.");
    }

    private Role createRoleIfNotExists(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role role = new Role(roleType);
                    roleRepository.save(role);
                    log.info("Created role: {}", roleType);
                    return role;
                });
    }

    private void createAdminUserIfNotExists(Role adminRole) {
        String adminEmail = "admin@fintech.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole(adminRole);
        adminUser.setActive(true);
        User savedAdmin = userRepository.save(adminUser);

        Customer adminCustomer = new Customer();
        adminCustomer.setUser(savedAdmin);
        adminCustomer.setName("System Administrator");
        customerRepository.save(adminCustomer);

        log.info("========================================");
        log.info("DEFAULT ADMIN USER CREATED:");
        log.info("Email: {}", adminEmail);
        log.info("Password: admin123");
        log.info("⚠️  CHANGE THIS PASSWORD IN PRODUCTION!");
        log.info("========================================");
    }
}
```

---

### Paso 12: Autorización con @PreAuthorize

#### ¿Qué es @PreAuthorize?

`@PreAuthorize` es una anotación de Spring Security que permite controlar el acceso a métodos y clases basándose en roles. Se evalúa **ANTES** de ejecutar el método.

#### Habilitación

⚠️ **CRÍTICO**: Para usar `@PreAuthorize`, debes habilitar `@EnableMethodSecurity` en SecurityConfig:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ⚠️ Sin esto, @PreAuthorize NO funciona
public class SecurityConfig {
    // ...
}
```

#### Expresiones Comunes

| Expresión | Descripción | Uso |
|-----------|-------------|-----|
| `hasRole('USER')` | Usuario tiene rol USER | `@PreAuthorize("hasRole('USER')")` |
| `hasRole('ADMIN')` | Usuario tiene rol ADMIN | `@PreAuthorize("hasRole('ADMIN')")` |
| `hasAnyRole('USER','ADMIN')` | Tiene cualquiera de esos roles | `@PreAuthorize("hasAnyRole('USER','ADMIN')")` |
| `isAuthenticated()` | Usuario está autenticado | `@PreAuthorize("isAuthenticated()")` |

**Nota**: Los roles en BD tienen prefijo `ROLE_` (`ROLE_USER`, `ROLE_ADMIN`), pero en `@PreAuthorize` se usa **SIN** el prefijo.

#### @PreAuthorize a Nivel de Clase (RECOMENDADO)

```java
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")  // ⭐ Aplica a TODOS los métodos
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable String id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public AccountResponse createAccount(@RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    // Todos los métodos requieren ROLE_USER automáticamente
}
```

**Ventajas:**
- ✅ DRY (Don't Repeat Yourself)
- ✅ Menos propenso a errores
- ✅ Código más limpio

#### Ejemplo: Controller para Administradores

```java
@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // Solo ROLE_ADMIN
public class AdminAccountController {

    private final AccountService accountService;

    // Retorna TODAS las cuentas (sin filtro de ownership)
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccountsAdmin();
    }

    // Puede acceder a CUALQUIER cuenta por ID
    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable String id) {
        return accountService.getAccountByIdAdmin(id);
    }
}
```

#### Códigos de Error

| Código | Condición | Ejemplo |
|--------|-----------|---------|
| **401 Unauthorized** | JWT ausente o inválido | Request sin header Authorization |
| **403 Forbidden** | JWT válido pero sin rol necesario | USER intenta acceder a `/admin/accounts` |

#### Arquitectura: USER vs ADMIN

```
┌─────────────────────────┐      ┌──────────────────────────┐
│  ROLE_USER              │      │  ROLE_ADMIN              │
├─────────────────────────┤      ├──────────────────────────┤
│ /accounts               │      │ /admin/accounts          │
│ /transactions           │      │ /admin/customers         │
│ /customer/profile       │      │ /admin/transactions      │
│                         │      │ /admin/reports           │
│ ✅ Con validación       │      │ /admin/settings          │
│    de ownership         │      │                          │
│    (solo sus recursos)  │      │ ❌ Sin validación        │
│                         │      │    de ownership          │
│                         │      │    (todos los recursos)  │
└─────────────────────────┘      └──────────────────────────┘
```

#### Mejores Prácticas

1. **✅ Usar @PreAuthorize a nivel de clase** cuando todos los métodos usan el mismo rol
2. **✅ Separar controllers USER y ADMIN** en clases diferentes
3. **✅ Habilitar @EnableMethodSecurity** en SecurityConfig
4. **✅ Validar ownership en Service layer** para usuarios normales
5. **❌ NO usar @PreAuthorize en servicios**, usarlo solo en controllers

---

## ✅ Resumen

**Configuración completa:**
1. ✅ Dependencias Maven agregadas (Spring Security, JJWT)
2. ✅ JWT configurado en `application.properties` (secret, expiration)
3. ✅ Entidades: User, Role, RoleType, Customer
4. ✅ JwtUtil (generar con email/name/customerId, validar, extraer claims)
5. ✅ JwtAuthenticationFilter (interceptar requests y validar JWT)
6. ✅ UserDetailsServiceImpl (cargar usuarios con roles desde BD)
7. ✅ SecurityConfig (rutas públicas/protegidas, @EnableMethodSecurity)
8. ✅ CorsConfig (permitir frontend desde localhost:3000, localhost:4200)
9. ✅ AuthService (register y login con transacciones)
10. ✅ AuthController (endpoints REST)
11. ✅ DataInitializer (roles ROLE_USER, ROLE_ADMIN y usuario admin)
12. ✅ @PreAuthorize (control de acceso basado en roles)

**JWT Claims incluidos:**
- `sub`: Email del usuario
- `name`: Nombre del usuario
- `customerId`: ID del Customer asociado
- `iat`: Fecha de emisión
- `exp`: Fecha de expiración (24 horas)

**Endpoints de Autenticación:**
- `POST /api/v1/auth/register` → Registrar usuario (crea User + Customer, retorna JWT)
- `POST /api/v1/auth/login` → Iniciar sesión (valida credenciales, retorna JWT)

**Endpoints Protegidos:**
- `/accounts` → Requiere `ROLE_USER` (con validación de ownership)
- `/transactions` → Requiere `ROLE_USER` (con validación de ownership)
- `/customer/profile` → Requiere `ROLE_USER` (solo su perfil)
- `/admin/**` → Requiere `ROLE_ADMIN` (sin validación de ownership)

**Credenciales Admin por defecto:**
- Email: `admin@fintech.com`
- Password: `admin123`
- ⚠️ **Cambiar en producción**

**Flujo de Autenticación:**
1. Usuario hace POST a `/auth/login` con email y password
2. Spring Security valida las credenciales (BCrypt)
3. Si es válido, genera JWT con email, nombre y customerId
4. Usuario recibe JWT en la respuesta
5. Usuario envía JWT en header `Authorization: Bearer <token>` en cada request
6. JwtAuthenticationFilter valida el JWT en cada request
7. @PreAuthorize verifica que el usuario tenga el rol necesario
8. Si tiene el rol, el controller ejecuta la lógica
9. El service valida ownership (para ROLE_USER) antes de retornar datos

---

**Última actualización**: 26 de Octubre, 2025
**Versión**: 2.0.0
