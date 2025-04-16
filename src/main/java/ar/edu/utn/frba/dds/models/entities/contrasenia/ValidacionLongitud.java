package ar.edu.utn.frba.dds.models.entities.contrasenia;

public class ValidacionLongitud implements EstrategiaValidacion{

    private int longMaxima;
    private int longMinima;

    public ValidacionLongitud(int longMaxima, int longMinima) {
        this.longMaxima = longMaxima;
        this.longMinima = longMinima;
    }

    @Override
    public boolean verificarContrasenia(String usuario, String contrasenia) throws ValidacionContraseniaException {
        int longitud = contrasenia.length();
        if (longitud < longMinima || longitud > longMaxima) {
            throw new ValidacionContraseniaException("La longitud de la contraseña debe estar entre " + longMinima + " y " + longMaxima + " caracteres.");
        }
        return true;
    }
}
