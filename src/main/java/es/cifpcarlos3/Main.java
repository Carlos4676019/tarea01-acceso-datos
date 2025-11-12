package es.cifpcarlos3;

import es.cifpcarlos3.vo.Alumno;
import es.cifpcarlos3.vo.Curso;
import es.cifpcarlos3.vo.SistemaAlumnado;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.cifpcarlos3.vo.*;

public class Main {

    // - Definición de Rutas
    private static final String RUTA_PAQUETE = "src/main/java/es/cifpcarlos3";
    private static final String RUTA_SALIDA = RUTA_PAQUETE + "/salida";

    // - Rutas de entrada
    private static final String RUTA_DAM2 = RUTA_PAQUETE + "/lista_alumnado_DAM2.txt";
    private static final String RUTA_DAW1 = RUTA_PAQUETE + "/lista_alumnado_DAW1.csv";

    // - Rutas de salida
    private static final Path RUTA_BINARIO = Paths.get(RUTA_SALIDA, "cursos.dat");
    private static final Path RUTA_JSON = Paths.get(RUTA_SALIDA, "cursos.json");
    private static final Path RUTA_XML = Paths.get(RUTA_SALIDA, "cursos.xml");


    public static void main(String[] args) {

        System.out.println("Iniciando la Tarea 1...");

        try {
            // - Flujo Principal

            // 1 - Preparar directorio
            crearDirectorioSalida();

            // 2 - Leer y filtrar
            List<Alumno> alumnosDAM2 = leerAlumnos(RUTA_DAM2, ",");
            List<Alumno> alumnosDAW1 = leerAlumnos(RUTA_DAW1, ";");
            System.out.println("\n--- 1. LECTURA Y FILTRADO ---");

            // 3 - Agrupar en el modelo de datos
            Curso cursoDAM2 = new Curso("DAM2", alumnosDAM2);
            Curso cursoDAW1 = new Curso("DAW1", alumnosDAW1);
            SistemaAlumnado sistema = new SistemaAlumnado(List.of(cursoDAM2, cursoDAW1));

            // 4. - Configurar Mappers de Jackson
            JsonMapper jsonMapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule()) // Para que entienda el LocalDateTime
                    .enable(SerializationFeature.INDENT_OUTPUT) // JSON formateado
                    .enable(SerializationFeature.WRAP_ROOT_VALUE) // Para usar @JsonRootName("cursos")
                    .enable(DeserializationFeature.UNWRAP_ROOT_VALUE) // Para leer el JSON con raíz
                    .build();

            XmlMapper xmlMapper = XmlMapper.builder()
                    .addModule(new JavaTimeModule())
                    .enable(SerializationFeature.INDENT_OUTPUT) // XML formateado
                    .build();

            // 5 - Escribir ficheros
            System.out.println("\n-- 2. ESCRITURA DE FICHEROS --");
            guardarBinario(sistema, RUTA_BINARIO);
            guardarJSON(sistema, RUTA_JSON, jsonMapper);
            guardarXML(sistema, RUTA_XML, xmlMapper);

            // 6 - Validar leyendo
            System.out.println("\n-- 3. VALIDACIÓN DE LECTURA --");
            System.out.println("\n* LEYENDO BINARIO (.dat) *");
            SistemaAlumnado datosBin = leerBinario(RUTA_BINARIO);
            mostrarDatos(datosBin);

            System.out.println("\n** LEYENDO JSON (.json) **");
            SistemaAlumnado datosJson = leerJSON(RUTA_JSON, jsonMapper);
            mostrarDatos(datosJson);

            System.out.println("\n*** LEYENDO XML (.xml) ***");
            SistemaAlumnado datosXml = leerXML(RUTA_XML, xmlMapper);
            mostrarDatos(datosXml);

            System.out.println("\n-- Tarea 1 Completada --");


        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error fatal en la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * - Crea el directorio 'salida' usando la ruta definida.
     */

    private static void crearDirectorioSalida() throws IOException {
        Path pathSalida = Paths.get(RUTA_SALIDA);
        if (Files.notExists(pathSalida)) {
            Files.createDirectory(pathSalida);
            System.out.println("Directorio '" + RUTA_SALIDA + "' creado.");
        }
    }

    /**
     * - Lectura y Parseo
     * Lee un fichero, filtra por "Cartagena" y devuelve la lista de Alumnos.
     */

    private static List<Alumno> leerAlumnos(String rutaFichero, String separador) throws IOException {
        List<Alumno> alumnosFiltrados = new ArrayList<>();
        Path path = Paths.get(rutaFichero);

        // Se usa try-with-resources y UTF-8
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            int numLinea = 0;

            while ((linea = br.readLine()) != null) {
                numLinea++;
                if (linea.isBlank()) {
                    continue;
                }

                // Con este try-catch evitamos que el programa se rompa si una línea
                // está mal formada, por seguridad.
                try {
                    String[] campos = linea.split(separador);

                    if (campos.length < 5) {
                        System.out.println("AVISO: Línea " + numLinea + " ignorada (incompleta) en " + rutaFichero);
                        continue;
                    }

                    // Filtrado
                    String ciudad = campos[3].trim();
                    if (ciudad.equalsIgnoreCase("Cartagena")) {

                        // Creación del objeto Alumno
                        String apellidos = campos[1].trim();
                        String nombre = campos[2].trim();
                        int edad = Integer.parseInt(campos[4].trim());
                        LocalDateTime fechaRegistro = LocalDateTime.now();

                        alumnosFiltrados.add(new Alumno(nombre, apellidos, edad, fechaRegistro));
                    }
                } catch (Exception e) {
                    // Si algo falla, se avisa y se continúa
                    System.out.println("AVISO: Línea " + numLinea + " ignorada (error: " + e.getMessage() + ") en " + rutaFichero + " -> " + linea);
                }
            }
        }
        return alumnosFiltrados;
    }

