package practica;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SolicitadorHTTP {

    /**
     * Envia una solicitud HTTP GET a la URL especificada
     * 
     * @param url La URL a la que se enviara la solicitud GET
     * @return La respuesta HTTP como un objeto HTTPResponse<String>
     * @throws Exception Si ocurre un error al enviar la solicitud
     */

    public HttpResponse<String> enviarSolicitudGet(String url) throws Exception {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Envia una solicitud HTTP POST a la URL especificada con el cuerpo y el header
     * especificado
     * 
     * @param url          La URL a la que se enviara la solicitud POST
     * @param cuerpo       El cuerpo de la solicitud POST en formato
     *                     x-www-form-urlencoded
     * @param nombreHeader El nombre del header que se enviara con la solicitud POST
     * @param valorHeader  El valor del header que se enviara con la solicitud POST
     * @return La respuesta HTTP como un objeto HTTPResponse<String>
     * @throws Exception Si ocurre un error al enviar la solicitud
     */

    public HttpResponse<String> enviarSolicitudPost(String url, String cuerpo, String nombreHeader, String valorHeader)
            throws Exception {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header(nombreHeader, valorHeader)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();

        return cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }
}
