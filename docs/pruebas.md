# Pruebas del Sistema de Gestión de Solicitudes Académicas

Rama: `feature/rodri-pruebas` (creada desde `main`, que ya incluye los merges de Tomás y Nazareno).
Comparación contra el tag `estado-inicial` (commit `46003e9`).

## 1. Qué se agregó para probar

- En `pom.xml` se agregó **solo** JUnit 5 (`junit-jupiter 5.10.2`, scope `test`) y el plugin `maven-surefire-plugin 3.2.5`, que hace falta para que Maven ejecute JUnit 5.
- Se crearon las pruebas en `src/test/java/ar/edu/metodologia/`.
- No se tocó ningún archivo de `src/main` ni el `README.md`.

| Archivo de prueba | Qué cubre | Cantidad |
|---|---|---|
| `EstudianteServiceTest` | Registro, IDs, validación de datos, sin datos | 9 |
| `SolicitudServiceTest` | Alta, rechazos, búsquedas, responsables, estados, reporte, sin datos | 27 |
| `ResponsableServiceTest` | Alta y búsqueda de responsables, inexistentes, sin datos | 3 |
| `ModeloYEnumsTest` | `EstadoSolicitud`, `Prioridad`, `DatosSolicitud`, `Solicitud.toString` | 8 |
| `SistemaConsolaTest` | El menú por consola, simulando el teclado con `System.in` | 18 |
| `DatosInicialesTest` | Que los datos de ejemplo sean los mismos que en la versión inicial | 1 |
| **Total** | | **66** |

## 2. Casos de prueba

Las pruebas usan datos inventados (por ejemplo "Estudiante Prueba"). Todas comprueban resultados con `assert`, no solo ejecutan métodos.

| N.º | Requisito | Caso | Resultado esperado | Resultado obtenido* |
|---|---|---|---|---|
| 1 | Registro correcto de estudiantes | Registrar un estudiante válido | Queda en la lista con nombre, email e ID 1 | OK |
| 1 | | Registrar tres estudiantes | IDs 1, 2 y 3 | OK |
| 2 | Estudiantes inválidos | Nombre vacío / email vacío / email sin `@` | `datosValidos` devuelve `false` | OK |
| 2 | | Por consola, email sin `@` o nombre con solo espacios | Mensaje "Datos inválidos…" y no se guarda | OK |
| 3 | Solicitud con estudiante existente | Registrar solicitud del estudiante 2 | Se crea con ID 1, estado PENDIENTE y sin responsable | OK |
| 4 | Solicitudes inválidas | Estudiante inexistente, tipo vacío, descripción vacía, prioridad nula | Devuelve `null` y no se guarda | OK |
| 4 | | Un rechazo no consume el siguiente ID | La solicitud válida siguiente tiene ID 1 | OK |
| 4 | | Por consola: estudiante inexistente, ID no numérico, prioridad inválida | Mensaje de error y no se guarda | OK |
| 5 | Buscar por ID | ID existente / inexistente | Devuelve la solicitud / `null` | OK |
| 6 | Buscar por estado y tipo | Una solicitud en cada estado; dos de tipo EXAMEN | Cada búsqueda devuelve las que corresponden | OK |
| 6 | | Sin coincidencias, tipo en minúscula, estado `null` | Lista vacía | OK |
| 7 | Asignar responsable | Asignar y reasignar | Queda el último responsable asignado | OK |
| 8 | Inexistentes | Solicitud inexistente / responsable `null` | Devuelve `false` y no cambia nada | OK |
| 8 | | Buscar responsable inexistente | Devuelve `null` | OK |
| 9 | Cambiar estado | PENDIENTE → EN_PROCESO → RESUELTA | El estado cambia en cada paso | OK |
| 10 | Estados inválidos | Estado `null`, "CANCELADA", "pendiente", vacío | `false` / `null`, la solicitud no cambia | OK |
| 11 | Reporte por estado | 4 solicitudes repartidas en estados | Contadores 1 / 1 / 2 y total 4; la suma coincide | OK |
| 12 | Sin datos | Sin estudiantes, solicitudes ni responsables | Listas vacías, búsquedas en `null`, reporte en 0 | OK |
| 12 | | Por consola sin datos | "No hay solicitudes." y reporte en 0 | OK |
| – | Datos de ejemplo | Cargar `DatosIniciales` | 3 estudiantes, 2 responsables, 3 solicitudes con los mismos estados | OK |

