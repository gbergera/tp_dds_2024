package ar.edu.utn.frba.dds.models.entities.excepciones;

public class PuntosInsuficienteException extends RuntimeException {
    public PuntosInsuficienteException(String message) {
        super(message);
    }
}
