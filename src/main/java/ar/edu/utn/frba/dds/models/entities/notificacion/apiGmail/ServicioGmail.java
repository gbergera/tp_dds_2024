package ar.edu.utn.frba.dds.models.entities.notificacion.apiGmail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Properties;

public class ServicioGmail {
    private Properties properties;
    private Session session;


    public static void enviarMensaje(String mail, String mensajeANotificar, String asunto) throws IOException, MessagingException {


        ServicioGmail m = new ServicioGmail(MailConfig.urlApi);

        m.enviarEmail(asunto
                , mensajeANotificar, mail);



    }

    public ServicioGmail(String ruta) throws IOException {
        this.properties = new Properties();
        loadConfig(ruta);
        session = Session.getDefaultInstance(properties);
    }

    private void loadConfig(String ruta) throws InvalidParameterException, IOException {
        InputStream is = new FileInputStream(ruta);
        this.properties.load(is);
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        checkConfig();
    }

    private void checkConfig() throws InvalidParameterException {

        String[] keys = {
                "mail.smtp.host",
                "mail.smtp.port",
                "mail.smtp.user",
                "mail.smtp.password",
                "mail.smtp.starttls.enable",
                "mail.smtp.auth"
        };

        for (int i = 0; i < keys.length; i++) {
            if (this.properties.get(keys[i]) == null) {
                throw new InvalidParameterException("No existe la clave " + keys[i]);
            }
        }

    }

    public void enviarEmail(String asunto, String mensaje, String correo) throws MessagingException {

        MimeMessage contenedor = new MimeMessage(session);
        contenedor.setFrom(new InternetAddress((String) this.properties.get("mail.smtp.user")));
        contenedor.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
        contenedor.setSubject(asunto);
        contenedor.setContent(mensaje, "text/html");
        Transport t = session.getTransport("smtp");
        t.connect((String) this.properties.get("mail.smtp.user"), (String) this.properties.get("mail.smtp.password"));
        t.sendMessage(contenedor, contenedor.getAllRecipients());

    }



}