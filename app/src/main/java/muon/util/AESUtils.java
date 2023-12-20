package muon.util;

import muon.dto.session.SavedSessionTree;
import muon.exceptions.AuthenticationException;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;

public class AESUtils {
    private static final int IV_LEN = 16;
    private static final int SALT_LEN = 16;

    public static void encrypt(String password, SavedSessionTree sessionTree) throws Exception {
        var file = new File(System.getProperty("user.home"), "session-store.dat");
        encrypt(file, password, sessionTree);
    }

    public static void encrypt(File file, String password, SavedSessionTree sessionTree) throws Exception {
        var algorithm = "AES/CBC/PKCS5Padding";
        var iv = generateIv();
        var salt = generateSalt();
        var key = getKeyFromPassword(password, salt);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        var tempFile = new File(file.getParent(), file.getName() + ".bak");
        try (var out = new FileOutputStream(tempFile);
             var oout = new ObjectOutputStream(
                     new CipherOutputStream(
                             out, cipher))) {
            out.write(StringUtils.isEmpty(password) ? 0 : 1);
            out.write(iv.getIV());
            out.write(salt);
            oout.writeObject(sessionTree);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);
    }

    public static SavedSessionTree decrypt(String password) throws Exception {
        var tempFile = new File(System.getProperty("user.home"), "session-store.dat");
        return decrypt(tempFile, password);
    }


    public static SavedSessionTree decrypt(File file, String password) throws Exception {
        try (var fin = new FileInputStream(file)) {
            if (fin.read() == 1 && StringUtils.isEmpty(password)) {
                throw new AuthenticationException("Password required");
            }

            var algorithm = "AES/CBC/PKCS5Padding";
            var iv = new IvParameterSpec(fin.readNBytes(IV_LEN));
            var salt = fin.readNBytes(SALT_LEN);
            var key = getKeyFromPassword(password, salt);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            try (var oin = new ObjectInputStream(
                    new CipherInputStream(
                            fin, cipher))) {
                return (SavedSessionTree) oin.readObject();
            } catch (StreamCorruptedException io) {
                io.printStackTrace();
                throw new AuthenticationException("Invalid password");
            }
        }
    }

    public static SecretKey getKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_LEN];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
