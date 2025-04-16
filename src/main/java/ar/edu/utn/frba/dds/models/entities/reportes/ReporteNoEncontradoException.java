package ar.edu.utn.frba.dds.models.entities.reportes;

public class ReporteNoEncontradoException extends RuntimeException {
    public ReporteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
