package ar.edu.utn.frba.dds.models.entities.reportes;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Documento {
    private Map<String, List<String>> datos;
    public Documento(){
        this.datos = new HashMap<String, List<String>>();
    }

    public void agregarDato(String clave, String ... valor){
        this.agregarClaveSiNoExiste(clave);
        Collections.addAll(this.datos.get(clave), valor);
    }

    private void agregarClaveSiNoExiste(String clave){
        if(!this.datos.containsKey(clave)){
            this.datos.put(clave, new ArrayList<String>());
        }
    }

    public void setDatos(Map<String, List<String>> datos) {
        this.datos = datos;
    }

    public Map<String, List<String>> datos() {
        return this.datos;
    }

}


