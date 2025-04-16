package ar.edu.utn.frba.dds.models.entities.contrasenia;

import java.util.ArrayList;
import java.util.List;

public class ValidadorContrasenia {
    private List<EstrategiaValidacion> estrategiasDeValidacion;

    public ValidadorContrasenia() {
        this.estrategiasDeValidacion = new ArrayList<>();
        //ver si agregamos las predeterminadas aca
    }

    public void agregarEstrategiaDeValidacion(EstrategiaValidacion estrategia){
        estrategiasDeValidacion.add(estrategia);
    }


    public void validar(String usuario, String contrasenia) throws ValidacionContraseniaException {
        for (EstrategiaValidacion estrategia : estrategiasDeValidacion) {
            estrategia.verificarContrasenia(usuario, contrasenia);
        }
    }

    public boolean esValida(String usuario,String contrasenia){
        try {
            for (EstrategiaValidacion estrategia : estrategiasDeValidacion) {
                if (!estrategia.verificarContrasenia(usuario,contrasenia)) {
                    return false;
                }
            }
        } catch (ValidacionContraseniaException e) {
            e.printStackTrace();
            return false; // O puedes lanzar una excepción específica si es necesario
        }
        return true;
    }

}
