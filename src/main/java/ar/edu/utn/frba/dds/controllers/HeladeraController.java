package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.ModeloHeladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.repositories.AlertaRepository;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.ModeloHeladeraRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;

import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HeladeraController extends Controller implements ICrudViewsHandler {
    private HeladeraRepository repoDeHeladeras;
    private AlertaRepository repositorioDeAlertas; // Repositorio de alertas
    private ModeloHeladeraRepository repoDeModelos;
    private Logger logger = Logger.getLogger(HeladeraController.class.getName());

    public HeladeraController(HeladeraRepository repoDeHeladeras,AlertaRepository repositorioDeAlertas, ModeloHeladeraRepository repoDeModelos) {

        this.repoDeHeladeras = repoDeHeladeras;
        this.repositorioDeAlertas = repositorioDeAlertas;
        this.repoDeModelos = repoDeModelos;

        // Aquí llenamos el repositorio con una alerta de ejemplo si está vacío
        if (this.repositorioDeAlertas.obtenerAlertas().isEmpty()) {
            Map<String, Object> alertaDePrueba = new HashMap<>();
            alertaDePrueba.put("tipo", "Alerta de prueba");
            alertaDePrueba.put("fechaYHora", LocalDate.now());
            alertaDePrueba.put("ubicacion", "Heladera 1");
            EntityTransaction tx = entityManager().getTransaction();
            try{
                if (!tx.isActive())
                    tx.begin();
                this.repositorioDeAlertas.agregarAlerta(alertaDePrueba);
            }catch (Exception e){
                logger.log(Level.SEVERE,"Error al agregar alerta de prueba: " + e.getMessage(), e);
                if (tx.isActive()) {
                    tx.rollback();
                }
            }
        }
    }

    @Override
    public void index(Context context) {
        List heladeras = this.repoDeHeladeras.buscarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("heladeras", heladeras);
        model.put("heladerasActive", true);
        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        context.render("heladeras.hbs", model );
    }

    public void getAll(Context context) {
        List<Heladera> heladeras = this.repoDeHeladeras.buscarTodos();
        if (heladeras == null) {
            context.status(404).result("No se encontraron heladeras.");
            return;
        }

        List<Heladera> heladerasActivas = heladeras.stream()
                .filter(Heladera::getActiva)
                .collect(Collectors.toList());

        context.json(heladerasActivas);
    }
    public void getModelos(Context context) {
        List<ModeloHeladera> modelos = this.repoDeModelos.buscarTodos();
        if (modelos == null) {
            context.status(404).result("No se encontraron modelos de heladeras.");
            return;
        }

        context.json(modelos);
    }
    @Override
    public void show(Context context) {
        Heladera heladera = (Heladera) this.repoDeHeladeras.buscar(Long.parseLong(context.pathParam("id")));
        Map<String, Object> model = new HashMap<>();
        model.put("heladera", heladera);
        //context.render(url, model );
    }

    @Override
    public void create(Context context) {
        Heladera heladera = (Heladera) this.repoDeHeladeras.buscar(Long.parseLong(context.pathParam("id")));
        Map<String, Object> model = new HashMap<>();
        model.put("heladera", heladera);
        //context.render(url, model );
    }

    public void mostrarAlertas(Context ctx) {
        try {
            List<Heladera> heladeras = repoDeHeladeras.buscarTodos();
            Long administradorId = Long.valueOf(Objects.requireNonNull(ctx.sessionAttribute("colaborador-id")));
            List<Map<String, Object>> alertasRelevantes = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


            for (Heladera heladera : heladeras) {
                List<Suscripcion> suscripciones = heladera.getSuscripciones();
                if (suscripciones.stream().anyMatch(s -> s.getColaborador().getId().equals(administradorId)) || heladera.getAdministrador().getId().equals(administradorId)) {

                    List<ReporteDeIncidentes> incidentes = heladera.getRegistroDeFallos();

                    for (ReporteDeIncidentes incidente : incidentes) {
                        // Verificar si el incidente no es null y tiene un incidente asociado
                        if (incidente != null && incidente.getIncidente() != null) {
                            String descripcion = incidente.getIncidente().getDescripcion();
                            // Verificar la descripción antes de compararla
                            if ("ALERTA DE CONEXION".equals(descripcion) ||
                                "ALERTA DE TEMPERATURA".equals(descripcion) ||
                                "ALERTA DE FRAUDE".equals(descripcion)) {

                                Map<String, Object> alerta = new HashMap<>();
                                alerta.put("tipo", descripcion);

                                // Formatear la fecha y hora del incidente
                                String fechaHoraActual = incidente.getFechaYHora().format(formatter);
                                alerta.put("fechaYHora", fechaHoraActual);
                                alerta.put("nombreHeladera", heladera.getNombre());
                                alerta.put("ubicacionHeladera", heladera.direccionActual().direccionCompleta());

                                alertasRelevantes.add(alerta);
                            }
                        }
                    }
                }
            }
            // Ordenar las alertas por fechaYHora de más reciente a menos reciente
            alertasRelevantes.sort((a, b) -> {
                LocalDateTime fechaA = LocalDateTime.parse((String) a.get("fechaYHora"), formatter);
                LocalDateTime fechaB = LocalDateTime.parse((String) b.get("fechaYHora"), formatter);
                return fechaB.compareTo(fechaA); // Orden descendente
            });


            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(2);


            int totalRegistros = alertasRelevantes.size();
            int totalPaginas = (int) Math.ceil((double) totalRegistros / size);


            int fromIndex = (page - 1) * size;
            if (fromIndex >= totalRegistros) {
                fromIndex = Math.max(0, totalRegistros - size);
            }
            int toIndex = Math.min(fromIndex + size, totalRegistros);
            List<Map<String, Object>> alertasPaginadas = alertasRelevantes.subList(fromIndex, toIndex);


            List<Map<String, Object>> paginas = new ArrayList<>();
            for (int i = 1; i <= totalPaginas; i++) {
                Map<String, Object> pagina = new HashMap<>();
                pagina.put("numero", i);
                pagina.put("clase", (i == page) ? "active" : "");
                paginas.add(pagina);
            }

            Map<String, Object> model = new HashMap<>();
            TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");
            model.put("isFisica", tipoPersona == TipoPersona.FISICA);
            model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
            model.put("alertasActive", true);
            model.put("alertas", alertasPaginadas);
            model.put("paginas", paginas);
            model.put("paginaActual", page);
            model.put("totalPaginas", totalPaginas);


            ctx.render("alertas.hbs", model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar las alertas: " + e.getMessage(), e);
            ctx.status(500).result("Error al mostrar las alertas: " + e.getMessage());
        }
    }

    public void misHeladeras(Context context) throws IOException {
        Long administradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        List<Heladera> misHeladeras = repoDeHeladeras.buscarPorAdministrador(administradorId);
        for (Heladera heladera : misHeladeras) {
            heladera.setDireccionCompleta(heladera.direccionActual().direccionCompleta());
        }
        Map<String, Object> model = new HashMap<>();
        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        model.put("heladeras", misHeladeras);
        model.put("misHeladerasActive", true);
        context.render("misheladeras.hbs", model);
    }



    @Override
    public void save(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void edit(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void update(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void delete(Context context) {
        Map<String, Object> model = new HashMap<>();
    }
}
