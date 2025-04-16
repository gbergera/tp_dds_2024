package ar.edu.utn.frba.dds.server.utils;

import io.javalin.http.Context;
import javax.mail.MessagingException;
import java.io.IOException;

public interface ICrudViewsHandler {
    void index(Context context);
    void show(Context context);
    void create(Context context);
    void save(Context context) throws MessagingException, IOException;
    void edit(Context context);
    void update(Context context);
    void delete(Context context);
}
