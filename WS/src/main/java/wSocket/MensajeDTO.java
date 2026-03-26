package wSocket;

/**
 *
 * @author victoria
 */
public class MensajeDTO {

    private String remitente;
    private String destinatario;
    private String tipo;
    private String contenido;

    public MensajeDTO() {
    }

    public MensajeDTO(String remitente, String tipo, String contenido) {
        this.remitente = remitente;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getContenido() {
        return contenido;
    }
    
    

}
