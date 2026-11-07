# Pruebas del Sistema de Gestión de Solicitudes Académicas

Comparamos la versión actual con el tag `estado-inicial` (commit `46003e9`) para comprobar que el refactoring mantuviera las funcionalidades originales.

## 1. Qué agregamos para probar

- En `pom.xml` agregamos JUnit 5 (`junit-jupiter 5.10.2`, con alcance `test`) y el plugin `maven-surefire-plugin 3.2.5`, necesario para ejecutar las pruebas con Maven.
- Creamos las pruebas en `src/test/java/ar/edu/metodologia/`.
- No modificamos ningún archivo de `src/main` ni el `README.md` al incorporar las pruebas.

| Archivo de prueba | Qué cubre | Cantidad |
|---|---|---|
| `EstudianteServiceTest` | Registro, IDs, validación de datos y casos sin datos | 9 |
| `SolicitudServiceTest` | Alta, rechazos, búsquedas, responsables, estados, reporte y casos sin datos | 27 |
| `ResponsableServiceTest` | Alta y búsqueda de responsables, inexistentes y casos sin datos | 3 |
| `ModeloYEnumsTest` | `EstadoSolicitud`, `Prioridad`, `DatosSolicitud` y `Solicitud.toString` | 8 |
| `SistemaConsolaTest` | Menú por consola, simulando el teclado con `System.in` | 18 |
| `DatosInicialesTest` | Verificación de que los datos de ejemplo sean los mismos que en la versión inicial | 1 |
| **Total** | | **66** |

## 2. Casos de prueba

Para las pruebas usamos datos ficticios, por ejemplo, "Estudiante Prueba". En todos los casos comprobamos los resultados con aserciones; no nos limitamos a ejecutar los métodos.

| N.º | Requisito | Caso | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| 1 | Registro correcto de estudiantes | Registrar un estudiante válido | Queda en la lista con nombre, email e ID 1 | OK |
| 1 | | Registrar tres estudiantes | IDs 1, 2 y 3 | OK |
| 2 | Estudiantes inválidos | Nombre vacío, email vacío o email sin `@` | `datosValidos` devuelve `false` | OK |
| 2 | | Por consola, email sin `@` o nombre con solo espacios | Se muestra "Datos inválidos…" y no se guarda | OK |
| 3 | Solicitud con estudiante existente | Registrar una solicitud del estudiante 2 | Se crea con ID 1, estado PENDIENTE y sin responsable | OK |
| 4 | Solicitudes inválidas | Estudiante inexistente, tipo vacío, descripción vacía o prioridad nula | Devuelve `null` y no se guarda | OK |
| 4 | | Un rechazo no consume el siguiente ID | La siguiente solicitud válida tiene ID 1 | OK |
| 4 | | Por consola: estudiante inexistente, ID no numérico o prioridad inválida | Se muestra un mensaje de error y no se guarda | OK |
| 5 | Buscar por ID | ID existente o inexistente | Devuelve la solicitud o `null` | OK |
| 6 | Buscar por estado y tipo | Una solicitud en cada estado y dos de tipo EXAMEN | Cada búsqueda devuelve las solicitudes correspondientes | OK |
| 6 | | Sin coincidencias, tipo en minúscula o estado `null` | Lista vacía | OK |
| 7 | Asignar responsable | Asignar y reasignar | Queda el último responsable asignado | OK |
| 8 | Inexistentes | Solicitud inexistente o responsable `null` | Devuelve `false` y no cambia nada | OK |
| 8 | | Buscar un responsable inexistente | Devuelve `null` | OK |
| 9 | Cambiar estado | PENDIENTE → EN_PROCESO → RESUELTA | El estado cambia en cada paso | OK |
| 10 | Estados inválidos | Estado `null`, "CANCELADA", "pendiente" o vacío | Devuelve `false` o `null`, y la solicitud no cambia | OK |
| 11 | Reporte por estado | Cuatro solicitudes repartidas en estados | Contadores 1 / 1 / 2 y total 4; la suma coincide | OK |
| 12 | Sin datos | Sin estudiantes, solicitudes ni responsables | Listas vacías, búsquedas en `null` y reporte en 0 | OK |
| 12 | | Por consola sin datos | Se muestra "No hay solicitudes." y el reporte queda en 0 | OK |
| – | Datos de ejemplo | Cargar `DatosIniciales` | Tres estudiantes, dos responsables y tres solicitudes con los mismos estados | OK |

