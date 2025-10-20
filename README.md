# Fintech API

API REST para operaciones bancarias básicas desarrollada con Spring Boot 3.5.6 y Java 21.

## Descripción

Sistema backend para gestión de cuentas bancarias y transacciones financieras (depósitos y retiros). Implementa patrones de arquitectura limpia, validaciones de negocio robustas y suite completa de pruebas unitarias e integración.

## Características Principales

- Gestión completa de cuentas bancarias
- Procesamiento de transacciones (depósitos y retiros)
- Validaciones de negocio en capa de dominio
- Manejo global de excepciones
- API REST versionada (`/api/v1`)
- Suite completa de tests (32 pruebas)
- Documentación de endpoints con Swagger UI y Postman

## Tecnologías

- **Java 21**
- **Spring Boot 3.5.6**
  - Spring Data JPA
  - Spring Web
  - Spring Validation
- **PostgreSQL** (Producción)
- **H2 Database** (Tests)
- **Lombok**
- **Maven**
- **JUnit 5**
- **Mockito**
- **AssertJ**

## Requisitos Previos

- Java 21 o superior
- Maven 3.8+
- PostgreSQL 14+ (para producción)

## Instalación

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd fintech-api
```

### 2. Configurar base de datos PostgreSQL

Crear base de datos:

```sql
CREATE DATABASE fintech_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE fintech_db TO postgres;
```

### 3. Configurar application.properties (opcional)

Editar `src/main/resources/application.properties` si necesitas cambiar credenciales:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fintech_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Compilar el proyecto

```bash
mvn clean install
```

### 5. Ejecutar tests

```bash
mvn test
```

### 6. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api/v1`

## Documentación de la API

### Swagger UI

Este proyecto incluye documentación interactiva completa de la API con Swagger/OpenAPI 3.0.

**Características de la documentación:**
- Todos los endpoints de Accounts (8) y Transactions (5) completamente documentados
- Descripciones detalladas de cada operación
- Esquemas de request/response documentados
- Códigos de respuesta HTTP con ejemplos
- Posibilidad de probar endpoints directamente desde el navegador

**Acceder a Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

### Postman Collection

El proyecto incluye una collection completa de Postman en el archivo `postman_collection.json`.

Importar en Postman:
1. Abrir Postman
2. Click en "Import"
3. Seleccionar `postman_collection.json`
4. La collection incluye variables de entorno y tests automáticos

## Endpoints de la API

### Accounts

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Crear nueva cuenta |
| GET | `/api/v1/accounts` | Listar todas las cuentas |
| GET | `/api/v1/accounts/{id}` | Obtener cuenta por ID |
| GET | `/api/v1/accounts/number/{accountNumber}` | Obtener cuenta por número |
| GET | `/api/v1/accounts/number/{accountNumber}/balance` | Consultar saldo |
| GET | `/api/v1/accounts/active` | Listar cuentas activas |
| PATCH | `/api/v1/accounts/{id}/activate` | Activar cuenta |
| PATCH | `/api/v1/accounts/{id}/deactivate` | Desactivar cuenta |

### Transactions

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/transactions` | Crear nueva transacción |
| GET | `/api/v1/transactions` | Listar todas las transacciones |
| GET | `/api/v1/transactions/{id}` | Obtener transacción por ID |
| GET | `/api/v1/transactions/account/{accountNumber}` | Listar transacciones por cuenta |

## Ejemplos de Uso

### Crear una cuenta

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "1234567890",
    "ownerName": "Juan Pérez",
    "ownerEmail": "juan@example.com",
    "initialBalance": 1000.00
  }'
```

### Realizar un depósito

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "1234567890",
    "type": "DEPOSIT",
    "amount": 500.00,
    "description": "Depósito de nómina"
  }'
```

### Realizar un retiro

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "1234567890",
    "type": "WITHDRAW",
    "amount": 200.00,
    "description": "Retiro en cajero"
  }'
```

### Consultar saldo

```bash
curl http://localhost:8080/api/v1/accounts/number/1234567890/balance
```

## Estructura del Proyecto

```
fintech-api/
├── src/
│   ├── main/
│   │   ├── java/com/fintech/api/
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/
│   │   │   │   ├── request/     # DTOs de entrada
│   │   │   │   └── response/    # DTOs de salida
│   │   │   ├── exception/       # Excepciones personalizadas
│   │   │   ├── handler/         # Manejador global de excepciones
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   └── service/         # Lógica de negocio
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/fintech/
│           ├── unit/            # Tests unitarios
│           └── integration/     # Tests de integración
├── pom.xml
├── README.md
├── TEST.md                      # Documentación de tests
└── postman_collection.json      # Collection de Postman
```

## Reglas de Negocio

### Cuentas
- El número de cuenta debe ser único
- El número de cuenta debe tener entre 10 y 20 dígitos
- El email debe ser válido
- El saldo inicial debe ser mayor o igual a cero
- Solo se pueden realizar transacciones en cuentas activas

### Transacciones
- El monto debe ser mayor a cero
- Los retiros no pueden exceder el saldo disponible
- Cada transacción registra el saldo resultante
- Las transacciones se ordenan por timestamp descendente

## Testing

El proyecto incluye 32 tests automatizados:

- **10 tests unitarios** de AccountService
- **10 tests unitarios** de TransactionService
- **11 tests de integración** de Account endpoints
- **1 test** de arranque de aplicación

### Ejecutar tests

```bash
# Todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest="**/unit/**"

# Solo tests de integración
mvn test -Dtest="**/integration/**"

# Con reporte de cobertura
mvn clean test jacoco:report
```

