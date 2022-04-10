package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_declinePasswordReset;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_passwordSettings;
import org.telegram.tgnet.TLRPC$TL_account_resetPassword;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordFailedWait;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordOk;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordRequestedWait;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoSHA512;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC$TL_secureSecretSettings;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TransformableLoginButtonView;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public Runnable errorColorTimeout = new TwoStepVerificationActivity$$ExternalSyntheticLambda13(this);
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private boolean forgotPasswordOnShow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    private RLottieImageView lockImageView;
    int otherwiseReloginDays = -1;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private OutlineTextContainerView passwordOutlineView;
    /* access modifiers changed from: private */
    public boolean postedErrorColorTimeout;
    private AlertDialog progressDialog;
    private RadialProgressView radialProgressView;
    /* access modifiers changed from: private */
    public boolean resetPasswordOnShow;
    private TextView resetWaitView;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScrollView scrollView;
    /* access modifiers changed from: private */
    public int setPasswordDetailRow;
    /* access modifiers changed from: private */
    public int setPasswordRow;
    /* access modifiers changed from: private */
    public int setRecoveryEmailRow;
    private TextView subtitleTextView;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int turnPasswordOffRow;
    private Runnable updateTimeRunnable = new TwoStepVerificationActivity$$ExternalSyntheticLambda12(this);

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkSecretValues$28(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.postedErrorColorTimeout = false;
        this.passwordOutlineView.animateError(0.0f);
    }

    public void setPassword(TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentPassword = tLRPC$TL_account_password;
        this.passwordEntered = false;
    }

    public void setCurrentPasswordParams(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr, long j, byte[] bArr2) {
        this.currentPassword = tLRPC$TL_account_password;
        this.currentPasswordHash = bArr;
        this.currentSecret = bArr2;
        this.currentSecretId = j;
        this.passwordEntered = (bArr != null && bArr.length > 0) || !tLRPC$TL_account_password.has_password;
    }

    public boolean onFragmentCreate() {
        byte[] bArr;
        super.onFragmentCreate();
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (tLRPC$TL_account_password == null || tLRPC$TL_account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, tLRPC$TL_account_password != null);
        }
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        this.destroyed = true;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0216  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0218  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0265  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x02c6  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x032c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x032f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0404  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x042f  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x045e  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0474  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0477  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0524  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0533  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x053d  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x054c  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x056d  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0580  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r33) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131165497(0x7var_, float:1.7945213E38)
            r2.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 0
            r2.setAllowOverlayTitle(r3)
            boolean r2 = r0.passwordEntered
            java.lang.String r4 = "windowBackgroundWhiteBlackText"
            java.lang.String r5 = "windowBackgroundWhite"
            if (r2 != 0) goto L_0x0045
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setBackgroundColor(r6)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTitleColor(r6)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setItemsColor(r6, r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r6 = "actionBarWhiteSelector"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r2.setItemsBackgroundColor(r6, r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.setCastShadows(r3)
        L_0x0045:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.TwoStepVerificationActivity$1 r6 = new org.telegram.ui.TwoStepVerificationActivity$1
            r6.<init>()
            r2.setActionBarMenuOnItemClick(r6)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.fragmentView = r2
            android.widget.FrameLayout r2 = (android.widget.FrameLayout) r2
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setBackgroundColor(r6)
            android.widget.ScrollView r6 = new android.widget.ScrollView
            r6.<init>(r1)
            r0.scrollView = r6
            r7 = 1
            r6.setFillViewport(r7)
            android.widget.ScrollView r6 = r0.scrollView
            r8 = -1
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9)
            r2.addView(r6, r10)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r6.setOrientation(r7)
            r6.setGravity(r7)
            android.widget.ScrollView r10 = r0.scrollView
            r11 = -2
            r12 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createScroll(r8, r11, r12)
            r10.addView(r6, r11)
            org.telegram.ui.Components.RLottieImageView r10 = new org.telegram.ui.Components.RLottieImageView
            r10.<init>(r1)
            r0.lockImageView = r10
            r11 = 2131558539(0x7f0d008b, float:1.8742397E38)
            r12 = 120(0x78, float:1.68E-43)
            r10.setAnimation(r11, r12, r12)
            org.telegram.ui.Components.RLottieImageView r10 = r0.lockImageView
            r10.playAnimation()
            org.telegram.ui.Components.RLottieImageView r10 = r0.lockImageView
            boolean r11 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
            r13 = 8
            if (r11 != 0) goto L_0x00b6
            android.graphics.Point r11 = org.telegram.messenger.AndroidUtilities.displaySize
            int r14 = r11.x
            int r11 = r11.y
            if (r14 <= r11) goto L_0x00b4
            goto L_0x00b6
        L_0x00b4:
            r11 = 0
            goto L_0x00b8
        L_0x00b6:
            r11 = 8
        L_0x00b8:
            r10.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r0.lockImageView
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r7)
            r6.addView(r10, r11)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r0.titleTextView = r10
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r10.setTextColor(r11)
            android.widget.TextView r10 = r0.titleTextView
            r11 = 1099956224(0x41900000, float:18.0)
            r10.setTextSize(r7, r11)
            android.widget.TextView r10 = r0.titleTextView
            r10.setGravity(r7)
            android.widget.TextView r10 = r0.titleTextView
            java.lang.String r12 = "fonts/rmedium.ttf"
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r10.setTypeface(r12)
            android.widget.TextView r10 = r0.titleTextView
            r14 = -2
            r15 = -2
            r16 = 1
            r17 = 24
            r18 = 8
            r19 = 24
            r20 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20)
            r6.addView(r10, r12)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r0.subtitleTextView = r10
            java.lang.String r12 = "windowBackgroundWhiteGrayText6"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r10.setTextColor(r14)
            android.widget.TextView r10 = r0.subtitleTextView
            r14 = 1097859072(0x41700000, float:15.0)
            r10.setTextSize(r7, r14)
            android.widget.TextView r10 = r0.subtitleTextView
            r10.setGravity(r7)
            android.widget.TextView r10 = r0.subtitleTextView
            r10.setVisibility(r13)
            android.widget.TextView r10 = r0.subtitleTextView
            r16 = -2
            r17 = 1
            r18 = 24
            r19 = 8
            r20 = 24
            r21 = 0
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r6.addView(r10, r15)
            org.telegram.ui.Components.OutlineTextContainerView r10 = new org.telegram.ui.Components.OutlineTextContainerView
            r10.<init>(r1)
            r0.passwordOutlineView = r10
            r15 = 2131625545(0x7f0e0649, float:1.88783E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r15)
            r10.setText(r9)
            org.telegram.ui.Components.OutlineTextContainerView r9 = r0.passwordOutlineView
            r10 = 1065353216(0x3var_, float:1.0)
            r9.animateSelection(r10, r3)
            org.telegram.ui.Components.OutlineTextContainerView r9 = r0.passwordOutlineView
            r17 = -1
            r18 = -2
            r19 = 1
            r21 = 24
            r22 = 24
            r23 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r6.addView(r9, r13)
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r1)
            r0.passwordEditText = r9
            r9.setTextSize(r7, r11)
            org.telegram.ui.Components.EditTextBoldCursor r9 = r0.passwordEditText
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r9.setTextColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            r9 = 0
            r4.setBackground(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            r4.setSingleLine(r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            r11 = 129(0x81, float:1.81E-43)
            r4.setInputType(r11)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            android.text.method.PasswordTransformationMethod r11 = android.text.method.PasswordTransformationMethod.getInstance()
            r4.setTransformationMethod(r11)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            android.graphics.Typeface r11 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r11)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            java.lang.String r11 = "windowBackgroundWhiteInputFieldActivated"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            r13 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r13)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString((int) r15)
            r4.setContentDescription(r13)
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.ui.Components.EditTextBoldCursor r13 = r0.passwordEditText
            r13.setPadding(r4, r4, r4, r4)
            org.telegram.ui.Components.OutlineTextContainerView r4 = r0.passwordOutlineView
            org.telegram.ui.Components.EditTextBoldCursor r13 = r0.passwordEditText
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r15)
            r4.addView(r13, r15)
            org.telegram.ui.Components.OutlineTextContainerView r4 = r0.passwordOutlineView
            org.telegram.ui.Components.EditTextBoldCursor r13 = r0.passwordEditText
            r4.attachEditText(r13)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda9 r13 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda9
            r13.<init>(r0)
            r4.setOnFocusChangeListener(r13)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda10 r13 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda10
            r13.<init>(r0)
            r4.setOnEditorActionListener(r13)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.passwordEditText
            org.telegram.ui.TwoStepVerificationActivity$2 r13 = new org.telegram.ui.TwoStepVerificationActivity$2
            r13.<init>()
            r4.addTextChangedListener(r13)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.bottomTextView = r4
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r4.setTextColor(r13)
            android.widget.TextView r4 = r0.bottomTextView
            r13 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r7, r13)
            android.widget.TextView r4 = r0.bottomTextView
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            r17 = 3
            if (r13 == 0) goto L_0x0218
            r13 = 5
            goto L_0x0219
        L_0x0218:
            r13 = 3
        L_0x0219:
            r13 = r13 | 48
            r4.setGravity(r13)
            android.widget.TextView r4 = r0.bottomTextView
            r13 = 2131629037(0x7f0e13ed, float:1.8885384E38)
            java.lang.String r15 = "YourEmailInfo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r4.setText(r13)
            android.widget.TextView r4 = r0.bottomTextView
            r24 = -2
            r25 = -2
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x0238
            r13 = 5
            goto L_0x0239
        L_0x0238:
            r13 = 3
        L_0x0239:
            r26 = r13 | 48
            r27 = 40
            r28 = 30
            r29 = 40
            r30 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r6.addView(r4, r13)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.resetWaitView = r4
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r4.setTextColor(r12)
            android.widget.TextView r4 = r0.resetWaitView
            r12 = 1094713344(0x41400000, float:12.0)
            r4.setTextSize(r7, r12)
            android.widget.TextView r4 = r0.resetWaitView
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x0267
            r15 = 5
            goto L_0x0268
        L_0x0267:
            r15 = 3
        L_0x0268:
            r12 = r15 | 48
            r4.setGravity(r12)
            android.widget.TextView r4 = r0.resetWaitView
            r17 = -1
            r18 = -2
            r19 = 1109393408(0x42200000, float:40.0)
            r20 = 1090519040(0x41000000, float:8.0)
            r21 = 1109393408(0x42200000, float:40.0)
            r22 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r6.addView(r4, r12)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r4.setOrientation(r7)
            r12 = 80
            r4.setGravity(r12)
            r4.setClipChildren(r3)
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r3, (float) r10)
            r6.addView(r4, r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = new org.telegram.ui.ActionBar.SimpleTextView
            r4.<init>(r1)
            r0.bottomButton = r4
            r6 = 15
            r4.setTextSize(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.bottomButton
            r6 = 19
            r4.setGravity(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.bottomButton
            r6 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r10, r3, r12, r3)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.bottomButton
            int r10 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r10 < r15) goto L_0x02c6
            r18 = 1113587712(0x42600000, float:56.0)
            goto L_0x02c8
        L_0x02c6:
            r18 = 1114636288(0x42700000, float:60.0)
        L_0x02c8:
            r19 = 80
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r4, r12)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.bottomButton
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda8 r12 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda8
            r12.<init>(r0)
            r4.setOnClickListener(r12)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.bottomButton
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.cancelResetButton = r4
            r4.setTextSize(r7, r14)
            android.widget.TextView r4 = r0.cancelResetButton
            r12 = 19
            r4.setGravity(r12)
            android.widget.TextView r4 = r0.cancelResetButton
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r12, r3, r6, r3)
            android.widget.TextView r4 = r0.cancelResetButton
            r6 = 2131624770(0x7f0e0342, float:1.887673E38)
            java.lang.String r12 = "CancelReset"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r4.setText(r6)
            android.widget.TextView r4 = r0.cancelResetButton
            java.lang.String r6 = "windowBackgroundWhiteBlueText4"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setTextColor(r6)
            android.widget.TextView r4 = r0.cancelResetButton
            r6 = 8
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.cancelResetButton
            r17 = -1
            if (r10 < r15) goto L_0x032f
            r18 = 1113587712(0x42600000, float:56.0)
            goto L_0x0331
        L_0x032f:
            r18 = 1114636288(0x42700000, float:60.0)
        L_0x0331:
            r19 = 80
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r4, r6)
            android.widget.TextView r4 = r0.cancelResetButton
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda7 r6 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda7
            r6.<init>(r0)
            r4.setOnClickListener(r6)
            android.widget.TextView r4 = r0.cancelResetButton
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.floatingButtonContainer = r4
            if (r10 < r15) goto L_0x03c3
            android.animation.StateListAnimator r4 = new android.animation.StateListAnimator
            r4.<init>()
            int[] r6 = new int[r7]
            r12 = 16842919(0x10100a7, float:2.3694026E-38)
            r6[r3] = r12
            org.telegram.ui.Components.TransformableLoginButtonView r12 = r0.floatingButtonIcon
            r14 = 2
            float[] r14 = new float[r14]
            r17 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r9 = (float) r9
            r14[r3] = r9
            r9 = 1082130432(0x40800000, float:4.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r14[r7] = r9
            java.lang.String r9 = "translationZ"
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r12, r9, r14)
            r13 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r9 = r9.setDuration(r13)
            r4.addState(r6, r9)
            int[] r6 = new int[r3]
            org.telegram.ui.Components.TransformableLoginButtonView r9 = r0.floatingButtonIcon
            r13 = 2
            float[] r13 = new float[r13]
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r13[r3] = r14
            r14 = 1073741824(0x40000000, float:2.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r13[r7] = r14
            java.lang.String r14 = "translationZ"
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r9, r14, r13)
            r13 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r9 = r9.setDuration(r13)
            r4.addState(r6, r9)
            android.widget.FrameLayout r6 = r0.floatingButtonContainer
            r6.setStateListAnimator(r4)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.TwoStepVerificationActivity$3 r6 = new org.telegram.ui.TwoStepVerificationActivity$3
            r6.<init>(r0)
            r4.setOutlineProvider(r6)
        L_0x03c3:
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r4)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda6
            r6.<init>(r0)
            r4.setOnClickListener(r6)
            org.telegram.ui.Components.TransformableLoginButtonView r4 = new org.telegram.ui.Components.TransformableLoginButtonView
            r4.<init>(r1)
            r0.floatingButtonIcon = r4
            r4.setTransformType(r7)
            org.telegram.ui.Components.TransformableLoginButtonView r4 = r0.floatingButtonIcon
            r6 = 0
            r4.setProgress(r6)
            org.telegram.ui.Components.TransformableLoginButtonView r4 = r0.floatingButtonIcon
            java.lang.String r6 = "chats_actionIcon"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setColor(r6)
            org.telegram.ui.Components.TransformableLoginButtonView r4 = r0.floatingButtonIcon
            r4.setDrawBackground(r3)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            r6 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString((int) r6)
            r4.setContentDescription(r6)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            org.telegram.ui.Components.TransformableLoginButtonView r6 = r0.floatingButtonIcon
            if (r10 < r15) goto L_0x0407
            r9 = 56
            goto L_0x0409
        L_0x0407:
            r9 = 60
        L_0x0409:
            if (r10 < r15) goto L_0x040e
            r13 = 1113587712(0x42600000, float:56.0)
            goto L_0x0410
        L_0x040e:
            r13 = 1114636288(0x42700000, float:60.0)
        L_0x0410:
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r13)
            r4.addView(r6, r9)
            r4 = 1113587712(0x42600000, float:56.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r4 = "chats_actionBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r9 = "chats_actionPressedBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r6, r4, r9)
            if (r10 >= r15) goto L_0x045e
            android.content.res.Resources r6 = r33.getResources()
            r9 = 2131165445(0x7var_, float:1.7945107E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r9)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r13, r14)
            r6.setColorFilter(r9)
            org.telegram.ui.Components.CombinedDrawable r9 = new org.telegram.ui.Components.CombinedDrawable
            r9.<init>(r6, r4, r3, r3)
            r6 = 1113587712(0x42600000, float:56.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9.setIconSize(r4, r12)
            r4 = r9
            goto L_0x0460
        L_0x045e:
            r6 = 1113587712(0x42600000, float:56.0)
        L_0x0460:
            android.widget.FrameLayout r9 = r0.floatingButtonContainer
            r9.setBackground(r4)
            android.widget.FrameLayout r4 = r0.floatingButtonContainer
            if (r10 < r15) goto L_0x046e
            r9 = 56
            r25 = 56
            goto L_0x0472
        L_0x046e:
            r9 = 60
            r25 = 60
        L_0x0472:
            if (r10 < r15) goto L_0x0477
            r26 = 1113587712(0x42600000, float:56.0)
            goto L_0x0479
        L_0x0477:
            r26 = 1114636288(0x42700000, float:60.0)
        L_0x0479:
            r27 = 85
            r28 = 0
            r29 = 0
            r30 = 1103101952(0x41CLASSNAME, float:24.0)
            r31 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r4, r6)
            org.telegram.ui.Components.EmptyTextProgressView r4 = new org.telegram.ui.Components.EmptyTextProgressView
            r4.<init>(r1)
            r0.emptyView = r4
            r4.showProgress()
            org.telegram.ui.Components.EmptyTextProgressView r4 = r0.emptyView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6)
            r2.addView(r4, r9)
            org.telegram.ui.Components.RecyclerListView r4 = new org.telegram.ui.Components.RecyclerListView
            r4.<init>(r1)
            r0.listView = r4
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            r6.<init>(r1, r7, r3)
            r4.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            org.telegram.ui.Components.EmptyTextProgressView r6 = r0.emptyView
            r4.setEmptyView(r6)
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r4.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6)
            r2.addView(r4, r6)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            org.telegram.ui.TwoStepVerificationActivity$ListAdapter r4 = new org.telegram.ui.TwoStepVerificationActivity$ListAdapter
            r4.<init>(r1)
            r0.listAdapter = r4
            r2.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda38 r4 = new org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda38
            r4.<init>(r0)
            r2.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.TwoStepVerificationActivity$4 r2 = new org.telegram.ui.TwoStepVerificationActivity$4
            r2.<init>(r0, r1)
            r0.radialProgressView = r2
            r1 = 1101004800(0x41a00000, float:20.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r1 = r0.radialProgressView
            r2 = 0
            r1.setAlpha(r2)
            org.telegram.ui.Components.RadialProgressView r1 = r0.radialProgressView
            r2 = 1036831949(0x3dcccccd, float:0.1)
            r1.setScaleX(r2)
            org.telegram.ui.Components.RadialProgressView r1 = r0.radialProgressView
            r1.setScaleY(r2)
            org.telegram.ui.Components.RadialProgressView r1 = r0.radialProgressView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r1.setProgressColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.Components.RadialProgressView r2 = r0.radialProgressView
            r6 = 32
            r7 = 1107296256(0x42000000, float:32.0)
            r8 = 21
            r9 = 0
            r10 = 0
            r11 = 1094713344(0x41400000, float:12.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r1.addView(r2, r4)
            r32.updateRows()
            boolean r1 = r0.passwordEntered
            if (r1 == 0) goto L_0x0533
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131628468(0x7f0e11b4, float:1.888423E38)
            java.lang.String r4 = "TwoStepVerificationTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setTitle(r2)
            goto L_0x0539
        L_0x0533:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 0
            r1.setTitle(r2)
        L_0x0539:
            org.telegram.ui.TwoStepVerificationActivity$TwoStepVerificationActivityDelegate r1 = r0.delegate
            if (r1 == 0) goto L_0x054c
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131627387(0x7f0e0d7b, float:1.8882037E38)
            java.lang.String r3 = "PleaseEnterCurrentPasswordTransfer"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x0569
        L_0x054c:
            android.widget.TextView r1 = r0.titleTextView
            r2 = 2131629047(0x7f0e13f7, float:1.8885404E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
            r1.setText(r2)
            android.widget.TextView r1 = r0.subtitleTextView
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.subtitleTextView
            r2 = 2131626343(0x7f0e0967, float:1.887992E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
            r1.setText(r2)
        L_0x0569:
            boolean r1 = r0.passwordEntered
            if (r1 == 0) goto L_0x0580
            android.view.View r1 = r0.fragmentView
            java.lang.String r2 = "windowBackgroundGray"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            android.view.View r1 = r0.fragmentView
            java.lang.String r2 = "windowBackgroundGray"
            r1.setTag(r2)
            goto L_0x058e
        L_0x0580:
            android.view.View r1 = r0.fragmentView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setBackgroundColor(r2)
            android.view.View r1 = r0.fragmentView
            r1.setTag(r5)
        L_0x058e:
            android.view.View r1 = r0.fragmentView
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, boolean z) {
        this.passwordOutlineView.animateSelection(z ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        onPasswordForgot();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
        cancelPasswordReset();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view, int i) {
        if (i == this.setPasswordRow || i == this.changePasswordRow) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(twoStepVerificationSetupActivity);
        } else if (i == this.setRecoveryEmailRow || i == this.changeRecoveryEmailRow) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity2 = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            twoStepVerificationSetupActivity2.addFragmentToClose(this);
            twoStepVerificationSetupActivity2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(twoStepVerificationSetupActivity2);
        } else if (i == this.turnPasswordOffRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
            if (this.currentPassword.has_secure_values) {
                string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
            }
            String string2 = LocaleController.getString("TurnPasswordOffQuestionTitle", NUM);
            String string3 = LocaleController.getString("Disable", NUM);
            builder.setMessage(string);
            builder.setTitle(string2);
            builder.setPositiveButton(string3, new TwoStepVerificationActivity$$ExternalSyntheticLambda3(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        clearPassword();
    }

    public void onConfigurationChanged(Configuration configuration) {
        int i;
        super.onConfigurationChanged(configuration);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (!AndroidUtilities.isSmallScreen()) {
            Point point = AndroidUtilities.displaySize;
            if (point.x <= point.y) {
                i = 0;
                rLottieImageView.setVisibility(i);
            }
        }
        i = 8;
        rLottieImageView.setVisibility(i);
    }

    private void cancelPasswordReset() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("CancelPasswordResetYes", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("CancelPasswordResetNo", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("CancelReset", NUM));
            builder.setMessage(LocaleController.getString("CancelPasswordReset", NUM));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$10(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC$TL_account_declinePasswordReset(), new TwoStepVerificationActivity$$ExternalSyntheticLambda30(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda16(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$8(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    private void resetPassword() {
        needShowProgress(true);
        getConnectionsManager().sendRequest(new TLRPC$TL_account_resetPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda29(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$13(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda15(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$12(TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLObject instanceof TLRPC$TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("ResetPassword", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordOk", NUM));
            showDialog(builder.create(), new TwoStepVerificationActivity$$ExternalSyntheticLambda5(this));
        } else if (tLObject instanceof TLRPC$TL_account_resetPasswordRequestedWait) {
            this.currentPassword.pending_reset_date = ((TLRPC$TL_account_resetPasswordRequestedWait) tLObject).until_date;
            updateBottomButton();
        } else if (tLObject instanceof TLRPC$TL_account_resetPasswordFailedWait) {
            int currentTime = ((TLRPC$TL_account_resetPasswordFailedWait) tLObject).retry_date - getConnectionsManager().getCurrentTime();
            if (currentTime > 86400) {
                str = LocaleController.formatPluralString("Days", currentTime / 86400);
            } else if (currentTime > 3600) {
                str = LocaleController.formatPluralString("Hours", currentTime / 86400);
            } else if (currentTime > 60) {
                str = LocaleController.formatPluralString("Minutes", currentTime / 60);
            } else {
                str = LocaleController.formatPluralString("Seconds", Math.max(1, currentTime));
            }
            showAlertWithText(LocaleController.getString("ResetPassword", NUM), LocaleController.formatString("ResetPasswordWait", NUM, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$11(DialogInterface dialogInterface) {
        getNotificationCenter().postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void updateBottomButton() {
        int i;
        String str;
        if (!this.passwordEntered) {
            if (this.currentPassword.pending_reset_date == 0 || getConnectionsManager().getCurrentTime() > (i = this.currentPassword.pending_reset_date)) {
                if (this.resetWaitView.getVisibility() != 8) {
                    this.resetWaitView.setVisibility(8);
                }
                if (this.currentPassword.pending_reset_date == 0) {
                    this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
                    this.cancelResetButton.setVisibility(8);
                    this.bottomButton.setVisibility(0);
                } else {
                    this.bottomButton.setText(LocaleController.getString("ResetPassword", NUM));
                    this.cancelResetButton.setVisibility(0);
                    this.bottomButton.setVisibility(0);
                }
                this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            } else {
                int max = Math.max(1, i - getConnectionsManager().getCurrentTime());
                if (max > 86400) {
                    str = LocaleController.formatPluralString("Days", max / 86400);
                } else if (max >= 3600) {
                    str = LocaleController.formatPluralString("Hours", max / 3600);
                } else {
                    str = String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(max / 60), Integer.valueOf(max % 60)});
                }
                this.resetWaitView.setText(LocaleController.formatString("RestorePasswordResetIn", NUM, str));
                this.resetWaitView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                if (this.bottomButton.getVisibility() != 8) {
                    this.bottomButton.setVisibility(8);
                }
                if (this.resetWaitView.getVisibility() != 0) {
                    this.resetWaitView.setVisibility(0);
                }
                this.cancelResetButton.setVisibility(0);
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
                AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 1000);
            }
            if (this.currentPassword == null || this.bottomButton == null || this.resetWaitView.getVisibility() != 0) {
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
                TextView textView = this.cancelResetButton;
                if (textView != null) {
                    textView.setVisibility(8);
                }
            }
        }
    }

    private void onPasswordForgot() {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (tLRPC$TL_account_password.pending_reset_date == 0 && tLRPC$TL_account_password.has_recovery) {
            needShowProgress(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$ExternalSyntheticLambda31(this), 10);
        } else if (getParentActivity() != null) {
            if (this.currentPassword.pending_reset_date == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda4(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText2", NUM));
                showDialog(builder.create());
            } else if (getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda2(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder2.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder2.setMessage(LocaleController.getString("RestorePasswordResetPasswordText", NUM));
                AlertDialog create = builder2.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else {
                cancelPasswordReset();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$15(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda21(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$14(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
            tLRPC$TL_account_password.email_unconfirmed_pattern = ((TLRPC$TL_auth_passwordRecovery) tLObject).email_pattern;
            AnonymousClass5 r8 = new TwoStepVerificationSetupActivity(this.currentAccount, 4, tLRPC$TL_account_password) {
                /* access modifiers changed from: protected */
                public void onReset() {
                    boolean unused = TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            r8.addFragmentToClose(this);
            r8.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(r8);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$16(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$17(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.twoStepPasswordChanged) {
            if (!(objArr == null || objArr.length <= 0 || objArr[0] == null)) {
                this.currentPasswordHash = objArr[0];
            }
            loadPasswordInfo(false, false);
            updateRows();
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setCurrentPasswordInfo(byte[] bArr, TLRPC$TL_account_password tLRPC$TL_account_password) {
        if (bArr != null) {
            this.currentPasswordHash = bArr;
        }
        this.currentPassword = tLRPC$TL_account_password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public static boolean canHandleCurrentPassword(TLRPC$TL_account_password tLRPC$TL_account_password, boolean z) {
        if (z) {
            if (tLRPC$TL_account_password.current_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((tLRPC$TL_account_password.new_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) || (tLRPC$TL_account_password.current_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) || (tLRPC$TL_account_password.new_secure_algo instanceof TLRPC$TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC$TL_account_password tLRPC$TL_account_password) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.new_algo;
        if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo;
            byte[] bArr = new byte[(tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1.length + 32)];
            Utilities.random.nextBytes(bArr);
            byte[] bArr2 = tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1;
            System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
            tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = bArr;
        }
        TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = tLRPC$TL_account_password.new_secure_algo;
        if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo;
            byte[] bArr3 = new byte[(tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt.length + 32)];
            Utilities.random.nextBytes(bArr3);
            byte[] bArr4 = tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt;
            System.arraycopy(bArr4, 0, bArr3, 0, bArr4.length);
            tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt = bArr3;
        }
    }

    private void loadPasswordInfo(boolean z, boolean z2) {
        if (!z2) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda35(this, z2, z), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$19(boolean z, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda24(this, tLRPC$TL_error, tLObject, z, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$18(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z, boolean z2) {
        if (tLRPC$TL_error == null) {
            this.loading = false;
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            if (!canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (!z || z2) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        updateRows();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (!z) {
            return;
        }
        if (this.forgotPasswordOnShow) {
            onPasswordForgot();
            this.forgotPasswordOnShow = false;
        } else if (this.resetPasswordOnShow) {
            resetPassword();
            this.resetPasswordOnShow = false;
        }
    }

    private void updateRows() {
        TLRPC$TL_account_password tLRPC$TL_account_password;
        StringBuilder sb = new StringBuilder();
        sb.append(this.setPasswordRow);
        sb.append(this.setPasswordDetailRow);
        sb.append(this.changePasswordRow);
        sb.append(this.turnPasswordOffRow);
        sb.append(this.setRecoveryEmailRow);
        sb.append(this.changeRecoveryEmailRow);
        sb.append(this.passwordEnabledDetailRow);
        sb.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (tLRPC$TL_account_password = this.currentPassword) != null && this.passwordEntered) {
            if (tLRPC$TL_account_password.has_password) {
                int i = 0 + 1;
                this.rowCount = i;
                this.changePasswordRow = 0;
                int i2 = i + 1;
                this.rowCount = i2;
                this.turnPasswordOffRow = i;
                if (tLRPC$TL_account_password.has_recovery) {
                    this.rowCount = i2 + 1;
                    this.changeRecoveryEmailRow = i2;
                } else {
                    this.rowCount = i2 + 1;
                    this.setRecoveryEmailRow = i2;
                }
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.passwordEnabledDetailRow = i3;
            } else {
                int i4 = 0 + 1;
                this.rowCount = i4;
                this.setPasswordRow = 0;
                this.rowCount = i4 + 1;
                this.setPasswordDetailRow = i4;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.setPasswordRow);
        sb2.append(this.setPasswordDetailRow);
        sb2.append(this.changePasswordRow);
        sb2.append(this.turnPasswordOffRow);
        sb2.append(this.setRecoveryEmailRow);
        sb2.append(this.changeRecoveryEmailRow);
        sb2.append(this.passwordEnabledDetailRow);
        sb2.append(this.rowCount);
        if (this.listAdapter != null && !sb.toString().equals(sb2.toString())) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.fragmentView == null) {
            return;
        }
        if (this.loading || this.passwordEntered) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.passwordEditText != null) {
                this.floatingButtonContainer.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(8);
                this.bottomButton.setVisibility(4);
                updateBottomButton();
            }
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
            return;
        }
        RecyclerListView recyclerListView2 = this.listView;
        if (recyclerListView2 != null) {
            recyclerListView2.setEmptyView((View) null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.emptyView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.floatingButtonContainer.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            updateBottomButton();
            this.bottomTextView.setVisibility(8);
            if (!TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(this.currentPassword.hint);
            } else {
                this.passwordEditText.setHint((CharSequence) null);
            }
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda14(this), 200);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRows$20() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean z) {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            if (!this.passwordEntered) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f})});
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.start();
                return;
            }
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            if (z) {
                this.progressDialog.showDelayed(300);
            } else {
                this.progressDialog.show();
            }
        }
    }

    public void needHideProgress() {
        if (!this.passwordEntered) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f})});
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.start();
            return;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    private void showAlertWithText(String str, String str2) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    private void clearPassword() {
        TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
        }
        tLRPC$TL_account_updatePasswordSettings.new_settings = new TLRPC$TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = tLRPC$TL_account_updatePasswordSettings.new_settings;
        tLRPC$TL_account_passwordInputSettings.flags = 3;
        tLRPC$TL_account_passwordInputSettings.hint = "";
        tLRPC$TL_account_passwordInputSettings.new_password_hash = new byte[0];
        tLRPC$TL_account_passwordInputSettings.new_algo = new TLRPC$TL_passwordKdfAlgoUnknown();
        tLRPC$TL_account_updatePasswordSettings.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda17(this, tLRPC$TL_account_updatePasswordSettings));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$27(TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        if (tLRPC$TL_account_updatePasswordSettings.password == null) {
            if (this.currentPassword.current_algo == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda32(this), 8);
                return;
            }
            tLRPC$TL_account_updatePasswordSettings.password = getNewSrpPassword();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, new TwoStepVerificationActivity$$ExternalSyntheticLambda28(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$22(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda22(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$21(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$26(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda19(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$25(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        if (tLRPC$TL_error == null || !"SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            needHideProgress();
            if (tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
                finishFragment();
            } else if (tLRPC$TL_error == null) {
            } else {
                if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
                    return;
                }
                showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda34(this), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$24(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda23(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$23(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public TLRPC$TL_inputCheckPasswordSRP getNewSrpPassword() {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        if (!(tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
    }

    private boolean checkSecretValues(byte[] bArr, TLRPC$TL_account_passwordSettings tLRPC$TL_account_passwordSettings) {
        byte[] bArr2;
        TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings = tLRPC$TL_account_passwordSettings.secure_settings;
        if (tLRPC$TL_secureSecretSettings != null) {
            this.currentSecret = tLRPC$TL_secureSecretSettings.secure_secret;
            TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = tLRPC$TL_secureSecretSettings.secure_algo;
            if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                bArr2 = Utilities.computePBKDF2(bArr, ((TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo).salt);
            } else if (!(tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                byte[] bArr3 = ((TLRPC$TL_securePasswordKdfAlgoSHA512) tLRPC$SecurePasswordKdfAlgo).salt;
                bArr2 = Utilities.computeSHA512(bArr3, bArr, bArr3);
            }
            this.currentSecretId = tLRPC$TL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] bArr4 = new byte[32];
            System.arraycopy(bArr2, 0, bArr4, 0, 32);
            byte[] bArr5 = new byte[16];
            System.arraycopy(bArr2, 32, bArr5, 0, 16);
            byte[] bArr6 = this.currentSecret;
            Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, bArr6.length, 0, 0);
            TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings2 = tLRPC$TL_account_passwordSettings.secure_settings;
            if (PassportActivity.checkSecret(tLRPC$TL_secureSecretSettings2.secure_secret, Long.valueOf(tLRPC$TL_secureSecretSettings2.secure_secret_id))) {
                return true;
            }
            TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
            tLRPC$TL_account_updatePasswordSettings.password = getNewSrpPassword();
            TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = new TLRPC$TL_account_passwordInputSettings();
            tLRPC$TL_account_updatePasswordSettings.new_settings = tLRPC$TL_account_passwordInputSettings;
            tLRPC$TL_account_passwordInputSettings.new_secure_settings = new TLRPC$TL_secureSecretSettings();
            TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings3 = tLRPC$TL_account_updatePasswordSettings.new_settings.new_secure_settings;
            tLRPC$TL_secureSecretSettings3.secure_secret = new byte[0];
            tLRPC$TL_secureSecretSettings3.secure_algo = new TLRPC$TL_securePasswordKdfAlgoUnknown();
            TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings2 = tLRPC$TL_account_updatePasswordSettings.new_settings;
            tLRPC$TL_account_passwordInputSettings2.new_secure_settings.secure_secret_id = 0;
            tLRPC$TL_account_passwordInputSettings2.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, TwoStepVerificationActivity$$ExternalSyntheticLambda37.INSTANCE);
            this.currentSecret = null;
            this.currentSecretId = 0;
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0;
        return true;
    }

    private void processDone() {
        if (!this.passwordEntered) {
            String obj = this.passwordEditText.getText().toString();
            if (obj.length() == 0) {
                onFieldError(this.passwordOutlineView, this.passwordEditText, false);
                return;
            }
            byte[] stringBytes = AndroidUtilities.getStringBytes(obj);
            needShowProgress();
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda26(this, stringBytes));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$35(byte[] bArr) {
        TLRPC$TL_account_getPasswordSettings tLRPC$TL_account_getPasswordSettings = new TLRPC$TL_account_getPasswordSettings();
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TwoStepVerificationActivity$$ExternalSyntheticLambda36 twoStepVerificationActivity$$ExternalSyntheticLambda36 = new TwoStepVerificationActivity$$ExternalSyntheticLambda36(this, bArr, x);
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getPasswordSettings.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                twoStepVerificationActivity$$ExternalSyntheticLambda36.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPasswordSettings, twoStepVerificationActivity$$ExternalSyntheticLambda36, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        twoStepVerificationActivity$$ExternalSyntheticLambda36.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$34(byte[] bArr, byte[] bArr2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda27(this, bArr, tLObject, bArr2));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda18(this, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$30(byte[] bArr, TLObject tLObject, byte[] bArr2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda25(this, checkSecretValues(bArr, (TLRPC$TL_account_passwordSettings) tLObject), bArr2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$29(boolean z, byte[] bArr) {
        if (this.delegate == null || !z) {
            needHideProgress();
        }
        if (z) {
            this.currentPasswordHash = bArr;
            this.passwordEntered = true;
            if (this.delegate != null) {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                this.delegate.didEnterPassword(getNewSrpPassword());
            } else if (!TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
                twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
                presentFragment(twoStepVerificationSetupActivity, true);
            } else {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                twoStepVerificationActivity.passwordEntered = true;
                twoStepVerificationActivity.currentPasswordHash = this.currentPasswordHash;
                twoStepVerificationActivity.currentPassword = this.currentPassword;
                twoStepVerificationActivity.currentSecret = this.currentSecret;
                twoStepVerificationActivity.currentSecretId = this.currentSecretId;
                presentFragment(twoStepVerificationActivity, true);
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$33(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if ("SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda33(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tLRPC$TL_error.text)) {
            onFieldError(this.passwordOutlineView, this.passwordEditText, true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$32(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda20(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$31(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
    }

    private void onFieldError(OutlineTextContainerView outlineTextContainerView, TextView textView, boolean z) {
        if (getParentActivity() != null) {
            try {
                textView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                textView.setText("");
            }
            outlineTextContainerView.animateError(1.0f);
            AndroidUtilities.shakeViewSpring(outlineTextContainerView, 5.0f, new TwoStepVerificationActivity$$ExternalSyntheticLambda11(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFieldError$36() {
        AndroidUtilities.cancelRunOnUIThread(this.errorColorTimeout);
        AndroidUtilities.runOnUIThread(this.errorColorTimeout, 1500);
        this.postedErrorColorTimeout = true;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("SetAdditionalPassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textSettingsCell.setText(LocaleController.getString("TurnPasswordOff", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangeRecoveryEmail", NUM), false);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            }
        }

        public int getItemViewType(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? 1 : 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return arrayList;
    }

    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0) {
            return super.onBackPressed();
        }
        showSetForcePasswordAlert();
        return false;
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", NUM), (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda1(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSetForcePasswordAlert$37(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public void setBlockingAlert(int i) {
        this.otherwiseReloginDays = i;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("afterSignup", true);
            presentFragment(new DialogsActivity(bundle), true);
            return;
        }
        super.finishFragment();
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
