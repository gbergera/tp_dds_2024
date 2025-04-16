package ar.edu.utn.frba.dds.models.entities.puntos;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.HacerseCargoHeladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class CalculadorDePuntaje {
    @Setter
    private LocalDateTime fechahoraUltimaActualizacion;
    private static final AtomicBoolean cronStarted = new AtomicBoolean(false);

    private Logger logger = Logger.getLogger(CalculadorDePuntaje.class.getName());


    public void calcularPuntaje(Colaborador colaborador) {
        LocalDate fechaActual = LocalDate.now();
        AtomicReference<Double> puntajeTotal = new AtomicReference<>(0.0);
        AtomicReference<Double> puntajeHacerseCargo = new AtomicReference<>(0.0);
        AtomicInteger contadorHacerseCargo = new AtomicInteger(0);

        colaborador.getColaboraciones().stream()
                .filter(c -> fechahoraUltimaActualizacion == null || c.getFechaColaboracion().isAfter(fechahoraUltimaActualizacion) && c.getFueExitosa())
                .forEach(c -> {
                    if (c instanceof HacerseCargoHeladera) {
                        puntajeHacerseCargo.updateAndGet(v -> v + c.calcularPuntaje());
                        contadorHacerseCargo.incrementAndGet();
                    } else {
                        puntajeTotal.updateAndGet(v -> v + c.calcularPuntaje());
                    }
                });

        // Multiplica el puntaje acumulado por el número de veces que apareció HacerseCargoHeladera
        if (contadorHacerseCargo.get() > 0) {
            puntajeTotal.updateAndGet(v -> v + (puntajeHacerseCargo.get() * contadorHacerseCargo.get()));
        }

        AtomicReference<Double> puntosARestar = new AtomicReference<>(0.0);

        //resto ofertas canjeadas si fecha is null
        if (fechahoraUltimaActualizacion == null) {
            colaborador.getOfertasCanjeadas().forEach(oferta -> {
                puntosARestar.updateAndGet(v -> v + oferta.getPuntos());
            });
        }

        colaborador.setPuntos(puntajeTotal.get() + colaborador.getPuntos() -puntosARestar.get());

    }

    public void iniciarCron() {
        if (cronStarted.get()) {
            logger.info("Cron job already started. Skipping initialization.");
            return;
        }
        if (fechahoraUltimaActualizacion == null) {
            setFechahoraUltimaActualizacion(LocalDateTime.now());
        }
        ColaboradorRepository colaboradorRepository = ServiceLocator.instanceOf(ColaboradorRepository.class);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Colaborador> colaboradores = colaboradorRepository.buscarTodos();
                for (Colaborador colaborador : colaboradores) {
                    calcularPuntaje(colaborador);
                    colaboradorRepository.abrirTransaccion();
                    colaboradorRepository.actualizar(colaborador);
                    colaboradorRepository.commitTransaccion();
                }setFechahoraUltimaActualizacion(LocalDateTime.now());
               logger.info("Puntajes actualizados --------------------CORRE CADA 1 MINUTO---------------");
            }
        }, 0,  60 * 1000);

        cronStarted.set(true);
    }
}