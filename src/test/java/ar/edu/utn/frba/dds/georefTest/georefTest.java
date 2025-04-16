package ar.edu.utn.frba.dds.georefTest;

import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.ServiceGeoref;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

public class georefTest {
/*
    @Test
    public void traerDireccionCompleta() throws IOException {

         String calle = "Av. Medrano";
         Integer numero = 951;
         Provincia provincia = new Provincia();
         provincia.setId(13);
         provincia.setNombre("CABA");
         Municipio municipio = new Municipio();
         municipio.setProvincia(provincia);
         Localidad localidad = new Localidad();
         localidad.setMunicipio(municipio);


         DireccionAdaptada direccion = new DireccionAdaptada();
         direccion.setCalle(calle);
         direccion.setNumero(numero);
         direccion.setLocalidad(localidad);

         System.out.println(direccion.direccionCompleta());

    }
    @Test
    public void testListadoDeProvincias() throws IOException {
        ServiceGeoref serviceGeoref = ServiceGeoref.instancia();
        List<Provincia> provincias = serviceGeoref.listadoDeProvincias().provincias;

        for(Provincia provinciaAdaptada: provincias){
            System.out.println(provinciaAdaptada.getNombre());
        }


    }

    @Test
    public void testListadoDeMunicipiosXProvincia() throws IOException {
        ServiceGeoref serviceGeoref = ServiceGeoref.instancia();
        Provincia provincia = new Provincia();
        provincia.setId(02);
        provincia.setNombre("CABA");
        List<Municipio> municipios = serviceGeoref.listadoDeMunicipiosPorProvincia(provincia.getId()).municipios;

        for(Municipio unMunicipio: municipios){
            System.out.println(unMunicipio.getNombre()+ "  " + unMunicipio.getProvincia().getNombre());
        }
    }

*/


}
