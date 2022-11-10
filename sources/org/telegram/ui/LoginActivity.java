package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import j$.util.Comparator$CC;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_changePhone;
import org.telegram.tgnet.TLRPC$TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC$TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC$TL_account_emailVerified;
import org.telegram.tgnet.TLRPC$TL_account_emailVerifiedLogin;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_sendVerifyEmailCode;
import org.telegram.tgnet.TLRPC$TL_account_sentEmailCode;
import org.telegram.tgnet.TLRPC$TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC$TL_auth_authorization;
import org.telegram.tgnet.TLRPC$TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC$TL_auth_checkPassword;
import org.telegram.tgnet.TLRPC$TL_auth_checkRecoveryPassword;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeMissedCall;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC$TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeEmailCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeMissedCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeSetUpEmailRequired;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_signIn;
import org.telegram.tgnet.TLRPC$TL_auth_signUp;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_emailVerificationCode;
import org.telegram.tgnet.TLRPC$TL_emailVerificationGoogle;
import org.telegram.tgnet.TLRPC$TL_emailVerifyPurposeLoginChange;
import org.telegram.tgnet.TLRPC$TL_emailVerifyPurposeLoginSetup;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_countriesList;
import org.telegram.tgnet.TLRPC$TL_help_country;
import org.telegram.tgnet.TLRPC$TL_help_countryCode;
import org.telegram.tgnet.TLRPC$TL_help_getCountriesList;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_nearestDc;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$account_Password;
import org.telegram.tgnet.TLRPC$auth_Authorization;
import org.telegram.tgnet.TLRPC$auth_CodeType;
import org.telegram.tgnet.TLRPC$auth_SentCodeType;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedPhoneNumberEditText;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoginOrView;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.LoginActivity;
@SuppressLint({"HardwareIds"})
/* loaded from: classes3.dex */
public class LoginActivity extends BaseFragment {
    private static final int SHOW_DELAY;
    private int activityMode;
    private Runnable animationFinishCallback;
    private ImageView backButtonView;
    private AlertDialog cancelDeleteProgressDialog;
    private TLRPC$TL_auth_sentCode cancelDeletionCode;
    private Bundle cancelDeletionParams;
    private String cancelDeletionPhone;
    private boolean checkPermissions;
    private boolean checkShowPermissions;
    private int currentDoneType;
    private TLRPC$TL_help_termsOfService currentTermsOfService;
    private int currentViewNum;
    private boolean customKeyboardWasVisible;
    private boolean[] doneButtonVisible;
    private AnimatorSet doneItemAnimation;
    private boolean[] doneProgressVisible;
    private Runnable[] editDoneCallback;
    private Runnable emailChangeFinishCallback;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private View introView;
    private boolean isAnimatingIntro;
    private ValueAnimator keyboardAnimator;
    private Runnable keyboardHideCallback;
    private LinearLayout keyboardLinearLayout;
    private CustomPhoneKeyboardView keyboardView;
    private boolean needRequestPermissions;
    private boolean newAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems;
    private PhoneNumberConfirmView phoneNumberConfirmView;
    private boolean[] postedEditDoneCallback;
    private int progressRequestId;
    private RadialProgressView radialProgressView;
    private boolean restoringState;
    private AnimatorSet[] showDoneAnimation;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private FrameLayout slideViewsContainer;
    private TextView startMessagingButton;
    private boolean syncContacts;
    private boolean testBackend;
    private SlideView[] views;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ProgressView extends View {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return true;
    }

    static {
        SHOW_DELAY = SharedConfig.getDevicePerformanceClass() <= 1 ? 150 : 100;
    }

    public LoginActivity() {
        this.views = new SlideView[15];
        this.permissionsItems = new ArrayList<>();
        this.permissionsShowItems = new ArrayList<>();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
    }

    public LoginActivity(int i) {
        this.views = new SlideView[15];
        this.permissionsItems = new ArrayList<>();
        this.permissionsShowItems = new ArrayList<>();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
        this.currentAccount = i;
        this.newAccount = true;
    }

    public LoginActivity changeEmail(Runnable runnable) {
        this.activityMode = 3;
        this.currentViewNum = 12;
        this.emailChangeFinishCallback = runnable;
        return this;
    }

    public LoginActivity cancelAccountDeletion(String str, Bundle bundle, TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode) {
        this.cancelDeletionPhone = str;
        this.cancelDeletionParams = bundle;
        this.cancelDeletionCode = tLRPC$TL_auth_sentCode;
        this.activityMode = 1;
        return this;
    }

    public LoginActivity changePhoneNumber() {
        this.activityMode = 2;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInCancelAccountDeletionMode() {
        return this.activityMode == 1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        Runnable[] runnableArr;
        super.onFragmentDestroy();
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[i] != null) {
                slideViewArr[i].onDestroyActivity();
            }
            i++;
        }
        AlertDialog alertDialog = this.cancelDeleteProgressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        for (Runnable runnable : this.editDoneCallback) {
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:117:0x0411, code lost:
        if (r2 != 4) goto L93;
     */
    /* JADX WARN: Removed duplicated region for block: B:114:0x040c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x041a  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0443  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0438 A[EDGE_INSN: B:134:0x0438->B:126:0x0438 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01fb  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x01fd  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x020a  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x026f  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x027e  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0283  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0289  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x028e  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0355  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0358  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x035c  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x035f  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x039e  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x03a6  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x03c1  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(android.content.Context r32) {
        /*
            Method dump skipped, instructions count: 1101
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, boolean z) {
        Runnable runnable;
        if (i > AndroidUtilities.dp(20.0f) && isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
        if (i > AndroidUtilities.dp(20.0f) || (runnable = this.keyboardHideCallback) == null) {
            return;
        }
        runnable.run();
        this.keyboardHideCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        onDoneButtonPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DynamicAnimation dynamicAnimation, float f, float f2) {
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView != null) {
            phoneNumberConfirmView.updateFabPosition();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        if (onBackPressed()) {
            finishFragment();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCustomKeyboardForceDisabled() {
        Point point = AndroidUtilities.displaySize;
        return point.x > point.y || AndroidUtilities.isTablet() || AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        return this.views[this.currentViewNum].hasCustomKeyboard() && !isCustomKeyboardForceDisabled();
    }

    private void setCustomKeyboardVisible(boolean z, boolean z2) {
        if (this.customKeyboardWasVisible != z || !z2) {
            this.customKeyboardWasVisible = z;
            if (isCustomKeyboardForceDisabled()) {
                z = false;
            }
            if (z) {
                AndroidUtilities.hideKeyboard(this.fragmentView);
                AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
                if (z2) {
                    ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                    this.keyboardAnimator = duration;
                    duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.keyboardAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            LoginActivity.this.lambda$setCustomKeyboardVisible$4(valueAnimator);
                        }
                    });
                    this.keyboardAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.6
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            LoginActivity.this.keyboardView.setVisibility(0);
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (LoginActivity.this.keyboardAnimator == animator) {
                                LoginActivity.this.keyboardAnimator = null;
                            }
                        }
                    });
                    this.keyboardAnimator.start();
                    return;
                }
                this.keyboardView.setVisibility(0);
                return;
            }
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
            if (z2) {
                ValueAnimator duration2 = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(300L);
                this.keyboardAnimator = duration2;
                duration2.setInterpolator(Easings.easeInOutQuad);
                this.keyboardAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LoginActivity.this.lambda$setCustomKeyboardVisible$5(valueAnimator);
                    }
                });
                this.keyboardAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        LoginActivity.this.keyboardView.setVisibility(8);
                        if (LoginActivity.this.keyboardAnimator == animator) {
                            LoginActivity.this.keyboardAnimator = null;
                        }
                    }
                });
                this.keyboardAnimator.start();
                return;
            }
            this.keyboardView.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setCustomKeyboardVisible$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * AndroidUtilities.dp(230.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setCustomKeyboardVisible$5(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * AndroidUtilities.dp(230.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        SlideView slideView;
        int i;
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.fragmentView.requestLayout();
        try {
            int i2 = this.currentViewNum;
            if (i2 >= 1 && i2 <= 4) {
                SlideView[] slideViewArr = this.views;
                if ((slideViewArr[i2] instanceof LoginActivitySmsView) && (i = ((LoginActivitySmsView) slideViewArr[i2]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - i) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        int i3 = this.currentViewNum;
        if (i3 == 0 && !this.needRequestPermissions && (slideView = this.views[i3]) != null) {
            slideView.onShow();
        }
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        setCustomKeyboardVisible(this.views[this.currentViewNum].hasCustomKeyboard(), false);
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView != null) {
            phoneNumberConfirmView.dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (strArr.length == 0 || iArr.length == 0) {
            return;
        }
        boolean z = iArr[0] == 0;
        if (i == 6) {
            this.checkPermissions = false;
            int i2 = this.currentViewNum;
            if (i2 != 0) {
                return;
            }
            ((PhoneView) this.views[i2]).confirmedNumber = true;
            this.views[this.currentViewNum].onNextPressed(null);
        } else if (i == 7) {
            this.checkShowPermissions = false;
            int i3 = this.currentViewNum;
            if (i3 != 0) {
                return;
            }
            ((PhoneView) this.views[i3]).fillNumber();
        } else if (i == 20) {
            if (!z) {
                return;
            }
            ((LoginActivityRegisterView) this.views[5]).imageUpdater.openCamera();
        } else if (i != 151 || !z) {
        } else {
            final LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
            loginActivityRegisterView.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.lambda$onRequestPermissionsResultFragment$6(LoginActivity.LoginActivityRegisterView.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onRequestPermissionsResultFragment$6(LoginActivityRegisterView loginActivityRegisterView) {
        loginActivityRegisterView.imageUpdater.openGallery();
    }

    public static Bundle loadCurrentState(boolean z) {
        if (z) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, ?> entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String[] split = key.split("_\\|_");
                if (split.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer) value).intValue());
                    }
                } else if (split.length == 2) {
                    Bundle bundle2 = bundle.getBundle(split[0]);
                    if (bundle2 == null) {
                        bundle2 = new Bundle();
                        bundle.putBundle(split[0], bundle2);
                    }
                    if (value instanceof String) {
                        bundle2.putString(split[1], (String) value);
                    } else if (value instanceof Integer) {
                        bundle2.putInt(split[1], ((Integer) value).intValue());
                    }
                }
            }
            return bundle;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private void clearCurrentState() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        edit.clear();
        edit.commit();
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String str) {
        for (String str2 : bundle.keySet()) {
            Object obj = bundle.get(str2);
            if (obj instanceof String) {
                if (str != null) {
                    editor.putString(str + "_|_" + str2, (String) obj);
                } else {
                    editor.putString(str2, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (str != null) {
                    editor.putInt(str + "_|_" + str2, ((Integer) obj).intValue());
                } else {
                    editor.putInt(str2, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, str2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
                    getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
                } else if (dialog != this.permissionsShowDialog || this.permissionsShowItems.isEmpty() || getParentActivity() == null) {
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda15
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.this.lambda$onDialogDismiss$7();
                        }
                    }, 200L);
                    getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
                }
            } catch (Exception unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDialogDismiss$7() {
        this.needRequestPermissions = false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        int i = this.currentViewNum;
        int i2 = 0;
        if (i != 0 && (this.activityMode != 3 || i != 12)) {
            if (i == 6) {
                this.views[i].onBackPressed(true);
                setPage(0, true, null, true);
            } else if (i == 7 || i == 8) {
                this.views[i].onBackPressed(true);
                setPage(6, true, null, true);
            } else if ((i < 1 || i > 4) && i != 11) {
                if (i == 5) {
                    ((LoginActivityRegisterView) this.views[i]).wrongNumber.callOnClick();
                } else if (i == 9) {
                    this.views[i].onBackPressed(true);
                    setPage(7, true, null, true);
                } else if (i == 10) {
                    this.views[i].onBackPressed(true);
                    setPage(9, true, null, true);
                } else if (i == 13) {
                    this.views[i].onBackPressed(true);
                    setPage(12, true, null, true);
                } else if (this.views[i].onBackPressed(true)) {
                    setPage(0, true, null, true);
                }
            } else if (this.views[i].onBackPressed(false)) {
                setPage(0, true, null, true);
            }
            return false;
        }
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i2 < slideViewArr.length) {
                if (slideViewArr[i2] != null) {
                    slideViewArr[i2].onDestroyActivity();
                }
                i2++;
            } else {
                clearCurrentState();
                return true;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
        if (loginActivityRegisterView != null) {
            loginActivityRegisterView.imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needShowAlert(String str, String str2) {
        if (str2 == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(str);
        builder.setMessage(str2);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFieldError(final View view, boolean z) {
        view.performHapticFeedback(3, 2);
        AndroidUtilities.shakeViewSpring(view, 3.5f);
        if (!z || !(view instanceof OutlineTextContainerView)) {
            return;
        }
        int i = R.id.timeout_callback;
        Runnable runnable = (Runnable) view.getTag(i);
        if (runnable != null) {
            view.removeCallbacks(runnable);
        }
        final OutlineTextContainerView outlineTextContainerView = (OutlineTextContainerView) view;
        AtomicReference atomicReference = new AtomicReference();
        final EditText attachedEditText = outlineTextContainerView.getAttachedEditText();
        final AnonymousClass8 anonymousClass8 = new AnonymousClass8(this, attachedEditText, atomicReference);
        outlineTextContainerView.animateError(1.0f);
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.lambda$onFieldError$9(OutlineTextContainerView.this, view, attachedEditText, anonymousClass8);
            }
        };
        atomicReference.set(runnable2);
        view.postDelayed(runnable2, 2000L);
        view.setTag(i, runnable2);
        if (attachedEditText == null) {
            return;
        }
        attachedEditText.addTextChangedListener(anonymousClass8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.LoginActivity$8  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass8 implements TextWatcher {
        final /* synthetic */ EditText val$editText;
        final /* synthetic */ AtomicReference val$timeoutCallbackRef;

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        AnonymousClass8(LoginActivity loginActivity, EditText editText, AtomicReference atomicReference) {
            this.val$editText = editText;
            this.val$timeoutCallbackRef = atomicReference;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            final EditText editText = this.val$editText;
            final AtomicReference atomicReference = this.val$timeoutCallbackRef;
            editText.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.AnonymousClass8.this.lambda$beforeTextChanged$0(editText, atomicReference);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$beforeTextChanged$0(EditText editText, AtomicReference atomicReference) {
            editText.removeTextChangedListener(this);
            editText.removeCallbacks((Runnable) atomicReference.get());
            ((Runnable) atomicReference.get()).run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onFieldError$9(OutlineTextContainerView outlineTextContainerView, View view, final EditText editText, final TextWatcher textWatcher) {
        outlineTextContainerView.animateError(0.0f);
        view.setTag(R.id.timeout_callback, null);
        if (editText != null) {
            editText.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    editText.removeTextChangedListener(textWatcher);
                }
            });
        }
    }

    public static void needShowInvalidAlert(BaseFragment baseFragment, String str, boolean z) {
        needShowInvalidAlert(baseFragment, str, null, z);
    }

