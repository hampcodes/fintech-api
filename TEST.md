# Documentación de Testing - API Fintech

## Índice

1. [¿Qué son los Tests?](#qué-son-los-tests)
   - Tipos de Tests
   - Pirámide de Tests

2. [Importancia del Testing](#importancia-del-testing)
   - Confianza en los Cambios
   - Desarrollo Más Rápido
   - Mejor Diseño

3. [Metodología de Testing](#metodología-de-testing)
   - Test-Driven Development (TDD)
   - Ciclo Rojo-Verde-Refactor

4. [Patrón Given-When-Then](#patrón-given-when-then)
   - Estructura del Patrón
   - Ejemplos Prácticos

5. [Convención de Nombres de Tests](#convención-de-nombres-de-tests)
   - Formato: `metodoProbado_escenario_resultadoEsperado`
   - Beneficios del Patrón

6. [Estructura de una Clase de Test](#estructura-de-una-clase-de-test)
   - Anotaciones y Configuración
   - Componentes Principales

7. [Relación con Historias de Usuario](#relación-con-historias-de-usuario)
   - Estructura y Formato
   - Ejemplo Completo: HU-001
   - Casos de Prueba Derivados
   - Formato de la Tabla de Caso de Prueba

8. [Importancia de los Casos de Prueba](#importancia-de-los-casos-de-prueba)
   - ¿Por qué son importantes?
   - Diferencias Clave (HU vs CA vs CP vs Test)
   - Ejemplo de Flujo Completo

9. [Product Backlog](#product-backlog)
   - HU-001: Crear Cuenta Bancaria
   - HU-002: Realizar Depósito
   - HU-003: Realizar Retiro
   - HU-004: Consultar Cuenta por ID
   - HU-005: Consultar Cuenta por Número
   - HU-006: Listar Todas las Cuentas
   - HU-007: Listar Cuentas Activas
   - HU-008: Desactivar Cuenta
   - HU-009: Activar Cuenta
   - HU-010: Consultar Saldo
   - HU-011: Consultar Transacción por ID
   - HU-012: Listar Todas las Transacciones
   - HU-013: Listar Transacciones por Cuenta

10. [Ejemplos Prácticos](#ejemplos-prácticos)
    - Tests Unitarios de AccountService (10 tests)
    - Tests Unitarios de TransactionService (10 tests)
    - Tests de Integración de AccountIntegrationTest (11 tests)

11. [Ejecutar Tests](#ejecutar-tests)
    - Comandos Maven
    - Resultados Esperados

---

## ¿Qué son los Tests?

Los **tests** son código automatizado que verifica el correcto funcionamiento de tu aplicación. Actúan como:
- **Guardianes de Calidad**: Aseguran que el código funcione como se espera
- **Documentación**: Ejemplos vivos de cómo deben comportarse los componentes
- **Red de Seguridad**: Detectan regresiones al hacer cambios
- **Herramienta de Diseño**: Impulsan mejor arquitectura a través de la testabilidad

### Tipos de Tests

```
┌─────────────────────────────────────────┐
│         Pirámide de Tests                │
├─────────────────────────────────────────┤
│              Tests E2E                   │  ← Pocos, lentos, costosos
│           ╱           ╲                  │
│     Tests Integración                    │  ← Algunos, velocidad moderada
│    ╱                  ╲                  │
│   Tests Unitarios                        │  ← Muchos, rápidos, económicos
└─────────────────────────────────────────┘
```

**En este proyecto:**
- ✅ 20 Tests Unitarios (AccountService + TransactionService)
- ✅ 11 Tests de Integración (API endpoints)
- ✅ Total: 31 tests automatizados

---

## Importancia del Testing

### 1. **Confianza en los Cambios**
Los tests te permiten refactorizar y agregar funcionalidades sin miedo a romper lo existente.

**Sin Tests:**
```
Desarrollador: "¡Arreglé la función de depósitos!"
Producción: *La función de retiros deja de funcionar*
Resultado: Quejas de clientes, dinero perdido, corrección de emergencia
```

**Con Tests:**
```
Desarrollador: "¡Arreglé la función de depósitos!"
Tests: *Los tests de retiros fallan inmediatamente*
Desarrollador: "Ups, déjame arreglar eso antes de desplegar"
Resultado: Sin problemas en producción, clientes felices
```

### 2. **Desarrollo Más Rápido**
- Testing manual: 10 minutos por función × 50 funciones = 500 minutos
- Testing automatizado: 2 segundos × 50 funciones = 100 segundos

### 3. **Mejor Diseño**
El código fácil de testear suele estar bien diseñado:
- Bajo acoplamiento
- Alta cohesión
- Responsabilidades claras

---

## Metodología de Testing

### Test-Driven Development (TDD) - Rojo-Verde-Refactor

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│   ROJO   │  →   │  VERDE   │  →   │ REFACTOR │
│ (Escribir│      │ (Hacer   │      │ (Mejorar │
│  test que│      │  pasar el│      │  calidad │
│  falla)  │      │  test)   │      │  código) │
└──────────┘      └──────────┘      └──────────┘
      ↑                                   │
      └───────────────────────────────────┘
```

**Ciclo en Práctica:**

1. **Rojo**: Escribir un test que falla
2. **Verde**: Escribir el código mínimo para que pase
3. **Refactor**: Mejorar el código sin cambiar funcionalidad
4. Repetir

---

## Patrón Given-When-Then

El patrón **Given-When-Then (GWT)** es un formato de especificación conductual que hace que los tests sean legibles y comprensibles.

### Estructura

```
GIVEN [Contexto Inicial/Precondiciones]
WHEN  [Acción/Evento que ocurre]
THEN  [Resultado Esperado]
```

### Explicación Detallada de Cada Fase

#### **GIVEN** - Fase de Preparación (Arrange)

**¿Qué es?**
El estado inicial del sistema ANTES de ejecutar la acción que queremos probar.

**¿Qué se hace aquí?**
- ✅ Crear objetos de prueba (test fixtures)
- ✅ Configurar mocks y stubs
- ✅ Establecer el estado de la base de datos
- ✅ Preparar datos de entrada
- ✅ Definir comportamientos esperados de dependencias

**¿Qué NO va en GIVEN?**
- ❌ Llamadas al método que se está probando
- ❌ Verificaciones o assertions
- ❌ Lógica de negocio

**Ejemplo:**
```java
// GIVEN - Preparamos una cuenta activa con $1000
Account account = new Account("1234567890", "Juan Pérez",
    "juan@example.com", new BigDecimal("1000.00"));
account.setActive(true);

// Configuramos el mock para retornar esta cuenta
given(accountRepository.findByAccountNumber("1234567890"))
    .willReturn(Optional.of(account));
```

---

#### **WHEN** - Fase de Ejecución (Act)

**¿Qué es?**
La acción o comportamiento que estamos probando. Es el "corazón" del test.

**¿Qué se hace aquí?**
- ✅ Llamar al método que queremos probar
- ✅ Ejecutar la acción con los datos preparados en GIVEN
- ✅ Capturar el resultado o la excepción

**¿Qué NO va en WHEN?**
- ❌ Preparación de datos (eso va en GIVEN)
- ❌ Múltiples acciones (un test = una acción)
- ❌ Verificaciones (eso va en THEN)

**Características:**
- Debe ser **UNA sola acción**
- Debe ser **clara y concisa**
- Representa el **comportamiento bajo prueba**

**Ejemplo:**
```java
// WHEN - Ejecutamos un depósito de $500
TransactionResponse response = transactionService.createTransaction(
    new TransactionRequest("1234567890", TransactionType.DEPOSIT,
        new BigDecimal("500.00"), "Depósito de nómina")
);
```

---

#### **THEN** - Fase de Verificación (Assert)

**¿Qué es?**
La verificación de que el resultado es el esperado. Aquí confirmamos que todo funcionó correctamente.

**¿Qué se hace aquí?**
- ✅ Verificar el valor de retorno
- ✅ Verificar el estado de los objetos
- ✅ Verificar que se llamaron los métodos correctos
- ✅ Verificar excepciones esperadas
- ✅ Verificar efectos secundarios

**¿Qué NO va en THEN?**
- ❌ Lógica de negocio
- ❌ Nuevas acciones
- ❌ Preparación de datos

**Ejemplo:**
```java
// THEN - Verificamos que la transacción fue exitosa
assertThat(response).isNotNull();
assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));

// Verificamos que se guardó en el repositorio
then(transactionRepository).should(times(1)).save(any(Transaction.class));
```

---

## Convención de Nombres de Tests

### Patrón: `metodoProbado_escenario_resultadoEsperado`

Este patrón hace que los nombres de los tests sean **autodocumentados** y fáciles de entender.

**Estructura:**
```
nombreMetodo_contextoOCondicion_comportamientoEsperado()
     ↓              ↓                    ↓
  Lo que         Bajo qué            Qué debe
  probamos       circunstancia       ocurrir
```

**Beneficios:**
- ✅ Autodocumentación: El nombre explica qué hace el test
- ✅ Claridad: Fácil de entender sin leer el código
- ✅ Mantenibilidad: Cuando falla, sabes exactamente qué está roto
- ✅ Consistencia: Todos los tests siguen el mismo patrón

**Ejemplos:**

| Nombre del Test | Significado |
|----------------|-------------|
| `createAccount_ValidData_Success()` | Al crear cuenta con datos válidos, debe ser exitoso |
| `createAccount_DuplicateNumber_ThrowsException()` | Al crear cuenta con número duplicado, debe lanzar excepción |
| `createTransaction_Deposit_Success()` | Al crear transacción de depósito, debe ser exitoso |
| `createTransaction_Withdraw_ThrowsException_WhenInsufficientBalance()` | Al crear retiro con saldo insuficiente, debe lanzar excepción |
| `getAccountById_NonExistingId_ReturnsNotFound()` | Al obtener cuenta por ID inexistente, debe retornar NotFound |

**Casos Especiales:**

Para tests de integración que verifican respuestas HTTP:
```java
createAccount_ValidData_ReturnsCreated()     // Retorna 201 Created
getAccountById_ExistingId_ReturnsOk()        // Retorna 200 OK
getAccountById_NonExistingId_ReturnsNotFound() // Retorna 404 Not Found
createAccount_InvalidEmail_ReturnsBadRequest() // Retorna 400 Bad Request
```

---

## Estructura de una Clase de Test

Una clase de test bien organizada tiene estas secciones claramente definidas:

### Anatomía Completa de una Clase de Test

A continuación se muestra la estructura completa con explicación de cada parte:

```java
// 1. ANOTACIONES DE CLASE
@ExtendWith(MockitoExtension.class)          // Framework de testing
@DisplayName("AccountService - Pruebas Unitarias")  // Nombre descriptivo en español
class AccountServiceTest {

    // 2. DEPENDENCIAS (Mocks e InjectMocks)
    @Mock
    private AccountRepository accountRepository;  // Dependencia simulada

    @InjectMocks
    private AccountService accountService;        // Clase bajo prueba

    // 3. FIXTURES (Datos de prueba reutilizables)
    private Account testAccount;
    private AccountRequest testRequest;

    // 4. CONFIGURACIÓN INICIAL (Se ejecuta antes de cada test)
    @BeforeEach
    void setUp() {
        // Preparar datos comunes para todos los tests
        testAccount = new Account("1234567890", "John Doe",
                "john@example.com", new BigDecimal("1000.00"));
        testAccount.setId("acc-001");

        testRequest = new AccountRequest(
                "1234567890", "John Doe",
                "john@example.com", new BigDecimal("1000.00")
        );
    }

    // 5. TESTS ORGANIZADOS POR FUNCIONALIDAD

    // Tests de creación de cuenta
    @Test
    @DisplayName("Debe crear cuenta exitosamente cuando el número no existe")
    void createAccount_ValidData_Success() {
        // GIVEN - Preparación
        given(accountRepository.existsByAccountNumber("1234567890"))
            .willReturn(false);
        given(accountRepository.save(any(Account.class)))
            .willReturn(testAccount);

        // WHEN - Ejecución
        AccountResponse response = accountService.createAccount(testRequest);

        // THEN - Verificación
        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("1234567890");

        then(accountRepository).should(times(1))
            .existsByAccountNumber("1234567890");
        then(accountRepository).should(times(1))
            .save(any(Account.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el número de cuenta ya existe")
    void createAccount_DuplicateNumber_ThrowsException() {
        // GIVEN
        given(accountRepository.existsByAccountNumber("1234567890"))
            .willReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> accountService.createAccount(testRequest))
                .isInstanceOf(DuplicateAccountException.class)
                .hasMessageContaining("already exists");

        then(accountRepository).should(times(0))
            .save(any(Account.class));
    }

    // Tests de consulta de cuenta
    @Test
    @DisplayName("Debe obtener cuenta por ID exitosamente")
    void getAccountById_ExistingId_Success() {
        // GIVEN
        given(accountRepository.findById("acc-001"))
            .willReturn(Optional.of(testAccount));

        // WHEN
        AccountResponse response = accountService.getAccountById("acc-001");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("acc-001");
    }
}
```

### Explicación de Cada Sección

#### 1. **Anotaciones de Clase**

Define el framework de testing y la descripción del conjunto de tests.

```java
@ExtendWith(MockitoExtension.class)  // Habilita Mockito para crear mocks
@DisplayName("AccountService - Pruebas Unitarias")  // Descripción en español
```

#### 2. **Dependencias (Mocks e InjectMocks)**

Declara las dependencias que necesita la clase bajo prueba.

```java
@Mock
private AccountRepository accountRepository;  // Simula el repositorio

@InjectMocks
private AccountService accountService;  // Inyecta los mocks automáticamente
```

**¿Por qué usar @Mock?**
- Simula dependencias sin necesitar implementaciones reales
- Permite definir comportamientos específicos para cada test
- Aísla la clase bajo prueba

#### 3. **Fixtures (Datos de Prueba)**

Objetos reutilizables en múltiples tests.

```java
private Account testAccount;
private AccountRequest testRequest;
```

#### 4. **Configuración Inicial (@BeforeEach)**

Se ejecuta antes de cada test para preparar el estado inicial.

```java
@BeforeEach
void setUp() {
    testAccount = new Account("1234567890", "John Doe",
            "john@example.com", new BigDecimal("1000.00"));
    testAccount.setId("acc-001");
}
```

**¿Por qué usar @BeforeEach?**
- Evita duplicación de código
- Garantiza que cada test comienza con un estado limpio
- Hace los tests más legibles

#### 5. **Tests Organizados**

Los tests se agrupan lógicamente por funcionalidad.

**Orden recomendado:**
1. Tests de casos exitosos (happy path)
2. Tests de validaciones
3. Tests de excepciones
4. Tests de casos límite

---

## Relación con Historias de Usuario

Los tests están directamente relacionados con las **Historias de Usuario**, sus **Criterios de Aceptación** y los **Casos de Prueba** derivados de estos.

### Estructura de una Historia de Usuario

```
Historia de Usuario: [Título]

Como: [Rol del usuario]
Quiero: [Funcionalidad deseada]
Para: [Beneficio o valor]

Criterios de Aceptación:
  Escenario 1: [Nombre del escenario exitoso]
    DADO [contexto inicial]
    CUANDO [acción ejecutada]
    ENTONCES [resultado esperado]

  Escenario 2: [Nombre del escenario alternativo]
    DADO [contexto inicial]
    CUANDO [acción ejecutada]
    ENTONCES [resultado esperado]
```

---

## Ejemplo Completo: Historia de Usuario

### HU-001: Crear Cuenta Bancaria

```
Como: Cliente del banco
Quiero: Poder crear una cuenta bancaria
Para: Empezar a realizar transacciones financieras

Criterios de Aceptación:

  Escenario 1: Creación exitosa de cuenta
    DADO que soy un nuevo cliente
    Y proporciono número de cuenta único de 10-20 dígitos
    Y proporciono email válido
    Y proporciono saldo inicial >= 0
    CUANDO solicito crear la cuenta
    ENTONCES la cuenta se crea exitosamente
    Y la cuenta está activa por defecto
    Y recibo confirmación con los datos de la cuenta

  Escenario 2: Rechazo por número de cuenta duplicado
    DADO que existe una cuenta con número "1234567890"
    CUANDO intento crear otra cuenta con el mismo número "1234567890"
    ENTONCES la operación es rechazada
    Y recibo mensaje "Account with number 1234567890 already exists"

  Escenario 3: Rechazo por email inválido
    DADO que proporciono un email sin formato válido
    CUANDO intento crear la cuenta
    ENTONCES la operación es rechazada
    Y recibo mensaje de error de validación

  Escenario 4: Rechazo por saldo inicial negativo
    DADO que proporciono saldo inicial negativo
    CUANDO intento crear la cuenta
    ENTONCES la operación es rechazada
    Y recibo mensaje de error de validación
```

### Casos de Prueba Derivados

Los **Casos de Prueba** son la especificación técnica de cómo verificar cada Criterio de Aceptación.

#### Formato de la Tabla de Caso de Prueba

Cada Caso de Prueba se documenta en una tabla estructurada con los siguientes campos:

| Campo | Descripción | Propósito |
|-------|-------------|-----------|
| **ID** | Identificador único (CP-XXX) | Trazabilidad y referencia rápida |
| **Historia** | Historia de Usuario relacionada (HU-XXX) | Vincula el CP con el requisito de negocio |
| **Escenario** | Nombre del escenario que se prueba | Indica qué caso específico se verifica |
| **Precondiciones** | Estado del sistema antes de ejecutar | Define el contexto necesario para la prueba |
| **Datos de Prueba** | Valores específicos a utilizar | Inputs concretos para reproducir el test |
| **Pasos** | Secuencia de acciones a ejecutar | Procedimiento paso a paso para realizar la prueba |
| **Resultado Esperado** | Comportamiento esperado del sistema | Define el criterio de éxito/fallo del test |
| **Tests Asociados** | Tests automatizados que implementan este CP | Trazabilidad con el código de tests |

**Ejemplo de uso:**
- **ID**: Permite referenciar el caso en conversaciones ("Revisa CP-005")
- **Historia**: Conecta con el valor de negocio (HU-001 → CP-001, CP-002, etc.)
- **Precondiciones**: Asegura que el test se ejecuta en el contexto correcto
- **Datos de Prueba**: Valores exactos para reproducibilidad
- **Resultado Esperado**: Criterio claro de pass/fail

---

#### CP-001: Verificar creación exitosa de cuenta

| Campo | Valor |
|-------|-------|
| **ID** | CP-001 |
| **Historia** | HU-001 |
| **Escenario** | Creación exitosa |
| **Precondiciones** | - Base de datos limpia<br>- No existe cuenta con número "1234567890" |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- ownerName: "John Doe"<br>- ownerEmail: "john@example.com"<br>- initialBalance: 1000.00 |
| **Pasos** | 1. Preparar request con datos válidos<br>2. Ejecutar createAccount()<br>3. Verificar respuesta |
| **Resultado Esperado** | - Cuenta creada con ID único<br>- active = true<br>- balance = 1000.00<br>- HTTP 201 Created |
| **Tests Asociados** | - `createAccount_ValidData_Success()` (Unitario)<br>- `createAccount_ValidData_ReturnsCreated()` (Integración) |

#### CP-002: Verificar rechazo por número duplicado

| Campo | Valor |
|-------|-------|
| **ID** | CP-002 |
| **Historia** | HU-001 |
| **Escenario** | Rechazo por duplicado |
| **Precondiciones** | - Existe cuenta con número "1234567890" |
| **Datos de Prueba** | - accountNumber: "1234567890" (duplicado) |
| **Pasos** | 1. Crear primera cuenta<br>2. Intentar crear segunda con mismo número<br>3. Verificar excepción |
| **Resultado Esperado** | - DuplicateAccountException lanzada<br>- Mensaje: "already exists"<br>- HTTP 409 Conflict<br>- Segunda cuenta NO guardada |
| **Tests Asociados** | - `createAccount_DuplicateNumber_ThrowsException()` (Unitario)<br>- `createAccount_DuplicateNumber_ReturnsConflict()` (Integración) |

#### CP-003: Verificar rechazo por email inválido

| Campo | Valor |
|-------|-------|
| **ID** | CP-003 |
| **Historia** | HU-001 |
| **Escenario** | Rechazo por email inválido |
| **Precondiciones** | - Ninguna |
| **Datos de Prueba** | - ownerEmail: "invalid-email" (sin @) |
| **Pasos** | 1. Preparar request con email inválido<br>2. Ejecutar createAccount()<br>3. Verificar error de validación |
| **Resultado Esperado** | - ValidationException<br>- HTTP 400 Bad Request<br>- Cuenta NO creada |
| **Tests Asociados** | - `createAccount_InvalidEmail_ReturnsBadRequest()` (Integración) |

#### CP-004: Verificar rechazo por saldo negativo

| Campo | Valor |
|-------|-------|
| **ID** | CP-004 |
| **Historia** | HU-001 |
| **Escenario** | Rechazo por saldo negativo |
| **Precondiciones** | - Ninguna |
| **Datos de Prueba** | - initialBalance: -100.00 |
| **Pasos** | 1. Preparar request con saldo negativo<br>2. Ejecutar createAccount()<br>3. Verificar error de validación |
| **Resultado Esperado** | - ValidationException<br>- HTTP 400 Bad Request<br>- Cuenta NO creada |
| **Tests Asociados** | - `createAccount_NegativeBalance_ReturnsBadRequest()` (Integración) |

---

## Importancia de los Casos de Prueba

### ¿Por qué son importantes los Casos de Prueba?

1. **Puente entre Negocio y Técnica**
   - Traducen requisitos de negocio en especificaciones técnicas ejecutables
   - Permiten que QA, Developers y PO hablen el mismo idioma

2. **Trazabilidad Completa**
   ```
   Requisito de Negocio
         ↓
   Historia de Usuario (HU)
         ↓
   Criterios de Aceptación (CA) - Formato Dado/Cuando/Entonces
         ↓
   Casos de Prueba (CP) - Especificación técnica
         ↓
   Tests Automatizados - Código ejecutable
         ↓
   Código de Producción
   ```

3. **Cobertura Verificable**
   - Cada CA debe tener al menos un CP
   - Cada CP debe tener al menos un test automatizado
   - Sin CPs, no hay forma de verificar que se cumple el CA

4. **Documentación Viva**
   - Los CPs documentan CÓMO verificar cada funcionalidad
   - Sirven como especificación para escribir tests
   - Son la referencia para testing manual cuando sea necesario

5. **Prevención de Defectos**
   - Pensar en los CPs antes de codificar revela edge cases
   - Identifica escenarios alternativos tempranamente
   - Reduce bugs en producción

### Diferencias Clave

| Concepto | Propósito | Audiencia | Formato |
|----------|-----------|-----------|---------|
| **Historia de Usuario** | Describir QUIÉN, QUÉ y POR QUÉ | Product Owner, Stakeholders | Narrativa de negocio |
| **Criterio de Aceptación** | Definir CUÁNDO está completo | PO, QA, Developers | Dado/Cuando/Entonces |
| **Caso de Prueba** | Especificar CÓMO verificar | QA, Developers | Tabla técnica con pasos |
| **Test Automatizado** | Ejecutar verificación | Sistema, CI/CD | Código (Java, etc.) |

### Ejemplo de Flujo Completo

**Requisito de Negocio:**
> "Los clientes deben poder depositar dinero en sus cuentas"

**↓ Se convierte en:**

**Historia de Usuario (HU-002):**
```
Como: Cliente del banco
Quiero: Depositar dinero en mi cuenta
Para: Aumentar mi saldo disponible
```

**↓ Se especifica con:**

**Criterio de Aceptación:**
```
Escenario: Depósito exitoso
  DADO que tengo una cuenta activa con saldo de $1000
  CUANDO deposito $500
  ENTONCES mi saldo aumenta a $1500
  Y recibo confirmación de la transacción
```

**↓ Se implementa como:**

**Caso de Prueba (CP-005):**
| Campo | Valor |
|-------|-------|
| **Datos de Prueba** | saldo inicial: 1000, monto: 500 |
| **Resultado Esperado** | saldo final: 1500 |
| **Tests Asociados** | createTransaction_Deposit_Success() |

**↓ Se automatiza con:**

**Test Automatizado:**
```java
void createTransaction_Deposit_Success() {
    // Implementación del test...
}
```

---

## Product Backlog

El **Product Backlog** es el listado priorizado de todas las Historias de Usuario que componen la funcionalidad del sistema. Cada historia representa valor de negocio y está vinculada a sus Criterios de Aceptación.

### HU-001: Crear Cuenta Bancaria

| Campo | Detalle |
|-------|---------|
| **ID** | HU-001 |
| **Título** | Crear Cuenta Bancaria |
| **Descripción** | **Como** cliente del banco<br>**Quiero** crear una cuenta bancaria con mis datos personales<br>**Para** poder empezar a realizar transacciones financieras |
| **Criterios de Aceptación** | **CA1 - Creación exitosa:**<br>DADO que soy un nuevo cliente<br>Y proporciono número de cuenta único de 10-20 dígitos<br>Y proporciono email válido<br>Y proporciono saldo inicial >= 0<br>CUANDO solicito crear la cuenta<br>ENTONCES la cuenta se crea exitosamente<br>Y la cuenta está activa por defecto<br><br>**CA2 - Rechazo por número duplicado:**<br>DADO que existe una cuenta con número "1234567890"<br>CUANDO intento crear otra cuenta con el mismo número<br>ENTONCES la operación es rechazada<br>Y recibo mensaje "already exists"<br><br>**CA3 - Rechazo por email inválido:**<br>DADO que proporciono un email sin formato válido<br>CUANDO intento crear la cuenta<br>ENTONCES la operación es rechazada<br>Y recibo mensaje de error de validación<br><br>**CA4 - Rechazo por saldo negativo:**<br>DADO que proporciono saldo inicial negativo<br>CUANDO intento crear la cuenta<br>ENTONCES la operación es rechazada<br>Y recibo mensaje de error de validación |
| **Prioridad** | Alta |
| **Estimación** | 5 Story Points |
| **Estado** | ✅ Completado |

---

### HU-002: Realizar Depósito

| Campo | Detalle |
|-------|---------|
| **ID** | HU-002 |
| **Título** | Realizar Depósito en Cuenta |
| **Descripción** | **Como** cliente del banco<br>**Quiero** depositar dinero en mi cuenta bancaria<br>**Para** incrementar mi saldo disponible |
| **Criterios de Aceptación** | **CA1 - Depósito exitoso:**<br>DADO que tengo una cuenta activa con número "1234567890"<br>Y la cuenta tiene un saldo de $1000<br>CUANDO deposito $500<br>ENTONCES el saldo aumenta a $1500<br>Y se registra la transacción tipo DEPOSIT<br>Y recibo confirmación con el nuevo saldo<br><br>**CA2 - Rechazo por monto inválido:**<br>DADO que intento realizar un depósito<br>CUANDO el monto es cero o negativo<br>ENTONCES la operación es rechazada<br>Y recibo mensaje "Amount must be greater than zero"<br><br>**CA3 - Rechazo por cuenta inexistente:**<br>DADO que intento depositar en cuenta inexistente<br>CUANDO la cuenta no existe en el sistema<br>ENTONCES la operación es rechazada<br>Y recibo mensaje "Account not found"<br><br>**CA4 - Rechazo por cuenta inactiva:**<br>DADO que la cuenta está en estado inactivo<br>CUANDO intento realizar un depósito<br>ENTONCES la operación es rechazada<br>Y recibo mensaje sobre "inactive account" |
| **Prioridad** | Alta |
| **Estimación** | 5 Story Points |
| **Estado** | ✅ Completado |

---

### HU-003: Realizar Retiro

| Campo | Detalle |
|-------|---------|
| **ID** | HU-003 |
| **Título** | Realizar Retiro de Cuenta |
| **Descripción** | **Como** cliente del banco<br>**Quiero** retirar dinero de mi cuenta bancaria<br>**Para** disponer de efectivo o realizar pagos |
| **Criterios de Aceptación** | **CA1 - Retiro exitoso:**<br>DADO que tengo una cuenta activa con número "1234567890"<br>Y la cuenta tiene un saldo de $1000<br>CUANDO retiro $300<br>ENTONCES el saldo disminuye a $700<br>Y se registra la transacción tipo WITHDRAW<br>Y recibo confirmación con el nuevo saldo<br><br>**CA2 - Rechazo por saldo insuficiente:**<br>DADO que tengo una cuenta con saldo de $1000<br>CUANDO intento retirar $2000<br>ENTONCES la operación es rechazada<br>Y recibo mensaje "Insufficient balance"<br>Y el saldo permanece sin cambios |
| **Prioridad** | Alta |
| **Estimación** | 5 Story Points |
| **Estado** | ✅ Completado |

---

### HU-004: Consultar Cuenta por ID

| Campo | Detalle |
|-------|---------|
| **ID** | HU-004 |
| **Título** | Consultar Información de Cuenta por ID |
| **Descripción** | **Como** usuario del sistema<br>**Quiero** consultar una cuenta bancaria por su ID único<br>**Para** verificar sus datos y estado actual |
| **Criterios de Aceptación** | **CA1 - Consulta exitosa:**<br>DADO que existe una cuenta con ID "acc-001"<br>CUANDO consulto la cuenta por ese ID<br>ENTONCES obtengo los datos completos de la cuenta<br>Y los datos corresponden al ID solicitado<br><br>**CA2 - ID inexistente:**<br>DADO que NO existe una cuenta con ID "invalid-id"<br>CUANDO consulto la cuenta por ese ID<br>ENTONCES recibo un error AccountNotFoundException<br>Y el mensaje indica "not found" |
| **Prioridad** | Media |
| **Estimación** | 3 Story Points |
| **Estado** | ✅ Completado |

---

### HU-005: Consultar Cuenta por Número

| Campo | Detalle |
|-------|---------|
| **ID** | HU-005 |
| **Título** | Consultar Información de Cuenta por Número |
| **Descripción** | **Como** cliente del banco<br>**Quiero** consultar una cuenta bancaria por su número de cuenta<br>**Para** verificar información y realizar operaciones |
| **Criterios de Aceptación** | **CA1 - Consulta exitosa:**<br>DADO que existe una cuenta con número "1234567890"<br>CUANDO consulto la cuenta por ese número<br>ENTONCES obtengo los datos completos de la cuenta<br>Y el número coincide con el solicitado |
| **Prioridad** | Media |
| **Estimación** | 3 Story Points |
| **Estado** | ✅ Completado |

---

### HU-006: Listar Todas las Cuentas

| Campo | Detalle |
|-------|---------|
| **ID** | HU-006 |
| **Título** | Listar Todas las Cuentas Bancarias |
| **Descripción** | **Como** administrador del sistema<br>**Quiero** listar todas las cuentas bancarias registradas<br>**Para** tener una vista general del sistema |
| **Criterios de Aceptación** | **CA1 - Listar exitosamente:**<br>DADO que existen múltiples cuentas en el sistema<br>CUANDO solicito listar todas las cuentas<br>ENTONCES obtengo una lista con todas las cuentas<br>Y cada cuenta incluye sus datos completos |
| **Prioridad** | Media |
| **Estimación** | 2 Story Points |
| **Estado** | ✅ Completado |

---

### HU-007: Listar Cuentas Activas

| Campo | Detalle |
|-------|---------|
| **ID** | HU-007 |
| **Título** | Listar Solo Cuentas Activas |
| **Descripción** | **Como** operador del banco<br>**Quiero** listar solo las cuentas activas<br>**Para** procesar transacciones solo en cuentas operativas |
| **Criterios de Aceptación** | **CA1 - Filtrar activas:**<br>DADO que existen cuentas activas e inactivas en el sistema<br>CUANDO solicito listar solo las cuentas activas<br>ENTONCES obtengo únicamente las cuentas con estado activo<br>Y no se incluyen cuentas inactivas |
| **Prioridad** | Baja |
| **Estimación** | 2 Story Points |
| **Estado** | ✅ Completado |

---

### HU-008: Desactivar Cuenta

| Campo | Detalle |
|-------|---------|
| **ID** | HU-008 |
| **Título** | Desactivar Cuenta Bancaria |
| **Descripción** | **Como** administrador del banco<br>**Quiero** desactivar una cuenta bancaria<br>**Para** suspender operaciones temporalmente sin eliminar la cuenta |
| **Criterios de Aceptación** | **CA1 - Desactivación exitosa:**<br>DADO que existe una cuenta activa con ID "acc-001"<br>CUANDO solicito desactivar la cuenta<br>ENTONCES la cuenta cambia a estado inactivo<br>Y se persiste el cambio en la base de datos<br>Y recibo confirmación del cambio |
| **Prioridad** | Media |
| **Estimación** | 3 Story Points |
| **Estado** | ✅ Completado |

---

### HU-009: Activar Cuenta

| Campo | Detalle |
|-------|---------|
| **ID** | HU-009 |
| **Título** | Reactivar Cuenta Bancaria Inactiva |
| **Descripción** | **Como** administrador del banco<br>**Quiero** reactivar una cuenta bancaria inactiva<br>**Para** permitir que el cliente vuelva a operar normalmente |
| **Criterios de Aceptación** | **CA1 - Activación exitosa:**<br>DADO que existe una cuenta inactiva con ID "acc-001"<br>CUANDO solicito activar la cuenta<br>ENTONCES la cuenta cambia a estado activo<br>Y se persiste el cambio en la base de datos<br>Y recibo confirmación del cambio |
| **Prioridad** | Media |
| **Estimación** | 2 Story Points |
| **Estado** | ✅ Completado |

---

### HU-010: Consultar Saldo

| Campo | Detalle |
|-------|---------|
| **ID** | HU-010 |
| **Título** | Consultar Saldo de Cuenta |
| **Descripción** | **Como** cliente del banco<br>**Quiero** consultar el saldo de mi cuenta bancaria<br>**Para** conocer mi disponibilidad financiera |
| **Criterios de Aceptación** | **CA1 - Consulta exitosa:**<br>DADO que tengo una cuenta con número "1234567890"<br>Y la cuenta tiene un saldo de $1000.00<br>CUANDO consulto el saldo<br>ENTONCES obtengo el valor exacto del saldo actual |
| **Prioridad** | Alta |
| **Estimación** | 2 Story Points |
| **Estado** | ✅ Completado |

---

### HU-011: Consultar Transacción por ID

| Campo | Detalle |
|-------|---------|
| **ID** | HU-011 |
| **Título** | Consultar Información de Transacción por ID |
| **Descripción** | **Como** usuario del sistema<br>**Quiero** consultar una transacción por su ID único<br>**Para** verificar los detalles de una operación específica |
| **Criterios de Aceptación** | **CA1 - Consulta exitosa:**<br>DADO que existe una transacción con ID "tx-001"<br>CUANDO consulto la transacción por ese ID<br>ENTONCES obtengo los datos completos de la transacción<br>Y los datos incluyen cuenta, monto, tipo y saldo resultante<br><br>**CA2 - ID inexistente:**<br>DADO que NO existe una transacción con ID "invalid-id"<br>CUANDO consulto la transacción por ese ID<br>ENTONCES recibo un error TransactionNotFoundException<br>Y el mensaje indica "not found" |
| **Prioridad** | Media |
| **Estimación** | 3 Story Points |
| **Estado** | ✅ Completado |

---

### HU-012: Listar Todas las Transacciones

| Campo | Detalle |
|-------|---------|
| **ID** | HU-012 |
| **Título** | Listar Todas las Transacciones |
| **Descripción** | **Como** administrador del sistema<br>**Quiero** listar todas las transacciones registradas<br>**Para** auditar y supervisar las operaciones financieras |
| **Criterios de Aceptación** | **CA1 - Listar exitosamente:**<br>DADO que existen múltiples transacciones en el sistema<br>CUANDO solicito listar todas las transacciones<br>ENTONCES obtengo una lista con todas las transacciones<br>Y cada transacción incluye sus datos completos |
| **Prioridad** | Media |
| **Estimación** | 2 Story Points |
| **Estado** | ✅ Completado |

---

### HU-013: Listar Transacciones por Cuenta

| Campo | Detalle |
|-------|---------|
| **ID** | HU-013 |
| **Título** | Consultar Historial de Transacciones de una Cuenta |
| **Descripción** | **Como** cliente del banco<br>**Quiero** ver el historial de transacciones de mi cuenta<br>**Para** revisar mis movimientos financieros |
| **Criterios de Aceptación** | **CA1 - Historial exitoso:**<br>DADO que tengo una cuenta con número "1234567890"<br>Y la cuenta tiene transacciones registradas<br>CUANDO solicito el historial de transacciones<br>ENTONCES obtengo todas las transacciones de esa cuenta<br>Y están ordenadas por fecha descendente (más recientes primero) |
| **Prioridad** | Alta |
| **Estimación** | 3 Story Points |
| **Estado** | ✅ Completado |

---

## Ejemplos Prácticos

Esta sección documenta **TODOS** los métodos de test del proyecto (31 tests en total), organizados por clase y funcionalidad.

---

## Tests Unitarios de AccountService (10 tests)

### 1. createAccount_ValidData_Success()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 1: Creación exitosa de cuenta
  DADO que soy un nuevo cliente
  Y proporciono número de cuenta único de 10-20 dígitos
  Y proporciono email válido
  Y proporciono saldo inicial >= 0
  CUANDO solicito crear la cuenta
  ENTONCES la cuenta se crea exitosamente
  Y la cuenta está activa por defecto
  Y recibo confirmación con los datos de la cuenta
```

**Caso de Prueba:** CP-001

| Campo | Valor |
|-------|-------|
| **ID** | CP-001 |
| **Historia** | HU-001 |
| **Escenario** | Creación exitosa |
| **Precondiciones** | - Base de datos limpia<br>- No existe cuenta con número "1234567890" |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- ownerName: "John Doe"<br>- ownerEmail: "john@example.com"<br>- initialBalance: 1000.00 |
| **Pasos** | 1. Preparar request con datos válidos<br>2. Ejecutar createAccount()<br>3. Verificar respuesta |
| **Resultado Esperado** | - Cuenta creada con ID único<br>- active = true<br>- balance = 1000.00<br>- Repositorio invocado correctamente |

**Explicación del Test:**
- **GIVEN**: Configuramos mocks para simular que no existe cuenta duplicada y que el guardado es exitoso
- **WHEN**: Ejecutamos createAccount con datos válidos
- **THEN**: Verificamos que la respuesta contiene los datos correctos y que se llamaron los métodos del repositorio

**Código:**

```java
@Test
@DisplayName("Debe crear cuenta exitosamente cuando el número no existe")
void createAccount_ValidData_Success() {
    // GIVEN
    given(accountRepository.existsByAccountNumber("1234567890")).willReturn(false);
    given(accountRepository.save(any(Account.class))).willReturn(testAccount);

    // WHEN
    AccountResponse response = accountService.createAccount(testRequest);

    // THEN
    assertThat(response).isNotNull();
    assertThat(response.accountNumber()).isEqualTo("1234567890");
    assertThat(response.ownerName()).isEqualTo("John Doe");
    assertThat(response.balance()).isEqualByComparingTo(new BigDecimal("1000.00"));

    then(accountRepository).should(times(1)).existsByAccountNumber("1234567890");
    then(accountRepository).should(times(1)).save(any(Account.class));
}
```

---

### 2. createAccount_DuplicateNumber_ThrowsException()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 2: Rechazo por número de cuenta duplicado
  DADO que existe una cuenta con número "1234567890"
  CUANDO intento crear otra cuenta con el mismo número
  ENTONCES la operación es rechazada
  Y recibo mensaje "Account with number 1234567890 already exists"
  Y no se crea ninguna cuenta nueva
```

**Caso de Prueba:** CP-002

| Campo | Valor |
|-------|-------|
| **ID** | CP-002 |
| **Historia** | HU-001 |
| **Escenario** | Número duplicado |
| **Precondiciones** | - Existe cuenta con número "1234567890" |
| **Datos de Prueba** | - accountNumber: "1234567890" (duplicado)<br>- ownerName: "John Doe"<br>- ownerEmail: "john@example.com"<br>- initialBalance: 1000.00 |
| **Pasos** | 1. Verificar que existe cuenta duplicada<br>2. Intentar crear cuenta con mismo número<br>3. Capturar excepción |
| **Resultado Esperado** | - Se lanza DuplicateAccountException<br>- Mensaje contiene "already exists"<br>- No se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para simular que ya existe una cuenta con ese número
- **WHEN & THEN**: Ejecutamos createAccount y verificamos que lanza DuplicateAccountException
- **THEN**: Verificamos que NO se intentó guardar la cuenta

**Código:**

```java
@Test
@DisplayName("Debe lanzar excepción cuando el número de cuenta ya existe")
void createAccount_DuplicateNumber_ThrowsException() {
    // GIVEN
    given(accountRepository.existsByAccountNumber("1234567890")).willReturn(true);

    // WHEN & THEN
    assertThatThrownBy(() -> accountService.createAccount(testRequest))
            .isInstanceOf(DuplicateAccountException.class)
            .hasMessageContaining("already exists");

    then(accountRepository).should(times(0)).save(any(Account.class));
}
```

---

### 3. getAccountById_ExistingId_Success()

**Historia de Usuario:** HU-004 - Consultar Cuenta por ID

```
Como: Usuario del sistema
Quiero: Consultar una cuenta bancaria por su ID
Para: Verificar sus datos y estado actual
```

**Criterio de Aceptación:**
```
Escenario 1: Consulta exitosa por ID existente
  DADO que existe una cuenta con ID "acc-001"
  CUANDO consulto la cuenta por ese ID
  ENTONCES obtengo los datos completos de la cuenta
  Y los datos corresponden al ID solicitado
```

**Caso de Prueba:** CP-009

| Campo | Valor |
|-------|-------|
| **ID** | CP-009 |
| **Historia** | HU-004 |
| **Escenario** | Consulta exitosa por ID |
| **Precondiciones** | - Existe cuenta con ID "acc-001" |
| **Datos de Prueba** | - id: "acc-001" |
| **Pasos** | 1. Ejecutar getAccountById("acc-001")<br>2. Verificar respuesta |
| **Resultado Esperado** | - Se retorna AccountResponse<br>- id = "acc-001"<br>- accountNumber = "1234567890" |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar una cuenta existente
- **WHEN**: Ejecutamos getAccountById con un ID válido
- **THEN**: Verificamos que la respuesta contiene los datos correctos de la cuenta

**Código:**

```java
@Test
@DisplayName("Debe obtener cuenta por ID exitosamente")
void getAccountById_ExistingId_Success() {
    // GIVEN
    given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));

    // WHEN
    AccountResponse response = accountService.getAccountById("acc-001");

    // THEN
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo("acc-001");
    assertThat(response.accountNumber()).isEqualTo("1234567890");

    then(accountRepository).should(times(1)).findById("acc-001");
}
```

---

### 4. getAccountById_NonExistingId_ThrowsException()

**Historia de Usuario:** HU-004 - Consultar Cuenta por ID

**Criterio de Aceptación:**
```
Escenario 2: ID inexistente
  DADO que NO existe una cuenta con ID "invalid-id"
  CUANDO consulto la cuenta por ese ID
  ENTONCES recibo un error AccountNotFoundException
  Y el mensaje indica "not found"
```

**Caso de Prueba:** CP-010

| Campo | Valor |
|-------|-------|
| **ID** | CP-010 |
| **Historia** | HU-004 |
| **Escenario** | ID inexistente |
| **Precondiciones** | - No existe cuenta con ID "invalid-id" |
| **Datos de Prueba** | - id: "invalid-id" |
| **Pasos** | 1. Ejecutar getAccountById("invalid-id")<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza AccountNotFoundException<br>- Mensaje contiene "not found" |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar Optional.empty()
- **WHEN & THEN**: Ejecutamos getAccountById y verificamos que lanza AccountNotFoundException

**Código:**

```java
@Test
@DisplayName("Debe lanzar excepción cuando el ID no existe")
void getAccountById_NonExistingId_ThrowsException() {
    // GIVEN
    given(accountRepository.findById("invalid-id")).willReturn(Optional.empty());

    // WHEN & THEN
    assertThatThrownBy(() -> accountService.getAccountById("invalid-id"))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining("not found");

    then(accountRepository).should(times(1)).findById("invalid-id");
}
```

---

### 5. getAccountByNumber_ExistingNumber_Success()

**Historia de Usuario:** HU-005 - Consultar Cuenta por Número

```
Como: Cliente del banco
Quiero: Consultar una cuenta bancaria por su número
Para: Verificar información y realizar operaciones
```

**Criterio de Aceptación:**
```
Escenario 1: Consulta exitosa por número existente
  DADO que existe una cuenta con número "1234567890"
  CUANDO consulto la cuenta por ese número
  ENTONCES obtengo los datos completos de la cuenta
  Y el número coincide con el solicitado
```

**Caso de Prueba:** CP-011

| Campo | Valor |
|-------|-------|
| **ID** | CP-011 |
| **Historia** | HU-005 |
| **Escenario** | Consulta exitosa por número |
| **Precondiciones** | - Existe cuenta con número "1234567890" |
| **Datos de Prueba** | - accountNumber: "1234567890" |
| **Pasos** | 1. Ejecutar getAccountByNumber("1234567890")<br>2. Verificar respuesta |
| **Resultado Esperado** | - Se retorna AccountResponse<br>- accountNumber = "1234567890" |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar una cuenta con el número especificado
- **WHEN**: Ejecutamos getAccountByNumber
- **THEN**: Verificamos que la respuesta contiene la cuenta correcta

**Código:**

```java
@Test
@DisplayName("Debe obtener cuenta por número exitosamente")
void getAccountByNumber_ExistingNumber_Success() {
    // GIVEN
    given(accountRepository.findByAccountNumber("1234567890"))
            .willReturn(Optional.of(testAccount));

    // WHEN
    AccountResponse response = accountService.getAccountByNumber("1234567890");

    // THEN
    assertThat(response).isNotNull();
    assertThat(response.accountNumber()).isEqualTo("1234567890");

    then(accountRepository).should(times(1)).findByAccountNumber("1234567890");
}
```

---

### 6. getAllAccounts_MultipleAccounts_Success()

**Historia de Usuario:** HU-006 - Listar Todas las Cuentas

```
Como: Administrador del sistema
Quiero: Listar todas las cuentas bancarias registradas
Para: Tener una vista general del sistema
```

**Criterio de Aceptación:**
```
Escenario 1: Listar todas las cuentas exitosamente
  DADO que existen múltiples cuentas en el sistema
  CUANDO solicito listar todas las cuentas
  ENTONCES obtengo una lista con todas las cuentas
  Y cada cuenta incluye sus datos completos
```

**Caso de Prueba:** CP-012

| Campo | Valor |
|-------|-------|
| **ID** | CP-012 |
| **Historia** | HU-006 |
| **Escenario** | Listar todas las cuentas |
| **Precondiciones** | - Existen 2 cuentas en el sistema |
| **Datos de Prueba** | - Cuenta 1: "1234567890"<br>- Cuenta 2: "0987654321" |
| **Pasos** | 1. Ejecutar getAllAccounts()<br>2. Verificar lista retornada |
| **Resultado Esperado** | - Lista con 2 elementos<br>- Contiene ambas cuentas |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar una lista con múltiples cuentas
- **WHEN**: Ejecutamos getAllAccounts
- **THEN**: Verificamos que se retornan todas las cuentas correctamente

**Código:**

```java
@Test
@DisplayName("Debe listar todas las cuentas")
void getAllAccounts_MultipleAccounts_Success() {
    // GIVEN
    Account account2 = new Account("0987654321", "Jane Smith",
            "jane@example.com", new BigDecimal("2000.00"));
    account2.setId("acc-002");

    List<Account> accounts = Arrays.asList(testAccount, account2);
    given(accountRepository.findAll()).willReturn(accounts);

    // WHEN
    List<AccountResponse> responses = accountService.getAllAccounts();

    // THEN
    assertThat(responses).isNotNull();
    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");
    assertThat(responses.get(1).accountNumber()).isEqualTo("0987654321");

    then(accountRepository).should(times(1)).findAll();
}
```

---

### 7. getActiveAccounts_FilterActive_Success()

**Historia de Usuario:** HU-007 - Listar Cuentas Activas

```
Como: Operador del banco
Quiero: Listar solo las cuentas activas
Para: Procesar transacciones solo en cuentas operativas
```

**Criterio de Aceptación:**
```
Escenario 1: Filtrar cuentas activas exitosamente
  DADO que existen cuentas activas e inactivas en el sistema
  CUANDO solicito listar solo las cuentas activas
  ENTONCES obtengo únicamente las cuentas con estado activo
  Y no se incluyen cuentas inactivas
```

**Caso de Prueba:** CP-013

| Campo | Valor |
|-------|-------|
| **ID** | CP-013 |
| **Historia** | HU-007 |
| **Escenario** | Filtrar cuentas activas |
| **Precondiciones** | - Existe al menos una cuenta activa |
| **Datos de Prueba** | - Cuenta activa: "1234567890" |
| **Pasos** | 1. Ejecutar getActiveAccounts()<br>2. Verificar filtrado |
| **Resultado Esperado** | - Lista contiene solo cuentas activas<br>- Tamaño correcto |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar solo cuentas con active=true
- **WHEN**: Ejecutamos getActiveAccounts
- **THEN**: Verificamos que se retornan solo las cuentas activas

**Código:**

```java
@Test
@DisplayName("Debe listar solo las cuentas activas")
void getActiveAccounts_FilterActive_Success() {
    // GIVEN
    List<Account> activeAccounts = Arrays.asList(testAccount);
    given(accountRepository.findByActive(true)).willReturn(activeAccounts);

    // WHEN
    List<AccountResponse> responses = accountService.getActiveAccounts();

    // THEN
    assertThat(responses).isNotNull();
    assertThat(responses).hasSize(1);

    then(accountRepository).should(times(1)).findByActive(true);
}
```

---

### 8. deactivateAccount_ActiveAccount_Success()

**Historia de Usuario:** HU-008 - Desactivar Cuenta

```
Como: Administrador del banco
Quiero: Desactivar una cuenta bancaria
Para: Suspender operaciones temporalmente sin eliminar la cuenta
```

**Criterio de Aceptación:**
```
Escenario 1: Desactivación exitosa de cuenta activa
  DADO que existe una cuenta activa con ID "acc-001"
  CUANDO solicito desactivar la cuenta
  ENTONCES la cuenta cambia a estado inactivo
  Y se persiste el cambio en la base de datos
  Y recibo confirmación del cambio
```

**Caso de Prueba:** CP-014

| Campo | Valor |
|-------|-------|
| **ID** | CP-014 |
| **Historia** | HU-008 |
| **Escenario** | Desactivación exitosa |
| **Precondiciones** | - Existe cuenta activa con ID "acc-001" |
| **Datos de Prueba** | - id: "acc-001" |
| **Pasos** | 1. Ejecutar deactivateAccount("acc-001")<br>2. Verificar cambio de estado<br>3. Verificar persistencia |
| **Resultado Esperado** | - active = false<br>- Se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos mocks con una cuenta activa
- **WHEN**: Ejecutamos deactivateAccount
- **THEN**: Verificamos que la cuenta quedó inactiva y se guardó en el repositorio

**Código:**

```java
@Test
@DisplayName("Debe desactivar cuenta exitosamente")
void deactivateAccount_ActiveAccount_Success() {
    // GIVEN
    given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
    given(accountRepository.save(any(Account.class))).willReturn(testAccount);

    // WHEN
    AccountResponse response = accountService.deactivateAccount("acc-001");

    // THEN
    assertThat(response).isNotNull();
    assertThat(testAccount.getActive()).isFalse();

    then(accountRepository).should(times(1)).findById("acc-001");
    then(accountRepository).should(times(1)).save(testAccount);
}
```

---

### 9. activateAccount_InactiveAccount_Success()

**Historia de Usuario:** HU-009 - Activar Cuenta

```
Como: Administrador del banco
Quiero: Reactivar una cuenta bancaria inactiva
Para: Permitir que el cliente vuelva a operar normalmente
```

**Criterio de Aceptación:**
```
Escenario 1: Activación exitosa de cuenta inactiva
  DADO que existe una cuenta inactiva con ID "acc-001"
  CUANDO solicito activar la cuenta
  ENTONCES la cuenta cambia a estado activo
  Y se persiste el cambio en la base de datos
  Y recibo confirmación del cambio
```

**Caso de Prueba:** CP-015

| Campo | Valor |
|-------|-------|
| **ID** | CP-015 |
| **Historia** | HU-009 |
| **Escenario** | Activación exitosa |
| **Precondiciones** | - Existe cuenta inactiva con ID "acc-001" |
| **Datos de Prueba** | - id: "acc-001"<br>- estado inicial: inactivo |
| **Pasos** | 1. Ejecutar activateAccount("acc-001")<br>2. Verificar cambio de estado<br>3. Verificar persistencia |
| **Resultado Esperado** | - active = true<br>- Se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos mocks con una cuenta previamente desactivada
- **WHEN**: Ejecutamos activateAccount
- **THEN**: Verificamos que la cuenta quedó activa

**Código:**

```java
@Test
@DisplayName("Debe activar cuenta exitosamente")
void activateAccount_InactiveAccount_Success() {
    // GIVEN
    testAccount.deactivate();
    given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
    given(accountRepository.save(any(Account.class))).willReturn(testAccount);

    // WHEN
    AccountResponse response = accountService.activateAccount("acc-001");

    // THEN
    assertThat(response).isNotNull();
    assertThat(testAccount.getActive()).isTrue();

    then(accountRepository).should(times(1)).findById("acc-001");
    then(accountRepository).should(times(1)).save(testAccount);
}
```

---

### 10. getAccountBalance_ExistingAccount_Success()

**Historia de Usuario:** HU-010 - Consultar Saldo

```
Como: Cliente del banco
Quiero: Consultar el saldo de mi cuenta
Para: Conocer mi disponibilidad financiera
```

**Criterio de Aceptación:**
```
Escenario 1: Consulta de saldo exitosa
  DADO que tengo una cuenta con número "1234567890"
  Y la cuenta tiene un saldo de $1000.00
  CUANDO consulto el saldo
  ENTONCES obtengo el valor exacto del saldo actual
```

**Caso de Prueba:** CP-016

| Campo | Valor |
|-------|-------|
| **ID** | CP-016 |
| **Historia** | HU-010 |
| **Escenario** | Consulta de saldo exitosa |
| **Precondiciones** | - Existe cuenta "1234567890" con saldo $1000 |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- balance: 1000.00 |
| **Pasos** | 1. Ejecutar getAccountBalance("1234567890")<br>2. Verificar saldo retornado |
| **Resultado Esperado** | - Se retorna BigDecimal con valor 1000.00 |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar una cuenta con saldo específico
- **WHEN**: Ejecutamos getAccountBalance
- **THEN**: Verificamos que el saldo retornado es correcto

**Código:**

```java
@Test
@DisplayName("Debe retornar el saldo de la cuenta")
void getAccountBalance_ExistingAccount_Success() {
    // GIVEN
    given(accountRepository.findByAccountNumber("1234567890"))
            .willReturn(Optional.of(testAccount));

    // WHEN
    BigDecimal balance = accountService.getAccountBalance("1234567890");

    // THEN
    assertThat(balance).isEqualByComparingTo(new BigDecimal("1000.00"));

    then(accountRepository).should(times(1)).findByAccountNumber("1234567890");
}
```

---

## Tests Unitarios de TransactionService (10 tests)

### 11. createTransaction_Deposit_Success()

**Historia de Usuario:** HU-002 - Realizar Depósito

```
Como: Cliente del banco
Quiero: Depositar dinero en mi cuenta
Para: Incrementar mi saldo disponible
```

**Criterio de Aceptación:**
```
Escenario 1: Depósito exitoso en cuenta activa
  DADO que tengo una cuenta activa con número "1234567890"
  Y la cuenta tiene un saldo de $1000
  CUANDO deposito $500
  ENTONCES el saldo aumenta a $1500
  Y se registra la transacción tipo DEPOSIT
  Y recibo confirmación con el nuevo saldo
```

**Caso de Prueba:** CP-005

| Campo | Valor |
|-------|-------|
| **ID** | CP-005 |
| **Historia** | HU-002 |
| **Escenario** | Depósito exitoso |
| **Precondiciones** | - Cuenta "1234567890" activa con saldo $1000 |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- type: DEPOSIT<br>- amount: 500.00<br>- description: "Salary deposit" |
| **Pasos** | 1. Ejecutar createTransaction()<br>2. Verificar aumento de saldo<br>3. Verificar persistencia |
| **Resultado Esperado** | - Saldo final: $1500<br>- Transacción guardada<br>- balanceAfter = 1500.00 |

**Explicación del Test:**
- **GIVEN**: Configuramos mocks con una cuenta activa y saldo inicial de $1000
- **WHEN**: Ejecutamos createTransaction con un depósito de $500
- **THEN**: Verificamos que el saldo aumentó a $1500 y la transacción se guardó

**Código:**

```java
@Test
@DisplayName("Debe crear un depósito exitosamente")
void createTransaction_Deposit_Success() {
    // GIVEN - Cuenta activa con $1000
    TransactionRequest request = new TransactionRequest(
            "1234567890",
            TransactionType.DEPOSIT,
            new BigDecimal("500.00"),
            "Salary deposit"
    );

    given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);
    given(transactionRepository.save(any(Transaction.class))).willReturn(testTransaction);

    // WHEN - Se realiza un depósito
    TransactionResponse response = transactionService.createTransaction(request);

    // THEN - La transacción se crea y el saldo aumenta
    assertThat(response).isNotNull();
    assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
    assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));
    assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));

    then(accountService).should(times(1)).findAccountByNumber("1234567890");
    then(transactionRepository).should(times(1)).save(any(Transaction.class));
}
```

---

### 12. createTransaction_Withdraw_Success()

**Historia de Usuario:** HU-003 - Realizar Retiro

```
Como: Cliente del banco
Quiero: Retirar dinero de mi cuenta
Para: Disponer de efectivo o realizar pagos
```

**Criterio de Aceptación:**
```
Escenario 1: Retiro exitoso con saldo suficiente
  DADO que tengo una cuenta activa con número "1234567890"
  Y la cuenta tiene un saldo de $1000
  CUANDO retiro $300
  ENTONCES el saldo disminuye a $700
  Y se registra la transacción tipo WITHDRAW
  Y recibo confirmación con el nuevo saldo
```

**Caso de Prueba:** CP-007

| Campo | Valor |
|-------|-------|
| **ID** | CP-007 |
| **Historia** | HU-003 |
| **Escenario** | Retiro exitoso |
| **Precondiciones** | - Cuenta "1234567890" activa con saldo $1000 |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- type: WITHDRAW<br>- amount: 300.00<br>- description: "Cash withdrawal" |
| **Pasos** | 1. Ejecutar createTransaction()<br>2. Verificar disminución de saldo<br>3. Verificar persistencia |
| **Resultado Esperado** | - Saldo final: $700<br>- Transacción guardada<br>- balanceAfter = 700.00 |

**Explicación del Test:**
- **GIVEN**: Configuramos cuenta activa con saldo suficiente ($1000)
- **WHEN**: Ejecutamos createTransaction con un retiro de $300
- **THEN**: Verificamos que el saldo disminuyó a $700

**Código:**

```java
@Test
@DisplayName("Debe crear un retiro exitosamente")
void createTransaction_Withdraw_Success() {
    // GIVEN - Cuenta activa con $1000
    TransactionRequest request = new TransactionRequest(
            "1234567890",
            TransactionType.WITHDRAW,
            new BigDecimal("300.00"),
            "Cash withdrawal"
    );

    Transaction withdrawalTransaction = new Transaction();
    withdrawalTransaction.setId("tx-002");
    withdrawalTransaction.setAccount(testAccount);
    withdrawalTransaction.setType(TransactionType.WITHDRAW);
    withdrawalTransaction.setAmount(new BigDecimal("300.00"));
    withdrawalTransaction.setBalanceAfter(new BigDecimal("700.00"));

    given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);
    given(transactionRepository.save(any(Transaction.class))).willReturn(withdrawalTransaction);

    // WHEN - Se realiza un retiro
    TransactionResponse response = transactionService.createTransaction(request);

    // THEN - La transacción se crea y el saldo disminuye
    assertThat(response).isNotNull();
    assertThat(response.type()).isEqualTo(TransactionType.WITHDRAW);
    assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("300.00"));
    assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("700.00"));
    assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("700.00"));

    then(accountService).should(times(1)).findAccountByNumber("1234567890");
    then(transactionRepository).should(times(1)).save(any(Transaction.class));
}
```

---

### 13. createTransaction_InvalidAmount_ThrowsException()

**Historia de Usuario:** HU-002 - Realizar Depósito

**Criterio de Aceptación:**
```
Escenario 2: Rechazo de monto inválido
  DADO que intento realizar una transacción
  CUANDO el monto es cero o negativo
  ENTONCES la operación es rechazada
  Y recibo mensaje "Amount must be greater than zero"
  Y no se crea ninguna transacción
