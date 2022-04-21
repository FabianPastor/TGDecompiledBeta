package org.telegram.messenger;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;

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

    /* access modifiers changed from: private */
    public static void generateNewKey(boolean notifyCheckFingerprint) {
        KeyPairGenerator generator = getKeyPairGenerator();
        if (generator != null) {
            try {
                Locale realLocale = Locale.getDefault();
                setLocale(Locale.ENGLISH);
                generator.initialize(new KeyGenParameterSpec.Builder("tmessages_passcode", 3).setDigests(new String[]{"SHA-256", "SHA-512"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setUserAuthenticationRequired(true).build());
                generator.generateKeyPair();
                setLocale(realLocale);
                AndroidUtilities.runOnUIThread(new FingerprintController$$ExternalSyntheticLambda1(notifyCheckFingerprint));
            } catch (InvalidAlgorithmParameterException e) {
                FileLog.e((Throwable) e);
            } catch (Exception e2) {
                if (!e2.getClass().getName().equals("android.security.KeyStoreException")) {
                    FileLog.e((Throwable) e2);
                }
            }
        }
    }

    public static void deleteInvalidKey() {
        try {
            getKeyStore().deleteEntry("tmessages_passcode");
        } catch (KeyStoreException e) {
            FileLog.e((Throwable) e);
        }
        hasChangedFingerprints = null;
        checkKeyReady(false);
    }

    public static void checkKeyReady() {
        checkKeyReady(true);
    }

    public static void checkKeyReady(boolean notifyCheckFingerprint) {
        if (!isKeyReady() && AndroidUtilities.isKeyguardSecure() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).hasEnrolledFingerprints()) {
            Utilities.globalQueue.postRunnable(new FingerprintController$$ExternalSyntheticLambda0(notifyCheckFingerprint));
        }
    }

    public static boolean isKeyReady() {
        try {
            return getKeyStore().containsAlias("tmessages_passcode");
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
            Boolean bool2 = false;
            hasChangedFingerprints = bool2;
            return bool2.booleanValue();
        } catch (KeyPermanentlyInvalidatedException e) {
            Boolean bool3 = true;
            hasChangedFingerprints = bool3;
            return bool3.booleanValue();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            Boolean bool4 = false;
            hasChangedFingerprints = bool4;
            return bool4.booleanValue();
        }
    }

    private static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources resources = ApplicationLoader.applicationContext.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
