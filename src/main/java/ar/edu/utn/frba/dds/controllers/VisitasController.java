package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.persona.Visita;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.VisitasRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

public class VisitasController extends Controller implements ICrudViewsHandler {
    private VisitasRepository repoDeVisitas;
    private ColaboradorRepository repoDeColaboradores;

    public VisitasController(VisitasRepository repoDeVisitas, ColaboradorRepository repoDeColaboradores) {
        this.repoDeVisitas = repoDeVisitas;
        this.repoDeColaboradores = repoDeColaboradores;
    }

    @Override
    public void index(Context context) {
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));

        List<Visita> visitas = new ArrayList<>(this.repoDeVisitas.obtenerVisitasPorAdministrador(colaboradorId));

        Set<Visita> visitasUnicas = new HashSet<>(visitas);
        visitasUnicas.addAll(this.repoDeVisitas.obtenerVisitasPorSuscripcion(colaboradorId));

        visitas = new ArrayList<>(visitasUnicas);

        Collections.sort(visitas, (v1, v2) -> v1.getFechaYHora().compareTo(v2.getFechaYHora()));

        Map<String, Object> model = new HashMap<>();
        model.put("visitas", visitas);
        model.put("visitasActive", true);

        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        model.put("isAdmin", false);
        context.render("visitasMisHeladeras.hbs", model);
    }



    @Override
    public void show(Context context) {

    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) throws MessagingException, IOException {

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
