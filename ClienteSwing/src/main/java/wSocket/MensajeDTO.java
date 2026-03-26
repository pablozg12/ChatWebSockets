/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wSocket;

/**
 *
 * @author erika
 */
public class MensajeDTO {
    private String remitente;
    private String destinatario;
    private String tipo;
    private String contenido;

    public MensajeDTO() {
    }

    // Constructor para mensajes públicos
    public MensajeDTO(String remitente, String tipo, String contenido) {
        this.remitente = remitente;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    // Constructor para mensajes privados
    public MensajeDTO(String remitente, String destinatario, String tipo, String contenido) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public String getRemitente() { return remitente; }
    public void setRemitente(String remitente) { this.remitente = remitente; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}