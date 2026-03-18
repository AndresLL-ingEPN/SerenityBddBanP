================================================================================
  SERENITY BDD - AUTOMATIZACION WEB CON PATRON SCREENPLAY
  Proyecto: Flujo completo de compra en Swaglabs (saucedemo.com)
================================================================================

DESCRIPCION
-----------
Automatizacion del flujo completo de compra en https://www.saucedemo.com
utilizando Serenity BDD 4.0.27 con patron Screenplay (BDD + SOLID).

Flujo cubierto:
  1. Apertura del navegador y autenticacion con credenciales validas
  2. Seleccion de dos productos desde el catalogo
  3. Navegacion al carrito de compras y proceso de checkout
  4. Ingreso de datos del cliente (nombre, apellido, codigo postal)
  5. Confirmacion de compra y validacion del mensaje de exito


================================================================================
  STACK TECNOLOGICO Y VERSIONES REQUERIDAS
================================================================================

  - Java JDK           : 11 (LTS) - REQUERIDO
  - Gradle Wrapper     : 7.6.1    - incluido en gradlew.bat
  - Serenity BDD       : 4.0.27
  - Cucumber           : 7.x (incluido con Serenity 4.0.27)
  - JUnit              : 4.13.2
  - Selenium WebDriver : 4.x (incluido con Serenity 4.0.27)
  - Microsoft Edge     : 146.x   - REQUERIDO en el equipo
  - msedgedriver.exe   : 146.0.3856.59 - incluido en /drivers/
  - Lombok             : 1.18.22
  - Apache POI         : 5.2.3
  - Logback            : 1.4.14
  - AssertJ            : 3.22.0


================================================================================
  PRE-REQUISITOS
================================================================================

  [1] Java JDK 11 instalado
      - Descargar desde:https://adoptium.net/temurin/releases/?version=11
      - Configurar variable de entorno JAVA_HOME apuntando a la carpeta del JDK
      - Agregar %JAVA_HOME%\bin al PATH del sistema

  [2] Verificar instalacion de Java (abrir PowerShell o CMD):
      > java -version
      Salida esperada: openjdk version "11.x.x"

  [3] Microsoft Edge version 146.x instalado
      - Verificar version: abrir Edge -> edge://version
      - Si la version es diferente, descargar el driver compatible desde:
        https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
        y reemplazar el archivo drivers/msedgedriver.exe

  [4] Conexion a Internet activa (necesaria para acceder a saucedemo.com)

  [5] Git (opcional, para clonar el repositorio)


================================================================================
  INSTRUCCIONES DE INSTALACION
================================================================================

  PASO 1: Obtener el proyecto
  ---------------------------
  Opcion A - Clonar con Git:
    > git clone <url-del-repositorio>
    > cd projectBase-main

  Opcion B - Descomprimir archivo ZIP:
    Descomprimir el ZIP en la carpeta deseada y abrir una terminal en:
    C:\ruta\donde\descomprimiste\projectBase-main

  PASO 2: Verificar el driver de Edge
  ------------------------------------
  El archivo drivers/msedgedriver.exe debe coincidir exactamente con la
  version de Edge instalada en tu equipo.

  Si la version de Edge es diferente a 146.x:
    a) Ir a: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
    b) Descargar el driver que coincida con tu version de Edge
    c) Reemplazar el archivo drivers/msedgedriver.exe

  PASO 3: Revisar configuracion de Serenity
  ------------------------------------------
  Archivo: src/test/resources/serenity.conf

  La URL del sitio de pruebas esta configurada como:
    webdriver.base.url.swaglabs = "https://www.saucedemo.com/"

  Para ejecutar sin ventana (modo headless), cambiar:
    #headless.mode = true
  por:
    headless.mode = true


================================================================================
  EJECUCION DE PRUEBAS