```

**Caso de Prueba:** CP-006

| Campo | Valor |
|-------|-------|
| **ID** | CP-006 |
| **Historia** | HU-002 |
| **Escenario** | Monto inválido |
| **Precondiciones** | - Ninguna |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- type: DEPOSIT<br>- amount: 0.00 (inválido) |
| **Pasos** | 1. Ejecutar createTransaction() con monto 0<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza IllegalArgumentException<br>- Mensaje: "must be greater than zero"<br>- No se invoca save() |

**Explicación del Test:**
- **GIVEN**: Preparamos una request con monto cero
- **WHEN & THEN**: Verificamos que lanza IllegalArgumentException
- **THEN**: Verificamos que NO se guardó la transacción

**Código:**

```java
@Test
@DisplayName("Debe rechazar monto cero o negativo")
void createTransaction_InvalidAmount_ThrowsException() {
    // GIVEN - Monto inválido (cero)
    TransactionRequest request = new TransactionRequest(
            "1234567890",
            TransactionType.DEPOSIT,
            new BigDecimal("0.00"),
            "Invalid amount"
    );

    // WHEN & THEN - Se lanza excepción
    assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must be greater than zero");

    then(transactionRepository).should(times(0)).save(any(Transaction.class));
}
```

---

### 14. createTransaction_NonExistingAccount_ThrowsException()

**Historia de Usuario:** HU-002 - Realizar Depósito / HU-003 - Realizar Retiro

**Criterio de Aceptación:**
```
Escenario 3: Rechazo por cuenta inexistente
  DADO que intento realizar una transacción
  CUANDO la cuenta especificada no existe en el sistema
  ENTONCES la operación es rechazada
  Y recibo mensaje "Account not found"
  Y no se crea ninguna transacción
