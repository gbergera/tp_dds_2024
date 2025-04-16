package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.TecnicoRepository;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UsuarioController extends Controller implements ICrudViewsHandler {
    private UsuarioRepository repoDeUsuarios;
    private ColaboradorRepository repoDeColaboradores;
    private TecnicoRepository tecnicoRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository repoDeUsuarios, ColaboradorRepository repoDeColaboradores, TecnicoRepository tecnicoRepository) {
        this.repoDeUsuarios = repoDeUsuarios;
        this.repoDeColaboradores = repoDeColaboradores;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    public void index(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);

        Map<String, Object> model = new HashMap<>();
        List<Usuario> usuarios = this.repoDeUsuarios.buscarTodos();
        model.put("usuarios", usuarios);
        context.render("", model);
    }

    @Override
    public void show(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);

        if(usuarioLogueado == null) context.redirect("/login");
        Usuario usuario = (Usuario) this.repoDeUsuarios.buscar(Long.parseLong(context.pathParam("id")));

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        context.render("", model);
    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);

        String username = context.pathParam("nombre");
        if (this.repoDeUsuarios.buscarPorNombre(username) != null) {
            String mensaje = "El nombre de usuario ya existe";
            Map<String, Object> model = new HashMap<>();
            model.put("acceso", mensaje);
            context.render("register.hbs", model);
            return;
        }

        Usuario usuario = new Usuario();
        this.asignarParametrosParaCrear(usuario, context);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeUsuarios.guardar(usuario);
        tx.commit();
        context.status(HttpStatus.CREATED);
        context.redirect("/usuarios");
    }

    @Override
    public void edit(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);


    }

    @Override
    public void update(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);
        Usuario usuario = (Usuario) this.repoDeUsuarios.buscar(Long.parseLong(context.pathParam("id")));


        this.asignarParametrosParaGuardar(usuario,context);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeUsuarios.actualizar(usuario);
        tx.commit();
        context.redirect("/usuarios");
    }

    @Override
    public void delete(Context context) {
        Usuario usuarioLogueado = super.usuarioLogueado(context);
        Usuario usuario = (Usuario) this.repoDeUsuarios.buscar(Long.parseLong(context.pathParam("id")));
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeUsuarios.darDeBaja(usuario);
        tx.commit();
        context.redirect("/usuarios");
    }
    public void verify(@NotNull Context context) {
        List<Usuario> usuarios = this.repoDeUsuarios.buscarTodos();
        List<Usuario> usuario_filtrado= usuarios.stream().filter(u -> u.getNombreUsuario().equals(context.formParam("user"))).toList();
        if (usuario_filtrado.isEmpty()) {
            String mensaje = "Usuario incorrecto";
            Map<String, Object> model = new HashMap<>();
            model.put("acceso", mensaje);
            context.render("login.hbs", model);
             return;
        }
        else {
            Usuario usuario = usuario_filtrado.get(0);
            if (!bCryptPasswordEncoder.matches(context.formParam("password"), usuario.getContrasenia())) {
                String mensaje = "Contraseña incorrecta";
                Map<String, Object> model = new HashMap<>();
                model.put("acceso", mensaje);
                context.render("login.hbs", model);
                return;
            }
            if (!usuario.getEstado()) {
                String mensaje = "Usuario inactivo - Comunicate con el administrador";
                Map<String, Object> model = new HashMap<>();
                model.put("acceso", mensaje);
                context.render("login.hbs", model);
                return;
            }
            else {
                Colaborador colaborador = this.repoDeColaboradores.buscarPorUsuarioId(usuario.getId());
                if(colaborador == null) {
                    if(usuario.getNombreUsuario().equals("admin")) {
                        context.sessionAttribute("tipo-persona", TipoPersona.ADMINISTRADOR);
                        context.sessionAttribute("usuario-id",usuario.getId().toString());
                        context.sessionAttribute("usuario-nombre",usuario.getNombreUsuario());
                        context.redirect("/administrador/usuarios");
                    }else{
                        Tecnico tecnico = this.tecnicoRepository.buscarPorUsuarioId(usuario.getId());
                        context.sessionAttribute("tecnico-id",tecnico.getId().toString());
                        context.sessionAttribute("tipo-persona",tecnico.getTipoPersona());
                        context.sessionAttribute("usuario-id",usuario.getId().toString());
                        context.sessionAttribute("usuario-nombre",usuario.getNombreUsuario());
                        context.redirect("/tecnico");
                    }
                }else{
                    context.sessionAttribute("colaborador-id",colaborador.getId().toString());
                    context.sessionAttribute("tipo-persona",colaborador.getTipoPersona());
                    context.sessionAttribute("usuario-id",usuario.getId().toString());
                    context.sessionAttribute("usuario-nombre",usuario.getNombreUsuario());
                    context.redirect("/dashboard");
                }

            }
        }
    }

    private void asignarParametrosParaGuardar(Usuario usuario, Context context) {
        usuario.setNombreUsuario(context.formParam("nombre"));


        String bCryptedPassword = bCryptPasswordEncoder.encode(context.formParam("contrasenia"));
        usuario.setContrasenia(bCryptedPassword);
    }

    private void asignarParametrosParaCrear(Usuario usuario, Context context) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    }
}
