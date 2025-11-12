package es.cifpcarlos3.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Modelo de datos para un Alumno.
 * Contiene nombre, apellidos, edad y la fecha de registro.
 * Implementa Serializable para la escritura en binario.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumno implements Serializable {

    // serialVersionUID para clases serializables
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;
    private int edad;

    //fechaRegistro con LocalDateTime y formato para Jackson
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaRegistro;
}