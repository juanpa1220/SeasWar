package client.model;

import client.control.ClientWindowController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socketRef;
    public DataInputStream reader;
    public DataOutputStream writer;
    private String nombre;
    private boolean running = true;
    private ClientWindowController refClientWindow;
    private int id;
    private int currentTurn = 0;

    public ClientThread(Socket socketRef, ClientWindowController refClientWindow) throws IOException {
        this.socketRef = socketRef;
        this.reader = new DataInputStream(socketRef.getInputStream());
        this.writer = new DataOutputStream(socketRef.getOutputStream());
        this.refClientWindow = refClientWindow;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // esperar hasta que reciba un entero
                int instructionId = reader.readInt();
                switch (instructionId) {
                    case 1: // pasan un mensaje por el chat
                        this.id = reader.readInt();
                        this.currentTurn = reader.readInt();
                        String turn = reader.readUTF();
                        Platform.runLater(() -> {
                            this.refClientWindow.printTurn(turn);
                        });
                        break;
                    case 2: // pasan un mensaje por el chat
                        String user = reader.readUTF();
                        String message = reader.readUTF();
                        //System.out.println("CLIENTE Recibido mensaje: " + mensaje);
                        Platform.runLater(() -> {
                            this.refClientWindow.writeMassage(user + " >> " + message + "\n", true);
                        });
//                        refClientWindow.addMensaje(user+"> " + message);
                        break;
                    case 3: // pasan un mensaje privado por el chat
                        String privateUser = reader.readUTF();
                        String privateMessage = reader.readUTF();
                        Platform.runLater(() -> {
                            this.refClientWindow.writeMassage(privateUser + " >> " + privateMessage + "\n", false);
                        });
                        break;

                    case 4: // pasan un error de mensaje por el chat
                        String errorMessage = reader.readUTF();
                        Platform.runLater(() -> {
                            this.refClientWindow.writeError(errorMessage);
//                            this.refClientWindow.writeMassage( ">> " + errorMessage + "\n", true);
                        });
                        break;
//                    case 3: // pasan un mensaje por el chat
//                        String usuario2 = reader.readUTF();
//                        int dado1 = reader.readInt();
//                        int dado2 = reader.readInt();
//                        this.currentTurn = reader.readInt();
//                        refClientWindow.pintarSiguienteTurno (reader.readUTF());
//                        refClientWindow.pintarDados(dado1, dado2, usuario2);
//                        break;
//                    case 4: //partida iniciada
//                        refClientWindow.pintarPartidaIniciada();
//
//                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getIdentificador() {
        return id;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }
}
