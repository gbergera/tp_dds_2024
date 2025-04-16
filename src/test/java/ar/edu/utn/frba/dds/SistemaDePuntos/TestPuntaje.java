package ar.edu.utn.frba.dds.SistemaDePuntos;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionDinero;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionViandas;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPuntaje {

    @Test
    public void PuntajeColaborador(){/*
        Colaborador colaborador = new Colaborador();
        colaborador.setNombre("mario");
        colaborador.setApellido("diaz");
        colaborador.setMail("marito@gmail.com");
        colaborador.setColaboraciones(new ArrayList<Colaboracion>());
        colaborador.setPuntos(0.0);

        // Colaboración Dinero un dia
        CalculadorDePuntaje calculadorDePuntaje = new CalculadorDePuntaje();

        Colaboracion colaboracion1 = new DonacionDinero(100, LocalDate.of(2023, 5, 10));
        colaborador.agregarColaboracion(colaboracion1);
        calculadorDePuntaje.setFechaUltimaActualizacion(LocalDate.of(2023,5,9));
        calculadorDePuntaje.calcularPuntaje(colaborador);

        Double cant = colaborador.getPuntos();
        assertEquals(50,cant);


        // Colaboración Donación de viandas en otro dia
        calculadorDePuntaje.setFechaUltimaActualizacion(LocalDate.of(2023,5,14));

        Colaboracion colaboracion2 = new DonacionViandas(LocalDate.of(2023, 5, 15), 50);
        colaborador.agregarColaboracion(colaboracion2);
        calculadorDePuntaje.calcularPuntaje(colaborador);
        cant = colaborador.getPuntos();

        assertEquals(125,cant );
        */


    }
}