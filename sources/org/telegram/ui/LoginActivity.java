package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
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
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import org.telegram.messenger.BuildVars;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.CountrySelectActivity;

public class LoginActivity extends BaseFragment {
    public static final int AUTH_TYPE_CALL = 4;
    public static final int AUTH_TYPE_FLASH_CALL = 3;
    public static final int AUTH_TYPE_MESSAGE = 1;
    public static final int AUTH_TYPE_MISSED_CALL = 11;
    public static final int AUTH_TYPE_SMS = 2;
    private static final int COUNTRY_STATE_EMPTY = 1;
    private static final int COUNTRY_STATE_INVALID = 2;
    private static final int COUNTRY_STATE_NOT_SET_OR_VALID = 0;
    private static final int DONE_TYPE_ACTION = 1;
    private static final int DONE_TYPE_FLOATING = 0;
    /* access modifiers changed from: private */
    public static final int SHOW_DELAY = (SharedConfig.getDevicePerformanceClass() <= 1 ? 150 : 100);
    private static final int VIEW_CODE_CALL = 4;
    private static final int VIEW_CODE_FLASH_CALL = 3;
    private static final int VIEW_CODE_MESSAGE = 1;
    private static final int VIEW_CODE_MISSED_CALL = 11;
    private static final int VIEW_CODE_SMS = 2;
    private static final int VIEW_NEW_PASSWORD_STAGE_1 = 9;
    private static final int VIEW_NEW_PASSWORD_STAGE_2 = 10;
    private static final int VIEW_PASSWORD = 6;
    private static final int VIEW_PHONE_INPUT = 0;
    private static final int VIEW_RECOVER = 7;
    private static final int VIEW_REGISTER = 5;
    private static final int VIEW_RESET_WAIT = 8;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public Runnable animationFinishCallback;
    /* access modifiers changed from: private */
    public ImageView backButtonView;
    /* access modifiers changed from: private */
    public boolean cancelAccountDeletionMode = false;
    private AlertDialog cancelDeleteProgressDialog;
    private TLRPC.TL_auth_sentCode cancelDeletionCode;
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
    public TLRPC.TL_help_termsOfService currentTermsOfService;
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

    @Retention(RetentionPolicy.SOURCE)
    public @interface AuthType {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ViewNumber {
    }

    private @interface CountryState {
    }

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

