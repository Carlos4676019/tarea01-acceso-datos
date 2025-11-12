package es.cifpcarlos3.vo;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Esta es la clase contenedora para la lista de Cursos.
 * Con esta clase se serializará a binario, JSON y XML.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
// Se define "cursos" como el elemento raíz
@JsonRootName("cursos")
public class SistemaAlumnado implements Serializable {

    private static final long serialVersionUID = 1L;

    // Anotaciones XML para la lista de cursos
    // useWrapping = false evita una etiqueta <cursos> extra.
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "curso")
    private List<Curso> cursos;
}