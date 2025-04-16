package ar.edu.utn.frba.dds.contrasenia;

import ar.edu.utn.frba.dds.models.entities.contrasenia.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestContrasenia {

    ValidadorContrasenia validador;

    @BeforeEach
    public void setup() {
        String archivoContraseniasDebiles = "src/main/resources/top10passwords.txt";
        ValidacionContraseniaDebil validacionDebil = new ValidacionContraseniaDebil(archivoContraseniasDebiles);
        ValidacionLongitud validacionLongitud = new ValidacionLongitud(64, 8);
        ValidacionDiferenteAlNombre validacionDiferenteAlNombre = new ValidacionDiferenteAlNombre();

        validador = new ValidadorContrasenia();
        validador.agregarEstrategiaDeValidacion(validacionDebil);
        validador.agregarEstrategiaDeValidacion(validacionLongitud);
        validador.agregarEstrategiaDeValidacion(validacionDiferenteAlNombre);
    }

    @Test
    public void testContraseniaCorrecta(){
        Assertions.assertTrue(validador.esValida("user1","ContraseñaSegura123!"));
        Assertions.assertTrue(validador.esValida("user1","P@ssw0rd"));
    }
    @Test
    public void testContraseniaDebilLongitud(){
        Assertions.assertFalse(validador.esValida("user1","jo"));
    }
    @Test
    public void testContraseniaIgualNombre(){
        Assertions.assertFalse(validador.esValida("username551","username551"));
    }
    @Test
    public void testContraseniaDebil(){
        Assertions.assertFalse(validador.esValida("user1","password123"));
    }
}
