package ServerPackage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final AtomicInteger compteur = new AtomicInteger(0);

    static void main() {
        try (ServerSocket server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(1234));
            System.out.println("Je suis un serveur en attente de connexions");
            while (true) {
                Socket client = server.accept();
                int numero = compteur.incrementAndGet();
                System.out.println("Client n°" + numero + " connecte : " + client.getRemoteSocketAddress());
                new Thread(new ClientHandler(client, numero)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int numero;

        public ClientHandler(Socket socket, int numero) {
            this.clientSocket = socket;
            this.numero = numero;
        }

        @Override
        public void run() {
            try (Socket s = clientSocket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

                out.println("Bienvenue, vous etes le client n°" + numero);

                String a;
                while ((a = in.readLine()) != null) {
                    if (a.equals("x")) break;

                    if (a.equals("syntaxe invalide")) {
                        System.out.println("Client n°" + numero + " -> syntaxe invalide");
                        out.println("syntaxe invalide");
                        continue;
                    }

                    String op = in.readLine();
                    String b = in.readLine();
                    if (op == null || b == null) {
                        System.out.println("Client n°" + numero + " : operation invalide");
                        break;
                    }

                    String resultat = calculer(a, op, b);
                    if (resultat.equals("division par 0") || resultat.equals("operation invalide")) {
                        System.out.println("Client n°" + numero + " -> " + resultat);
                    } else {
                        System.out.println("Client n°" + numero + " -> " + a + op + b + " = " + resultat);
                    }
                    out.println(resultat);
                }
            } catch (IOException e) {
                System.out.println("Client n°" + numero + " : connexion perdue (" + e.getMessage() + ")");
            }
            System.out.println("Client n°" + numero + " deconnecte");
        }
    }

    static String calculer(String a, String op, String b) {
        double i, j;
        try {
            i = Double.parseDouble(a);
            j = Double.parseDouble(b);
        } catch (NumberFormatException e) {
            return "operation invalide";
        }
        double r;
        switch (op) {
            case "+": r = i + j; break;
            case "-": r = i - j; break;
            case "*": r = i * j; break;
            case "/":
                if (j == 0) return "division par 0";
                r = i / j;
                break;
            default:
                return "operation invalide";
        }
        return String.valueOf(r);
    }
}