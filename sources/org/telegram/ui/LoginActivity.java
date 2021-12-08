package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity;

public class LoginActivity extends BaseFragment {
    public static final int AUTH_TYPE_CALL = 4;
    public static final int AUTH_TYPE_FLASH_CALL = 3;
    public static final int AUTH_TYPE_MISSED_CALL = 11;
    public static final int AUTH_TYPE_SMS = 2;
    private static final int DONE_TYPE_ACTION = 1;
    private static final int DONE_TYPE_FLOATING = 0;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public boolean checkPermissions = true;
    /* access modifiers changed from: private */
    public boolean checkShowPermissions = true;
    /* access modifiers changed from: private */
    public int currentDoneType;
    /* access modifiers changed from: private */
    public TLRPC.TL_help_termsOfService currentTermsOfService;
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
    private SlideView[] views = new SlideView[12];

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

        public void startProgressAnimation(long duration2) {
            this.animating = true;
            this.duration = duration2;
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
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            this.path.rewind();
            this.radius = ((float) h) / 2.0f;
            this.boundsRect.set(0.0f, 0.0f, (float) w, (float) h);
            this.rect.set(this.boundsRect);
            Path path2 = this.path;
            RectF rectF = this.boundsRect;
            float f = this.radius;
            path2.addRoundRect(rectF, f, f, Path.Direction.CW);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float progress;
            if (this.duration > 0) {
                progress = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.duration));
            } else {
                progress = 0.0f;
            }
            canvas.clipPath(this.path);
            RectF rectF = this.boundsRect;
            float f = this.radius;
            canvas.drawRoundRect(rectF, f, f, this.paint);
            this.rect.right = this.boundsRect.right * progress;
            RectF rectF2 = this.rect;
            float f2 = this.radius;
            canvas.drawRoundRect(rectF2, f2, f2, this.paint2);
            boolean z = this.animating & (this.duration > 0 && progress < 1.0f);
            this.animating = z;
            if (z) {
                postInvalidateOnAnimation();
            }
        }
    }

    public LoginActivity() {
    }

    public LoginActivity(int account) {
        this.currentAccount = account;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a < slideViewArr.length) {
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:109:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0410  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r30) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r3 = "AppName"
            r4 = 2131624300(0x7f0e016c, float:1.8875776E38)
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
            r7 = 2131165515(0x7var_b, float:1.794525E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r3.addItemWithWidth(r4, r7, r6)
            r0.doneItem = r6
            org.telegram.ui.Components.ContextProgressView r6 = new org.telegram.ui.Components.ContextProgressView
            r6.<init>(r1, r4)
            r0.doneProgressView = r6
            r7 = 0
            r6.setAlpha(r7)
            org.telegram.ui.Components.ContextProgressView r6 = r0.doneProgressView
            r8 = 1036831949(0x3dcccccd, float:0.1)
            r6.setScaleX(r8)
            org.telegram.ui.Components.ContextProgressView r6 = r0.doneProgressView
            r6.setScaleY(r8)
            org.telegram.ui.Components.ContextProgressView r6 = r0.doneProgressView
            r9 = 4
            r6.setVisibility(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            r6.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            r6.setScaleX(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            r6.setScaleY(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            org.telegram.ui.Components.ContextProgressView r10 = r0.doneProgressView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12)
            r6.addView(r10, r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            java.lang.String r10 = "Done"
            r13 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r10, r13)
            r6.setContentDescription(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.doneItem
            boolean[] r14 = r0.doneButtonVisible
            boolean r14 = r14[r4]
            r15 = 8
            if (r14 == 0) goto L_0x0095
            r14 = 0
            goto L_0x0097
        L_0x0095:
            r14 = 8
        L_0x0097:
            r6.setVisibility(r14)
            org.telegram.ui.LoginActivity$2 r6 = new org.telegram.ui.LoginActivity$2
            r6.<init>(r1)
            r0.fragmentView = r6
            org.telegram.ui.LoginActivity$3 r14 = new org.telegram.ui.LoginActivity$3
            r14.<init>(r1)
            r14.setFillViewport(r4)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12)
            r6.addView(r14, r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r12 = -2
            r7 = 51
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r11, r12, r7)
            r14.addView(r8, r7)
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$PhoneView r12 = new org.telegram.ui.LoginActivity$PhoneView
            r12.<init>(r0, r1)
            r7[r2] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r12.<init>(r0, r1, r4)
            r7[r4] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r11 = 2
            r12.<init>(r0, r1, r11)
            r7[r11] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r13 = 3
            r12.<init>(r0, r1, r13)
            r7[r13] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r12.<init>(r0, r1, r9)
            r7[r9] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r12 = 5
            org.telegram.ui.LoginActivity$LoginActivityRegisterView r13 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView
            r13.<init>(r0, r1)
            r7[r12] = r13
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r12 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView
            r12.<init>(r0, r1)
            r13 = 6
            r7[r13] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r12 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView
            r12.<init>(r0, r1)
            r11 = 7
            r7[r11] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityResetWaitView r12 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView
            r12.<init>(r0, r1)
            r7[r15] = r12
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r12 = 9
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r15 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r15.<init>(r0, r1, r2)
            r7[r12] = r15
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            r12 = 10
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r15 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r15.<init>(r0, r1, r4)
            r7[r12] = r15
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r12 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r15 = 11
            r12.<init>(r0, r1, r15)
            r7[r15] = r12
            r7 = 0
        L_0x0137:
            org.telegram.ui.Components.SlideView[] r12 = r0.views
            int r15 = r12.length
            if (r7 >= r15) goto L_0x0181
            r12 = r12[r7]
            if (r7 != 0) goto L_0x0142
            r15 = 0
            goto L_0x0144
        L_0x0142:
            r15 = 8
        L_0x0144:
            r12.setVisibility(r15)
            org.telegram.ui.Components.SlideView[] r12 = r0.views
            r12 = r12[r7]
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            boolean r15 = org.telegram.messenger.AndroidUtilities.isTablet()
            r19 = 1104150528(0x41d00000, float:26.0)
            r20 = 1099956224(0x41900000, float:18.0)
            if (r15 == 0) goto L_0x015e
            r15 = 1104150528(0x41d00000, float:26.0)
            goto L_0x0160
        L_0x015e:
            r15 = 1099956224(0x41900000, float:18.0)
        L_0x0160:
            r21 = 1106247680(0x41var_, float:30.0)
            boolean r22 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r22 == 0) goto L_0x016b
            r22 = 1104150528(0x41d00000, float:26.0)
            goto L_0x016d
        L_0x016b:
            r22 = 1099956224(0x41900000, float:18.0)
        L_0x016d:
            r23 = 0
            r19 = r15
            r20 = r21
            r21 = r22
            r22 = r23
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r8.addView(r12, r15)
            int r7 = r7 + 1
            goto L_0x0137
        L_0x0181:
            android.os.Bundle r7 = r29.loadCurrentState()
            if (r7 == 0) goto L_0x01f7
            java.lang.String r12 = "currentViewNum"
            int r12 = r7.getInt(r12, r2)
            r0.currentViewNum = r12
            java.lang.String r12 = "syncContacts"
            int r12 = r7.getInt(r12, r4)
            if (r12 != r4) goto L_0x0199
            r12 = 1
            goto L_0x019a
        L_0x0199:
            r12 = 0
        L_0x019a:
            r0.syncContacts = r12
            int r12 = r0.currentViewNum
            if (r12 < r4) goto L_0x01cb
            if (r12 > r9) goto L_0x01cb
            java.lang.String r11 = "open"
            int r11 = r7.getInt(r11)
            if (r11 == 0) goto L_0x01c8
            long r12 = java.lang.System.currentTimeMillis()
            r16 = 1000(0x3e8, double:4.94E-321)
            long r12 = r12 / r16
            r16 = r10
            long r9 = (long) r11
            long r12 = r12 - r9
            long r9 = java.lang.Math.abs(r12)
            r12 = 86400(0x15180, double:4.26873E-319)
            int r17 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r17 < 0) goto L_0x01ca
            r0.currentViewNum = r2
            r7 = 0
            r29.clearCurrentState()
            goto L_0x01ca
        L_0x01c8:
            r16 = r10
        L_0x01ca:
            goto L_0x01f9
        L_0x01cb:
            r16 = r10
            if (r12 != r13) goto L_0x01e2
            org.telegram.ui.Components.SlideView[] r9 = r0.views
            r9 = r9[r13]
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r9 = (org.telegram.ui.LoginActivity.LoginActivityPasswordView) r9
            org.telegram.tgnet.TLRPC$TL_account_password r10 = r9.currentPassword
            if (r10 != 0) goto L_0x01e1
            r0.currentViewNum = r2
            r7 = 0
            r29.clearCurrentState()
        L_0x01e1:
            goto L_0x01f9
        L_0x01e2:
            if (r12 != r11) goto L_0x01e1
            org.telegram.ui.Components.SlideView[] r9 = r0.views
            r9 = r9[r11]
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r9 = (org.telegram.ui.LoginActivity.LoginActivityRecoverView) r9
            java.lang.String r10 = r9.passwordString
            if (r10 != 0) goto L_0x01f9
            r0.currentViewNum = r2
            r7 = 0
            r29.clearCurrentState()
            goto L_0x01f9
        L_0x01f7:
            r16 = r10
        L_0x01f9:
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            r0.floatingButtonContainer = r9
            boolean[] r10 = r0.doneButtonVisible
            boolean r10 = r10[r2]
            if (r10 == 0) goto L_0x0208
            r10 = 0
            goto L_0x020a
        L_0x0208:
            r10 = 8
        L_0x020a:
            r9.setVisibility(r10)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.String r10 = "chats_actionBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            java.lang.String r11 = "chats_actionPressedBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r9, r10, r11)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 >= r11) goto L_0x0253
            android.content.res.Resources r10 = r30.getResources()
            r12 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r10 = r10.getDrawable(r12)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r15)
            r10.setColorFilter(r12)
            org.telegram.ui.Components.CombinedDrawable r12 = new org.telegram.ui.Components.CombinedDrawable
            r12.<init>(r10, r9, r2, r2)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r12.setIconSize(r13, r15)
            r9 = r12
        L_0x0253:
            android.widget.FrameLayout r10 = r0.floatingButtonContainer
            r10.setBackgroundDrawable(r9)
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 < r11) goto L_0x02c6
            android.animation.StateListAnimator r10 = new android.animation.StateListAnimator
            r10.<init>()
            int[] r12 = new int[r4]
            r13 = 16842919(0x10100a7, float:2.3694026E-38)
            r12[r2] = r13
            android.widget.ImageView r13 = r0.floatingButtonIcon
            r15 = 2
            float[] r5 = new float[r15]
            r15 = 1073741824(0x40000000, float:2.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r11 = (float) r11
            r5[r2] = r11
            r11 = 1082130432(0x40800000, float:4.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r15 = (float) r15
            r5[r4] = r15
            java.lang.String r15 = "translationZ"
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r13, r15, r5)
            r21 = r3
            r2 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r5.setDuration(r2)
            r10.addState(r12, r2)
            r2 = 0
            int[] r3 = new int[r2]
            android.widget.ImageView r5 = r0.floatingButtonIcon
            r12 = 2
            float[] r13 = new float[r12]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r13[r2] = r11
            r2 = r13
            r11 = 1073741824(0x40000000, float:2.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r2[r4] = r11
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r5, r15, r2)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r11)
            r10.addState(r3, r2)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            r2.setStateListAnimator(r10)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$4 r3 = new org.telegram.ui.LoginActivity$4
            r3.<init>()
            r2.setOutlineProvider(r3)
            goto L_0x02c8
        L_0x02c6:
            r21 = r3
        L_0x02c8:
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r2)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r3 < r5) goto L_0x02da
            r3 = 56
            r22 = 56
            goto L_0x02de
        L_0x02da:
            r3 = 60
            r22 = 60
        L_0x02de:
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r5) goto L_0x02e5
            r23 = 1113587712(0x42600000, float:56.0)
            goto L_0x02e9
        L_0x02e5:
            r3 = 1114636288(0x42700000, float:60.0)
            r23 = 1114636288(0x42700000, float:60.0)
        L_0x02e9:
            r24 = 85
            r25 = 0
            r26 = 0
            r27 = 1096810496(0x41600000, float:14.0)
            r28 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r6.addView(r2, r3)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2
            r3.<init>(r0)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.floatingButtonIcon = r2
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            android.widget.ImageView r2 = r0.floatingButtonIcon
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "chats_actionIcon"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r10)
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.floatingButtonIcon
            r3 = 2131165246(0x7var_e, float:1.7944704E38)
            r2.setImageResource(r3)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            r5 = r16
            r3 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setContentDescription(r3)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            android.widget.ImageView r3 = r0.floatingButtonIcon
            int r5 = android.os.Build.VERSION.SDK_INT
            r10 = 21
            if (r5 < r10) goto L_0x0345
            r5 = 56
            goto L_0x0347
        L_0x0345:
            r5 = 60
        L_0x0347:
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r10) goto L_0x034e
            r10 = 1113587712(0x42600000, float:56.0)
            goto L_0x0350
        L_0x034e:
            r10 = 1114636288(0x42700000, float:60.0)
        L_0x0350:
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r10)
            r2.addView(r3, r5)
            org.telegram.ui.Components.RadialProgressView r2 = new org.telegram.ui.Components.RadialProgressView
            r2.<init>(r1)
            r0.floatingProgressView = r2
            r3 = 1102053376(0x41b00000, float:22.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setSize(r3)
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setProgressColor(r3)
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            r3 = 0
            r2.setAlpha(r3)
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r2.setScaleX(r3)
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            r2.setScaleY(r3)
            org.telegram.ui.Components.RadialProgressView r2 = r0.floatingProgressView
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.Components.RadialProgressView r3 = r0.floatingProgressView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r5)
            r2.addView(r3, r5)
            if (r7 == 0) goto L_0x039d
            r0.restoringState = r4
        L_0x039d:
            r2 = 0
        L_0x039e:
            org.telegram.ui.Components.SlideView[] r3 = r0.views
            int r5 = r3.length
            if (r2 >= r5) goto L_0x0428
            if (r7 == 0) goto L_0x03b9
            if (r2 < r4) goto L_0x03b4
            r5 = 4
            if (r2 > r5) goto L_0x03b4
            int r5 = r0.currentViewNum
            if (r2 != r5) goto L_0x03b9
            r3 = r3[r2]
            r3.restoreStateParams(r7)
            goto L_0x03b9
        L_0x03b4:
            r3 = r3[r2]
            r3.restoreStateParams(r7)
        L_0x03b9:
            int r3 = r0.currentViewNum
            if (r3 != r2) goto L_0x0418
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.ui.Components.SlideView[] r5 = r0.views
            r5 = r5[r2]
            boolean r5 = r5.needBackButton()
            if (r5 != 0) goto L_0x03d0
            boolean r5 = r0.newAccount
            if (r5 == 0) goto L_0x03ce
            goto L_0x03d0
        L_0x03ce:
            r5 = 0
            goto L_0x03d3
        L_0x03d0:
            r5 = 2131165485(0x7var_d, float:1.7945188E38)
        L_0x03d3:
            r3.setBackButtonImage(r5)
            org.telegram.ui.Components.SlideView[] r3 = r0.views
            r3 = r3[r2]
            r5 = 0
            r3.setVisibility(r5)
            org.telegram.ui.Components.SlideView[] r3 = r0.views
            r3 = r3[r2]
            r3.onShow()
            r0.currentDoneType = r5
            if (r2 == r4) goto L_0x03fd
            r3 = 2
            if (r2 == r3) goto L_0x03fd
            r3 = 3
            if (r2 == r3) goto L_0x03fd
            r3 = 4
            if (r2 == r3) goto L_0x03fd
            r3 = 8
            if (r2 != r3) goto L_0x03f8
            r3 = 0
            goto L_0x03fe
        L_0x03f8:
            r3 = 0
            r0.showDoneButton(r4, r3)
            goto L_0x0401
        L_0x03fd:
            r3 = 0
        L_0x03fe:
            r0.showDoneButton(r3, r3)
        L_0x0401:
            if (r2 == r4) goto L_0x0410
            r3 = 2
            if (r2 == r3) goto L_0x0411
            r5 = 3
            if (r2 == r5) goto L_0x0412
            r10 = 4
            if (r2 != r10) goto L_0x040d
            goto L_0x0413
        L_0x040d:
            r12 = 8
            goto L_0x0424
        L_0x0410:
            r3 = 2
        L_0x0411:
            r5 = 3
        L_0x0412:
            r10 = 4
        L_0x0413:
            r0.currentDoneType = r4
            r12 = 8
            goto L_0x0424
        L_0x0418:
            r3 = 2
            r5 = 3
            r10 = 4
            org.telegram.ui.Components.SlideView[] r11 = r0.views
            r11 = r11[r2]
            r12 = 8
            r11.setVisibility(r12)
        L_0x0424:
            int r2 = r2 + 1
            goto L_0x039e
        L_0x0428:
            r2 = 0
            r0.restoringState = r2
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.Components.SlideView[] r3 = r0.views
            int r4 = r0.currentViewNum
            r3 = r3[r4]
            java.lang.String r3 = r3.getHeaderName()
            r2.setTitle(r3)
            android.view.View r2 = r0.fragmentView
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m3184lambda$createView$0$orgtelegramuiLoginActivity(View view) {
        onDoneButtonPressed();
    }

    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    public void onResume() {
        SlideView view;
        int time;
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
                if ((slideViewArr[i] instanceof LoginActivitySmsView) && (time = ((LoginActivitySmsView) slideViewArr[i]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, (Bundle) null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        int i2 = this.currentViewNum;
        if (i2 == 0 && !this.needRequestPermissions && (view = this.views[i2]) != null) {
            view.onShow();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            int i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed((String) null);
            }
        } else if (requestCode == 7) {
            this.checkShowPermissions = false;
            int i2 = this.currentViewNum;
            if (i2 == 0) {
                ((PhoneView) this.views[i2]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
        if (this.newAccount) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, ?> entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String[] args = key.split("_\\|_");
                if (args.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer) value).intValue());
                    }
                } else if (args.length == 2) {
                    Bundle inner = bundle.getBundle(args[0]);
                    if (inner == null) {
                        inner = new Bundle();
                        bundle.putBundle(args[0], inner);
                    }
                    if (value instanceof String) {
                        inner.putString(args[1], (String) value);
                    } else if (value instanceof Integer) {
                        inner.putInt(args[1], ((Integer) value).intValue());
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
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        editor.clear();
        editor.commit();
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String prefix) {
        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, ((Integer) obj).intValue());
                } else {
                    editor.putInt(key, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
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
            } catch (Exception e) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda3(this), 200);
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
            } catch (Exception e2) {
            }
        }
    }

    /* renamed from: lambda$onDialogDismiss$1$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m3186lambda$onDialogDismiss$1$orgtelegramuiLoginActivity() {
        this.needRequestPermissions = false;
    }

    public boolean onBackPressed() {
        int i = this.currentViewNum;
        if (i == 0) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a < slideViewArr.length) {
                    if (slideViewArr[a] != null) {
                        slideViewArr[a].onDestroyActivity();
                    }
                    a++;
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        LoginActivityRegisterView registerView = (LoginActivityRegisterView) this.views[5];
        if (registerView != null) {
            registerView.imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* access modifiers changed from: private */
    public void needShowAlert(String title, String text) {
        if (text != null && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void onFieldError(View view) {
        try {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
        } catch (Throwable th) {
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    public static void needShowInvalidAlert(BaseFragment fragment, String phoneNumber, boolean banned) {
        if (fragment != null && fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (banned) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", NUM));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new LoginActivity$$ExternalSyntheticLambda1(banned, phoneNumber, fragment));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$needShowInvalidAlert$2(boolean banned, String phoneNumber, BaseFragment fragment, DialogInterface dialog, int which) {
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
            Intent mailer = new Intent("android.intent.action.SENDTO");
            mailer.setData(Uri.parse("mailto:"));
            String[] strArr = new String[1];
            strArr[0] = banned ? "recover@telegram.org" : "login@stel.com";
            mailer.putExtra("android.intent.extra.EMAIL", strArr);
            if (banned) {
                mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            fragment.getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
        } catch (Exception e) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", NUM));
            builder2.setMessage(LocaleController.getString("NoMailInstalled", NUM));
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder2.create());
        }
    }

    /* access modifiers changed from: private */
    public void showDoneButton(final boolean show, boolean animated) {
        Interpolator interpolator;
        int duration;
        int i = this.currentDoneType;
        final boolean floating = i == 0;
        if (this.doneButtonVisible[i] != show) {
            AnimatorSet[] animatorSetArr = this.showDoneAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            boolean[] zArr = this.doneButtonVisible;
            int i2 = this.currentDoneType;
            zArr[i2] = show;
            if (animated) {
                this.showDoneAnimation[i2] = new AnimatorSet();
                if (show) {
                    if (floating) {
                        this.floatingButtonContainer.setVisibility(0);
                        this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{0.0f}));
                    } else {
                        this.doneItem.setVisibility(0);
                        this.showDoneAnimation[this.currentDoneType].playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{1.0f})});
                    }
                } else if (floating) {
                    this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{AndroidUtilities.dpf2(70.0f)}));
                } else {
                    this.showDoneAnimation[this.currentDoneType].playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{0.0f})});
                }
                this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (LoginActivity.this.showDoneAnimation[!floating] != null && LoginActivity.this.showDoneAnimation[!floating].equals(animation) && !show) {
                            if (floating) {
                                LoginActivity.this.floatingButtonContainer.setVisibility(8);
                            } else {
                                LoginActivity.this.doneItem.setVisibility(8);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (LoginActivity.this.showDoneAnimation[!floating] != null && LoginActivity.this.showDoneAnimation[!floating].equals(animation)) {
                            LoginActivity.this.showDoneAnimation[!floating] = null;
                        }
                    }
                });
                if (!floating) {
                    duration = 150;
                    interpolator = null;
                } else if (show) {
                    duration = 200;
                    interpolator = AndroidUtilities.decelerateInterpolator;
                } else {
                    duration = 150;
                    interpolator = AndroidUtilities.accelerateInterpolator;
                }
                this.showDoneAnimation[this.currentDoneType].setDuration((long) duration);
                this.showDoneAnimation[this.currentDoneType].setInterpolator(interpolator);
                this.showDoneAnimation[this.currentDoneType].start();
            } else if (show) {
                if (floating) {
                    this.floatingButtonContainer.setVisibility(0);
                    this.floatingButtonContainer.setTranslationY(0.0f);
                    return;
                }
                this.doneItem.setVisibility(0);
                this.doneItem.setScaleX(1.0f);
                this.doneItem.setScaleY(1.0f);
                this.doneItem.setAlpha(1.0f);
            } else if (floating) {
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
                this.views[this.currentViewNum].onNextPressed((String) null);
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

    /* renamed from: lambda$onDoneButtonPressed$3$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m3187lambda$onDoneButtonPressed$3$orgtelegramuiLoginActivity(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        final boolean floating = this.currentDoneType == 0;
        if (animated) {
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.doneProgressView.setTag(1);
                if (floating) {
                    this.floatingProgressView.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(false);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.doneProgressView.setVisibility(0);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{1.0f})});
                }
            } else {
                this.doneProgressView.setTag((Object) null);
                if (floating) {
                    this.floatingButtonIcon.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(true);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.doneItem.getContentView().setVisibility(0);
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
                }
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                        if (floating) {
                            if (!z) {
                                LoginActivity.this.floatingProgressView.setVisibility(4);
                            } else {
                                LoginActivity.this.floatingButtonIcon.setVisibility(4);
                            }
                        } else if (!z) {
                            LoginActivity.this.doneProgressView.setVisibility(4);
                        } else {
                            LoginActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = LoginActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (!z) {
            this.doneProgressView.setTag((Object) null);
            if (floating) {
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
        } else if (floating) {
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
    public void needShowProgress(int reqiestId) {
        needShowProgress(reqiestId, true);
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int reqiestId, boolean animated) {
        this.progressRequestId = reqiestId;
        showEditDoneProgress(true, animated);
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean cancel) {
        needHideProgress(cancel, true);
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean cancel, boolean animated) {
        if (this.progressRequestId != 0) {
            if (cancel) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        showEditDoneProgress(false, animated);
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        final boolean needFloatingButton = page == 0 || page == 5 || page == 6 || page == 7 || page == 9 || page == 10 || page == 11;
        if (needFloatingButton) {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, animated);
            this.currentDoneType = 0;
            showEditDoneProgress(false, false);
            if (!animated) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, animated);
            if (page != 8) {
                this.currentDoneType = 1;
            }
        }
        int i = NUM;
        if (animated) {
            SlideView[] slideViewArr = this.views;
            final SlideView outView = slideViewArr[this.currentViewNum];
            SlideView newView = slideViewArr[page];
            this.currentViewNum = page;
            ActionBar actionBar = this.actionBar;
            if (!newView.needBackButton() && !this.newAccount) {
                i = 0;
            }
            actionBar.setBackButtonImage(i);
            newView.setParams(params, false);
            this.actionBar.setTitle(newView.getHeaderName());
            setParentActivityTitle(newView.getHeaderName());
            newView.onShow();
            int i2 = AndroidUtilities.displaySize.x;
            if (back) {
                i2 = -i2;
            }
            newView.setX((float) i2);
            newView.setVisibility(0);
            AnimatorSet pagesAnimation = new AnimatorSet();
            pagesAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (LoginActivity.this.currentDoneType == 0 && needFloatingButton) {
                        LoginActivity.this.showDoneButton(true, true);
                    }
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            int i3 = AndroidUtilities.displaySize.x;
            if (!back) {
                i3 = -i3;
            }
            fArr[0] = (float) i3;
            animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, new float[]{0.0f});
            pagesAnimation.playTogether(animatorArr);
            pagesAnimation.setDuration(300);
            pagesAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            pagesAnimation.start();
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!this.views[page].needBackButton() && !this.newAccount) {
            i = 0;
        }
        actionBar2.setBackButtonImage(i);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params, false);
        this.views[page].setVisibility(0);
        this.actionBar.setTitle(this.views[page].getHeaderName());
        setParentActivityTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts ? 1 : 0);
            for (int a = 0; a <= this.currentViewNum; a++) {
                SlideView v = this.views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            editor.clear();
            putBundleToEditor(bundle, editor, (String) null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void needFinishActivity(boolean afterSignup, boolean showSetPasswordConfirm, int otherwiseRelogin) {
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false);
                finishFragment();
                return;
            }
            if (!afterSignup || !showSetPasswordConfirm) {
                Bundle args = new Bundle();
                args.putBoolean("afterSignup", afterSignup);
                presentFragment(new DialogsActivity(args), true);
            } else {
                TwoStepVerificationSetupActivity twoStepVerification = new TwoStepVerificationSetupActivity(6, (TLRPC.TL_account_password) null);
                twoStepVerification.setBlockingAlert(otherwiseRelogin);
                presentFragment(twoStepVerification, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        onAuthSuccess(res, false);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res, boolean afterSignup) {
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(res.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkPromoInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        needFinishActivity(afterSignup, res.setup_password_required, res.otherwise_relogin_days);
    }

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeMissedCall) {
            params.putInt("nextType", 11);
        }
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeApp) {
            params.putInt("type", 1);
            params.putInt("length", res.type.length);
            setPage(1, true, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeMissedCall) {
            params.putInt("type", 11);
            params.putInt("length", res.type.length);
            params.putString("prefix", res.type.prefix);
            setPage(11, true, params, false);
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(org.telegram.ui.LoginActivity r23, android.content.Context r24) {
            /*
                r22 = this;
                r1 = r22
                r2 = r23
                r3 = r24
                r1.this$0 = r2
                r1.<init>(r3)
                r0 = 0
                r1.countryState = r0
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r1.countriesArray = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.codesMap = r4
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>()
                r1.phoneFormatMap = r4
                r1.ignoreSelection = r0
                r1.ignoreOnTextChange = r0
                r1.ignoreOnPhoneChange = r0
                r1.nextPressed = r0
                r4 = 1
                r1.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r3)
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
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda8 r8 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda8
                r8.<init>(r1)
                r5.setOnClickListener(r8)
                android.view.View r5 = new android.view.View
                r5.<init>(r3)
                r1.view = r5
                r8 = 1094713344(0x41400000, float:12.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r5.setPadding(r11, r0, r8, r0)
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
                r5.<init>(r3)
                r5.setOrientation(r0)
                r12 = -2
                r13 = 0
                r14 = 1101004800(0x41a00000, float:20.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r5, r8)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r3)
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
                r8.<init>(r3)
                r1.codeField = r8
                r8.setInputType(r10)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r1.codeField
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r3, r0)
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
                r8.setPadding(r13, r0, r0, r0)
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
                r8[r0] = r15
                org.telegram.ui.Components.EditTextBoldCursor r15 = r1.codeField
                r15.setFilters(r8)
                org.telegram.ui.Components.EditTextBoldCursor r15 = r1.codeField
                r16 = 55
                r17 = 36
                r18 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                r19 = 0
                r20 = 1098907648(0x41800000, float:16.0)
                r21 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
                r5.addView(r15, r9)
                org.telegram.ui.Components.EditTextBoldCursor r9 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$1 r15 = new org.telegram.ui.LoginActivity$PhoneView$1
                r15.<init>(r2)
                r9.addTextChangedListener(r15)
                org.telegram.ui.Components.EditTextBoldCursor r9 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10 r15 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10
                r15.<init>(r1)
                r9.setOnEditorActionListener(r15)
                org.telegram.ui.LoginActivity$PhoneView$2 r9 = new org.telegram.ui.LoginActivity$PhoneView$2
                r9.<init>(r3, r2)
                r1.phoneField = r9
                r9.setInputType(r10)
                org.telegram.ui.Components.HintEditText r9 = r1.phoneField
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r9.setTextColor(r15)
                org.telegram.ui.Components.HintEditText r9 = r1.phoneField
                java.lang.String r15 = "windowBackgroundWhiteHintText"
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r9.setHintTextColor(r15)
                org.telegram.ui.Components.HintEditText r9 = r1.phoneField
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r3, r0)
                r9.setBackgroundDrawable(r15)
                org.telegram.ui.Components.HintEditText r9 = r1.phoneField
                r9.setPadding(r0, r0, r0, r0)
                org.telegram.ui.Components.HintEditText r9 = r1.phoneField
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r9.setCursorColor(r7)
                org.telegram.ui.Components.HintEditText r7 = r1.phoneField
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r7.setCursorSize(r9)
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
                r9 = 1108344832(0x42100000, float:36.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
                r5.addView(r6, r7)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$3 r7 = new org.telegram.ui.LoginActivity$PhoneView$3
                r7.<init>(r2)
                r6.addTextChangedListener(r7)
                org.telegram.ui.Components.HintEditText r6 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11
                r7.<init>(r1)
                r6.setOnEditorActionListener(r7)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r3)
                r1.textView2 = r6
                r7 = 2131627926(0x7f0e0var_, float:1.888313E38)
                java.lang.String r9 = "StartText"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
                r6.setText(r7)
                android.widget.TextView r6 = r1.textView2
                java.lang.String r7 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r6.setTextColor(r7)
                android.widget.TextView r6 = r1.textView2
                r7 = 1096810496(0x41600000, float:14.0)
                r6.setTextSize(r4, r7)
                android.widget.TextView r6 = r1.textView2
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0248
                r7 = 5
                goto L_0x0249
            L_0x0248:
                r7 = 3
            L_0x0249:
                r6.setGravity(r7)
                android.widget.TextView r6 = r1.textView2
                r7 = 1073741824(0x40000000, float:2.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                r9 = 1065353216(0x3var_, float:1.0)
                r6.setLineSpacing(r7, r9)
                android.widget.TextView r6 = r1.textView2
                r11 = -2
                r12 = -2
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0264
                r13 = 5
                goto L_0x0265
            L_0x0264:
                r13 = 3
            L_0x0265:
                r14 = 0
                r15 = 28
                r16 = 0
                r17 = 10
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r6, r7)
                boolean r6 = r23.newAccount
                java.lang.String r7 = ""
                r9 = 2
                if (r6 == 0) goto L_0x02b0
                org.telegram.ui.Cells.CheckBoxCell r6 = new org.telegram.ui.Cells.CheckBoxCell
                r6.<init>(r3, r9)
                r1.checkBoxCell = r6
                r11 = 2131628023(0x7f0e0ff7, float:1.8883327E38)
                java.lang.String r12 = "SyncContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                boolean r12 = r23.syncContacts
                r6.setText(r11, r7, r12, r0)
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.checkBoxCell
                r11 = -2
                r12 = -1
                r13 = 51
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r6, r11)
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.checkBoxCell
                org.telegram.ui.LoginActivity$PhoneView$4 r11 = new org.telegram.ui.LoginActivity$PhoneView$4
                r11.<init>(r2)
                r6.setOnClickListener(r11)
            L_0x02b0:
                boolean r6 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
                if (r6 == 0) goto L_0x02e1
                org.telegram.ui.Cells.CheckBoxCell r6 = new org.telegram.ui.Cells.CheckBoxCell
                r6.<init>(r3, r9)
                r1.testBackendCheckBox = r6
                boolean r11 = r23.testBackend
                java.lang.String r12 = "Test Backend"
                r6.setText(r12, r7, r11, r0)
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.testBackendCheckBox
                r11 = -2
                r12 = -1
                r13 = 51
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r1.addView(r6, r11)
                org.telegram.ui.Cells.CheckBoxCell r6 = r1.testBackendCheckBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9 r11 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9
                r11.<init>(r1)
                r6.setOnClickListener(r11)
            L_0x02e1:
                java.util.HashMap r6 = new java.util.HashMap
                r6.<init>()
                java.io.BufferedReader r11 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0342 }
                java.io.InputStreamReader r12 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0342 }
                android.content.res.Resources r13 = r22.getResources()     // Catch:{ Exception -> 0x0342 }
                android.content.res.AssetManager r13 = r13.getAssets()     // Catch:{ Exception -> 0x0342 }
                java.lang.String r14 = "countries.txt"
                java.io.InputStream r13 = r13.open(r14)     // Catch:{ Exception -> 0x0342 }
                r12.<init>(r13)     // Catch:{ Exception -> 0x0342 }
                r11.<init>(r12)     // Catch:{ Exception -> 0x0342 }
            L_0x02fe:
                java.lang.String r12 = r11.readLine()     // Catch:{ Exception -> 0x0342 }
                r13 = r12
                if (r12 == 0) goto L_0x033e
                java.lang.String r12 = ";"
                java.lang.String[] r12 = r13.split(r12)     // Catch:{ Exception -> 0x0342 }
                org.telegram.ui.CountrySelectActivity$Country r14 = new org.telegram.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0342 }
                r14.<init>()     // Catch:{ Exception -> 0x0342 }
                r15 = r12[r9]     // Catch:{ Exception -> 0x0342 }
                r14.name = r15     // Catch:{ Exception -> 0x0342 }
                r15 = r12[r0]     // Catch:{ Exception -> 0x0342 }
                r14.code = r15     // Catch:{ Exception -> 0x0342 }
                r15 = r12[r4]     // Catch:{ Exception -> 0x0342 }
                r14.shortname = r15     // Catch:{ Exception -> 0x0342 }
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r15 = r1.countriesArray     // Catch:{ Exception -> 0x0342 }
                r15.add(r0, r14)     // Catch:{ Exception -> 0x0342 }
                java.util.HashMap<java.lang.String, org.telegram.ui.CountrySelectActivity$Country> r15 = r1.codesMap     // Catch:{ Exception -> 0x0342 }
                r9 = r12[r0]     // Catch:{ Exception -> 0x0342 }
                r15.put(r9, r14)     // Catch:{ Exception -> 0x0342 }
                int r9 = r12.length     // Catch:{ Exception -> 0x0342 }
                if (r9 <= r10) goto L_0x0334
                java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0342 }
                r15 = r12[r0]     // Catch:{ Exception -> 0x0342 }
                r0 = r12[r10]     // Catch:{ Exception -> 0x0342 }
                r9.put(r15, r0)     // Catch:{ Exception -> 0x0342 }
            L_0x0334:
                r0 = r12[r4]     // Catch:{ Exception -> 0x0342 }
                r9 = 2
                r15 = r12[r9]     // Catch:{ Exception -> 0x0342 }
                r6.put(r0, r15)     // Catch:{ Exception -> 0x0342 }
                r0 = 0
                goto L_0x02fe
            L_0x033e:
                r11.close()     // Catch:{ Exception -> 0x0342 }
                goto L_0x0346
            L_0x0342:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0346:
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r0 = r1.countriesArray
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2 r9 = org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2.INSTANCE
                java.util.Comparator r9 = j$.util.Comparator.CC.comparing(r9)
                java.util.Collections.sort(r0, r9)
                r9 = 0
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x035f }
                java.lang.String r10 = "phone"
                java.lang.Object r0 = r0.getSystemService(r10)     // Catch:{ Exception -> 0x035f }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x035f }
                r9 = 0
                goto L_0x0363
            L_0x035f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0363:
                r0 = 10
                if (r9 == 0) goto L_0x036f
                java.lang.String r10 = r9.toUpperCase()
                r1.setCountry(r6, r10)
                goto L_0x0384
            L_0x036f:
                org.telegram.tgnet.TLRPC$TL_help_getNearestDc r10 = new org.telegram.tgnet.TLRPC$TL_help_getNearestDc
                r10.<init>()
                org.telegram.messenger.AccountInstance r11 = r23.getAccountInstance()
                org.telegram.tgnet.ConnectionsManager r11 = r11.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6 r12 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6
                r12.<init>(r1, r6)
                r11.sendRequest(r10, r12, r0)
            L_0x0384:
                org.telegram.ui.Components.EditTextBoldCursor r10 = r1.codeField
                int r10 = r10.length()
                if (r10 != 0) goto L_0x03a2
                android.widget.TextView r10 = r1.countryButton
                r11 = 2131624961(0x7f0e0401, float:1.8877116E38)
                java.lang.String r12 = "ChooseCountry"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r10.setText(r11)
                org.telegram.ui.Components.HintEditText r10 = r1.phoneField
                r11 = 0
                r10.setHintText(r11)
                r1.countryState = r4
            L_0x03a2:
                org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
                int r4 = r4.length()
                if (r4 == 0) goto L_0x03b9
                org.telegram.ui.Components.HintEditText r4 = r1.phoneField
                r4.requestFocus()
                org.telegram.ui.Components.HintEditText r4 = r1.phoneField
                int r10 = r4.length()
                r4.setSelection(r10)
                goto L_0x03be
            L_0x03b9:
                org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
                r4.requestFocus()
            L_0x03be:
                org.telegram.tgnet.TLRPC$TL_help_getCountriesList r4 = new org.telegram.tgnet.TLRPC$TL_help_getCountriesList
                r4.<init>()
                r4.lang_code = r7
                org.telegram.tgnet.ConnectionsManager r7 = r23.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3 r10 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3
                r10.<init>(r1)
                r7.sendRequest(r4, r10, r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3255lambda$new$2$orgtelegramuiLoginActivity$PhoneView(View view2) {
            CountrySelectActivity fragment = new CountrySelectActivity(true, this.countriesArray);
            fragment.setCountrySelectActivityDelegate(new LoginActivity$PhoneView$$ExternalSyntheticLambda7(this));
            this.this$0.presentFragment(fragment);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3253lambda$new$1$orgtelegramuiLoginActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda12(this), 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3252lambda$new$0$orgtelegramuiLoginActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m3256lambda$new$3$orgtelegramuiLoginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m3257lambda$new$4$orgtelegramuiLoginActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3258lambda$new$5$orgtelegramuiLoginActivity$PhoneView(View v) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.testBackend = !loginActivity.testBackend;
                ((CheckBoxCell) v).setChecked(this.this$0.testBackend, true);
            }
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3260lambda$new$8$orgtelegramuiLoginActivity$PhoneView(HashMap languageMap, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda14(this, response, languageMap));
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3259lambda$new$7$orgtelegramuiLoginActivity$PhoneView(TLObject response, HashMap languageMap) {
            if (response != null) {
                TLRPC.TL_nearestDc res = (TLRPC.TL_nearestDc) response;
                if (this.codeField.length() == 0) {
                    setCountry(languageMap, res.country.toUpperCase());
                }
            }
        }

        /* renamed from: lambda$new$10$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3254lambda$new$10$orgtelegramuiLoginActivity$PhoneView(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda16(this, error, response));
        }

        /* renamed from: lambda$new$9$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3261lambda$new$9$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, TLObject response) {
            if (error == null) {
                this.countriesArray.clear();
                this.codesMap.clear();
                this.phoneFormatMap.clear();
                TLRPC.TL_help_countriesList help_countriesList = (TLRPC.TL_help_countriesList) response;
                for (int i = 0; i < help_countriesList.countries.size(); i++) {
                    TLRPC.TL_help_country c = help_countriesList.countries.get(i);
                    for (int k = 0; k < c.country_codes.size(); k++) {
                        CountrySelectActivity.Country countryWithCode = new CountrySelectActivity.Country();
                        countryWithCode.name = c.default_name;
                        countryWithCode.code = c.country_codes.get(k).country_code;
                        this.countriesArray.add(countryWithCode);
                        this.codesMap.put(c.country_codes.get(k).country_code, countryWithCode);
                        if (c.country_codes.get(k).patterns.size() > 0) {
                            this.phoneFormatMap.put(c.country_codes.get(k).country_code, c.country_codes.get(k).patterns.get(0));
                        }
                    }
                }
            }
        }

        public void selectCountry(CountrySelectActivity.Country country) {
            this.ignoreOnTextChange = true;
            String code = country.code;
            this.codeField.setText(code);
            this.countryButton.setText(country.name);
            String hint = this.phoneFormatMap.get(code);
            this.phoneField.setHintText(hint != null ? hint.replace('X', 8211) : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }

        private void setCountry(HashMap<String, String> languageMap, String country) {
            if (languageMap.get(country) != null && this.countriesArray != null) {
                CountrySelectActivity.Country countryWithCode = null;
                int i = 0;
                while (true) {
                    if (i < this.countriesArray.size()) {
                        if (this.countriesArray.get(i) != null && this.countriesArray.get(i).name.equals(country)) {
                            countryWithCode = this.countriesArray.get(i);
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                if (countryWithCode != null) {
                    this.codeField.setText(countryWithCode.code);
                    this.countryState = 0;
                }
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesArray.get(i).code);
            this.ignoreOnTextChange = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed(String code) {
            boolean allowCall;
            boolean isTestBakcend;
            int resId;
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("sim status = " + tm.getSimState());
                }
                int state = tm.getSimState();
                boolean simcardAvailable = (state == 1 || state == 0 || tm.getPhoneType() == 0 || AndroidUtilities.isAirplaneModeOn()) ? false : true;
                boolean allowCancelCall = true;
                boolean allowReadCallLog = true;
                boolean allowReadPhoneNumbers = true;
                if (Build.VERSION.SDK_INT < 23 || !simcardAvailable) {
                    allowCall = true;
                } else {
                    allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    allowCancelCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    allowReadCallLog = Build.VERSION.SDK_INT < 28 || this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                    if (Build.VERSION.SDK_INT >= 26) {
                        allowReadPhoneNumbers = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                    }
                    if (this.this$0.checkPermissions) {
                        this.this$0.permissionsItems.clear();
                        if (!allowCall) {
                            this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowCancelCall) {
                            this.this$0.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        if (!allowReadCallLog) {
                            this.this$0.permissionsItems.add("android.permission.READ_CALL_LOG");
                        }
                        if (!allowReadPhoneNumbers) {
                            this.this$0.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                        }
                        boolean ok = true;
                        if (!this.this$0.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (preferences.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("Contin", NUM), (DialogInterface.OnClickListener) null);
                                if (!allowCall && (!allowCancelCall || !allowReadCallLog)) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndLog", NUM));
                                    resId = NUM;
                                } else if (!allowCancelCall || !allowReadCallLog) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallLog", NUM));
                                    resId = NUM;
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                    resId = NUM;
                                }
                                int i = state;
                                builder.setTopAnimation(resId, 46, false, Theme.getColor("dialogTopBackground"));
                                LoginActivity loginActivity = this.this$0;
                                Dialog unused = loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                            } else {
                                try {
                                    this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[0]), 6);
                                    int i2 = state;
                                } catch (Exception e) {
                                    ok = false;
                                    int i3 = state;
                                }
                            }
                            if (ok) {
                                return;
                            }
                        }
                    }
                }
                int i4 = this.countryState;
                if (i4 == 1) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ChooseCountry", NUM));
                } else if (i4 == 2 && !BuildVars.DEBUG_VERSION) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("WrongCountry", NUM));
                } else if (this.codeField.length() == 0) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (this.phoneField.length() == 0) {
                    this.this$0.onFieldError(this.phoneField);
                } else {
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    boolean isTestBakcend2 = BuildVars.DEBUG_PRIVATE_VERSION && this.this$0.getConnectionsManager().isTestBackend();
                    if (isTestBakcend2 != this.this$0.testBackend) {
                        this.this$0.getConnectionsManager().switchBackend(false);
                        isTestBakcend = this.this$0.testBackend;
                    } else {
                        isTestBakcend = isTestBakcend2;
                    }
                    if (this.this$0.getParentActivity() instanceof LaunchActivity) {
                        for (int a = 0; a < 3; a++) {
                            UserConfig userConfig = UserConfig.getInstance(a);
                            if (userConfig.isClientActivated() && PhoneNumberUtils.compare(phone, userConfig.getCurrentUser().phone)) {
                                if (ConnectionsManager.getInstance(a).isTestBackend() == isTestBakcend) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                                    builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("AccountSwitch", NUM), new LoginActivity$PhoneView$$ExternalSyntheticLambda0(this, a));
                                    builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                    this.this$0.showDialog(builder2.create());
                                    return;
                                }
                            }
                        }
                    }
                    ConnectionsManager.getInstance(this.this$0.currentAccount).cleanup(false);
                    TLRPC.TL_auth_sendCode req = new TLRPC.TL_auth_sendCode();
                    req.api_hash = BuildVars.APP_HASH;
                    req.api_id = BuildVars.APP_ID;
                    req.phone_number = phone;
                    req.settings = new TLRPC.TL_codeSettings();
                    req.settings.allow_flashcall = simcardAvailable && allowCall && allowCancelCall && allowReadCallLog;
                    req.settings.allow_missed_call = simcardAvailable && allowCall;
                    req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    ArrayList<TLRPC.TL_auth_loggedOut> tokens = MessagesController.getSavedLogOutTokens();
                    if (tokens != null) {
                        for (int i5 = 0; i5 < tokens.size(); i5++) {
                            if (req.settings.logout_tokens == null) {
                                req.settings.logout_tokens = new ArrayList<>();
                            }
                            req.settings.logout_tokens.add(tokens.get(i5).future_auth_token);
                        }
                        MessagesController.saveLogOutTokens(tokens);
                    }
                    if (req.settings.logout_tokens != null) {
                        req.settings.flags |= 64;
                    }
                    SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (req.settings.allow_app_hash) {
                        ArrayList<TLRPC.TL_auth_loggedOut> arrayList = tokens;
                        preferences2.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                    } else {
                        preferences2.edit().remove("sms_hash").commit();
                    }
                    if (req.settings.allow_flashcall) {
                        try {
                            String number = tm.getLine1Number();
                            if (!TextUtils.isEmpty(number)) {
                                req.settings.current_number = PhoneNumberUtils.compare(phone, number);
                                if (!req.settings.current_number) {
                                    req.settings.allow_flashcall = false;
                                }
                            } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                req.settings.allow_flashcall = false;
                            } else {
                                req.settings.current_number = false;
                            }
                        } catch (Exception e2) {
                            req.settings.allow_flashcall = false;
                            FileLog.e((Throwable) e2);
                        }
                    }
                    Bundle params = new Bundle();
                    StringBuilder sb = new StringBuilder();
                    sb.append("+");
                    TelephonyManager telephonyManager = tm;
                    sb.append(this.codeField.getText());
                    sb.append(" ");
                    boolean z = simcardAvailable;
                    sb.append(this.phoneField.getText());
                    params.putString("phone", sb.toString());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$PhoneView$$ExternalSyntheticLambda5(this, params, req), 27));
                }
            }
        }

        /* renamed from: lambda$onNextPressed$11$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3262lambda$onNextPressed$11$orgtelegramuiLoginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(num, false);
            }
            this.this$0.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$15$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3266lambda$onNextPressed$15$orgtelegramuiLoginActivity$PhoneView(Bundle params, TLRPC.TL_auth_sendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda15(this, error, params, response, req));
        }

        /* renamed from: lambda$onNextPressed$14$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3265lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_sendCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$PhoneView$$ExternalSyntheticLambda4(this), 10);
                } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.needShowInvalidAlert(this.this$0, req.phone_number, false);
                } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                    LoginActivity.needShowInvalidAlert(this.this$0, req.phone_number, true);
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
                }
            }
            this.this$0.needHideProgress(false);
        }

        /* renamed from: lambda$onNextPressed$13$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3264lambda$onNextPressed$13$orgtelegramuiLoginActivity$PhoneView(TLObject response1, TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda1(this, error1, response1));
        }

        /* renamed from: lambda$onNextPressed$12$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3263lambda$onNextPressed$12$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error1, TLObject response1) {
            this.nextPressed = false;
            this.this$0.showDoneButton(false, true);
            if (error1 == null) {
                TLRPC.TL_account_password password = (TLRPC.TL_account_password) response1;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                    return;
                }
                Bundle bundle = new Bundle();
                SerializedData data = new SerializedData(password.getObjectSize());
                password.serializeToStream(data);
                bundle.putString("password", Utilities.bytesToHex(data.toByteArray()));
                this.this$0.setPage(6, true, bundle, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error1.text);
        }

        public void fillNumber() {
            if (!this.numberFilled) {
                try {
                    TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (tm.getSimState() != 1 && tm.getPhoneType() != 0) {
                        boolean allowCall = true;
                        boolean allowReadPhoneNumbers = true;
                        if (Build.VERSION.SDK_INT >= 23) {
                            allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                            if (Build.VERSION.SDK_INT >= 26) {
                                allowReadPhoneNumbers = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                            }
                            if (this.this$0.checkShowPermissions && (!allowCall || !allowReadPhoneNumbers)) {
                                this.this$0.permissionsShowItems.clear();
                                if (!allowCall) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                                }
                                if (!allowReadPhoneNumbers) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_NUMBERS");
                                }
                                if (!this.this$0.permissionsShowItems.isEmpty()) {
                                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                    if (!preferences.getBoolean("firstloginshow", true)) {
                                        if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                            this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsShowItems.toArray(new String[0]), 7);
                                            return;
                                        }
                                    }
                                    preferences.edit().putBoolean("firstloginshow", false).commit();
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                    builder.setTopAnimation(NUM, 46, false, Theme.getColor("dialogTopBackground"));
                                    builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                                    builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                                    LoginActivity loginActivity = this.this$0;
                                    Dialog unused = loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create());
                                    boolean unused2 = this.this$0.needRequestPermissions = true;
                                    return;
                                }
                                return;
                            }
                        }
                        this.numberFilled = true;
                        if (!this.this$0.newAccount && allowCall && allowReadPhoneNumbers) {
                            String number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                            String textToSet = null;
                            boolean ok = false;
                            if (!TextUtils.isEmpty(number)) {
                                if (number.length() > 4) {
                                    int a = 4;
                                    while (true) {
                                        if (a < 1) {
                                            break;
                                        }
                                        String sub = number.substring(0, a);
                                        if (this.codesMap.get(sub) != null) {
                                            ok = true;
                                            textToSet = number.substring(a);
                                            this.codeField.setText(sub);
                                            break;
                                        }
                                        a--;
                                    }
                                    if (!ok) {
                                        textToSet = number.substring(1);
                                        this.codeField.setText(number.substring(0, 1));
                                    }
                                }
                                if (textToSet != null) {
                                    this.phoneField.requestFocus();
                                    this.phoneField.setText(textToSet);
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
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda13(this), 100);
        }

        /* renamed from: lambda$onShow$16$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m3267lambda$onShow$16$orgtelegramuiLoginActivity$PhoneView() {
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
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("phoneview_code", code);
            }
            String phone = this.phoneField.getText().toString();
            if (phone.length() != 0) {
                bundle.putString("phoneview_phone", phone);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            String code = bundle.getString("phoneview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
            String phone = bundle.getString("phoneview_phone");
            if (phone != null) {
                this.phoneField.setText(phone);
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
        public CodeFieldContainer codeFieldContainer;
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
        private boolean ignoreOnTextChange;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        private int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        /* access modifiers changed from: private */
        public int openTime;
        private String pattern = "*";
        private String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        private String prefix = "";
        private TextView prefixTextView;
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

        static /* synthetic */ int access$4826(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$5426(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.LoginActivity r27, android.content.Context r28, int r29) {
            /*
                r26 = this;
                r0 = r26
                r1 = r27
                r2 = r28
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
                r3 = r29
                r0.currentType = r3
                r4 = 1
                r0.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.confirmTextView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r9, r10)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r12)
                android.widget.TextView r5 = r0.titleTextView
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x007c
                r12 = 5
                goto L_0x007d
            L_0x007c:
                r12 = 3
            L_0x007d:
                r5.setGravity(r12)
                android.widget.TextView r5 = r0.titleTextView
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r12 = (float) r12
                r5.setLineSpacing(r12, r10)
                android.widget.TextView r5 = r0.titleTextView
                r12 = 49
                r5.setGravity(r12)
                int r5 = r0.currentType
                r15 = 11
                if (r5 != r15) goto L_0x0201
                android.widget.TextView r5 = r0.titleTextView
                r15 = 2131626423(0x7f0e09b7, float:1.8880082E38)
                java.lang.String r14 = "MissedCallDescriptionTitle"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r15)
                r5.setText(r14)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.ImageView r14 = new android.widget.ImageView
                r14.<init>(r2)
                android.widget.ImageView r15 = new android.widget.ImageView
                r15.<init>(r2)
                r5.addView(r14)
                r5.addView(r15)
                r12 = 2131165621(0x7var_b5, float:1.7945464E38)
                r14.setImageResource(r12)
                android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
                java.lang.String r16 = "windowBackgroundWhiteInputFieldActivated"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r16)
                android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.SRC_IN
                r12.<init>(r13, r10)
                r14.setColorFilter(r12)
                r10 = 2131165622(0x7var_b6, float:1.7945466E38)
                r15.setImageResource(r10)
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.SRC_IN
                r10.<init>(r12, r13)
                r15.setColorFilter(r10)
                r17 = 64
                r18 = 64
                r19 = 1
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r5, r10)
                android.widget.TextView r10 = r0.titleTextView
                r17 = -2
                r18 = -2
                r19 = 49
                r21 = 8
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r10, r12)
                android.widget.TextView r10 = new android.widget.TextView
                r10.<init>(r2)
                java.lang.String r12 = "windowBackgroundWhiteGrayText"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r10.setTextColor(r13)
                r10.setTextSize(r4, r7)
                r10.setGravity(r4)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r13 = (float) r13
                r8 = 1065353216(0x3var_, float:1.0)
                r10.setLineSpacing(r13, r8)
                r8 = 2131626421(0x7f0e09b5, float:1.8880078E38)
                java.lang.String r13 = "MissedCallDescriptionSubtitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
                android.text.SpannableStringBuilder r8 = org.telegram.messenger.AndroidUtilities.replaceTags(r8)
                r10.setText(r8)
                r18 = -1
                r19 = -2
                r20 = 49
                r21 = 36
                r22 = 16
                r23 = 36
                r24 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
                r0.addView(r10, r8)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$1 r8 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$1
                r8.<init>(r2, r1)
                r0.codeFieldContainer = r8
                android.widget.LinearLayout r8 = new android.widget.LinearLayout
                r8.<init>(r2)
                r13 = 0
                r8.setOrientation(r13)
                android.widget.TextView r13 = new android.widget.TextView
                r13.<init>(r2)
                r0.prefixTextView = r13
                r7 = 1101004800(0x41a00000, float:20.0)
                r13.setTextSize(r4, r7)
                android.widget.TextView r7 = r0.prefixTextView
                r7.setMaxLines(r4)
                android.widget.TextView r7 = r0.prefixTextView
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r7.setTypeface(r11)
                android.widget.TextView r7 = r0.prefixTextView
                r11 = 0
                r7.setPadding(r11, r11, r11, r11)
                android.widget.TextView r7 = r0.prefixTextView
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r7.setTextColor(r9)
                android.widget.TextView r7 = r0.prefixTextView
                r9 = 16
                r7.setGravity(r9)
                android.widget.TextView r7 = r0.prefixTextView
                r20 = -1
                r21 = 16
                r22 = 0
                r23 = 0
                r24 = 4
                r25 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r8.addView(r7, r9)
                org.telegram.ui.CodeFieldContainer r7 = r0.codeFieldContainer
                r9 = -1
                r11 = -2
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
                r8.addView(r7, r9)
                r20 = 34
                r21 = 1
                r23 = 28
                r24 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r0.addView(r8, r7)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r2)
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r7.setTextColor(r9)
                r9 = 1096810496(0x41600000, float:14.0)
                r7.setTextSize(r4, r9)
                r7.setGravity(r4)
                r9 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r9 = (float) r10
                r10 = 1065353216(0x3var_, float:1.0)
                r7.setLineSpacing(r9, r10)
                r9 = 2131626422(0x7f0e09b6, float:1.888008E38)
                java.lang.String r10 = "MissedCallDescriptionSubtitle2"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                android.text.SpannableStringBuilder r9 = org.telegram.messenger.AndroidUtilities.replaceTags(r9)
                r7.setText(r9)
                r19 = -1
                r20 = -2
                r21 = 49
                r22 = 36
                r24 = 36
                r25 = 12
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r0.addView(r7, r9)
                goto L_0x038e
            L_0x0201:
                r7 = 3
                if (r5 != r7) goto L_0x0290
                android.widget.TextView r5 = r0.confirmTextView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x020c
                r7 = 5
                goto L_0x020d
            L_0x020c:
                r7 = 3
            L_0x020d:
                r7 = r7 | 48
                r5.setGravity(r7)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x021d
                r7 = 5
                goto L_0x021e
            L_0x021d:
                r7 = 3
            L_0x021e:
                r8 = -2
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r8, (int) r7)
                r0.addView(r5, r7)
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r8 = 2131165959(0x7var_, float:1.794615E38)
                r7.setImageResource(r8)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x0261
                r9 = 64
                r10 = 1117257728(0x42980000, float:76.0)
                r11 = 19
                r12 = 1073741824(0x40000000, float:2.0)
                r13 = 1073741824(0x40000000, float:2.0)
                r14 = 0
                r15 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r5.addView(r7, r8)
                android.widget.TextView r8 = r0.confirmTextView
                r9 = -1
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0253
                r11 = 5
                goto L_0x0254
            L_0x0253:
                r11 = 3
            L_0x0254:
                r12 = 1118044160(0x42a40000, float:82.0)
                r13 = 0
                r14 = 0
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r5.addView(r8, r9)
                goto L_0x028e
            L_0x0261:
                android.widget.TextView r8 = r0.confirmTextView
                r9 = -1
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x026c
                r11 = 5
                goto L_0x026d
            L_0x026c:
                r11 = 3
            L_0x026d:
                r12 = 0
                r13 = 0
                r14 = 1118044160(0x42a40000, float:82.0)
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r5.addView(r8, r9)
                r19 = 64
                r20 = 1117257728(0x42980000, float:76.0)
                r21 = 21
                r22 = 0
                r23 = 1073741824(0x40000000, float:2.0)
                r24 = 0
                r25 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
                r5.addView(r7, r8)
            L_0x028e:
                goto L_0x038e
            L_0x0290:
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 49
                r5.setGravity(r7)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                r8 = -2
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r8, (int) r7)
                r0.addView(r5, r10)
                int r7 = r0.currentType
                java.lang.String r8 = "chats_actionBackground"
                if (r7 != r4) goto L_0x0314
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r0.blackImageView = r7
                r10 = 2131166106(0x7var_a, float:1.7946448E38)
                r7.setImageResource(r10)
                android.widget.ImageView r7 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
                r10.<init>(r9, r11)
                r7.setColorFilter(r10)
                android.widget.ImageView r7 = r0.blackImageView
                r9 = -2
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 51
                r12 = 0
                r13 = 0
                r14 = 0
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r5.addView(r7, r9)
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131166104(0x7var_, float:1.7946444E38)
                r7.setImageResource(r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r8, r10)
                r7.setColorFilter(r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                r8 = -2
                r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r10 = 51
                r11 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r5.addView(r7, r8)
                android.widget.TextView r7 = r0.titleTextView
                r8 = 2131627744(0x7f0e0ee0, float:1.8882761E38)
                java.lang.String r9 = "SentAppCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r7.setText(r8)
                goto L_0x0370
            L_0x0314:
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
                r10 = 2131558494(0x7f0d005e, float:1.8742305E38)
                r9 = 1115684864(0x42800000, float:64.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r14 = 1
                r15 = 0
                java.lang.String r11 = "NUM"
                r9 = r7
                r9.<init>(r10, r11, r12, r13, r14, r15)
                r0.hintDrawable = r7
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                java.lang.String r10 = "Bubble.**"
                r7.setLayerColor(r10, r9)
                org.telegram.ui.Components.RLottieDrawable r7 = r0.hintDrawable
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                java.lang.String r9 = "Phone.**"
                r7.setLayerColor(r9, r8)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                org.telegram.ui.Components.RLottieDrawable r8 = r0.hintDrawable
                r7.setAnimation(r8)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                r8 = 64
                r9 = 1115684864(0x42800000, float:64.0)
                r10 = 51
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r5.addView(r7, r8)
                android.widget.TextView r7 = r0.titleTextView
                r8 = 2131627748(0x7f0e0ee4, float:1.888277E38)
                java.lang.String r9 = "SentSmsCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r7.setText(r8)
            L_0x0370:
                android.widget.TextView r7 = r0.titleTextView
                r8 = -2
                r9 = -2
                r10 = 49
                r11 = 0
                r12 = 18
                r13 = 0
                r14 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r7, r8)
                android.widget.TextView r7 = r0.confirmTextView
                r8 = -2
                r12 = 17
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r7, r8)
            L_0x038e:
                int r5 = r0.currentType
                r7 = 11
                if (r5 == r7) goto L_0x03a5
                org.telegram.ui.LoginActivity$LoginActivitySmsView$2 r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$2
                r5.<init>(r2, r1)
                r0.codeFieldContainer = r5
                r7 = 42
                r8 = -2
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r7, (int) r4)
                r0.addView(r5, r7)
            L_0x03a5:
                int r5 = r0.currentType
                r7 = 3
                if (r5 != r7) goto L_0x03b1
                org.telegram.ui.CodeFieldContainer r5 = r0.codeFieldContainer
                r7 = 8
                r5.setVisibility(r7)
            L_0x03b1:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$3 r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$3
                r5.<init>(r2, r1)
                r0.timeText = r5
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.timeText
                r6 = 1073741824(0x40000000, float:2.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r7
                r7 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r6, r7)
                int r5 = r0.currentType
                r6 = 1097859072(0x41700000, float:15.0)
                r7 = 1092616192(0x41200000, float:10.0)
                r8 = 3
                if (r5 != r8) goto L_0x0412
                android.widget.TextView r5 = r0.timeText
                r8 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r8)
                android.widget.TextView r5 = r0.timeText
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x03e5
                r8 = 5
                goto L_0x03e6
            L_0x03e5:
                r8 = 3
            L_0x03e6:
                r9 = -2
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r9, (int) r8)
                r0.addView(r5, r8)
                org.telegram.ui.LoginActivity$ProgressView r5 = new org.telegram.ui.LoginActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                android.widget.TextView r5 = r0.timeText
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x03fd
                r13 = 5
                goto L_0x03fe
            L_0x03fd:
                r13 = 3
            L_0x03fe:
                r5.setGravity(r13)
                org.telegram.ui.LoginActivity$ProgressView r5 = r0.progressView
                r8 = -1
                r9 = 3
                r10 = 0
                r11 = 1094713344(0x41400000, float:12.0)
                r12 = 0
                r13 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r9, r10, r11, r12, r13)
                r0.addView(r5, r8)
                goto L_0x0438
            L_0x0412:
                android.widget.TextView r5 = r0.timeText
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r10 = 0
                r5.setPadding(r10, r9, r10, r8)
                android.widget.TextView r5 = r0.timeText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.timeText
                r8 = 49
                r5.setGravity(r8)
                android.widget.TextView r5 = r0.timeText
                r9 = -2
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r9, (int) r8)
                r0.addView(r5, r10)
            L_0x0438:
                org.telegram.ui.LoginActivity$LoginActivitySmsView$4 r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$4
                r5.<init>(r2, r1)
                r0.problemText = r5
                java.lang.String r8 = "windowBackgroundWhiteBlueText4"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r5.setTextColor(r8)
                android.widget.TextView r5 = r0.problemText
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r9, r10)
                android.widget.TextView r5 = r0.problemText
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r9 = 0
                r5.setPadding(r9, r8, r9, r7)
                android.widget.TextView r5 = r0.problemText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.problemText
                r6 = 49
                r5.setGravity(r6)
                int r5 = r0.currentType
                if (r5 != r4) goto L_0x049f
                int r4 = r0.nextType
                r5 = 3
                if (r4 == r5) goto L_0x0490
                r5 = 4
                if (r4 == r5) goto L_0x0490
                r5 = 11
                if (r4 != r5) goto L_0x0481
                goto L_0x0490
            L_0x0481:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625277(0x7f0e053d, float:1.8877757E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x04ad
            L_0x0490:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625276(0x7f0e053c, float:1.8877755E38)
                java.lang.String r6 = "DidNotGetTheCodPhone"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x04ad
            L_0x049f:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625275(0x7f0e053b, float:1.8877753E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x04ad:
                android.widget.TextView r4 = r0.problemText
                r5 = 49
                r6 = -2
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r6, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.problemText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1
                r5.<init>(r0)
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3239lambda$new$0$orgtelegramuiLoginActivity$LoginActivitySmsView(View v) {
            if (!this.nextPressed) {
                int i = this.nextType;
                if ((i == 4 && this.currentType == 2) || i == 0) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                        Intent mailer = new Intent("android.intent.action.SENDTO");
                        mailer.setData(Uri.parse("mailto:"));
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                        mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                        mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                    } catch (Exception e) {
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
                    }
                } else if (this.this$0.doneProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            RLottieImageView rLottieImageView;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.currentType != 3 && (rLottieImageView = this.blueImageView) != null) {
                int innerHeight = rLottieImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (this.this$0.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), Math.min(this.this$0.scrollHeight, maxHeight));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int t2;
            super.onLayout(changed, l, t, r, b);
            if (this.currentType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int h = this.problemText.getMeasuredHeight();
                    t2 = (bottom + height) - h;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
                } else {
                    t2 = bottom + height;
                }
                int h3 = this.codeFieldContainer.getMeasuredHeight();
                int t3 = (((t2 - bottom) - h3) / 2) + bottom;
                CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
                codeFieldContainer2.layout(codeFieldContainer2.getLeft(), t3, this.codeFieldContainer.getRight(), t3 + h3);
                int height2 = t3;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(this, params), 10));
        }

        /* renamed from: lambda$resendCode$2$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3247xfa0321f9(Bundle params, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(this, error, params, response));
        }

        /* renamed from: lambda$resendCode$1$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3246xcc2a879a(TLRPC.TL_error error, Bundle params, TLObject response) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    this.this$0.setPage(0, true, (Bundle) null, true);
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    LoginActivity loginActivity = this.this$0;
                    String string = LocaleController.getString("AppName", NUM);
                    loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                }
            }
            this.this$0.needHideProgress(false);
        }

        public String getHeaderName() {
            int i = this.currentType;
            if (i == 1 || i == 11) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        public boolean needBackButton() {
            return true;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
                this.waitingForEvent = true;
                int i3 = this.currentType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.emailPhone = bundle.getString("ephone");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.time = bundle.getInt("timeout");
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                this.prefix = bundle.getString("prefix");
                int i4 = bundle.getInt("length");
                this.length = i4;
                if (i4 == 0) {
                    this.length = 5;
                }
                this.codeFieldContainer.setNumbersCount(this.length, this.currentType);
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    int i5 = this.currentType;
                    if (i5 == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
                    } else if (i5 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(number)));
                    } else if (i5 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(number)));
                    } else if (i5 == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[0]);
                        this.codeFieldContainer.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeFieldContainer.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    int i6 = this.currentType;
                    if (i6 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (i6 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i7 = this.nextType;
                        if (i7 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, 1, 0));
                        } else if (i7 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, 1, 0));
                        }
                        String callLogNumber = restore ? AndroidUtilities.obtainLoginPhoneCall(this.pattern) : null;
                        if (callLogNumber != null) {
                            onNextPressed(callLogNumber);
                        } else {
                            String str2 = this.catchedPhone;
                            if (str2 != null) {
                                onNextPressed(str2);
                            } else {
                                createTimer();
                            }
                        }
                    } else if (i6 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        String hash = preferences.getString("sms_hash", (String) null);
                        String savedCode = null;
                        if (!TextUtils.isEmpty(hash)) {
                            String savedCode2 = preferences.getString("sms_hash_code", (String) null);
                            if (savedCode2 != null) {
                                if (savedCode2.contains(hash + "|")) {
                                    savedCode = savedCode2.substring(savedCode2.indexOf(124) + 1);
                                }
                            }
                            savedCode = null;
                        }
                        if (savedCode != null) {
                            this.codeFieldContainer.setCode(savedCode);
                            onNextPressed((String) null);
                        } else {
                            createTimer();
                        }
                    } else if (i6 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                    if (this.currentType == 11) {
                        String pref = this.prefix;
                        for (int i8 = 0; i8 < this.length; i8++) {
                            pref = pref + "0";
                        }
                        String pref2 = PhoneFormat.getInstance().format("+" + pref);
                        for (int i9 = 0; i9 < this.length; i9++) {
                            int index = pref2.lastIndexOf("0");
                            if (index >= 0) {
                                pref2 = pref2.substring(0, index);
                            }
                        }
                        this.prefixTextView.setText(pref2.replaceAll("\\)", "").replaceAll("\\(", ""));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0(this));
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m3248xvar_b283() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$4700 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView.access$4826(LoginActivitySmsView.this, currentTime - access$4700);
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
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* renamed from: lambda$run$2$org-telegram-ui-LoginActivity$LoginActivitySmsView$6  reason: not valid java name */
                    public /* synthetic */ void m3251x25bbafc2() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$5300 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCurrentTime = currentTime;
                        LoginActivitySmsView.access$5426(LoginActivitySmsView.this, currentTime - access$5300);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int minutes = (LoginActivitySmsView.this.time / 1000) / 60;
                            int seconds = (LoginActivitySmsView.this.time / 1000) - (minutes * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
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
                                TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                                req.phone_number = LoginActivitySmsView.this.requestPhone;
                                req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda2(this), 10);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                boolean unused3 = LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    /* renamed from: lambda$run$1$org-telegram-ui-LoginActivity$LoginActivitySmsView$6  reason: not valid java name */
                    public /* synthetic */ void m3250xba03123(TLObject response, TLRPC.TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda1(this, error));
                        }
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$6  reason: not valid java name */
                    public /* synthetic */ void m3249xvar_b284(TLRPC.TL_error error) {
                        String unused = LoginActivitySmsView.this.lastError = error.text;
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

        public void onNextPressed(String code) {
            if (this.this$0.currentViewNum == 11) {
                if (this.nextPressed) {
                    return;
                }
            } else if (this.nextPressed || this.this$0.currentViewNum < 1 || this.this$0.currentViewNum > 4) {
                return;
            }
            if (code == null) {
                code = this.codeFieldContainer.getCode();
            }
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
            TLRPC.TL_auth_signIn req = new TLRPC.TL_auth_signIn();
            req.phone_number = this.requestPhone;
            req.phone_code = code;
            req.phone_code_hash = this.phoneHash;
            destroyTimer();
            this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this, req), 10), false);
            this.this$0.showDoneButton(true, true);
        }

        /* renamed from: lambda$onNextPressed$6$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3244xdCLASSNAMEc7(TLRPC.TL_auth_signIn req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(this, error, response, req));
        }

        /* renamed from: lambda$onNextPressed$5$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3243xae6e8var_(TLRPC.TL_error error, TLObject response, TLRPC.TL_auth_signIn req) {
            int i;
            int i2;
            boolean ok = false;
            if (error == null) {
                this.nextPressed = false;
                ok = true;
                this.this$0.showDoneButton(false, true);
                destroyTimer();
                destroyCodeTimer();
                if (response instanceof TLRPC.TL_auth_authorizationSignUpRequired) {
                    TLRPC.TL_auth_authorizationSignUpRequired authorization = (TLRPC.TL_auth_authorizationSignUpRequired) response;
                    if (authorization.terms_of_service != null) {
                        TLRPC.TL_help_termsOfService unused = this.this$0.currentTermsOfService = authorization.terms_of_service;
                    }
                    Bundle params = new Bundle();
                    params.putString("phoneFormated", this.requestPhone);
                    params.putString("phoneHash", this.phoneHash);
                    params.putString("code", req.phone_code);
                    this.this$0.setPage(5, true, params, false);
                } else {
                    this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
                }
            } else {
                this.lastError = error.text;
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ok = true;
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7(this, req), 10);
                    destroyTimer();
                    destroyCodeTimer();
                } else {
                    this.nextPressed = false;
                    this.this$0.showDoneButton(false, true);
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
                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                            for (CodeNumberField text : this.codeFieldContainer.codeField) {
                                text.setText("");
                            }
                            this.codeFieldContainer.codeField[0].requestFocus();
                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                        } else {
                            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                        }
                    }
                }
            }
            if (ok && this.currentType == 3) {
                AndroidUtilities.endIncomingCall();
            }
        }

        /* renamed from: lambda$onNextPressed$4$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3242x8095var_(TLRPC.TL_auth_signIn req, TLObject response1, TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this, error1, response1, req));
        }

        /* renamed from: lambda$onNextPressed$3$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3241x52bd5aaa(TLRPC.TL_error error1, TLObject response1, TLRPC.TL_auth_signIn req) {
            this.nextPressed = false;
            this.this$0.showDoneButton(false, true);
            if (error1 == null) {
                TLRPC.TL_account_password password = (TLRPC.TL_account_password) response1;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                    return;
                }
                Bundle bundle = new Bundle();
                SerializedData data = new SerializedData(password.getObjectSize());
                password.serializeToStream(data);
                bundle.putString("password", Utilities.bytesToHex(data.toByteArray()));
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", req.phone_code);
                this.this$0.setPage(6, true, bundle, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error1.text);
        }

        public boolean onBackPressed(boolean force) {
            if (!force) {
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
            TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9.INSTANCE, 10);
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

        /* renamed from: lambda$onBackPressed$7$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3240x9813b81a(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        static /* synthetic */ void lambda$onBackPressed$8(TLObject response, TLRPC.TL_error error) {
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
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this), 100);
            }
        }

        /* renamed from: lambda$onShow$9$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m3245x7b78afde() {
            if (this.codeFieldContainer.codeField != null) {
                int a = this.codeFieldContainer.codeField.length - 1;
                while (true) {
                    if (a < 0) {
                        break;
                    } else if (a == 0 || this.codeFieldContainer.codeField[a].length() != 0) {
                        this.codeFieldContainer.codeField[a].requestFocus();
                        this.codeFieldContainer.codeField[a].setSelection(this.codeFieldContainer.codeField[a].length());
                        AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[a]);
                    } else {
                        a--;
                    }
                }
                this.codeFieldContainer.codeField[a].requestFocus();
                this.codeFieldContainer.codeField[a].setSelection(this.codeFieldContainer.codeField[a].length());
                AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[a]);
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.waitingForEvent && this.codeFieldContainer.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.codeFieldContainer.setText("" + args[0]);
                    onNextPressed((String) null);
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = "" + args[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                        if (!this.pattern.equals("*")) {
                            this.catchedPhone = num;
                            AndroidUtilities.endIncomingCall();
                        }
                        onNextPressed(num);
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
            String catched = bundle.getString("catchedPhone");
            if (catched != null) {
                this.catchedPhone = catched;
            }
            String code = bundle.getString("smsview_code_" + this.currentType);
            if (!(code == null || this.codeFieldContainer.codeField == null)) {
                this.codeFieldContainer.setText(code);
            }
            int t = bundle.getInt("time");
            if (t != 0) {
                this.time = t;
            }
            int t2 = bundle.getInt("open");
            if (t2 != 0) {
                this.openTime = t2;
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
        public TLRPC.TL_account_password currentPassword;
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityPasswordView(org.telegram.ui.LoginActivity r21, android.content.Context r22) {
            /*
                r20 = this;
                r0 = r20
                r1 = r22
                r2 = r21
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r7 == 0) goto L_0x0030
                r7 = 5
                goto L_0x0031
            L_0x0030:
                r7 = 3
            L_0x0031:
                r4.setGravity(r7)
                android.widget.TextView r4 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r11 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r10, r11)
                android.widget.TextView r4 = r0.confirmTextView
                r10 = 2131626204(0x7f0e08dc, float:1.8879638E38)
                java.lang.String r12 = "LoginPasswordText"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
                r4.setText(r10)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x0058
                r10 = 5
                goto L_0x0059
            L_0x0058:
                r10 = 3
            L_0x0059:
                r12 = -2
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                r0.addView(r4, r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = new org.telegram.ui.Components.EditTextBoldCursor
                r4.<init>(r1)
                r0.codeField = r4
                java.lang.String r10 = "windowBackgroundWhiteBlackText"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r4.setTextColor(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r4.setCursorColor(r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r10 = 1101004800(0x41a00000, float:20.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r4.setCursorSize(r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r10 = 1069547520(0x3fCLASSNAME, float:1.5)
                r4.setCursorWidth(r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                java.lang.String r10 = "windowBackgroundWhiteHintText"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r4.setHintTextColor(r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r10 = 0
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r10)
                r4.setBackgroundDrawable(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r13 = 2131626203(0x7f0e08db, float:1.8879636E38)
                java.lang.String r14 = "LoginPassword"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r4.setHint(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r13 = 268435461(0x10000005, float:2.5243564E-29)
                r4.setImeOptions(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r13 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r4.setMaxLines(r3)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r4.setPadding(r10, r10, r10, r10)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r13 = 129(0x81, float:1.81E-43)
                r4.setInputType(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                android.text.method.PasswordTransformationMethod r13 = android.text.method.PasswordTransformationMethod.getInstance()
                r4.setTransformationMethod(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
                r4.setTypeface(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x00e7
                r13 = 5
                goto L_0x00e8
            L_0x00e7:
                r13 = 3
            L_0x00e8:
                r4.setGravity(r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r13 = -1
                r14 = 36
                r15 = 1
                r16 = 0
                r17 = 20
                r18 = 0
                r19 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r4, r13)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10 r13 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10
                r13.<init>(r0)
                r4.setOnEditorActionListener(r13)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.cancelButton = r4
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x0117
                r13 = 5
                goto L_0x0118
            L_0x0117:
                r13 = 3
            L_0x0118:
                r13 = r13 | 48
                r4.setGravity(r13)
                android.widget.TextView r4 = r0.cancelButton
                java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r4.setTextColor(r13)
                android.widget.TextView r4 = r0.cancelButton
                r13 = 2131625701(0x7f0e06e5, float:1.8878617E38)
                java.lang.String r14 = "ForgotPassword"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r4.setText(r13)
                android.widget.TextView r4 = r0.cancelButton
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.cancelButton
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r13 = (float) r13
                r4.setLineSpacing(r13, r11)
                android.widget.TextView r4 = r0.cancelButton
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r4.setPadding(r10, r13, r10, r10)
                android.widget.TextView r4 = r0.cancelButton
                r13 = -1
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x0157
                r14 = 5
                goto L_0x0158
            L_0x0157:
                r14 = 3
            L_0x0158:
                r14 = r14 | 48
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r12, (int) r14)
                r0.addView(r4, r12)
                android.widget.TextView r4 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8 r12 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8
                r12.<init>(r0)
                r4.setOnClickListener(r12)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.resetAccountButton = r4
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0178
                r12 = 5
                goto L_0x0179
            L_0x0178:
                r12 = 3
            L_0x0179:
                r12 = r12 | 48
                r4.setGravity(r12)
                android.widget.TextView r4 = r0.resetAccountButton
                java.lang.String r12 = "windowBackgroundWhiteRedText6"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r4.setTextColor(r12)
                android.widget.TextView r4 = r0.resetAccountButton
                r12 = 8
                r4.setVisibility(r12)
                android.widget.TextView r4 = r0.resetAccountButton
                r13 = 2131627539(0x7f0e0e13, float:1.8882345E38)
                java.lang.String r14 = "ResetMyAccount"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r4.setText(r13)
                android.widget.TextView r4 = r0.resetAccountButton
                java.lang.String r13 = "fonts/rmedium.ttf"
                android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
                r4.setTypeface(r13)
                android.widget.TextView r4 = r0.resetAccountButton
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.resetAccountButton
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r13 = (float) r13
                r4.setLineSpacing(r13, r11)
                android.widget.TextView r4 = r0.resetAccountButton
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r4.setPadding(r10, r13, r10, r10)
                android.widget.TextView r4 = r0.resetAccountButton
                r13 = -2
                r14 = -2
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x01cb
                r10 = 5
                goto L_0x01cc
            L_0x01cb:
                r10 = 3
            L_0x01cc:
                r15 = r10 | 48
                r16 = 0
                r17 = 34
                r18 = 0
                r19 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r4, r10)
                android.widget.TextView r4 = r0.resetAccountButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9 r10 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9
                r10.<init>(r0)
                r4.setOnClickListener(r10)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.resetAccountText = r4
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x01f4
                r10 = 5
                goto L_0x01f5
            L_0x01f4:
                r10 = 3
            L_0x01f5:
                r10 = r10 | 48
                r4.setGravity(r10)
                android.widget.TextView r4 = r0.resetAccountText
                r4.setVisibility(r12)
                android.widget.TextView r4 = r0.resetAccountText
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.resetAccountText
                r5 = 2131627540(0x7f0e0e14, float:1.8882347E38)
                java.lang.String r10 = "ResetMyAccountText"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
                r4.setText(r5)
                android.widget.TextView r4 = r0.resetAccountText
                r4.setTextSize(r3, r6)
                android.widget.TextView r3 = r0.resetAccountText
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r4 = (float) r4
                r3.setLineSpacing(r4, r11)
                android.widget.TextView r3 = r0.resetAccountText
                r10 = -2
                r11 = -2
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x022e
                goto L_0x022f
            L_0x022e:
                r8 = 3
            L_0x022f:
                r12 = r8 | 48
                r13 = 0
                r14 = 7
                r15 = 0
                r16 = 14
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r3, r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ boolean m3197x81c2d861(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3201x1812fCLASSNAME(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                if (this.currentPassword.has_recovery) {
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3(this), 10);
                    return;
                }
                this.resetAccountText.setVisibility(0);
                this.resetAccountButton.setVisibility(0);
                AndroidUtilities.hideKeyboard(this.codeField);
                this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEitle", NUM), LocaleController.getString("RestorePasswordNoEmailText", NUM));
            }
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3200xvar_evar_(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda15(this, error, response));
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3199xcceaea63(TLRPC.TL_error error, TLObject response) {
            String timeString;
            this.this$0.needHideProgress(false);
            if (error == null) {
                TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, res.email_pattern));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7(this, res));
                Dialog dialog = this.this$0.showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            } else if (error.text.startsWith("FLOOD_WAIT")) {
                int time = Utilities.parseInt(error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3198xa756e162(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
            bundle.putString("password", this.passwordString);
            this.this$0.setPage(7, true, bundle, false);
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3205xae632069(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
            }
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3204x88cvar_(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4(this), 10);
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3203x633b0e67(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda14(this, error));
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3202x3da70566(TLRPC.TL_error error) {
            this.this$0.needHideProgress(false);
            if (error == null) {
                Bundle params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                Bundle params2 = new Bundle();
                params2.putString("phoneFormated", this.requestPhone);
                params2.putString("phoneHash", this.phoneHash);
                params2.putString("code", this.phoneCode);
                params2.putInt("startTime", ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime());
                params2.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                this.this$0.setPage(8, true, params2, false);
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                if (params.isEmpty()) {
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
                this.codeField.setText("");
                this.currentParams = params;
                String string = params.getString("password");
                this.passwordString = string;
                if (string != null) {
                    SerializedData data = new SerializedData(Utilities.hexToBytes(this.passwordString));
                    this.currentPassword = TLRPC.TL_account_password.TLdeserialize(data, data.readInt32(false), false);
                }
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                TLRPC.TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password == null || TextUtils.isEmpty(tL_account_password.hint)) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", NUM));
                } else {
                    this.codeField.setHint(this.currentPassword.hint);
                }
            }
        }

        private void onPasscodeError(boolean clear) {
            if (this.this$0.getParentActivity() != null) {
                if (clear) {
                    this.codeField.setText("");
                }
                this.this$0.onFieldError(this.confirmTextView);
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                String oldPassword = this.codeField.getText().toString();
                if (oldPassword.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12(this, oldPassword));
            }
        }

        /* renamed from: lambda$onNextPressed$14$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3210x129fe286(String oldPassword) {
            byte[] passwordBytes;
            TLRPC.PasswordKdfAlgo current_algo = this.currentPassword.current_algo;
            if (current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                passwordBytes = SRPHelper.getX(AndroidUtilities.getStringBytes(oldPassword), (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo);
            } else {
                passwordBytes = null;
            }
            TLRPC.TL_auth_checkPassword req = new TLRPC.TL_auth_checkPassword();
            RequestDelegate requestDelegate = new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6(this);
            if (current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                req.password = SRPHelper.startCheck(passwordBytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo);
                if (req.password == null) {
                    TLRPC.TL_error error = new TLRPC.TL_error();
                    error.text = "PASSWORD_HASH_INVALID";
                    requestDelegate.run((TLObject) null, error);
                    return;
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, requestDelegate, 10);
            }
        }

        /* renamed from: lambda$onNextPressed$13$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3209xed0bd985(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1(this, error, response));
        }

        /* renamed from: lambda$onNextPressed$12$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3208xCLASSNAMEd084(TLRPC.TL_error error, TLObject response) {
            String timeString;
            this.nextPressed = false;
            if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5(this), 8);
            } else if (response instanceof TLRPC.TL_auth_authorization) {
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13(this, response), 150);
            } else {
                this.this$0.needHideProgress(false);
                if (error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                } else {
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$10$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3206x7c4fbe82(TLObject response2, TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2(this, error2, response2));
        }

        /* renamed from: lambda$onNextPressed$9$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3211xe97e9e5a(TLRPC.TL_error error2, TLObject response2) {
            if (error2 == null) {
                this.currentPassword = (TLRPC.TL_account_password) response2;
                onNextPressed((String) null);
            }
        }

        /* renamed from: lambda$onNextPressed$11$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3207xa1e3CLASSNAME(TLObject response) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.codeField);
            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        public boolean needBackButton() {
            return true;
        }

        public boolean onBackPressed(boolean force) {
            this.nextPressed = false;
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11(this), 100);
        }

        /* renamed from: lambda$onShow$15$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m3212x8b90fvar_() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.codeField);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("passview_code", code);
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
            String code = bundle.getString("passview_code");
            if (code != null) {
                this.codeField.setText(code);
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityResetWaitView(org.telegram.ui.LoginActivity r20, android.content.Context r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r21
                r2 = r20
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r7 == 0) goto L_0x0030
                r7 = 5
                goto L_0x0031
            L_0x0030:
                r7 = 3
            L_0x0031:
                r4.setGravity(r7)
                android.widget.TextView r4 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r11 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r10, r11)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x004a
                r10 = 5
                goto L_0x004b
            L_0x004a:
                r10 = 3
            L_0x004b:
                r12 = -2
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                r0.addView(r4, r10)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.resetAccountText = r4
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x0060
                r10 = 5
                goto L_0x0061
            L_0x0060:
                r10 = 3
            L_0x0061:
                r10 = r10 | 48
                r4.setGravity(r10)
                android.widget.TextView r4 = r0.resetAccountText
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r10)
                android.widget.TextView r4 = r0.resetAccountText
                r10 = 2131627530(0x7f0e0e0a, float:1.8882327E38)
                java.lang.String r12 = "ResetAccountStatus"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
                r4.setText(r10)
                android.widget.TextView r4 = r0.resetAccountText
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.resetAccountText
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r4.setLineSpacing(r10, r11)
                android.widget.TextView r4 = r0.resetAccountText
                r12 = -2
                r13 = -2
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x0096
                r10 = 5
                goto L_0x0097
            L_0x0096:
                r10 = 3
            L_0x0097:
                r14 = r10 | 48
                r15 = 0
                r16 = 24
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r4, r10)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.resetAccountTime = r4
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x00b4
                r10 = 5
                goto L_0x00b5
            L_0x00b4:
                r10 = 3
            L_0x00b5:
                r10 = r10 | 48
                r4.setGravity(r10)
                android.widget.TextView r4 = r0.resetAccountTime
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.resetAccountTime
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.resetAccountTime
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r11)
                android.widget.TextView r4 = r0.resetAccountTime
                r12 = -2
                r13 = -2
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00dc
                r5 = 5
                goto L_0x00dd
            L_0x00dc:
                r5 = 3
            L_0x00dd:
                r14 = r5 | 48
                r15 = 0
                r16 = 2
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r4, r5)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.resetAccountButton = r4
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00fa
                r5 = 5
                goto L_0x00fb
            L_0x00fa:
                r5 = 3
            L_0x00fb:
                r5 = r5 | 48
                r4.setGravity(r5)
                android.widget.TextView r4 = r0.resetAccountButton
                r5 = 2131627527(0x7f0e0e07, float:1.888232E38)
                java.lang.String r10 = "ResetAccountButton"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
                r4.setText(r5)
                android.widget.TextView r4 = r0.resetAccountButton
                java.lang.String r5 = "fonts/rmedium.ttf"
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                r4.setTypeface(r5)
                android.widget.TextView r4 = r0.resetAccountButton
                r4.setTextSize(r3, r6)
                android.widget.TextView r3 = r0.resetAccountButton
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r4 = (float) r4
                r3.setLineSpacing(r4, r11)
                android.widget.TextView r3 = r0.resetAccountButton
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r5 = 0
                r3.setPadding(r5, r4, r5, r5)
                android.widget.TextView r3 = r0.resetAccountButton
                r10 = -2
                r11 = -2
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x013b
                goto L_0x013c
            L_0x013b:
                r8 = 3
            L_0x013c:
                r12 = r8 | 48
                r13 = 0
                r14 = 7
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r3, r4)
                android.widget.TextView r3 = r0.resetAccountButton
                org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1
                r4.<init>(r0)
                r3.setOnClickListener(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityResetWaitView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m3238xa1ae125(View view) {
            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m3237x7d2dca06(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3(this), 10);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m3236xvar_b2e7(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2(this, error));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m3235x63539bc8(TLRPC.TL_error error) {
            this.this$0.needHideProgress(false);
            if (error == null) {
                Bundle params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", NUM);
        }

        /* access modifiers changed from: private */
        public void updateTimeText() {
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int days = timeLeft / 86400;
            int hours = (timeLeft - (days * 86400)) / 3600;
            int minutes = ((timeLeft - (86400 * days)) - (hours * 3600)) / 60;
            int seconds = timeLeft % 60;
            if (days != 0) {
                TextView textView = this.resetAccountTime;
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", days) + " " + LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes)));
            } else {
                TextView textView2 = this.resetAccountTime;
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes) + " " + LocaleController.formatPluralString("SecondsBold", seconds)));
            }
            if (timeLeft > 0) {
                this.resetAccountButton.setTag("windowBackgroundWhiteGrayText6");
                this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                return;
            }
            this.resetAccountButton.setTag("windowBackgroundWhiteRedText6");
            this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.currentParams = params;
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.startTime = params.getInt("startTime");
                this.waitTime = params.getInt("waitTime");
                TextView textView = this.confirmTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", NUM, LocaleController.addNbsp(instance.format("+" + this.requestPhone)))));
                updateTimeText();
                AnonymousClass1 r0 = new Runnable() {
                    public void run() {
                        if (LoginActivityResetWaitView.this.timeRunnable == this) {
                            LoginActivityResetWaitView.this.updateTimeText();
                            AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                        }
                    }
                };
                this.timeRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 1000);
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public boolean onBackPressed(boolean force) {
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRecoverView(org.telegram.ui.LoginActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                r2 = r19
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.confirmTextView
                r5 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 5
                r8 = 3
                if (r6 == 0) goto L_0x0030
                r6 = 5
                goto L_0x0031
            L_0x0030:
                r6 = 3
            L_0x0031:
                r4.setGravity(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r9, r10)
                android.widget.TextView r4 = r0.confirmTextView
                r9 = 2131627553(0x7f0e0e21, float:1.8882374E38)
                java.lang.String r11 = "RestoreEmailSentInfo"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r4.setText(r9)
                android.widget.TextView r4 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x0058
                r9 = 5
                goto L_0x0059
            L_0x0058:
                r9 = 3
            L_0x0059:
                r11 = -2
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r9)
                r0.addView(r4, r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = new org.telegram.ui.Components.EditTextBoldCursor
                r4.<init>(r1)
                r0.codeField = r4
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r4.setTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r4.setCursorColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r9 = 1101004800(0x41a00000, float:20.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r4.setCursorSize(r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                r4.setCursorWidth(r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                java.lang.String r9 = "windowBackgroundWhiteHintText"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r4.setHintTextColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r9 = 0
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r9)
                r4.setBackgroundDrawable(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r11 = 2131627023(0x7f0e0c0f, float:1.8881299E38)
                java.lang.String r12 = "PasswordCode"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r4.setHint(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r11 = 268435461(0x10000005, float:2.5243564E-29)
                r4.setImeOptions(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r11 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r4.setMaxLines(r3)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r4.setPadding(r9, r9, r9, r9)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r4.setInputType(r8)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                android.text.method.PasswordTransformationMethod r11 = android.text.method.PasswordTransformationMethod.getInstance()
                r4.setTransformationMethod(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
                r4.setTypeface(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x00e5
                r11 = 5
                goto L_0x00e6
            L_0x00e5:
                r11 = 3
            L_0x00e6:
                r4.setGravity(r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                r11 = -1
                r12 = 36
                r13 = 1
                r14 = 0
                r15 = 20
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r4, r11)
                org.telegram.ui.Components.EditTextBoldCursor r4 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2
                r11.<init>(r0)
                r4.setOnEditorActionListener(r11)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r0.cancelButton = r4
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0114
                r11 = 5
                goto L_0x0115
            L_0x0114:
                r11 = 3
            L_0x0115:
                r11 = r11 | 80
                r4.setGravity(r11)
                android.widget.TextView r4 = r0.cancelButton
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r4.setTextColor(r11)
                android.widget.TextView r4 = r0.cancelButton
                r4.setTextSize(r3, r5)
                android.widget.TextView r3 = r0.cancelButton
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r4 = (float) r4
                r3.setLineSpacing(r4, r10)
                android.widget.TextView r3 = r0.cancelButton
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3.setPadding(r9, r4, r9, r9)
                android.widget.TextView r3 = r0.cancelButton
                r9 = -2
                r10 = -2
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x0146
                goto L_0x0147
            L_0x0146:
                r7 = 3
            L_0x0147:
                r11 = r7 | 80
                r12 = 0
                r13 = 0
                r14 = 0
                r15 = 14
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r0.addView(r3, r4)
                android.widget.TextView r3 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1
                r4.<init>(r0)
                r3.setOnClickListener(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRecoverView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ boolean m3213xcd144188(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m3215xcvar_e746(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0(this));
            Dialog dialog = this.this$0.showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m3214xce4a9467(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.codeField.setText("");
                this.currentParams = params;
                this.passwordString = params.getString("password");
                String email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, email_unconfirmed_pattern));
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
            }
        }

        private void onPasscodeError(boolean clear) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                if (clear) {
                    this.codeField.setText("");
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                String code2 = this.codeField.getText().toString();
                if (code2.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
                req.code = code2;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5(this, code2), 10);
            }
        }

        /* renamed from: lambda$onNextPressed$4$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m3217x42bba14(String finalCode, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4(this, response, finalCode, error));
        }

        /* renamed from: lambda$onNextPressed$3$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m3216x2var_(TLObject response, String finalCode, TLRPC.TL_error error) {
            String timeString;
            this.this$0.needHideProgress(false);
            this.nextPressed = false;
            if (response instanceof TLRPC.TL_boolTrue) {
                Bundle params = new Bundle();
                params.putString("emailCode", finalCode);
                params.putString("password", this.passwordString);
                this.this$0.setPage(9, true, params, false);
            } else if (error == null || error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (error.text.startsWith("FLOOD_WAIT")) {
                int time = Utilities.parseInt(error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public boolean onBackPressed(boolean force) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3(this), 100);
        }

        /* renamed from: lambda$onShow$5$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m3218x201b6ed() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
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
            String code = bundle.getString("recoveryview_code");
            if (code != null) {
                this.codeField.setText(code);
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
        private TLRPC.TL_account_password currentPassword;
        private int currentStage;
        private String emailCode;
        private String newPassword;
        private boolean nextPressed;
        private String passwordString;
        final /* synthetic */ LoginActivity this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityNewPasswordView(org.telegram.ui.LoginActivity r20, android.content.Context r21, int r22) {
            /*
                r19 = this;
                r0 = r19
                r1 = r21
                r2 = r22
                r3 = r20
                r0.this$0 = r3
                r0.<init>(r1)
                r0.currentStage = r2
                r4 = 1
                r0.setOrientation(r4)
                if (r2 != r4) goto L_0x0017
                r5 = 1
                goto L_0x0018
            L_0x0017:
                r5 = 2
            L_0x0018:
                org.telegram.ui.Components.EditTextBoldCursor[] r5 = new org.telegram.ui.Components.EditTextBoldCursor[r5]
                r0.codeField = r5
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r1)
                r0.confirmTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.confirmTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.confirmTextView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r7 == 0) goto L_0x003d
                r7 = 5
                goto L_0x003e
            L_0x003d:
                r7 = 3
            L_0x003e:
                r5.setGravity(r7)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r11 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r10, r11)
                android.widget.TextView r5 = r0.confirmTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x0057
                r10 = 5
                goto L_0x0058
            L_0x0057:
                r10 = 3
            L_0x0058:
                r12 = -2
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r10)
                r0.addView(r5, r10)
                r5 = 0
            L_0x0061:
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                int r12 = r10.length
                r13 = 0
                if (r5 >= r12) goto L_0x0164
                org.telegram.ui.Components.EditTextBoldCursor r12 = new org.telegram.ui.Components.EditTextBoldCursor
                r12.<init>(r1)
                r10[r5] = r12
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                java.lang.String r12 = "windowBackgroundWhiteBlackText"
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r10.setTextColor(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r10.setCursorColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = 1101004800(0x41a00000, float:20.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r10.setCursorSize(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = 1069547520(0x3fCLASSNAME, float:1.5)
                r10.setCursorWidth(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                java.lang.String r12 = "windowBackgroundWhiteHintText"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r10.setHintTextColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r13)
                r10.setBackgroundDrawable(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = 268435461(0x10000005, float:2.5243564E-29)
                r10.setImeOptions(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = 1099956224(0x41900000, float:18.0)
                r10.setTextSize(r4, r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r10.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r10.setPadding(r13, r13, r13, r13)
                if (r2 != 0) goto L_0x00e0
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = 129(0x81, float:1.81E-43)
                r10.setInputType(r12)
            L_0x00e0:
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                android.text.method.PasswordTransformationMethod r12 = android.text.method.PasswordTransformationMethod.getInstance()
                r10.setTransformationMethod(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                android.graphics.Typeface r12 = android.graphics.Typeface.DEFAULT
                r10.setTypeface(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x00fe
                r12 = 5
                goto L_0x00ff
            L_0x00fe:
                r12 = 3
            L_0x00ff:
                r10.setGravity(r12)
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                r10 = r10[r5]
                r12 = -1
                r13 = 36
                r14 = 1
                r15 = 0
                if (r5 != 0) goto L_0x0110
                r16 = 20
                goto L_0x0112
            L_0x0110:
                r16 = 30
            L_0x0112:
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r10, r12)
                r10 = r5
                org.telegram.ui.Components.EditTextBoldCursor[] r12 = r0.codeField
                r12 = r12[r5]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2 r13 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                r13.<init>(r0, r10)
                r12.setOnEditorActionListener(r13)
                if (r2 != 0) goto L_0x0150
                if (r5 != 0) goto L_0x013f
                org.telegram.ui.Components.EditTextBoldCursor[] r12 = r0.codeField
                r12 = r12[r5]
                r13 = 2131627214(0x7f0e0cce, float:1.8881686E38)
                java.lang.String r14 = "PleaseEnterNewFirstPasswordHint"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r12.setHint(r13)
                goto L_0x0160
            L_0x013f:
                org.telegram.ui.Components.EditTextBoldCursor[] r12 = r0.codeField
                r12 = r12[r5]
                r13 = 2131627216(0x7f0e0cd0, float:1.888169E38)
                java.lang.String r14 = "PleaseEnterNewSecondPasswordHint"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r12.setHint(r13)
                goto L_0x0160
            L_0x0150:
                org.telegram.ui.Components.EditTextBoldCursor[] r12 = r0.codeField
                r12 = r12[r5]
                r13 = 2131627027(0x7f0e0CLASSNAME, float:1.8881307E38)
                java.lang.String r14 = "PasswordHintPlaceholder"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r12.setHint(r13)
            L_0x0160:
                int r5 = r5 + 1
                goto L_0x0061
            L_0x0164:
                if (r2 != 0) goto L_0x0175
                android.widget.TextView r5 = r0.confirmTextView
                r10 = 2131627215(0x7f0e0ccf, float:1.8881688E38)
                java.lang.String r12 = "PleaseEnterNewFirstPasswordLogin"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
                r5.setText(r10)
                goto L_0x0183
            L_0x0175:
                android.widget.TextView r5 = r0.confirmTextView
                r10 = 2131627029(0x7f0e0CLASSNAME, float:1.888131E38)
                java.lang.String r12 = "PasswordHintTextLogin"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
                r5.setText(r10)
            L_0x0183:
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r1)
                r0.cancelButton = r5
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x0190
                r10 = 5
                goto L_0x0191
            L_0x0190:
                r10 = 3
            L_0x0191:
                r10 = r10 | 80
                r5.setGravity(r10)
                android.widget.TextView r5 = r0.cancelButton
                java.lang.String r10 = "windowBackgroundWhiteBlueText4"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r5.setTextColor(r10)
                android.widget.TextView r5 = r0.cancelButton
                r5.setTextSize(r4, r6)
                android.widget.TextView r4 = r0.cancelButton
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r11)
                android.widget.TextView r4 = r0.cancelButton
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r4.setPadding(r13, r5, r13, r13)
                android.widget.TextView r4 = r0.cancelButton
                r5 = 2131628734(0x7f0e12be, float:1.888477E38)
                java.lang.String r6 = "YourEmailSkip"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                android.widget.TextView r4 = r0.cancelButton
                r10 = -2
                r11 = -2
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x01d0
                goto L_0x01d1
            L_0x01d0:
                r8 = 3
            L_0x01d1:
                r12 = r8 | 80
                r13 = 0
                r14 = 6
                r15 = 0
                r16 = 14
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1
                r5.<init>(r0)
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ boolean m3188x7b12217f(int num, TextView textView, int i, KeyEvent keyEvent) {
            if (num == 0) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr.length == 2) {
                    editTextBoldCursorArr[1].requestFocus();
                    return true;
                }
            }
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3189x8115ecde(View view) {
            if (this.currentStage == 0) {
                recoverPassword((String) null, (String) null);
            } else {
                recoverPassword(this.newPassword, (String) null);
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("NewPassword", NUM);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                int a = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                    if (a >= editTextBoldCursorArr.length) {
                        break;
                    }
                    editTextBoldCursorArr[a].setText("");
                    a++;
                }
                this.currentParams = params;
                this.emailCode = params.getString("emailCode");
                String string = this.currentParams.getString("password");
                this.passwordString = string;
                if (string != null) {
                    SerializedData data = new SerializedData(Utilities.hexToBytes(this.passwordString));
                    TLRPC.TL_account_password TLdeserialize = TLRPC.TL_account_password.TLdeserialize(data, data.readInt32(false), false);
                    this.currentPassword = TLdeserialize;
                    TwoStepVerificationActivity.initPasswordNewAlgo(TLdeserialize);
                }
                this.newPassword = this.currentParams.getString("new_password");
                AndroidUtilities.showKeyboard(this.codeField[0]);
                this.codeField[0].requestFocus();
            }
        }

        private void onPasscodeError(boolean clear, int num) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.codeField[num], 2.0f, 0);
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                String code2 = this.codeField[0].getText().toString();
                if (code2.length() == 0) {
                    onPasscodeError(false, 0);
                } else if (this.currentStage != 0) {
                    this.nextPressed = true;
                    this.this$0.needShowProgress(0);
                    recoverPassword(this.newPassword, code2);
                } else if (!code2.equals(this.codeField[1].getText().toString())) {
                    onPasscodeError(false, 1);
                } else {
                    Bundle params = new Bundle();
                    params.putString("emailCode", this.emailCode);
                    params.putString("new_password", code2);
                    params.putString("password", this.passwordString);
                    this.this$0.setPage(10, true, params, false);
                }
            }
        }

        private void recoverPassword(String password, String hint) {
            TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
            req.code = this.emailCode;
            if (!TextUtils.isEmpty(password)) {
                req.flags |= 1;
                req.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req.new_settings.flags |= 1;
                req.new_settings.hint = hint != null ? hint : "";
                req.new_settings.new_algo = this.currentPassword.new_algo;
            }
            Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4(this, password, hint, req));
        }

        /* renamed from: lambda$recoverPassword$7$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3196x5b23dvar_(String password, String hint, TLRPC.TL_auth_recoverPassword req) {
            byte[] newPasswordBytes;
            if (password != null) {
                newPasswordBytes = AndroidUtilities.getStringBytes(password);
            } else {
                newPasswordBytes = null;
            }
            RequestDelegate requestDelegate = new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8(this, password, hint);
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (password != null) {
                    req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
                    if (req.new_settings.new_password_hash == null) {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "ALGO_INVALID";
                        requestDelegate.run((TLObject) null, error);
                    }
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, requestDelegate, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run((TLObject) null, error2);
        }

        /* renamed from: lambda$recoverPassword$6$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3195x5520141a(String password, String hint, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5(this, error, password, hint, response));
        }

        /* renamed from: lambda$recoverPassword$5$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3194x4f1CLASSNAMEbb(TLRPC.TL_error error, String password, String hint, TLObject response) {
            String timeString;
            if (error == null || (!"SRP_ID_INVALID".equals(error.text) && !"NEW_SALT_INVALID".equals(error.text))) {
                this.this$0.needHideProgress(false);
                if (response instanceof TLRPC.auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0(this, response));
                    if (TextUtils.isEmpty(password)) {
                        builder.setMessage(LocaleController.getString("PasswordReset", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("YourPasswordChangedSuccessText", NUM));
                    }
                    builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
                    Dialog dialog = this.this$0.showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                    }
                } else if (error != null) {
                    this.nextPressed = false;
                    if (error.text.startsWith("FLOOD_WAIT")) {
                        int time = Utilities.parseInt(error.text).intValue();
                        if (time < 60) {
                            timeString = LocaleController.formatPluralString("Seconds", time);
                        } else {
                            timeString = LocaleController.formatPluralString("Minutes", time / 60);
                        }
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                        return;
                    }
                    this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
                }
            } else {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7(this, password, hint), 8);
            }
        }

        /* renamed from: lambda$recoverPassword$3$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3192x4314b1fd(String password, String hint, TLObject response2, TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(this, error2, response2, password, hint));
        }

        /* renamed from: lambda$recoverPassword$2$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3191x3d10e69e(TLRPC.TL_error error2, TLObject response2, String password, String hint) {
            if (error2 == null) {
                TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
                this.currentPassword = tL_account_password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
                recoverPassword(password, hint);
            }
        }

        /* renamed from: lambda$recoverPassword$4$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3193x49187d5c(TLObject response, DialogInterface dialogInterface, int i) {
            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        public boolean onBackPressed(boolean force) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3(this), 100);
        }

        /* renamed from: lambda$onShow$8$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m3190xf9d7be81() {
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
        private TLRPC.FileLocation avatar;
        /* access modifiers changed from: private */
        public AnimatorSet avatarAnimation;
        private TLRPC.FileLocation avatarBig;
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
        private boolean createAfterUpload;
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

        public /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        public /* synthetic */ void onUploadProgressChanged(float f) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$onUploadProgressChanged(this, f);
        }

        public class LinkSpan extends ClickableSpan {
            public LinkSpan() {
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            public void onClick(View widget) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        /* access modifiers changed from: private */
        public void showTermsOfService(boolean needAccept) {
            if (this.this$0.currentTermsOfService != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("TermsOfService", NUM));
                if (needAccept) {
                    builder.setPositiveButton(LocaleController.getString("Accept", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7(this));
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                }
                SpannableStringBuilder text = new SpannableStringBuilder(this.this$0.currentTermsOfService.text);
                MessageObject.addEntitiesToText(text, this.this$0.currentTermsOfService.entities, false, false, false, false);
                builder.setMessage(text);
                this.this$0.showDialog(builder.create());
            }
        }

        /* renamed from: lambda$showTermsOfService$0$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3231x9CLASSNAMEb17(DialogInterface dialog, int which) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* renamed from: lambda$showTermsOfService$3$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3234x7a7e261a(DialogInterface dialog, int which) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder1.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder1.setMessage(LocaleController.getString("TosDecline", NUM));
            builder1.setPositiveButton(LocaleController.getString("SignUp", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8(this));
            builder1.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9(this));
            this.this$0.showDialog(builder1.create());
        }

        /* renamed from: lambda$showTermsOfService$1$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3232x2var_(DialogInterface dialog1, int which1) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* renamed from: lambda$showTermsOfService$2$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3233x54ea1d19(DialogInterface dialog12, int which12) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRegisterView(org.telegram.ui.LoginActivity r31, android.content.Context r32) {
            /*
                r30 = this;
                r0 = r30
                r1 = r31
                r2 = r32
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
                org.telegram.ui.Components.ImageUpdater r5 = r0.imageUpdater
                r5.setDelegate(r0)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.textView = r5
                java.lang.String r6 = "RegisterText2"
                r7 = 2131627444(0x7f0e0db4, float:1.8882153E38)
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
                r5.setText(r6)
                android.widget.TextView r5 = r0.textView
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.textView
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0055
                r7 = 5
                goto L_0x0056
            L_0x0055:
                r7 = 3
            L_0x0056:
                r5.setGravity(r7)
                android.widget.TextView r5 = r0.textView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.textView
                r10 = -2
                r11 = -2
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x006a
                r12 = 5
                goto L_0x006b
            L_0x006a:
                r12 = 3
            L_0x006b:
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
                if (r13 == 0) goto L_0x00bc
                r13 = 5
                goto L_0x00bd
            L_0x00bc:
                r13 = 3
            L_0x00bd:
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
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x00e8
                r14 = 5
                goto L_0x00e9
            L_0x00e8:
                r14 = 3
            L_0x00e9:
                r14 = r14 | 48
                r15 = 0
                r16 = 1098907648(0x41800000, float:16.0)
                r17 = 0
                r18 = 0
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r5.addView(r11, r12)
                android.view.View r11 = r0.avatarOverlay
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12 r12 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12
                r12.<init>(r0)
                r11.setOnClickListener(r12)
                org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
                r14 = 2131558410(0x7f0d000a, float:1.8742135E38)
                r12 = 1114636288(0x42700000, float:60.0)
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r17 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r18 = 0
                r19 = 0
                java.lang.String r15 = "NUM"
                r13 = r11
                r13.<init>(r14, r15, r16, r17, r18, r19)
                r0.cameraDrawable = r11
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$3 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$3
                r11.<init>(r2, r1)
                r0.avatarEditor = r11
                android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
                r11.setScaleType(r12)
                org.telegram.ui.Components.RLottieImageView r11 = r0.avatarEditor
                org.telegram.ui.Components.RLottieDrawable r12 = r0.cameraDrawable
                r11.setAnimation(r12)
                org.telegram.ui.Components.RLottieImageView r11 = r0.avatarEditor
                r11.setEnabled(r3)
                org.telegram.ui.Components.RLottieImageView r11 = r0.avatarEditor
                r11.setClickable(r3)
                org.telegram.ui.Components.RLottieImageView r11 = r0.avatarEditor
                r12 = 1073741824(0x40000000, float:2.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r14 = 1065353216(0x3var_, float:1.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r11.setPadding(r13, r3, r3, r15)
                org.telegram.ui.Components.RLottieImageView r11 = r0.avatarEditor
                r15 = 64
                r16 = 1115684864(0x42800000, float:64.0)
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x0158
                r13 = 5
                goto L_0x0159
            L_0x0158:
                r13 = 3
            L_0x0159:
                r17 = r13 | 48
                r18 = 0
                r19 = 1098907648(0x41800000, float:16.0)
                r20 = 0
                r21 = 0
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r5.addView(r11, r13)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$4
                r11.<init>(r2, r1)
                r0.avatarProgressView = r11
                r13 = 1106247680(0x41var_, float:30.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r11.setSize(r13)
                org.telegram.ui.Components.RadialProgressView r11 = r0.avatarProgressView
                r13 = -1
                r11.setProgressColor(r13)
                org.telegram.ui.Components.RadialProgressView r11 = r0.avatarProgressView
                r15 = 64
                r16 = 1115684864(0x42800000, float:64.0)
                boolean r17 = org.telegram.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x018d
                r17 = 5
                goto L_0x018f
            L_0x018d:
                r17 = 3
            L_0x018f:
                r17 = r17 | 48
                r18 = 0
                r19 = 1098907648(0x41800000, float:16.0)
                r20 = 0
                r21 = 0
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r5.addView(r11, r15)
                r0.showAvatarProgress(r3, r3)
                org.telegram.ui.Components.EditTextBoldCursor r11 = new org.telegram.ui.Components.EditTextBoldCursor
                r11.<init>(r2)
                r0.firstNameField = r11
                java.lang.String r15 = "windowBackgroundWhiteHintText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r11.setHintTextColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                java.lang.String r11 = "windowBackgroundWhiteBlackText"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r8.setTextColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r3)
                r8.setBackgroundDrawable(r9)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r8.setCursorColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r9 = 1101004800(0x41a00000, float:20.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r8.setCursorSize(r13)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r13 = 1069547520(0x3fCLASSNAME, float:1.5)
                r8.setCursorWidth(r13)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r14 = 2131625679(0x7f0e06cf, float:1.8878573E38)
                java.lang.String r12 = "FirstName"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r14)
                r8.setHint(r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r12 = 268435461(0x10000005, float:2.5243564E-29)
                r8.setImeOptions(r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r12 = 1099431936(0x41880000, float:17.0)
                r8.setTextSize(r4, r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r8.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r14 = 8192(0x2000, float:1.14794E-41)
                r8.setInputType(r14)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r21 = -1
                r22 = 1108344832(0x42100000, float:36.0)
                boolean r23 = org.telegram.messenger.LocaleController.isRTL
                if (r23 == 0) goto L_0x0218
                r23 = 5
                goto L_0x021a
            L_0x0218:
                r23 = 3
            L_0x021a:
                r23 = r23 | 48
                boolean r24 = org.telegram.messenger.LocaleController.isRTL
                r28 = 0
                r29 = 1118437376(0x42aa0000, float:85.0)
                if (r24 == 0) goto L_0x0227
                r24 = 0
                goto L_0x0229
            L_0x0227:
                r24 = 1118437376(0x42aa0000, float:85.0)
            L_0x0229:
                r25 = 0
                boolean r26 = org.telegram.messenger.LocaleController.isRTL
                if (r26 == 0) goto L_0x0232
                r26 = 1118437376(0x42aa0000, float:85.0)
                goto L_0x0234
            L_0x0232:
                r26 = 0
            L_0x0234:
                r27 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
                r5.addView(r8, r7)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14
                r8.<init>(r0)
                r7.setOnEditorActionListener(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = new org.telegram.ui.Components.EditTextBoldCursor
                r7.<init>(r2)
                r0.lastNameField = r7
                r8 = 2131626103(0x7f0e0877, float:1.8879433E38)
                java.lang.String r14 = "LastName"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
                r7.setHint(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r7.setHintTextColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r7.setTextColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r3)
                r7.setBackgroundDrawable(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r7.setCursorColor(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r7.setCursorSize(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r7.setCursorWidth(r13)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r8 = 268435462(0x10000006, float:2.5243567E-29)
                r7.setImeOptions(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r7.setTextSize(r4, r12)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r7.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r8 = 8192(0x2000, float:1.14794E-41)
                r7.setInputType(r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                r21 = -1
                r22 = 1108344832(0x42100000, float:36.0)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x02b1
                r8 = 5
                goto L_0x02b2
            L_0x02b1:
                r8 = 3
            L_0x02b2:
                r23 = r8 | 48
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x02bb
                r24 = 0
                goto L_0x02bd
            L_0x02bb:
                r24 = 1118437376(0x42aa0000, float:85.0)
            L_0x02bd:
                r25 = 1112276992(0x424CLASSNAME, float:51.0)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x02c6
                r26 = 1118437376(0x42aa0000, float:85.0)
                goto L_0x02c8
            L_0x02c6:
                r26 = 0
            L_0x02c8:
                r27 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
                r5.addView(r7, r8)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15
                r8.<init>(r0)
                r7.setOnEditorActionListener(r8)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r2)
                r0.wrongNumber = r7
                r8 = 2131624710(0x7f0e0306, float:1.8876607E38)
                java.lang.String r9 = "CancelRegistration"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r7.setText(r8)
                android.widget.TextView r7 = r0.wrongNumber
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x02f6
                r8 = 5
                goto L_0x02f7
            L_0x02f6:
                r8 = 3
            L_0x02f7:
                r8 = r8 | r4
                r7.setGravity(r8)
                android.widget.TextView r7 = r0.wrongNumber
                java.lang.String r8 = "windowBackgroundWhiteBlueText4"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r7.setTextColor(r8)
                android.widget.TextView r7 = r0.wrongNumber
                r8 = 1096810496(0x41600000, float:14.0)
                r7.setTextSize(r4, r8)
                android.widget.TextView r7 = r0.wrongNumber
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r9
                r9 = 1065353216(0x3var_, float:1.0)
                r7.setLineSpacing(r8, r9)
                android.widget.TextView r7 = r0.wrongNumber
                r8 = 1103101952(0x41CLASSNAME, float:24.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setPadding(r3, r8, r3, r3)
                android.widget.TextView r7 = r0.wrongNumber
                r8 = 8
                r7.setVisibility(r8)
                android.widget.TextView r7 = r0.wrongNumber
                r21 = -2
                r22 = -2
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x0339
                r8 = 5
                goto L_0x033a
            L_0x0339:
                r8 = 3
            L_0x033a:
                r23 = r8 | 48
                r24 = 0
                r25 = 20
                r26 = 0
                r27 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r0.addView(r7, r8)
                android.widget.TextView r7 = r0.wrongNumber
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13
                r8.<init>(r0)
                r7.setOnClickListener(r8)
                android.widget.FrameLayout r7 = new android.widget.FrameLayout
                r7.<init>(r2)
                r7.setClipToPadding(r3)
                r8 = 1105199104(0x41e00000, float:28.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r9 = 1120403456(0x42CLASSNAME, float:100.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r11 = 1098907648(0x41800000, float:16.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r7.setPadding(r3, r8, r9, r11)
                r3 = 83
                r8 = -1
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r8, (int) r3)
                r0.addView(r7, r9)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.privacyView = r8
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r8.setTextColor(r6)
                android.widget.TextView r6 = r0.privacyView
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r8 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r8.<init>()
                r6.setMovementMethod(r8)
                android.widget.TextView r6 = r0.privacyView
                java.lang.String r8 = "windowBackgroundWhiteLinkText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r6.setLinkTextColor(r8)
                android.widget.TextView r6 = r0.privacyView
                r8 = 1096810496(0x41600000, float:14.0)
                r6.setTextSize(r4, r8)
                android.widget.TextView r4 = r0.privacyView
                r6 = 1073741824(0x40000000, float:2.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                r8 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r6, r8)
                android.widget.TextView r4 = r0.privacyView
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
                android.widget.TextView r4 = r0.privacyView
                r6 = -2
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r3)
                r7.addView(r4, r3)
                r3 = 2131628084(0x7f0e1034, float:1.888345E38)
                java.lang.String r4 = "TermsOfServiceLogin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder
                r4.<init>(r3)
                r6 = 42
                int r6 = r3.indexOf(r6)
                r8 = 42
                int r8 = r3.lastIndexOf(r8)
                r9 = -1
                if (r6 == r9) goto L_0x03fe
                if (r8 == r9) goto L_0x03fe
                if (r6 == r8) goto L_0x03fe
                int r9 = r8 + 1
                java.lang.String r11 = ""
                r4.replace(r8, r9, r11)
                int r9 = r6 + 1
                java.lang.String r11 = ""
                r4.replace(r6, r9, r11)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan r9 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan
                r9.<init>()
                int r11 = r8 + -1
                r12 = 33
                r4.setSpan(r9, r6, r11, r12)
            L_0x03fe:
                android.widget.TextView r9 = r0.privacyView
                r9.setText(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRegisterView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3222xdba43e6f(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1(this), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11(this));
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.avatarEditor.playAnimation();
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3220x907c2c6d() {
            this.avatar = null;
            this.avatarBig = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3221xb610356e(DialogInterface dialog) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.playAnimation();
                return;
            }
            this.cameraDrawable.setCurrentFrame(0, false);
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ boolean m3223x1384770(TextView textView2, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ boolean m3224x26cCLASSNAME(TextView textView2, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$9$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3225x4CLASSNAME(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                onBackPressed(false);
            }
        }

        public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5(this, smallSize, bigSize));
        }

        /* renamed from: lambda$didUploadPhoto$10$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3219xffeCLASSNAMEe(TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        }

        private void showAvatarProgress(final boolean show, boolean animated) {
            if (this.avatarEditor != null) {
                AnimatorSet animatorSet = this.avatarAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.avatarAnimation = null;
                }
                if (animated) {
                    this.avatarAnimation = new AnimatorSet();
                    if (show) {
                        this.avatarProgressView.setVisibility(0);
                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                    } else {
                        this.avatarEditor.setVisibility(0);
                        this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                    }
                    this.avatarAnimation.setDuration(180);
                    this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (LoginActivityRegisterView.this.avatarAnimation != null && LoginActivityRegisterView.this.avatarEditor != null) {
                                if (show) {
                                    LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                                } else {
                                    LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                                }
                                AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            AnimatorSet unused = LoginActivityRegisterView.this.avatarAnimation = null;
                        }
                    });
                    this.avatarAnimation.start();
                } else if (show) {
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

        public boolean onBackPressed(boolean force) {
            if (!force) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("AreYouSureRegistration", NUM));
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda0(this));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                this.this$0.showDialog(builder.create());
                return false;
            }
            this.this$0.needHideProgress(true);
            this.nextPressed = false;
            this.currentParams = null;
            return true;
        }

        /* renamed from: lambda$onBackPressed$11$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3226x1bavar_f(DialogInterface dialogInterface, int i) {
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

        public boolean needBackButton() {
            return true;
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2(this), 100);
        }

        /* renamed from: lambda$onShow$12$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3230x933e1406() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            }
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.firstNameField.setText("");
                this.lastNameField.setText("");
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.currentParams = params;
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                if (this.this$0.currentTermsOfService != null && this.this$0.currentTermsOfService.popup) {
                    showTermsOfService(true);
                } else if (this.firstNameField.length() == 0) {
                    this.this$0.onFieldError(this.firstNameField);
                } else {
                    this.nextPressed = true;
                    TLRPC.TL_auth_signUp req = new TLRPC.TL_auth_signUp();
                    req.phone_code_hash = this.phoneHash;
                    req.phone_number = this.requestPhone;
                    req.first_name = this.firstNameField.getText().toString();
                    req.last_name = this.lastNameField.getText().toString();
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6(this), 10);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$15$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3229xb09d1b8f(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4(this, response, error));
        }

        /* renamed from: lambda$onNextPressed$14$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3228x8b09128e(TLObject response, TLRPC.TL_error error) {
            this.nextPressed = false;
            if (response instanceof TLRPC.TL_auth_authorization) {
                hidePrivacyView();
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3(this, response), 150);
                return;
            }
            this.this$0.needHideProgress(false);
            if (error.text.contains("PHONE_NUMBER_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (error.text.contains("FIRSTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (error.text.contains("LASTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        /* renamed from: lambda$onNextPressed$13$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m3227x6575098d(TLObject response) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.this$0.fragmentView.findFocus());
            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response, true);
            if (this.avatarBig != null) {
                MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(this.avatarBig);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String first = this.firstNameField.getText().toString();
            if (first.length() != 0) {
                bundle.putString("registerview_first", first);
            }
            String last = this.lastNameField.getText().toString();
            if (last.length() != 0) {
                bundle.putString("registerview_last", last);
            }
            if (this.this$0.currentTermsOfService != null) {
                SerializedData data = new SerializedData(this.this$0.currentTermsOfService.getObjectSize());
                this.this$0.currentTermsOfService.serializeToStream(data);
                bundle.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                data.cleanup();
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("registerview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            byte[] arr;
            Bundle bundle2 = bundle.getBundle("registerview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            try {
                String terms = bundle.getString("terms");
                if (!(terms == null || (arr = Base64.decode(terms, 0)) == null)) {
                    SerializedData data = new SerializedData(arr);
                    TLRPC.TL_help_termsOfService unused = this.this$0.currentTermsOfService = TLRPC.TL_help_termsOfService.TLdeserialize(data, data.readInt32(false), false);
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            String first = bundle.getString("registerview_first");
            if (first != null) {
                this.firstNameField.setText(first);
            }
            String last = bundle.getString("registerview_last");
            if (last != null) {
                this.lastNameField.setText(last);
            }
        }

        private void hidePrivacyView() {
            this.privacyView.animate().alpha(0.0f).setDuration(150).setStartDelay(0).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                PhoneView phoneView = (PhoneView) slideViewArr[0];
                LoginActivitySmsView smsView1 = (LoginActivitySmsView) slideViewArr[1];
                LoginActivitySmsView smsView2 = (LoginActivitySmsView) slideViewArr[2];
                LoginActivitySmsView smsView3 = (LoginActivitySmsView) slideViewArr[3];
                LoginActivitySmsView smsView4 = (LoginActivitySmsView) slideViewArr[4];
                LoginActivityRegisterView registerView = (LoginActivityRegisterView) slideViewArr[5];
                LoginActivityPasswordView passwordView = (LoginActivityPasswordView) slideViewArr[6];
                LoginActivityRecoverView recoverView = (LoginActivityRecoverView) slideViewArr[7];
                LoginActivityResetWaitView waitView = (LoginActivityResetWaitView) slideViewArr[8];
                ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new LoginActivity$$ExternalSyntheticLambda4(this);
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
                arrayList.add(new ThemeDescription(passwordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(passwordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(passwordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(passwordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(registerView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(registerView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
                arrayList.add(new ThemeDescription(recoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(recoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(waitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(waitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(waitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView1.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (smsView1.codeFieldContainer.codeField != null) {
                    for (int a2 = 0; a2 < smsView1.codeFieldContainer.codeField.length; a2++) {
                        arrayList.add(new ThemeDescription(smsView1.codeFieldContainer.codeField[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(smsView1.codeFieldContainer.codeField[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(smsView1.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView1.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (smsView2.codeFieldContainer.codeField != null) {
                    for (int a3 = 0; a3 < smsView2.codeFieldContainer.codeField.length; a3++) {
                        arrayList.add(new ThemeDescription(smsView2.codeFieldContainer.codeField[a3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(smsView2.codeFieldContainer.codeField[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(smsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (smsView3.codeFieldContainer.codeField != null) {
                    for (int a4 = 0; a4 < smsView3.codeFieldContainer.codeField.length; a4++) {
                        arrayList.add(new ThemeDescription(smsView3.codeFieldContainer.codeField[a4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(smsView3.codeFieldContainer.codeField[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(smsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                if (smsView4.codeFieldContainer.codeField != null) {
                    for (int a5 = 0; a5 < smsView4.codeFieldContainer.codeField.length; a5++) {
                        arrayList.add(new ThemeDescription(smsView4.codeFieldContainer.codeField[a5], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(smsView4.codeFieldContainer.codeField[a5], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
                arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
                ThemeDescription themeDescription = r11;
                ThemeDescription themeDescription2 = new ThemeDescription(smsView4.blueImageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "chats_actionBackground");
                arrayList.add(themeDescription);
                for (int a6 = 0; a6 < 2; a6++) {
                    SlideView[] slideViewArr2 = this.views;
                    if (slideViewArr2[a6 + 9] != null) {
                        LoginActivityNewPasswordView view = (LoginActivityNewPasswordView) slideViewArr2[a6 + 9];
                        arrayList.add(new ThemeDescription(view.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
                        for (int b = 0; b < view.codeField.length; b++) {
                            arrayList.add(new ThemeDescription(view.codeField[b], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                            arrayList.add(new ThemeDescription(view.codeField[b], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                            arrayList.add(new ThemeDescription(view.codeField[b], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                            arrayList.add(new ThemeDescription(view.codeField[b], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                        }
                        arrayList.add(new ThemeDescription(view.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                    }
                }
                return arrayList;
            } else if (slideViewArr[a] == null) {
                return new ArrayList<>();
            } else {
                a++;
            }
        }
    }

    /* renamed from: lambda$getThemeDescriptions$4$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m3185lambda$getThemeDescriptions$4$orgtelegramuiLoginActivity() {
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i < slideViewArr.length) {
                if (slideViewArr[i] instanceof LoginActivitySmsView) {
                    LoginActivitySmsView smsView = (LoginActivitySmsView) slideViewArr[i];
                    if (smsView.hintDrawable != null) {
                        smsView.hintDrawable.setLayerColor("Bubble.**", Theme.getColor("chats_actionBackground"));
                        smsView.hintDrawable.setLayerColor("Phone.**", Theme.getColor("chats_actionBackground"));
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }
}