En la columna "Resultado obtenido", **OK** indica que comprobamos el resultado esperado durante la ejecución de las pruebas con JUnit 5.

### Prueba que documenta una limitación conocida

- `nombreConSoloEspaciosPasaLaValidacionDelServicio`: comprobamos que `EstudianteService.datosValidos("   ", ...)` devuelve `true`. La consola aplica `trim()` antes y por eso no permite cargar ese nombre desde el menú, pero el servicio por sí solo no lo impide.

Que esta prueba apruebe significa que confirmamos el comportamiento actual, no que lo consideremos correcto o deseable. Si en el futuro corregimos esta limitación, también tendremos que actualizar la prueba.

Después del code review corregimos el caso del tipo nulo. La prueba ahora se llama `datosSolicitudConTipoNullEsInvalido` y comprueba que `DatosSolicitud.sonValidos()` devuelve `false` cuando `tipo` es `null`, en lugar de esperar una excepción.

## 3. Resultados de ejecución

Inicialmente tuvimos dificultades para ejecutar Maven debido a restricciones del entorno, que impedían descargar desde Maven Central los plugins y las dependencias necesarias. Más adelante pudimos realizar la ejecución correctamente desde el panel Maven de IntelliJ IDEA, usando el objetivo `test`.

La ejecución real se realizó el **07/11/2026**, con **Java 21** y **JUnit 5**. Obtuvimos los siguientes resultados:

| Dato | Resultado |
|---|---|
| Objetivo Maven ejecutado | `test` |
| Tests run | **66** |
| Failures | **0** |
| Errors | **0** |
| Skipped | **0** |
| Resultado de la compilación | **BUILD SUCCESS** |

Con estos resultados comprobamos que las 66 pruebas finalizaron correctamente, sin fallos, errores ni pruebas omitidas.

Además, durante la preparación de las pruebas realizamos otras verificaciones complementarias:

| Verificación | Resultado |
|---|---|
| Compilar con Java 21 (`javac --release 21`), versión actual | Sin errores |
| Compilar con Java 21 (`javac --release 21`), versión inicial | Sin errores |
| Ejecutar el sistema por consola con un guion de teclado | Ejecución correcta |
| Comparar la consola actual con `estado-inicial` | Salidas idénticas |

## 4. Comparación antes y después

Compilamos las dos versiones con Java 21 y usamos el mismo guion de teclado, de 63 líneas de entrada. Incluimos operaciones para listar, consultar por ID, buscar por estado y tipo, registrar estudiantes y solicitudes, asignar responsables, cambiar estados, mostrar el reporte y probar entradas inválidas.

| Aspecto | `estado-inicial` | Versión actual con mejoras y pruebas |
|---|---|---|
| Archivos de producción | 5, con casi todo concentrado en `Sistema` | 14, organizados en servicios, repositorio, enums y `DatosSolicitud` |
| Compila con Java 21 | Sí | Sí |
| Salida por consola con el guion | 394 líneas | 394 líneas idénticas, comparadas con `diff` |
| Datos de ejemplo | 3 estudiantes, 2 responsables y 3 solicitudes | Las mismas cantidades, estados y responsables |
| Pruebas automatizadas | 0 | 66 |

No encontramos diferencias en el comportamiento visible. De esta manera, comprobamos que el refactoring y las mejoras de calidad interna mantuvieron las funcionalidades de la versión inicial.

También observamos una limitación compartida por ambas versiones: si se cierra la entrada sin ingresar `0`, el programa termina con `NoSuchElementException`. No se trata de una regresión introducida por las mejoras, ya que este comportamiento ya existía en la versión inicial.

## 5. Cómo reproducir las pruebas

Desde el panel Maven de IntelliJ IDEA, seleccionamos el objetivo:

```text
test
```

También podemos ejecutarlas desde IntelliJ haciendo clic derecho sobre `src/test/java` y seleccionando *Run 'All Tests'*. Para ejecutar el sistema podemos usar `mvn compile exec:java`.