```

**Caso de Prueba:** CP-017

| Campo | Valor |
|-------|-------|
| **ID** | CP-017 |
| **Historia** | HU-002 / HU-003 |
| **Escenario** | Cuenta inexistente |
| **Precondiciones** | - No existe cuenta "9999999999" |
| **Datos de Prueba** | - accountNumber: "9999999999" (inexistente)<br>- type: DEPOSIT<br>- amount: 100.00 |
| **Pasos** | 1. Ejecutar createTransaction()<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza AccountNotFoundException<br>- Mensaje: "not found"<br>- No se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para lanzar AccountNotFoundException
- **WHEN & THEN**: Verificamos que la excepción se propaga correctamente
- **THEN**: Verificamos que NO se guardó la transacción

**Código:**

```java
@Test
@DisplayName("Debe rechazar cuenta inexistente")
void createTransaction_NonExistingAccount_ThrowsException() {
    // GIVEN - Cuenta que no existe
    TransactionRequest request = new TransactionRequest(
            "9999999999",
            TransactionType.DEPOSIT,
            new BigDecimal("100.00"),
            "Test"
    );

    given(accountService.findAccountByNumber("9999999999"))
            .willThrow(new AccountNotFoundException("Account not found"));

    // WHEN & THEN - Se lanza excepción
    assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining("not found");

    then(transactionRepository).should(times(0)).save(any(Transaction.class));
}
```

---

### 15. createTransaction_InactiveAccount_ThrowsException()

**Historia de Usuario:** HU-002 - Realizar Depósito / HU-003 - Realizar Retiro

**Criterio de Aceptación:**
```
Escenario 4: Rechazo por cuenta inactiva
  DADO que existe una cuenta con número "1234567890"
  Y la cuenta está en estado inactivo
  CUANDO intento realizar una transacción
  ENTONCES la operación es rechazada
  Y recibo mensaje sobre "inactive account"
  Y no se crea ninguna transacción
