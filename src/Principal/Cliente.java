package Principal;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 8080;
        System.out.println("Intentando conectar con el servidor...");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), 5000);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 Scanner sc = new Scanner(System.in)) {

                System.out.println("¡Conexión establecida!");

                while (true) {
                    System.out.print("\nIntroduce operación (o 'ejemplos'): ");
                    String input = sc.nextLine().trim();

                    if (input.isEmpty()){
                        continue;
                    }

                    if (input.equalsIgnoreCase("ejemplos")) {
                        System.out.println("--- Ejemplos ---");
                        System.out.println("Suma: suma [n1] [n2]...");
                        System.out.println("Resta: resta [n1] [n2]...");
                        System.out.println("Multiplicación: mult [n1] [n2]...");
                        System.out.println("División: div [n1] [n2]...");
                        System.out.println("Acumular resultado: acumular [n1] [n2]...");
                        System.out.println("Utilizar resultado: last");
                        continue;
                    }

                    if (input.equalsIgnoreCase("adios")) {
                        out.println(input);
                        System.out.println("Cerrando cliente...");
                        break;
                    }

                    out.println(input);

                    String respuesta = in.readLine();
                    if (respuesta != null) {
                        System.out.println("SERVIDOR: " + respuesta);
                    } else {
                        System.out.println("El servidor ha finalizado la conexión.");
                        break;
                    }
                }
            }

        } catch (ConnectException e) {
            System.out.println("Creo que debías iniciar el servidor primero....");
        } catch (SocketTimeoutException e) {
            System.err.println("Mucho tiempo esperandooo!!!");
        } catch (IOException e) {
            System.err.println("ERROR de E/S: " + e.getMessage());
        }
    }
}