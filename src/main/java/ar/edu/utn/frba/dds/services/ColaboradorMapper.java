package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;

public class ColaboradorMapper {

    public static Colaborador fromDTO(ColaboradorDTO dto) {
        Colaborador colaborador = new Colaborador();
        colaborador.setNombre(dto.getNombre());
        colaborador.setApellido(dto.getApellido());

        // Aquí se puede hacer una lógica para mapear "contacto" a teléfono o mail
        if (dto.getContacto().contains("@")) {
            colaborador.setMail(dto.getContacto());
        } else {
            colaborador.setTelefono(dto.getContacto());
        }

        colaborador.setPuntos(dto.getPuntos());

        // Otras asignaciones según sea necesario...
        return colaborador;
    }
}

