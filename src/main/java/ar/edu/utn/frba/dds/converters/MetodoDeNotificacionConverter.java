package ar.edu.utn.frba.dds.converters;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.Whatsapp;
import ar.edu.utn.frba.dds.models.entities.notificacion.Telegram;

@Converter(autoApply = true)
public class MetodoDeNotificacionConverter implements AttributeConverter<MetodoDeNotificacion, String> {

    @Override
    public String convertToDatabaseColumn(MetodoDeNotificacion metodo) {
        if (metodo == null) {
            return null;
        } else
        if (metodo instanceof Email) {
            return "EMAIL";
        } else if (metodo instanceof Telegram) {
            return "TELEGRAM";
        } else if (metodo instanceof Whatsapp) {
            return "WHATSAPP";
        }
        throw new IllegalArgumentException("Unknown MetodoDeNotificacion type");
    }

    @Override
    public MetodoDeNotificacion convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            //DEBERIAMOS HACER CON UN SERVICE LOCATOR
            case "EMAIL":
                return new Email();
            case "TELEGRAM":
                return new Telegram();
            case "WHATSAPP":
                return new Whatsapp();
            default:
                return null;
        }
    }
}
