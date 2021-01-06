package server.model;

import server.control.ServerWindowController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private final ServerWindowController refServerWindow;
    public ArrayList<ServerThread> connections;
    private boolean running = true;
    private ServerSocket srv;
    private int turn;
    private boolean hasInit = false;
    private int numGamers = 0;

//    public int lanzamientoInicial[] = new int[6];

    public Server(ServerWindowController serverWindowController) {
        this.refServerWindow = serverWindowController;
        this.connections = new ArrayList<ServerThread>();
    }

//    public String printArregloDados() {
//        String str = "Arreglo:  ";
//        for (int i = 0; i < conections.size(); i++) {
//            str += lanzamientoInicial[i] + "   ";
//        }
//        return str;
//    }

    public void InitGame() {
        if (this.numGamers > 2 && this.numGamers <= 6) {
            this.hasInit = true;
        } else {
            this.refServerWindow.addLogMessage(".: Número invalido de jugadores conectados");
        }

    }

    public int getNextTurn() {
        if (++this.turn >= this.connections.size()) {
            this.turn = 0;
        }
        return this.turn;
    }

    public int getTurn() {
        return this.turn;
    }

    @Override
    public void run() {
        int contador = 0;
        try {
            srv = new ServerSocket(35775);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {
            try {
                this.refServerWindow.addLogMessage(".: Esperando conexiones");
                Socket refSocket = srv.accept();
                if (!hasInit) {
                    if (numGamers > 6) {
                        this.refServerWindow.addLogMessage(".: Ya ha alcanzado la máxima cantidad de jugadores");
                    } else {
                        this.refServerWindow.addLogMessage(".: Conexion realizada: " + (++contador));
                        // Thread
                        ServerThread newThread = new ServerThread(refSocket, this, this.connections.size());
                        this.connections.add(newThread);
                        newThread.start();
                        this.numGamers++;
                    }
                } else {
                    this.refServerWindow.addLogMessage(".: Conexion denegada, partida ya inicio");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
