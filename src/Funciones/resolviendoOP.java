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