# 🧪 Serenity BDD — Automatización Web con Patrón Screenplay

Proyecto de automatización de pruebas funcionales sobre **Swaglabs (SauceDemo)** usando el patrón **Screenplay** con **Serenity BDD + Cucumber + JUnit**.

---

## 📋 Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Stack tecnológico y versiones](#stack-tecnológico-y-versiones)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Pre-requisitos](#pre-requisitos)
- [Instalación y configuración](#instalación-y-configuración)
- [Ejecución de pruebas](#ejecución-de-pruebas)
- [Reporte de resultados](#reporte-de-resultados)
- [Configuración de entornos](#configuración-de-entornos)

---

## Descripción del proyecto

Automatización del flujo completo de compra en [https://www.saucedemo.com](https://www.saucedemo.com):

1. Apertura del navegador y autenticación con credenciales válidas
2. Selección de múltiples productos desde el catálogo
3. Navegación al carrito de compras y proceso de checkout
4. Ingreso de datos del cliente y confirmación de compra
5. Validación del mensaje de compra exitosa

El diseño sigue la arquitectura **Screenplay Pattern** (principios SOLID) con separación de responsabilidades:
`Actor → Task → Interaction → UI (Target / Page Object)`

---

## Stack tecnológico y versiones

| Tecnología             | Versión         | Rol                                       |
|------------------------|-----------------|-------------------------------------------|
| **Java JDK**           | 11 (LTS)        | Lenguaje base del proyecto                |
| **Gradle Wrapper**     | 7.6.1           | Build tool y gestión de dependencias      |
| **Serenity BDD**       | 4.0.27          | Framework de pruebas + reporting HTML     |
| **Cucumber**           | 7.x (bundled)   | Motor BDD para features Gherkin           |
| **JUnit**              | 4.13.2          | Runner de pruebas                         |
| **Selenium WebDriver** | 4.x (bundled)   | Driver de automatización web              |
| **Microsoft Edge**     | 146.x           | Navegador de ejecución                    |
| **msedgedriver**       | 146.0.3856.59   | Driver local para Edge                    |
| **Lombok**             | 1.18.22         | Reducción de boilerplate (getters/setters)|
| **Apache POI**         | 5.2.3           | Lectura de datos desde Excel (.xlsx)      |
| **JavaFaker**          | 1.0.2           | Generación de datos de prueba aleatorios  |
| **Logback**            | 1.4.14          | Logging estructurado                      |
| **AssertJ**            | 3.22.0          | Assertions fluidas                        |

---

## Estructura del proyecto

```
projectBase-main/
├── build.gradle                          # Configuración de build y dependencias
├── settings.gradle                       # Nombre del proyecto Gradle
├── drivers/
│   └── msedgedriver.exe                  # Driver Edge v146.0.3856.59
└── src/
    ├── main/java/co/com/screenplay/project/
    │   ├── enhancers/
    │   │   └── LocalEdgeDriverEnhancer.java  # Configura driver antes de cada escenario
    │   ├── exceptions/                       # Excepciones personalizadas del framework
    │   ├── hook/
    │   │   └── OpenWeb.java                  # Task: abre navegador en URL configurada
    │   ├── model/
    │   │   ├── demoblaze/
    │   │   │   ├── ModelCredentials.java     # Modelo de credenciales de login
    │   │   │   └── ModelCustomer.java        # Modelo de datos del cliente
    │   │   └── ModelUser.java                # Modelo de usuario (ParaBank)
    │   ├── tasks/                            # Tasks del patrón Screenplay
    │   │   ├── FindProduct.java              # Busca y agrega producto al carrito
    │   │   ├── MakeLogin.java                # Realiza login en Swaglabs
    │   │   ├── RegisterCustomer.java         # Completa formulario de checkout
    │   │   └── ViewCart.java                 # Navega al carrito y abre checkout
    │   ├── ui/                               # Page Objects (locators)
    │   │   ├── PageMain.java                 # Login page de Swaglabs
    │   │   ├── PageProducts.java             # Catálogo de productos
    │   │   └── PageCar.java                  # Carrito y checkout
    │   └── util/                             # Utilidades transversales
    │       ├── ExcelDataReader.java          # Lector de archivos Excel
    │       ├── EnvironmentConfig.java        # Acceso a variables de entorno
    │       └── overwritedata/                # Pre-procesamiento de features
    └── test/
        ├── java/co/com/screenplay/project/
        │   ├── glue/swaglabs/
        │   │   └── swaglabsGlue.java         # Step definitions de Swaglabs
        │   ├── runners/
        │   │   └── RunnerOpenWeb.java        # JUnit Runner con pre-procesado
        │   └── stepdefinition/hook/
        │       └── Hook.java                 # @Before setup del stage
        └── resources/
            ├── serenity.conf                 # Configuración de Serenity y WebDriver
            └── features/
                └── Swaglabs.feature          # Escenario BDD del flujo de compra
```

---

## Pre-requisitos

1. **Java JDK 11** instalado y `JAVA_HOME` configurado
2. **Microsoft Edge versión 146.x** instalado
3. **msedgedriver.exe v146.0.3856.59** — ya incluido en `/drivers/`
4. Conexión a Internet (para `https://www.saucedemo.com`)

### Verificar Java

```powershell
java -version
# Salida esperada: openjdk version "11.x.x"
```

### Verificar versión de Edge

Abrir Edge → barra de dirección → `edge://version`  
La versión debe ser **146.x**. Si difiere, reemplazar `drivers/msedgedriver.exe` con el driver de la versión correcta desde:
[https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)

---

## Instalación y configuración

### 1. Clonar el repositorio

```powershell
git clone <url-del-repositorio>
cd projectBase-main
```

### 2. Verificar configuración en `src/test/resources/serenity.conf`

```hocon
environments {
  default {
    webdriver.base.url.swaglabs = "https://www.saucedemo.com/"
  }
}
```

### 3. (Opcional) Modo headless

Para ejecutar sin abrir ventana del navegador, editar `serenity.conf`:

```hocon
headless.mode = true
```

---

## Ejecución de pruebas

> Todos los comandos se ejecutan desde la raíz del proyecto: `projectBase-main/`

### ▶ Comando completo recomendado

```powershell
.\gradlew.bat clean test aggregate
```

Este comando realiza:
1. `clean` — elimina builds anteriores
2. `test` — ejecuta todos los escenarios del tag `@FlujoCompleto3`
3. `aggregate` — consolida y genera el reporte HTML de Serenity

### ▶ Ejecutar solo con un tag específico

```powershell
.\gradlew.bat clean test aggregate -Dcucumber.filter.tags="@FlujoCompleto3"
```

### ▶ Verificar que Gradle descarga dependencias correctamente

```powershell
.\gradlew.bat dependencies
```

### ▶ Compilar sin ejecutar pruebas

```powershell
.\gradlew.bat compileTestJava
```

---

## Reporte de resultados

Tras la ejecución, el reporte HTML de Serenity se genera en:

```
target/site/serenity/index.html
```

Abrir el archivo en cualquier navegador para ver:
- ✅ Resumen de resultados (passed / failed / pending)
- 📋 Detalle de cada paso Gherkin (Given / When / Then)
- 📸 Capturas de pantalla automáticas por acción
- ⏱ Tiempo de ejecución por escenario

---

## Configuración de entornos

```hocon
# serenity.conf
environments {
  default {
    webdriver.base.url.swaglabs = "https://www.saucedemo.com/"
  }
  staging {
    webdriver.base.url.swaglabs = "https://staging.saucedemo.com/"
  }
}
```

Ejecutar contra entorno específico:

```powershell
.\gradlew.bat clean test aggregate -Denvironment=staging
```

---

## Tags disponibles

| Tag               | Descripción                               |
|-------------------|-------------------------------------------|
| `@FlujoCompleto3` | Flujo completo de compra en Swaglabs      |
| `@flujoCompleto`  | Feature completa de Swaglabs (nivel suite)|

---

*Proyecto desarrollado con Serenity BDD 4.0.27 · Screenplay Pattern · Java 11*