```

**Caso de Prueba:** CP-018

| Campo | Valor |
|-------|-------|
| **ID** | CP-018 |
| **Historia** | HU-002 / HU-003 |
| **Escenario** | Cuenta inactiva |
| **Precondiciones** | - Existe cuenta "1234567890"<br>- Cuenta está inactiva |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- type: DEPOSIT<br>- amount: 100.00 |
| **Pasos** | 1. Ejecutar createTransaction() en cuenta inactiva<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza InactiveAccountException<br>- Mensaje: "inactive account"<br>- No se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos una cuenta desactivada
- **WHEN & THEN**: Verificamos que lanza InactiveAccountException
- **THEN**: Verificamos que NO se guardó la transacción

**Código:**

```java
@Test
@DisplayName("Debe rechazar cuenta inactiva")
void createTransaction_InactiveAccount_ThrowsException() {
    // GIVEN - Cuenta inactiva
    testAccount.deactivate();
    TransactionRequest request = new TransactionRequest(
            "1234567890",
            TransactionType.DEPOSIT,
            new BigDecimal("100.00"),
            "Test"
    );

    given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);

    // WHEN & THEN - Se lanza excepción
    assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(InactiveAccountException.class)
            .hasMessageContaining("inactive account");

    then(transactionRepository).should(times(0)).save(any(Transaction.class));
}
```

---

### 16. createTransaction_Withdraw_ThrowsException_WhenInsufficientBalance()

**Historia de Usuario:** HU-003 - Realizar Retiro

**Criterio de Aceptación:**
```
Escenario 2: Rechazo por saldo insuficiente
  DADO que tengo una cuenta activa con saldo de $1000
  CUANDO intento retirar $2000
  ENTONCES la operación es rechazada
  Y recibo mensaje "Insufficient balance"
  Y el saldo permanece sin cambios
  Y no se crea ninguna transacción
