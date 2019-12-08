package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;

public class NewContactActivity extends BaseFragment implements OnItemSelectedListener {
    private static final int done_button = 1;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private EditTextBoldCursor codeField;
    private HashMap<String, String> codesMap = new HashMap();
    private ArrayList<String> countriesArray = new ArrayList();
    private HashMap<String, String> countriesMap = new HashMap();
    private TextView countryButton;
    private int countryState;
    private boolean donePressed;
    private ActionBarMenuItem editDoneItem;
    private AnimatorSet editDoneItemAnimation;
    private ContextProgressView editDoneItemProgress;
    private EditTextBoldCursor firstNameField;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private boolean ignoreSelection;
    private String initialPhoneNumber;
    private EditTextBoldCursor lastNameField;
    private View lineView;
    private HintEditText phoneField;
    private HashMap<String, String> phoneFormatMap = new HashMap();
    private TextView textView;

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x04a4  */
    public android.view.View createView(android.content.Context r24) {
        /*
        r23 = this;
        r1 = r23;
        r0 = r24;
        r2 = r1.actionBar;
        r3 = NUM; // 0x7var_a5 float:1.7944913E38 double:1.0529355845E-314;
        r2.setBackButtonImage(r3);
        r2 = r1.actionBar;
        r3 = 1;
        r2.setAllowOverlayTitle(r3);
        r2 = r1.actionBar;
        r4 = "AddContactTitle";
        r5 = NUM; // 0x7f0d00ab float:1.8742462E38 double:1.053129862E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.setTitle(r4);
        r2 = r1.actionBar;
        r4 = new org.telegram.ui.NewContactActivity$1;
        r4.<init>();
        r2.setActionBarMenuOnItemClick(r4);
        r2 = new org.telegram.ui.Components.AvatarDrawable;
        r2.<init>();
        r1.avatarDrawable = r2;
        r2 = r1.avatarDrawable;
        r4 = "";
        r5 = 5;
        r2.setInfo(r5, r4, r4);
        r2 = r1.actionBar;
        r2 = r2.createMenu();
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_c3 float:1.7944973E38 double:1.0529355994E-314;
        r2 = r2.addItemWithWidth(r3, r6, r4);
        r1.editDoneItem = r2;
        r2 = r1.editDoneItem;
        r4 = "Done";
        r6 = NUM; // 0x7f0d039e float:1.8743993E38 double:1.053130235E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r6);
        r2.setContentDescription(r4);
        r2 = new org.telegram.ui.Components.ContextProgressView;
        r2.<init>(r0, r3);
        r1.editDoneItemProgress = r2;
        r2 = r1.editDoneItem;
        r4 = r1.editDoneItemProgress;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r2.addView(r4, r7);
        r2 = r1.editDoneItemProgress;
        r4 = 4;
        r2.setVisibility(r4);
        r2 = new android.widget.ScrollView;
        r2.<init>(r0);
        r1.fragmentView = r2;
        r2 = new android.widget.LinearLayout;
        r2.<init>(r0);
        r4 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = 0;
        r2.setPadding(r7, r8, r4, r8);
        r2.setOrientation(r3);
        r4 = r1.fragmentView;
        r4 = (android.widget.ScrollView) r4;
        r7 = -2;
        r9 = 51;
        r9 = org.telegram.ui.Components.LayoutHelper.createScroll(r6, r7, r9);
        r4.addView(r2, r9);
        r4 = org.telegram.ui.-$$Lambda$NewContactActivity$dyt1ArQHbLSL06GO-wtQQZkRhQE.INSTANCE;
        r2.setOnTouchListener(r4);
        r4 = new android.widget.FrameLayout;
        r4.<init>(r0);
        r9 = -1;
        r10 = -2;
        r11 = 0;
        r12 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r13 = 0;
        r14 = 0;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10, r11, r12, r13, r14);
        r2.addView(r4, r9);
        r9 = new org.telegram.ui.Components.BackupImageView;
        r9.<init>(r0);
        r1.avatarImage = r9;
        r9 = r1.avatarImage;
        r10 = r1.avatarDrawable;
        r9.setImageDrawable(r10);
        r9 = r1.avatarImage;
        r10 = 60;
        r11 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r12 = 51;
        r14 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r15 = 0;
        r16 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16);
        r4.addView(r9, r10);
        r9 = new org.telegram.ui.Components.EditTextBoldCursor;
        r9.<init>(r0);
        r1.firstNameField = r9;
        r9 = r1.firstNameField;
        r10 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r9.setTextSize(r3, r10);
        r9 = r1.firstNameField;
        r11 = "windowBackgroundWhiteHintText";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r9.setHintTextColor(r12);
        r9 = r1.firstNameField;
        r12 = "windowBackgroundWhiteBlackText";
        r13 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9.setTextColor(r13);
        r9 = r1.firstNameField;
        r9.setMaxLines(r3);
        r9 = r1.firstNameField;
        r9.setLines(r3);
        r9 = r1.firstNameField;
        r9.setSingleLine(r3);
        r9 = r1.firstNameField;
        r13 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8);
        r9.setBackgroundDrawable(r13);
        r9 = r1.firstNameField;
        r13 = 3;
        r9.setGravity(r13);
        r9 = r1.firstNameField;
        r14 = 49152; // 0xCLASSNAME float:6.8877E-41 double:2.42843E-319;
        r9.setInputType(r14);
        r9 = r1.firstNameField;
        r9.setImeOptions(r5);
        r9 = r1.firstNameField;
        r14 = "FirstName";
        r15 = NUM; // 0x7f0d0478 float:1.8744435E38 double:1.053130343E-314;
        r14 = org.telegram.messenger.LocaleController.getString(r14, r15);
        r9.setHint(r14);
        r9 = r1.firstNameField;
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r9.setCursorColor(r14);
        r9 = r1.firstNameField;
        r14 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r9.setCursorSize(r15);
        r9 = r1.firstNameField;
        r15 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r9.setCursorWidth(r15);
        r9 = r1.firstNameField;
        r16 = -1;
        r17 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r18 = 51;
        r19 = NUM; // 0x42a80000 float:84.0 double:5.525167263E-315;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r4.addView(r9, r6);
        r6 = r1.firstNameField;
        r9 = new org.telegram.ui.-$$Lambda$NewContactActivity$OEffd5rsJU1asHgIO5gqt5wMyr4;
        r9.<init>(r1);
        r6.setOnEditorActionListener(r9);
        r6 = r1.firstNameField;
        r9 = new org.telegram.ui.NewContactActivity$2;
        r9.<init>();
        r6.addTextChangedListener(r9);
        r6 = new org.telegram.ui.Components.EditTextBoldCursor;
        r6.<init>(r0);
        r1.lastNameField = r6;
        r6 = r1.lastNameField;
        r6.setTextSize(r3, r10);
        r6 = r1.lastNameField;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r6.setHintTextColor(r9);
        r6 = r1.lastNameField;
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r6.setTextColor(r9);
        r6 = r1.lastNameField;
        r9 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8);
        r6.setBackgroundDrawable(r9);
        r6 = r1.lastNameField;
        r6.setMaxLines(r3);
        r6 = r1.lastNameField;
        r6.setLines(r3);
        r6 = r1.lastNameField;
        r6.setSingleLine(r3);
        r6 = r1.lastNameField;
        r6.setGravity(r13);
        r6 = r1.lastNameField;
        r9 = 49152; // 0xCLASSNAME float:6.8877E-41 double:2.42843E-319;
        r6.setInputType(r9);
        r6 = r1.lastNameField;
        r6.setImeOptions(r5);
        r5 = r1.lastNameField;
        r6 = "LastName";
        r9 = NUM; // 0x7f0d0568 float:1.8744922E38 double:1.0531304613E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r9);
        r5.setHint(r6);
        r5 = r1.lastNameField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r5.setCursorColor(r6);
        r5 = r1.lastNameField;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r5.setCursorSize(r6);
        r5 = r1.lastNameField;
        r5.setCursorWidth(r15);
        r5 = r1.lastNameField;
        r20 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r4.addView(r5, r6);
        r4 = r1.lastNameField;
        r5 = new org.telegram.ui.-$$Lambda$NewContactActivity$caCQM7G1cFeQM5WDamEHC5G1jDk;
        r5.<init>(r1);
        r4.setOnEditorActionListener(r5);
        r4 = r1.lastNameField;
        r5 = new org.telegram.ui.NewContactActivity$3;
        r5.<init>();
        r4.addTextChangedListener(r5);
        r4 = new android.widget.TextView;
        r4.<init>(r0);
        r1.countryButton = r4;
        r4 = r1.countryButton;
        r4.setTextSize(r3, r10);
        r4 = r1.countryButton;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r4.setPadding(r5, r6, r9, r8);
        r4 = r1.countryButton;
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r4.setTextColor(r5);
        r4 = r1.countryButton;
        r4.setMaxLines(r3);
        r4 = r1.countryButton;
        r4.setSingleLine(r3);
        r4 = r1.countryButton;
        r5 = android.text.TextUtils.TruncateAt.END;
        r4.setEllipsize(r5);
        r4 = r1.countryButton;
        r4.setGravity(r13);
        r4 = r1.countryButton;
        r5 = NUM; // 0x7var_ float:1.7945823E38 double:1.0529358064E-314;
        r4.setBackgroundResource(r5);
        r4 = r1.countryButton;
        r17 = 36;
        r18 = 0;
        r19 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r20 = 0;
        r21 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21);
        r2.addView(r4, r5);
        r4 = r1.countryButton;
        r5 = new org.telegram.ui.-$$Lambda$NewContactActivity$PQwWlWssBZjUKNZQJ0dIzeJV9OI;
        r5.<init>(r1);
        r4.setOnClickListener(r5);
        r4 = new android.view.View;
        r4.<init>(r0);
        r1.lineView = r4;
        r4 = r1.lineView;
        r5 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setPadding(r5, r8, r6, r8);
        r4 = r1.lineView;
        r5 = "windowBackgroundWhiteGrayLine";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = r1.lineView;
        r17 = 1;
        r19 = -NUM; // 0xffffffffCLASSNAMECLASSNAME float:-17.5 double:NaN;
        r21 = 0;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21);
        r2.addView(r4, r5);
        r4 = new android.widget.LinearLayout;
        r4.<init>(r0);
        r4.setOrientation(r8);
        r17 = -2;
        r19 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21);
        r2.addView(r4, r5);
        r2 = new android.widget.TextView;
        r2.<init>(r0);
        r1.textView = r2;
        r2 = r1.textView;
        r5 = "+";
        r2.setText(r5);
        r2 = r1.textView;
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r2.setTextColor(r5);
        r2 = r1.textView;
        r2.setTextSize(r3, r10);
        r2 = r1.textView;
        r5 = 2;
        r2.setImportantForAccessibility(r5);
        r2 = r1.textView;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r7);
        r4.addView(r2, r6);
        r2 = new org.telegram.ui.Components.EditTextBoldCursor;
        r2.<init>(r0);
        r1.codeField = r2;
        r2 = r1.codeField;
        r2.setInputType(r13);
        r2 = r1.codeField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r2.setTextColor(r6);
        r2 = r1.codeField;
        r6 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8);
        r2.setBackgroundDrawable(r6);
        r2 = r1.codeField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r2.setCursorColor(r6);
        r2 = r1.codeField;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2.setCursorSize(r6);
        r2 = r1.codeField;
        r2.setCursorWidth(r15);
        r2 = r1.codeField;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2.setPadding(r6, r8, r8, r8);
        r2 = r1.codeField;
        r2.setTextSize(r3, r10);
        r2 = r1.codeField;
        r2.setMaxLines(r3);
        r2 = r1.codeField;
        r6 = 19;
        r2.setGravity(r6);
        r2 = r1.codeField;
        r6 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r2.setImeOptions(r6);
        r2 = r1.codeField;
        r16 = 55;
        r17 = 36;
        r18 = -NUM; // 0xffffffffCLASSNAME float:-9.0 double:NaN;
        r19 = 0;
        r20 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21);
        r4.addView(r2, r6);
        r2 = r1.codeField;
        r6 = new org.telegram.ui.NewContactActivity$4;
        r6.<init>();
        r2.addTextChangedListener(r6);
        r2 = r1.codeField;
        r6 = new org.telegram.ui.-$$Lambda$NewContactActivity$sAQJMXy-aH9t_IBkPAm16jh4ito;
        r6.<init>(r1);
        r2.setOnEditorActionListener(r6);
        r2 = new org.telegram.ui.Components.HintEditText;
        r2.<init>(r0);
        r1.phoneField = r2;
        r2 = r1.phoneField;
        r2.setInputType(r13);
        r2 = r1.phoneField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r2.setTextColor(r6);
        r2 = r1.phoneField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r2.setHintTextColor(r6);
        r2 = r1.phoneField;
        r6 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r0, r8);
        r2.setBackgroundDrawable(r6);
        r2 = r1.phoneField;
        r2.setPadding(r8, r8, r8, r8);
        r2 = r1.phoneField;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r2.setCursorColor(r6);
        r2 = r1.phoneField;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2.setCursorSize(r6);
        r2 = r1.phoneField;
        r2.setCursorWidth(r15);
        r2 = r1.phoneField;
        r2.setTextSize(r3, r10);
        r2 = r1.phoneField;
        r2.setMaxLines(r3);
        r2 = r1.phoneField;
        r6 = 19;
        r2.setGravity(r6);
        r2 = r1.phoneField;
        r6 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r2.setImeOptions(r6);
        r2 = r1.phoneField;
        r6 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r7 = -1;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6);
        r4.addView(r2, r6);
        r2 = r1.phoneField;
        r4 = new org.telegram.ui.NewContactActivity$5;
        r4.<init>();
        r2.addTextChangedListener(r4);
        r2 = r1.phoneField;
        r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$6q60KF1tjXtvySPg1IX8F4PNNEY;
        r4.<init>(r1);
        r2.setOnEditorActionListener(r4);
        r2 = r1.phoneField;
        r4 = new org.telegram.ui.-$$Lambda$NewContactActivity$5Ca3pvZCNy2Se-fsqStW34es8nQ;
        r4.<init>(r1);
        r2.setOnKeyListener(r4);
        r2 = new java.util.HashMap;
        r2.<init>();
        r4 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0442 }
        r6 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x0442 }
        r0 = r24.getResources();	 Catch:{ Exception -> 0x0442 }
        r0 = r0.getAssets();	 Catch:{ Exception -> 0x0442 }
        r7 = "countries.txt";
        r0 = r0.open(r7);	 Catch:{ Exception -> 0x0442 }
        r6.<init>(r0);	 Catch:{ Exception -> 0x0442 }
        r4.<init>(r6);	 Catch:{ Exception -> 0x0442 }
    L_0x0405:
        r0 = r4.readLine();	 Catch:{ Exception -> 0x0442 }
        if (r0 == 0) goto L_0x043e;
    L_0x040b:
        r6 = ";";
        r0 = r0.split(r6);	 Catch:{ Exception -> 0x0442 }
        r6 = r1.countriesArray;	 Catch:{ Exception -> 0x0442 }
        r7 = r0[r5];	 Catch:{ Exception -> 0x0442 }
        r6.add(r8, r7);	 Catch:{ Exception -> 0x0442 }
        r6 = r1.countriesMap;	 Catch:{ Exception -> 0x0442 }
        r7 = r0[r5];	 Catch:{ Exception -> 0x0442 }
        r9 = r0[r8];	 Catch:{ Exception -> 0x0442 }
        r6.put(r7, r9);	 Catch:{ Exception -> 0x0442 }
        r6 = r1.codesMap;	 Catch:{ Exception -> 0x0442 }
        r7 = r0[r8];	 Catch:{ Exception -> 0x0442 }
        r9 = r0[r5];	 Catch:{ Exception -> 0x0442 }
        r6.put(r7, r9);	 Catch:{ Exception -> 0x0442 }
        r6 = r0.length;	 Catch:{ Exception -> 0x0442 }
        if (r6 <= r13) goto L_0x0436;
    L_0x042d:
        r6 = r1.phoneFormatMap;	 Catch:{ Exception -> 0x0442 }
        r7 = r0[r8];	 Catch:{ Exception -> 0x0442 }
        r9 = r0[r13];	 Catch:{ Exception -> 0x0442 }
        r6.put(r7, r9);	 Catch:{ Exception -> 0x0442 }
    L_0x0436:
        r6 = r0[r3];	 Catch:{ Exception -> 0x0442 }
        r0 = r0[r5];	 Catch:{ Exception -> 0x0442 }
        r2.put(r6, r0);	 Catch:{ Exception -> 0x0442 }
        goto L_0x0405;
    L_0x043e:
        r4.close();	 Catch:{ Exception -> 0x0442 }
        goto L_0x0446;
    L_0x0442:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0446:
        r0 = r1.countriesArray;
        r4 = org.telegram.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE;
        java.util.Collections.sort(r0, r4);
        r0 = r1.initialPhoneNumber;
        r0 = android.text.TextUtils.isEmpty(r0);
        r4 = 0;
        if (r0 != 0) goto L_0x0460;
    L_0x0456:
        r0 = r1.codeField;
        r2 = r1.initialPhoneNumber;
        r0.setText(r2);
        r1.initialPhoneNumber = r4;
        goto L_0x04b9;
    L_0x0460:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0475 }
        r5 = "phone";
        r0 = r0.getSystemService(r5);	 Catch:{ Exception -> 0x0475 }
        r0 = (android.telephony.TelephonyManager) r0;	 Catch:{ Exception -> 0x0475 }
        if (r0 == 0) goto L_0x0479;
    L_0x046c:
        r0 = r0.getSimCountryIso();	 Catch:{ Exception -> 0x0475 }
        r0 = r0.toUpperCase();	 Catch:{ Exception -> 0x0475 }
        goto L_0x047a;
    L_0x0475:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0479:
        r0 = r4;
    L_0x047a:
        if (r0 == 0) goto L_0x049c;
    L_0x047c:
        r0 = r2.get(r0);
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x049c;
    L_0x0484:
        r2 = r1.countriesArray;
        r2 = r2.indexOf(r0);
        r5 = -1;
        if (r2 == r5) goto L_0x049c;
    L_0x048d:
        r2 = r1.codeField;
        r5 = r1.countriesMap;
        r0 = r5.get(r0);
        r0 = (java.lang.CharSequence) r0;
        r2.setText(r0);
        r1.countryState = r8;
    L_0x049c:
        r0 = r1.codeField;
        r0 = r0.length();
        if (r0 != 0) goto L_0x04b9;
    L_0x04a4:
        r0 = r1.countryButton;
        r2 = NUM; // 0x7f0d02c2 float:1.8743547E38 double:1.0531301264E-314;
        r5 = "ChooseCountry";
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r0.setText(r2);
        r0 = r1.phoneField;
        r0.setHintText(r4);
        r1.countryState = r3;
    L_0x04b9:
        r0 = r1.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NewContactActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ boolean lambda$createView$1$NewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$NewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ void lambda$createView$5$NewContactActivity(View view) {
        CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true);
        countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$NewContactActivity$RKjBEG5T2-fQQhQFnlm7ULenE4I(this));
        presentFragment(countrySelectActivity);
    }

    public /* synthetic */ void lambda$null$4$NewContactActivity(String str, String str2) {
        selectCountry(str);
        AndroidUtilities.runOnUIThread(new -$$Lambda$NewContactActivity$kdGKhx_RKvrLvJRlZLpJl1Jr720(this), 300);
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
    }

    public /* synthetic */ void lambda$null$3$NewContactActivity() {
        AndroidUtilities.showKeyboard(this.phoneField);
    }

    public /* synthetic */ boolean lambda$createView$6$NewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$7$NewContactActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.editDoneItem.performClick();
        return true;
    }

