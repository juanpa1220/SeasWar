package server.model;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerThread extends Thread {
    private final DataOutputStream writer;
    private final DataInputStream reader;

    private final Server server;
    private final int id;
    private String name;
    private boolean running = true;


    public ServerThread(Socket socketRef, Server server, int id) throws IOException {
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
                    case 0: // la partida ya inició?
                        this.writer.writeInt(0);
                        this.writer.writeBoolean(this.server.hasInit);
                        break;
                    case 1: // pasan el nombre del usuario
                        this.name = reader.readUTF();
                        this.writer.writeInt(1);
                        this.writer.writeInt(id);
                        this.writer.writeUTF(this.name);
//                        this.writer.writeInt(server.getTurn());
//                        this.writer.writeUTF(server.connections.get(server.getTurn()).name);
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
                    case 3: // pasa un mensaje privado por el chat
                        String privateTo = reader.readUTF();
                        String privateMessage = reader.readUTF();
                        AtomicBoolean flag = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.name.equals(privateTo) && serverThread.id != this.id && flag.get()) {
                                serverThread.writer.writeInt(3);
                                serverThread.writer.writeUTF(this.name);
                                serverThread.writer.writeUTF(privateMessage);
                                flag.set(false);
                            }
                        }
                        if (flag.get()) {
                            this.writer.writeInt(4);
                            this.writer.writeUTF("Mensaje no enviado. Usuario no encontrado");
                        }
                        break;
                    case 4: // ya ha configurado sus warriors
                        this.server.hasConfigured.add(this);
                        break;

                    case 5: // intenta iniciar el juego
                        if (this.server.hasConfigured.contains(this)) {
                            if (this.server.numGamers < 2) {
                                this.writer.writeInt(4);
                                this.writer.writeUTF("Deben de haber entre dos y seis jugadores conectados para iniciar la partida");
                            } else if (this.server.hasConfigured.size() == this.server.connections.size()) {
                                this.server.InitGame(); // no recibe más jugadores
                                for (ServerThread serverThread : this.server.connections) {
                                    serverThread.writer.writeInt(5);
                                    serverThread.writer.writeInt(server.getTurn()); // id turn
                                    serverThread.writer.writeUTF(server.connections.get(server.getTurn()).name); // name
                                }
                            } else {
                                this.writer.writeInt(3);
                                this.writer.writeUTF("Informe");
                                this.writer.writeUTF("Esperando que los demás jugadores configuren sus luchadores");
                            }
                        } else {
                            this.writer.writeInt(4);
                            this.writer.writeUTF("Debe configurar sus jugadores antes de iniciar el juego");
                        }
                        break;

                    case 6: // ataque
                        String warriorType = reader.readUTF();
                        String attackTo = reader.readUTF();
                        String attack = reader.readUTF();
                        String args = reader.readUTF();
                        int warriorPower = reader.readInt();
                        int warriorId = reader.readInt();
                        if (this.server.getTurn() == this.id) {
                            AtomicBoolean flag2 = new AtomicBoolean(true);

                            for (ServerThread serverThread : this.server.connections) {
                                if (serverThread.name.equals(attackTo) && serverThread.id != this.id && flag2.get()) {
                                    serverThread.writer.writeInt(6);
                                    serverThread.writer.writeUTF(warriorType);
                                    serverThread.writer.writeUTF(attack);
                                    serverThread.writer.writeUTF(args);
                                    serverThread.writer.writeInt(warriorPower);
                                    serverThread.writer.writeInt(warriorId);
                                    flag2.set(false);
                                }
                            }
                            if (flag2.get()) {
                                this.writer.writeInt(4);
                                this.writer.writeUTF("Ataque no ejecutado. Usuario no encontrado");
                                if (warriorPower > 0) {
                                    this.writer.writeInt(14);
                                }
                            }
                        } else {
                            this.writer.writeInt(4);
                            this.writer.writeUTF("No es su turno de jugar");
                            if (warriorPower > 0) {
                                this.writer.writeInt(14);
                            }
                        }
                        break;
                    case 7: // bitácora
                        String inform = reader.readUTF();
                        for (ServerThread serverThread : this.server.connections) {
                            serverThread.writer.writeInt(7);
                            serverThread.writer.writeUTF(inform);
                        }
                        break;

                    case 8: // pasa un success
                        int successToID = reader.readInt();
                        String successMessage = reader.readUTF();
                        AtomicBoolean flag2 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.id == successToID && serverThread.id != this.id && flag2.get()) {
                                serverThread.writer.writeInt(8);
                                serverThread.writer.writeUTF(successMessage);
                                flag2.set(false);
                            }
                        }
                        break;

                    case 9: // next turn
                        this.server.setNextTurn();
                        for (ServerThread serverThread : this.server.connections) {
                            serverThread.writer.writeInt(9);
                            serverThread.writer.writeInt(server.getTurn()); // id turn
                            serverThread.writer.writeUTF(server.connections.get(server.getTurn()).name); // name
                        }
                        break;

                    case 10: // attack log
                        int reportTo = reader.readInt();
                        String report = reader.readUTF();
                        AtomicBoolean flag3 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.id == reportTo && serverThread.id != this.id && flag3.get()) {
                                serverThread.writer.writeInt(10);
                                serverThread.writer.writeUTF(report);
                                flag3.set(false);
                            }
                        }
                        break;

                    case 11: // error
                        int errorTo = reader.readInt();
                        String errorMessage = reader.readUTF();
                        AtomicBoolean flag4 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.id == errorTo && serverThread.id != this.id && flag4.get()) {
                                serverThread.writer.writeInt(4);
                                serverThread.writer.writeUTF(errorMessage);
                                flag4.set(false);
                            }
                        }
                        break;
                    case 12: // give up or loose
