package ar.edu.utn.frba.dds.converters;


import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TipoIncidenteConverter implements AttributeConverter<TipoIncidente, String> {

    @Override
    public String convertToDatabaseColumn(TipoIncidente tipoIncidente) {
        if (tipoIncidente == null) {
            return null;
        }
        return tipoIncidente.getDescripcion();
    }

    @Override
    public TipoIncidente convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return new TipoIncidente(dbData);
    }
}