    public /* synthetic */ boolean lambda$createView$8$NewContactActivity(View view, int i, KeyEvent keyEvent) {
        if (i != 67 || this.phoneField.length() != 0) {
            return false;
        }
        this.codeField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.codeField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.codeField.dispatchKeyEvent(keyEvent);
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void setInitialPhoneNumber(String str) {
        this.initialPhoneNumber = str;
    }

    public void selectCountry(String str) {
        if (this.countriesArray.indexOf(str) != -1) {
            this.ignoreOnTextChange = true;
            String str2 = (String) this.countriesMap.get(str);
            this.codeField.setText(str2);
            this.countryButton.setText(str);
            str = (String) this.phoneFormatMap.get(str2);
            this.phoneField.setHintText(str != null ? str.replace('X', 8211) : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z;
        AnimatorSet animatorSet = this.editDoneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z2) {
            this.editDoneItemAnimation = new AnimatorSet();
            String str = "alpha";
            String str2 = "scaleY";
            String str3 = "scaleX";
            if (z3) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                AnimatorSet animatorSet2 = this.editDoneItemAnimation;
                Animator[] animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.editDoneItem.getContentView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str3, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str2, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str3, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str2, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr2);
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                        if (z3) {
                            NewContactActivity.this.editDoneItem.getContentView().setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                        NewContactActivity.this.editDoneItemAnimation = null;
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

    public ThemeDescription[] getThemeDescriptions() {
        r10 = new ThemeDescription[34];
        -$$Lambda$NewContactActivity$-TdFXabn_cMkV_y0uLVuNwOAoYk -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk = new -$$Lambda$NewContactActivity$-TdFXabn_cMkV_y0uLVuNwOAoYk(this);
        r10[26] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_text");
        r10[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundRed");
        r10[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundOrange");
        r10[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundViolet");
        r10[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundGreen");
        r10[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundCyan");
        r10[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundBlue");
        r10[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_backgroundPink");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$9$NewContactActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, this.firstNameField.getText().toString(), this.lastNameField.getText().toString());
            this.avatarImage.invalidate();
        }
    }
}
