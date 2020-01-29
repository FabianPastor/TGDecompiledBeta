package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.core.os.CancellationSignal;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

@TargetApi(23)
public class TestActivity extends BaseFragment {
    private static final String KEY_NAME = "wallet_key11";
    private Cipher cipher;
    /* access modifiers changed from: private */
    public EditTextBoldCursor codeField;
    /* access modifiers changed from: private */
    public String encryptedString;
    private FingerprintHelper fingerprintHelper;
    private KeyPairGenerator keyPairGenerator;
    private KeyStore keyStore;

    public boolean onFragmentCreate() {
        try {
            this.keyStore = KeyStore.getInstance("AndroidKeyStore");
            this.keyStore.load((KeyStore.LoadStoreParameter) null);
            this.keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            this.cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.encryptedString != null) {
            MessagesController.getMainSettings(UserConfig.selectedAccount).edit().remove("test_enc").commit();
        }
    }

    public boolean createKeyPair() {
        try {
            KeyGenParameterSpec.Builder keySize = new KeyGenParameterSpec.Builder("wallet_key11", 3).setDigests(new String[]{"SHA-1", "SHA-256"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setKeySize(2048);
            keySize.setIsStrongBoxBacked(true);
            keySize.setInvalidatedByBiometricEnrollment(true);
            keySize.setUserAuthenticationRequired(true);
            this.keyPairGenerator.initialize(keySize.build());
            this.keyPairGenerator.generateKeyPair();
            return true;
        } catch (InvalidAlgorithmParameterException unused) {
            return false;
        }
    }

    private boolean isKeyCreated() {
        try {
            return this.keyStore.containsAlias("wallet_key11") || createKeyPair();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    private boolean initCipher(int i) {
        if (i == 1) {
            PublicKey publicKey = this.keyStore.getCertificate("wallet_key11").getPublicKey();
            this.cipher.init(i, KeyFactory.getInstance(publicKey.getAlgorithm()).generatePublic(new X509EncodedKeySpec(publicKey.getEncoded())), new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
        } else if (i != 2) {
            return false;
        } else {
            try {
                OAEPParameterSpec oAEPParameterSpec = new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
                this.cipher.init(i, (PrivateKey) this.keyStore.getKey("wallet_key11", (char[]) null), oAEPParameterSpec);
            } catch (KeyPermanentlyInvalidatedException unused) {
                deleteInvalidKey();
                return false;
            } catch (UnrecoverableKeyException unused2) {
                deleteInvalidKey();
                return false;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
        return true;
    }

    private void deleteInvalidKey() {
        try {
            this.keyStore.deleteEntry("wallet_key11");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private String encode(String str) {
        try {
            if (!isKeyCreated() || !initCipher(1)) {
                return null;
            }
            return Base64.encodeToString(this.cipher.doFinal(str.getBytes()), 2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static String decode(String str, Cipher cipher2) {
        try {
            return new String(cipher2.doFinal(Base64.decode(str, 2)));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("Test");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    TestActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        this.fragmentView = frameLayout;
        this.codeField = new EditTextBoldCursor(context);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.codeField.setCursorWidth(1.5f);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.codeField.setImeOptions(NUM);
        this.codeField.setTextSize(1, 18.0f);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 10, 20, 10, 0));
        Button button = new Button(context);
        button.setText("encrypt");
        frameLayout.addView(button, LayoutHelper.createLinear(-1, 48, 1, 10, 80, 10, 0));
        button.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TestActivity.this.lambda$createView$0$TestActivity(view);
            }
        });
        this.encryptedString = MessagesController.getMainSettings(UserConfig.selectedAccount).getString("test_enc", (String) null);
        String str = this.encryptedString;
        if (str != null) {
            this.codeField.setText(str);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$TestActivity(View view) {
        String encode = encode(this.codeField.getText().toString());
        if (encode != null) {
            MessagesController.getMainSettings(UserConfig.selectedAccount).edit().putString("test_enc", encode).commit();
            Toast.makeText(getParentActivity(), "String encoded", 0).show();
            finishFragment();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.encryptedString != null) {
            prepareSensor();
        }
    }

    private void prepareSensor() {
        isKeyCreated();
        initCipher(2);
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(this.cipher);
        Toast.makeText(getParentActivity(), "use fingerprint to login", 1).show();
        this.fingerprintHelper = new FingerprintHelper(getParentActivity());
        this.fingerprintHelper.startAuth(cryptoObject);
    }

    public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {
        private CancellationSignal mCancellationSignal;
        private Context mContext;

        FingerprintHelper(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: package-private */
        public void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
            this.mCancellationSignal = new CancellationSignal();
            FingerprintManagerCompat.from(this.mContext).authenticate(cryptoObject, 0, this.mCancellationSignal, this, (Handler) null);
        }

        /* access modifiers changed from: package-private */
        public void cancel() {
            CancellationSignal cancellationSignal = this.mCancellationSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }

        public void onAuthenticationError(int i, CharSequence charSequence) {
            Toast.makeText(this.mContext, charSequence, 0).show();
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            Toast.makeText(this.mContext, charSequence, 0).show();
        }

        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult authenticationResult) {
            TestActivity.this.codeField.setText(TestActivity.decode(TestActivity.this.encryptedString, authenticationResult.getCryptoObject().getCipher()));
            Toast.makeText(this.mContext, "success", 0).show();
        }

        public void onAuthenticationFailed() {
            Toast.makeText(this.mContext, "try again", 0).show();
        }
    }
}
