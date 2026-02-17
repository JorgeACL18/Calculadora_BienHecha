package Funciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    private Socket socket;
    private double memoriaSesion = 0;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String ipCliente = socket.getInetAddress().getHostAddress();

        try (
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String mensaje;
            while ((mensaje = lector.readLine()) != null) {
                if ("adios".equalsIgnoreCase(mensaje)) {
                    System.out.println("LOG [" + ipCliente + "]: Se ha desconectado.");
                    break;
                }
                String resultado = resolviendoOP.resOp(mensaje, memoriaSesion);
                System.out.println("LOG [" + ipCliente + "]: Op: '" + mensaje + "' -> Res: " + resultado);

                try {
                    if (!resultado.startsWith("ERROR")) {
                        memoriaSesion = Double.parseDouble(resultado);
                    }
                } catch (NumberFormatException e) {
                }

                escritor.println(resultado);
            }
        } catch (IOException e) {
            System.out.println("LOG [" + ipCliente + "]: Desconexión abrupta.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}