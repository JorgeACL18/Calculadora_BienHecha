# Calculadora
Para este trabajo, tuvimos que hacer un programa servidor-cliente, el cual es concurrente, que simule la funcionalidad de una calculadora.

## Paquete Principal:
### Servidor:
~~~ java
package Principal;

import Funciones.*;

import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        int puerto = 8080;
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto);

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Nuevo cliente conectado: " + socketCliente.getInetAddress());

                new ManejadorCliente(socketCliente).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
~~~~


En este pequeño bloque de código tiene la funcionalidad de, como puede indicar su nombre, el servidor y de manejador de clientes (esto lo veremos más adelante).

### Cliente:

~~~ java
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
~~~~

Esta es la parte del código con la que interactuamos directamente ya que, después de iniciar el código del servidor, podremos iniciar este otro para utilizar la calculadora. Además, aquí podemos ver el bucle el cual contiene las funciones de "ejemplos" y "adiós".

---

## Paquete Funciones:
### ManejadorCliente

~~~ java
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
~~~

Este código es utilizado directamente por el servidor. 

Se encarga de controlar los clientes que se conecten al servidor y de los mensajes (operaciones) que manda al mismo.

### resolviendoOP
~~~ java
package Funciones;

public class resolviendoOP {
    public static String resOp(String ent, double ultimoResultado) {
        String[] partes = ent.trim().split("\\s+");
        if (partes.length < 1) return "ERROR: Formato vacío";
        String op = partes[0].toLowerCase();

        try {
            switch (op) {
                case "last":
                    return String.valueOf(ultimoResultado);

                case "acumular":
                    if (partes.length < 2) return "ERROR: Uso -> acumular [n1] [n2]...";

                    double sumaAcumulada = ultimoResultado;

                    for (int i = 1; i < partes.length; i++) {
                        sumaAcumulada += obtenerValor(partes[i], ultimoResultado);
                    }
                    return String.valueOf(sumaAcumulada);


                case "suma":
                    if (partes.length < 3) return "ERROR: Uso -> suma [n1] [n2]...";

                    double resSum = obtenerValor(partes[1], ultimoResultado);
                    for (int i = 2; i < partes.length; i++) {
                        resSum += obtenerValor(partes[i], ultimoResultado);
                    }
                    return String.valueOf(resSum);

                case "resta":

                    if (partes.length < 3) return "ERROR: Uso -> resta [n1] [n2]...";

                    double resRes = obtenerValor(partes[1], ultimoResultado);
                    for (int i = 2; i < partes.length; i++) {
                        resRes -= obtenerValor(partes[i], ultimoResultado);
                    }
                    return String.valueOf(resRes);

                case "mult":
                    if (partes.length < 3) return "ERROR: Uso -> mult [n1] [n2]...";

                    double resMul = obtenerValor(partes[1], ultimoResultado);
                    for (int i = 2; i < partes.length; i++) {
                        resMul *= obtenerValor(partes[i], ultimoResultado);
                    }
                    return String.valueOf(resMul);

                case "div":
                    if (partes.length < 3) return "ERROR: Uso -> div [n1] [n2]...";

                    double resDiv = obtenerValor(partes[1], ultimoResultado);
                    for (int i = 2; i < partes.length; i++) {
                        double divisor = obtenerValor(partes[i], ultimoResultado);
                        if (divisor == 0) return "Borra cuenta";
                        resDiv /= divisor;
                    }
                    return String.valueOf(resDiv);

                default:
                    return "ERROR: Operación desconocida ('" + op + "'). Usa 'ejemplos' en el cliente.";
            }
        } catch (NumberFormatException e) {
            return "ERROR: Uno de los argumentos no es un número válido.";
        } catch (Exception e) {
            return "ERROR: Fallo inesperado procesando la operación.";
        }
    }

    private static double obtenerValor(String token, double ultimoResultado) throws NumberFormatException {
        if (token.equalsIgnoreCase("last")) {
            return ultimoResultado;
        }
        return Double.parseDouble(token);
    }
}
~~~

Este es el código utilizado por el cliente. Aquí se encuentra las distintas operaciones que se pueden hacer en el programa.

¡NOTA! la línea `String[] partes = ent.trim().split("\\s+");` es lo que permite que el programa no se rompa si el usuario pone un espacio demás en las operaciones.
