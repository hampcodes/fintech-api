# Guía de Uso - Endpoints de Reportes

## 🔐 Autenticación
Todos los endpoints requieren **rol ADMIN** y token JWT.

```http
Authorization: Bearer {{adminToken}}
```

---

## 📅 Formato de Fechas

**IMPORTANTE**: Use el formato `YYYY-MM-DD`

### ✅ Ejemplos Correctos:
- `2025-01-31` - 31 de enero de 2025
- `2025-02-28` - 28 de febrero de 2025
- `2025-11-30` - 30 de noviembre de 2025
- `2025-12-31` - 31 de diciembre de 2025

### ❌ Fechas Inválidas (Causarán Error 400):
- `2025-11-31` - ❌ Noviembre solo tiene 30 días
- `2025-02-30` - ❌ Febrero no tiene 30 días
- `2025-04-31` - ❌ Abril solo tiene 30 días
- `31-01-2025` - ❌ Formato incorrecto (debe ser YYYY-MM-DD)
- `2025/01/31` - ❌ Use guiones (-) no barras (/)

---

## 📊 1. REPORTES DE TRANSACCIONES

### 1.1 Transacciones por Período
**Para gráficos de líneas o barras temporales**

```http
GET /api/v1/reports/transactions/by-period?startDate=2025-01-01&endDate=2025-01-31
```

**Respuesta:**
```json
[
  {
    "period": "2025-01-15",
    "totalTransactions": 45,
    "totalDeposits": 30,
    "totalWithdrawals": 15,
    "totalDepositAmount": 15000.00,
    "totalWithdrawalAmount": 5000.00,
    "netCashFlow": 10000.00
  }
]
```

**Uso en gráficos:**
- **Eje X**: `period`
- **Eje Y**: `totalTransactions`, `netCashFlow`, etc.

---

### 1.2 Transacciones por Tipo
**Para gráficos de pie/dona**

```http
GET /api/v1/reports/transactions/by-type?startDate=2025-01-01&endDate=2025-01-31
```

**Respuesta:**
```json
[
  {
    "type": "DEPOSIT",
    "count": 150,
    "totalAmount": 50000.00,
    "percentage": 62.5
  },
  {
    "type": "WITHDRAW",
    "count": 90,
    "totalAmount": 30000.00,
    "percentage": 37.5
  }
]
```

---

### 1.3 Top Cuentas por Transacciones
**Para ranking/tabla**

```http
GET /api/v1/reports/transactions/top-accounts?startDate=2025-01-01&endDate=2025-01-31&limit=10
```

**Parámetros:**
- `limit` (opcional, default: 10): Número de cuentas a mostrar

---

## 🏦 2. REPORTES DE CUENTAS

### 2.1 Distribución de Saldos
**Para histogramas**

```http
GET /api/v1/reports/accounts/balance-distribution
```

**Respuesta:**
```json
[
  {
    "range": "0-1000",
    "accountCount": 45,
    "totalBalance": 30000.00
  },
  {
    "range": "1000-5000",
    "accountCount": 30,
    "totalBalance": 90000.00
  }
]
```

---

### 2.2 Top Cuentas por Saldo

```http
GET /api/v1/reports/accounts/top-by-balance?limit=10
```

---

## 👥 3. REPORTES DE USUARIOS

### 3.1 Usuarios Más Activos

```http
GET /api/v1/reports/users/top-by-activity?startDate=2025-01-01&endDate=2025-01-31&limit=10
```

### 3.2 Crecimiento de Usuarios

```http
GET /api/v1/reports/users/growth?startDate=2025-01-01&endDate=2025-01-31
```

---

## 📈 4. DASHBOARD

### Métricas Consolidadas

```http
GET /api/v1/reports/dashboard
```

**Respuesta:**
```json
{
  "totalUsers": 500,
  "activeUsers": 450,
  "totalAccounts": 750,
  "activeAccounts": 700,
  "totalBalance": 5000000.00,
  "totalTransactions": 15000,
  "todayTransactions": 120,
  "todayVolume": 50000.00,
  "averageTransactionAmount": 3333.33,
  "userGrowthRate": 5.2,
  "transactionGrowthRate": 8.5,
  "volumeGrowthRate": 12.3
}
```

---

## 🔄 5. COMPARACIÓN DE PERÍODOS

### Comparar Mes Actual vs Mes Anterior

```http
GET /api/v1/reports/comparison?currentStartDate=2025-02-01&currentEndDate=2025-02-28&previousStartDate=2025-01-01&previousEndDate=2025-01-31
```

**Ejemplo para comparar semanas:**
```http
GET /api/v1/reports/comparison?currentStartDate=2025-01-15&currentEndDate=2025-01-21&previousStartDate=2025-01-08&previousEndDate=2025-01-14
```

