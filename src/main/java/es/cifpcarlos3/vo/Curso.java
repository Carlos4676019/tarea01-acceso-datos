package es.cifpcarlos3.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Modelo de datos para un Curso.
 * Contiene el nombre del curso y la lista de alumnos filtrados.
 * Implementa Serializable para la escritura en binario.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre; // "DAM2" o "DAW1"

    // Anotaciones XML para listas
    @JacksonXmlElementWrapper(localName = "alumnos")
    @JacksonXmlProperty(localName = "alumno")
    private List<Alumno> alumnos;
}