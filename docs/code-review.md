# Code review del Sistema de Gestión de Solicitudes Académicas

Revisamos la versión actual del sistema, que incluye las mejoras de organización y refactoring realizadas por el equipo. Leímos las 14 clases de `src/main/java/ar/edu/metodologia`, ejecutamos el sistema por consola y desarrollamos pruebas automatizadas. No modificamos el código de producción como parte de esta revisión.

## 1. Checklist técnico

| # | Punto | Resultado | Observación concreta |
|---|---|---|---|
| 1 | Nombres claros de clases, métodos y variables | **Cumple** | Observamos nombres en español y descriptivos, como `SolicitudService`, `buscarPorEstado` y `cantidadesPorEstado`. Como detalle menor, en `Solicitud.toString()` se usa la variable `r` para el responsable. |
| 2 | Métodos con responsabilidades definidas | **Cumple parcialmente** | Comprobamos una mejora frente a la versión inicial: `Sistema`, que antes concentraba el almacenamiento, las validaciones y las búsquedas, ahora delega esas tareas en servicios. Sin embargo, `asignarResponsable()` y `cambiarEstado()` todavía solicitan datos, validan existencias, realizan búsquedas y muestran mensajes dentro del mismo método. |
| 3 | Ausencia de duplicación innecesaria | **Cumple parcialmente** | Se eliminó gran parte de la duplicación inicial. Todavía encontramos repetido cuatro veces en `Sistema` el bloque para pedir un ID, llamar a `leerNumero()` y mostrar "ID inválido.". También se repite la búsqueda por ID en `EstudianteService`, `ResponsableService` y `SolicitudRepositorioEnMemoria`, además de `desdeTexto` en los dos enums. |
| 4 | Aplicación de SOLID cuando corresponda | **Cumple parcialmente** | Respecto de responsabilidad única, comprobamos que los servicios están separados por tema. En inversión de dependencias, `SolicitudService` depende de la interfaz `SolicitudRepositorio`. Como aspectos pendientes, `Sistema` y `SolicitudService` dependen de las clases concretas `EstudianteService` y `ResponsableService`, y `ejecutarOpcion` usa una cadena de `if/else if` que debe modificarse cada vez que se agrega una opción. |
| 5 | Separación entre consola y lógica | **Cumple parcialmente** | Los servicios no usan `System.out` ni `Scanner`, lo que consideramos una mejora importante. Sin embargo, `Sistema` crea internamente su propio `Scanner(System.in)`, por lo que no podemos inyectar otra entrada directamente. Para probarlo tuvimos que reemplazar `System.in` antes de construirlo. Además, `Sistema` decide la validación del estudiante antes de llamar a `registrarEstudiante`. |
| 6 | Validaciones y manejo de errores | **Requiere mejora** | Observamos que varios errores se informan mediante `null` o `false`, sin indicar el motivo. `registrarEstudiante` no valida por sí mismo y `datosValidos` solo comprueba que el email contenga `@`. `sonValidos()` lanza `NullPointerException` si el tipo es `null`. Tampoco hay control de duplicados ni reglas para las transiciones de estado. |
| 7 | Uso correcto de enums | **Cumple parcialmente** | `EstadoSolicitud` y `Prioridad` reemplazan los `String` de la versión inicial, y `contarPorEstado()` usa `EnumMap`. Sin embargo, el tipo de solicitud sigue siendo un `String` libre aunque el menú ofrezca cuatro valores, por lo que actualmente se puede registrar un tipo como "XYZ". |
| 8 | Uso adecuado de servicios y repositorios | **Cumple parcialmente** | Las solicitudes cuentan con una interfaz de repositorio y una implementación en memoria. En cambio, estudiantes y responsables guardan sus listas dentro del servicio, lo que genera una organización inconsistente. Además, los métodos `listar*` devuelven las listas internas y permiten que se modifiquen desde afuera. |
| 9 | Legibilidad y facilidad de mantenimiento | **Cumple** | Consideramos que el código es corto, ordenado y fácil de seguir. `Main` permite comprender con claridad cómo se construyen los objetos. Como detalle menor, algunos atributos podrían declararse `final` porque no cambian. |
| 10 | Pruebas y comportamiento funcional | **Cumple parcialmente** | La versión inicial no tenía pruebas. En la versión actual ejecutamos **66 pruebas con JUnit 5** mediante el objetivo Maven `test` y todas finalizaron correctamente: 0 fallos, 0 errores y 0 omitidas. También comprobamos que el comportamiento por consola es idéntico al de `estado-inicial`. Mantenemos la valoración parcial porque no medimos cobertura y `Sistema` se prueba de forma indirecta mediante la consola. |

## 2. Mejoras verificadas

Comprobamos las siguientes mejoras mediante la lectura del código, las pruebas y la comparación con `estado-inicial`:

