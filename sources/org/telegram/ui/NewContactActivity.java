package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

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
    private EditTextBoldCursor lastNameField;
    private View lineView;
    private HintEditText phoneField;
    private HashMap<String, String> phoneFormatMap = new HashMap();
    private TextView textView;

    /* renamed from: org.telegram.ui.NewContactActivity$2 */
    class C15342 implements OnTouchListener {
        C15342() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$3 */
    class C15353 implements OnEditorActionListener {
        C15353() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.lastNameField.requestFocus();
            NewContactActivity.this.lastNameField.setSelection(NewContactActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$4 */
    class C15364 implements TextWatcher {
        C15364() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
            NewContactActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$5 */
    class C15375 implements OnEditorActionListener {
        C15375() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$6 */
    class C15386 implements TextWatcher {
        C15386() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
            NewContactActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$7 */
    class C15407 implements OnClickListener {

        /* renamed from: org.telegram.ui.NewContactActivity$7$1 */
        class C22081 implements CountrySelectActivityDelegate {

            /* renamed from: org.telegram.ui.NewContactActivity$7$1$1 */
            class C15391 implements Runnable {
                C15391() {
                }

                public void run() {
                    AndroidUtilities.showKeyboard(NewContactActivity.this.phoneField);
                }
            }

            C22081() {
            }

            public void didSelectCountry(String name, String shortName) {
                NewContactActivity.this.selectCountry(name);
                AndroidUtilities.runOnUIThread(new C15391(), 300);
                NewContactActivity.this.phoneField.requestFocus();
                NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            }
        }

        C15407() {
        }

        public void onClick(View view) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new C22081());
            NewContactActivity.this.presentFragment(fragment);
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$8 */
    class C15418 implements TextWatcher {
        C15418() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (!NewContactActivity.this.ignoreOnTextChange) {
                NewContactActivity.this.ignoreOnTextChange = true;
                String text = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                NewContactActivity.this.codeField.setText(text);
                String str = null;
                if (text.length() == 0) {
                    NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                    NewContactActivity.this.phoneField.setHintText(null);
                    NewContactActivity.this.countryState = 1;
                } else {
                    boolean ok = false;
                    String textToSet = null;
                    int a = 4;
                    if (text.length() > 4) {
                        int a2;
                        String sub;
                        NewContactActivity.this.ignoreOnTextChange = true;
                        while (true) {
                            a2 = a;
                            if (a2 < 1) {
                                break;
                            }
                            sub = text.substring(0, a2);
                            if (((String) NewContactActivity.this.codesMap.get(sub)) != null) {
                                break;
                            }
                            a = a2 - 1;
                        }
                        ok = true;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(text.substring(a2, text.length()));
                        stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                        textToSet = stringBuilder.toString();
                        text = sub;
                        NewContactActivity.this.codeField.setText(sub);
                        if (!ok) {
                            NewContactActivity.this.ignoreOnTextChange = true;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(text.substring(1, text.length()));
                            stringBuilder2.append(NewContactActivity.this.phoneField.getText().toString());
                            textToSet = stringBuilder2.toString();
                            EditTextBoldCursor access$200 = NewContactActivity.this.codeField;
                            CharSequence substring = text.substring(0, 1);
                            text = substring;
                            access$200.setText(substring);
                        }
                    }
                    String country = (String) NewContactActivity.this.codesMap.get(text);
                    if (country != null) {
                        int index = NewContactActivity.this.countriesArray.indexOf(country);
                        if (index != -1) {
                            NewContactActivity.this.ignoreSelection = true;
                            NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(index));
                            String hint = (String) NewContactActivity.this.phoneFormatMap.get(text);
                            HintEditText access$300 = NewContactActivity.this.phoneField;
                            if (hint != null) {
                                str = hint.replace('X', '\u2013');
                            }
                            access$300.setHintText(str);
                            NewContactActivity.this.countryState = 0;
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                            NewContactActivity.this.phoneField.setHintText(null);
                            NewContactActivity.this.countryState = 2;
                        }
                    } else {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                        NewContactActivity.this.phoneField.setHintText(null);
                        NewContactActivity.this.countryState = 2;
                    }
                    if (!ok) {
                        NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                    }
                    if (textToSet != null) {
                        NewContactActivity.this.phoneField.requestFocus();
                        NewContactActivity.this.phoneField.setText(textToSet);
                        NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
                    }
                }
                NewContactActivity.this.ignoreOnTextChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$9 */
    class C15429 implements OnEditorActionListener {
        C15429() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$1 */
    class C22071 extends ActionBarMenuOnItemClick {
        C22071() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NewContactActivity.this.finishFragment();
            } else if (id == 1 && !NewContactActivity.this.donePressed) {
                Vibrator v;
                if (NewContactActivity.this.firstNameField.length() == 0) {
                    v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                } else if (NewContactActivity.this.codeField.length() == 0) {
                    v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                } else if (NewContactActivity.this.phoneField.length() == 0) {
                    v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                } else {
                    NewContactActivity.this.donePressed = true;
                    NewContactActivity.this.showEditDoneProgress(true, true);
                    final TL_contacts_importContacts req = new TL_contacts_importContacts();
                    final TL_inputPhoneContact inputPhoneContact = new TL_inputPhoneContact();
                    inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                    inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(NewContactActivity.this.codeField.getText().toString());
                    stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                    inputPhoneContact.phone = stringBuilder.toString();
                    req.contacts.add(inputPhoneContact);
                    ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, final TL_error error) {
                            final TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.ui.NewContactActivity$1$1$1$1 */
                                class C15321 implements DialogInterface.OnClickListener {
                                    C15321() {
                                    }

                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", inputPhoneContact.phone, null));
                                            intent.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                                            NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                }

                                public void run() {
                                    NewContactActivity.this.donePressed = false;
                                    if (res == null) {
                                        NewContactActivity.this.showEditDoneProgress(false, true);
                                        AlertsCreator.processError(NewContactActivity.this.currentAccount, error, NewContactActivity.this, req, new Object[0]);
                                    } else if (!res.users.isEmpty()) {
                                        MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(res.users, false);
                                        MessagesController.openChatOrProfileWith((User) res.users.get(0), null, NewContactActivity.this, 1, true);
                                    } else if (NewContactActivity.this.getParentActivity() != null) {
                                        NewContactActivity.this.showEditDoneProgress(false, true);
                                        Builder builder = new Builder(NewContactActivity.this.getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        builder.setMessage(LocaleController.formatString("ContactNotRegistered", R.string.ContactNotRegistered, ContactsController.formatName(inputPhoneContact.first_name, inputPhoneContact.last_name)));
                                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        builder.setPositiveButton(LocaleController.getString("Invite", R.string.Invite), new C15321());
                                        NewContactActivity.this.showDialog(builder.create());
                                    }
                                }
                            });
                        }
                    }, 2), NewContactActivity.this.classGuid);
                }
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C22071());
        this.avatarDrawable = new AvatarDrawable();
        int i = 0;
        this.avatarDrawable.setInfo(5, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, false);
        this.editDoneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.editDoneItemProgress = new ContextProgressView(context2, 1);
        this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new C15342());
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60.0f, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new C15353());
        this.firstNameField.addTextChangedListener(new C15364());
        this.lastNameField = new EditTextBoldCursor(context2);
        this.lastNameField.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        this.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new C15375());
        this.lastNameField.addTextChangedListener(new C15386());
        this.countryButton = new TextView(context2);
        this.countryButton.setTextSize(1, 18.0f);
        this.countryButton.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f), 0);
        this.countryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.countryButton.setMaxLines(1);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackgroundResource(R.drawable.spinner_states);
        linearLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new C15407());
        this.lineView = new View(context2);
        this.lineView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
        linearLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        this.textView = new TextView(context2);
        this.textView.setText("+");
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 18.0f);
        linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditTextBoldCursor(context2);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.codeField.setCursorWidth(1.5f);
        this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0f);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        this.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
        linearLayout2.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
        this.codeField.addTextChangedListener(new C15418());
        this.codeField.setOnEditorActionListener(new C15429());
        this.phoneField = new HintEditText(context2);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.phoneField.setCursorWidth(1.5f);
        this.phoneField.setTextSize(1, 18.0f);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435462);
        linearLayout2.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
        this.phoneField.addTextChangedListener(new TextWatcher() {
            private int actionPosition;
            private int characterAction = -1;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0 && after == 1) {
                    this.characterAction = 1;
                } else if (count != 1 || after != 0) {
                    this.characterAction = -1;
                } else if (s.charAt(start) != ' ' || start <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = start - 1;
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    StringBuilder stringBuilder;
                    int start = NewContactActivity.this.phoneField.getSelectionStart();
                    String phoneChars = "0123456789";
                    String str = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str.substring(0, this.actionPosition));
                        stringBuilder.append(str.substring(this.actionPosition + 1, str.length()));
                        str = stringBuilder.toString();
                        start--;
                    }
                    stringBuilder = new StringBuilder(str.length());
                    for (int a = 0; a < str.length(); a++) {
                        String ch = str.substring(a, a + 1);
                        if (phoneChars.contains(ch)) {
                            stringBuilder.append(ch);
                        }
                    }
                    NewContactActivity.this.ignoreOnPhoneChange = true;
                    String hint = NewContactActivity.this.phoneField.getHintText();
                    if (hint != null) {
                        int start2 = start;
                        start = 0;
                        while (start < stringBuilder.length()) {
                            if (start < hint.length()) {
                                if (hint.charAt(start) == ' ') {
                                    stringBuilder.insert(start, ' ');
                                    start++;
                                    if (!(start2 != start || this.characterAction == 2 || this.characterAction == 3)) {
                                        start2++;
                                    }
                                }
                                start++;
                            } else {
                                stringBuilder.insert(start, ' ');
                                if (!(start2 != start + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                    start = start2 + 1;
                                }
                                start = start2;
                            }
                        }
                        start = start2;
                    }
                    NewContactActivity.this.phoneField.setText(stringBuilder);
                    if (start >= 0) {
                        NewContactActivity.this.phoneField.setSelection(start <= NewContactActivity.this.phoneField.length() ? start : NewContactActivity.this.phoneField.length());
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                NewContactActivity.this.editDoneItem.performClick();
                return true;
            }
        });
        HashMap<String, String> languageMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                r1.countriesArray.add(i, args[2]);
                r1.countriesMap.put(args[2], args[i]);
                r1.codesMap.put(args[i], args[2]);
                if (args.length > 3) {
                    r1.phoneFormatMap.put(args[i], args[3]);
                }
                languageMap.put(args[1], args[2]);
                i = 0;
            }
            reader.close();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        Collections.sort(r1.countriesArray, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        String country = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                country = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        if (country != null) {
            String countryName = (String) languageMap.get(country);
            if (!(countryName == null || r1.countriesArray.indexOf(countryName) == -1)) {
                r1.codeField.setText((CharSequence) r1.countriesMap.get(countryName));
                r1.countryState = 0;
            }
        }
        if (r1.codeField.length() == 0) {
            r1.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
            r1.phoneField.setHintText(null);
            r1.countryState = 1;
        }
        return r1.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void selectCountry(String name) {
        if (this.countriesArray.indexOf(name) != -1) {
            this.ignoreOnTextChange = true;
            String code = (String) this.countriesMap.get(name);
            this.codeField.setText(code);
            this.countryButton.setText(name);
            String hint = (String) this.phoneFormatMap.get(code);
            this.phoneField.setHintText(hint != null ? hint.replace('X', '\u2013') : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        if (this.editDoneItemAnimation != null) {
            r0.editDoneItemAnimation.cancel();
        }
        if (animated) {
            r0.editDoneItemAnimation = new AnimatorSet();
            Animator[] animatorArr;
            if (z) {
                r0.editDoneItemProgress.setVisibility(0);
                r0.editDoneItem.setEnabled(false);
                AnimatorSet animatorSet = r0.editDoneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                r0.editDoneItem.getImageView().setVisibility(0);
                r0.editDoneItem.setEnabled(true);
                AnimatorSet animatorSet2 = r0.editDoneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.editDoneItemProgress, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.editDoneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            }
            r0.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        if (z) {
                            NewContactActivity.this.editDoneItem.getImageView().setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            r0.editDoneItemAnimation.setDuration(150);
            r0.editDoneItemAnimation.start();
        } else if (z) {
            r0.editDoneItem.getImageView().setScaleX(0.1f);
            r0.editDoneItem.getImageView().setScaleY(0.1f);
            r0.editDoneItem.getImageView().setAlpha(0.0f);
            r0.editDoneItemProgress.setScaleX(1.0f);
            r0.editDoneItemProgress.setScaleY(1.0f);
            r0.editDoneItemProgress.setAlpha(1.0f);
            r0.editDoneItem.getImageView().setVisibility(4);
            r0.editDoneItemProgress.setVisibility(0);
            r0.editDoneItem.setEnabled(false);
        } else {
            r0.editDoneItemProgress.setScaleX(0.1f);
            r0.editDoneItemProgress.setScaleY(0.1f);
            r0.editDoneItemProgress.setAlpha(0.0f);
            r0.editDoneItem.getImageView().setScaleX(1.0f);
            r0.editDoneItem.getImageView().setScaleY(1.0f);
            r0.editDoneItem.getImageView().setAlpha(1.0f);
            r0.editDoneItem.getImageView().setVisibility(0);
            r0.editDoneItemProgress.setVisibility(4);
            r0.editDoneItem.setEnabled(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (NewContactActivity.this.avatarImage != null) {
                    NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
                    NewContactActivity.this.avatarImage.invalidate();
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[34];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[7] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[8] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[9] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[10] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[12] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[13] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[14] = new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[16] = new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[17] = new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[19] = new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[20] = new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[21] = new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[22] = new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhiteGrayLine);
        themeDescriptionArr[23] = new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[24] = new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, Theme.key_contextProgressInner2);
        themeDescriptionArr[25] = new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, Theme.key_contextProgressOuter2);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
