// add package

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import javax.swing.JFileChooser;

public class Decrypt {

    // Define la clave secreta fija (ideal)
    private static final String secretKey = "es";

    public static void main(String[] args) {
        try {
            // Permitir al usuario seleccionar el archivo cifrado
            byte[] iv = new byte[12];
            JFileChooser jfc = new JFileChooser());
            jfc.showOpenDialog(null);
            File fDir = jfc.getCurrentDirectory();
            for (File files : fDir.listFiles()) {
                if (!files.isDirectory() && files.getName().contains(".DS_")) // para los casos de MacOS
                {
                    files.delete();
                }
                if (!files.isDirectory()) { // Files.isRegularFile(Paths.get(files.getAbsolutePath()))
                    String filePath = files.getAbsolutePath();

                    System.out.println("\nLeyendo IV del fichero encriptado: " + filePath);
                    // Lee el IV del archivo cifrado
                    byte[] cipherFile = (Files.readAllBytes(Paths.get(filePath)));
                    System.arraycopy(cipherFile, cipherFile.length - 12, iv, 0, iv.length);

                    // Convierte la clave en un arreglo de bytes
                    System.out.println("Obteniendo llave...");
                    byte[] keyBytes = secretKey.getBytes();
                    SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

                    // Inicializa el cifrador en modo GCM
                    System.out.println("Iniciando proceso de descifrado...");
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

                    // Lee el archivo cifrado y obtiene IV
                    System.out.println("Extrayendo datos cifrados...");
                    byte[] encryptedData = Files.readAllBytes(Paths.get(filePath));
                    byte[] encryptedDataWithoutIV = new byte[encryptedData.length - iv.length];
                    System.arraycopy(encryptedData, 0, encryptedDataWithoutIV, 0, encryptedDataWithoutIV.length);

                    // Descifra el archivo y lo almacena en un arreglo
                    System.out.println("Descifrando datos...");
                    byte[] decryptedData = cipher.doFinal(encryptedDataWithoutIV);

                    // Obtiene la extensión del archivo cifrado
                    String extension;
                    String decryptedFileName;
                    int dotIndex = filePath.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        extension = filePath.substring(dotIndex);
                        decryptedFileName = filePath.substring(0, dotIndex) + extension;
                    } else {
                        extension = "._decrypted";
                        decryptedFileName = filePath.concat(extension);
                    }
                    // Guarda el archivo descifrado
                    Files.write(Paths.get(decryptedFileName), decryptedData);

                    System.out.println("Archivo " + files.getName() + " descifrado con éxito.");
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException
                | IOException e) {
            System.err.println("Proceso inconcluso debido a: " + e.getMessage()+"\nDetalles: ");
            e.printStackTrace();
        }
    }
}
