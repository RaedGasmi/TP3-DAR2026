package ServerPackage;

import OperationSerializable.Operation;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final AtomicInteger compteurClients = new AtomicInteger(0);

    private static int compteurOperations = 0;

    private static synchronized void incrementerCompteur() {
        compteurOperations++;
        System.out.println("Compteur global d'operations = " + compteurOperations);
    }

    static void main() {
        try (ServerSocket server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(1234));
            System.out.println("Je suis un serveur en attente de connexions");
            while (true) {
                Socket client = server.accept();
                int numero = compteurClients.incrementAndGet();
                System.out.println("Client n°" + numero + " connecte : " + client.getRemoteSocketAddress());
                new Thread(new ClientProcess(client, numero)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientProcess implements Runnable {
        private final Socket clientSocket;
        private final int numero;

        public ClientProcess(Socket socket, int numero) {
            this.clientSocket = socket;
            this.numero = numero;
        }

        @Override
        public void run() {
            try (Socket s = clientSocket;
                 ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
                out.flush();
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());

                out.writeObject("Bienvenue, vous etes le client n°" + numero);
                out.flush();

                while (true) {
                    Operation operation = (Operation) in.readObject();
                    if (operation == null) {
                        System.out.println("Client n°" + numero + " : fin de la communication");
                        break;
                    }

                    double a = operation.getO1();
                    char op = operation.getOp();
                    double b = operation.getO2();

                    Object reponse;
                    switch (op) {
                        case '+': reponse = a + b; break;
                        case '-': reponse = a - b; break;
                        case '*': reponse = a * b; break;
                        case '/':
                            if (b == 0) reponse = "division par 0";
                            else reponse = a / b;
                            break;
                        default: reponse = "operateur invalide :" + op;
                    }

                    System.out.println("Client n°" + numero + " : " + a + op + b + " => reponse : " + reponse);
                    incrementerCompteur();

                    out.writeObject(reponse);
                    out.flush();
                }
            } catch (EOFException e) {
                System.out.println("Client n°" + numero + " deconnecte");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client n°" + numero + " : erreur (" + e.getMessage() + ")");
            }
            System.out.println("Client n°" + numero + " : socket ferme");
        }
    }
}