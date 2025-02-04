package practica;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Clase que analiza un documento HTML
public class AnalizadorHTML {

    private Document documento;

    // Contenido HTML del documento
    public AnalizadorHTML(String contenidoHtml) {
        this.documento = Jsoup.parse(contenidoHtml);

    }

    // 1. Indicar la cantidad de lines de texto que tiene el documento HTML
    public int contar_Lineas() {
        return documento.html().split("\n").length;
    }

    // 2. Indicar la cantidad de párrafos que tiene el documento HTML
    public int contar_Parrafos() {
        Elements parrafos = documento.select("p");
        return parrafos.size();
    }

    // 3. Indicar la cantidad de imágenes que tiene el documento HTML
    public int contar_Imagenes() {
        Elements imagenes = documento.select("img");
        return imagenes.size();
    }

    // 4. Indicar la cantidad de formularios con metodo GET o POST que tiene el
    // documento HTML
    public int contar_Formularios_Metodo(String metodo) {

        int contador = 0;
        Elements formularios = documento.select("form");
        for (Element formulario : formularios) {
            if (formulario.attr("method").equalsIgnoreCase(metodo)) {
                contador++;
            }
        }
        return contador;

    }

    // 5. Obtener los formularios del documento HTML
    public Elements obtener_Formularios() {
        return documento.select("form");
    }

    // 6. Para cada formulario mostrar los campos de tipo input y su respectivo tipo
    // que contiene en el documento HTML
    public Elements obtener_Campos_Input(Element formulario) {
        return formulario.select("input");
    }
}
