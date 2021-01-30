package server.model;

import server.control.ServerWindowController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server extends Thread {
    private final ServerWindowController refServerWindow;
    public ArrayList<ServerThread> connections;
    public ArrayList<ServerThread> hasConfigured;
    private boolean running = true;
    private ServerSocket srv;
    public boolean hasInit = false;
    public int numGamers = 0;

    public ArrayList<Integer> turns;
    private int turnIndex;

//    public int lanzamientoInicial[] = new int[6];

    public Server(ServerWindowController serverWindowController) {
        this.refServerWindow = serverWindowController;
        this.connections = new ArrayList<>();
        this.hasConfigured = new ArrayList<>();
        this.turns = new ArrayList<>();
        this.turnIndex = 0;
    }

//    public String printArregloDados() {
//        String str = "Arreglo:  ";
//        for (int i = 0; i < conections.size(); i++) {
//            str += lanzamientoInicial[i] + "   ";
//        }
//        return str;
//    }

    public void InitGame() {
        this.refServerWindow.addLogMessage(".: La partida ya ha iniciado");
        this.hasInit = true;
    }

    public void setNextTurn() {
        if (++this.turnIndex >= this.turns.size()) {
            this.turnIndex = 0;
        }
    }

    public int getTurn() {
        if (this.turnIndex < 0) {
            return this.turns.get(0);
        }

        return this.turns.get(this.turnIndex);
    }

    @Override
    public void run() {
        try {
            srv = new ServerSocket(35775);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int counter = 0;
        while (running) {
            try {
                this.refServerWindow.addLogMessage(".: Esperando conexiones");
                Socket refSocket = srv.accept();
                if (!hasInit) {
                    if (numGamers > 6) {
                        this.refServerWindow.addLogMessage(".: Ya ha alcanzado la máxima cantidad de jugadores");
                    } else {
                        this.refServerWindow.addLogMessage(".: Conexión realizada: " + (++counter));
                        // Thread
                        int treadId = this.connections.size();
                        ServerThread newThread = new ServerThread(refSocket, this, treadId);
                        this.connections.add(newThread);

                        // add turn id in random position in turns arraylist
                        this.turns.add(new Random().nextInt(this.turns.size() + 1), treadId);

                        newThread.start();
                        this.numGamers++;
                    }
                } else {
                    this.refServerWindow.addLogMessage(".: Conexión denegada, la partida ya inició");
                    DataOutputStream temWriter = new DataOutputStream(refSocket.getOutputStream());
                    temWriter.writeInt(0);
                    temWriter.writeBoolean(true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void printLogMessage(String message) {
        this.refServerWindow.addLogMessage(".: " + message);
    }

    public void removeTurn(int id) {
        for (int i = 0; i < this.turns.size(); i++) {
            if (this.turns.get(i) == id) {
                this.turns.remove(i);
                this.turnIndex--;
                return;
            }
        }
    }
}
