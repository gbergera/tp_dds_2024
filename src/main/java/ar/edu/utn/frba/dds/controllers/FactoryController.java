package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.repositories.TipoIncidenteRepository;

public class FactoryController {
    public static Object controller(String nombre) {
        Object controller = null;
        switch (nombre) {
            case "Usuario": controller = ServiceLocator.instanceOf(UsuarioController.class); break;
            case "Colaborador": controller = ServiceLocator.instanceOf(ColaboradorController.class); break;
            case "Heladera": controller = ServiceLocator.instanceOf(HeladeraController.class); break;
            case "Tecnico": controller = ServiceLocator.instanceOf(TecnicoController.class); break;
            case "Colaboracion": controller = ServiceLocator.instanceOf(ColaboracionController.class); break;
            case "PersonaVulnerable": controller = ServiceLocator.instanceOf(PersonaVulnerableController.class); break;
            case "Admin": controller = ServiceLocator.instanceOf(AdminController.class); break;
            case "Suscripciones":controller = ServiceLocator.instanceOf(SuscripcionesController.class); break;
            case "Registro-Fallas":controller = ServiceLocator.instanceOf(RegistroDeFallaController.class); break;
            case "TipoIncidente":controller = ServiceLocator.instanceOf(TipoIncidenteController.class);break;
            case "Reporte": controller = ServiceLocator.instanceOf(ReporteController.class); break;
            case "Visita": controller = ServiceLocator.instanceOf(VisitasController.class); break;
        }
        return controller;
    }
}
