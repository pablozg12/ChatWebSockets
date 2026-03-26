/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wSocket;

/**
 *
 * @author erika
 */
import com.google.gson.Gson;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import jakarta.websocket.*;

@ClientEndpoint
public class ChatClientFrame extends JFrame {
    private Session session;
    private Gson gson = new Gson();
    private String miUsuario;

    // Componentes de la interfaz
    private JTextArea txtChat = new JTextArea();
    private JTextField txtMensaje = new JTextField();
    private JComboBox<String> comboTipo = new JComboBox<>(new String[]{"PUBLIC", "PRIVATE"});
    private JTextField txtDestino = new JTextField(10); // Para escribir a quie va el mensaje privado

    public ChatClientFrame(String username) {
        this.miUsuario = username;
        initUI();
        conectarServidor();
    }

    private void initUI() {
        setTitle("Chat de: " + miUsuario);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // area central para los mensajes
        txtChat.setEditable(false);
        txtChat.setLineWrap(true);
        add(new JScrollPane(txtChat), BorderLayout.CENTER);

        // Panel inferior para enviar mensajes
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(comboTipo, BorderLayout.WEST);
        panelInferior.add(txtMensaje, BorderLayout.CENTER);
        
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(e -> enviarMensaje());
        panelInferior.add(btnEnviar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);
        
        // Panel superior para configurar mensajes privados
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Si es privado, escribe el destinatario: "));
        panelSuperior.add(txtDestino);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Enviar con el enter je
        txtMensaje.addActionListener(e -> enviarMensaje());
    }

    private void conectarServidor() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            // IMPORTANTE: Cambia "TuAppServidor" por el nombre real de tu proyecto de servidor (Context Path)
            String uri = "ws://localhost:8080/WS/chat/" + miUsuario; 
            container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        MensajeDTO m = gson.fromJson(message, MensajeDTO.class);

        SwingUtilities.invokeLater(() -> {
            if ("SYSTEM".equals(m.getTipo())) {
                txtChat.append(">> " + m.getContenido() + "\n");
            } else {
                String prefijo = "PRIVATE".equals(m.getTipo()) ? "[Privado] " : "";
                txtChat.append(prefijo + m.getRemitente() + ": " + m.getContenido() + "\n");
            }
        });
    }

    private void enviarMensaje() {
        String contenido = txtMensaje.getText().trim();
        if (contenido.isEmpty()) return;

        String tipo = (String) comboTipo.getSelectedItem();
        MensajeDTO msg;

        if ("PRIVATE".equals(tipo)) {
            String destino = txtDestino.getText().trim();
            if (destino.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes escribir un destinatario para el mensaje privado.");
                return;
            }
            msg = new MensajeDTO(miUsuario, destino, tipo, contenido);
            txtChat.append("[Privado para " + destino + "]: " + contenido + "\n");
        } else {
            msg = new MensajeDTO(miUsuario, tipo, contenido);

            txtChat.append("Tú: " + contenido + "\n");
        }

        try {
            session.getBasicRemote().sendText(gson.toJson(msg));
            txtMensaje.setText(""); // Limpiar el textpo
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    public static void main(String[] args) {
        // Pedimos el nombre al iniciar
        String nombre = JOptionPane.showInputDialog("Ingresa tu nombre de usuario:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> new ChatClientFrame(nombre.trim()).setVisible(true));
        } else {
            System.exit(0);
        }
    }
}