```

**Caso de Prueba:** CP-008

| Campo | Valor |
|-------|-------|
| **ID** | CP-008 |
| **Historia** | HU-003 |
| **Escenario** | Saldo insuficiente |
| **Precondiciones** | - Cuenta "1234567890" activa con saldo $1000 |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- type: WITHDRAW<br>- amount: 2000.00<br>- saldo actual: 1000.00 |
| **Pasos** | 1. Ejecutar createTransaction() con monto mayor al saldo<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza IllegalStateException<br>- Mensaje: "Insufficient balance"<br>- Saldo no cambia<br>- No se invoca save() |

**Explicación del Test:**
- **GIVEN**: Configuramos cuenta con $1000 e intento de retiro de $2000
- **WHEN & THEN**: Verificamos que lanza IllegalStateException por saldo insuficiente
- **THEN**: Verificamos que NO se guardó la transacción

**Código:**

```java
@Test
@DisplayName("Debe rechazar retiro con saldo insuficiente")
void createTransaction_Withdraw_ThrowsException_WhenInsufficientBalance() {
    // GIVEN - Retiro mayor al saldo disponible
    TransactionRequest request = new TransactionRequest(
            "1234567890",
            TransactionType.WITHDRAW,
            new BigDecimal("2000.00"),
            "Overdraft attempt"
    );

    given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);

    // WHEN & THEN - Se lanza excepción
    assertThatThrownBy(() -> transactionService.createTransaction(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Insufficient balance");

    then(transactionRepository).should(times(0)).save(any(Transaction.class));
}
```

---

### 17. getTransactionById_ExistingId_Success()

**Historia de Usuario:** HU-011 - Consultar Transacción por ID

```
Como: Usuario del sistema
Quiero: Consultar una transacción por su ID
Para: Verificar los detalles de una operación específica
```

**Criterio de Aceptación:**
```
Escenario 1: Consulta exitosa por ID existente
  DADO que existe una transacción con ID "tx-001"
  CUANDO consulto la transacción por ese ID
  ENTONCES obtengo los datos completos de la transacción
  Y los datos incluyen cuenta, monto, tipo y saldo resultante
```

**Caso de Prueba:** CP-019

| Campo | Valor |
|-------|-------|
| **ID** | CP-019 |
| **Historia** | HU-011 |
| **Escenario** | Consulta exitosa por ID |
| **Precondiciones** | - Existe transacción con ID "tx-001" |
| **Datos de Prueba** | - id: "tx-001" |
| **Pasos** | 1. Ejecutar getTransactionById("tx-001")<br>2. Verificar respuesta |
| **Resultado Esperado** | - Se retorna TransactionResponse<br>- id = "tx-001"<br>- accountNumber = "1234567890" |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar una transacción existente
- **WHEN**: Ejecutamos getTransactionById
- **THEN**: Verificamos que la respuesta contiene los datos correctos

**Código:**

```java
@Test
@DisplayName("Debe obtener transacción por ID")
void getTransactionById_ExistingId_Success() {
    // GIVEN - Transacción existente
    given(transactionRepository.findById("tx-001")).willReturn(Optional.of(testTransaction));

    // WHEN - Se busca por ID
    TransactionResponse response = transactionService.getTransactionById("tx-001");

    // THEN - Se retorna la transacción
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo("tx-001");
    assertThat(response.accountNumber()).isEqualTo("1234567890");

    then(transactionRepository).should(times(1)).findById("tx-001");
}
```

---

### 18. getTransactionById_NonExistingId_ThrowsException()

**Historia de Usuario:** HU-011 - Consultar Transacción por ID

**Criterio de Aceptación:**
```
Escenario 2: ID inexistente
  DADO que NO existe una transacción con ID "invalid-id"
  CUANDO consulto la transacción por ese ID
  ENTONCES recibo un error TransactionNotFoundException
  Y el mensaje indica "not found"
```

**Caso de Prueba:** CP-020

| Campo | Valor |
|-------|-------|
| **ID** | CP-020 |
| **Historia** | HU-011 |
| **Escenario** | ID inexistente |
| **Precondiciones** | - No existe transacción con ID "invalid-id" |
| **Datos de Prueba** | - id: "invalid-id" |
| **Pasos** | 1. Ejecutar getTransactionById("invalid-id")<br>2. Capturar excepción |
| **Resultado Esperado** | - Se lanza TransactionNotFoundException<br>- Mensaje contiene "not found" |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar Optional.empty()
- **WHEN & THEN**: Verificamos que lanza TransactionNotFoundException

**Código:**

```java
@Test
@DisplayName("Debe lanzar excepción si ID no existe")
void getTransactionById_NonExistingId_ThrowsException() {
    // GIVEN - ID inexistente
    given(transactionRepository.findById("invalid-id")).willReturn(Optional.empty());

    // WHEN & THEN - Se lanza excepción
    assertThatThrownBy(() -> transactionService.getTransactionById("invalid-id"))
            .isInstanceOf(TransactionNotFoundException.class)
            .hasMessageContaining("not found");

    then(transactionRepository).should(times(1)).findById("invalid-id");
}
```

---

### 19. getAllTransactions_MultipleTransactions_Success()

**Historia de Usuario:** HU-012 - Listar Todas las Transacciones

```
Como: Administrador del sistema
Quiero: Listar todas las transacciones registradas
Para: Auditar y supervisar las operaciones financieras
```

**Criterio de Aceptación:**
```
Escenario 1: Listar todas las transacciones exitosamente
  DADO que existen múltiples transacciones en el sistema
  CUANDO solicito listar todas las transacciones
  ENTONCES obtengo una lista con todas las transacciones
  Y cada transacción incluye sus datos completos
```

**Caso de Prueba:** CP-021

| Campo | Valor |
|-------|-------|
| **ID** | CP-021 |
| **Historia** | HU-012 |
| **Escenario** | Listar todas las transacciones |
| **Precondiciones** | - Existen 2 transacciones en el sistema |
| **Datos de Prueba** | - Transacción 1: DEPOSIT $500<br>- Transacción 2: WITHDRAW $200 |
| **Pasos** | 1. Ejecutar getAllTransactions()<br>2. Verificar lista retornada |
| **Resultado Esperado** | - Lista con 2 elementos<br>- Contiene ambas transacciones |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar lista con múltiples transacciones
- **WHEN**: Ejecutamos getAllTransactions
- **THEN**: Verificamos que se retornan todas las transacciones

**Código:**

```java
@Test
@DisplayName("Debe listar todas las transacciones")
void getAllTransactions_MultipleTransactions_Success() {
    // GIVEN - Múltiples transacciones
    Transaction transaction2 = new Transaction();
    transaction2.setId("tx-002");
    transaction2.setAccount(testAccount);
    transaction2.setType(TransactionType.WITHDRAW);
    transaction2.setAmount(new BigDecimal("200.00"));
    transaction2.setBalanceAfter(new BigDecimal("800.00"));

    List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
    given(transactionRepository.findAll()).willReturn(transactions);

    // WHEN - Se solicitan todas las transacciones
    List<TransactionResponse> responses = transactionService.getAllTransactions();

    // THEN - Se retornan todas
    assertThat(responses).isNotNull();
    assertThat(responses).hasSize(2);

    then(transactionRepository).should(times(1)).findAll();
}
```

---

### 20. getTransactionsByAccountNumber_ExistingAccount_Success()

**Historia de Usuario:** HU-013 - Listar Transacciones por Cuenta

```
Como: Cliente del banco
Quiero: Ver el historial de transacciones de mi cuenta
Para: Revisar mis movimientos financieros
```

**Criterio de Aceptación:**
```
Escenario 1: Listar transacciones de cuenta específica
  DADO que tengo una cuenta con número "1234567890"
  Y la cuenta tiene transacciones registradas
  CUANDO solicito el historial de transacciones
  ENTONCES obtengo todas las transacciones de esa cuenta
  Y están ordenadas por fecha descendente (más recientes primero)
```

**Caso de Prueba:** CP-022

| Campo | Valor |
|-------|-------|
| **ID** | CP-022 |
| **Historia** | HU-013 |
| **Escenario** | Listar transacciones por cuenta |
| **Precondiciones** | - Existe cuenta "1234567890" con transacciones |
| **Datos de Prueba** | - accountNumber: "1234567890" |
| **Pasos** | 1. Ejecutar getTransactionsByAccountNumber("1234567890")<br>2. Verificar filtrado y orden |
| **Resultado Esperado** | - Lista contiene solo transacciones de esa cuenta<br>- Ordenadas por timestamp desc |

**Explicación del Test:**
- **GIVEN**: Configuramos el mock para retornar transacciones de una cuenta específica
- **WHEN**: Ejecutamos getTransactionsByAccountNumber
- **THEN**: Verificamos que solo se retornan transacciones de esa cuenta

**Código:**

```java
@Test
@DisplayName("Debe filtrar transacciones por número de cuenta")
void getTransactionsByAccountNumber_ExistingAccount_Success() {
    // GIVEN - Transacciones de una cuenta específica
    List<Transaction> transactions = Arrays.asList(testTransaction);
    given(transactionRepository.findByAccountNumberOrderByTimestampDesc("1234567890"))
            .willReturn(transactions);

    // WHEN - Se filtran por número de cuenta
    List<TransactionResponse> responses = transactionService
            .getTransactionsByAccountNumber("1234567890");

    // THEN - Se retornan solo las de esa cuenta
    assertThat(responses).isNotNull();
    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");

    then(transactionRepository).should(times(1))
            .findByAccountNumberOrderByTimestampDesc("1234567890");
}
```

---

## Tests de Integración de AccountIntegrationTest (11 tests)

### 21. createAccount_ValidData_ReturnsCreated()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 1: Creación exitosa de cuenta (End-to-End)
  DADO que soy un cliente del banco
  Y proporciono datos válidos vía API REST
  CUANDO envío POST a /api/v1/accounts
  ENTONCES recibo respuesta HTTP 201 Created
  Y la respuesta contiene los datos de la cuenta creada
  Y la cuenta está activa por defecto
```

**Caso de Prueba:** CP-023

| Campo | Valor |
|-------|-------|
| **ID** | CP-023 |
| **Historia** | HU-001 |
| **Escenario** | Creación E2E exitosa |
| **Precondiciones** | - Base de datos limpia<br>- API disponible |
| **Datos de Prueba** | - JSON: {accountNumber: "1234567890", ownerName: "John Doe", ownerEmail: "john@example.com", initialBalance: 1000.00} |
| **Pasos** | 1. Enviar POST /accounts con JSON<br>2. Verificar response HTTP<br>3. Verificar JSON response |
| **Resultado Esperado** | - HTTP 201 Created<br>- JSON con id, accountNumber, balance=1000, active=true |

**Explicación del Test:**
- **GIVEN**: Preparamos un JSON de request con datos válidos
- **WHEN**: Hacemos POST a /api/v1/accounts
- **THEN**: Verificamos HTTP 201 Created y estructura de la respuesta JSON

**Código:**

```java
@Test
@DisplayName("Debe crear cuenta exitosamente con datos válidos")
void createAccount_ValidData_ReturnsCreated() throws Exception {
    // GIVEN
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    // WHEN & THEN
    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.accountNumber", is("1234567890")))
            .andExpect(jsonPath("$.ownerName", is("John Doe")))
            .andExpect(jsonPath("$.ownerEmail", is("john@example.com")))
            .andExpect(jsonPath("$.balance", is(1000.00)))
            .andExpect(jsonPath("$.active", is(true)));
}
```

---

### 22. createAccount_DuplicateNumber_ReturnsConflict()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 2: Rechazo por número duplicado (End-to-End)
  DADO que existe una cuenta con número "1234567890"
  CUANDO intento crear otra cuenta con el mismo número vía API
  ENTONCES recibo respuesta HTTP 409 Conflict
  Y el mensaje indica "already exists"
```

**Caso de Prueba:** CP-024

| Campo | Valor |
|-------|-------|
| **ID** | CP-024 |
| **Historia** | HU-001 |
| **Escenario** | Número duplicado E2E |
| **Precondiciones** | - Ninguna cuenta existente inicialmente |
| **Datos de Prueba** | - accountNumber: "1234567890" (se crea dos veces) |
| **Pasos** | 1. POST /accounts (éxito)<br>2. POST /accounts con mismo número<br>3. Verificar 409 Conflict |
| **Resultado Esperado** | - HTTP 409 Conflict<br>- Mensaje: "already exists" |

**Explicación del Test:**
- **GIVEN**: Creamos primero una cuenta, luego intentamos crear otra con el mismo número
- **WHEN**: Hacemos segundo POST con número duplicado
- **THEN**: Verificamos HTTP 409 Conflict y mensaje de error

**Código:**

```java
@Test
@DisplayName("Debe rechazar número de cuenta duplicado")
void createAccount_DuplicateNumber_ReturnsConflict() throws Exception {
    // GIVEN - Create first account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

    // WHEN & THEN - Try to create duplicate
    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", containsString("already exists")));
}
```

---

### 23. createAccount_InvalidEmail_ReturnsBadRequest()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 3: Rechazo por email inválido (End-to-End)
  DADO que proporciono datos con email sin formato válido
  CUANDO envío POST a /api/v1/accounts
  ENTONCES recibo respuesta HTTP 400 Bad Request
  Y la validación rechaza el email inválido
```

**Caso de Prueba:** CP-025

| Campo | Valor |
|-------|-------|
| **ID** | CP-025 |
| **Historia** | HU-001 |
| **Escenario** | Email inválido E2E |
| **Precondiciones** | - API disponible |
| **Datos de Prueba** | - ownerEmail: "invalid-email" (sin @) |
| **Pasos** | 1. Enviar POST /accounts con email inválido<br>2. Verificar 400 Bad Request |
| **Resultado Esperado** | - HTTP 400 Bad Request |

**Explicación del Test:**
- **GIVEN**: Preparamos JSON con email inválido (sin @)
- **WHEN**: Hacemos POST con datos inválidos
- **THEN**: Verificamos HTTP 400 Bad Request

**Código:**

```java
@Test
@DisplayName("Debe rechazar cuenta con email inválido")
void createAccount_InvalidEmail_ReturnsBadRequest() throws Exception {
    // GIVEN
    String requestJson = """
            {
                "accountNumber": "1234567890",
                "ownerName": "John Doe",
                "ownerEmail": "invalid-email",
                "initialBalance": 1000.00
            }
            """;

    // WHEN & THEN
    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isBadRequest());
}
```

---

### 24. createAccount_NegativeBalance_ReturnsBadRequest()

**Historia de Usuario:** HU-001 - Crear Cuenta Bancaria

**Criterio de Aceptación:**
```
Escenario 4: Rechazo por saldo negativo (End-to-End)
  DADO que proporciono saldo inicial negativo
  CUANDO envío POST a /api/v1/accounts
  ENTONCES recibo respuesta HTTP 400 Bad Request
  Y la validación rechaza el saldo negativo
```

**Caso de Prueba:** CP-026

| Campo | Valor |
|-------|-------|
| **ID** | CP-026 |
| **Historia** | HU-001 |
| **Escenario** | Saldo negativo E2E |
| **Precondiciones** | - API disponible |
| **Datos de Prueba** | - initialBalance: -100.00 |
| **Pasos** | 1. Enviar POST /accounts con saldo negativo<br>2. Verificar 400 Bad Request |
| **Resultado Esperado** | - HTTP 400 Bad Request |

**Explicación del Test:**
- **GIVEN**: Preparamos request con saldo negativo (-100)
- **WHEN**: Hacemos POST
- **THEN**: Verificamos HTTP 400 Bad Request

**Código:**

```java
@Test
@DisplayName("Debe rechazar cuenta con saldo negativo")
void createAccount_NegativeBalance_ReturnsBadRequest() throws Exception {
    // GIVEN
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("-100.00")
    );

    // WHEN & THEN
    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
}
```

---

### 25. getAccountById_ExistingId_ReturnsOk()

**Historia de Usuario:** HU-004 - Consultar Cuenta por ID

**Criterio de Aceptación:**
```
Escenario 1: Consulta exitosa por ID (End-to-End)
  DADO que existe una cuenta creada en el sistema
  CUANDO envío GET a /api/v1/accounts/{id}
  ENTONCES recibo respuesta HTTP 200 OK
  Y la respuesta contiene los datos completos de la cuenta
```

**Caso de Prueba:** CP-027

| Campo | Valor |
|-------|-------|
| **ID** | CP-027 |
| **Historia** | HU-004 |
| **Escenario** | Consulta por ID E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - Crear cuenta primero, luego consultar por su ID |
| **Pasos** | 1. POST /accounts (crear)<br>2. Extraer ID de respuesta<br>3. GET /accounts/{id}<br>4. Verificar 200 OK |
| **Resultado Esperado** | - HTTP 200 OK<br>- JSON con datos de la cuenta |

**Explicación del Test:**
- **GIVEN**: Creamos una cuenta y guardamos su ID
- **WHEN**: Hacemos GET a /api/v1/accounts/{id}
- **THEN**: Verificamos HTTP 200 OK y datos correctos

**Código:**

```java
@Test
@DisplayName("Debe obtener cuenta por ID exitosamente")
void getAccountById_ExistingId_ReturnsOk() throws Exception {
    // GIVEN - Create account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    String createResponse = mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String accountId = objectMapper.readTree(createResponse).get("id").asText();

    // WHEN & THEN
    mockMvc.perform(get("/accounts/" + accountId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(accountId)))
            .andExpect(jsonPath("$.accountNumber", is("1234567890")));
}
```

---

### 26. getAccountById_NonExistingId_ReturnsNotFound()

**Historia de Usuario:** HU-004 - Consultar Cuenta por ID

**Criterio de Aceptación:**
```
Escenario 2: ID inexistente (End-to-End)
  DADO que NO existe una cuenta con el ID especificado
  CUANDO envío GET a /api/v1/accounts/{id}
  ENTONCES recibo respuesta HTTP 404 Not Found
  Y el mensaje indica "not found"
```

**Caso de Prueba:** CP-028

| Campo | Valor |
|-------|-------|
| **ID** | CP-028 |
| **Historia** | HU-004 |
| **Escenario** | ID inexistente E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - id: "non-existent-id" |
| **Pasos** | 1. GET /accounts/non-existent-id<br>2. Verificar 404 Not Found |
| **Resultado Esperado** | - HTTP 404 Not Found<br>- Mensaje: "not found" |

**Explicación del Test:**
- **GIVEN**: Preparamos un ID que no existe en la base de datos
- **WHEN**: Hacemos GET con ID inexistente
- **THEN**: Verificamos HTTP 404 Not Found y mensaje de error

**Código:**

```java
@Test
@DisplayName("Debe retornar 404 cuando el ID no existe")
void getAccountById_NonExistingId_ReturnsNotFound() throws Exception {
    // GIVEN
    String fakeId = "non-existent-id";

    // WHEN & THEN
    mockMvc.perform(get("/accounts/" + fakeId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("not found")));
}
```

---

### 27. getAccountByNumber_ExistingNumber_ReturnsOk()

**Historia de Usuario:** HU-005 - Consultar Cuenta por Número

**Criterio de Aceptación:**
```
Escenario 1: Consulta exitosa por número (End-to-End)
  DADO que existe una cuenta con número "1234567890"
  CUANDO envío GET a /api/v1/accounts/number/{accountNumber}
  ENTONCES recibo respuesta HTTP 200 OK
  Y la respuesta contiene los datos completos de la cuenta
```

**Caso de Prueba:** CP-029

| Campo | Valor |
|-------|-------|
| **ID** | CP-029 |
| **Historia** | HU-005 |
| **Escenario** | Consulta por número E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - accountNumber: "1234567890" |
| **Pasos** | 1. POST /accounts (crear)<br>2. GET /accounts/number/1234567890<br>3. Verificar 200 OK |
| **Resultado Esperado** | - HTTP 200 OK<br>- JSON con accountNumber="1234567890" |

**Explicación del Test:**
- **GIVEN**: Creamos una cuenta con número específico
- **WHEN**: Hacemos GET a /api/v1/accounts/number/{accountNumber}
- **THEN**: Verificamos HTTP 200 y datos correctos

**Código:**

```java
@Test
@DisplayName("Debe obtener cuenta por número exitosamente")
void getAccountByNumber_ExistingNumber_ReturnsOk() throws Exception {
    // GIVEN - Create account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

    // WHEN & THEN
    mockMvc.perform(get("/accounts/number/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber", is("1234567890")))
            .andExpect(jsonPath("$.ownerName", is("John Doe")));
}
```

---

### 28. getAllAccounts_MultipleAccounts_ReturnsOk()

**Historia de Usuario:** HU-006 - Listar Todas las Cuentas

**Criterio de Aceptación:**
```
Escenario 1: Listar todas las cuentas (End-to-End)
  DADO que existen 3 cuentas en el sistema
  CUANDO envío GET a /api/v1/accounts
  ENTONCES recibo respuesta HTTP 200 OK
  Y la respuesta contiene un array con las 3 cuentas
```

**Caso de Prueba:** CP-030

| Campo | Valor |
|-------|-------|
| **ID** | CP-030 |
| **Historia** | HU-006 |
| **Escenario** | Listar todas E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - 3 cuentas: "1234567891", "1234567892", "1234567893" |
| **Pasos** | 1. POST /accounts (3 veces)<br>2. GET /accounts<br>3. Verificar array size=3 |
| **Resultado Esperado** | - HTTP 200 OK<br>- Array JSON con 3 elementos |

**Explicación del Test:**
- **GIVEN**: Creamos 3 cuentas diferentes
- **WHEN**: Hacemos GET a /api/v1/accounts
- **THEN**: Verificamos HTTP 200 y que retorna array con 3 elementos

**Código:**

```java
@Test
@DisplayName("Debe listar todas las cuentas")
void getAllAccounts_MultipleAccounts_ReturnsOk() throws Exception {
    // GIVEN - Create 3 accounts
    for (int i = 1; i <= 3; i++) {
        AccountRequest request = new AccountRequest(
                "123456789" + i,
                "User " + i,
                "user" + i + "@example.com",
                new BigDecimal("1000.00")
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // WHEN & THEN
    mockMvc.perform(get("/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
}
```

---

### 29. deactivateAccount_ActiveAccount_ReturnsOk()

**Historia de Usuario:** HU-008 - Desactivar Cuenta

**Criterio de Aceptación:**
```
Escenario 1: Desactivación exitosa (End-to-End)
  DADO que existe una cuenta activa
  CUANDO envío PATCH a /api/v1/accounts/{id}/deactivate
  ENTONCES recibo respuesta HTTP 200 OK
  Y la cuenta cambia a estado inactivo (active=false)
```

**Caso de Prueba:** CP-031

| Campo | Valor |
|-------|-------|
| **ID** | CP-031 |
| **Historia** | HU-008 |
| **Escenario** | Desactivación E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - Cuenta activa creada previamente |
| **Pasos** | 1. POST /accounts (crear activa)<br>2. PATCH /accounts/{id}/deactivate<br>3. Verificar active=false |
| **Resultado Esperado** | - HTTP 200 OK<br>- JSON con active=false |

**Explicación del Test:**
- **GIVEN**: Creamos una cuenta activa
- **WHEN**: Hacemos PATCH a /api/v1/accounts/{id}/deactivate
- **THEN**: Verificamos HTTP 200 y active=false

**Código:**

```java
@Test
@DisplayName("Debe desactivar cuenta exitosamente")
void deactivateAccount_ActiveAccount_ReturnsOk() throws Exception {
    // GIVEN - Create account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    String createResponse = mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String accountId = objectMapper.readTree(createResponse).get("id").asText();

    // WHEN & THEN
    mockMvc.perform(patch("/accounts/" + accountId + "/deactivate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active", is(false)));
}
```

---

### 30. activateAccount_InactiveAccount_ReturnsOk()

**Historia de Usuario:** HU-009 - Activar Cuenta

**Criterio de Aceptación:**
```
Escenario 1: Activación exitosa (End-to-End)
  DADO que existe una cuenta inactiva
  CUANDO envío PATCH a /api/v1/accounts/{id}/activate
  ENTONCES recibo respuesta HTTP 200 OK
  Y la cuenta cambia a estado activo (active=true)
```

**Caso de Prueba:** CP-032

| Campo | Valor |
|-------|-------|
| **ID** | CP-032 |
| **Historia** | HU-009 |
| **Escenario** | Activación E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - Cuenta creada y luego desactivada |
| **Pasos** | 1. POST /accounts (crear)<br>2. PATCH /accounts/{id}/deactivate<br>3. PATCH /accounts/{id}/activate<br>4. Verificar active=true |
| **Resultado Esperado** | - HTTP 200 OK<br>- JSON con active=true |

**Explicación del Test:**
- **GIVEN**: Creamos cuenta y la desactivamos
- **WHEN**: Hacemos PATCH a /api/v1/accounts/{id}/activate
- **THEN**: Verificamos HTTP 200 y active=true

**Código:**

```java
@Test
@DisplayName("Debe activar cuenta exitosamente")
void activateAccount_InactiveAccount_ReturnsOk() throws Exception {
    // GIVEN - Create and deactivate account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1000.00")
    );

    String createResponse = mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String accountId = objectMapper.readTree(createResponse).get("id").asText();

    mockMvc.perform(patch("/accounts/" + accountId + "/deactivate"))
            .andExpect(status().isOk());

    // WHEN & THEN
    mockMvc.perform(patch("/accounts/" + accountId + "/activate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active", is(true)));
}
```

---

### 31. getAccountBalance_ExistingAccount_ReturnsOk()

**Historia de Usuario:** HU-010 - Consultar Saldo

**Criterio de Aceptación:**
```
Escenario 1: Consulta de saldo exitosa (End-to-End)
  DADO que tengo una cuenta con saldo de $1500.50
  CUANDO envío GET a /api/v1/accounts/number/{accountNumber}/balance
  ENTONCES recibo respuesta HTTP 200 OK
  Y la respuesta contiene el saldo exacto como texto plano
```

**Caso de Prueba:** CP-033

| Campo | Valor |
|-------|-------|
| **ID** | CP-033 |
| **Historia** | HU-010 |
| **Escenario** | Consulta de saldo E2E |
| **Precondiciones** | - Base de datos limpia |
| **Datos de Prueba** | - accountNumber: "1234567890"<br>- initialBalance: 1500.50 |
| **Pasos** | 1. POST /accounts con balance 1500.50<br>2. GET /accounts/number/1234567890/balance<br>3. Verificar texto plano "1500.50" |
| **Resultado Esperado** | - HTTP 200 OK<br>- Content-Type: text/plain<br>- Body: "1500.50" |

**Explicación del Test:**
- **GIVEN**: Creamos cuenta con saldo inicial de $1500.50
- **WHEN**: Hacemos GET a /api/v1/accounts/number/{accountNumber}/balance
- **THEN**: Verificamos HTTP 200 y que retorna el saldo correcto como texto plano

**Código:**

```java
@Test
@DisplayName("Debe obtener saldo de cuenta exitosamente")
void getAccountBalance_ExistingAccount_ReturnsOk() throws Exception {
    // GIVEN - Create account
    AccountRequest request = new AccountRequest(
            "1234567890",
            "John Doe",
            "john@example.com",
            new BigDecimal("1500.50")
    );

    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

    // WHEN & THEN
    mockMvc.perform(get("/accounts/number/1234567890/balance"))
            .andExpect(status().isOk())
            .andExpect(content().string("1500.50"));
}
```

---

## Ejecutar Tests

### Comandos Maven

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo tests unitarios
mvn test -Dtest="**/unit/**"

# Ejecutar solo tests de integración
mvn test -Dtest="**/integration/**"

# Ejecutar un test específico
mvn test -Dtest=AccountServiceTest

# Ejecutar con reporte de cobertura
mvn clean test jacoco:report
```

### Resultados en este Proyecto

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0

Desglose:
- AccountService (Unitarios): 10 tests ✅
- TransactionService (Unitarios): 10 tests ✅
- Account API (Integración): 11 tests ✅
```

### Cobertura de Tests

```
┌─────────────────────┬──────────┐
│ Componente          │ Cobertura│
├─────────────────────┼──────────┤
│ AccountService      │   100%   │
│ TransactionService  │   100%   │
│ Account Controller  │   100%   │
│ Transaction Ctrl    │    -     │
├─────────────────────┼──────────┤
│ TOTAL               │   ~85%   │
└─────────────────────┴──────────┘
```

---

## Mejores Prácticas

### ✅ DO (Hacer)

1. **Nombres Descriptivos**
   ```java
   // ✅ BIEN
   void createTransaction_Deposit_Success()

   // ❌ MAL
   void test1()
   ```

2. **Un Assert por Concepto**
   ```java
   // ✅ BIEN - Verificamos el concepto "transacción creada correctamente"
   assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
   assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
   assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));

   // ❌ MAL - Mezclando múltiples conceptos
   assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
   assertThat(account.isActive()).isTrue();  // Concepto diferente
   ```

3. **Tests Independientes**
   ```java
   // ✅ BIEN - Cada test crea su propia data
   @BeforeEach
   void setUp() {
       testAccount = new Account(...);
   }

   // ❌ MAL - Tests dependen del orden de ejecución
   static Account sharedAccount;
   ```

4. **Mensajes de Error Claros**
   ```java
   // ✅ BIEN
   assertThat(response.balance())
       .as("El saldo después del depósito debe ser 1500")
       .isEqualByComparingTo(new BigDecimal("1500.00"));
   ```

### ❌ DON'T (No Hacer)

1. **No usar lógica compleja en tests**
   ```java
   // ❌ MAL
   for (int i = 0; i < 10; i++) {
       // código de test
   }

   // ✅ BIEN - Tests explícitos y simples
   ```

2. **No testear múltiples cosas a la vez**
   ```java
   // ❌ MAL
   void testEverything() {
       // crear cuenta
       // hacer depósito
       // hacer retiro
       // desactivar cuenta
   }

   // ✅ BIEN - Un test por comportamiento
   ```

3. **No ignorar tests fallidos**
   ```java
   // ❌ MAL
   @Disabled("Arreglar después")
   void importantTest() { }
   ```

---

## Glosario de Testing

| Término | Significado |
|---------|-------------|
| **Mock** | Objeto simulado que reemplaza dependencias reales |
| **Stub** | Mock con comportamiento predefinido |
| **Fixture** | Datos de prueba reutilizables |
| **Assertion** | Verificación de un resultado esperado |
| **SUT** | System Under Test (Sistema bajo prueba) |
| **AAA** | Arrange-Act-Assert (Given-When-Then) |
| **TDD** | Test-Driven Development |
| **BDD** | Behavior-Driven Development |

---

## Recursos Adicionales

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

---

## Conclusión

Los tests son una **inversión**, no un costo:

- **Corto plazo**: Requieren tiempo inicial
- **Largo plazo**: Ahorran tiempo, dinero y dolores de cabeza

**Recuerda:**
> "El código sin tests es código heredado por definición." - Michael Feathers

Con 31 tests automatizados, este proyecto tiene una base sólida para crecer con confianza. 🚀
