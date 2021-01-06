package client.model;

import client.ClientMain;
import client.control.ClientWindowController;

import java.net.Socket;

public class Client {
    private Socket socketRef;
    private ClientWindowController refClientWindow;
    public ClientThread clientThread;
    private String name;

    public Client(ClientWindowController clientWindowController) {
        this.refClientWindow = clientWindowController;
        this.refClientWindow.setRefClient(this);
    }

    public boolean connect(String server, int port) {
        try {
            //  port 35775
            this.socketRef = new Socket(server, port);
            this.clientThread = new ClientThread(socketRef, refClientWindow);
            this.clientThread.start();

//            String name = JOptionPane.showInputDialog("Introduzca un Nick:");
            this.clientThread.writer.writeInt(1); //instruccion para el switch del thraed servidor
            this.clientThread.writer.writeUTF(this.name); //instruccion para el switch del thraed servidor
//            this.refClientWindow.setTitle(name);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public void setName(String name) {
        this.name = name;
        ClientMain.primaryStage.setTitle(name);
    }

}
