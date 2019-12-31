package org.telegram.ui.Components;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback;
import android.hardware.biometrics.BiometricPrompt.AuthenticationResult;
import android.hardware.biometrics.BiometricPrompt.Builder;
import android.hardware.biometrics.BiometricPrompt.CryptoObject;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class BiometricPromtHelper {
    private static final int STATE_AUTHENTICATED = 4;
    private static final int STATE_AUTHENTICATING = 1;
    private static final int STATE_ERROR = 2;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PENDING_CONFIRMATION = 3;
    static final String[] badBiometricModels;
    static final String[] hideBiometricModels = new String[]{"SM-G97", "SM-N97"};
    private BottomSheet bottomSheet;
    private CancellationSignal cancellationSignal;
    private int currentState;
    private TextView errorTextView;
    private ImageView iconImageView;
    private Button negativeButton;
    private BaseFragment parentFragment;
    private Runnable resetRunnable = new -$$Lambda$BiometricPromtHelper$Ty8CMFc7d5KRc_VVAz2DeJH4p2k(this);

    public interface CipherCallback {
        void run(Cipher cipher);
    }

    public interface ContinueCallback {
        void run(boolean z);
    }

    static /* synthetic */ void lambda$promtWithCipher$0(DialogInterface dialogInterface, int i) {
    }

    private boolean shouldAnimateForTransition(int i, int i2) {
        if (i2 == 2) {
            return true;
        }
        if (i == 2 && i2 == 1) {
            return true;
        }
        if (i == 1 && i2 == 4) {
            return false;
        }
        if (!(i == 2 && i2 == 4) && i2 == 1) {
        }
        return false;
    }

    public BiometricPromtHelper(BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
    }

    public void promtWithCipher(Cipher cipher, String str, CipherCallback cipherCallback) {
        promtWithCipher(cipher, str, cipherCallback, shouldUseFingerprintForCrypto());
    }

    private void promtWithCipher(Cipher cipher, String str, CipherCallback cipherCallback, boolean z) {
        final Cipher cipher2 = cipher;
        final String str2 = str;
        final CipherCallback cipherCallback2 = cipherCallback;
        if (cipher2 != null && cipherCallback2 != null) {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment != null && baseFragment.getParentActivity() != null) {
                Activity parentActivity = this.parentFragment.getParentActivity();
                String str3 = "Cancel";
                String str4 = "Wallet";
                if (VERSION.SDK_INT >= 28 && !z) {
                    this.cancellationSignal = new CancellationSignal();
                    Builder builder = new Builder(parentActivity);
                    builder.setTitle(LocaleController.getString(str4, NUM));
                    builder.setDescription(str2);
                    builder.setNegativeButton(LocaleController.getString(str3, NUM), parentActivity.getMainExecutor(), -$$Lambda$BiometricPromtHelper$8iJ3p94NRkmT4GGZwbEsySMo9Io.INSTANCE);
                    builder.build().authenticate(new CryptoObject(cipher2), this.cancellationSignal, parentActivity.getMainExecutor(), new AuthenticationCallback() {
                        public void onAuthenticationFailed() {
                        }

                        public void onAuthenticationHelp(int i, CharSequence charSequence) {
                        }

                        public void onAuthenticationError(int i, CharSequence charSequence) {
                            if (i == 7) {
                                AlertsCreator.showSimpleAlert(BiometricPromtHelper.this.parentFragment, LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletBiometricTooManyAttempts", NUM));
                            } else if (i == 11) {
                                BiometricPromtHelper.this.promtWithCipher(cipher2, str2, cipherCallback2, true);
                            }
                        }

                        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
                            cipherCallback2.run(authenticationResult.getCryptoObject().getCipher());
                        }
                    });
                } else if (VERSION.SDK_INT >= 23) {
                    this.cancellationSignal = new CancellationSignal();
                    parentActivity = this.parentFragment.getParentActivity();
                    final BottomSheet.Builder builder2 = new BottomSheet.Builder(parentActivity);
                    builder2.setUseFullWidth(false);
                    builder2.setApplyTopPadding(false);
                    builder2.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(parentActivity);
                    linearLayout.setOrientation(1);
                    linearLayout.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 4.0f, 4.0f, 4.0f));
                    builder2.setCustomView(linearLayout);
                    TextView textView = new TextView(parentActivity);
                    String str5 = "fonts/rmedium.ttf";
                    textView.setTypeface(AndroidUtilities.getTypeface(str5));
                    textView.setGravity(1);
                    textView.setTextSize(1, 20.0f);
                    String str6 = "dialogTextBlack";
                    textView.setTextColor(Theme.getColor(str6));
                    textView.setText(LocaleController.getString(str4, NUM));
                    linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 24.0f, 0.0f));
                    TextView textView2 = new TextView(parentActivity);
                    textView2.setGravity(1);
                    textView2.setTextSize(1, 16.0f);
                    textView2.setTextColor(Theme.getColor(str6));
                    textView2.setText(str2);
                    textView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                    linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 0.0f));
                    this.iconImageView = new ImageView(parentActivity);
                    this.iconImageView.setScaleType(ScaleType.FIT_XY);
                    linearLayout.addView(this.iconImageView, LayoutHelper.createLinear(64, 64, 1, 0, 48, 0, 0));
                    this.errorTextView = new TextView(parentActivity);
                    this.errorTextView.setGravity(1);
                    this.errorTextView.setTextSize(1, 12.0f);
                    this.errorTextView.setTextColor(Theme.getColor("dialogTextGray2"));
                    this.errorTextView.setText(LocaleController.getString(str4, NUM));
                    this.errorTextView.setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(24.0f));
                    linearLayout.addView(this.errorTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 0.0f));
                    this.negativeButton = new Button(parentActivity);
                    this.negativeButton.setGravity(17);
                    String str7 = "dialogButton";
                    this.negativeButton.setTextColor(Theme.getColor(str7));
                    this.negativeButton.setText(LocaleController.getString(str3, NUM));
                    this.negativeButton.setTypeface(AndroidUtilities.getTypeface(str5));
                    this.negativeButton.setSingleLine(true);
                    this.negativeButton.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                    this.negativeButton.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(Theme.getColor(str7)));
                    linearLayout.addView(this.negativeButton, LayoutHelper.createLinear(-2, 36, 14.0f, 42.0f, 0.0f, 14.0f));
                    this.negativeButton.setOnClickListener(new -$$Lambda$BiometricPromtHelper$R_6nsmpBN5MdmARTMuTi25WvKIw(builder2));
                    BaseFragment baseFragment2 = this.parentFragment;
                    BottomSheet create = builder2.create();
                    this.bottomSheet = create;
                    baseFragment2.showDialog(create, new -$$Lambda$BiometricPromtHelper$3BZnIp31gJFrrpdPxqIiJJd1s_A(this));
                    FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull();
                    if (fingerprintManagerOrNull != null) {
                        fingerprintManagerOrNull.authenticate(new FingerprintManager.CryptoObject(cipher2), this.cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                            public void onAuthenticationError(int i, CharSequence charSequence) {
                                if (i == 10) {
                                    BiometricPromtHelper.this.bottomSheet.dismiss();
                                    return;
                                }
                                BiometricPromtHelper.this.updateState(2);
                                BiometricPromtHelper.this.showTemporaryMessage(charSequence);
                            }

                            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                                BiometricPromtHelper.this.updateState(2);
                                BiometricPromtHelper.this.showTemporaryMessage(charSequence);
                            }

                            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                                builder2.getDismissRunnable().run();
                                cipherCallback2.run(authenticationResult.getCryptoObject().getCipher());
                            }

                            public void onAuthenticationFailed() {
                                BiometricPromtHelper.this.updateState(2);
                                BiometricPromtHelper.this.showTemporaryMessage(LocaleController.getString("WalletTouchFingerprintNotRecognized", NUM));
                            }
                        }, null);
                    }
                    updateState(1);
                    this.errorTextView.setText(LocaleController.getString("WalletTouchFingerprint", NUM));
                    this.errorTextView.setVisibility(0);
                }
            }
        }
    }

    public /* synthetic */ void lambda$promtWithCipher$2$BiometricPromtHelper(DialogInterface dialogInterface) {
        CancellationSignal cancellationSignal = this.cancellationSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.cancellationSignal = null;
        }
        this.bottomSheet = null;
    }

    private void updateState(int i) {
        if (i == 3) {
            AndroidUtilities.cancelRunOnUIThread(this.resetRunnable);
            this.errorTextView.setVisibility(4);
        } else if (i == 4) {
            this.negativeButton.setVisibility(8);
            this.errorTextView.setVisibility(4);
        }
        updateIcon(this.currentState, i);
        this.currentState = i;
    }

    private void showTemporaryMessage(CharSequence charSequence) {
        AndroidUtilities.cancelRunOnUIThread(this.resetRunnable);
        this.errorTextView.setText(charSequence);
        this.errorTextView.setTextColor(Theme.getColor("dialogTextRed2"));
        this.errorTextView.setContentDescription(charSequence);
        AndroidUtilities.runOnUIThread(this.resetRunnable, 2000);
    }

    private void handleResetMessage() {
        if (this.errorTextView != null) {
            updateState(1);
            this.errorTextView.setText(LocaleController.getString("WalletTouchFingerprint", NUM));
            this.errorTextView.setTextColor(Theme.getColor("dialogButton"));
        }
    }

    private void updateIcon(int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
            Drawable animationForTransition = getAnimationForTransition(i, i2);
            if (animationForTransition != null) {
                AnimatedVectorDrawable animatedVectorDrawable = animationForTransition instanceof AnimatedVectorDrawable ? (AnimatedVectorDrawable) animationForTransition : null;
                this.iconImageView.setImageDrawable(animationForTransition);
                if (animatedVectorDrawable != null && shouldAnimateForTransition(i, i2)) {
                    animatedVectorDrawable.start();
                }
            }
        }
    }

    /* JADX WARNING: Missing block: B:14:0x0025, code skipped:
            if (r7 == 4) goto L_0x002f;
     */
    /* JADX WARNING: Missing block: B:16:0x002a, code skipped:
            if (r7 == 4) goto L_0x002f;
     */
    /* JADX WARNING: Missing block: B:17:0x002d, code skipped:
            if (r7 == 1) goto L_0x002f;
     */
    private android.graphics.drawable.Drawable getAnimationForTransition(int r6, int r7) {
        /*
        r5 = this;
        r0 = r5.parentFragment;
        r1 = 0;
        if (r0 == 0) goto L_0x003a;
    L_0x0005:
        r0 = r0.getParentActivity();
        if (r0 == 0) goto L_0x003a;
    L_0x000b:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r0 >= r2) goto L_0x0012;
    L_0x0011:
        goto L_0x003a;
    L_0x0012:
        r0 = 2;
        r2 = NUM; // 0x7var_c4 float:1.7944975E38 double:1.0529356E-314;
        if (r7 != r0) goto L_0x0019;
    L_0x0018:
        goto L_0x002f;
    L_0x0019:
        r3 = 1;
        if (r6 != r0) goto L_0x0022;
    L_0x001c:
        if (r7 != r3) goto L_0x0022;
    L_0x001e:
        r2 = NUM; // 0x7var_c3 float:1.7944973E38 double:1.0529355994E-314;
        goto L_0x002f;
    L_0x0022:
        r4 = 4;
        if (r6 != r3) goto L_0x0028;
    L_0x0025:
        if (r7 != r4) goto L_0x0028;
    L_0x0027:
        goto L_0x002f;
    L_0x0028:
        if (r6 != r0) goto L_0x002d;
    L_0x002a:
        if (r7 != r4) goto L_0x002d;
    L_0x002c:
        goto L_0x002f;
    L_0x002d:
        if (r7 != r3) goto L_0x003a;
    L_0x002f:
        r6 = r5.parentFragment;
        r6 = r6.getParentActivity();
        r6 = r6.getDrawable(r2);
        return r6;
    L_0x003a:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BiometricPromtHelper.getAnimationForTransition(int, int):android.graphics.drawable.Drawable");
    }

    public void onPause() {
        BottomSheet bottomSheet = this.bottomSheet;
        if (bottomSheet != null) {
            bottomSheet.dismiss();
            this.bottomSheet = null;
        }
        CancellationSignal cancellationSignal = this.cancellationSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.cancellationSignal = null;
        }
    }

    public void cancelPromt() {
        this.cancellationSignal.cancel();
        this.cancellationSignal = null;
    }

    private static FingerprintManager getFingerprintManagerOrNull() {
        int i = VERSION.SDK_INT;
        if (i == 23) {
            return (FingerprintManager) ApplicationLoader.applicationContext.getSystemService(FingerprintManager.class);
        }
        return (i <= 23 || !ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) ? null : (FingerprintManager) ApplicationLoader.applicationContext.getSystemService(FingerprintManager.class);
    }

    public static boolean hasBiometricEnrolled() {
        boolean z = true;
        if (VERSION.SDK_INT >= 29 && !shouldUseFingerprintForCrypto()) {
            if (((BiometricManager) ApplicationLoader.applicationContext.getSystemService(BiometricManager.class)).canAuthenticate() != 0) {
                z = false;
            }
            return z;
        } else if (VERSION.SDK_INT < 23) {
            return false;
        } else {
            FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull();
            if (!(fingerprintManagerOrNull != null && fingerprintManagerOrNull.isHardwareDetected() && fingerprintManagerOrNull.hasEnrolledFingerprints())) {
                z = false;
            }
            return z;
        }
    }

    public static boolean canAddBiometric() {
        if (VERSION.SDK_INT < 29 || shouldUseFingerprintForCrypto()) {
            return hasFingerprintHardware();
        }
        boolean z = true;
        if (((BiometricManager) ApplicationLoader.applicationContext.getSystemService(BiometricManager.class)).canAuthenticate() == 1) {
            z = false;
        }
        return z;
    }

    public static boolean hasFingerprintHardware() {
        if (VERSION.SDK_INT < 23) {
            return false;
        }
        FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull();
        if (fingerprintManagerOrNull == null || !fingerprintManagerOrNull.isHardwareDetected()) {
            return false;
        }
        return true;
    }

    public static boolean hasLockscreenProtected() {
        return VERSION.SDK_INT >= 23 ? ((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isDeviceSecure() : false;
    }

    public static void askForBiometric(BaseFragment baseFragment, ContinueCallback continueCallback, String str) {
        if (baseFragment != null && baseFragment.getParentActivity() != null && continueCallback != null) {
            boolean hasBiometricEnrolled = hasBiometricEnrolled();
            if (hasBiometricEnrolled || VERSION.SDK_INT < 23 || (hasLockscreenProtected() && !hasFingerprintHardware())) {
                continueCallback.run(hasBiometricEnrolled);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("WalletSecurityAlertTitle", NUM));
            boolean z = false;
            if (hasLockscreenProtected()) {
                builder.setMessage(LocaleController.getString("WalletSecurityAlertTextBiometric", NUM));
                z = true;
            } else if (hasFingerprintHardware()) {
                builder.setMessage(LocaleController.getString("WalletSecurityAlertTextLockscreenBiometric", NUM));
            } else {
                builder.setMessage(LocaleController.getString("WalletSecurityAlertTextLockscreen", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("WalletSecurityAlertSetup", NUM), new -$$Lambda$BiometricPromtHelper$TXMp1z6RNX9zEMWYnBxwe83XkOg(z, baseFragment));
            builder.setNegativeButton(str, new -$$Lambda$BiometricPromtHelper$OEA0RNRvsc3CizQofe65tz-aKdE(continueCallback));
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$askForBiometric$3(boolean z, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        if (z) {
            try {
                if (VERSION.SDK_INT >= 28) {
                    baseFragment.getParentActivity().startActivity(new Intent("android.settings.FINGERPRINT_ENROLL"));
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        baseFragment.getParentActivity().startActivity(new Intent("android.settings.SECURITY_SETTINGS"));
    }

    static {
        String[] strArr = new String[7];
        strArr[0] = "SM-G95";
        strArr[1] = "SM-G96";
        strArr[2] = "SM-G97";
        strArr[3] = "SM-N95";
        strArr[4] = "SM-N96";
        strArr[5] = "SM-N97";
        strArr[6] = "SM-A20";
        badBiometricModels = strArr;
    }

    private static boolean shouldHideBiometric() {
        return isModelInList(Build.MODEL, hideBiometricModels);
    }

    private static boolean shouldUseFingerprintForCrypto() {
        int i = VERSION.SDK_INT;
        return (i < 28 || i > 29) ? false : isModelInList(Build.MODEL, badBiometricModels);
    }

    private static boolean isModelInList(String str, String[] strArr) {
        if (str == null) {
            return false;
        }
        for (String startsWith : strArr) {
            if (str.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }
}
