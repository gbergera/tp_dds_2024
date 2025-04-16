package ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ColaboradorDTO {
    private String tipoDocumento;
    private String documento ;
    private String nombre ;
    private String apellido ;
    private String mail ;
    private LocalDateTime fechaColaboracion ;
    private String formaColaboracion;
    private Integer cantidad;

    public ColaboradorDTO(String tipoDocumento, String documento, String nombre, String apellido, String mail, LocalDate fechaColaboracion, String formaColaboracion, Integer cantidad) {
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.fechaColaboracion = fechaColaboracion.atStartOfDay();
        this.formaColaboracion = formaColaboracion;
        this.cantidad = cantidad;
    }
}