================================================================================

  IMPORTANTE: Ejecutar todos los comandos desde la raiz del proyecto
              (donde esta el archivo gradlew.bat)

  ------------------------------------------------------------------
  COMANDO PRINCIPAL (recomendado)
  ------------------------------------------------------------------
  Abre PowerShell o CMD en la carpeta projectBase-main y ejecuta:

    .\gradlew.bat clean test aggregate

  Este comando hace:
    1. clean     -> Elimina resultados de ejecuciones anteriores
    2. test      -> Ejecuta todos los escenarios con tag @FlujoCompleto3
    3. aggregate -> Genera el reporte HTML de Serenity

  ------------------------------------------------------------------
  EJECUTAR CON TAG ESPECIFICO
  ------------------------------------------------------------------
    .\gradlew.bat clean test aggregate -Dcucumber.filter.tags="@FlujoCompleto3"

  ------------------------------------------------------------------
  SOLO COMPILAR (sin ejecutar pruebas)
  ------------------------------------------------------------------
    .\gradlew.bat compileTestJava

  ------------------------------------------------------------------
  VER DEPENDENCIAS DEL PROYECTO
  ------------------------------------------------------------------
    .\gradlew.bat dependencies

  ------------------------------------------------------------------
  EJECUTAR EN MODO HEADLESS (sin abrir navegador)
  ------------------------------------------------------------------
    1. Editar src/test/resources/serenity.conf
    2. Cambiar:  #headless.mode = true
       por:      headless.mode = true
    3. Ejecutar: .\gradlew.bat clean test aggregate

  ------------------------------------------------------------------
  EJECUTAR CONTRA OTRO ENTORNO
  ------------------------------------------------------------------
    .\gradlew.bat clean test aggregate -Denvironment=staging
    (Requiere configurar el entorno en serenity.conf)


================================================================================
  REPORTE DE RESULTADOS
================================================================================

  Tras la ejecucion, abrir en el navegador:
    target/site/serenity/index.html

  El reporte muestra:
    - Resumen: total passed / failed / pending
    - Detalle de cada paso Given/When/Then
    - Capturas de pantalla por cada accion
    - Tiempo de ejecucion por escenario
    - Historial de ejecuciones anteriores (carpeta history/)


================================================================================
  TAGS DISPONIBLES EN LOS FEATURES
================================================================================

  @FlujoCompletoBP -> Flujo completo de compra en Swaglabs (escenario activo)
  @flujoCompleto  -> Nivel Feature completo de Swaglabs


================================================================================
  ESTRUCTURA DE CAPAS (PATRON SCREENPLAY)
================================================================================

  FEATURES (Gherkin)
    |
    v
  STEP DEFINITIONS (Glue)        -> src/test/java/.../glue/
    |
    v
  TASKS (Orchestration)          -> src/main/java/.../tasks/
    |
    v
  INTERACTIONS (Screenplay API)  -> Click, Enter, WaitUntil, etc.
    |
    v
  UI / PAGE OBJECTS (Locators)   -> src/main/java/.../ui/
    |
    v
  MODELS (Data)                  -> src/main/java/.../model/


================================================================================
  SOLUCION DE PROBLEMAS COMUNES
================================================================================

  Error: "Could not find or load main class"
  -> Verificar que JAVA_HOME apunta a JDK 11

  Error: "SessionNotCreatedException: session not created"
  -> La version de msedgedriver.exe no coincide con la version de Edge
  -> Verificar edge://version y descargar el driver correcto

  Error: "No tests found"
  -> Verificar que el tag en Swaglabs.feature sea @FlujoCompleto3
  -> Verificar que el runner apunte a: tags = "@FlujoCompleto3"

  Error: "Connection refused" o "ERR_CONNECTION_REFUSED"
  -> Verificar conexion a Internet
  -> Verificar que https://www.saucedemo.com este accesible

  Gradle no descarga dependencias:
  -> Verificar conexion a Internet
  -> Ejecutar: .\gradlew.bat clean --refresh-dependencies


================================================================================
  CREDENCIALES DE PRUEBA (Swaglabs)
================================================================================

  Usuario  : standard_user
  Password : secret_sauce

  NOTA: Estas credenciales son publicas del sitio de pruebas saucedemo.com


================================================================================
  Proyecto: Serenity BDD 4.0.27 | Screenplay Pattern | Java 11 | Gradle 7.6.1
================================================================================

