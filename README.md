# Sistema de Gestión de Solicitudes Académicas

Este proyecto corresponde al Trabajo Práctico Integrador grupal de **Metodología de Sistemas II**. Desarrollamos una aplicación de consola para registrar y gestionar solicitudes académicas de estudiantes.

## Descripción del sistema

El sistema permite registrar estudiantes y solicitudes académicas, consultar las solicitudes cargadas, buscarlas por ID, estado o tipo, asignarles responsables, cambiar sus estados y generar un reporte con la cantidad de solicitudes en cada estado.

La aplicación incluye datos ficticios iniciales para facilitar su uso y sus pruebas. Toda la información se guarda en memoria, por lo que se pierde al finalizar la ejecución.

## Tecnologías utilizadas

- **Java 21** para desarrollar la aplicación.
- **Maven** para compilar, ejecutar y administrar las dependencias del proyecto.
- **JUnit 5** para las pruebas automatizadas.

El sistema funciona por consola y no utiliza una base de datos: estudiantes, responsables y solicitudes se almacenan en memoria mientras el programa está en ejecución.

## Funcionamiento

Al iniciar la aplicación se muestra un menú con las operaciones disponibles. Desde allí podemos:

- registrar estudiantes con nombre y correo electrónico;
- registrar solicitudes indicando estudiante, tipo, descripción y prioridad;
- listar todas las solicitudes y consultar una solicitud por su ID;
- buscar solicitudes por estado o por tipo;
- asignar o reasignar un responsable a una solicitud;
- cambiar el estado de una solicitud;
- generar un reporte con las cantidades en estado `PENDIENTE`, `EN_PROCESO` y `RESUELTA`, además del total.

## Mejoras realizadas

Analizamos la versión inicial y aplicamos criterios de **Clean Code** y principios **SOLID** para separar responsabilidades y facilitar la lectura, el mantenimiento y las pruebas. Entre las principales refactorizaciones realizamos:

- **Extract Method**, para dividir tareas extensas en métodos más claros;
- **Move Method**, para trasladar lógica a las clases y servicios correspondientes;
- **Introduce Parameter Object**, mediante `DatosSolicitud`, que agrupa los datos necesarios para registrar una solicitud;
- reemplazo de textos repetidos por los enums `EstadoSolicitud` y `Prioridad`;
- separación de la lógica en servicios y creación de una interfaz de repositorio para las solicitudes.

Estas mejoras mantienen el comportamiento de la versión inicial. También documentamos aspectos que podrían seguir mejorándose en la revisión de código.

## Estructura del proyecto

```text
src/
├── main/java/ar/edu/metodologia/   Código de la aplicación
└── test/java/ar/edu/metodologia/   Pruebas automatizadas
docs/                               Documentación de pruebas y revisión
pom.xml                             Configuración de Maven
```

Las clases principales cumplen las siguientes funciones:

- `Main` crea las dependencias e inicia la aplicación.
- `Sistema` administra el menú y la interacción por consola.
- `Estudiante`, `Responsable` y `Solicitud` representan las entidades del sistema.
- `DatosSolicitud` agrupa los datos de entrada de una solicitud.
- `EstadoSolicitud` y `Prioridad` definen mediante enums los valores permitidos.
- `DatosIniciales` carga información ficticia para comenzar a usar el sistema.
- `EstudianteService`, `ResponsableService` y `SolicitudService` contienen la lógica de cada área.
- `SolicitudRepositorio` define las operaciones de almacenamiento y `SolicitudRepositorioEnMemoria` las implementa usando memoria.
- `src/test` contiene las pruebas de los servicios, modelos, enums, datos iniciales y funcionamiento por consola.
- `docs` reúne el detalle de las pruebas y el informe de revisión de código.

## Cómo ejecutar el sistema

### Requisitos

- JDK 21 configurado en el equipo.
- Maven disponible en el `PATH` para utilizar el comando `mvn` desde la terminal.

Podemos comprobar las instalaciones con:

```bash
java -version
mvn -version
```

### Desde IntelliJ IDEA

1. Abrimos la carpeta del proyecto como un proyecto Maven.
2. Configuramos el **Project SDK** con Java 21.
3. Esperamos a que IntelliJ cargue las dependencias de Maven.
4. Abrimos `src/main/java/ar/edu/metodologia/Main.java`.
5. Ejecutamos el método `main` desde el botón **Run**.

### Desde la terminal

Ubicados en la carpeta raíz del proyecto, ejecutamos:

```bash
mvn clean compile
mvn exec:java
```

También podemos compilar y ejecutar en un solo comando:

```bash
mvn clean compile exec:java
```

## Pruebas

Desarrollamos pruebas automatizadas con JUnit 5. Para ejecutarlas desde la terminal, con Maven disponible, usamos:

```bash
mvn test
```

Desde IntelliJ IDEA podemos hacer clic derecho sobre `src/test/java` y seleccionar **Run 'All Tests'**. También podemos ejecutar el objetivo `test` desde el panel de Maven.

El **07/11/2026** comprobamos el siguiente resultado:

- 66 pruebas ejecutadas;
- 0 fallos;
- 0 errores;
- 0 pruebas omitidas;
- resultado final: **BUILD SUCCESS**.

## Documentación

- [Detalle de las pruebas](docs/pruebas.md)
- [Revisión de código](docs/code-review.md)

## Versiones del proyecto

Usamos etiquetas de Git para conservar y consultar los distintos momentos del trabajo:

- `estado-inicial`: versión original, anterior a las mejoras;
- `estadorefactorizado`: versión posterior al refactoring;
- `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas` y `etapa-docs`: etapas del proceso de análisis, mejora, pruebas y documentación.

Podemos listar todas las etiquetas con:

```bash
git tag --list
```

Para consultar una versión sin modificar los archivos actuales podemos usar, por ejemplo:

```bash
git show estado-inicial
git show estadorefactorizado
```

También podemos comparar ambas versiones con:

```bash
git diff estado-inicial estadorefactorizado
```

## Trabajo grupal

Desarrollamos este proyecto como un Trabajo Práctico Integrador grupal de la materia **Metodología de Sistemas II**. Realizamos en conjunto el análisis, las refactorizaciones, las pruebas y la documentación, sin dividir el resultado por integrantes.