Para más información sobre la metodología de testing, consultar [TEST.md](TEST.md).

## CI/CD y Calidad de Código

El proyecto utiliza GitHub Actions para automatizar la integración continua y análisis de calidad.

### Workflows Configurados

#### 1. CI - Build & Test (`.github/workflows/ci.yml`)

Se ejecuta automáticamente en cada push o pull request a las ramas `main` y `develop`.

**Funcionalidad:**
- Configura PostgreSQL como servicio para tests de integración
- Compila el proyecto con Maven
- Ejecuta todos los tests (32 tests unitarios e integración)
- Genera artefacto JAR de la aplicación
- Comenta resultados en pull requests

**Ejecutar localmente:**
```bash
mvn clean compile
mvn test
mvn package -DskipTests
```

#### 2. Code Quality Analysis (`.github/workflows/code-quality.yml`)

Análisis automático de calidad de código en cada push o pull request.

**Herramientas integradas:**
- **JaCoCo**: Cobertura de código (mínimo 70%)
- **Checkstyle**: Validación de estilo y convenciones Java
- **SpotBugs**: Detección automática de bugs

**Ejecutar localmente:**
```bash
# Reporte de cobertura
mvn clean test jacoco:report

# Ver reporte HTML
open target/site/jacoco/index.html

# Checkstyle
mvn checkstyle:check

# SpotBugs
mvn spotbugs:check
```

### Configuración de Calidad

#### JaCoCo Coverage
- Umbral mínimo: 70% de cobertura de líneas
- Reportes generados en: `target/site/jacoco/`
- Configuración: `pom.xml` (líneas 103-143)

#### Checkstyle
- Basado en Google Java Style Guide
- Configuración: `checkstyle.xml`
- Supresiones: `checkstyle-suppressions.xml`
- Validaciones principales:
  - Convenciones de nombres
  - Longitud de métodos (max 150 líneas)
  - Longitud de líneas (max 120 caracteres)
  - Complejidad ciclomática (max 15)
  - Imports y espacios en blanco

#### SpotBugs
- Nivel de esfuerzo: Máximo
- Threshold: Low (detecta todos los bugs)
- Reportes XML en: `target/spotbugsXml.xml`

### Badges de Estado

Puedes agregar badges al README después del primer workflow exitoso:

```markdown
![CI](https://github.com/tu-usuario/fintech-api/workflows/CI%20-%20Build%20%26%20Test/badge.svg)
![Code Quality](https://github.com/tu-usuario/fintech-api/workflows/Code%20Quality%20Analysis/badge.svg)
```

### Integración con SonarCloud (Opcional)

Para habilitar análisis con SonarCloud:

1. Crear cuenta en [sonarcloud.io](https://sonarcloud.io)
2. Agregar el proyecto y obtener token
3. Agregar secrets en GitHub:
   - `SONAR_TOKEN`: Token de SonarCloud
4. Descomentar sección SonarCloud en `.github/workflows/code-quality.yml`
5. Actualizar `projectKey` y `organization` en el workflow

## Documentación Adicional

### Swagger Documentation

La API incluye documentación interactiva completa generada automáticamente con Swagger/OpenAPI 3.0.

**Características:**
- Interfaz interactiva para probar endpoints
- Definiciones de esquemas de datos
- Códigos de respuesta HTTP documentados
- Ejemplos de requests y responses

**Cómo usar Swagger UI:**
1. Iniciar la aplicación: `mvn spring-boot:run`
2. Abrir navegador en: `http://localhost:8080/swagger-ui.html`
3. Explorar y probar endpoints directamente desde el navegador

### Postman Collection

El proyecto incluye una collection de Postman con todos los endpoints organizados por carpetas. Importar el archivo `postman_collection.json` en Postman.

La collection incluye:
- Variables de entorno configurables (`baseUrl`, `accountId`, `accountNumber`, `transactionId`)
- Ejemplos de requests para todos los endpoints
- Tests automáticos que validan respuestas
- Organización por recursos (Accounts, Transactions, Error Examples)
- Scripts que auto-guardan IDs para usar en requests subsiguientes

## Manejo de Errores

La API utiliza un manejador global de excepciones que retorna respuestas consistentes:

### Ejemplo de respuesta de error

```json
{
  "message": "Account with number 1234567890 not found",
  "timestamp": "2025-10-19T18:03:45.123456"
}
```

### Códigos de estado HTTP

- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Datos de entrada inválidos
- `404 Not Found` - Recurso no encontrado
- `409 Conflict` - Conflicto (ej: cuenta duplicada)
- `500 Internal Server Error` - Error del servidor

## Validaciones

### AccountRequest
- `accountNumber`: Requerido, 10-20 dígitos
- `ownerName`: Requerido, 3-100 caracteres
- `ownerEmail`: Requerido, formato email válido
- `initialBalance`: Requerido, >= 0

### TransactionRequest
- `accountNumber`: Requerido
- `type`: Requerido (DEPOSIT o WITHDRAW)
- `amount`: Requerido, > 0
- `description`: Opcional

## Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir un Pull Request

## Convenciones de Código

- Nombres de métodos de test en inglés simple
- Anotaciones `@DisplayName` en español
- Comentarios en español
- Seguir patrón Given-When-Then en tests
- Usar Lombok para reducir boilerplate

## Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## Autor

Desarrollado como proyecto de demostración de arquitectura Spring Boot con mejores prácticas.

## Contacto

Para preguntas o sugerencias, por favor abrir un issue en el repositorio.
