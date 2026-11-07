# Code review del Sistema de Gestión de Solicitudes Académicas

Rama revisada: `feature/rodri-pruebas` (código de `main` con las mejoras de Tomás y Nazareno).
Se leyeron las 14 clases de `src/main/java/ar/edu/metodologia`, se ejecutó el sistema por consola y se escribieron pruebas. No se modificó código de producción.

## 1. Checklist técnico

| # | Punto | Resultado | Observación concreta |
|---|---|---|---|
| 1 | Nombres claros de clases, métodos y variables | **Cumple** | Nombres en español y descriptivos (`SolicitudService`, `buscarPorEstado`, `cantidadesPorEstado`). Único detalle: en `Solicitud.toString()` hay una variable `r` para el responsable. |
| 2 | Métodos con responsabilidades definidas | **Cumple parcialmente** | Mejor que la versión inicial: en `estado-inicial` `Sistema` (174 líneas) guardaba los datos, validaba y buscaba todo en los mismos métodos; ahora delega en servicios (`Sistema` quedó en 236 líneas porque se extrajeron métodos más chicos, pero casi todo es consola). Pero `asignarResponsable()` y `cambiarEstado()` de `Sistema` todavía piden datos, validan existencia, buscan y muestran mensajes en el mismo método. |
| 3 | Ausencia de duplicación innecesaria | **Cumple parcialmente** | Se eliminó la duplicación grande de la versión inicial. Queda el bloque "pedir ID → `leerNumero()` → si es `null` imprimir 'ID inválido.'" repetido 4 veces en `Sistema`. También se repite el `for` de búsqueda por ID en `EstudianteService`, `ResponsableService` y `SolicitudRepositorioEnMemoria`, y el `desdeTexto` en los dos enums. |
| 4 | Aplicación de SOLID cuando corresponda | **Cumple parcialmente** | **S:** servicios separados por tema, bien. **D:** `SolicitudService` depende de la interfaz `SolicitudRepositorio`, bien. **Pendiente:** `Sistema` y `SolicitudService` dependen de clases concretas `EstudianteService` y `ResponsableService` (sin interfaz). `ejecutarOpcion` es una cadena de `if/else if`: agregar una opción obliga a modificarla (OCP). |
| 5 | Separación entre consola y lógica | **Cumple parcialmente** | Los servicios no tienen `System.out` ni `Scanner`, y eso es lo más valioso del refactoring. Pero `Sistema` crea su propio `Scanner(System.in)` internamente, así que no se puede inyectar otra entrada; para probarlo hubo que reemplazar `System.in` antes de construirlo. Además, la validación del estudiante la decide `Sistema` (llama a `datosValidos` antes de `registrarEstudiante`). |
| 6 | Validaciones y manejo de errores | **Requiere mejora** | Los errores se resuelven devolviendo `null` o `false`, sin mensajes ni excepciones: quien llama no sabe *por qué* falló (ver `registrarSolicitud`, que devuelve `null` tanto si no existe el estudiante como si los datos son inválidos). `registrarEstudiante` no valida por sí mismo. `datosValidos` solo mira `contains("@")`: "@" sería un email válido. `sonValidos()` lanza `NullPointerException` si el tipo es `null`. No hay control de duplicados ni de transiciones de estado (se puede pasar de RESUELTA a PENDIENTE). |
| 7 | Uso correcto de enums | **Cumple parcialmente** | `EstadoSolicitud` y `Prioridad` reemplazan los `String` de la versión inicial, y `contarPorEstado()` usa `EnumMap` correctamente. Pero el **tipo** de solicitud sigue siendo un `String` libre aunque el menú ofrece solo 4 valores (CERTIFICADO/EQUIVALENCIA/EXAMEN/OTRA): hoy se puede registrar "XYZ" como tipo. |
| 8 | Uso adecuado de servicios y repositorios | **Cumple parcialmente** | Solicitudes sí tienen repositorio (interfaz + implementación en memoria). Estudiantes y responsables guardan su lista dentro del servicio, sin repositorio: es inconsistente. Además `listarSolicitudes()`, `listarEstudiantes()` y `listarResponsables()` devuelven la lista interna, así que quien la recibe puede modificarla desde afuera. |
| 9 | Legibilidad y facilidad de mantenimiento | **Cumple** | Código corto, ordenado y fácil de seguir. `Main` muestra claramente cómo se arma todo. Detalle menor: los atributos de las clases no son `final` aunque no cambian. |
| 10 | Pruebas y comportamiento funcional | **Cumple parcialmente** | Antes no había ninguna prueba; ahora hay 66 y el comportamiento por consola es idéntico al de `estado-inicial`. Pero: (a) `mvn clean test` no pudo ejecutarse en el entorno donde se prepararon, así que falta la corrida real con JUnit; (b) no se midió cobertura; (c) `Sistema` se prueba solo de forma indirecta. |

