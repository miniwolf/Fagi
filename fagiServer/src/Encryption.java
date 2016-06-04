import com.fagi.encryption.KeyStorage;
import com.fagi.encryption.RSA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Marcus on 04-06-2016.
 */
public class Encryption {
    private static Encryption instance;

    private RSA rsa;

    private Encryption() {
        File f = new File(KeyStorage.PUBLICKEYFOLDER);
        if (!f.exists()) {
            this.rsa = new RSA();
            KeyPair key = (KeyPair) rsa.getKey().getKey();
            try {
                KeyStorage.SaveKeyPair(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                KeyPair key = KeyStorage.LoadKeyPair("RSA");
                this.rsa = new RSA(key);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }

    }

    public static Encryption getInstance() {
        if (instance == null) {
            instance = new Encryption();
        }
        return instance;
    }

    public RSA getRSA() {
        return rsa;
    }
}
