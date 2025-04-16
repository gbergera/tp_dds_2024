package ar.edu.utn.frba.dds.models.entities.contrasenia;

public interface EstrategiaValidacion {

    boolean verificarContrasenia(String usuario, String contrasenia) throws ValidacionContraseniaException;
}