## 2. Mejoras verificadas

Estas mejoras de Tomás y Nazareno se comprobaron leyendo el código, con las pruebas y con la comparación contra `estado-inicial`:

- `Sistema` ya no concentra todo: la lógica está en `EstudianteService`, `ResponsableService` y `SolicitudService`.
- Hay una interfaz `SolicitudRepositorio` con implementación en memoria, y `SolicitudService` la recibe por constructor.
- `DatosSolicitud` agrupa los datos de una solicitud (antes eran 6 parámetros sueltos).
- Estados y prioridades son enums con `desdeTexto`.
- Los datos de ejemplo se separaron en `DatosIniciales`.
- El reporte usa `EnumMap` y el total sale de `contarTotal()`.
- **El comportamiento no cambió:** mismo guion de teclado, misma salida (394 líneas idénticas) y mismos datos iniciales.

## 3. Problemas pendientes y recomendaciones

| Prioridad | Problema | Recomendación |
|---|---|---|
| Media | `registrarSolicitud` devuelve `null` sin decir el motivo; `registrarEstudiante` no valida | Lanzar `IllegalArgumentException` con mensaje, o devolver un resultado con el motivo. Mover la validación dentro de `registrarEstudiante`. |
| Media | El tipo de solicitud es un `String` libre | Crear un enum `TipoSolicitud` con `desdeTexto`, igual que los otros. |
| Media | Validación de email débil y nombres con solo espacios pasan el servicio | Hacer `trim()` en el servicio y validar un formato mínimo (algo antes y después de la `@`). |
| Media | `Sistema` no permite inyectar la entrada | Recibir el `Scanner` (o un `InputStream`) por constructor para poder probarlo sin tocar `System.in`. |
| Media | Si se cierra el teclado sin ingresar `0`, el programa termina con `NoSuchElementException` (pasa igual en `estado-inicial`) | Atrapar la excepción en `leerNumero()` o chequear `hasNextLine()`. |
| Baja | Los métodos `listar*` devuelven la lista interna | Devolver `List.copyOf(...)` o `Collections.unmodifiableList(...)`. |
| Baja | "ID inválido." y la lectura de ID repetidos en `Sistema` | Extraer un método `pedirNumero(String mensaje)`. |
| Baja | Cadena de `if/else if` en `ejecutarOpcion` | Pasar a `switch` o a un mapa de opciones. |
| Baja | Estudiantes y responsables sin repositorio; servicios concretos sin interfaz | Aplicar el mismo esquema que en solicitudes si el sistema va a crecer. |
| Baja | Al registrar una solicitud no se muestra el ID asignado | Mostrarlo en el mensaje de éxito, así el usuario puede consultarla. |
| Baja | No hay reglas de transición de estado | Definir si RESUELTA puede volver a PENDIENTE; si no, validarlo. |
| Baja | Falta medir la cobertura | Agregar JaCoCo más adelante (no se agregó porque no era necesario para esta entrega). |

Las dos pruebas marcadas como "limitación conocida" (`nombreConSoloEspaciosPasaLaValidacionDelServicio` y `datosSolicitudConTipoNullLanzaExcepcion`) documentan el comportamiento actual. Si se corrigen estos puntos, hay que actualizarlas.

## 4. Criterios de aceptación técnica

| Criterio | Estado |
|---|---|
| El proyecto compila con Java 21 | Cumplido (verificado con `javac --release 21`; falta confirmar con `mvn`) |
| Todas las pruebas automatizadas pasan | Cumplido en el mini-runner (66/66). **Pendiente** confirmar con `mvn clean test` y JUnit real |
| El resultado de `mvn clean test` es `BUILD SUCCESS` | **Pendiente** de ejecutar y capturar |
| Las 12 áreas pedidas tienen pruebas con aserciones | Cumplido |
| El sistema se comporta igual que `estado-inicial` | Cumplido (salidas idénticas con el mismo guion) |
| No se cambió código de producción | Cumplido (`pom.xml` solo con dependencias de prueba; `src/main` y `README.md` intactos) |
| Sin problemas críticos pendientes | Cumplido: no se encontraron errores que rompan el funcionamiento; sí quedan mejoras de prioridad media y baja |

**Conclusión:** la versión final es funcionalmente igual a la inicial y está bastante mejor organizada, pero no está "perfecta". Los puntos de mayor valor para mejorar son el manejo de errores (punto 6), el enum para el tipo (punto 7) y poder inyectar la entrada en `Sistema` (punto 5).
