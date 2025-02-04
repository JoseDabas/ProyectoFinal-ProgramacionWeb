package practica;

import java.net.http.HttpResponse;
import java.util.Scanner;
import org.jsoup.nodes.Element;

public class Main {

    public static void main(String[] args) {
        // Leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        // Procesar múltiples URLs hasta que el usuario decida salir
        while (continuar) {
            try {
                System.out.print("Introduce una URL válida: ");
                if (!scanner.hasNextLine()) {
                    System.out.println("No se encontró una línea de entrada.");
                    break;
                }
                String url = scanner.nextLine();

                if (url.equalsIgnoreCase("salir")) {
                    continuar = false;
                    break;
                }

                // Verificar si la URL es válida
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    System.out.println("La URL no es válida.");
                    continue;
                }

                SolicitadorHTTP solicitadorHttp = new SolicitadorHTTP();

                // Imprimir la URL a la que se enviará la solicitud GET
                System.out.println("Enviando solicitud GET a: " + url);

                HttpResponse<String> respuesta = solicitadorHttp.enviarSolicitudGet(url);

                // Imprimir la respuesta de la solicitud GET
                System.out.println("Respuesta de la solicitud GET: " + respuesta.body());

                String tipoContenido = respuesta.headers().firstValue("Content-Type").orElse("desconocido");
                System.out.println("Tipo de recurso: " + tipoContenido);

                if (tipoContenido.contains("text/html")) {
                    AnalizadorHTML analizadorHtml = new AnalizadorHTML(respuesta.body());

                    // Imprimir la cantidad de líneas, párrafos e imágenes
                    System.out.println("Cantidad de líneas: " + analizadorHtml.contar_Lineas());
                    System.out.println("Cantidad de párrafos: " + analizadorHtml.contar_Parrafos());
                    System.out.println("Cantidad de imágenes: " + analizadorHtml.contar_Imagenes());
                    System.out.println(
                            "Cantidad de formularios POST: " + analizadorHtml.contar_Formularios_Metodo("post"));
                    System.out
                            .println("Cantidad de formularios GET: " + analizadorHtml.contar_Formularios_Metodo("get"));

                    ProcesadorFormularios procesadorFormularios = new ProcesadorFormularios(solicitadorHttp,
                            "1014-4270");

                    for (Element formulario : analizadorHtml.obtener_Formularios()) {
                        System.out.println("Formulario encontrado:");
                        for (Element input : analizadorHtml.obtener_Campos_Input(formulario)) {
                            System.out
                                    .println("  Campo input: " + input.attr("name") + " - Tipo: " + input.attr("type"));
                        }
                        procesadorFormularios.procesarFormulario(formulario, url);
                        System.out
                                .println("Formulario procesado con método " + formulario.attr("method").toUpperCase());
                    }

                } else {
                    System.out.println("El recurso no es un documento HTML.");
                }

                System.out.print("¿Deseas consultar otra URL? (si/no): ");
                if (!scanner.hasNextLine()) {
                    System.out.println("No se encontró una línea de entrada.");
                    break;
                }
                String opcion = scanner.nextLine();
                continuar = opcion.equalsIgnoreCase("si");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.print("¿Deseas intentar con otra URL? (si/no): ");
                if (!scanner.hasNextLine()) {
                    System.out.println("No se encontró una línea de entrada.");
                    break;
                }
                String opcion = scanner.nextLine();
                continuar = opcion.equalsIgnoreCase("si");
            }
        }

        System.out.println("Fin.");
        scanner.close();
    }

}
