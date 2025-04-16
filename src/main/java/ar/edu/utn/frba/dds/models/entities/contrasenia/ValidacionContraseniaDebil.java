package ar.edu.utn.frba.dds.models.entities.contrasenia;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ValidacionContraseniaDebil implements EstrategiaValidacion{

    private List<String> contraseniasDebiles;

    public ValidacionContraseniaDebil(String archivoDeContraseniasDebiles) {
        this.contraseniasDebiles = cargarContraseniasDebiles(archivoDeContraseniasDebiles);
    }

    private List<String> cargarContraseniasDebiles(String archivoDeContraseniasDebiles) {
        List<String> contrasenias = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoDeContraseniasDebiles))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contrasenias.add(linea.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contrasenias;
    }

    @Override
    public boolean verificarContrasenia(String usuario,String contrasenia) throws ValidacionContraseniaException {
        if (contraseniasDebiles.contains(contrasenia)) {
            throw new ValidacionContraseniaException("La contraseña es demasiado débil.");
        }
        return true;
    }
}
