package server.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerThread extends Thread {
    private final DataOutputStream writer;
    private final DataInputStream reader;
    private final Socket socketRef;
    private final Server server;
    private final int id;
    private String name;
    private boolean running = true;


    public ServerThread(Socket socketRef, Server server, int id) throws IOException {
        this.socketRef = socketRef;
        this.reader = new DataInputStream(socketRef.getInputStream());
        this.writer = new DataOutputStream(socketRef.getOutputStream());
        this.server = server;
        this.id = id;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // esperar hasta que reciba un entero
                int instructionId = reader.readInt();

                switch (instructionId) {
                    case 1: // pasan el nombre del usuario
                        this.name = reader.readUTF();

                        writer.writeInt(1);
                        writer.writeInt(id);
                        writer.writeInt(server.getTurn());
                        writer.writeUTF(server.connections.get(server.getTurn()).name);
//                        writer.writeUTF(server.conexiones.get(server.getTurno()).nombre);
                        break;
                    case 2: // pasan un mensaje por el chat
                        String message = reader.readUTF();
                        for (ServerThread serverThread : this.server.connections) {
                            serverThread.writer.writeInt(2);
                            serverThread.writer.writeUTF(this.name);
                            serverThread.writer.writeUTF(message);
                        }
                        break;
                    case 3: // pasan un mensaje por el chat
                        String privateTo = reader.readUTF();
                        String privateMessage = reader.readUTF();
                        AtomicBoolean flag = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.name.equals(privateTo)) {
                                serverThread.writer.writeInt(3);
                                serverThread.writer.writeUTF(this.name);
                                serverThread.writer.writeUTF(privateMessage);
                                flag.set(false);
                            }
                        }
                        if (flag.get()) {
                            this.writer.writeInt(4);
                            this.writer.writeUTF("Mensaje no enviado. Usuario no encontrado.");
                        }
                        break;
//                    case 3: //LANZAR DADOS
//
//                        int primero = (new Random()).nextInt(6) + 1;
//                        int segundo = (new Random()).nextInt(6) + 1;
//                        server.lanzamientoInicial[this.id] = primero + segundo;
//                        server.refPantalla.addMensaje(server.printArregloDados());
//
//                        /*writer.writeInt(3);
//                        writer.writeUTF(nombre);
//                        writer.writeInt(primero);
//                        writer.writeInt(segundo);
//                         */
//                        int turno = server.getTurnoSiguiente();
//                        String nombreDelTurno = server.conexiones.get(turno).nombre;
//                        for (int i = 0; i < server.conexiones.size(); i++) {
//
//                            ThreadServidor current = server.conexiones.get(i);
//                            current.writer.writeInt(3);
//                            current.writer.writeUTF(nombre);
//                            current.writer.writeInt(primero);
//                            current.writer.writeInt(segundo);
//                            current.writer.writeInt(turno);
//                            current.writer.writeUTF(nombreDelTurno);
//                        }
//
//                        break;
//                    case 4:
//                        server.iniciarPartida();
//                        for (int i = 0; i < server.conexiones.size(); i++) {
//                            ThreadServidor current = server.conexiones.get(i);
//                            current.writer.writeInt(4);
//
//                        }
//
//                        break;

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