    // - Métodos de ESCRITURA
    // Todos usan try-with-resources y Java NIO

    /**
     * - Guarda en binario usando Serialización.
     */
    private static void guardarBinario(SistemaAlumnado datos, Path ruta) throws IOException {
        System.out.println("Guardando binario en: " + ruta);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(ruta))) {
            oos.writeObject(datos);
        }
        System.out.println("-> Binario guardado.");
    }

    /**
     * - Guarda en JSON usando Jackson.
     */
    private static void guardarJSON(SistemaAlumnado datos, Path ruta, JsonMapper mapper) throws IOException {
        System.out.println("Guardando JSON en: " + ruta);
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            mapper.writeValue(writer, datos);
        }
        System.out.println("-> JSON guardado.");
    }

    /**
     * - Guarda en XML usando Jackson.
     */
    private static void guardarXML(SistemaAlumnado datos, Path ruta, XmlMapper mapper) throws IOException {
        System.out.println("Guardando XML en: " + ruta);
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            mapper.writeValue(writer, datos);
        }
        System.out.println("-> XML guardado.");
    }

    // - Métodos de LECTURA
    // Con TWR para leer los ficheros generados.

    /**
     * - Lee (Deserializa) el fichero binario.
     */
    private static SistemaAlumnado leerBinario(Path ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(ruta))) {
            return (SistemaAlumnado) ois.readObject();
        }
    }

    /**
     * - Lee (Deserializa) el fichero JSON.
     */
    private static SistemaAlumnado leerJSON(Path ruta, JsonMapper mapper) throws IOException {
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            return mapper.readValue(reader, SistemaAlumnado.class);
        }
    }

    /**
     * - Lee (Deserializa) el fichero XML.
     */
    private static SistemaAlumnado leerXML(Path ruta, XmlMapper mapper) throws IOException {
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            return mapper.readValue(reader, SistemaAlumnado.class);
        }
    }

    /**
     * - Método de Validación
     * Muestra los datos leídos por consola.
     */
    private static void mostrarDatos(SistemaAlumnado sistema) {
        if (sistema == null || sistema.getCursos() == null) {
            System.out.println("  -> ERROR: No se han podido cargar los datos.");
            return;
        }

        // Itera sobre los cursos y alumnos para mostrarlos ordenados
        for (Curso curso : sistema.getCursos()) {
            System.out.println("  Curso: " + curso.getNombre() + " (" + curso.getAlumnos().size() + " alumnos)");
            for (Alumno alumno : curso.getAlumnos()) {
                System.out.println("    - " + alumno.getNombre() + " " + alumno.getApellidos() +
                        ", " + alumno.getEdad() + " años" +
                        " (Registrado: " + alumno.getFechaRegistro().toLocalDate() + ")");
            }
        }
    }
}