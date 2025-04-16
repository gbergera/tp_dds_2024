package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.repositories.ColaboracionRepository;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.PersonaVulnerableRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;

import java.util.*;

public class PersonaVulnerableController extends Controller implements ICrudViewsHandler {
    private PersonaVulnerableRepository repositorioPersonaVulnerable;
    private ColaboradorRepository repositorioColaboradores;

    public PersonaVulnerableController(PersonaVulnerableRepository repo, ColaboradorRepository repoColaboradores){
        this.repositorioPersonaVulnerable = repo;
        this.repositorioColaboradores = repoColaboradores;
    }
    @Override
    public void index(Context context) {
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        int page = context.queryParamAsClass("page", Integer.class).getOrDefault(1); // Página actual
        int size = context.queryParamAsClass("size", Integer.class).getOrDefault(5); // Tamaño de página (5 por defecto)

        // Obtener todas las personas y paginar
        List<PersonaSituacionVulnerable> personas = this.repositorioPersonaVulnerable.buscarPorColaboradorResponsable(colaboradorId);
        personas.sort(Comparator.comparing(PersonaSituacionVulnerable::getFechaRegistro).reversed());

        // Calcular paginación
        int totalRegistros = personas.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / size);

        // Sublista para la página actual
        int fromIndex = (page - 1) * size;

        // Asegurarse de que fromIndex esté dentro del tamaño de la lista
        if (fromIndex >= totalRegistros) {
            fromIndex = Math.max(0, totalRegistros - size); // Ajustar a la última página si se excede
        }

        int toIndex = Math.min(fromIndex + size, totalRegistros);
        List<PersonaSituacionVulnerable> personasPaginadas = personas.subList(fromIndex, toIndex);
        List<Map<String, Object>> paginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            Map<String, Object> pagina = new HashMap<>();
            pagina.put("numero", i);
            pagina.put("clase", (i == page) ? "active" : ""); // Si es la página actual, agrega la clase "active"
            paginas.add(pagina);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("paginas", paginas);

        model.put("personas", personasPaginadas);  // Solo pasamos las personas de esta página
        model.put("personaActive", true);
        model.put("paginaActual", page);
        model.put("totalPaginas", totalPaginas);

        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);

        context.render("registroDePersonas.hbs", model);
    }





    @Override
    public void show(Context context) {

    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {

    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {

    }
}