    public LoginActivity cancelAccountDeletion(String phone, Bundle params, TLRPC.TL_auth_sentCode sentCode) {
        this.cancelDeletionPhone = phone;
        this.cancelDeletionParams = params;
        this.cancelDeletionCode = sentCode;
        this.cancelAccountDeletionMode = true;
        return this;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[a] != null) {
                slideViewArr[a].onDestroyActivity();
            }
            a++;
        }
        AlertDialog alertDialog = this.cancelDeleteProgressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        for (Runnable callback : this.editDoneCallback) {
            if (callback != null) {
                AndroidUtilities.cancelRunOnUIThread(callback);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x03e8  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x03f7  */
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
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda13 r5 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda13
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
            android.widget.Space r5 = new android.widget.Space
            r5.<init>(r1)
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x0067
            r8 = 0
            goto L_0x0069
        L_0x0067:
            int r8 = org.telegram.messenger.AndroidUtilities.statusBarHeight
        L_0x0069:
            r5.setMinimumHeight(r8)
            android.widget.LinearLayout r8 = r0.keyboardLinearLayout
            r8.addView(r5)
            org.telegram.ui.LoginActivity$4 r8 = new org.telegram.ui.LoginActivity$4
            r8.<init>(r1)
            r0.slideViewsContainer = r8
            android.widget.LinearLayout r9 = r0.keyboardLinearLayout
            r10 = 1065353216(0x3var_, float:1.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r3, (float) r10)
            r9.addView(r8, r11)
            org.telegram.ui.Components.CustomPhoneKeyboardView r8 = new org.telegram.ui.Components.CustomPhoneKeyboardView
            r8.<init>(r1)
            r0.keyboardView = r8
            android.widget.LinearLayout r9 = r0.keyboardLinearLayout
            r11 = 230(0xe6, float:3.22E-43)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r11)
            r9.addView(r8, r11)
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$PhoneView r9 = new org.telegram.ui.LoginActivity$PhoneView
            r9.<init>(r0, r1)
            r8[r3] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r9.<init>(r0, r1, r4)
            r8[r4] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r11 = 2
            r9.<init>(r0, r1, r11)
            r8[r11] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r12 = 3
            r9.<init>(r0, r1, r12)
            r8[r12] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r13 = 4
            r9.<init>(r0, r1, r13)
            r8[r13] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRegisterView r9 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView
            r9.<init>(r0, r1)
            r14 = 5
            r8[r14] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r9 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView
            r9.<init>(r0, r1)
            r15 = 6
            r8[r15] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r9 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView
            r9.<init>(r0, r1)
            r12 = 7
            r8[r12] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityResetWaitView r9 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView
            r9.<init>(r0, r1)
            r14 = 8
            r8[r14] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r9 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r9.<init>(r0, r1, r3)
            r14 = 9
            r8[r14] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivityNewPasswordView r9 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView
            r9.<init>(r0, r1, r4)
            r14 = 10
            r8[r14] = r9
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            org.telegram.ui.LoginActivity$LoginActivitySmsView r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView
            r14 = 11
            r9.<init>(r0, r1, r14)
            r8[r14] = r9
            r8 = 0
        L_0x0110:
            org.telegram.ui.Components.SlideView[] r9 = r0.views
            int r14 = r9.length
            if (r8 >= r14) goto L_0x0156
            r9 = r9[r8]
            if (r8 != 0) goto L_0x011b
            r14 = 0
            goto L_0x011d
        L_0x011b:
            r14 = 8
        L_0x011d:
            r9.setVisibility(r14)
            android.widget.FrameLayout r9 = r0.slideViewsContainer
            org.telegram.ui.Components.SlideView[] r14 = r0.views
            r14 = r14[r8]
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
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r9.addView(r14, r6)
            int r8 = r8 + 1
            r6 = -1
            goto L_0x0110
        L_0x0156:
            boolean r6 = r0.newAccount
            android.os.Bundle r6 = loadCurrentState(r6)
            if (r6 == 0) goto L_0x01c7
            java.lang.String r8 = "currentViewNum"
            int r8 = r6.getInt(r8, r3)
            r0.currentViewNum = r8
            java.lang.String r8 = "syncContacts"
            int r8 = r6.getInt(r8, r4)
            if (r8 != r4) goto L_0x0170
            r8 = 1
            goto L_0x0171
        L_0x0170:
            r8 = 0
        L_0x0171:
            r0.syncContacts = r8
            int r8 = r0.currentViewNum
            if (r8 < r4) goto L_0x019e
            if (r8 > r13) goto L_0x019e
            java.lang.String r8 = "open"
            int r8 = r6.getInt(r8)
            if (r8 == 0) goto L_0x019d
            long r16 = java.lang.System.currentTimeMillis()
            r18 = 1000(0x3e8, double:4.94E-321)
            long r16 = r16 / r18
            long r13 = (long) r8
            long r16 = r16 - r13
            long r12 = java.lang.Math.abs(r16)
            r16 = 86400(0x15180, double:4.26873E-319)
            int r14 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r14 < 0) goto L_0x019d
            r0.currentViewNum = r3
            r6 = 0
            r31.clearCurrentState()
        L_0x019d:
            goto L_0x01c7
        L_0x019e:
            if (r8 != r15) goto L_0x01b3
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            r8 = r8[r15]
            org.telegram.ui.LoginActivity$LoginActivityPasswordView r8 = (org.telegram.ui.LoginActivity.LoginActivityPasswordView) r8
            org.telegram.tgnet.TLRPC$TL_account_password r12 = r8.currentPassword
            if (r12 != 0) goto L_0x01b2
            r0.currentViewNum = r3
            r6 = 0
            r31.clearCurrentState()
        L_0x01b2:
            goto L_0x01c7
        L_0x01b3:
            if (r8 != r12) goto L_0x01b2
            org.telegram.ui.Components.SlideView[] r8 = r0.views
            r8 = r8[r12]
            org.telegram.ui.LoginActivity$LoginActivityRecoverView r8 = (org.telegram.ui.LoginActivity.LoginActivityRecoverView) r8
            java.lang.String r12 = r8.passwordString
            if (r12 != 0) goto L_0x01c7
            r0.currentViewNum = r3
            r6 = 0
            r31.clearCurrentState()
        L_0x01c7:
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r0.floatingButtonContainer = r8
            boolean[] r12 = r0.doneButtonVisible
            boolean r12 = r12[r3]
            if (r12 == 0) goto L_0x01d6
            r12 = 0
            goto L_0x01d8
        L_0x01d6:
            r12 = 8
        L_0x01d8:
            r8.setVisibility(r12)
            int r8 = android.os.Build.VERSION.SDK_INT
            r12 = 1082130432(0x40800000, float:4.0)
            r13 = 21
            if (r8 < r13) goto L_0x0246
            android.animation.StateListAnimator r8 = new android.animation.StateListAnimator
            r8.<init>()
            int[] r14 = new int[r4]
            r16 = 16842919(0x10100a7, float:2.3694026E-38)
            r14[r3] = r16
            org.telegram.ui.Components.TransformableLoginButtonView r9 = r0.floatingButtonIcon
            float[] r15 = new float[r11]
            r18 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r7 = (float) r7
            r15[r3] = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r7 = (float) r7
            r15[r4] = r7
            java.lang.String r7 = "translationZ"
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r7, r15)
            r20 = r5
            r4 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r9.setDuration(r4)
            r8.addState(r14, r4)
            int[] r4 = new int[r3]
            org.telegram.ui.Components.TransformableLoginButtonView r5 = r0.floatingButtonIcon
            float[] r9 = new float[r11]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r14 = (float) r14
            r9[r3] = r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r14 = (float) r14
            r15 = 1
            r9[r15] = r14
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r9)
            r10 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r5 = r5.setDuration(r10)
            r8.addState(r4, r5)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            r4.setStateListAnimator(r8)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$5 r5 = new org.telegram.ui.LoginActivity$5
            r5.<init>()
            r4.setOutlineProvider(r5)
            goto L_0x0248
        L_0x0246:
            r20 = r5
        L_0x0248:
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator r4 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            r0.floatingAutoAnimator = r4
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r0.sizeNotifierFrameLayout
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r13) goto L_0x025d
            r8 = 56
            r24 = 56
            goto L_0x0261
        L_0x025d:
            r8 = 60
            r24 = 60
        L_0x0261:
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r13) goto L_0x026a
            r8 = 1113587712(0x42600000, float:56.0)
            r25 = 1113587712(0x42600000, float:56.0)
            goto L_0x026e
        L_0x026a:
            r8 = 1114636288(0x42700000, float:60.0)
            r25 = 1114636288(0x42700000, float:60.0)
        L_0x026e:
            r26 = 85
            r27 = 0
            r28 = 0
            r29 = 1103101952(0x41CLASSNAME, float:24.0)
            r30 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r4.addView(r5, r8)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda21 r5 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda21
            r5.<init>(r0)
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.VerticalPositionAutoAnimator r4 = r0.floatingAutoAnimator
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda2
            r5.<init>(r0)
            r4.addUpdateListener(r5)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.backButtonView = r4
            r5 = 2131165503(0x7var_f, float:1.7945225E38)
            r4.setImageResource(r5)
            android.widget.ImageView r4 = r0.backButtonView
            org.telegram.ui.LoginActivity$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.LoginActivity$$ExternalSyntheticLambda1
            r5.<init>(r0)
            r4.setOnClickListener(r5)
            android.widget.ImageView r4 = r0.backButtonView
            r5 = 2131624577(0x7f0e0281, float:1.8876338E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
            r4.setContentDescription(r5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.widget.ImageView r5 = r0.backButtonView
            r5.setPadding(r4, r4, r4, r4)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r0.sizeNotifierFrameLayout
            android.widget.ImageView r8 = r0.backButtonView
            r24 = 32
            r25 = 1107296256(0x42000000, float:32.0)
            r26 = 51
            r27 = 1098907648(0x41800000, float:16.0)
            r28 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r5.addView(r8, r9)
            org.telegram.ui.Components.RadialProgressView r5 = new org.telegram.ui.Components.RadialProgressView
            r5.<init>(r1)
            r0.radialProgressView = r5
            r8 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r5.setSize(r8)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r8 = 0
            r5.setAlpha(r8)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r8 = 1036831949(0x3dcccccd, float:0.1)
            r5.setScaleX(r8)
            org.telegram.ui.Components.RadialProgressView r5 = r0.radialProgressView
            r5.setScaleY(r8)
            org.telegram.ui.Components.SizeNotifierFrameLayout r5 = r0.sizeNotifierFrameLayout
            org.telegram.ui.Components.RadialProgressView r9 = r0.radialProgressView
            r26 = 53
            r27 = 0
            r29 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r5.addView(r9, r10)
            org.telegram.ui.Components.TransformableLoginButtonView r5 = new org.telegram.ui.Components.TransformableLoginButtonView
            r5.<init>(r1)
            r0.floatingButtonIcon = r5
            r5.setTransformType(r3)
            org.telegram.ui.Components.TransformableLoginButtonView r5 = r0.floatingButtonIcon
            r7 = 1065353216(0x3var_, float:1.0)
            r5.setProgress(r7)
            org.telegram.ui.Components.TransformableLoginButtonView r5 = r0.floatingButtonIcon
            r5.setDrawBackground(r3)
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            r7 = 2131625430(0x7f0e05d6, float:1.8878068E38)
            java.lang.String r9 = "Done"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r5.setContentDescription(r7)
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            org.telegram.ui.Components.TransformableLoginButtonView r7 = r0.floatingButtonIcon
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 < r13) goto L_0x033b
            r9 = 56
            goto L_0x033d
        L_0x033b:
            r9 = 60
        L_0x033d:
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 < r13) goto L_0x0344
            r10 = 1113587712(0x42600000, float:56.0)
            goto L_0x0346
        L_0x0344:
            r10 = 1114636288(0x42700000, float:60.0)
        L_0x0346:
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10)
            r5.addView(r7, r9)
            org.telegram.ui.Components.RadialProgressView r5 = new org.telegram.ui.Components.RadialProgressView
            r5.<init>(r1)
            r0.floatingProgressView = r5
            r7 = 1102053376(0x41b00000, float:22.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.setSize(r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.floatingProgressView
            r7 = 0
            r5.setAlpha(r7)
            org.telegram.ui.Components.RadialProgressView r5 = r0.floatingProgressView
            r5.setScaleX(r8)
            org.telegram.ui.Components.RadialProgressView r5 = r0.floatingProgressView
            r5.setScaleY(r8)
            org.telegram.ui.Components.RadialProgressView r5 = r0.floatingProgressView
            r7 = 4
            r5.setVisibility(r7)
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            org.telegram.ui.Components.RadialProgressView r7 = r0.floatingProgressView
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r8)
            r5.addView(r7, r8)
            if (r6 == 0) goto L_0x0386
            r5 = 1
            r0.restoringState = r5
        L_0x0386:
            r5 = 0
        L_0x0387:
            org.telegram.ui.Components.SlideView[] r7 = r0.views
            int r8 = r7.length
            if (r5 >= r8) goto L_0x041b
            r7 = r7[r5]
            if (r6 == 0) goto L_0x03a1
            r8 = 1
            if (r5 < r8) goto L_0x039e
            r8 = 4
            if (r5 > r8) goto L_0x039e
            int r8 = r0.currentViewNum
            if (r5 != r8) goto L_0x03a1
            r7.restoreStateParams(r6)
            goto L_0x03a1
        L_0x039e:
            r7.restoreStateParams(r6)
        L_0x03a1:
            int r8 = r0.currentViewNum
            if (r8 != r5) goto L_0x0400
            android.widget.ImageView r8 = r0.backButtonView
            boolean r10 = r7.needBackButton()
            if (r10 != 0) goto L_0x03b5
            boolean r10 = r0.newAccount
            if (r10 == 0) goto L_0x03b2
            goto L_0x03b5
        L_0x03b2:
            r10 = 8
            goto L_0x03b6
        L_0x03b5:
            r10 = 0
        L_0x03b6:
            r8.setVisibility(r10)
            r7.setVisibility(r3)
            r7.onShow()
            boolean r8 = r7.hasCustomKeyboard()
            r0.setCustomKeyboardVisible(r8, r3)
            r0.currentDoneType = r3
            if (r5 == 0) goto L_0x03db
            r8 = 5
            if (r5 == r8) goto L_0x03dc
            r10 = 6
            if (r5 == r10) goto L_0x03dd
            r11 = 9
            if (r5 == r11) goto L_0x03df
            r12 = 10
            if (r5 != r12) goto L_0x03d9
            goto L_0x03e1
        L_0x03d9:
            r13 = 0
            goto L_0x03e2
        L_0x03db:
            r8 = 5
        L_0x03dc:
            r10 = 6
        L_0x03dd:
            r11 = 9
        L_0x03df:
            r12 = 10
        L_0x03e1:
            r13 = 1
        L_0x03e2:
            r0.showDoneButton(r13, r3)
            r15 = 1
            if (r5 == r15) goto L_0x03f7
            r14 = 2
            if (r5 == r14) goto L_0x03f4
            r8 = 3
            if (r5 == r8) goto L_0x03f5
            r9 = 4
            if (r5 != r9) goto L_0x03f2
            goto L_0x03fa
        L_0x03f2:
            r15 = 1
            goto L_0x03fd
        L_0x03f4:
            r8 = 3
        L_0x03f5:
            r9 = 4
            goto L_0x03fa
        L_0x03f7:
            r8 = 3
            r9 = 4
            r14 = 2
        L_0x03fa:
            r15 = 1
            r0.currentDoneType = r15
        L_0x03fd:
            r8 = 8
            goto L_0x0417
        L_0x0400:
            r8 = 3
            r9 = 4
            r10 = 6
            r11 = 9
            r12 = 10
            r14 = 2
            r15 = 1
            int r13 = r7.getVisibility()
            r8 = 8
            if (r13 == r8) goto L_0x0417
            r7.setVisibility(r8)
            r7.onHide()
        L_0x0417:
            int r5 = r5 + 1
            goto L_0x0387
        L_0x041b:
            r0.restoringState = r3
            r31.updateColors()
            boolean r5 = r0.cancelAccountDeletionMode
            if (r5 == 0) goto L_0x042b
            android.os.Bundle r5 = r0.cancelDeletionParams
            org.telegram.tgnet.TLRPC$TL_auth_sentCode r7 = r0.cancelDeletionCode
            r0.fillNextCodeParams(r5, r7, r3)
        L_0x042b:
            android.view.View r3 = r0.fragmentView
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2445lambda$createView$0$orgtelegramuiLoginActivity(int keyboardHeight, boolean isWidthGreater) {
        Runnable runnable;
        if (keyboardHeight > AndroidUtilities.dp(20.0f) && isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
        if (keyboardHeight <= AndroidUtilities.dp(20.0f) && (runnable = this.keyboardHideCallback) != null) {
            runnable.run();
            this.keyboardHideCallback = null;
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2446lambda$createView$1$orgtelegramuiLoginActivity(View view) {
        onDoneButtonPressed();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2447lambda$createView$2$orgtelegramuiLoginActivity(DynamicAnimation animation, float value, float velocity) {
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.updateFabPosition();
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2448lambda$createView$3$orgtelegramuiLoginActivity(View v) {
        if (onBackPressed()) {
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardForceDisabled() {
        return AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y || AndroidUtilities.isTablet() || AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        return this.views[this.currentViewNum].hasCustomKeyboard() && !isCustomKeyboardForceDisabled();
    }

    private void setCustomKeyboardVisible(boolean visible, boolean animate) {
        if (this.customKeyboardWasVisible != visible || !animate) {
            this.customKeyboardWasVisible = visible;
            if (isCustomKeyboardForceDisabled()) {
                visible = false;
            }
            if (visible) {
                AndroidUtilities.hideKeyboard(this.fragmentView);
                AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
                if (animate) {
                    ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                    anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    anim.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda0(this));
                    anim.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            LoginActivity.this.keyboardView.setVisibility(0);
                        }
                    });
                    anim.start();
                    return;
                }
                this.keyboardView.setVisibility(0);
                return;
            }
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
            if (animate) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(300);
                anim2.setInterpolator(Easings.easeInOutQuad);
                anim2.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda11(this));
                anim2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        LoginActivity.this.keyboardView.setVisibility(8);
                    }
                });
                anim2.start();
                return;
            }
            this.keyboardView.setVisibility(8);
        }
    }

    /* renamed from: lambda$setCustomKeyboardVisible$4$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2452lambda$setCustomKeyboardVisible$4$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * ((float) AndroidUtilities.dp(230.0f)));
    }

    /* renamed from: lambda$setCustomKeyboardVisible$5$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2453lambda$setCustomKeyboardVisible$5$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * ((float) AndroidUtilities.dp(230.0f)));
    }

    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
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
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        setCustomKeyboardVisible(this.views[this.currentViewNum].hasCustomKeyboard(), false);
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.dismiss();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length != 0 && grantResults.length != 0) {
            boolean granted = grantResults[0] == 0;
            if (requestCode == 6) {
                this.checkPermissions = false;
                int i = this.currentViewNum;
                if (i == 0) {
                    boolean unused = ((PhoneView) this.views[i]).confirmedNumber = true;
                    this.views[this.currentViewNum].onNextPressed((String) null);
                }
            } else if (requestCode == 7) {
                this.checkShowPermissions = false;
                int i2 = this.currentViewNum;
                if (i2 == 0) {
                    ((PhoneView) this.views[i2]).fillNumber();
                }
            } else if (requestCode == 20) {
                if (granted) {
                    ((LoginActivityRegisterView) this.views[5]).imageUpdater.openCamera();
                }
            } else if (requestCode == 151 && granted) {
                LoginActivityRegisterView registerView = (LoginActivityRegisterView) this.views[5];
                registerView.post(new LoginActivity$$ExternalSyntheticLambda5(registerView));
            }
        }
    }

    public static Bundle loadCurrentState(boolean newAccount2) {
        if (newAccount2) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda6(this), 200);
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
            } catch (Exception e2) {
            }
        }
    }

    /* renamed from: lambda$onDialogDismiss$7$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2450lambda$onDialogDismiss$7$orgtelegramuiLoginActivity() {
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
    public void onFieldError(View view, boolean allowErrorSelection) {
        view.performHapticFeedback(3, 2);
        AndroidUtilities.shakeViewSpring(view, 3.5f);
        if (allowErrorSelection && (view instanceof OutlineTextContainerView)) {
            Runnable callback = (Runnable) view.getTag(NUM);
            if (callback != null) {
                view.removeCallbacks(callback);
            }
            OutlineTextContainerView outlineTextContainerView = (OutlineTextContainerView) view;
            final AtomicReference<Runnable> timeoutCallbackRef = new AtomicReference<>();
            final EditText editText = outlineTextContainerView.getAttachedEditText();
            TextWatcher textWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    EditText editText = editText;
                    editText.post(new LoginActivity$8$$ExternalSyntheticLambda0(this, editText, timeoutCallbackRef));
                }

                /* renamed from: lambda$beforeTextChanged$0$org-telegram-ui-LoginActivity$8  reason: not valid java name */
                public /* synthetic */ void m2461lambda$beforeTextChanged$0$orgtelegramuiLoginActivity$8(EditText editText, AtomicReference timeoutCallbackRef) {
                    editText.removeTextChangedListener(this);
                    editText.removeCallbacks((Runnable) timeoutCallbackRef.get());
                    ((Runnable) timeoutCallbackRef.get()).run();
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                }
            };
            outlineTextContainerView.animateError(1.0f);
            Runnable timeoutCallback = new LoginActivity$$ExternalSyntheticLambda4(outlineTextContainerView, view, editText, textWatcher);
            timeoutCallbackRef.set(timeoutCallback);
            view.postDelayed(timeoutCallback, 2000);
            view.setTag(NUM, timeoutCallback);
            if (editText != null) {
                editText.addTextChangedListener(textWatcher);
            }
        }
    }

    static /* synthetic */ void lambda$onFieldError$9(OutlineTextContainerView outlineTextContainerView, View view, EditText editText, TextWatcher textWatcher) {
        outlineTextContainerView.animateError(0.0f);
        view.setTag(NUM, (Object) null);
        if (editText != null) {
            editText.post(new LoginActivity$$ExternalSyntheticLambda3(editText, textWatcher));
        }
    }

    public static void needShowInvalidAlert(BaseFragment fragment, String phoneNumber, boolean banned) {
        needShowInvalidAlert(fragment, phoneNumber, (PhoneInputData) null, banned);
    }

    public static void needShowInvalidAlert(BaseFragment fragment, String phoneNumber, PhoneInputData inputData, boolean banned) {
        if (fragment != null && fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            if (banned) {
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else if (inputData == null || inputData.patterns == null || inputData.patterns.isEmpty() || inputData.country == null) {
                builder.setTitle(LocaleController.getString(NUM));
                builder.setMessage(LocaleController.getString(NUM));
            } else {
                int patternLength = Integer.MAX_VALUE;
                for (String pattern : inputData.patterns) {
                    int length = pattern.replace(" ", "").length();
                    if (length < patternLength) {
                        patternLength = length;
                    }
                }
                if (PhoneFormat.stripExceptNumbers(phoneNumber).length() - inputData.country.code.length() < patternLength) {
                    builder.setTitle(LocaleController.getString(NUM));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ShortNumberInfo", NUM, inputData.country.name, inputData.phoneNumber)));
                } else {
                    builder.setTitle(LocaleController.getString(NUM));
                    builder.setMessage(LocaleController.getString(NUM));
                }
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new LoginActivity$$ExternalSyntheticLambda20(banned, phoneNumber, fragment));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$needShowInvalidAlert$10(boolean banned, String phoneNumber, BaseFragment fragment, DialogInterface dialog, int which) {
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
            builder2.setTitle(LocaleController.getString(NUM));
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
                if (animated) {
                    animatorSetArr[i].removeAllListeners();
                }
                this.showDoneAnimation[this.currentDoneType].cancel();
            }
            boolean[] zArr = this.doneButtonVisible;
            int i2 = this.currentDoneType;
            zArr[i2] = show;
            if (animated) {
                this.showDoneAnimation[i2] = new AnimatorSet();
                if (show) {
                    if (floating) {
                        if (this.floatingButtonContainer.getVisibility() != 0) {
                            this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
                            this.floatingButtonContainer.setVisibility(0);
                        }
                        ValueAnimator offsetAnimator = ValueAnimator.ofFloat(new float[]{this.floatingAutoAnimator.getOffsetY(), 0.0f});
                        offsetAnimator.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda14(this));
                        this.showDoneAnimation[this.currentDoneType].play(offsetAnimator);
                    }
                } else if (floating) {
                    ValueAnimator offsetAnimator2 = ValueAnimator.ofFloat(new float[]{this.floatingAutoAnimator.getOffsetY(), AndroidUtilities.dpf2(70.0f)});
                    offsetAnimator2.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda15(this));
                    this.showDoneAnimation[this.currentDoneType].play(offsetAnimator2);
                }
                this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (LoginActivity.this.showDoneAnimation[!floating] != null && LoginActivity.this.showDoneAnimation[!floating].equals(animation) && !show) {
                            if (floating) {
                                LoginActivity.this.floatingButtonContainer.setVisibility(8);
                            }
                            if (floating && LoginActivity.this.floatingButtonIcon.getAlpha() != 1.0f) {
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
                    this.floatingAutoAnimator.setOffsetY(0.0f);
                }
            } else if (floating) {
                this.floatingButtonContainer.setVisibility(8);
                this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
            }
        }
    }

    /* renamed from: lambda$showDoneButton$11$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2454lambda$showDoneButton$11$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(val);
        this.floatingButtonContainer.setAlpha(1.0f - (val / AndroidUtilities.dpf2(70.0f)));
    }

    /* renamed from: lambda$showDoneButton$12$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2455lambda$showDoneButton$12$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(val);
        this.floatingButtonContainer.setAlpha(1.0f - (val / AndroidUtilities.dpf2(70.0f)));
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
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$$ExternalSyntheticLambda18(this));
                showDialog(builder.create());
            }
        }
    }

    /* renamed from: lambda$onDoneButtonPressed$13$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2451lambda$onDoneButtonPressed$13$orgtelegramuiLoginActivity(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean show, boolean animated) {
        showEditDoneProgress(show, animated, false);
    }

    private void showEditDoneProgress(final boolean show, boolean animated, boolean fromCallback) {
        if (!animated || this.doneProgressVisible[this.currentDoneType] != show || fromCallback) {
            int i = this.currentDoneType;
            final boolean floating = i == 0;
            if (fromCallback || floating) {
                this.postedEditDoneCallback[i] = false;
                this.doneProgressVisible[i] = show;
            } else {
                this.doneProgressVisible[i] = show;
                int doneType = this.currentDoneType;
                if (animated) {
                    if (this.postedEditDoneCallback[i]) {
                        AndroidUtilities.cancelRunOnUIThread(this.editDoneCallback[i]);
                        this.postedEditDoneCallback[this.currentDoneType] = false;
                        return;
                    } else if (show) {
                        Runnable[] runnableArr = this.editDoneCallback;
                        LoginActivity$$ExternalSyntheticLambda7 loginActivity$$ExternalSyntheticLambda7 = new LoginActivity$$ExternalSyntheticLambda7(this, doneType, show, animated);
                        runnableArr[i] = loginActivity$$ExternalSyntheticLambda7;
                        AndroidUtilities.runOnUIThread(loginActivity$$ExternalSyntheticLambda7, 2000);
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
            if (animated) {
                this.doneItemAnimation = new AnimatorSet();
                float[] fArr = new float[2];
                fArr[0] = show ? 0.0f : 1.0f;
                if (show) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator animator = ValueAnimator.ofFloat(fArr);
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        if (!show) {
                            return;
                        }
                        if (floating) {
                            LoginActivity.this.floatingButtonIcon.setVisibility(0);
                            LoginActivity.this.floatingProgressView.setVisibility(0);
                            LoginActivity.this.floatingButtonContainer.setEnabled(false);
                            return;
                        }
                        LoginActivity.this.radialProgressView.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (floating) {
                            if (!show) {
                                LoginActivity.this.floatingProgressView.setVisibility(4);
                                LoginActivity.this.floatingButtonIcon.setVisibility(0);
                                LoginActivity.this.floatingButtonContainer.setEnabled(true);
                            } else {
                                LoginActivity.this.floatingButtonIcon.setVisibility(4);
                                LoginActivity.this.floatingProgressView.setVisibility(0);
                            }
                        } else if (!show) {
                            LoginActivity.this.radialProgressView.setVisibility(4);
                        }
                        if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                            AnimatorSet unused = LoginActivity.this.doneItemAnimation = null;
                        }
                    }
                });
                animator.addUpdateListener(new LoginActivity$$ExternalSyntheticLambda17(this, floating));
                this.doneItemAnimation.playTogether(new Animator[]{animator});
                this.doneItemAnimation.setDuration(150);
                this.doneItemAnimation.start();
            } else if (!show) {
                this.radialProgressView.setTag((Object) null);
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
                this.radialProgressView.setVisibility(4);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setAlpha(0.0f);
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
                this.radialProgressView.setVisibility(0);
                this.radialProgressView.setScaleX(1.0f);
                this.radialProgressView.setScaleY(1.0f);
                this.radialProgressView.setAlpha(1.0f);
            }
        }
    }

    /* renamed from: lambda$showEditDoneProgress$14$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2456lambda$showEditDoneProgress$14$orgtelegramuiLoginActivity(int doneType, boolean show, boolean animated) {
        int type = this.currentDoneType;
        this.currentDoneType = doneType;
        showEditDoneProgress(show, animated, true);
        this.currentDoneType = type;
    }

    /* renamed from: lambda$showEditDoneProgress$15$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2457lambda$showEditDoneProgress$15$orgtelegramuiLoginActivity(boolean floating, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        if (floating) {
            float scale = ((1.0f - val) * 0.9f) + 0.1f;
            this.floatingButtonIcon.setScaleX(scale);
            this.floatingButtonIcon.setScaleY(scale);
            this.floatingButtonIcon.setAlpha(1.0f - val);
            float scale2 = (0.9f * val) + 0.1f;
            this.floatingProgressView.setScaleX(scale2);
            this.floatingProgressView.setScaleY(scale2);
            this.floatingProgressView.setAlpha(val);
            return;
        }
        float scale3 = (0.9f * val) + 0.1f;
        this.radialProgressView.setScaleX(scale3);
        this.radialProgressView.setScaleY(scale3);
        this.radialProgressView.setAlpha(val);
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int requestId) {
        needShowProgress(requestId, true);
    }

    /* access modifiers changed from: private */
    public void needShowProgress(int requestId, boolean animated) {
        if (!this.cancelAccountDeletionMode || requestId != 0) {
            this.progressRequestId = requestId;
            showEditDoneProgress(true, animated);
        } else if (this.cancelDeleteProgressDialog == null && getParentActivity() != null && !getParentActivity().isFinishing()) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.cancelDeleteProgressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.cancelDeleteProgressDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean cancel) {
        needHideProgress(cancel, true);
    }

    /* access modifiers changed from: private */
    public void needHideProgress(boolean cancel, boolean animated) {
        AlertDialog alertDialog;
        if (this.progressRequestId != 0) {
            if (cancel) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        if (this.cancelAccountDeletionMode && (alertDialog = this.cancelDeleteProgressDialog) != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        showEditDoneProgress(false, animated);
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        final boolean needFloatingButton = page == 0 || page == 5 || page == 6 || page == 9 || page == 10;
        int i = 8;
        if (needFloatingButton) {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, animated);
            showEditDoneProgress(false, animated);
            this.currentDoneType = 0;
            showEditDoneProgress(false, animated);
            if (!animated) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, animated);
            showEditDoneProgress(false, animated);
            if (page != 8) {
                this.currentDoneType = 1;
            }
        }
        if (animated) {
            SlideView[] slideViewArr = this.views;
            final SlideView outView = slideViewArr[this.currentViewNum];
            SlideView newView = slideViewArr[page];
            this.currentViewNum = page;
            ImageView imageView = this.backButtonView;
            if (newView.needBackButton() || this.newAccount) {
                i = 0;
            }
            imageView.setVisibility(i);
            newView.setParams(params, false);
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
            setCustomKeyboardVisible(newView.hasCustomKeyboard(), true);
            return;
        }
        this.backButtonView.setVisibility((this.views[page].needBackButton() || this.newAccount) ? 0 : 8);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params, false);
        this.views[page].setVisibility(0);
        setParentActivityTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
        setCustomKeyboardVisible(this.views[page].hasCustomKeyboard(), false);
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
        if (getParentActivity() != null) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), false);
        }
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false, new LoginActivity$$ExternalSyntheticLambda9(afterSignup));
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
                twoStepVerification.setFromRegistration(true);
                presentFragment(twoStepVerification, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    static /* synthetic */ DialogsActivity lambda$needFinishActivity$16(boolean afterSignup, Void obj) {
        Bundle args = new Bundle();
        args.putBoolean("afterSignup", afterSignup);
        return new DialogsActivity(args);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        onAuthSuccess(res, false);
    }

    /* access modifiers changed from: private */
    public void onAuthSuccess(TLRPC.TL_auth_authorization res, boolean afterSignup) {
        MessagesController.getInstance(this.currentAccount).cleanup();
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
        if (afterSignup) {
            MessagesController.getInstance(this.currentAccount).putDialogsEndReachedAfterRegistration();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, true);
        needFinishActivity(afterSignup, res.setup_password_required, res.otherwise_relogin_days);
    }

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res) {
        fillNextCodeParams(params, res, true);
    }

    private void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res, boolean animate) {
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
            setPage(1, animate, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeMissedCall) {
            params.putInt("type", 11);
            params.putInt("length", res.type.length);
            params.putString("prefix", res.type.prefix);
            setPage(11, animate, params, false);
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(org.telegram.ui.LoginActivity r29, android.content.Context r30) {
            /*
                r28 = this;
                r1 = r28
                r2 = r29
                r3 = r30
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
                r1.confirmedNumber = r0
                r4 = 1
                r1.setOrientation(r4)
                r5 = 17
                r1.setGravity(r5)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r3)
                r1.titleView = r6
                r7 = 1099956224(0x41900000, float:18.0)
                r6.setTextSize(r4, r7)
                android.widget.TextView r6 = r1.titleView
                java.lang.String r7 = "fonts/rmedium.ttf"
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r6.setTypeface(r7)
                android.widget.TextView r6 = r1.titleView
                r7 = 2131629058(0x7f0e1402, float:1.8885426E38)
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r7)
                r6.setText(r7)
                android.widget.TextView r6 = r1.titleView
                r6.setGravity(r5)
                android.widget.TextView r6 = r1.titleView
                r7 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r6.setLineSpacing(r8, r9)
                android.widget.TextView r6 = r1.titleView
                r10 = -1
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 1
                r13 = 1107296256(0x42000000, float:32.0)
                r14 = 0
                r15 = 1107296256(0x42000000, float:32.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r1.addView(r6, r8)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r3)
                r1.subtitleView = r6
                java.lang.String r8 = "StartText"
                r10 = 2131628207(0x7f0e10af, float:1.88837E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)
                r6.setText(r8)
                android.widget.TextView r6 = r1.subtitleView
                r8 = 1096810496(0x41600000, float:14.0)
                r6.setTextSize(r4, r8)
                android.widget.TextView r6 = r1.subtitleView
                r6.setGravity(r5)
                android.widget.TextView r5 = r1.subtitleView
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r9)
                android.widget.TextView r5 = r1.subtitleView
                r6 = -1
                r7 = -2
                r8 = 1
                r9 = 32
                r10 = 8
                r11 = 32
                r12 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12)
                r1.addView(r5, r6)
                org.telegram.ui.Components.TextViewSwitcher r5 = new org.telegram.ui.Components.TextViewSwitcher
                r5.<init>(r3)
                r1.countryButton = r5
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21 r6 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21
                r6.<init>(r3)
                r5.setFactory(r6)
                r5 = 2130771992(0x7var_, float:1.714709E38)
                android.view.animation.Animation r5 = android.view.animation.AnimationUtils.loadAnimation(r3, r5)
                android.view.animation.Interpolator r6 = org.telegram.ui.Components.Easings.easeInOutQuad
                r5.setInterpolator(r6)
                org.telegram.ui.Components.TextViewSwitcher r6 = r1.countryButton
                r6.setInAnimation(r5)
                android.widget.ImageView r6 = new android.widget.ImageView
                r6.<init>(r3)
                r1.chevronRight = r6
                r7 = 2131165809(0x7var_, float:1.7945846E38)
                r6.setImageResource(r7)
                android.widget.LinearLayout r6 = new android.widget.LinearLayout
                r6.<init>(r3)
                r6.setOrientation(r0)
                r7 = 16
                r6.setGravity(r7)
                org.telegram.ui.Components.TextViewSwitcher r8 = r1.countryButton
                r9 = 0
                r10 = -2
                r11 = 1065353216(0x3var_, float:1.0)
                r13 = 0
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (float) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r6.addView(r8, r9)
                android.widget.ImageView r8 = r1.chevronRight
                r9 = 1103101952(0x41CLASSNAME, float:24.0)
                r10 = 1103101952(0x41CLASSNAME, float:24.0)
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 1096810496(0x41600000, float:14.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinearRelatively(r9, r10, r11, r12, r13, r14, r15)
                r6.addView(r8, r9)
                org.telegram.ui.Components.OutlineTextContainerView r8 = new org.telegram.ui.Components.OutlineTextContainerView
                r8.<init>(r3)
                r1.countryOutlineView = r8
                r9 = 2131625174(0x7f0e04d6, float:1.8877549E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r9)
                r8.setText(r10)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                r10 = -1
                r11 = -1082130432(0xffffffffbvar_, float:-1.0)
                r12 = 48
                r14 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r8.addView(r6, r10)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                r8.setForceUseCenter(r4)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                r8.setFocusable(r4)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
                r8.setContentDescription(r9)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18 r9 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18
                r9.<init>(r1)
                r8.setOnFocusChangeListener(r9)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                r9 = -1
                r10 = 58
                r11 = 1098907648(0x41800000, float:16.0)
                r12 = 1103101952(0x41CLASSNAME, float:24.0)
                r13 = 1098907648(0x41800000, float:16.0)
                r14 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10, r11, r12, r13, r14)
                r1.addView(r8, r9)
                org.telegram.ui.Components.OutlineTextContainerView r8 = r1.countryOutlineView
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda15 r9 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda15
                r9.<init>(r1)
                r8.setOnClickListener(r9)
                android.widget.LinearLayout r8 = new android.widget.LinearLayout
                r8.<init>(r3)
                r8.setOrientation(r0)
                org.telegram.ui.Components.OutlineTextContainerView r9 = new org.telegram.ui.Components.OutlineTextContainerView
                r9.<init>(r3)
                r1.phoneOutlineView = r9
                r10 = -1
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 16
                r14 = 1090519040(0x41000000, float:8.0)
                r15 = 1098907648(0x41800000, float:16.0)
                r16 = 1090519040(0x41000000, float:8.0)
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r9.addView(r8, r10)
                org.telegram.ui.Components.OutlineTextContainerView r9 = r1.phoneOutlineView
                r10 = 2131627332(0x7f0e0d44, float:1.8881925E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r10)
                r9.setText(r11)
                org.telegram.ui.Components.OutlineTextContainerView r9 = r1.phoneOutlineView
                r11 = -1
                r12 = 58
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r9, r11)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r3)
                r1.plusTextView = r9
                java.lang.String r11 = "+"
                r9.setText(r11)
                android.widget.TextView r9 = r1.plusTextView
                r11 = 1098907648(0x41800000, float:16.0)
                r9.setTextSize(r4, r11)
                android.widget.TextView r9 = r1.plusTextView
                r9.setFocusable(r0)
                android.widget.TextView r9 = r1.plusTextView
                r12 = -2
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r12)
                r8.addView(r9, r13)
                org.telegram.ui.LoginActivity$PhoneView$1 r9 = new org.telegram.ui.LoginActivity$PhoneView$1
                r9.<init>(r3, r2)
                r1.codeField = r9
                r13 = 3
                r9.setInputType(r13)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r14 = 1101004800(0x41a00000, float:20.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r9.setCursorSize(r15)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r15 = 1069547520(0x3fCLASSNAME, float:1.5)
                r9.setCursorWidth(r15)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r16 = 1092616192(0x41200000, float:10.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
                r9.setPadding(r12, r0, r0, r0)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r9.setTextSize(r4, r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r9.setMaxLines(r4)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r12 = 19
                r9.setGravity(r12)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r7 = 268435461(0x10000005, float:2.5243564E-29)
                r9.setImeOptions(r7)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r10 = 0
                r9.setBackground(r10)
                int r9 = android.os.Build.VERSION.SDK_INT
                r10 = 21
                if (r9 < r10) goto L_0x0231
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                boolean r19 = r28.hasCustomKeyboard()
                if (r19 == 0) goto L_0x022d
                boolean r19 = r29.isCustomKeyboardForceDisabled()
                if (r19 == 0) goto L_0x022b
                goto L_0x022d
            L_0x022b:
                r10 = 0
                goto L_0x022e
            L_0x022d:
                r10 = 1
            L_0x022e:
                r9.setShowSoftInputOnFocus(r10)
            L_0x0231:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r10 = 2131626348(0x7f0e096c, float:1.887993E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
                r9.setContentDescription(r10)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                r20 = 55
                r21 = 36
                r22 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                r23 = 0
                r24 = 0
                r25 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
                r8.addView(r9, r10)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$2 r10 = new org.telegram.ui.LoginActivity$PhoneView$2
                r10.<init>(r2)
                r9.addTextChangedListener(r10)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r9 = r1.codeField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda19 r10 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda19
                r10.<init>(r1)
                r9.setOnEditorActionListener(r10)
                android.view.View r9 = new android.view.View
                r9.<init>(r3)
                r1.codeDividerView = r9
                r20 = 0
                r21 = -1
                r22 = 1082130432(0x40800000, float:4.0)
                r23 = 1090519040(0x41000000, float:8.0)
                r24 = 1094713344(0x41400000, float:12.0)
                r25 = 1090519040(0x41000000, float:8.0)
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
                r10 = 1056964608(0x3var_, float:0.5)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r7 = 2
                int r10 = java.lang.Math.max(r7, r10)
                r9.width = r10
                android.view.View r10 = r1.codeDividerView
                r8.addView(r10, r9)
                org.telegram.ui.LoginActivity$PhoneView$3 r10 = new org.telegram.ui.LoginActivity$PhoneView$3
                r10.<init>(r3, r2)
                r1.phoneField = r10
                r10.setInputType(r13)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r10.setPadding(r0, r0, r0, r0)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r10.setCursorSize(r14)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r10.setCursorWidth(r15)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r10.setTextSize(r4, r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r10.setMaxLines(r4)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r10.setGravity(r12)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r11 = 268435461(0x10000005, float:2.5243564E-29)
                r10.setImeOptions(r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r11 = 0
                r10.setBackground(r11)
                int r10 = android.os.Build.VERSION.SDK_INT
                r11 = 21
                if (r10 < r11) goto L_0x02e4
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                boolean r11 = r28.hasCustomKeyboard()
                if (r11 == 0) goto L_0x02e0
                boolean r11 = r29.isCustomKeyboardForceDisabled()
                if (r11 == 0) goto L_0x02de
                goto L_0x02e0
            L_0x02de:
                r11 = 0
                goto L_0x02e1
            L_0x02e0:
                r11 = 1
            L_0x02e1:
                r10.setShowSoftInputOnFocus(r11)
            L_0x02e4:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r11 = 2131627332(0x7f0e0d44, float:1.8881925E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r11)
                r10.setContentDescription(r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                r11 = -1
                r12 = 1108344832(0x42100000, float:36.0)
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12)
                r8.addView(r10, r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$4 r11 = new org.telegram.ui.LoginActivity$PhoneView$4
                r11.<init>(r2)
                r10.addTextChangedListener(r11)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r10 = r1.phoneField
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda20 r11 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda20
                r11.<init>(r1)
                r10.setOnEditorActionListener(r11)
                r10 = 72
                boolean r11 = r29.newAccount
                java.lang.String r15 = ""
                if (r11 == 0) goto L_0x036d
                org.telegram.ui.Cells.CheckBoxCell r11 = new org.telegram.ui.Cells.CheckBoxCell
                r11.<init>(r3, r7)
                r1.syncContactsBox = r11
                r12 = 2131628306(0x7f0e1112, float:1.88839E38)
                java.lang.String r14 = "SyncContacts"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
                boolean r14 = r29.syncContacts
                r11.setText(r12, r15, r14, r0)
                org.telegram.ui.Cells.CheckBoxCell r11 = r1.syncContactsBox
                r21 = -2
                r22 = -1
                r23 = 51
                r24 = 16
                r25 = 0
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0353
                boolean r12 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r12 == 0) goto L_0x0353
                int r12 = android.os.Build.VERSION.SDK_INT
                r14 = 21
                if (r12 < r14) goto L_0x0350
                r12 = 56
                goto L_0x0354
            L_0x0350:
                r12 = 60
                goto L_0x0354
            L_0x0353:
                r12 = 0
            L_0x0354:
                r14 = 16
                int r26 = r14 + r12
                r27 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r1.addView(r11, r12)
                int r10 = r10 + -24
                org.telegram.ui.Cells.CheckBoxCell r11 = r1.syncContactsBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda16 r12 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda16
                r12.<init>(r1)
                r11.setOnClickListener(r12)
            L_0x036d:
                boolean r11 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
                if (r11 == 0) goto L_0x03bd
                org.telegram.ui.Cells.CheckBoxCell r11 = new org.telegram.ui.Cells.CheckBoxCell
                r11.<init>(r3, r7)
                r1.testBackendCheckBox = r11
                boolean r12 = r29.testBackend
                java.lang.String r14 = "Test Backend"
                r11.setText(r14, r15, r12, r0)
                org.telegram.ui.Cells.CheckBoxCell r11 = r1.testBackendCheckBox
                r21 = -2
                r22 = -1
                r23 = 51
                r24 = 16
                r25 = 0
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x03a3
                boolean r12 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r12 == 0) goto L_0x03a3
                int r12 = android.os.Build.VERSION.SDK_INT
                r14 = 21
                if (r12 < r14) goto L_0x03a0
                r12 = 56
                goto L_0x03a4
            L_0x03a0:
                r12 = 60
                goto L_0x03a4
            L_0x03a3:
                r12 = 0
            L_0x03a4:
                r14 = 16
                int r26 = r14 + r12
                r27 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r1.addView(r11, r12)
                int r10 = r10 + -24
                org.telegram.ui.Cells.CheckBoxCell r11 = r1.testBackendCheckBox
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda17 r12 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda17
                r12.<init>(r1)
                r11.setOnClickListener(r12)
            L_0x03bd:
                if (r10 <= 0) goto L_0x03da
                boolean r11 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r11 != 0) goto L_0x03da
                android.widget.Space r11 = new android.widget.Space
                r11.<init>(r3)
                float r12 = (float) r10
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r11.setMinimumHeight(r12)
                r12 = -2
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r12)
                r1.addView(r11, r12)
            L_0x03da:
                java.util.HashMap r11 = new java.util.HashMap
                r11.<init>()
                java.io.BufferedReader r12 = new java.io.BufferedReader     // Catch:{ Exception -> 0x044e }
                java.io.InputStreamReader r14 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x044e }
                android.content.res.Resources r16 = r28.getResources()     // Catch:{ Exception -> 0x044e }
                android.content.res.AssetManager r13 = r16.getAssets()     // Catch:{ Exception -> 0x044e }
                java.lang.String r4 = "countries.txt"
                java.io.InputStream r4 = r13.open(r4)     // Catch:{ Exception -> 0x044e }
                r14.<init>(r4)     // Catch:{ Exception -> 0x044e }
                r12.<init>(r14)     // Catch:{ Exception -> 0x044e }
                r4 = r12
            L_0x03f8:
                java.lang.String r12 = r4.readLine()     // Catch:{ Exception -> 0x044e }
                r13 = r12
                if (r12 == 0) goto L_0x044a
                java.lang.String r12 = ";"
                java.lang.String[] r12 = r13.split(r12)     // Catch:{ Exception -> 0x044e }
                org.telegram.ui.CountrySelectActivity$Country r14 = new org.telegram.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x044e }
                r14.<init>()     // Catch:{ Exception -> 0x044e }
                r0 = r12[r7]     // Catch:{ Exception -> 0x044e }
                r14.name = r0     // Catch:{ Exception -> 0x044e }
                r0 = 0
                r7 = r12[r0]     // Catch:{ Exception -> 0x044e }
                r14.code = r7     // Catch:{ Exception -> 0x044e }
                r7 = 1
                r0 = r12[r7]     // Catch:{ Exception -> 0x044e }
                r14.shortname = r0     // Catch:{ Exception -> 0x044e }
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r0 = r1.countriesArray     // Catch:{ Exception -> 0x044e }
                r7 = 0
                r0.add(r7, r14)     // Catch:{ Exception -> 0x044e }
                java.util.HashMap<java.lang.String, org.telegram.ui.CountrySelectActivity$Country> r0 = r1.codesMap     // Catch:{ Exception -> 0x044e }
                r2 = r12[r7]     // Catch:{ Exception -> 0x044e }
                r0.put(r2, r14)     // Catch:{ Exception -> 0x044e }
                int r0 = r12.length     // Catch:{ Exception -> 0x044e }
                r2 = 3
                if (r0 <= r2) goto L_0x043a
                java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> r0 = r1.phoneFormatMap     // Catch:{ Exception -> 0x044e }
                r7 = 0
                r2 = r12[r7]     // Catch:{ Exception -> 0x044e }
                r17 = 3
                r18 = r12[r17]     // Catch:{ Exception -> 0x044e }
                java.util.List r7 = java.util.Collections.singletonList(r18)     // Catch:{ Exception -> 0x044e }
                r0.put(r2, r7)     // Catch:{ Exception -> 0x044e }
                goto L_0x043c
            L_0x043a:
                r17 = 3
            L_0x043c:
                r2 = 1
                r0 = r12[r2]     // Catch:{ Exception -> 0x044e }
                r2 = 2
                r7 = r12[r2]     // Catch:{ Exception -> 0x044e }
                r11.put(r0, r7)     // Catch:{ Exception -> 0x044e }
                r2 = r29
                r0 = 0
                r7 = 2
                goto L_0x03f8
            L_0x044a:
                r4.close()     // Catch:{ Exception -> 0x044e }
                goto L_0x0452
            L_0x044e:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0452:
                java.util.ArrayList<org.telegram.ui.CountrySelectActivity$Country> r0 = r1.countriesArray
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda8 r2 = org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda8.INSTANCE
                java.util.Comparator r2 = j$.util.Comparator.CC.comparing(r2)
                java.util.Collections.sort(r0, r2)
                r2 = 0
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x046b }
                java.lang.String r4 = "phone"
                java.lang.Object r0 = r0.getSystemService(r4)     // Catch:{ Exception -> 0x046b }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x046b }
                r2 = 0
                goto L_0x046f
            L_0x046b:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x046f:
                if (r2 == 0) goto L_0x0479
                java.lang.String r0 = r2.toUpperCase()
                r1.setCountry(r11, r0)
                goto L_0x0490
            L_0x0479:
                org.telegram.tgnet.TLRPC$TL_help_getNearestDc r0 = new org.telegram.tgnet.TLRPC$TL_help_getNearestDc
                r0.<init>()
                org.telegram.messenger.AccountInstance r4 = r29.getAccountInstance()
                org.telegram.tgnet.ConnectionsManager r4 = r4.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13
                r7.<init>(r1, r11)
                r12 = 10
                r4.sendRequest(r0, r7, r12)
            L_0x0490:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x04a4
                r4 = 0
                r1.setCountryButtonText(r4)
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                r0.setHintText(r4)
                r4 = 1
                r1.countryState = r4
            L_0x04a4:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x04bb
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                r0.requestFocus()
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.phoneField
                int r4 = r0.length()
                r0.setSelection(r4)
                goto L_0x04c0
            L_0x04bb:
                org.telegram.ui.Components.AnimatedPhoneNumberEditText r0 = r1.codeField
                r0.requestFocus()
            L_0x04c0:
                org.telegram.tgnet.TLRPC$TL_help_getCountriesList r0 = new org.telegram.tgnet.TLRPC$TL_help_getCountriesList
                r0.<init>()
                r0.lang_code = r15
                org.telegram.tgnet.ConnectionsManager r4 = r29.getConnectionsManager()
                org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9 r7 = new org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9
                r7.<init>(r1)
                r12 = 10
                r4.sendRequest(r0, r7, r12)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        static /* synthetic */ View lambda$new$0(Context context) {
            TextView tv = new TextView(context);
            tv.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            tv.setTextSize(1, 16.0f);
            tv.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            tv.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            tv.setMaxLines(1);
            tv.setSingleLine(true);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            return tv;
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2568lambda$new$1$orgtelegramuiLoginActivity$PhoneView(View v, boolean hasFocus) {
            this.countryOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2575lambda$new$4$orgtelegramuiLoginActivity$PhoneView(View view) {
            CountrySelectActivity fragment = new CountrySelectActivity(true, this.countriesArray);
            fragment.setCountrySelectActivityDelegate(new LoginActivity$PhoneView$$ExternalSyntheticLambda14(this));
            this.this$0.presentFragment(fragment);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2574lambda$new$3$orgtelegramuiLoginActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda22(this), 300);
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2573lambda$new$2$orgtelegramuiLoginActivity$PhoneView() {
            boolean unused = this.this$0.showKeyboard(this.phoneField);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m2576lambda$new$5$orgtelegramuiLoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
            return true;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m2577lambda$new$6$orgtelegramuiLoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            if (this.this$0.phoneNumberConfirmView != null) {
                this.this$0.phoneNumberConfirmView.popupFabContainer.callOnClick();
                return true;
            }
            m2580lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView((String) null);
            return true;
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2578lambda$new$7$orgtelegramuiLoginActivity$PhoneView(View v) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.syncContacts = !loginActivity.syncContacts;
                ((CheckBoxCell) v).setChecked(this.this$0.syncContacts, true);
                if (this.this$0.syncContacts) {
                    BulletinFactory.of(this.this$0.slideViewsContainer, (Theme.ResourcesProvider) null).createSimpleBulletin(NUM, LocaleController.getString("SyncContactsOn", NUM)).show();
                } else {
                    BulletinFactory.of(this.this$0.slideViewsContainer, (Theme.ResourcesProvider) null).createSimpleBulletin(NUM, LocaleController.getString("SyncContactsOff", NUM)).show();
                }
            }
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2579lambda$new$8$orgtelegramuiLoginActivity$PhoneView(View v) {
            if (this.this$0.getParentActivity() != null) {
                LoginActivity loginActivity = this.this$0;
                boolean unused = loginActivity.testBackend = !loginActivity.testBackend;
                ((CheckBoxCell) v).setChecked(this.this$0.testBackend, true);
            }
        }

        /* renamed from: lambda$new$11$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2570lambda$new$11$orgtelegramuiLoginActivity$PhoneView(HashMap languageMap, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda4(this, response, languageMap));
        }

        /* renamed from: lambda$new$10$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2569lambda$new$10$orgtelegramuiLoginActivity$PhoneView(TLObject response, HashMap languageMap) {
            if (response != null) {
                TLRPC.TL_nearestDc res = (TLRPC.TL_nearestDc) response;
                if (this.codeField.length() == 0) {
                    setCountry(languageMap, res.country.toUpperCase());
                }
            }
        }

        /* renamed from: lambda$new$13$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2572lambda$new$13$orgtelegramuiLoginActivity$PhoneView(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda6(this, error, response));
        }

        /* renamed from: lambda$new$12$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2571lambda$new$12$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, TLObject response) {
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
                        countryWithCode.shortname = c.iso2;
                        this.countriesArray.add(countryWithCode);
                        this.codesMap.put(c.country_codes.get(k).country_code, countryWithCode);
                        if (c.country_codes.get(k).patterns.size() > 0) {
                            this.phoneFormatMap.put(c.country_codes.get(k).country_code, c.country_codes.get(k).patterns);
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

        public boolean hasCustomKeyboard() {
            return true;
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
            String code = country.code;
            this.codeField.setText(code);
            setCountryHint(code, country);
            this.currentCountry = country;
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }

        /* access modifiers changed from: private */
        public void setCountryHint(String code, CountrySelectActivity.Country country) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            String flag = LocaleController.getLanguageFlag(country.shortname);
            if (flag != null) {
                sb.append(flag).append(" ");
                sb.setSpan(new ReplacementSpan() {
                    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                        return AndroidUtilities.dp(16.0f);
                    }

                    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    }
                }, flag.length(), flag.length() + 1, 0);
            }
            sb.append(country.name);
            setCountryButtonText(Emoji.replaceEmoji(sb, this.countryButton.getCurrentView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            String str = null;
            if (this.phoneFormatMap.get(code) == null || this.phoneFormatMap.get(code).isEmpty()) {
                this.phoneField.setHintText((String) null);
                return;
            }
            String hint = (String) this.phoneFormatMap.get(code).get(0);
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            if (hint != null) {
                str = hint.replace('X', '0');
            }
            animatedPhoneNumberEditText.setHintText(str);
        }

        /* access modifiers changed from: private */
        public void setCountryButtonText(CharSequence cs) {
            Animation anim = AnimationUtils.loadAnimation(ApplicationLoader.applicationContext, (this.countryButton.getCurrentView().getText() == null || cs != null) ? NUM : NUM);
            anim.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setOutAnimation(anim);
            CharSequence prevText = this.countryButton.getCurrentView().getText();
            this.countryButton.setText(cs, (!TextUtils.isEmpty(cs) || !TextUtils.isEmpty(prevText)) && !ColorUtils$$ExternalSyntheticBackport0.m(prevText, cs));
            this.countryOutlineView.animateSelection(cs != null ? 1.0f : 0.0f);
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

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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

        /* renamed from: onNextPressed */
        public void m2580lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(String code) {
            boolean allowCall;
            boolean allowCancelCall;
            boolean testBackend;
            ArrayList<TLRPC.TL_auth_loggedOut> tokens;
            boolean simcardAvailable;
            int resId;
            final String str = code;
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("sim status = " + tm.getSimState());
                }
                if (this.codeField.length() == 0) {
                } else if (this.phoneField.length() == 0) {
                    TelephonyManager telephonyManager = tm;
                } else {
                    String phoneNumber = "+" + this.codeField.getText() + " " + this.phoneField.getText();
                    String obj = this.codeField.getText().toString();
                    if (this.confirmedNumber) {
                        this.confirmedNumber = false;
                        if (this.this$0.phoneNumberConfirmView != null) {
                            this.this$0.phoneNumberConfirmView.dismiss();
                        }
                        boolean simcardAvailable2 = AndroidUtilities.isSimAvailable();
                        boolean allowReadCallLog = true;
                        if (Build.VERSION.SDK_INT < 23 || !simcardAvailable2) {
                            allowCall = true;
                            allowCancelCall = true;
                        } else {
                            allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                            boolean allowCancelCall2 = this.this$0.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                            allowReadCallLog = Build.VERSION.SDK_INT < 28 || this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                            boolean allowReadPhoneNumbers = true;
                            if (Build.VERSION.SDK_INT >= 26) {
                                allowReadPhoneNumbers = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                            }
                            if (this.this$0.checkPermissions) {
                                this.this$0.permissionsItems.clear();
                                if (!allowCall) {
                                    this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                                }
                                if (!allowCancelCall2) {
                                    this.this$0.permissionsItems.add("android.permission.CALL_PHONE");
                                }
                                if (!allowReadCallLog) {
                                    this.this$0.permissionsItems.add("android.permission.READ_CALL_LOG");
                                }
                                if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                                    this.this$0.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                                }
                                if (!this.this$0.permissionsItems.isEmpty()) {
                                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                    if (preferences.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                        preferences.edit().putBoolean("firstlogin", false).commit();
                                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                        builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                                        if (!allowCall && (!allowCancelCall2 || !allowReadCallLog)) {
                                            builder.setMessage(LocaleController.getString("AllowReadCallAndLog", NUM));
                                            resId = NUM;
                                        } else if (!allowCancelCall2 || !allowReadCallLog) {
                                            builder.setMessage(LocaleController.getString("AllowReadCallLog", NUM));
                                            resId = NUM;
                                        } else {
                                            builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                            resId = NUM;
                                        }
                                        builder.setTopAnimation(resId, 46, false, Theme.getColor("dialogTopBackground"));
                                        LoginActivity loginActivity = this.this$0;
                                        Dialog unused = loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                                        this.confirmedNumber = true;
                                        return;
                                    }
                                    try {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[0]), 6);
                                        return;
                                    } catch (Exception e) {
                                        FileLog.e((Throwable) e);
                                        return;
                                    }
                                }
                            }
                            allowCancelCall = allowCancelCall2;
                            boolean z = allowReadPhoneNumbers;
                        }
                        int i = this.countryState;
                        if (i == 1) {
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ChooseCountry", NUM));
                            this.this$0.needHideProgress(false);
                            return;
                        } else if (i != 2 || BuildVars.DEBUG_VERSION) {
                            String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                            boolean testBackend2 = BuildVars.DEBUG_PRIVATE_VERSION && this.this$0.getConnectionsManager().isTestBackend();
                            if (testBackend2 != this.this$0.testBackend) {
                                this.this$0.getConnectionsManager().switchBackend(false);
                                testBackend = this.this$0.testBackend;
                            } else {
                                testBackend = testBackend2;
                            }
                            if (this.this$0.getParentActivity() instanceof LaunchActivity) {
                                for (int a = 0; a < 3; a++) {
                                    UserConfig userConfig = UserConfig.getInstance(a);
                                    if (userConfig.isClientActivated()) {
                                        if (PhoneNumberUtils.compare(phone, userConfig.getCurrentUser().phone)) {
                                            UserConfig userConfig2 = userConfig;
                                            if (ConnectionsManager.getInstance(a).isTestBackend() == testBackend) {
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                                builder2.setTitle(LocaleController.getString(NUM));
                                                builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", NUM));
                                                builder2.setPositiveButton(LocaleController.getString("AccountSwitch", NUM), new LoginActivity$PhoneView$$ExternalSyntheticLambda10(this, a));
                                                builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                                this.this$0.showDialog(builder2.create());
                                                this.this$0.needHideProgress(false);
                                                return;
                                            }
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
                            req.settings.allow_flashcall = simcardAvailable2 && allowCall && allowCancelCall && allowReadCallLog;
                            req.settings.allow_missed_call = simcardAvailable2 && allowCall;
                            req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                            ArrayList<TLRPC.TL_auth_loggedOut> tokens2 = MessagesController.getSavedLogOutTokens();
                            if (tokens2 != null) {
                                int i2 = 0;
                                while (true) {
                                    boolean allowCancelCall3 = allowCancelCall;
                                    if (i2 >= tokens2.size()) {
                                        break;
                                    }
                                    if (req.settings.logout_tokens == null) {
                                        simcardAvailable = simcardAvailable2;
                                        req.settings.logout_tokens = new ArrayList<>();
                                    } else {
                                        simcardAvailable = simcardAvailable2;
                                    }
                                    req.settings.logout_tokens.add(tokens2.get(i2).future_auth_token);
                                    i2++;
                                    allowCancelCall = allowCancelCall3;
                                    simcardAvailable2 = simcardAvailable;
                                }
                                MessagesController.saveLogOutTokens(tokens2);
                            } else {
                                boolean z2 = simcardAvailable2;
                            }
                            if (req.settings.logout_tokens != null) {
                                req.settings.flags |= 64;
                            }
                            SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (req.settings.allow_app_hash) {
                                tokens = tokens2;
                                preferences2.edit().putString("sms_hash", BuildVars.SMS_HASH).apply();
                            } else {
                                tokens = tokens2;
                                preferences2.edit().remove("sms_hash").apply();
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
                            params.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                            try {
                                params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                            } catch (Exception e3) {
                                FileLog.e((Throwable) e3);
                                params.putString("ephone", "+" + phone);
                            }
                            params.putString("phoneFormated", phone);
                            this.nextPressed = true;
                            PhoneInputData phoneInputData = new PhoneInputData();
                            String unused2 = phoneInputData.phoneNumber = "+" + this.codeField.getText() + " " + this.phoneField.getText();
                            CountrySelectActivity.Country unused3 = phoneInputData.country = this.currentCountry;
                            List unused4 = phoneInputData.patterns = this.phoneFormatMap.get(this.codeField.getText().toString());
                            ArrayList<TLRPC.TL_auth_loggedOut> arrayList = tokens;
                            SharedPreferences sharedPreferences = preferences2;
                            LoginActivity$PhoneView$$ExternalSyntheticLambda11 loginActivity$PhoneView$$ExternalSyntheticLambda11 = r1;
                            TelephonyManager telephonyManager2 = tm;
                            Bundle bundle = params;
                            LoginActivity$PhoneView$$ExternalSyntheticLambda11 loginActivity$PhoneView$$ExternalSyntheticLambda112 = new LoginActivity$PhoneView$$ExternalSyntheticLambda11(this, params, phone, req, phoneInputData);
                            this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, loginActivity$PhoneView$$ExternalSyntheticLambda11, 27));
                            return;
                        } else {
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("WrongCountry", NUM));
                            this.this$0.needHideProgress(false);
                            return;
                        }
                    } else if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y || this.this$0.isCustomKeyboardVisible() || this.this$0.sizeNotifierFrameLayout.measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                        PhoneNumberConfirmView unused5 = this.this$0.phoneNumberConfirmView = new PhoneNumberConfirmView(this.this$0.fragmentView.getContext(), (ViewGroup) this.this$0.fragmentView, this.this$0.floatingButtonContainer, phoneNumber, new PhoneNumberConfirmView.IConfirmDialogCallback() {
                            public void onFabPressed(PhoneNumberConfirmView confirmView, TransformableLoginButtonView fab) {
                                onConfirm(confirmView);
                            }

                            public void onEditPressed(PhoneNumberConfirmView confirmView, TextView editTextView) {
                                confirmView.dismiss();
                            }

                            public void onConfirmPressed(PhoneNumberConfirmView confirmView, TextView confirmTextView) {
                                onConfirm(confirmView);
                            }

                            public void onDismiss(PhoneNumberConfirmView confirmView) {
                                PhoneNumberConfirmView unused = PhoneView.this.this$0.phoneNumberConfirmView = null;
                            }

                            private void onConfirm(PhoneNumberConfirmView confirmView) {
                                int resId;
                                boolean unused = PhoneView.this.confirmedNumber = true;
                                int unused2 = PhoneView.this.this$0.currentDoneType = 0;
                                PhoneView.this.this$0.needShowProgress(0, false);
                                if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.isSimAvailable()) {
                                    boolean allowCall = PhoneView.this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                                    boolean allowCancelCall = PhoneView.this.this$0.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                                    boolean allowReadCallLog = Build.VERSION.SDK_INT < 28 || PhoneView.this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                                    boolean allowReadPhoneNumbers = Build.VERSION.SDK_INT < 26 || PhoneView.this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                                    if (PhoneView.this.this$0.checkPermissions) {
                                        PhoneView.this.this$0.permissionsItems.clear();
                                        if (!allowCall) {
                                            PhoneView.this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                                        }
                                        if (!allowCancelCall) {
                                            PhoneView.this.this$0.permissionsItems.add("android.permission.CALL_PHONE");
                                        }
                                        if (!allowReadCallLog) {
                                            PhoneView.this.this$0.permissionsItems.add("android.permission.READ_CALL_LOG");
                                        }
                                        if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                                            PhoneView.this.this$0.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                                        }
                                        if (!PhoneView.this.this$0.permissionsItems.isEmpty()) {
                                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                            if (preferences.getBoolean("firstlogin", true) || PhoneView.this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || PhoneView.this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                                preferences.edit().putBoolean("firstlogin", false).commit();
                                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhoneView.this.this$0.getParentActivity());
                                                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
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
                                                builder.setTopAnimation(resId, 46, false, Theme.getColor("dialogTopBackground"));
                                                Dialog unused3 = PhoneView.this.this$0.permissionsDialog = PhoneView.this.this$0.showDialog(builder.create());
                                                boolean unused4 = PhoneView.this.confirmedNumber = true;
                                                return;
                                            }
                                            try {
                                                PhoneView.this.this$0.getParentActivity().requestPermissions((String[]) PhoneView.this.this$0.permissionsItems.toArray(new String[0]), 6);
                                                return;
                                            } catch (Exception e) {
                                                FileLog.e((Throwable) e);
                                                return;
                                            }
                                        }
                                    }
                                }
                                confirmView.animateProgress(new LoginActivity$PhoneView$6$$ExternalSyntheticLambda1(this, confirmView, str));
                            }

                            /* renamed from: lambda$onConfirm$1$org-telegram-ui-LoginActivity$PhoneView$6  reason: not valid java name */
                            public /* synthetic */ void m2589lambda$onConfirm$1$orgtelegramuiLoginActivity$PhoneView$6(PhoneNumberConfirmView confirmView, String code) {
                                confirmView.dismiss();
                                AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$6$$ExternalSyntheticLambda0(this, code, confirmView), 150);
                            }

                            /* renamed from: lambda$onConfirm$0$org-telegram-ui-LoginActivity$PhoneView$6  reason: not valid java name */
                            public /* synthetic */ void m2588lambda$onConfirm$0$orgtelegramuiLoginActivity$PhoneView$6(String code, PhoneNumberConfirmView confirmView) {
                                PhoneView.this.m2580lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(code);
                                PhoneView.this.this$0.floatingProgressView.sync(confirmView.floatingProgressView);
                            }
                        });
                        this.this$0.phoneNumberConfirmView.show();
                        return;
                    } else {
                        Runnable unused6 = this.this$0.keyboardHideCallback = new LoginActivity$PhoneView$$ExternalSyntheticLambda2(this, str);
                        AndroidUtilities.hideKeyboard(this.this$0.fragmentView);
                        return;
                    }
                }
                this.this$0.onFieldError(this.phoneOutlineView, false);
            }
        }

        /* renamed from: lambda$onNextPressed$15$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2581lambda$onNextPressed$15$orgtelegramuiLoginActivity$PhoneView(String code) {
            postDelayed(new LoginActivity$PhoneView$$ExternalSyntheticLambda1(this, code), 200);
        }

        /* renamed from: lambda$onNextPressed$16$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2582lambda$onNextPressed$16$orgtelegramuiLoginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(num, false);
            }
            this.this$0.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$20$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2586lambda$onNextPressed$20$orgtelegramuiLoginActivity$PhoneView(Bundle params, String phone, TLRPC.TL_auth_sendCode req, PhoneInputData phoneInputData, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda5(this, error, params, response, phone, req, phoneInputData));
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2585lambda$onNextPressed$19$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, String phone, TLRPC.TL_auth_sendCode req, PhoneInputData phoneInputData) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$PhoneView$$ExternalSyntheticLambda12(this, phone), 10);
                } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.needShowInvalidAlert(this.this$0, req.phone_number, phoneInputData, false);
                } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                    LoginActivity.needShowInvalidAlert(this.this$0, req.phone_number, phoneInputData, true);
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
                }
            }
            this.this$0.needHideProgress(false);
        }

        /* renamed from: lambda$onNextPressed$18$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2584lambda$onNextPressed$18$orgtelegramuiLoginActivity$PhoneView(String phone, TLObject response1, TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda7(this, error1, response1, phone));
        }

        /* renamed from: lambda$onNextPressed$17$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2583lambda$onNextPressed$17$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error1, TLObject response1, String phone) {
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
                bundle.putString("phoneFormated", phone);
                this.this$0.setPage(6, true, bundle, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(NUM), error1.text);
        }

        public void fillNumber() {
            if (!this.numberFilled) {
                try {
                    TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (AndroidUtilities.isSimAvailable()) {
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
                                if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_NUMBERS");
                                }
                                if (!this.this$0.permissionsShowItems.isEmpty()) {
                                    Runnable r = new LoginActivity$PhoneView$$ExternalSyntheticLambda3(this, new ArrayList<>(this.this$0.permissionsShowItems));
                                    if (this.this$0.isAnimatingIntro) {
                                        Runnable unused = this.this$0.animationFinishCallback = r;
                                        return;
                                    } else {
                                        r.run();
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                        this.numberFilled = true;
                        if (!this.this$0.newAccount && allowCall && allowReadPhoneNumbers) {
                            this.codeField.setAlpha(0.0f);
                            this.phoneField.setAlpha(0.0f);
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
                                    AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                                    animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                                }
                            }
                            if (this.phoneField.length() > 0) {
                                AnimatorSet set = new AnimatorSet().setDuration(300);
                                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.codeField, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.phoneField, View.ALPHA, new float[]{1.0f})});
                                set.start();
                                this.confirmedNumber = true;
                                return;
                            }
                            this.codeField.setAlpha(1.0f);
                            this.phoneField.setAlpha(1.0f);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }

        /* renamed from: lambda$fillNumber$21$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2567lambda$fillNumber$21$orgtelegramuiLoginActivity$PhoneView(List callbackPermissionItems) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (preferences.getBoolean("firstloginshow", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                preferences.edit().putBoolean("firstloginshow", false).commit();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTopAnimation(NUM, 46, false, Theme.getColor("dialogTopBackground"));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                LoginActivity loginActivity = this.this$0;
                Dialog unused = loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
                boolean unused2 = this.this$0.needRequestPermissions = true;
                return;
            }
            this.this$0.getParentActivity().requestPermissions((String[]) callbackPermissionItems.toArray(new String[0]), 7);
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$ExternalSyntheticLambda0(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$22$org-telegram-ui-LoginActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m2587lambda$onShow$22$orgtelegramuiLoginActivity$PhoneView() {
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

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.emojiLoaded) {
                this.countryButton.getCurrentView().invalidate();
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
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
        public Runnable errorColorTimeout = new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33(this);
        private ViewSwitcher errorViewSwitcher;
        RLottieDrawable hintDrawable;
        private boolean ignoreOnTextChange;
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

        static /* synthetic */ int access$7826(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$8426(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2525lambda$new$0$orgtelegramuiLoginActivity$LoginActivitySmsView() {
            this.postedErrorColorTimeout = false;
            for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                animateErrorProgress.animateErrorProgress(0.0f);
            }
            if (this.errorViewSwitcher.getCurrentView() != this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.LoginActivity r37, android.content.Context r38, int r39) {
            /*
                r36 = this;
                r0 = r36
                r1 = r37
                r2 = r38
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
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33 r3 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33
                r3.<init>(r0)
                r0.errorColorTimeout = r3
                r3 = r39
                r0.currentType = r3
                r4 = 1
                r0.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                r6 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r8, r9)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                r8 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r8)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r8 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
                r5.setTypeface(r10)
                android.widget.TextView r5 = r0.titleTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                if (r10 == 0) goto L_0x006d
                r10 = 5
                goto L_0x006e
            L_0x006d:
                r10 = 3
            L_0x006e:
                r5.setGravity(r10)
                android.widget.TextView r5 = r0.titleTextView
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r5.setLineSpacing(r10, r9)
                android.widget.TextView r5 = r0.titleTextView
                r10 = 49
                r5.setGravity(r10)
                boolean r5 = r37.cancelAccountDeletionMode
                if (r5 == 0) goto L_0x0090
                r5 = 2131624754(0x7f0e0332, float:1.8876697E38)
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
                goto L_0x0091
            L_0x0090:
                r5 = 0
            L_0x0091:
                r12 = 0
                int r13 = r0.currentType
                r15 = 11
                r14 = -2
                r11 = 0
                if (r13 != r15) goto L_0x01e8
                android.widget.TextView r13 = r0.titleTextView
                if (r5 == 0) goto L_0x00a0
                r10 = r5
                goto L_0x00a9
            L_0x00a0:
                r15 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
                java.lang.String r10 = "MissedCallDescriptionTitle"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r15)
            L_0x00a9:
                r13.setText(r10)
                android.widget.FrameLayout r10 = new android.widget.FrameLayout
                r10.<init>(r2)
                android.widget.ImageView r13 = new android.widget.ImageView
                r13.<init>(r2)
                r0.missedCallArrowIcon = r13
                android.widget.ImageView r13 = new android.widget.ImageView
                r13.<init>(r2)
                r0.missedCallPhoneIcon = r13
                android.widget.ImageView r13 = r0.missedCallArrowIcon
                r10.addView(r13)
                android.widget.ImageView r13 = r0.missedCallPhoneIcon
                r10.addView(r13)
                android.widget.ImageView r13 = r0.missedCallArrowIcon
                r15 = 2131165638(0x7var_c6, float:1.7945499E38)
                r13.setImageResource(r15)
                android.widget.ImageView r13 = r0.missedCallPhoneIcon
                r15 = 2131165639(0x7var_c7, float:1.79455E38)
                r13.setImageResource(r15)
                r20 = 64
                r21 = 64
                r22 = 1
                r23 = 0
                r24 = 16
                r25 = 0
                r26 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r10, r13)
                android.widget.TextView r13 = r0.titleTextView
                r20 = -2
                r21 = -2
                r22 = 49
                r24 = 8
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
                r0.addView(r13, r15)
                android.widget.TextView r13 = new android.widget.TextView
                r13.<init>(r2)
                r0.missedCallDescriptionSubtitle = r13
                r13.setTextSize(r4, r6)
                android.widget.TextView r13 = r0.missedCallDescriptionSubtitle
                r13.setGravity(r4)
                android.widget.TextView r13 = r0.missedCallDescriptionSubtitle
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r15 = (float) r15
                r13.setLineSpacing(r15, r9)
                android.widget.TextView r13 = r0.missedCallDescriptionSubtitle
                r15 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
                java.lang.String r9 = "MissedCallDescriptionSubtitle"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r15)
                android.text.SpannableStringBuilder r9 = org.telegram.messenger.AndroidUtilities.replaceTags(r9)
                r13.setText(r9)
                android.widget.TextView r9 = r0.missedCallDescriptionSubtitle
                r21 = -1
                r22 = -2
                r23 = 49
                r24 = 36
                r25 = 16
                r26 = 36
                r27 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r0.addView(r9, r13)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$1 r9 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$1
                r9.<init>(r2, r1)
                r0.codeFieldContainer = r9
                android.widget.LinearLayout r9 = new android.widget.LinearLayout
                r9.<init>(r2)
                r9.setOrientation(r11)
                android.widget.TextView r13 = new android.widget.TextView
                r13.<init>(r2)
                r0.prefixTextView = r13
                r15 = 1101004800(0x41a00000, float:20.0)
                r13.setTextSize(r4, r15)
                android.widget.TextView r13 = r0.prefixTextView
                r13.setMaxLines(r4)
                android.widget.TextView r13 = r0.prefixTextView
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
                r13.setTypeface(r8)
                android.widget.TextView r8 = r0.prefixTextView
                r8.setPadding(r11, r11, r11, r11)
                android.widget.TextView r8 = r0.prefixTextView
                r13 = 16
                r8.setGravity(r13)
                android.widget.TextView r8 = r0.prefixTextView
                r21 = -2
                r22 = -1
                r23 = 16
                r24 = 0
                r25 = 0
                r26 = 4
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r9.addView(r8, r13)
                org.telegram.ui.CodeFieldContainer r8 = r0.codeFieldContainer
                r13 = -1
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r13)
                r9.addView(r8, r15)
                r22 = 34
                r23 = 1
                r25 = 28
                r26 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r0.addView(r9, r8)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.missedCallDescriptionSubtitle = r8
                r8.setTextSize(r4, r6)
                android.widget.TextView r6 = r0.missedCallDescriptionSubtitle
                r6.setGravity(r4)
                android.widget.TextView r6 = r0.missedCallDescriptionSubtitle
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r13 = 1065353216(0x3var_, float:1.0)
                r6.setLineSpacing(r8, r13)
                android.widget.TextView r6 = r0.missedCallDescriptionSubtitle
                r8 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
                java.lang.String r13 = "MissedCallDescriptionSubtitle2"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
                android.text.SpannableStringBuilder r8 = org.telegram.messenger.AndroidUtilities.replaceTags(r8)
                r6.setText(r8)
                android.widget.TextView r6 = r0.missedCallDescriptionSubtitle
                r21 = -1
                r22 = -2
                r23 = 49
                r24 = 36
                r26 = 36
                r27 = 12
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
                r0.addView(r6, r8)
                goto L_0x03f1
            L_0x01e8:
                r6 = 64
                r8 = 3
                if (r13 != r8) goto L_0x029e
                android.widget.TextView r8 = r0.confirmTextView
                r8.setGravity(r4)
                android.widget.FrameLayout r8 = new android.widget.FrameLayout
                r8.<init>(r2)
                r12 = r8
                r8 = -1
                r9 = 1065353216(0x3var_, float:1.0)
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r11, (float) r9)
                r0.addView(r12, r10)
                android.widget.LinearLayout r9 = new android.widget.LinearLayout
                r9.<init>(r2)
                r9.setOrientation(r4)
                r9.setGravity(r4)
                r10 = 17
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r14, (int) r10)
                r12.addView(r9, r13)
                android.view.ViewGroup$LayoutParams r8 = r9.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
                boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r10 == 0) goto L_0x0224
                r10 = 0
                goto L_0x0226
            L_0x0224:
                int r10 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            L_0x0226:
                r8.bottomMargin = r10
                android.widget.FrameLayout r10 = new android.widget.FrameLayout
                r10.<init>(r2)
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r14, (int) r4)
                r9.addView(r10, r13)
                org.telegram.ui.Components.RLottieImageView r13 = new org.telegram.ui.Components.RLottieImageView
                r13.<init>(r2)
                r0.blueImageView = r13
                org.telegram.ui.Components.RLottieDrawable r13 = new org.telegram.ui.Components.RLottieDrawable
                r22 = 2131558497(0x7f0d0061, float:1.8742311E38)
                r15 = 2131558497(0x7f0d0061, float:1.8742311E38)
                java.lang.String r23 = java.lang.String.valueOf(r15)
                r15 = 1115684864(0x42800000, float:64.0)
                int r24 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r25 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r26 = 1
                r27 = 0
                r21 = r13
                r21.<init>(r22, r23, r24, r25, r26, r27)
                r0.hintDrawable = r13
                org.telegram.ui.Components.RLottieImageView r14 = r0.blueImageView
                r14.setAnimation(r13)
                org.telegram.ui.Components.RLottieImageView r13 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r15)
                r10.addView(r13, r6)
                android.widget.TextView r6 = r0.titleTextView
                if (r5 == 0) goto L_0x0270
                r13 = r5
                goto L_0x0277
            L_0x0270:
                r13 = 2131629041(0x7f0e13f1, float:1.8885392E38)
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString((int) r13)
            L_0x0277:
                r6.setText(r13)
                android.widget.TextView r6 = r0.titleTextView
                r22 = -2
                r23 = -2
                r24 = 1
                r25 = 0
                r26 = 16
                r27 = 0
                r28 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r9.addView(r6, r13)
                android.widget.TextView r6 = r0.confirmTextView
                r26 = 8
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r9.addView(r6, r13)
                goto L_0x03f1
            L_0x029e:
                android.widget.TextView r8 = r0.confirmTextView
                r9 = 49
                r8.setGravity(r9)
                android.widget.FrameLayout r8 = new android.widget.FrameLayout
                r8.<init>(r2)
                r22 = -2
                r23 = -2
                r24 = 49
                r25 = 0
                r26 = 16
                r27 = 0
                r28 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r0.addView(r8, r9)
                int r9 = r0.currentType
                if (r9 != r4) goto L_0x02c5
                r6 = 128(0x80, float:1.794E-43)
            L_0x02c5:
                if (r9 != r4) goto L_0x02ea
                org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
                r23 = 2131558428(0x7f0d001c, float:1.8742172E38)
                r10 = 2131558428(0x7f0d001c, float:1.8742172E38)
                java.lang.String r24 = java.lang.String.valueOf(r10)
                float r10 = (float) r6
                int r25 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r6
                int r26 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r27 = 1
                r28 = 0
                r22 = r9
                r22.<init>(r23, r24, r25, r26, r27, r28)
                r0.hintDrawable = r9
                goto L_0x0366
            L_0x02ea:
                org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
                r30 = 2131558516(0x7f0d0074, float:1.874235E38)
                r10 = 2131558516(0x7f0d0074, float:1.874235E38)
                java.lang.String r31 = java.lang.String.valueOf(r10)
                float r10 = (float) r6
                int r32 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r6
                int r33 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r34 = 1
                r35 = 0
                r29 = r9
                r29.<init>(r30, r31, r32, r33, r34, r35)
                r0.hintDrawable = r9
                org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
                r23 = 2131558498(0x7f0d0062, float:1.8742314E38)
                r10 = 2131558498(0x7f0d0062, float:1.8742314E38)
                java.lang.String r24 = java.lang.String.valueOf(r10)
                float r10 = (float) r6
                int r25 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r6
                int r26 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r27 = 1
                r28 = 0
                r22 = r9
                r22.<init>(r23, r24, r25, r26, r27, r28)
                r0.starsToDotsDrawable = r9
                org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
                r30 = 2131558495(0x7f0d005f, float:1.8742307E38)
                r10 = 2131558495(0x7f0d005f, float:1.8742307E38)
                java.lang.String r31 = java.lang.String.valueOf(r10)
                float r10 = (float) r6
                int r32 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r6
                int r33 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r29 = r9
                r29.<init>(r30, r31, r32, r33, r34, r35)
                r0.dotsDrawable = r9
                org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
                r23 = 2131558496(0x7f0d0060, float:1.874231E38)
                r10 = 2131558496(0x7f0d0060, float:1.874231E38)
                java.lang.String r24 = java.lang.String.valueOf(r10)
                float r10 = (float) r6
                int r25 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r6
                int r26 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r22 = r9
                r22.<init>(r23, r24, r25, r26, r27, r28)
                r0.dotsToStarsDrawable = r9
            L_0x0366:
                org.telegram.ui.Components.RLottieImageView r9 = new org.telegram.ui.Components.RLottieImageView
                r9.<init>(r2)
                r0.blueImageView = r9
                org.telegram.ui.Components.RLottieDrawable r10 = r0.hintDrawable
                r9.setAnimation(r10)
                int r9 = r0.currentType
                if (r9 != r4) goto L_0x0389
                boolean r9 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r9 != 0) goto L_0x0389
                org.telegram.ui.Components.RLottieImageView r9 = r0.blueImageView
                r10 = 1103101952(0x41CLASSNAME, float:24.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r10 = -r10
                float r10 = (float) r10
                r9.setTranslationY(r10)
            L_0x0389:
                org.telegram.ui.Components.RLottieImageView r9 = r0.blueImageView
                float r10 = (float) r6
                r24 = 51
                r25 = 0
                r26 = 0
                r27 = 0
                int r13 = r0.currentType
                if (r13 != r4) goto L_0x03a9
                boolean r13 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r13 != 0) goto L_0x03a9
                r13 = 1098907648(0x41800000, float:16.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r13 = -r13
                float r13 = (float) r13
                r28 = r13
                goto L_0x03ac
            L_0x03a9:
                r13 = 0
                r28 = 0
            L_0x03ac:
                r22 = r6
                r23 = r10
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
                r8.addView(r9, r10)
                android.widget.TextView r9 = r0.titleTextView
                if (r5 == 0) goto L_0x03bd
                r10 = r5
                goto L_0x03cc
            L_0x03bd:
                int r10 = r0.currentType
                if (r10 != r4) goto L_0x03c5
                r10 = 2131628010(0x7f0e0fea, float:1.88833E38)
                goto L_0x03c8
            L_0x03c5:
                r10 = 2131628015(0x7f0e0fef, float:1.888331E38)
            L_0x03c8:
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
            L_0x03cc:
                r9.setText(r10)
                android.widget.TextView r9 = r0.titleTextView
                r22 = -2
                r23 = -2
                r24 = 49
                r25 = 0
                r26 = 18
                r27 = 0
                r28 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r0.addView(r9, r10)
                android.widget.TextView r9 = r0.confirmTextView
                r26 = 17
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r0.addView(r9, r10)
            L_0x03f1:
                int r6 = r0.currentType
                r8 = 11
                if (r6 == r8) goto L_0x0413
                org.telegram.ui.LoginActivity$LoginActivitySmsView$2 r6 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$2
                r6.<init>(r2, r1)
                r0.codeFieldContainer = r6
                r22 = -2
                r23 = 42
                r24 = 1
                r25 = 0
                r26 = 32
                r27 = 0
                r28 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r0.addView(r6, r8)
            L_0x0413:
                int r6 = r0.currentType
                r8 = 3
                if (r6 != r8) goto L_0x041f
                org.telegram.ui.CodeFieldContainer r6 = r0.codeFieldContainer
                r8 = 8
                r6.setVisibility(r8)
            L_0x041f:
                android.widget.FrameLayout r6 = new android.widget.FrameLayout
                r6.<init>(r2)
                r0.problemFrame = r6
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r0.timeText = r6
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r6.setLineSpacing(r8, r9)
                android.widget.TextView r6 = r0.timeText
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r9 = 1092616192(0x41200000, float:10.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r6.setPadding(r11, r8, r11, r9)
                android.widget.TextView r6 = r0.timeText
                r8 = 1097859072(0x41700000, float:15.0)
                r6.setTextSize(r4, r8)
                android.widget.TextView r6 = r0.timeText
                r9 = 49
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.timeText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30 r10 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30
                r10.<init>(r0)
                r6.setOnClickListener(r10)
                android.widget.FrameLayout r6 = r0.problemFrame
                android.widget.TextView r10 = r0.timeText
                r13 = -2
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r9)
                r6.addView(r10, r14)
                org.telegram.ui.LoginActivity$LoginActivitySmsView$3 r6 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$3
                r6.<init>(r2, r1)
                r0.errorViewSwitcher = r6
                r6 = 2130771992(0x7var_, float:1.714709E38)
                android.view.animation.Animation r6 = android.view.animation.AnimationUtils.loadAnimation(r2, r6)
                android.view.animation.Interpolator r9 = org.telegram.ui.Components.Easings.easeInOutQuad
                r6.setInterpolator(r9)
                android.widget.ViewSwitcher r9 = r0.errorViewSwitcher
                r9.setInAnimation(r6)
                r9 = 2130771993(0x7var_, float:1.7147092E38)
                android.view.animation.Animation r6 = android.view.animation.AnimationUtils.loadAnimation(r2, r9)
                android.view.animation.Interpolator r9 = org.telegram.ui.Components.Easings.easeInOutQuad
                r6.setInterpolator(r9)
                android.widget.ViewSwitcher r9 = r0.errorViewSwitcher
                r9.setOutAnimation(r6)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                r0.problemText = r9
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r13 = 1065353216(0x3var_, float:1.0)
                r9.setLineSpacing(r10, r13)
                android.widget.TextView r9 = r0.problemText
                r9.setTextSize(r4, r8)
                android.widget.TextView r9 = r0.problemText
                r10 = 49
                r9.setGravity(r10)
                android.widget.TextView r9 = r0.problemText
                r10 = 1082130432(0x40800000, float:4.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.setPadding(r11, r13, r11, r14)
                android.widget.FrameLayout r9 = r0.problemFrame
                android.widget.TextView r13 = r0.problemText
                r14 = 17
                r15 = -2
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r15, (int) r14)
                r9.addView(r13, r14)
                android.widget.ViewSwitcher r9 = r0.errorViewSwitcher
                android.widget.FrameLayout r13 = r0.problemFrame
                r9.addView(r13)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                r0.wrongCode = r9
                r13 = 2131629018(0x7f0e13da, float:1.8885345E38)
                java.lang.String r14 = "WrongCode"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r9.setText(r13)
                android.widget.TextView r9 = r0.wrongCode
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                r13 = 1065353216(0x3var_, float:1.0)
                r9.setLineSpacing(r7, r13)
                android.widget.TextView r7 = r0.wrongCode
                r7.setTextSize(r4, r8)
                android.widget.TextView r7 = r0.wrongCode
                r8 = 49
                r7.setGravity(r8)
                android.widget.TextView r7 = r0.wrongCode
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r7.setPadding(r11, r8, r11, r9)
                android.widget.ViewSwitcher r7 = r0.errorViewSwitcher
                android.widget.TextView r8 = r0.wrongCode
                r7.addView(r8)
                int r7 = r0.currentType
                if (r7 != r4) goto L_0x0542
                int r4 = r0.nextType
                r7 = 3
                if (r4 == r7) goto L_0x0533
                r7 = 4
                if (r4 == r7) goto L_0x0533
                r7 = 11
                if (r4 != r7) goto L_0x0524
                goto L_0x0533
            L_0x0524:
                android.widget.TextView r4 = r0.problemText
                r7 = 2131625380(0x7f0e05a4, float:1.8877966E38)
                java.lang.String r8 = "DidNotGetTheCodeSms"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r4.setText(r7)
                goto L_0x0550
            L_0x0533:
                android.widget.TextView r4 = r0.problemText
                r7 = 2131625379(0x7f0e05a3, float:1.8877964E38)
                java.lang.String r8 = "DidNotGetTheCodePhone"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r4.setText(r7)
                goto L_0x0550
            L_0x0542:
                android.widget.TextView r4 = r0.problemText
                r7 = 2131625375(0x7f0e059f, float:1.8877956E38)
                java.lang.String r8 = "DidNotGetTheCode"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
                r4.setText(r7)
            L_0x0550:
                if (r12 != 0) goto L_0x057c
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.bottomContainer = r4
                android.widget.ViewSwitcher r7 = r0.errorViewSwitcher
                r13 = -2
                r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r15 = 81
                r16 = 0
                r17 = 0
                r18 = 0
                r19 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r4.addView(r7, r8)
                android.widget.FrameLayout r4 = r0.bottomContainer
                r7 = -1
                r8 = 1065353216(0x3var_, float:1.0)
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r11, (float) r8)
                r0.addView(r4, r7)
                goto L_0x0592
            L_0x057c:
                android.widget.ViewSwitcher r4 = r0.errorViewSwitcher
                r13 = -2
                r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r15 = 81
                r16 = 0
                r17 = 0
                r18 = 0
                r19 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r12.addView(r4, r7)
            L_0x0592:
                android.widget.ViewSwitcher r4 = r0.errorViewSwitcher
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
                android.widget.TextView r4 = r0.problemText
                org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda31 r7 = new org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda31
                r7.<init>(r0, r2)
                r4.setOnClickListener(r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2528lambda$new$3$orgtelegramuiLoginActivity$LoginActivitySmsView(View v) {
            int i = this.nextType;
            if (i == 4 || i == 2 || i == 11) {
                this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                int i2 = this.nextType;
                if (i2 == 4 || i2 == 11) {
                    this.timeText.setText(LocaleController.getString("Calling", NUM));
                } else {
                    this.timeText.setText(LocaleController.getString("SendingSms", NUM));
                }
                createCodeTimer();
                TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23(this), 10);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                this.waitingForEvent = false;
                destroyCodeTimer();
                resendCode();
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2527lambda$new$2$orgtelegramuiLoginActivity$LoginActivitySmsView(TLObject response, TLRPC.TL_error error) {
            if (error != null && error.text != null) {
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17(this, error));
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2526lambda$new$1$orgtelegramuiLoginActivity$LoginActivitySmsView(TLRPC.TL_error error) {
            this.lastError = error.text;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2531lambda$new$6$orgtelegramuiLoginActivity$LoginActivitySmsView(Context context, View v) {
            if (!this.nextPressed) {
                int i = this.nextType;
                if ((i == 4 && this.currentType == 2) || i == 0) {
                    new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DidNotGetTheCodeInfo", NUM, this.phone))).setNeutralButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda0(this)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda11(this)).show();
                } else if (this.this$0.radialProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2529lambda$new$4$orgtelegramuiLoginActivity$LoginActivitySmsView(DialogInterface dialog, int which) {
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
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("NoMailInstalled", NUM));
            }
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2530lambda$new$5$orgtelegramuiLoginActivity$LoginActivitySmsView(DialogInterface dialog, int which) {
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        public void updateColors() {
            this.confirmTextView.setTextColor(Theme.getColor(this.this$0.cancelAccountDeletionMode ? "windowBackgroundWhiteBlackText" : "windowBackgroundWhiteGrayText6"));
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
            String timeTextColorTag = (String) this.timeText.getTag();
            if (timeTextColorTag == null) {
                timeTextColorTag = "windowBackgroundWhiteGrayText6";
            }
            this.timeText.setTextColor(Theme.getColor(timeTextColorTag));
            this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.wrongCode.setTextColor(Theme.getColor("dialogTextRed"));
        }

        private void applyLottieColors(RLottieDrawable drawable) {
            if (drawable != null) {
                drawable.setLayerColor("Bubble.**", Theme.getColor("chats_actionBackground"));
                drawable.setLayerColor("Phone.**", Theme.getColor("windowBackgroundWhiteBlackText"));
                drawable.setLayerColor("Note.**", Theme.getColor("windowBackgroundWhiteBlackText"));
            }
        }

        public boolean hasCustomKeyboard() {
            return this.currentType != 3;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            tryShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda24(this, params), 10));
        }

        /* renamed from: lambda$resendCode$8$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2546xd16CLASSNAME(Bundle params, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda18(this, error, params, response));
        }

        /* renamed from: lambda$resendCode$7$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2545xdf3e25d4(TLRPC.TL_error error, Bundle params, TLObject response) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    this.this$0.setPage(0, true, (Bundle) null, true);
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    LoginActivity loginActivity = this.this$0;
                    String string = LocaleController.getString(NUM);
                    loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                }
            }
            tryHideProgress(false);
        }

        private void tryShowProgress(int reqId) {
            m2557x4ffCLASSNAMEf(reqId, true);
        }

        /* access modifiers changed from: private */
        /* renamed from: tryShowProgress */
        public void m2557x4ffCLASSNAMEf(int reqId, boolean animate) {
            if (this.starsToDotsDrawable == null) {
                this.this$0.needShowProgress(reqId, animate);
            } else if (!this.isDotsAnimationVisible) {
                this.isDotsAnimationVisible = true;
                if (this.hintDrawable.getCurrentFrame() != this.hintDrawable.getFramesCount() - 1) {
                    this.hintDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10(this, reqId, animate));
                    return;
                }
                this.starsToDotsDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this));
                this.blueImageView.setAutoRepeat(false);
                this.starsToDotsDrawable.setCurrentFrame(0, false);
                this.blueImageView.setAnimation(this.starsToDotsDrawable);
                this.blueImageView.playAnimation();
            }
        }

        /* renamed from: lambda$tryShowProgress$10$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2554xa6490091(int reqId, boolean animate) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda12(this, reqId, animate));
        }

        /* renamed from: lambda$tryShowProgress$12$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2556x1fa354f() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7(this));
        }

        /* renamed from: lambda$tryShowProgress$11$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2555xd4219af0() {
            this.blueImageView.setAutoRepeat(true);
            this.dotsDrawable.setCurrentFrame(0, false);
            this.dotsDrawable.setAutoRepeat(1);
            this.blueImageView.setAnimation(this.dotsDrawable);
            this.blueImageView.playAnimation();
        }

        private void tryHideProgress(boolean cancel) {
            tryHideProgress(cancel, true);
        }

        private void tryHideProgress(boolean cancel, boolean animate) {
            if (this.starsToDotsDrawable == null) {
                this.this$0.needHideProgress(cancel, animate);
            } else if (this.isDotsAnimationVisible) {
                this.isDotsAnimationVisible = false;
                this.blueImageView.setAutoRepeat(false);
                this.dotsDrawable.setAutoRepeat(0);
                this.dotsDrawable.setOnFinishCallback(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(this), this.dotsDrawable.getFramesCount() - 1);
            }
        }

        /* renamed from: lambda$tryHideProgress$16$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2553xe8ceCLASSNAME() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(this));
        }

        /* renamed from: lambda$tryHideProgress$14$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2551x8d1d9192() {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(this));
        }

        /* renamed from: lambda$tryHideProgress$15$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2552xbavar_bf1() {
            this.dotsToStarsDrawable.setOnAnimationEndListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this));
            this.blueImageView.setAutoRepeat(false);
            this.dotsToStarsDrawable.setCurrentFrame(0, false);
            this.blueImageView.setAnimation(this.dotsToStarsDrawable);
            this.blueImageView.playAnimation();
        }

        /* renamed from: lambda$tryHideProgress$13$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2550x5var_var_() {
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

        public boolean needBackButton() {
            return true;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
                boolean z = true;
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
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        f.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || this.this$0.isCustomKeyboardForceDisabled());
                    }
                    f.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if (LoginActivitySmsView.this.postedErrorColorTimeout) {
                                LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                                loginActivitySmsView.removeCallbacks(loginActivitySmsView.errorColorTimeout);
                                LoginActivitySmsView.this.errorColorTimeout.run();
                            }
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                        }
                    });
                    f.setOnFocusChangeListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32(this));
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    if (this.this$0.cancelAccountDeletionMode) {
                        SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo2", NUM, PhoneFormat.getInstance().format("+" + number))));
                        int startIndex = TextUtils.indexOf(spanned, '*');
                        int lastIndex = TextUtils.lastIndexOf(spanned, '*');
                        if (!(startIndex == -1 || lastIndex == -1 || startIndex == lastIndex)) {
                            this.confirmTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                            spanned.replace(lastIndex, lastIndex + 1, "");
                            spanned.replace(startIndex, startIndex + 1, "");
                            spanned.setSpan(new URLSpanNoUnderline("tg://settings/change_number"), startIndex, lastIndex - 1, 33);
                        }
                        str = spanned;
                    } else {
                        int i5 = this.currentType;
                        if (i5 == 1) {
                            str = AndroidUtilities.replaceTags(LocaleController.formatString("SentAppCodeWithPhone", NUM, LocaleController.addNbsp(number)));
                        } else if (i5 == 2) {
                            str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(number)));
                        } else if (i5 == 3) {
                            str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(number)));
                        } else if (i5 == 4) {
                            str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(number)));
                        }
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        boolean unused = this.this$0.showKeyboard(this.codeFieldContainer.codeField[0]);
                        this.codeFieldContainer.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeFieldContainer.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    int i6 = this.currentType;
                    if (i6 == 1) {
                        setProblemTextVisible(true);
                        this.timeText.setVisibility(8);
                    } else if (i6 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        setProblemTextVisible(false);
                        this.timeText.setVisibility(0);
                        int i7 = this.nextType;
                        if (i7 == 4 || i7 == 11) {
                            this.timeText.setText(LocaleController.formatString("CallAvailableIn", NUM, 1, 0));
                        } else if (i7 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsAvailableIn", NUM, 1, 0));
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
                        this.timeText.setText(LocaleController.formatString("CallAvailableIn", NUM, 2, 0));
                        setProblemTextVisible(this.time < 1000);
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
                        this.timeText.setText(LocaleController.formatString("SmsAvailableIn", NUM, 2, 0));
                        if (this.time >= 1000) {
                            z = false;
                        }
                        setProblemTextVisible(z);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        setProblemTextVisible(false);
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

        /* renamed from: lambda$setParams$17$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2547x468cbe0d(View v, boolean hasFocus) {
            if (hasFocus) {
                this.this$0.keyboardView.setEditText((EditText) v);
                this.this$0.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* access modifiers changed from: private */
        public void setProblemTextVisible(boolean visible) {
            float newAlpha = visible ? 1.0f : 0.0f;
            if (this.problemText.getAlpha() != newAlpha) {
                this.problemText.animate().cancel();
                this.problemText.animate().alpha(newAlpha).setDuration(150).start();
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

                    /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m2558xvar_b283() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$7700 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView.access$7826(LoginActivitySmsView.this, currentTime - access$7700);
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

                    /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$6  reason: not valid java name */
                    public /* synthetic */ void m2559xvar_b284() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$8300 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCurrentTime = currentTime;
                        LoginActivitySmsView.access$8426(LoginActivitySmsView.this, currentTime - access$8300);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int minutes = (LoginActivitySmsView.this.time / 1000) / 60;
                            int seconds = (LoginActivitySmsView.this.time / 1000) - (minutes * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 11) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallAvailableIn", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsAvailableIn", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                            }
                            if (LoginActivitySmsView.this.progressView != null && !LoginActivitySmsView.this.progressView.isProgressAnimationRunning()) {
                                LoginActivitySmsView.this.progressView.startProgressAnimation(((long) LoginActivitySmsView.this.time) - 1000);
                                return;
                            }
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
            int i = 0;
            if (TextUtils.isEmpty(code)) {
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
                if (this.this$0.cancelAccountDeletionMode) {
                    this.requestPhone = this.this$0.cancelDeletionPhone;
                    TLRPC.TL_account_confirmPhone req = new TLRPC.TL_account_confirmPhone();
                    req.phone_code = code;
                    req.phone_code_hash = this.phoneHash;
                    destroyTimer();
                    this.codeFieldContainer.isFocusSuppressed = true;
                    CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                    int length2 = codeNumberFieldArr.length;
                    while (i < length2) {
                        codeNumberFieldArr[i].animateFocusedProgress(0.0f);
                        i++;
                    }
                    tryShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda25(this, req), 2));
                    return;
                }
                TLRPC.TL_auth_signIn req2 = new TLRPC.TL_auth_signIn();
                req2.phone_number = this.requestPhone;
                req2.phone_code = code;
                req2.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.codeFieldContainer.isFocusSuppressed = true;
                CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                int length3 = codeNumberFieldArr2.length;
                while (i < length3) {
                    codeNumberFieldArr2[i].animateFocusedProgress(0.0f);
                    i++;
                }
                m2557x4ffCLASSNAMEf(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req2, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda27(this, req2), 10), true);
                this.this$0.showDoneButton(true, true);
            }
        }

        /* renamed from: lambda$onNextPressed$21$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2536x619ab9c(TLRPC.TL_account_confirmPhone req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21(this, error, req));
        }

        /* renamed from: lambda$onNextPressed$20$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2535xd841113d(TLRPC.TL_error error, TLRPC.TL_account_confirmPhone req) {
            int i;
            int i2;
            tryHideProgress(false);
            this.nextPressed = false;
            if (error == null) {
                animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34(this));
                return;
            }
            this.lastError = error.text;
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
                AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
            }
            if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                shakeWrongCode();
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                this.this$0.setPage(0, true, (Bundle) null, true);
            }
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2534xe7a3cd13() {
            AlertDialog.Builder title = new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString(NUM));
            PhoneFormat instance = PhoneFormat.getInstance();
            title.setMessage(LocaleController.formatString("CancelLinkSuccess", NUM, instance.format("+" + this.phone))).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda29(this)).show();
        }

        /* renamed from: lambda$onNextPressed$18$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2533xb9cb32b4(DialogInterface dialog) {
            this.this$0.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$28$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2543x4705e435(TLRPC.TL_auth_signIn req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda20(this, error, response, req));
        }

        /* renamed from: lambda$onNextPressed$27$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2542x192d49d6(TLRPC.TL_error error, TLObject response, TLRPC.TL_auth_signIn req) {
            int i;
            int i2;
            tryHideProgress(false, true);
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
                    animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda13(this, params));
                } else {
                    animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda16(this, response));
                }
            } else {
                this.lastError = error.text;
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ok = true;
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda26(this, req), 10);
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
                        boolean isWrongCode = false;
                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                            shakeWrongCode();
                            isWrongCode = true;
                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null, true);
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("FloodWait", NUM));
                        } else {
                            this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                        }
                        if (!isWrongCode) {
                            for (CodeNumberField text : this.codeFieldContainer.codeField) {
                                text.setText("");
                            }
                            this.codeFieldContainer.isFocusSuppressed = false;
                            this.codeFieldContainer.codeField[0].requestFocus();
                        }
                    }
                }
            }
            if (ok && this.currentType == 3) {
                AndroidUtilities.endIncomingCall();
            }
        }

        /* renamed from: lambda$onNextPressed$22$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2537x33var_fb(Bundle params) {
            this.this$0.setPage(5, true, params, false);
        }

        /* renamed from: lambda$onNextPressed$23$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2538x61cae05a(TLObject response) {
            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        /* renamed from: lambda$onNextPressed$26$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2541xeb54avar_(TLRPC.TL_auth_signIn req, TLObject response1, TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda19(this, error1, response1, req));
        }

        /* renamed from: lambda$onNextPressed$25$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2540xbd7CLASSNAME(TLRPC.TL_error error1, TLObject response1, TLRPC.TL_auth_signIn req) {
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
                animateSuccess(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda14(this, bundle));
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(NUM), error1.text);
        }

        /* renamed from: lambda$onNextPressed$24$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2539x8fa37ab9(Bundle bundle) {
            this.this$0.setPage(6, true, bundle, false);
        }

        private void animateSuccess(Runnable callback) {
            for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
                this.codeFieldContainer.postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(this, i), ((long) i) * 75);
            }
            this.codeFieldContainer.postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda15(this, callback), (((long) this.codeFieldContainer.codeField.length) * 75) + 400);
        }

        /* renamed from: lambda$animateSuccess$29$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2523x806911c0(int finalI) {
            this.codeFieldContainer.codeField[finalI].animateSuccessProgress(1.0f);
        }

        /* renamed from: lambda$animateSuccess$30$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2524x710655ea(Runnable callback) {
            for (CodeNumberField animateSuccessProgress : this.codeFieldContainer.codeField) {
                animateSuccessProgress.animateSuccessProgress(0.0f);
            }
            callback.run();
            this.codeFieldContainer.isFocusSuppressed = false;
        }

        private void shakeWrongCode() {
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                this.codeFieldContainer.codeField[a].setText("");
                this.codeFieldContainer.codeField[a].animateErrorProgress(1.0f);
            }
            if (this.errorViewSwitcher.getCurrentView() == this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, this.currentType == 11 ? 3.5f : 10.0f, new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this));
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 5000);
            this.postedErrorColorTimeout = true;
        }

        /* renamed from: lambda$shakeWrongCode$32$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2549xa551141a() {
            postDelayed(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1(this), 150);
        }

        /* renamed from: lambda$shakeWrongCode$31$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2548x777879bb() {
            this.codeFieldContainer.isFocusSuppressed = false;
            this.codeFieldContainer.codeField[0].requestFocus();
            for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                animateErrorProgress.animateErrorProgress(0.0f);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        public boolean onBackPressed(boolean force) {
            if (this.this$0.cancelAccountDeletionMode) {
                this.this$0.finishFragment();
                return false;
            } else if (!force) {
                this.this$0.showDialog(new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditNumberInfo", NUM, this.phone))).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda22(this)).create());
                return false;
            } else {
                this.nextPressed = false;
                tryHideProgress(true);
                TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda28.INSTANCE, 10);
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

        /* renamed from: lambda$onBackPressed$33$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2532x1var_CLASSNAME(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        static /* synthetic */ void lambda$onBackPressed$34(TLObject response, TLRPC.TL_error error) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda35(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$35$org-telegram-ui-LoginActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m2544xea05eedf() {
            if (this.currentType != 3 && this.codeFieldContainer.codeField != null) {
                int a = this.codeFieldContainer.codeField.length - 1;
                while (true) {
                    if (a < 0) {
                        break;
                    } else if (a == 0 || this.codeFieldContainer.codeField[a].length() != 0) {
                        this.codeFieldContainer.codeField[a].requestFocus();
                        this.codeFieldContainer.codeField[a].setSelection(this.codeFieldContainer.codeField[a].length());
                        boolean unused = this.this$0.showKeyboard(this.codeFieldContainer.codeField[a]);
                    } else {
                        a--;
                    }
                }
                this.codeFieldContainer.codeField[a].requestFocus();
                this.codeFieldContainer.codeField[a].setSelection(this.codeFieldContainer.codeField[a].length());
                boolean unused2 = this.this$0.showKeyboard(this.codeFieldContainer.codeField[a]);
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
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public TLRPC.TL_account_password currentPassword;
        private RLottieImageView lockImageView;
        private boolean nextPressed;
        private OutlineTextContainerView outlineCodeField;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private TextView titleView;

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
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r1)
                org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
                r5.<init>(r1)
                r0.lockImageView = r5
                r6 = 2131558540(0x7f0d008c, float:1.8742399E38)
                r7 = 120(0x78, float:1.68E-43)
                r5.setAnimation(r6, r7, r7)
                org.telegram.ui.Components.RLottieImageView r5 = r0.lockImageView
                r6 = 0
                r5.setAutoRepeat(r6)
                org.telegram.ui.Components.RLottieImageView r5 = r0.lockImageView
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r3)
                r4.addView(r5, r7)
                boolean r5 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r5 != 0) goto L_0x004b
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r5.x
                android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r7.y
                if (r5 <= r7) goto L_0x0049
                boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r5 != 0) goto L_0x0049
                goto L_0x004b
            L_0x0049:
                r5 = 0
                goto L_0x004d
            L_0x004b:
                r5 = 8
            L_0x004d:
                r4.setVisibility(r5)
                r5 = -1
                r7 = -2
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r7, (int) r3)
                r0.addView(r4, r8)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r1)
                r0.titleView = r8
                r9 = 1099956224(0x41900000, float:18.0)
                r8.setTextSize(r3, r9)
                android.widget.TextView r8 = r0.titleView
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r8.setTypeface(r10)
                android.widget.TextView r8 = r0.titleView
                r10 = 2131629061(0x7f0e1405, float:1.8885432E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
                r8.setText(r10)
                android.widget.TextView r8 = r0.titleView
                r10 = 17
                r8.setGravity(r10)
                android.widget.TextView r8 = r0.titleView
                r10 = 1073741824(0x40000000, float:2.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r11
                r12 = 1065353216(0x3var_, float:1.0)
                r8.setLineSpacing(r11, r12)
                android.widget.TextView r8 = r0.titleView
                r13 = -1
                r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r15 = 1
                r16 = 1107296256(0x42000000, float:32.0)
                r17 = 1098907648(0x41800000, float:16.0)
                r18 = 1107296256(0x42000000, float:32.0)
                r19 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r0.addView(r8, r11)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r1)
                r0.confirmTextView = r8
                r11 = 1096810496(0x41600000, float:14.0)
                r8.setTextSize(r3, r11)
                android.widget.TextView r8 = r0.confirmTextView
                r8.setGravity(r3)
                android.widget.TextView r8 = r0.confirmTextView
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r11
                r8.setLineSpacing(r11, r12)
                android.widget.TextView r8 = r0.confirmTextView
                r11 = 2131626353(0x7f0e0971, float:1.887994E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r11)
                r8.setText(r11)
                android.widget.TextView r8 = r0.confirmTextView
                r13 = -2
                r14 = -2
                r16 = 12
                r17 = 8
                r18 = 12
                r19 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r8, r11)
                org.telegram.ui.Components.OutlineTextContainerView r8 = new org.telegram.ui.Components.OutlineTextContainerView
                r8.<init>(r1)
                r0.outlineCodeField = r8
                r11 = 2131625555(0x7f0e0653, float:1.8878321E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r11)
                r8.setText(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = new org.telegram.ui.Components.EditTextBoldCursor
                r8.<init>(r1)
                r0.codeField = r8
                r11 = 1101004800(0x41a00000, float:20.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r8.setCursorSize(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.codeField
                r11 = 1069547520(0x3fCLASSNAME, float:1.5)
                r8.setCursorWidth(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.codeField
                r11 = 0
                r8.setBackground(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.codeField
                r11 = 268435461(0x10000005, float:2.5243564E-29)
                r8.setImeOptions(r11)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.codeField
                r8.setTextSize(r3, r9)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.codeField
                r8.setMaxLines(r3)
                r8 = 1098907648(0x41800000, float:16.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                r11.setPadding(r9, r9, r9, r9)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                r13 = 129(0x81, float:1.81E-43)
                r11.setInputType(r13)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                android.text.method.PasswordTransformationMethod r13 = android.text.method.PasswordTransformationMethod.getInstance()
                r11.setTransformationMethod(r13)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                android.graphics.Typeface r13 = android.graphics.Typeface.DEFAULT
                r11.setTypeface(r13)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x014a
                r13 = 5
                goto L_0x014b
            L_0x014a:
                r13 = 3
            L_0x014b:
                r11.setGravity(r13)
                org.telegram.ui.Components.EditTextBoldCursor r11 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7 r13 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7
                r13.<init>(r0)
                r11.setOnFocusChangeListener(r13)
                org.telegram.ui.Components.OutlineTextContainerView r11 = r0.outlineCodeField
                org.telegram.ui.Components.EditTextBoldCursor r13 = r0.codeField
                r11.attachEditText(r13)
                org.telegram.ui.Components.OutlineTextContainerView r11 = r0.outlineCodeField
                org.telegram.ui.Components.EditTextBoldCursor r13 = r0.codeField
                r14 = 48
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r7, (int) r14)
                r11.addView(r13, r7)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r0.codeField
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8 r11 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8
                r11.<init>(r0)
                r7.setOnEditorActionListener(r11)
                org.telegram.ui.Components.OutlineTextContainerView r7 = r0.outlineCodeField
                r13 = -1
                r14 = -2
                r15 = 1
                r16 = 16
                r17 = 32
                r18 = 16
                r19 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r7, r11)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.cancelButton = r7
                r11 = 19
                r7.setGravity(r11)
                android.widget.TextView r7 = r0.cancelButton
                r11 = 2131625838(0x7f0e076e, float:1.8878895E38)
                java.lang.String r13 = "ForgotPassword"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
                r7.setText(r11)
                android.widget.TextView r7 = r0.cancelButton
                r11 = 1097859072(0x41700000, float:15.0)
                r7.setTextSize(r3, r11)
                android.widget.TextView r3 = r0.cancelButton
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r7 = (float) r7
                r3.setLineSpacing(r7, r12)
                android.widget.TextView r3 = r0.cancelButton
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r3.setPadding(r7, r6, r8, r6)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r1)
                android.widget.TextView r6 = r0.cancelButton
                r10 = -1
                int r7 = android.os.Build.VERSION.SDK_INT
                r8 = 21
                if (r7 < r8) goto L_0x01d3
                r7 = 56
                goto L_0x01d5
            L_0x01d3:
                r7 = 60
            L_0x01d5:
                float r11 = (float) r7
                r12 = 80
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r3.addView(r6, r7)
                r6 = 80
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r5, (int) r6)
                r0.addView(r3, r5)
                android.widget.TextView r5 = r0.cancelButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
                android.widget.TextView r5 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6
                r6.<init>(r0, r1)
                r5.setOnClickListener(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2472x81c2d861(View v, boolean hasFocus) {
            this.outlineCodeField.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ boolean m2473xa756e162(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2478x633b0e67(Context context, View view) {
            if (this.this$0.radialProgressView.getTag() == null) {
                if (this.currentPassword.has_recovery) {
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2(this), 10);
                    return;
                }
                AndroidUtilities.hideKeyboard(this.codeField);
                new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0(this)).show();
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2476x1812fCLASSNAME(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12(this, error, response));
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2475xvar_evar_(TLRPC.TL_error error, TLObject response) {
            String timeString;
            this.this$0.needHideProgress(false);
            if (error == null) {
                TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                String rawPattern = res.email_pattern;
                SpannableStringBuilder emailPattern = SpannableStringBuilder.valueOf(rawPattern);
                int startIndex = rawPattern.indexOf(42);
                int endIndex = rawPattern.lastIndexOf(42);
                if (!(startIndex == endIndex || startIndex == -1 || endIndex == -1)) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    run.flags |= 256;
                    run.start = startIndex;
                    run.end = endIndex + 1;
                    emailPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                }
                builder.setMessage(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), emailPattern));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
                builder.setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5(this, res));
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
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2474xcceaea63(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
            bundle.putString("password", this.passwordString);
            bundle.putString("requestPhone", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("phoneCode", this.phoneCode);
            this.this$0.setPage(7, true, bundle, false);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2477x3da70566(DialogInterface dialog, int which) {
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

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                if (params.isEmpty()) {
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
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
                    this.codeField.setHint((CharSequence) null);
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
                this.this$0.onFieldError(this.outlineCodeField, true);
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
                Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10(this, oldPassword));
            }
        }

        /* renamed from: lambda$onNextPressed$12$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2481xCLASSNAMEd084(String oldPassword) {
            byte[] passwordBytes;
            TLRPC.PasswordKdfAlgo current_algo = this.currentPassword.current_algo;
            if (current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                passwordBytes = SRPHelper.getX(AndroidUtilities.getStringBytes(oldPassword), (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo);
            } else {
                passwordBytes = null;
            }
            TLRPC.TL_auth_checkPassword req = new TLRPC.TL_auth_checkPassword();
            RequestDelegate requestDelegate = new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3(this);
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

        /* renamed from: lambda$onNextPressed$11$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2480xa1e3CLASSNAME(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13(this, error, response));
        }

        /* renamed from: lambda$onNextPressed$10$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2479x7c4fbe82(TLRPC.TL_error error, TLObject response) {
            String timeString;
            this.nextPressed = false;
            if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4(this), 8);
            } else if (response instanceof TLRPC.TL_auth_authorization) {
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11(this, response), 150);
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
                    this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                } else {
                    this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$8$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2483xc3ea9559(TLObject response2, TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1(this, error2, response2));
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2482x9e568CLASSNAME(TLRPC.TL_error error2, TLObject response2) {
            if (error2 == null) {
                this.currentPassword = (TLRPC.TL_account_password) response2;
                onNextPressed((String) null);
            }
        }

        /* renamed from: lambda$onNextPressed$9$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2484xe97e9e5a(TLObject response) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$13$org-telegram-ui-LoginActivity$LoginActivityPasswordView  reason: not valid java name */
        public /* synthetic */ void m2485x4068ecff() {
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityResetWaitView(org.telegram.ui.LoginActivity r25, android.content.Context r26) {
            /*
                r24 = this;
                r0 = r24
                r1 = r26
                r2 = r25
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 1
                r0.setOrientation(r3)
                android.widget.LinearLayout r4 = new android.widget.LinearLayout
                r4.<init>(r1)
                r4.setOrientation(r3)
                r5 = 17
                r4.setGravity(r5)
                android.widget.FrameLayout r6 = new android.widget.FrameLayout
                r6.<init>(r1)
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r1)
                r0.waitImageView = r7
                r7.setAutoRepeat(r3)
                org.telegram.ui.Components.RLottieImageView r7 = r0.waitImageView
                r8 = 2131558511(0x7f0d006f, float:1.874234E38)
                r9 = 120(0x78, float:1.68E-43)
                r7.setAnimation(r8, r9, r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.waitImageView
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r3)
                r6.addView(r7, r8)
                android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r7.x
                android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
                int r8 = r8.y
                r9 = 0
                if (r7 <= r8) goto L_0x0052
                boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r7 != 0) goto L_0x0052
                r7 = 8
                goto L_0x0053
            L_0x0052:
                r7 = 0
            L_0x0053:
                r6.setVisibility(r7)
                r7 = -2
                r8 = -1
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r7, (int) r3)
                r4.addView(r6, r7)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.titleView = r7
                r10 = 1099956224(0x41900000, float:18.0)
                r7.setTextSize(r3, r10)
                android.widget.TextView r7 = r0.titleView
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r7.setTypeface(r11)
                android.widget.TextView r7 = r0.titleView
                r11 = 2131627786(0x7f0e0f0a, float:1.8882846E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r11)
                r7.setText(r12)
                android.widget.TextView r7 = r0.titleView
                r7.setGravity(r5)
                android.widget.TextView r7 = r0.titleView
                r12 = 1073741824(0x40000000, float:2.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r13 = (float) r13
                r14 = 1065353216(0x3var_, float:1.0)
                r7.setLineSpacing(r13, r14)
                android.widget.TextView r7 = r0.titleView
                r15 = -1
                r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r17 = 1
                r18 = 1107296256(0x42000000, float:32.0)
                r19 = 1098907648(0x41800000, float:16.0)
                r20 = 1107296256(0x42000000, float:32.0)
                r21 = 0
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r4.addView(r7, r13)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.confirmTextView = r7
                r13 = 1096810496(0x41600000, float:14.0)
                r7.setTextSize(r3, r13)
                android.widget.TextView r7 = r0.confirmTextView
                r7.setGravity(r3)
                android.widget.TextView r7 = r0.confirmTextView
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r15 = (float) r15
                r7.setLineSpacing(r15, r14)
                android.widget.TextView r7 = r0.confirmTextView
                r15 = -2
                r16 = -2
                r18 = 12
                r19 = 8
                r20 = 12
                r21 = 0
                android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
                r4.addView(r7, r15)
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (float) r14)
                r0.addView(r4, r7)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.resetAccountText = r7
                r7.setGravity(r3)
                android.widget.TextView r7 = r0.resetAccountText
                r15 = 2131627790(0x7f0e0f0e, float:1.8882854E38)
                java.lang.String r8 = "ResetAccountStatus"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r15)
                r7.setText(r8)
                android.widget.TextView r7 = r0.resetAccountText
                r7.setTextSize(r3, r13)
                android.widget.TextView r7 = r0.resetAccountText
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r8 = (float) r8
                r7.setLineSpacing(r8, r14)
                android.widget.TextView r7 = r0.resetAccountText
                r17 = -2
                r18 = -2
                r19 = 49
                r20 = 0
                r21 = 24
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r7, r8)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.resetAccountTime = r7
                r7.setGravity(r3)
                android.widget.TextView r7 = r0.resetAccountTime
                r8 = 1101004800(0x41a00000, float:20.0)
                r7.setTextSize(r3, r8)
                android.widget.TextView r7 = r0.resetAccountTime
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r7.setTypeface(r8)
                android.widget.TextView r7 = r0.resetAccountTime
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r8 = (float) r8
                r7.setLineSpacing(r8, r14)
                android.widget.TextView r7 = r0.resetAccountTime
                r19 = 1
                r21 = 8
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r7, r8)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.resetAccountButton = r7
                r7.setGravity(r5)
                android.widget.TextView r5 = r0.resetAccountButton
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r11)
                r5.setText(r7)
                android.widget.TextView r5 = r0.resetAccountButton
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r5.setTypeface(r7)
                android.widget.TextView r5 = r0.resetAccountButton
                r7 = 1097859072(0x41700000, float:15.0)
                r5.setTextSize(r3, r7)
                android.widget.TextView r3 = r0.resetAccountButton
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r5 = (float) r5
                r3.setLineSpacing(r5, r14)
                android.widget.TextView r3 = r0.resetAccountButton
                r5 = 1107820544(0x42080000, float:34.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3.setPadding(r7, r9, r5, r9)
                android.widget.TextView r3 = r0.resetAccountButton
                r5 = -1
                r3.setTextColor(r5)
                android.widget.TextView r3 = r0.resetAccountButton
                r7 = -1
                r8 = 50
                r9 = 1
                r10 = 16
                r11 = 32
                r12 = 16
                r13 = 48
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13)
                r0.addView(r3, r5)
                android.widget.TextView r3 = r0.resetAccountButton
                org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1
                r5.<init>(r0)
                r3.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityResetWaitView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m2522xa1ae125(View view) {
            if (this.this$0.radialProgressView.getTag() == null) {
                this.this$0.showDialog(new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString("ResetMyAccountWarning", NUM)).setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM)).setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).create());
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m2521x7d2dca06(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3(this), 10);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m2520xvar_b2e7(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2(this, error));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityResetWaitView  reason: not valid java name */
        public /* synthetic */ void m2519x63539bc8(TLRPC.TL_error error) {
            this.this$0.needHideProgress(false);
            if (error == null) {
                if (this.requestPhone == null || this.phoneHash == null || this.phoneCode == null) {
                    this.this$0.setPage(0, true, (Bundle) null, true);
                    return;
                }
                Bundle params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
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
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int days = timeLeft / 86400;
            int daysRounded = Math.round(((float) timeLeft) / 86400.0f);
            int hours = timeLeft / 3600;
            int minutes = (timeLeft / 60) % 60;
            int seconds = timeLeft % 60;
            if (days >= 2) {
                this.resetAccountTime.setText(LocaleController.formatPluralString("Days", daysRounded));
            } else {
                this.resetAccountTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}));
            }
            boolean isResetButtonActive = timeLeft == 0;
            Boolean bool = this.wasResetButtonActive;
            if (bool == null || bool.booleanValue() != isResetButtonActive) {
                if (!isResetButtonActive) {
                    this.waitImageView.setAutoRepeat(true);
                    if (!this.waitImageView.isPlaying()) {
                        this.waitImageView.playAnimation();
                    }
                } else {
                    this.waitImageView.getAnimatedDrawable().setAutoRepeat(0);
                }
                this.resetAccountTime.setVisibility(isResetButtonActive ? 4 : 0);
                this.resetAccountText.setVisibility(isResetButtonActive ? 4 : 0);
                TextView textView = this.resetAccountButton;
                if (!isResetButtonActive) {
                    i = 4;
                }
                textView.setVisibility(i);
                this.wasResetButtonActive = Boolean.valueOf(isResetButtonActive);
            }
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
        private CodeFieldContainer codeFieldContainer;
        private TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public Runnable errorColorTimeout = new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4(this);
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

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2486xcd144188() {
            this.postedErrorColorTimeout = false;
            for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                animateErrorProgress.animateErrorProgress(0.0f);
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRecoverView(org.telegram.ui.LoginActivity r20, android.content.Context r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                r2 = r21
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4 r3 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4
                r3.<init>(r0)
                r0.errorColorTimeout = r3
                r3 = 1
                r0.setOrientation(r3)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
                r5.<init>(r2)
                r0.inboxImageView = r5
                r6 = 2131558541(0x7f0d008d, float:1.87424E38)
                r7 = 120(0x78, float:1.68E-43)
                r5.setAnimation(r6, r7, r7)
                org.telegram.ui.Components.RLottieImageView r5 = r0.inboxImageView
                r6 = 0
                r5.setAutoRepeat(r6)
                org.telegram.ui.Components.RLottieImageView r5 = r0.inboxImageView
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r3)
                r4.addView(r5, r7)
                boolean r5 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r5 != 0) goto L_0x0052
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r5.x
                android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r7.y
                if (r5 <= r7) goto L_0x0050
                boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r5 != 0) goto L_0x0050
                goto L_0x0052
            L_0x0050:
                r5 = 0
                goto L_0x0054
            L_0x0052:
                r5 = 8
            L_0x0054:
                r4.setVisibility(r5)
                r5 = -2
                r7 = -1
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r3)
                r0.addView(r4, r5)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleView = r5
                r8 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r3, r8)
                android.widget.TextView r5 = r0.titleView
                java.lang.String r8 = "fonts/rmedium.ttf"
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
                r5.setTypeface(r8)
                android.widget.TextView r5 = r0.titleView
                r8 = 2131625549(0x7f0e064d, float:1.887831E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString((int) r8)
                r5.setText(r8)
                android.widget.TextView r5 = r0.titleView
                r8 = 17
                r5.setGravity(r8)
                android.widget.TextView r5 = r0.titleView
                r9 = 1073741824(0x40000000, float:2.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r10 = (float) r10
                r11 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r10, r11)
                android.widget.TextView r5 = r0.titleView
                r12 = -1
                r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r14 = 1
                r15 = 1107296256(0x42000000, float:32.0)
                r16 = 1098907648(0x41800000, float:16.0)
                r17 = 1107296256(0x42000000, float:32.0)
                r18 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r0.addView(r5, r10)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                r10 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r3, r10)
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r8)
                android.widget.TextView r5 = r0.confirmTextView
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r12 = (float) r12
                r5.setLineSpacing(r12, r11)
                android.widget.TextView r5 = r0.confirmTextView
                r12 = 2131627814(0x7f0e0var_, float:1.8882903E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r5.setText(r12)
                android.widget.TextView r5 = r0.confirmTextView
                r12 = -2
                r13 = -2
                r15 = 12
                r16 = 8
                r17 = 12
                r18 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r5, r12)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$1 r5 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$1
                r5.<init>(r2, r1)
                r0.codeFieldContainer = r5
                r12 = 6
                r5.setNumbersCount(r12, r3)
                org.telegram.ui.CodeFieldContainer r5 = r0.codeFieldContainer
                org.telegram.ui.CodeNumberField[] r5 = r5.codeField
                int r12 = r5.length
                r13 = 0
            L_0x00f8:
                if (r13 >= r12) goto L_0x0122
                r14 = r5[r13]
                boolean r15 = r19.hasCustomKeyboard()
                if (r15 == 0) goto L_0x010b
                boolean r15 = r20.isCustomKeyboardForceDisabled()
                if (r15 == 0) goto L_0x0109
                goto L_0x010b
            L_0x0109:
                r15 = 0
                goto L_0x010c
            L_0x010b:
                r15 = 1
            L_0x010c:
                r14.setShowSoftInputOnFocusCompat(r15)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$2 r15 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$2
                r15.<init>(r1)
                r14.addTextChangedListener(r15)
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3 r15 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3
                r15.<init>(r0)
                r14.setOnFocusChangeListener(r15)
                int r13 = r13 + 1
                goto L_0x00f8
            L_0x0122:
                org.telegram.ui.CodeFieldContainer r5 = r0.codeFieldContainer
                r12 = -2
                r13 = 42
                r14 = 1
                r15 = 0
                r16 = 32
                r17 = 0
                r18 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r5, r12)
                org.telegram.ui.Components.spoilers.SpoilersTextView r5 = new org.telegram.ui.Components.spoilers.SpoilersTextView
                r5.<init>(r2)
                r0.troubleButton = r5
                r5.setGravity(r8)
                android.widget.TextView r5 = r0.troubleButton
                r5.setTextSize(r3, r10)
                android.widget.TextView r3 = r0.troubleButton
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r5 = (float) r5
                r3.setLineSpacing(r5, r11)
                android.widget.TextView r3 = r0.troubleButton
                r5 = 1098907648(0x41800000, float:16.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r3.setPadding(r8, r9, r10, r5)
                android.widget.TextView r3 = r0.troubleButton
                r5 = 2
                r3.setMaxLines(r5)
                android.widget.TextView r3 = r0.troubleButton
                org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2
                r5.<init>(r0)
                r3.setOnClickListener(r5)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                android.widget.TextView r5 = r0.troubleButton
                r12 = -1
                r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r14 = 80
                r15 = 0
                r16 = 0
                r17 = 0
                r18 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r3.addView(r5, r8)
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r6, (float) r11)
                r0.addView(r3, r5)
                android.widget.TextView r5 = r0.troubleButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRecoverView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2487xce4a9467(View v, boolean hasFocus) {
            if (hasFocus) {
                this.this$0.keyboardView.setEditText((EditText) v);
                this.this$0.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2490xd1ed8d04(View view) {
            Dialog dialog = this.this$0.showDialog(new AlertDialog.Builder((Context) this.this$0.getParentActivity()).setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM)).setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM)).setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1(this)).create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2488xcvar_e746(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2489xd0b73a25(DialogInterface dialog, int which) {
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

        public boolean hasCustomKeyboard() {
            return true;
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
                this.codeFieldContainer.setText("");
                this.currentParams = params;
                this.passwordString = params.getString("password");
                this.requestPhone = this.currentParams.getString("requestPhone");
                this.phoneHash = this.currentParams.getString("phoneHash");
                this.phoneCode = this.currentParams.getString("phoneCode");
                String rawPattern = this.currentParams.getString("email_unconfirmed_pattern");
                SpannableStringBuilder unconfirmedPattern = SpannableStringBuilder.valueOf(rawPattern);
                int startIndex = rawPattern.indexOf(42);
                int endIndex = rawPattern.lastIndexOf(42);
                if (!(startIndex == endIndex || startIndex == -1 || endIndex == -1)) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    run.flags |= 256;
                    run.start = startIndex;
                    run.end = endIndex + 1;
                    unconfirmedPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                }
                this.troubleButton.setText(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), unconfirmedPattern));
                boolean unused = this.this$0.showKeyboard(this.codeFieldContainer);
                this.codeFieldContainer.requestFocus();
            }
        }

        private void onPasscodeError(boolean clear) {
            if (this.this$0.getParentActivity() != null) {
                try {
                    this.codeFieldContainer.performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                if (clear) {
                    for (CodeNumberField f : this.codeFieldContainer.codeField) {
                        f.setText("");
                    }
                }
                for (CodeNumberField f2 : this.codeFieldContainer.codeField) {
                    f2.animateErrorProgress(1.0f);
                }
                this.codeFieldContainer.codeField[0].requestFocus();
                AndroidUtilities.shakeViewSpring((View) this.codeFieldContainer, (Runnable) new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda6(this));
            }
        }

        /* renamed from: lambda$onPasscodeError$6$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2494xbb061fb7() {
            postDelayed(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5(this), 150);
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 3000);
            this.postedErrorColorTimeout = true;
        }

        /* renamed from: lambda$onPasscodeError$5$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2493xb9cfccd8() {
            this.codeFieldContainer.isFocusSuppressed = false;
            this.codeFieldContainer.codeField[0].requestFocus();
            for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                animateErrorProgress.animateErrorProgress(0.0f);
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                this.codeFieldContainer.isFocusSuppressed = true;
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.animateFocusedProgress(0.0f);
                }
                String code2 = this.codeFieldContainer.getCode();
                if (code2.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
                req.code = code2;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda9(this, code2), 10);
            }
        }

        /* renamed from: lambda$onNextPressed$8$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2492x9050590(String finalCode, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8(this, response, finalCode, error));
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2491x7ceb2b1(TLObject response, String finalCode, TLRPC.TL_error error) {
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
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda7(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$9$org-telegram-ui-LoginActivity$LoginActivityRecoverView  reason: not valid java name */
        public /* synthetic */ void m2495x6db0269() {
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
            String code = bundle.getString("recoveryview_code");
            if (code != null) {
                this.codeFieldContainer.setText(code);
            }
        }
    }

    public class LoginActivityNewPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor[] codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC.TL_account_password currentPassword;
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityNewPasswordView(org.telegram.ui.LoginActivity r28, android.content.Context r29, int r30) {
            /*
                r27 = this;
                r0 = r27
                r1 = r28
                r2 = r29
                r3 = r30
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
                r8 = 2131628030(0x7f0e0ffe, float:1.8883341E38)
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
            L_0x00aa:
                org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.codeField
                int r10 = r10.length
                r11 = -1
                r12 = 0
                if (r5 >= r10) goto L_0x0201
                org.telegram.ui.Components.OutlineTextContainerView r10 = new org.telegram.ui.Components.OutlineTextContainerView
                r10.<init>(r2)
                org.telegram.ui.Components.OutlineTextContainerView[] r13 = r0.outlineFields
                r13[r5] = r10
                if (r3 != 0) goto L_0x00c6
                if (r5 != 0) goto L_0x00c2
                r13 = 2131627401(0x7f0e0d89, float:1.8882065E38)
                goto L_0x00c9
            L_0x00c2:
                r13 = 2131627403(0x7f0e0d8b, float:1.888207E38)
                goto L_0x00c9
            L_0x00c6:
                r13 = 2131627201(0x7f0e0cc1, float:1.888166E38)
            L_0x00c9:
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString((int) r13)
                r10.setText(r13)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                org.telegram.ui.Components.EditTextBoldCursor r14 = new org.telegram.ui.Components.EditTextBoldCursor
                r14.<init>(r2)
                r13[r5] = r14
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r14 = 1101004800(0x41a00000, float:20.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r13.setCursorSize(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r14 = 1069547520(0x3fCLASSNAME, float:1.5)
                r13.setCursorWidth(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r13.setImeOptions(r14)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r13.setTextSize(r4, r6)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r13.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.codeField
                r13 = r13[r5]
                r14 = 0
                r13.setBackground(r14)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                r14.setPadding(r13, r13, r13, r13)
                if (r3 != 0) goto L_0x0130
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                r15 = 129(0x81, float:1.81E-43)
                r14.setInputType(r15)
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                android.text.method.PasswordTransformationMethod r15 = android.text.method.PasswordTransformationMethod.getInstance()
                r14.setTransformationMethod(r15)
            L_0x0130:
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                android.graphics.Typeface r15 = android.graphics.Typeface.DEFAULT
                r14.setTypeface(r15)
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                boolean r15 = org.telegram.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x0143
                r15 = 5
                goto L_0x0144
            L_0x0143:
                r15 = 3
            L_0x0144:
                r14.setGravity(r15)
                org.telegram.ui.Components.EditTextBoldCursor[] r14 = r0.codeField
                r14 = r14[r5]
                if (r5 != 0) goto L_0x0151
                if (r3 != 0) goto L_0x0151
                r15 = 1
                goto L_0x0152
            L_0x0151:
                r15 = 0
            L_0x0152:
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$1 r6 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$1
                r6.<init>(r1, r15)
                r14.addTextChangedListener(r6)
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r5]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4
                r8.<init>(r10)
                r6.setOnFocusChangeListener(r8)
                if (r15 == 0) goto L_0x01c1
                android.widget.LinearLayout r8 = new android.widget.LinearLayout
                r8.<init>(r2)
                r8.setOrientation(r12)
                r7 = 16
                r8.setGravity(r7)
                org.telegram.ui.Components.EditTextBoldCursor[] r7 = r0.codeField
                r7 = r7[r5]
                r6 = -2
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r6, (float) r9)
                r8.addView(r7, r6)
                android.widget.ImageView r6 = new android.widget.ImageView
                r6.<init>(r2)
                r0.passwordButton = r6
                r7 = 2131165828(0x7var_, float:1.7945884E38)
                r6.setImageResource(r7)
                android.widget.ImageView r6 = r0.passwordButton
                r7 = 1036831949(0x3dcccccd, float:0.1)
                org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r6, r4, r7, r12)
                android.widget.ImageView r6 = r0.passwordButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                r7.<init>(r0)
                r6.setOnClickListener(r7)
                android.widget.ImageView r6 = r0.passwordButton
                r20 = 1103101952(0x41CLASSNAME, float:24.0)
                r21 = 1103101952(0x41CLASSNAME, float:24.0)
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 1096810496(0x41600000, float:14.0)
                r26 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinearRelatively(r20, r21, r22, r23, r24, r25, r26)
                r8.addView(r6, r7)
                r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6)
                r10.addView(r8, r6)
                goto L_0x01ce
            L_0x01c1:
                r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                org.telegram.ui.Components.EditTextBoldCursor[] r7 = r0.codeField
                r7 = r7[r5]
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6)
                r10.addView(r7, r6)
            L_0x01ce:
                org.telegram.ui.Components.EditTextBoldCursor[] r6 = r0.codeField
                r6 = r6[r5]
                r10.attachEditText(r6)
                r19 = -1
                r20 = -2
                r21 = 1
                r22 = 16
                r23 = 16
                r24 = 16
                r25 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r0.addView(r10, r6)
                r6 = r5
                org.telegram.ui.Components.EditTextBoldCursor[] r7 = r0.codeField
                r7 = r7[r5]
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5 r8 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5
                r8.<init>(r0, r6)
                r7.setOnEditorActionListener(r8)
                int r5 = r5 + 1
                r6 = 1099956224(0x41900000, float:18.0)
                r7 = 1073741824(0x40000000, float:2.0)
                r8 = 1098907648(0x41800000, float:16.0)
                goto L_0x00aa
            L_0x0201:
                if (r3 != 0) goto L_0x0212
                android.widget.TextView r5 = r0.confirmTextView
                r6 = 2131627402(0x7f0e0d8a, float:1.8882067E38)
                java.lang.String r7 = "PleaseEnterNewFirstPasswordLogin"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                r5.setText(r6)
                goto L_0x0220
            L_0x0212:
                android.widget.TextView r5 = r0.confirmTextView
                r6 = 2131627203(0x7f0e0cc3, float:1.8881664E38)
                java.lang.String r7 = "PasswordHintTextLogin"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                r5.setText(r6)
            L_0x0220:
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.cancelButton = r5
                r6 = 19
                r5.setGravity(r6)
                android.widget.TextView r5 = r0.cancelButton
                r6 = 1097859072(0x41700000, float:15.0)
                r5.setTextSize(r4, r6)
                android.widget.TextView r4 = r0.cancelButton
                r5 = 1073741824(0x40000000, float:2.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r9)
                android.widget.TextView r4 = r0.cancelButton
                r5 = 1098907648(0x41800000, float:16.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r4.setPadding(r6, r12, r5, r12)
                android.widget.TextView r4 = r0.cancelButton
                r5 = 2131629050(0x7f0e13fa, float:1.888541E38)
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
                r4.setText(r5)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                android.widget.TextView r5 = r0.cancelButton
                r12 = -1
                int r6 = android.os.Build.VERSION.SDK_INT
                r7 = 21
                if (r6 < r7) goto L_0x026b
                r6 = 56
                goto L_0x026d
            L_0x026b:
                r6 = 60
            L_0x026d:
                float r13 = (float) r6
                r14 = 80
                r15 = 0
                r16 = 0
                r17 = 0
                r18 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r4.addView(r5, r6)
                r5 = 80
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r5 = r0.cancelButton
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
                android.widget.TextView r5 = r0.cancelButton
                org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.<init>(org.telegram.ui.LoginActivity, android.content.Context, int):void");
        }

        static /* synthetic */ void lambda$new$0(OutlineTextContainerView outlineField, View v, boolean hasFocus) {
            outlineField.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2462x8115ecde(View v) {
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

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ boolean m2463x8719b83d(int num, TextView textView, int i, KeyEvent keyEvent) {
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

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2464x8d1d839c(View view) {
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
                EditTextBoldCursor editText = editTextBoldCursorArr[i];
                editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                editText.setCursorColor(Theme.getColor(str));
                i++;
            }
            for (OutlineTextContainerView outlineField : this.outlineFields) {
                outlineField.updateColor();
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
                boolean unused = this.this$0.showKeyboard(this.codeField[0]);
                this.codeField[0].requestFocus();
            }
        }

        private void onPasscodeError(boolean clear, int num) {
            if (this.this$0.getParentActivity() != null) {
                try {
                    this.codeField[num].performHapticFeedback(3, 2);
                } catch (Exception e) {
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
            Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7(this, password, hint, req));
        }

        /* renamed from: lambda$recoverPassword$9$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2471x672b7637(String password, String hint, TLRPC.TL_auth_recoverPassword req) {
            byte[] newPasswordBytes;
            if (password != null) {
                newPasswordBytes = AndroidUtilities.getStringBytes(password);
            } else {
                newPasswordBytes = null;
            }
            RequestDelegate requestDelegate = new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1(this, password, hint);
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

        /* renamed from: lambda$recoverPassword$8$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2470x6127aad8(String password, String hint, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8(this, error, password, hint, response));
        }

        /* renamed from: lambda$recoverPassword$7$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2469x5b23dvar_(TLRPC.TL_error error, String password, String hint, TLObject response) {
            String timeString;
            if (error == null || (!"SRP_ID_INVALID".equals(error.text) && !"NEW_SALT_INVALID".equals(error.text))) {
                this.this$0.needHideProgress(false);
                if (response instanceof TLRPC.auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString(NUM), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0(this, response));
                    if (TextUtils.isEmpty(password)) {
                        builder.setMessage(LocaleController.getString(NUM));
                    } else {
                        builder.setMessage(LocaleController.getString(NUM));
                    }
                    builder.setTitle(LocaleController.getString(NUM));
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
                        this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                        return;
                    }
                    this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
                }
            } else {
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10(this, password, hint), 8);
            }
        }

        /* renamed from: lambda$recoverPassword$5$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2467x4f1CLASSNAMEbb(String password, String hint, TLObject response2, TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9(this, error2, response2, password, hint));
        }

        /* renamed from: lambda$recoverPassword$4$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2466x49187d5c(TLRPC.TL_error error2, TLObject response2, String password, String hint) {
            if (error2 == null) {
                TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
                this.currentPassword = tL_account_password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
                recoverPassword(password, hint);
            }
        }

        /* renamed from: lambda$recoverPassword$6$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2468x5520141a(TLObject response, DialogInterface dialogInterface, int i) {
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
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$10$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView  reason: not valid java name */
        public /* synthetic */ void m2465x69fffe04() {
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
        /* access modifiers changed from: private */
        public RLottieDrawable cameraDrawable;
        /* access modifiers changed from: private */
        public RLottieDrawable cameraWaitDrawable;
        private boolean createAfterUpload;
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
                    builder.setPositiveButton(LocaleController.getString("Accept", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11(this));
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14(this));
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
        public /* synthetic */ void m2512x9CLASSNAMEb17(DialogInterface dialog, int which) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* renamed from: lambda$showTermsOfService$3$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2515x7a7e261a(DialogInterface dialog, int which) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
            builder1.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder1.setMessage(LocaleController.getString("TosDecline", NUM));
            builder1.setPositiveButton(LocaleController.getString("SignUp", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12(this));
            builder1.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13(this));
            this.this$0.showDialog(builder1.create());
        }

        /* renamed from: lambda$showTermsOfService$1$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2513x2var_(DialogInterface dialog1, int which1) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed((String) null);
        }

        /* renamed from: lambda$showTermsOfService$2$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2514x54ea1d19(DialogInterface dialog12, int which12) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivityRegisterView(org.telegram.ui.LoginActivity r27, android.content.Context r28) {
            /*
                r26 = this;
                r0 = r26
                r1 = r27
                r2 = r28
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
                org.telegram.ui.Components.ImageUpdater r5 = r0.imageUpdater
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
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r10, r11)
                android.view.View r10 = r0.avatarOverlay
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda17 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda17
                r11.<init>(r0)
                r10.setOnClickListener(r11)
                org.telegram.ui.Components.RLottieDrawable r10 = new org.telegram.ui.Components.RLottieDrawable
                r11 = 2131558412(0x7f0d000c, float:1.874214E38)
                java.lang.String r14 = java.lang.String.valueOf(r11)
                r11 = 1116471296(0x428CLASSNAME, float:70.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r13 = 2131558412(0x7f0d000c, float:1.874214E38)
                r17 = 0
                r18 = 0
                r12 = r10
                r12.<init>(r13, r14, r15, r16, r17, r18)
                r0.cameraDrawable = r10
                org.telegram.ui.Components.RLottieDrawable r10 = new org.telegram.ui.Components.RLottieDrawable
                r12 = 2131558415(0x7f0d000f, float:1.8742145E38)
                java.lang.String r21 = java.lang.String.valueOf(r12)
                int r22 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r20 = 2131558415(0x7f0d000f, float:1.8742145E38)
                r24 = 0
                r25 = 0
                r19 = r10
                r19.<init>(r20, r21, r22, r23, r24, r25)
                r0.cameraWaitDrawable = r10
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
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r10, r11)
                org.telegram.ui.Components.RLottieImageView r10 = r0.avatarEditor
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$4
                r11.<init>(r1)
                r10.addOnAttachStateChangeListener(r11)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$5 r10 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$5
                r10.<init>(r2, r1)
                r0.avatarProgressView = r10
                r11 = 1106247680(0x41var_, float:30.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.setSize(r11)
                org.telegram.ui.Components.RadialProgressView r10 = r0.avatarProgressView
                r10.setProgressColor(r7)
                org.telegram.ui.Components.RadialProgressView r10 = r0.avatarProgressView
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
                r5.addView(r10, r8)
                r0.showAvatarProgress(r3, r3)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.titleTextView = r8
                r10 = 2131627686(0x7f0e0ea6, float:1.8882643E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
                r8.setText(r10)
                android.widget.TextView r8 = r0.titleTextView
                r10 = 1099956224(0x41900000, float:18.0)
                r8.setTextSize(r4, r10)
                android.widget.TextView r8 = r0.titleTextView
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r8.setTypeface(r10)
                android.widget.TextView r8 = r0.titleTextView
                r10 = 1073741824(0x40000000, float:2.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r11
                r12 = 1065353216(0x3var_, float:1.0)
                r8.setLineSpacing(r11, r12)
                android.widget.TextView r8 = r0.titleTextView
                r8.setGravity(r4)
                android.widget.TextView r8 = r0.titleTextView
                r13 = -2
                r14 = -2
                r15 = 1
                r16 = 8
                r17 = 12
                r18 = 8
                r19 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r8, r11)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.descriptionTextView = r8
                java.lang.String r11 = "RegisterText2"
                r13 = 2131627685(0x7f0e0ea5, float:1.8882641E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r13)
                r8.setText(r11)
                android.widget.TextView r8 = r0.descriptionTextView
                r8.setGravity(r4)
                android.widget.TextView r8 = r0.descriptionTextView
                r11 = 1096810496(0x41600000, float:14.0)
                r8.setTextSize(r4, r11)
                android.widget.TextView r8 = r0.descriptionTextView
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r13 = (float) r13
                r8.setLineSpacing(r13, r12)
                android.widget.TextView r8 = r0.descriptionTextView
                r13 = -2
                r17 = 6
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
                r0.addView(r8, r13)
                android.widget.FrameLayout r8 = new android.widget.FrameLayout
                r8.<init>(r2)
                r0.editTextContainer = r8
                r13 = -1
                r15 = 1090519040(0x41000000, float:8.0)
                r16 = 1101529088(0x41a80000, float:21.0)
                r17 = 1090519040(0x41000000, float:8.0)
                r18 = 0
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r14, r15, r16, r17, r18)
                r0.addView(r8, r13)
                org.telegram.ui.Components.OutlineTextContainerView r8 = new org.telegram.ui.Components.OutlineTextContainerView
                r8.<init>(r2)
                r0.firstNameOutlineView = r8
                r13 = 2131625807(0x7f0e074f, float:1.8878832E38)
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString((int) r13)
                r8.setText(r13)
                org.telegram.ui.Components.EditTextBoldCursor r8 = new org.telegram.ui.Components.EditTextBoldCursor
                r8.<init>(r2)
                r0.firstNameField = r8
                r13 = 1101004800(0x41a00000, float:20.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r8.setCursorSize(r14)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r14 = 1069547520(0x3fCLASSNAME, float:1.5)
                r8.setCursorWidth(r14)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r15 = 268435461(0x10000005, float:2.5243564E-29)
                r8.setImeOptions(r15)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r15 = 1099431936(0x41880000, float:17.0)
                r8.setTextSize(r4, r15)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r8.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r3 = 8192(0x2000, float:1.14794E-41)
                r8.setInputType(r3)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda19 r12 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda19
                r12.<init>(r0)
                r8.setOnFocusChangeListener(r12)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r8.setBackground(r9)
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r12 = 1098907648(0x41800000, float:16.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r8.setPadding(r10, r11, r9, r3)
                org.telegram.ui.Components.OutlineTextContainerView r3 = r0.firstNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r3.attachEditText(r8)
                org.telegram.ui.Components.OutlineTextContainerView r3 = r0.firstNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.firstNameField
                r9 = -2
                r10 = 48
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r9, (int) r10)
                r3.addView(r8, r11)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.firstNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2
                r8.<init>(r0)
                r3.setOnEditorActionListener(r8)
                org.telegram.ui.Components.OutlineTextContainerView r3 = new org.telegram.ui.Components.OutlineTextContainerView
                r3.<init>(r2)
                r0.lastNameOutlineView = r3
                r8 = 2131626249(0x7f0e0909, float:1.8879729E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString((int) r8)
                r3.setText(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
                r3.<init>(r2)
                r0.lastNameField = r3
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r3.setCursorSize(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r3.setCursorWidth(r14)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r8 = 268435462(0x10000006, float:2.5243567E-29)
                r3.setImeOptions(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r3.setTextSize(r4, r15)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r3.setMaxLines(r4)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r8 = 8192(0x2000, float:1.14794E-41)
                r3.setInputType(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda18 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda18
                r8.<init>(r0)
                r3.setOnFocusChangeListener(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                r8 = 0
                r3.setBackground(r8)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r3.setPadding(r8, r11, r13, r12)
                org.telegram.ui.Components.OutlineTextContainerView r3 = r0.lastNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.lastNameField
                r3.attachEditText(r8)
                org.telegram.ui.Components.OutlineTextContainerView r3 = r0.lastNameOutlineView
                org.telegram.ui.Components.EditTextBoldCursor r8 = r0.lastNameField
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r9, (int) r10)
                r3.addView(r8, r9)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r0.lastNameField
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1 r8 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1
                r8.<init>(r0)
                r3.setOnEditorActionListener(r8)
                boolean r3 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                r0.buildEditTextLayout(r3)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.wrongNumber = r3
                java.lang.String r8 = "CancelRegistration"
                r9 = 2131624772(0x7f0e0344, float:1.8876733E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)
                r3.setText(r8)
                android.widget.TextView r3 = r0.wrongNumber
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                r9 = 5
                r11 = 3
                if (r8 == 0) goto L_0x02ea
                r8 = 5
                goto L_0x02eb
            L_0x02ea:
                r8 = 3
            L_0x02eb:
                r8 = r8 | r4
                r3.setGravity(r8)
                android.widget.TextView r3 = r0.wrongNumber
                r8 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r4, r8)
                android.widget.TextView r3 = r0.wrongNumber
                r12 = 1073741824(0x40000000, float:2.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r13
                r13 = 1065353216(0x3var_, float:1.0)
                r3.setLineSpacing(r12, r13)
                android.widget.TextView r3 = r0.wrongNumber
                r12 = 1103101952(0x41CLASSNAME, float:24.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r13 = 0
                r3.setPadding(r13, r12, r13, r13)
                android.widget.TextView r3 = r0.wrongNumber
                r12 = 8
                r3.setVisibility(r12)
                android.widget.TextView r3 = r0.wrongNumber
                r19 = -2
                r20 = -2
                boolean r12 = org.telegram.messenger.LocaleController.isRTL
                if (r12 == 0) goto L_0x0322
                goto L_0x0323
            L_0x0322:
                r9 = 3
            L_0x0323:
                r21 = r9 | 48
                r22 = 0
                r23 = 20
                r24 = 0
                r25 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
                r0.addView(r3, r9)
                android.widget.TextView r3 = r0.wrongNumber
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda16 r9 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda16
                r9.<init>(r0)
                r3.setOnClickListener(r9)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                r9 = 83
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r7, (int) r9)
                r0.addView(r3, r9)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                r0.privacyView = r9
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r10 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r10.<init>()
                r9.setMovementMethod(r10)
                android.widget.TextView r9 = r0.privacyView
                boolean r10 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
                if (r10 == 0) goto L_0x0366
                r11 = 1095761920(0x41500000, float:13.0)
                goto L_0x0368
            L_0x0366:
                r11 = 1096810496(0x41600000, float:14.0)
            L_0x0368:
                r9.setTextSize(r4, r11)
                android.widget.TextView r4 = r0.privacyView
                r8 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r8, r9)
                android.widget.TextView r4 = r0.privacyView
                r8 = 16
                r4.setGravity(r8)
                android.widget.TextView r4 = r0.privacyView
                r8 = -2
                int r9 = android.os.Build.VERSION.SDK_INT
                r10 = 21
                if (r9 < r10) goto L_0x038c
                r9 = 1113587712(0x42600000, float:56.0)
                goto L_0x038e
            L_0x038c:
                r9 = 1114636288(0x42700000, float:60.0)
            L_0x038e:
                r10 = 83
                r11 = 1096810496(0x41600000, float:14.0)
                r12 = 0
                r13 = 1116471296(0x428CLASSNAME, float:70.0)
                r14 = 1107296256(0x42000000, float:32.0)
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r3.addView(r4, r8)
                android.widget.TextView r4 = r0.privacyView
                org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
                r4 = 2131628369(0x7f0e1151, float:1.8884029E38)
                java.lang.String r8 = "TermsOfServiceLogin"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
                android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
                r8.<init>(r4)
                r9 = 42
                int r10 = r4.indexOf(r9)
                int r9 = r4.lastIndexOf(r9)
                if (r10 == r7) goto L_0x03d9
                if (r9 == r7) goto L_0x03d9
                if (r10 == r9) goto L_0x03d9
                int r7 = r9 + 1
                java.lang.String r11 = ""
                r8.replace(r9, r7, r11)
                int r7 = r10 + 1
                r8.replace(r10, r7, r11)
                org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan r7 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView$LinkSpan
                r7.<init>()
                int r11 = r9 + -1
                r12 = 33
                r8.setSpan(r7, r10, r11, r12)
            L_0x03d9:
                android.widget.TextView r7 = r0.privacyView
                r7.setText(r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivityRegisterView.<init>(org.telegram.ui.LoginActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2503x1384770(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3(this), new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15(this));
            this.isCameraWaitAnimationAllowed = false;
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.avatarEditor.playAnimation();
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2500x907c2c6d() {
            this.avatar = null;
            this.avatarBig = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2502xdba43e6f(DialogInterface dialog) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.avatarEditor.setAnimation(this.cameraDrawable);
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.setOnAnimationEndListener(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4(this));
                this.avatarEditor.playAnimation();
                return;
            }
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0, false);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2501xb610356e() {
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2504x26cCLASSNAME(View v, boolean hasFocus) {
            this.firstNameOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$9$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ boolean m2505x4CLASSNAME(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        /* renamed from: lambda$new$10$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2497x5951CLASSNAMEa(View v, boolean hasFocus) {
            this.lastNameOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$11$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ boolean m2498x7ee5ce7b(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$12$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2499xa479d77c(View view) {
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

        private void buildEditTextLayout(boolean small) {
            boolean firstHasFocus = this.firstNameField.hasFocus();
            boolean lastHasFocus = this.lastNameField.hasFocus();
            this.editTextContainer.removeAllViews();
            if (small) {
                LinearLayout linearLayout = new LinearLayout(this.this$0.getParentActivity());
                linearLayout.setOrientation(0);
                this.firstNameOutlineView.setText(LocaleController.getString(NUM));
                this.lastNameOutlineView.setText(LocaleController.getString(NUM));
                linearLayout.addView(this.firstNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 8, 0));
                linearLayout.addView(this.lastNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 8, 0, 0, 0));
                this.editTextContainer.addView(linearLayout);
                if (firstHasFocus) {
                    this.firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.firstNameField);
                } else if (lastHasFocus) {
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

        public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9(this, smallSize, bigSize));
        }

        /* renamed from: lambda$didUploadPhoto$13$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2496x70a81var_(TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
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
                builder.setTitle(LocaleController.getString(NUM));
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

        /* renamed from: lambda$onBackPressed$14$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2506x8c6b3var_(DialogInterface dialogInterface, int i) {
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
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5(this), (long) LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$15$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2511x3fa2var_() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
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
                    this.this$0.onFieldError(this.firstNameOutlineView, true);
                } else {
                    this.nextPressed = true;
                    TLRPC.TL_auth_signUp req = new TLRPC.TL_auth_signUp();
                    req.phone_code_hash = this.phoneHash;
                    req.phone_number = this.requestPhone;
                    req.first_name = this.firstNameField.getText().toString();
                    req.last_name = this.lastNameField.getText().toString();
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10(this), 10);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2510x46ed3var_(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7(this, response, error));
        }

        /* renamed from: lambda$onNextPressed$18$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2509x21593692(TLObject response, TLRPC.TL_error error) {
            this.nextPressed = false;
            if (response instanceof TLRPC.TL_auth_authorization) {
                hidePrivacyView();
                this.this$0.showDoneButton(false, true);
                postDelayed(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6(this, response), 150);
                return;
            }
            this.this$0.needHideProgress(false);
            if (error.text.contains("PHONE_NUMBER_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (error.text.contains("FIRSTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (error.text.contains("LASTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(NUM), error.text);
            }
        }

        /* renamed from: lambda$onNextPressed$17$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2508xfbCLASSNAMEd91(TLObject response) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.this$0.fragmentView.findFocus());
            this.this$0.onAuthSuccess((TLRPC.TL_auth_authorization) response, true);
            if (this.avatarBig != null) {
                Utilities.cacheClearQueue.postRunnable(new LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8(this, this.avatarBig));
            }
        }

        /* renamed from: lambda$onNextPressed$16$org-telegram-ui-LoginActivity$LoginActivityRegisterView  reason: not valid java name */
        public /* synthetic */ void m2507xd6312490(TLRPC.FileLocation avatar2) {
            MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(avatar2);
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

    /* access modifiers changed from: private */
    public boolean showKeyboard(View editText) {
        if (!isCustomKeyboardVisible()) {
            return AndroidUtilities.showKeyboard(editText);
        }
        return true;
    }

    public LoginActivity setIntroView(View intro, TextView startButton) {
        this.introView = intro;
        this.startMessagingButton = startButton;
        this.isAnimatingIntro = true;
        return this;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (!isOpen || this.introView == null) {
            return null;
        }
        final TransformableLoginButtonView transformButton = new TransformableLoginButtonView(this.fragmentView.getContext());
        transformButton.setButtonText(this.startMessagingButton.getPaint(), this.startMessagingButton.getText().toString());
        int oldTransformWidth = this.startMessagingButton.getWidth();
        int oldTransformHeight = this.startMessagingButton.getHeight();
        int newTransformSize = this.floatingButtonIcon.getLayoutParams().width;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(oldTransformWidth, oldTransformHeight);
        transformButton.setLayoutParams(layoutParams);
        int[] loc = new int[2];
        this.fragmentView.getLocationInWindow(loc);
        int fragmentX = loc[0];
        int fragmentY = loc[1];
        this.startMessagingButton.getLocationInWindow(loc);
        float fromX = (float) (loc[0] - fragmentX);
        float fromY = (float) (loc[1] - fragmentY);
        transformButton.setTranslationX(fromX);
        transformButton.setTranslationY(fromY);
        int toX = (((getParentLayout().getWidth() - this.floatingButtonIcon.getLayoutParams().width) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).rightMargin) - getParentLayout().getPaddingLeft()) - getParentLayout().getPaddingRight();
        int toY = ((((getParentLayout().getHeight() - this.floatingButtonIcon.getLayoutParams().height) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).bottomMargin) - (isCustomKeyboardVisible() ? AndroidUtilities.dp(230.0f) : 0)) - getParentLayout().getPaddingTop()) - getParentLayout().getPaddingBottom();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        final Runnable runnable = callback;
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                LoginActivity.this.floatingButtonContainer.setVisibility(4);
                LoginActivity.this.keyboardLinearLayout.setAlpha(0.0f);
                LoginActivity.this.fragmentView.setBackgroundColor(0);
                LoginActivity.this.startMessagingButton.setVisibility(4);
                ((FrameLayout) LoginActivity.this.fragmentView).addView(transformButton);
            }

            public void onAnimationEnd(Animator animation) {
                LoginActivity.this.keyboardLinearLayout.setAlpha(1.0f);
                LoginActivity.this.startMessagingButton.setVisibility(0);
                LoginActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                LoginActivity.this.floatingButtonContainer.setVisibility(0);
                ((FrameLayout) LoginActivity.this.fragmentView).removeView(transformButton);
                if (LoginActivity.this.animationFinishCallback != null) {
                    AndroidUtilities.runOnUIThread(LoginActivity.this.animationFinishCallback);
                    Runnable unused = LoginActivity.this.animationFinishCallback = null;
                }
                boolean unused2 = LoginActivity.this.isAnimatingIntro = false;
                runnable.run();
            }
        });
        int bgColor = Theme.getColor("windowBackgroundWhite");
        LoginActivity$$ExternalSyntheticLambda16 loginActivity$$ExternalSyntheticLambda16 = r0;
        ValueAnimator animator2 = animator;
        int i = oldTransformWidth;
        int[] iArr = loc;
        FrameLayout.LayoutParams layoutParams2 = layoutParams;
        int i2 = newTransformSize;
        int i3 = oldTransformHeight;
        LoginActivity$$ExternalSyntheticLambda16 loginActivity$$ExternalSyntheticLambda162 = new LoginActivity$$ExternalSyntheticLambda16(this, bgColor, Color.alpha(bgColor), layoutParams, oldTransformWidth, newTransformSize, oldTransformHeight, transformButton, fromX, toX, fromY, toY);
        animator2.addUpdateListener(loginActivity$$ExternalSyntheticLambda16);
        animator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(new Animator[]{animator2});
        set.start();
        return set;
    }

    /* renamed from: lambda$onCustomTransitionAnimation$17$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2449x6dvar_(int bgColor, int initialAlpha, ViewGroup.MarginLayoutParams transformParams, int oldTransformWidth, int newTransformSize, int oldTransformHeight, TransformableLoginButtonView transformButton, float fromX, int toX, float fromY, int toY, ValueAnimator animation) {
        ViewGroup.MarginLayoutParams marginLayoutParams = transformParams;
        int i = oldTransformWidth;
        int i2 = oldTransformHeight;
        TransformableLoginButtonView transformableLoginButtonView = transformButton;
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardLinearLayout.setAlpha(val);
        int i3 = bgColor;
        this.fragmentView.setBackgroundColor(ColorUtils.setAlphaComponent(bgColor, (int) (((float) initialAlpha) * val)));
        float inverted = 1.0f - val;
        this.slideViewsContainer.setTranslationY(((float) AndroidUtilities.dp(20.0f)) * inverted);
        if (!isCustomKeyboardForceDisabled()) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            customPhoneKeyboardView.setTranslationY(((float) customPhoneKeyboardView.getLayoutParams().height) * inverted);
            this.floatingButtonContainer.setTranslationY(((float) this.keyboardView.getLayoutParams().height) * inverted);
        }
        this.introView.setTranslationY(((float) (-AndroidUtilities.dp(20.0f))) * val);
        float sc = (0.05f * inverted) + 0.95f;
        this.introView.setScaleX(sc);
        this.introView.setScaleY(sc);
        marginLayoutParams.width = (int) (((float) i) + (((float) (newTransformSize - i)) * val));
        marginLayoutParams.height = (int) (((float) i2) + (((float) (newTransformSize - i2)) * val));
        transformButton.requestLayout();
        transformableLoginButtonView.setProgress(val);
        transformableLoginButtonView.setTranslationX(fromX + ((((float) toX) - fromX) * val));
        transformableLoginButtonView.setTranslationY(fromY + ((((float) toY) - fromY) * val));
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        Context context = getParentActivity();
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
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
        PhoneNumberConfirmView phoneNumberConfirmView2 = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView2 != null) {
            phoneNumberConfirmView2.updateColors();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new LoginActivity$$ExternalSyntheticLambda12(this), "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText6", "windowBackgroundWhiteHintText", "listSelectorSDK21", "chats_actionBackground", "chats_actionIcon", "windowBackgroundWhiteInputField", "windowBackgroundWhiteInputFieldActivated", "windowBackgroundWhiteValueText", "dialogTextRed", "windowBackgroundWhiteGrayText", "checkbox", "windowBackgroundWhiteBlueText4", "changephoneinfo_image2", "chats_actionPressedBackground", "windowBackgroundWhiteRedText2", "windowBackgroundWhiteLinkText", "checkboxSquareUnchecked", "checkboxSquareBackground", "checkboxSquareCheck", "dialogBackground", "dialogTextGray2", "dialogTextBlack");
    }

    /* access modifiers changed from: private */
    public void tryResetAccount(String requestPhone, String phoneHash, String phoneCode) {
        if (this.radialProgressView.getTag() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
            builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$$ExternalSyntheticLambda19(this, requestPhone, phoneHash, phoneCode));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$tryResetAccount$20$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2460lambda$tryResetAccount$20$orgtelegramuiLoginActivity(String requestPhone, String phoneHash, String phoneCode, DialogInterface dialogInterface, int i) {
        needShowProgress(0);
        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
        req.reason = "Forgot password";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LoginActivity$$ExternalSyntheticLambda10(this, requestPhone, phoneHash, phoneCode), 10);
    }

    /* renamed from: lambda$tryResetAccount$19$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2459lambda$tryResetAccount$19$orgtelegramuiLoginActivity(String requestPhone, String phoneHash, String phoneCode, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LoginActivity$$ExternalSyntheticLambda8(this, error, requestPhone, phoneHash, phoneCode));
    }

    /* renamed from: lambda$tryResetAccount$18$org-telegram-ui-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m2458lambda$tryResetAccount$18$orgtelegramuiLoginActivity(TLRPC.TL_error error, String requestPhone, String phoneHash, String phoneCode) {
        needHideProgress(false);
        if (error == null) {
            if (requestPhone == null || phoneHash == null || phoneCode == null) {
                setPage(0, true, (Bundle) null, true);
                return;
            }
            Bundle params = new Bundle();
            params.putString("phoneFormated", requestPhone);
            params.putString("phoneHash", phoneHash);
            params.putString("code", phoneCode);
            setPage(5, true, params, false);
        } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
            needShowAlert(LocaleController.getString(NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
        } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
            Bundle params2 = new Bundle();
            params2.putString("phoneFormated", requestPhone);
            params2.putString("phoneHash", phoneHash);
            params2.putString("code", phoneCode);
            params2.putInt("startTime", ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            params2.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
            setPage(8, true, params2, false);
        } else {
            needShowAlert(LocaleController.getString(NUM), error.text);
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
        private PhoneNumberConfirmView(Context context, ViewGroup fragmentView2, View fabContainer2, String numberText, IConfirmDialogCallback callback2) {
            super(context);
            Context context2 = context;
            IConfirmDialogCallback iConfirmDialogCallback = callback2;
            this.fragmentView = fragmentView2;
            this.fabContainer = fabContainer2;
            this.callback = iConfirmDialogCallback;
            View view = new View(getContext());
            this.blurredView = view;
            view.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda3(this));
            addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
            View view2 = new View(getContext());
            this.dimmView = view2;
            view2.setBackgroundColor(NUM);
            this.dimmView.setAlpha(0.0f);
            addView(this.dimmView, LayoutHelper.createFrame(-1, -1.0f));
            TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(getContext());
            this.fabTransform = transformableLoginButtonView;
            transformableLoginButtonView.setTransformType(1);
            this.fabTransform.setDrawBackground(false);
            FrameLayout frameLayout = new FrameLayout(context2);
            this.popupFabContainer = frameLayout;
            frameLayout.addView(this.fabTransform, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda4(this, iConfirmDialogCallback));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.floatingProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(22.0f));
            this.floatingProgressView.setAlpha(0.0f);
            this.floatingProgressView.setScaleX(0.1f);
            this.floatingProgressView.setScaleY(0.1f);
            this.popupFabContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setContentDescription(LocaleController.getString(NUM));
            addView(this.popupFabContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.popupLayout = frameLayout2;
            addView(frameLayout2, LayoutHelper.createFrame(-1, 140.0f, 49, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context2);
            this.confirmMessageView = textView;
            textView.setText(LocaleController.getString(NUM));
            this.confirmMessageView.setTextSize(1, 14.0f);
            this.confirmMessageView.setSingleLine();
            this.popupLayout.addView(this.confirmMessageView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 20.0f, 24.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.numberView = textView2;
            textView2.setText(numberText);
            this.numberView.setTextSize(1, 18.0f);
            this.numberView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.numberView.setSingleLine();
            this.popupLayout.addView(this.numberView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 48.0f, 24.0f, 0.0f));
            int buttonPadding = AndroidUtilities.dp(16.0f);
            TextView textView3 = new TextView(context2);
            this.editTextView = textView3;
            textView3.setText(LocaleController.getString(NUM));
            this.editTextView.setSingleLine();
            this.editTextView.setTextSize(1, 16.0f);
            this.editTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.editTextView.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda5(this, iConfirmDialogCallback));
            this.editTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.editTextView.setPadding(buttonPadding, buttonPadding / 2, buttonPadding, buttonPadding / 2);
            this.popupLayout.addView(this.editTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, (float) 8, (float) 8, (float) 8, (float) 8));
            TextView textView4 = new TextView(context2);
            this.confirmTextView = textView4;
            textView4.setText(LocaleController.getString(NUM));
            this.confirmTextView.setSingleLine();
            this.confirmTextView.setTextSize(1, 16.0f);
            this.confirmTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("changephoneinfo_image2")));
            this.confirmTextView.setOnClickListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda6(this, iConfirmDialogCallback));
            this.confirmTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.confirmTextView.setPadding(buttonPadding, buttonPadding / 2, buttonPadding, buttonPadding / 2);
            this.popupLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 80, (float) 8, (float) 8, (float) 8, (float) 8));
            updateFabPosition();
            updateColors();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2562x2b64fCLASSNAME(View v) {
            dismiss();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2563x45807b24(IConfirmDialogCallback callback2, View v) {
            callback2.onFabPressed(this, this.fabTransform);
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2564x5f9bf9c3(IConfirmDialogCallback callback2, View v) {
            callback2.onEditPressed(this, this.editTextView);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2565x79b77862(IConfirmDialogCallback callback2, View v) {
            callback2.onConfirmPressed(this, this.confirmTextView);
        }

        /* access modifiers changed from: private */
        public void updateFabPosition() {
            int[] loc = new int[2];
            this.fragmentView.getLocationInWindow(loc);
            int fragmentX = loc[0];
            int fragmentY = loc[1];
            this.fabContainer.getLocationInWindow(loc);
            this.popupFabContainer.setTranslationX((float) (loc[0] - fragmentX));
            this.popupFabContainer.setTranslationY((float) (loc[1] - fragmentY));
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
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int height = this.popupLayout.getMeasuredHeight();
            int popupBottom = (int) (this.popupFabContainer.getTranslationY() - ((float) AndroidUtilities.dp(32.0f)));
            FrameLayout frameLayout = this.popupLayout;
            frameLayout.layout(frameLayout.getLeft(), popupBottom - height, this.popupLayout.getRight(), popupBottom);
        }

        /* access modifiers changed from: private */
        public void show() {
            ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(250);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(8);
                    int w = (int) (((float) PhoneNumberConfirmView.this.fragmentView.getMeasuredWidth()) / 10.0f);
                    int h = (int) (((float) PhoneNumberConfirmView.this.fragmentView.getMeasuredHeight()) / 10.0f);
                    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.scale(1.0f / 10.0f, 1.0f / 10.0f);
                    PhoneNumberConfirmView.this.fragmentView.draw(canvas);
                    Utilities.stackBlurBitmap(bitmap, Math.max(8, Math.max(w, h) / 150));
                    PhoneNumberConfirmView.this.blurredView.setBackground(new BitmapDrawable(PhoneNumberConfirmView.this.getContext().getResources(), bitmap));
                    PhoneNumberConfirmView.this.blurredView.setAlpha(0.0f);
                    PhoneNumberConfirmView.this.blurredView.setVisibility(0);
                    PhoneNumberConfirmView.this.fragmentView.addView(PhoneNumberConfirmView.this);
                }

                public void onAnimationEnd(Animator animation) {
                    if (AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                        PhoneNumberConfirmView.this.popupFabContainer.requestFocus();
                    }
                }
            });
            anim.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2(this));
            anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anim.start();
        }

        /* renamed from: lambda$show$4$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2566x5a9042aa(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            this.fabTransform.setProgress(val);
            this.blurredView.setAlpha(val);
            this.dimmView.setAlpha(val);
            this.popupLayout.setAlpha(val);
            float scale = (val * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(scale);
            this.popupLayout.setScaleY(scale);
        }

        /* access modifiers changed from: private */
        public void animateProgress(final Runnable callback2) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    callback2.run();
                }
            });
            animator.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda0(this));
            animator.setDuration(150);
            animator.start();
        }

        /* renamed from: lambda$animateProgress$5$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2560xdd7ddbd2(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            float scale = ((1.0f - val) * 0.9f) + 0.1f;
            this.fabTransform.setScaleX(scale);
            this.fabTransform.setScaleY(scale);
            this.fabTransform.setAlpha(1.0f - val);
            float scale2 = (0.9f * val) + 0.1f;
            this.floatingProgressView.setScaleX(scale2);
            this.floatingProgressView.setScaleY(scale2);
            this.floatingProgressView.setAlpha(val);
        }

        /* access modifiers changed from: private */
        public void dismiss() {
            if (!this.dismissed) {
                this.dismissed = true;
                this.callback.onDismiss(this);
                ValueAnimator anim = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f}).setDuration(250);
                anim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhoneNumberConfirmView.this.getParent() instanceof ViewGroup) {
                            ((ViewGroup) PhoneNumberConfirmView.this.getParent()).removeView(PhoneNumberConfirmView.this);
                        }
                        PhoneNumberConfirmView.this.fabContainer.setVisibility(0);
                    }
                });
                anim.addUpdateListener(new LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda1(this));
                anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                anim.start();
            }
        }

        /* renamed from: lambda$dismiss$6$org-telegram-ui-LoginActivity$PhoneNumberConfirmView  reason: not valid java name */
        public /* synthetic */ void m2561x774ce595(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            this.blurredView.setAlpha(val);
            this.dimmView.setAlpha(val);
            this.fabTransform.setProgress(val);
            this.popupLayout.setAlpha(val);
            float scale = (val * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(scale);
            this.popupLayout.setScaleY(scale);
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
