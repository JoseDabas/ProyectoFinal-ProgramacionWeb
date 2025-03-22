package practica.Controladores;

import io.javalin.http.Context;
import practica.Servicios.UsuarioServices;
import practica.CookieEncryptor;
import jakarta.servlet.http.Cookie;
import practica.RegistrarLogin;
//import jakarta.servlet.http.HttpServletResponse;

public class AuthControlador {

    public static void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        boolean rememberMe = "on".equals(ctx.formParam("rememberMe"));

        if (UsuarioServices.getInstance().autenticar(username, password)) {
            ctx.sessionAttribute("user", username);

            RegistrarLogin.GuardarLogin(username);

            if (rememberMe) {
                String encryptedUsername = CookieEncryptor.encrypt(username);
                Cookie rememberCookie = new Cookie("rememberMe", encryptedUsername);
                rememberCookie.setMaxAge(7 * 24 * 60 * 60); // 1 semana
                rememberCookie.setHttpOnly(true);
                rememberCookie.setPath("/");
                ctx.res().addCookie(rememberCookie);
            }
            ctx.redirect("/");
        } else {
            ctx.redirect("/login");
        }
    }

    public static void logout(Context ctx) {
        ctx.sessionAttribute("user", null);

        Cookie cookie = new Cookie("rememberMe", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        ctx.res().addCookie(cookie);

        ctx.redirect("/");
    }

    public static void checkRememberMe(Context ctx) {
        if (ctx.sessionAttribute("user") == null) {
            Cookie[] cookies = ctx.req().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("rememberMe".equals(cookie.getName())) {
                        String decryptedUser = CookieEncryptor.decrypt(cookie.getValue());
                        ctx.sessionAttribute("user", decryptedUser);
                        break;
                    }
                }
            }
        }
    }

}
