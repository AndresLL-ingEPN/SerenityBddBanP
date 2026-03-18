# 🧪 Serenity BDD – Automatización Web Swaglabs (SauceDemo)

> **Perspectiva QA Senior:** La ejecución correcta de este proyecto depende de que el entorno esté configurado de forma **exacta y reproducible**. Cualquier desvío en versiones de JDK, driver o Gradle rompe la suite. Lea cada sección antes de ejecutar.

---

## 📋 Tabla de Contenidos

1. [Stack tecnológico y versiones exactas](#1-stack-tecnológico-y-versiones-exactas)
2. [Arquitectura del proyecto](#2-arquitectura-del-proyecto)
3. [Prerrequisitos del entorno](#3-prerrequisitos-del-entorno)
4. [Configuración paso a paso](#4-configuración-paso-a-paso)
5. [Ejecución de pruebas](#5-ejecución-de-pruebas)
6. [Datos de prueba (Excel)](#6-datos-de-prueba-excel)
7. [Reporte Serenity](#7-reporte-serenity)
8. [Estructura del proyecto](#8-estructura-del-proyecto)
9. [Flujo de automatización](#9-flujo-de-automatización)
10. [Lo que más importa (QA Sr.)](#10-lo-que-más-importa-qa-sr)
11. [Hallazgos y mejoras pendientes](#11-hallazgos-y-mejoras-pendientes)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Stack tecnológico y versiones exactas

> ⚠️ **CRÍTICO:** No usar versiones superiores o inferiores sin validación previa. La combinación Serenity 4.x + JUnit 4 + Java 11 es la probada y estable para este proyecto.

| Tecnología | Versión requerida | Justificación |
|---|---|---|
| **Java (JDK)** | `11` (LTS) | `sourceCompatibility = 11` declarado en `build.gradle` |
| **Gradle (Wrapper)** | `7.6.1` | Definido en `gradle-wrapper.properties` — **no requiere instalación manual** |
| **Serenity BDD Core** | `4.0.27` | Declarado en `build.gradle → serenityCoreVersion` |
| **Serenity Cucumber** | `4.0.27` | Misma versión unificada con el core |
| **Serenity Screenplay** | `4.0.27` | Patrón Actor-Task-UI |
| **Serenity Screenplay WebDriver** | `4.0.27` | Integración con WebDriver |
| **Cucumber JVM** | Gestionado por Serenity 4.0.27 | Transitivo; no declarar manualmente |
| **JUnit** | `4.13.2` | `testImplementation "junit:junit:4.13.2"` |
| **Microsoft Edge (navegador)** | `146.0.3856.59` | Debe coincidir exactamente con el `msedgedriver.exe` |
| **MS Edge WebDriver** | `146.0.3856.59` | Incluido en `drivers/msedgedriver.exe` |
| **Apache POI** | `5.2.3` | Lectura de datos desde Excel (`.xlsx`) |
| **Lombok** | `1.18.22` | Reducción de boilerplate (`@Slf4j`, `@Data`, etc.) |
| **Logback Classic** | `1.4.14` | Sistema de logging estructurado |
| **AssertJ** | `3.22.0` | Aserciones fluidas |
| **JavaFaker** | `1.0.2` | Generación de datos de prueba dinámicos |
| **SLF4J** | `1.7.7` | Fachada de logging |

---

## 2. Arquitectura del proyecto

El proyecto implementa el **patrón Screenplay** (Serenity BDD):

```
Actor
 └── attemptsTo(Task)
       └── Task → Interaction(s)
             └── UI (Target / PageObject)
```

```
src/
├── main/java/
│   └── co.com.screenplay.project/
│       ├── hook/          → OpenWeb (abre el navegador y navega a la URL)
│       ├── model/         → ModelCredentials, ModelCustomer (objetos de datos)
│       ├── tasks/         → FindProduct, MakeLogin, RegisterCustomer, ViewCart
│       ├── ui/            → PageMain, PageProducts, PageCar (localizadores)
│       └── util/          → Constantes, configuración de entorno, lectura de Excel
│
└── test/
    ├── java/
    │   └── co.com.screenplay.project/
    │       ├── runners/   → RunnerOpenWeb (punto de entrada JUnit + Cucumber)
    │       ├── glue/      → SwaglabsGlue (step definitions)
    │       └── stepdefinition/ → Hooks de Screenplay (Stage setup)
    └── resources/
        ├── features/      → Swaglabs.feature (escenarios BDD en Gherkin)
        ├── data/registro/ → dataDemoblaze.xlsx (datos externos de prueba)
        └── serenity.conf  → Configuración de WebDriver y URLs por entorno
```

---

## 3. Prerrequisitos del entorno

### 3.1 Java 11 (JDK)

1. Descargar **JDK 11 LTS** desde [Adoptium Temurin 11](https://adoptium.net/temurin/releases/?version=11) o [Oracle JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).
2. Instalar y configurar la variable de entorno:

```powershell
# Verificar instalación
java -version
# Resultado esperado: openjdk version "11.x.x" o java version "11.x.x"

javac -version
# Resultado esperado: javac 11.x.x
```

3. Asegurarse de que `JAVA_HOME` apunte al JDK 11:

```powershell
# En Windows PowerShell (verificar)
echo $env:JAVA_HOME
# Debe mostrar la ruta al JDK 11, ej: C:\Program Files\Eclipse Adoptium\jdk-11.0.xx-hotspot
```

> ⚠️ **QA Sr. advierte:** Si `JAVA_HOME` apunta a JDK 17 o superior, el proyecto compilará pero puede haber incompatibilidades con Serenity 4.0.27 y la generación del reporte. Usar JDK 11 estrictamente.

### 3.2 Microsoft Edge y WebDriver (versión sincronizada)

> ⚠️ **CRÍTICO — el punto de falla más común en CI/CD:** El `msedgedriver.exe` incluido en `drivers/` es la versión `146.0.3856.59`. El navegador **Microsoft Edge instalado en la máquina DEBE ser exactamente esa versión**.

```powershell
# Verificar versión de Edge instalada
(Get-Item "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe").VersionInfo.FileVersion
# o desde Edge: navegar a  edge://settings/help
```

Si la versión de Edge no coincide con el driver:
- **Actualizar Edge** a `146.0.3856.59` desde [Microsoft Edge Release Schedule](https://docs.microsoft.com/en-us/deployedge/microsoft-edge-release-schedule), **o bien**
- **Reemplazar el driver:** descargar la versión correcta desde [https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/) y reemplazar `drivers/msedgedriver.exe`.

### 3.3 Gradle (NO requiere instalación manual)

El proyecto incluye el **Gradle Wrapper** (`gradlew.bat`). Gradle `7.6.1` se descarga automáticamente en el primer uso.

```powershell
# Verificar que el wrapper funciona
cd C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main
.\gradlew.bat --version
# Resultado esperado: Gradle 7.6.1
```

### 3.4 Conexión a Internet (primera vez)

La primera ejecución requiere conexión para descargar:
- Gradle `7.6.1` (~100 MB)
- Dependencias Maven Central (~200 MB)

Las siguientes ejecuciones usan caché local (`~/.gradle/`).

---

## 4. Configuración paso a paso

### 4.1 Clonar / obtener el proyecto

```powershell
# Si el proyecto está en ZIP, descomprimir en:
# C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main

# Navegar al directorio raíz del proyecto
cd C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main
```

### 4.2 Verificar el archivo `serenity.conf`

Ubicado en: `src/test/resources/serenity.conf`

Valores clave:
```hocon
webdriver {
  driver = edge    # Navegador configurado: Microsoft Edge
}

environments {
  default {
    webdriver.base.url.swaglabs = "https://www.saucedemo.com/"
  }
}
```

> No modificar la URL base a menos que el entorno de prueba haya cambiado. La URL `https://www.saucedemo.com/` es el AUT (Application Under Test).

### 4.3 Verificar el archivo de datos Excel

Ruta: `src/test/resources/data/registro/dataDemoblaze.xlsx`

El archivo debe contener la hoja **`compra`** con las siguientes columnas (sin espacios en los encabezados):

| user | password | producto | producto1 | name | lastname | zip |
|---|---|---|---|---|---|---|
| standard_user | secret_sauce | Sauce Labs Backpack | Sauce Labs Bike Light | John | Doe | 12345 |

> ⚠️ El nombre de la hoja (`compra`) y los encabezados de columna deben coincidir **exactamente** con los referenciados en el feature: `@externaldata@registro/dataDemoblaze.xlsx..compra`

### 4.4 Verificar el driver de Edge

```powershell
# Confirmar que el driver existe en la ruta esperada
Test-Path "C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main\drivers\msedgedriver.exe"
# Debe retornar: True
```

---

## 5. Ejecución de pruebas

### 5.1 Ejecución completa (recomendada)

```powershell
cd C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main

.\gradlew.bat clean test aggregate
```

Este comando ejecuta:
1. `clean` — limpia el build anterior (`build/`)
2. `test` — ejecuta los escenarios marcados con `@FlujoCompletoBP`
3. `aggregate` — genera el reporte HTML de Serenity en `target/site/serenity/`

> ⚠️ **QA Sr. advierte:** Siempre ejecutar `clean` antes de `test`. Sin `clean`, los resultados de ejecuciones anteriores pueden contaminar el reporte o generar falsos positivos/negativos en el `aggregate`.

### 5.2 Ejecución solo de pruebas (sin limpiar)

```powershell
.\gradlew.bat test aggregate
```

### 5.3 Ejecución con tag específico de Cucumber

El tag activo está configurado en `RunnerOpenWeb.java`:

```java
tags = "@FlujoCompletoBP"
```

Para ejecutar un tag diferente desde línea de comandos:

```powershell
.\gradlew.bat clean test aggregate -Dcucumber.filter.tags="@FlujoCompletoBP"
```

### 5.4 Ejecución con modo headless (opcional)

En `src/test/resources/serenity.conf`, descomentar:

```hocon
# headless.mode = true
```

Cambiar a:

```hocon
headless.mode = true
```

> Útil para entornos CI/CD (Jenkins, GitHub Actions, GitLab CI) donde no hay pantalla disponible.

### 5.5 Ver logs en tiempo real

Los logs están configurados con **Logback** (`logback-test.xml`). Durante la ejecución se verán en consola:

```
[GIVEN] Actor: 'cliente' | Usuario: 'standard_user' | Producto inicial: 'Sauce Labs Backpack'
[GIVEN] Ejecutando: OpenWeb → MakeLogin → FindProduct
[GIVEN] ✔ Sesión iniciada y producto 'Sauce Labs Backpack' agregado al carrito
...
[THEN] ✔ Compra confirmada - 'Thank you for your order!' visible en pantalla
```

---

## 6. Datos de prueba (Excel)

El proyecto usa **Apache POI 5.2.3** para leer datos desde Excel mediante el mecanismo `@externaldata`:

```gherkin
Examples:
  | @externaldata@registro/dataDemoblaze.xlsx..compra |
```

**Sintaxis del `@externaldata`:**
- `registro/dataDemoblaze.xlsx` → ruta relativa dentro de `src/test/resources/data/`
- `..compra` → nombre de la hoja dentro del archivo Excel

**Columnas requeridas en la hoja `compra`:**

| Columna | Descripción | Ejemplo |
|---|---|---|
| `user` | Usuario de login en SauceDemo | `standard_user` |
| `password` | Contraseña de login | `secret_sauce` |
| `producto` | Primer producto a agregar al carrito | `Sauce Labs Backpack` |
| `producto1` | Segundo producto a agregar al carrito | `Sauce Labs Bike Light` |
| `name` | Nombre del cliente para el checkout | `John` |
| `lastname` | Apellido del cliente para el checkout | `Doe` |
| `zip` | Código postal para el checkout | `12345` |

> ⚠️ **QA Sr. advierte:** Los nombres de los productos deben coincidir **exactamente** con los textos visibles en `https://www.saucedemo.com/inventory.html`. Cualquier diferencia (mayúsculas, espacios) genera un fallo de localizador en `FindProduct`.

---

## 7. Reporte Serenity

### 7.1 Ubicación del reporte

```
projectBase-main/
└── target/
    └── site/
        └── serenity/
            └── index.html   ← Abrir este archivo en el navegador
```

### 7.2 Abrir el reporte

```powershell
# Abrir automáticamente en el navegador predeterminado
Start-Process "C:\Users\Usuario\Documents\SerenityBddBanP\projectBase-main\target\site\serenity\index.html"
```

### 7.3 Contenido del reporte

El reporte HTML de Serenity incluye:
- ✅ Resumen ejecutivo (passed / failed / pending)
- 📸 Capturas de pantalla por cada acción (`FOR_EACH_ACTION` activo en `serenity.conf`)
- 📋 Detalle de cada escenario con pasos Gherkin
- ⏱️ Tiempo de ejecución por escenario y paso
- 🧪 Cobertura de requerimientos

---

## 8. Estructura del proyecto

```
projectBase-main/
│
├── build.gradle                    ← Definición de dependencias y tareas Gradle
├── settings.gradle                 ← Nombre del proyecto: "projectBase"
├── gradlew / gradlew.bat           ← Gradle Wrapper (usar siempre este, NO gradle global)
│
├── drivers/
│   └── msedgedriver.exe            ← Edge WebDriver v146.0.3856.59 (debe sincronizar con Edge)
│
├── gradle/wrapper/
│   └── gradle-wrapper.properties   ← Apunta a Gradle 7.6.1
│
└── src/
    ├── main/java/.../
    │   ├── hook/
    │   │   └── OpenWeb.java        ← Task: abre el navegador con la URL de serenity.conf
    │   ├── model/demoblaze/
    │   │   ├── ModelCredentials.java   ← DTO: usuario + contraseña
    │   │   └── ModelCustomer.java      ← DTO: nombre, apellido, zip
    │   ├── tasks/
    │   │   ├── MakeLogin.java          ← Task: login con credenciales
    │   │   ├── FindProduct.java        ← Task: busca y agrega producto al carrito
    │   │   ├── ViewCart.java           ← Task: navega al carrito y hace checkout
    │   │   └── RegisterCustomer.java   ← Task: completa el formulario de compra
    │   └── ui/
    │       ├── PageMain.java           ← Localizadores: pantalla de login
    │       ├── PageProducts.java       ← Localizadores: catálogo de productos
    │       └── PageCar.java            ← Localizadores: carrito y checkout
    │
    └── test/
        ├── java/.../
        │   ├── runners/
        │   │   └── RunnerOpenWeb.java   ← Punto de entrada: @RunWith + @CucumberOptions
        │   ├── glue/swaglabs/
        │   │   └── SwaglabsGlue.java    ← Step definitions: Given/And/When/Then
        │   └── stepdefinition/         ← Hooks: configuración del Stage de Screenplay
        └── resources/
            ├── features/
            │   └── Swaglabs.feature    ← Escenario BDD en Gherkin con @externaldata
            ├── data/registro/
            │   └── dataDemoblaze.xlsx  ← Datos de prueba externos
            └── serenity.conf           ← Configuración WebDriver + URLs
```

---

## 9. Flujo de automatización

El escenario cubre el **flujo completo de compra** en [SauceDemo](https://www.saucedemo.com/):

```gherkin
@FlujoCompletoBP
Scenario Outline: Flujo completo de compra con múltiples productos
  Given que el "cliente" inicia sesion con "<user>" y "<password>" y selecciona el producto "<producto>"
  And el cliente agrega el segundo producto "<producto1>" al carrito
  When el cliente procede al checkout con nombre "<name>" apellido "<lastname>" y zip "<zip>"
  Then el cliente verifica que la compra fue realizada exitosamente
```

**Secuencia de ejecución:**

```
1. OpenWeb          → Abre Edge → navega a https://www.saucedemo.com/
2. MakeLogin        → Ingresa usuario y contraseña → click en "Login"
3. FindProduct      → Busca el producto 1 por nombre → "Add to cart"
4. FindProduct      → Busca el producto 2 por nombre → "Add to cart"
5. ViewCart         → Navega al carrito → click en "Checkout"
6. RegisterCustomer → Completa First Name, Last Name, Zip → "Continue" → "Finish"
7. Assertion        → Verifica que "Thank you for your order!" es visible en pantalla
```

---

## 10. Lo que más importa (QA Sr.)

> Esta sección resume los puntos críticos que determinan si la suite es confiable o no.

### ✅ 10.1 Sincronización Edge ↔ EdgeDriver — Prioridad MÁXIMA

La regla de oro: **misma versión de Edge en el navegador y en `msedgedriver.exe`**. Sin esto, la suite no arranca. Verificar antes de cada ejecución en un entorno nuevo.

### ✅ 10.2 JDK 11 — No negociable

El `build.gradle` declara `sourceCompatibility = 11`. JDK 8 no compilará. JDK 17+ puede generar advertencias de módulos y problemas con Serenity 4.0.27 en el procesador de anotaciones de Lombok.

### ✅ 10.3 Usar siempre `gradlew.bat` — Nunca `gradle` directo

El Wrapper garantiza que se usa Gradle 7.6.1, independientemente de lo que esté instalado globalmente. `gradle clean test` puede usar una versión diferente y romper la compatibilidad con los plugins de Serenity.

### ✅ 10.4 Los datos del Excel deben ser consistentes con la UI

Los valores de `producto` y `producto1` en el Excel deben coincidir exactamente con el texto del elemento en la UI de SauceDemo. El localizador `FindProduct` usa texto visible (`normalize-space()`).

### ✅ 10.5 El reporte es evidencia — ejecutar siempre `aggregate`

El paso `aggregate` consolida los resultados de Cucumber con Serenity para generar el reporte en `target/site/serenity/`. Sin `aggregate`, el reporte queda incompleto o vacío.

### ✅ 10.6 Conectividad con el AUT

La aplicación bajo prueba (`https://www.saucedemo.com/`) debe ser accesible desde la red donde se ejecuta la suite. En redes corporativas con proxy, configurar las propiedades JVM:

```powershell
.\gradlew.bat clean test aggregate -Dhttps.proxyHost=PROXY_HOST -Dhttps.proxyPort=PUERTO
```

---

## 11. Hallazgos y mejoras pendientes

> Documentados por QA Sr. post-revisión de código (ver `conclusiones.txt` para detalle completo).

| Prioridad | ID | Hallazgo | Estado |
|---|---|---|---|
| 🔴 ALTA | LA-01 | Localizadores XPath donde hay ID disponible en `PageCar` y `PageProducts` | ⏳ Pendiente |
| 🔴 ALTA | LA-02 | Falta assertion post-login con `LABEL_PRODUCT` (no se valida que el login fue exitoso) | ⏳ Pendiente |
| 🟡 MEDIA | LA-03 | Localizador por clase CSS frágil en `PageProducts.BUTTON_CART` | ⏳ Pendiente |
| 🟡 MEDIA | ET-01 | Solo existe el happy path — sin escenarios negativos (credenciales inválidas, campos vacíos) | ⏳ Pendiente |
| 🟡 MEDIA | ET-02 | Datos de prueba CSV (`WebDataSwagLabs.csv`) presentes pero no integrados al flujo | ⏳ Pendiente |
| 🟢 BAJA | DM-01 | Evaluar WebDriverManager para gestión automática del driver (eliminar `drivers/` del repo) | ⏳ Pendiente |
| 🟢 BAJA | DM-02 | Agregar pruebas unitarias a las Tasks con JUnit + Mockito | ⏳ Pendiente |

**Calificación del proyecto (post-correcciones aplicadas):**

| Dimensión | Calificación |
|---|---|
| Arquitectura Screenplay | 9/10 |
| Estrategia de localizadores | 5/10 |
| Cobertura de escenarios | 4/10 |
| Logging y trazabilidad | 8/10 |
| Configuración del framework | 7/10 |
| Calidad y limpieza del código | 8/10 |
| Documentación técnica | 8/10 |
| **GLOBAL** | **7.5/10** |

---

## 12. Troubleshooting

### ❌ `SessionNotCreatedException: session not created`
**Causa:** Versión de Edge y `msedgedriver.exe` no coinciden.  
**Solución:** Sincronizar versiones. Ver sección [3.2](#32-microsoft-edge-y-webdriver-versión-sincronizada).

### ❌ `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`
**Causa:** El archivo `gradle/wrapper/gradle-wrapper.jar` no está presente.  
**Solución:** Verificar que el archivo existe en `gradle/wrapper/`. Si fue eliminado por error, ejecutar `gradle wrapper --gradle-version 7.6.1` con Gradle instalado globalmente para regenerarlo.

### ❌ `No tests found`
**Causa:** El tag `@FlujoCompletoBP` en `RunnerOpenWeb.java` no coincide con el tag del feature.  
**Solución:** Verificar que `Swaglabs.feature` contiene `@FlujoCompletoBP` en el escenario.

### ❌ `NullPointerException` al leer el Excel
**Causa:** El nombre de la hoja en `dataDemoblaze.xlsx` no es exactamente `compra`, o los encabezados de columna tienen espacios o caracteres especiales.  
**Solución:** Abrir el Excel, verificar el nombre de la pestaña y los encabezados. Ver sección [6](#6-datos-de-prueba-excel).

### ❌ `UnsupportedClassVersionError`
**Causa:** El proyecto fue compilado con JDK 17+ pero se ejecuta con JRE 11, o viceversa.  
**Solución:** Asegurar que `JAVA_HOME` y el JDK activo en el PATH son JDK 11. Ver sección [3.1](#31-java-11-jdk).

### ❌ El reporte `target/site/serenity/index.html` está vacío o falta
**Causa:** Se ejecutó `test` sin `aggregate`.  
**Solución:** Siempre ejecutar `.\gradlew.bat clean test aggregate`.

### ❌ `Element not found` en `FindProduct`
**Causa:** El texto del producto en el Excel no coincide exactamente con el texto visible en la UI.  
**Solución:** Copiar el nombre del producto directamente desde la UI de `https://www.saucedemo.com/inventory.html` y pegarlo en el Excel sin espacios extras.

---

## 🔗 Referencias

- [Serenity BDD Documentation](https://serenity-bdd.github.io/docs/guide/user_guide_intro)
- [Cucumber JVM Documentation](https://cucumber.io/docs/cucumber/)
- [Screenplay Pattern](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
- [SauceDemo (AUT)](https://www.saucedemo.com/)
- [MS Edge WebDriver Downloads](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
- [Adoptium JDK 11](https://adoptium.net/temurin/releases/?version=11)

---

*Generado con perspectiva QA Senior — Lo más importante es que el entorno sea reproducible y que cada ejecución genere evidencia confiable.*
