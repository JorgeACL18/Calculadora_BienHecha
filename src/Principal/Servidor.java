package Principal;

import Funciones.*;

import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        int puerto = 8080;
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor Concurrente iniciado en puerto " + puerto);

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Nuevo ingeniero conectado: " + socketCliente.getInetAddress());

                new ManejadorCliente(socketCliente).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}