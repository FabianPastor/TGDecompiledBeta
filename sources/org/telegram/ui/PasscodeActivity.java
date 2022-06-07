package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int autoLockDetailRow;
    /* access modifiers changed from: private */
    public int autoLockRow;
    /* access modifiers changed from: private */
    public int captureDetailRow;
    /* access modifiers changed from: private */
    public int captureHeaderRow;
    /* access modifiers changed from: private */
    public int captureRow;
    /* access modifiers changed from: private */
    public int changePasscodeRow;
    /* access modifiers changed from: private */
    public CodeFieldContainer codeFieldContainer;
    /* access modifiers changed from: private */
    public int currentPasswordType = 0;
    private TextViewSwitcher descriptionTextSwitcher;
    /* access modifiers changed from: private */
    public int disablePasscodeRow;
    /* access modifiers changed from: private */
    public int fingerprintRow;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    /* access modifiers changed from: private */
    public Animator floatingButtonAnimator;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    /* access modifiers changed from: private */
    public Runnable hidePasscodesDoNotMatch = new PasscodeActivity$$ExternalSyntheticLambda14(this);
    /* access modifiers changed from: private */
    public int hintRow;
    /* access modifiers changed from: private */
    public CustomPhoneKeyboardView keyboardView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView lockImageView;
    private Runnable onShowKeyboardCallback;
    private ActionBarMenuItem otherItem;
    private OutlineTextContainerView outlinePasswordView;
    /* access modifiers changed from: private */
    public int passcodeSetStep = 0;
    private TextView passcodesDoNotMatchTextView;
    /* access modifiers changed from: private */
    public ImageView passwordButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public boolean postedHidePasscodesDoNotMatch;
    /* access modifiers changed from: private */
    public int rowCount;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int type;
    /* access modifiers changed from: private */
    public int utyanRow;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.postedHidePasscodesDoNotMatch = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false);
    }

    public PasscodeActivity(int i) {
        this.type = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0207  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0227  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x02c5  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x02d2  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x02db  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x036d  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x03f8 A[LOOP:0: B:60:0x03f6->B:61:0x03f8, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x044f  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x04d2  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x04d7  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x052f  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0536  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0539  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0558  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r30) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131165449(0x7var_, float:1.7945115E38)
            r2.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r4 = 0
            r2.setAllowOverlayTitle(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.PasscodeActivity$1 r5 = new org.telegram.ui.PasscodeActivity$1
            r5.<init>()
            r2.setActionBarMenuOnItemClick(r5)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            int r5 = r0.type
            r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r7 = -1
            r8 = 1
            if (r5 != 0) goto L_0x002b
            r5 = r2
            goto L_0x003a
        L_0x002b:
            android.widget.ScrollView r5 = new android.widget.ScrollView
            r5.<init>(r1)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6)
            r5.addView(r2, r9)
            r5.setFillViewport(r8)
        L_0x003a:
            org.telegram.ui.PasscodeActivity$2 r9 = new org.telegram.ui.PasscodeActivity$2
            r9.<init>(r1, r5)
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda23 r10 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda23
            r10.<init>(r0)
            r9.setDelegate(r10)
            r0.fragmentView = r9
            r10 = 1065353216(0x3var_, float:1.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r4, (float) r10)
            r9.addView(r5, r11)
            org.telegram.ui.Components.CustomPhoneKeyboardView r5 = new org.telegram.ui.Components.CustomPhoneKeyboardView
            r5.<init>(r1)
            r0.keyboardView = r5
            boolean r11 = r29.isCustomKeyboardVisible()
            if (r11 == 0) goto L_0x0061
            r11 = 0
            goto L_0x0063
        L_0x0061:
            r11 = 8
        L_0x0063:
            r5.setVisibility(r11)
            org.telegram.ui.Components.CustomPhoneKeyboardView r5 = r0.keyboardView
            r11 = 230(0xe6, float:3.22E-43)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r11)
            r9.addView(r5, r11)
            int r5 = r0.type
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r5 == 0) goto L_0x058d
            r13 = 2
            if (r5 == r8) goto L_0x007e
            if (r5 == r13) goto L_0x007e
            goto L_0x05e6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r14 = "windowBackgroundWhite"
            java.lang.String r15 = "windowBackgroundWhiteBlackText"
            if (r5 == 0) goto L_0x00d8
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r5.setBackgroundColor(r12)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r5.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setItemsColor(r5, r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r5 = "actionBarWhiteSelector"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setItemsBackgroundColor(r5, r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r3.setCastShadows(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r3.createMenu()
            int r5 = r0.type
            if (r5 != r8) goto L_0x00cd
            r5 = 2131165453(0x7var_d, float:1.7945124E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r3.addItem((int) r4, (int) r5)
            r0.otherItem = r3
            r5 = 2131165840(0x7var_, float:1.7945908E38)
            r12 = 2131627159(0x7f0e0CLASSNAME, float:1.8881575E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r8, r5, r12)
            goto L_0x00ce
        L_0x00cd:
            r3 = 0
        L_0x00ce:
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.PasscodeActivity$4 r12 = new org.telegram.ui.PasscodeActivity$4
            r12.<init>(r3)
            r5.setActionBarMenuOnItemClick(r12)
        L_0x00d8:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r5.setOrientation(r8)
            r5.setGravity(r8)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r2.addView(r5, r9)
            org.telegram.ui.Components.RLottieImageView r9 = new org.telegram.ui.Components.RLottieImageView
            r9.<init>(r1)
            r0.lockImageView = r9
            r9.setFocusable(r4)
            org.telegram.ui.Components.RLottieImageView r9 = r0.lockImageView
            r12 = 2131558542(0x7f0d008e, float:1.8742403E38)
            r6 = 120(0x78, float:1.68E-43)
            r9.setAnimation(r12, r6, r6)
            org.telegram.ui.Components.RLottieImageView r9 = r0.lockImageView
            r9.setAutoRepeat(r4)
            org.telegram.ui.Components.RLottieImageView r9 = r0.lockImageView
            r9.playAnimation()
            org.telegram.ui.Components.RLottieImageView r9 = r0.lockImageView
            boolean r12 = org.telegram.messenger.AndroidUtilities.isSmallScreen()
            if (r12 != 0) goto L_0x011f
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r12.x
            int r12 = r12.y
            if (r7 >= r12) goto L_0x011f
            r7 = 0
            goto L_0x0121
        L_0x011f:
            r7 = 8
        L_0x0121:
            r9.setVisibility(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.lockImageView
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r6, (int) r8)
            r5.addView(r7, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.titleTextView = r6
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r6.setTextColor(r7)
            android.widget.TextView r6 = r0.titleTextView
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r7)
            int r6 = r0.type
            if (r6 != r8) goto L_0x0170
            java.lang.String r6 = org.telegram.messenger.SharedConfig.passcodeHash
            int r6 = r6.length()
            if (r6 == 0) goto L_0x0161
            android.widget.TextView r6 = r0.titleTextView
            r7 = 2131625643(0x7f0e06ab, float:1.88785E38)
            java.lang.String r9 = "EnterNewPasscode"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setText(r7)
            goto L_0x017c
        L_0x0161:
            android.widget.TextView r6 = r0.titleTextView
            r7 = 2131625276(0x7f0e053c, float:1.8877755E38)
            java.lang.String r9 = "CreatePasscode"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setText(r7)
            goto L_0x017c
        L_0x0170:
            android.widget.TextView r6 = r0.titleTextView
            r7 = 2131625647(0x7f0e06af, float:1.8878508E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString((int) r7)
            r6.setText(r7)
        L_0x017c:
            android.widget.TextView r6 = r0.titleTextView
            r7 = 1099956224(0x41900000, float:18.0)
            r6.setTextSize(r8, r7)
            android.widget.TextView r6 = r0.titleTextView
            r6.setGravity(r8)
            android.widget.TextView r6 = r0.titleTextView
            r19 = -2
            r20 = -2
            r21 = 1
            r22 = 0
            r23 = 16
            r24 = 0
            r25 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r5.addView(r6, r9)
            org.telegram.ui.Components.TextViewSwitcher r6 = new org.telegram.ui.Components.TextViewSwitcher
            r6.<init>(r1)
            r0.descriptionTextSwitcher = r6
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda10 r9 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda10
            r9.<init>(r1)
            r6.setFactory(r9)
            org.telegram.ui.Components.TextViewSwitcher r6 = r0.descriptionTextSwitcher
            r9 = 2130771968(0x7var_, float:1.7147041E38)
            r6.setInAnimation(r1, r9)
            org.telegram.ui.Components.TextViewSwitcher r6 = r0.descriptionTextSwitcher
            r9 = 2130771969(0x7var_, float:1.7147043E38)
            r6.setOutAnimation(r1, r9)
            org.telegram.ui.Components.TextViewSwitcher r6 = r0.descriptionTextSwitcher
            r22 = 20
            r23 = 8
            r24 = 20
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r5.addView(r6, r9)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r8, r9)
            java.lang.String r12 = "featuredStickers_addButton"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setTextColor(r12)
            r12 = 1107296256(0x42000000, float:32.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.setPadding(r11, r4, r12, r4)
            boolean r11 = r29.isPassword()
            if (r11 == 0) goto L_0x01f4
            r11 = 3
            goto L_0x01f5
        L_0x01f4:
            r11 = 1
        L_0x01f5:
            r12 = 16
            r11 = r11 | r12
            r6.setGravity(r11)
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda4 r11 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda4
            r11.<init>(r1)
            r6.setOnClickListener(r11)
            int r11 = r0.type
            if (r11 != r13) goto L_0x0209
            r11 = 0
            goto L_0x020b
        L_0x0209:
            r11 = 8
        L_0x020b:
            r6.setVisibility(r11)
            r11 = 2131625929(0x7f0e07c9, float:1.887908E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString((int) r11)
            r6.setText(r11)
            r21 = -1
            int r11 = android.os.Build.VERSION.SDK_INT
            r16 = 1114636288(0x42700000, float:60.0)
            r28 = 1113587712(0x42600000, float:56.0)
            r13 = 21
            if (r11 < r13) goto L_0x0227
            r22 = 1113587712(0x42600000, float:56.0)
            goto L_0x0229
        L_0x0227:
            r22 = 1114636288(0x42700000, float:60.0)
        L_0x0229:
            r23 = 81
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r6, r11)
            org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.passcodesDoNotMatchTextView = r6
            r6.setTextSize(r8, r9)
            android.widget.TextView r6 = r0.passcodesDoNotMatchTextView
            java.lang.String r9 = "windowBackgroundWhiteGrayText6"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r9)
            android.widget.TextView r6 = r0.passcodesDoNotMatchTextView
            r9 = 2131627160(0x7f0e0CLASSNAME, float:1.8881577E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
            r6.setText(r9)
            android.widget.TextView r6 = r0.passcodesDoNotMatchTextView
            r9 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r11 = 1094713344(0x41400000, float:12.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r6.setPadding(r4, r9, r4, r11)
            android.widget.TextView r6 = r0.passcodesDoNotMatchTextView
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r6, r4, r10, r4)
            android.widget.TextView r6 = r0.passcodesDoNotMatchTextView
            r21 = -2
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r6, r9)
            org.telegram.ui.Components.OutlineTextContainerView r6 = new org.telegram.ui.Components.OutlineTextContainerView
            r6.<init>(r1)
            r0.outlinePasswordView = r6
            r9 = 2131625644(0x7f0e06ac, float:1.8878502E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString((int) r9)
            r6.setText(r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r1)
            r0.passwordEditText = r6
            r9 = 524417(0x80081, float:7.34865E-40)
            r6.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r6.setTextSize(r8, r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r6.setTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r7 = 0
            r6.setBackground(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r6.setMaxLines(r8)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r6.setLines(r8)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x02c5
            r7 = 5
            goto L_0x02c6
        L_0x02c5:
            r7 = 3
        L_0x02c6:
            r6.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r6.setSingleLine(r8)
            int r6 = r0.type
            if (r6 != r8) goto L_0x02db
            r0.passcodeSetStep = r4
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r7 = 5
            r6.setImeOptions(r7)
            goto L_0x02e3
        L_0x02db:
            r0.passcodeSetStep = r8
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r7 = 6
            r6.setImeOptions(r7)
        L_0x02e3:
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            android.text.method.PasswordTransformationMethod r7 = android.text.method.PasswordTransformationMethod.getInstance()
            r6.setTransformationMethod(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            android.graphics.Typeface r7 = android.graphics.Typeface.DEFAULT
            r6.setTypeface(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            java.lang.String r7 = "windowBackgroundWhiteInputFieldActivated"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setCursorColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setCursorSize(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            r7 = 1069547520(0x3fCLASSNAME, float:1.5)
            r6.setCursorWidth(r7)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            org.telegram.ui.Components.EditTextBoldCursor r7 = r0.passwordEditText
            r7.setPadding(r6, r6, r6, r6)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda7 r7 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda7
            r7.<init>(r0)
            r6.setOnFocusChangeListener(r7)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r6.setOrientation(r4)
            r6.setGravity(r12)
            org.telegram.ui.Components.EditTextBoldCursor r7 = r0.passwordEditText
            r9 = -2
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r9, (float) r10)
            r6.addView(r7, r9)
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r1)
            r0.passwordButton = r7
            r9 = 2131165800(0x7var_, float:1.7945827E38)
            r7.setImageResource(r9)
            android.widget.ImageView r7 = r0.passwordButton
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setColorFilter(r9)
            android.widget.ImageView r7 = r0.passwordButton
            java.lang.String r9 = "listSelectorSDK21"
            int r9 = r0.getThemedColor(r9)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r8)
            r7.setBackground(r9)
            android.widget.ImageView r7 = r0.passwordButton
            int r9 = r0.type
            if (r9 != r8) goto L_0x036d
            int r9 = r0.passcodeSetStep
            if (r9 != 0) goto L_0x036d
            r9 = 1
            goto L_0x036e
        L_0x036d:
            r9 = 0
        L_0x036e:
            r10 = 1036831949(0x3dcccccd, float:0.1)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r7, r9, r10, r4)
            java.util.concurrent.atomic.AtomicBoolean r7 = new java.util.concurrent.atomic.AtomicBoolean
            r7.<init>(r4)
            org.telegram.ui.Components.EditTextBoldCursor r9 = r0.passwordEditText
            org.telegram.ui.PasscodeActivity$5 r10 = new org.telegram.ui.PasscodeActivity$5
            r10.<init>(r7)
            r9.addTextChangedListener(r10)
            android.widget.ImageView r9 = r0.passwordButton
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda6 r10 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda6
            r10.<init>(r0, r7)
            r9.setOnClickListener(r10)
            android.widget.ImageView r7 = r0.passwordButton
            r19 = 1103101952(0x41CLASSNAME, float:24.0)
            r20 = 1103101952(0x41CLASSNAME, float:24.0)
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 1096810496(0x41600000, float:14.0)
            r25 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinearRelatively(r19, r20, r21, r22, r23, r24, r25)
            r6.addView(r7, r9)
            org.telegram.ui.Components.OutlineTextContainerView r7 = r0.outlinePasswordView
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)
            r7.addView(r6, r9)
            org.telegram.ui.Components.OutlineTextContainerView r6 = r0.outlinePasswordView
            r17 = -1
            r18 = -2
            r19 = 1
            r20 = 32
            r22 = 32
            r23 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r3.addView(r6, r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda9 r7 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda9
            r7.<init>(r0)
            r6.setOnEditorActionListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            org.telegram.ui.PasscodeActivity$6 r7 = new org.telegram.ui.PasscodeActivity$6
            r7.<init>()
            r6.addTextChangedListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.passwordEditText
            org.telegram.ui.PasscodeActivity$7 r7 = new org.telegram.ui.PasscodeActivity$7
            r7.<init>(r0)
            r6.setCustomSelectionActionModeCallback(r7)
            org.telegram.ui.PasscodeActivity$8 r6 = new org.telegram.ui.PasscodeActivity$8
            r6.<init>(r1)
            r0.codeFieldContainer = r6
            r7 = 4
            r9 = 10
            r6.setNumbersCount(r7, r9)
            org.telegram.ui.CodeFieldContainer r6 = r0.codeFieldContainer
            org.telegram.ui.CodeNumberField[] r6 = r6.codeField
            int r7 = r6.length
            r9 = 0
        L_0x03f6:
            if (r9 >= r7) goto L_0x0421
            r10 = r6[r9]
            boolean r11 = r29.isCustomKeyboardVisible()
            r11 = r11 ^ r8
            r10.setShowSoftInputOnFocusCompat(r11)
            android.text.method.PasswordTransformationMethod r11 = android.text.method.PasswordTransformationMethod.getInstance()
            r10.setTransformationMethod(r11)
            r11 = 1103101952(0x41CLASSNAME, float:24.0)
            r10.setTextSize(r8, r11)
            org.telegram.ui.PasscodeActivity$9 r11 = new org.telegram.ui.PasscodeActivity$9
            r11.<init>()
            r10.addTextChangedListener(r11)
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda8 r11 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda8
            r11.<init>(r0, r10)
            r10.setOnFocusChangeListener(r11)
            int r9 = r9 + 1
            goto L_0x03f6
        L_0x0421:
            org.telegram.ui.CodeFieldContainer r6 = r0.codeFieldContainer
            r17 = -2
            r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 1
            r20 = 1109393408(0x42200000, float:40.0)
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 1109393408(0x42200000, float:40.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r3.addView(r6, r7)
            r17 = -1
            r18 = -2
            r20 = 0
            r21 = 32
            r22 = 0
            r23 = 72
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r5.addView(r3, r6)
            int r3 = r0.type
            if (r3 != r8) goto L_0x0452
            r2.setTag(r14)
        L_0x0452:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.floatingButtonContainer = r3
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r13) goto L_0x04c6
            android.animation.StateListAnimator r5 = new android.animation.StateListAnimator
            r5.<init>()
            int[] r6 = new int[r8]
            r7 = 16842919(0x10100a7, float:2.3694026E-38)
            r6[r4] = r7
            org.telegram.ui.Components.TransformableLoginButtonView r7 = r0.floatingButtonIcon
            r9 = 2
            float[] r10 = new float[r9]
            r9 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r10[r4] = r9
            r9 = 1082130432(0x40800000, float:4.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r10[r8] = r9
            java.lang.String r9 = "translationZ"
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r10)
            r9 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r7 = r7.setDuration(r9)
            r5.addState(r6, r7)
            int[] r6 = new int[r4]
            org.telegram.ui.Components.TransformableLoginButtonView r7 = r0.floatingButtonIcon
            r9 = 2
            float[] r9 = new float[r9]
            r10 = 1082130432(0x40800000, float:4.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9[r4] = r10
            r10 = 1073741824(0x40000000, float:2.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r9[r8] = r10
            java.lang.String r10 = "translationZ"
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r10, r9)
            r9 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r7 = r7.setDuration(r9)
            r5.addState(r6, r7)
            android.widget.FrameLayout r6 = r0.floatingButtonContainer
            r6.setStateListAnimator(r5)
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            org.telegram.ui.PasscodeActivity$10 r6 = new org.telegram.ui.PasscodeActivity$10
            r6.<init>(r0)
            r5.setOutlineProvider(r6)
        L_0x04c6:
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            org.telegram.ui.Components.VerticalPositionAutoAnimator r5 = org.telegram.ui.Components.VerticalPositionAutoAnimator.attach(r5)
            r0.floatingAutoAnimator = r5
            android.widget.FrameLayout r5 = r0.floatingButtonContainer
            if (r3 < r13) goto L_0x04d7
            r6 = 56
            r17 = 56
            goto L_0x04db
        L_0x04d7:
            r6 = 60
            r17 = 60
        L_0x04db:
            if (r3 < r13) goto L_0x04e0
            r18 = 1113587712(0x42600000, float:56.0)
            goto L_0x04e2
        L_0x04e0:
            r18 = 1114636288(0x42700000, float:60.0)
        L_0x04e2:
            r19 = 85
            r20 = 0
            r21 = 0
            r22 = 1103101952(0x41CLASSNAME, float:24.0)
            r23 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r5, r6)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda5 r5 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda5
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            org.telegram.ui.Components.TransformableLoginButtonView r2 = new org.telegram.ui.Components.TransformableLoginButtonView
            r2.<init>(r1)
            r0.floatingButtonIcon = r2
            r2.setTransformType(r8)
            org.telegram.ui.Components.TransformableLoginButtonView r2 = r0.floatingButtonIcon
            r5 = 0
            r2.setProgress(r5)
            org.telegram.ui.Components.TransformableLoginButtonView r2 = r0.floatingButtonIcon
            java.lang.String r5 = "chats_actionIcon"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setColor(r5)
            org.telegram.ui.Components.TransformableLoginButtonView r2 = r0.floatingButtonIcon
            r2.setDrawBackground(r4)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            r5 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
            r2.setContentDescription(r5)
            android.widget.FrameLayout r2 = r0.floatingButtonContainer
            org.telegram.ui.Components.TransformableLoginButtonView r5 = r0.floatingButtonIcon
            if (r3 < r13) goto L_0x0532
            r6 = 56
            goto L_0x0534
        L_0x0532:
            r6 = 60
        L_0x0534:
            if (r3 < r13) goto L_0x0539
            r7 = 1113587712(0x42600000, float:56.0)
            goto L_0x053b
        L_0x0539:
            r7 = 1114636288(0x42700000, float:60.0)
        L_0x053b:
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7)
            r2.addView(r5, r6)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r28)
            java.lang.String r5 = "chats_actionBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            java.lang.String r6 = "chats_actionPressedBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r5, r6)
            if (r3 >= r13) goto L_0x0584
            android.content.res.Resources r1 = r30.getResources()
            r3 = 2131165414(0x7var_e6, float:1.7945044E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r6)
            r1.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            r3.<init>(r1, r2, r4, r4)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r28)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r28)
            r3.setIconSize(r1, r2)
            r2 = r3
        L_0x0584:
            android.widget.FrameLayout r1 = r0.floatingButtonContainer
            r1.setBackground(r2)
            r29.updateFields()
            goto L_0x05e6
        L_0x058d:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131627151(0x7f0e0c8f, float:1.8881558E38)
            java.lang.String r6 = "Passcode"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setTitle(r5)
            java.lang.String r3 = "windowBackgroundGray"
            r2.setTag(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setBackgroundColor(r3)
            org.telegram.ui.Components.RecyclerListView r3 = new org.telegram.ui.Components.RecyclerListView
            r3.<init>(r1)
            r0.listView = r3
            org.telegram.ui.PasscodeActivity$3 r5 = new org.telegram.ui.PasscodeActivity$3
            r5.<init>(r0, r1, r8, r4)
            r3.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r4 = 0
            r3.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setLayoutAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r9)
            r2.addView(r3, r4)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            org.telegram.ui.PasscodeActivity$ListAdapter r3 = new org.telegram.ui.PasscodeActivity$ListAdapter
            r3.<init>(r1)
            r0.listAdapter = r3
            r2.setAdapter(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda22 r2 = new org.telegram.ui.PasscodeActivity$$ExternalSyntheticLambda22
            r2.<init>(r0)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
        L_0x05e6:
            android.view.View r1 = r0.fragmentView
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PasscodeActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(int i, boolean z) {
        Runnable runnable;
        if (i >= AndroidUtilities.dp(20.0f) && (runnable = this.onShowKeyboardCallback) != null) {
            runnable.run();
            this.onShowKeyboardCallback = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view, int i) {
        if (view.isEnabled()) {
            if (i == this.disablePasscodeRow) {
                AlertDialog create = new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setPositiveButton(LocaleController.getString(NUM), new PasscodeActivity$$ExternalSyntheticLambda2(this)).create();
                create.show();
                ((TextView) create.getButton(-1)).setTextColor(Theme.getColor("dialogTextRed"));
            } else if (i == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (i == this.autoLockRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AutoLock", NUM));
                    NumberPicker numberPicker = new NumberPicker(getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(4);
                    int i2 = SharedConfig.autoLockIn;
                    if (i2 == 0) {
                        numberPicker.setValue(0);
                    } else if (i2 == 60) {
                        numberPicker.setValue(1);
                    } else if (i2 == 300) {
                        numberPicker.setValue(2);
                    } else if (i2 == 3600) {
                        numberPicker.setValue(3);
                    } else if (i2 == 18000) {
                        numberPicker.setValue(4);
                    }
                    numberPicker.setFormatter(PasscodeActivity$$ExternalSyntheticLambda21.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", NUM), new PasscodeActivity$$ExternalSyntheticLambda3(this, numberPicker, i));
                    showDialog(builder.create());
                }
            } else if (i == this.fingerprintRow) {
                SharedConfig.useFingerprint = !SharedConfig.useFingerprint;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (i == this.captureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, Boolean.FALSE);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", NUM));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DialogInterface dialogInterface, int i) {
        SharedConfig.passcodeHash = "";
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        getMediaDataController().buildShortcuts();
        int childCount = this.listView.getChildCount();
        int i2 = 0;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof TextSettingsCell) {
                ((TextSettingsCell) childAt).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                break;
            }
            i2++;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createView$3(int i) {
        if (i == 0) {
            return LocaleController.getString("AutoLockDisabled", NUM);
        }
        if (i == 1) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 1, new Object[0]));
        } else if (i == 2) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 5, new Object[0]));
        } else if (i == 3) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 1, new Object[0]));
        } else if (i != 4) {
            return "";
        } else {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 5, new Object[0]));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(NumberPicker numberPicker, int i, DialogInterface dialogInterface, int i2) {
        int value = numberPicker.getValue();
        if (value == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (value == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (value == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (value == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (value == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(i);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ View lambda$createView$6(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        textView.setGravity(1);
        textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        textView.setTextSize(1, 15.0f);
        return textView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view, boolean z) {
        this.outlinePasswordView.animateSelection(z ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(AtomicBoolean atomicBoolean, View view) {
        atomicBoolean.set(!atomicBoolean.get());
        int selectionStart = this.passwordEditText.getSelectionStart();
        int selectionEnd = this.passwordEditText.getSelectionEnd();
        this.passwordEditText.setInputType((atomicBoolean.get() ? 144 : 128) | 1);
        this.passwordEditText.setSelection(selectionStart, selectionEnd);
        this.passwordButton.setColorFilter(Theme.getColor(atomicBoolean.get() ? "windowBackgroundWhiteInputFieldActivated" : "windowBackgroundWhiteHintText"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$10(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        } else if (i2 != 1) {
            return false;
        } else {
            processDone();
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(CodeNumberField codeNumberField, View view, boolean z) {
        this.keyboardView.setEditText(codeNumberField);
        this.keyboardView.setDispatchBackWhenEmpty(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(View view) {
        int i = this.type;
        if (i == 1) {
            if (this.passcodeSetStep == 0) {
                processNext();
            } else {
                processDone();
            }
        } else if (i == 2) {
            processDone();
        }
    }

    public boolean hasForceLightStatusBar() {
        return this.type != 0;
    }

    private void setCustomKeyboardVisible(final boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
        int i = 0;
        float f = 1.0f;
        float f2 = 0.0f;
        if (!z2) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            if (!z) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
            CustomPhoneKeyboardView customPhoneKeyboardView2 = this.keyboardView;
            if (!z) {
                f = 0.0f;
            }
            customPhoneKeyboardView2.setAlpha(f);
            CustomPhoneKeyboardView customPhoneKeyboardView3 = this.keyboardView;
            if (!z) {
                f2 = (float) AndroidUtilities.dp(230.0f);
            }
            customPhoneKeyboardView3.setTranslationY(f2);
            this.fragmentView.requestLayout();
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(150);
        duration.setInterpolator(z ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
        duration.addUpdateListener(new PasscodeActivity$$ExternalSyntheticLambda0(this));
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (z) {
                    PasscodeActivity.this.keyboardView.setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PasscodeActivity.this.keyboardView.setVisibility(8);
                }
            }
        });
        duration.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCustomKeyboardVisible$13(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * ((float) AndroidUtilities.dp(230.0f)) * 0.75f);
        this.fragmentView.requestLayout();
    }

    private void setFloatingButtonVisible(final boolean z, boolean z2) {
        Animator animator = this.floatingButtonAnimator;
        if (animator != null) {
            animator.cancel();
            this.floatingButtonAnimator = null;
        }
        int i = 0;
        float f = 1.0f;
        if (!z2) {
            this.floatingAutoAnimator.setOffsetY(z ? 0.0f : (float) AndroidUtilities.dp(70.0f));
            FrameLayout frameLayout = this.floatingButtonContainer;
            if (!z) {
                f = 0.0f;
            }
            frameLayout.setAlpha(f);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (!z) {
                i = 8;
            }
            frameLayout2.setVisibility(i);
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(150);
        duration.setInterpolator(z ? AndroidUtilities.decelerateInterpolator : AndroidUtilities.accelerateInterpolator);
        duration.addUpdateListener(new PasscodeActivity$$ExternalSyntheticLambda1(this));
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (z) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (PasscodeActivity.this.floatingButtonAnimator == animator) {
                    Animator unused = PasscodeActivity.this.floatingButtonAnimator = null;
                }
            }
        });
        duration.start();
        this.floatingButtonAnimator = duration;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setFloatingButtonVisible$14(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(((float) AndroidUtilities.dp(70.0f)) * (1.0f - floatValue));
        this.floatingButtonContainer.setAlpha(floatValue);
    }

    public static BaseFragment determineOpenFragment() {
        if (SharedConfig.passcodeHash.length() != 0) {
            return new PasscodeActivity(2);
        }
        return new ActionIntroActivity(6);
    }

    private void animateSuccessAnimation(Runnable runnable) {
        if (!isPinCode()) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            CodeNumberField[] codeNumberFieldArr = codeFieldContainer2.codeField;
            if (i < codeNumberFieldArr.length) {
                CodeNumberField codeNumberField = codeNumberFieldArr[i];
                codeNumberField.postDelayed(new PasscodeActivity$$ExternalSyntheticLambda11(codeNumberField), ((long) i) * 75);
                i++;
            } else {
                codeFieldContainer2.postDelayed(new PasscodeActivity$$ExternalSyntheticLambda18(this, runnable), (((long) this.codeFieldContainer.codeField.length) * 75) + 350);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSuccessAnimation$16(Runnable runnable) {
        for (CodeNumberField animateSuccessProgress : this.codeFieldContainer.codeField) {
            animateSuccessProgress.animateSuccessProgress(0.0f);
        }
        runnable.run();
    }

    public void onConfigurationChanged(Configuration configuration) {
        int i;
        super.onConfigurationChanged(configuration);
        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (rLottieImageView != null) {
            if (!AndroidUtilities.isSmallScreen()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x < point.y) {
                    i = 0;
                    rLottieImageView.setVisibility(i);
                }
            }
            i = 8;
            rLottieImageView.setVisibility(i);
        }
        CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
        int length = codeNumberFieldArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            codeNumberFieldArr[i2].setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.type != 0 && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(new PasscodeActivity$$ExternalSyntheticLambda15(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.didSetPasscode) {
            return;
        }
        if ((objArr.length == 0 || objArr[0].booleanValue()) && this.type == 0) {
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.utyanRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.hintRow = i;
        this.rowCount = i2 + 1;
        this.changePasscodeRow = i2;
        try {
            if (Build.VERSION.SDK_INT < 23) {
                this.fingerprintRow = -1;
            } else if (!FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() || !AndroidUtilities.isKeyguardSecure()) {
                this.fingerprintRow = -1;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.fingerprintRow = i3;
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.autoLockRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.autoLockDetailRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.captureHeaderRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.captureRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.captureDetailRow = i8;
        this.rowCount = i9 + 1;
        this.disablePasscodeRow = i9;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type != 0) {
            showKeyboard();
        }
    }

    /* access modifiers changed from: private */
    public void showKeyboard() {
        if (isPinCode()) {
            this.codeFieldContainer.codeField[0].requestFocus();
            if (!isCustomKeyboardVisible()) {
                AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[0]);
            }
        } else if (isPassword()) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    /* access modifiers changed from: private */
    public void updateFields() {
        String str;
        int i = NUM;
        if (this.type == 2) {
            str = LocaleController.getString(NUM);
        } else if (this.passcodeSetStep == 0) {
            str = LocaleController.getString(this.currentPasswordType == 0 ? NUM : NUM);
        } else {
            str = this.descriptionTextSwitcher.getCurrentView().getText().toString();
        }
        boolean z = !this.descriptionTextSwitcher.getCurrentView().getText().equals(str) && !TextUtils.isEmpty(this.descriptionTextSwitcher.getCurrentView().getText());
        if (this.type == 2) {
            this.descriptionTextSwitcher.setText(LocaleController.getString(NUM), z);
        } else if (this.passcodeSetStep == 0) {
            TextViewSwitcher textViewSwitcher = this.descriptionTextSwitcher;
            if (this.currentPasswordType != 0) {
                i = NUM;
            }
            textViewSwitcher.setText(LocaleController.getString(i), z);
        }
        if (isPinCode()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, true, 1.0f, z);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, false, 1.0f, z);
        } else if (isPassword()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, false, 1.0f, z);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, true, 1.0f, z);
        }
        boolean isPassword = isPassword();
        if (isPassword) {
            PasscodeActivity$$ExternalSyntheticLambda20 passcodeActivity$$ExternalSyntheticLambda20 = new PasscodeActivity$$ExternalSyntheticLambda20(this, isPassword, z);
            this.onShowKeyboardCallback = passcodeActivity$$ExternalSyntheticLambda20;
            AndroidUtilities.runOnUIThread(passcodeActivity$$ExternalSyntheticLambda20, 3000);
        } else {
            setFloatingButtonVisible(isPassword, z);
        }
        setCustomKeyboardVisible(isCustomKeyboardVisible(), z);
        showKeyboard();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$17(boolean z, boolean z2) {
        setFloatingButtonVisible(z, z2);
        AndroidUtilities.cancelRunOnUIThread(this.onShowKeyboardCallback);
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        if (isPinCode() && this.type != 0 && !AndroidUtilities.isTablet()) {
            Point point = AndroidUtilities.displaySize;
            return point.x < point.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
        }
    }

    /* access modifiers changed from: private */
    public void processNext() {
        if (!(this.currentPasswordType == 1 && this.passwordEditText.getText().length() == 0) && (this.currentPasswordType != 0 || this.codeFieldContainer.getCode().length() == 4)) {
            ActionBarMenuItem actionBarMenuItem = this.otherItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            this.titleTextView.setText(LocaleController.getString("ConfirmCreatePasscode", NUM));
            this.descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString("PasscodeReinstallNotice", NUM)));
            this.firstPassword = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            this.passwordEditText.setText("");
            this.passwordEditText.setInputType(524417);
            for (CodeNumberField text : this.codeFieldContainer.codeField) {
                text.setText("");
            }
            showKeyboard();
            this.passcodeSetStep = 1;
            return;
        }
        onPasscodeError();
    }

    /* access modifiers changed from: private */
    public boolean isPinCode() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 0) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 0;
    }

    private boolean isPassword() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 1) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 1;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (!isPassword() || this.passwordEditText.getText().length() != 0) {
            String code = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            int i = this.type;
            int i2 = 0;
            if (i == 1) {
                if (!this.firstPassword.equals(code)) {
                    AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, true);
                    for (CodeNumberField text : this.codeFieldContainer.codeField) {
                        text.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    this.passwordEditText.setText("");
                    onPasscodeError();
                    this.codeFieldContainer.removeCallbacks(this.hidePasscodesDoNotMatch);
                    this.codeFieldContainer.post(new PasscodeActivity$$ExternalSyntheticLambda12(this));
                    return;
                }
                boolean z = SharedConfig.passcodeHash.length() == 0;
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    byte[] bytes = this.firstPassword.getBytes("UTF-8");
                    int length = bytes.length + 32;
                    byte[] bArr = new byte[length];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, 0, 16);
                    System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, bytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, (long) length));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                SharedConfig.allowScreenCapture = true;
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                int length2 = codeNumberFieldArr.length;
                while (i2 < length2) {
                    CodeNumberField codeNumberField = codeNumberFieldArr[i2];
                    codeNumberField.clearFocus();
                    AndroidUtilities.hideKeyboard(codeNumberField);
                    i2++;
                }
                this.keyboardView.setEditText((EditText) null);
                animateSuccessAnimation(new PasscodeActivity$$ExternalSyntheticLambda19(this, z));
            } else if (i == 2) {
                long j = SharedConfig.passcodeRetryInMs;
                if (j > 0) {
                    double d = (double) j;
                    Double.isNaN(d);
                    Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", Math.max(1, (int) Math.ceil(d / 1000.0d)), new Object[0])), 0).show();
                    for (CodeNumberField text2 : this.codeFieldContainer.codeField) {
                        text2.setText("");
                    }
                    this.passwordEditText.setText("");
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else if (!SharedConfig.checkPasscode(code)) {
                    SharedConfig.increaseBadPasscodeTries();
                    this.passwordEditText.setText("");
                    for (CodeNumberField text3 : this.codeFieldContainer.codeField) {
                        text3.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else {
                    SharedConfig.badPasscodeTries = 0;
                    SharedConfig.saveConfig();
                    this.passwordEditText.clearFocus();
                    AndroidUtilities.hideKeyboard(this.passwordEditText);
                    CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                    int length3 = codeNumberFieldArr2.length;
                    while (i2 < length3) {
                        CodeNumberField codeNumberField2 = codeNumberFieldArr2[i2];
                        codeNumberField2.clearFocus();
                        AndroidUtilities.hideKeyboard(codeNumberField2);
                        i2++;
                    }
                    this.keyboardView.setEditText((EditText) null);
                    animateSuccessAnimation(new PasscodeActivity$$ExternalSyntheticLambda17(this));
                }
            }
        } else {
            onPasscodeError();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$18() {
        this.codeFieldContainer.postDelayed(this.hidePasscodesDoNotMatch, 3000);
        this.postedHidePasscodesDoNotMatch = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$19(boolean z) {
        getMediaDataController().buildShortcuts();
        if (z) {
            presentFragment(new PasscodeActivity(0), true);
        } else {
            finishFragment();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$20() {
        presentFragment(new PasscodeActivity(0), true);
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (isPinCode()) {
                for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                    animateErrorProgress.animateErrorProgress(1.0f);
                }
            } else {
                this.outlinePasswordView.animateError(1.0f);
            }
            AndroidUtilities.shakeViewSpring(isPinCode() ? this.codeFieldContainer : this.outlinePasswordView, isPinCode() ? 10.0f : 4.0f, new PasscodeActivity$$ExternalSyntheticLambda16(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasscodeError$22() {
        AndroidUtilities.runOnUIThread(new PasscodeActivity$$ExternalSyntheticLambda13(this), isPinCode() ? 150 : 1000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasscodeError$21() {
        if (isPinCode()) {
            for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
                animateErrorProgress.animateErrorProgress(0.0f);
            }
            return;
        }
        this.outlinePasswordView.animateError(0.0f);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PasscodeActivity.this.fingerprintRow || adapterPosition == PasscodeActivity.this.autoLockRow || adapterPosition == PasscodeActivity.this.captureRow || adapterPosition == PasscodeActivity.this.changePasscodeRow || adapterPosition == PasscodeActivity.this.disablePasscodeRow;
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i == 0) {
                view2 = new TextCheckCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 1) {
                view2 = new TextSettingsCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 3) {
                if (i != 4) {
                    view = new TextInfoPrivacyCell(this.mContext);
                } else {
                    view = new RLottieImageHolderView(this.mContext);
                }
                return new RecyclerListView.Holder(view);
            } else {
                view2 = new HeaderCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PasscodeActivity.this.fingerprintRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", NUM), SharedConfig.useFingerprint, true);
                } else if (i == PasscodeActivity.this.captureRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString(NUM), SharedConfig.allowScreenCapture, false);
                }
            } else if (itemViewType != 1) {
                int i2 = 3;
                if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == PasscodeActivity.this.hintRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString(NUM));
                        textInfoPrivacyCell.setBackground((Drawable) null);
                        textInfoPrivacyCell.getTextView().setGravity(1);
                    } else if (i == PasscodeActivity.this.autoLockDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString(NUM));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        TextView textView = textInfoPrivacyCell.getTextView();
                        if (LocaleController.isRTL) {
                            i2 = 5;
                        }
                        textView.setGravity(i2);
                    } else if (i == PasscodeActivity.this.captureDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString(NUM));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        TextView textView2 = textInfoPrivacyCell.getTextView();
                        if (LocaleController.isRTL) {
                            i2 = 5;
                        }
                        textView2.setGravity(i2);
                    }
                } else if (itemViewType == 3) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setHeight(46);
                    if (i == PasscodeActivity.this.captureHeaderRow) {
                        headerCell.setText(LocaleController.getString(NUM));
                    }
                } else if (itemViewType == 4) {
                    RLottieImageHolderView rLottieImageHolderView = (RLottieImageHolderView) viewHolder.itemView;
                    rLottieImageHolderView.imageView.setAnimation(NUM, 100, 100);
                    rLottieImageHolderView.imageView.playAnimation();
                }
            } else {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == PasscodeActivity.this.changePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePasscode", NUM), true);
                    if (SharedConfig.passcodeHash.length() == 0) {
                        textSettingsCell.setTag("windowBackgroundWhiteGrayText7");
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                        return;
                    }
                    textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                } else if (i == PasscodeActivity.this.autoLockRow) {
                    int i3 = SharedConfig.autoLockIn;
                    if (i3 == 0) {
                        str = LocaleController.formatString("AutoLockDisabled", NUM, new Object[0]);
                    } else if (i3 < 3600) {
                        str = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", i3 / 60, new Object[0]));
                    } else if (i3 < 86400) {
                        str = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) i3) / 60.0f) / 60.0f)), new Object[0]));
                    } else {
                        str = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) i3) / 60.0f) / 60.0f) / 24.0f)), new Object[0]));
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", NUM), str, true);
                    textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                } else if (i == PasscodeActivity.this.disablePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString(NUM), false);
                    textSettingsCell.setTag("dialogTextRed");
                    textSettingsCell.setTextColor(Theme.getColor("dialogTextRed"));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (i == PasscodeActivity.this.changePasscodeRow || i == PasscodeActivity.this.autoLockRow || i == PasscodeActivity.this.disablePasscodeRow) {
                return 1;
            }
            if (i == PasscodeActivity.this.autoLockDetailRow || i == PasscodeActivity.this.captureDetailRow || i == PasscodeActivity.this.hintRow) {
                return 2;
            }
            if (i == PasscodeActivity.this.captureHeaderRow) {
                return 3;
            }
            if (i == PasscodeActivity.this.utyanRow) {
                return 4;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText7"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return arrayList;
    }

    private static final class RLottieImageHolderView extends FrameLayout {
        /* access modifiers changed from: private */
        public RLottieImageView imageView;

        private RLottieImageHolderView(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setOnClickListener(new PasscodeActivity$RLottieImageHolderView$$ExternalSyntheticLambda0(this));
            int dp = AndroidUtilities.dp(120.0f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp, dp);
            layoutParams.gravity = 1;
            addView(this.imageView, layoutParams);
            setPadding(0, AndroidUtilities.dp(32.0f), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.imageView.getAnimatedDrawable().isRunning()) {
                this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.imageView.playAnimation();
            }
        }
    }
}