    public static void needShowInvalidAlert(final BaseFragment baseFragment, final String str, PhoneInputData phoneInputData, final boolean z) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("BannedPhoneNumber", R.string.BannedPhoneNumber));
        } else if (phoneInputData == null || phoneInputData.patterns == null || phoneInputData.patterns.isEmpty() || phoneInputData.country == null) {
            builder.setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString(R.string.InvalidPhoneNumber));
        } else {
            int i = Integer.MAX_VALUE;
            for (String str2 : phoneInputData.patterns) {
                int length = str2.replace(" ", "").length();
                if (length < i) {
                    i = length;
                }
            }
            if (PhoneFormat.stripExceptNumbers(str).length() - phoneInputData.country.code.length() < i) {
                builder.setTitle(LocaleController.getString(R.string.WrongNumberFormat));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ShortNumberInfo", R.string.ShortNumberInfo, phoneInputData.country.name, phoneInputData.phoneNumber)));
            } else {
                builder.setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle));
                builder.setMessage(LocaleController.getString(R.string.InvalidPhoneNumber));
            }
        }
        builder.setNeutralButton(LocaleController.getString("BotHelp", R.string.BotHelp), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda8
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                LoginActivity.lambda$needShowInvalidAlert$10(z, str, baseFragment, dialogInterface, i2);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        baseFragment.showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$needShowInvalidAlert$10(boolean z, String str, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String format = String.format(Locale.US, "%s (%d)", packageInfo.versionName, Integer.valueOf(packageInfo.versionCode));
            Intent intent = new Intent("android.intent.action.SENDTO");
            intent.setData(Uri.parse("mailto:"));
            String[] strArr = new String[1];
            strArr[0] = z ? "recover@telegram.org" : "login@stel.com";
            intent.putExtra("android.intent.extra.EMAIL", strArr);
            if (z) {
                intent.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + str);
                intent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + str + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                intent.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + str);
                intent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + str + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            baseFragment.getParentActivity().startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (Exception unused) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            baseFragment.showDialog(builder.create());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDoneButton(final boolean z, boolean z2) {
        TimeInterpolator timeInterpolator;
        int i = this.currentDoneType;
        final boolean z3 = i == 0;
        if (this.doneButtonVisible[i] == z) {
            return;
        }
        AnimatorSet[] animatorSetArr = this.showDoneAnimation;
        if (animatorSetArr[i] != null) {
            if (z2) {
                animatorSetArr[i].removeAllListeners();
            }
            this.showDoneAnimation[this.currentDoneType].cancel();
        }
        boolean[] zArr = this.doneButtonVisible;
        int i2 = this.currentDoneType;
        zArr[i2] = z;
        if (!z2) {
            if (z) {
                if (!z3) {
                    return;
                }
                this.floatingButtonContainer.setVisibility(0);
                this.floatingAutoAnimator.setOffsetY(0.0f);
                return;
            } else if (!z3) {
                return;
            } else {
                this.floatingButtonContainer.setVisibility(8);
                this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
                return;
            }
        }
        this.showDoneAnimation[i2] = new AnimatorSet();
        if (z) {
            if (z3) {
                if (this.floatingButtonContainer.getVisibility() != 0) {
                    this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
                    this.floatingButtonContainer.setVisibility(0);
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.floatingAutoAnimator.getOffsetY(), 0.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LoginActivity.this.lambda$showDoneButton$11(valueAnimator);
                    }
                });
                this.showDoneAnimation[this.currentDoneType].play(ofFloat);
            }
        } else if (z3) {
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.floatingAutoAnimator.getOffsetY(), AndroidUtilities.dpf2(70.0f));
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.this.lambda$showDoneButton$12(valueAnimator);
                }
            });
            this.showDoneAnimation[this.currentDoneType].play(ofFloat2);
        }
        this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (LoginActivity.this.showDoneAnimation[!z3 ? 1 : 0] == null || !LoginActivity.this.showDoneAnimation[!z3 ? 1 : 0].equals(animator) || z) {
                    return;
                }
                if (z3) {
                    LoginActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (!z3 || LoginActivity.this.floatingButtonIcon.getAlpha() == 1.0f) {
                    return;
                }
                LoginActivity.this.floatingButtonIcon.setAlpha(1.0f);
                LoginActivity.this.floatingButtonIcon.setScaleX(1.0f);
                LoginActivity.this.floatingButtonIcon.setScaleY(1.0f);
                LoginActivity.this.floatingButtonIcon.setVisibility(0);
                LoginActivity.this.floatingButtonContainer.setEnabled(true);
                LoginActivity.this.floatingProgressView.setAlpha(0.0f);
                LoginActivity.this.floatingProgressView.setScaleX(0.1f);
                LoginActivity.this.floatingProgressView.setScaleY(0.1f);
                LoginActivity.this.floatingProgressView.setVisibility(4);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (LoginActivity.this.showDoneAnimation[!z3 ? 1 : 0] == null || !LoginActivity.this.showDoneAnimation[!z3 ? 1 : 0].equals(animator)) {
                    return;
                }
                LoginActivity.this.showDoneAnimation[!z3 ? 1 : 0] = null;
            }
        });
        int i3 = 150;
        if (!z3) {
            timeInterpolator = null;
        } else if (z) {
            i3 = 200;
            timeInterpolator = AndroidUtilities.decelerateInterpolator;
        } else {
            timeInterpolator = AndroidUtilities.accelerateInterpolator;
        }
        this.showDoneAnimation[this.currentDoneType].setDuration(i3);
        this.showDoneAnimation[this.currentDoneType].setInterpolator(timeInterpolator);
        this.showDoneAnimation[this.currentDoneType].start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDoneButton$11(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(floatValue);
        this.floatingButtonContainer.setAlpha(1.0f - (floatValue / AndroidUtilities.dpf2(70.0f)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDoneButton$12(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(floatValue);
        this.floatingButtonContainer.setAlpha(1.0f - (floatValue / AndroidUtilities.dpf2(70.0f)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDoneButtonPressed() {
        if (!this.doneButtonVisible[this.currentDoneType]) {
            return;
        }
        if (this.radialProgressView.getTag() != null) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
            builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), null);
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda6
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.lambda$onDoneButtonPressed$13(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return;
        }
        this.views[this.currentViewNum].onNextPressed(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDoneButtonPressed$13(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        showEditDoneProgress(z, z2, false);
    }

    private void showEditDoneProgress(final boolean z, final boolean z2, boolean z3) {
        if (!z2 || this.doneProgressVisible[this.currentDoneType] != z || z3) {
            final int i = this.currentDoneType;
            final boolean z4 = i == 0;
            if (!z3 && !z4) {
                this.doneProgressVisible[i] = z;
                if (z2) {
                    if (this.postedEditDoneCallback[i]) {
                        AndroidUtilities.cancelRunOnUIThread(this.editDoneCallback[i]);
                        this.postedEditDoneCallback[this.currentDoneType] = false;
                        return;
                    } else if (z) {
                        Runnable[] runnableArr = this.editDoneCallback;
                        Runnable runnable = new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda16
                            @Override // java.lang.Runnable
                            public final void run() {
                                LoginActivity.this.lambda$showEditDoneProgress$14(i, z, z2);
                            }
                        };
                        runnableArr[i] = runnable;
                        AndroidUtilities.runOnUIThread(runnable, 2000L);
                        this.postedEditDoneCallback[this.currentDoneType] = true;
                        return;
                    }
                }
            } else {
                this.postedEditDoneCallback[i] = false;
                this.doneProgressVisible[i] = z;
            }
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            float f = 0.0f;
            if (z2) {
                this.doneItemAnimation = new AnimatorSet();
                float[] fArr = new float[2];
                fArr[0] = z ? 0.0f : 1.0f;
                if (z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.10
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        if (z) {
                            if (z4) {
                                LoginActivity.this.floatingButtonIcon.setVisibility(0);
                                LoginActivity.this.floatingProgressView.setVisibility(0);
                                LoginActivity.this.floatingButtonContainer.setEnabled(false);
                                return;
                            }
                            LoginActivity.this.radialProgressView.setVisibility(0);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (z4) {
                            if (!z) {
                                LoginActivity.this.floatingProgressView.setVisibility(4);
                                LoginActivity.this.floatingButtonIcon.setVisibility(0);
                                LoginActivity.this.floatingButtonContainer.setEnabled(true);
                            } else {
                                LoginActivity.this.floatingButtonIcon.setVisibility(4);
                                LoginActivity.this.floatingProgressView.setVisibility(0);
                            }
                        } else if (!z) {
                            LoginActivity.this.radialProgressView.setVisibility(4);
                        }
                        if (LoginActivity.this.doneItemAnimation == null || !LoginActivity.this.doneItemAnimation.equals(animator)) {
                            return;
                        }
                        LoginActivity.this.doneItemAnimation = null;
                    }
                });
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LoginActivity.this.lambda$showEditDoneProgress$15(z4, valueAnimator);
                    }
                });
                this.doneItemAnimation.playTogether(ofFloat);
                this.doneItemAnimation.setDuration(150L);
                this.doneItemAnimation.start();
            } else if (z) {
                if (z4) {
                    this.floatingProgressView.setVisibility(0);
                    this.floatingButtonIcon.setVisibility(4);
                    this.floatingButtonContainer.setEnabled(false);
                    this.floatingButtonIcon.setScaleX(0.1f);
                    this.floatingButtonIcon.setScaleY(0.1f);
                    this.floatingButtonIcon.setAlpha(0.0f);
                    this.floatingProgressView.setScaleX(1.0f);
                    this.floatingProgressView.setScaleY(1.0f);
                    this.floatingProgressView.setAlpha(1.0f);
                    return;
                }
                this.radialProgressView.setVisibility(0);
                this.radialProgressView.setScaleX(1.0f);
                this.radialProgressView.setScaleY(1.0f);
                this.radialProgressView.setAlpha(1.0f);
            } else {
                this.radialProgressView.setTag(null);
                if (z4) {
                    this.floatingProgressView.setVisibility(4);
                    this.floatingButtonIcon.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(true);
                    this.floatingProgressView.setScaleX(0.1f);
                    this.floatingProgressView.setScaleY(0.1f);
                    this.floatingProgressView.setAlpha(0.0f);
                    this.floatingButtonIcon.setScaleX(1.0f);
                    this.floatingButtonIcon.setScaleY(1.0f);
                    this.floatingButtonIcon.setAlpha(1.0f);
                    return;
                }
                this.radialProgressView.setVisibility(4);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setAlpha(0.0f);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showEditDoneProgress$14(int i, boolean z, boolean z2) {
        int i2 = this.currentDoneType;
        this.currentDoneType = i;
        showEditDoneProgress(z, z2, true);
        this.currentDoneType = i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showEditDoneProgress$15(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (z) {
            float f = 1.0f - floatValue;
            float f2 = (f * 0.9f) + 0.1f;
            this.floatingButtonIcon.setScaleX(f2);
            this.floatingButtonIcon.setScaleY(f2);
            this.floatingButtonIcon.setAlpha(f);
            float f3 = (0.9f * floatValue) + 0.1f;
            this.floatingProgressView.setScaleX(f3);
            this.floatingProgressView.setScaleY(f3);
            this.floatingProgressView.setAlpha(floatValue);
            return;
        }
        float f4 = (0.9f * floatValue) + 0.1f;
        this.radialProgressView.setScaleX(f4);
        this.radialProgressView.setScaleY(f4);
        this.radialProgressView.setAlpha(floatValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needShowProgress(int i) {
        needShowProgress(i, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needShowProgress(int i, boolean z) {
        if (isInCancelAccountDeletionMode() && i == 0) {
            if (this.cancelDeleteProgressDialog != null || getParentActivity() == null || getParentActivity().isFinishing()) {
                return;
            }
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.cancelDeleteProgressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.cancelDeleteProgressDialog.show();
            return;
        }
        this.progressRequestId = i;
        showEditDoneProgress(true, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needHideProgress(boolean z) {
        needHideProgress(z, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needHideProgress(boolean z, boolean z2) {
        AlertDialog alertDialog;
        if (this.progressRequestId != 0) {
            if (z) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        if (isInCancelAccountDeletionMode() && (alertDialog = this.cancelDeleteProgressDialog) != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        showEditDoneProgress(false, z2);
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        final boolean z3 = i == 0 || i == 5 || i == 6 || i == 9 || i == 10 || i == 12;
        int i2 = 8;
        if (z3) {
            if (i == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, z);
            showEditDoneProgress(false, z);
            this.currentDoneType = 0;
            showEditDoneProgress(false, z);
            if (!z) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, z);
            showEditDoneProgress(false, z);
            if (i != 8) {
                this.currentDoneType = 1;
            }
        }
        if (z) {
            SlideView[] slideViewArr = this.views;
            final SlideView slideView = slideViewArr[this.currentViewNum];
            SlideView slideView2 = slideViewArr[i];
            this.currentViewNum = i;
            ImageView imageView = this.backButtonView;
            if (slideView2.needBackButton() || this.newAccount) {
                i2 = 0;
            }
            imageView.setVisibility(i2);
            slideView2.setParams(bundle, false);
            setParentActivityTitle(slideView2.getHeaderName());
            slideView2.onShow();
            int i3 = AndroidUtilities.displaySize.x;
            if (z2) {
                i3 = -i3;
            }
            slideView2.setX(i3);
            slideView2.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (LoginActivity.this.currentDoneType == 0 && z3) {
                        LoginActivity.this.showDoneButton(true, true);
                    }
                    slideView.setVisibility(8);
                    slideView.setX(0.0f);
                }
            });
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x;
            animatorArr[0] = ObjectAnimator.ofFloat(slideView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(slideView2, View.TRANSLATION_X, 0.0f);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300L);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            setCustomKeyboardVisible(slideView2.hasCustomKeyboard(), true);
            return;
        }
        this.backButtonView.setVisibility((this.views[i].needBackButton() || this.newAccount) ? 0 : 8);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = i;
        this.views[i].setParams(bundle, false);
        this.views[i].setVisibility(0);
        setParentActivityTitle(this.views[i].getHeaderName());
        this.views[i].onShow();
        setCustomKeyboardVisible(this.views[i].hasCustomKeyboard(), false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle bundle) {
        try {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("currentViewNum", this.currentViewNum);
            bundle2.putInt("syncContacts", this.syncContacts ? 1 : 0);
            for (int i = 0; i <= this.currentViewNum; i++) {
                SlideView slideView = this.views[i];
                if (slideView != null) {
                    slideView.saveStateParams(bundle2);
                }
            }
            SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            edit.clear();
            putBundleToEditor(bundle2, edit, null);
            edit.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void needFinishActivity(final boolean z, boolean z2, int i) {
        if (getParentActivity() != null) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), false);
        }
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false, new GenericProvider() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda18
                    @Override // org.telegram.messenger.GenericProvider
                    public final Object provide(Object obj) {
                        DialogsActivity lambda$needFinishActivity$16;
                        lambda$needFinishActivity$16 = LoginActivity.lambda$needFinishActivity$16(z, (Void) obj);
                        return lambda$needFinishActivity$16;
                    }
                });
                finishFragment();
                return;
            }
            if (z && z2) {
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(6, null);
                twoStepVerificationSetupActivity.setBlockingAlert(i);
                twoStepVerificationSetupActivity.setFromRegistration(true);
                presentFragment(twoStepVerificationSetupActivity, true);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("afterSignup", z);
                presentFragment(new DialogsActivity(bundle), true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        } else if (!(getParentActivity() instanceof ExternalActionActivity)) {
        } else {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ DialogsActivity lambda$needFinishActivity$16(boolean z, Void r2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", z);
        return new DialogsActivity(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization) {
        onAuthSuccess(tLRPC$TL_auth_authorization, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization, boolean z) {
        MessagesController.getInstance(this.currentAccount).cleanup();
        ConnectionsManager.getInstance(this.currentAccount).setUserId(tLRPC$TL_auth_authorization.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(tLRPC$TL_auth_authorization.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<TLRPC$User> arrayList = new ArrayList<>();
        arrayList.add(tLRPC$TL_auth_authorization.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(tLRPC$TL_auth_authorization.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkPromoInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        if (z) {
            MessagesController.getInstance(this.currentAccount).putDialogsEndReachedAfterRegistration();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, true);
        needFinishActivity(z, tLRPC$TL_auth_authorization.setup_password_required, tLRPC$TL_auth_authorization.otherwise_relogin_days);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillNextCodeParams(Bundle bundle, TLRPC$TL_account_sentEmailCode tLRPC$TL_account_sentEmailCode) {
        bundle.putString("emailPattern", tLRPC$TL_account_sentEmailCode.email_pattern);
        bundle.putInt("length", tLRPC$TL_account_sentEmailCode.length);
        setPage(13, true, bundle, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillNextCodeParams(Bundle bundle, TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode) {
        fillNextCodeParams(bundle, tLRPC$TL_auth_sentCode, true);
    }

    private void fillNextCodeParams(Bundle bundle, TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode, boolean z) {
        bundle.putString("phoneHash", tLRPC$TL_auth_sentCode.phone_code_hash);
        TLRPC$auth_CodeType tLRPC$auth_CodeType = tLRPC$TL_auth_sentCode.next_type;
        if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeMissedCall) {
            bundle.putInt("nextType", 11);
        }
        if (tLRPC$TL_auth_sentCode.type instanceof TLRPC$TL_auth_sentCodeTypeApp) {
            bundle.putInt("type", 1);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(1, z, bundle, false);
            return;
        }
        if (tLRPC$TL_auth_sentCode.timeout == 0) {
            tLRPC$TL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tLRPC$TL_auth_sentCode.timeout * 1000);
        TLRPC$auth_SentCodeType tLRPC$auth_SentCodeType = tLRPC$TL_auth_sentCode.type;
        if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeCall) {
            bundle.putInt("type", 4);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(4, z, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tLRPC$TL_auth_sentCode.type.pattern);
            setPage(3, z, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeSms) {
            bundle.putInt("type", 2);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(2, z, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeMissedCall) {
            bundle.putInt("type", 11);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            bundle.putString("prefix", tLRPC$TL_auth_sentCode.type.prefix);
            setPage(11, z, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeSetUpEmailRequired) {
            bundle.putBoolean("googleSignInAllowed", tLRPC$auth_SentCodeType.google_signin_allowed);
            setPage(12, z, bundle, false);
        } else if (!(tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeEmailCode)) {
        } else {
            bundle.putBoolean("googleSignInAllowed", tLRPC$auth_SentCodeType.google_signin_allowed);
            bundle.putString("emailPattern", tLRPC$TL_auth_sentCode.type.email_pattern);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            bundle.putInt("nextPhoneLoginDate", tLRPC$TL_auth_sentCode.type.next_phone_login_date);
            setPage(14, z, bundle, false);
        }
    }

    /* loaded from: classes3.dex */
    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener, NotificationCenter.NotificationCenterDelegate {
        private ImageView chevronRight;
        private View codeDividerView;
        private AnimatedPhoneNumberEditText codeField;
        private HashMap<String, CountrySelectActivity.Country> codesMap;
        private boolean confirmedNumber;
        private ArrayList<CountrySelectActivity.Country> countriesArray;
        private TextViewSwitcher countryButton;
        private OutlineTextContainerView countryOutlineView;
        private int countryState;
        private CountrySelectActivity.Country currentCountry;
        private boolean ignoreOnPhoneChange;
        private boolean ignoreOnTextChange;
        private boolean ignoreSelection;
        private boolean nextPressed;
        private boolean numberFilled;
        private AnimatedPhoneNumberEditText phoneField;
        private HashMap<String, List<String>> phoneFormatMap;
        private OutlineTextContainerView phoneOutlineView;
        private TextView plusTextView;
        private TextView subtitleView;
        private CheckBoxCell syncContactsBox;
        private CheckBoxCell testBackendCheckBox;
        private TextView titleView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public PhoneView(final Context context) {
            super(context);
            int i;
            this.countryState = 0;
            this.countriesArray = new ArrayList<>();
            this.codesMap = new HashMap<>();
            this.phoneFormatMap = new HashMap<>();
            this.ignoreSelection = false;
            this.ignoreOnTextChange = false;
            this.ignoreOnPhoneChange = false;
            this.nextPressed = false;
            this.confirmedNumber = false;
            setOrientation(1);
            setGravity(17);
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setText(LocaleController.getString(LoginActivity.this.activityMode == 2 ? R.string.ChangePhoneNewNumber : R.string.YourNumber));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 0.0f, 32.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setText(LocaleController.getString(LoginActivity.this.activityMode == 2 ? R.string.ChangePhoneHelp : R.string.StartText));
            this.subtitleView.setTextSize(1, 14.0f);
            this.subtitleView.setGravity(17);
            this.subtitleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.subtitleView, LayoutHelper.createLinear(-1, -2, 1, 32, 8, 32, 0));
            TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
            this.countryButton = textViewSwitcher;
            textViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda7
                @Override // android.widget.ViewSwitcher.ViewFactory
                public final View makeView() {
                    View lambda$new$0;
                    lambda$new$0 = LoginActivity.PhoneView.lambda$new$0(context);
                    return lambda$new$0;
                }
            });
            Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.text_in);
            loadAnimation.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setInAnimation(loadAnimation);
            ImageView imageView = new ImageView(context);
            this.chevronRight = imageView;
            imageView.setImageResource(R.drawable.msg_inputarrow);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            linearLayout.setGravity(16);
            linearLayout.addView(this.countryButton, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 0));
            linearLayout.addView(this.chevronRight, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.countryOutlineView = outlineTextContainerView;
            int i2 = R.string.Country;
            outlineTextContainerView.setText(LocaleController.getString(i2));
            this.countryOutlineView.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.countryOutlineView.setForceUseCenter(true);
            this.countryOutlineView.setFocusable(true);
            this.countryOutlineView.setContentDescription(LocaleController.getString(i2));
            this.countryOutlineView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    LoginActivity.PhoneView.this.lambda$new$1(view, z);
                }
            });
            addView(this.countryOutlineView, LayoutHelper.createLinear(-1, 58, 16.0f, 24.0f, 16.0f, 14.0f));
            this.countryOutlineView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.PhoneView.this.lambda$new$4(view);
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(0);
            OutlineTextContainerView outlineTextContainerView2 = new OutlineTextContainerView(context);
            this.phoneOutlineView = outlineTextContainerView2;
            outlineTextContainerView2.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 16, 16.0f, 8.0f, 16.0f, 8.0f));
            OutlineTextContainerView outlineTextContainerView3 = this.phoneOutlineView;
            int i3 = R.string.PhoneNumber;
            outlineTextContainerView3.setText(LocaleController.getString(i3));
            addView(this.phoneOutlineView, LayoutHelper.createLinear(-1, 58, 16.0f, 8.0f, 16.0f, 8.0f));
            TextView textView3 = new TextView(context);
            this.plusTextView = textView3;
            textView3.setText("+");
            this.plusTextView.setTextSize(1, 16.0f);
            this.plusTextView.setFocusable(false);
            linearLayout2.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2));
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = new AnimatedPhoneNumberEditText(context, LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.PhoneView.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onFocusChanged(boolean z, int i4, Rect rect) {
                    super.onFocusChanged(z, i4, rect);
                    PhoneView.this.phoneOutlineView.animateSelection((z || PhoneView.this.phoneField.isFocused()) ? 1.0f : 0.0f);
                    if (z) {
                        LoginActivity.this.keyboardView.setEditText(this);
                    }
                }
            };
            this.codeField = animatedPhoneNumberEditText;
            animatedPhoneNumberEditText.setInputType(3);
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 16.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(NUM);
            this.codeField.setBackground(null);
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 21) {
                this.codeField.setShowSoftInputOnFocus(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
            }
            this.codeField.setContentDescription(LocaleController.getString(R.string.LoginAccessibilityCountryCode));
            linearLayout2.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 0.0f, 0.0f));
            this.codeField.addTextChangedListener(new TextWatcher(LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.PhoneView.2
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    String str;
                    boolean z;
                    if (PhoneView.this.ignoreOnTextChange) {
                        return;
                    }
                    PhoneView.this.ignoreOnTextChange = true;
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                    PhoneView.this.codeField.setText(stripExceptNumbers);
                    if (stripExceptNumbers.length() == 0) {
                        PhoneView.this.setCountryButtonText(null);
                        PhoneView.this.phoneField.setHintText((String) null);
                        PhoneView.this.countryState = 1;
                    } else {
                        int i5 = 4;
                        if (stripExceptNumbers.length() > 4) {
                            while (true) {
                                if (i5 < 1) {
                                    str = null;
                                    z = false;
                                    break;
                                }
                                String substring = stripExceptNumbers.substring(0, i5);
                                if (((CountrySelectActivity.Country) PhoneView.this.codesMap.get(substring)) != null) {
                                    String str2 = stripExceptNumbers.substring(i5) + PhoneView.this.phoneField.getText().toString();
                                    PhoneView.this.codeField.setText(substring);
                                    z = true;
                                    str = str2;
                                    stripExceptNumbers = substring;
                                    break;
                                }
                                i5--;
                            }
                            if (!z) {
                                str = stripExceptNumbers.substring(1) + PhoneView.this.phoneField.getText().toString();
                                AnimatedPhoneNumberEditText animatedPhoneNumberEditText2 = PhoneView.this.codeField;
                                stripExceptNumbers = stripExceptNumbers.substring(0, 1);
                                animatedPhoneNumberEditText2.setText(stripExceptNumbers);
                            }
                        } else {
                            str = null;
                            z = false;
                        }
                        CountrySelectActivity.Country country = null;
                        int i6 = 0;
                        for (CountrySelectActivity.Country country2 : PhoneView.this.codesMap.values()) {
                            if (country2.code.startsWith(stripExceptNumbers)) {
                                i6++;
                                if (country2.code.equals(stripExceptNumbers)) {
                                    country = country2;
                                }
                            }
                        }
                        if (i6 == 1 && country != null && str == null) {
                            str = stripExceptNumbers.substring(country.code.length()) + PhoneView.this.phoneField.getText().toString();
                            AnimatedPhoneNumberEditText animatedPhoneNumberEditText3 = PhoneView.this.codeField;
                            String str3 = country.code;
                            animatedPhoneNumberEditText3.setText(str3);
                            stripExceptNumbers = str3;
                        }
                        CountrySelectActivity.Country country3 = (CountrySelectActivity.Country) PhoneView.this.codesMap.get(stripExceptNumbers);
                        if (country3 != null) {
                            PhoneView.this.ignoreSelection = true;
                            PhoneView.this.currentCountry = country3;
                            PhoneView.this.setCountryHint(stripExceptNumbers, country3);
                            PhoneView.this.countryState = 0;
                        } else {
                            PhoneView.this.setCountryButtonText(null);
                            PhoneView.this.phoneField.setHintText((String) null);
                            PhoneView.this.countryState = 2;
                        }
                        if (!z) {
                            PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                        }
                        if (str != null) {
                            PhoneView.this.phoneField.requestFocus();
                            PhoneView.this.phoneField.setText(str);
                            PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                        }
                    }
                    PhoneView.this.ignoreOnTextChange = false;
                }
            });
            this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView4, int i5, KeyEvent keyEvent) {
                    boolean lambda$new$5;
                    lambda$new$5 = LoginActivity.PhoneView.this.lambda$new$5(textView4, i5, keyEvent);
                    return lambda$new$5;
                }
            });
            this.codeDividerView = new View(context);
            LinearLayout.LayoutParams createLinear = LayoutHelper.createLinear(0, -1, 4.0f, 8.0f, 12.0f, 8.0f);
            createLinear.width = Math.max(2, AndroidUtilities.dp(0.5f));
            linearLayout2.addView(this.codeDividerView, createLinear);
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText2 = new AnimatedPhoneNumberEditText(context, LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.PhoneView.3
                @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
                public boolean onKeyDown(int i5, KeyEvent keyEvent) {
                    if (i5 == 67 && PhoneView.this.phoneField.length() == 0) {
                        PhoneView.this.codeField.requestFocus();
                        PhoneView.this.codeField.setSelection(PhoneView.this.codeField.length());
                        PhoneView.this.codeField.dispatchKeyEvent(keyEvent);
                    }
                    return super.onKeyDown(i5, keyEvent);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0 && !LoginActivity.this.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(motionEvent);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onFocusChanged(boolean z, int i5, Rect rect) {
                    super.onFocusChanged(z, i5, rect);
                    PhoneView.this.phoneOutlineView.animateSelection((z || PhoneView.this.codeField.isFocused()) ? 1.0f : 0.0f);
                    if (z) {
                        LoginActivity.this.keyboardView.setEditText(this);
                        LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
                        if (PhoneView.this.countryState != 2) {
                            return;
                        }
                        PhoneView.this.setCountryButtonText(LocaleController.getString(R.string.WrongCountry));
                    } else if (PhoneView.this.countryState != 2) {
                    } else {
                        PhoneView.this.setCountryButtonText(null);
                    }
                }
            };
            this.phoneField = animatedPhoneNumberEditText2;
            animatedPhoneNumberEditText2.setInputType(3);
            this.phoneField.setPadding(0, 0, 0, 0);
            this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.phoneField.setCursorWidth(1.5f);
            this.phoneField.setTextSize(1, 16.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(NUM);
            this.phoneField.setBackground(null);
            if (i4 >= 21) {
                this.phoneField.setShowSoftInputOnFocus(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
            }
            this.phoneField.setContentDescription(LocaleController.getString(i3));
            linearLayout2.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new TextWatcher(LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.PhoneView.4
                private int actionPosition;
                private int characterAction = -1;

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                    if (i6 == 0 && i7 == 1) {
                        this.characterAction = 1;
                    } else if (i6 == 1 && i7 == 0) {
                        if (charSequence.charAt(i5) == ' ' && i5 > 0) {
                            this.characterAction = 3;
                            this.actionPosition = i5 - 1;
                            return;
                        }
                        this.characterAction = 2;
                    } else {
                        this.characterAction = -1;
                    }
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    int i5;
                    int i6;
                    if (PhoneView.this.ignoreOnPhoneChange) {
                        return;
                    }
                    int selectionStart = PhoneView.this.phoneField.getSelectionStart();
                    String obj = PhoneView.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        obj = obj.substring(0, this.actionPosition) + obj.substring(this.actionPosition + 1);
                        selectionStart--;
                    }
                    StringBuilder sb = new StringBuilder(obj.length());
                    int i7 = 0;
                    while (i7 < obj.length()) {
                        int i8 = i7 + 1;
                        String substring = obj.substring(i7, i8);
                        if ("NUM".contains(substring)) {
                            sb.append(substring);
                        }
                        i7 = i8;
                    }
                    PhoneView.this.ignoreOnPhoneChange = true;
                    String hintText = PhoneView.this.phoneField.getHintText();
                    if (hintText != null) {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= sb.length()) {
                                break;
                            } else if (i9 < hintText.length()) {
                                if (hintText.charAt(i9) == ' ') {
                                    sb.insert(i9, ' ');
                                    i9++;
                                    if (selectionStart == i9 && (i6 = this.characterAction) != 2 && i6 != 3) {
                                        selectionStart++;
                                    }
                                }
                                i9++;
                            } else {
                                sb.insert(i9, ' ');
                                if (selectionStart == i9 + 1 && (i5 = this.characterAction) != 2 && i5 != 3) {
                                    selectionStart++;
                                }
                            }
                        }
                    }
                    editable.replace(0, editable.length(), sb);
                    if (selectionStart >= 0) {
                        PhoneView.this.phoneField.setSelection(Math.min(selectionStart, PhoneView.this.phoneField.length()));
                    }
                    PhoneView.this.phoneField.onTextChange();
                    PhoneView.this.ignoreOnPhoneChange = false;
                }
            });
            this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView4, int i5, KeyEvent keyEvent) {
                    boolean lambda$new$6;
                    lambda$new$6 = LoginActivity.PhoneView.this.lambda$new$6(textView4, i5, keyEvent);
                    return lambda$new$6;
                }
            });
            int i5 = 72;
            int i6 = 56;
            if (LoginActivity.this.newAccount && LoginActivity.this.activityMode == 0) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(context, 2);
                this.syncContactsBox = checkBoxCell;
                checkBoxCell.setText(LocaleController.getString("SyncContacts", R.string.SyncContacts), "", LoginActivity.this.syncContacts, false);
                addView(this.syncContactsBox, LayoutHelper.createLinear(-2, -1, 51, 16, 0, 16 + ((!LocaleController.isRTL || !AndroidUtilities.isSmallScreen()) ? 0 : i4 >= 21 ? 56 : 60), 0));
                i5 = 48;
                this.syncContactsBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LoginActivity.PhoneView.this.lambda$new$7(view);
                    }
                });
            }
            if (BuildVars.DEBUG_PRIVATE_VERSION && LoginActivity.this.activityMode == 0) {
                CheckBoxCell checkBoxCell2 = new CheckBoxCell(context, 2);
                this.testBackendCheckBox = checkBoxCell2;
                checkBoxCell2.setText("Test Backend", "", LoginActivity.this.testBackend, false);
                View view = this.testBackendCheckBox;
                if (!LocaleController.isRTL || !AndroidUtilities.isSmallScreen()) {
                    i = 16;
                    i6 = 0;
                } else if (i4 >= 21) {
                    i = 16;
                } else {
                    i = 16;
                    i6 = 60;
                }
                addView(view, LayoutHelper.createLinear(-2, -1, 51, 16, 0, i + i6, 0));
                i5 -= 24;
                this.testBackendCheckBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        LoginActivity.PhoneView.this.lambda$new$8(view2);
                    }
                });
            }
            if (i5 > 0 && !AndroidUtilities.isSmallScreen()) {
                Space space = new Space(context);
                space.setMinimumHeight(AndroidUtilities.dp(i5));
                addView(space, LayoutHelper.createLinear(-2, -2));
            }
            final HashMap hashMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    String[] split = readLine.split(";");
                    CountrySelectActivity.Country country = new CountrySelectActivity.Country();
                    country.name = split[2];
                    country.code = split[0];
                    country.shortname = split[1];
                    this.countriesArray.add(0, country);
                    this.codesMap.put(split[0], country);
                    if (split.length > 3) {
                        this.phoneFormatMap.put(split[0], Collections.singletonList(split[3]));
                    }
                    hashMap.put(split[1], split[2]);
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, Comparator$CC.comparing(LoginActivity$PhoneView$$ExternalSyntheticLambda17.INSTANCE));
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            LoginActivity.this.getAccountInstance().getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_help_getNearestDc
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i7, boolean z) {
                    return TLRPC$TL_nearestDc.TLdeserialize(abstractSerializedData, i7, z);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            }, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.PhoneView.this.lambda$new$11(hashMap, tLObject, tLRPC$TL_error);
                }
            }, 10);
            if (this.codeField.length() == 0) {
                setCountryButtonText(null);
                this.phoneField.setHintText((String) null);
                this.countryState = 1;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText3 = this.phoneField;
                animatedPhoneNumberEditText3.setSelection(animatedPhoneNumberEditText3.length());
            } else {
                this.codeField.requestFocus();
            }
            TLRPC$TL_help_getCountriesList tLRPC$TL_help_getCountriesList = new TLRPC$TL_help_getCountriesList();
            tLRPC$TL_help_getCountriesList.lang_code = "";
            LoginActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_help_getCountriesList, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.PhoneView.this.lambda$new$13(tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ View lambda$new$0(Context context) {
            TextView textView = new TextView(context);
            textView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            return textView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view, boolean z) {
            this.countryOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true, this.countriesArray);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda22
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    LoginActivity.PhoneView.this.lambda$new$3(country);
                }
            });
            LoginActivity.this.presentFragment(countrySelectActivity);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$new$2();
                }
            }, 300L);
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            LoginActivity.this.showKeyboard(this.phoneField);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$5(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                this.phoneField.requestFocus();
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$6(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                if (LoginActivity.this.phoneNumberConfirmView == null) {
                    lambda$onNextPressed$14(null);
                    return true;
                }
                LoginActivity.this.phoneNumberConfirmView.popupFabContainer.callOnClick();
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(View view) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.syncContacts = !loginActivity.syncContacts;
            ((CheckBoxCell) view).setChecked(LoginActivity.this.syncContacts, true);
            if (LoginActivity.this.syncContacts) {
                BulletinFactory.of(LoginActivity.this.slideViewsContainer, null).createSimpleBulletin(R.raw.contacts_sync_on, LocaleController.getString("SyncContactsOn", R.string.SyncContactsOn)).show();
            } else {
                BulletinFactory.of(LoginActivity.this.slideViewsContainer, null).createSimpleBulletin(R.raw.contacts_sync_off, LocaleController.getString("SyncContactsOff", R.string.SyncContactsOff)).show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(View view) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.testBackend = !loginActivity.testBackend;
            ((CheckBoxCell) view).setChecked(LoginActivity.this.testBackend, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$11(final HashMap hashMap, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$new$10(tLObject, hashMap);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10(TLObject tLObject, HashMap hashMap) {
            if (tLObject == null) {
                return;
            }
            TLRPC$TL_nearestDc tLRPC$TL_nearestDc = (TLRPC$TL_nearestDc) tLObject;
            if (this.codeField.length() != 0) {
                return;
            }
            setCountry(hashMap, tLRPC$TL_nearestDc.country.toUpperCase());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$13(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$new$12(tLRPC$TL_error, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$12(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            boolean z;
            if (tLRPC$TL_error == null) {
                this.countriesArray.clear();
                this.codesMap.clear();
                this.phoneFormatMap.clear();
                TLRPC$TL_help_countriesList tLRPC$TL_help_countriesList = (TLRPC$TL_help_countriesList) tLObject;
                for (int i = 0; i < tLRPC$TL_help_countriesList.countries.size(); i++) {
                    TLRPC$TL_help_country tLRPC$TL_help_country = tLRPC$TL_help_countriesList.countries.get(i);
                    for (int i2 = 0; i2 < tLRPC$TL_help_country.country_codes.size(); i2++) {
                        TLRPC$TL_help_countryCode tLRPC$TL_help_countryCode = tLRPC$TL_help_country.country_codes.get(i2);
                        if (tLRPC$TL_help_countryCode != null) {
                            CountrySelectActivity.Country country = new CountrySelectActivity.Country();
                            country.name = tLRPC$TL_help_country.default_name;
                            country.code = tLRPC$TL_help_countryCode.country_code;
                            country.shortname = tLRPC$TL_help_country.iso2;
                            this.countriesArray.add(country);
                            this.codesMap.put(tLRPC$TL_help_countryCode.country_code, country);
                            if (tLRPC$TL_help_countryCode.patterns.size() > 0) {
                                this.phoneFormatMap.put(tLRPC$TL_help_countryCode.country_code, tLRPC$TL_help_countryCode.patterns);
                            }
                        }
                    }
                }
                if (LoginActivity.this.activityMode != 2) {
                    return;
                }
                String stripExceptNumbers = PhoneFormat.stripExceptNumbers(UserConfig.getInstance(((BaseFragment) LoginActivity.this).currentAccount).getClientPhone());
                if (TextUtils.isEmpty(stripExceptNumbers)) {
                    return;
                }
                int i3 = 4;
                if (stripExceptNumbers.length() <= 4) {
                    return;
                }
                while (true) {
                    if (i3 < 1) {
                        z = false;
                        break;
                    }
                    String substring = stripExceptNumbers.substring(0, i3);
                    if (this.codesMap.get(substring) != null) {
                        this.codeField.setText(substring);
                        z = true;
                        break;
                    }
                    i3--;
                }
                if (z) {
                    return;
                }
                this.codeField.setText(stripExceptNumbers.substring(0, 1));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            for (int i = 0; i < this.countryButton.getChildCount(); i++) {
                TextView textView = (TextView) this.countryButton.getChildAt(i);
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            }
            this.chevronRight.setColorFilter(Theme.getColor("windowBackgroundWhiteHintText"));
            this.chevronRight.setBackground(Theme.createSelectorDrawable(LoginActivity.this.getThemedColor("listSelectorSDK21"), 1));
            this.plusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
            this.codeDividerView.setBackgroundColor(Theme.getColor("windowBackgroundWhiteInputField"));
            this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.phoneField.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setSquareCheckBoxColor("checkboxSquareUnchecked", "checkboxSquareBackground", "checkboxSquareCheck");
                this.syncContactsBox.updateTextColor();
            }
            CheckBoxCell checkBoxCell2 = this.testBackendCheckBox;
            if (checkBoxCell2 != null) {
                checkBoxCell2.setSquareCheckBoxColor("checkboxSquareUnchecked", "checkboxSquareBackground", "checkboxSquareCheck");
                this.testBackendCheckBox.updateTextColor();
            }
            this.phoneOutlineView.updateColor();
            this.countryOutlineView.updateColor();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void selectCountry(CountrySelectActivity.Country country) {
            this.ignoreOnTextChange = true;
            String str = country.code;
            this.codeField.setText(str);
            setCountryHint(str, country);
            this.currentCountry = country;
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCountryHint(String str, CountrySelectActivity.Country country) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            String languageFlag = LocaleController.getLanguageFlag(country.shortname);
            if (languageFlag != null) {
                spannableStringBuilder.append((CharSequence) languageFlag).append((CharSequence) " ");
                spannableStringBuilder.setSpan(new ReplacementSpan(this) { // from class: org.telegram.ui.LoginActivity.PhoneView.5
                    @Override // android.text.style.ReplacementSpan
                    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                    }

                    @Override // android.text.style.ReplacementSpan
                    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                        return AndroidUtilities.dp(16.0f);
                    }
                }, languageFlag.length(), languageFlag.length() + 1, 0);
            }
            spannableStringBuilder.append((CharSequence) country.name);
            setCountryButtonText(Emoji.replaceEmoji(spannableStringBuilder, this.countryButton.getCurrentView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            String str2 = null;
            if (this.phoneFormatMap.get(str) != null && !this.phoneFormatMap.get(str).isEmpty()) {
                String str3 = this.phoneFormatMap.get(str).get(0);
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                if (str3 != null) {
                    str2 = str3.replace('X', '0');
                }
                animatedPhoneNumberEditText.setHintText(str2);
                return;
            }
            this.phoneField.setHintText((String) null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCountryButtonText(CharSequence charSequence) {
            Animation loadAnimation = AnimationUtils.loadAnimation(ApplicationLoader.applicationContext, (this.countryButton.getCurrentView().getText() == null || charSequence != null) ? R.anim.text_out : R.anim.text_out_down);
            loadAnimation.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setOutAnimation(loadAnimation);
            CharSequence text = this.countryButton.getCurrentView().getText();
            this.countryButton.setText(charSequence, (!TextUtils.isEmpty(charSequence) || !TextUtils.isEmpty(text)) && !ObjectsCompat$$ExternalSyntheticBackport0.m(text, charSequence));
            this.countryOutlineView.animateSelection(charSequence != null ? 1.0f : 0.0f);
        }

        private void setCountry(HashMap<String, String> hashMap, String str) {
            if (hashMap.get(str) == null || this.countriesArray == null) {
                return;
            }
            CountrySelectActivity.Country country = null;
            int i = 0;
            while (true) {
                if (i < this.countriesArray.size()) {
                    if (this.countriesArray.get(i) != null && this.countriesArray.get(i).name.equals(str)) {
                        country = this.countriesArray.get(i);
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (country == null) {
                return;
            }
            this.codeField.setText(country.code);
            this.countryState = 0;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesArray.get(i).code);
            this.ignoreOnTextChange = false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:183:0x042f  */
        /* JADX WARN: Removed duplicated region for block: B:184:0x0439  */
        @Override // org.telegram.ui.Components.SlideView
        /* renamed from: onNextPressed */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void lambda$onNextPressed$14(final java.lang.String r19) {
            /*
                Method dump skipped, instructions count: 1353
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.lambda$onNextPressed$14(java.lang.String):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$15(final String str) {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$onNextPressed$14(str);
                }
            }, 200L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.LoginActivity$PhoneView$6  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass6 implements PhoneNumberConfirmView.IConfirmDialogCallback {
            final /* synthetic */ String val$code;

            AnonymousClass6(String str) {
                this.val$code = str;
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onFabPressed(PhoneNumberConfirmView phoneNumberConfirmView, TransformableLoginButtonView transformableLoginButtonView) {
                onConfirm(phoneNumberConfirmView);
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onEditPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView) {
                phoneNumberConfirmView.dismiss();
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onConfirmPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView) {
                onConfirm(phoneNumberConfirmView);
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onDismiss(PhoneNumberConfirmView phoneNumberConfirmView) {
                LoginActivity.this.phoneNumberConfirmView = null;
            }

            private void onConfirm(final PhoneNumberConfirmView phoneNumberConfirmView) {
                int i;
                PhoneView.this.confirmedNumber = true;
                LoginActivity.this.currentDoneType = 0;
                LoginActivity.this.needShowProgress(0, false);
                int i2 = Build.VERSION.SDK_INT;
                if (i2 >= 23 && AndroidUtilities.isSimAvailable()) {
                    boolean z = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean z2 = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    boolean z3 = i2 < 28 || LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                    boolean z4 = i2 < 26 || LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!z) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!z2) {
                            LoginActivity.this.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        if (!z3) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_CALL_LOG");
                        }
                        if (!z4 && i2 >= 26) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                        }
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                            if (!globalMainSettings.getBoolean("firstlogin", true) && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                try {
                                    LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[0]), 6);
                                    return;
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    return;
                                }
                            }
                            globalMainSettings.edit().putBoolean("firstlogin", false).commit();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                            if (!z && (!z2 || !z3)) {
                                builder.setMessage(LocaleController.getString("AllowReadCallAndLog", R.string.AllowReadCallAndLog));
                                i = R.raw.calls_log;
                            } else if (!z2 || !z3) {
                                builder.setMessage(LocaleController.getString("AllowReadCallLog", R.string.AllowReadCallLog));
                                i = R.raw.calls_log;
                            } else {
                                builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                i = R.raw.incoming_calls;
                            }
                            builder.setTopAnimation(i, 46, false, Theme.getColor("dialogTopBackground"));
                            LoginActivity loginActivity = LoginActivity.this;
                            loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                            PhoneView.this.confirmedNumber = true;
                            return;
                        }
                    }
                }
                final String str = this.val$code;
                phoneNumberConfirmView.animateProgress(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$6$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.PhoneView.AnonymousClass6.this.lambda$onConfirm$1(phoneNumberConfirmView, str);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onConfirm$1(final PhoneNumberConfirmView phoneNumberConfirmView, final String str) {
                phoneNumberConfirmView.dismiss();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.PhoneView.AnonymousClass6.this.lambda$onConfirm$0(str, phoneNumberConfirmView);
                    }
                }, 150L);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onConfirm$0(String str, PhoneNumberConfirmView phoneNumberConfirmView) {
                PhoneView.this.lambda$onNextPressed$14(str);
                LoginActivity.this.floatingProgressView.sync(phoneNumberConfirmView.floatingProgressView);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$16(int i, DialogInterface dialogInterface, int i2) {
            if (UserConfig.selectedAccount != i) {
                ((LaunchActivity) LoginActivity.this.getParentActivity()).switchToAccount(i, false);
            }
            LoginActivity.this.finishFragment();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$20(final Bundle bundle, final String str, final PhoneInputData phoneInputData, final TLObject tLObject, final TLObject tLObject2, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$onNextPressed$19(tLRPC$TL_error, bundle, tLObject2, str, phoneInputData, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, final String str, PhoneInputData phoneInputData, TLObject tLObject2) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                LoginActivity.this.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str2 = tLRPC$TL_error.text;
                if (str2 != null) {
                    if (str2.contains("SESSION_PASSWORD_NEEDED")) {
                        ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda20
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject3, TLRPC$TL_error tLRPC$TL_error2) {
                                LoginActivity.PhoneView.this.lambda$onNextPressed$18(str, tLObject3, tLRPC$TL_error2);
                            }
                        }, 10);
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.needShowInvalidAlert(LoginActivity.this, str, phoneInputData, false);
                    } else if (tLRPC$TL_error.text.contains("PHONE_PASSWORD_FLOOD")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_FLOOD")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("PhoneNumberFlood", R.string.PhoneNumberFlood));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_BANNED")) {
                        LoginActivity.needShowInvalidAlert(LoginActivity.this, str, phoneInputData, true);
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                    } else if (tLRPC$TL_error.code != -1000) {
                        AlertsCreator.processError(((BaseFragment) LoginActivity.this).currentAccount, tLRPC$TL_error, LoginActivity.this, tLObject2, phoneInputData.phoneNumber);
                    }
                }
            }
            LoginActivity.this.needHideProgress(false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$18(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$onNextPressed$17(tLRPC$TL_error, tLObject, str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$17(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            if (tLRPC$TL_error != null) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                return;
            }
            TLRPC$account_Password tLRPC$account_Password = (TLRPC$account_Password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$account_Password, true)) {
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            Bundle bundle = new Bundle();
            SerializedData serializedData = new SerializedData(tLRPC$account_Password.getObjectSize());
            tLRPC$account_Password.serializeToStream(serializedData);
            bundle.putString("password", Utilities.bytesToHex(serializedData.toByteArray()));
            bundle.putString("phoneFormated", str);
            LoginActivity.this.setPage(6, true, bundle, false);
        }

        /* JADX WARN: Removed duplicated region for block: B:28:0x0064 A[Catch: Exception -> 0x0164, TryCatch #0 {Exception -> 0x0164, blocks: (B:7:0x0010, B:9:0x0020, B:11:0x0028, B:17:0x003d, B:22:0x004d, B:26:0x0059, B:28:0x0064, B:31:0x0071, B:32:0x007a, B:34:0x0086, B:36:0x009e, B:37:0x00a4, B:40:0x00aa, B:44:0x00b8, B:46:0x00d2, B:49:0x00db, B:51:0x00e9, B:55:0x00fa, B:52:0x00f4, B:57:0x0109, B:58:0x011c, B:60:0x0126, B:61:0x0159), top: B:66:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:34:0x0086 A[Catch: Exception -> 0x0164, TryCatch #0 {Exception -> 0x0164, blocks: (B:7:0x0010, B:9:0x0020, B:11:0x0028, B:17:0x003d, B:22:0x004d, B:26:0x0059, B:28:0x0064, B:31:0x0071, B:32:0x007a, B:34:0x0086, B:36:0x009e, B:37:0x00a4, B:40:0x00aa, B:44:0x00b8, B:46:0x00d2, B:49:0x00db, B:51:0x00e9, B:55:0x00fa, B:52:0x00f4, B:57:0x0109, B:58:0x011c, B:60:0x0126, B:61:0x0159), top: B:66:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:69:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void fillNumber() {
            /*
                Method dump skipped, instructions count: 361
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.fillNumber():void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$fillNumber$21(List list) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (globalMainSettings.getBoolean("firstloginshow", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                globalMainSettings.edit().putBoolean("firstloginshow", false).commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                builder.setTopAnimation(R.raw.incoming_calls, 46, false, Theme.getColor("dialogTopBackground"));
                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                builder.setMessage(LocaleController.getString("AllowFillNumber", R.string.AllowFillNumber));
                LoginActivity loginActivity = LoginActivity.this;
                loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create(), true, null);
                LoginActivity.this.needRequestPermissions = true;
                return;
            }
            LoginActivity.this.getParentActivity().requestPermissions((String[]) list.toArray(new String[0]), 7);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setChecked(LoginActivity.this.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.lambda$onShow$22();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$22() {
            if (this.phoneField != null) {
                if (LoginActivity.this.needRequestPermissions) {
                    this.codeField.clearFocus();
                    this.phoneField.clearFocus();
                } else if (this.codeField.length() != 0) {
                    this.phoneField.requestFocus();
                    if (!this.numberFilled) {
                        AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                        animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                    }
                    LoginActivity.this.showKeyboard(this.phoneField);
                } else {
                    this.codeField.requestFocus();
                    LoginActivity.this.showKeyboard(this.codeField);
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("YourPhone", R.string.YourPhone);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("phoneview_code", obj);
            }
            String obj2 = this.phoneField.getText().toString();
            if (obj2.length() != 0) {
                bundle.putString("phoneview_phone", obj2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            String string = bundle.getString("phoneview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
            String string2 = bundle.getString("phoneview_phone");
            if (string2 != null) {
                this.phoneField.setText(string2);
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.emojiLoaded) {
                this.countryButton.getCurrentView().invalidate();
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private RLottieImageView blueImageView;
        private FrameLayout bottomContainer;
        private String catchedPhone;
        private CodeFieldContainer codeFieldContainer;
        private int codeTime;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private RLottieDrawable dotsDrawable;
        private RLottieDrawable dotsToStarsDrawable;
        private String emailPhone;
        private Runnable errorColorTimeout;
        private ViewSwitcher errorViewSwitcher;
        RLottieDrawable hintDrawable;
        private boolean isDotsAnimationVisible;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError;
        private int length;
        private ImageView missedCallArrowIcon;
        private TextView missedCallDescriptionSubtitle;
        private ImageView missedCallPhoneIcon;
        private boolean nextPressed;
        private int nextType;
        private int openTime;
        private String pattern;
        private String phone;
        private String phoneHash;
        private boolean postedErrorColorTimeout;
        private String prefix;
        private TextView prefixTextView;
        private FrameLayout problemFrame;
        private TextView problemText;
        private ProgressView progressView;
        private String requestPhone;
        private RLottieDrawable starsToDotsDrawable;
        private int time;
        private TextView timeText;
        private Timer timeTimer;
        private final Object timerSync;
        private TextView titleTextView;
        private boolean waitingForEvent;
        private TextView wrongCode;

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onBackPressed$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        static /* synthetic */ int access$8326(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = loginActivitySmsView.codeTime;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$8926(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = loginActivitySmsView.time;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.time = i;
            return i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            int i = 0;
            this.postedErrorColorTimeout = false;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i >= codeNumberFieldArr.length) {
                    break;
                }
                codeNumberFieldArr[i].animateErrorProgress(0.0f);
                i++;
            }
            if (this.errorViewSwitcher.getCurrentView() != this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:57:0x03c1  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x03e1  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x04d5  */
        /* JADX WARN: Removed duplicated region for block: B:72:0x04fd  */
        /* JADX WARN: Removed duplicated region for block: B:74:0x050c  */
        /* JADX WARN: Removed duplicated region for block: B:75:0x0531  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoginActivitySmsView(final android.content.Context r36, int r37) {
            /*
                Method dump skipped, instructions count: 1364
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            int i = this.nextType;
            if (i != 4 && i != 2 && i != 11) {
                if (i != 3) {
                    return;
                }
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                this.waitingForEvent = false;
                destroyCodeTimer();
                resendCode();
                return;
            }
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            int i2 = this.nextType;
            if (i2 == 4 || i2 == 11) {
                this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
            } else {
                this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
            }
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            createCodeTimer();
            TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda36
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivitySmsView.this.lambda$new$3(bundle, tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(Bundle bundle, TLObject tLObject) {
            LoginActivity.this.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(final Bundle bundle, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda25
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.lambda$new$1(bundle, tLObject);
                    }
                });
            } else if (tLRPC$TL_error == null || tLRPC$TL_error.text == null) {
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.lambda$new$2(tLRPC$TL_error);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(TLRPC$TL_error tLRPC$TL_error) {
            this.lastError = tLRPC$TL_error.text;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(Context context, View view) {
            if (this.nextPressed) {
                return;
            }
            if (!(this.nextType == 0)) {
                if (LoginActivity.this.radialProgressView.getTag() != null) {
                    return;
                }
                resendCode();
                return;
            }
            new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DidNotGetTheCodeInfo", R.string.DidNotGetTheCodeInfo, this.phone))).setNeutralButton(LocaleController.getString(R.string.DidNotGetTheCodeHelpButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivitySmsView.this.lambda$new$5(dialogInterface, i);
                }
            }).setPositiveButton(LocaleController.getString(R.string.Close), null).setNegativeButton(LocaleController.getString(R.string.DidNotGetTheCodeEditNumberButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivitySmsView.this.lambda$new$6(dialogInterface, i);
                }
            }).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(DialogInterface dialogInterface, int i) {
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String format = String.format(Locale.US, "%s (%d)", packageInfo.versionName, Integer.valueOf(packageInfo.versionCode));
                Intent intent = new Intent("android.intent.action.SENDTO");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + this.emailPhone);
                intent.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                getContext().startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (Exception unused) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(DialogInterface dialogInterface, int i) {
            LoginActivity.this.setPage(0, true, null, true);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            String str = "windowBackgroundWhiteGrayText6";
            this.confirmTextView.setTextColor(Theme.getColor(LoginActivity.this.isInCancelAccountDeletionMode() ? "windowBackgroundWhiteBlackText" : str));
            this.confirmTextView.setLinkTextColor(Theme.getColor("chats_actionBackground"));
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            if (this.currentType == 11) {
                this.missedCallDescriptionSubtitle.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                this.missedCallArrowIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), PorterDuff.Mode.SRC_IN));
                this.missedCallPhoneIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.SRC_IN));
                this.prefixTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
            applyLottieColors(this.hintDrawable);
            applyLottieColors(this.starsToDotsDrawable);
            applyLottieColors(this.dotsDrawable);
            applyLottieColors(this.dotsToStarsDrawable);
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null) {
                codeFieldContainer.invalidate();
            }
            String str2 = (String) this.timeText.getTag();
            if (str2 != null) {
                str = str2;
            }
            this.timeText.setTextColor(Theme.getColor(str));
            this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.wrongCode.setTextColor(Theme.getColor("dialogTextRed"));
        }

        private void applyLottieColors(RLottieDrawable rLottieDrawable) {
            if (rLottieDrawable != null) {
                rLottieDrawable.setLayerColor("Bubble.**", Theme.getColor("chats_actionBackground"));
                rLottieDrawable.setLayerColor("Phone.**", Theme.getColor("windowBackgroundWhiteBlackText"));
                rLottieDrawable.setLayerColor("Note.**", Theme.getColor("windowBackgroundWhiteBlackText"));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return this.currentType != 3;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            tryShowProgress(ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda35
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivitySmsView.this.lambda$resendCode$9(bundle, tLObject, tLRPC$TL_error);
                }
            }, 10));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$9(final Bundle bundle, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$resendCode$8(tLRPC$TL_error, bundle, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$8(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                LoginActivity.this.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str = tLRPC$TL_error.text;
                if (str != null) {
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        onBackPressed(true);
                        LoginActivity.this.setPage(0, true, null, true);
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                    } else if (tLRPC$TL_error.code != -1000) {
                        LoginActivity loginActivity = LoginActivity.this;
                        String string = LocaleController.getString(R.string.RestorePasswordNoEmailTitle);
                        loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + tLRPC$TL_error.text);
                    }
                }
            }
            tryHideProgress(false);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            CodeNumberField[] codeNumberFieldArr;
            super.onConfigurationChanged(configuration);
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer == null || (codeNumberFieldArr = codeFieldContainer.codeField) == null) {
                return;
            }
            for (CodeNumberField codeNumberField : codeNumberFieldArr) {
                if (Build.VERSION.SDK_INT >= 21) {
                    codeNumberField.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
                }
            }
        }

        private void tryShowProgress(int i) {
            lambda$tryShowProgress$10(i, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: tryShowProgress */
        public void lambda$tryShowProgress$10(final int i, final boolean z) {
            if (this.starsToDotsDrawable == null) {
                LoginActivity.this.needShowProgress(i, z);
            } else if (this.isDotsAnimationVisible) {
            } else {
                this.isDotsAnimationVisible = true;
                if (this.hintDrawable.getCurrentFrame() != this.hintDrawable.getFramesCount() - 1) {
                    this.hintDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.lambda$tryShowProgress$11(i, z);
                        }
                    });
                    return;
                }
                this.starsToDotsDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.lambda$tryShowProgress$13();
                    }
                });
                this.blueImageView.setAutoRepeat(false);
                this.starsToDotsDrawable.setCurrentFrame(0, false);
                this.blueImageView.setAnimation(this.starsToDotsDrawable);
                this.blueImageView.playAnimation();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryShowProgress$11(final int i, final boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$tryShowProgress$10(i, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryShowProgress$13() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$tryShowProgress$12();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryShowProgress$12() {
            this.blueImageView.setAutoRepeat(true);
            this.dotsDrawable.setCurrentFrame(0, false);
            this.dotsDrawable.setAutoRepeat(1);
            this.blueImageView.setAnimation(this.dotsDrawable);
            this.blueImageView.playAnimation();
        }

        private void tryHideProgress(boolean z) {
            tryHideProgress(z, true);
        }

        private void tryHideProgress(boolean z, boolean z2) {
            if (this.starsToDotsDrawable == null) {
                LoginActivity.this.needHideProgress(z, z2);
            } else if (!this.isDotsAnimationVisible) {
            } else {
                this.isDotsAnimationVisible = false;
                this.blueImageView.setAutoRepeat(false);
                this.dotsDrawable.setAutoRepeat(0);
                this.dotsDrawable.setOnFinishCallback(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.lambda$tryHideProgress$17();
                    }
                }, this.dotsDrawable.getFramesCount() - 1);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$17() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$tryHideProgress$16();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$15() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$tryHideProgress$14();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$16() {
            this.dotsToStarsDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$tryHideProgress$15();
                }
            });
            this.blueImageView.setAutoRepeat(false);
            this.dotsToStarsDrawable.setCurrentFrame(0, false);
            this.blueImageView.setAnimation(this.dotsToStarsDrawable);
            this.blueImageView.playAnimation();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$14() {
            this.blueImageView.setAutoRepeat(false);
            this.blueImageView.setAnimation(this.hintDrawable);
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            int i = this.currentType;
            if (i == 3 || i == 11) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        /* JADX WARN: Removed duplicated region for block: B:94:0x02ca  */
        /* JADX WARN: Removed duplicated region for block: B:95:0x02d3  */
        @Override // org.telegram.ui.Components.SlideView
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void setParams(android.os.Bundle r14, boolean r15) {
            /*
                Method dump skipped, instructions count: 883
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.setParams(android.os.Bundle, boolean):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setParams$18(View view, boolean z) {
            if (z) {
                LoginActivity.this.keyboardView.setEditText((EditText) view);
                LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setProblemTextVisible(boolean z) {
            float f = z ? 1.0f : 0.0f;
            if (this.problemText.getAlpha() != f) {
                this.problemText.animate().cancel();
                this.problemText.animate().alpha(f).setDuration(150L).start();
            }
        }

        private void createCodeTimer() {
            if (this.codeTimer != null) {
                return;
            }
            this.codeTime = 15000;
            this.codeTimer = new Timer();
            this.lastCodeTime = System.currentTimeMillis();
            this.codeTimer.schedule(new AnonymousClass5(), 0L, 1000L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$5  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass5 extends TimerTask {
            AnonymousClass5() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.AnonymousClass5.this.lambda$run$0();
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                double currentTimeMillis = System.currentTimeMillis();
                double d = LoginActivitySmsView.this.lastCodeTime;
                Double.isNaN(currentTimeMillis);
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                LoginActivitySmsView.access$8326(LoginActivitySmsView.this, currentTimeMillis - d);
                if (LoginActivitySmsView.this.codeTime <= 1000) {
                    LoginActivitySmsView.this.setProblemTextVisible(true);
                    LoginActivitySmsView.this.timeText.setVisibility(8);
                    LoginActivitySmsView.this.destroyCodeTimer();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.codeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer != null) {
                return;
            }
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.timeText.setTag(R.id.color_key_tag, "windowBackgroundWhiteGrayText6");
            Timer timer = new Timer();
            this.timeTimer = timer;
            timer.schedule(new AnonymousClass6(), 0L, 1000L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$6  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass6 extends TimerTask {
            AnonymousClass6() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (LoginActivitySmsView.this.timeTimer == null) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.AnonymousClass6.this.lambda$run$0();
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                double currentTimeMillis = System.currentTimeMillis();
                double d = LoginActivitySmsView.this.lastCurrentTime;
                Double.isNaN(currentTimeMillis);
                LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                LoginActivitySmsView.access$8926(LoginActivitySmsView.this, currentTimeMillis - d);
                if (LoginActivitySmsView.this.time >= 1000) {
                    int i = (LoginActivitySmsView.this.time / 1000) / 60;
                    int i2 = (LoginActivitySmsView.this.time / 1000) - (i * 60);
                    if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 11) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallAvailableIn", R.string.CallAvailableIn, Integer.valueOf(i), Integer.valueOf(i2)));
                    } else if (LoginActivitySmsView.this.nextType == 2) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsAvailableIn", R.string.SmsAvailableIn, Integer.valueOf(i), Integer.valueOf(i2)));
                    }
                    ProgressView unused = LoginActivitySmsView.this.progressView;
                    return;
                }
                LoginActivitySmsView.this.destroyTimer();
                if (LoginActivitySmsView.this.nextType != 3 && LoginActivitySmsView.this.nextType != 4 && LoginActivitySmsView.this.nextType != 2 && LoginActivitySmsView.this.nextType != 11) {
                    return;
                }
                if (LoginActivitySmsView.this.nextType == 4) {
                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestCallButton", R.string.RequestCallButton));
                } else if (LoginActivitySmsView.this.nextType == 11 || LoginActivitySmsView.this.nextType == 3) {
                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestMissedCall", R.string.RequestMissedCall));
                } else {
                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestSmsButton", R.string.RequestSmsButton));
                }
                LoginActivitySmsView.this.timeText.setTextColor(Theme.getColor("chats_actionBackground"));
                LoginActivitySmsView.this.timeText.setTag(R.id.color_key_tag, "chats_actionBackground");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void destroyTimer() {
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.timeText.setTag(R.id.color_key_tag, "windowBackgroundWhiteGrayText6");
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (LoginActivity.this.currentViewNum == 11) {
                if (this.nextPressed) {
                    return;
                }
            } else if (this.nextPressed || LoginActivity.this.currentViewNum < 1 || LoginActivity.this.currentViewNum > 4) {
                return;
            }
            if (str == null) {
                str = this.codeFieldContainer.getCode();
            }
            int i = 0;
            if (TextUtils.isEmpty(str)) {
                LoginActivity.this.onFieldError(this.codeFieldContainer, false);
            } else if (LoginActivity.this.currentViewNum >= 1 && LoginActivity.this.currentViewNum <= 4 && this.codeFieldContainer.isFocusSuppressed) {
            } else {
                this.nextPressed = true;
                int i2 = this.currentType;
                if (i2 == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                int i3 = LoginActivity.this.activityMode;
                if (i3 == 1) {
                    this.requestPhone = LoginActivity.this.cancelDeletionPhone;
                    final TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone = new TLRPC$TL_account_confirmPhone();
                    tLRPC$TL_account_confirmPhone.phone_code = str;
                    tLRPC$TL_account_confirmPhone.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
                    codeFieldContainer.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr = codeFieldContainer.codeField;
                    int length = codeNumberFieldArr.length;
                    while (i < length) {
                        codeNumberFieldArr[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    tryShowProgress(ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_account_confirmPhone, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda37
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$26(tLRPC$TL_account_confirmPhone, tLObject, tLRPC$TL_error);
                        }
                    }, 2));
                } else if (i3 == 2) {
                    TLRPC$TL_account_changePhone tLRPC$TL_account_changePhone = new TLRPC$TL_account_changePhone();
                    tLRPC$TL_account_changePhone.phone_number = this.requestPhone;
                    tLRPC$TL_account_changePhone.phone_code = str;
                    tLRPC$TL_account_changePhone.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                    codeFieldContainer2.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr2 = codeFieldContainer2.codeField;
                    int length2 = codeNumberFieldArr2.length;
                    while (i < length2) {
                        codeNumberFieldArr2[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    lambda$tryShowProgress$10(ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_account_changePhone, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$22(tLObject, tLRPC$TL_error);
                        }
                    }, 2), true);
                    LoginActivity.this.showDoneButton(true, true);
                } else {
                    final TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn = new TLRPC$TL_auth_signIn();
                    tLRPC$TL_auth_signIn.phone_number = this.requestPhone;
                    tLRPC$TL_auth_signIn.phone_code = str;
                    tLRPC$TL_auth_signIn.phone_code_hash = this.phoneHash;
                    tLRPC$TL_auth_signIn.flags |= 1;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer3 = this.codeFieldContainer;
                    codeFieldContainer3.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr3 = codeFieldContainer3.codeField;
                    int length3 = codeNumberFieldArr3.length;
                    while (i < length3) {
                        codeNumberFieldArr3[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    lambda$tryShowProgress$10(ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_signIn, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$33(tLRPC$TL_auth_signIn, tLObject, tLRPC$TL_error);
                        }
                    }, 10), true);
                    LoginActivity.this.showDoneButton(true, true);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$22(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$21(tLRPC$TL_error, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:46:0x0173  */
        /* JADX WARN: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$onNextPressed$21(org.telegram.tgnet.TLRPC$TL_error r8, org.telegram.tgnet.TLObject r9) {
            /*
                Method dump skipped, instructions count: 397
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.lambda$onNextPressed$21(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$20() {
            try {
                ((BaseFragment) LoginActivity.this).fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.YourPasswordSuccess)).setMessage(LocaleController.getString(R.string.ChangePhoneNumberSuccess)).setPositiveButton(LocaleController.getString(R.string.OK), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$19(dialogInterface);
                }
            }).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(DialogInterface dialogInterface) {
            LoginActivity.this.finishFragment();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$26(final TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$25(tLRPC$TL_error, tLRPC$TL_account_confirmPhone);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$25(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone) {
            int i;
            int i2;
            tryHideProgress(false);
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$24();
                    }
                });
                return;
            }
            this.lastError = tLRPC$TL_error.text;
            int i3 = this.currentType;
            if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                createTimer();
            }
            int i4 = this.currentType;
            if (i4 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i4 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.currentType != 3) {
                AlertsCreator.processError(((BaseFragment) LoginActivity.this).currentAccount, tLRPC$TL_error, LoginActivity.this, tLRPC$TL_account_confirmPhone, new Object[0]);
            }
            if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                shakeWrongCode();
            } else if (!tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
            } else {
                onBackPressed(true);
                LoginActivity.this.setPage(0, true, null, true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$24() {
            AlertDialog.Builder title = new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString(R.string.CancelLinkSuccessTitle));
            int i = R.string.CancelLinkSuccess;
            PhoneFormat phoneFormat = PhoneFormat.getInstance();
            title.setMessage(LocaleController.formatString("CancelLinkSuccess", i, phoneFormat.format("+" + this.phone))).setPositiveButton(LocaleController.getString(R.string.Close), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$23(dialogInterface);
                }
            }).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$23(DialogInterface dialogInterface) {
            LoginActivity.this.finishFragment();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$33(final TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$32(tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:53:0x0170  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$onNextPressed$32(org.telegram.tgnet.TLRPC$TL_error r6, final org.telegram.tgnet.TLObject r7, final org.telegram.tgnet.TLRPC$TL_auth_signIn r8) {
            /*
                Method dump skipped, instructions count: 403
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.lambda$onNextPressed$32(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_auth_signIn):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$27(Bundle bundle) {
            LoginActivity.this.setPage(5, true, bundle, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$28(TLObject tLObject) {
            LoginActivity.this.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$31(final TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$30(tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$30(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn) {
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            if (tLRPC$TL_error != null) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                return;
            }
            TLRPC$account_Password tLRPC$account_Password = (TLRPC$account_Password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$account_Password, true)) {
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            final Bundle bundle = new Bundle();
            SerializedData serializedData = new SerializedData(tLRPC$account_Password.getObjectSize());
            tLRPC$account_Password.serializeToStream(serializedData);
            bundle.putString("password", Utilities.bytesToHex(serializedData.toByteArray()));
            bundle.putString("phoneFormated", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("code", tLRPC$TL_auth_signIn.phone_code);
            animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onNextPressed$29(bundle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$29(Bundle bundle) {
            LoginActivity.this.setPage(6, true, bundle, false);
        }

        private void animateSuccess(final Runnable runnable) {
            final int i = 0;
            while (true) {
                CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
                if (i < codeFieldContainer.codeField.length) {
                    codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda20
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.lambda$animateSuccess$34(i);
                        }
                    }, i * 75);
                    i++;
                } else {
                    codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda26
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.lambda$animateSuccess$35(runnable);
                        }
                    }, (this.codeFieldContainer.codeField.length * 75) + 400);
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateSuccess$34(int i) {
            this.codeFieldContainer.codeField[i].animateSuccessProgress(1.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateSuccess$35(Runnable runnable) {
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateSuccessProgress(0.0f);
                    i++;
                } else {
                    runnable.run();
                    this.codeFieldContainer.isFocusSuppressed = false;
                    return;
                }
            }
        }

        private void shakeWrongCode() {
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i >= codeNumberFieldArr.length) {
                    break;
                }
                codeNumberFieldArr[i].setText("");
                this.codeFieldContainer.codeField[i].animateErrorProgress(1.0f);
                i++;
            }
            if (this.errorViewSwitcher.getCurrentView() == this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, this.currentType == 11 ? 3.5f : 10.0f, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$shakeWrongCode$37();
                }
            });
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 5000L);
            this.postedErrorColorTimeout = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$37() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$shakeWrongCode$36();
                }
            }, 150L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$36() {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer.isFocusSuppressed = false;
            codeFieldContainer.codeField[0].requestFocus();
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateErrorProgress(0.0f);
                    i++;
                } else {
                    return;
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            if (LoginActivity.this.activityMode != 0) {
                LoginActivity.this.finishFragment();
                return false;
            } else if (!z) {
                LoginActivity loginActivity = LoginActivity.this;
                loginActivity.showDialog(new AlertDialog.Builder(loginActivity.getParentActivity()).setTitle(LocaleController.getString(R.string.EditNumber)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditNumberInfo", R.string.EditNumberInfo, this.phone))).setPositiveButton(LocaleController.getString(R.string.Close), null).setNegativeButton(LocaleController.getString(R.string.Edit), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivitySmsView.this.lambda$onBackPressed$38(dialogInterface, i);
                    }
                }).create());
                return false;
            } else {
                this.nextPressed = false;
                tryHideProgress(true);
                TLRPC$TL_auth_cancelCode tLRPC$TL_auth_cancelCode = new TLRPC$TL_auth_cancelCode();
                tLRPC$TL_auth_cancelCode.phone_number = this.requestPhone;
                tLRPC$TL_auth_cancelCode.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_cancelCode, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda40.INSTANCE, 10);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                int i = this.currentType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$38(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onDestroyActivity() {
            super.onDestroyActivity();
            int i = this.currentType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCurrentFrame(0);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.lambda$onShow$40();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$40() {
            CodeNumberField[] codeNumberFieldArr;
            if (this.currentType != 3 && (codeNumberFieldArr = this.codeFieldContainer.codeField) != null) {
                for (int length = codeNumberFieldArr.length - 1; length >= 0; length--) {
                    if (length == 0 || this.codeFieldContainer.codeField[length].length() != 0) {
                        this.codeFieldContainer.codeField[length].requestFocus();
                        CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                        codeNumberFieldArr2[length].setSelection(codeNumberFieldArr2[length].length());
                        LoginActivity.this.showKeyboard(this.codeFieldContainer.codeField[length]);
                        break;
                    }
                }
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (this.waitingForEvent) {
                CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
                if (codeFieldContainer.codeField == null) {
                    return;
                }
                if (i == NotificationCenter.didReceiveSmsCode) {
                    codeFieldContainer.setText("" + objArr[0]);
                    onNextPressed(null);
                } else if (i != NotificationCenter.didReceiveCall) {
                } else {
                    String str = "" + objArr[0];
                    if (!AndroidUtilities.checkPhonePattern(this.pattern, str)) {
                        return;
                    }
                    if (!this.pattern.equals("*")) {
                        this.catchedPhone = str;
                        AndroidUtilities.endIncomingCall();
                    }
                    onNextPressed(str);
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (code.length() != 0) {
                bundle.putString("smsview_code_" + this.currentType, code);
            }
            String str = this.catchedPhone;
            if (str != null) {
                bundle.putString("catchedPhone", str);
            }
            if (this.currentParams != null) {
                bundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
            }
            int i = this.time;
            if (i != 0) {
                bundle.putInt("time", i);
            }
            int i2 = this.openTime;
            if (i2 != 0) {
                bundle.putInt("open", i2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("smsview_params_" + this.currentType);
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("catchedPhone");
            if (string != null) {
                this.catchedPhone = string;
            }
            String string2 = bundle.getString("smsview_code_" + this.currentType);
            if (string2 != null) {
                CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
                if (codeFieldContainer.codeField != null) {
                    codeFieldContainer.setText(string2);
                }
            }
            int i = bundle.getInt("time");
            if (i != 0) {
                this.time = i;
            }
            int i2 = bundle.getInt("open");
            if (i2 != 0) {
                this.openTime = i2;
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC$account_Password currentPassword;
        private RLottieImageView lockImageView;
        private boolean nextPressed;
        private OutlineTextContainerView outlineCodeField;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView titleView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x0142  */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0144  */
        /* JADX WARN: Removed duplicated region for block: B:17:0x01c9  */
        /* JADX WARN: Removed duplicated region for block: B:18:0x01cc  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoginActivityPasswordView(final android.content.Context r20) {
            /*
                Method dump skipped, instructions count: 502
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view, boolean z) {
            this.outlineCodeField.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(Context context, View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            if (this.currentPassword.has_recovery) {
                LoginActivity.this.needShowProgress(0);
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LoginActivity.LoginActivityPasswordView.this.lambda$new$4(tLObject, tLRPC$TL_error);
                    }
                }, 10);
                return;
            }
            AndroidUtilities.hideKeyboard(this.codeField);
            new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.RestorePasswordNoEmailTitle)).setMessage(LocaleController.getString(R.string.RestorePasswordNoEmailText)).setPositiveButton(LocaleController.getString(R.string.Close), null).setNegativeButton(LocaleController.getString(R.string.ResetAccount), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityPasswordView.this.lambda$new$5(dialogInterface, i);
                }
            }).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.lambda$new$3(tLRPC$TL_error, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            String formatPluralString;
            LoginActivity.this.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                final TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery = (TLRPC$TL_auth_passwordRecovery) tLObject;
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                String str = tLRPC$TL_auth_passwordRecovery.email_pattern;
                SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str);
                int indexOf = str.indexOf(42);
                int lastIndexOf = str.lastIndexOf(42);
                if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i = lastIndexOf + 1;
                    textStyleRun.end = i;
                    valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i, 0);
                }
                builder.setMessage(AndroidUtilities.formatSpannable(LocaleController.getString(R.string.RestoreEmailSent), valueOf));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", R.string.RestoreEmailSentTitle));
                builder.setPositiveButton(LocaleController.getString(R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        LoginActivity.LoginActivityPasswordView.this.lambda$new$2(tLRPC$TL_auth_passwordRecovery, dialogInterface, i2);
                    }
                });
                Dialog showDialog = LoginActivity.this.showDialog(builder.create());
                if (showDialog == null) {
                    return;
                }
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            } else if (!tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
            } else {
                int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                }
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.WrongCodeTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", tLRPC$TL_auth_passwordRecovery.email_pattern);
            bundle.putString("password", this.passwordString);
            bundle.putString("requestPhone", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("phoneCode", this.phoneCode);
            LoginActivity.this.setPage(7, true, bundle, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(DialogInterface dialogInterface, int i) {
            LoginActivity.this.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.outlineCodeField.updateColor();
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            if (bundle.isEmpty()) {
                AndroidUtilities.hideKeyboard(this.codeField);
                return;
            }
            this.codeField.setText("");
            this.currentParams = bundle;
            String string = bundle.getString("password");
            this.passwordString = string;
            if (string != null) {
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
                this.currentPassword = TLRPC$account_Password.TLdeserialize(serializedData, serializedData.readInt32(false), false);
            }
            this.requestPhone = bundle.getString("phoneFormated");
            this.phoneHash = bundle.getString("phoneHash");
            this.phoneCode = bundle.getString("code");
            TLRPC$account_Password tLRPC$account_Password = this.currentPassword;
            if (tLRPC$account_Password != null && !TextUtils.isEmpty(tLRPC$account_Password.hint)) {
                this.codeField.setHint(this.currentPassword.hint);
            } else {
                this.codeField.setHint((CharSequence) null);
            }
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            if (z) {
                this.codeField.setText("");
            }
            LoginActivity.this.onFieldError(this.outlineCodeField, true);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (this.nextPressed) {
                return;
            }
            final String obj = this.codeField.getText().toString();
            if (obj.length() == 0) {
                onPasscodeError(false);
                return;
            }
            this.nextPressed = true;
            LoginActivity.this.needShowProgress(0);
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$12(obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$12(String str) {
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
            boolean z = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
            byte[] x = z ? SRPHelper.getX(AndroidUtilities.getStringBytes(str), (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
            TLRPC$TL_auth_checkPassword tLRPC$TL_auth_checkPassword = new TLRPC$TL_auth_checkPassword();
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$11(tLObject, tLRPC$TL_error);
                }
            };
            if (z) {
                TLRPC$account_Password tLRPC$account_Password = this.currentPassword;
                TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$account_Password.srp_id, tLRPC$account_Password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                tLRPC$TL_auth_checkPassword.password = startCheck;
                if (startCheck != null) {
                    ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_checkPassword, requestDelegate, 10);
                    return;
                }
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "PASSWORD_HASH_INVALID";
                requestDelegate.run(null, tLRPC$TL_error);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$11(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$10(tLRPC$TL_error, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$10(TLRPC$TL_error tLRPC$TL_error, final TLObject tLObject) {
            String formatPluralString;
            this.nextPressed = false;
            if (tLRPC$TL_error != null && "SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                        LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$8(tLObject2, tLRPC$TL_error2);
                    }
                }, 8);
            } else if (tLObject instanceof TLRPC$TL_auth_authorization) {
                LoginActivity.this.showDoneButton(false, true);
                postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$9(tLObject);
                    }
                }, 150L);
            } else {
                LoginActivity.this.needHideProgress(false);
                if (tLRPC$TL_error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else if (!tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                } else {
                    int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        formatPluralString = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                    } else {
                        formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                    }
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString));
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.lambda$onNextPressed$7(tLRPC$TL_error, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            if (tLRPC$TL_error == null) {
                this.currentPassword = (TLRPC$account_Password) tLObject;
                onNextPressed(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$9(TLObject tLObject) {
            LoginActivity.this.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.codeField);
            LoginActivity.this.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            this.nextPressed = false;
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.lambda$onShow$13();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$13() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                LoginActivity.this.showKeyboard(this.codeField);
                this.lockImageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.lockImageView.playAnimation();
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("passview_code", obj);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("passview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("passview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("passview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityResetWaitView extends SlideView {
        private TextView confirmTextView;
        private Bundle currentParams;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;
        private TextView resetAccountTime;
        private int startTime;
        private Runnable timeRunnable;
        private TextView titleView;
        private RLottieImageView waitImageView;
        private int waitTime;
        private Boolean wasResetButtonActive;

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        public LoginActivityResetWaitView(Context context) {
            super(context);
            setOrientation(1);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            linearLayout.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.waitImageView = rLottieImageView;
            rLottieImageView.setAutoRepeat(true);
            this.waitImageView.setAnimation(R.raw.sandclock, 120, 120);
            frameLayout.addView(this.waitImageView, LayoutHelper.createFrame(120, 120, 1));
            Point point = AndroidUtilities.displaySize;
            frameLayout.setVisibility((point.x <= point.y || AndroidUtilities.isTablet()) ? 0 : 8);
            linearLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -2, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextView textView2 = this.titleView;
            int i = R.string.ResetAccount;
            textView2.setText(LocaleController.getString(i));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            linearLayout.addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 16.0f, 32.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.confirmTextView = textView3;
            textView3.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(1);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            linearLayout.addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 12, 8, 12, 0));
            addView(linearLayout, LayoutHelper.createLinear(-1, 0, 1.0f));
            TextView textView4 = new TextView(context);
            this.resetAccountText = textView4;
            textView4.setGravity(1);
            this.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", R.string.ResetAccountStatus));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, 49, 0, 24, 0, 0));
            TextView textView5 = new TextView(context);
            this.resetAccountTime = textView5;
            textView5.setGravity(1);
            this.resetAccountTime.setTextSize(1, 20.0f);
            this.resetAccountTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountTime.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountTime, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
            TextView textView6 = new TextView(context);
            this.resetAccountButton = textView6;
            textView6.setGravity(17);
            this.resetAccountButton.setText(LocaleController.getString(i));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 15.0f);
            this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            this.resetAccountButton.setTextColor(-1);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-1, 50, 1, 16, 32, 16, 48));
            this.resetAccountButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityResetWaitView.this.lambda$new$3(view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.showDialog(new AlertDialog.Builder(loginActivity.getParentActivity()).setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning)).setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText)).setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityResetWaitView.this.lambda$new$2(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface, int i) {
            LoginActivity.this.needShowProgress(0);
            TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
            tLRPC$TL_account_deleteAccount.reason = "Forgot password";
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityResetWaitView.this.lambda$new$1(tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityResetWaitView.this.lambda$new$0(tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(TLRPC$TL_error tLRPC$TL_error) {
            LoginActivity.this.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                if (this.requestPhone == null || this.phoneHash == null || this.phoneCode == null) {
                    LoginActivity.this.setPage(0, true, null, true);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", this.phoneCode);
                LoginActivity.this.setPage(5, true, bundle, false);
            } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
            } else {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountTime.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2"), Theme.getColor("chats_actionPressedBackground")));
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", R.string.ResetAccount);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateTimeText() {
            int i = 0;
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).getCurrentTime() - this.startTime));
            int i2 = max / 86400;
            int round = Math.round(max / 86400.0f);
            int i3 = max / 3600;
            int i4 = (max / 60) % 60;
            int i5 = max % 60;
            if (i2 >= 2) {
                this.resetAccountTime.setText(LocaleController.formatPluralString("Days", round, new Object[0]));
            } else {
                this.resetAccountTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)));
            }
            boolean z = max == 0;
            Boolean bool = this.wasResetButtonActive;
            if (bool == null || bool.booleanValue() != z) {
                if (!z) {
                    this.waitImageView.setAutoRepeat(true);
                    if (!this.waitImageView.isPlaying()) {
                        this.waitImageView.playAnimation();
                    }
                } else {
                    this.waitImageView.getAnimatedDrawable().setAutoRepeat(0);
                }
                this.resetAccountTime.setVisibility(z ? 4 : 0);
                this.resetAccountText.setVisibility(z ? 4 : 0);
                TextView textView = this.resetAccountButton;
                if (!z) {
                    i = 4;
                }
                textView.setVisibility(i);
                this.wasResetButtonActive = Boolean.valueOf(z);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            this.currentParams = bundle;
            this.requestPhone = bundle.getString("phoneFormated");
            this.phoneHash = bundle.getString("phoneHash");
            this.phoneCode = bundle.getString("code");
            this.startTime = bundle.getInt("startTime");
            this.waitTime = bundle.getInt("waitTime");
            TextView textView = this.confirmTextView;
            int i = R.string.ResetAccountInfo;
            PhoneFormat phoneFormat = PhoneFormat.getInstance();
            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", i, LocaleController.addNbsp(phoneFormat.format("+" + this.requestPhone)))));
            updateTimeText();
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.LoginActivity.LoginActivityResetWaitView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (LoginActivityResetWaitView.this.timeRunnable != this) {
                        return;
                    }
                    LoginActivityResetWaitView.this.updateTimeText();
                    AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000L);
                }
            };
            this.timeRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 1000L);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            LoginActivity.this.needHideProgress(true);
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("resetview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("resetview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivitySetupEmail extends SlideView {
        private Bundle currentParams;
        private EditTextBoldCursor emailField;
        private OutlineTextContainerView emailOutlineView;
        private String emailPhone;
        private GoogleSignInAccount googleAccount;
        private RLottieImageView inboxImageView;
        private LoginOrView loginOrView;
        private boolean nextPressed;
        private String phone;
        private String phoneHash;
        private String requestPhone;
        private TextView signInWithGoogleView;
        private TextView subtitleView;
        private TextView titleView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x0076  */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0079  */
        /* JADX WARN: Removed duplicated region for block: B:17:0x00f3  */
        /* JADX WARN: Removed duplicated region for block: B:18:0x00f6  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoginActivitySetupEmail(android.content.Context r27) {
            /*
                Method dump skipped, instructions count: 597
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySetupEmail.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view, boolean z) {
            this.emailOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.ui.LoginActivity.LoginActivitySetupEmail.2
                @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
                public void didReceivedNotification(int i, int i2, Object... objArr) {
                    int intValue = ((Integer) objArr[0]).intValue();
                    ((Integer) objArr[1]).intValue();
                    Intent intent = (Intent) objArr[2];
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onActivityResultReceived);
                    if (intValue == 200) {
                        try {
                            LoginActivitySetupEmail.this.googleAccount = GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException.class);
                            LoginActivitySetupEmail.this.onNextPressed(null);
                        } catch (ApiException e) {
                            FileLog.e(e);
                        }
                    }
                }
            }, NotificationCenter.onActivityResultReceived);
            final GoogleSignInClient client = GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder().requestIdToken(BuildVars.GOOGLE_AUTH_CLIENT_ID).requestEmail().build());
            client.signOut().addOnCompleteListener(new OnCompleteListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda3
                @Override // com.google.android.gms.tasks.OnCompleteListener
                public final void onComplete(Task task) {
                    LoginActivity.LoginActivitySetupEmail.this.lambda$new$2(client, task);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(GoogleSignInClient googleSignInClient, Task task) {
            LoginActivity.this.getParentActivity().startActivityForResult(googleSignInClient.getSignInIntent(), 200);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.emailField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.signInWithGoogleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.loginOrView.updateColors();
            this.emailOutlineView.invalidate();
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("AddEmailTitle", R.string.AddEmailTitle);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            this.emailField.setText("");
            this.currentParams = bundle;
            this.phone = bundle.getString("phone");
            this.emailPhone = this.currentParams.getString("ephone");
            this.requestPhone = this.currentParams.getString("phoneFormated");
            this.phoneHash = this.currentParams.getString("phoneHash");
            int i = bundle.getBoolean("googleSignInAllowed") ? 0 : 8;
            this.loginOrView.setVisibility(i);
            this.signInWithGoogleView.setVisibility(i);
            LoginActivity.this.showKeyboard(this.emailField);
            this.emailField.requestFocus();
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.emailOutlineView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                this.emailField.setText("");
            }
            this.emailField.requestFocus();
            LoginActivity.this.onFieldError(this.emailOutlineView, true);
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySetupEmail.this.lambda$onPasscodeError$4();
                }
            }, 300L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$4() {
            this.emailField.requestFocus();
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (this.nextPressed) {
                return;
            }
            GoogleSignInAccount googleSignInAccount = this.googleAccount;
            String email = googleSignInAccount != null ? googleSignInAccount.getEmail() : this.emailField.getText().toString();
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("email", email);
            bundle.putBoolean("setup", true);
            if (this.googleAccount != null) {
                final TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail = new TLRPC$TL_account_verifyEmail();
                if (LoginActivity.this.activityMode == 3) {
                    tLRPC$TL_account_verifyEmail.purpose = new TLRPC$TL_emailVerifyPurposeLoginChange();
                } else {
                    TLRPC$TL_emailVerifyPurposeLoginSetup tLRPC$TL_emailVerifyPurposeLoginSetup = new TLRPC$TL_emailVerifyPurposeLoginSetup();
                    tLRPC$TL_emailVerifyPurposeLoginSetup.phone_number = this.requestPhone;
                    tLRPC$TL_emailVerifyPurposeLoginSetup.phone_code_hash = this.phoneHash;
                    tLRPC$TL_account_verifyEmail.purpose = tLRPC$TL_emailVerifyPurposeLoginSetup;
                }
                TLRPC$TL_emailVerificationGoogle tLRPC$TL_emailVerificationGoogle = new TLRPC$TL_emailVerificationGoogle();
                tLRPC$TL_emailVerificationGoogle.token = this.googleAccount.getIdToken();
                tLRPC$TL_account_verifyEmail.verification = tLRPC$TL_emailVerificationGoogle;
                this.googleAccount = null;
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_account_verifyEmail, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda9
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LoginActivity.LoginActivitySetupEmail.this.lambda$onNextPressed$6(bundle, tLRPC$TL_account_verifyEmail, tLObject, tLRPC$TL_error);
                    }
                }, 10);
            } else if (TextUtils.isEmpty(email)) {
                onPasscodeError(false);
            } else {
                this.nextPressed = true;
                LoginActivity.this.needShowProgress(0);
                final TLRPC$TL_account_sendVerifyEmailCode tLRPC$TL_account_sendVerifyEmailCode = new TLRPC$TL_account_sendVerifyEmailCode();
                if (LoginActivity.this.activityMode == 3) {
                    tLRPC$TL_account_sendVerifyEmailCode.purpose = new TLRPC$TL_emailVerifyPurposeLoginChange();
                } else {
                    TLRPC$TL_emailVerifyPurposeLoginSetup tLRPC$TL_emailVerifyPurposeLoginSetup2 = new TLRPC$TL_emailVerifyPurposeLoginSetup();
                    tLRPC$TL_emailVerifyPurposeLoginSetup2.phone_number = this.requestPhone;
                    tLRPC$TL_emailVerifyPurposeLoginSetup2.phone_code_hash = this.phoneHash;
                    tLRPC$TL_account_sendVerifyEmailCode.purpose = tLRPC$TL_emailVerifyPurposeLoginSetup2;
                }
                tLRPC$TL_account_sendVerifyEmailCode.email = email;
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_account_sendVerifyEmailCode, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda8
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LoginActivity.LoginActivitySetupEmail.this.lambda$onNextPressed$8(bundle, tLRPC$TL_account_sendVerifyEmailCode, tLObject, tLRPC$TL_error);
                    }
                }, 10);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$6(final Bundle bundle, final TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySetupEmail.this.lambda$onNextPressed$5(tLObject, bundle, tLRPC$TL_error, tLRPC$TL_account_verifyEmail);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$5(TLObject tLObject, Bundle bundle, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail) {
            if ((tLObject instanceof TLRPC$TL_account_emailVerified) && LoginActivity.this.activityMode == 3) {
                LoginActivity.this.finishFragment();
                LoginActivity.this.emailChangeFinishCallback.run();
            } else if (tLObject instanceof TLRPC$TL_account_emailVerifiedLogin) {
                TLRPC$TL_account_emailVerifiedLogin tLRPC$TL_account_emailVerifiedLogin = (TLRPC$TL_account_emailVerifiedLogin) tLObject;
                bundle.putString("email", tLRPC$TL_account_emailVerifiedLogin.email);
                LoginActivity.this.fillNextCodeParams(bundle, tLRPC$TL_account_emailVerifiedLogin.sent_code);
            } else if (tLRPC$TL_error == null) {
            } else {
                if (tLRPC$TL_error.text.contains("EMAIL_NOT_ALLOWED")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString(R.string.EmailNotAllowed));
                } else if (tLRPC$TL_error.text.contains("EMAIL_TOKEN_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString(R.string.EmailTokenInvalid));
                } else if (tLRPC$TL_error.code == -1000) {
                } else {
                    AlertsCreator.processError(((BaseFragment) LoginActivity.this).currentAccount, tLRPC$TL_error, LoginActivity.this, tLRPC$TL_account_verifyEmail, new Object[0]);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(final Bundle bundle, final TLRPC$TL_account_sendVerifyEmailCode tLRPC$TL_account_sendVerifyEmailCode, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySetupEmail.this.lambda$onNextPressed$7(tLObject, bundle, tLRPC$TL_error, tLRPC$TL_account_sendVerifyEmailCode);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLObject tLObject, Bundle bundle, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_sendVerifyEmailCode tLRPC$TL_account_sendVerifyEmailCode) {
            LoginActivity.this.needHideProgress(false);
            this.nextPressed = false;
            if (tLObject instanceof TLRPC$TL_account_sentEmailCode) {
                LoginActivity.this.fillNextCodeParams(bundle, (TLRPC$TL_account_sentEmailCode) tLObject);
                return;
            }
            String str = tLRPC$TL_error.text;
            if (str == null) {
                return;
            }
            if (str.contains("EMAIL_INVALID")) {
                onPasscodeError(false);
            } else if (tLRPC$TL_error.text.contains("EMAIL_NOT_ALLOWED")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString(R.string.EmailNotAllowed));
            } else if (tLRPC$TL_error.text.contains("PHONE_PASSWORD_FLOOD")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
            } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_FLOOD")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("PhoneNumberFlood", R.string.PhoneNumberFlood));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
            } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
            } else if (tLRPC$TL_error.code == -1000) {
            } else {
                AlertsCreator.processError(((BaseFragment) LoginActivity.this).currentAccount, tLRPC$TL_error, LoginActivity.this, tLRPC$TL_account_sendVerifyEmailCode, this.requestPhone);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySetupEmail.this.lambda$onShow$9();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$9() {
            this.inboxImageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.inboxImageView.playAnimation();
            this.emailField.requestFocus();
            AndroidUtilities.showKeyboard(this.emailField);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String obj = this.emailField.getText().toString();
            if (obj != null && obj.length() != 0) {
                bundle.putString("emailsetup_email", obj);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("emailsetup_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("emailsetup_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("emailsetup_email");
            if (string != null) {
                this.emailField.setText(string);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityEmailCodeView extends SlideView {
        private CodeFieldContainer codeFieldContainer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private String email;
        private String emailPhone;
        private Runnable errorColorTimeout;
        private ViewSwitcher errorViewSwitcher;
        private GoogleSignInAccount googleAccount;
        private RLottieImageView inboxImageView;
        private boolean isFromSetup;
        private boolean isSetup;
        private int length;
        private LoginOrView loginOrView;
        private boolean nextPressed;
        private String phone;
        private String phoneHash;
        private boolean postedErrorColorTimeout;
        private String requestPhone;
        private Runnable resendCodeTimeout;
        private TextView resendCodeView;
        private FrameLayout resendFrameLayout;
        private TextView signInWithGoogleView;
        private TextView titleView;
        private TextView wrongCodeView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            int i = 0;
            this.postedErrorColorTimeout = false;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i >= codeNumberFieldArr.length) {
                    break;
                }
                codeNumberFieldArr[i].animateErrorProgress(0.0f);
                i++;
            }
            if (this.errorViewSwitcher.getCurrentView() != this.resendFrameLayout) {
                this.errorViewSwitcher.showNext();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            showResendCodeView(true);
        }

        /* JADX WARN: Removed duplicated region for block: B:20:0x009b  */
        /* JADX WARN: Removed duplicated region for block: B:21:0x009e  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x0113  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x0116  */
        /* JADX WARN: Removed duplicated region for block: B:30:0x0284  */
        /* JADX WARN: Removed duplicated region for block: B:31:0x029c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoginActivityEmailCodeView(android.content.Context r28, boolean r29) {
            /*
                Method dump skipped, instructions count: 732
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityEmailCodeView.<init>(org.telegram.ui.LoginActivity, android.content.Context, boolean):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.ui.LoginActivity.LoginActivityEmailCodeView.3
                @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
                public void didReceivedNotification(int i, int i2, Object... objArr) {
                    int intValue = ((Integer) objArr[0]).intValue();
                    ((Integer) objArr[1]).intValue();
                    Intent intent = (Intent) objArr[2];
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onActivityResultReceived);
                    if (intValue == 200) {
                        try {
                            LoginActivityEmailCodeView.this.googleAccount = GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException.class);
                            LoginActivityEmailCodeView.this.onNextPressed(null);
                        } catch (ApiException e) {
                            FileLog.e(e);
                        }
                    }
                }
            }, NotificationCenter.onActivityResultReceived);
            final GoogleSignInClient client = GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder().requestIdToken(BuildVars.GOOGLE_AUTH_CLIENT_ID).requestEmail().build());
            client.signOut().addOnCompleteListener(new OnCompleteListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda3
                @Override // com.google.android.gms.tasks.OnCompleteListener
                public final void onComplete(Task task) {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$new$2(client, task);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(GoogleSignInClient googleSignInClient, Task task) {
            LoginActivity.this.getParentActivity().startActivityForResult(googleSignInClient.getSignInIntent(), 200);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(View view) {
            showResendCodeView(false);
            final TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda19
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$new$5(bundle, tLRPC$TL_auth_resendCode, tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(final Bundle bundle, final TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$new$4(tLObject, bundle, tLRPC$TL_error, tLRPC$TL_auth_resendCode);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(TLObject tLObject, Bundle bundle, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode) {
            if (tLObject instanceof TLRPC$TL_auth_sentCode) {
                LoginActivity.this.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else if (tLRPC$TL_error == null || tLRPC$TL_error.text == null) {
            } else {
                AlertsCreator.processError(((BaseFragment) LoginActivity.this).currentAccount, tLRPC$TL_error, LoginActivity.this, tLRPC$TL_auth_resendCode, new Object[0]);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.signInWithGoogleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.loginOrView.updateColors();
            this.resendCodeView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.wrongCodeView.setTextColor(Theme.getColor("dialogTextRed"));
            this.codeFieldContainer.invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
            removeCallbacks(this.resendCodeTimeout);
        }

        private void showResendCodeView(boolean z) {
            AndroidUtilities.updateViewVisibilityAnimated(this.resendCodeView, z);
            if (this.loginOrView.getVisibility() != 8) {
                this.loginOrView.setLayoutParams(LayoutHelper.createFrame(-1, 16.0f, 17, 0.0f, 0.0f, 0.0f, z ? 8.0f : 16.0f));
                this.loginOrView.requestLayout();
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString(R.string.VerificationCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            CodeNumberField[] codeNumberFieldArr;
            if (bundle == null) {
                return;
            }
            this.currentParams = bundle;
            this.requestPhone = bundle.getString("phoneFormated");
            this.phoneHash = this.currentParams.getString("phoneHash");
            this.phone = this.currentParams.getString("phone");
            this.emailPhone = this.currentParams.getString("ephone");
            this.isFromSetup = this.currentParams.getBoolean("setup");
            this.length = this.currentParams.getInt("length");
            this.email = this.currentParams.getString("email");
            if (LoginActivity.this.activityMode == 3) {
                this.confirmTextView.setText(LocaleController.formatString(R.string.CheckYourNewEmailSubtitle, this.email));
            } else if (this.isSetup) {
                this.confirmTextView.setText(LocaleController.formatString(R.string.VerificationCodeSubtitle, this.email));
            }
            this.codeFieldContainer.setNumbersCount(this.length, 1);
            for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                codeNumberField.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
                codeNumberField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.LoginActivityEmailCodeView.5
                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        if (LoginActivityEmailCodeView.this.postedErrorColorTimeout) {
                            LoginActivityEmailCodeView loginActivityEmailCodeView = LoginActivityEmailCodeView.this;
                            loginActivityEmailCodeView.removeCallbacks(loginActivityEmailCodeView.errorColorTimeout);
                            LoginActivityEmailCodeView.this.errorColorTimeout.run();
                        }
                    }
                });
                codeNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z2) {
                        LoginActivity.LoginActivityEmailCodeView.this.lambda$setParams$7(view, z2);
                    }
                });
            }
            this.codeFieldContainer.setText("");
            if (!this.isFromSetup && LoginActivity.this.activityMode != 3) {
                String string = this.currentParams.getString("emailPattern");
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i = lastIndexOf + 1;
                    textStyleRun.end = i;
                    spannableStringBuilder.setSpan(new TextStyleSpan(textStyleRun), indexOf, i, 0);
                }
                this.confirmTextView.setText(AndroidUtilities.formatSpannable(LocaleController.getString(R.string.CheckYourEmailSubtitle), spannableStringBuilder));
            }
            int i2 = bundle.getBoolean("googleSignInAllowed") ? 0 : 8;
            this.loginOrView.setVisibility(i2);
            this.signInWithGoogleView.setVisibility(i2);
            LoginActivity.this.showKeyboard(this.codeFieldContainer.codeField[0]);
            this.codeFieldContainer.requestFocus();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setParams$7(View view, boolean z) {
            if (z) {
                LoginActivity.this.keyboardView.setEditText((EditText) view);
                LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.setText("");
                }
            }
            for (CodeNumberField codeNumberField2 : this.codeFieldContainer.codeField) {
                codeNumberField2.animateErrorProgress(1.0f);
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onPasscodeError$9();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$9() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onPasscodeError$8();
                }
            }, 150L);
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 3000L);
            this.postedErrorColorTimeout = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$8() {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer.isFocusSuppressed = false;
            codeFieldContainer.codeField[0].requestFocus();
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateErrorProgress(0.0f);
                    i++;
                } else {
                    return;
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn;
            if (this.nextPressed) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.resendCodeTimeout);
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            codeFieldContainer.isFocusSuppressed = true;
            for (CodeNumberField codeNumberField : codeFieldContainer.codeField) {
                codeNumberField.animateFocusedProgress(0.0f);
            }
            final String code = this.codeFieldContainer.getCode();
            if (code.length() == 0 && this.googleAccount == null) {
                onPasscodeError(false);
                return;
            }
            this.nextPressed = true;
            LoginActivity.this.needShowProgress(0);
            if (LoginActivity.this.activityMode == 3) {
                TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail = new TLRPC$TL_account_verifyEmail();
                tLRPC$TL_account_verifyEmail.purpose = new TLRPC$TL_emailVerifyPurposeLoginChange();
                TLRPC$TL_emailVerificationCode tLRPC$TL_emailVerificationCode = new TLRPC$TL_emailVerificationCode();
                tLRPC$TL_emailVerificationCode.code = code;
                tLRPC$TL_account_verifyEmail.verification = tLRPC$TL_emailVerificationCode;
                tLRPC$TL_auth_signIn = tLRPC$TL_account_verifyEmail;
            } else if (this.isFromSetup) {
                TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail2 = new TLRPC$TL_account_verifyEmail();
                TLRPC$TL_emailVerifyPurposeLoginSetup tLRPC$TL_emailVerifyPurposeLoginSetup = new TLRPC$TL_emailVerifyPurposeLoginSetup();
                tLRPC$TL_emailVerifyPurposeLoginSetup.phone_number = this.requestPhone;
                tLRPC$TL_emailVerifyPurposeLoginSetup.phone_code_hash = this.phoneHash;
                tLRPC$TL_account_verifyEmail2.purpose = tLRPC$TL_emailVerifyPurposeLoginSetup;
                TLRPC$TL_emailVerificationCode tLRPC$TL_emailVerificationCode2 = new TLRPC$TL_emailVerificationCode();
                tLRPC$TL_emailVerificationCode2.code = code;
                tLRPC$TL_account_verifyEmail2.verification = tLRPC$TL_emailVerificationCode2;
                tLRPC$TL_auth_signIn = tLRPC$TL_account_verifyEmail2;
            } else {
                TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn2 = new TLRPC$TL_auth_signIn();
                tLRPC$TL_auth_signIn2.phone_number = this.requestPhone;
                tLRPC$TL_auth_signIn2.phone_code_hash = this.phoneHash;
                if (this.googleAccount != null) {
                    TLRPC$TL_emailVerificationGoogle tLRPC$TL_emailVerificationGoogle = new TLRPC$TL_emailVerificationGoogle();
                    tLRPC$TL_emailVerificationGoogle.token = this.googleAccount.getIdToken();
                    tLRPC$TL_auth_signIn2.email_verification = tLRPC$TL_emailVerificationGoogle;
                } else {
                    TLRPC$TL_emailVerificationCode tLRPC$TL_emailVerificationCode3 = new TLRPC$TL_emailVerificationCode();
                    tLRPC$TL_emailVerificationCode3.code = code;
                    tLRPC$TL_auth_signIn2.email_verification = tLRPC$TL_emailVerificationCode3;
                }
                tLRPC$TL_auth_signIn2.flags |= 2;
                tLRPC$TL_auth_signIn = tLRPC$TL_auth_signIn2;
            }
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            codeFieldContainer2.isFocusSuppressed = true;
            for (CodeNumberField codeNumberField2 : codeFieldContainer2.codeField) {
                codeNumberField2.animateFocusedProgress(0.0f);
            }
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_signIn, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda20
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onNextPressed$16(code, tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$16(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onNextPressed$15(tLRPC$TL_error, str, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:42:0x017c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$onNextPressed$15(org.telegram.tgnet.TLRPC$TL_error r6, final java.lang.String r7, final org.telegram.tgnet.TLObject r8) {
            /*
                Method dump skipped, instructions count: 408
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityEmailCodeView.lambda$onNextPressed$15(org.telegram.tgnet.TLRPC$TL_error, java.lang.String, org.telegram.tgnet.TLObject):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$10(Bundle bundle) {
            LoginActivity.this.setPage(5, true, bundle, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$11(TLObject tLObject, Bundle bundle) {
            if ((tLObject instanceof TLRPC$TL_account_emailVerified) && LoginActivity.this.activityMode == 3) {
                LoginActivity.this.finishFragment();
                LoginActivity.this.emailChangeFinishCallback.run();
            } else if (tLObject instanceof TLRPC$TL_account_emailVerifiedLogin) {
                LoginActivity.this.fillNextCodeParams(bundle, ((TLRPC$TL_account_emailVerifiedLogin) tLObject).sent_code);
            } else if (!(tLObject instanceof TLRPC$TL_auth_authorization)) {
            } else {
                LoginActivity.this.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$14(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onNextPressed$13(tLRPC$TL_error, tLObject, str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$13(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            if (tLRPC$TL_error != null) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                return;
            }
            TLRPC$account_Password tLRPC$account_Password = (TLRPC$account_Password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$account_Password, true)) {
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            final Bundle bundle = new Bundle();
            SerializedData serializedData = new SerializedData(tLRPC$account_Password.getObjectSize());
            tLRPC$account_Password.serializeToStream(serializedData);
            bundle.putString("password", Utilities.bytesToHex(serializedData.toByteArray()));
            bundle.putString("phoneFormated", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("code", str);
            animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onNextPressed$12(bundle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$12(Bundle bundle) {
            LoginActivity.this.setPage(6, true, bundle, false);
        }

        private void animateSuccess(final Runnable runnable) {
            if (this.googleAccount != null) {
                runnable.run();
                return;
            }
            final int i = 0;
            while (true) {
                CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
                if (i < codeFieldContainer.codeField.length) {
                    codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivityEmailCodeView.this.lambda$animateSuccess$17(i);
                        }
                    }, i * 75);
                    i++;
                } else {
                    codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivityEmailCodeView.this.lambda$animateSuccess$18(runnable);
                        }
                    }, (this.codeFieldContainer.codeField.length * 75) + 400);
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateSuccess$17(int i) {
            this.codeFieldContainer.codeField[i].animateSuccessProgress(1.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateSuccess$18(Runnable runnable) {
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateSuccessProgress(0.0f);
                    i++;
                } else {
                    runnable.run();
                    this.codeFieldContainer.isFocusSuppressed = false;
                    return;
                }
            }
        }

        private void shakeWrongCode() {
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i >= codeNumberFieldArr.length) {
                    break;
                }
                codeNumberFieldArr[i].setText("");
                this.codeFieldContainer.codeField[i].animateErrorProgress(1.0f);
                i++;
            }
            if (this.errorViewSwitcher.getCurrentView() == this.resendFrameLayout) {
                this.errorViewSwitcher.showNext();
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, 10.0f, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$shakeWrongCode$20();
                }
            });
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 5000L);
            this.postedErrorColorTimeout = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$20() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$shakeWrongCode$19();
                }
            }, 150L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$19() {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer.isFocusSuppressed = false;
            codeFieldContainer.codeField[0].requestFocus();
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateErrorProgress(0.0f);
                    i++;
                } else {
                    return;
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityEmailCodeView.this.lambda$onShow$21();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$21() {
            this.inboxImageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.inboxImageView.playAnimation();
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null) {
                codeFieldContainer.setText("");
                this.codeFieldContainer.codeField[0].requestFocus();
            }
            AndroidUtilities.runOnUIThread(this.resendCodeTimeout, 60000L);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (code != null && code.length() != 0) {
                bundle.putString("emailcode_code", code);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("emailcode_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("emailcode_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("emailcode_code");
            if (string != null) {
                this.codeFieldContainer.setText(string);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityRecoverView extends SlideView {
        private CodeFieldContainer codeFieldContainer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private Runnable errorColorTimeout;
        private RLottieImageView inboxImageView;
        private boolean nextPressed;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private boolean postedErrorColorTimeout;
        private String requestPhone;
        private TextView titleView;
        private TextView troubleButton;

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            int i = 0;
            this.postedErrorColorTimeout = false;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateErrorProgress(0.0f);
                    i++;
                } else {
                    return;
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x00f5  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoginActivityRecoverView(android.content.Context r20) {
            /*
                Method dump skipped, instructions count: 407
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRecoverView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view, boolean z) {
            if (z) {
                LoginActivity.this.keyboardView.setEditText((EditText) view);
                LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            Dialog showDialog = LoginActivity.this.showDialog(new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle)).setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText)).setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRecoverView.this.lambda$new$2(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.ResetAccount), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRecoverView.this.lambda$new$3(dialogInterface, i);
                }
            }).create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface, int i) {
            LoginActivity.this.setPage(6, true, new Bundle(), true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(DialogInterface dialogInterface, int i) {
            LoginActivity.this.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.troubleButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.codeFieldContainer.invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            this.codeFieldContainer.setText("");
            this.currentParams = bundle;
            this.passwordString = bundle.getString("password");
            this.requestPhone = this.currentParams.getString("requestPhone");
            this.phoneHash = this.currentParams.getString("phoneHash");
            this.phoneCode = this.currentParams.getString("phoneCode");
            String string = this.currentParams.getString("email_unconfirmed_pattern");
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(string);
            int indexOf = string.indexOf(42);
            int lastIndexOf = string.lastIndexOf(42);
            if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                textStyleRun.flags |= 256;
                textStyleRun.start = indexOf;
                int i = lastIndexOf + 1;
                textStyleRun.end = i;
                valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i, 0);
            }
            this.troubleButton.setText(AndroidUtilities.formatSpannable(LocaleController.getString(R.string.RestoreEmailNoAccess), valueOf));
            LoginActivity.this.showKeyboard(this.codeFieldContainer);
            this.codeFieldContainer.requestFocus();
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.setText("");
                }
            }
            for (CodeNumberField codeNumberField2 : this.codeFieldContainer.codeField) {
                codeNumberField2.animateErrorProgress(1.0f);
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.lambda$onPasscodeError$6();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$6() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.lambda$onPasscodeError$5();
                }
            }, 150L);
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 3000L);
            this.postedErrorColorTimeout = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$5() {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer.isFocusSuppressed = false;
            codeFieldContainer.codeField[0].requestFocus();
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                if (i < codeNumberFieldArr.length) {
                    codeNumberFieldArr[i].animateErrorProgress(0.0f);
                    i++;
                } else {
                    return;
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (this.nextPressed) {
                return;
            }
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            codeFieldContainer.isFocusSuppressed = true;
            for (CodeNumberField codeNumberField : codeFieldContainer.codeField) {
                codeNumberField.animateFocusedProgress(0.0f);
            }
            final String code = this.codeFieldContainer.getCode();
            if (code.length() == 0) {
                onPasscodeError(false);
                return;
            }
            this.nextPressed = true;
            LoginActivity.this.needShowProgress(0);
            TLRPC$TL_auth_checkRecoveryPassword tLRPC$TL_auth_checkRecoveryPassword = new TLRPC$TL_auth_checkRecoveryPassword();
            tLRPC$TL_auth_checkRecoveryPassword.code = code;
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_checkRecoveryPassword, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityRecoverView.this.lambda$onNextPressed$8(code, tLObject, tLRPC$TL_error);
                }
            }, 10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.lambda$onNextPressed$7(tLObject, str, tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
            String formatPluralString;
            LoginActivity.this.needHideProgress(false);
            this.nextPressed = false;
            if (tLObject instanceof TLRPC$TL_boolTrue) {
                Bundle bundle = new Bundle();
                bundle.putString("emailCode", str);
                bundle.putString("password", this.passwordString);
                LoginActivity.this.setPage(9, true, bundle, false);
            } else if (tLRPC$TL_error == null || tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (!tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
            } else {
                int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                }
                LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.lambda$onShow$9();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$9() {
            this.inboxImageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.inboxImageView.playAnimation();
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null) {
                codeFieldContainer.codeField[0].requestFocus();
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (code != null && code.length() != 0) {
                bundle.putString("recoveryview_code", code);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("recoveryview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("recoveryview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("recoveryview_code");
            if (string != null) {
                this.codeFieldContainer.setText(string);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityNewPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor[] codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC$account_Password currentPassword;
        private int currentStage;
        private String emailCode;
        private boolean isPasswordVisible;
        private String newPassword;
        private boolean nextPressed;
        private OutlineTextContainerView[] outlineFields;
        private ImageView passwordButton;
        private String passwordString;
        private TextView titleTextView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        public LoginActivityNewPasswordView(Context context, int i) {
            super(context);
            int i2;
            this.currentStage = i;
            setOrientation(1);
            EditTextBoldCursor[] editTextBoldCursorArr = new EditTextBoldCursor[i == 1 ? 1 : 2];
            this.codeField = editTextBoldCursorArr;
            this.outlineFields = new OutlineTextContainerView[editTextBoldCursorArr.length];
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            float f = 18.0f;
            textView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            this.titleTextView.setText(LocaleController.getString(R.string.SetNewPassword));
            addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 8, AndroidUtilities.isSmallScreen() ? 16 : 72, 8, 0));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            textView2.setTextSize(1, 16.0f);
            this.confirmTextView.setGravity(1);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 6, 8, 16));
            final int i3 = 0;
            while (i3 < this.codeField.length) {
                final OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
                this.outlineFields[i3] = outlineTextContainerView;
                if (i == 0) {
                    i2 = i3 == 0 ? R.string.PleaseEnterNewFirstPasswordHint : R.string.PleaseEnterNewSecondPasswordHint;
                } else {
                    i2 = R.string.PasswordHintPlaceholder;
                }
                outlineTextContainerView.setText(LocaleController.getString(i2));
                this.codeField[i3] = new EditTextBoldCursor(context);
                this.codeField[i3].setCursorSize(AndroidUtilities.dp(20.0f));
                this.codeField[i3].setCursorWidth(1.5f);
                this.codeField[i3].setImeOptions(NUM);
                this.codeField[i3].setTextSize(1, f);
                this.codeField[i3].setMaxLines(1);
                this.codeField[i3].setBackground(null);
                int dp = AndroidUtilities.dp(16.0f);
                this.codeField[i3].setPadding(dp, dp, dp, dp);
                if (i == 0) {
                    this.codeField[i3].setInputType(129);
                    this.codeField[i3].setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                this.codeField[i3].setTypeface(Typeface.DEFAULT);
                this.codeField[i3].setGravity(LocaleController.isRTL ? 5 : 3);
                EditTextBoldCursor editTextBoldCursor = this.codeField[i3];
                boolean z = i3 == 0 && i == 0;
                editTextBoldCursor.addTextChangedListener(new TextWatcher(LoginActivity.this, z) { // from class: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.1
                    final /* synthetic */ boolean val$showPasswordButton;

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
                    }

                    {
                        this.val$showPasswordButton = z;
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        if (this.val$showPasswordButton) {
                            if (LoginActivityNewPasswordView.this.passwordButton.getVisibility() == 0 || TextUtils.isEmpty(editable)) {
                                if (LoginActivityNewPasswordView.this.passwordButton.getVisibility() == 8 || !TextUtils.isEmpty(editable)) {
                                    return;
                                }
                                AndroidUtilities.updateViewVisibilityAnimated(LoginActivityNewPasswordView.this.passwordButton, false, 0.1f, true);
                                return;
                            }
                            if (LoginActivityNewPasswordView.this.isPasswordVisible) {
                                LoginActivityNewPasswordView.this.passwordButton.callOnClick();
                            }
                            AndroidUtilities.updateViewVisibilityAnimated(LoginActivityNewPasswordView.this.passwordButton, true, 0.1f, true);
                        }
                    }
                });
                this.codeField[i3].setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z2) {
                        LoginActivity.LoginActivityNewPasswordView.lambda$new$0(OutlineTextContainerView.this, view, z2);
                    }
                });
                if (z) {
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(0);
                    linearLayout.setGravity(16);
                    linearLayout.addView(this.codeField[i3], LayoutHelper.createLinear(0, -2, 1.0f));
                    ImageView imageView = new ImageView(context);
                    this.passwordButton = imageView;
                    imageView.setImageResource(R.drawable.msg_message);
                    AndroidUtilities.updateViewVisibilityAnimated(this.passwordButton, true, 0.1f, false);
                    this.passwordButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            LoginActivity.LoginActivityNewPasswordView.this.lambda$new$1(view);
                        }
                    });
                    linearLayout.addView(this.passwordButton, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
                    outlineTextContainerView.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
                } else {
                    outlineTextContainerView.addView(this.codeField[i3], LayoutHelper.createFrame(-1, -2.0f));
                }
                outlineTextContainerView.attachEditText(this.codeField[i3]);
                addView(outlineTextContainerView, LayoutHelper.createLinear(-1, -2, 1, 16, 16, 16, 0));
                this.codeField[i3].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView3, int i4, KeyEvent keyEvent) {
                        boolean lambda$new$2;
                        lambda$new$2 = LoginActivity.LoginActivityNewPasswordView.this.lambda$new$2(i3, textView3, i4, keyEvent);
                        return lambda$new$2;
                    }
                });
                i3++;
                f = 18.0f;
            }
            if (i == 0) {
                this.confirmTextView.setText(LocaleController.getString("PleaseEnterNewFirstPasswordLogin", R.string.PleaseEnterNewFirstPasswordLogin));
            } else {
                this.confirmTextView.setText(LocaleController.getString("PasswordHintTextLogin", R.string.PasswordHintTextLogin));
            }
            TextView textView3 = new TextView(context);
            this.cancelButton = textView3;
            textView3.setGravity(19);
            this.cancelButton.setTextSize(1, 15.0f);
            this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.cancelButton.setText(LocaleController.getString(R.string.YourEmailSkip));
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.addView(this.cancelButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 80, 0.0f, 0.0f, 0.0f, 32.0f));
            addView(frameLayout, LayoutHelper.createLinear(-1, -1, 80));
            VerticalPositionAutoAnimator.attach(this.cancelButton);
            this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$new$3(view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$new$0(OutlineTextContainerView outlineTextContainerView, View view, boolean z) {
            outlineTextContainerView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            this.isPasswordVisible = !this.isPasswordVisible;
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                int selectionStart = editTextBoldCursorArr[i].getSelectionStart();
                int selectionEnd = this.codeField[i].getSelectionEnd();
                this.codeField[i].setInputType((this.isPasswordVisible ? 144 : 128) | 1);
                this.codeField[i].setSelection(selectionStart, selectionEnd);
                i++;
            }
            this.passwordButton.setTag(Boolean.valueOf(this.isPasswordVisible));
            this.passwordButton.setColorFilter(Theme.getColor(this.isPasswordVisible ? "windowBackgroundWhiteInputFieldActivated" : "windowBackgroundWhiteHintText"));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$2(int i, TextView textView, int i2, KeyEvent keyEvent) {
            if (i == 0) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr.length == 2) {
                    editTextBoldCursorArr[1].requestFocus();
                    return true;
                }
            }
            if (i2 == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            if (this.currentStage == 0) {
                recoverPassword(null, null);
            } else {
                recoverPassword(this.newPassword, null);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            String str;
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            int length = editTextBoldCursorArr.length;
            int i = 0;
            while (true) {
                str = "windowBackgroundWhiteInputFieldActivated";
                if (i >= length) {
                    break;
                }
                EditTextBoldCursor editTextBoldCursor = editTextBoldCursorArr[i];
                editTextBoldCursor.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                editTextBoldCursor.setCursorColor(Theme.getColor(str));
                i++;
            }
            for (OutlineTextContainerView outlineTextContainerView : this.outlineFields) {
                outlineTextContainerView.updateColor();
            }
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            ImageView imageView = this.passwordButton;
            if (imageView != null) {
                if (!this.isPasswordVisible) {
                    str = "windowBackgroundWhiteHintText";
                }
                imageView.setColorFilter(Theme.getColor(str));
                this.passwordButton.setBackground(Theme.createSelectorDrawable(LoginActivity.this.getThemedColor("listSelectorSDK21"), 1));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("NewPassword", R.string.NewPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                editTextBoldCursorArr[i].setText("");
                i++;
            }
            this.currentParams = bundle;
            this.emailCode = bundle.getString("emailCode");
            String string = this.currentParams.getString("password");
            this.passwordString = string;
            if (string != null) {
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
                TLRPC$account_Password TLdeserialize = TLRPC$account_Password.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                this.currentPassword = TLdeserialize;
                TwoStepVerificationActivity.initPasswordNewAlgo(TLdeserialize);
            }
            this.newPassword = this.currentParams.getString("new_password");
            LoginActivity.this.showKeyboard(this.codeField[0]);
            this.codeField[0].requestFocus();
        }

        private void onPasscodeError(boolean z, int i) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.codeField[i].performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            AndroidUtilities.shakeView(this.codeField[i]);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (this.nextPressed) {
                return;
            }
            String obj = this.codeField[0].getText().toString();
            if (obj.length() == 0) {
                onPasscodeError(false, 0);
            } else if (this.currentStage == 0) {
                if (!obj.equals(this.codeField[1].getText().toString())) {
                    onPasscodeError(false, 1);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("emailCode", this.emailCode);
                bundle.putString("new_password", obj);
                bundle.putString("password", this.passwordString);
                LoginActivity.this.setPage(10, true, bundle, false);
            } else {
                this.nextPressed = true;
                LoginActivity.this.needShowProgress(0);
                recoverPassword(this.newPassword, obj);
            }
        }

        private void recoverPassword(final String str, final String str2) {
            final TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword = new TLRPC$TL_auth_recoverPassword();
            tLRPC$TL_auth_recoverPassword.code = this.emailCode;
            if (!TextUtils.isEmpty(str)) {
                tLRPC$TL_auth_recoverPassword.flags |= 1;
                TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = new TLRPC$TL_account_passwordInputSettings();
                tLRPC$TL_auth_recoverPassword.new_settings = tLRPC$TL_account_passwordInputSettings;
                tLRPC$TL_account_passwordInputSettings.flags |= 1;
                tLRPC$TL_account_passwordInputSettings.hint = str2 != null ? str2 : "";
                tLRPC$TL_account_passwordInputSettings.new_algo = this.currentPassword.new_algo;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$9(str, str2, tLRPC$TL_auth_recoverPassword);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$9(final String str, final String str2, TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword) {
            byte[] stringBytes = str != null ? AndroidUtilities.getStringBytes(str) : null;
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$8(str, str2, tLObject, tLRPC$TL_error);
                }
            };
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (str != null) {
                    tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                    if (tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash == null) {
                        TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                        tLRPC$TL_error.text = "ALGO_INVALID";
                        requestDelegate.run(null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_recoverPassword, requestDelegate, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run(null, tLRPC$TL_error2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$8(final String str, final String str2, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$7(tLRPC$TL_error, str, str2, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$7(TLRPC$TL_error tLRPC$TL_error, final String str, final String str2, final TLObject tLObject) {
            String formatPluralString;
            if (tLRPC$TL_error == null || (!"SRP_ID_INVALID".equals(tLRPC$TL_error.text) && !"NEW_SALT_INVALID".equals(tLRPC$TL_error.text))) {
                LoginActivity.this.needHideProgress(false);
                if (tLObject instanceof TLRPC$auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString(R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$6(tLObject, dialogInterface, i);
                        }
                    });
                    if (TextUtils.isEmpty(str)) {
                        builder.setMessage(LocaleController.getString(R.string.YourPasswordReset));
                    } else {
                        builder.setMessage(LocaleController.getString(R.string.YourPasswordChangedSuccessText));
                    }
                    builder.setTitle(LocaleController.getString(R.string.TwoStepVerificationTitle));
                    Dialog showDialog = LoginActivity.this.showDialog(builder.create());
                    if (showDialog == null) {
                        return;
                    }
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                    return;
                } else if (tLRPC$TL_error == null) {
                    return;
                } else {
                    this.nextPressed = false;
                    if (!tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                        return;
                    }
                    int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        formatPluralString = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                    } else {
                        formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                    }
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, formatPluralString));
                    return;
                }
            }
            ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$5(str, str2, tLObject2, tLRPC$TL_error2);
                }
            }, 8);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$5(final String str, final String str2, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$recoverPassword$4(tLRPC$TL_error, tLObject, str, str2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$4(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, String str2) {
            if (tLRPC$TL_error == null) {
                TLRPC$account_Password tLRPC$account_Password = (TLRPC$account_Password) tLObject;
                this.currentPassword = tLRPC$account_Password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$account_Password);
                recoverPassword(str, str2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$6(TLObject tLObject, DialogInterface dialogInterface, int i) {
            LoginActivity.this.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.lambda$onShow$10();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$10() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                editTextBoldCursorArr[0].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                editTextBoldCursorArr2[0].setSelection(editTextBoldCursorArr2[0].length());
                AndroidUtilities.showKeyboard(this.codeField[0]);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            if (this.currentParams != null) {
                bundle.putBundle("recoveryview_params" + this.currentStage, this.currentParams);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("recoveryview_params" + this.currentStage);
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class LoginActivityRegisterView extends SlideView implements ImageUpdater.ImageUpdaterDelegate {
        private TLRPC$FileLocation avatar;
        private AnimatorSet avatarAnimation;
        private TLRPC$FileLocation avatarBig;
        private AvatarDrawable avatarDrawable;
        private RLottieImageView avatarEditor;
        private BackupImageView avatarImage;
        private View avatarOverlay;
        private RadialProgressView avatarProgressView;
        private RLottieDrawable cameraDrawable;
        private RLottieDrawable cameraWaitDrawable;
        private Bundle currentParams;
        private TextView descriptionTextView;
        private FrameLayout editTextContainer;
        private EditTextBoldCursor firstNameField;
        private OutlineTextContainerView firstNameOutlineView;
        private ImageUpdater imageUpdater;
        private boolean isCameraWaitAnimationAllowed;
        private EditTextBoldCursor lastNameField;
        private OutlineTextContainerView lastNameOutlineView;
        private boolean nextPressed;
        private String phoneHash;
        private TextView privacyView;
        private String requestPhone;
        private TextView titleTextView;
        private TextView wrongNumber;

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ void didStartUpload(boolean z) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$didStartUpload(this, z);
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* bridge */ /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ void onUploadProgressChanged(float f) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$onUploadProgressChanged(this, f);
        }

        /* loaded from: classes3.dex */
        public class LinkSpan extends ClickableSpan {
            public LinkSpan() {
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }

            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showTermsOfService(boolean z) {
            if (LoginActivity.this.currentTermsOfService == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
            if (z) {
                builder.setPositiveButton(LocaleController.getString("Accept", R.string.Accept), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$0(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$3(dialogInterface, i);
                    }
                });
            } else {
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LoginActivity.this.currentTermsOfService.text);
            MessageObject.addEntitiesToText(spannableStringBuilder, LoginActivity.this.currentTermsOfService.entities, false, false, false, false);
            builder.setMessage(spannableStringBuilder);
            LoginActivity.this.showDialog(builder.create());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$0(DialogInterface dialogInterface, int i) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$3(DialogInterface dialogInterface, int i) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
            builder.setMessage(LocaleController.getString("TosDecline", R.string.TosDecline));
            builder.setPositiveButton(LocaleController.getString("SignUp", R.string.SignUp), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface2, int i2) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$1(dialogInterface2, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface2, int i2) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$showTermsOfService$2(dialogInterface2, i2);
                }
            });
            LoginActivity.this.showDialog(builder.create());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$1(DialogInterface dialogInterface, int i) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$2(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        public LoginActivityRegisterView(Context context) {
            super(context);
            this.nextPressed = false;
            this.isCameraWaitAnimationAllowed = true;
            setOrientation(1);
            ImageUpdater imageUpdater = new ImageUpdater(false);
            this.imageUpdater = imageUpdater;
            imageUpdater.setOpenWithFrontfaceCamera(true);
            this.imageUpdater.setSearchAvailable(false);
            this.imageUpdater.setUploadAfterSelect(false);
            ImageUpdater imageUpdater2 = this.imageUpdater;
            imageUpdater2.parentFragment = LoginActivity.this;
            imageUpdater2.setDelegate(this);
            FrameLayout frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createLinear(78, 78, 1));
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(context, LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.1
                @Override // android.view.View
                public void invalidate() {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                @Override // android.view.View
                public void invalidate(int i, int i2, int i3, int i4) {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.avatarImage = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(64.0f));
            this.avatarDrawable.setAvatarType(13);
            this.avatarDrawable.setInfo(5L, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(-1, -1.0f));
            Paint paint = new Paint(1);
            paint.setColor(NUM);
            View view = new View(context, LoginActivity.this, paint) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.2
                final /* synthetic */ Paint val$paint;

                {
                    this.val$paint = paint;
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (LoginActivityRegisterView.this.avatarImage == null || LoginActivityRegisterView.this.avatarProgressView.getVisibility() != 0) {
                        return;
                    }
                    this.val$paint.setAlpha((int) (LoginActivityRegisterView.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f * LoginActivityRegisterView.this.avatarProgressView.getAlpha()));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, this.val$paint);
                }
            };
            this.avatarOverlay = view;
            frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$7(view2);
                }
            });
            int i = R.raw.camera;
            this.cameraDrawable = new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f), false, null);
            int i2 = R.raw.camera_wait;
            this.cameraWaitDrawable = new RLottieDrawable(i2, String.valueOf(i2), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f), false, null);
            RLottieImageView rLottieImageView = new RLottieImageView(context, LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.3
                @Override // android.view.View
                public void invalidate(int i3, int i4, int i5, int i6) {
                    super.invalidate(i3, i4, i5, i6);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }

                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(-1, -1.0f));
            this.avatarEditor.addOnAttachStateChangeListener(new AnonymousClass4(LoginActivity.this));
            RadialProgressView radialProgressView = new RadialProgressView(context, LoginActivity.this) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.5
                @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(-1, -1.0f));
            showAvatarProgress(false, false);
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setText(LocaleController.getString(R.string.RegistrationProfileInfo));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(1);
            addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 12, 8, 0));
            TextView textView2 = new TextView(context);
            this.descriptionTextView = textView2;
            textView2.setText(LocaleController.getString("RegisterText2", R.string.RegisterText2));
            this.descriptionTextView.setGravity(1);
            float f = 14.0f;
            this.descriptionTextView.setTextSize(1, 14.0f);
            this.descriptionTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.descriptionTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 6, 8, 0));
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.editTextContainer = frameLayout2;
            addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 8.0f, 21.0f, 8.0f, 0.0f));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.firstNameOutlineView = outlineTextContainerView;
            outlineTextContainerView.setText(LocaleController.getString(R.string.FirstName));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.firstNameField = editTextBoldCursor;
            editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
            this.firstNameField.setCursorWidth(1.5f);
            this.firstNameField.setImeOptions(NUM);
            this.firstNameField.setTextSize(1, 17.0f);
            this.firstNameField.setMaxLines(1);
            this.firstNameField.setInputType(8192);
            this.firstNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view2, boolean z) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$8(view2, z);
                }
            });
            this.firstNameField.setBackground(null);
            this.firstNameField.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.firstNameOutlineView.attachEditText(this.firstNameField);
            this.firstNameOutlineView.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2, 48));
            this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i3, KeyEvent keyEvent) {
                    boolean lambda$new$9;
                    lambda$new$9 = LoginActivity.LoginActivityRegisterView.this.lambda$new$9(textView3, i3, keyEvent);
                    return lambda$new$9;
                }
            });
            OutlineTextContainerView outlineTextContainerView2 = new OutlineTextContainerView(context);
            this.lastNameOutlineView = outlineTextContainerView2;
            outlineTextContainerView2.setText(LocaleController.getString(R.string.LastName));
            EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
            this.lastNameField = editTextBoldCursor2;
            editTextBoldCursor2.setCursorSize(AndroidUtilities.dp(20.0f));
            this.lastNameField.setCursorWidth(1.5f);
            this.lastNameField.setImeOptions(NUM);
            this.lastNameField.setTextSize(1, 17.0f);
            this.lastNameField.setMaxLines(1);
            this.lastNameField.setInputType(8192);
            this.lastNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view2, boolean z) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$10(view2, z);
                }
            });
            this.lastNameField.setBackground(null);
            this.lastNameField.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.lastNameOutlineView.attachEditText(this.lastNameField);
            this.lastNameOutlineView.addView(this.lastNameField, LayoutHelper.createFrame(-1, -2, 48));
            this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i3, KeyEvent keyEvent) {
                    boolean lambda$new$11;
                    lambda$new$11 = LoginActivity.LoginActivityRegisterView.this.lambda$new$11(textView3, i3, keyEvent);
                    return lambda$new$11;
                }
            });
            buildEditTextLayout(AndroidUtilities.isSmallScreen());
            TextView textView3 = new TextView(context);
            this.wrongNumber = textView3;
            textView3.setText(LocaleController.getString("CancelRegistration", R.string.CancelRegistration));
            int i3 = 5;
            this.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.wrongNumber.setTextSize(1, 14.0f);
            this.wrongNumber.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            this.wrongNumber.setVisibility(8);
            addView(this.wrongNumber, LayoutHelper.createLinear(-2, -2, (!LocaleController.isRTL ? 3 : i3) | 48, 0, 20, 0, 0));
            this.wrongNumber.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$12(view2);
                }
            });
            FrameLayout frameLayout3 = new FrameLayout(context);
            addView(frameLayout3, LayoutHelper.createLinear(-1, -1, 83));
            TextView textView4 = new TextView(context);
            this.privacyView = textView4;
            textView4.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
            this.privacyView.setTextSize(1, AndroidUtilities.isSmallScreen() ? 13.0f : f);
            this.privacyView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.privacyView.setGravity(16);
            frameLayout3.addView(this.privacyView, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 83, 14.0f, 0.0f, 70.0f, 32.0f));
            VerticalPositionAutoAnimator.attach(this.privacyView);
            String string = LocaleController.getString("TermsOfServiceLogin", R.string.TermsOfServiceLogin);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf(42);
            int lastIndexOf = string.lastIndexOf(42);
            if (indexOf != -1 && lastIndexOf != -1 && indexOf != lastIndexOf) {
                spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, (CharSequence) "");
                spannableStringBuilder.replace(indexOf, indexOf + 1, (CharSequence) "");
                spannableStringBuilder.setSpan(new LinkSpan(), indexOf, lastIndexOf - 1, 33);
            }
            this.privacyView.setText(spannableStringBuilder);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$4();
                }
            }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$new$6(dialogInterface);
                }
            });
            this.isCameraWaitAnimationAllowed = false;
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.avatarEditor.playAnimation();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4() {
            this.avatar = null;
            this.avatarBig = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (Object) null);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(DialogInterface dialogInterface) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.avatarEditor.setAnimation(this.cameraDrawable);
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.this.lambda$new$5();
                    }
                });
                this.avatarEditor.playAnimation();
                return;
            }
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0, false);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5() {
            this.isCameraWaitAnimationAllowed = true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass4 implements View.OnAttachStateChangeListener {
            private boolean isAttached;
            private long lastRun = System.currentTimeMillis();
            private Runnable cameraWaitCallback = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.lambda$$2();
                }
            };

            AnonymousClass4(LoginActivity loginActivity) {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$$2() {
                if (this.isAttached) {
                    if (LoginActivityRegisterView.this.isCameraWaitAnimationAllowed && System.currentTimeMillis() - this.lastRun >= 10000) {
                        LoginActivityRegisterView.this.avatarEditor.setAnimation(LoginActivityRegisterView.this.cameraWaitDrawable);
                        LoginActivityRegisterView.this.cameraWaitDrawable.setCurrentFrame(0, false);
                        LoginActivityRegisterView.this.cameraWaitDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.lambda$$1();
                            }
                        });
                        LoginActivityRegisterView.this.avatarEditor.playAnimation();
                        this.lastRun = System.currentTimeMillis();
                    }
                    LoginActivityRegisterView.this.avatarEditor.postDelayed(this.cameraWaitCallback, 1000L);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$$1() {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.lambda$$0();
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$$0() {
                LoginActivityRegisterView.this.cameraDrawable.setCurrentFrame(0, false);
                LoginActivityRegisterView.this.avatarEditor.setAnimation(LoginActivityRegisterView.this.cameraDrawable);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                this.isAttached = true;
                view.post(this.cameraWaitCallback);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                this.isAttached = false;
                view.removeCallbacks(this.cameraWaitCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(View view, boolean z) {
            this.firstNameOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$9(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                this.lastNameField.requestFocus();
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10(View view, boolean z) {
            this.lastNameOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$11(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 6 || i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$12(View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            onBackPressed(false);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.avatarDrawable.invalidateSelf();
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
            this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.lastNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
            this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.privacyView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.privacyView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.firstNameOutlineView.updateColor();
            this.lastNameOutlineView.updateColor();
        }

        private void buildEditTextLayout(boolean z) {
            boolean hasFocus = this.firstNameField.hasFocus();
            boolean hasFocus2 = this.lastNameField.hasFocus();
            this.editTextContainer.removeAllViews();
            if (z) {
                LinearLayout linearLayout = new LinearLayout(LoginActivity.this.getParentActivity());
                linearLayout.setOrientation(0);
                this.firstNameOutlineView.setText(LocaleController.getString(R.string.FirstNameSmall));
                this.lastNameOutlineView.setText(LocaleController.getString(R.string.LastNameSmall));
                linearLayout.addView(this.firstNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 8, 0));
                linearLayout.addView(this.lastNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 8, 0, 0, 0));
                this.editTextContainer.addView(linearLayout);
                if (hasFocus) {
                    this.firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.firstNameField);
                    return;
                } else if (!hasFocus2) {
                    return;
                } else {
                    this.lastNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.lastNameField);
                    return;
                }
            }
            this.firstNameOutlineView.setText(LocaleController.getString(R.string.FirstName));
            this.lastNameOutlineView.setText(LocaleController.getString(R.string.LastName));
            this.editTextContainer.addView(this.firstNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 0.0f, 8.0f, 0.0f));
            this.editTextContainer.addView(this.lastNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 82.0f, 8.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, final TLRPC$PhotoSize tLRPC$PhotoSize, final TLRPC$PhotoSize tLRPC$PhotoSize2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.lambda$didUploadPhoto$13(tLRPC$PhotoSize2, tLRPC$PhotoSize);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$didUploadPhoto$13(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            this.avatar = tLRPC$FileLocation;
            this.avatarBig = tLRPC$PhotoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", this.avatarDrawable, (Object) null);
        }

        private void showAvatarProgress(final boolean z, boolean z2) {
            if (this.avatarEditor == null) {
                return;
            }
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f));
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f));
                }
                this.avatarAnimation.setDuration(180L);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (LoginActivityRegisterView.this.avatarAnimation == null || LoginActivityRegisterView.this.avatarEditor == null) {
                            return;
                        }
                        if (z) {
                            LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                        } else {
                            LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                        }
                        LoginActivityRegisterView.this.avatarAnimation = null;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        LoginActivityRegisterView.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean z) {
            if (z) {
                LoginActivity.this.needHideProgress(true);
                this.nextPressed = false;
                this.currentParams = null;
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.Warning));
            builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRegisterView.this.lambda$onBackPressed$14(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
            LoginActivity.this.showDialog(builder.create());
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$14(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
            hidePrivacyView();
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("YourName", R.string.YourName);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            if (this.privacyView != null) {
                if (LoginActivity.this.restoringState) {
                    this.privacyView.setAlpha(1.0f);
                } else {
                    this.privacyView.setAlpha(0.0f);
                    this.privacyView.animate().alpha(1.0f).setDuration(200L).setStartDelay(300L).setInterpolator(AndroidUtilities.decelerateInterpolator).start();
                }
            }
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.lambda$onShow$15();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$15() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            this.firstNameField.setText("");
            this.lastNameField.setText("");
            this.requestPhone = bundle.getString("phoneFormated");
            this.phoneHash = bundle.getString("phoneHash");
            this.currentParams = bundle;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String str) {
            if (this.nextPressed) {
                return;
            }
            if (LoginActivity.this.currentTermsOfService != null && LoginActivity.this.currentTermsOfService.popup) {
                showTermsOfService(true);
            } else if (this.firstNameField.length() == 0) {
                LoginActivity.this.onFieldError(this.firstNameOutlineView, true);
            } else {
                this.nextPressed = true;
                TLRPC$TL_auth_signUp tLRPC$TL_auth_signUp = new TLRPC$TL_auth_signUp();
                tLRPC$TL_auth_signUp.phone_code_hash = this.phoneHash;
                tLRPC$TL_auth_signUp.phone_number = this.requestPhone;
                tLRPC$TL_auth_signUp.first_name = this.firstNameField.getText().toString();
                tLRPC$TL_auth_signUp.last_name = this.lastNameField.getText().toString();
                LoginActivity.this.needShowProgress(0);
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tLRPC$TL_auth_signUp, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda19
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LoginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$19(tLObject, tLRPC$TL_error);
                    }
                }, 10);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$18(tLObject, tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$18(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            this.nextPressed = false;
            if (!(tLObject instanceof TLRPC$TL_auth_authorization)) {
                LoginActivity.this.needHideProgress(false);
                if (tLRPC$TL_error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    return;
                } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    return;
                } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    return;
                } else if (tLRPC$TL_error.text.contains("FIRSTNAME_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidFirstName", R.string.InvalidFirstName));
                    return;
                } else if (tLRPC$TL_error.text.contains("LASTNAME_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidLastName", R.string.InvalidLastName));
                    return;
                } else {
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
                    return;
                }
            }
            hidePrivacyView();
            LoginActivity.this.showDoneButton(false, true);
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$17(tLObject);
                }
            }, 150L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$17(TLObject tLObject) {
            LoginActivity.this.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(((BaseFragment) LoginActivity.this).fragmentView.findFocus());
            LoginActivity.this.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject, true);
            final TLRPC$FileLocation tLRPC$FileLocation = this.avatarBig;
            if (tLRPC$FileLocation != null) {
                Utilities.cacheClearQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.this.lambda$onNextPressed$16(tLRPC$FileLocation);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$16(TLRPC$FileLocation tLRPC$FileLocation) {
            MessagesController.getInstance(((BaseFragment) LoginActivity.this).currentAccount).uploadAndApplyUserAvatar(tLRPC$FileLocation);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String obj = this.firstNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_first", obj);
            }
            String obj2 = this.lastNameField.getText().toString();
            if (obj2.length() != 0) {
                bundle.putString("registerview_last", obj2);
            }
            if (LoginActivity.this.currentTermsOfService != null) {
                SerializedData serializedData = new SerializedData(LoginActivity.this.currentTermsOfService.getObjectSize());
                LoginActivity.this.currentTermsOfService.serializeToStream(serializedData);
                bundle.putString("terms", Base64.encodeToString(serializedData.toByteArray(), 0));
                serializedData.cleanup();
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("registerview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            byte[] decode;
            Bundle bundle2 = bundle.getBundle("registerview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            try {
                String string = bundle.getString("terms");
                if (string != null && (decode = Base64.decode(string, 0)) != null) {
                    SerializedData serializedData = new SerializedData(decode);
                    LoginActivity.this.currentTermsOfService = TLRPC$TL_help_termsOfService.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    serializedData.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            String string2 = bundle.getString("registerview_first");
            if (string2 != null) {
                this.firstNameField.setText(string2);
            }
            String string3 = bundle.getString("registerview_last");
            if (string3 != null) {
                this.lastNameField.setText(string3);
            }
        }

        private void hidePrivacyView() {
            this.privacyView.animate().alpha(0.0f).setDuration(150L).setStartDelay(0L).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showKeyboard(View view) {
        if (!isCustomKeyboardVisible()) {
            return AndroidUtilities.showKeyboard(view);
        }
        return true;
    }

    public LoginActivity setIntroView(View view, TextView textView) {
        this.introView = view;
        this.startMessagingButton = textView;
        this.isAnimatingIntro = true;
        return this;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!z || this.introView == null) {
            return null;
        }
        if (this.fragmentView.getParent() instanceof View) {
            ((View) this.fragmentView.getParent()).setTranslationX(0.0f);
        }
        final TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(this.fragmentView.getContext());
        transformableLoginButtonView.setButtonText(this.startMessagingButton.getPaint(), this.startMessagingButton.getText().toString());
        final int width = this.startMessagingButton.getWidth();
        final int height = this.startMessagingButton.getHeight();
        final int i = this.floatingButtonIcon.getLayoutParams().width;
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        transformableLoginButtonView.setLayoutParams(layoutParams);
        int[] iArr = new int[2];
        this.fragmentView.getLocationInWindow(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        this.startMessagingButton.getLocationInWindow(iArr);
        final float f = iArr[0] - i2;
        final float f2 = iArr[1] - i3;
        transformableLoginButtonView.setTranslationX(f);
        transformableLoginButtonView.setTranslationY(f2);
        final int width2 = (((getParentLayout().getView().getWidth() - this.floatingButtonIcon.getLayoutParams().width) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).rightMargin) - getParentLayout().getView().getPaddingLeft()) - getParentLayout().getView().getPaddingRight();
        final int height2 = ((((getParentLayout().getView().getHeight() - this.floatingButtonIcon.getLayoutParams().height) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).bottomMargin) - (isCustomKeyboardVisible() ? AndroidUtilities.dp(230.0f) : 0)) - getParentLayout().getView().getPaddingTop()) - getParentLayout().getView().getPaddingBottom();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                LoginActivity.this.floatingButtonContainer.setVisibility(4);
                LoginActivity.this.keyboardLinearLayout.setAlpha(0.0f);
                ((BaseFragment) LoginActivity.this).fragmentView.setBackgroundColor(0);
                LoginActivity.this.startMessagingButton.setVisibility(4);
                ((FrameLayout) ((BaseFragment) LoginActivity.this).fragmentView).addView(transformableLoginButtonView);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                LoginActivity.this.keyboardLinearLayout.setAlpha(1.0f);
                LoginActivity.this.startMessagingButton.setVisibility(0);
                ((BaseFragment) LoginActivity.this).fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                LoginActivity.this.floatingButtonContainer.setVisibility(0);
                ((FrameLayout) ((BaseFragment) LoginActivity.this).fragmentView).removeView(transformableLoginButtonView);
                if (LoginActivity.this.animationFinishCallback != null) {
                    AndroidUtilities.runOnUIThread(LoginActivity.this.animationFinishCallback);
                    LoginActivity.this.animationFinishCallback = null;
                }
                LoginActivity.this.isAnimatingIntro = false;
                runnable.run();
            }
        });
        final int color = Theme.getColor("windowBackgroundWhite");
        final int alpha = Color.alpha(color);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LoginActivity.this.lambda$onCustomTransitionAnimation$17(color, alpha, layoutParams, width, i, height, transformableLoginButtonView, f, width2, f2, height2, valueAnimator);
            }
        });
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300L);
        animatorSet.playTogether(ofFloat);
        animatorSet.start();
        return animatorSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$17(int i, int i2, ViewGroup.MarginLayoutParams marginLayoutParams, int i3, int i4, int i5, TransformableLoginButtonView transformableLoginButtonView, float f, int i6, float f2, int i7, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardLinearLayout.setAlpha(floatValue);
        this.fragmentView.setBackgroundColor(ColorUtils.setAlphaComponent(i, (int) (i2 * floatValue)));
        float f3 = 1.0f - floatValue;
        this.slideViewsContainer.setTranslationY(AndroidUtilities.dp(20.0f) * f3);
        if (!isCustomKeyboardForceDisabled()) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            customPhoneKeyboardView.setTranslationY(customPhoneKeyboardView.getLayoutParams().height * f3);
            this.floatingButtonContainer.setTranslationY(this.keyboardView.getLayoutParams().height * f3);
        }
        this.introView.setTranslationY((-AndroidUtilities.dp(20.0f)) * floatValue);
        float f4 = (f3 * 0.05f) + 0.95f;
        this.introView.setScaleX(f4);
        this.introView.setScaleY(f4);
        marginLayoutParams.width = (int) (i3 + ((i4 - i3) * floatValue));
        marginLayoutParams.height = (int) (i5 + ((i4 - i5) * floatValue));
        transformableLoginButtonView.requestLayout();
        transformableLoginButtonView.setProgress(floatValue);
        transformableLoginButtonView.setTranslationX(f + ((i6 - f) * floatValue));
        transformableLoginButtonView.setTranslationY(f2 + ((i7 - f2) * floatValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColors() {
        Activity parentActivity = getParentActivity();
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = parentActivity.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        this.backButtonView.setColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.backButtonView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
        this.radialProgressView.setProgressColor(Theme.getColor("chats_actionBackground"));
        this.floatingButtonIcon.setColor(Theme.getColor("chats_actionIcon"));
        this.floatingButtonIcon.setBackgroundColor(Theme.getColor("chats_actionBackground"));
        this.floatingProgressView.setProgressColor(Theme.getColor("chats_actionIcon"));
        for (SlideView slideView : this.views) {
            slideView.updateColors();
        }
        this.keyboardView.updateColors();
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView != null) {
            phoneNumberConfirmView.updateColors();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda20
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                LoginActivity.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText6", "windowBackgroundWhiteHintText", "listSelectorSDK21", "chats_actionBackground", "chats_actionIcon", "windowBackgroundWhiteInputField", "windowBackgroundWhiteInputFieldActivated", "windowBackgroundWhiteValueText", "dialogTextRed", "windowBackgroundWhiteGrayText", "checkbox", "windowBackgroundWhiteBlueText4", "changephoneinfo_image2", "chats_actionPressedBackground", "windowBackgroundWhiteRedText2", "windowBackgroundWhiteLinkText", "checkboxSquareUnchecked", "checkboxSquareBackground", "checkboxSquareCheck", "dialogBackground", "dialogTextGray2", "dialogTextBlack");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryResetAccount(final String str, final String str2, final String str3) {
        if (this.radialProgressView.getTag() != null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
        builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
        builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda7
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LoginActivity.this.lambda$tryResetAccount$20(str, str2, str3, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$20(final String str, final String str2, final String str3, DialogInterface dialogInterface, int i) {
        needShowProgress(0);
        TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
        tLRPC$TL_account_deleteAccount.reason = "Forgot password";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda19
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LoginActivity.this.lambda$tryResetAccount$19(str, str2, str3, tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$19(final String str, final String str2, final String str3, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.this.lambda$tryResetAccount$18(tLRPC$TL_error, str, str2, str3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$18(TLRPC$TL_error tLRPC$TL_error, String str, String str2, String str3) {
        needHideProgress(false);
        if (tLRPC$TL_error == null) {
            if (str == null || str2 == null || str3 == null) {
                setPage(0, true, null, true);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("phoneFormated", str);
            bundle.putString("phoneHash", str2);
            bundle.putString("code", str3);
            setPage(5, true, bundle, false);
        } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
            needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
        } else if (tLRPC$TL_error.text.startsWith("2FA_CONFIRM_WAIT_")) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("phoneFormated", str);
            bundle2.putString("phoneHash", str2);
            bundle2.putString("code", str3);
            bundle2.putInt("startTime", ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            bundle2.putInt("waitTime", Utilities.parseInt((CharSequence) tLRPC$TL_error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
            setPage(8, true, bundle2, false);
        } else {
            needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), tLRPC$TL_error.text);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class PhoneNumberConfirmView extends FrameLayout {
        private View blurredView;
        private IConfirmDialogCallback callback;
        private TextView confirmMessageView;
        private TextView confirmTextView;
        private View dimmView;
        private boolean dismissed;
        private TextView editTextView;
        private View fabContainer;
        private TransformableLoginButtonView fabTransform;
        private RadialProgressView floatingProgressView;
        private ViewGroup fragmentView;
        private TextView numberView;
        private FrameLayout popupFabContainer;
        private FrameLayout popupLayout;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public interface IConfirmDialogCallback {
            void onConfirmPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onDismiss(PhoneNumberConfirmView phoneNumberConfirmView);

            void onEditPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onFabPressed(PhoneNumberConfirmView phoneNumberConfirmView, TransformableLoginButtonView transformableLoginButtonView);
        }

        private PhoneNumberConfirmView(Context context, ViewGroup viewGroup, View view, String str, final IConfirmDialogCallback iConfirmDialogCallback) {
            super(context);
            this.fragmentView = viewGroup;
            this.fabContainer = view;
            this.callback = iConfirmDialogCallback;
            View view2 = new View(getContext());
            this.blurredView = view2;
            view2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$new$0(view3);
                }
            });
            addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
            View view3 = new View(getContext());
            this.dimmView = view3;
            view3.setBackgroundColor(NUM);
            this.dimmView.setAlpha(0.0f);
            addView(this.dimmView, LayoutHelper.createFrame(-1, -1.0f));
            TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(getContext());
            this.fabTransform = transformableLoginButtonView;
            transformableLoginButtonView.setTransformType(1);
            this.fabTransform.setDrawBackground(false);
            FrameLayout frameLayout = new FrameLayout(context);
            this.popupFabContainer = frameLayout;
            frameLayout.addView(this.fabTransform, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$new$1(iConfirmDialogCallback, view4);
                }
            });
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.floatingProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(22.0f));
            this.floatingProgressView.setAlpha(0.0f);
            this.floatingProgressView.setScaleX(0.1f);
            this.floatingProgressView.setScaleY(0.1f);
            this.popupFabContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setContentDescription(LocaleController.getString(R.string.Done));
            View view4 = this.popupFabContainer;
            int i = Build.VERSION.SDK_INT;
            addView(view4, LayoutHelper.createFrame(i >= 21 ? 56 : 60, i >= 21 ? 56.0f : 60.0f));
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.popupLayout = frameLayout2;
            addView(frameLayout2, LayoutHelper.createFrame(-1, 140.0f, 49, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context);
            this.confirmMessageView = textView;
            textView.setText(LocaleController.getString(R.string.ConfirmCorrectNumber));
            this.confirmMessageView.setTextSize(1, 14.0f);
            this.confirmMessageView.setSingleLine();
            int i2 = 5;
            this.popupLayout.addView(this.confirmMessageView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 20.0f, 24.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.numberView = textView2;
            textView2.setText(str);
            this.numberView.setTextSize(1, 18.0f);
            this.numberView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.numberView.setSingleLine();
            this.popupLayout.addView(this.numberView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 48.0f, 24.0f, 0.0f));
            int dp = AndroidUtilities.dp(16.0f);
            TextView textView3 = new TextView(context);
            this.editTextView = textView3;
            textView3.setText(LocaleController.getString(R.string.Edit));
            this.editTextView.setSingleLine();
            this.editTextView.setTextSize(1, 16.0f);
            this.editTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.editTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view5) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$new$2(iConfirmDialogCallback, view5);
                }
            });
            this.editTextView.setTypeface(Typeface.DEFAULT_BOLD);
            int i3 = dp / 2;
            this.editTextView.setPadding(dp, i3, dp, i3);
            float f = 8;
            this.popupLayout.addView(this.editTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, f, f, f, f));
            TextView textView4 = new TextView(context);
            this.confirmTextView = textView4;
            textView4.setText(LocaleController.getString(R.string.CheckPhoneNumberYes));
            this.confirmTextView.setSingleLine();
            this.confirmTextView.setTextSize(1, 16.0f);
            this.confirmTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.confirmTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view5) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$new$3(iConfirmDialogCallback, view5);
                }
            });
            this.confirmTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.confirmTextView.setPadding(dp, i3, dp, i3);
            this.popupLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i2) | 80, f, f, f, f));
            updateFabPosition();
            updateColors();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            dismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onFabPressed(this, this.fabTransform);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onEditPressed(this, this.editTextView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onConfirmPressed(this, this.confirmTextView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateFabPosition() {
            int[] iArr = new int[2];
            this.fragmentView.getLocationInWindow(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            this.fabContainer.getLocationInWindow(iArr);
            this.popupFabContainer.setTranslationX(iArr[0] - i);
            this.popupFabContainer.setTranslationY(iArr[1] - i2);
            requestLayout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateColors() {
            this.fabTransform.setColor(Theme.getColor("chats_actionIcon"));
            this.fabTransform.setBackgroundColor(Theme.getColor("chats_actionBackground"));
            this.popupLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.0f), Theme.getColor("dialogBackground")));
            this.confirmMessageView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.numberView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.editTextView.setTextColor(Theme.getColor("changephoneinfo_image2"));
            this.confirmTextView.setTextColor(Theme.getColor("changephoneinfo_image2"));
            this.popupFabContainer.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground")));
            this.floatingProgressView.setProgressColor(Theme.getColor("chats_actionIcon"));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.popupLayout.getMeasuredHeight();
            int translationY = (int) (this.popupFabContainer.getTranslationY() - AndroidUtilities.dp(32.0f));
            FrameLayout frameLayout = this.popupLayout;
            frameLayout.layout(frameLayout.getLeft(), translationY - measuredHeight, this.popupLayout.getRight(), translationY);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void show() {
            if (Build.VERSION.SDK_INT >= 21) {
                View view = this.fabContainer;
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, view.getTranslationZ(), 0.0f).setDuration(150L).start();
            }
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(250L);
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(8);
                    int measuredWidth = (int) (PhoneNumberConfirmView.this.fragmentView.getMeasuredWidth() / 10.0f);
                    int measuredHeight = (int) (PhoneNumberConfirmView.this.fragmentView.getMeasuredHeight() / 10.0f);
                    Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.scale(0.1f, 0.1f);
                    canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                    PhoneNumberConfirmView.this.fragmentView.draw(canvas);
                    Utilities.stackBlurBitmap(createBitmap, Math.max(8, Math.max(measuredWidth, measuredHeight) / 150));
                    PhoneNumberConfirmView.this.blurredView.setBackground(new BitmapDrawable(PhoneNumberConfirmView.this.getContext().getResources(), createBitmap));
                    PhoneNumberConfirmView.this.blurredView.setAlpha(0.0f);
                    PhoneNumberConfirmView.this.blurredView.setVisibility(0);
                    PhoneNumberConfirmView.this.fragmentView.addView(PhoneNumberConfirmView.this);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                        PhoneNumberConfirmView.this.popupFabContainer.requestFocus();
                    }
                }
            });
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$show$4(valueAnimator);
                }
            });
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$show$4(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.fabTransform.setProgress(floatValue);
            this.blurredView.setAlpha(floatValue);
            this.dimmView.setAlpha(floatValue);
            this.popupLayout.setAlpha(floatValue);
            float f = (floatValue * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(f);
            this.popupLayout.setScaleY(f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void animateProgress(final Runnable runnable) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    runnable.run();
                }
            });
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$animateProgress$5(valueAnimator);
                }
            });
            ofFloat.setDuration(150L);
            ofFloat.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateProgress$5(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f = 1.0f - floatValue;
            float f2 = (f * 0.9f) + 0.1f;
            this.fabTransform.setScaleX(f2);
            this.fabTransform.setScaleY(f2);
            this.fabTransform.setAlpha(f);
            float f3 = (0.9f * floatValue) + 0.1f;
            this.floatingProgressView.setScaleX(f3);
            this.floatingProgressView.setScaleY(f3);
            this.floatingProgressView.setAlpha(floatValue);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dismiss() {
            if (this.dismissed) {
                return;
            }
            this.dismissed = true;
            this.callback.onDismiss(this);
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(250L);
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (PhoneNumberConfirmView.this.getParent() instanceof ViewGroup) {
                        ((ViewGroup) PhoneNumberConfirmView.this.getParent()).removeView(PhoneNumberConfirmView.this);
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        ObjectAnimator.ofFloat(PhoneNumberConfirmView.this.fabContainer, View.TRANSLATION_Z, 0.0f, AndroidUtilities.dp(2.0f)).setDuration(150L).start();
                    }
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(0);
                }
            });
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.lambda$dismiss$6(valueAnimator);
                }
            });
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dismiss$6(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.blurredView.setAlpha(floatValue);
            this.dimmView.setAlpha(floatValue);
            this.fabTransform.setProgress(floatValue);
            this.popupLayout.setAlpha(floatValue);
            float f = (floatValue * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(f);
            this.popupLayout.setScaleY(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class PhoneInputData {
        private CountrySelectActivity.Country country;
        private List<String> patterns;
        private String phoneNumber;

        private PhoneInputData() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) > 0.699999988079071d;
    }
}
