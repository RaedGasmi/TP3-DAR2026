package ClientPackage;

import OperationSerializable.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1234;

    static void main() {
        try {
            Socket socket = new Socket();
            System.out.println("Je suis un client pas encore connecte");

            socket.connect(new InetSocketAddress(InetAddress.getByName(SERVER_IP), SERVER_PORT), 5000);
            System.out.println("je suis un client connecte");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);

            try {
                System.out.println("Serveur : " + in.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            while (true) {
                System.out.print("premier operande (ou x pour quitter) : ");
                if (!sc.hasNextDouble()) {
                    String s = sc.next();
                    if (s.equals("x")) break;
                    System.out.println("n'est pas un nombre");
                    continue;
                }
                double a = sc.nextDouble();

                System.out.println("operateur (+,-,*,/):");
                String opv = sc.next();
                if (opv.length() != 1 || "+-*/".indexOf(opv.charAt(0)) < 0) {
                    System.out.println("operateur invalide");
                    continue;
                }
                char op = opv.charAt(0);

                System.out.println("deuxieme operande :");
                if (!sc.hasNextDouble()) {
                    System.out.println("n'est pas un nombre");
                    sc.next();
                    continue;
                }
                double b = sc.nextDouble();

                out.writeObject(new Operation(a, op, b));
                out.flush();

                try {
                    Object reponse = in.readObject();
                    if (reponse instanceof Double) {
                        System.out.println("resultat: " + a + op + b + "=" + reponse);
                    } else {
                        System.out.println("erreur serveur: " + reponse);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            out.writeObject(null);
            out.flush();

            sc.close();
            in.close();
            out.close();
            socket.close();
            System.out.println("Socket se ferme");
        } catch (IOException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}