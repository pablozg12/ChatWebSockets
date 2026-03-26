package wSocket;

import java.util.List;

/**
 *
 * @author victoria
 */
public class MensajeDTO {

    private String remitente;
    private String destinatario;
    private String tipo;
    private String contenido;
    private List<String> usuarios;

    public MensajeDTO() {
    }

    public MensajeDTO(String remitente, String tipo, String contenido) {
        this.remitente = remitente;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public MensajeDTO(String remitente, String destinatario, String tipo, String contenido) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public MensajeDTO(String tipo, List<String> usuarios) {
        this.tipo = tipo;
        this.usuarios = usuarios;
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

    public List<String> getUsuarios() {
        return usuarios;
    }

}
