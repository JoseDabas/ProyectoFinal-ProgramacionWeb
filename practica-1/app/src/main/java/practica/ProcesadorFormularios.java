package practica;

import java.net.http.HttpResponse;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProcesadorFormularios {

    private SolicitadorHTTP solicitadorHTTP;
    private String matricula = "1014-4270";

    /**
     * Constructor de la clase ProcesadorFormularios
     * 
     * @param solicitadorHTTP Utilizado para enviar solicitudes HTTP
     * @param matricula       El ID de la matricula que en este caso es 1014-4270
     *                        que se envia en el header de la solicitud POST
     */
    public ProcesadorFormularios(SolicitadorHTTP solicitadorHTTP, String matricula) {
        this.solicitadorHTTP = solicitadorHTTP;
        this.matricula = matricula;
    }

    /**
     * Procesa un formulario HTML y envia una solicitud HTTP POST si el metodo del
     * formulario es POST
     * 
     * @param formulario El elemento del formulario HTML a procesar
     * @param urlBase    La URL base utilizada para construir la URL de la accion
     *                   del formulario
     */
    public void procesarFormulario(Element formulario, String urlBase) {
        String metodo = formulario.attr("method").toUpperCase();
        String accion = formulario.attr("action");

        // Construir la URL de la accion del formulario
        if (!accion.startsWith("http")) {
            accion = urlBase + accion;
        }

        if (metodo.equals("POST")) {
            System.out.println("Procesando formulario con metodo POST y accion " + accion);

            try {
                String parametros = "asignatura=practica1";
                Elements inputs = formulario.select("input");
                for (Element input : inputs) {
                    String nombre = input.attr("name");
                    String valor = input.attr("value");
                    if (!nombre.isEmpty() && !nombre.equals("asignatura")) {
                        parametros += "&" + nombre + "=" + valor;
                    }
                }

                HttpResponse<String> respuesta = solicitadorHTTP.enviarSolicitudPost(
                        accion,
                        parametros,
                        "matricula-id",
                        matricula);

                System.out.println("Peticion POST enviada correctamente.");
                System.out.println("Respuesta del servidor: " + respuesta.body());
            } catch (Exception e) {
                System.out.println("Error al enviar la peticion POST: " + e.getMessage());
            }
        }
    }
}