//                        this.server.turns.remove(this.id);
                        this.server.removeTurn(this.id);
                        this.writer.writeInt(12);
                        if (this.checkWin()) {
                            this.server.connections.get(this.server.getTurn()).writer.writeInt(11);
                            this.server.connections.get(this.server.getTurn()).writer.writeInt(12);
                        }
                        break;
                    case 13: // consult enemy
                        String enemy = reader.readUTF();
                        AtomicBoolean flag5 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.name.equals(enemy) && serverThread.id != this.id && flag5.get()) {
                                serverThread.writer.writeInt(13);
                                serverThread.writer.writeInt(this.id);
                                flag5.set(false);
                            }
                        }
                        if (flag5.get()) {
                            this.writer.writeInt(4);
                            this.writer.writeUTF("Usuario no encontrado");
                        }
                        break;
                    case 14: // traspasa poderes
                        int powersTo = reader.readInt();
                        int warriorId2 = reader.readInt();
                        String warriorType2 = reader.readUTF();
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.id == powersTo) {
                                serverThread.writer.writeInt(15);
                                serverThread.writer.writeInt(warriorId2);
                                serverThread.writer.writeUTF(warriorType2);
                            }
                        }
                        break;
                    case 15: // attack error
                        int errorTo2 = reader.readInt();
                        AtomicBoolean flag6 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.id == errorTo2 && serverThread.id != this.id && flag6.get()) {
                                serverThread.writer.writeInt(16);
                                flag6.set(false);
                            }
                        }
                        break;

                    case 16: // send nums
                        String numsTo = reader.readUTF();
                        int num1 = reader.readInt();
                        int num2 = reader.readInt();
                        int num3 = reader.readInt();
                        AtomicBoolean flag7 = new AtomicBoolean(true);
                        for (ServerThread serverThread : this.server.connections) {
                            if (serverThread.name.equals(numsTo) && serverThread.id != this.id && flag7.get()) {
                                serverThread.writer.writeInt(17);
                                serverThread.writer.writeInt(num1);
                                serverThread.writer.writeInt(num2);
                                serverThread.writer.writeInt(num3);
                                flag7.set(false);
                            }
                        }
                        break;


                }
            } catch (IOException ex) {
                this.running = false;
                this.server.connections.remove(this);
                this.server.removeTurn(this.id);
                this.server.printLogMessage(this.name + " se ha desconectado de la partida");
                System.out.println(this.name + " se ha desconectado de la partida");
            }
        }
    }

    private boolean checkWin() {
        return this.server.turns.size() == 1;
    }
}
