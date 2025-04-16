package ar.edu.utn.frba.dds.models.entities.contrasenia;

public class ValidacionDiferenteAlNombre implements EstrategiaValidacion{

    @Override
    public boolean verificarContrasenia(String usuario, String contrasenia) throws ValidacionContraseniaException {
        if (usuario.equals(contrasenia)) {
            throw new ValidacionContraseniaException("La contraseña no puede ser igual al nombre de usuario.");
        }
        return true;

    }
}
