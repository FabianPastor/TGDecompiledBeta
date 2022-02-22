package org.telegram.messenger;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import javax.crypto.Cipher;

public class FingerprintController {
    private static final String KEY_ALIAS = "tmessages_passcode";
    private static Boolean hasChangedFingerprints;
    private static KeyPairGenerator keyPairGenerator;
    private static KeyStore keyStore;

    private static KeyStore getKeyStore() {
        KeyStore keyStore2 = keyStore;
        if (keyStore2 != null) {
            return keyStore2;
        }
        try {
            KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
            keyStore = instance;
            instance.load((KeyStore.LoadStoreParameter) null);
            return keyStore;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        KeyPairGenerator keyPairGenerator2 = keyPairGenerator;
        if (keyPairGenerator2 != null) {
            return keyPairGenerator2;
        }
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            keyPairGenerator = instance;
            return instance;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    private static boolean generateNewKey() {
        KeyPairGenerator keyPairGenerator2 = getKeyPairGenerator();
        if (keyPairGenerator2 != null) {
            try {
                keyPairGenerator2.initialize(new KeyGenParameterSpec.Builder("tmessages_passcode", 3).setDigests(new String[]{"SHA-256", "SHA-512"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setUserAuthenticationRequired(true).build());
                keyPairGenerator2.generateKeyPair();
                return true;
            } catch (InvalidAlgorithmParameterException e) {
                FileLog.e((Throwable) e);
            }
        }
        return false;
    }

    public static void deleteInvalidKey() {
        try {
            getKeyStore().deleteEntry("tmessages_passcode");
        } catch (KeyStoreException e) {
            FileLog.e((Throwable) e);
        }
        hasChangedFingerprints = null;
    }

    public static boolean isKeyReady() {
        try {
            return getKeyStore().containsAlias("tmessages_passcode") || generateNewKey();
        } catch (KeyStoreException e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean checkDeviceFingerprintsChanged() {
        Boolean bool = hasChangedFingerprints;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").init(2, keyStore.getKey("tmessages_passcode", (char[]) null));
            hasChangedFingerprints = Boolean.FALSE;
            return false;
        } catch (KeyPermanentlyInvalidatedException unused) {
            hasChangedFingerprints = Boolean.TRUE;
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            hasChangedFingerprints = Boolean.FALSE;
            return false;
        }
    }
}
