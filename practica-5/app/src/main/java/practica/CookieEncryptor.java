package practica;

import org.jasypt.util.text.BasicTextEncryptor;

public class CookieEncryptor {

    private static final String SECRET_KEY = "1009"; // Clave secreta para encriptar/desencriptar

    private static BasicTextEncryptor textEncryptor;

    static {
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(SECRET_KEY);
    }

    public static String encrypt(String data) {
        return textEncryptor.encrypt(data);
    }

    public static String decrypt(String encryptedData) {
        return textEncryptor.decrypt(encryptedData);
    }

}