---

## ⚡ REPORTES RÁPIDOS

### Hoy
```http
GET /api/v1/reports/today
```

### Semana Actual
```http
GET /api/v1/reports/current-week
```

### Mes Actual
```http
GET /api/v1/reports/current-month
```

---

## 🚨 Manejo de Errores

### Error 400 - Fecha Inválida
```json
{
  "message": "Invalid date for parameter 'endDate': 2025-11-31. Please check the date is valid (e.g., November has only 30 days)",
  "status": 400
}
```

**Solución**: Verifique que la fecha sea válida según el calendario.

### Error 400 - Formato Incorrecto
```json
{
  "message": "Invalid value for parameter 'startDate': 31-01-2025. Expected format: YYYY-MM-DD (e.g., 2025-11-30)",
  "status": 400
}
```

**Solución**: Use formato `YYYY-MM-DD`

### Error 401 - No Autenticado
```json
{
  "message": "Unauthorized",
  "status": 401
}
```

**Solución**: Incluya el token JWT en el header `Authorization: Bearer {{token}}`

### Error 403 - Sin Permisos
```json
{
  "message": "Access Denied",
  "status": 403
}
```

**Solución**: Solo usuarios con rol ADMIN pueden acceder a estos endpoints.

---

## 📅 Días por Mes (Referencia Rápida)

| Mes | Días | Ejemplo Válido |
|-----|------|----------------|
| Enero | 31 | `2025-01-31` |
| Febrero | 28/29* | `2025-02-28` |
| Marzo | 31 | `2025-03-31` |
| Abril | 30 | `2025-04-30` |
| Mayo | 31 | `2025-05-31` |
| Junio | 30 | `2025-06-30` |
| Julio | 31 | `2025-07-31` |
| Agosto | 31 | `2025-08-31` |
| Septiembre | 30 | `2025-09-30` |
| Octubre | 31 | `2025-10-31` |
| Noviembre | 30 | `2025-11-30` ✅ |
| Diciembre | 31 | `2025-12-31` |

*Febrero tiene 29 días en años bisiestos (2024, 2028, etc.)

---

## 💡 Tips de Integración Frontend

### TypeScript/Angular Ejemplo

```typescript
// Servicio de Reportes
@Injectable()
export class ReportService {
  constructor(private http: HttpClient) {}

  getTransactionsByPeriod(startDate: string, endDate: string) {
    return this.http.get<TransactionReportDTO[]>(
      `/api/v1/reports/transactions/by-period`,
      { params: { startDate, endDate } }
    );
  }

  getDashboard() {
    return this.http.get<DashboardMetricsDTO>(
      `/api/v1/reports/dashboard`
    );
  }
}

// Uso en componente
this.reportService.getTransactionsByPeriod('2025-01-01', '2025-01-31')
  .subscribe(data => {
    // data está listo para Chart.js, Recharts, etc.
    this.chartData = data;
  });
```

### React/Recharts Ejemplo

```javascript
import { LineChart, Line, XAxis, YAxis } from 'recharts';

function TransactionChart() {
  const [data, setData] = useState([]);

  useEffect(() => {
    fetch('/api/v1/reports/transactions/by-period?startDate=2025-01-01&endDate=2025-01-31', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(setData);
  }, []);

  return (
    <LineChart data={data}>
      <XAxis dataKey="period" />
      <YAxis />
      <Line type="monotone" dataKey="totalTransactions" stroke="#8884d8" />
      <Line type="monotone" dataKey="netCashFlow" stroke="#82ca9d" />
    </LineChart>
  );
}
```

---

## 🎯 Casos de Uso Comunes

### 1. Dashboard de Administración
```
GET /api/v1/reports/dashboard
```
Muestra KPIs principales en cards.

### 2. Análisis de Transacciones del Mes
```
GET /api/v1/reports/transactions/by-period?startDate=2025-01-01&endDate=2025-01-31
GET /api/v1/reports/transactions/by-type?startDate=2025-01-01&endDate=2025-01-31
```

### 3. Comparar Rendimiento Mensual
```
GET /api/v1/reports/comparison?currentStartDate=2025-02-01&currentEndDate=2025-02-28&previousStartDate=2025-01-01&previousEndDate=2025-01-31
```

### 4. Identificar Usuarios/Cuentas Más Activos
```
GET /api/v1/reports/users/top-by-activity?startDate=2025-01-01&endDate=2025-01-31&limit=10
GET /api/v1/reports/transactions/top-accounts?startDate=2025-01-01&endDate=2025-01-31&limit=10
```

---

**Documentación generada para Fintech API - Sistema de Reportes Gráficos**
