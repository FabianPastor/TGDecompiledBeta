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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import java.util.ArrayList;
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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_changePhone;
import org.telegram.tgnet.TLRPC$TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC$TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
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
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeMissedCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_signIn;
import org.telegram.tgnet.TLRPC$TL_auth_signUp;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_countriesList;
import org.telegram.tgnet.TLRPC$TL_help_country;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_nearestDc;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
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

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final int SHOW_DELAY = (SharedConfig.getDevicePerformanceClass() <= 1 ? 150 : 100);
    /* access modifiers changed from: private */
    public int activityMode = 0;
    /* access modifiers changed from: private */
    public Runnable animationFinishCallback;
    /* access modifiers changed from: private */
    public ImageView backButtonView;
    private AlertDialog cancelDeleteProgressDialog;
    private TLRPC$TL_auth_sentCode cancelDeletionCode;
    private Bundle cancelDeletionParams;
    /* access modifiers changed from: private */
    public String cancelDeletionPhone;
    /* access modifiers changed from: private */
    public boolean checkPermissions = true;
    /* access modifiers changed from: private */
    public boolean checkShowPermissions = true;
    /* access modifiers changed from: private */
    public int currentDoneType;
    /* access modifiers changed from: private */
    public TLRPC$TL_help_termsOfService currentTermsOfService;
    /* access modifiers changed from: private */
    public int currentViewNum;
    private boolean customKeyboardWasVisible = false;
    private boolean[] doneButtonVisible = {true, false};
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    private boolean[] doneProgressVisible = new boolean[2];
    private Runnable[] editDoneCallback = new Runnable[2];
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public TransformableLoginButtonView floatingButtonIcon;
    /* access modifiers changed from: private */
    public RadialProgressView floatingProgressView;
    private View introView;
    /* access modifiers changed from: private */
    public boolean isAnimatingIntro;
    /* access modifiers changed from: private */
    public Runnable keyboardHideCallback;
    /* access modifiers changed from: private */
    public LinearLayout keyboardLinearLayout;
    /* access modifiers changed from: private */
    public CustomPhoneKeyboardView keyboardView;
    /* access modifiers changed from: private */
    public boolean needRequestPermissions;
    /* access modifiers changed from: private */
    public boolean newAccount;
    /* access modifiers changed from: private */
    public Dialog permissionsDialog;
    /* access modifiers changed from: private */
    public ArrayList<String> permissionsItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public Dialog permissionsShowDialog;
    /* access modifiers changed from: private */
    public ArrayList<String> permissionsShowItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public PhoneNumberConfirmView phoneNumberConfirmView;
    private boolean[] postedEditDoneCallback = new boolean[2];
    private int progressRequestId;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    /* access modifiers changed from: private */
    public boolean restoringState;
    /* access modifiers changed from: private */
    public AnimatorSet[] showDoneAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public FrameLayout slideViewsContainer;
    /* access modifiers changed from: private */
    public TextView startMessagingButton;
    /* access modifiers changed from: private */
    public boolean syncContacts = true;
    /* access modifiers changed from: private */
    public boolean testBackend = false;
    /* access modifiers changed from: private */
    public SlideView[] views = new SlideView[12];

    private static class ProgressView extends View {
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public LoginActivity() {
    }

    public LoginActivity(int i) {
        this.currentAccount = i;
        this.newAccount = true;
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

    /* access modifiers changed from: private */
    public boolean isInCancelAccountDeletionMode() {
        return this.activityMode == 1;
    }

    public void onFragmentDestroy() {
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

    /* JADX WARNING: Code restructure failed: missing block: B:109:0x03e9, code lost:
        if (r1 != 4) goto L_0x03f0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x03e3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0418  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x040d A[EDGE_INSN: B:125:0x040d->B:117:0x040d ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x01da  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x025a  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x025f  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0265  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0336  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x033d  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x037f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0387  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r32) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 0
            r2.setAddToContainer(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.LoginActivity$1 r4 = new org.telegram.ui.LoginActivity$1
            r4.<init>()
            r2.setActionBarMenuOnItemClick(r4)
            r0.currentDoneType = r3
            boolean[] r2 = r0.doneButtonVisible
            r4 = 1
            r2[r3] = r4
            r2[r4] = r3
            org.telegram.ui.LoginActivity$2 r2 = new org.telegram.ui.LoginActivity$2
            r2.<init>(r1)
            r0.sizeNotifierFrameLayout = r2
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda21 r5 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda21
            r5.<init>(r0)
            r2.setDelegate(r5)
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = r0.sizeNotifierFrameLayout
            r0.fragmentView = r2
            org.telegram.ui.LoginActivity$3 r2 = new org.telegram.ui.LoginActivity$3
            r2.<init>(r1)
            r2.setFillViewport(r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r0.sizeNotifierFrameLayout
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7)
            r5.addView(r2, r8)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r0.keyboardLinearLayout = r5
            r5.setOrientation(r4)
            android.widget.LinearLayout r5 = r0.keyboardLinearLayout
            r8 = -2
            r9 = 51
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createScroll(r6, r8, r9)
            r2.addView(r5, r8)
            android.widget.Space r2 = new android.widget.Space
            r2.<init>(r1)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0067
            r5 = 0
            goto L_0x0069
        L_0x0067:
            int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0069:
            r2.setMinimumHeight(r5)
            android.widget.LinearLayout r5 = r0.keyboardLinearLayout
            r5.addView(r2)
            org.telegram.ui.LoginActivity$4 r2 = new org.telegram.ui.LoginActivity$4
            r2.<init>(r1)
            r0.slideViewsContainer = r2
            android.widget.LinearLayout r5 = r0.keyboardLinearLayout
            r8 = 1065353216(0x3var_, float:1.0)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r3, (float) r8)
            r5.addView(r2, r9)
            org.telegram.ui.Components.CustomPhoneKeyboardView r2 = new org.telegram.ui.Components.CustomPhoneKeyboardView
            r2.<init>(r1)
            r0.keyboardView = r2
            android.widget.LinearLayout r5 = r0.keyboardLinearLayout
            r9 = 230(0xe6, float:3.22E-43)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r9)
            r5.addView(r2, r9)
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$PhoneView r5 = new org.telegram.ui.LoginActivity$PhoneView
            r5.<init>(r0, r1)
            r2[r3] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r5.<init>(r0, r1, r4)
            r2[r4] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r9 = 2
            r5.<init>(r0, r1, r9)
            r2[r9] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r10 = 3
            r5.<init>(r0, r1, r10)
            r2[r10] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r11 = 4
            r5.<init>(r0, r1, r11)
            r2[r11] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRegisterView r5 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView
            r5.<init>(r0, r1)
            r12 = 5
            r2[r12] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r5 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView
            r5.<init>(r0, r1)
            r13 = 6
            r2[r13] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r5 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView
            r5.<init>(r0, r1)
            r14 = 7
            r2[r14] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityResetWaitView r5 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView
            r5.<init>(r0, r1)
            r15 = 8
            r2[r15] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r5 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r5.<init>(r0, r1, r3)
            r15 = 9
            r2[r15] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r5 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r5.<init>(r0, r1, r4)
            r10 = 10
            r2[r10] = r5
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r10 = 11
            r5.<init>(r0, r1, r10)
            r2[r10] = r5
            r2 = 0
        L_0x0110:
            org.telegram.ui.Components.SlideView[] r5 = r0.views
            int r10 = r5.length
            if (r2 >= r10) goto L_0x0157
            r5 = r5[r2]
            if (r2 != 0) goto L_0x011b
            r10 = 0
            goto L_0x011d
        L_0x011b:
            r10 = 8
        L_0x011d:
            r5.setVisibility(r10)
            android.widget.FrameLayout r5 = r0.slideViewsContainer
            org.telegram.ui.Components.SlideView[] r10 = r0.views
            r10 = r10[r2]
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 17
            boolean r19 = org.telegram.messenger.AndroidUtilities.isTablet()
            r20 = 1104150528(0x41d00000, float:26.0)
            r21 = 1099956224(0x41900000, float:18.0)
            if (r19 == 0) goto L_0x0139
            r19 = 1104150528(0x41d00000, float:26.0)
            goto L_0x013b
        L_0x0139:
            r19 = 1099956224(0x41900000, float:18.0)
        L_0x013b:
            r22 = 1106247680(0x41var_, float:30.0)
            boolean r23 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r23 == 0) goto L_0x0145
            r21 = 1104150528(0x41d00000, float:26.0)
        L_0x0145:
            r23 = 0
            r20 = r22
            r22 = r23
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r5.addView(r10, r15)
            int r2 = r2 + 1
            r15 = 9
            goto L_0x0110
        L_0x0157:
            int r2 = r0.activityMode
            if (r2 != 0) goto L_0x0162
            boolean r2 = r0.newAccount
            android.os.Bundle r2 = loadCurrentState(r2)
            goto L_0x0163
        L_0x0162:
            r2 = 0
        L_0x0163:
            if (r2 == 0) goto L_0x01cc
            java.lang.String r10 = "currentViewNum"
            int r10 = r2.getInt(r10, r3)
            r0.currentViewNum = r10
            java.lang.String r10 = "syncContacts"
            int r10 = r2.getInt(r10, r4)
            if (r10 != r4) goto L_0x0177
            r10 = 1
            goto L_0x0178
        L_0x0177:
            r10 = 0
        L_0x0178:
            r0.syncContacts = r10
            int r10 = r0.currentViewNum
            if (r10 < r4) goto L_0x01a3
            if (r10 > r11) goto L_0x01a3
            java.lang.String r10 = "open"
            int r10 = r2.getInt(r10)
            if (r10 == 0) goto L_0x01cc
            long r14 = java.lang.System.currentTimeMillis()
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            long r5 = (long) r10
            long r14 = r14 - r5
            long r5 = java.lang.Math.abs(r14)
            r14 = 86400(0x15180, double:4.26873E-319)
            int r10 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r10 < 0) goto L_0x01cc
            r0.currentViewNum = r3
            r31.clearCurrentState()
            goto L_0x01ca
        L_0x01a3:
            if (r10 != r13) goto L_0x01b7
            org.telegram.ui.Components.SlideView[] r5 = r0.views
            r5 = r5[r13]
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r5 = (org.telegram.ui.LoginActivity.LoginActivityPasswordView) r5
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r5.currentPassword
            if (r5 != 0) goto L_0x01cc
            r0.currentViewNum = r3
            r31.clearCurrentState()
            goto L_0x01ca
        L_0x01b7:
            if (r10 != r14) goto L_0x01cc
            org.telegram.ui.Components.SlideView[] r5 = r0.views
            r5 = r5[r14]
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r5 = (org.telegram.ui.LoginActivity.LoginActivityRecoverView) r5
            java.lang.String r5 = r5.passwordString
            if (r5 != 0) goto L_0x01cc
            r0.currentViewNum = r3
            r31.clearCurrentState()
        L_0x01ca:
            r5 = 0
            goto L_0x01cd
        L_0x01cc:
            r5 = r2
        L_0x01cd:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.floatingButtonContainer = r2
            boolean[] r6 = r0.doneButtonVisible
            boolean r6 = r6[r3]
            if (r6 == 0) goto L_0x01dc
            r6 = 0
            goto L_0x01de
        L_0x01dc:
            r6 = 8
        L_0x01de:
            r2.setVisibility(r6)
            int r2 = android.os.Build.VERSION.SDK_INT
            r6 = 1082130432(0x40800000, float:4.0)
            r10 = 21
            if (r2 < r10) goto L_0x024a
            android.animation.StateListAnimator r14 = new android.animation.StateListAnimator
            r14.<init>()
            int[] r15 = new int[r4]
            r17 = 16842919(0x10100a7, float:2.3694026E-38)
            r15[r3] = r17
            org.telegram.ui.Components.TransformableLoginButtonView r13 = r0.floatingButtonIcon
            float[] r12 = new float[r9]
            r19 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r7 = (float) r7
            r12[r3] = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            r12[r4] = r7
            java.lang.String r7 = "translationZ"
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r13, r7, r12)
            r10 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r10 = r12.setDuration(r10)
            r14.addState(r15, r10)
            int[] r10 = new int[r3]
            org.telegram.ui.Components.TransformableLoginButtonView r11 = r0.floatingButtonIcon
            float[] r12 = new float[r9]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r15 = (float) r15
            r12[r3] = r15
            r15 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r12[r4] = r15
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r11, r7, r12)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r7 = r7.setDuration(r11)
            r14.addState(r10, r7)
            android.widget.FrameLayout r7 = r0.floatingButtonContainer
            r7.setStateListAnimator(r14)
            android.widget.FrameLayout r7 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$5 r10 = new org.telegram.ui.LoginActivity$5
            r10.<init>(r0)
            r7.setOutlineProvider(r10)
        L_0x024a:
            android.widget.FrameLayout r7 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator r7 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r7)
            r0.floatingAutoAnimator = r7
            org.telegram.ui.Components.SizeNotifierFrameLayout r7 = r0.sizeNotifierFrameLayout
            android.widget.FrameLayout r10 = r0.floatingButtonContainer
            r11 = 21
            if (r2 < r11) goto L_0x025f
            r12 = 56
            r24 = 56
            goto L_0x0263
        L_0x025f:
            r12 = 60
            r24 = 60
        L_0x0263:
            if (r2 < r11) goto L_0x026a
            r11 = 1113587712(0x42600000, float:56.0)
            r25 = 1113587712(0x42600000, float:56.0)
            goto L_0x026e
        L_0x026a:
            r11 = 1114636288(0x42700000, float:60.0)
            r25 = 1114636288(0x42700000, float:60.0)
        L_0x026e:
            r26 = 85
            r27 = 0
            r28 = 0
            r29 = 1103101952(0x41CLASSNAME, float:24.0)
            r30 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r7.addView(r10, r11)
            android.widget.FrameLayout r7 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda9 r10 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda9
            r10.<init>(r0)
            r7.setOnClickListener(r10)
            org.telegram.ui.Components.VerticalPositionAutoAnimator r7 = r0.floatingAutoAnimator
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda11 r10 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda11
            r10.<init>(r0)
            r7.addUpdateListener(r10)
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r1)
            r0.backButtonView = r7
            r10 = 2131165449(0x7var_, float:1.7945115E38)
            r7.setImageResource(r10)
            android.widget.ImageView r7 = r0.backButtonView
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda10 r10 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda10
            r10.<init>(r0)
            r7.setOnClickListener(r10)
            android.widget.ImageView r7 = r0.backButtonView
            r10 = 2131624636(0x7f0e02bc, float:1.8876457E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
            r7.setContentDescription(r10)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.widget.ImageView r7 = r0.backButtonView
            r7.setPadding(r6, r6, r6, r6)
            org.telegram.ui.Components.SizeNotifierFrameLayout r6 = r0.sizeNotifierFrameLayout
            android.widget.ImageView r7 = r0.backButtonView
            r24 = 32
            r25 = 1107296256(0x42000000, float:32.0)
            r26 = 51
            r27 = 1098907648(0x41800000, float:16.0)
            r28 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r6.addView(r7, r10)
            org.telegram.ui.Components.RadialProgressView r6 = new org.telegram.ui.Components.RadialProgressView
            r6.<init>(r1)
            r0.radialProgressView = r6
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setSize(r7)
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            r7 = 0
            r6.setAlpha(r7)
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            r7 = 1036831949(0x3dcccccd, float:0.1)
            r6.setScaleX(r7)
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            r6.setScaleY(r7)
            org.telegram.ui.Components.SizeNotifierFrameLayout r6 = r0.sizeNotifierFrameLayout
            org.telegram.ui.Components.RadialProgressView r10 = r0.radialProgressView
            r26 = 53
            r27 = 0
            r29 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r6.addView(r10, r11)
            org.telegram.ui.Components.TransformableLoginButtonView r6 = new org.telegram.ui.Components.TransformableLoginButtonView
            r6.<init>(r1)
            r0.floatingButtonIcon = r6
            r6.setTransformType(r3)
            org.telegram.ui.Components.TransformableLoginButtonView r6 = r0.floatingButtonIcon
            r6.setProgress(r8)
            org.telegram.ui.Components.TransformableLoginButtonView r6 = r0.floatingButtonIcon
            r6.setDrawBackground(r3)
            android.widget.FrameLayout r6 = r0.floatingButtonContainer
            r8 = 2131625525(0x7f0e0635, float:1.887826E38)
            java.lang.String r10 = "Done"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r6.setContentDescription(r8)
            android.widget.FrameLayout r6 = r0.floatingButtonContainer
            org.telegram.ui.Components.TransformableLoginButtonView r8 = r0.floatingButtonIcon
            r10 = 21
            if (r2 < r10) goto L_0x0339
            r11 = 56
            goto L_0x033b
        L_0x0339:
            r11 = 60
        L_0x033b:
            if (r2 < r10) goto L_0x0340
            r2 = 1113587712(0x42600000, float:56.0)
            goto L_0x0342
        L_0x0340:
            r2 = 1114636288(0x42700000, float:60.0)
        L_0x0342:
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r2)
            r6.addView(r8, r2)
            org.telegram.ui.Components.RadialProgressView r2 = new org.telegram.ui.Components.RadialProgressView
            r2.<init>(r1)
            r0.floatingProgressView = r2
            r1 = 1102053376(0x41b00000, float:22.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r2 = 0
            r1.setAlpha(r2)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r1.setScaleX(r7)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r1.setScaleY(r7)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r2 = 4
            r1.setVisibility(r2)
            android.widget.FrameLayout r1 = r0.floatingButtonContainer
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6)
            r1.addView(r2, r6)
            if (r5 == 0) goto L_0x0381
            r0.restoringState = r4
        L_0x0381:
            r1 = 0
        L_0x0382:
            org.telegram.ui.Components.SlideView[] r2 = r0.views
            int r6 = r2.length
            if (r1 >= r6) goto L_0x040d
            r2 = r2[r1]
            if (r5 == 0) goto L_0x039b
            if (r1 < r4) goto L_0x0398
            r6 = 4
            if (r1 > r6) goto L_0x0398
            int r6 = r0.currentViewNum
            if (r1 != r6) goto L_0x039b
            r2.restoreStateParams(r5)
            goto L_0x039b
        L_0x0398:
            r2.restoreStateParams(r5)
        L_0x039b:
            int r6 = r0.currentViewNum
            if (r6 != r1) goto L_0x03f3
            android.widget.ImageView r6 = r0.backButtonView
            boolean r7 = r2.needBackButton()
            if (r7 != 0) goto L_0x03b3
            boolean r7 = r0.newAccount
            if (r7 != 0) goto L_0x03b3
            int r7 = r0.activityMode
            if (r7 != r9) goto L_0x03b0
            goto L_0x03b3
        L_0x03b0:
            r7 = 8
            goto L_0x03b4
        L_0x03b3:
            r7 = 0
        L_0x03b4:
            r6.setVisibility(r7)
            r2.setVisibility(r3)
            r2.onShow()
            boolean r2 = r2.hasCustomKeyboard()
            r0.setCustomKeyboardVisible(r2, r3)
            r0.currentDoneType = r3
            r6 = 5
            r7 = 6
            if (r1 == 0) goto L_0x03d9
            if (r1 == r6) goto L_0x03d9
            r8 = 9
            if (r1 == r7) goto L_0x03db
            r10 = 10
            if (r1 == r8) goto L_0x03dd
            if (r1 != r10) goto L_0x03d7
            goto L_0x03dd
        L_0x03d7:
            r2 = 0
            goto L_0x03de
        L_0x03d9:
            r8 = 9
        L_0x03db:
            r10 = 10
        L_0x03dd:
            r2 = 1
        L_0x03de:
            r0.showDoneButton(r2, r3)
            if (r1 == r4) goto L_0x03ec
            if (r1 == r9) goto L_0x03ec
            r11 = 3
            r12 = 4
            if (r1 == r11) goto L_0x03ee
            if (r1 != r12) goto L_0x03f0
            goto L_0x03ee
        L_0x03ec:
            r11 = 3
            r12 = 4
        L_0x03ee:
            r0.currentDoneType = r4
        L_0x03f0:
            r14 = 8
            goto L_0x0409
        L_0x03f3:
            r6 = 5
            r7 = 6
            r8 = 9
            r10 = 10
            r11 = 3
            r12 = 4
            int r13 = r2.getVisibility()
            r14 = 8
            if (r13 == r14) goto L_0x0409
            r2.setVisibility(r14)
            r2.onHide()
        L_0x0409:
            int r1 = r1 + 1
            goto L_0x0382
        L_0x040d:
            r0.restoringState = r3
            r31.updateColors()
            boolean r1 = r31.isInCancelAccountDeletionMode()
            if (r1 == 0) goto L_0x041f
            android.os.Bundle r1 = r0.cancelDeletionParams
            org.telegram.tgnet.TLRPC$TL_auth_sentCode r2 = r0.cancelDeletionCode
            r0.fillNextCodeParams(r1, r2, r3)
        L_0x041f:
            android.view.View r1 = r0.fragmentView
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, boolean z) {
        Runnable runnable;
        if (i > AndroidUtilities.dp(20.0f) && isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
        if (i <= AndroidUtilities.dp(20.0f) && (runnable = this.keyboardHideCallback) != null) {
            runnable.run();
            this.keyboardHideCallback = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        onDoneButtonPressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DynamicAnimation dynamicAnimation, float f, float f2) {
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.updateFabPosition();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        if (onBackPressed()) {
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardForceDisabled() {
        Point point = AndroidUtilities.displaySize;
        return point.x > point.y || AndroidUtilities.isTablet() || AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    /* access modifiers changed from: private */
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
                    ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                    duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    duration.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda1(this));
                    duration.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animator) {
                            LoginActivity.this.keyboardView.setVisibility(0);
                        }
                    });
                    duration.start();
                    return;
                }
                this.keyboardView.setVisibility(0);
                return;
            }
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
            if (z2) {
                ValueAnimator duration2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(300);
                duration2.setInterpolator(Easings.easeInOutQuad);
                duration2.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda2(this));
                duration2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        LoginActivity.this.keyboardView.setVisibility(8);
                    }
                });
                duration2.start();
                return;
            }
            this.keyboardView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCustomKeyboardVisible$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * ((float) AndroidUtilities.dp(230.0f)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCustomKeyboardVisible$5(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * ((float) AndroidUtilities.dp(230.0f)));
    }

    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        SlideView slideView;
        int access$1000;
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.fragmentView.requestLayout();
        try {
            int i = this.currentViewNum;
            if (i >= 1 && i <= 4) {
                SlideView[] slideViewArr = this.views;
                if ((slideViewArr[i] instanceof LoginActivitySmsView) && (access$1000 = ((LoginActivitySmsView) slideViewArr[i]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) access$1000)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, (Bundle) null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        int i2 = this.currentViewNum;
        if (i2 == 0 && !this.needRequestPermissions && (slideView = this.views[i2]) != null) {
            slideView.onShow();
        }
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        setCustomKeyboardVisible(this.views[this.currentViewNum].hasCustomKeyboard(), false);
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.dismiss();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (strArr.length != 0 && iArr.length != 0) {
            boolean z = iArr[0] == 0;
            if (i == 6) {
                this.checkPermissions = false;
                int i2 = this.currentViewNum;
                if (i2 == 0) {
                    boolean unused = ((PhoneView) this.views[i2]).confirmedNumber = true;
                    this.views[this.currentViewNum].onNextPressed((String) null);
                }
            } else if (i == 7) {
                this.checkShowPermissions = false;
                int i3 = this.currentViewNum;
                if (i3 == 0) {
                    ((PhoneView) this.views[i3]).fillNumber();
                }
            } else if (i == 20) {
                if (z) {
                    ((LoginActivityRegisterView) this.views[5]).imageUpdater.openCamera();
                }
            } else if (i == 151 && z) {
                LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
                loginActivityRegisterView.post(new LoginActivity$$ExternalSyntheticLambda14(loginActivityRegisterView));
            }
        }
    }

    public static Bundle loadCurrentState(boolean z) {
        if (z) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Map.Entry next : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String str = (String) next.getKey();
                Object value = next.getValue();
                String[] split = str.split("_\\|_");
                if (split.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(str, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(str, ((Integer) value).intValue());
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
            FileLog.e((Throwable) e);
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

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
            } catch (Exception unused) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda15(this), 200);
            getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDialogDismiss$7() {
        this.needRequestPermissions = false;
    }

    public boolean onBackPressed() {
        int i = this.currentViewNum;
        int i2 = 0;
        if (i == 0) {
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
        } else {
            if (i == 6) {
                this.views[i].onBackPressed(true);
                setPage(0, true, (Bundle) null, true);
            } else if (i == 7 || i == 8) {
                this.views[i].onBackPressed(true);
                setPage(6, true, (Bundle) null, true);
            } else if ((i < 1 || i > 4) && i != 11) {
                if (i == 5) {
                    ((LoginActivityRegisterView) this.views[i]).wrongNumber.callOnClick();
                } else if (i == 9) {
                    this.views[i].onBackPressed(true);
                    setPage(7, true, (Bundle) null, true);
                } else if (i == 10) {
                    this.views[i].onBackPressed(true);
                    setPage(9, true, (Bundle) null, true);
                }
            } else if (this.views[i].onBackPressed(false)) {
                setPage(0, true, (Bundle) null, true);
            }
            return false;
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
        if (loginActivityRegisterView != null) {
            loginActivityRegisterView.imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: private */
    public void needShowAlert(String str, String str2) {
        if (str2 != null && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(str);
            builder.setMessage(str2);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void onFieldError(View view, boolean z) {
        view.performHapticFeedback(3, 2);
        AndroidUtilities.shakeViewSpring(view, 3.5f);
        if (z && (view instanceof OutlineTextContainerView)) {
            Runnable runnable = (Runnable) view.getTag(NUM);
            if (runnable != null) {
                view.removeCallbacks(runnable);
            }
            OutlineTextContainerView outlineTextContainerView = (OutlineTextContainerView) view;
            final AtomicReference atomicReference = new AtomicReference();
            final EditText attachedEditText = outlineTextContainerView.getAttachedEditText();
            AnonymousClass8 r3 = new TextWatcher(this) {
                public void afterTextChanged(Editable editable) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    EditText editText = attachedEditText;
                    editText.post(new LoginActivity$8$$ExternalSyntheticLambda0(this, editText, atomicReference));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$beforeTextChanged$0(EditText editText, AtomicReference atomicReference) {
                    editText.removeTextChangedListener(this);
                    editText.removeCallbacks((Runnable) atomicReference.get());
                    ((Runnable) atomicReference.get()).run();
                }
            };
            outlineTextContainerView.animateError(1.0f);
            LoginActivity$$ExternalSyntheticLambda13 loginActivity$$ExternalSyntheticLambda13 = new LoginActivity$$ExternalSyntheticLambda13(outlineTextContainerView, view, attachedEditText, r3);
            atomicReference.set(loginActivity$$ExternalSyntheticLambda13);
            view.postDelayed(loginActivity$$ExternalSyntheticLambda13, 2000);
            view.setTag(NUM, loginActivity$$ExternalSyntheticLambda13);
            if (attachedEditText != null) {
                attachedEditText.addTextChangedListener(r3);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onFieldError$9(OutlineTextContainerView outlineTextContainerView, View view, EditText editText, TextWatcher textWatcher) {
        outlineTextContainerView.animateError(0.0f);
        view.setTag(NUM, (Object) null);
        if (editText != null) {
            editText.post(new LoginActivity$$ExternalSyntheticLambda12(editText, textWatcher));
        }
    }

    public static void needShowInvalidAlert(BaseFragment baseFragment, String str, boolean z) {
        needShowInvalidAlert(baseFragment, str, (PhoneInputData) null, z);
    }

    public static void needShowInvalidAlert(BaseFragment baseFragment, String str, PhoneInputData phoneInputData, boolean z) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            if (z) {
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else if (phoneInputData == null || phoneInputData.patterns == null || phoneInputData.patterns.isEmpty() || phoneInputData.country == null) {
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString(NUM));
            } else {
                int i = Integer.MAX_VALUE;
                for (String replace : phoneInputData.patterns) {
                    int length = replace.replace(" ", "").length();
                    if (length < i) {
                        i = length;
                    }
                }
                if (PhoneFormat.stripExceptNumbers(str).length() - phoneInputData.country.code.length() < i) {
                    builder.setTitle(LocaleController.getString(NUM));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ShortNumberInfo", NUM, phoneInputData.country.name, phoneInputData.phoneNumber)));
                } else {
                    builder.setTitle(LocaleController.getString(NUM));
                    builder.setMessage(LocaleController.getString(NUM));
                }
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new LoginActivity$$ExternalSyntheticLambda8(z, str, baseFragment));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$needShowInvalidAlert$10(boolean z, String str, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
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
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString(NUM));
            builder.setMessage(LocaleController.getString("NoMailInstalled", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void showDoneButton(final boolean z, boolean z2) {
        TimeInterpolator timeInterpolator;
        int i = this.currentDoneType;
        final boolean z3 = i == 0;
        if (this.doneButtonVisible[i] != z) {
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
            if (z2) {
                this.showDoneAnimation[i2] = new AnimatorSet();
                if (z) {
                    if (z3) {
                        if (this.floatingButtonContainer.getVisibility() != 0) {
                            this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
                            this.floatingButtonContainer.setVisibility(0);
                        }
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.floatingAutoAnimator.getOffsetY(), 0.0f});
                        ofFloat.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda0(this));
                        this.showDoneAnimation[this.currentDoneType].play(ofFloat);
                    }
                } else if (z3) {
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.floatingAutoAnimator.getOffsetY(), AndroidUtilities.dpf2(70.0f)});
                    ofFloat2.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda3(this));
                    this.showDoneAnimation[this.currentDoneType].play(ofFloat2);
                }
                this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (LoginActivity.this.showDoneAnimation[!z3] != null && LoginActivity.this.showDoneAnimation[!z3].equals(animator) && !z) {
                            if (z3) {
                                LoginActivity.this.floatingButtonContainer.setVisibility(8);
                            }
                            if (z3 && LoginActivity.this.floatingButtonIcon.getAlpha() != 1.0f) {
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
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (LoginActivity.this.showDoneAnimation[!z3] != null && LoginActivity.this.showDoneAnimation[!z3].equals(animator)) {
                            LoginActivity.this.showDoneAnimation[!z3] = null;
                        }
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
                this.showDoneAnimation[this.currentDoneType].setDuration((long) i3);
                this.showDoneAnimation[this.currentDoneType].setInterpolator(timeInterpolator);
                this.showDoneAnimation[this.currentDoneType].start();
            } else if (z) {
                if (z3) {
                    this.floatingButtonContainer.setVisibility(0);
                    this.floatingAutoAnimator.setOffsetY(0.0f);
                }
            } else if (z3) {
                this.floatingButtonContainer.setVisibility(8);
                this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDoneButton$11(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(floatValue);
        this.floatingButtonContainer.setAlpha(1.0f - (floatValue / AndroidUtilities.dpf2(70.0f)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDoneButton$12(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(floatValue);
        this.floatingButtonContainer.setAlpha(1.0f - (floatValue / AndroidUtilities.dpf2(70.0f)));
    }

    /* access modifiers changed from: private */
    public void onDoneButtonPressed() {
        if (this.doneButtonVisible[this.currentDoneType]) {
            if (this.radialProgressView.getTag() == null) {
                this.views[this.currentViewNum].onNextPressed((String) null);
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString("StopLoading", NUM));
                builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$$ExternalSyntheticLambda6(this));
                showDialog(builder.create());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDoneButtonPressed$13(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        showEditDoneProgress(z, z2, false);
    }

    private void showEditDoneProgress(final boolean z, boolean z2, boolean z3) {
        if (!z2 || this.doneProgressVisible[this.currentDoneType] != z || z3) {
            int i = this.currentDoneType;
            final boolean z4 = i == 0;
            if (z3 || z4) {
                this.postedEditDoneCallback[i] = false;
                this.doneProgressVisible[i] = z;
            } else {
                this.doneProgressVisible[i] = z;
                if (z2) {
                    if (this.postedEditDoneCallback[i]) {
                        AndroidUtilities.cancelRunOnUIThread(this.editDoneCallback[i]);
                        this.postedEditDoneCallback[this.currentDoneType] = false;
                        return;
                    } else if (z) {
                        Runnable[] runnableArr = this.editDoneCallback;
                        LoginActivity$$ExternalSyntheticLambda16 loginActivity$$ExternalSyntheticLambda16 = new LoginActivity$$ExternalSyntheticLambda16(this, i, z, z2);
                        runnableArr[i] = loginActivity$$ExternalSyntheticLambda16;
                        AndroidUtilities.runOnUIThread(loginActivity$$ExternalSyntheticLambda16, 2000);
                        this.postedEditDoneCallback[this.currentDoneType] = true;
                        return;
                    }
                }
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
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        if (!z) {
                            return;
                        }
                        if (z4) {
                            LoginActivity.this.floatingButtonIcon.setVisibility(0);
                            LoginActivity.this.floatingProgressView.setVisibility(0);
                            LoginActivity.this.floatingButtonContainer.setEnabled(false);
                            return;
                        }
                        LoginActivity.this.radialProgressView.setVisibility(0);
                    }

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
                        if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animator)) {
                            AnimatorSet unused = LoginActivity.this.doneItemAnimation = null;
                        }
                    }
                });
                ofFloat.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda5(this, z4));
                this.doneItemAnimation.playTogether(new Animator[]{ofFloat});
                this.doneItemAnimation.setDuration(150);
                this.doneItemAnimation.start();
            } else if (!z) {
                this.radialProgressView.setTag((Object) null);
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
            } else if (z4) {
                this.floatingProgressView.setVisibility(0);
                this.floatingButtonIcon.setVisibility(4);
                this.floatingButtonContainer.setEnabled(false);
                this.floatingButtonIcon.setScaleX(0.1f);
                this.floatingButtonIcon.setScaleY(0.1f);
                this.floatingButtonIcon.setAlpha(0.0f);
                this.floatingProgressView.setScaleX(1.0f);
                this.floatingProgressView.setScaleY(1.0f);
                this.floatingProgressView.setAlpha(1.0f);
            } else {
                this.radialProgressView.setVisibility(0);
                this.radialProgressView.setScaleX(1.0f);
                this.radialProgressView.setScaleY(1.0f);
                this.radialProgressView.setAlpha(1.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showEditDoneProgress$14(int i, boolean z, boolean z2) {
        int i2 = this.currentDoneType;
        this.currentDoneType = i;
        showEditDoneProgress(z, z2, true);
        this.currentDoneType = i2;
    }

    /* access modifiers changed from: private */
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

    /* access modifiers changed from: private */
    public void needShowProgress(int i) {
        needShowProgress(i, true);
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int i, boolean z) {
        if (!isInCancelAccountDeletionMode() || i != 0) {
            this.progressRequestId = i;
            showEditDoneProgress(true, z);
        } else if (this.cancelDeleteProgressDialog == null && getParentActivity() != null && !getParentActivity().isFinishing()) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.cancelDeleteProgressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.cancelDeleteProgressDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean z) {
        needHideProgress(z, true);
    }

    /* access modifiers changed from: private */
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
        final boolean z3 = i == 0 || i == 5 || i == 6 || i == 9 || i == 10;
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
            slideView2.setX((float) i3);
            slideView2.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() {
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
            fArr[0] = (float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
            animatorArr[0] = ObjectAnimator.ofFloat(slideView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(slideView2, View.TRANSLATION_X, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
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
            putBundleToEditor(bundle2, edit, (String) null);
            edit.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void needFinishActivity(boolean z, boolean z2, int i) {
        if (getParentActivity() != null) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), false);
        }
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false, new LoginActivity$$ExternalSyntheticLambda18(z));
                finishFragment();
                return;
            }
            if (!z || !z2) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("afterSignup", z);
                presentFragment(new DialogsActivity(bundle), true);
            } else {
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(6, (TLRPC$TL_account_password) null);
                twoStepVerificationSetupActivity.setBlockingAlert(i);
                twoStepVerificationSetupActivity.setFromRegistration(true);
                presentFragment(twoStepVerificationSetupActivity, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ DialogsActivity lambda$needFinishActivity$16(boolean z, Void voidR) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", z);
        return new DialogsActivity(bundle);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization) {
        onAuthSuccess(tLRPC$TL_auth_authorization, false);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization, boolean z) {
        MessagesController.getInstance(this.currentAccount).cleanup();
        ConnectionsManager.getInstance(this.currentAccount).setUserId(tLRPC$TL_auth_authorization.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(tLRPC$TL_auth_authorization.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tLRPC$TL_auth_authorization.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, (ArrayList<TLRPC$Chat>) null, true, true);
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

    /* access modifiers changed from: private */
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
        }
    }

    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener, NotificationCenter.NotificationCenterDelegate {
        private ImageView chevronRight;
        private View codeDividerView;
        /* access modifiers changed from: private */
        public AnimatedPhoneNumberEditText codeField;
        /* access modifiers changed from: private */
        public HashMap<String, CountrySelectActivity.Country> codesMap = new HashMap<>();
        /* access modifiers changed from: private */
        public boolean confirmedNumber = false;
        private ArrayList<CountrySelectActivity.Country> countriesArray = new ArrayList<>();
        private TextViewSwitcher countryButton;
        private OutlineTextContainerView countryOutlineView;
        /* access modifiers changed from: private */
        public int countryState = 0;
        /* access modifiers changed from: private */
        public CountrySelectActivity.Country currentCountry;
        /* access modifiers changed from: private */
        public boolean ignoreOnPhoneChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreSelection = false;
        private boolean nextPressed = false;
        private boolean numberFilled;
        /* access modifiers changed from: private */
        public AnimatedPhoneNumberEditText phoneField;
        private HashMap<String, List<String>> phoneFormatMap = new HashMap<>();
        /* access modifiers changed from: private */
        public OutlineTextContainerView phoneOutlineView;
        private TextView plusTextView;
        private TextView subtitleView;
        private CheckBoxCell syncContactsBox;
        private CheckBoxCell testBackendCheckBox;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleView;

        public boolean hasCustomKeyboard() {
            return true;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(org.telegram.ui.LoginActivity r27, android.content.Context r28) {
            /*
                r26 = this;
                r1 = r26
                r2 = r27
                r0 = r28
                r1.this$0 = r2
                r1.<init>(r0)
                r3 = 0
                r1.countryState = r3
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r1.countriesArray = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.codesMap = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.phoneFormatMap = r4
                r1.ignoreSelection = r3
                r1.ignoreOnTextChange = r3
                r1.ignoreOnPhoneChange = r3
                r1.nextPressed = r3
                r1.confirmedNumber = r3
                r4 = 1
                r1.setOrientation(r4)
                r5 = 17
                r1.setGravity(r5)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r0)
                r1.titleView = r6
                r7 = 1099956224(0x41900000, float:18.0)
                r6.setTextSize(r4, r7)
                android.widget.TextView r6 = r1.titleView
                java.lang.String r7 = "fonts/rmedium.ttf"
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r6.setTypeface(r7)
                android.widget.TextView r6 = r1.titleView
                int r7 = r27.activityMode
                r8 = 2
                if (r7 != r8) goto L_0x005a
                r7 = 2131624858(0x7f0e039a, float:1.8876908E38)
                goto L_0x005d
            L_0x005a:
                r7 = 2131629292(0x7f0e14ec, float:1.88859E38)
            L_0x005d:
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r7)
                r6.setText(r7)
                android.widget.TextView r6 = r1.titleView
                r6.setGravity(r5)
                android.widget.TextView r6 = r1.titleView
                r7 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r6.setLineSpacing(r9, r10)
                android.widget.TextView r6 = r1.titleView
                r11 = -1
                r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r13 = 1
                r14 = 1107296256(0x42000000, float:32.0)
                r15 = 0
                r16 = 1107296256(0x42000000, float:32.0)
                r17 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r1.addView(r6, r9)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r0)
                r1.subtitleView = r6
                int r9 = r27.activityMode
                if (r9 != r8) goto L_0x009c
                r9 = 2131624857(0x7f0e0399, float:1.8876906E38)
                goto L_0x009f
            L_0x009c:
                r9 = 2131628421(0x7f0e1185, float:1.8884134E38)
            L_0x009f:
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r6.setText(r9)
                android.widget.TextView r6 = r1.subtitleView
                r9 = 1096810496(0x41600000, float:14.0)
                r6.setTextSize(r4, r9)
                android.widget.TextView r6 = r1.subtitleView
                r6.setGravity(r5)
                android.widget.TextView r5 = r1.subtitleView
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r10)
                android.widget.TextView r5 = r1.subtitleView
                r9 = -1
                r10 = -2
                r11 = 1
                r12 = 32
                r13 = 8
                r14 = 32
                r15 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r1.addView(r5, r6)
                org.telegram.ui.Components.TextViewSwitcher r5 = new org.telegram.ui.Components.TextViewSwitcher
                r5.<init>(r0)
                r1.countryButton = r5
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda7 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda7
                r6.<init>(r0)
                r5.setFactory(r6)
                r5 = 2130771992(0x7var_, float:1.714709E38)
                android.view.animation.Animation r5 = android.view.animation.AnimationUtils.loadAnimation(r0, r5)
                android.view.animation.Interpolator r6 = org.telegram.ui.Components.Easings.easeInOutQuad
                r5.setInterpolator(r6)
                org.telegram.ui.Components.TextViewSwitcher r6 = r1.countryButton
                r6.setInAnimation(r5)
                android.widget.ImageView r5 = new android.widget.ImageView
                r5.<init>(r0)
                r1.chevronRight = r5
                r6 = 2131165764(0x7var_, float:1.7945754E38)
                r5.setImageResource(r6)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r0)
                r5.setOrientation(r3)
                r6 = 16
                r5.setGravity(r6)
                org.telegram.ui.Components.TextViewSwitcher r7 = r1.countryButton
                r9 = 0
                r11 = 1065353216(0x3var_, float:1.0)
                r12 = 0
                r13 = 0
                r14 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (float) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r5.addView(r7, r9)
                android.widget.ImageView r7 = r1.chevronRight
                r9 = 1103101952(0x41CLASSNAME, float:24.0)
                r10 = 1103101952(0x41CLASSNAME, float:24.0)
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 1096810496(0x41600000, float:14.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinearRelatively(r9, r10, r11, r12, r13, r14, r15)
                r5.addView(r7, r9)
                org.telegram.ui.Components.OutlineTextContainerView r7 = new org.telegram.ui.Components.OutlineTextContainerView
                r7.<init>(r0)
                r1.countryOutlineView = r7
                r9 = 2131625267(0x7f0e0533, float:1.8877737E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r9)
                r7.setText(r10)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r1.countryOutlineView
                r10 = -1
                r11 = -1082130432(0xffffffffbvar_, float:-1.0)
                r12 = 48
                r14 = 0
                r16 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r7.addView(r5, r10)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                r5.setForceUseCenter(r4)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                r5.setFocusable(r4)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r9)
                r5.setContentDescription(r7)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4
                r7.<init>(r1)
                r5.setOnFocusChangeListener(r7)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                r9 = -1
                r10 = 58
                r11 = 1098907648(0x41800000, float:16.0)
                r12 = 1103101952(0x41CLASSNAME, float:24.0)
                r13 = 1098907648(0x41800000, float:16.0)
                r14 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10, r11, r12, r13, r14)
                r1.addView(r5, r7)
                org.telegram.ui.Components.OutlineTextContainerView r5 = r1.countryOutlineView
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2
                r7.<init>(r1)
                r5.setOnClickListener(r7)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r0)
                r5.setOrientation(r3)
                org.telegram.ui.Components.OutlineTextContainerView r7 = new org.telegram.ui.Components.OutlineTextContainerView
                r7.<init>(r0)
                r1.phoneOutlineView = r7
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 16
                r12 = 1098907648(0x41800000, float:16.0)
                r13 = 1090519040(0x41000000, float:8.0)
                r14 = 1098907648(0x41800000, float:16.0)
                r15 = 1090519040(0x41000000, float:8.0)
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r7.addView(r5, r9)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r1.phoneOutlineView
                r9 = 2131627493(0x7f0e0de5, float:1.8882252E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r9)
                r7.setText(r10)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r1.phoneOutlineView
                r10 = -1
                r11 = 58
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11, r12, r13, r14, r15)
                r1.addView(r7, r10)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r0)
                r1.plusTextView = r7
                java.lang.String r10 = "+"
                r7.setText(r10)
                android.widget.TextView r7 = r1.plusTextView
                r10 = 1098907648(0x41800000, float:16.0)
                r7.setTextSize(r4, r10)
                android.widget.TextView r7 = r1.plusTextView
                r7.setFocusable(r3)
                android.widget.TextView r7 = r1.plusTextView
                r11 = -2
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
                r5.addView(r7, r12)
                org.telegram.ui.LoginActivity$PhoneView$1 r7 = new org.telegram.ui.LoginActivity$PhoneView$1
                r7.<init>(r0, r2)
                r1.codeField = r7
                r12 = 3
                r7.setInputType(r12)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r13 = 1101004800(0x41a00000, float:20.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r7.setCursorSize(r14)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r14 = 1069547520(0x3fCLASSNAME, float:1.5)
                r7.setCursorWidth(r14)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r15 = 1092616192(0x41200000, float:10.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r7.setPadding(r15, r3, r3, r3)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r7.setTextSize(r4, r10)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r7.setMaxLines(r4)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r15 = 19
                r7.setGravity(r15)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r11 = 268435461(0x10000005, float:2.5243564E-29)
                r7.setImeOptions(r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r7 = r1.codeField
                r6 = 0
                r7.setBackground(r6)
                int r7 = android.os.Build.VERSION.SDK_INT
                r9 = 21
                if (r7 < r9) goto L_0x0246
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                boolean r18 = r26.hasCustomKeyboard()
                if (r18 == 0) goto L_0x0242
                boolean r18 = r27.isCustomKeyboardForceDisabled()
                if (r18 == 0) goto L_0x0240
                goto L_0x0242
            L_0x0240:
                r6 = 0
                goto L_0x0243
            L_0x0242:
                r6 = 1
            L_0x0243:
                r9.setShowSoftInputOnFocus(r6)
            L_0x0246:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.codeField
                r9 = 2131626501(0x7f0e0a05, float:1.888024E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r6.setContentDescription(r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.codeField
                r19 = 55
                r20 = 36
                r21 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                r22 = 0
                r23 = 0
                r24 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24)
                r5.addView(r6, r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$2 r9 = new org.telegram.ui.LoginActivity$PhoneView$2
                r9.<init>(r2)
                r6.addTextChangedListener(r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5 r9 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5
                r9.<init>(r1)
                r6.setOnEditorActionListener(r9)
                android.view.View r6 = new android.view.View
                r6.<init>(r0)
                r1.codeDividerView = r6
                r19 = 0
                r20 = -1
                r21 = 1082130432(0x40800000, float:4.0)
                r22 = 1090519040(0x41000000, float:8.0)
                r23 = 1094713344(0x41400000, float:12.0)
                r24 = 1090519040(0x41000000, float:8.0)
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24)
                r9 = 1056964608(0x3var_, float:0.5)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r9 = java.lang.Math.max(r8, r9)
                r6.width = r9
                android.view.View r9 = r1.codeDividerView
                r5.addView(r9, r6)
                org.telegram.ui.LoginActivity$PhoneView$3 r6 = new org.telegram.ui.LoginActivity$PhoneView$3
                r6.<init>(r0, r2)
                r1.phoneField = r6
                r6.setInputType(r12)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setPadding(r3, r3, r3, r3)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r6.setCursorSize(r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setCursorWidth(r14)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setTextSize(r4, r10)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setMaxLines(r4)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setGravity(r15)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r6.setImeOptions(r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r9 = 0
                r6.setBackground(r9)
                r6 = 21
                if (r7 < r6) goto L_0x02f3
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                boolean r9 = r26.hasCustomKeyboard()
                if (r9 == 0) goto L_0x02ef
                boolean r9 = r27.isCustomKeyboardForceDisabled()
                if (r9 == 0) goto L_0x02ed
                goto L_0x02ef
            L_0x02ed:
                r9 = 0
                goto L_0x02f0
            L_0x02ef:
                r9 = 1
            L_0x02f0:
                r6.setShowSoftInputOnFocus(r9)
            L_0x02f3:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r9 = 2131627493(0x7f0e0de5, float:1.8882252E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r6.setContentDescription(r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r6 = r1.phoneField
                r9 = -1
                r10 = 1108344832(0x42100000, float:36.0)
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10)
                r5.addView(r6, r9)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r5 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$4 r6 = new org.telegram.ui.LoginActivity$PhoneView$4
                r6.<init>(r2)
                r5.addTextChangedListener(r6)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r5 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6
                r6.<init>(r1)
                r5.setOnEditorActionListener(r6)
                r5 = 72
                boolean r6 = r27.newAccount
                r9 = 56
                r10 = 60
                java.lang.String r11 = ""
                if (r6 == 0) goto L_0x0384
                int r6 = r27.activityMode
                if (r6 != 0) goto L_0x0384
                org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
                r5.<init>(r0, r8)
                r1.syncContactsBox = r5
                r6 = 2131628525(0x7f0e11ed, float:1.8884345E38)
                java.lang.String r13 = "SyncContacts"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)
                boolean r13 = r27.syncContacts
                r5.setText(r6, r11, r13, r3)
                org.telegram.ui.Cells.CheckBoxCell r5 = r1.syncContactsBox
                r19 = -2
                r20 = -1
                r21 = 51
                r22 = 16
                r23 = 0
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x036a
                boolean r6 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r6 == 0) goto L_0x036a
                r6 = 21
                if (r7 < r6) goto L_0x0367
                r6 = 56
                goto L_0x036b
            L_0x0367:
                r6 = 60
                goto L_0x036b
            L_0x036a:
                r6 = 0
            L_0x036b:
                r13 = 16
                int r24 = r13 + r6
                r25 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r1.addView(r5, r6)
                r5 = 48
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.syncContactsBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3 r13 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3
                r13.<init>(r1)
                r6.setOnClickListener(r13)
            L_0x0384:
                boolean r6 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
                if (r6 == 0) goto L_0x03da
                int r6 = r27.activityMode
                if (r6 != 0) goto L_0x03da
                org.telegram.ui.Cells.CheckBoxCell r6 = new org.telegram.ui.Cells.CheckBoxCell
                r6.<init>(r0, r8)
                r1.testBackendCheckBox = r6
                boolean r13 = r27.testBackend
                java.lang.String r14 = "Test Backend"
                r6.setText(r14, r11, r13, r3)
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.testBackendCheckBox
                r19 = -2
                r20 = -1
                r21 = 51
                r22 = 16
                r23 = 0
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x03c0
                boolean r13 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r13 == 0) goto L_0x03c0
                r13 = 21
                if (r7 < r13) goto L_0x03bb
                r7 = 16
                goto L_0x03c3
            L_0x03bb:
                r7 = 16
                r9 = 60
                goto L_0x03c3
            L_0x03c0:
                r7 = 16
                r9 = 0
            L_0x03c3:
                int r24 = r7 + r9
                r25 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r1.addView(r6, r7)
                int r5 = r5 + -24
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.testBackendCheckBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1
                r7.<init>(r1)
                r6.setOnClickListener(r7)
            L_0x03da:
                if (r5 <= 0) goto L_0x03f7
                boolean r6 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r6 != 0) goto L_0x03f7
                android.widget.Space r6 = new android.widget.Space
                r6.<init>(r0)
                float r0 = (float) r5
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r6.setMinimumHeight(r0)
                r0 = -2
                android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r0, r0)
                r1.addView(r6, r0)
            L_0x03f7:
                java.util.HashMap r5 = new java.util.HashMap
                r5.<init>()
                java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0459 }
                java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0459 }
                android.content.res.Resources r7 = r26.getResources()     // Catch:{ Exception -> 0x0459 }
                android.content.res.AssetManager r7 = r7.getAssets()     // Catch:{ Exception -> 0x0459 }
                java.lang.String r9 = "countries.txt"
                java.io.InputStream r7 = r7.open(r9)     // Catch:{ Exception -> 0x0459 }
                r6.<init>(r7)     // Catch:{ Exception -> 0x0459 }
                r0.<init>(r6)     // Catch:{ Exception -> 0x0459 }
            L_0x0414:
                java.lang.String r6 = r0.readLine()     // Catch:{ Exception -> 0x0459 }
                if (r6 == 0) goto L_0x0455
                java.lang.String r7 = ";"
                java.lang.String[] r6 = r6.split(r7)     // Catch:{ Exception -> 0x0459 }
                org.telegram.ui.CountrySelectActivity$Country r7 = new org.telegram.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0459 }
                r7.<init>()     // Catch:{ Exception -> 0x0459 }
                r9 = r6[r8]     // Catch:{ Exception -> 0x0459 }
                r7.name = r9     // Catch:{ Exception -> 0x0459 }
                r9 = r6[r3]     // Catch:{ Exception -> 0x0459 }
                r7.code = r9     // Catch:{ Exception -> 0x0459 }
                r9 = r6[r4]     // Catch:{ Exception -> 0x0459 }
                r7.shortname = r9     // Catch:{ Exception -> 0x0459 }
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r9 = r1.countriesArray     // Catch:{ Exception -> 0x0459 }
                r9.add(r3, r7)     // Catch:{ Exception -> 0x0459 }
                java.util.HashMap<java.lang.String, org.telegram.ui.CountrySelectActivity$Country> r9 = r1.codesMap     // Catch:{ Exception -> 0x0459 }
                r10 = r6[r3]     // Catch:{ Exception -> 0x0459 }
                r9.put(r10, r7)     // Catch:{ Exception -> 0x0459 }
                int r7 = r6.length     // Catch:{ Exception -> 0x0459 }
                if (r7 <= r12) goto L_0x044d
                java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> r7 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0459 }
                r9 = r6[r3]     // Catch:{ Exception -> 0x0459 }
                r10 = r6[r12]     // Catch:{ Exception -> 0x0459 }
                java.util.List r10 = java.util.Collections.singletonList(r10)     // Catch:{ Exception -> 0x0459 }
                r7.put(r9, r10)     // Catch:{ Exception -> 0x0459 }
            L_0x044d:
                r7 = r6[r4]     // Catch:{ Exception -> 0x0459 }
                r6 = r6[r8]     // Catch:{ Exception -> 0x0459 }
                r5.put(r7, r6)     // Catch:{ Exception -> 0x0459 }
                goto L_0x0414
            L_0x0455:
                r0.close()     // Catch:{ Exception -> 0x0459 }
                goto L_0x045d
            L_0x0459:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x045d:
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r0 = r1.countriesArray
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda17 r3 = org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda17.INSTANCE
                java.util.Comparator r3 = j$.util.Comparator$CC.comparing(r3)
                java.util.Collections.sort(r0, r3)
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0473 }
                java.lang.String r3 = "phone"
                java.lang.Object r0 = r0.getSystemService(r3)     // Catch:{ Exception -> 0x0473 }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0473 }
                goto L_0x0477
            L_0x0473:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0477:
                org.telegram.tgnet.TLRPC$TL_help_getNearestDc r0 = new org.telegram.tgnet.TLRPC$TL_help_getNearestDc
                r0.<init>()
                org.telegram.messenger.AccountInstance r3 = r27.getAccountInstance()
                org.telegram.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21
                r6.<init>(r1, r5)
                r5 = 10
                r3.sendRequest(r0, r6, r5)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x04a1
                r3 = 0
                r1.setCountryButtonText(r3)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                r0.setHintText(r3)
                r1.countryState = r4
            L_0x04a1:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x04b8
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                r0.requestFocus()
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                int r3 = r0.length()
                r0.setSelection(r3)
                goto L_0x04bd
            L_0x04b8:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                r0.requestFocus()
            L_0x04bd:
                org.telegram.tgnet.TLRPC$TL_help_getCountriesList r0 = new org.telegram.tgnet.TLRPC$TL_help_getCountriesList
                r0.<init>()
                r0.lang_code = r11
                org.telegram.tgnet.ConnectionsManager r2 = r27.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18 r3 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18
                r3.<init>(r1)
                r2.sendRequest(r0, r3, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view, boolean z) {
            this.countryOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true, this.countriesArray);
            countrySelectActivity.setCountrySelectActivityDelegate(new LoginActivity$PhoneView$$ExternalSyntheticLambda22(this));
            this.this$0.presentFragment(countrySelectActivity);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda8(this), 300);
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            boolean unused = this.this$0.showKeyboard(this.phoneField);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$5(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$6(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            if (this.this$0.phoneNumberConfirmView != null) {
                this.this$0.phoneNumberConfirmView.popupFabContainer.callOnClick();
                return true;
            }
            lambda$onNextPressed$14((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(View view) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.syncContacts = !loginActivity.syncContacts;
                ((CheckBoxCell) view).setChecked(this.this$0.syncContacts, true);
                if (this.this$0.syncContacts) {
                    BulletinFactory.of(this.this$0.slideViewsContainer, (Theme.ResourcesProvider) null).createSimpleBulletin(NUM, LocaleController.getString("SyncContactsOn", NUM)).show();
                } else {
                    BulletinFactory.of(this.this$0.slideViewsContainer, (Theme.ResourcesProvider) null).createSimpleBulletin(NUM, LocaleController.getString("SyncContactsOff", NUM)).show();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(View view) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.testBackend = !loginActivity.testBackend;
                ((CheckBoxCell) view).setChecked(this.this$0.testBackend, true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$11(HashMap hashMap, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda13(this, tLObject, hashMap));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10(TLObject tLObject, HashMap hashMap) {
            if (tLObject != null) {
                TLRPC$TL_nearestDc tLRPC$TL_nearestDc = (TLRPC$TL_nearestDc) tLObject;
                if (this.codeField.length() == 0) {
                    setCountry(hashMap, tLRPC$TL_nearestDc.country.toUpperCase());
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$13(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda15(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
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
                        CountrySelectActivity.Country country = new CountrySelectActivity.Country();
                        country.name = tLRPC$TL_help_country.default_name;
                        country.code = tLRPC$TL_help_country.country_codes.get(i2).country_code;
                        country.shortname = tLRPC$TL_help_country.iso2;
                        this.countriesArray.add(country);
                        this.codesMap.put(tLRPC$TL_help_country.country_codes.get(i2).country_code, country);
                        if (tLRPC$TL_help_country.country_codes.get(i2).patterns.size() > 0) {
                            this.phoneFormatMap.put(tLRPC$TL_help_country.country_codes.get(i2).country_code, tLRPC$TL_help_country.country_codes.get(i2).patterns);
                        }
                    }
                }
                if (this.this$0.activityMode == 2) {
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(UserConfig.getInstance(this.this$0.currentAccount).getClientPhone());
                    if (!TextUtils.isEmpty(stripExceptNumbers)) {
                        int i3 = 4;
                        if (stripExceptNumbers.length() > 4) {
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
                            if (!z) {
                                this.codeField.setText(stripExceptNumbers.substring(0, 1));
                            }
                        }
                    }
                }
            }
        }

        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            for (int i = 0; i < this.countryButton.getChildCount(); i++) {
                TextView textView = (TextView) this.countryButton.getChildAt(i);
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            }
            this.chevronRight.setColorFilter(Theme.getColor("windowBackgroundWhiteHintText"));
            this.chevronRight.setBackground(Theme.createSelectorDrawable(this.this$0.getThemedColor("listSelectorSDK21"), 1));
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

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
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

        /* access modifiers changed from: private */
        public void setCountryHint(String str, CountrySelectActivity.Country country) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            String languageFlag = LocaleController.getLanguageFlag(country.shortname);
            if (languageFlag != null) {
                spannableStringBuilder.append(languageFlag).append(" ");
                spannableStringBuilder.setSpan(new ReplacementSpan(this) {
                    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                    }

                    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                        return AndroidUtilities.dp(16.0f);
                    }
                }, languageFlag.length(), languageFlag.length() + 1, 0);
            }
            spannableStringBuilder.append(country.name);
            setCountryButtonText(Emoji.replaceEmoji(spannableStringBuilder, this.countryButton.getCurrentView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            String str2 = null;
            if (this.phoneFormatMap.get(str) == null || this.phoneFormatMap.get(str).isEmpty()) {
                this.phoneField.setHintText((String) null);
                return;
            }
            String str3 = (String) this.phoneFormatMap.get(str).get(0);
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            if (str3 != null) {
                str2 = str3.replace('X', '0');
            }
            animatedPhoneNumberEditText.setHintText(str2);
        }

        /* access modifiers changed from: private */
        public void setCountryButtonText(CharSequence charSequence) {
            Animation loadAnimation = AnimationUtils.loadAnimation(ApplicationLoader.applicationContext, (this.countryButton.getCurrentView().getText() == null || charSequence != null) ? NUM : NUM);
            loadAnimation.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setOutAnimation(loadAnimation);
            CharSequence text = this.countryButton.getCurrentView().getText();
            this.countryButton.setText(charSequence, (!TextUtils.isEmpty(charSequence) || !TextUtils.isEmpty(text)) && !ObjectsCompat$$ExternalSyntheticBackport0.m(text, charSequence));
            this.countryOutlineView.animateSelection(charSequence != null ? 1.0f : 0.0f);
        }

        private void setCountry(HashMap<String, String> hashMap, String str) {
            if (hashMap.get(str) != null && this.countriesArray != null) {
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
                if (country != null) {
                    this.codeField.setText(country.code);
                    this.countryState = 0;
                }
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesArray.get(i).code);
            this.ignoreOnTextChange = false;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.tgnet.TLRPC$TL_auth_sendCode} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v32, resolved type: org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v69, resolved type: org.telegram.tgnet.TLRPC$TL_auth_sendCode} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v70, resolved type: org.telegram.tgnet.TLRPC$TL_auth_sendCode} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:175:0x0434  */
        /* JADX WARNING: Removed duplicated region for block: B:176:0x043e  */
        /* renamed from: onNextPressed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void lambda$onNextPressed$14(java.lang.String r19) {
            /*
                r18 = this;
                r7 = r18
                r0 = r19
                java.lang.String r1 = "ephone"
                org.telegram.ui.LoginActivity r2 = r7.this$0
                android.app.Activity r2 = r2.getParentActivity()
                if (r2 == 0) goto L_0x054d
                boolean r2 = r7.nextPressed
                if (r2 == 0) goto L_0x0014
                goto L_0x054d
            L_0x0014:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
                java.lang.String r3 = "phone"
                java.lang.Object r2 = r2.getSystemService(r3)
                android.telephony.TelephonyManager r2 = (android.telephony.TelephonyManager) r2
                boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
                if (r4 == 0) goto L_0x003a
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "sim status = "
                r4.append(r5)
                int r5 = r2.getSimState()
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                org.telegram.messenger.FileLog.d(r4)
            L_0x003a:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r4 = r7.codeField
                int r4 = r4.length()
                r5 = 0
                if (r4 == 0) goto L_0x0545
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r4 = r7.phoneField
                int r4 = r4.length()
                if (r4 != 0) goto L_0x004d
                goto L_0x0545
            L_0x004d:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r6 = "+"
                r4.append(r6)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r8 = r7.codeField
                android.text.Editable r8 = r8.getText()
                r4.append(r8)
                java.lang.String r8 = " "
                r4.append(r8)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r7.phoneField
                android.text.Editable r9 = r9.getText()
                r4.append(r9)
                java.lang.String r14 = r4.toString()
                boolean r4 = r7.confirmedNumber
                if (r4 != 0) goto L_0x00e1
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r2 = r1.x
                int r1 = r1.y
                if (r2 <= r1) goto L_0x00ac
                org.telegram.ui.LoginActivity r1 = r7.this$0
                boolean r1 = r1.isCustomKeyboardVisible()
                if (r1 != 0) goto L_0x00ac
                org.telegram.ui.LoginActivity r1 = r7.this$0
                org.telegram.ui.Components.SizeNotifierFrameLayout r1 = r1.sizeNotifierFrameLayout
                int r1 = r1.measureKeyboardHeight()
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                if (r1 <= r2) goto L_0x00ac
                org.telegram.ui.LoginActivity r1 = r7.this$0
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11
                r2.<init>(r7, r0)
                java.lang.Runnable unused = r1.keyboardHideCallback = r2
                org.telegram.ui.LoginActivity r0 = r7.this$0
                android.view.View r0 = r0.fragmentView
                org.telegram.messenger.AndroidUtilities.hideKeyboard(r0)
                return
            L_0x00ac:
                org.telegram.ui.LoginActivity r1 = r7.this$0
                org.telegram.ui.LoginActivity$PhoneNumberConfirmView r2 = new org.telegram.ui.LoginActivity$PhoneNumberConfirmView
                org.telegram.ui.LoginActivity r3 = r7.this$0
                android.view.View r3 = r3.fragmentView
                android.content.Context r11 = r3.getContext()
                org.telegram.ui.LoginActivity r3 = r7.this$0
                android.view.View r3 = r3.fragmentView
                r12 = r3
                android.view.ViewGroup r12 = (android.view.ViewGroup) r12
                org.telegram.ui.LoginActivity r3 = r7.this$0
                android.widget.FrameLayout r13 = r3.floatingButtonContainer
                org.telegram.ui.LoginActivity$PhoneView$6 r15 = new org.telegram.ui.LoginActivity$PhoneView$6
                r15.<init>(r0)
                r16 = 0
                r10 = r2
                r10.<init>(r11, r12, r13, r14, r15)
                org.telegram.ui.LoginActivity.PhoneNumberConfirmView unused = r1.phoneNumberConfirmView = r2
                org.telegram.ui.LoginActivity r0 = r7.this$0
                org.telegram.ui.LoginActivity$PhoneNumberConfirmView r0 = r0.phoneNumberConfirmView
                r0.show()
                return
            L_0x00e1:
                r7.confirmedNumber = r5
                org.telegram.ui.LoginActivity r0 = r7.this$0
                org.telegram.ui.LoginActivity$PhoneNumberConfirmView r0 = r0.phoneNumberConfirmView
                if (r0 == 0) goto L_0x00f4
                org.telegram.ui.LoginActivity r0 = r7.this$0
                org.telegram.ui.LoginActivity$PhoneNumberConfirmView r0 = r0.phoneNumberConfirmView
                r0.dismiss()
            L_0x00f4:
                boolean r0 = org.telegram.messenger.AndroidUtilities.isSimAvailable()
                int r4 = android.os.Build.VERSION.SDK_INT
                r9 = 23
                if (r4 < r9) goto L_0x0261
                if (r0 == 0) goto L_0x0261
                org.telegram.ui.LoginActivity r9 = r7.this$0
                android.app.Activity r9 = r9.getParentActivity()
                java.lang.String r12 = "android.permission.READ_PHONE_STATE"
                int r9 = r9.checkSelfPermission(r12)
                if (r9 != 0) goto L_0x0110
                r9 = 1
                goto L_0x0111
            L_0x0110:
                r9 = 0
            L_0x0111:
                org.telegram.ui.LoginActivity r13 = r7.this$0
                android.app.Activity r13 = r13.getParentActivity()
                java.lang.String r14 = "android.permission.CALL_PHONE"
                int r13 = r13.checkSelfPermission(r14)
                if (r13 != 0) goto L_0x0121
                r13 = 1
                goto L_0x0122
            L_0x0121:
                r13 = 0
            L_0x0122:
                r15 = 28
                java.lang.String r10 = "android.permission.READ_CALL_LOG"
                if (r4 < r15) goto L_0x0137
                org.telegram.ui.LoginActivity r15 = r7.this$0
                android.app.Activity r15 = r15.getParentActivity()
                int r15 = r15.checkSelfPermission(r10)
                if (r15 != 0) goto L_0x0135
                goto L_0x0137
            L_0x0135:
                r15 = 0
                goto L_0x0138
            L_0x0137:
                r15 = 1
            L_0x0138:
                java.lang.String r5 = "android.permission.READ_PHONE_NUMBERS"
                r11 = 26
                if (r4 < r11) goto L_0x0150
                org.telegram.ui.LoginActivity r11 = r7.this$0
                android.app.Activity r11 = r11.getParentActivity()
                int r11 = r11.checkSelfPermission(r5)
                if (r11 != 0) goto L_0x014c
                r11 = 1
                goto L_0x014d
            L_0x014c:
                r11 = 0
            L_0x014d:
                r17 = r1
                goto L_0x0153
            L_0x0150:
                r17 = r1
                r11 = 1
            L_0x0153:
                org.telegram.ui.LoginActivity r1 = r7.this$0
                boolean r1 = r1.checkPermissions
                if (r1 == 0) goto L_0x025f
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                r1.clear()
                if (r9 != 0) goto L_0x016f
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                r1.add(r12)
            L_0x016f:
                if (r13 != 0) goto L_0x017a
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                r1.add(r14)
            L_0x017a:
                if (r15 != 0) goto L_0x0185
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                r1.add(r10)
            L_0x0185:
                if (r11 != 0) goto L_0x0194
                r1 = 26
                if (r4 < r1) goto L_0x0194
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                r1.add(r5)
            L_0x0194:
                org.telegram.ui.LoginActivity r1 = r7.this$0
                java.util.ArrayList r1 = r1.permissionsItems
                boolean r1 = r1.isEmpty()
                if (r1 != 0) goto L_0x025f
                android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
                java.lang.String r1 = "firstlogin"
                r2 = 1
                boolean r3 = r0.getBoolean(r1, r2)
                if (r3 != 0) goto L_0x01e7
                org.telegram.ui.LoginActivity r2 = r7.this$0
                android.app.Activity r2 = r2.getParentActivity()
                boolean r2 = r2.shouldShowRequestPermissionRationale(r12)
                if (r2 != 0) goto L_0x01e7
                org.telegram.ui.LoginActivity r2 = r7.this$0
                android.app.Activity r2 = r2.getParentActivity()
                boolean r2 = r2.shouldShowRequestPermissionRationale(r10)
                if (r2 == 0) goto L_0x01c6
                goto L_0x01e7
            L_0x01c6:
                org.telegram.ui.LoginActivity r0 = r7.this$0     // Catch:{ Exception -> 0x01e1 }
                android.app.Activity r0 = r0.getParentActivity()     // Catch:{ Exception -> 0x01e1 }
                org.telegram.ui.LoginActivity r1 = r7.this$0     // Catch:{ Exception -> 0x01e1 }
                java.util.ArrayList r1 = r1.permissionsItems     // Catch:{ Exception -> 0x01e1 }
                r2 = 0
                java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x01e1 }
                java.lang.Object[] r1 = r1.toArray(r2)     // Catch:{ Exception -> 0x01e1 }
                java.lang.String[] r1 = (java.lang.String[]) r1     // Catch:{ Exception -> 0x01e1 }
                r2 = 6
                r0.requestPermissions(r1, r2)     // Catch:{ Exception -> 0x01e1 }
                goto L_0x025e
            L_0x01e1:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x025e
            L_0x01e7:
                android.content.SharedPreferences$Editor r0 = r0.edit()
                r2 = 0
                android.content.SharedPreferences$Editor r0 = r0.putBoolean(r1, r2)
                r0.commit()
                org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                org.telegram.ui.LoginActivity r1 = r7.this$0
                android.app.Activity r1 = r1.getParentActivity()
                r0.<init>((android.content.Context) r1)
                r1 = 2131625246(0x7f0e051e, float:1.8877695E38)
                java.lang.String r2 = "Continue"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r2 = 0
                r0.setPositiveButton(r1, r2)
                r1 = 2131558412(0x7f0d000c, float:1.874214E38)
                if (r9 != 0) goto L_0x0221
                if (r13 == 0) goto L_0x0214
                if (r15 != 0) goto L_0x0221
            L_0x0214:
                r2 = 2131624330(0x7f0e018a, float:1.8875837E38)
                java.lang.String r3 = "AllowReadCallAndLog"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setMessage(r2)
                goto L_0x0242
            L_0x0221:
                if (r13 == 0) goto L_0x0236
                if (r15 != 0) goto L_0x0226
                goto L_0x0236
            L_0x0226:
                r1 = 2131624329(0x7f0e0189, float:1.8875835E38)
                java.lang.String r2 = "AllowReadCall"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r0.setMessage(r1)
                r1 = 2131558477(0x7f0d004d, float:1.874227E38)
                goto L_0x0242
            L_0x0236:
                r2 = 2131624331(0x7f0e018b, float:1.8875839E38)
                java.lang.String r3 = "AllowReadCallLog"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setMessage(r2)
            L_0x0242:
                r2 = 46
                java.lang.String r3 = "dialogTopBackground"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r4 = 0
                r0.setTopAnimation(r1, r2, r4, r3)
                org.telegram.ui.LoginActivity r1 = r7.this$0
                org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
                android.app.Dialog r0 = r1.showDialog(r0)
                android.app.Dialog unused = r1.permissionsDialog = r0
                r1 = 1
                r7.confirmedNumber = r1
            L_0x025e:
                return
            L_0x025f:
                r1 = 1
                goto L_0x0267
            L_0x0261:
                r17 = r1
                r1 = 1
                r9 = 1
                r13 = 1
                r15 = 1
            L_0x0267:
                int r4 = r7.countryState
                r5 = 2131628020(0x7f0e0ff4, float:1.888332E38)
                if (r4 != r1) goto L_0x0287
                org.telegram.ui.LoginActivity r0 = r7.this$0
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r5)
                r2 = 2131625104(0x7f0e0490, float:1.8877407E38)
                java.lang.String r3 = "ChooseCountry"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.needShowAlert(r1, r2)
                org.telegram.ui.LoginActivity r0 = r7.this$0
                r1 = 0
                r0.needHideProgress(r1)
                return
            L_0x0287:
                r1 = 2
                if (r4 != r1) goto L_0x02a7
                boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
                if (r4 != 0) goto L_0x02a7
                org.telegram.ui.LoginActivity r0 = r7.this$0
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r5)
                r2 = 2131629254(0x7f0e14c6, float:1.8885824E38)
                java.lang.String r3 = "WrongCountry"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.needShowAlert(r1, r2)
                org.telegram.ui.LoginActivity r0 = r7.this$0
                r1 = 0
                r0.needHideProgress(r1)
                return
            L_0x02a7:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = ""
                r4.append(r5)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r5 = r7.codeField
                android.text.Editable r5 = r5.getText()
                r4.append(r5)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r5 = r7.phoneField
                android.text.Editable r5 = r5.getText()
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                java.lang.String r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4)
                org.telegram.ui.LoginActivity r5 = r7.this$0
                int r5 = r5.activityMode
                if (r5 != 0) goto L_0x037f
                boolean r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
                if (r5 == 0) goto L_0x02e5
                org.telegram.ui.LoginActivity r5 = r7.this$0
                org.telegram.tgnet.ConnectionsManager r5 = r5.getConnectionsManager()
                boolean r5 = r5.isTestBackend()
                if (r5 == 0) goto L_0x02e5
                r5 = 1
                goto L_0x02e6
            L_0x02e5:
                r5 = 0
            L_0x02e6:
                org.telegram.ui.LoginActivity r10 = r7.this$0
                boolean r10 = r10.testBackend
                if (r5 == r10) goto L_0x02fe
                org.telegram.ui.LoginActivity r5 = r7.this$0
                org.telegram.tgnet.ConnectionsManager r5 = r5.getConnectionsManager()
                r10 = 0
                r5.switchBackend(r10)
                org.telegram.ui.LoginActivity r5 = r7.this$0
                boolean r5 = r5.testBackend
            L_0x02fe:
                org.telegram.ui.LoginActivity r10 = r7.this$0
                android.app.Activity r10 = r10.getParentActivity()
                boolean r10 = r10 instanceof org.telegram.ui.LaunchActivity
                if (r10 == 0) goto L_0x037f
                r10 = 0
            L_0x0309:
                r11 = 4
                if (r10 >= r11) goto L_0x037f
                org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r10)
                boolean r12 = r11.isClientActivated()
                if (r12 != 0) goto L_0x0317
                goto L_0x037c
            L_0x0317:
                org.telegram.tgnet.TLRPC$User r11 = r11.getCurrentUser()
                java.lang.String r11 = r11.phone
                boolean r11 = android.telephony.PhoneNumberUtils.compare(r4, r11)
                if (r11 == 0) goto L_0x037c
                org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r10)
                boolean r11 = r11.isTestBackend()
                if (r11 != r5) goto L_0x037c
                org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                org.telegram.ui.LoginActivity r1 = r7.this$0
                android.app.Activity r1 = r1.getParentActivity()
                r0.<init>((android.content.Context) r1)
                r1 = 2131624375(0x7f0e01b7, float:1.8875928E38)
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
                r0.setTitle(r1)
                r1 = 2131624128(0x7f0e00c0, float:1.8875427E38)
                java.lang.String r2 = "AccountAlreadyLoggedIn"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r0.setMessage(r1)
                r1 = 2131624130(0x7f0e00c2, float:1.8875431E38)
                java.lang.String r2 = "AccountSwitch"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda0
                r2.<init>(r7, r10)
                r0.setPositiveButton(r1, r2)
                r1 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
                java.lang.String r2 = "OK"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r2 = 0
                r0.setNegativeButton(r1, r2)
                org.telegram.ui.LoginActivity r1 = r7.this$0
                org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
                r1.showDialog(r0)
                org.telegram.ui.LoginActivity r0 = r7.this$0
                r1 = 0
                r0.needHideProgress(r1)
                return
            L_0x037c:
                int r10 = r10 + 1
                goto L_0x0309
            L_0x037f:
                org.telegram.tgnet.TLRPC$TL_codeSettings r5 = new org.telegram.tgnet.TLRPC$TL_codeSettings
                r5.<init>()
                if (r0 == 0) goto L_0x038e
                if (r9 == 0) goto L_0x038e
                if (r13 == 0) goto L_0x038e
                if (r15 == 0) goto L_0x038e
                r10 = 1
                goto L_0x038f
            L_0x038e:
                r10 = 0
            L_0x038f:
                r5.allow_flashcall = r10
                if (r0 == 0) goto L_0x0397
                if (r9 == 0) goto L_0x0397
                r0 = 1
                goto L_0x0398
            L_0x0397:
                r0 = 0
            L_0x0398:
                r5.allow_missed_call = r0
                boolean r0 = org.telegram.messenger.ApplicationLoader.hasPlayServices
                r5.allow_app_hash = r0
                java.util.ArrayList r0 = org.telegram.messenger.MessagesController.getSavedLogOutTokens()
                if (r0 == 0) goto L_0x03c9
                r9 = 0
            L_0x03a5:
                int r10 = r0.size()
                if (r9 >= r10) goto L_0x03c6
                java.util.ArrayList<byte[]> r10 = r5.logout_tokens
                if (r10 != 0) goto L_0x03b6
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                r5.logout_tokens = r10
            L_0x03b6:
                java.util.ArrayList<byte[]> r10 = r5.logout_tokens
                java.lang.Object r11 = r0.get(r9)
                org.telegram.tgnet.TLRPC$TL_auth_loggedOut r11 = (org.telegram.tgnet.TLRPC$TL_auth_loggedOut) r11
                byte[] r11 = r11.future_auth_token
                r10.add(r11)
                int r9 = r9 + 1
                goto L_0x03a5
            L_0x03c6:
                org.telegram.messenger.MessagesController.saveLogOutTokens(r0)
            L_0x03c9:
                java.util.ArrayList<byte[]> r0 = r5.logout_tokens
                if (r0 == 0) goto L_0x03d3
                int r0 = r5.flags
                r0 = r0 | 64
                r5.flags = r0
            L_0x03d3:
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
                java.lang.String r9 = "mainconfig"
                r10 = 0
                android.content.SharedPreferences r0 = r0.getSharedPreferences(r9, r10)
                boolean r9 = r5.allow_app_hash
                java.lang.String r10 = "sms_hash"
                if (r9 == 0) goto L_0x03f0
                android.content.SharedPreferences$Editor r0 = r0.edit()
                java.lang.String r9 = org.telegram.messenger.BuildVars.SMS_HASH
                android.content.SharedPreferences$Editor r0 = r0.putString(r10, r9)
                r0.apply()
                goto L_0x03fb
            L_0x03f0:
                android.content.SharedPreferences$Editor r0 = r0.edit()
                android.content.SharedPreferences$Editor r0 = r0.remove(r10)
                r0.apply()
            L_0x03fb:
                boolean r0 = r5.allow_flashcall
                if (r0 == 0) goto L_0x042c
                java.lang.String r0 = r2.getLine1Number()     // Catch:{ Exception -> 0x0425 }
                boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0425 }
                if (r2 != 0) goto L_0x0415
                boolean r0 = android.telephony.PhoneNumberUtils.compare(r4, r0)     // Catch:{ Exception -> 0x0425 }
                r5.current_number = r0     // Catch:{ Exception -> 0x0425 }
                if (r0 != 0) goto L_0x042c
                r2 = 0
                r5.allow_flashcall = r2     // Catch:{ Exception -> 0x0423 }
                goto L_0x042c
            L_0x0415:
                int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0425 }
                if (r0 <= 0) goto L_0x041f
                r2 = 0
                r5.allow_flashcall = r2     // Catch:{ Exception -> 0x0423 }
                goto L_0x042c
            L_0x041f:
                r2 = 0
                r5.current_number = r2     // Catch:{ Exception -> 0x0423 }
                goto L_0x042c
            L_0x0423:
                r0 = move-exception
                goto L_0x0427
            L_0x0425:
                r0 = move-exception
                r2 = 0
            L_0x0427:
                r5.allow_flashcall = r2
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x042c:
                org.telegram.ui.LoginActivity r0 = r7.this$0
                int r0 = r0.activityMode
                if (r0 != r1) goto L_0x043e
                org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode r0 = new org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode
                r0.<init>()
                r0.phone_number = r4
                r0.settings = r5
                goto L_0x045d
            L_0x043e:
                org.telegram.ui.LoginActivity r0 = r7.this$0
                int r0 = r0.currentAccount
                org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
                r1 = 0
                r0.cleanup(r1)
                org.telegram.tgnet.TLRPC$TL_auth_sendCode r0 = new org.telegram.tgnet.TLRPC$TL_auth_sendCode
                r0.<init>()
                java.lang.String r1 = org.telegram.messenger.BuildVars.APP_HASH
                r0.api_hash = r1
                int r1 = org.telegram.messenger.BuildVars.APP_ID
                r0.api_id = r1
                r0.phone_number = r4
                r0.settings = r5
            L_0x045d:
                r9 = r0
                android.os.Bundle r5 = new android.os.Bundle
                r5.<init>()
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r6)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r1 = r7.codeField
                android.text.Editable r1 = r1.getText()
                r0.append(r1)
                r0.append(r8)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r1 = r7.phoneField
                android.text.Editable r1 = r1.getText()
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                r5.putString(r3, r0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c0 }
                r0.<init>()     // Catch:{ Exception -> 0x04c0 }
                r0.append(r6)     // Catch:{ Exception -> 0x04c0 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r1 = r7.codeField     // Catch:{ Exception -> 0x04c0 }
                android.text.Editable r1 = r1.getText()     // Catch:{ Exception -> 0x04c0 }
                java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04c0 }
                java.lang.String r1 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r1)     // Catch:{ Exception -> 0x04c0 }
                r0.append(r1)     // Catch:{ Exception -> 0x04c0 }
                r0.append(r8)     // Catch:{ Exception -> 0x04c0 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r1 = r7.phoneField     // Catch:{ Exception -> 0x04c0 }
                android.text.Editable r1 = r1.getText()     // Catch:{ Exception -> 0x04c0 }
                java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04c0 }
                java.lang.String r1 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r1)     // Catch:{ Exception -> 0x04c0 }
                r0.append(r1)     // Catch:{ Exception -> 0x04c0 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04c0 }
                r1 = r17
                r5.putString(r1, r0)     // Catch:{ Exception -> 0x04be }
                goto L_0x04d8
            L_0x04be:
                r0 = move-exception
                goto L_0x04c3
            L_0x04c0:
                r0 = move-exception
                r1 = r17
            L_0x04c3:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r6)
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                r5.putString(r1, r0)
            L_0x04d8:
                java.lang.String r0 = "phoneFormated"
                r5.putString(r0, r4)
                r1 = 1
                r7.nextPressed = r1
                org.telegram.ui.LoginActivity$PhoneInputData r0 = new org.telegram.ui.LoginActivity$PhoneInputData
                r1 = 0
                r0.<init>()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r6)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r2 = r7.codeField
                android.text.Editable r2 = r2.getText()
                r1.append(r2)
                r1.append(r8)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r2 = r7.phoneField
                android.text.Editable r2 = r2.getText()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                java.lang.String unused = r0.phoneNumber = r1
                org.telegram.ui.CountrySelectActivity$Country r1 = r7.currentCountry
                org.telegram.ui.CountrySelectActivity.Country unused = r0.country = r1
                java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> r1 = r7.phoneFormatMap
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r2 = r7.codeField
                android.text.Editable r2 = r2.getText()
                java.lang.String r2 = r2.toString()
                java.lang.Object r1 = r1.get(r2)
                java.util.List r1 = (java.util.List) r1
                java.util.List unused = r0.patterns = r1
                org.telegram.ui.LoginActivity r1 = r7.this$0
                int r1 = r1.currentAccount
                org.telegram.tgnet.ConnectionsManager r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda19 r10 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda19
                r1 = r10
                r2 = r18
                r3 = r5
                r5 = r0
                r6 = r9
                r1.<init>(r2, r3, r4, r5, r6)
                r0 = 27
                int r0 = r8.sendRequest(r9, r10, r0)
                org.telegram.ui.LoginActivity r1 = r7.this$0
                r1.needShowProgress(r0)
                return
            L_0x0545:
                org.telegram.ui.LoginActivity r0 = r7.this$0
                org.telegram.ui.Components.OutlineTextContainerView r1 = r7.phoneOutlineView
                r2 = 0
                r0.onFieldError(r1, r2)
            L_0x054d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.lambda$onNextPressed$14(java.lang.String):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$15(String str) {
            postDelayed(new LoginActivity$PhoneView$$ExternalSyntheticLambda10(this, str), 200);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$16(int i, DialogInterface dialogInterface, int i2) {
            if (UserConfig.selectedAccount != i) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(i, false);
            }
            this.this$0.finishFragment();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$20(Bundle bundle, String str, PhoneInputData phoneInputData, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda14(this, tLRPC$TL_error, bundle, tLObject2, str, phoneInputData, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, String str, PhoneInputData phoneInputData, TLObject tLObject2) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str2 = tLRPC$TL_error.text;
                if (str2 != null) {
                    if (str2.contains("SESSION_PASSWORD_NEEDED")) {
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new LoginActivity$PhoneView$$ExternalSyntheticLambda20(this, str), 10);
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.needShowInvalidAlert(this.this$0, str, phoneInputData, false);
                    } else if (tLRPC$TL_error.text.contains("PHONE_PASSWORD_FLOOD")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_FLOOD")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_BANNED")) {
                        LoginActivity.needShowInvalidAlert(this.this$0, str, phoneInputData, true);
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.code != -1000) {
                        AlertsCreator.processError(this.this$0.currentAccount, tLRPC$TL_error, this.this$0, tLObject2, phoneInputData.phoneNumber);
                    }
                }
            }
            this.this$0.needHideProgress(false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$18(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda16(this, tLRPC$TL_error, tLObject, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$17(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
            this.nextPressed = false;
            this.this$0.showDoneButton(false, true);
            if (tLRPC$TL_error == null) {
                TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, true)) {
                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                    return;
                }
                Bundle bundle = new Bundle();
                SerializedData serializedData = new SerializedData(tLRPC$TL_account_password.getObjectSize());
                tLRPC$TL_account_password.serializeToStream(serializedData);
                bundle.putString("password", Utilities.bytesToHex(serializedData.toByteArray()));
                bundle.putString("phoneFormated", str);
                this.this$0.setPage(6, true, bundle, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
        }

        /* JADX WARNING: Removed duplicated region for block: B:27:0x0064 A[Catch:{ Exception -> 0x0164 }] */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x0086 A[Catch:{ Exception -> 0x0164 }] */
        /* JADX WARNING: Removed duplicated region for block: B:65:? A[Catch:{ Exception -> 0x0164 }, RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fillNumber() {
            /*
                r10 = this;
                java.lang.String r0 = "android.permission.READ_PHONE_STATE"
                boolean r1 = r10.numberFilled
                if (r1 != 0) goto L_0x0168
                org.telegram.ui.LoginActivity r1 = r10.this$0
                int r1 = r1.activityMode
                if (r1 == 0) goto L_0x0010
                goto L_0x0168
            L_0x0010:
                android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0164 }
                java.lang.String r2 = "phone"
                java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ Exception -> 0x0164 }
                android.telephony.TelephonyManager r1 = (android.telephony.TelephonyManager) r1     // Catch:{ Exception -> 0x0164 }
                boolean r2 = org.telegram.messenger.AndroidUtilities.isSimAvailable()     // Catch:{ Exception -> 0x0164 }
                if (r2 == 0) goto L_0x0168
                int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0164 }
                r3 = 23
                r4 = 0
                r5 = 1
                if (r2 < r3) goto L_0x00a8
                org.telegram.ui.LoginActivity r3 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                android.app.Activity r3 = r3.getParentActivity()     // Catch:{ Exception -> 0x0164 }
                int r3 = r3.checkSelfPermission(r0)     // Catch:{ Exception -> 0x0164 }
                if (r3 != 0) goto L_0x0036
                r3 = 1
                goto L_0x0037
            L_0x0036:
                r3 = 0
            L_0x0037:
                java.lang.String r6 = "android.permission.READ_PHONE_NUMBERS"
                r7 = 26
                if (r2 < r7) goto L_0x004c
                org.telegram.ui.LoginActivity r8 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                android.app.Activity r8 = r8.getParentActivity()     // Catch:{ Exception -> 0x0164 }
                int r8 = r8.checkSelfPermission(r6)     // Catch:{ Exception -> 0x0164 }
                if (r8 != 0) goto L_0x004a
                goto L_0x004c
            L_0x004a:
                r8 = 0
                goto L_0x004d
            L_0x004c:
                r8 = 1
            L_0x004d:
                org.telegram.ui.LoginActivity r9 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                boolean r9 = r9.checkShowPermissions     // Catch:{ Exception -> 0x0164 }
                if (r9 == 0) goto L_0x00aa
                if (r3 == 0) goto L_0x0059
                if (r8 != 0) goto L_0x00aa
            L_0x0059:
                org.telegram.ui.LoginActivity r1 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.util.ArrayList r1 = r1.permissionsShowItems     // Catch:{ Exception -> 0x0164 }
                r1.clear()     // Catch:{ Exception -> 0x0164 }
                if (r3 != 0) goto L_0x006d
                org.telegram.ui.LoginActivity r1 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.util.ArrayList r1 = r1.permissionsShowItems     // Catch:{ Exception -> 0x0164 }
                r1.add(r0)     // Catch:{ Exception -> 0x0164 }
            L_0x006d:
                if (r8 != 0) goto L_0x007a
                if (r2 < r7) goto L_0x007a
                org.telegram.ui.LoginActivity r0 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.util.ArrayList r0 = r0.permissionsShowItems     // Catch:{ Exception -> 0x0164 }
                r0.add(r6)     // Catch:{ Exception -> 0x0164 }
            L_0x007a:
                org.telegram.ui.LoginActivity r0 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.util.ArrayList r0 = r0.permissionsShowItems     // Catch:{ Exception -> 0x0164 }
                boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0164 }
                if (r0 != 0) goto L_0x00a7
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.LoginActivity r1 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.util.ArrayList r1 = r1.permissionsShowItems     // Catch:{ Exception -> 0x0164 }
                r0.<init>(r1)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda12     // Catch:{ Exception -> 0x0164 }
                r1.<init>(r10, r0)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.LoginActivity r0 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                boolean r0 = r0.isAnimatingIntro     // Catch:{ Exception -> 0x0164 }
                if (r0 == 0) goto L_0x00a4
                org.telegram.ui.LoginActivity r0 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                java.lang.Runnable unused = r0.animationFinishCallback = r1     // Catch:{ Exception -> 0x0164 }
                goto L_0x00a7
            L_0x00a4:
                r1.run()     // Catch:{ Exception -> 0x0164 }
            L_0x00a7:
                return
            L_0x00a8:
                r3 = 1
                r8 = 1
            L_0x00aa:
                r10.numberFilled = r5     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.LoginActivity r0 = r10.this$0     // Catch:{ Exception -> 0x0164 }
                boolean r0 = r0.newAccount     // Catch:{ Exception -> 0x0164 }
                if (r0 != 0) goto L_0x0168
                if (r3 == 0) goto L_0x0168
                if (r8 == 0) goto L_0x0168
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.codeField     // Catch:{ Exception -> 0x0164 }
                r2 = 0
                r0.setAlpha(r2)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                r0.setAlpha(r2)     // Catch:{ Exception -> 0x0164 }
                java.lang.String r0 = r1.getLine1Number()     // Catch:{ Exception -> 0x0164 }
                java.lang.String r0 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0)     // Catch:{ Exception -> 0x0164 }
                r1 = 0
                boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0164 }
                if (r2 != 0) goto L_0x011c
                int r2 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r3 = 4
                if (r2 <= r3) goto L_0x0107
            L_0x00d9:
                if (r3 < r5) goto L_0x00f7
                java.lang.String r2 = r0.substring(r4, r3)     // Catch:{ Exception -> 0x0164 }
                java.util.HashMap<java.lang.String, org.telegram.ui.CountrySelectActivity$Country> r6 = r10.codesMap     // Catch:{ Exception -> 0x0164 }
                java.lang.Object r6 = r6.get(r2)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.CountrySelectActivity$Country r6 = (org.telegram.ui.CountrySelectActivity.Country) r6     // Catch:{ Exception -> 0x0164 }
                if (r6 == 0) goto L_0x00f4
                java.lang.String r1 = r0.substring(r3)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r3 = r10.codeField     // Catch:{ Exception -> 0x0164 }
                r3.setText(r2)     // Catch:{ Exception -> 0x0164 }
                r2 = 1
                goto L_0x00f8
            L_0x00f4:
                int r3 = r3 + -1
                goto L_0x00d9
            L_0x00f7:
                r2 = 0
            L_0x00f8:
                if (r2 != 0) goto L_0x0107
                java.lang.String r1 = r0.substring(r5)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r2 = r10.codeField     // Catch:{ Exception -> 0x0164 }
                java.lang.String r0 = r0.substring(r4, r5)     // Catch:{ Exception -> 0x0164 }
                r2.setText(r0)     // Catch:{ Exception -> 0x0164 }
            L_0x0107:
                if (r1 == 0) goto L_0x011c
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                r0.requestFocus()     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                r0.setText(r1)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                int r1 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r0.setSelection(r1)     // Catch:{ Exception -> 0x0164 }
            L_0x011c:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                int r0 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r1 = 1065353216(0x3var_, float:1.0)
                if (r0 <= 0) goto L_0x0159
                android.animation.AnimatorSet r0 = new android.animation.AnimatorSet     // Catch:{ Exception -> 0x0164 }
                r0.<init>()     // Catch:{ Exception -> 0x0164 }
                r2 = 300(0x12c, double:1.48E-321)
                android.animation.AnimatorSet r0 = r0.setDuration(r2)     // Catch:{ Exception -> 0x0164 }
                r2 = 2
                android.animation.Animator[] r2 = new android.animation.Animator[r2]     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r3 = r10.codeField     // Catch:{ Exception -> 0x0164 }
                android.util.Property r6 = android.view.View.ALPHA     // Catch:{ Exception -> 0x0164 }
                float[] r7 = new float[r5]     // Catch:{ Exception -> 0x0164 }
                r7[r4] = r1     // Catch:{ Exception -> 0x0164 }
                android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r6, r7)     // Catch:{ Exception -> 0x0164 }
                r2[r4] = r3     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r3 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                android.util.Property r6 = android.view.View.ALPHA     // Catch:{ Exception -> 0x0164 }
                float[] r7 = new float[r5]     // Catch:{ Exception -> 0x0164 }
                r7[r4] = r1     // Catch:{ Exception -> 0x0164 }
                android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r3, r6, r7)     // Catch:{ Exception -> 0x0164 }
                r2[r5] = r1     // Catch:{ Exception -> 0x0164 }
                r0.playTogether(r2)     // Catch:{ Exception -> 0x0164 }
                r0.start()     // Catch:{ Exception -> 0x0164 }
                r10.confirmedNumber = r5     // Catch:{ Exception -> 0x0164 }
                goto L_0x0168
            L_0x0159:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.codeField     // Catch:{ Exception -> 0x0164 }
                r0.setAlpha(r1)     // Catch:{ Exception -> 0x0164 }
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r10.phoneField     // Catch:{ Exception -> 0x0164 }
                r0.setAlpha(r1)     // Catch:{ Exception -> 0x0164 }
                goto L_0x0168
            L_0x0164:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0168:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.fillNumber():void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$fillNumber$21(List list) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (globalMainSettings.getBoolean("firstloginshow", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                globalMainSettings.edit().putBoolean("firstloginshow", false).commit();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTopAnimation(NUM, 46, false, Theme.getColor("dialogTopBackground"));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                LoginActivity loginActivity = this.this$0;
                Dialog unused = loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
                boolean unused2 = this.this$0.needRequestPermissions = true;
                return;
            }
            this.this$0.getParentActivity().requestPermissions((String[]) list.toArray(new String[0]), 7);
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda9(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$22() {
            if (this.phoneField == null) {
                return;
            }
            if (this.this$0.needRequestPermissions) {
                this.codeField.clearFocus();
                this.phoneField.clearFocus();
            } else if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                if (!this.numberFilled) {
                    AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                    animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                }
                boolean unused = this.this$0.showKeyboard(this.phoneField);
            } else {
                this.codeField.requestFocus();
                boolean unused2 = this.this$0.showKeyboard(this.codeField);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("YourPhone", NUM);
        }

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

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.emojiLoaded) {
                this.countryButton.getCurrentView().invalidate();
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private RLottieImageView blueImageView;
        private FrameLayout bottomContainer;
        private String catchedPhone;
        private CodeFieldContainer codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private RLottieDrawable dotsDrawable;
        private RLottieDrawable dotsToStarsDrawable;
        private String emailPhone;
        /* access modifiers changed from: private */
        public Runnable errorColorTimeout = new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this);
        private ViewSwitcher errorViewSwitcher;
        RLottieDrawable hintDrawable;
        private boolean isDotsAnimationVisible;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        private String lastError = "";
        private int length;
        private ImageView missedCallArrowIcon;
        private TextView missedCallDescriptionSubtitle;
        private ImageView missedCallPhoneIcon;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        /* access modifiers changed from: private */
        public int openTime;
        private String pattern = "*";
        private String phone;
        private String phoneHash;
        /* access modifiers changed from: private */
        public boolean postedErrorColorTimeout;
        private String prefix = "";
        private TextView prefixTextView;
        private FrameLayout problemFrame;
        private TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        private String requestPhone;
        private RLottieDrawable starsToDotsDrawable;
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public int time = 60000;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private boolean waitingForEvent;
        private TextView wrongCode;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onBackPressed$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public boolean needBackButton() {
            return true;
        }

        static /* synthetic */ int access$8126(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = (double) loginActivitySmsView.codeTime;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$8726(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = (double) loginActivitySmsView.time;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.time = i;
            return i;
        }

        /* access modifiers changed from: private */
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x03e2  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0402  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x04f9  */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x0523  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0533  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x0558  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.LoginActivity r35, android.content.Context r36, int r37) {
            /*
                r34 = this;
                r0 = r34
                r1 = r35
                r2 = r36
                r0.this$0 = r1
                r0.<init>(r2)
                java.lang.Object r3 = new java.lang.Object
                r3.<init>()
                r0.timerSync = r3
                r3 = 60000(0xea60, float:8.4078E-41)
                r0.time = r3
                r3 = 15000(0x3a98, float:2.102E-41)
                r0.codeTime = r3
                java.lang.String r3 = ""
                r0.lastError = r3
                java.lang.String r4 = "*"
                r0.pattern = r4
                r0.prefix = r3
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8 r3 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8
                r3.<init>(r0)
                r0.errorColorTimeout = r3
                r3 = r37
                r0.currentType = r3
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.confirmTextView = r4
                r5 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1073741824(0x40000000, float:2.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r7 = (float) r7
                r8 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r7, r8)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.titleTextView = r4
                r7 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r7)
                android.widget.TextView r4 = r0.titleTextView
                java.lang.String r7 = "fonts/rmedium.ttf"
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r4.setTypeface(r9)
                android.widget.TextView r4 = r0.titleTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                r10 = 3
                if (r9 == 0) goto L_0x006e
                r9 = 5
                goto L_0x006f
            L_0x006e:
                r9 = 3
            L_0x006f:
                r4.setGravity(r9)
                android.widget.TextView r4 = r0.titleTextView
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r4.setLineSpacing(r9, r8)
                android.widget.TextView r4 = r0.titleTextView
                r9 = 49
                r4.setGravity(r9)
                int r4 = r35.activityMode
                if (r4 == r3) goto L_0x008b
                r4 = 0
                goto L_0x0092
            L_0x008b:
                r4 = 2131624820(0x7f0e0374, float:1.887683E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString((int) r4)
            L_0x0092:
                int r12 = r0.currentType
                r13 = 17
                r14 = 11
                r15 = -1
                r11 = -2
                r9 = 0
                if (r12 != r14) goto L_0x01e1
                android.widget.TextView r12 = r0.titleTextView
                if (r4 == 0) goto L_0x00a2
                goto L_0x00ab
            L_0x00a2:
                r4 = 2131626726(0x7f0e0ae6, float:1.8880696E38)
                java.lang.String r14 = "MissedCallDescriptionTitle"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r4)
            L_0x00ab:
                r12.setText(r4)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.missedCallArrowIcon = r12
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.missedCallPhoneIcon = r12
                android.widget.ImageView r12 = r0.missedCallArrowIcon
                r4.addView(r12)
                android.widget.ImageView r12 = r0.missedCallPhoneIcon
                r4.addView(r12)
                android.widget.ImageView r12 = r0.missedCallArrowIcon
                r14 = 2131165590(0x7var_, float:1.7945401E38)
                r12.setImageResource(r14)
                android.widget.ImageView r12 = r0.missedCallPhoneIcon
                r14 = 2131165591(0x7var_, float:1.7945403E38)
                r12.setImageResource(r14)
                r17 = 64
                r18 = 64
                r19 = 1
                r20 = 0
                r21 = 16
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r12)
                android.widget.TextView r4 = r0.titleTextView
                r17 = -2
                r18 = -2
                r19 = 49
                r21 = 8
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r12)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.missedCallDescriptionSubtitle = r4
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r4.setGravity(r3)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r12 = (float) r12
                r4.setLineSpacing(r12, r8)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r12 = 2131626724(0x7f0e0ae4, float:1.8880692E38)
                java.lang.String r14 = "MissedCallDescriptionSubtitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
                android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r12)
                r4.setText(r12)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r17 = -1
                r20 = 36
                r21 = 16
                r22 = 36
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r12)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$1 r4 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$1
                r4.<init>(r2, r1)
                r0.codeFieldContainer = r4
                android.widget.LinearLayout r4 = new android.widget.LinearLayout
                r4.<init>(r2)
                r4.setOrientation(r9)
                android.widget.TextView r12 = new android.widget.TextView
                r12.<init>(r2)
                r0.prefixTextView = r12
                r14 = 1101004800(0x41a00000, float:20.0)
                r12.setTextSize(r3, r14)
                android.widget.TextView r12 = r0.prefixTextView
                r12.setMaxLines(r3)
                android.widget.TextView r12 = r0.prefixTextView
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r12.setTypeface(r7)
                android.widget.TextView r7 = r0.prefixTextView
                r7.setPadding(r9, r9, r9, r9)
                android.widget.TextView r7 = r0.prefixTextView
                r12 = 16
                r7.setGravity(r12)
                android.widget.TextView r7 = r0.prefixTextView
                r17 = -2
                r18 = -1
                r19 = 16
                r20 = 0
                r21 = 0
                r22 = 4
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r4.addView(r7, r12)
                org.telegram.ui.CodeFieldContainer r7 = r0.codeFieldContainer
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r15)
                r4.addView(r7, r12)
                r18 = 34
                r19 = 1
                r21 = 28
                r22 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r7)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.missedCallDescriptionSubtitle = r4
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r4.setGravity(r3)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r8)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r5 = 2131626725(0x7f0e0ae5, float:1.8880694E38)
                java.lang.String r7 = "MissedCallDescriptionSubtitle2"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
                android.text.SpannableStringBuilder r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5)
                r4.setText(r5)
                android.widget.TextView r4 = r0.missedCallDescriptionSubtitle
                r17 = -1
                r18 = -2
                r19 = 49
                r20 = 36
                r22 = 36
                r23 = 12
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r5)
                goto L_0x03db
            L_0x01e1:
                r5 = 64
                if (r12 != r10) goto L_0x0292
                android.widget.TextView r7 = r0.confirmTextView
                r7.setGravity(r3)
                android.widget.FrameLayout r7 = new android.widget.FrameLayout
                r7.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r9, (float) r8)
                r0.addView(r7, r12)
                android.widget.LinearLayout r12 = new android.widget.LinearLayout
                r12.<init>(r2)
                r12.setOrientation(r3)
                r12.setGravity(r3)
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r11, (int) r13)
                r7.addView(r12, r14)
                android.view.ViewGroup$LayoutParams r14 = r12.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
                boolean r17 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r17 == 0) goto L_0x0216
                r15 = 0
                goto L_0x021a
            L_0x0216:
                int r17 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                r15 = r17
            L_0x021a:
                r14.bottomMargin = r15
                android.widget.FrameLayout r14 = new android.widget.FrameLayout
                r14.<init>(r2)
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r3)
                r12.addView(r14, r15)
                org.telegram.ui.Components.RLottieImageView r15 = new org.telegram.ui.Components.RLottieImageView
                r15.<init>(r2)
                r0.blueImageView = r15
                org.telegram.ui.Components.RLottieDrawable r15 = new org.telegram.ui.Components.RLottieDrawable
                r20 = 2131558500(0x7f0d0064, float:1.8742318E38)
                r17 = 2131558500(0x7f0d0064, float:1.8742318E38)
                java.lang.String r21 = java.lang.String.valueOf(r17)
                r13 = 1115684864(0x42800000, float:64.0)
                int r22 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r24 = 1
                r25 = 0
                r19 = r15
                r19.<init>(r20, r21, r22, r23, r24, r25)
                r0.hintDrawable = r15
                org.telegram.ui.Components.RLottieImageView r11 = r0.blueImageView
                r11.setAnimation(r15)
                org.telegram.ui.Components.RLottieImageView r11 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r13)
                r14.addView(r11, r5)
                android.widget.TextView r5 = r0.titleTextView
                if (r4 == 0) goto L_0x0263
                goto L_0x026a
            L_0x0263:
                r4 = 2131629275(0x7f0e14db, float:1.8885866E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString((int) r4)
            L_0x026a:
                r5.setText(r4)
                android.widget.TextView r4 = r0.titleTextView
                r20 = -2
                r21 = -2
                r22 = 1
                r23 = 0
                r24 = 16
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r12.addView(r4, r5)
                android.widget.TextView r4 = r0.confirmTextView
                r24 = 8
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r12.addView(r4, r5)
                r11 = r7
                goto L_0x03dc
            L_0x0292:
                android.widget.TextView r7 = r0.confirmTextView
                r11 = 49
                r7.setGravity(r11)
                android.widget.FrameLayout r7 = new android.widget.FrameLayout
                r7.<init>(r2)
                r20 = -2
                r21 = -2
                r22 = 49
                r23 = 0
                r24 = 16
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r7, r11)
                int r11 = r0.currentType
                if (r11 != r3) goto L_0x02b9
                r5 = 128(0x80, float:1.794E-43)
            L_0x02b9:
                if (r11 != r3) goto L_0x02dc
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r21 = 2131558429(0x7f0d001d, float:1.8742174E38)
                r12 = 2131558429(0x7f0d001d, float:1.8742174E38)
                java.lang.String r22 = java.lang.String.valueOf(r12)
                float r12 = (float) r5
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r24 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r25 = 1
                r26 = 0
                r20 = r11
                r20.<init>(r21, r22, r23, r24, r25, r26)
                r0.hintDrawable = r11
                goto L_0x0351
            L_0x02dc:
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r28 = 2131558530(0x7f0d0082, float:1.8742378E38)
                r12 = 2131558530(0x7f0d0082, float:1.8742378E38)
                java.lang.String r29 = java.lang.String.valueOf(r12)
                float r12 = (float) r5
                int r30 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r31 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r32 = 1
                r33 = 0
                r27 = r11
                r27.<init>(r28, r29, r30, r31, r32, r33)
                r0.hintDrawable = r11
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r21 = 2131558501(0x7f0d0065, float:1.874232E38)
                r13 = 2131558501(0x7f0d0065, float:1.874232E38)
                java.lang.String r22 = java.lang.String.valueOf(r13)
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r24 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r25 = 1
                r26 = 0
                r20 = r11
                r20.<init>(r21, r22, r23, r24, r25, r26)
                r0.starsToDotsDrawable = r11
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r28 = 2131558498(0x7f0d0062, float:1.8742314E38)
                r13 = 2131558498(0x7f0d0062, float:1.8742314E38)
                java.lang.String r29 = java.lang.String.valueOf(r13)
                int r30 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r31 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r27 = r11
                r27.<init>(r28, r29, r30, r31, r32, r33)
                r0.dotsDrawable = r11
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r21 = 2131558499(0x7f0d0063, float:1.8742316E38)
                r13 = 2131558499(0x7f0d0063, float:1.8742316E38)
                java.lang.String r22 = java.lang.String.valueOf(r13)
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r24 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r20 = r11
                r20.<init>(r21, r22, r23, r24, r25, r26)
                r0.dotsToStarsDrawable = r11
            L_0x0351:
                org.telegram.ui.Components.RLottieImageView r11 = new org.telegram.ui.Components.RLottieImageView
                r11.<init>(r2)
                r0.blueImageView = r11
                org.telegram.ui.Components.RLottieDrawable r12 = r0.hintDrawable
                r11.setAnimation(r12)
                int r11 = r0.currentType
                if (r11 != r3) goto L_0x0374
                boolean r11 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r11 != 0) goto L_0x0374
                org.telegram.ui.Components.RLottieImageView r11 = r0.blueImageView
                r12 = 1103101952(0x41CLASSNAME, float:24.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r12 = -r12
                float r12 = (float) r12
                r11.setTranslationY(r12)
            L_0x0374:
                org.telegram.ui.Components.RLottieImageView r11 = r0.blueImageView
                float r12 = (float) r5
                r22 = 51
                r23 = 0
                r24 = 0
                r25 = 0
                int r13 = r0.currentType
                if (r13 != r3) goto L_0x0394
                boolean r13 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r13 != 0) goto L_0x0394
                r13 = 1098907648(0x41800000, float:16.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r13 = -r13
                float r13 = (float) r13
                r26 = r13
                goto L_0x0397
            L_0x0394:
                r13 = 0
                r26 = 0
            L_0x0397:
                r20 = r5
                r21 = r12
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
                r7.addView(r11, r5)
                android.widget.TextView r5 = r0.titleTextView
                if (r4 == 0) goto L_0x03a7
                goto L_0x03b6
            L_0x03a7:
                int r4 = r0.currentType
                if (r4 != r3) goto L_0x03af
                r4 = 2131628222(0x7f0e10be, float:1.888373E38)
                goto L_0x03b2
            L_0x03af:
                r4 = 2131628227(0x7f0e10c3, float:1.888374E38)
            L_0x03b2:
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString((int) r4)
            L_0x03b6:
                r5.setText(r4)
                android.widget.TextView r4 = r0.titleTextView
                r20 = -2
                r21 = -2
                r22 = 49
                r23 = 0
                r24 = 18
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.confirmTextView
                r24 = 17
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r4, r5)
            L_0x03db:
                r11 = 0
            L_0x03dc:
                int r4 = r0.currentType
                r5 = 11
                if (r4 == r5) goto L_0x03fe
                org.telegram.ui.LoginActivity$LoginActivitySmsView$2 r4 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$2
                r4.<init>(r2, r1)
                r0.codeFieldContainer = r4
                r20 = -2
                r21 = 42
                r22 = 1
                r23 = 0
                r24 = 32
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r4, r5)
            L_0x03fe:
                int r4 = r0.currentType
                if (r4 != r10) goto L_0x0409
                org.telegram.ui.CodeFieldContainer r4 = r0.codeFieldContainer
                r5 = 8
                r4.setVisibility(r5)
            L_0x0409:
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.problemFrame = r4
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.timeText = r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r8)
                android.widget.TextView r4 = r0.timeText
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r7 = 1092616192(0x41200000, float:10.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.setPadding(r9, r5, r9, r7)
                android.widget.TextView r4 = r0.timeText
                r5 = 1097859072(0x41700000, float:15.0)
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.timeText
                r7 = 49
                r4.setGravity(r7)
                android.widget.TextView r4 = r0.timeText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5 r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5
                r12.<init>(r0)
                r4.setOnClickListener(r12)
                android.widget.FrameLayout r4 = r0.problemFrame
                android.widget.TextView r12 = r0.timeText
                r13 = -2
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r7)
                r4.addView(r12, r14)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$3 r4 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$3
                r4.<init>(r0, r2, r1)
                r0.errorViewSwitcher = r4
                r1 = 2130771992(0x7var_, float:1.714709E38)
                android.view.animation.Animation r1 = android.view.animation.AnimationUtils.loadAnimation(r2, r1)
                android.view.animation.Interpolator r4 = org.telegram.ui.Components.Easings.easeInOutQuad
                r1.setInterpolator(r4)
                android.widget.ViewSwitcher r7 = r0.errorViewSwitcher
                r7.setInAnimation(r1)
                r1 = 2130771993(0x7var_, float:1.7147092E38)
                android.view.animation.Animation r1 = android.view.animation.AnimationUtils.loadAnimation(r2, r1)
                r1.setInterpolator(r4)
                android.widget.ViewSwitcher r4 = r0.errorViewSwitcher
                r4.setOutAnimation(r1)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.problemText = r1
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r4 = (float) r4
                r1.setLineSpacing(r4, r8)
                android.widget.TextView r1 = r0.problemText
                r1.setTextSize(r3, r5)
                android.widget.TextView r1 = r0.problemText
                r4 = 49
                r1.setGravity(r4)
                android.widget.TextView r1 = r0.problemText
                r4 = 1082130432(0x40800000, float:4.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r1.setPadding(r9, r7, r9, r12)
                android.widget.FrameLayout r1 = r0.problemFrame
                android.widget.TextView r7 = r0.problemText
                r12 = 17
                r13 = -2
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
                r1.addView(r7, r12)
                android.widget.ViewSwitcher r1 = r0.errorViewSwitcher
                android.widget.FrameLayout r7 = r0.problemFrame
                r1.addView(r7)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.wrongCode = r1
                r7 = 2131629252(0x7f0e14c4, float:1.888582E38)
                java.lang.String r12 = "WrongCode"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r12, r7)
                r1.setText(r7)
                android.widget.TextView r1 = r0.wrongCode
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                r1.setLineSpacing(r6, r8)
                android.widget.TextView r1 = r0.wrongCode
                r1.setTextSize(r3, r5)
                android.widget.TextView r1 = r0.wrongCode
                r5 = 49
                r1.setGravity(r5)
                android.widget.TextView r1 = r0.wrongCode
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r1.setPadding(r9, r5, r9, r4)
                android.widget.ViewSwitcher r1 = r0.errorViewSwitcher
                android.widget.TextView r4 = r0.wrongCode
                r1.addView(r4)
                int r1 = r0.currentType
                if (r1 != r3) goto L_0x0523
                int r1 = r0.nextType
                if (r1 == r10) goto L_0x0514
                r3 = 4
                if (r1 == r3) goto L_0x0514
                r3 = 11
                if (r1 != r3) goto L_0x0505
                goto L_0x0514
            L_0x0505:
                android.widget.TextView r1 = r0.problemText
                r3 = 2131625475(0x7f0e0603, float:1.887816E38)
                java.lang.String r4 = "DidNotGetTheCodeSms"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setText(r3)
                goto L_0x0531
            L_0x0514:
                android.widget.TextView r1 = r0.problemText
                r3 = 2131625474(0x7f0e0602, float:1.8878157E38)
                java.lang.String r4 = "DidNotGetTheCodePhone"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setText(r3)
                goto L_0x0531
            L_0x0523:
                android.widget.TextView r1 = r0.problemText
                r3 = 2131625470(0x7f0e05fe, float:1.8878149E38)
                java.lang.String r4 = "DidNotGetTheCode"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setText(r3)
            L_0x0531:
                if (r11 != 0) goto L_0x0558
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r0.bottomContainer = r1
                android.widget.ViewSwitcher r3 = r0.errorViewSwitcher
                r10 = -2
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 81
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r1.addView(r3, r4)
                android.widget.FrameLayout r1 = r0.bottomContainer
                r3 = -1
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r9, (float) r8)
                r0.addView(r1, r3)
                goto L_0x056b
            L_0x0558:
                android.widget.ViewSwitcher r1 = r0.errorViewSwitcher
                r3 = -2
                r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r5 = 81
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
                r11.addView(r1, r3)
            L_0x056b:
                android.widget.ViewSwitcher r1 = r0.errorViewSwitcher
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r1)
                android.widget.TextView r1 = r0.problemText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6
                r3.<init>(r0, r2)
                r1.setOnClickListener(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            int i = this.nextType;
            if (i == 4 || i == 2 || i == 11) {
                this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                int i2 = this.nextType;
                if (i2 == 4 || i2 == 11) {
                    this.timeText.setText(LocaleController.getString("Calling", NUM));
                } else {
                    this.timeText.setText(LocaleController.getString("SendingSms", NUM));
                }
                Bundle bundle = new Bundle();
                bundle.putString("phone", this.phone);
                bundle.putString("ephone", this.emailPhone);
                bundle.putString("phoneFormated", this.requestPhone);
                createCodeTimer();
                TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
                tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
                tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda36(this, bundle), 10);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                this.waitingForEvent = false;
                destroyCodeTimer();
                resendCode();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(Bundle bundle, TLObject tLObject) {
            this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda25(this, bundle, tLObject));
            } else if (tLRPC$TL_error != null && tLRPC$TL_error.text != null) {
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda28(this, tLRPC$TL_error));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(TLRPC$TL_error tLRPC$TL_error) {
            this.lastError = tLRPC$TL_error.text;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(Context context, View view) {
            if (!this.nextPressed) {
                if (this.nextType == 0) {
                    new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DidNotGetTheCodeInfo", NUM, this.phone))).setNeutralButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda0(this)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this)).show();
                } else if (this.this$0.radialProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(DialogInterface dialogInterface, int i) {
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                Intent intent = new Intent("android.intent.action.SENDTO");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + this.emailPhone);
                intent.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                getContext().startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (Exception unused) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("NoMailInstalled", NUM));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        public void updateColors() {
            String str = "windowBackgroundWhiteGrayText6";
            this.confirmTextView.setTextColor(Theme.getColor(this.this$0.isInCancelAccountDeletionMode() ? "windowBackgroundWhiteBlackText" : str));
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
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            if (codeFieldContainer2 != null) {
                codeFieldContainer2.invalidate();
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

        public boolean hasCustomKeyboard() {
            return this.currentType != 3;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            tryShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda35(this, bundle), 10));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$9(Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda29(this, tLRPC$TL_error, bundle, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$8(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str = tLRPC$TL_error.text;
                if (str != null) {
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        onBackPressed(true);
                        this.this$0.setPage(0, true, (Bundle) null, true);
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.code != -1000) {
                        LoginActivity loginActivity = this.this$0;
                        String string = LocaleController.getString(NUM);
                        loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
                    }
                }
            }
            tryHideProgress(false);
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration configuration) {
            CodeNumberField[] codeNumberFieldArr;
            super.onConfigurationChanged(configuration);
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            if (codeFieldContainer2 != null && (codeNumberFieldArr = codeFieldContainer2.codeField) != null) {
                for (CodeNumberField codeNumberField : codeNumberFieldArr) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        codeNumberField.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || this.this$0.isCustomKeyboardForceDisabled());
                    }
                }
            }
        }

        private void tryShowProgress(int i) {
            lambda$tryShowProgress$10(i, true);
        }

        /* access modifiers changed from: private */
        /* renamed from: tryShowProgress */
        public void lambda$tryShowProgress$10(int i, boolean z) {
            if (this.starsToDotsDrawable == null) {
                this.this$0.needShowProgress(i, z);
            } else if (!this.isDotsAnimationVisible) {
                this.isDotsAnimationVisible = true;
                if (this.hintDrawable.getCurrentFrame() != this.hintDrawable.getFramesCount() - 1) {
                    this.hintDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21(this, i, z));
                    return;
                }
                this.starsToDotsDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(this));
                this.blueImageView.setAutoRepeat(false);
                this.starsToDotsDrawable.setCurrentFrame(0, false);
                this.blueImageView.setAnimation(this.starsToDotsDrawable);
                this.blueImageView.playAnimation();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryShowProgress$11(int i, boolean z) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda22(this, i, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryShowProgress$13() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda15(this));
        }

        /* access modifiers changed from: private */
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
                this.this$0.needHideProgress(z, z2);
            } else if (this.isDotsAnimationVisible) {
                this.isDotsAnimationVisible = false;
                this.blueImageView.setAutoRepeat(false);
                this.dotsDrawable.setAutoRepeat(0);
                this.dotsDrawable.setOnFinishCallback(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda16(this), this.dotsDrawable.getFramesCount() - 1);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$17() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$15() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda11(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$16() {
            this.dotsToStarsDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda14(this));
            this.blueImageView.setAutoRepeat(false);
            this.dotsToStarsDrawable.setCurrentFrame(0, false);
            this.blueImageView.setAnimation(this.dotsToStarsDrawable);
            this.blueImageView.playAnimation();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$tryHideProgress$14() {
            this.blueImageView.setAutoRepeat(false);
            this.blueImageView.setAnimation(this.hintDrawable);
        }

        public String getHeaderName() {
            int i = this.currentType;
            if (i == 3 || i == 11) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        /* JADX WARNING: Removed duplicated region for block: B:91:0x02d5  */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x02de  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setParams(android.os.Bundle r17, boolean r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                if (r1 != 0) goto L_0x0007
                return
            L_0x0007:
                r2 = 1
                r0.waitingForEvent = r2
                int r3 = r0.currentType
                r4 = 3
                r5 = 2
                if (r3 != r5) goto L_0x001d
                org.telegram.messenger.AndroidUtilities.setWaitingForSms(r2)
                org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r6 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
                r3.addObserver(r0, r6)
                goto L_0x002b
            L_0x001d:
                if (r3 != r4) goto L_0x002b
                org.telegram.messenger.AndroidUtilities.setWaitingForCall(r2)
                org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r6 = org.telegram.messenger.NotificationCenter.didReceiveCall
                r3.addObserver(r0, r6)
            L_0x002b:
                r0.currentParams = r1
                java.lang.String r3 = "phone"
                java.lang.String r3 = r1.getString(r3)
                r0.phone = r3
                java.lang.String r3 = "ephone"
                java.lang.String r3 = r1.getString(r3)
                r0.emailPhone = r3
                java.lang.String r3 = "phoneFormated"
                java.lang.String r3 = r1.getString(r3)
                r0.requestPhone = r3
                java.lang.String r3 = "phoneHash"
                java.lang.String r3 = r1.getString(r3)
                r0.phoneHash = r3
                java.lang.String r3 = "timeout"
                int r3 = r1.getInt(r3)
                r0.time = r3
                long r6 = java.lang.System.currentTimeMillis()
                r8 = 1000(0x3e8, double:4.94E-321)
                long r6 = r6 / r8
                int r3 = (int) r6
                r0.openTime = r3
                java.lang.String r3 = "nextType"
                int r3 = r1.getInt(r3)
                r0.nextType = r3
                java.lang.String r3 = "pattern"
                java.lang.String r3 = r1.getString(r3)
                r0.pattern = r3
                java.lang.String r3 = "prefix"
                java.lang.String r3 = r1.getString(r3)
                r0.prefix = r3
                java.lang.String r3 = "length"
                int r1 = r1.getInt(r3)
                r0.length = r1
                if (r1 != 0) goto L_0x0084
                r1 = 5
                r0.length = r1
            L_0x0084:
                org.telegram.ui.CodeFieldContainer r1 = r0.codeFieldContainer
                int r3 = r0.length
                int r6 = r0.currentType
                r1.setNumbersCount(r3, r6)
                org.telegram.ui.CodeFieldContainer r1 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r1 = r1.codeField
                int r3 = r1.length
                r6 = 0
                r7 = 0
            L_0x0094:
                if (r7 >= r3) goto L_0x00c6
                r8 = r1[r7]
                int r9 = android.os.Build.VERSION.SDK_INT
                r10 = 21
                if (r9 < r10) goto L_0x00b3
                boolean r9 = r16.hasCustomKeyboard()
                if (r9 == 0) goto L_0x00af
                org.telegram.ui.LoginActivity r9 = r0.this$0
                boolean r9 = r9.isCustomKeyboardForceDisabled()
                if (r9 == 0) goto L_0x00ad
                goto L_0x00af
            L_0x00ad:
                r9 = 0
                goto L_0x00b0
            L_0x00af:
                r9 = 1
            L_0x00b0:
                r8.setShowSoftInputOnFocusCompat(r9)
            L_0x00b3:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$4 r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$4
                r9.<init>()
                r8.addTextChangedListener(r9)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7 r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7
                r9.<init>(r0)
                r8.setOnFocusChangeListener(r9)
                int r7 = r7 + 1
                goto L_0x0094
            L_0x00c6:
                java.lang.String r1 = r0.phone
                if (r1 != 0) goto L_0x00cb
                return
            L_0x00cb:
                org.telegram.PhoneFormat.PhoneFormat r1 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.String r3 = r0.phone
                java.lang.String r1 = r1.format(r3)
                org.telegram.ui.LoginActivity r3 = r0.this$0
                boolean r3 = r3.isInCancelAccountDeletionMode()
                java.lang.String r7 = "+"
                r8 = 4
                java.lang.String r9 = ""
                if (r3 == 0) goto L_0x0142
                android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
                r10 = 2131624821(0x7f0e0375, float:1.8876833E38)
                java.lang.Object[] r11 = new java.lang.Object[r2]
                org.telegram.PhoneFormat.PhoneFormat r12 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                r13.append(r7)
                r13.append(r1)
                java.lang.String r1 = r13.toString()
                java.lang.String r1 = r12.format(r1)
                r11[r6] = r1
                java.lang.String r1 = "CancelAccountResetInfo2"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r10, r11)
                android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
                r3.<init>(r1)
                r1 = 42
                int r10 = android.text.TextUtils.indexOf(r3, r1)
                int r1 = android.text.TextUtils.lastIndexOf(r3, r1)
                r11 = -1
                if (r10 == r11) goto L_0x01a5
                if (r1 == r11) goto L_0x01a5
                if (r10 == r1) goto L_0x01a5
                android.widget.TextView r11 = r0.confirmTextView
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r12 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r12.<init>()
                r11.setMovementMethod(r12)
                int r11 = r1 + 1
                r3.replace(r1, r11, r9)
                int r11 = r10 + 1
                r3.replace(r10, r11, r9)
                org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline
                java.lang.String r12 = "tg://settings/change_number"
                r11.<init>(r12)
                int r1 = r1 - r2
                r12 = 33
                r3.setSpan(r11, r10, r1, r12)
                goto L_0x01a5
            L_0x0142:
                int r3 = r0.currentType
                if (r3 != r2) goto L_0x015c
                r3 = 2131628223(0x7f0e10bf, float:1.8883733E38)
                java.lang.Object[] r10 = new java.lang.Object[r2]
                java.lang.String r1 = org.telegram.messenger.LocaleController.addNbsp(r1)
                r10[r6] = r1
                java.lang.String r1 = "SentAppCodeWithPhone"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r10)
                android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
                goto L_0x01a5
            L_0x015c:
                if (r3 != r5) goto L_0x0174
                r3 = 2131628226(0x7f0e10c2, float:1.8883739E38)
                java.lang.Object[] r10 = new java.lang.Object[r2]
                java.lang.String r1 = org.telegram.messenger.LocaleController.addNbsp(r1)
                r10[r6] = r1
                java.lang.String r1 = "SentSmsCode"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r10)
                android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
                goto L_0x01a5
            L_0x0174:
                if (r3 != r4) goto L_0x018c
                r3 = 2131628224(0x7f0e10c0, float:1.8883735E38)
                java.lang.Object[] r10 = new java.lang.Object[r2]
                java.lang.String r1 = org.telegram.messenger.LocaleController.addNbsp(r1)
                r10[r6] = r1
                java.lang.String r1 = "SentCallCode"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r10)
                android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
                goto L_0x01a5
            L_0x018c:
                if (r3 != r8) goto L_0x01a4
                r3 = 2131628225(0x7f0e10c1, float:1.8883737E38)
                java.lang.Object[] r10 = new java.lang.Object[r2]
                java.lang.String r1 = org.telegram.messenger.LocaleController.addNbsp(r1)
                r10[r6] = r1
                java.lang.String r1 = "SentCallOnly"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r10)
                android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
                goto L_0x01a5
            L_0x01a4:
                r3 = r9
            L_0x01a5:
                android.widget.TextView r1 = r0.confirmTextView
                r1.setText(r3)
                int r1 = r0.currentType
                if (r1 == r4) goto L_0x01c3
                org.telegram.ui.LoginActivity r1 = r0.this$0
                org.telegram.ui.CodeFieldContainer r3 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r3 = r3.codeField
                r3 = r3[r6]
                boolean unused = r1.showKeyboard(r3)
                org.telegram.ui.CodeFieldContainer r1 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r1 = r1.codeField
                r1 = r1[r6]
                r1.requestFocus()
                goto L_0x01cc
            L_0x01c3:
                org.telegram.ui.CodeFieldContainer r1 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r1 = r1.codeField
                r1 = r1[r6]
                org.telegram.messenger.AndroidUtilities.hideKeyboard(r1)
            L_0x01cc:
                r16.destroyTimer()
                r16.destroyCodeTimer()
                long r10 = java.lang.System.currentTimeMillis()
                double r10 = (double) r10
                r0.lastCurrentTime = r10
                int r1 = r0.currentType
                r3 = 11
                r10 = 8
                if (r1 != r2) goto L_0x01eb
                r0.setProblemTextVisible(r2)
                android.widget.TextView r1 = r0.timeText
                r1.setVisibility(r10)
                goto L_0x0326
            L_0x01eb:
                r11 = 2131628375(0x7f0e1157, float:1.888404E38)
                java.lang.String r12 = "SmsAvailableIn"
                r13 = 2131624783(0x7f0e034f, float:1.8876755E38)
                java.lang.String r14 = "CallAvailableIn"
                r15 = 0
                if (r1 != r4) goto L_0x025b
                int r10 = r0.nextType
                if (r10 == r8) goto L_0x01fe
                if (r10 != r5) goto L_0x025b
            L_0x01fe:
                r0.setProblemTextVisible(r6)
                android.widget.TextView r1 = r0.timeText
                r1.setVisibility(r6)
                int r1 = r0.nextType
                if (r1 == r8) goto L_0x0227
                if (r1 != r3) goto L_0x020d
                goto L_0x0227
            L_0x020d:
                if (r1 != r5) goto L_0x023e
                android.widget.TextView r1 = r0.timeText
                java.lang.Object[] r4 = new java.lang.Object[r5]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r2)
                r4[r6] = r5
                java.lang.Integer r5 = java.lang.Integer.valueOf(r6)
                r4[r2] = r5
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r11, r4)
                r1.setText(r2)
                goto L_0x023e
            L_0x0227:
                android.widget.TextView r1 = r0.timeText
                java.lang.Object[] r4 = new java.lang.Object[r5]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r2)
                r4[r6] = r5
                java.lang.Integer r5 = java.lang.Integer.valueOf(r6)
                r4[r2] = r5
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r14, r13, r4)
                r1.setText(r2)
            L_0x023e:
                if (r18 == 0) goto L_0x0246
                java.lang.String r1 = r0.pattern
                java.lang.String r15 = org.telegram.messenger.AndroidUtilities.obtainLoginPhoneCall(r1)
            L_0x0246:
                if (r15 == 0) goto L_0x024d
                r0.onNextPressed(r15)
                goto L_0x0326
            L_0x024d:
                java.lang.String r1 = r0.catchedPhone
                if (r1 == 0) goto L_0x0256
                r0.onNextPressed(r1)
                goto L_0x0326
            L_0x0256:
                r16.createTimer()
                goto L_0x0326
            L_0x025b:
                r10 = 1000(0x3e8, float:1.401E-42)
                if (r1 != r5) goto L_0x02e2
                int r3 = r0.nextType
                if (r3 == r8) goto L_0x0265
                if (r3 != r4) goto L_0x02e2
            L_0x0265:
                android.widget.TextView r1 = r0.timeText
                java.lang.Object[] r3 = new java.lang.Object[r5]
                java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
                r3[r6] = r4
                java.lang.Integer r4 = java.lang.Integer.valueOf(r6)
                r3[r2] = r4
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r14, r13, r3)
                r1.setText(r3)
                int r1 = r0.time
                if (r1 >= r10) goto L_0x0282
                r1 = 1
                goto L_0x0283
            L_0x0282:
                r1 = 0
            L_0x0283:
                r0.setProblemTextVisible(r1)
                android.widget.TextView r1 = r0.timeText
                int r3 = r0.time
                if (r3 >= r10) goto L_0x028f
                r10 = 8
                goto L_0x0290
            L_0x028f:
                r10 = 0
            L_0x0290:
                r1.setVisibility(r10)
                android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
                java.lang.String r3 = "mainconfig"
                android.content.SharedPreferences r1 = r1.getSharedPreferences(r3, r6)
                java.lang.String r3 = "sms_hash"
                java.lang.String r3 = r1.getString(r3, r15)
                boolean r4 = android.text.TextUtils.isEmpty(r3)
                if (r4 != 0) goto L_0x02d2
                java.lang.String r4 = "sms_hash_code"
                java.lang.String r1 = r1.getString(r4, r15)
                if (r1 == 0) goto L_0x02d2
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r3)
                java.lang.String r3 = "|"
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                boolean r3 = r1.contains(r3)
                if (r3 == 0) goto L_0x02d2
                r3 = 124(0x7c, float:1.74E-43)
                int r3 = r1.indexOf(r3)
                int r3 = r3 + r2
                java.lang.String r1 = r1.substring(r3)
                goto L_0x02d3
            L_0x02d2:
                r1 = r15
            L_0x02d3:
                if (r1 == 0) goto L_0x02de
                org.telegram.ui.CodeFieldContainer r2 = r0.codeFieldContainer
                r2.setCode(r1)
                r0.onNextPressed(r15)
                goto L_0x0326
            L_0x02de:
                r16.createTimer()
                goto L_0x0326
            L_0x02e2:
                if (r1 != r8) goto L_0x0319
                int r1 = r0.nextType
                if (r1 != r5) goto L_0x0319
                android.widget.TextView r1 = r0.timeText
                java.lang.Object[] r3 = new java.lang.Object[r5]
                java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
                r3[r6] = r4
                java.lang.Integer r4 = java.lang.Integer.valueOf(r6)
                r3[r2] = r4
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r11, r3)
                r1.setText(r3)
                int r1 = r0.time
                if (r1 >= r10) goto L_0x0304
                goto L_0x0305
            L_0x0304:
                r2 = 0
            L_0x0305:
                r0.setProblemTextVisible(r2)
                android.widget.TextView r1 = r0.timeText
                int r2 = r0.time
                if (r2 >= r10) goto L_0x0311
                r10 = 8
                goto L_0x0312
            L_0x0311:
                r10 = 0
            L_0x0312:
                r1.setVisibility(r10)
                r16.createTimer()
                goto L_0x0326
            L_0x0319:
                android.widget.TextView r1 = r0.timeText
                r2 = 8
                r1.setVisibility(r2)
                r0.setProblemTextVisible(r6)
                r16.createCodeTimer()
            L_0x0326:
                int r1 = r0.currentType
                r2 = 11
                if (r1 != r2) goto L_0x0381
                java.lang.String r1 = r0.prefix
                r2 = 0
            L_0x032f:
                int r3 = r0.length
                java.lang.String r4 = "0"
                if (r2 >= r3) goto L_0x0347
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r1)
                r3.append(r4)
                java.lang.String r1 = r3.toString()
                int r2 = r2 + 1
                goto L_0x032f
            L_0x0347:
                org.telegram.PhoneFormat.PhoneFormat r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r7)
                r3.append(r1)
                java.lang.String r1 = r3.toString()
                java.lang.String r1 = r2.format(r1)
                r2 = 0
            L_0x035f:
                int r3 = r0.length
                if (r2 >= r3) goto L_0x0370
                int r3 = r1.lastIndexOf(r4)
                if (r3 < 0) goto L_0x036d
                java.lang.String r1 = r1.substring(r6, r3)
            L_0x036d:
                int r2 = r2 + 1
                goto L_0x035f
            L_0x0370:
                java.lang.String r2 = "\\)"
                java.lang.String r1 = r1.replaceAll(r2, r9)
                java.lang.String r2 = "\\("
                java.lang.String r1 = r1.replaceAll(r2, r9)
                android.widget.TextView r2 = r0.prefixTextView
                r2.setText(r1)
            L_0x0381:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.setParams(android.os.Bundle, boolean):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setParams$18(View view, boolean z) {
            if (z) {
                this.this$0.keyboardView.setEditText((EditText) view);
                this.this$0.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* access modifiers changed from: private */
        public void setProblemTextVisible(boolean z) {
            float f = z ? 1.0f : 0.0f;
            if (this.problemText.getAlpha() != f) {
                this.problemText.animate().cancel();
                this.problemText.animate().alpha(f).setDuration(150).start();
            }
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0(this));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$8000 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                        LoginActivitySmsView.access$8126(LoginActivitySmsView.this, currentTimeMillis - access$8000);
                        if (LoginActivitySmsView.this.codeTime <= 1000) {
                            LoginActivitySmsView.this.setProblemTextVisible(true);
                            LoginActivitySmsView.this.timeText.setVisibility(8);
                            LoginActivitySmsView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
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
                FileLog.e((Throwable) e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                this.timeText.setTag(NUM, "windowBackgroundWhiteGrayText6");
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$8600 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTimeMillis);
                        double unused = LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                        LoginActivitySmsView.access$8726(LoginActivitySmsView.this, currentTimeMillis - access$8600);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int access$8700 = (LoginActivitySmsView.this.time / 1000) / 60;
                            int access$87002 = (LoginActivitySmsView.this.time / 1000) - (access$8700 * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 11) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallAvailableIn", NUM, Integer.valueOf(access$8700), Integer.valueOf(access$87002)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsAvailableIn", NUM, Integer.valueOf(access$8700), Integer.valueOf(access$87002)));
                            }
                            ProgressView unused2 = LoginActivitySmsView.this.progressView;
                            return;
                        }
                        LoginActivitySmsView.this.destroyTimer();
                        if (LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2 || LoginActivitySmsView.this.nextType == 11) {
                            if (LoginActivitySmsView.this.nextType == 4) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestCallButton", NUM));
                            } else if (LoginActivitySmsView.this.nextType == 11 || LoginActivitySmsView.this.nextType == 3) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestMissedCall", NUM));
                            } else {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestSmsButton", NUM));
                            }
                            LoginActivitySmsView.this.timeText.setTextColor(Theme.getColor("chats_actionBackground"));
                            LoginActivitySmsView.this.timeText.setTag(NUM, "chats_actionBackground");
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyTimer() {
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.timeText.setTag(NUM, "windowBackgroundWhiteGrayText6");
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void onNextPressed(String str) {
            if (this.this$0.currentViewNum == 11) {
                if (this.nextPressed) {
                    return;
                }
            } else if (this.nextPressed || this.this$0.currentViewNum < 1 || this.this$0.currentViewNum > 4) {
                return;
            }
            if (str == null) {
                str = this.codeFieldContainer.getCode();
            }
            int i = 0;
            if (TextUtils.isEmpty(str)) {
                this.this$0.onFieldError(this.codeFieldContainer, false);
            } else if (this.this$0.currentViewNum < 1 || this.this$0.currentViewNum > 4 || !this.codeFieldContainer.isFocusSuppressed) {
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
                int access$2400 = this.this$0.activityMode;
                if (access$2400 == 1) {
                    this.requestPhone = this.this$0.cancelDeletionPhone;
                    TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone = new TLRPC$TL_account_confirmPhone();
                    tLRPC$TL_account_confirmPhone.phone_code = str;
                    tLRPC$TL_account_confirmPhone.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                    codeFieldContainer2.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr = codeFieldContainer2.codeField;
                    int length2 = codeNumberFieldArr.length;
                    while (i < length2) {
                        codeNumberFieldArr[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    tryShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_account_confirmPhone, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda37(this, tLRPC$TL_account_confirmPhone), 2));
                } else if (access$2400 != 2) {
                    TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn = new TLRPC$TL_auth_signIn();
                    tLRPC$TL_auth_signIn.phone_number = this.requestPhone;
                    tLRPC$TL_auth_signIn.phone_code = str;
                    tLRPC$TL_auth_signIn.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer3 = this.codeFieldContainer;
                    codeFieldContainer3.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr2 = codeFieldContainer3.codeField;
                    int length3 = codeNumberFieldArr2.length;
                    while (i < length3) {
                        codeNumberFieldArr2[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    lambda$tryShowProgress$10(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_signIn, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38(this, tLRPC$TL_auth_signIn), 10), true);
                    this.this$0.showDoneButton(true, true);
                } else {
                    TLRPC$TL_account_changePhone tLRPC$TL_account_changePhone = new TLRPC$TL_account_changePhone();
                    tLRPC$TL_account_changePhone.phone_number = this.requestPhone;
                    tLRPC$TL_account_changePhone.phone_code = str;
                    tLRPC$TL_account_changePhone.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    CodeFieldContainer codeFieldContainer4 = this.codeFieldContainer;
                    codeFieldContainer4.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr3 = codeFieldContainer4.codeField;
                    int length4 = codeNumberFieldArr3.length;
                    while (i < length4) {
                        codeNumberFieldArr3[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    lambda$tryShowProgress$10(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_account_changePhone, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34(this), 2), true);
                    this.this$0.showDoneButton(true, true);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$22(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x0172  */
        /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onNextPressed$21(org.telegram.tgnet.TLRPC$TL_error r8, org.telegram.tgnet.TLObject r9) {
            /*
                r7 = this;
                r0 = 0
                r1 = 1
                r7.tryHideProgress(r0, r1)
                r7.nextPressed = r0
                r2 = 0
                r3 = 3
                if (r8 != 0) goto L_0x007e
                org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC$User) r9
                r7.destroyTimer()
                r7.destroyCodeTimer()
                org.telegram.ui.LoginActivity r8 = r7.this$0
                int r8 = r8.currentAccount
                org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
                r8.setCurrentUser(r9)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                int r8 = r8.currentAccount
                org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
                r8.saveConfig(r1)
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                r8.add(r9)
                org.telegram.ui.LoginActivity r4 = r7.this$0
                int r4 = r4.currentAccount
                org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r4)
                r4.putUsersAndChats(r8, r2, r1, r1)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                int r8 = r8.currentAccount
                org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
                r8.putUser(r9, r0)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                int r8 = r8.currentAccount
                org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)
                int r9 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
                java.lang.Object[] r0 = new java.lang.Object[r0]
                r8.postNotificationName(r9, r0)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                org.telegram.messenger.MessagesController r8 = r8.getMessagesController()
                r0 = 0
                java.lang.String r9 = "VALIDATE_PHONE_NUMBER"
                r8.removeSuggestion(r0, r9)
                int r8 = r7.currentType
                if (r8 != r3) goto L_0x0074
                org.telegram.messenger.AndroidUtilities.endIncomingCall()
            L_0x0074:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda18 r8 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda18
                r8.<init>(r7)
                r7.animateSuccess(r8)
                goto L_0x018b
            L_0x007e:
                java.lang.String r9 = r8.text
                r7.lastError = r9
                r7.nextPressed = r0
                org.telegram.ui.LoginActivity r9 = r7.this$0
                r9.showDoneButton(r0, r1)
                int r9 = r7.currentType
                r4 = 4
                r5 = 2
                if (r9 != r3) goto L_0x0095
                int r6 = r7.nextType
                if (r6 == r4) goto L_0x00a3
                if (r6 == r5) goto L_0x00a3
            L_0x0095:
                if (r9 != r5) goto L_0x009d
                int r6 = r7.nextType
                if (r6 == r4) goto L_0x00a3
                if (r6 == r3) goto L_0x00a3
            L_0x009d:
                if (r9 != r4) goto L_0x00a6
                int r9 = r7.nextType
                if (r9 != r5) goto L_0x00a6
            L_0x00a3:
                r7.createTimer()
            L_0x00a6:
                int r9 = r7.currentType
                if (r9 != r5) goto L_0x00b7
                org.telegram.messenger.AndroidUtilities.setWaitingForSms(r1)
                org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r4 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
                r9.addObserver(r7, r4)
                goto L_0x00c5
            L_0x00b7:
                if (r9 != r3) goto L_0x00c5
                org.telegram.messenger.AndroidUtilities.setWaitingForCall(r1)
                org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r4 = org.telegram.messenger.NotificationCenter.didReceiveCall
                r9.addObserver(r7, r4)
            L_0x00c5:
                r7.waitingForEvent = r1
                int r9 = r7.currentType
                if (r9 == r3) goto L_0x018b
                java.lang.String r9 = r8.text
                java.lang.String r3 = "PHONE_NUMBER_INVALID"
                boolean r9 = r9.contains(r3)
                r3 = 2131628020(0x7f0e0ff4, float:1.888332E38)
                if (r9 == 0) goto L_0x00ec
                org.telegram.ui.LoginActivity r8 = r7.this$0
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r3)
                r1 = 2131626250(0x7f0e090a, float:1.887973E38)
                java.lang.String r2 = "InvalidPhoneNumber"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r8.needShowAlert(r9, r1)
                goto L_0x016b
            L_0x00ec:
                java.lang.String r9 = r8.text
                java.lang.String r4 = "PHONE_CODE_EMPTY"
                boolean r9 = r9.contains(r4)
                if (r9 != 0) goto L_0x016d
                java.lang.String r9 = r8.text
                java.lang.String r4 = "PHONE_CODE_INVALID"
                boolean r9 = r9.contains(r4)
                if (r9 == 0) goto L_0x0101
                goto L_0x016d
            L_0x0101:
                java.lang.String r9 = r8.text
                java.lang.String r4 = "PHONE_CODE_EXPIRED"
                boolean r9 = r9.contains(r4)
                if (r9 == 0) goto L_0x0126
                r7.onBackPressed(r1)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                r8.setPage(r0, r1, r2, r1)
                org.telegram.ui.LoginActivity r8 = r7.this$0
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r3)
                r1 = 2131625171(0x7f0e04d3, float:1.8877542E38)
                java.lang.String r2 = "CodeExpired"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r8.needShowAlert(r9, r1)
                goto L_0x016b
            L_0x0126:
                java.lang.String r9 = r8.text
                java.lang.String r1 = "FLOOD_WAIT"
                boolean r9 = r9.startsWith(r1)
                if (r9 == 0) goto L_0x0143
                org.telegram.ui.LoginActivity r8 = r7.this$0
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r3)
                r1 = 2131625908(0x7f0e07b4, float:1.8879037E38)
                java.lang.String r2 = "FloodWait"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r8.needShowAlert(r9, r1)
                goto L_0x016b
            L_0x0143:
                org.telegram.ui.LoginActivity r9 = r7.this$0
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r3)
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r3 = 2131625657(0x7f0e06b9, float:1.8878528E38)
                java.lang.String r4 = "ErrorOccurred"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.append(r3)
                java.lang.String r3 = "\n"
                r2.append(r3)
                java.lang.String r8 = r8.text
                r2.append(r8)
                java.lang.String r8 = r2.toString()
                r9.needShowAlert(r1, r8)
            L_0x016b:
                r1 = 0
                goto L_0x0170
            L_0x016d:
                r7.shakeWrongCode()
            L_0x0170:
                if (r1 != 0) goto L_0x018b
                r8 = 0
            L_0x0173:
                org.telegram.ui.CodeFieldContainer r9 = r7.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r1 = r9.codeField
                int r2 = r1.length
                if (r8 >= r2) goto L_0x0184
                r9 = r1[r8]
                java.lang.String r1 = ""
                r9.setText(r1)
                int r8 = r8 + 1
                goto L_0x0173
            L_0x0184:
                r9.isFocusSuppressed = r0
                r8 = r1[r0]
                r8.requestFocus()
            L_0x018b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.lambda$onNextPressed$21(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$20() {
            try {
                this.this$0.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(this)).show();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(DialogInterface dialogInterface) {
            this.this$0.finishFragment();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$26(TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33(this, tLRPC$TL_error, tLRPC$TL_account_confirmPhone));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$25(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone) {
            int i;
            int i2;
            tryHideProgress(false);
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10(this));
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
                AlertsCreator.processError(this.this$0.currentAccount, tLRPC$TL_error, this.this$0, tLRPC$TL_account_confirmPhone, new Object[0]);
            }
            if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                shakeWrongCode();
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                this.this$0.setPage(0, true, (Bundle) null, true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$24() {
            AlertDialog.Builder title = new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString(NUM));
            PhoneFormat instance = PhoneFormat.getInstance();
            title.setMessage(LocaleController.formatString("CancelLinkSuccess", NUM, instance.format("+" + this.phone))).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this)).show();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$23(DialogInterface dialogInterface) {
            this.this$0.finishFragment();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$33(TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32(this, tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x016f  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x018e  */
        /* JADX WARNING: Removed duplicated region for block: B:62:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onNextPressed$32(org.telegram.tgnet.TLRPC$TL_error r6, org.telegram.tgnet.TLObject r7, org.telegram.tgnet.TLRPC$TL_auth_signIn r8) {
            /*
                r5 = this;
                r0 = 0
                r1 = 1
                r5.tryHideProgress(r0, r1)
                r2 = 3
                if (r6 != 0) goto L_0x0050
                r5.nextPressed = r0
                org.telegram.ui.LoginActivity r6 = r5.this$0
                r6.showDoneButton(r0, r1)
                r5.destroyTimer()
                r5.destroyCodeTimer()
                boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired
                if (r6 == 0) goto L_0x0047
                org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired r7 = (org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired) r7
                org.telegram.tgnet.TLRPC$TL_help_termsOfService r6 = r7.terms_of_service
                if (r6 == 0) goto L_0x0024
                org.telegram.ui.LoginActivity r7 = r5.this$0
                org.telegram.tgnet.TLRPC$TL_help_termsOfService unused = r7.currentTermsOfService = r6
            L_0x0024:
                android.os.Bundle r6 = new android.os.Bundle
                r6.<init>()
                java.lang.String r7 = r5.requestPhone
                java.lang.String r0 = "phoneFormated"
                r6.putString(r0, r7)
                java.lang.String r7 = r5.phoneHash
                java.lang.String r0 = "phoneHash"
                r6.putString(r0, r7)
                java.lang.String r7 = r8.phone_code
                java.lang.String r8 = "code"
                r6.putString(r8, r7)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23 r7 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23
                r7.<init>(r5, r6)
                r5.animateSuccess(r7)
                goto L_0x007b
            L_0x0047:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda27 r6 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda27
                r6.<init>(r5, r7)
                r5.animateSuccess(r6)
                goto L_0x007b
            L_0x0050:
                java.lang.String r7 = r6.text
                r5.lastError = r7
                java.lang.String r3 = "SESSION_PASSWORD_NEEDED"
                boolean r7 = r7.contains(r3)
                if (r7 == 0) goto L_0x007e
                org.telegram.tgnet.TLRPC$TL_account_getPassword r6 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
                r6.<init>()
                org.telegram.ui.LoginActivity r7 = r5.this$0
                int r7 = r7.currentAccount
                org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda39 r0 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda39
                r0.<init>(r5, r8)
                r8 = 10
                r7.sendRequest(r6, r0, r8)
                r5.destroyTimer()
                r5.destroyCodeTimer()
            L_0x007b:
                r0 = 1
                goto L_0x0188
            L_0x007e:
                r5.nextPressed = r0
                org.telegram.ui.LoginActivity r7 = r5.this$0
                r7.showDoneButton(r0, r1)
                int r7 = r5.currentType
                r8 = 4
                r3 = 2
                if (r7 != r2) goto L_0x0091
                int r4 = r5.nextType
                if (r4 == r8) goto L_0x009f
                if (r4 == r3) goto L_0x009f
            L_0x0091:
                if (r7 != r3) goto L_0x0099
                int r4 = r5.nextType
                if (r4 == r8) goto L_0x009f
                if (r4 == r2) goto L_0x009f
            L_0x0099:
                if (r7 != r8) goto L_0x00a2
                int r7 = r5.nextType
                if (r7 != r3) goto L_0x00a2
            L_0x009f:
                r5.createTimer()
            L_0x00a2:
                int r7 = r5.currentType
                if (r7 != r3) goto L_0x00b3
                org.telegram.messenger.AndroidUtilities.setWaitingForSms(r1)
                org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r8 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
                r7.addObserver(r5, r8)
                goto L_0x00c1
            L_0x00b3:
                if (r7 != r2) goto L_0x00c1
                org.telegram.messenger.AndroidUtilities.setWaitingForCall(r1)
                org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r8 = org.telegram.messenger.NotificationCenter.didReceiveCall
                r7.addObserver(r5, r8)
            L_0x00c1:
                r5.waitingForEvent = r1
                int r7 = r5.currentType
                if (r7 == r2) goto L_0x0188
                java.lang.String r7 = r6.text
                java.lang.String r8 = "PHONE_NUMBER_INVALID"
                boolean r7 = r7.contains(r8)
                r8 = 2131628020(0x7f0e0ff4, float:1.888332E38)
                if (r7 == 0) goto L_0x00e8
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r8)
                r8 = 2131626250(0x7f0e090a, float:1.887973E38)
                java.lang.String r1 = "InvalidPhoneNumber"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r1, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x0168
            L_0x00e8:
                java.lang.String r7 = r6.text
                java.lang.String r3 = "PHONE_CODE_EMPTY"
                boolean r7 = r7.contains(r3)
                if (r7 != 0) goto L_0x016a
                java.lang.String r7 = r6.text
                java.lang.String r3 = "PHONE_CODE_INVALID"
                boolean r7 = r7.contains(r3)
                if (r7 == 0) goto L_0x00fd
                goto L_0x016a
            L_0x00fd:
                java.lang.String r7 = r6.text
                java.lang.String r3 = "PHONE_CODE_EXPIRED"
                boolean r7 = r7.contains(r3)
                if (r7 == 0) goto L_0x0123
                r5.onBackPressed(r1)
                org.telegram.ui.LoginActivity r6 = r5.this$0
                r7 = 0
                r6.setPage(r0, r1, r7, r1)
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r8)
                r8 = 2131625171(0x7f0e04d3, float:1.8877542E38)
                java.lang.String r1 = "CodeExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r1, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x0168
            L_0x0123:
                java.lang.String r7 = r6.text
                java.lang.String r1 = "FLOOD_WAIT"
                boolean r7 = r7.startsWith(r1)
                if (r7 == 0) goto L_0x0140
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r8)
                r8 = 2131625908(0x7f0e07b4, float:1.8879037E38)
                java.lang.String r1 = "FloodWait"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r1, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x0168
            L_0x0140:
                org.telegram.ui.LoginActivity r7 = r5.this$0
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString((int) r8)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r3 = 2131625657(0x7f0e06b9, float:1.8878528E38)
                java.lang.String r4 = "ErrorOccurred"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.append(r3)
                java.lang.String r3 = "\n"
                r1.append(r3)
                java.lang.String r6 = r6.text
                r1.append(r6)
                java.lang.String r6 = r1.toString()
                r7.needShowAlert(r8, r6)
            L_0x0168:
                r1 = 0
                goto L_0x016d
            L_0x016a:
                r5.shakeWrongCode()
            L_0x016d:
                if (r1 != 0) goto L_0x0188
                r6 = 0
            L_0x0170:
                org.telegram.ui.CodeFieldContainer r7 = r5.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r8 = r7.codeField
                int r1 = r8.length
                if (r6 >= r1) goto L_0x0181
                r7 = r8[r6]
                java.lang.String r8 = ""
                r7.setText(r8)
                int r6 = r6 + 1
                goto L_0x0170
            L_0x0181:
                r7.isFocusSuppressed = r0
                r6 = r8[r0]
                r6.requestFocus()
            L_0x0188:
                if (r0 == 0) goto L_0x0191
                int r6 = r5.currentType
                if (r6 != r2) goto L_0x0191
                org.telegram.messenger.AndroidUtilities.endIncomingCall()
            L_0x0191:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.lambda$onNextPressed$32(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_auth_signIn):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$27(Bundle bundle) {
            this.this$0.setPage(5, true, bundle, false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$28(TLObject tLObject) {
            this.this$0.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$31(TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda31(this, tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$30(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn) {
            this.nextPressed = false;
            this.this$0.showDoneButton(false, true);
            if (tLRPC$TL_error == null) {
                TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, true)) {
                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                    return;
                }
                Bundle bundle = new Bundle();
                SerializedData serializedData = new SerializedData(tLRPC$TL_account_password.getObjectSize());
                tLRPC$TL_account_password.serializeToStream(serializedData);
                bundle.putString("password", Utilities.bytesToHex(serializedData.toByteArray()));
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", tLRPC$TL_auth_signIn.phone_code);
                animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda24(this, bundle));
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$29(Bundle bundle) {
            this.this$0.setPage(6, true, bundle, false);
        }

        private void animateSuccess(Runnable runnable) {
            int i = 0;
            while (true) {
                CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                if (i < codeFieldContainer2.codeField.length) {
                    codeFieldContainer2.postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda20(this, i), ((long) i) * 75);
                    i++;
                } else {
                    codeFieldContainer2.postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda26(this, runnable), (((long) this.codeFieldContainer.codeField.length) * 75) + 400);
                    return;
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$animateSuccess$34(int i) {
            this.codeFieldContainer.codeField[i].animateSuccessProgress(1.0f);
        }

        /* access modifiers changed from: private */
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
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, this.currentType == 11 ? 3.5f : 10.0f, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda19(this));
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 5000);
            this.postedErrorColorTimeout = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$37() {
            postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda13(this), 150);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeWrongCode$36() {
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer2.isFocusSuppressed = false;
            codeFieldContainer2.codeField[0].requestFocus();
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

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        public boolean onBackPressed(boolean z) {
            if (this.this$0.activityMode != 0) {
                this.this$0.finishFragment();
                return false;
            } else if (!z) {
                LoginActivity loginActivity = this.this$0;
                loginActivity.showDialog(new AlertDialog.Builder((Context) loginActivity.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditNumberInfo", NUM, this.phone))).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1(this)).create());
                return false;
            } else {
                this.nextPressed = false;
                tryHideProgress(true);
                TLRPC$TL_auth_cancelCode tLRPC$TL_auth_cancelCode = new TLRPC$TL_auth_cancelCode();
                tLRPC$TL_auth_cancelCode.phone_number = this.requestPhone;
                tLRPC$TL_auth_cancelCode.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_cancelCode, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda40.INSTANCE, 10);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$38(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

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

        public void onShow() {
            super.onShow();
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCurrentFrame(0);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda12(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$40() {
            CodeNumberField[] codeNumberFieldArr;
            if (this.currentType != 3 && (codeNumberFieldArr = this.codeFieldContainer.codeField) != null) {
                int length2 = codeNumberFieldArr.length - 1;
                while (true) {
                    if (length2 < 0) {
                        break;
                    } else if (length2 == 0 || this.codeFieldContainer.codeField[length2].length() != 0) {
                        this.codeFieldContainer.codeField[length2].requestFocus();
                        CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                        codeNumberFieldArr2[length2].setSelection(codeNumberFieldArr2[length2].length());
                        boolean unused = this.this$0.showKeyboard(this.codeFieldContainer.codeField[length2]);
                    } else {
                        length2--;
                    }
                }
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (this.waitingForEvent) {
                CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                if (codeFieldContainer2.codeField != null) {
                    if (i == NotificationCenter.didReceiveSmsCode) {
                        codeFieldContainer2.setText("" + objArr[0]);
                        onNextPressed((String) null);
                    } else if (i == NotificationCenter.didReceiveCall) {
                        String str = "" + objArr[0];
                        if (AndroidUtilities.checkPhonePattern(this.pattern, str)) {
                            if (!this.pattern.equals("*")) {
                                this.catchedPhone = str;
                                AndroidUtilities.endIncomingCall();
                            }
                            onNextPressed(str);
                        }
                    }
                }
            }
        }

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
                CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                if (codeFieldContainer2.codeField != null) {
                    codeFieldContainer2.setText(string2);
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

    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public TLRPC$TL_account_password currentPassword;
        private RLottieImageView lockImageView;
        private boolean nextPressed;
        private OutlineTextContainerView outlineCodeField;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleView;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* JADX WARNING: Removed duplicated region for block: B:10:0x0146  */
        /* JADX WARNING: Removed duplicated region for block: B:11:0x0148  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x01ce  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x01d1  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityPasswordView(org.telegram.ui.LoginActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                r1 = 1
                r0.setOrientation(r1)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                org.telegram.ui.Components.RLottieImageView r4 = new org.telegram.ui.Components.RLottieImageView
                r4.<init>(r2)
                r0.lockImageView = r4
                r5 = 2131558558(0x7f0d009e, float:1.8742435E38)
                r6 = 120(0x78, float:1.68E-43)
                r4.setAnimation(r5, r6, r6)
                org.telegram.ui.Components.RLottieImageView r4 = r0.lockImageView
                r5 = 0
                r4.setAutoRepeat(r5)
                org.telegram.ui.Components.RLottieImageView r4 = r0.lockImageView
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r1)
                r3.addView(r4, r6)
                boolean r4 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r4 != 0) goto L_0x0049
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r6 = r4.x
                int r4 = r4.y
                if (r6 <= r4) goto L_0x0047
                boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r4 != 0) goto L_0x0047
                goto L_0x0049
            L_0x0047:
                r4 = 0
                goto L_0x004b
            L_0x0049:
                r4 = 8
            L_0x004b:
                r3.setVisibility(r4)
                r4 = -1
                r6 = -2
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r1)
                r0.addView(r3, r7)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.titleView = r3
                r7 = 1099956224(0x41900000, float:18.0)
                r3.setTextSize(r1, r7)
                android.widget.TextView r3 = r0.titleView
                java.lang.String r8 = "fonts/rmedium.ttf"
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
                r3.setTypeface(r8)
                android.widget.TextView r3 = r0.titleView
                r8 = 2131629295(0x7f0e14ef, float:1.8885907E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString((int) r8)
                r3.setText(r8)
                android.widget.TextView r3 = r0.titleView
                r8 = 17
                r3.setGravity(r8)
                android.widget.TextView r3 = r0.titleView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r3.setLineSpacing(r9, r10)
                android.widget.TextView r3 = r0.titleView
                r11 = -1
                r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r13 = 1
                r14 = 1107296256(0x42000000, float:32.0)
                r15 = 1098907648(0x41800000, float:16.0)
                r16 = 1107296256(0x42000000, float:32.0)
                r17 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r3, r9)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.confirmTextView = r3
                r9 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r1, r9)
                android.widget.TextView r3 = r0.confirmTextView
                r3.setGravity(r1)
                android.widget.TextView r3 = r0.confirmTextView
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r3.setLineSpacing(r9, r10)
                android.widget.TextView r3 = r0.confirmTextView
                r9 = 2131626506(0x7f0e0a0a, float:1.888025E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r3.setText(r9)
                android.widget.TextView r3 = r0.confirmTextView
                r11 = -2
                r12 = -2
                r14 = 12
                r15 = 8
                r16 = 12
                r17 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r3, r9)
                org.telegram.ui.Components.OutlineTextContainerView r3 = new org.telegram.ui.Components.OutlineTextContainerView
                r3.<init>(r2)
                r0.outlineCodeField = r3
                r9 = 2131625651(0x7f0e06b3, float:1.8878516E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r3.setText(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
                r3.<init>(r2)
                r0.codeField = r3
                r9 = 1101004800(0x41a00000, float:20.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r3.setCursorSize(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                r3.setCursorWidth(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 0
                r3.setBackground(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 268435461(0x10000005, float:2.5243564E-29)
                r3.setImeOptions(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setTextSize(r1, r7)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setMaxLines(r1)
                r3 = 1098907648(0x41800000, float:16.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
                org.telegram.ui.Components.EditTextBoldCursor r9 = r0.codeField
                r9.setPadding(r7, r7, r7, r7)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                r9 = 129(0x81, float:1.81E-43)
                r7.setInputType(r9)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                android.text.method.PasswordTransformationMethod r9 = android.text.method.PasswordTransformationMethod.getInstance()
                r7.setTransformationMethod(r9)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
                r7.setTypeface(r9)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0148
                r9 = 5
                goto L_0x0149
            L_0x0148:
                r9 = 3
            L_0x0149:
                r7.setGravity(r9)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3 r9 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3
                r9.<init>(r0)
                r7.setOnFocusChangeListener(r9)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r0.outlineCodeField
                org.telegram.ui.Components.EditTextBoldCursor r9 = r0.codeField
                r7.attachEditText(r9)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r0.outlineCodeField
                org.telegram.ui.Components.EditTextBoldCursor r9 = r0.codeField
                r11 = 48
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r11)
                r7.addView(r9, r6)
                org.telegram.ui.Components.EditTextBoldCursor r6 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4 r7 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4
                r7.<init>(r0)
                r6.setOnEditorActionListener(r7)
                org.telegram.ui.Components.OutlineTextContainerView r6 = r0.outlineCodeField
                r11 = -1
                r12 = -2
                r13 = 1
                r14 = 16
                r15 = 32
                r16 = 16
                r17 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r6, r7)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r0.cancelButton = r6
                r7 = 19
                r6.setGravity(r7)
                android.widget.TextView r6 = r0.cancelButton
                r7 = 2131625939(0x7f0e07d3, float:1.88791E38)
                java.lang.String r9 = "ForgotPassword"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
                r6.setText(r7)
                android.widget.TextView r6 = r0.cancelButton
                r7 = 1097859072(0x41700000, float:15.0)
                r6.setTextSize(r1, r7)
                android.widget.TextView r1 = r0.cancelButton
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r6 = (float) r6
                r1.setLineSpacing(r6, r10)
                android.widget.TextView r1 = r0.cancelButton
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r6, r5, r3, r5)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                android.widget.TextView r3 = r0.cancelButton
                r5 = -1
                int r6 = android.os.Build.VERSION.SDK_INT
                r7 = 21
                if (r6 < r7) goto L_0x01d1
                r6 = 56
                goto L_0x01d3
            L_0x01d1:
                r6 = 60
            L_0x01d3:
                float r6 = (float) r6
                r7 = 80
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                r1.addView(r3, r5)
                r3 = 80
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r4, (int) r3)
                r0.addView(r1, r3)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r1)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2
                r3.<init>(r0, r2)
                r1.setOnClickListener(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view, boolean z) {
            this.outlineCodeField.animateSelection(z ? 1.0f : 0.0f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(Context context, View view) {
            if (this.this$0.radialProgressView.getTag() == null) {
                if (this.currentPassword.has_recovery) {
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12(this), 10);
                    return;
                }
                AndroidUtilities.hideKeyboard(this.codeField);
                new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0(this)).show();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            String str;
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery = (TLRPC$TL_auth_passwordRecovery) tLObject;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                String str2 = tLRPC$TL_auth_passwordRecovery.email_pattern;
                SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str2);
                int indexOf = str2.indexOf(42);
                int lastIndexOf = str2.lastIndexOf(42);
                if (!(indexOf == lastIndexOf || indexOf == -1 || lastIndexOf == -1)) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i = lastIndexOf + 1;
                    textStyleRun.end = i;
                    valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i, 0);
                }
                builder.setMessage(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), valueOf));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
                builder.setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1(this, tLRPC$TL_auth_passwordRecovery));
                Dialog showDialog = this.this$0.showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                } else {
                    str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                }
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", tLRPC$TL_auth_passwordRecovery.email_pattern);
            bundle.putString("password", this.passwordString);
            bundle.putString("requestPhone", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("phoneCode", this.phoneCode);
            this.this$0.setPage(7, true, bundle, false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(DialogInterface dialogInterface, int i) {
            this.this$0.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.outlineCodeField.updateColor();
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
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
                    this.currentPassword = TLRPC$TL_account_password.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                }
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                if (tLRPC$TL_account_password == null || TextUtils.isEmpty(tLRPC$TL_account_password.hint)) {
                    this.codeField.setHint((CharSequence) null);
                } else {
                    this.codeField.setHint(this.currentPassword.hint);
                }
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                if (z) {
                    this.codeField.setText("");
                }
                this.this$0.onFieldError(this.outlineCodeField, true);
            }
        }

        public void onNextPressed(String str) {
            if (!this.nextPressed) {
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6(this, obj));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$12(String str) {
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
            boolean z = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
            byte[] x = z ? SRPHelper.getX(AndroidUtilities.getStringBytes(str), (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
            TLRPC$TL_auth_checkPassword tLRPC$TL_auth_checkPassword = new TLRPC$TL_auth_checkPassword();
            LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13 loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13 = new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13(this);
            if (z) {
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                tLRPC$TL_auth_checkPassword.password = startCheck;
                if (startCheck == null) {
                    TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                    tLRPC$TL_error.text = "PASSWORD_HASH_INVALID";
                    loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13.run((TLObject) null, tLRPC$TL_error);
                    return;
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_checkPassword, loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13, 10);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$10(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            String str;
            this.nextPressed = false;
            if (tLRPC$TL_error != null && "SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11(this), 8);
            } else if (tLObject instanceof TLRPC$TL_auth_authorization) {
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7(this, tLObject), 150);
            } else {
                this.this$0.needHideProgress(false);
                if (tLRPC$TL_error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                    } else {
                        str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                    }
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
                } else {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            if (tLRPC$TL_error == null) {
                this.currentPassword = (TLRPC$TL_account_password) tLObject;
                onNextPressed((String) null);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$9(TLObject tLObject) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.codeField);
            this.this$0.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        public boolean onBackPressed(boolean z) {
            this.nextPressed = false;
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$13() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                boolean unused = this.this$0.showKeyboard(this.codeField);
                this.lockImageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.lockImageView.playAnimation();
            }
        }

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
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public Runnable timeRunnable;
        private TextView titleView;
        private RLottieImageView waitImageView;
        private int waitTime;
        private Boolean wasResetButtonActive;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityResetWaitView(org.telegram.ui.LoginActivity r21, android.content.Context r22) {
            /*
                r20 = this;
                r0 = r20
                r1 = r21
                r2 = r22
                r0.this$0 = r1
                r0.<init>(r2)
                r1 = 1
                r0.setOrientation(r1)
                android.widget.LinearLayout r3 = new android.widget.LinearLayout
                r3.<init>(r2)
                r3.setOrientation(r1)
                r4 = 17
                r3.setGravity(r4)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
                r6.<init>(r2)
                r0.waitImageView = r6
                r6.setAutoRepeat(r1)
                org.telegram.ui.Components.RLottieImageView r6 = r0.waitImageView
                r7 = 2131558525(0x7f0d007d, float:1.8742368E38)
                r8 = 120(0x78, float:1.68E-43)
                r6.setAnimation(r7, r8, r8)
                org.telegram.ui.Components.RLottieImageView r6 = r0.waitImageView
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r1)
                r5.addView(r6, r7)
                android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r6.x
                int r6 = r6.y
                r8 = 0
                if (r7 <= r6) goto L_0x0050
                boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r6 != 0) goto L_0x0050
                r6 = 8
                goto L_0x0051
            L_0x0050:
                r6 = 0
            L_0x0051:
                r5.setVisibility(r6)
                r6 = -2
                r7 = -1
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r6, (int) r1)
                r3.addView(r5, r6)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleView = r5
                r6 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r1, r6)
                android.widget.TextView r5 = r0.titleView
                java.lang.String r6 = "fonts/rmedium.ttf"
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r5.setTypeface(r9)
                android.widget.TextView r5 = r0.titleView
                r9 = 2131627981(0x7f0e0fcd, float:1.8883242E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r9)
                r5.setText(r10)
                android.widget.TextView r5 = r0.titleView
                r5.setGravity(r4)
                android.widget.TextView r5 = r0.titleView
                r10 = 1073741824(0x40000000, float:2.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r11
                r12 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r11, r12)
                android.widget.TextView r5 = r0.titleView
                r13 = -1
                r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r15 = 1
                r16 = 1107296256(0x42000000, float:32.0)
                r17 = 1098907648(0x41800000, float:16.0)
                r18 = 1107296256(0x42000000, float:32.0)
                r19 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r3.addView(r5, r11)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r1, r11)
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r1)
                android.widget.TextView r5 = r0.confirmTextView
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r13 = (float) r13
                r5.setLineSpacing(r13, r12)
                android.widget.TextView r5 = r0.confirmTextView
                r13 = -2
                r14 = -2
                r16 = 12
                r17 = 8
                r18 = 12
                r19 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r3.addView(r5, r13)
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (float) r12)
                r0.addView(r3, r5)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountText = r3
                r3.setGravity(r1)
                android.widget.TextView r3 = r0.resetAccountText
                r5 = 2131627985(0x7f0e0fd1, float:1.888325E38)
                java.lang.String r13 = "ResetAccountStatus"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
                r3.setText(r5)
                android.widget.TextView r3 = r0.resetAccountText
                r3.setTextSize(r1, r11)
                android.widget.TextView r3 = r0.resetAccountText
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r5 = (float) r5
                r3.setLineSpacing(r5, r12)
                android.widget.TextView r3 = r0.resetAccountText
                r13 = -2
                r15 = 49
                r16 = 0
                r17 = 24
                r18 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r3, r5)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountTime = r3
                r3.setGravity(r1)
                android.widget.TextView r3 = r0.resetAccountTime
                r5 = 1101004800(0x41a00000, float:20.0)
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.resetAccountTime
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r3.setTypeface(r5)
                android.widget.TextView r3 = r0.resetAccountTime
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r5 = (float) r5
                r3.setLineSpacing(r5, r12)
                android.widget.TextView r3 = r0.resetAccountTime
                r15 = 1
                r17 = 8
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r3, r5)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountButton = r3
                r3.setGravity(r4)
                android.widget.TextView r2 = r0.resetAccountButton
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString((int) r9)
                r2.setText(r3)
                android.widget.TextView r2 = r0.resetAccountButton
                android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r2.setTypeface(r3)
                android.widget.TextView r2 = r0.resetAccountButton
                r3 = 1097859072(0x41700000, float:15.0)
                r2.setTextSize(r1, r3)
                android.widget.TextView r1 = r0.resetAccountButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r12)
                android.widget.TextView r1 = r0.resetAccountButton
                r2 = 1107820544(0x42080000, float:34.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r1.setPadding(r3, r8, r2, r8)
                android.widget.TextView r1 = r0.resetAccountButton
                r1.setTextColor(r7)
                android.widget.TextView r1 = r0.resetAccountButton
                r2 = -1
                r3 = 50
                r4 = 1
                r5 = 16
                r6 = 32
                r7 = 16
                r8 = 48
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.resetAccountButton
                org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityResetWaitView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            if (this.this$0.radialProgressView.getTag() == null) {
                LoginActivity loginActivity = this.this$0;
                loginActivity.showDialog(new AlertDialog.Builder((Context) loginActivity.getParentActivity()).setTitle(LocaleController.getString("ResetMyAccountWarning", NUM)).setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM)).setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).create());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
            tLRPC$TL_account_deleteAccount.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3(this), 10);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2(this, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(TLRPC$TL_error tLRPC$TL_error) {
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                if (this.requestPhone == null || this.phoneHash == null || this.phoneCode == null) {
                    this.this$0.setPage(0, true, (Bundle) null, true);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, bundle, false);
            } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
            }
        }

        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountTime.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.resetAccountButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2"), Theme.getColor("chats_actionPressedBackground")));
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", NUM);
        }

        /* access modifiers changed from: private */
        public void updateTimeText() {
            int i = 0;
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int i2 = max / 86400;
            int round = Math.round(((float) max) / 86400.0f);
            int i3 = max / 3600;
            int i4 = (max / 60) % 60;
            int i5 = max % 60;
            if (i2 >= 2) {
                this.resetAccountTime.setText(LocaleController.formatPluralString("Days", round, new Object[0]));
            } else {
                this.resetAccountTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)}));
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

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.currentParams = bundle;
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.startTime = bundle.getInt("startTime");
                this.waitTime = bundle.getInt("waitTime");
                TextView textView = this.confirmTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", NUM, LocaleController.addNbsp(instance.format("+" + this.requestPhone)))));
                updateTimeText();
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        if (LoginActivityResetWaitView.this.timeRunnable == this) {
                            LoginActivityResetWaitView.this.updateTimeText();
                            AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                        }
                    }
                };
                this.timeRunnable = r6;
                AndroidUtilities.runOnUIThread(r6, 1000);
            }
        }

        public boolean onBackPressed(boolean z) {
            this.this$0.needHideProgress(true);
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
            return true;
        }

        public void saveStateParams(Bundle bundle) {
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("resetview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("resetview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    public class LoginActivityRecoverView extends SlideView {
        private CodeFieldContainer codeFieldContainer;
        private TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public Runnable errorColorTimeout = new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda6(this);
        private RLottieImageView inboxImageView;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public String passwordString;
        private String phoneCode;
        private String phoneHash;
        /* access modifiers changed from: private */
        public boolean postedErrorColorTimeout;
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleView;
        private TextView troubleButton;

        public boolean hasCustomKeyboard() {
            return true;
        }

        public boolean needBackButton() {
            return true;
        }

        /* access modifiers changed from: private */
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* JADX WARNING: Removed duplicated region for block: B:10:0x00f8  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRecoverView(org.telegram.ui.LoginActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda6
                r3.<init>(r0)
                r0.errorColorTimeout = r3
                r3 = 1
                r0.setOrientation(r3)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
                r5.<init>(r2)
                r0.inboxImageView = r5
                r6 = 2131558559(0x7f0d009f, float:1.8742437E38)
                r7 = 120(0x78, float:1.68E-43)
                r5.setAnimation(r6, r7, r7)
                org.telegram.ui.Components.RLottieImageView r5 = r0.inboxImageView
                r6 = 0
                r5.setAutoRepeat(r6)
                org.telegram.ui.Components.RLottieImageView r5 = r0.inboxImageView
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r3)
                r4.addView(r5, r7)
                boolean r5 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r5 != 0) goto L_0x0050
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r5.x
                int r5 = r5.y
                if (r7 <= r5) goto L_0x004e
                boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r5 != 0) goto L_0x004e
                goto L_0x0050
            L_0x004e:
                r5 = 0
                goto L_0x0052
            L_0x0050:
                r5 = 8
            L_0x0052:
                r4.setVisibility(r5)
                r5 = -2
                r7 = -1
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r3)
                r0.addView(r4, r5)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.titleView = r4
                r5 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.titleView
                java.lang.String r5 = "fonts/rmedium.ttf"
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                r4.setTypeface(r5)
                android.widget.TextView r4 = r0.titleView
                r5 = 2131625645(0x7f0e06ad, float:1.8878504E38)
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
                r4.setText(r5)
                android.widget.TextView r4 = r0.titleView
                r5 = 17
                r4.setGravity(r5)
                android.widget.TextView r4 = r0.titleView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r9, r10)
                android.widget.TextView r4 = r0.titleView
                r11 = -1
                r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r13 = 1
                r14 = 1107296256(0x42000000, float:32.0)
                r15 = 1098907648(0x41800000, float:16.0)
                r16 = 1107296256(0x42000000, float:32.0)
                r17 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r4, r9)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.confirmTextView = r4
                r9 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r9)
                android.widget.TextView r4 = r0.confirmTextView
                r4.setGravity(r5)
                android.widget.TextView r4 = r0.confirmTextView
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r4.setLineSpacing(r11, r10)
                android.widget.TextView r4 = r0.confirmTextView
                r11 = 2131628012(0x7f0e0fec, float:1.8883305E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r11)
                r4.setText(r11)
                android.widget.TextView r4 = r0.confirmTextView
                r11 = -2
                r12 = -2
                r14 = 12
                r15 = 8
                r16 = 12
                r17 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r4, r11)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$1 r4 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$1
                r4.<init>(r2, r1)
                r0.codeFieldContainer = r4
                r11 = 6
                r4.setNumbersCount(r11, r3)
                org.telegram.ui.CodeFieldContainer r4 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r4 = r4.codeField
                int r11 = r4.length
                r12 = 0
            L_0x00f6:
                if (r12 >= r11) goto L_0x0120
                r13 = r4[r12]
                boolean r14 = r18.hasCustomKeyboard()
                if (r14 == 0) goto L_0x0109
                boolean r14 = r19.isCustomKeyboardForceDisabled()
                if (r14 == 0) goto L_0x0107
                goto L_0x0109
            L_0x0107:
                r14 = 0
                goto L_0x010a
            L_0x0109:
                r14 = 1
            L_0x010a:
                r13.setShowSoftInputOnFocusCompat(r14)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$2 r14 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$2
                r14.<init>(r1)
                r13.addTextChangedListener(r14)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3 r14 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3
                r14.<init>(r0)
                r13.setOnFocusChangeListener(r14)
                int r12 = r12 + 1
                goto L_0x00f6
            L_0x0120:
                org.telegram.ui.CodeFieldContainer r1 = r0.codeFieldContainer
                r11 = -2
                r12 = 42
                r13 = 1
                r14 = 0
                r15 = 32
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r1, r4)
                org.telegram.ui.Components.spoilers.SpoilersTextView r1 = new org.telegram.ui.Components.spoilers.SpoilersTextView
                r1.<init>(r2)
                r0.troubleButton = r1
                r1.setGravity(r5)
                android.widget.TextView r1 = r0.troubleButton
                r1.setTextSize(r3, r9)
                android.widget.TextView r1 = r0.troubleButton
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r3 = (float) r3
                r1.setLineSpacing(r3, r10)
                android.widget.TextView r1 = r0.troubleButton
                r3 = 1098907648(0x41800000, float:16.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r4, r5, r8, r3)
                android.widget.TextView r1 = r0.troubleButton
                r3 = 2
                r1.setMaxLines(r3)
                android.widget.TextView r1 = r0.troubleButton
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2
                r3.<init>(r0)
                r1.setOnClickListener(r3)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                android.widget.TextView r2 = r0.troubleButton
                r11 = -1
                r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r13 = 80
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r1.addView(r2, r3)
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r6, (float) r10)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.troubleButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRecoverView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view, boolean z) {
            if (z) {
                this.this$0.keyboardView.setEditText((EditText) view);
                this.this$0.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            Dialog showDialog = this.this$0.showDialog(new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM)).setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM)).setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1(this)).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0(this)).create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(DialogInterface dialogInterface, int i) {
            this.this$0.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.troubleButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.codeFieldContainer.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
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
                if (!(indexOf == lastIndexOf || indexOf == -1 || lastIndexOf == -1)) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i = lastIndexOf + 1;
                    textStyleRun.end = i;
                    valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i, 0);
                }
                this.troubleButton.setText(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), valueOf));
                boolean unused = this.this$0.showKeyboard(this.codeFieldContainer);
                this.codeFieldContainer.requestFocus();
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                try {
                    this.codeFieldContainer.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                if (z) {
                    for (CodeNumberField text : this.codeFieldContainer.codeField) {
                        text.setText("");
                    }
                }
                for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                    animateErrorProgress.animateErrorProgress(1.0f);
                }
                this.codeFieldContainer.codeField[0].requestFocus();
                AndroidUtilities.shakeViewSpring((View) this.codeFieldContainer, (Runnable) new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda7(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$6() {
            postDelayed(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5(this), 150);
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 3000);
            this.postedErrorColorTimeout = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPasscodeError$5() {
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            int i = 0;
            codeFieldContainer2.isFocusSuppressed = false;
            codeFieldContainer2.codeField[0].requestFocus();
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

        public void onNextPressed(String str) {
            if (!this.nextPressed) {
                CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                codeFieldContainer2.isFocusSuppressed = true;
                for (CodeNumberField animateFocusedProgress : codeFieldContainer2.codeField) {
                    animateFocusedProgress.animateFocusedProgress(0.0f);
                }
                String code = this.codeFieldContainer.getCode();
                if (code.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                TLRPC$TL_auth_checkRecoveryPassword tLRPC$TL_auth_checkRecoveryPassword = new TLRPC$TL_auth_checkRecoveryPassword();
                tLRPC$TL_auth_checkRecoveryPassword.code = code;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_checkRecoveryPassword, new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda9(this, code), 10);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8(this, tLObject, str, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
            String str2;
            this.this$0.needHideProgress(false);
            this.nextPressed = false;
            if (tLObject instanceof TLRPC$TL_boolTrue) {
                Bundle bundle = new Bundle();
                bundle.putString("emailCode", str);
                bundle.putString("password", this.passwordString);
                this.this$0.setPage(9, true, bundle, false);
            } else if (tLRPC$TL_error == null || tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    str2 = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                } else {
                    str2 = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                }
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
            }
        }

        public boolean onBackPressed(boolean z) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$9() {
            this.inboxImageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.inboxImageView.playAnimation();
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            if (codeFieldContainer2 != null) {
                codeFieldContainer2.codeField[0].requestFocus();
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (!(code == null || code.length() == 0)) {
                bundle.putString("recoveryview_code", code);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("recoveryview_params", bundle2);
            }
        }

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

    public class LoginActivityNewPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor[] codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC$TL_account_password currentPassword;
        private int currentStage;
        private String emailCode;
        /* access modifiers changed from: private */
        public boolean isPasswordVisible;
        private String newPassword;
        private boolean nextPressed;
        private OutlineTextContainerView[] outlineFields;
        /* access modifiers changed from: private */
        public ImageView passwordButton;
        private String passwordString;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleTextView;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityNewPasswordView(org.telegram.ui.LoginActivity r25, android.content.Context r26, int r27) {
            /*
                r24 = this;
                r0 = r24
                r1 = r25
                r2 = r26
                r3 = r27
                r0.this$0 = r1
                r0.<init>(r2)
                r0.currentStage = r3
                r4 = 1
                r0.setOrientation(r4)
                if (r3 != r4) goto L_0x0017
                r5 = 1
                goto L_0x0018
            L_0x0017:
                r5 = 2
            L_0x0018:
                org.telegram.ui.Components.EditTextBoldCursor[] r5 = new org.telegram.ui.Components.EditTextBoldCursor[r5]
                r0.codeField = r5
                int r5 = r5.length
                org.telegram.ui.Components.OutlineTextContainerView[] r5 = new org.telegram.ui.Components.OutlineTextContainerView[r5]
                r0.outlineFields = r5
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                r6 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r7 = "fonts/rmedium.ttf"
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r5.setTypeface(r7)
                android.widget.TextView r5 = r0.titleTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r8, r9)
                android.widget.TextView r5 = r0.titleTextView
                r8 = 49
                r5.setGravity(r8)
                android.widget.TextView r5 = r0.titleTextView
                r8 = 2131628243(0x7f0e10d3, float:1.8883773E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString((int) r8)
                r5.setText(r8)
                android.widget.TextView r5 = r0.titleTextView
                r10 = -2
                r11 = -2
                r12 = 1
                r13 = 8
                boolean r8 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                r15 = 16
                if (r8 == 0) goto L_0x006b
                r14 = 16
                goto L_0x006f
            L_0x006b:
                r8 = 72
                r14 = 72
            L_0x006f:
                r8 = 8
                r16 = 0
                r15 = r8
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r5, r8)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                r8 = 1098907648(0x41800000, float:16.0)
                r5.setTextSize(r4, r8)
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r4)
                android.widget.TextView r5 = r0.confirmTextView
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r5.setLineSpacing(r10, r9)
                android.widget.TextView r5 = r0.confirmTextView
                r10 = -2
                r11 = -2
                r12 = 1
                r13 = 8
                r14 = 6
                r15 = 8
                r16 = 16
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r5, r10)
                r5 = 0
                r10 = 0
            L_0x00ab:
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                int r11 = r11.length
                r12 = -1
                if (r10 >= r11) goto L_0x01fa
                org.telegram.ui.Components.OutlineTextContainerView r11 = new org.telegram.ui.Components.OutlineTextContainerView
                r11.<init>(r2)
                org.telegram.ui.Components.OutlineTextContainerView[] r13 = r0.outlineFields
                r13[r10] = r11
                if (r3 != 0) goto L_0x00c6
                if (r10 != 0) goto L_0x00c2
                r13 = 2131627564(0x7f0e0e2c, float:1.8882396E38)
                goto L_0x00c9
            L_0x00c2:
                r13 = 2131627566(0x7f0e0e2e, float:1.88824E38)
                goto L_0x00c9
            L_0x00c6:
                r13 = 2131627358(0x7f0e0d5e, float:1.8881978E38)
            L_0x00c9:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString((int) r13)
                r11.setText(r13)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                org.telegram.ui.Components.EditTextBoldCursor r14 = new org.telegram.ui.Components.EditTextBoldCursor
                r14.<init>(r2)
                r13[r10] = r14
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r14 = 1101004800(0x41a00000, float:20.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r13.setCursorSize(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r14 = 1069547520(0x3fCLASSNAME, float:1.5)
                r13.setCursorWidth(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r13.setImeOptions(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r13.setTextSize(r4, r6)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r13.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r14 = 0
                r13.setBackground(r14)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r10]
                r14.setPadding(r13, r13, r13, r13)
                if (r3 != 0) goto L_0x0130
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                r14 = 129(0x81, float:1.81E-43)
                r13.setInputType(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                android.text.method.PasswordTransformationMethod r14 = android.text.method.PasswordTransformationMethod.getInstance()
                r13.setTransformationMethod(r14)
            L_0x0130:
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                android.graphics.Typeface r14 = android.graphics.Typeface.DEFAULT
                r13.setTypeface(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x0143
                r14 = 5
                goto L_0x0144
            L_0x0143:
                r14 = 3
            L_0x0144:
                r13.setGravity(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                if (r10 != 0) goto L_0x0151
                if (r3 != 0) goto L_0x0151
                r14 = 1
                goto L_0x0152
            L_0x0151:
                r14 = 0
            L_0x0152:
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$1 r15 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$1
                r15.<init>(r1, r14)
                r13.addTextChangedListener(r15)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r10]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3 r15 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3
                r15.<init>(r11)
                r13.setOnFocusChangeListener(r15)
                r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                if (r14 == 0) goto L_0x01c1
                android.widget.LinearLayout r14 = new android.widget.LinearLayout
                r14.<init>(r2)
                r14.setOrientation(r5)
                r15 = 16
                r14.setGravity(r15)
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r10]
                r15 = -2
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r15, (float) r9)
                r14.addView(r6, r15)
                android.widget.ImageView r6 = new android.widget.ImageView
                r6.<init>(r2)
                r0.passwordButton = r6
                r15 = 2131165800(0x7var_, float:1.7945827E38)
                r6.setImageResource(r15)
                android.widget.ImageView r6 = r0.passwordButton
                r15 = 1036831949(0x3dcccccd, float:0.1)
                org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r6, r4, r15, r5)
                android.widget.ImageView r6 = r0.passwordButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1 r15 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1
                r15.<init>(r0)
                r6.setOnClickListener(r15)
                android.widget.ImageView r6 = r0.passwordButton
                r17 = 1103101952(0x41CLASSNAME, float:24.0)
                r18 = 1103101952(0x41CLASSNAME, float:24.0)
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 1096810496(0x41600000, float:14.0)
                r23 = 0
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinearRelatively(r17, r18, r19, r20, r21, r22, r23)
                r14.addView(r6, r15)
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13)
                r11.addView(r14, r6)
                goto L_0x01cc
            L_0x01c1:
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r10]
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13)
                r11.addView(r6, r12)
            L_0x01cc:
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r10]
                r11.attachEditText(r6)
                r17 = -1
                r18 = -2
                r19 = 1
                r20 = 16
                r21 = 16
                r22 = 16
                r23 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r11, r6)
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r10]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4 r11 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4
                r11.<init>(r0, r10)
                r6.setOnEditorActionListener(r11)
                int r10 = r10 + 1
                r6 = 1099956224(0x41900000, float:18.0)
                goto L_0x00ab
            L_0x01fa:
                if (r3 != 0) goto L_0x020b
                android.widget.TextView r1 = r0.confirmTextView
                r3 = 2131627565(0x7f0e0e2d, float:1.8882398E38)
                java.lang.String r6 = "PleaseEnterNewFirstPasswordLogin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
                r1.setText(r3)
                goto L_0x0219
            L_0x020b:
                android.widget.TextView r1 = r0.confirmTextView
                r3 = 2131627360(0x7f0e0d60, float:1.8881982E38)
                java.lang.String r6 = "PasswordHintTextLogin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
                r1.setText(r3)
            L_0x0219:
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.cancelButton = r1
                r3 = 19
                r1.setGravity(r3)
                android.widget.TextView r1 = r0.cancelButton
                r3 = 1097859072(0x41700000, float:15.0)
                r1.setTextSize(r4, r3)
                android.widget.TextView r1 = r0.cancelButton
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r3 = (float) r3
                r1.setLineSpacing(r3, r9)
                android.widget.TextView r1 = r0.cancelButton
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r1.setPadding(r3, r5, r4, r5)
                android.widget.TextView r1 = r0.cancelButton
                r3 = 2131629284(0x7f0e14e4, float:1.8885885E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString((int) r3)
                r1.setText(r3)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                android.widget.TextView r2 = r0.cancelButton
                r3 = -1
                int r4 = android.os.Build.VERSION.SDK_INT
                r5 = 21
                if (r4 < r5) goto L_0x0260
                r4 = 56
                goto L_0x0262
            L_0x0260:
                r4 = 60
            L_0x0262:
                float r4 = (float) r4
                r5 = 80
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
                r1.addView(r2, r3)
                r2 = 80
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r2)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r1)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$new$0(OutlineTextContainerView outlineTextContainerView, View view, boolean z) {
            outlineTextContainerView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* access modifiers changed from: private */
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

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$2(int i, TextView textView, int i2, KeyEvent keyEvent) {
            if (i == 0) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr.length == 2) {
                    editTextBoldCursorArr[1].requestFocus();
                    return true;
                }
            }
            if (i2 != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            if (this.currentStage == 0) {
                recoverPassword((String) null, (String) null);
            } else {
                recoverPassword(this.newPassword, (String) null);
            }
        }

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
            for (OutlineTextContainerView updateColor : this.outlineFields) {
                updateColor.updateColor();
            }
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            ImageView imageView = this.passwordButton;
            if (imageView != null) {
                if (!this.isPasswordVisible) {
                    str = "windowBackgroundWhiteHintText";
                }
                imageView.setColorFilter(Theme.getColor(str));
                this.passwordButton.setBackground(Theme.createSelectorDrawable(this.this$0.getThemedColor("listSelectorSDK21"), 1));
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("NewPassword", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
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
                    TLRPC$TL_account_password TLdeserialize = TLRPC$TL_account_password.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    this.currentPassword = TLdeserialize;
                    TwoStepVerificationActivity.initPasswordNewAlgo(TLdeserialize);
                }
                this.newPassword = this.currentParams.getString("new_password");
                boolean unused = this.this$0.showKeyboard(this.codeField[0]);
                this.codeField[0].requestFocus();
            }
        }

        private void onPasscodeError(boolean z, int i) {
            if (this.this$0.getParentActivity() != null) {
                try {
                    this.codeField[i].performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                AndroidUtilities.shakeView(this.codeField[i], 2.0f, 0);
            }
        }

        public void onNextPressed(String str) {
            if (!this.nextPressed) {
                String obj = this.codeField[0].getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false, 0);
                } else if (this.currentStage != 0) {
                    this.nextPressed = true;
                    this.this$0.needShowProgress(0);
                    recoverPassword(this.newPassword, obj);
                } else if (!obj.equals(this.codeField[1].getText().toString())) {
                    onPasscodeError(false, 1);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("emailCode", this.emailCode);
                    bundle.putString("new_password", obj);
                    bundle.putString("password", this.passwordString);
                    this.this$0.setPage(10, true, bundle, false);
                }
            }
        }

        private void recoverPassword(String str, String str2) {
            TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword = new TLRPC$TL_auth_recoverPassword();
            tLRPC$TL_auth_recoverPassword.code = this.emailCode;
            if (!TextUtils.isEmpty(str)) {
                tLRPC$TL_auth_recoverPassword.flags |= 1;
                TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = new TLRPC$TL_account_passwordInputSettings();
                tLRPC$TL_auth_recoverPassword.new_settings = tLRPC$TL_account_passwordInputSettings;
                tLRPC$TL_account_passwordInputSettings.flags |= 1;
                tLRPC$TL_account_passwordInputSettings.hint = str2 != null ? str2 : "";
                tLRPC$TL_account_passwordInputSettings.new_algo = this.currentPassword.new_algo;
            }
            Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(this, str, str2, tLRPC$TL_auth_recoverPassword));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$9(String str, String str2, TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword) {
            byte[] stringBytes = str != null ? AndroidUtilities.getStringBytes(str) : null;
            LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9 loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9 = new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9(this, str, str2);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (str != null) {
                    tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                    if (tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash == null) {
                        TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                        tLRPC$TL_error.text = "ALGO_INVALID";
                        loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9.run((TLObject) null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_recoverPassword, loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9.run((TLObject) null, tLRPC$TL_error2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$8(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7(this, tLRPC$TL_error, str, str2, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$7(TLRPC$TL_error tLRPC$TL_error, String str, String str2, TLObject tLObject) {
            String str3;
            if (tLRPC$TL_error == null || (!"SRP_ID_INVALID".equals(tLRPC$TL_error.text) && !"NEW_SALT_INVALID".equals(tLRPC$TL_error.text))) {
                this.this$0.needHideProgress(false);
                if (tLObject instanceof TLRPC$auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0(this, tLObject));
                    if (TextUtils.isEmpty(str)) {
                        builder.setMessage(LocaleController.getString(NUM));
                    } else {
                        builder.setMessage(LocaleController.getString(NUM));
                    }
                    builder.setTitle(LocaleController.getString(NUM));
                    Dialog showDialog = this.this$0.showDialog(builder.create());
                    if (showDialog != null) {
                        showDialog.setCanceledOnTouchOutside(false);
                        showDialog.setCancelable(false);
                    }
                } else if (tLRPC$TL_error != null) {
                    this.nextPressed = false;
                    if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                        if (intValue < 60) {
                            str3 = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                        } else {
                            str3 = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                        }
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, str3));
                        return;
                    }
                    this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
                }
            } else {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10(this, str, str2), 8);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$5(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8(this, tLRPC$TL_error, tLObject, str, str2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$4(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, String str2) {
            if (tLRPC$TL_error == null) {
                TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
                this.currentPassword = tLRPC$TL_account_password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
                recoverPassword(str, str2);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$6(TLObject tLObject, DialogInterface dialogInterface, int i) {
            this.this$0.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject);
        }

        public boolean onBackPressed(boolean z) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$10() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                editTextBoldCursorArr[0].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                editTextBoldCursorArr2[0].setSelection(editTextBoldCursorArr2[0].length());
                AndroidUtilities.showKeyboard(this.codeField[0]);
            }
        }

        public void saveStateParams(Bundle bundle) {
            if (this.currentParams != null) {
                bundle.putBundle("recoveryview_params" + this.currentStage, this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("recoveryview_params" + this.currentStage);
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    public class LoginActivityRegisterView extends SlideView implements ImageUpdater.ImageUpdaterDelegate {
        private TLRPC$FileLocation avatar;
        /* access modifiers changed from: private */
        public AnimatorSet avatarAnimation;
        private TLRPC$FileLocation avatarBig;
        private AvatarDrawable avatarDrawable;
        /* access modifiers changed from: private */
        public RLottieImageView avatarEditor;
        /* access modifiers changed from: private */
        public BackupImageView avatarImage;
        /* access modifiers changed from: private */
        public View avatarOverlay;
        /* access modifiers changed from: private */
        public RadialProgressView avatarProgressView;
        /* access modifiers changed from: private */
        public RLottieDrawable cameraDrawable;
        /* access modifiers changed from: private */
        public RLottieDrawable cameraWaitDrawable;
        private Bundle currentParams;
        private TextView descriptionTextView;
        private FrameLayout editTextContainer;
        private EditTextBoldCursor firstNameField;
        private OutlineTextContainerView firstNameOutlineView;
        /* access modifiers changed from: private */
        public ImageUpdater imageUpdater;
        /* access modifiers changed from: private */
        public boolean isCameraWaitAnimationAllowed = true;
        private EditTextBoldCursor lastNameField;
        private OutlineTextContainerView lastNameOutlineView;
        private boolean nextPressed = false;
        private String phoneHash;
        private TextView privacyView;
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleTextView;
        /* access modifiers changed from: private */
        public TextView wrongNumber;

        public /* synthetic */ void didStartUpload(boolean z) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$didStartUpload(this, z);
        }

        public /* bridge */ /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        public boolean needBackButton() {
            return true;
        }

        public /* synthetic */ void onUploadProgressChanged(float f) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$onUploadProgressChanged(this, f);
        }

        public class LinkSpan extends ClickableSpan {
            public LinkSpan() {
            }

            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }

            public void onClick(View view) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        /* access modifiers changed from: private */
        public void showTermsOfService(boolean z) {
            if (this.this$0.currentTermsOfService != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("TermsOfService", NUM));
                if (z) {
                    builder.setPositiveButton(LocaleController.getString("Accept", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.this$0.currentTermsOfService.text);
                MessageObject.addEntitiesToText(spannableStringBuilder, this.this$0.currentTermsOfService.entities, false, false, false, false);
                builder.setMessage(spannableStringBuilder);
                this.this$0.showDialog(builder.create());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$0(DialogInterface dialogInterface, int i) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$3(DialogInterface dialogInterface, int i) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder.setMessage(LocaleController.getString("TosDecline", NUM));
            builder.setPositiveButton(LocaleController.getString("SignUp", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4(this));
            builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1(this));
            this.this$0.showDialog(builder.create());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$1(DialogInterface dialogInterface, int i) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$2(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRegisterView(org.telegram.ui.LoginActivity r26, android.content.Context r27) {
            /*
                r25 = this;
                r0 = r25
                r1 = r26
                r2 = r27
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 0
                r0.nextPressed = r3
                r4 = 1
                r0.isCameraWaitAnimationAllowed = r4
                r0.setOrientation(r4)
                org.telegram.ui.Components.ImageUpdater r5 = new org.telegram.ui.Components.ImageUpdater
                r5.<init>(r3)
                r0.imageUpdater = r5
                r5.setOpenWithFrontfaceCamera(r4)
                org.telegram.ui.Components.ImageUpdater r5 = r0.imageUpdater
                r5.setSearchAvailable(r3)
                org.telegram.ui.Components.ImageUpdater r5 = r0.imageUpdater
                r5.setUploadAfterSelect(r3)
                org.telegram.ui.Components.ImageUpdater r5 = r0.imageUpdater
                r5.parentFragment = r1
                r5.setDelegate(r0)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                r6 = 78
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r6, (int) r4)
                r0.addView(r5, r6)
                org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
                r6.<init>()
                r0.avatarDrawable = r6
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$1 r6 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$1
                r6.<init>(r2, r1)
                r0.avatarImage = r6
                r7 = 1115684864(0x42800000, float:64.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r6.setRoundRadius(r7)
                org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
                r7 = 13
                r6.setAvatarType(r7)
                org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
                r7 = 5
                r9 = 0
                r6.setInfo(r7, r9, r9)
                org.telegram.ui.Components.BackupImageView r6 = r0.avatarImage
                org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
                r6.setImageDrawable(r7)
                org.telegram.ui.Components.BackupImageView r6 = r0.avatarImage
                r7 = -1
                r8 = -1082130432(0xffffffffbvar_, float:-1.0)
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r6, r10)
                android.graphics.Paint r6 = new android.graphics.Paint
                r6.<init>(r4)
                r10 = 1426063360(0x55000000, float:8.796093E12)
                r6.setColor(r10)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$2 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$2
                r10.<init>(r2, r1, r6)
                r0.avatarOverlay = r10
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r10, r6)
                android.view.View r6 = r0.avatarOverlay
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6
                r10.<init>(r0)
                r6.setOnClickListener(r10)
                org.telegram.ui.Components.RLottieDrawable r6 = new org.telegram.ui.Components.RLottieDrawable
                r10 = 2131558413(0x7f0d000d, float:1.8742141E38)
                java.lang.String r13 = java.lang.String.valueOf(r10)
                r10 = 1116471296(0x428CLASSNAME, float:70.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r12 = 2131558413(0x7f0d000d, float:1.8742141E38)
                r16 = 0
                r17 = 0
                r11 = r6
                r11.<init>(r12, r13, r14, r15, r16, r17)
                r0.cameraDrawable = r6
                org.telegram.ui.Components.RLottieDrawable r6 = new org.telegram.ui.Components.RLottieDrawable
                r11 = 2131558416(0x7f0d0010, float:1.8742147E38)
                java.lang.String r20 = java.lang.String.valueOf(r11)
                int r21 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r22 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r19 = 2131558416(0x7f0d0010, float:1.8742147E38)
                r23 = 0
                r24 = 0
                r18 = r6
                r18.<init>(r19, r20, r21, r22, r23, r24)
                r0.cameraWaitDrawable = r6
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$3 r6 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$3
                r6.<init>(r2, r1)
                r0.avatarEditor = r6
                android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
                r6.setScaleType(r10)
                org.telegram.ui.Components.RLottieImageView r6 = r0.avatarEditor
                org.telegram.ui.Components.RLottieDrawable r10 = r0.cameraDrawable
                r6.setAnimation(r10)
                org.telegram.ui.Components.RLottieImageView r6 = r0.avatarEditor
                r6.setEnabled(r3)
                org.telegram.ui.Components.RLottieImageView r6 = r0.avatarEditor
                r6.setClickable(r3)
                org.telegram.ui.Components.RLottieImageView r6 = r0.avatarEditor
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r6, r10)
                org.telegram.ui.Components.RLottieImageView r6 = r0.avatarEditor
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$4
                r10.<init>(r1)
                r6.addOnAttachStateChangeListener(r10)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$5 r6 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$5
                r6.<init>(r2, r1)
                r0.avatarProgressView = r6
                r1 = 1106247680(0x41var_, float:30.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r6.setSize(r1)
                org.telegram.ui.Components.RadialProgressView r1 = r0.avatarProgressView
                r1.setProgressColor(r7)
                org.telegram.ui.Components.RadialProgressView r1 = r0.avatarProgressView
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r1, r6)
                r0.showAvatarProgress(r3, r3)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.titleTextView = r1
                r5 = 2131627880(0x7f0e0var_, float:1.8883037E38)
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
                r1.setText(r5)
                android.widget.TextView r1 = r0.titleTextView
                r5 = 1099956224(0x41900000, float:18.0)
                r1.setTextSize(r4, r5)
                android.widget.TextView r1 = r0.titleTextView
                java.lang.String r5 = "fonts/rmedium.ttf"
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                r1.setTypeface(r5)
                android.widget.TextView r1 = r0.titleTextView
                r5 = 1073741824(0x40000000, float:2.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r6 = (float) r6
                r8 = 1065353216(0x3var_, float:1.0)
                r1.setLineSpacing(r6, r8)
                android.widget.TextView r1 = r0.titleTextView
                r1.setGravity(r4)
                android.widget.TextView r1 = r0.titleTextView
                r10 = -2
                r11 = -2
                r12 = 1
                r13 = 8
                r14 = 12
                r15 = 8
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r1, r6)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.descriptionTextView = r1
                java.lang.String r6 = "RegisterText2"
                r10 = 2131627879(0x7f0e0var_, float:1.8883035E38)
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
                r1.setText(r6)
                android.widget.TextView r1 = r0.descriptionTextView
                r1.setGravity(r4)
                android.widget.TextView r1 = r0.descriptionTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r1.setTextSize(r4, r6)
                android.widget.TextView r1 = r0.descriptionTextView
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r10 = (float) r10
                r1.setLineSpacing(r10, r8)
                android.widget.TextView r1 = r0.descriptionTextView
                r10 = -2
                r14 = 6
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r1, r10)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r0.editTextContainer = r1
                r10 = -1
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1101529088(0x41a80000, float:21.0)
                r14 = 1090519040(0x41000000, float:8.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11, r12, r13, r14, r15)
                r0.addView(r1, r10)
                org.telegram.ui.Components.OutlineTextContainerView r1 = new org.telegram.ui.Components.OutlineTextContainerView
                r1.<init>(r2)
                r0.firstNameOutlineView = r1
                r10 = 2131625906(0x7f0e07b2, float:1.8879033E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
                r1.setText(r10)
                org.telegram.ui.Components.EditTextBoldCursor r1 = new org.telegram.ui.Components.EditTextBoldCursor
                r1.<init>(r2)
                r0.firstNameField = r1
                r10 = 1101004800(0x41a00000, float:20.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r1.setCursorSize(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r11 = 1069547520(0x3fCLASSNAME, float:1.5)
                r1.setCursorWidth(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r12 = 268435461(0x10000005, float:2.5243564E-29)
                r1.setImeOptions(r12)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r12 = 1099431936(0x41880000, float:17.0)
                r1.setTextSize(r4, r12)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r1.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r13 = 8192(0x2000, float:1.14794E-41)
                r1.setInputType(r13)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9 r14 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9
                r14.<init>(r0)
                r1.setOnFocusChangeListener(r14)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r1.setBackground(r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r14 = 1098907648(0x41800000, float:16.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.setPadding(r15, r3, r8, r5)
                org.telegram.ui.Components.OutlineTextContainerView r1 = r0.firstNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.firstNameField
                r1.attachEditText(r3)
                org.telegram.ui.Components.OutlineTextContainerView r1 = r0.firstNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.firstNameField
                r5 = -2
                r8 = 48
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r8)
                r1.addView(r3, r15)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10 r3 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10
                r3.<init>(r0)
                r1.setOnEditorActionListener(r3)
                org.telegram.ui.Components.OutlineTextContainerView r1 = new org.telegram.ui.Components.OutlineTextContainerView
                r1.<init>(r2)
                r0.lastNameOutlineView = r1
                r3 = 2131626368(0x7f0e0980, float:1.887997E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString((int) r3)
                r1.setText(r3)
                org.telegram.ui.Components.EditTextBoldCursor r1 = new org.telegram.ui.Components.EditTextBoldCursor
                r1.<init>(r2)
                r0.lastNameField = r1
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r1.setCursorSize(r3)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setCursorWidth(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r3 = 268435462(0x10000006, float:2.5243567E-29)
                r1.setImeOptions(r3)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setTextSize(r4, r12)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setInputType(r13)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8 r3 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8
                r3.<init>(r0)
                r1.setOnFocusChangeListener(r3)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setBackground(r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.setPadding(r3, r9, r10, r11)
                org.telegram.ui.Components.OutlineTextContainerView r1 = r0.lastNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r1.attachEditText(r3)
                org.telegram.ui.Components.OutlineTextContainerView r1 = r0.lastNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r8)
                r1.addView(r3, r5)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11 r3 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11
                r3.<init>(r0)
                r1.setOnEditorActionListener(r3)
                boolean r1 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                r0.buildEditTextLayout(r1)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.wrongNumber = r1
                java.lang.String r3 = "CancelRegistration"
                r5 = 2131624838(0x7f0e0386, float:1.8876867E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)
                r1.setText(r3)
                android.widget.TextView r1 = r0.wrongNumber
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                r5 = 5
                r9 = 3
                if (r3 == 0) goto L_0x02e1
                r3 = 5
                goto L_0x02e2
            L_0x02e1:
                r3 = 3
            L_0x02e2:
                r3 = r3 | r4
                r1.setGravity(r3)
                android.widget.TextView r1 = r0.wrongNumber
                r1.setTextSize(r4, r6)
                android.widget.TextView r1 = r0.wrongNumber
                r3 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r10
                r10 = 1065353216(0x3var_, float:1.0)
                r1.setLineSpacing(r3, r10)
                android.widget.TextView r1 = r0.wrongNumber
                r3 = 1103101952(0x41CLASSNAME, float:24.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r10 = 0
                r1.setPadding(r10, r3, r10, r10)
                android.widget.TextView r1 = r0.wrongNumber
                r3 = 8
                r1.setVisibility(r3)
                android.widget.TextView r1 = r0.wrongNumber
                r10 = -2
                r11 = -2
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                if (r3 == 0) goto L_0x0315
                goto L_0x0316
            L_0x0315:
                r5 = 3
            L_0x0316:
                r12 = r5 | 48
                r13 = 0
                r14 = 20
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r1, r3)
                android.widget.TextView r1 = r0.wrongNumber
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7 r3 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7
                r3.<init>(r0)
                r1.setOnClickListener(r3)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r3 = 83
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r7, (int) r3)
                r0.addView(r1, r3)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.privacyView = r3
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r2.<init>()
                r3.setMovementMethod(r2)
                android.widget.TextView r2 = r0.privacyView
                boolean r3 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r3 == 0) goto L_0x0356
                r6 = 1095761920(0x41500000, float:13.0)
            L_0x0356:
                r2.setTextSize(r4, r6)
                android.widget.TextView r2 = r0.privacyView
                r3 = 1073741824(0x40000000, float:2.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                r5 = 1065353216(0x3var_, float:1.0)
                r2.setLineSpacing(r3, r5)
                android.widget.TextView r2 = r0.privacyView
                r3 = 16
                r2.setGravity(r3)
                android.widget.TextView r2 = r0.privacyView
                r8 = -2
                int r3 = android.os.Build.VERSION.SDK_INT
                r5 = 21
                if (r3 < r5) goto L_0x037c
                r3 = 1113587712(0x42600000, float:56.0)
                r9 = 1113587712(0x42600000, float:56.0)
                goto L_0x0380
            L_0x037c:
                r3 = 1114636288(0x42700000, float:60.0)
                r9 = 1114636288(0x42700000, float:60.0)
            L_0x0380:
                r10 = 83
                r11 = 1096810496(0x41600000, float:14.0)
                r12 = 0
                r13 = 1116471296(0x428CLASSNAME, float:70.0)
                r14 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r1.addView(r2, r3)
                android.widget.TextView r1 = r0.privacyView
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r1)
                r1 = 2131628594(0x7f0e1232, float:1.8884485E38)
                java.lang.String r2 = "TermsOfServiceLogin"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
                r2.<init>(r1)
                r3 = 42
                int r5 = r1.indexOf(r3)
                int r1 = r1.lastIndexOf(r3)
                if (r5 == r7) goto L_0x03ca
                if (r1 == r7) goto L_0x03ca
                if (r5 == r1) goto L_0x03ca
                int r3 = r1 + 1
                java.lang.String r6 = ""
                r2.replace(r1, r3, r6)
                int r3 = r5 + 1
                r2.replace(r5, r3, r6)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan r3 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan
                r3.<init>()
                int r1 = r1 - r4
                r4 = 33
                r2.setSpan(r3, r5, r1, r4)
            L_0x03ca:
                android.widget.TextView r1 = r0.privacyView
                r1.setText(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRegisterView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13(this), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5(this));
            this.isCameraWaitAnimationAllowed = false;
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.avatarEditor.playAnimation();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4() {
            this.avatar = null;
            this.avatarBig = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(DialogInterface dialogInterface) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.avatarEditor.setAnimation(this.cameraDrawable);
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.setOnAnimationEndListener(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14(this));
                this.avatarEditor.playAnimation();
                return;
            }
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0, false);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5() {
            this.isCameraWaitAnimationAllowed = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(View view, boolean z) {
            this.firstNameOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$9(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10(View view, boolean z) {
            this.lastNameOutlineView.animateSelection(z ? 1.0f : 0.0f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$11(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$12(View view) {
            if (this.this$0.radialProgressView.getTag() == null) {
                onBackPressed(false);
            }
        }

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
                LinearLayout linearLayout = new LinearLayout(this.this$0.getParentActivity());
                linearLayout.setOrientation(0);
                this.firstNameOutlineView.setText(LocaleController.getString(NUM));
                this.lastNameOutlineView.setText(LocaleController.getString(NUM));
                linearLayout.addView(this.firstNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 8, 0));
                linearLayout.addView(this.lastNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 8, 0, 0, 0));
                this.editTextContainer.addView(linearLayout);
                if (hasFocus) {
                    this.firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.firstNameField);
                } else if (hasFocus2) {
                    this.lastNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.lastNameField);
                }
            } else {
                this.firstNameOutlineView.setText(LocaleController.getString(NUM));
                this.lastNameOutlineView.setText(LocaleController.getString(NUM));
                this.editTextContainer.addView(this.firstNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 0.0f, 8.0f, 0.0f));
                this.editTextContainer.addView(this.lastNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 82.0f, 8.0f, 0.0f));
            }
        }

        public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda18(this, tLRPC$PhotoSize2, tLRPC$PhotoSize));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didUploadPhoto$13(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            this.avatar = tLRPC$FileLocation;
            this.avatarBig = tLRPC$PhotoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        }

        private void showAvatarProgress(final boolean z, boolean z2) {
            if (this.avatarEditor != null) {
                AnimatorSet animatorSet = this.avatarAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.avatarAnimation = null;
                }
                if (z2) {
                    this.avatarAnimation = new AnimatorSet();
                    if (z) {
                        this.avatarProgressView.setVisibility(0);
                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                    } else {
                        this.avatarEditor.setVisibility(0);
                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                    }
                    this.avatarAnimation.setDuration(180);
                    this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (LoginActivityRegisterView.this.avatarAnimation != null && LoginActivityRegisterView.this.avatarEditor != null) {
                                if (z) {
                                    LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                                } else {
                                    LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                                }
                                AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
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
        }

        public boolean onBackPressed(boolean z) {
            if (!z) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString("AreYouSureRegistration", NUM));
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2(this));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
                return false;
            }
            this.this$0.needHideProgress(true);
            this.nextPressed = false;
            this.currentParams = null;
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$14(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
            hidePrivacyView();
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onShow() {
            super.onShow();
            if (this.privacyView != null) {
                if (this.this$0.restoringState) {
                    this.privacyView.setAlpha(1.0f);
                } else {
                    this.privacyView.setAlpha(0.0f);
                    this.privacyView.animate().alpha(1.0f).setDuration(200).setStartDelay(300).setInterpolator(AndroidUtilities.decelerateInterpolator).start();
                }
            }
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$15() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.firstNameField.setText("");
                this.lastNameField.setText("");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.currentParams = bundle;
            }
        }

        public void onNextPressed(String str) {
            if (!this.nextPressed) {
                if (this.this$0.currentTermsOfService != null && this.this$0.currentTermsOfService.popup) {
                    showTermsOfService(true);
                } else if (this.firstNameField.length() == 0) {
                    this.this$0.onFieldError(this.firstNameOutlineView, true);
                } else {
                    this.nextPressed = true;
                    TLRPC$TL_auth_signUp tLRPC$TL_auth_signUp = new TLRPC$TL_auth_signUp();
                    tLRPC$TL_auth_signUp.phone_code_hash = this.phoneHash;
                    tLRPC$TL_auth_signUp.phone_number = this.requestPhone;
                    tLRPC$TL_auth_signUp.first_name = this.firstNameField.getText().toString();
                    tLRPC$TL_auth_signUp.last_name = this.lastNameField.getText().toString();
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_signUp, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda19(this), 10);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$19(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda16(this, tLObject, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$18(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            this.nextPressed = false;
            if (tLObject instanceof TLRPC$TL_auth_authorization) {
                hidePrivacyView();
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15(this, tLObject), 150);
                return;
            }
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error.text.contains("PHONE_NUMBER_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (tLRPC$TL_error.text.contains("FIRSTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (tLRPC$TL_error.text.contains("LASTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$17(TLObject tLObject) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.this$0.fragmentView.findFocus());
            this.this$0.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject, true);
            TLRPC$FileLocation tLRPC$FileLocation = this.avatarBig;
            if (tLRPC$FileLocation != null) {
                Utilities.cacheClearQueue.postRunnable(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda17(this, tLRPC$FileLocation));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$16(TLRPC$FileLocation tLRPC$FileLocation) {
            MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(tLRPC$FileLocation);
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.firstNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_first", obj);
            }
            String obj2 = this.lastNameField.getText().toString();
            if (obj2.length() != 0) {
                bundle.putString("registerview_last", obj2);
            }
            if (this.this$0.currentTermsOfService != null) {
                SerializedData serializedData = new SerializedData(this.this$0.currentTermsOfService.getObjectSize());
                this.this$0.currentTermsOfService.serializeToStream(serializedData);
                bundle.putString("terms", Base64.encodeToString(serializedData.toByteArray(), 0));
                serializedData.cleanup();
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("registerview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            byte[] decode;
            Bundle bundle2 = bundle.getBundle("registerview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            try {
                String string = bundle.getString("terms");
                if (!(string == null || (decode = Base64.decode(string, 0)) == null)) {
                    SerializedData serializedData = new SerializedData(decode);
                    TLRPC$TL_help_termsOfService unused = this.this$0.currentTermsOfService = TLRPC$TL_help_termsOfService.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    serializedData.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
            this.privacyView.animate().alpha(0.0f).setDuration(150).setStartDelay(0).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
        }
    }

    /* access modifiers changed from: private */
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

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        if (!z || this.introView == null) {
            return null;
        }
        final TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(this.fragmentView.getContext());
        transformableLoginButtonView.setButtonText(this.startMessagingButton.getPaint(), this.startMessagingButton.getText().toString());
        int width = this.startMessagingButton.getWidth();
        int height = this.startMessagingButton.getHeight();
        int i = this.floatingButtonIcon.getLayoutParams().width;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        transformableLoginButtonView.setLayoutParams(layoutParams);
        int[] iArr = new int[2];
        this.fragmentView.getLocationInWindow(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        this.startMessagingButton.getLocationInWindow(iArr);
        float f = (float) (iArr[0] - i2);
        float f2 = (float) (iArr[1] - i3);
        transformableLoginButtonView.setTranslationX(f);
        transformableLoginButtonView.setTranslationY(f2);
        int width2 = (((getParentLayout().getWidth() - this.floatingButtonIcon.getLayoutParams().width) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).rightMargin) - getParentLayout().getPaddingLeft()) - getParentLayout().getPaddingRight();
        int height2 = ((((getParentLayout().getHeight() - this.floatingButtonIcon.getLayoutParams().height) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).bottomMargin) - (isCustomKeyboardVisible() ? AndroidUtilities.dp(230.0f) : 0)) - getParentLayout().getPaddingTop()) - getParentLayout().getPaddingBottom();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        final Runnable runnable2 = runnable;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                LoginActivity.this.floatingButtonContainer.setVisibility(4);
                LoginActivity.this.keyboardLinearLayout.setAlpha(0.0f);
                LoginActivity.this.fragmentView.setBackgroundColor(0);
                LoginActivity.this.startMessagingButton.setVisibility(4);
                ((FrameLayout) LoginActivity.this.fragmentView).addView(transformableLoginButtonView);
            }

            public void onAnimationEnd(Animator animator) {
                LoginActivity.this.keyboardLinearLayout.setAlpha(1.0f);
                LoginActivity.this.startMessagingButton.setVisibility(0);
                LoginActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                LoginActivity.this.floatingButtonContainer.setVisibility(0);
                ((FrameLayout) LoginActivity.this.fragmentView).removeView(transformableLoginButtonView);
                if (LoginActivity.this.animationFinishCallback != null) {
                    AndroidUtilities.runOnUIThread(LoginActivity.this.animationFinishCallback);
                    Runnable unused = LoginActivity.this.animationFinishCallback = null;
                }
                boolean unused2 = LoginActivity.this.isAnimatingIntro = false;
                runnable2.run();
            }
        });
        int color = Theme.getColor("windowBackgroundWhite");
        LoginActivity$$ExternalSyntheticLambda4 loginActivity$$ExternalSyntheticLambda4 = r0;
        ValueAnimator valueAnimator = ofFloat;
        LoginActivity$$ExternalSyntheticLambda4 loginActivity$$ExternalSyntheticLambda42 = new LoginActivity$$ExternalSyntheticLambda4(this, color, Color.alpha(color), layoutParams, width, i, height, transformableLoginButtonView, f, width2, f2, height2);
        valueAnimator.addUpdateListener(loginActivity$$ExternalSyntheticLambda4);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(new Animator[]{valueAnimator});
        animatorSet.start();
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$17(int i, int i2, ViewGroup.MarginLayoutParams marginLayoutParams, int i3, int i4, int i5, TransformableLoginButtonView transformableLoginButtonView, float f, int i6, float f2, int i7, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardLinearLayout.setAlpha(floatValue);
        this.fragmentView.setBackgroundColor(ColorUtils.setAlphaComponent(i, (int) (((float) i2) * floatValue)));
        float f3 = 1.0f - floatValue;
        this.slideViewsContainer.setTranslationY(((float) AndroidUtilities.dp(20.0f)) * f3);
        if (!isCustomKeyboardForceDisabled()) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            customPhoneKeyboardView.setTranslationY(((float) customPhoneKeyboardView.getLayoutParams().height) * f3);
            this.floatingButtonContainer.setTranslationY(((float) this.keyboardView.getLayoutParams().height) * f3);
        }
        this.introView.setTranslationY(((float) (-AndroidUtilities.dp(20.0f))) * floatValue);
        float f4 = (f3 * 0.05f) + 0.95f;
        this.introView.setScaleX(f4);
        this.introView.setScaleY(f4);
        marginLayoutParams.width = (int) (((float) i3) + (((float) (i4 - i3)) * floatValue));
        marginLayoutParams.height = (int) (((float) i5) + (((float) (i4 - i5)) * floatValue));
        transformableLoginButtonView.requestLayout();
        transformableLoginButtonView.setProgress(floatValue);
        transformableLoginButtonView.setTranslationX(f + ((((float) i6) - f) * floatValue));
        transformableLoginButtonView.setTranslationY(f2 + ((((float) i7) - f2) * floatValue));
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        Activity parentActivity = getParentActivity();
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = parentActivity.getResources().getDrawable(NUM).mutate();
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
        for (SlideView updateColors : this.views) {
            updateColors.updateColors();
        }
        this.keyboardView.updateColors();
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.updateColors();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new LoginActivity$$ExternalSyntheticLambda20(this), "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText6", "windowBackgroundWhiteHintText", "listSelectorSDK21", "chats_actionBackground", "chats_actionIcon", "windowBackgroundWhiteInputField", "windowBackgroundWhiteInputFieldActivated", "windowBackgroundWhiteValueText", "dialogTextRed", "windowBackgroundWhiteGrayText", "checkbox", "windowBackgroundWhiteBlueText4", "changephoneinfo_image2", "chats_actionPressedBackground", "windowBackgroundWhiteRedText2", "windowBackgroundWhiteLinkText", "checkboxSquareUnchecked", "checkboxSquareBackground", "checkboxSquareCheck", "dialogBackground", "dialogTextGray2", "dialogTextBlack");
    }

    /* access modifiers changed from: private */
    public void tryResetAccount(String str, String str2, String str3) {
        if (this.radialProgressView.getTag() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
            builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$$ExternalSyntheticLambda7(this, str, str2, str3));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$20(String str, String str2, String str3, DialogInterface dialogInterface, int i) {
        needShowProgress(0);
        TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
        tLRPC$TL_account_deleteAccount.reason = "Forgot password";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new LoginActivity$$ExternalSyntheticLambda19(this, str, str2, str3), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$19(String str, String str2, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda17(this, tLRPC$TL_error, str, str2, str3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$tryResetAccount$18(TLRPC$TL_error tLRPC$TL_error, String str, String str2, String str3) {
        needHideProgress(false);
        if (tLRPC$TL_error == null) {
            if (str == null || str2 == null || str3 == null) {
                setPage(0, true, (Bundle) null, true);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("phoneFormated", str);
            bundle.putString("phoneHash", str2);
            bundle.putString("code", str3);
            setPage(5, true, bundle, false);
        } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
            needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
        } else if (tLRPC$TL_error.text.startsWith("2FA_CONFIRM_WAIT_")) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("phoneFormated", str);
            bundle2.putString("phoneHash", str2);
            bundle2.putString("code", str3);
            bundle2.putInt("startTime", ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            bundle2.putInt("waitTime", Utilities.parseInt((CharSequence) tLRPC$TL_error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
            setPage(8, true, bundle2, false);
        } else {
            needShowAlert(LocaleController.getString(NUM), tLRPC$TL_error.text);
        }
    }

    private static final class PhoneNumberConfirmView extends FrameLayout {
        /* access modifiers changed from: private */
        public View blurredView;
        private IConfirmDialogCallback callback;
        private TextView confirmMessageView;
        private TextView confirmTextView;
        private View dimmView;
        private boolean dismissed;
        private TextView editTextView;
        /* access modifiers changed from: private */
        public View fabContainer;
        private TransformableLoginButtonView fabTransform;
        /* access modifiers changed from: private */
        public RadialProgressView floatingProgressView;
        /* access modifiers changed from: private */
        public ViewGroup fragmentView;
        private TextView numberView;
        /* access modifiers changed from: private */
        public FrameLayout popupFabContainer;
        private FrameLayout popupLayout;

        private interface IConfirmDialogCallback {
            void onConfirmPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onDismiss(PhoneNumberConfirmView phoneNumberConfirmView);

            void onEditPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onFabPressed(PhoneNumberConfirmView phoneNumberConfirmView, TransformableLoginButtonView transformableLoginButtonView);
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        private PhoneNumberConfirmView(Context context, ViewGroup viewGroup, View view, String str, IConfirmDialogCallback iConfirmDialogCallback) {
            super(context);
            Context context2 = context;
            IConfirmDialogCallback iConfirmDialogCallback2 = iConfirmDialogCallback;
            this.fragmentView = viewGroup;
            this.fabContainer = view;
            this.callback = iConfirmDialogCallback2;
            View view2 = new View(getContext());
            this.blurredView = view2;
            view2.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda3(this));
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
            FrameLayout frameLayout = new FrameLayout(context2);
            this.popupFabContainer = frameLayout;
            frameLayout.addView(this.fabTransform, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda6(this, iConfirmDialogCallback2));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.floatingProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(22.0f));
            this.floatingProgressView.setAlpha(0.0f);
            this.floatingProgressView.setScaleX(0.1f);
            this.floatingProgressView.setScaleY(0.1f);
            this.popupFabContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setContentDescription(LocaleController.getString(NUM));
            FrameLayout frameLayout2 = this.popupFabContainer;
            int i = Build.VERSION.SDK_INT;
            addView(frameLayout2, LayoutHelper.createFrame(i >= 21 ? 56 : 60, i >= 21 ? 56.0f : 60.0f));
            FrameLayout frameLayout3 = new FrameLayout(context2);
            this.popupLayout = frameLayout3;
            addView(frameLayout3, LayoutHelper.createFrame(-1, 140.0f, 49, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context2);
            this.confirmMessageView = textView;
            textView.setText(LocaleController.getString(NUM));
            this.confirmMessageView.setTextSize(1, 14.0f);
            this.confirmMessageView.setSingleLine();
            int i2 = 5;
            this.popupLayout.addView(this.confirmMessageView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 20.0f, 24.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.numberView = textView2;
            textView2.setText(str);
            this.numberView.setTextSize(1, 18.0f);
            this.numberView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.numberView.setSingleLine();
            this.popupLayout.addView(this.numberView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 48.0f, 24.0f, 0.0f));
            int dp = AndroidUtilities.dp(16.0f);
            TextView textView3 = new TextView(context2);
            this.editTextView = textView3;
            textView3.setText(LocaleController.getString(NUM));
            this.editTextView.setSingleLine();
            this.editTextView.setTextSize(1, 16.0f);
            this.editTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.editTextView.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda4(this, iConfirmDialogCallback2));
            this.editTextView.setTypeface(Typeface.DEFAULT_BOLD);
            int i3 = dp / 2;
            this.editTextView.setPadding(dp, i3, dp, i3);
            float f = (float) 8;
            this.popupLayout.addView(this.editTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, f, f, f, f));
            TextView textView4 = new TextView(context2);
            this.confirmTextView = textView4;
            textView4.setText(LocaleController.getString(NUM));
            this.confirmTextView.setSingleLine();
            this.confirmTextView.setTextSize(1, 16.0f);
            this.confirmTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.confirmTextView.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda5(this, iConfirmDialogCallback2));
            this.confirmTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.confirmTextView.setPadding(dp, i3, dp, i3);
            this.popupLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i2) | 80, f, f, f, f));
            updateFabPosition();
            updateColors();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            dismiss();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onFabPressed(this, this.fabTransform);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onEditPressed(this, this.editTextView);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(IConfirmDialogCallback iConfirmDialogCallback, View view) {
            iConfirmDialogCallback.onConfirmPressed(this, this.confirmTextView);
        }

        /* access modifiers changed from: private */
        public void updateFabPosition() {
            int[] iArr = new int[2];
            this.fragmentView.getLocationInWindow(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            this.fabContainer.getLocationInWindow(iArr);
            this.popupFabContainer.setTranslationX((float) (iArr[0] - i));
            this.popupFabContainer.setTranslationY((float) (iArr[1] - i2));
            requestLayout();
        }

        /* access modifiers changed from: private */
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

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.popupLayout.getMeasuredHeight();
            int translationY = (int) (this.popupFabContainer.getTranslationY() - ((float) AndroidUtilities.dp(32.0f)));
            FrameLayout frameLayout = this.popupLayout;
            frameLayout.layout(frameLayout.getLeft(), translationY - measuredHeight, this.popupLayout.getRight(), translationY);
        }

        /* access modifiers changed from: private */
        public void show() {
            if (Build.VERSION.SDK_INT >= 21) {
                View view = this.fabContainer;
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, new float[]{view.getTranslationZ(), 0.0f}).setDuration(150).start();
            }
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(250);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(8);
                    int measuredWidth = (int) (((float) PhoneNumberConfirmView.this.fragmentView.getMeasuredWidth()) / 10.0f);
                    int measuredHeight = (int) (((float) PhoneNumberConfirmView.this.fragmentView.getMeasuredHeight()) / 10.0f);
                    Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.scale(0.1f, 0.1f);
                    PhoneNumberConfirmView.this.fragmentView.draw(canvas);
                    Utilities.stackBlurBitmap(createBitmap, Math.max(8, Math.max(measuredWidth, measuredHeight) / 150));
                    PhoneNumberConfirmView.this.blurredView.setBackground(new BitmapDrawable(PhoneNumberConfirmView.this.getContext().getResources(), createBitmap));
                    PhoneNumberConfirmView.this.blurredView.setAlpha(0.0f);
                    PhoneNumberConfirmView.this.blurredView.setVisibility(0);
                    PhoneNumberConfirmView.this.fragmentView.addView(PhoneNumberConfirmView.this);
                }

                public void onAnimationEnd(Animator animator) {
                    if (AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                        PhoneNumberConfirmView.this.popupFabContainer.requestFocus();
                    }
                }
            });
            duration.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda1(this));
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.start();
        }

        /* access modifiers changed from: private */
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

        /* access modifiers changed from: private */
        public void animateProgress(final Runnable runnable) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addListener(new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(Animator animator) {
                    runnable.run();
                }
            });
            ofFloat.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2(this));
            ofFloat.setDuration(150);
            ofFloat.start();
        }

        /* access modifiers changed from: private */
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

        /* access modifiers changed from: private */
        public void dismiss() {
            if (!this.dismissed) {
                this.dismissed = true;
                this.callback.onDismiss(this);
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(250);
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhoneNumberConfirmView.this.getParent() instanceof ViewGroup) {
                            ((ViewGroup) PhoneNumberConfirmView.this.getParent()).removeView(PhoneNumberConfirmView.this);
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            ObjectAnimator.ofFloat(PhoneNumberConfirmView.this.fabContainer, View.TRANSLATION_Z, new float[]{0.0f, (float) AndroidUtilities.dp(2.0f)}).setDuration(150).start();
                        }
                        PhoneNumberConfirmView.this.fabContainer.setVisibility(0);
                    }
                });
                duration.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda0(this));
                duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                duration.start();
            }
        }

        /* access modifiers changed from: private */
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

    private static final class PhoneInputData {
        /* access modifiers changed from: private */
        public CountrySelectActivity.Country country;
        /* access modifiers changed from: private */
        public List<String> patterns;
        /* access modifiers changed from: private */
        public String phoneNumber;

        private PhoneInputData() {
        }
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
