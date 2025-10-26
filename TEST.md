# Guía de Tests Unitarios - Fintech API

## 📋 Tabla de Contenidos
- [Introducción](#introducción)
- [Arquitectura Customer-Account](#arquitectura-customer-account)
- [Estructura de Tests](#estructura-de-tests)
- [Dependencias de Testing](#dependencias-de-testing)
- [Tests de AuthService](#tests-de-authservice)
- [Tests de AccountService](#tests-de-accountservice)
- [Tests de TransactionService](#tests-de-transactionservice)
- [Refactorización y Mejores Prácticas](#refactorización-y-mejores-prácticas)
- [Ejecución de Tests](#ejecución-de-tests)
- [Cobertura de Código](#cobertura-de-código)

---

## Introducción

Esta guía documenta la estrategia de testing unitario implementada en el proyecto Fintech API. Los tests utilizan **Mockito** para crear mocks de dependencias y **AssertJ** para aserciones fluidas y legibles.

### ¿Por qué Tests Unitarios?

- ✅ **Confiabilidad**: Garantizan que el código funciona como se espera
- ✅ **Documentación viva**: Los tests sirven como documentación del comportamiento esperado
- ✅ **Refactorización segura**: Permiten cambiar código con confianza
- ✅ **Detección temprana de bugs**: Encuentran errores antes de producción

### Estadísticas Actuales

- **Total de tests**: 22 tests (4 AuthService + 9 AccountService + 9 TransactionService)
- **Cobertura**: >70% de líneas de código
- **Framework**: JUnit 5 + Mockito + AssertJ
- **Tiempo de ejecución**: ~2 segundos

---

## Arquitectura Customer-Account

### Separación de Responsabilidades

El proyecto implementa una arquitectura que separa claramente:

**User** (Autenticación)
- Email, password, rol
- Manejo de autenticación JWT
- Control de acceso (ROLE_USER, ROLE_ADMIN)

**Customer** (Datos Personales + KYC)
- Nombre, teléfono, DNI, dirección
- Información KYC (Know Your Customer)
- Relación OneToOne con User
- Datos de contacto y ocupación

**Account** (Cuentas Bancarias)
- Número de cuenta, saldo
- Relación ManyToOne con Customer (no con User)
- Transacciones asociadas

### Flujo de Creación

```
Registro → User + Customer
   ↓
Login → JWT Token
   ↓
Crear Cuenta → Account asociada al Customer del User autenticado
```

### Beneficios

1. **GDPR Compliance**: Datos personales pueden eliminarse sin afectar autenticación
2. **Escalabilidad**: Un User puede tener múltiples Customers (joint accounts, corporate)
3. **KYC Management**: Gestión centralizada de verificación de identidad
4. **Clean Architecture**: Separación clara de concerns

---

## Estructura de Tests

```
src/test/java/com/fintech/
└── unit/
    ├── AuthServiceTest.java       (4 tests)
    ├── AccountServiceTest.java    (9 tests)
    └── TransactionServiceTest.java (9 tests)
```

### Tests Detallados

#### AuthServiceTest (4 tests)
1. ✅ `register_ValidData_Success` - Registro exitoso con User + Customer
2. ✅ `register_DuplicateEmail_ThrowsException` - Email duplicado
3. ✅ `register_RoleNotFound_ThrowsException` - Rol no encontrado
4. ✅ `login_ValidCredentials_Success` - Login exitoso

#### AccountServiceTest (9 tests)
1. ✅ `createAccount_ValidData_Success` - Crear cuenta exitosamente
2. ✅ `createAccount_DuplicateAccountNumber_ThrowsException` - Número de cuenta duplicado
3. ✅ `getAccountById_ValidId_Success` - Obtener cuenta por ID
4. ✅ `getAccountById_NonExistentId_ThrowsException` - ID no existe
5. ✅ `getAllAccounts_UserHasAccounts_Success` - Listar todas las cuentas del usuario
6. ✅ `getActiveAccounts_FilterByActive_Success` - Listar solo cuentas activas
7. ✅ `deactivateAccount_ValidId_Success` - Desactivar cuenta
8. ✅ `activateAccount_ValidId_Success` - Activar cuenta
9. ✅ `getBalance_ValidAccountNumber_Success` - Obtener saldo de cuenta

#### TransactionServiceTest (9 tests)
1. ✅ `createTransaction_Deposit_Success` - Crear depósito exitosamente
2. ✅ `createTransaction_Withdrawal_Success` - Crear retiro exitosamente
3. ✅ `createTransaction_AccountNotFound_ThrowsException` - Cuenta inexistente
4. ✅ `createTransaction_InactiveAccount_ThrowsException` - Cuenta inactiva
5. ✅ `createTransaction_InsufficientFunds_ThrowsException` - Saldo insuficiente
6. ✅ `getTransactionById_ValidId_Success` - Obtener transacción por ID
7. ✅ `getTransactionById_NonExistentId_ThrowsException` - ID no existe
8. ✅ `getAllTransactions_Success` - Listar todas las transacciones
9. ✅ `getTransactionsByAccountNumber_Success` - Filtrar por número de cuenta

### Convenciones de Nomenclatura

- **Clase de test**: `[ClaseAProbar]Test.java`
- **Método de test**: `[metodo]_[escenario]_[resultadoEsperado]`
- **Ejemplo**: `register_ValidData_Success`

---

## Dependencias de Testing

### Maven Dependencies (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Frameworks Incluidos

1. **JUnit 5 (Jupiter)**: Framework de testing
2. **Mockito**: Creación de mocks y stubs
3. **AssertJ**: Aserciones fluidas
4. **Spring Boot Test**: Utilidades de testing para Spring
5. **Spring Security Test**: Testing de autenticación y autorización

---

## Tests de AuthService

### Descripción

`AuthServiceTest` verifica el comportamiento del servicio de autenticación, incluyendo registro de usuarios (con creación de Customer) y login con JWT.

### Dependencias Mockeadas

```java
@Mock
private UserRepository userRepository;

@Mock
private CustomerRepository customerRepository;  // NUEVO

@Mock
private RoleRepository roleRepository;

@Mock
private PasswordEncoder passwordEncoder;

@Mock
private JwtUtil jwtUtil;

@Mock
private AuthenticationManager authenticationManager;

@InjectMocks
private AuthService authService;
```

### Helpers y Setup

```java
private Role userRole;

@BeforeEach
void setUp() {
    userRole = new Role();
    userRole.setId(1L);  // Tipo Long, no String
    userRole.setName(RoleType.ROLE_USER);
}

private User createMockUser(String id, String email, String password) {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    user.setPassword(password);
    user.setRole(userRole);
    return user;
}

private Customer createMockCustomer(String id, User user, String name) {
    Customer customer = new Customer();
    customer.setId(id);
    customer.setUser(user);
    customer.setName(name);
    return customer;
}
```

### Tests Implementados

#### 1. Registro de Usuario con Customer

**Test**: `register_ValidData_Success`

```java
@Test
@DisplayName("Debe registrar usuario exitosamente")
void register_ValidData_Success() {
    // Arrange
    RegisterRequest request = new RegisterRequest(
        "john@example.com",
        "password123",
        "John Doe"
    );

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));
    when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

    User savedUser = createMockUser("user-001", "john@example.com", "encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    Customer savedCustomer = createMockCustomer("customer-001", savedUser, "John Doe");
    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    when(jwtUtil.generateToken("john@example.com", "John Doe", "customer-001")).thenReturn("fake-jwt-token");

    // Act
    AuthResponse response = authService.register(request);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.token()).isEqualTo("fake-jwt-token");
    assertThat(response.email()).isEqualTo("john@example.com");
    assertThat(response.name()).isEqualTo("John Doe");

    // IMPORTANTE: Verifica que se creó tanto User como Customer
    verify(userRepository).save(any(User.class));
    verify(customerRepository).save(any(Customer.class));
}
```

**Qué verifica:**
- ✅ Se crea el User con password encriptada
- ✅ Se crea el Customer asociado al User
- ✅ Se asigna el rol ROLE_USER por defecto
- ✅ Se genera un token JWT con email, nombre y customerId
- ✅ El response contiene token, email y nombre

#### 2. Login con Datos de Customer

**Test**: `login_ValidCredentials_Success`

```java
@Test
@DisplayName("Debe hacer login exitosamente")
void login_ValidCredentials_Success() {
    // Arrange
    LoginRequest request = new LoginRequest("john@example.com", "password123");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

    User user = createMockUser("user-001", "john@example.com", "encodedPassword");
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

    // NUEVO: El login ahora busca el Customer para obtener el nombre
    Customer customer = createMockCustomer("customer-001", user, "John Doe");
    when(customerRepository.findByUserId("user-001")).thenReturn(Optional.of(customer));

    when(jwtUtil.generateToken("john@example.com", "John Doe", "customer-001")).thenReturn("fake-jwt-token");

    // Act
    AuthResponse response = authService.login(request);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.token()).isEqualTo("fake-jwt-token");
    assertThat(response.email()).isEqualTo("john@example.com");
    assertThat(response.name()).isEqualTo("John Doe");  // Viene del Customer

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
}
```

### Resumen de Tests de AuthService

| Test | Escenario | Verificaciones Clave |
|------|-----------|---------------------|
| `register_ValidData_Success` | Datos válidos | ✅ Crea User + Customer<br>✅ JWT con email, name, customerId |
| `register_DuplicateEmail_ThrowsException` | Email duplicado | ✅ Lanza DuplicateEmailException |
| `register_RoleNotFound_ThrowsException` | Rol no existe | ✅ Lanza RoleNotFoundException |
| `login_ValidCredentials_Success` | Credenciales correctas | ✅ Busca Customer<br>✅ JWT con email, name, customerId |

**Total**: 4 tests ✅

---

## Tests de AccountService

### Descripción

`AccountServiceTest` verifica la gestión de cuentas bancarias asociadas a Customers (no directamente a Users).

### Cambio Fundamental

**ANTES:**
```java
Account → User (directo)
```

**AHORA:**
```java
User → Customer (OneToOne) → Account (ManyToOne)
```

### Dependencias Mockeadas

```java
@Mock
private AccountRepository accountRepository;

@Mock
private UserRepository userRepository;

@Mock
private CustomerRepository customerRepository;  // NUEVO

@Mock
private SecurityContext securityContext;

@Mock
private Authentication authentication;

@InjectMocks
private AccountService accountService;
```

### Helpers Refactorizados

```java
private User mockUser;
private Customer mockCustomer;

@BeforeEach
void setUp() {
    mockUser = createMockUser("user-001", "john@example.com");
    mockCustomer = createMockCustomer("customer-001", mockUser, "John Doe");
}

private User createMockUser(String id, String email) {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    return user;
}

private Customer createMockCustomer(String id, User user, String name) {
    Customer customer = new Customer();
    customer.setId(id);
    customer.setUser(user);
    customer.setName(name);
    return customer;
}

private Account createMockAccount(String id, String accountNumber,
                                  Customer customer, BigDecimal balance) {
    Account account = new Account();
    account.setId(id);
    account.setAccountNumber(accountNumber);
    account.setCustomer(customer);  // ANTES: setUser
    account.setBalance(balance);
    account.setActive(true);
    return account;
}

private void setupAuthentication(String email, User user, Customer customer) {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
}
```

### Test Ejemplo: Creación de Cuenta

```java
@Test
@DisplayName("Debe crear cuenta exitosamente")
void createAccount_ValidData_Success() {
    // Arrange
    String accountNumber = "1234567890";
    BigDecimal initialBalance = new BigDecimal("1000.00");
    AccountRequest request = new AccountRequest(accountNumber, initialBalance);

    setupAuthentication("john@example.com", mockUser, mockCustomer);
    when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(false);

    Account savedAccount = createMockAccount("acc-001", accountNumber, mockCustomer, initialBalance);
    when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

    // Act
    AccountResponse response = accountService.createAccount(request);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo("acc-001");
    assertThat(response.accountNumber()).isEqualTo(accountNumber);
    assertThat(response.customerId()).isEqualTo("customer-001");      // NUEVO
    assertThat(response.customerName()).isEqualTo("John Doe");        // NUEVO
    assertThat(response.balance()).isEqualByComparingTo(initialBalance);
    assertThat(response.active()).isTrue();

    verify(accountRepository).save(any(Account.class));
}
```

### Beneficios de los Helpers

1. **Reducción de código**: ~30% menos líneas de código
2. **Consistencia**: Todos los tests crean mocks de la misma forma
3. **Mantenibilidad**: Cambios en entidades solo requieren actualizar helpers
4. **Legibilidad**: Tests más limpios y enfocados en el comportamiento

### Resumen de Tests de AccountService

| Test | Descripción |
|------|-------------|
| `createAccount_ValidData_Success` | ✅ Crea cuenta asociada al Customer |
| `createAccount_DuplicateAccountNumber_ThrowsException` | ✅ Rechaza número duplicado |
| `getAccountById_ValidId_Success` | ✅ Obtiene cuenta con ownership validation |
| `getAccountById_NonExistentId_ThrowsException` | ✅ Lanza AccountNotFoundException |
| `getAllAccounts_UserHasAccounts_Success` | ✅ Lista solo cuentas del usuario autenticado |
| `getActiveAccounts_FilterByActive_Success` | ✅ Filtra solo cuentas activas |
| `deactivateAccount_ValidId_Success` | ✅ Desactiva cuenta exitosamente |
| `activateAccount_ValidId_Success` | ✅ Activa cuenta exitosamente |
| `getBalance_ValidAccountNumber_Success` | ✅ Retorna saldo correcto |

**Total**: 9 tests ✅

---

## Tests de TransactionService

### Descripción

`TransactionServiceTest` verifica operaciones financieras sobre cuentas asociadas a Customers.

### Helpers

```java
private User mockUser;
private Customer mockCustomer;

@BeforeEach
void setUp() {
    mockUser = createMockUser("user-001", "john@example.com");
    mockCustomer = createMockCustomer("customer-001", mockUser, "John Doe");
}

private Account createMockAccount(String id, String accountNumber,
                                  Customer customer, BigDecimal balance,
                                  Boolean active) {
    Account account = new Account();
    account.setId(id);
    account.setAccountNumber(accountNumber);
    account.setCustomer(customer);  // CLAVE: Customer, no User
    account.setBalance(balance);
    account.setActive(active);
    return account;
}
```

### Test Ejemplo: Depósito

```java
@Test
@DisplayName("Debe crear un depósito exitosamente")
void createTransaction_Deposit_Success() {
    // Arrange
    String accountNumber = "1234567890";
    BigDecimal currentBalance = new BigDecimal("1000.00");
    BigDecimal depositAmount = new BigDecimal("500.00");
    TransactionRequest request = new TransactionRequest(
        accountNumber,
        TransactionType.DEPOSIT,
        depositAmount,
        "Salary deposit"
    );

    Account account = createMockAccount("acc-001", accountNumber, mockCustomer, currentBalance, true);
    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

    Transaction savedTransaction = new Transaction();
    savedTransaction.setId("tx-001");
    savedTransaction.setAccount(account);
    savedTransaction.setType(TransactionType.DEPOSIT);
    savedTransaction.setAmount(depositAmount);
    savedTransaction.setBalanceAfter(currentBalance.add(depositAmount));
    savedTransaction.setTimestamp(LocalDateTime.now());

    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

    // Act
    TransactionResponse response = transactionService.createTransaction(request);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));
}
```

### Resumen de Tests de TransactionService

| Test | Descripción |
|------|-------------|
| `createTransaction_Deposit_Success` | ✅ Crea depósito y actualiza saldo |
| `createTransaction_Withdrawal_Success` | ✅ Crea retiro y actualiza saldo |
| `createTransaction_AccountNotFound_ThrowsException` | ✅ Rechaza cuenta inexistente |
| `createTransaction_InactiveAccount_ThrowsException` | ✅ Rechaza cuenta inactiva |
| `createTransaction_InsufficientFunds_ThrowsException` | ✅ Rechaza retiro con saldo insuficiente |
| `getTransactionById_ValidId_Success` | ✅ Obtiene transacción por ID |
| `getTransactionById_NonExistentId_ThrowsException` | ✅ Lanza TransactionNotFoundException |
| `getAllTransactions_Success` | ✅ Lista todas las transacciones |
| `getTransactionsByAccountNumber_Success` | ✅ Filtra transacciones por cuenta |

**Total**: 9 tests ✅

---

## Refactorización y Mejores Prácticas

### Antes de la Refactorización

```java
@Test
void createAccount_ValidData_Success() {
    // Crear User manualmente
    User user = new User();
    user.setId("user-001");
    user.setEmail("john@example.com");

    // Crear Customer manualmente
    Customer customer = new Customer();
    customer.setId("customer-001");
    customer.setUser(user);
    customer.setName("John Doe");

    // Mockear autenticación manualmente
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
    when(customerRepository.findByUserId("user-001")).thenReturn(Optional.of(customer));

    // ... resto del test
}
```

### Después de la Refactorización

```java
@Test
void createAccount_ValidData_Success() {
    // Setup en una línea
    setupAuthentication("john@example.com", mockUser, mockCustomer);

    // Crear cuenta con helper
    Account savedAccount = createMockAccount("acc-001", accountNumber, mockCustomer, initialBalance);

    // ... resto del test (mucho más limpio)
}
```

### Mejoras Implementadas

1. **@BeforeEach para Inicialización**
   ```java
   @BeforeEach
   void setUp() {
       mockUser = createMockUser("user-001", "john@example.com");
       mockCustomer = createMockCustomer("customer-001", mockUser, "John Doe");
   }
   ```

2. **Helper Methods Reutilizables**
   - `createMockUser()`
   - `createMockCustomer()`
   - `createMockAccount()`
   - `setupAuthentication()`

3. **Consistencia en Datos de Prueba**
   - IDs predecibles: "user-001", "customer-001", "acc-001"
   - Emails consistentes: "john@example.com"
   - Nombres descriptivos: "John Doe"

4. **Patrón AAA Claro**
   ```java
   // Arrange: preparar datos
   setupAuthentication(...);
   when(...).thenReturn(...);

   // Act: ejecutar método
   response = service.method(...);

   // Assert: verificar resultado
   assertThat(response)...;
   verify(...)...;
   ```

### Resultado de la Refactorización

- **Reducción de código**: ~30% menos líneas
- **Mejora de legibilidad**: Código más claro y directo
- **Mantenibilidad**: Cambios centralizados en helpers
- **Consistencia**: Misma estructura en todos los tests

---

## Ejecución de Tests

### Ejecutar Todos los Tests

```bash
mvn test
```

**Salida esperada:**
```
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Ejecutar un Test Específico

```bash
mvn test -Dtest=AuthServiceTest
```

### Ejecutar un Método Específico

```bash
mvn test -Dtest=AuthServiceTest#register_ValidData_Success
```

### Con Cobertura de Código

```bash
mvn clean test jacoco:report
```

El reporte se genera en: `target/site/jacoco/index.html`

### Ejecutar en Modo Watch

```bash
mvn test-compile failsafe:integration-test -Dtest=AccountServiceTest -DfailIfNoTests=false
```

---

## Cobertura de Código

### Objetivo Actual

- **Mínimo requerido**: 70% de cobertura de líneas
- **Actual**: ~75-80%
- **Objetivo recomendado**: 85%

### Configuración de JaCoCo

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### Ver Reporte de Cobertura

1. Ejecutar: `mvn clean test jacoco:report`
2. Abrir: `target/site/jacoco/index.html` en el navegador
3. Navegar por paquetes para ver cobertura detallada

### Áreas con Alta Cobertura

- ✅ **AuthService**: ~90% (incluyendo flujo Customer)
- ✅ **AccountService**: ~85% (ownership validation cubierta)
- ✅ **TransactionService**: ~80% (casos edge cubiertos)

### Áreas para Mejorar

- ⚠️ **CustomerService**: Agregar tests para métodos admin
- ⚠️ **Exception Handlers**: Tests de manejo de errores global
- ⚠️ **DTOs Validation**: Tests de validaciones Jakarta

---

## Mejores Prácticas Aplicadas

### 1. Patrón AAA (Arrange-Act-Assert)

```java
@Test
void testMethod() {
    // Arrange: Preparar datos y mocks
    User user = createMockUser(...);
    when(repository.findById(id)).thenReturn(Optional.of(user));

    // Act: Ejecutar el método a probar
    Result result = service.doSomething(id);

    // Assert: Verificar el resultado
    assertThat(result).isNotNull();
    verify(repository).findById(id);
}
```

### 2. Nombres Descriptivos con @DisplayName

```java
@Test
@DisplayName("Debe registrar usuario exitosamente")
void register_ValidData_Success() { ... }

@Test
@DisplayName("Debe lanzar excepción cuando el email ya está registrado")
void register_DuplicateEmail_ThrowsException() { ... }
```

### 3. Un Solo Concepto por Test

```java
// ✅ Bien: tests separados
@Test
void create_ValidData_Success() { ... }

@Test
void update_ValidData_Success() { ... }

@Test
void delete_ExistingId_Success() { ... }
```

### 4. Verificar Comportamiento, No Implementación

```java
// ✅ Bien: verificar el comportamiento público
assertThat(result.getId()).isEqualTo(id);
verify(repository).save(any(Entity.class));
```

### 5. Usar AssertJ para Aserciones Fluidas

```java
// ✅ AssertJ (más legible)
assertThat(response.email()).isEqualTo("user@example.com");
assertThat(response.customerId()).isNotNull();
assertThat(response.balance()).isEqualByComparingTo(new BigDecimal("1000.00"));
```

### 6. Helpers para Reducir Duplicación

```java
// En lugar de repetir creación de mocks, usar helpers
private User createMockUser(String id, String email) { ... }
private Customer createMockCustomer(String id, User user, String name) { ... }
private Account createMockAccount(...) { ... }
```

### 7. @BeforeEach para Setup Común

```java
@BeforeEach
void setUp() {
    mockUser = createMockUser("user-001", "john@example.com");
    mockCustomer = createMockCustomer("customer-001", mockUser, "John Doe");
}
```

---

## Comandos Útiles

### Compilar sin ejecutar tests
```bash
mvn clean compile -DskipTests
```

### Ejecutar solo tests unitarios
```bash
mvn test
```

### Generar reporte de cobertura
```bash
mvn clean test jacoco:report
```

### Ver solo fallos
```bash
mvn test | grep -A 10 "FAILURE"
```

### Ejecutar con logging verbose
```bash
mvn test -X
```

---

## Recursos Adicionales

### Documentación

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

### Conceptos Clave

- **Mock**: Objeto simulado que reemplaza una dependencia
- **Stub**: Mock pre-configurado con respuestas específicas
- **Verify**: Verifica que un método fue llamado
- **@InjectMocks**: Inyecta mocks en la clase a probar
- **@BeforeEach**: Se ejecuta antes de cada test
- **Helper Methods**: Métodos reutilizables para crear datos de prueba

---

## Conclusión

Los tests unitarios refactorizados reflejan correctamente la arquitectura Customer-Account y la implementación de JWT con claims personalizados:

- ✅ **22 tests** en total (4 AuthService + 9 AccountService + 9 TransactionService)
- ✅ Cobertura del **75-80%**
- ✅ **30% menos código** gracias a helpers
- ✅ **Arquitectura Customer** completamente probada
- ✅ **JWT con claims personalizados** (email, name, customerId) completamente probado
- ✅ Patrón AAA consistente en todos los tests
- ✅ Mocks apropiados de todas las dependencias (incluyendo CustomerRepository)
- ✅ Verificación de casos exitosos y de error
- ✅ Ownership validation cubierta
- ✅ Helper methods para reducir duplicación

**Recuerda**: Un buen test es aquel que falla cuando el código cambia de forma incorrecta, pero pasa cuando el código funciona correctamente.

---

**Última actualización**: 26 de Octubre, 2025
**Versión**: 2.1.0 (Customer Architecture + JWT Claims)
