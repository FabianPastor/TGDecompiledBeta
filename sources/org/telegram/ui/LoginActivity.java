package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
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
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC$TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_auth_sendCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_signIn;
import org.telegram.tgnet.TLRPC$TL_auth_signUp;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_codeSettings;
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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
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
    private boolean[] doneButtonVisible = {true, false};
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public ContextProgressView doneProgressView;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public ImageView floatingButtonIcon;
    /* access modifiers changed from: private */
    public RadialProgressView floatingProgressView;
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
    private int progressRequestId;
    /* access modifiers changed from: private */
    public boolean restoringState;
    /* access modifiers changed from: private */
    public int scrollHeight;
    /* access modifiers changed from: private */
    public AnimatorSet[] showDoneAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public boolean syncContacts = true;
    /* access modifiers changed from: private */
    public boolean testBackend = false;
    private SlideView[] views = new SlideView[11];

    private static class ProgressView extends View {
        private boolean animating;
        private final RectF boundsRect = new RectF();
        private long duration;
        private final Paint paint;
        private final Paint paint2;
        private final Path path = new Path();
        private float radius;
        private final RectF rect = new RectF();
        private long startTime;

        public ProgressView(Context context) {
            super(context);
            Paint paint3 = new Paint(1);
            this.paint = paint3;
            Paint paint4 = new Paint(1);
            this.paint2 = paint4;
            paint3.setColor(Theme.getColor("login_progressInner"));
            paint4.setColor(Theme.getColor("login_progressOuter"));
        }

        public void startProgressAnimation(long j) {
            this.animating = true;
            this.duration = j;
            this.startTime = System.currentTimeMillis();
            invalidate();
        }

        public void resetProgressAnimation() {
            this.duration = 0;
            this.startTime = 0;
            this.animating = false;
            invalidate();
        }

        public boolean isProgressAnimationRunning() {
            return this.animating;
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            this.path.rewind();
            float f = (float) i2;
            this.radius = f / 2.0f;
            this.boundsRect.set(0.0f, 0.0f, (float) i, f);
            this.rect.set(this.boundsRect);
            Path path2 = this.path;
            RectF rectF = this.boundsRect;
            float f2 = this.radius;
            path2.addRoundRect(rectF, f2, f2, Path.Direction.CW);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float min = this.duration > 0 ? Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.duration)) : 0.0f;
            canvas.clipPath(this.path);
            RectF rectF = this.boundsRect;
            float f = this.radius;
            canvas.drawRoundRect(rectF, f, f, this.paint);
            RectF rectF2 = this.rect;
            rectF2.right = this.boundsRect.right * min;
            float f2 = this.radius;
            canvas.drawRoundRect(rectF2, f2, f2, this.paint2);
            boolean z = this.animating & (this.duration > 0 && min < 1.0f);
            this.animating = z;
            if (z) {
                postInvalidateOnAnimation();
            }
        }
    }

    public LoginActivity() {
    }

    public LoginActivity(int i) {
        this.currentAccount = i;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i < slideViewArr.length) {
                if (slideViewArr[i] != null) {
                    slideViewArr[i].onDestroyActivity();
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:104:0x03db, code lost:
        if (r3 != 4) goto L_0x03e2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r28) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r3 = "AppName"
            r4 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r2.setTitle(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.LoginActivity$1 r3 = new org.telegram.ui.LoginActivity$1
            r3.<init>()
            r2.setActionBarMenuOnItemClick(r3)
            r2 = 0
            r0.currentDoneType = r2
            boolean[] r3 = r0.doneButtonVisible
            r4 = 1
            r3[r2] = r4
            r3[r4] = r2
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r3.createMenu()
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r5.setAllowOverlayTitle(r4)
            r5 = 1113587712(0x42600000, float:56.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 2131165498(0x7var_a, float:1.7945215E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r3.addItemWithWidth(r4, r7, r6)
            r0.doneItem = r3
            org.telegram.ui.Components.ContextProgressView r3 = new org.telegram.ui.Components.ContextProgressView
            r3.<init>(r1, r4)
            r0.doneProgressView = r3
            r6 = 0
            r3.setAlpha(r6)
            org.telegram.ui.Components.ContextProgressView r3 = r0.doneProgressView
            r7 = 1036831949(0x3dcccccd, float:0.1)
            r3.setScaleX(r7)
            org.telegram.ui.Components.ContextProgressView r3 = r0.doneProgressView
            r3.setScaleY(r7)
            org.telegram.ui.Components.ContextProgressView r3 = r0.doneProgressView
            r8 = 4
            r3.setVisibility(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            r3.setAlpha(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            r3.setScaleX(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            r3.setScaleY(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            org.telegram.ui.Components.ContextProgressView r9 = r0.doneProgressView
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11)
            r3.addView(r9, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            java.lang.String r9 = "Done"
            r12 = 2131625272(0x7f0e0538, float:1.8877747E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r12)
            r3.setContentDescription(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.doneItem
            boolean[] r13 = r0.doneButtonVisible
            boolean r13 = r13[r4]
            r14 = 8
            if (r13 == 0) goto L_0x0095
            r13 = 0
            goto L_0x0097
        L_0x0095:
            r13 = 8
        L_0x0097:
            r3.setVisibility(r13)
            org.telegram.ui.LoginActivity$2 r3 = new org.telegram.ui.LoginActivity$2
            r3.<init>(r1)
            r0.fragmentView = r3
            org.telegram.ui.LoginActivity$3 r13 = new org.telegram.ui.LoginActivity$3
            r13.<init>(r1)
            r13.setFillViewport(r4)
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11)
            r3.addView(r13, r15)
            android.widget.FrameLayout r15 = new android.widget.FrameLayout
            r15.<init>(r1)
            r11 = -2
            r7 = 51
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r10, r11, r7)
            r13.addView(r15, r7)
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$PhoneView r11 = new org.telegram.ui.LoginActivity$PhoneView
            r11.<init>(r0, r1)
            r7[r2] = r11
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r11 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r11.<init>(r0, r1, r4)
            r7[r4] = r11
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r11 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r13 = 2
            r11.<init>(r0, r1, r13)
            r7[r13] = r11
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r11 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r10 = 3
            r11.<init>(r0, r1, r10)
            r7[r10] = r11
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r11 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r11.<init>(r0, r1, r8)
            r7[r8] = r11
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r11 = 5
            org.telegram.ui.LoginActivity$LoginActivityRegisterView r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView
            r10.<init>(r0, r1)
            r7[r11] = r10
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r10 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView
            r10.<init>(r0, r1)
            r11 = 6
            r7[r11] = r10
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r10 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView
            r10.<init>(r0, r1)
            r6 = 7
            r7[r6] = r10
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityResetWaitView r10 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView
            r10.<init>(r0, r1)
            r7[r14] = r10
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r10 = 9
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r14 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r14.<init>(r0, r1, r2)
            r7[r10] = r14
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r10 = 10
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r14 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r14.<init>(r0, r1, r4)
            r7[r10] = r14
            r7 = 0
        L_0x012c:
            org.telegram.ui.Components.SlideView[] r10 = r0.views
            int r14 = r10.length
            if (r7 >= r14) goto L_0x0176
            r10 = r10[r7]
            if (r7 != 0) goto L_0x0137
            r14 = 0
            goto L_0x0139
        L_0x0137:
            r14 = 8
        L_0x0139:
            r10.setVisibility(r14)
            org.telegram.ui.Components.SlideView[] r10 = r0.views
            r10 = r10[r7]
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            boolean r14 = org.telegram.messenger.AndroidUtilities.isTablet()
            r19 = 1104150528(0x41d00000, float:26.0)
            r20 = 1099956224(0x41900000, float:18.0)
            if (r14 == 0) goto L_0x0153
            r14 = 1104150528(0x41d00000, float:26.0)
            goto L_0x0155
        L_0x0153:
            r14 = 1099956224(0x41900000, float:18.0)
        L_0x0155:
            r21 = 1106247680(0x41var_, float:30.0)
            boolean r22 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r22 == 0) goto L_0x0160
            r22 = 1104150528(0x41d00000, float:26.0)
            goto L_0x0162
        L_0x0160:
            r22 = 1099956224(0x41900000, float:18.0)
        L_0x0162:
            r23 = 0
            r19 = r14
            r20 = r21
            r21 = r22
            r22 = r23
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r15.addView(r10, r14)
            int r7 = r7 + 1
            goto L_0x012c
        L_0x0176:
            android.os.Bundle r7 = r27.loadCurrentState()
            if (r7 == 0) goto L_0x01e2
            java.lang.String r14 = "currentViewNum"
            int r14 = r7.getInt(r14, r2)
            r0.currentViewNum = r14
            java.lang.String r14 = "syncContacts"
            int r14 = r7.getInt(r14, r4)
            if (r14 != r4) goto L_0x018e
            r14 = 1
            goto L_0x018f
        L_0x018e:
            r14 = 0
        L_0x018f:
            r0.syncContacts = r14
            int r14 = r0.currentViewNum
            if (r14 < r4) goto L_0x01ba
            if (r14 > r8) goto L_0x01ba
            java.lang.String r6 = "open"
            int r6 = r7.getInt(r6)
            if (r6 == 0) goto L_0x01e2
            long r14 = java.lang.System.currentTimeMillis()
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            long r10 = (long) r6
            long r14 = r14 - r10
            long r10 = java.lang.Math.abs(r14)
            r14 = 86400(0x15180, double:4.26873E-319)
            int r6 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r6 < 0) goto L_0x01e2
            r0.currentViewNum = r2
            r27.clearCurrentState()
            goto L_0x01e1
        L_0x01ba:
            if (r14 != r11) goto L_0x01ce
            org.telegram.ui.Components.SlideView[] r6 = r0.views
            r6 = r6[r11]
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r6 = (org.telegram.ui.LoginActivity.LoginActivityPasswordView) r6
            org.telegram.tgnet.TLRPC$TL_account_password r6 = r6.currentPassword
            if (r6 != 0) goto L_0x01e2
            r0.currentViewNum = r2
            r27.clearCurrentState()
            goto L_0x01e1
        L_0x01ce:
            if (r14 != r6) goto L_0x01e2
            org.telegram.ui.Components.SlideView[] r10 = r0.views
            r6 = r10[r6]
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r6 = (org.telegram.ui.LoginActivity.LoginActivityRecoverView) r6
            java.lang.String r6 = r6.passwordString
            if (r6 != 0) goto L_0x01e2
            r0.currentViewNum = r2
            r27.clearCurrentState()
        L_0x01e1:
            r7 = 0
        L_0x01e2:
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r0.floatingButtonContainer = r6
            boolean[] r10 = r0.doneButtonVisible
            boolean r10 = r10[r2]
            if (r10 == 0) goto L_0x01f1
            r10 = 0
            goto L_0x01f3
        L_0x01f1:
            r10 = 8
        L_0x01f3:
            r6.setVisibility(r10)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.String r10 = "chats_actionBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            java.lang.String r11 = "chats_actionPressedBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r6, r10, r11)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 >= r11) goto L_0x023c
            android.content.res.Resources r14 = r28.getResources()
            r15 = 2131165418(0x7var_ea, float:1.7945053E38)
            android.graphics.drawable.Drawable r14 = r14.getDrawable(r15)
            android.graphics.drawable.Drawable r14 = r14.mutate()
            android.graphics.PorterDuffColorFilter r15 = new android.graphics.PorterDuffColorFilter
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r15.<init>(r8, r12)
            r14.setColorFilter(r15)
            org.telegram.ui.Components.CombinedDrawable r8 = new org.telegram.ui.Components.CombinedDrawable
            r8.<init>(r14, r6, r2, r2)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r8.setIconSize(r6, r12)
            r6 = r8
        L_0x023c:
            android.widget.FrameLayout r8 = r0.floatingButtonContainer
            r8.setBackgroundDrawable(r6)
            if (r10 < r11) goto L_0x02a7
            android.animation.StateListAnimator r6 = new android.animation.StateListAnimator
            r6.<init>()
            int[] r8 = new int[r4]
            r12 = 16842919(0x10100a7, float:2.3694026E-38)
            r8[r2] = r12
            android.widget.ImageView r12 = r0.floatingButtonIcon
            float[] r14 = new float[r13]
            r15 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r5 = (float) r5
            r14[r2] = r5
            r5 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r11 = (float) r11
            r14[r4] = r11
            java.lang.String r11 = "translationZ"
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r11, r14)
            r4 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r12.setDuration(r4)
            r6.addState(r8, r4)
            int[] r4 = new int[r2]
            android.widget.ImageView r5 = r0.floatingButtonIcon
            float[] r8 = new float[r13]
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8[r2] = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r12 = (float) r12
            r14 = 1
            r8[r14] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r11, r8)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r5 = r5.setDuration(r11)
            r6.addState(r4, r5)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            r4.setStateListAnimator(r6)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$4 r5 = new org.telegram.ui.LoginActivity$4
            r5.<init>(r0)
            r4.setOutlineProvider(r5)
        L_0x02a7:
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            r5 = 21
            if (r10 < r5) goto L_0x02b7
            r6 = 56
            r20 = 56
            goto L_0x02bb
        L_0x02b7:
            r6 = 60
            r20 = 60
        L_0x02bb:
            if (r10 < r5) goto L_0x02c0
            r21 = 1113587712(0x42600000, float:56.0)
            goto L_0x02c4
        L_0x02c0:
            r5 = 1114636288(0x42700000, float:60.0)
            r21 = 1114636288(0x42700000, float:60.0)
        L_0x02c4:
            r22 = 85
            r23 = 0
            r24 = 0
            r25 = 1096810496(0x41600000, float:14.0)
            r26 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r4, r5)
            android.widget.FrameLayout r3 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2
            r4.<init>(r0)
            r3.setOnClickListener(r4)
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r1)
            r0.floatingButtonIcon = r3
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView r3 = r0.floatingButtonIcon
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "chats_actionIcon"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r6)
            r3.setColorFilter(r4)
            android.widget.ImageView r3 = r0.floatingButtonIcon
            r4 = 2131165246(0x7var_e, float:1.7944704E38)
            r3.setImageResource(r4)
            android.widget.FrameLayout r3 = r0.floatingButtonContainer
            r4 = 2131625272(0x7f0e0538, float:1.8877747E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r3.setContentDescription(r4)
            android.widget.FrameLayout r3 = r0.floatingButtonContainer
            android.widget.ImageView r4 = r0.floatingButtonIcon
            r5 = 21
            if (r10 < r5) goto L_0x031c
            r6 = 56
            goto L_0x031e
        L_0x031c:
            r6 = 60
        L_0x031e:
            if (r10 < r5) goto L_0x0323
            r5 = 1113587712(0x42600000, float:56.0)
            goto L_0x0325
        L_0x0323:
            r5 = 1114636288(0x42700000, float:60.0)
        L_0x0325:
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r3.addView(r4, r5)
            org.telegram.ui.Components.RadialProgressView r3 = new org.telegram.ui.Components.RadialProgressView
            r3.<init>(r1)
            r0.floatingProgressView = r3
            r1 = 1102053376(0x41b00000, float:22.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setProgressColor(r3)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r3 = 0
            r1.setAlpha(r3)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r1.setScaleX(r3)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r1.setScaleY(r3)
            org.telegram.ui.Components.RadialProgressView r1 = r0.floatingProgressView
            r3 = 4
            r1.setVisibility(r3)
            android.widget.FrameLayout r1 = r0.floatingButtonContainer
            org.telegram.ui.Components.RadialProgressView r3 = r0.floatingProgressView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r1.addView(r3, r4)
            r1 = 1
            if (r7 == 0) goto L_0x0373
            r0.restoringState = r1
        L_0x0373:
            r3 = 0
        L_0x0374:
            org.telegram.ui.Components.SlideView[] r4 = r0.views
            int r5 = r4.length
            if (r3 >= r5) goto L_0x03f4
            if (r7 == 0) goto L_0x038f
            if (r3 < r1) goto L_0x038a
            r1 = 4
            if (r3 > r1) goto L_0x038a
            int r1 = r0.currentViewNum
            if (r3 != r1) goto L_0x038f
            r1 = r4[r3]
            r1.restoreStateParams(r7)
            goto L_0x038f
        L_0x038a:
            r1 = r4[r3]
            r1.restoreStateParams(r7)
        L_0x038f:
            int r1 = r0.currentViewNum
            if (r1 != r3) goto L_0x03e5
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.Components.SlideView[] r4 = r0.views
            r4 = r4[r3]
            boolean r4 = r4.needBackButton()
            if (r4 != 0) goto L_0x03a6
            boolean r4 = r0.newAccount
            if (r4 == 0) goto L_0x03a4
            goto L_0x03a6
        L_0x03a4:
            r4 = 0
            goto L_0x03a9
        L_0x03a6:
            r4 = 2131165468(0x7var_c, float:1.7945154E38)
        L_0x03a9:
            r1.setBackButtonImage(r4)
            org.telegram.ui.Components.SlideView[] r1 = r0.views
            r1 = r1[r3]
            r1.setVisibility(r2)
            org.telegram.ui.Components.SlideView[] r1 = r0.views
            r1 = r1[r3]
            r1.onShow()
            r0.currentDoneType = r2
            r1 = 1
            if (r3 == r1) goto L_0x03d0
            if (r3 == r13) goto L_0x03d0
            r4 = 3
            if (r3 == r4) goto L_0x03d0
            r4 = 4
            if (r3 == r4) goto L_0x03d0
            r4 = 8
            if (r3 != r4) goto L_0x03cc
            goto L_0x03d0
        L_0x03cc:
            r0.showDoneButton(r1, r2)
            goto L_0x03d3
        L_0x03d0:
            r0.showDoneButton(r2, r2)
        L_0x03d3:
            if (r3 == r1) goto L_0x03de
            if (r3 == r13) goto L_0x03de
            r5 = 3
            r6 = 4
            if (r3 == r5) goto L_0x03e0
            if (r3 != r6) goto L_0x03e2
            goto L_0x03e0
        L_0x03de:
            r5 = 3
            r6 = 4
        L_0x03e0:
            r0.currentDoneType = r1
        L_0x03e2:
            r8 = 8
            goto L_0x03f1
        L_0x03e5:
            r1 = 1
            r5 = 3
            r6 = 4
            org.telegram.ui.Components.SlideView[] r4 = r0.views
            r4 = r4[r3]
            r8 = 8
            r4.setVisibility(r8)
        L_0x03f1:
            int r3 = r3 + 1
            goto L_0x0374
        L_0x03f4:
            r0.restoringState = r2
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            int r2 = r0.currentViewNum
            r2 = r4[r2]
            java.lang.String r2 = r2.getHeaderName()
            r1.setTitle(r2)
            android.view.View r1 = r0.fragmentView
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        onDoneButtonPressed();
    }

    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    public void onResume() {
        SlideView slideView;
        int access$600;
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
                if ((slideViewArr[i] instanceof LoginActivitySmsView) && (access$600 = ((LoginActivitySmsView) slideViewArr[i]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) access$600)) >= 86400) {
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
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            int i2 = this.currentViewNum;
            if (i2 == 0) {
                this.views[i2].onNextPressed();
            }
        } else if (i == 7) {
            this.checkShowPermissions = false;
            int i3 = this.currentViewNum;
            if (i3 == 0) {
                ((PhoneView) this.views[i3]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
        if (this.newAccount) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda3(this), 200);
            getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDialogDismiss$1() {
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
            } else if (i < 1 || i > 4) {
                if (i == 5) {
                    ((LoginActivityRegisterView) this.views[i]).wrongNumber.callOnClick();
                } else if (i == 9) {
                    this.views[i].onBackPressed(true);
                    setPage(7, true, (Bundle) null, true);
                } else if (i == 10 || i == 11) {
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
    public void onFieldError(View view) {
        try {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
        } catch (Throwable unused) {
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    public static void needShowInvalidAlert(BaseFragment baseFragment, String str, boolean z) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", NUM));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new LoginActivity$$ExternalSyntheticLambda1(z, str, baseFragment));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$needShowInvalidAlert$2(boolean z, String str, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
            Intent intent = new Intent("android.intent.action.SENDTO");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
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
            builder.setTitle(LocaleController.getString("AppName", NUM));
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
                animatorSetArr[i].cancel();
            }
            boolean[] zArr = this.doneButtonVisible;
            int i2 = this.currentDoneType;
            zArr[i2] = z;
            if (z2) {
                this.showDoneAnimation[i2] = new AnimatorSet();
                if (z) {
                    if (z3) {
                        this.floatingButtonContainer.setVisibility(0);
                        this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{0.0f}));
                    } else {
                        this.doneItem.setVisibility(0);
                        this.showDoneAnimation[this.currentDoneType].playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{1.0f})});
                    }
                } else if (z3) {
                    this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{AndroidUtilities.dpf2(70.0f)}));
                } else {
                    this.showDoneAnimation[this.currentDoneType].playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{0.0f})});
                }
                this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (LoginActivity.this.showDoneAnimation[!z3] != null && LoginActivity.this.showDoneAnimation[!z3].equals(animator) && !z) {
                            if (z3) {
                                LoginActivity.this.floatingButtonContainer.setVisibility(8);
                            } else {
                                LoginActivity.this.doneItem.setVisibility(8);
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
                    this.floatingButtonContainer.setTranslationY(0.0f);
                    return;
                }
                this.doneItem.setVisibility(0);
                this.doneItem.setScaleX(1.0f);
                this.doneItem.setScaleY(1.0f);
                this.doneItem.setAlpha(1.0f);
            } else if (z3) {
                this.floatingButtonContainer.setVisibility(8);
                this.floatingButtonContainer.setTranslationY(AndroidUtilities.dpf2(70.0f));
            } else {
                this.doneItem.setVisibility(8);
                this.doneItem.setScaleX(0.1f);
                this.doneItem.setScaleY(0.1f);
                this.doneItem.setAlpha(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onDoneButtonPressed() {
        if (this.doneButtonVisible[this.currentDoneType]) {
            if (this.doneProgressView.getTag() == null) {
                this.views[this.currentViewNum].onNextPressed();
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("StopLoading", NUM));
                builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$$ExternalSyntheticLambda0(this));
                showDialog(builder.create());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDoneButtonPressed$3(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        final boolean z4 = this.currentDoneType == 0;
        if (z2) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.doneProgressView.setTag(1);
                if (z4) {
                    this.floatingProgressView.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(false);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.doneProgressView.setVisibility(0);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{1.0f})});
                }
            } else {
                this.doneProgressView.setTag((Object) null);
                if (z4) {
                    this.floatingButtonIcon.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(true);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.doneItem.getContentView().setVisibility(0);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
                }
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animator)) {
                        if (z4) {
                            if (!z3) {
                                LoginActivity.this.floatingProgressView.setVisibility(4);
                            } else {
                                LoginActivity.this.floatingButtonIcon.setVisibility(4);
                            }
                        } else if (!z3) {
                            LoginActivity.this.doneProgressView.setVisibility(4);
                        } else {
                            LoginActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = LoginActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (!z3) {
            this.doneProgressView.setTag((Object) null);
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
            this.doneItem.getContentView().setVisibility(0);
            this.doneProgressView.setVisibility(4);
            this.doneProgressView.setScaleX(0.1f);
            this.doneProgressView.setScaleY(0.1f);
            this.doneProgressView.setAlpha(0.0f);
            this.doneItem.getContentView().setScaleX(1.0f);
            this.doneItem.getContentView().setScaleY(1.0f);
            this.doneItem.getContentView().setAlpha(1.0f);
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
            this.doneProgressView.setVisibility(0);
            this.doneItem.getContentView().setVisibility(4);
            this.doneItem.getContentView().setScaleX(0.1f);
            this.doneItem.getContentView().setScaleY(0.1f);
            this.doneItem.getContentView().setAlpha(0.0f);
            this.doneProgressView.setScaleX(1.0f);
            this.doneProgressView.setScaleY(1.0f);
            this.doneProgressView.setAlpha(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int i) {
        needShowProgress(i, true);
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int i, boolean z) {
        this.progressRequestId = i;
        showEditDoneProgress(true, z);
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean z) {
        needHideProgress(z, true);
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean z, boolean z2) {
        if (this.progressRequestId != 0) {
            if (z) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        showEditDoneProgress(false, z2);
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        final boolean z3 = i == 0 || i == 5 || i == 6 || i == 7 || i == 9 || i == 10 || i == 11;
        if (z3) {
            if (i == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, z);
            this.currentDoneType = 0;
            showEditDoneProgress(false, false);
            if (!z) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, z);
            if (i != 8) {
                this.currentDoneType = 1;
            }
        }
        int i2 = NUM;
        if (z) {
            SlideView[] slideViewArr = this.views;
            final SlideView slideView = slideViewArr[this.currentViewNum];
            SlideView slideView2 = slideViewArr[i];
            this.currentViewNum = i;
            ActionBar actionBar = this.actionBar;
            if (!slideView2.needBackButton() && !this.newAccount) {
                i2 = 0;
            }
            actionBar.setBackButtonImage(i2);
            slideView2.setParams(bundle, false);
            this.actionBar.setTitle(slideView2.getHeaderName());
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
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!this.views[i].needBackButton() && !this.newAccount) {
            i2 = 0;
        }
        actionBar2.setBackButtonImage(i2);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = i;
        this.views[i].setParams(bundle, false);
        this.views[i].setVisibility(0);
        this.actionBar.setTitle(this.views[i].getHeaderName());
        setParentActivityTitle(this.views[i].getHeaderName());
        this.views[i].onShow();
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

    private void needFinishActivity(boolean z) {
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false);
                finishFragment();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("afterSignup", z);
            presentFragment(new DialogsActivity(bundle), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization) {
        onAuthSuccess(tLRPC$TL_auth_authorization, false);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization, boolean z) {
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
        needFinishActivity(z);
    }

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle bundle, TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode) {
        bundle.putString("phoneHash", tLRPC$TL_auth_sentCode.phone_code_hash);
        TLRPC$auth_CodeType tLRPC$auth_CodeType = tLRPC$TL_auth_sentCode.next_type;
        if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        }
        if (tLRPC$TL_auth_sentCode.type instanceof TLRPC$TL_auth_sentCodeTypeApp) {
            bundle.putInt("type", 1);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(1, true, bundle, false);
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
            setPage(4, true, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tLRPC$TL_auth_sentCode.type.pattern);
            setPage(3, true, bundle, false);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeSms) {
            bundle.putInt("type", 2);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(2, true, bundle, false);
        }
    }

    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener {
        private CheckBoxCell checkBoxCell;
        /* access modifiers changed from: private */
        public EditTextBoldCursor codeField;
        /* access modifiers changed from: private */
        public HashMap<String, CountrySelectActivity.Country> codesMap = new HashMap<>();
        private ArrayList<CountrySelectActivity.Country> countriesArray = new ArrayList<>();
        /* access modifiers changed from: private */
        public TextView countryButton;
        /* access modifiers changed from: private */
        public int countryState = 0;
        /* access modifiers changed from: private */
        public boolean ignoreOnPhoneChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreSelection = false;
        private boolean nextPressed = false;
        private boolean numberFilled;
        /* access modifiers changed from: private */
        public HintEditText phoneField;
        /* access modifiers changed from: private */
        public HashMap<String, String> phoneFormatMap = new HashMap<>();
        private CheckBoxCell testBackendCheckBox;
        /* access modifiers changed from: private */
        public TextView textView;
        /* access modifiers changed from: private */
        public TextView textView2;
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public View view;

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(org.telegram.ui.LoginActivity r22, android.content.Context r23) {
            /*
                r21 = this;
                r1 = r21
                r2 = r22
                r0 = r23
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
                r4 = 1
                r1.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r0)
                r1.countryButton = r5
                r6 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r1.countryButton
                r7 = 1082130432(0x40800000, float:4.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r5.setPadding(r8, r9, r10, r7)
                android.widget.TextView r5 = r1.countryButton
                java.lang.String r7 = "windowBackgroundWhiteBlackText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r5.setTextColor(r8)
                android.widget.TextView r5 = r1.countryButton
                r5.setMaxLines(r4)
                android.widget.TextView r5 = r1.countryButton
                r5.setSingleLine(r4)
                android.widget.TextView r5 = r1.countryButton
                android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r8)
                android.widget.TextView r5 = r1.countryButton
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                r9 = 5
                r10 = 3
                if (r8 == 0) goto L_0x0078
                r8 = 5
                goto L_0x0079
            L_0x0078:
                r8 = 3
            L_0x0079:
                r8 = r8 | r4
                r5.setGravity(r8)
                android.widget.TextView r5 = r1.countryButton
                java.lang.String r8 = "listSelectorSDK21"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r11 = 7
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r11)
                r5.setBackground(r8)
                android.widget.TextView r5 = r1.countryButton
                r11 = -1
                r12 = 36
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r5, r8)
                android.widget.TextView r5 = r1.countryButton
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1 r8 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1
                r8.<init>(r1)
                r5.setOnClickListener(r8)
                android.view.View r5 = new android.view.View
                r5.<init>(r0)
                r1.view = r5
                r8 = 1094713344(0x41400000, float:12.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r5.setPadding(r11, r3, r8, r3)
                android.view.View r5 = r1.view
                java.lang.String r8 = "windowBackgroundWhiteGrayLine"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r5.setBackgroundColor(r8)
                android.view.View r5 = r1.view
                r11 = -1
                r12 = 1
                r13 = 1082130432(0x40800000, float:4.0)
                r14 = -1047789568(0xffffffffCLASSNAMECLASSNAME, float:-17.5)
                r15 = 1082130432(0x40800000, float:4.0)
                r16 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r5, r8)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r0)
                r5.setOrientation(r3)
                r12 = -2
                r13 = 0
                r14 = 1101004800(0x41a00000, float:20.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r5, r8)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r0)
                r1.textView = r8
                java.lang.String r11 = "+"
                r8.setText(r11)
                android.widget.TextView r8 = r1.textView
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setTextColor(r11)
                android.widget.TextView r8 = r1.textView
                r8.setTextSize(r4, r6)
                android.widget.TextView r8 = r1.textView
                r11 = -2
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
                r5.addView(r8, r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = new org.telegram.ui.Components.EditTextBoldCursor
                r8.<init>(r0)
                r1.codeField = r8
                r8.setInputType(r10)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r3)
                r8.setBackgroundDrawable(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setCursorColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r11 = 1101004800(0x41a00000, float:20.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r8.setCursorSize(r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r12 = 1069547520(0x3fCLASSNAME, float:1.5)
                r8.setCursorWidth(r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r13 = 1092616192(0x41200000, float:10.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r8.setPadding(r13, r3, r3, r3)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r8.setTextSize(r4, r6)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r8.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r13 = 19
                r8.setGravity(r13)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r8.setImeOptions(r14)
                android.text.InputFilter[] r8 = new android.text.InputFilter[r4]
                android.text.InputFilter$LengthFilter r15 = new android.text.InputFilter$LengthFilter
                r15.<init>(r9)
                r8[r3] = r15
                org.telegram.ui.Components.EditTextBoldCursor r15 = r1.codeField
                r15.setFilters(r8)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                r15 = 55
                r16 = 36
                r17 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                r18 = 0
                r19 = 1098907648(0x41800000, float:16.0)
                r20 = 0
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r16, r17, r18, r19, r20)
                r5.addView(r8, r15)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$1 r15 = new org.telegram.ui.LoginActivity$PhoneView$1
                r15.<init>(r2)
                r8.addTextChangedListener(r15)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5 r15 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5
                r15.<init>(r1)
                r8.setOnEditorActionListener(r15)
                org.telegram.ui.LoginActivity$PhoneView$2 r8 = new org.telegram.ui.LoginActivity$PhoneView$2
                r8.<init>(r1, r0, r2)
                r1.phoneField = r8
                r8.setInputType(r10)
                org.telegram.ui.Components.HintEditText r8 = r1.phoneField
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setTextColor(r15)
                org.telegram.ui.Components.HintEditText r8 = r1.phoneField
                java.lang.String r15 = "windowBackgroundWhiteHintText"
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r8.setHintTextColor(r15)
                org.telegram.ui.Components.HintEditText r8 = r1.phoneField
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r3)
                r8.setBackgroundDrawable(r15)
                org.telegram.ui.Components.HintEditText r8 = r1.phoneField
                r8.setPadding(r3, r3, r3, r3)
                org.telegram.ui.Components.HintEditText r8 = r1.phoneField
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setCursorColor(r7)
                org.telegram.ui.Components.HintEditText r7 = r1.phoneField
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r7.setCursorSize(r8)
                org.telegram.ui.Components.HintEditText r7 = r1.phoneField
                r7.setCursorWidth(r12)
                org.telegram.ui.Components.HintEditText r7 = r1.phoneField
                r7.setTextSize(r4, r6)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                r6.setMaxLines(r4)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                r6.setGravity(r13)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                r6.setImeOptions(r14)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                r7 = -1
                r8 = 1108344832(0x42100000, float:36.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r6, r7)
                org.telegram.ui.Components.HintEditText r5 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$3 r6 = new org.telegram.ui.LoginActivity$PhoneView$3
                r6.<init>(r2)
                r5.addTextChangedListener(r6)
                org.telegram.ui.Components.HintEditText r5 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4
                r6.<init>(r1)
                r5.setOnEditorActionListener(r6)
                org.telegram.ui.Components.HintEditText r5 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3
                r6.<init>(r1)
                r5.setOnKeyListener(r6)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r0)
                r1.textView2 = r5
                r6 = 2131627784(0x7f0e0var_, float:1.8882842E38)
                java.lang.String r7 = "StartText"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                r5.setText(r6)
                android.widget.TextView r5 = r1.textView2
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r1.textView2
                r6 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r1.textView2
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0252
                r6 = 5
                goto L_0x0253
            L_0x0252:
                r6 = 3
            L_0x0253:
                r5.setGravity(r6)
                android.widget.TextView r5 = r1.textView2
                r6 = 1073741824(0x40000000, float:2.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                r7 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r6, r7)
                android.widget.TextView r5 = r1.textView2
                r11 = -2
                r12 = -2
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x026e
                r13 = 5
                goto L_0x026f
            L_0x026e:
                r13 = 3
            L_0x026f:
                r14 = 0
                r15 = 28
                r16 = 0
                r17 = 10
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r5, r6)
                boolean r5 = r22.newAccount
                java.lang.String r6 = ""
                r7 = 2
                if (r5 == 0) goto L_0x02ba
                org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
                r5.<init>(r0, r7)
                r1.checkBoxCell = r5
                r8 = 2131627880(0x7f0e0var_, float:1.8883037E38)
                java.lang.String r9 = "SyncContacts"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                boolean r9 = r22.syncContacts
                r5.setText(r8, r6, r9, r3)
                org.telegram.ui.Cells.CheckBoxCell r5 = r1.checkBoxCell
                r11 = -2
                r12 = -1
                r13 = 51
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r5, r8)
                org.telegram.ui.Cells.CheckBoxCell r5 = r1.checkBoxCell
                org.telegram.ui.LoginActivity$PhoneView$4 r8 = new org.telegram.ui.LoginActivity$PhoneView$4
                r8.<init>(r2)
                r5.setOnClickListener(r8)
            L_0x02ba:
                boolean r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
                if (r5 == 0) goto L_0x02eb
                org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
                r5.<init>(r0, r7)
                r1.testBackendCheckBox = r5
                boolean r0 = r22.testBackend
                java.lang.String r8 = "Test Backend"
                r5.setText(r8, r6, r0, r3)
                org.telegram.ui.Cells.CheckBoxCell r0 = r1.testBackendCheckBox
                r11 = -2
                r12 = -1
                r13 = 51
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r0, r5)
                org.telegram.ui.Cells.CheckBoxCell r0 = r1.testBackendCheckBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2
                r5.<init>(r1)
                r0.setOnClickListener(r5)
            L_0x02eb:
                java.util.HashMap r5 = new java.util.HashMap
                r5.<init>()
                java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0349 }
                java.io.InputStreamReader r8 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0349 }
                android.content.res.Resources r9 = r21.getResources()     // Catch:{ Exception -> 0x0349 }
                android.content.res.AssetManager r9 = r9.getAssets()     // Catch:{ Exception -> 0x0349 }
                java.lang.String r11 = "countries.txt"
                java.io.InputStream r9 = r9.open(r11)     // Catch:{ Exception -> 0x0349 }
                r8.<init>(r9)     // Catch:{ Exception -> 0x0349 }
                r0.<init>(r8)     // Catch:{ Exception -> 0x0349 }
            L_0x0308:
                java.lang.String r8 = r0.readLine()     // Catch:{ Exception -> 0x0349 }
                if (r8 == 0) goto L_0x0345
                java.lang.String r9 = ";"
                java.lang.String[] r8 = r8.split(r9)     // Catch:{ Exception -> 0x0349 }
                org.telegram.ui.CountrySelectActivity$Country r9 = new org.telegram.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0349 }
                r9.<init>()     // Catch:{ Exception -> 0x0349 }
                r11 = r8[r7]     // Catch:{ Exception -> 0x0349 }
                r9.name = r11     // Catch:{ Exception -> 0x0349 }
                r11 = r8[r3]     // Catch:{ Exception -> 0x0349 }
                r9.code = r11     // Catch:{ Exception -> 0x0349 }
                r11 = r8[r4]     // Catch:{ Exception -> 0x0349 }
                r9.shortname = r11     // Catch:{ Exception -> 0x0349 }
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r11 = r1.countriesArray     // Catch:{ Exception -> 0x0349 }
                r11.add(r3, r9)     // Catch:{ Exception -> 0x0349 }
                java.util.HashMap<java.lang.String, org.telegram.ui.CountrySelectActivity$Country> r11 = r1.codesMap     // Catch:{ Exception -> 0x0349 }
                r12 = r8[r3]     // Catch:{ Exception -> 0x0349 }
                r11.put(r12, r9)     // Catch:{ Exception -> 0x0349 }
                int r9 = r8.length     // Catch:{ Exception -> 0x0349 }
                if (r9 <= r10) goto L_0x033d
                java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0349 }
                r11 = r8[r3]     // Catch:{ Exception -> 0x0349 }
                r12 = r8[r10]     // Catch:{ Exception -> 0x0349 }
                r9.put(r11, r12)     // Catch:{ Exception -> 0x0349 }
            L_0x033d:
                r9 = r8[r4]     // Catch:{ Exception -> 0x0349 }
                r8 = r8[r7]     // Catch:{ Exception -> 0x0349 }
                r5.put(r9, r8)     // Catch:{ Exception -> 0x0349 }
                goto L_0x0308
            L_0x0345:
                r0.close()     // Catch:{ Exception -> 0x0349 }
                goto L_0x034d
            L_0x0349:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x034d:
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r0 = r1.countriesArray
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10 r3 = org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10.INSTANCE
                java.util.Comparator r3 = j$.util.Comparator$CC.comparing(r3)
                java.util.Collections.sort(r0, r3)
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0363 }
                java.lang.String r3 = "phone"
                java.lang.Object r0 = r0.getSystemService(r3)     // Catch:{ Exception -> 0x0363 }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0363 }
                goto L_0x0367
            L_0x0363:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0367:
                org.telegram.tgnet.TLRPC$TL_help_getNearestDc r0 = new org.telegram.tgnet.TLRPC$TL_help_getNearestDc
                r0.<init>()
                org.telegram.messenger.AccountInstance r3 = r22.getAccountInstance()
                org.telegram.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13
                r7.<init>(r1, r5)
                r5 = 10
                r3.sendRequest(r0, r7, r5)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x039c
                android.widget.TextView r0 = r1.countryButton
                r3 = 2131624926(0x7f0e03de, float:1.8877045E38)
                java.lang.String r7 = "ChooseCountry"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r0.setText(r3)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r3 = 0
                r0.setHintText(r3)
                r1.countryState = r4
            L_0x039c:
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x03b3
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.requestFocus()
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                int r3 = r0.length()
                r0.setSelection(r3)
                goto L_0x03b8
            L_0x03b3:
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r0.requestFocus()
            L_0x03b8:
                org.telegram.tgnet.TLRPC$TL_help_getCountriesList r0 = new org.telegram.tgnet.TLRPC$TL_help_getCountriesList
                r0.<init>()
                r0.lang_code = r6
                org.telegram.tgnet.ConnectionsManager r2 = r22.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11 r3 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11
                r3.<init>(r1)
                r2.sendRequest(r0, r3, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view2) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true, this.countriesArray);
            countrySelectActivity.setCountrySelectActivityDelegate(new LoginActivity$PhoneView$$ExternalSyntheticLambda14(this));
            this.this$0.presentFragment(countrySelectActivity);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda6(this), 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$3(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$4(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$5(View view2, int i, KeyEvent keyEvent) {
            if (i != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(keyEvent);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(View view2) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.testBackend = !loginActivity.testBackend;
                ((CheckBoxCell) view2).setChecked(this.this$0.testBackend, true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$9(HashMap hashMap, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda8(this, tLObject, hashMap));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(TLObject tLObject, HashMap hashMap) {
            if (tLObject != null) {
                TLRPC$TL_nearestDc tLRPC$TL_nearestDc = (TLRPC$TL_nearestDc) tLObject;
                if (this.codeField.length() == 0) {
                    setCountry(hashMap, tLRPC$TL_nearestDc.country.toUpperCase());
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                        this.countriesArray.add(country);
                        this.codesMap.put(tLRPC$TL_help_country.country_codes.get(i2).country_code, country);
                        if (tLRPC$TL_help_country.country_codes.get(i2).patterns.size() > 0) {
                            this.phoneFormatMap.put(tLRPC$TL_help_country.country_codes.get(i2).country_code, tLRPC$TL_help_country.country_codes.get(i2).patterns.get(0));
                        }
                    }
                }
            }
        }

        public void selectCountry(CountrySelectActivity.Country country) {
            this.ignoreOnTextChange = true;
            String str = country.code;
            this.codeField.setText(str);
            this.countryButton.setText(country.name);
            String str2 = this.phoneFormatMap.get(str);
            this.phoneField.setHintText(str2 != null ? str2.replace('X', 8211) : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }

        private void setCountry(HashMap<String, String> hashMap, String str) {
            if (hashMap.get(str) != null) {
                CountrySelectActivity.Country country = null;
                int i = 0;
                while (true) {
                    if (i >= this.countriesArray.size()) {
                        break;
                    } else if (this.countriesArray.get(i).name.equals(str)) {
                        country = this.countriesArray.get(i);
                        break;
                    } else {
                        i++;
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

        public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesArray.get(i).code);
            this.ignoreOnTextChange = false;
        }

        public void onNextPressed() {
            boolean z;
            boolean z2;
            boolean z3;
            boolean z4;
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("sim status = " + telephonyManager.getSimState());
                }
                int simState = telephonyManager.getSimState();
                boolean z5 = (simState == 1 || simState == 0 || telephonyManager.getPhoneType() == 0 || AndroidUtilities.isAirplaneModeOn()) ? false : true;
                int i = Build.VERSION.SDK_INT;
                if (i < 23 || !z5) {
                    z3 = true;
                    z2 = true;
                    z = true;
                } else {
                    z2 = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    z = this.this$0.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    z3 = i < 28 || this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                    if (this.this$0.checkPermissions) {
                        this.this$0.permissionsItems.clear();
                        if (!z2) {
                            this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!z) {
                            this.this$0.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        if (!z3) {
                            this.this$0.permissionsItems.add("android.permission.READ_CALL_LOG");
                        }
                        if (!this.this$0.permissionsItems.isEmpty()) {
                            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                            if (globalMainSettings.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                globalMainSettings.edit().putBoolean("firstlogin", false).commit();
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                if (!z2 && (!z || !z3)) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndLog", NUM));
                                } else if (!z || !z3) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallLog", NUM));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                }
                                LoginActivity loginActivity = this.this$0;
                                Dialog unused = loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                            } else {
                                try {
                                    this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[0]), 6);
                                } catch (Exception unused2) {
                                    z4 = false;
                                }
                            }
                            z4 = true;
                            if (z4) {
                                return;
                            }
                        }
                    }
                }
                int i2 = this.countryState;
                if (i2 == 1) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ChooseCountry", NUM));
                } else if (i2 == 2 && !BuildVars.DEBUG_VERSION) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("WrongCountry", NUM));
                } else if (this.codeField.length() == 0) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (this.phoneField.length() == 0) {
                    this.this$0.onFieldError(this.phoneField);
                } else {
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    boolean z6 = BuildVars.DEBUG_PRIVATE_VERSION && this.this$0.getConnectionsManager().isTestBackend();
                    if (z6 != this.this$0.testBackend) {
                        this.this$0.getConnectionsManager().switchBackend(false);
                        z6 = this.this$0.testBackend;
                    }
                    if (this.this$0.getParentActivity() instanceof LaunchActivity) {
                        for (int i3 = 0; i3 < 3; i3++) {
                            UserConfig instance = UserConfig.getInstance(i3);
                            if (instance.isClientActivated() && PhoneNumberUtils.compare(stripExceptNumbers, instance.getCurrentUser().phone) && ConnectionsManager.getInstance(i3).isTestBackend() == z6) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder2.setTitle(LocaleController.getString("AppName", NUM));
                                builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", NUM));
                                builder2.setPositiveButton(LocaleController.getString("AccountSwitch", NUM), new LoginActivity$PhoneView$$ExternalSyntheticLambda0(this, i3));
                                builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                this.this$0.showDialog(builder2.create());
                                return;
                            }
                        }
                    }
                    ConnectionsManager.getInstance(this.this$0.currentAccount).cleanup(false);
                    TLRPC$TL_auth_sendCode tLRPC$TL_auth_sendCode = new TLRPC$TL_auth_sendCode();
                    tLRPC$TL_auth_sendCode.api_hash = BuildVars.APP_HASH;
                    tLRPC$TL_auth_sendCode.api_id = BuildVars.APP_ID;
                    tLRPC$TL_auth_sendCode.phone_number = stripExceptNumbers;
                    TLRPC$TL_codeSettings tLRPC$TL_codeSettings = new TLRPC$TL_codeSettings();
                    tLRPC$TL_auth_sendCode.settings = tLRPC$TL_codeSettings;
                    tLRPC$TL_codeSettings.allow_flashcall = z5 && z2 && z && z3;
                    tLRPC$TL_codeSettings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (tLRPC$TL_auth_sendCode.settings.allow_app_hash) {
                        sharedPreferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                    } else {
                        sharedPreferences.edit().remove("sms_hash").commit();
                    }
                    if (tLRPC$TL_auth_sendCode.settings.allow_flashcall) {
                        try {
                            String line1Number = telephonyManager.getLine1Number();
                            if (!TextUtils.isEmpty(line1Number)) {
                                tLRPC$TL_auth_sendCode.settings.current_number = PhoneNumberUtils.compare(stripExceptNumbers, line1Number);
                                TLRPC$TL_codeSettings tLRPC$TL_codeSettings2 = tLRPC$TL_auth_sendCode.settings;
                                if (!tLRPC$TL_codeSettings2.current_number) {
                                    tLRPC$TL_codeSettings2.allow_flashcall = false;
                                }
                            } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                tLRPC$TL_auth_sendCode.settings.allow_flashcall = false;
                            } else {
                                tLRPC$TL_auth_sendCode.settings.current_number = false;
                            }
                        } catch (Exception e) {
                            tLRPC$TL_auth_sendCode.settings.allow_flashcall = false;
                            FileLog.e((Throwable) e);
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        bundle.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        bundle.putString("ephone", "+" + stripExceptNumbers);
                    }
                    bundle.putString("phoneFormated", stripExceptNumbers);
                    this.nextPressed = true;
                    this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_sendCode, new LoginActivity$PhoneView$$ExternalSyntheticLambda12(this, bundle, tLRPC$TL_auth_sendCode), 27));
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$11(int i, DialogInterface dialogInterface, int i2) {
            if (UserConfig.selectedAccount != i) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(i, false);
            }
            this.this$0.finishFragment();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$13(Bundle bundle, TLRPC$TL_auth_sendCode tLRPC$TL_auth_sendCode, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda9(this, tLRPC$TL_error, bundle, tLObject, tLRPC$TL_auth_sendCode));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$12(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, TLRPC$TL_auth_sendCode tLRPC$TL_auth_sendCode) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str = tLRPC$TL_error.text;
                if (str != null) {
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        LoginActivity.needShowInvalidAlert(this.this$0, tLRPC$TL_auth_sendCode.phone_number, false);
                    } else if (tLRPC$TL_error.text.contains("PHONE_PASSWORD_FLOOD")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_FLOOD")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_NUMBER_BANNED")) {
                        LoginActivity.needShowInvalidAlert(this.this$0, tLRPC$TL_auth_sendCode.phone_number, true);
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.code != -1000) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                    }
                }
            }
            this.this$0.needHideProgress(false);
        }

        public void fillNumber() {
            boolean z;
            boolean z2;
            if (!this.numberFilled) {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager.getSimState() != 1 && telephonyManager.getPhoneType() != 0) {
                        String str = null;
                        if (Build.VERSION.SDK_INT >= 23) {
                            z = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                            if (this.this$0.checkShowPermissions && !z) {
                                this.this$0.permissionsShowItems.clear();
                                if (!z) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                                }
                                if (!this.this$0.permissionsShowItems.isEmpty()) {
                                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                                    if (!globalMainSettings.getBoolean("firstloginshow", true)) {
                                        if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                            this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsShowItems.toArray(new String[0]), 7);
                                            return;
                                        }
                                    }
                                    globalMainSettings.edit().putBoolean("firstloginshow", false).commit();
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                    builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                                    LoginActivity loginActivity = this.this$0;
                                    Dialog unused = loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create());
                                    boolean unused2 = this.this$0.needRequestPermissions = true;
                                    return;
                                }
                                return;
                            }
                        } else {
                            z = true;
                        }
                        this.numberFilled = true;
                        if (!this.this$0.newAccount && z) {
                            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(telephonyManager.getLine1Number());
                            if (!TextUtils.isEmpty(stripExceptNumbers)) {
                                int i = 4;
                                if (stripExceptNumbers.length() > 4) {
                                    while (true) {
                                        if (i < 1) {
                                            z2 = false;
                                            break;
                                        }
                                        String substring = stripExceptNumbers.substring(0, i);
                                        if (this.codesMap.get(substring) != null) {
                                            String substring2 = stripExceptNumbers.substring(i);
                                            this.codeField.setText(substring);
                                            str = substring2;
                                            z2 = true;
                                            break;
                                        }
                                        i--;
                                    }
                                    if (!z2) {
                                        str = stripExceptNumbers.substring(1);
                                        this.codeField.setText(stripExceptNumbers.substring(0, 1));
                                    }
                                }
                                if (str != null) {
                                    this.phoneField.requestFocus();
                                    this.phoneField.setText(str);
                                    HintEditText hintEditText = this.phoneField;
                                    hintEditText.setSelection(hintEditText.length());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell2 = this.checkBoxCell;
            if (checkBoxCell2 != null) {
                checkBoxCell2.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda7(this), 100);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$14() {
            if (this.phoneField == null) {
                return;
            }
            if (this.this$0.needRequestPermissions) {
                this.codeField.clearFocus();
                this.phoneField.clearFocus();
            } else if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                AndroidUtilities.showKeyboard(this.phoneField);
            } else {
                this.codeField.requestFocus();
                AndroidUtilities.showKeyboard(this.codeField);
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
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        /* access modifiers changed from: private */
        public ImageView blackImageView;
        /* access modifiers changed from: private */
        public RLottieImageView blueImageView;
        private String catchedPhone;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public int currentType;
        private String emailPhone;
        RLottieDrawable hintDrawable;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        /* access modifiers changed from: private */
        public int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        /* access modifiers changed from: private */
        public int openTime;
        private String pattern = "*";
        private String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        /* access modifiers changed from: private */
        public String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public int time = 60000;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        private final Object timerSync = new Object();
        /* access modifiers changed from: private */
        public TextView titleTextView;
        /* access modifiers changed from: private */
        public boolean waitingForEvent;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onBackPressed$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public boolean needBackButton() {
            return true;
        }

        static /* synthetic */ int access$5126(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = (double) loginActivitySmsView.codeTime;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$5726(LoginActivitySmsView loginActivitySmsView, double d) {
            double d2 = (double) loginActivitySmsView.time;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            loginActivitySmsView.time = i;
            return i;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.LoginActivity r25, android.content.Context r26, int r27) {
            /*
                r24 = this;
                r0 = r24
                r1 = r25
                r2 = r26
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
                java.lang.String r3 = "*"
                r0.pattern = r3
                r3 = r27
                r0.currentType = r3
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r8, r9)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.titleTextView = r4
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r4.setTextColor(r10)
                android.widget.TextView r4 = r0.titleTextView
                r10 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r10)
                android.widget.TextView r4 = r0.titleTextView
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r4.setTypeface(r10)
                android.widget.TextView r4 = r0.titleTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                r12 = 3
                if (r10 == 0) goto L_0x007b
                r10 = 5
                goto L_0x007c
            L_0x007b:
                r10 = 3
            L_0x007c:
                r4.setGravity(r10)
                android.widget.TextView r4 = r0.titleTextView
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r4.setLineSpacing(r10, r9)
                android.widget.TextView r4 = r0.titleTextView
                r10 = 49
                r4.setGravity(r10)
                int r4 = r0.currentType
                r13 = -2
                if (r4 != r12) goto L_0x012a
                android.widget.TextView r4 = r0.confirmTextView
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x009d
                r8 = 5
                goto L_0x009e
            L_0x009d:
                r8 = 3
            L_0x009e:
                r8 = r8 | 48
                r4.setGravity(r8)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x00ae
                r8 = 5
                goto L_0x00af
            L_0x00ae:
                r8 = 3
            L_0x00af:
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r8)
                r0.addView(r4, r8)
                android.widget.ImageView r8 = new android.widget.ImageView
                r8.<init>(r2)
                r14 = 2131165934(0x7var_ee, float:1.79461E38)
                r8.setImageResource(r14)
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x00f9
                r15 = 64
                r16 = 1117257728(0x42980000, float:76.0)
                r17 = 19
                r18 = 1073741824(0x40000000, float:2.0)
                r19 = 1073741824(0x40000000, float:2.0)
                r20 = 0
                r21 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r4.addView(r8, r14)
                android.widget.TextView r8 = r0.confirmTextView
                r14 = -1
                r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r16 = org.telegram.messenger.LocaleController.isRTL
                if (r16 == 0) goto L_0x00e6
                r16 = 5
                goto L_0x00e8
            L_0x00e6:
                r16 = 3
            L_0x00e8:
                r17 = 1118044160(0x42a40000, float:82.0)
                r18 = 0
                r19 = 0
                r20 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r4.addView(r8, r14)
                goto L_0x022f
            L_0x00f9:
                android.widget.TextView r15 = r0.confirmTextView
                r16 = -1
                r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                if (r14 == 0) goto L_0x0104
                r18 = 5
                goto L_0x0106
            L_0x0104:
                r18 = 3
            L_0x0106:
                r19 = 0
                r20 = 0
                r21 = 1118044160(0x42a40000, float:82.0)
                r22 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r4.addView(r15, r14)
                r16 = 64
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 21
                r20 = 1073741824(0x40000000, float:2.0)
                r21 = 0
                r22 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r4.addView(r8, r14)
                goto L_0x022f
            L_0x012a:
                android.widget.TextView r4 = r0.confirmTextView
                r4.setGravity(r10)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r10)
                r0.addView(r4, r14)
                int r14 = r0.currentType
                java.lang.String r15 = "chats_actionBackground"
                if (r14 != r3) goto L_0x01aa
                android.widget.ImageView r14 = new android.widget.ImageView
                r14.<init>(r2)
                r0.blackImageView = r14
                r11 = 2131166081(0x7var_, float:1.7946397E38)
                r14.setImageResource(r11)
                android.widget.ImageView r11 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r14 = new android.graphics.PorterDuffColorFilter
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
                r14.<init>(r8, r10)
                r11.setColorFilter(r14)
                android.widget.ImageView r8 = r0.blackImageView
                r17 = -2
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 51
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                org.telegram.ui.Components.RLottieImageView r8 = new org.telegram.ui.Components.RLottieImageView
                r8.<init>(r2)
                r0.blueImageView = r8
                r10 = 2131166079(0x7var_f, float:1.7946393E38)
                r8.setImageResource(r10)
                org.telegram.ui.Components.RLottieImageView r8 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
                r10.<init>(r11, r14)
                r8.setColorFilter(r10)
                org.telegram.ui.Components.RLottieImageView r8 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                android.widget.TextView r4 = r0.titleTextView
                r8 = 2131627608(0x7f0e0e58, float:1.8882485E38)
                java.lang.String r10 = "SentAppCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r4.setText(r8)
                goto L_0x020d
            L_0x01aa:
                org.telegram.ui.Components.RLottieImageView r8 = new org.telegram.ui.Components.RLottieImageView
                r8.<init>(r2)
                r0.blueImageView = r8
                org.telegram.ui.Components.RLottieDrawable r8 = new org.telegram.ui.Components.RLottieDrawable
                r18 = 2131558483(0x7f0d0053, float:1.8742283E38)
                r10 = 1115684864(0x42800000, float:64.0)
                int r20 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r21 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r22 = 1
                r23 = 0
                java.lang.String r19 = "NUM"
                r17 = r8
                r17.<init>((int) r18, (java.lang.String) r19, (int) r20, (int) r21, (boolean) r22, (int[]) r23)
                r0.hintDrawable = r8
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                java.lang.String r11 = "Bubble.**"
                r8.setLayerColor(r11, r10)
                org.telegram.ui.Components.RLottieDrawable r8 = r0.hintDrawable
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                java.lang.String r11 = "Phone.**"
                r8.setLayerColor(r11, r10)
                org.telegram.ui.Components.RLottieImageView r8 = r0.blueImageView
                org.telegram.ui.Components.RLottieDrawable r10 = r0.hintDrawable
                r8.setAnimation(r10)
                org.telegram.ui.Components.RLottieImageView r8 = r0.blueImageView
                r17 = 64
                r18 = 1115684864(0x42800000, float:64.0)
                r19 = 51
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                android.widget.TextView r4 = r0.titleTextView
                r8 = 2131627612(0x7f0e0e5c, float:1.8882493E38)
                java.lang.String r10 = "SentSmsCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r4.setText(r8)
            L_0x020d:
                android.widget.TextView r4 = r0.titleTextView
                r17 = -2
                r18 = -2
                r19 = 49
                r20 = 0
                r21 = 18
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r8)
                android.widget.TextView r4 = r0.confirmTextView
                r21 = 17
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r8)
            L_0x022f:
                android.widget.LinearLayout r4 = new android.widget.LinearLayout
                r4.<init>(r2)
                r0.codeFieldContainer = r4
                r8 = 0
                r4.setOrientation(r8)
                android.widget.LinearLayout r4 = r0.codeFieldContainer
                r10 = 36
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r10, (int) r3)
                r0.addView(r4, r10)
                int r4 = r0.currentType
                if (r4 != r12) goto L_0x0250
                android.widget.LinearLayout r4 = r0.codeFieldContainer
                r10 = 8
                r4.setVisibility(r10)
            L_0x0250:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$1 r4 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$1
                r4.<init>(r0, r2, r1)
                r0.timeText = r4
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.timeText
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r9)
                int r4 = r0.currentType
                r5 = 1097859072(0x41700000, float:15.0)
                r10 = 1092616192(0x41200000, float:10.0)
                if (r4 != r12) goto L_0x02ae
                android.widget.TextView r4 = r0.timeText
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.timeText
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x027d
                r6 = 5
                goto L_0x027e
            L_0x027d:
                r6 = 3
            L_0x027e:
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r6)
                r0.addView(r4, r6)
                org.telegram.ui.LoginActivity$ProgressView r4 = new org.telegram.ui.LoginActivity$ProgressView
                r4.<init>(r2)
                r0.progressView = r4
                android.widget.TextView r4 = r0.timeText
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0294
                r11 = 5
                goto L_0x0295
            L_0x0294:
                r11 = 3
            L_0x0295:
                r4.setGravity(r11)
                org.telegram.ui.LoginActivity$ProgressView r4 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
                r0.addView(r4, r6)
                goto L_0x02d0
            L_0x02ae:
                android.widget.TextView r4 = r0.timeText
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r4.setPadding(r8, r6, r8, r11)
                android.widget.TextView r4 = r0.timeText
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.timeText
                r6 = 49
                r4.setGravity(r6)
                android.widget.TextView r4 = r0.timeText
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r6)
                r0.addView(r4, r11)
            L_0x02d0:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$2 r4 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$2
                r4.<init>(r0, r2, r1)
                r0.problemText = r4
                java.lang.String r1 = "windowBackgroundWhiteBlueText4"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r4.setTextColor(r1)
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r9)
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r1.setPadding(r8, r2, r8, r4)
                android.widget.TextView r1 = r0.problemText
                r1.setTextSize(r3, r5)
                android.widget.TextView r1 = r0.problemText
                r2 = 49
                r1.setGravity(r2)
                int r1 = r0.currentType
                if (r1 != r3) goto L_0x0316
                android.widget.TextView r1 = r0.problemText
                r2 = 2131625230(0x7f0e050e, float:1.8877662E38)
                java.lang.String r3 = "DidNotGetTheCodeSms"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0324
            L_0x0316:
                android.widget.TextView r1 = r0.problemText
                r2 = 2131625229(0x7f0e050d, float:1.887766E38)
                java.lang.String r3 = "DidNotGetTheCode"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x0324:
                android.widget.TextView r1 = r0.problemText
                r2 = 49
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r2)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.problemText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.nextPressed) {
                int i = this.nextType;
                if ((i == 4 && this.currentType == 2) || i == 0) {
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                        Intent intent = new Intent("android.intent.action.SENDTO");
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra("android.intent.extra.EMAIL", new String[]{"reports@stel.com"});
                        intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + this.emailPhone);
                        intent.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                    } catch (Exception unused) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
                    }
                } else if (this.this$0.doneProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            RLottieImageView rLottieImageView;
            super.onMeasure(i, i2);
            if (this.currentType != 3 && (rLottieImageView = this.blueImageView) != null) {
                int measuredHeight = rLottieImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int dp = AndroidUtilities.dp(80.0f);
                int dp2 = AndroidUtilities.dp(291.0f);
                if (this.this$0.scrollHeight - measuredHeight < dp) {
                    setMeasuredDimension(getMeasuredWidth(), measuredHeight + dp);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), Math.min(this.this$0.scrollHeight, dp2));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            super.onLayout(z, i, i2, i3, i4);
            if (this.currentType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int measuredHeight = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int measuredHeight2 = this.problemText.getMeasuredHeight();
                    i5 = (measuredHeight + bottom) - measuredHeight2;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), i5, this.problemText.getRight(), measuredHeight2 + i5);
                } else if (this.timeText.getVisibility() == 0) {
                    int measuredHeight3 = this.timeText.getMeasuredHeight();
                    i5 = (measuredHeight + bottom) - measuredHeight3;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), i5, this.timeText.getRight(), measuredHeight3 + i5);
                } else {
                    i5 = measuredHeight + bottom;
                }
                int measuredHeight4 = this.codeFieldContainer.getMeasuredHeight();
                int i6 = (((i5 - bottom) - measuredHeight4) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), i6, this.codeFieldContainer.getRight(), measuredHeight4 + i6);
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this, bundle), 10);
            this.this$0.needShowProgress(0);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$2(Bundle bundle, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(this, tLRPC$TL_error, bundle, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$1(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject);
            } else {
                String str = tLRPC$TL_error.text;
                if (str != null) {
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                    } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        onBackPressed(true);
                        this.this$0.setPage(0, true, (Bundle) null, true);
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                    } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error.code != -1000) {
                        LoginActivity loginActivity = this.this$0;
                        String string = LocaleController.getString("AppName", NUM);
                        loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text);
                    }
                }
            }
            this.this$0.needHideProgress(false);
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            int i;
            String string;
            int i2;
            Bundle bundle2 = bundle;
            if (bundle2 != null) {
                this.waitingForEvent = true;
                int i3 = this.currentType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle2;
                this.phone = bundle2.getString("phone");
                this.emailPhone = bundle2.getString("ephone");
                this.requestPhone = bundle2.getString("phoneFormated");
                this.phoneHash = bundle2.getString("phoneHash");
                this.time = bundle2.getInt("timeout");
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle2.getInt("nextType");
                this.pattern = bundle2.getString("pattern");
                int i4 = bundle2.getInt("length");
                this.length = i4;
                if (i4 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                CharSequence charSequence = "";
                int i5 = 8;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    int i6 = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (i6 >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[i6].setText(charSequence);
                        i6++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    final int i7 = 0;
                    while (i7 < this.length) {
                        this.codeField[i7] = new EditTextBoldCursor(getContext());
                        this.codeField[i7].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[i7].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[i7].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[i7].setCursorWidth(1.5f);
                        Drawable mutate = getResources().getDrawable(NUM).mutate();
                        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), PorterDuff.Mode.MULTIPLY));
                        this.codeField[i7].setBackgroundDrawable(mutate);
                        this.codeField[i7].setImeOptions(NUM);
                        this.codeField[i7].setTextSize(1, 20.0f);
                        this.codeField[i7].setMaxLines(1);
                        this.codeField[i7].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[i7].setPadding(0, 0, 0, 0);
                        this.codeField[i7].setGravity(49);
                        if (this.currentType == 3) {
                            this.codeField[i7].setEnabled(false);
                            this.codeField[i7].setInputType(0);
                            this.codeField[i7].setVisibility(8);
                        } else {
                            this.codeField[i7].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[i7], LayoutHelper.createLinear(34, 36, 1, 0, 0, i7 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[i7].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void afterTextChanged(Editable editable) {
                                int length;
                                if (!LoginActivitySmsView.this.ignoreOnTextChange && (length = editable.length()) >= 1) {
                                    if (length > 1) {
                                        String obj = editable.toString();
                                        boolean unused = LoginActivitySmsView.this.ignoreOnTextChange = true;
                                        for (int i = 0; i < Math.min(LoginActivitySmsView.this.length - i7, length); i++) {
                                            if (i == 0) {
                                                editable.replace(0, length, obj.substring(i, i + 1));
                                            } else {
                                                LoginActivitySmsView.this.codeField[i7 + i].setText(obj.substring(i, i + 1));
                                            }
                                        }
                                        boolean unused2 = LoginActivitySmsView.this.ignoreOnTextChange = false;
                                    }
                                    if (i7 != LoginActivitySmsView.this.length - 1) {
                                        LoginActivitySmsView.this.codeField[i7 + 1].setSelection(LoginActivitySmsView.this.codeField[i7 + 1].length());
                                        LoginActivitySmsView.this.codeField[i7 + 1].requestFocus();
                                    }
                                    if ((i7 == LoginActivitySmsView.this.length - 1 || (i7 == LoginActivitySmsView.this.length - 2 && length >= 2)) && LoginActivitySmsView.this.getCode().length() == LoginActivitySmsView.this.length) {
                                        LoginActivitySmsView.this.onNextPressed();
                                    }
                                }
                            }
                        });
                        this.codeField[i7].setOnKeyListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this, i7));
                        this.codeField[i7].setOnEditorActionListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(this));
                        i7++;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String format = PhoneFormat.getInstance().format(this.phone);
                    int i8 = this.currentType;
                    if (i8 == 1) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
                    } else if (i8 == 2) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i8 == 3) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i8 == 4) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(format)));
                    }
                    this.confirmTextView.setText(charSequence);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    int i9 = this.currentType;
                    if (i9 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                        return;
                    }
                    String str = null;
                    if (i9 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i10 = this.nextType;
                        if (i10 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, 1, 0));
                        } else if (i10 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, 1, 0));
                        }
                        if (z) {
                            str = AndroidUtilities.obtainLoginPhoneCall(this.pattern);
                        }
                        if (str != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(str);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                            return;
                        }
                        String str2 = this.catchedPhone;
                        if (str2 != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(str2);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                            return;
                        }
                        createTimer();
                    } else if (i9 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView = this.timeText;
                        if (this.time >= 1000) {
                            i5 = 0;
                        }
                        textView.setVisibility(i5);
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        String string2 = sharedPreferences.getString("sms_hash", (String) null);
                        if (!TextUtils.isEmpty(string2) && (string = sharedPreferences.getString("sms_hash_code", (String) null)) != null) {
                            if (string.contains(string2 + "|")) {
                                str = string.substring(string.indexOf(124) + 1);
                            }
                        }
                        if (str != null) {
                            this.codeField[0].setText(str);
                            onNextPressed();
                            return;
                        }
                        createTimer();
                    } else if (i9 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView2 = this.timeText;
                        if (this.time >= 1000) {
                            i5 = 0;
                        }
                        textView2.setVisibility(i5);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setParams$3(int i, View view, int i2, KeyEvent keyEvent) {
            if (i2 != 67 || this.codeField[i].length() != 0 || i <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            int i3 = i - 1;
            editTextBoldCursorArr[i3].setSelection(editTextBoldCursorArr[i3].length());
            this.codeField[i3].requestFocus();
            this.codeField[i3].dispatchKeyEvent(keyEvent);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setParams$4(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$4$$ExternalSyntheticLambda0(this));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$5000 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                        LoginActivitySmsView.access$5126(LoginActivitySmsView.this, currentTimeMillis - access$5000);
                        if (LoginActivitySmsView.this.codeTime <= 1000) {
                            LoginActivitySmsView.this.problemText.setVisibility(0);
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
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.resetProgressAnimation();
                }
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$2() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$5600 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTimeMillis);
                        double unused = LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                        LoginActivitySmsView.access$5726(LoginActivitySmsView.this, currentTimeMillis - access$5600);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int access$5700 = (LoginActivitySmsView.this.time / 1000) / 60;
                            int access$57002 = (LoginActivitySmsView.this.time / 1000) - (access$5700 * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(access$5700), Integer.valueOf(access$57002)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(access$5700), Integer.valueOf(access$57002)));
                            }
                            if (LoginActivitySmsView.this.progressView != null && !LoginActivitySmsView.this.progressView.isProgressAnimationRunning()) {
                                LoginActivitySmsView.this.progressView.startProgressAnimation(((long) LoginActivitySmsView.this.time) - 1000);
                                return;
                            }
                            return;
                        }
                        LoginActivitySmsView.this.destroyTimer();
                        if (LoginActivitySmsView.this.currentType == 3) {
                            AndroidUtilities.setWaitingForCall(false);
                            NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                            boolean unused2 = LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        } else if (LoginActivitySmsView.this.currentType != 2 && LoginActivitySmsView.this.currentType != 4) {
                        } else {
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2) {
                                if (LoginActivitySmsView.this.nextType == 4) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", NUM));
                                } else {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("SendingSms", NUM));
                                }
                                LoginActivitySmsView.this.createCodeTimer();
                                TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
                                tLRPC$TL_auth_resendCode.phone_number = LoginActivitySmsView.this.requestPhone;
                                tLRPC$TL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda2(this), 10);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                boolean unused3 = LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        if (tLRPC$TL_error != null && tLRPC$TL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda1(this, tLRPC$TL_error));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error) {
                        String unused = LoginActivitySmsView.this.lastError = tLRPC$TL_error.text;
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyTimer() {
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

        /* access modifiers changed from: private */
        public String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    return sb.toString();
                }
                sb.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[i].getText().toString()));
                i++;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed && this.this$0.currentViewNum >= 1 && this.this$0.currentViewNum <= 4) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    this.this$0.onFieldError(this.codeFieldContainer);
                    return;
                }
                this.nextPressed = true;
                int i = this.currentType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn = new TLRPC$TL_auth_signIn();
                tLRPC$TL_auth_signIn.phone_number = this.requestPhone;
                tLRPC$TL_auth_signIn.phone_code = code;
                tLRPC$TL_auth_signIn.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_signIn, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(this, tLRPC$TL_auth_signIn), 10), false);
                this.this$0.showDoneButton(true, true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$8(TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0192  */
        /* JADX WARNING: Removed duplicated region for block: B:59:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onNextPressed$7(org.telegram.tgnet.TLRPC$TL_error r6, org.telegram.tgnet.TLObject r7, org.telegram.tgnet.TLRPC$TL_auth_signIn r8) {
            /*
                r5 = this;
                r0 = 3
                r1 = 0
                r2 = 1
                if (r6 != 0) goto L_0x004a
                r5.nextPressed = r1
                org.telegram.ui.LoginActivity r6 = r5.this$0
                r6.showDoneButton(r1, r2)
                r5.destroyTimer()
                r5.destroyCodeTimer()
                boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired
                if (r6 == 0) goto L_0x0042
                org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired r7 = (org.telegram.tgnet.TLRPC$TL_auth_authorizationSignUpRequired) r7
                org.telegram.tgnet.TLRPC$TL_help_termsOfService r6 = r7.terms_of_service
                if (r6 == 0) goto L_0x0021
                org.telegram.ui.LoginActivity r7 = r5.this$0
                org.telegram.tgnet.TLRPC$TL_help_termsOfService unused = r7.currentTermsOfService = r6
            L_0x0021:
                android.os.Bundle r6 = new android.os.Bundle
                r6.<init>()
                java.lang.String r7 = r5.requestPhone
                java.lang.String r3 = "phoneFormated"
                r6.putString(r3, r7)
                java.lang.String r7 = r5.phoneHash
                java.lang.String r3 = "phoneHash"
                r6.putString(r3, r7)
                java.lang.String r7 = r8.phone_code
                java.lang.String r8 = "code"
                r6.putString(r8, r7)
                org.telegram.ui.LoginActivity r7 = r5.this$0
                r8 = 5
                r7.setPage(r8, r2, r6, r1)
                goto L_0x0075
            L_0x0042:
                org.telegram.ui.LoginActivity r6 = r5.this$0
                org.telegram.tgnet.TLRPC$TL_auth_authorization r7 = (org.telegram.tgnet.TLRPC$TL_auth_authorization) r7
                r6.onAuthSuccess(r7)
                goto L_0x0075
            L_0x004a:
                java.lang.String r7 = r6.text
                r5.lastError = r7
                java.lang.String r3 = "SESSION_PASSWORD_NEEDED"
                boolean r7 = r7.contains(r3)
                if (r7 == 0) goto L_0x0078
                org.telegram.tgnet.TLRPC$TL_account_getPassword r6 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
                r6.<init>()
                org.telegram.ui.LoginActivity r7 = r5.this$0
                int r7 = r7.currentAccount
                org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10
                r1.<init>(r5, r8)
                r8 = 10
                r7.sendRequest(r6, r1, r8)
                r5.destroyTimer()
                r5.destroyCodeTimer()
            L_0x0075:
                r1 = 1
                goto L_0x018c
            L_0x0078:
                r5.nextPressed = r1
                org.telegram.ui.LoginActivity r7 = r5.this$0
                r7.showDoneButton(r1, r2)
                int r7 = r5.currentType
                r8 = 4
                r3 = 2
                if (r7 != r0) goto L_0x008b
                int r4 = r5.nextType
                if (r4 == r8) goto L_0x0099
                if (r4 == r3) goto L_0x0099
            L_0x008b:
                if (r7 != r3) goto L_0x0093
                int r4 = r5.nextType
                if (r4 == r8) goto L_0x0099
                if (r4 == r0) goto L_0x0099
            L_0x0093:
                if (r7 != r8) goto L_0x009c
                int r7 = r5.nextType
                if (r7 != r3) goto L_0x009c
            L_0x0099:
                r5.createTimer()
            L_0x009c:
                int r7 = r5.currentType
                if (r7 != r3) goto L_0x00ad
                org.telegram.messenger.AndroidUtilities.setWaitingForSms(r2)
                org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r8 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode
                r7.addObserver(r5, r8)
                goto L_0x00bb
            L_0x00ad:
                if (r7 != r0) goto L_0x00bb
                org.telegram.messenger.AndroidUtilities.setWaitingForCall(r2)
                org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r8 = org.telegram.messenger.NotificationCenter.didReceiveCall
                r7.addObserver(r5, r8)
            L_0x00bb:
                r5.waitingForEvent = r2
                int r7 = r5.currentType
                if (r7 == r0) goto L_0x018c
                java.lang.String r7 = r6.text
                java.lang.String r8 = "PHONE_NUMBER_INVALID"
                boolean r7 = r7.contains(r8)
                r8 = 2131624288(0x7f0e0160, float:1.8875751E38)
                java.lang.String r3 = "AppName"
                if (r7 == 0) goto L_0x00e4
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r8)
                r8 = 2131625933(0x7f0e07cd, float:1.8879088E38)
                java.lang.String r2 = "InvalidPhoneNumber"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x018c
            L_0x00e4:
                java.lang.String r7 = r6.text
                java.lang.String r4 = "PHONE_CODE_EMPTY"
                boolean r7 = r7.contains(r4)
                if (r7 != 0) goto L_0x0165
                java.lang.String r7 = r6.text
                java.lang.String r4 = "PHONE_CODE_INVALID"
                boolean r7 = r7.contains(r4)
                if (r7 == 0) goto L_0x00f9
                goto L_0x0165
            L_0x00f9:
                java.lang.String r7 = r6.text
                java.lang.String r4 = "PHONE_CODE_EXPIRED"
                boolean r7 = r7.contains(r4)
                if (r7 == 0) goto L_0x011f
                r5.onBackPressed(r2)
                org.telegram.ui.LoginActivity r6 = r5.this$0
                r7 = 0
                r6.setPage(r1, r2, r7, r2)
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r8)
                r8 = 2131624972(0x7f0e040c, float:1.8877139E38)
                java.lang.String r2 = "CodeExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x018c
            L_0x011f:
                java.lang.String r7 = r6.text
                java.lang.String r2 = "FLOOD_WAIT"
                boolean r7 = r7.startsWith(r2)
                if (r7 == 0) goto L_0x013c
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r8)
                r8 = 2131625626(0x7f0e069a, float:1.8878465E38)
                java.lang.String r2 = "FloodWait"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r8)
                r6.needShowAlert(r7, r8)
                goto L_0x018c
            L_0x013c:
                org.telegram.ui.LoginActivity r7 = r5.this$0
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r3, r8)
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r3 = 2131625383(0x7f0e05a7, float:1.8877972E38)
                java.lang.String r4 = "ErrorOccurred"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.append(r3)
                java.lang.String r3 = "\n"
                r2.append(r3)
                java.lang.String r6 = r6.text
                r2.append(r6)
                java.lang.String r6 = r2.toString()
                r7.needShowAlert(r8, r6)
                goto L_0x018c
            L_0x0165:
                org.telegram.ui.LoginActivity r6 = r5.this$0
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r8)
                r8 = 2131625930(0x7f0e07ca, float:1.8879082E38)
                java.lang.String r2 = "InvalidCode"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r8)
                r6.needShowAlert(r7, r8)
                r6 = 0
            L_0x0178:
                org.telegram.ui.Components.EditTextBoldCursor[] r7 = r5.codeField
                int r8 = r7.length
                if (r6 >= r8) goto L_0x0187
                r7 = r7[r6]
                java.lang.String r8 = ""
                r7.setText(r8)
                int r6 = r6 + 1
                goto L_0x0178
            L_0x0187:
                r6 = r7[r1]
                r6.requestFocus()
            L_0x018c:
                if (r1 == 0) goto L_0x0195
                int r6 = r5.currentType
                if (r6 != r0) goto L_0x0195
                org.telegram.messenger.AndroidUtilities.endIncomingCall()
            L_0x0195:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.lambda$onNextPressed$7(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_auth_signIn):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$6(TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7(this, tLRPC$TL_error, tLObject, tLRPC$TL_auth_signIn));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$5(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn) {
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
                this.this$0.setPage(6, true, bundle, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }

        public boolean onBackPressed(boolean z) {
            if (!z) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("StopVerification", NUM));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda0(this));
                this.this$0.showDialog(builder.create());
                return false;
            }
            this.nextPressed = false;
            this.this$0.needHideProgress(true);
            TLRPC$TL_auth_cancelCode tLRPC$TL_auth_cancelCode = new TLRPC$TL_auth_cancelCode();
            tLRPC$TL_auth_cancelCode.phone_number = this.requestPhone;
            tLRPC$TL_auth_cancelCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_cancelCode, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda11.INSTANCE, 10);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$9(DialogInterface dialogInterface, int i) {
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
            if (this.currentType != 3) {
                RLottieDrawable rLottieDrawable = this.hintDrawable;
                if (rLottieDrawable != null) {
                    rLottieDrawable.setCurrentFrame(0);
                }
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this), 100);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$11() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                int length2 = editTextBoldCursorArr.length - 1;
                while (true) {
                    if (length2 < 0) {
                        break;
                    } else if (length2 == 0 || this.codeField[length2].length() != 0) {
                        this.codeField[length2].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        editTextBoldCursorArr2[length2].setSelection(editTextBoldCursorArr2[length2].length());
                        AndroidUtilities.showKeyboard(this.codeField[length2]);
                    } else {
                        length2--;
                    }
                }
                this.codeField[length2].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr22 = this.codeField;
                editTextBoldCursorArr22[length2].setSelection(editTextBoldCursorArr22[length2].length());
                AndroidUtilities.showKeyboard(this.codeField[length2]);
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            EditTextBoldCursor[] editTextBoldCursorArr;
            if (this.waitingForEvent && (editTextBoldCursorArr = this.codeField) != null) {
                if (i == NotificationCenter.didReceiveSmsCode) {
                    editTextBoldCursorArr[0].setText("" + objArr[0]);
                    onNextPressed();
                } else if (i == NotificationCenter.didReceiveCall) {
                    String str = "" + objArr[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, str)) {
                        if (!this.pattern.equals("*")) {
                            this.catchedPhone = str;
                            AndroidUtilities.endIncomingCall();
                        }
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(str);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = getCode();
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
            EditTextBoldCursor[] editTextBoldCursorArr;
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
            if (!(string2 == null || (editTextBoldCursorArr = this.codeField) == null)) {
                editTextBoldCursorArr[0].setText(string2);
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
        /* access modifiers changed from: private */
        public TextView cancelButton;
        /* access modifiers changed from: private */
        public EditTextBoldCursor codeField;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public TLRPC$TL_account_password currentPassword;
        private boolean nextPressed;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        /* access modifiers changed from: private */
        public TextView resetAccountButton;
        /* access modifiers changed from: private */
        public TextView resetAccountText;
        final /* synthetic */ LoginActivity this$0;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityPasswordView(org.telegram.ui.LoginActivity r20, android.content.Context r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                r2 = r21
                r0.this$0 = r1
                r0.<init>(r2)
                r1 = 1
                r0.setOrientation(r1)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.confirmTextView = r3
                java.lang.String r4 = "windowBackgroundWhiteGrayText6"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setTextColor(r5)
                android.widget.TextView r3 = r0.confirmTextView
                r5 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 5
                r8 = 3
                if (r6 == 0) goto L_0x0030
                r6 = 5
                goto L_0x0031
            L_0x0030:
                r6 = 3
            L_0x0031:
                r3.setGravity(r6)
                android.widget.TextView r3 = r0.confirmTextView
                r6 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r3.setLineSpacing(r9, r10)
                android.widget.TextView r3 = r0.confirmTextView
                r9 = 2131626117(0x7f0e0885, float:1.8879461E38)
                java.lang.String r11 = "LoginPasswordText"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r3.setText(r9)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0058
                r9 = 5
                goto L_0x0059
            L_0x0058:
                r9 = 3
            L_0x0059:
                r11 = -2
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r9)
                r0.addView(r3, r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
                r3.<init>(r2)
                r0.codeField = r3
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r3.setTextColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r3.setCursorColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 1101004800(0x41a00000, float:20.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r3.setCursorSize(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                r3.setCursorWidth(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                java.lang.String r9 = "windowBackgroundWhiteHintText"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r3.setHintTextColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r9 = 0
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r9)
                r3.setBackgroundDrawable(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r12 = 2131626116(0x7f0e0884, float:1.887946E38)
                java.lang.String r13 = "LoginPassword"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r3.setHint(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r12 = 268435461(0x10000005, float:2.5243564E-29)
                r3.setImeOptions(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r12 = 1099956224(0x41900000, float:18.0)
                r3.setTextSize(r1, r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setMaxLines(r1)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setPadding(r9, r9, r9, r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r12 = 129(0x81, float:1.81E-43)
                r3.setInputType(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                android.text.method.PasswordTransformationMethod r12 = android.text.method.PasswordTransformationMethod.getInstance()
                r3.setTransformationMethod(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
                r3.setTypeface(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x00e7
                r12 = 5
                goto L_0x00e8
            L_0x00e7:
                r12 = 3
            L_0x00e8:
                r3.setGravity(r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r12 = -1
                r13 = 36
                r14 = 1
                r15 = 0
                r16 = 20
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r3, r12)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4 r12 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4
                r12.<init>(r0)
                r3.setOnEditorActionListener(r12)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.cancelButton = r3
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0116
                r12 = 5
                goto L_0x0117
            L_0x0116:
                r12 = 3
            L_0x0117:
                r12 = r12 | 48
                r3.setGravity(r12)
                android.widget.TextView r3 = r0.cancelButton
                java.lang.String r12 = "windowBackgroundWhiteBlueText4"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r3.setTextColor(r12)
                android.widget.TextView r3 = r0.cancelButton
                r12 = 2131625639(0x7f0e06a7, float:1.8878492E38)
                java.lang.String r13 = "ForgotPassword"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r3.setText(r12)
                android.widget.TextView r3 = r0.cancelButton
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.cancelButton
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r12 = (float) r12
                r3.setLineSpacing(r12, r10)
                android.widget.TextView r3 = r0.cancelButton
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3.setPadding(r9, r12, r9, r9)
                android.widget.TextView r3 = r0.cancelButton
                r12 = -1
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x0156
                r13 = 5
                goto L_0x0157
            L_0x0156:
                r13 = 3
            L_0x0157:
                r13 = r13 | 48
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r11, (int) r13)
                r0.addView(r3, r11)
                android.widget.TextView r3 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2
                r11.<init>(r0)
                r3.setOnClickListener(r11)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountButton = r3
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0177
                r11 = 5
                goto L_0x0178
            L_0x0177:
                r11 = 3
            L_0x0178:
                r11 = r11 | 48
                r3.setGravity(r11)
                android.widget.TextView r3 = r0.resetAccountButton
                java.lang.String r11 = "windowBackgroundWhiteRedText6"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r3.setTextColor(r11)
                android.widget.TextView r3 = r0.resetAccountButton
                r11 = 8
                r3.setVisibility(r11)
                android.widget.TextView r3 = r0.resetAccountButton
                r12 = 2131627413(0x7f0e0d95, float:1.888209E38)
                java.lang.String r13 = "ResetMyAccount"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r3.setText(r12)
                android.widget.TextView r3 = r0.resetAccountButton
                java.lang.String r12 = "fonts/rmedium.ttf"
                android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
                r3.setTypeface(r12)
                android.widget.TextView r3 = r0.resetAccountButton
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.resetAccountButton
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r12 = (float) r12
                r3.setLineSpacing(r12, r10)
                android.widget.TextView r3 = r0.resetAccountButton
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3.setPadding(r9, r12, r9, r9)
                android.widget.TextView r3 = r0.resetAccountButton
                r12 = -2
                r13 = -2
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x01ca
                r9 = 5
                goto L_0x01cb
            L_0x01ca:
                r9 = 3
            L_0x01cb:
                r14 = r9 | 48
                r15 = 0
                r16 = 34
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r3, r9)
                android.widget.TextView r3 = r0.resetAccountButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3 r9 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3
                r9.<init>(r0)
                r3.setOnClickListener(r9)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountText = r3
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x01f2
                r2 = 5
                goto L_0x01f3
            L_0x01f2:
                r2 = 3
            L_0x01f3:
                r2 = r2 | 48
                r3.setGravity(r2)
                android.widget.TextView r2 = r0.resetAccountText
                r2.setVisibility(r11)
                android.widget.TextView r2 = r0.resetAccountText
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r2.setTextColor(r3)
                android.widget.TextView r2 = r0.resetAccountText
                r3 = 2131627414(0x7f0e0d96, float:1.8882092E38)
                java.lang.String r4 = "ResetMyAccountText"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.setText(r3)
                android.widget.TextView r2 = r0.resetAccountText
                r2.setTextSize(r1, r5)
                android.widget.TextView r1 = r0.resetAccountText
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r10)
                android.widget.TextView r1 = r0.resetAccountText
                r9 = -2
                r10 = -2
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x022c
                goto L_0x022d
            L_0x022c:
                r7 = 3
            L_0x022d:
                r11 = r7 | 48
                r12 = 0
                r13 = 7
                r14 = 0
                r15 = 14
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r0.addView(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                if (this.currentPassword.has_recovery) {
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda14(this), 10);
                    return;
                }
                this.resetAccountText.setVisibility(0);
                this.resetAccountButton.setVisibility(0);
                AndroidUtilities.hideKeyboard(this.codeField);
                this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEitle", NUM), LocaleController.getString("RestorePasswordNoEmailText", NUM));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            String str;
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery = (TLRPC$TL_auth_passwordRecovery) tLObject;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, tLRPC$TL_auth_passwordRecovery.email_pattern));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1(this, tLRPC$TL_auth_passwordRecovery));
                Dialog showDialog = this.this$0.showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    str = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    str = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", tLRPC$TL_auth_passwordRecovery.email_pattern);
            bundle.putString("password", this.passwordString);
            this.this$0.setPage(7, true, bundle, false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
            tLRPC$TL_account_deleteAccount.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda15(this), 10);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8(this, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(TLRPC$TL_error tLRPC$TL_error) {
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error == null) {
                Bundle bundle = new Bundle();
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, bundle, false);
            } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else if (tLRPC$TL_error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                Bundle bundle2 = new Bundle();
                bundle2.putString("phoneFormated", this.requestPhone);
                bundle2.putString("phoneHash", this.phoneHash);
                bundle2.putString("code", this.phoneCode);
                bundle2.putInt("startTime", ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime());
                bundle2.putInt("waitTime", Utilities.parseInt(tLRPC$TL_error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                this.this$0.setPage(8, true, bundle2, false);
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
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
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
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
                    this.codeField.setHint(LocaleController.getString("LoginPassword", NUM));
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
                this.this$0.onFieldError(this.confirmTextView);
            }
        }

        public void onNextPressed() {
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
        public /* synthetic */ void lambda$onNextPressed$14(String str) {
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
            boolean z = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
            byte[] x = z ? SRPHelper.getX(AndroidUtilities.getStringBytes(str), (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
            TLRPC$TL_auth_checkPassword tLRPC$TL_auth_checkPassword = new TLRPC$TL_auth_checkPassword();
            LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12 loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12 = new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12(this);
            if (z) {
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                tLRPC$TL_auth_checkPassword.password = startCheck;
                if (startCheck == null) {
                    TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                    tLRPC$TL_error.text = "PASSWORD_HASH_INVALID";
                    loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12.run((TLObject) null, tLRPC$TL_error);
                    return;
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_checkPassword, loginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12, 10);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$13(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$12(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            String str;
            this.nextPressed = false;
            if (tLRPC$TL_error != null && "SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13(this), 8);
            } else if (tLObject instanceof TLRPC$TL_auth_authorization) {
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7(this, tLObject), 150);
            } else {
                this.this$0.needHideProgress(false);
                if (tLRPC$TL_error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
                } else {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$9(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            if (tLRPC$TL_error == null) {
                this.currentPassword = (TLRPC$TL_account_password) tLObject;
                onNextPressed();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$11(TLObject tLObject) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5(this), 100);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$15() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.codeField);
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
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        /* access modifiers changed from: private */
        public TextView resetAccountButton;
        /* access modifiers changed from: private */
        public TextView resetAccountText;
        /* access modifiers changed from: private */
        public TextView resetAccountTime;
        private int startTime;
        final /* synthetic */ LoginActivity this$0;
        /* access modifiers changed from: private */
        public Runnable timeRunnable;
        private int waitTime;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityResetWaitView(org.telegram.ui.LoginActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                r1 = 1
                r0.setOrientation(r1)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.confirmTextView = r3
                java.lang.String r4 = "windowBackgroundWhiteGrayText6"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setTextColor(r5)
                android.widget.TextView r3 = r0.confirmTextView
                r5 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 5
                r8 = 3
                if (r6 == 0) goto L_0x0030
                r6 = 5
                goto L_0x0031
            L_0x0030:
                r6 = 3
            L_0x0031:
                r3.setGravity(r6)
                android.widget.TextView r3 = r0.confirmTextView
                r6 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r3.setLineSpacing(r9, r10)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x004a
                r9 = 5
                goto L_0x004b
            L_0x004a:
                r9 = 3
            L_0x004b:
                r11 = -2
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r9)
                r0.addView(r3, r9)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountText = r3
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0060
                r9 = 5
                goto L_0x0061
            L_0x0060:
                r9 = 3
            L_0x0061:
                r9 = r9 | 48
                r3.setGravity(r9)
                android.widget.TextView r3 = r0.resetAccountText
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setTextColor(r9)
                android.widget.TextView r3 = r0.resetAccountText
                r9 = 2131627404(0x7f0e0d8c, float:1.8882071E38)
                java.lang.String r11 = "ResetAccountStatus"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r3.setText(r9)
                android.widget.TextView r3 = r0.resetAccountText
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.resetAccountText
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r3.setLineSpacing(r9, r10)
                android.widget.TextView r3 = r0.resetAccountText
                r11 = -2
                r12 = -2
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0096
                r9 = 5
                goto L_0x0097
            L_0x0096:
                r9 = 3
            L_0x0097:
                r13 = r9 | 48
                r14 = 0
                r15 = 24
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r3, r9)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountTime = r3
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00b4
                r9 = 5
                goto L_0x00b5
            L_0x00b4:
                r9 = 3
            L_0x00b5:
                r9 = r9 | 48
                r3.setGravity(r9)
                android.widget.TextView r3 = r0.resetAccountTime
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.resetAccountTime
                r3.setTextSize(r1, r5)
                android.widget.TextView r3 = r0.resetAccountTime
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r4 = (float) r4
                r3.setLineSpacing(r4, r10)
                android.widget.TextView r3 = r0.resetAccountTime
                r11 = -2
                r12 = -2
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00dc
                r4 = 5
                goto L_0x00dd
            L_0x00dc:
                r4 = 3
            L_0x00dd:
                r13 = r4 | 48
                r14 = 0
                r15 = 2
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r3, r4)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.resetAccountButton = r3
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x00f9
                r2 = 5
                goto L_0x00fa
            L_0x00f9:
                r2 = 3
            L_0x00fa:
                r2 = r2 | 48
                r3.setGravity(r2)
                android.widget.TextView r2 = r0.resetAccountButton
                r3 = 2131627401(0x7f0e0d89, float:1.8882065E38)
                java.lang.String r4 = "ResetAccountButton"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.setText(r3)
                android.widget.TextView r2 = r0.resetAccountButton
                java.lang.String r3 = "fonts/rmedium.ttf"
                android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
                r2.setTypeface(r3)
                android.widget.TextView r2 = r0.resetAccountButton
                r2.setTextSize(r1, r5)
                android.widget.TextView r1 = r0.resetAccountButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r10)
                android.widget.TextView r1 = r0.resetAccountButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3 = 0
                r1.setPadding(r3, r2, r3, r3)
                android.widget.TextView r1 = r0.resetAccountButton
                r9 = -2
                r10 = -2
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x013a
                goto L_0x013b
            L_0x013a:
                r7 = 3
            L_0x013b:
                r11 = r7 | 48
                r12 = 0
                r13 = 7
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
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
            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
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
                Bundle bundle = new Bundle();
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, bundle, false);
            } else if (tLRPC$TL_error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", NUM);
        }

        /* access modifiers changed from: private */
        public void updateTimeText() {
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int i = max / 86400;
            int i2 = max - (86400 * i);
            int i3 = i2 / 3600;
            int i4 = (i2 - (i3 * 3600)) / 60;
            int i5 = max % 60;
            if (i != 0) {
                TextView textView = this.resetAccountTime;
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", i) + " " + LocaleController.formatPluralString("HoursBold", i3) + " " + LocaleController.formatPluralString("MinutesBold", i4)));
            } else {
                TextView textView2 = this.resetAccountTime;
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", i3) + " " + LocaleController.formatPluralString("MinutesBold", i4) + " " + LocaleController.formatPluralString("SecondsBold", i5)));
            }
            if (max > 0) {
                this.resetAccountButton.setTag("windowBackgroundWhiteGrayText6");
                this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                return;
            }
            this.resetAccountButton.setTag("windowBackgroundWhiteRedText6");
            this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
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
        /* access modifiers changed from: private */
        public TextView cancelButton;
        /* access modifiers changed from: private */
        public EditTextBoldCursor codeField;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public String passwordString;
        final /* synthetic */ LoginActivity this$0;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRecoverView(org.telegram.ui.LoginActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.this$0 = r1
                r0.<init>(r2)
                r1 = 1
                r0.setOrientation(r1)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.confirmTextView = r3
                java.lang.String r4 = "windowBackgroundWhiteGrayText6"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.confirmTextView
                r4 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r1, r4)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                r6 = 5
                r7 = 3
                if (r5 == 0) goto L_0x0030
                r5 = 5
                goto L_0x0031
            L_0x0030:
                r5 = 3
            L_0x0031:
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.confirmTextView
                r5 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r3.setLineSpacing(r8, r9)
                android.widget.TextView r3 = r0.confirmTextView
                r8 = 2131627427(0x7f0e0da3, float:1.8882118E38)
                java.lang.String r10 = "RestoreEmailSentInfo"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r3.setText(r8)
                android.widget.TextView r3 = r0.confirmTextView
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x0058
                r8 = 5
                goto L_0x0059
            L_0x0058:
                r8 = 3
            L_0x0059:
                r10 = -2
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r10, (int) r8)
                r0.addView(r3, r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
                r3.<init>(r2)
                r0.codeField = r3
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setTextColor(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setCursorColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r8 = 1101004800(0x41a00000, float:20.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r3.setCursorSize(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r8 = 1069547520(0x3fCLASSNAME, float:1.5)
                r3.setCursorWidth(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                java.lang.String r8 = "windowBackgroundWhiteHintText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setHintTextColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r8 = 0
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r8)
                r3.setBackgroundDrawable(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r10 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
                java.lang.String r11 = "PasswordCode"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                r3.setHint(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r10 = 268435461(0x10000005, float:2.5243564E-29)
                r3.setImeOptions(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r10 = 1099956224(0x41900000, float:18.0)
                r3.setTextSize(r1, r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setMaxLines(r1)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setPadding(r8, r8, r8, r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r3.setInputType(r7)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                android.text.method.PasswordTransformationMethod r10 = android.text.method.PasswordTransformationMethod.getInstance()
                r3.setTransformationMethod(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                android.graphics.Typeface r10 = android.graphics.Typeface.DEFAULT
                r3.setTypeface(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x00e5
                r10 = 5
                goto L_0x00e6
            L_0x00e5:
                r10 = 3
            L_0x00e6:
                r3.setGravity(r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                r10 = -1
                r11 = 36
                r12 = 1
                r13 = 0
                r14 = 20
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r3, r10)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2 r10 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2
                r10.<init>(r0)
                r3.setOnEditorActionListener(r10)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.cancelButton = r3
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x0113
                r2 = 5
                goto L_0x0114
            L_0x0113:
                r2 = 3
            L_0x0114:
                r2 = r2 | 80
                r3.setGravity(r2)
                android.widget.TextView r2 = r0.cancelButton
                java.lang.String r3 = "windowBackgroundWhiteBlueText4"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r2.setTextColor(r3)
                android.widget.TextView r2 = r0.cancelButton
                r2.setTextSize(r1, r4)
                android.widget.TextView r1 = r0.cancelButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r9)
                android.widget.TextView r1 = r0.cancelButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r1.setPadding(r8, r2, r8, r8)
                android.widget.TextView r1 = r0.cancelButton
                r8 = -2
                r9 = -2
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x0145
                goto L_0x0146
            L_0x0145:
                r6 = 3
            L_0x0146:
                r10 = r6 | 80
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 14
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRecoverView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0(this));
            Dialog showDialog = this.this$0.showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.codeField.setText("");
                this.currentParams = bundle;
                this.passwordString = bundle.getString("password");
                String string = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, string));
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                if (z) {
                    this.codeField.setText("");
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                TLRPC$TL_auth_checkRecoveryPassword tLRPC$TL_auth_checkRecoveryPassword = new TLRPC$TL_auth_checkRecoveryPassword();
                tLRPC$TL_auth_checkRecoveryPassword.code = obj;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_checkRecoveryPassword, new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5(this, obj), 10);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$4(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4(this, tLObject, str, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$3(TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
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
                int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                if (intValue < 60) {
                    str2 = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3(this), 100);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$5() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (!(obj == null || obj.length() == 0)) {
                bundle.putString("recoveryview_code", obj);
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
                this.codeField.setText(string);
            }
        }
    }

    public class LoginActivityNewPasswordView extends SlideView {
        /* access modifiers changed from: private */
        public TextView cancelButton;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC$TL_account_password currentPassword;
        private int currentStage;
        private String emailCode;
        private String newPassword;
        private boolean nextPressed;
        private String passwordString;
        final /* synthetic */ LoginActivity this$0;

        public boolean needBackButton() {
            return true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityNewPasswordView(org.telegram.ui.LoginActivity r20, android.content.Context r21, int r22) {
            /*
                r19 = this;
                r0 = r19
                r1 = r21
                r2 = r20
                r3 = r22
                r0.this$0 = r2
                r0.<init>(r1)
                r0.currentStage = r3
                r2 = 1
                r0.setOrientation(r2)
                if (r3 != r2) goto L_0x0017
                r4 = 1
                goto L_0x0018
            L_0x0017:
                r4 = 2
            L_0x0018:
                org.telegram.ui.Components.EditTextBoldCursor[] r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4]
                r0.codeField = r4
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.confirmTextView
                r5 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r2, r5)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 5
                r8 = 3
                if (r6 == 0) goto L_0x003d
                r6 = 5
                goto L_0x003e
            L_0x003d:
                r6 = 3
            L_0x003e:
                r4.setGravity(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r9, r10)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0057
                r9 = 5
                goto L_0x0058
            L_0x0057:
                r9 = 3
            L_0x0058:
                r11 = -2
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r9)
                r0.addView(r4, r9)
                r4 = 0
                r9 = 0
            L_0x0062:
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                int r12 = r11.length
                if (r9 >= r12) goto L_0x0163
                org.telegram.ui.Components.EditTextBoldCursor r12 = new org.telegram.ui.Components.EditTextBoldCursor
                r12.<init>(r1)
                r11[r9] = r12
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                java.lang.String r12 = "windowBackgroundWhiteBlackText"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r11.setTextColor(r13)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r11.setCursorColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 1101004800(0x41a00000, float:20.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r11.setCursorSize(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 1069547520(0x3fCLASSNAME, float:1.5)
                r11.setCursorWidth(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                java.lang.String r12 = "windowBackgroundWhiteHintText"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r11.setHintTextColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r4)
                r11.setBackgroundDrawable(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 268435461(0x10000005, float:2.5243564E-29)
                r11.setImeOptions(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 1099956224(0x41900000, float:18.0)
                r11.setTextSize(r2, r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r11.setMaxLines(r2)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r11.setPadding(r4, r4, r4, r4)
                if (r3 != 0) goto L_0x00e0
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 129(0x81, float:1.81E-43)
                r11.setInputType(r12)
            L_0x00e0:
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                android.text.method.PasswordTransformationMethod r12 = android.text.method.PasswordTransformationMethod.getInstance()
                r11.setTransformationMethod(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
                r11.setTypeface(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x00fe
                r12 = 5
                goto L_0x00ff
            L_0x00fe:
                r12 = 3
            L_0x00ff:
                r11.setGravity(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = -1
                r13 = 36
                r14 = 1
                r15 = 0
                if (r9 != 0) goto L_0x0110
                r16 = 20
                goto L_0x0112
            L_0x0110:
                r16 = 30
            L_0x0112:
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r11, r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2 r12 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                r12.<init>(r0, r9)
                r11.setOnEditorActionListener(r12)
                if (r3 != 0) goto L_0x014f
                if (r9 != 0) goto L_0x013e
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 2131627098(0x7f0e0c5a, float:1.888145E38)
                java.lang.String r13 = "PleaseEnterNewFirstPasswordHint"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r11.setHint(r12)
                goto L_0x015f
            L_0x013e:
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 2131627100(0x7f0e0c5c, float:1.8881455E38)
                java.lang.String r13 = "PleaseEnterNewSecondPasswordHint"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r11.setHint(r12)
                goto L_0x015f
            L_0x014f:
                org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.codeField
                r11 = r11[r9]
                r12 = 2131626911(0x7f0e0b9f, float:1.8881072E38)
                java.lang.String r13 = "PasswordHintPlaceholder"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r11.setHint(r12)
            L_0x015f:
                int r9 = r9 + 1
                goto L_0x0062
            L_0x0163:
                if (r3 != 0) goto L_0x0174
                android.widget.TextView r3 = r0.confirmTextView
                r9 = 2131627099(0x7f0e0c5b, float:1.8881453E38)
                java.lang.String r11 = "PleaseEnterNewFirstPasswordLogin"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r3.setText(r9)
                goto L_0x0182
            L_0x0174:
                android.widget.TextView r3 = r0.confirmTextView
                r9 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
                java.lang.String r11 = "PasswordHintTextLogin"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r3.setText(r9)
            L_0x0182:
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.cancelButton = r3
                boolean r1 = org.telegram.messenger.LocaleController.isRTL
                if (r1 == 0) goto L_0x018f
                r1 = 5
                goto L_0x0190
            L_0x018f:
                r1 = 3
            L_0x0190:
                r1 = r1 | 80
                r3.setGravity(r1)
                android.widget.TextView r1 = r0.cancelButton
                java.lang.String r3 = "windowBackgroundWhiteBlueText4"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setTextColor(r3)
                android.widget.TextView r1 = r0.cancelButton
                r1.setTextSize(r2, r5)
                android.widget.TextView r1 = r0.cancelButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r10)
                android.widget.TextView r1 = r0.cancelButton
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r1.setPadding(r4, r2, r4, r4)
                android.widget.TextView r1 = r0.cancelButton
                r2 = 2131628580(0x7f0e1224, float:1.8884457E38)
                java.lang.String r3 = "YourEmailSkip"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                android.widget.TextView r1 = r0.cancelButton
                r9 = -2
                r10 = -2
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x01cf
                goto L_0x01d0
            L_0x01cf:
                r7 = 3
            L_0x01d0:
                r11 = r7 | 80
                r12 = 0
                r13 = 6
                r14 = 0
                r15 = 14
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(int i, TextView textView, int i2, KeyEvent keyEvent) {
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
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            if (this.currentStage == 0) {
                recoverPassword((String) null, (String) null);
            } else {
                recoverPassword(this.newPassword, (String) null);
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
                AndroidUtilities.showKeyboard(this.codeField[0]);
                this.codeField[0].requestFocus();
            }
        }

        private void onPasscodeError(boolean z, int i) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(this.codeField[i], 2.0f, 0);
            }
        }

        public void onNextPressed() {
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
            Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4(this, str, str2, tLRPC$TL_auth_recoverPassword));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$7(String str, String str2, TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword) {
            byte[] stringBytes = str != null ? AndroidUtilities.getStringBytes(str) : null;
            LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7 loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7 = new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7(this, str, str2);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (str != null) {
                    tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                    if (tLRPC$TL_auth_recoverPassword.new_settings.new_password_hash == null) {
                        TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                        tLRPC$TL_error.text = "ALGO_INVALID";
                        loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7.run((TLObject) null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_recoverPassword, loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            loginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7.run((TLObject) null, tLRPC$TL_error2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$6(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5(this, tLRPC$TL_error, str, str2, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$5(TLRPC$TL_error tLRPC$TL_error, String str, String str2, TLObject tLObject) {
            String str3;
            if (tLRPC$TL_error == null || (!"SRP_ID_INVALID".equals(tLRPC$TL_error.text) && !"NEW_SALT_INVALID".equals(tLRPC$TL_error.text))) {
                this.this$0.needHideProgress(false);
                if (tLObject instanceof TLRPC$auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0(this, tLObject));
                    if (TextUtils.isEmpty(str)) {
                        builder.setMessage(LocaleController.getString("PasswordReset", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("YourPasswordChangedSuccessText", NUM));
                    }
                    builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
                    Dialog showDialog = this.this$0.showDialog(builder.create());
                    if (showDialog != null) {
                        showDialog.setCanceledOnTouchOutside(false);
                        showDialog.setCancelable(false);
                    }
                } else if (tLRPC$TL_error != null) {
                    this.nextPressed = false;
                    if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                        int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                        if (intValue < 60) {
                            str3 = LocaleController.formatPluralString("Seconds", intValue);
                        } else {
                            str3 = LocaleController.formatPluralString("Minutes", intValue / 60);
                        }
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str3));
                        return;
                    }
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                }
            } else {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8(this, str, str2), 8);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$3(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLObject, str, str2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, String str2) {
            if (tLRPC$TL_error == null) {
                TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
                this.currentPassword = tLRPC$TL_account_password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
                recoverPassword(str, str2);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$recoverPassword$4(TLObject tLObject, DialogInterface dialogInterface, int i) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3(this), 100);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$8() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                editTextBoldCursorArr[0].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                editTextBoldCursorArr2[0].setSelection(editTextBoldCursorArr2[0].length());
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
        private RLottieDrawable cameraDrawable;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public EditTextBoldCursor firstNameField;
        /* access modifiers changed from: private */
        public ImageUpdater imageUpdater;
        /* access modifiers changed from: private */
        public EditTextBoldCursor lastNameField;
        private boolean nextPressed = false;
        private String phoneHash;
        /* access modifiers changed from: private */
        public TextView privacyView;
        private String requestPhone;
        /* access modifiers changed from: private */
        public TextView textView;
        final /* synthetic */ LoginActivity this$0;
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
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2(this));
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
            onNextPressed();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$3(DialogInterface dialogInterface, int i) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder.setMessage(LocaleController.getString("TosDecline", NUM));
            builder.setPositiveButton(LocaleController.getString("SignUp", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3(this));
            builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1(this));
            this.this$0.showDialog(builder.create());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$1(DialogInterface dialogInterface, int i) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showTermsOfService$2(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRegisterView(org.telegram.ui.LoginActivity r29, android.content.Context r30) {
            /*
                r28 = this;
                r0 = r28
                r1 = r29
                r2 = r30
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 0
                r0.nextPressed = r3
                r4 = 1
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
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.textView = r5
                java.lang.String r6 = "RegisterText2"
                r7 = 2131627328(0x7f0e0d40, float:1.8881917E38)
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
                r5.setText(r6)
                android.widget.TextView r5 = r0.textView
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.textView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0053
                r7 = 5
                goto L_0x0054
            L_0x0053:
                r7 = 3
            L_0x0054:
                r5.setGravity(r7)
                android.widget.TextView r5 = r0.textView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.textView
                r10 = -2
                r11 = -2
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0068
                r12 = 5
                goto L_0x0069
            L_0x0068:
                r12 = 3
            L_0x0069:
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r5, r10)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                r10 = -1
                r11 = -2
                r12 = 0
                r13 = 1101529088(0x41a80000, float:21.0)
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11, r12, r13, r14, r15)
                r0.addView(r5, r10)
                org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable
                r10.<init>()
                r0.avatarDrawable = r10
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$1 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$1
                r10.<init>(r2, r1)
                r0.avatarImage = r10
                r11 = 1107296256(0x42000000, float:32.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.setRoundRadius(r11)
                org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
                r11 = 5
                r13 = 0
                r10.setInfo(r11, r13, r13)
                org.telegram.ui.Components.BackupImageView r10 = r0.avatarImage
                org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
                r10.setImageDrawable(r11)
                org.telegram.ui.Components.BackupImageView r10 = r0.avatarImage
                r11 = 64
                r12 = 1115684864(0x42800000, float:64.0)
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x00ba
                r13 = 5
                goto L_0x00bb
            L_0x00ba:
                r13 = 3
            L_0x00bb:
                r13 = r13 | 48
                r14 = 0
                r15 = 1098907648(0x41800000, float:16.0)
                r16 = 0
                r17 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r5.addView(r10, r11)
                android.graphics.Paint r10 = new android.graphics.Paint
                r10.<init>(r4)
                r11 = 1426063360(0x55000000, float:8.796093E12)
                r10.setColor(r11)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$2 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$2
                r11.<init>(r2, r1, r10)
                r0.avatarOverlay = r11
                r12 = 64
                r13 = 1115684864(0x42800000, float:64.0)
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x00e6
                r10 = 5
                goto L_0x00e7
            L_0x00e6:
                r10 = 3
            L_0x00e7:
                r14 = r10 | 48
                r15 = 0
                r16 = 1098907648(0x41800000, float:16.0)
                r17 = 0
                r18 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r5.addView(r11, r10)
                android.view.View r10 = r0.avatarOverlay
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6
                r11.<init>(r0)
                r10.setOnClickListener(r11)
                org.telegram.ui.Components.RLottieDrawable r10 = new org.telegram.ui.Components.RLottieDrawable
                r13 = 2131558408(0x7f0d0008, float:1.874213E38)
                r11 = 1114636288(0x42700000, float:60.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r17 = 0
                r18 = 0
                java.lang.String r14 = "NUM"
                r12 = r10
                r12.<init>((int) r13, (java.lang.String) r14, (int) r15, (int) r16, (boolean) r17, (int[]) r18)
                r0.cameraDrawable = r10
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$3 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$3
                r10.<init>(r2, r1)
                r0.avatarEditor = r10
                android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
                r10.setScaleType(r11)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                org.telegram.ui.Components.RLottieDrawable r11 = r0.cameraDrawable
                r10.setAnimation(r11)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                r10.setEnabled(r3)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                r10.setClickable(r3)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                r11 = 1073741824(0x40000000, float:2.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r13 = 1065353216(0x3var_, float:1.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r10.setPadding(r12, r3, r3, r14)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                r14 = 64
                r15 = 1115684864(0x42800000, float:64.0)
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0156
                r12 = 5
                goto L_0x0157
            L_0x0156:
                r12 = 3
            L_0x0157:
                r16 = r12 | 48
                r17 = 0
                r18 = 1098907648(0x41800000, float:16.0)
                r19 = 0
                r20 = 0
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r5.addView(r10, r12)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$4
                r10.<init>(r2, r1)
                r0.avatarProgressView = r10
                r1 = 1106247680(0x41var_, float:30.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r10.setSize(r1)
                org.telegram.ui.Components.RadialProgressView r1 = r0.avatarProgressView
                r10 = -1
                r1.setProgressColor(r10)
                org.telegram.ui.Components.RadialProgressView r1 = r0.avatarProgressView
                r14 = 64
                r15 = 1115684864(0x42800000, float:64.0)
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x018a
                r12 = 5
                goto L_0x018b
            L_0x018a:
                r12 = 3
            L_0x018b:
                r16 = r12 | 48
                r17 = 0
                r18 = 1098907648(0x41800000, float:16.0)
                r19 = 0
                r20 = 0
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r5.addView(r1, r12)
                r0.showAvatarProgress(r3, r3)
                org.telegram.ui.Components.EditTextBoldCursor r1 = new org.telegram.ui.Components.EditTextBoldCursor
                r1.<init>(r2)
                r0.firstNameField = r1
                java.lang.String r12 = "windowBackgroundWhiteHintText"
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r1.setHintTextColor(r14)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                java.lang.String r14 = "windowBackgroundWhiteBlackText"
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r1.setTextColor(r15)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r3)
                r1.setBackgroundDrawable(r15)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r1.setCursorColor(r15)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r15 = 1101004800(0x41a00000, float:20.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r1.setCursorSize(r8)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r8 = 1069547520(0x3fCLASSNAME, float:1.5)
                r1.setCursorWidth(r8)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r9 = 2131625625(0x7f0e0699, float:1.8878463E38)
                java.lang.String r10 = "FirstName"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                r1.setHint(r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r9 = 268435461(0x10000005, float:2.5243564E-29)
                r1.setImeOptions(r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r9 = 1099431936(0x41880000, float:17.0)
                r1.setTextSize(r4, r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r1.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r10 = 8192(0x2000, float:1.14794E-41)
                r1.setInputType(r10)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                r18 = -1
                r19 = 1108344832(0x42100000, float:36.0)
                boolean r20 = org.telegram.messenger.LocaleController.isRTL
                if (r20 == 0) goto L_0x0214
                r21 = 5
                goto L_0x0216
            L_0x0214:
                r21 = 3
            L_0x0216:
                r21 = r21 | 48
                r25 = 0
                r26 = 1118437376(0x42aa0000, float:85.0)
                if (r20 == 0) goto L_0x0221
                r22 = 0
                goto L_0x0223
            L_0x0221:
                r22 = 1118437376(0x42aa0000, float:85.0)
            L_0x0223:
                r23 = 0
                if (r20 == 0) goto L_0x022a
                r24 = 1118437376(0x42aa0000, float:85.0)
                goto L_0x022c
            L_0x022a:
                r24 = 0
            L_0x022c:
                r27 = 0
                r20 = r21
                r21 = r22
                r22 = r23
                r23 = r24
                r24 = r27
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
                r5.addView(r1, r13)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9 r13 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9
                r13.<init>(r0)
                r1.setOnEditorActionListener(r13)
                org.telegram.ui.Components.EditTextBoldCursor r1 = new org.telegram.ui.Components.EditTextBoldCursor
                r1.<init>(r2)
                r0.lastNameField = r1
                r13 = 2131626020(0x7f0e0824, float:1.8879264E38)
                java.lang.String r11 = "LastName"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r13)
                r1.setHint(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r1.setHintTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r1.setTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r3)
                r1.setBackgroundDrawable(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r1.setCursorColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r1.setCursorSize(r11)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setCursorWidth(r8)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r8 = 268435462(0x10000006, float:2.5243567E-29)
                r1.setImeOptions(r8)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setTextSize(r4, r9)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r1.setInputType(r10)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                r8 = -1
                r9 = 1108344832(0x42100000, float:36.0)
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x02b0
                r11 = 5
                goto L_0x02b1
            L_0x02b0:
                r11 = 3
            L_0x02b1:
                r11 = r11 | 48
                if (r10 == 0) goto L_0x02b7
                r12 = 0
                goto L_0x02b9
            L_0x02b7:
                r12 = 1118437376(0x42aa0000, float:85.0)
            L_0x02b9:
                r13 = 1112276992(0x424CLASSNAME, float:51.0)
                if (r10 == 0) goto L_0x02bf
                r25 = 1118437376(0x42aa0000, float:85.0)
            L_0x02bf:
                r14 = 0
                r10 = r11
                r11 = r12
                r12 = r13
                r13 = r25
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r5.addView(r1, r8)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8 r5 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8
                r5.<init>(r0)
                r1.setOnEditorActionListener(r5)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r0.wrongNumber = r1
                r5 = 2131624681(0x7f0e02e9, float:1.8876549E38)
                java.lang.String r8 = "CancelRegistration"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
                r1.setText(r5)
                android.widget.TextView r1 = r0.wrongNumber
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x02f1
                r5 = 5
                goto L_0x02f2
            L_0x02f1:
                r5 = 3
            L_0x02f2:
                r5 = r5 | r4
                r1.setGravity(r5)
                android.widget.TextView r1 = r0.wrongNumber
                java.lang.String r5 = "windowBackgroundWhiteBlueText4"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r1.setTextColor(r5)
                android.widget.TextView r1 = r0.wrongNumber
                r1.setTextSize(r4, r7)
                android.widget.TextView r1 = r0.wrongNumber
                r5 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r8
                r8 = 1065353216(0x3var_, float:1.0)
                r1.setLineSpacing(r5, r8)
                android.widget.TextView r1 = r0.wrongNumber
                r5 = 1103101952(0x41CLASSNAME, float:24.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r1.setPadding(r3, r5, r3, r3)
                android.widget.TextView r1 = r0.wrongNumber
                r5 = 8
                r1.setVisibility(r5)
                android.widget.TextView r1 = r0.wrongNumber
                r8 = -2
                r9 = -2
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0331
                r16 = 5
                goto L_0x0333
            L_0x0331:
                r16 = 3
            L_0x0333:
                r10 = r16 | 48
                r11 = 0
                r12 = 20
                r13 = 0
                r14 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r1, r5)
                android.widget.TextView r1 = r0.wrongNumber
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7 r5 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7
                r5.<init>(r0)
                r1.setOnClickListener(r5)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r1.setClipToPadding(r3)
                r5 = 1105199104(0x41e00000, float:28.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r8 = 1120403456(0x42CLASSNAME, float:100.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r9 = 1098907648(0x41800000, float:16.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r1.setPadding(r3, r5, r8, r9)
                r3 = 83
                r5 = -1
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r5, (int) r3)
                r0.addView(r1, r8)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.privacyView = r5
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r2)
                android.widget.TextView r2 = r0.privacyView
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r5 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r5.<init>()
                r2.setMovementMethod(r5)
                android.widget.TextView r2 = r0.privacyView
                java.lang.String r5 = "windowBackgroundWhiteLinkText"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r2.setLinkTextColor(r5)
                android.widget.TextView r2 = r0.privacyView
                r2.setTextSize(r4, r7)
                android.widget.TextView r2 = r0.privacyView
                r5 = 1073741824(0x40000000, float:2.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r6 = 1065353216(0x3var_, float:1.0)
                r2.setLineSpacing(r5, r6)
                android.widget.TextView r2 = r0.privacyView
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r2)
                android.widget.TextView r2 = r0.privacyView
                r5 = -2
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r3)
                r1.addView(r2, r3)
                r1 = 2131627939(0x7f0e0fa3, float:1.8883157E38)
                java.lang.String r2 = "TermsOfServiceLogin"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
                r2.<init>(r1)
                r3 = 42
                int r3 = r1.indexOf(r3)
                r5 = 42
                int r1 = r1.lastIndexOf(r5)
                r5 = -1
                if (r3 == r5) goto L_0x03f1
                if (r1 == r5) goto L_0x03f1
                if (r3 == r1) goto L_0x03f1
                int r5 = r1 + 1
                java.lang.String r6 = ""
                r2.replace(r1, r5, r6)
                int r5 = r3 + 1
                java.lang.String r6 = ""
                r2.replace(r3, r5, r6)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan r5 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan
                r5.<init>()
                int r1 = r1 - r4
                r4 = 33
                r2.setSpan(r5, r3, r1, r4)
            L_0x03f1:
                android.widget.TextView r1 = r0.privacyView
                r1.setText(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRegisterView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11(this), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5(this));
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
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(DialogInterface dialogInterface) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.playAnimation();
                return;
            }
            this.cameraDrawable.setCurrentFrame(0, false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$7(TextView textView2, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$8(TextView textView2, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$9(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                onBackPressed(false);
            }
        }

        public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14(this, tLRPC$PhotoSize2, tLRPC$PhotoSize));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didUploadPhoto$10(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
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
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("AreYouSureRegistration", NUM));
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4(this));
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
        public /* synthetic */ void lambda$onBackPressed$11(DialogInterface dialogInterface, int i) {
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
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10(this), 100);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onShow$12() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
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

        public void onNextPressed() {
            if (!this.nextPressed) {
                if (this.this$0.currentTermsOfService != null && this.this$0.currentTermsOfService.popup) {
                    showTermsOfService(true);
                } else if (this.firstNameField.length() == 0) {
                    this.this$0.onFieldError(this.firstNameField);
                } else {
                    this.nextPressed = true;
                    TLRPC$TL_auth_signUp tLRPC$TL_auth_signUp = new TLRPC$TL_auth_signUp();
                    tLRPC$TL_auth_signUp.phone_code_hash = this.phoneHash;
                    tLRPC$TL_auth_signUp.phone_number = this.requestPhone;
                    tLRPC$TL_auth_signUp.first_name = this.firstNameField.getText().toString();
                    tLRPC$TL_auth_signUp.last_name = this.lastNameField.getText().toString();
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_signUp, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15(this), 10);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$15(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13(this, tLObject, tLRPC$TL_error));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$14(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            this.nextPressed = false;
            if (tLObject instanceof TLRPC$TL_auth_authorization) {
                hidePrivacyView();
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12(this, tLObject), 150);
                return;
            }
            this.this$0.needHideProgress(false);
            if (tLRPC$TL_error.text.contains("PHONE_NUMBER_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (tLRPC$TL_error.text.contains("FIRSTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (tLRPC$TL_error.text.contains("LASTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$13(TLObject tLObject) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.this$0.fragmentView.findFocus());
            this.this$0.onAuthSuccess((TLRPC$TL_auth_authorization) tLObject, true);
            if (this.avatarBig != null) {
                MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(this.avatarBig);
            }
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                PhoneView phoneView = (PhoneView) slideViewArr[0];
                LoginActivitySmsView loginActivitySmsView = (LoginActivitySmsView) slideViewArr[1];
                LoginActivitySmsView loginActivitySmsView2 = (LoginActivitySmsView) slideViewArr[2];
                LoginActivitySmsView loginActivitySmsView3 = (LoginActivitySmsView) slideViewArr[3];
                LoginActivitySmsView loginActivitySmsView4 = (LoginActivitySmsView) slideViewArr[4];
                LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) slideViewArr[5];
                LoginActivityPasswordView loginActivityPasswordView = (LoginActivityPasswordView) slideViewArr[6];
                LoginActivityRecoverView loginActivityRecoverView = (LoginActivityRecoverView) slideViewArr[7];
                LoginActivityResetWaitView loginActivityResetWaitView = (LoginActivityResetWaitView) slideViewArr[8];
                ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                LoginActivity$$ExternalSyntheticLambda4 loginActivity$$ExternalSyntheticLambda4 = new LoginActivity$$ExternalSyntheticLambda4(this);
                arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
                arrayList.add(new ThemeDescription(this.floatingProgressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
                arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayLine"));
                arrayList.add(new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.privacyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.privacyView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView.codeField != null) {
                    for (int i2 = 0; i2 < loginActivitySmsView.codeField.length; i2++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView2.codeField != null) {
                    for (int i3 = 0; i3 < loginActivitySmsView2.codeField.length; i3++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView3.codeField != null) {
                    for (int i4 = 0; i4 < loginActivitySmsView3.codeField.length; i4++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView4.codeField != null) {
                    for (int i5 = 0; i5 < loginActivitySmsView4.codeField.length; i5++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i5], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i5], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.blueImageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, loginActivity$$ExternalSyntheticLambda4, "chats_actionBackground"));
                for (int i6 = 0; i6 < 2; i6++) {
                    SlideView[] slideViewArr2 = this.views;
                    int i7 = i6 + 9;
                    if (slideViewArr2[i7] != null) {
                        LoginActivityNewPasswordView loginActivityNewPasswordView = (LoginActivityNewPasswordView) slideViewArr2[i7];
                        arrayList.add(new ThemeDescription(loginActivityNewPasswordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                        for (int i8 = 0; i8 < loginActivityNewPasswordView.codeField.length; i8++) {
                            arrayList.add(new ThemeDescription(loginActivityNewPasswordView.codeField[i8], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                            arrayList.add(new ThemeDescription(loginActivityNewPasswordView.codeField[i8], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                            arrayList.add(new ThemeDescription(loginActivityNewPasswordView.codeField[i8], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                            arrayList.add(new ThemeDescription(loginActivityNewPasswordView.codeField[i8], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                        }
                        arrayList.add(new ThemeDescription(loginActivityNewPasswordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                    }
                }
                return arrayList;
            } else if (slideViewArr[i] == null) {
                return new ArrayList<>();
            } else {
                i++;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000c, code lost:
        r1 = (org.telegram.ui.LoginActivity.LoginActivitySmsView) r1[r0];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getThemeDescriptions$4() {
        /*
            r6 = this;
            r0 = 0
        L_0x0001:
            org.telegram.ui.Components.SlideView[] r1 = r6.views
            int r2 = r1.length
            if (r0 >= r2) goto L_0x002d
            r2 = r1[r0]
            boolean r2 = r2 instanceof org.telegram.ui.LoginActivity.LoginActivitySmsView
            if (r2 == 0) goto L_0x002a
            r1 = r1[r0]
            org.telegram.ui.LoginActivity$LoginActivitySmsView r1 = (org.telegram.ui.LoginActivity.LoginActivitySmsView) r1
            org.telegram.ui.Components.RLottieDrawable r2 = r1.hintDrawable
            if (r2 == 0) goto L_0x002a
            java.lang.String r3 = "chats_actionBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r5 = "Bubble.**"
            r2.setLayerColor(r5, r4)
            org.telegram.ui.Components.RLottieDrawable r1 = r1.hintDrawable
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r3 = "Phone.**"
            r1.setLayerColor(r3, r2)
        L_0x002a:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x002d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.lambda$getThemeDescriptions$4():void");
    }
}
