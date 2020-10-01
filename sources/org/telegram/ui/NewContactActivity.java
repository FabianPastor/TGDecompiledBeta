package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.CountrySelectActivity;

public class NewContactActivity extends BaseFragment implements AdapterView.OnItemSelectedListener {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public EditTextBoldCursor codeField;
    /* access modifiers changed from: private */
    public HashMap<String, String> codesMap = new HashMap<>();
    private LinearLayout contentLayout;
    /* access modifiers changed from: private */
    public ArrayList<String> countriesArray = new ArrayList<>();
    private HashMap<String, String> countriesMap = new HashMap<>();
    /* access modifiers changed from: private */
    public TextView countryButton;
    /* access modifiers changed from: private */
    public int countryState;
    /* access modifiers changed from: private */
    public boolean donePressed;
    /* access modifiers changed from: private */
    public ActionBarMenuItem editDoneItem;
    /* access modifiers changed from: private */
    public AnimatorSet editDoneItemAnimation;
    /* access modifiers changed from: private */
    public ContextProgressView editDoneItemProgress;
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public boolean ignoreSelection;
    private String initialFirstName;
    private String initialLastName;
    /* access modifiers changed from: private */
    public String initialPhoneNumber;
    /* access modifiers changed from: private */
    public EditTextBoldCursor lastNameField;
    private View lineView;
    /* access modifiers changed from: private */
    public HintEditText phoneField;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap = new HashMap<>();
    private TextView textView;

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x04bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r25) {
        /*
            r24 = this;
            r1 = r24
            r0 = r25
            org.telegram.ui.ActionBar.ActionBar r2 = r1.actionBar
            r3 = 2131165458(0x7var_, float:1.7945134E38)
            r2.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r1.actionBar
            r3 = 1
            r2.setAllowOverlayTitle(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r1.actionBar
            java.lang.String r4 = "AddContactTitle"
            r5 = 2131624162(0x7f0e00e2, float:1.8875496E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r2.setTitle(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r1.actionBar
            org.telegram.ui.NewContactActivity$1 r4 = new org.telegram.ui.NewContactActivity$1
            r4.<init>()
            r2.setActionBarMenuOnItemClick(r4)
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r1.avatarDrawable = r2
            r4 = 5
            java.lang.String r5 = ""
            r2.setInfo(r4, r5, r5)
            org.telegram.ui.ActionBar.ActionBar r2 = r1.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r2 = r2.createMenu()
            r5 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 2131165488(0x7var_, float:1.7945195E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r2.addItemWithWidth(r3, r6, r5)
            r1.editDoneItem = r2
            java.lang.String r5 = "Done"
            r6 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)
            r2.setContentDescription(r5)
            org.telegram.ui.Components.ContextProgressView r2 = new org.telegram.ui.Components.ContextProgressView
            r2.<init>(r0, r3)
            r1.editDoneItemProgress = r2
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r1.editDoneItem
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7)
            r5.addView(r2, r7)
            org.telegram.ui.Components.ContextProgressView r2 = r1.editDoneItemProgress
            r5 = 4
            r2.setVisibility(r5)
            android.widget.ScrollView r2 = new android.widget.ScrollView
            r2.<init>(r0)
            r1.fragmentView = r2
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r0)
            r1.contentLayout = r2
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r8 = 0
            r2.setPadding(r7, r8, r5, r8)
            android.widget.LinearLayout r2 = r1.contentLayout
            r2.setOrientation(r3)
            android.view.View r2 = r1.fragmentView
            android.widget.ScrollView r2 = (android.widget.ScrollView) r2
            android.widget.LinearLayout r5 = r1.contentLayout
            r7 = -2
            r9 = 51
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createScroll(r6, r7, r9)
            r2.addView(r5, r9)
            android.widget.LinearLayout r2 = r1.contentLayout
            org.telegram.ui.-$$Lambda$NewContactActivity$dyt1ArQHbLSL06GO-wtQQZkRhQE r5 = org.telegram.ui.$$Lambda$NewContactActivity$dyt1ArQHbLSL06GOwtQQZkRhQE.INSTANCE
            r2.setOnTouchListener(r5)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r0)
            android.widget.LinearLayout r5 = r1.contentLayout
            r9 = -1
            r10 = -2
            r11 = 0
            r12 = 1103101952(0x41CLASSNAME, float:24.0)
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10, r11, r12, r13, r14)
            r5.addView(r2, r9)
            org.telegram.ui.Components.BackupImageView r5 = new org.telegram.ui.Components.BackupImageView
            r5.<init>(r0)
            r1.avatarImage = r5
            org.telegram.ui.Components.AvatarDrawable r9 = r1.avatarDrawable
            r5.setImageDrawable(r9)
            org.telegram.ui.Components.BackupImageView r5 = r1.avatarImage
            r9 = 60
            r10 = 1114636288(0x42700000, float:60.0)
            r11 = 51
            r12 = 0
            r13 = 1091567616(0x41100000, float:9.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r2.addView(r5, r9)
            org.telegram.ui.Components.EditTextBoldCursor r5 = new org.telegram.ui.Components.EditTextBoldCursor
            r5.<init>(r0)
            r1.firstNameField = r5
            r9 = 1099956224(0x41900000, float:18.0)
            r5.setTextSize(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            java.lang.String r10 = "windowBackgroundWhiteHintText"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r5.setHintTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            java.lang.String r11 = "windowBackgroundWhiteBlackText"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setTextColor(r12)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r5.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r5.setLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r5.setSingleLine(r3)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8)
            r5.setBackgroundDrawable(r12)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r12 = 3
            r5.setGravity(r12)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r13 = 49152(0xCLASSNAME, float:6.8877E-41)
            r5.setInputType(r13)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r5.setImeOptions(r4)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            java.lang.String r14 = "FirstName"
            r15 = 2131625406(0x7f0e05be, float:1.887802E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r15)
            r5.setHint(r14)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setCursorColor(r14)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r14 = 1101004800(0x41a00000, float:20.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.setCursorSize(r15)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.firstNameField
            r15 = 1069547520(0x3fCLASSNAME, float:1.5)
            r5.setCursorWidth(r15)
            java.lang.String r5 = r1.initialFirstName
            r6 = 0
            if (r5 == 0) goto L_0x0163
            org.telegram.ui.Components.EditTextBoldCursor r7 = r1.firstNameField
            r7.setText(r5)
            r1.initialFirstName = r6
            r5 = 1
            goto L_0x0164
        L_0x0163:
            r5 = 0
        L_0x0164:
            org.telegram.ui.Components.EditTextBoldCursor r7 = r1.firstNameField
            r17 = -1
            r18 = 1107820544(0x42080000, float:34.0)
            r19 = 51
            r20 = 1118306304(0x42a80000, float:84.0)
            r21 = 0
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r7, r6)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.firstNameField
            org.telegram.ui.-$$Lambda$NewContactActivity$OEffd5rsJU1asHgIO5gqt5wMyr4 r7 = new org.telegram.ui.-$$Lambda$NewContactActivity$OEffd5rsJU1asHgIO5gqt5wMyr4
            r7.<init>()
            r6.setOnEditorActionListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.firstNameField
            org.telegram.ui.NewContactActivity$2 r7 = new org.telegram.ui.NewContactActivity$2
            r7.<init>()
            r6.addTextChangedListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r0)
            r1.lastNameField = r6
            r6.setTextSize(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setHintTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r6.setTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8)
            r6.setBackgroundDrawable(r7)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setSingleLine(r3)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setGravity(r12)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setInputType(r13)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r1.lastNameField
            r6.setImeOptions(r4)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.lastNameField
            r6 = 2131625682(0x7f0e06d2, float:1.8878579E38)
            java.lang.String r7 = "LastName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.lastNameField
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.lastNameField
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.lastNameField
            r4.setCursorWidth(r15)
            java.lang.String r4 = r1.initialLastName
            if (r4 == 0) goto L_0x0204
            org.telegram.ui.Components.EditTextBoldCursor r5 = r1.lastNameField
            r5.setText(r4)
            r4 = 0
            r1.initialLastName = r4
            r5 = 1
        L_0x0204:
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.lastNameField
            r17 = -1
            r18 = 1107820544(0x42080000, float:34.0)
            r19 = 51
            r20 = 1118306304(0x42a80000, float:84.0)
            r21 = 1110441984(0x42300000, float:44.0)
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor r2 = r1.lastNameField
            org.telegram.ui.-$$Lambda$NewContactActivity$caCQM7G1cFeQM5WDamEHC5G1jDk r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$caCQM7G1cFeQM5WDamEHC5G1jDk
            r4.<init>()
            r2.setOnEditorActionListener(r4)
            org.telegram.ui.Components.EditTextBoldCursor r2 = r1.lastNameField
            org.telegram.ui.NewContactActivity$3 r4 = new org.telegram.ui.NewContactActivity$3
            r4.<init>()
            r2.addTextChangedListener(r4)
            if (r5 == 0) goto L_0x0234
            r24.invalidateAvatar()
        L_0x0234:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r0)
            r1.countryButton = r2
            r2.setTextSize(r3, r9)
            android.widget.TextView r2 = r1.countryButton
            r4 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r8, r5, r8, r4)
            android.widget.TextView r2 = r1.countryButton
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setTextColor(r4)
            android.widget.TextView r2 = r1.countryButton
            r2.setMaxLines(r3)
            android.widget.TextView r2 = r1.countryButton
            r2.setSingleLine(r3)
            android.widget.TextView r2 = r1.countryButton
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r4)
            android.widget.TextView r2 = r1.countryButton
            r2.setGravity(r12)
            android.widget.TextView r2 = r1.countryButton
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
            r2.setBackground(r4)
            android.widget.LinearLayout r2 = r1.contentLayout
            android.widget.TextView r4 = r1.countryButton
            r17 = -1
            r18 = 36
            r19 = 0
            r20 = 1103101952(0x41CLASSNAME, float:24.0)
            r21 = 0
            r22 = 1096810496(0x41600000, float:14.0)
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r2.addView(r4, r5)
            android.widget.TextView r2 = r1.countryButton
            org.telegram.ui.-$$Lambda$NewContactActivity$1MsqwwjUsbFVncyFfb6LZrQJtdc r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$1MsqwwjUsbFVncyFfb6LZrQJtdc
            r4.<init>()
            r2.setOnClickListener(r4)
            android.view.View r2 = new android.view.View
            r2.<init>(r0)
            r1.lineView = r2
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r5, r8, r4, r8)
            android.view.View r2 = r1.lineView
            java.lang.String r4 = "windowBackgroundWhiteGrayLine"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setBackgroundColor(r4)
            android.widget.LinearLayout r2 = r1.contentLayout
            android.view.View r4 = r1.lineView
            r18 = 1
            r20 = -1047789568(0xffffffffCLASSNAMECLASSNAME, float:-17.5)
            r22 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r2.addView(r4, r5)
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r0)
            r2.setOrientation(r8)
            android.widget.LinearLayout r4 = r1.contentLayout
            r18 = -2
            r20 = 1101004800(0x41a00000, float:20.0)
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r4.addView(r2, r5)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r0)
            r1.textView = r4
            java.lang.String r5 = "+"
            r4.setText(r5)
            android.widget.TextView r4 = r1.textView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r5)
            android.widget.TextView r4 = r1.textView
            r4.setTextSize(r3, r9)
            android.widget.TextView r4 = r1.textView
            r5 = 2
            r4.setImportantForAccessibility(r5)
            android.widget.TextView r4 = r1.textView
            r6 = -2
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r6)
            r2.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = new org.telegram.ui.Components.EditTextBoldCursor
            r4.<init>(r0)
            r1.codeField = r4
            r4.setInputType(r12)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8)
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r4.setCursorWidth(r15)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r6, r8, r8, r8)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r4.setTextSize(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r4.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r6 = 19
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r7 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            r16 = 55
            r17 = 36
            r18 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
            r20 = 1098907648(0x41800000, float:16.0)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r2.addView(r4, r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            org.telegram.ui.NewContactActivity$4 r7 = new org.telegram.ui.NewContactActivity$4
            r7.<init>()
            r4.addTextChangedListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r1.codeField
            org.telegram.ui.-$$Lambda$NewContactActivity$_4M8UwTcrAOqbcxRs_dWKUHAnJM r7 = new org.telegram.ui.-$$Lambda$NewContactActivity$_4M8UwTcrAOqbcxRs_dWKUHAnJM
            r7.<init>()
            r4.setOnEditorActionListener(r7)
            org.telegram.ui.Components.HintEditText r4 = new org.telegram.ui.Components.HintEditText
            r4.<init>(r0)
            r1.phoneField = r4
            r4.setInputType(r12)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r7)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r4.setHintTextColor(r7)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8)
            r4.setBackgroundDrawable(r7)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r4.setPadding(r8, r8, r8, r8)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r7)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r4.setCursorSize(r7)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r4.setCursorWidth(r15)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r4.setTextSize(r3, r9)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r4.setMaxLines(r3)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r4.setGravity(r6)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            org.telegram.ui.Components.HintEditText r4 = r1.phoneField
            r6 = 1108344832(0x42100000, float:36.0)
            r7 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6)
            r2.addView(r4, r6)
            org.telegram.ui.Components.HintEditText r2 = r1.phoneField
            org.telegram.ui.NewContactActivity$5 r4 = new org.telegram.ui.NewContactActivity$5
            r4.<init>()
            r2.addTextChangedListener(r4)
            org.telegram.ui.Components.HintEditText r2 = r1.phoneField
            org.telegram.ui.-$$Lambda$NewContactActivity$sAQJMXy-aH9t_IBkPAm16jh4ito r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$sAQJMXy-aH9t_IBkPAm16jh4ito
            r4.<init>()
            r2.setOnEditorActionListener(r4)
            org.telegram.ui.Components.HintEditText r2 = r1.phoneField
            org.telegram.ui.-$$Lambda$NewContactActivity$285Xs2RNjYsiReFBHjMd6DM1AKI r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$285Xs2RNjYsiReFBHjMd6DM1AKI
            r4.<init>()
            r2.setOnKeyListener(r4)
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ Exception -> 0x045e }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x045e }
            android.content.res.Resources r0 = r25.getResources()     // Catch:{ Exception -> 0x045e }
            android.content.res.AssetManager r0 = r0.getAssets()     // Catch:{ Exception -> 0x045e }
            java.lang.String r7 = "countries.txt"
            java.io.InputStream r0 = r0.open(r7)     // Catch:{ Exception -> 0x045e }
            r6.<init>(r0)     // Catch:{ Exception -> 0x045e }
            r4.<init>(r6)     // Catch:{ Exception -> 0x045e }
        L_0x0421:
            java.lang.String r0 = r4.readLine()     // Catch:{ Exception -> 0x045e }
            if (r0 == 0) goto L_0x045a
            java.lang.String r6 = ";"
            java.lang.String[] r0 = r0.split(r6)     // Catch:{ Exception -> 0x045e }
            java.util.ArrayList<java.lang.String> r6 = r1.countriesArray     // Catch:{ Exception -> 0x045e }
            r7 = r0[r5]     // Catch:{ Exception -> 0x045e }
            r6.add(r8, r7)     // Catch:{ Exception -> 0x045e }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.countriesMap     // Catch:{ Exception -> 0x045e }
            r7 = r0[r5]     // Catch:{ Exception -> 0x045e }
            r9 = r0[r8]     // Catch:{ Exception -> 0x045e }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x045e }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.codesMap     // Catch:{ Exception -> 0x045e }
            r7 = r0[r8]     // Catch:{ Exception -> 0x045e }
            r9 = r0[r5]     // Catch:{ Exception -> 0x045e }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x045e }
            int r6 = r0.length     // Catch:{ Exception -> 0x045e }
            if (r6 <= r12) goto L_0x0452
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.phoneFormatMap     // Catch:{ Exception -> 0x045e }
            r7 = r0[r8]     // Catch:{ Exception -> 0x045e }
            r9 = r0[r12]     // Catch:{ Exception -> 0x045e }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x045e }
        L_0x0452:
            r6 = r0[r3]     // Catch:{ Exception -> 0x045e }
            r0 = r0[r5]     // Catch:{ Exception -> 0x045e }
            r2.put(r6, r0)     // Catch:{ Exception -> 0x045e }
            goto L_0x0421
        L_0x045a:
            r4.close()     // Catch:{ Exception -> 0x045e }
            goto L_0x0462
        L_0x045e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0462:
            java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
            org.telegram.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r3 = org.telegram.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
            java.util.Collections.sort(r0, r3)
            java.lang.String r0 = r1.initialPhoneNumber
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x047c
            org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
            java.lang.String r2 = r1.initialPhoneNumber
            r0.setText(r2)
            r2 = 0
            r1.initialPhoneNumber = r2
            goto L_0x04d3
        L_0x047c:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0492 }
            java.lang.String r3 = "phone"
            java.lang.Object r0 = r0.getSystemService(r3)     // Catch:{ Exception -> 0x0492 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0492 }
            if (r0 == 0) goto L_0x0496
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0492 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x0492 }
            r4 = r0
            goto L_0x0497
        L_0x0492:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0496:
            r4 = 0
        L_0x0497:
            if (r4 == 0) goto L_0x04b7
            java.lang.Object r0 = r2.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x04b7
            java.util.ArrayList<java.lang.String> r2 = r1.countriesArray
            int r2 = r2.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x04b7
            org.telegram.ui.Components.EditTextBoldCursor r2 = r1.codeField
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r1.countriesMap
            java.lang.Object r0 = r3.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2.setText(r0)
        L_0x04b7:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
            int r0 = r0.length()
            if (r0 != 0) goto L_0x04d3
            android.widget.TextView r0 = r1.countryButton
            r2 = 2131624786(0x7f0e0352, float:1.8876762E38)
            java.lang.String r3 = "ChooseCountry"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            org.telegram.ui.Components.HintEditText r0 = r1.phoneField
            r2 = 0
            r0.setHintText(r2)
        L_0x04d3:
            android.view.View r0 = r1.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NewContactActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ boolean lambda$createView$1$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ void lambda$createView$4$NewContactActivity(View view) {
        CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true);
        countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
            public final void didSelectCountry(String str, String str2) {
                NewContactActivity.this.lambda$null$3$NewContactActivity(str, str2);
            }
        });
        presentFragment(countrySelectActivity);
    }

    public /* synthetic */ void lambda$null$3$NewContactActivity(String str, String str2) {
        selectCountry(str);
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
    }

    public /* synthetic */ boolean lambda$createView$5$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$6$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.editDoneItem.performClick();
        return true;
    }

    public /* synthetic */ boolean lambda$createView$7$NewContactActivity(View view, int i, KeyEvent keyEvent) {
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
    public void invalidateAvatar() {
        this.avatarDrawable.setInfo(5, this.firstNameField.getText().toString(), this.lastNameField.getText().toString());
        this.avatarImage.invalidate();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            View findFocus = this.contentLayout.findFocus();
            if (findFocus == null) {
                this.firstNameField.requestFocus();
                findFocus = this.firstNameField;
            }
            AndroidUtilities.showKeyboard(findFocus);
        }
    }

    public void setInitialPhoneNumber(String str) {
        this.initialPhoneNumber = str;
    }

    public void setInitialName(String str, String str2) {
        this.initialFirstName = str;
        this.initialLastName = str2;
    }

    public void selectCountry(String str) {
        if (this.countriesArray.indexOf(str) != -1) {
            this.ignoreOnTextChange = true;
            String str2 = this.countriesMap.get(str);
            this.codeField.setText(str2);
            this.countryButton.setText(str);
            String str3 = this.phoneFormatMap.get(str2);
            this.phoneField.setHintText(str3 != null ? str3.replace('X', 8211) : null);
            this.ignoreOnTextChange = false;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z;
        AnimatorSet animatorSet = this.editDoneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z2) {
            this.editDoneItemAnimation = new AnimatorSet();
            if (z3) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{1.0f})});
            } else {
                this.editDoneItem.getContentView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                        if (!z3) {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                        AnimatorSet unused = NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z3) {
            this.editDoneItem.getContentView().setScaleX(0.1f);
            this.editDoneItem.getContentView().setScaleY(0.1f);
            this.editDoneItem.getContentView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(1.0f);
            this.editDoneItemProgress.setScaleY(1.0f);
            this.editDoneItemProgress.setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(4);
            this.editDoneItemProgress.setVisibility(0);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getContentView().setScaleX(1.0f);
            this.editDoneItem.getContentView().setScaleY(1.0f);
            this.editDoneItem.getContentView().setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(0);
            this.editDoneItemProgress.setVisibility(4);
            this.editDoneItem.setEnabled(true);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$NewContactActivity$w3KTwAlwLqpY0K9fVsMRxI5REAU r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NewContactActivity.this.lambda$getThemeDescriptions$8$NewContactActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayLine"));
        arrayList.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        $$Lambda$NewContactActivity$w3KTwAlwLqpY0K9fVsMRxI5REAU r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$NewContactActivity() {
        if (this.avatarImage != null) {
            invalidateAvatar();
        }
    }
}