\* "OK" = resultado obtenido en la corrida con el mini-runner descripto en la sección 3. **No** es todavía el resultado de `mvn clean test`; ver más abajo.

### Dos pruebas que documentan limitaciones (no son errores corregidos)

- `nombreConSoloEspaciosPasaLaValidacionDelServicio`: `EstudianteService.datosValidos("   ", ...)` devuelve `true`. La consola hace `trim()` antes, por eso desde el menú no se puede cargar, pero el servicio por sí solo no lo protege.
- `datosSolicitudConTipoNullLanzaExcepcion`: `DatosSolicitud.sonValidos()` lanza `NullPointerException` si el tipo es `null`.

Si se corrigen estos comportamientos, estas dos pruebas tienen que actualizarse.

## 3. Resultados de ejecución (lo que realmente se pudo verificar)

### Qué se pudo y qué no

| Verificación | Estado |
|---|---|
| Compilar con Java 21 (`javac --release 21`), versión final | Se pudo: sin errores |
| Compilar con Java 21 (`javac --release 21`), versión inicial (tag) | Se pudo: sin errores |
| Ejecutar las 66 pruebas con un mini-runner propio | Se pudo: **66 ejecutadas, 66 aprobadas, 0 fallidas** |
| Ejecutar el sistema por consola con un guion de teclado | Se pudo |
| Comparar consola final vs. `estado-inicial` | Se pudo: salidas idénticas |
| **`mvn clean test` con JUnit 5 real** | **No se pudo en el entorno donde se trabajó** |

**Por qué no se pudo correr Maven:** el entorno donde se prepararon las pruebas no tiene acceso a Maven Central (el proxy respondió `403` al intentar bajar `repo.maven.apache.org`), así que Maven no pudo descargar ni siquiera sus plugins básicos ni JUnit. No se usó ningún atajo para saltear esa restricción.

**Qué es el mini-runner:** un programa chiquito, descartable y fuera del repositorio, que imita las anotaciones (`@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`) y las aserciones usadas. Sirve para detectar errores de lógica en las pruebas, pero **no reemplaza a JUnit**. Los números de arriba (66/66) salen de ese runner.

### Resultado de `mvn clean test`

> **PENDIENTE: completar con la ejecución real en IntelliJ o en la terminal.**
>
> | Dato | Valor real |
> |---|---|
> | Tests run | ___ |
> | Failures | ___ |
> | Errors | ___ |
> | Skipped | ___ |
> | BUILD | ___ |

Se espera `Tests run: 66, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`, pero eso es una expectativa y no un resultado hasta que se ejecute.

## 4. Comparación antes y después

Se compilaron las dos versiones con Java 21 y se les pasó el **mismo guion de teclado** (63 líneas de entrada): listar, consultar por ID (existente, inexistente y texto), buscar por estado y tipo (con y sin resultados), registrar estudiantes (válido, sin `@`, nombre vacío), registrar solicitudes (válida, estudiante inexistente, ID no numérico, prioridad inválida), asignar responsable (válido, responsable inexistente, solicitud inexistente, ID no numérico), cambiar estado (válido, inválido, solicitud inexistente), reporte, opción inválida y texto no numérico en el menú.

| Aspecto | `estado-inicial` | `feature/rodri-pruebas` (main + pruebas) |
|---|---|---|
| Archivos de producción | 5 (casi todo en `Sistema`) | 14 (servicios, repositorio, enums, `DatosSolicitud`) |
| Compila con Java 21 | Sí | Sí |
| Salida por consola con el guion | 394 líneas | 394 líneas, **idénticas** (comparado con `diff`) |
| Datos de ejemplo | 3 / 2 / 3 | 3 / 2 / 3, mismos estados y responsables |
| Pruebas automatizadas | 0 | 66 |

**Diferencias de comportamiento encontradas: ninguna.** El refactoring de Tomás y Nazareno mantuvo el funcionamiento visible.

Una coincidencia que conviene saber: si el teclado se cierra sin ingresar `0` (fin de entrada), **las dos versiones** terminan con `NoSuchElementException`. No es una regresión, venía de la versión inicial.

## 5. Cómo reproducirlo

```bash
mvn clean test
```

o en IntelliJ: clic derecho sobre `src/test/java` → *Run 'All Tests'*.
Para ejecutar el sistema: `mvn compile exec:java`.