- `Sistema` ya no concentra toda la lógica; ahora intervienen `EstudianteService`, `ResponsableService` y `SolicitudService`.
- Existe la interfaz `SolicitudRepositorio`, con una implementación en memoria, y `SolicitudService` la recibe por constructor.
- `DatosSolicitud` agrupa los datos de una solicitud, que antes se pasaban como seis parámetros separados.
- Los estados y las prioridades se representan con enums que incluyen `desdeTexto`.
- Los datos de ejemplo se separaron en `DatosIniciales`.
- El reporte usa `EnumMap` y obtiene el total mediante `contarTotal()`.
- Verificamos que el comportamiento original se mantiene: usamos el mismo guion de teclado, obtuvimos las mismas 394 líneas de salida y conservamos los mismos datos iniciales.
- Ejecutamos las 66 pruebas con JUnit 5, usando Java 21 y el objetivo Maven `test` desde IntelliJ IDEA. El resultado del 07/11/2026 fue **BUILD SUCCESS**, sin fallos, errores ni pruebas omitidas.

## 3. Problemas pendientes y recomendaciones

Aunque todas las pruebas finalizaron correctamente, no consideramos que el sistema sea perfecto. Las pruebas confirman el comportamiento actual, pero todavía observamos aspectos que podrían mejorarse.

| Prioridad | Problema | Recomendación |
|---|---|---|
| Media | `registrarSolicitud` devuelve `null` sin explicar el motivo y `registrarEstudiante` no valida | Lanzar `IllegalArgumentException` con un mensaje o devolver un resultado que incluya el motivo. Mover la validación dentro de `registrarEstudiante`. |
| Media | El tipo de solicitud es un `String` libre | Crear un enum `TipoSolicitud` con `desdeTexto`, de manera similar a los otros enums. |
| Media | La validación del email es débil y el servicio acepta nombres con solo espacios | Aplicar `trim()` en el servicio y validar un formato mínimo, con contenido antes y después de la `@`. |
| Media | `Sistema` no permite inyectar la entrada | Recibir el `Scanner` o un `InputStream` por constructor para facilitar las pruebas sin modificar `System.in`. |
| Media | Si se cierra la entrada sin ingresar `0`, el programa termina con `NoSuchElementException`; esto también ocurre en `estado-inicial` | Controlar `hasNextLine()` o tratar la excepción en `leerNumero()`. |
| Baja | Los métodos `listar*` devuelven la lista interna | Devolver `List.copyOf(...)` o `Collections.unmodifiableList(...)`. |
| Baja | La lectura del ID y el mensaje "ID inválido." se repiten en `Sistema` | Extraer un método como `pedirNumero(String mensaje)`. |
| Baja | `ejecutarOpcion` contiene una cadena de `if/else if` | Usar un `switch` o un mapa de opciones. |
| Baja | Estudiantes y responsables no tienen repositorio, y sus servicios no usan interfaces | Aplicar el mismo esquema que en solicitudes si el sistema crece. |
| Baja | Al registrar una solicitud no se muestra el ID asignado | Incluirlo en el mensaje de éxito para facilitar las consultas posteriores. |
| Baja | No hay reglas de transición de estado | Definir si una solicitud RESUELTA puede volver a PENDIENTE y validar la regla elegida. |
| Baja | No medimos la cobertura | Incorporar JaCoCo en una mejora futura; no lo agregamos porque no era necesario para esta entrega. |

Las pruebas `nombreConSoloEspaciosPasaLaValidacionDelServicio` y `datosSolicitudConTipoNullLanzaExcepcion` dejan registradas dos limitaciones conocidas. Que ambas aprueben confirma el comportamiento actual, pero no significa que lo consideremos correcto o deseable. Si decidimos corregir esos comportamientos en el futuro, también tendremos que actualizar las pruebas.

## 4. Criterios de aceptación técnica

| Criterio | Estado |
|---|---|
| El proyecto compila con Java 21 | **Cumplido**. Lo comprobamos durante la ejecución realizada con Java 21. |
| Todas las pruebas automatizadas pasan | **Cumplido**. Ejecutamos 66 pruebas con JUnit 5: 66 correctas, 0 fallos, 0 errores y 0 omitidas. |
| El resultado del objetivo Maven `test` es `BUILD SUCCESS` | **Cumplido**. Lo verificamos desde el panel Maven de IntelliJ IDEA el 07/11/2026. |
| Las 12 áreas solicitadas tienen pruebas con aserciones | **Cumplido**. |
| El sistema se comporta igual que `estado-inicial` | **Cumplido**. Obtuvimos salidas idénticas con el mismo guion. |
| No se modificó código de producción al agregar las pruebas | **Cumplido**. Mantuvimos intactos `src/main` y `README.md`; en `pom.xml` solo se habían incorporado las dependencias necesarias para las pruebas. |
| No quedan problemas críticos pendientes | **Cumplido**. No encontramos errores que impidan el funcionamiento, aunque sí registramos mejoras de prioridad media y baja. |

## 5. Conclusión

Luego de analizar ambas versiones, comprobamos que la versión actual conserva las funcionalidades originales y presenta una mejor organización interna. La aplicación de Clean Code, SOLID y refactoring permitió separar responsabilidades y facilitar las pruebas.

La ejecución de las 66 pruebas con JUnit 5 finalizó correctamente, pero esto no elimina las limitaciones encontradas. Consideramos que las mejoras futuras más importantes son fortalecer el manejo de errores, representar el tipo de solicitud con un enum y permitir la inyección de la entrada en `Sistema`.
