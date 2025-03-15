package parcial.Controllers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import parcial.Class.Registro;
import parcial.Class.Respuesta;
import parcial.Class.Usuario;
import parcial.Service.RegistroServices;
import parcial.Service.RespuestaServices;
import parcial.Service.UserServices;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EncuestaController extends BaseController {

    public EncuestaController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.before("/encuesta", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/");
            }
        });

        app.get("/encuesta", ctx -> {
            ctx.render("public/templates/index.html");
        });

        app.before("/encuesta/stored", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/");
            }
        });

        app.get("/encuesta/stored", ctx -> {
            ctx.render("public/templates/ListarStoredRegistrar.html");
        });

        app.ws("/encuesta/sincronizar", ws -> {
            ws.onConnect(session -> {
                System.out.println("Conectado");
            });
            ws.onMessage(ctx -> {
                String message = ctx.message();
                System.out.println("Received: " + message);

                // Parsear el mensaje JSON a un JsonObject
                JsonElement jelement = JsonParser.parseString(message);
                if (jelement.isJsonArray()) {
                    JsonArray jsonArray = jelement.getAsJsonArray();

                    for (JsonElement element : jsonArray) {
                        JsonObject jobject = element.getAsJsonObject();
                        // Obtener cada campo individualmente
                        String nombre = jobject.get("nombre").getAsString();
                        String sector = jobject.get("sector").getAsString();
                        String nivelEscolar = jobject.get("nivelEscolar").getAsString();
                        String usuario = jobject.get("usuario").getAsString();
                        double latitud = jobject.get("latitud").getAsDouble();
                        double longitud = jobject.get("longitud").getAsDouble();

                        Usuario user = UserServices.getInstancia().find(usuario);
                        Respuesta respuesta = new Respuesta(nombre, sector, nivelEscolar, user,
                                latitud, longitud);
                        Registro registro = new Registro(respuesta, user);

                        try {
                            RespuestaServices.getInstancia().insert(respuesta);
                            RegistroServices.getInstancia().insert(registro);
                            System.out.println("Registro almacenado en la base de datos");
                        } catch (Exception e) {
                            System.out.println("Error al procesar el registro: " + e.getMessage());
                        }
                    }
                }
            });

            ws.onClose(ctx -> {
                System.out.println("Desconectado");
            });

            ws.onBinaryMessage(ctx -> {
                System.out.println("Jorgen von Strangle");
            });

            ws.onError(ctx -> {
                System.out.println("Error en WS");
            });
        });
    }

}
