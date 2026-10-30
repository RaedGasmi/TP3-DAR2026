package ClientPackage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1234;

    static void main() {
        System.out.println("Je suis un client pas encore connecte");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(InetAddress.getByName(SERVER_IP), SERVER_PORT), 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Serveur : " + in.readLine());

            while (true) {
                System.out.println("Entrez une operation ou x pour quitter :");
                String line = clavier.readLine();
                if (line == null) break;
                line = line.replace(" ", "");
                if (line.equals("x")) {
                    out.println("x");
                    System.out.println("client deconnecte");
                    break;
                }

                int pos = -1;
                for (int k = 1; k < line.length(); k++) {
                    char c = line.charAt(k);
                    if (c == '+' || c == '-' || c == '*' || c == '/') { pos = k; break; }
                }
                if (pos == -1) {
                    out.println("syntaxe invalide");
                    String rep = in.readLine();
                    if (rep == null) {
                        System.out.println("serveur deconnecte");
                        break;
                    }
                    System.out.println(rep);
                    continue;
                }

                String a = line.substring(0, pos);
                String op = line.substring(pos, pos + 1);
                String b = line.substring(pos + 1);

                out.println(a);
                out.println(op);
                out.println(b);

                String resultat = in.readLine();
                if (resultat == null) { System.out.println("serveur deconnecte"); break; }
                if (resultat.equals("division par 0") || resultat.equals("operation invalide")) {
                    System.out.println(resultat);
                } else {
                    System.out.println("resultat : " + a + op + b + " = " + resultat);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        System.out.println("Socket se ferme");
    }
}