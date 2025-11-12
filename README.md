# Tarea 1: Acceso a Datos 2º DAM

Proyecto de la Tarea 1 para la asignatura de Acceso a Datos del CIFP Carlos III.

## 📝 Descripción

El programa lee dos ficheros de texto (`.txt` y `.csv`) con listas de alumnos, filtra aquellos que son de "Cartagena", y guarda los datos en tres formatos diferentes:

1.  **Binario:** `cursos.dat` (Serialización Java)
2.  **JSON:** `cursos.json` (Usando Jackson 3)
3.  **XML:** `cursos.xml` (Usando Jackson 3)

Por ultimo el programa valida la escritura leyendo estos tres ficheros generados y mostrando los datos por consola.

## ⚙️ Requisitos

* Java 21 (o 17+)
* Apache Maven 3.8+

## 🚀 Cómo Compilar y Ejecutar

1.  Clona este repositorio.
2.  Asegúrate de que los ficheros `lista_alumnado_DAM2.txt` y `lista_alumnado_DAW1.csv` están ubicados en `src/main/java/es/cifpcarlos3/`.
3.  Compila el proyecto con Maven:
    ```bash
    mvn clean package
    ```
4.  Ejecuta la clase principal:
    (Desde un IDE, ejecuta el `Main.java`)

## 📂 Estructura del Proyecto

* `/pom.xml`: Fichero de configuración de Maven y dependencias.
* `/src/main/java/es/cifpcarlos3/`: Código fuente del proyecto.
    * `Main.java`: Orquestador principal (lee, escribe, valida).
    * `Alumno.java`: Modelo de datos (con Lombok y Serializable).
    * `Curso.java`: Modelo de datos (con Lombok y Serializable).
    * `SistemaAlumnado.java`: Clase "wrapper" raíz para la serialización.
    * `lista_alumnado...`: Ficheros de datos de entrada.
* `/src/main/java/es/cifpcarlos3/salida/`: Carpeta donde se generan los ficheros (`.dat`, `.json`, `.xml`).
