package org.telegram.messenger.support.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import org.telegram.messenger.FileLog;

@TargetApi(23)
public final class FingerprintManagerCompatApi23 {

    public static abstract class AuthenticationCallback {
        public void onAuthenticationError(int i, CharSequence charSequence) {
        }

        public void onAuthenticationFailed() {
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
        }

        public void onAuthenticationSucceeded(AuthenticationResultInternal authenticationResultInternal) {
        }
    }

    private static FingerprintManager getFingerprintManager(Context context) {
        return (FingerprintManager) context.getSystemService(FingerprintManager.class);
    }

    public static boolean hasEnrolledFingerprints(Context context) {
        try {
            return getFingerprintManager(context).hasEnrolledFingerprints();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean isHardwareDetected(Context context) {
        try {
            return getFingerprintManager(context).isHardwareDetected();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static void authenticate(Context context, CryptoObject cryptoObject, int i, Object obj, AuthenticationCallback authenticationCallback, Handler handler) {
        try {
            getFingerprintManager(context).authenticate(wrapCryptoObject(cryptoObject), (CancellationSignal) obj, i, wrapCallback(authenticationCallback), handler);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private static FingerprintManager.CryptoObject wrapCryptoObject(CryptoObject cryptoObject) {
        if (cryptoObject == null) {
            return null;
        }
        if (cryptoObject.getCipher() != null) {
            return new FingerprintManager.CryptoObject(cryptoObject.getCipher());
        }
        if (cryptoObject.getSignature() != null) {
            return new FingerprintManager.CryptoObject(cryptoObject.getSignature());
        }
        if (cryptoObject.getMac() != null) {
            return new FingerprintManager.CryptoObject(cryptoObject.getMac());
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static CryptoObject unwrapCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        if (cryptoObject == null) {
            return null;
        }
        if (cryptoObject.getCipher() != null) {
            return new CryptoObject(cryptoObject.getCipher());
        }
        if (cryptoObject.getSignature() != null) {
            return new CryptoObject(cryptoObject.getSignature());
        }
        if (cryptoObject.getMac() != null) {
            return new CryptoObject(cryptoObject.getMac());
        }
        return null;
    }

    private static FingerprintManager.AuthenticationCallback wrapCallback(final AuthenticationCallback authenticationCallback) {
        return new FingerprintManager.AuthenticationCallback() {
            public void onAuthenticationError(int i, CharSequence charSequence) {
                authenticationCallback.onAuthenticationError(i, charSequence);
            }

            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                authenticationCallback.onAuthenticationHelp(i, charSequence);
            }

            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                authenticationCallback.onAuthenticationSucceeded(new AuthenticationResultInternal(FingerprintManagerCompatApi23.unwrapCryptoObject(authenticationResult.getCryptoObject())));
            }

            public void onAuthenticationFailed() {
                authenticationCallback.onAuthenticationFailed();
            }
        };
    }

    public static class CryptoObject {
        private final Cipher mCipher;
        private final Mac mMac;
        private final Signature mSignature;

        public CryptoObject(Signature signature) {
            this.mSignature = signature;
            this.mCipher = null;
            this.mMac = null;
        }

        public CryptoObject(Cipher cipher) {
            this.mCipher = cipher;
            this.mSignature = null;
            this.mMac = null;
        }

        public CryptoObject(Mac mac) {
            this.mMac = mac;
            this.mCipher = null;
            this.mSignature = null;
        }

        public Signature getSignature() {
            return this.mSignature;
        }

        public Cipher getCipher() {
            return this.mCipher;
        }

        public Mac getMac() {
            return this.mMac;
        }
    }

    public static final class AuthenticationResultInternal {
        private CryptoObject mCryptoObject;

        public AuthenticationResultInternal(CryptoObject cryptoObject) {
            this.mCryptoObject = cryptoObject;
        }

        public CryptoObject getCryptoObject() {
            return this.mCryptoObject;
        }
    }
}
