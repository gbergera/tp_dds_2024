package ar.edu.utn.frba.dds.models.entities.reportes;

public class ExportadorAPDF {
    private AdapterApachePDF adapter;

    public ExportadorAPDF(AdapterApachePDF adapter) {
        this.adapter = adapter;
    }

    public String exportar(Documento documento){
        return this.adapter.exportar(documento);
    }
}
