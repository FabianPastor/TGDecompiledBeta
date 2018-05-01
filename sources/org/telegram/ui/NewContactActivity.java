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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C15342() {
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$3 */
    class C15353 implements OnEditorActionListener {
        C15353() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return null;
            }
            NewContactActivity.this.lastNameField.requestFocus();
            NewContactActivity.this.lastNameField.setSelection(NewContactActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$4 */
    class C15364 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15364() {
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
                return null;
            }
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$6 */
    class C15386 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15386() {
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

            public void didSelectCountry(String str, String str2) {
                NewContactActivity.this.selectCountry(str);
                AndroidUtilities.runOnUIThread(new C15391(), 300);
                NewContactActivity.this.phoneField.requestFocus();
                NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            }
        }

        C15407() {
        }

        public void onClick(View view) {
            view = new CountrySelectActivity(true);
            view.setCountrySelectActivityDelegate(new C22081());
            NewContactActivity.this.presentFragment(view);
        }
    }

    /* renamed from: org.telegram.ui.NewContactActivity$8 */
    class C15418 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15418() {
        }

        public void afterTextChanged(Editable editable) {
            if (NewContactActivity.this.ignoreOnTextChange == null) {
                NewContactActivity.this.ignoreOnTextChange = true;
                editable = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                NewContactActivity.this.codeField.setText(editable);
                String str = null;
                if (editable.length() == 0) {
                    NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
                    NewContactActivity.this.phoneField.setHintText(null);
                    NewContactActivity.this.countryState = 1;
                } else {
                    Object substring;
                    CharSequence charSequence;
                    int i = 4;
                    if (editable.length() > 4) {
                        NewContactActivity.this.ignoreOnTextChange = true;
                        while (i >= 1) {
                            substring = editable.substring(0, i);
                            if (((String) NewContactActivity.this.codesMap.get(substring)) != null) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(editable.substring(i, editable.length()));
                                stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                                editable = stringBuilder.toString();
                                NewContactActivity.this.codeField.setText(substring);
                                charSequence = editable;
                                editable = 1;
                                break;
                            }
                            i--;
                        }
                        substring = editable;
                        editable = null;
                        charSequence = null;
                        if (editable == null) {
                            NewContactActivity.this.ignoreOnTextChange = true;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(substring.substring(1, substring.length()));
                            stringBuilder2.append(NewContactActivity.this.phoneField.getText().toString());
                            charSequence = stringBuilder2.toString();
                            EditTextBoldCursor access$200 = NewContactActivity.this.codeField;
                            substring = substring.substring(0, 1);
                            access$200.setText(substring);
                        }
                    } else {
                        substring = editable;
                        editable = null;
                        charSequence = null;
                    }
                    String str2 = (String) NewContactActivity.this.codesMap.get(substring);
                    if (str2 != null) {
                        int indexOf = NewContactActivity.this.countriesArray.indexOf(str2);
                        if (indexOf != -1) {
                            NewContactActivity.this.ignoreSelection = true;
                            NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(indexOf));
                            String str3 = (String) NewContactActivity.this.phoneFormatMap.get(substring);
                            HintEditText access$300 = NewContactActivity.this.phoneField;
                            if (str3 != null) {
                                str = str3.replace('X', '\u2013');
                            }
                            access$300.setHintText(str);
                            NewContactActivity.this.countryState = 0;
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", C0446R.string.WrongCountry));
                            NewContactActivity.this.phoneField.setHintText(null);
                            NewContactActivity.this.countryState = 2;
                        }
                    } else {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", C0446R.string.WrongCountry));
                        NewContactActivity.this.phoneField.setHintText(null);
                        NewContactActivity.this.countryState = 2;
                    }
                    if (editable == null) {
                        NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                    }
                    if (charSequence != null) {
                        NewContactActivity.this.phoneField.requestFocus();
                        NewContactActivity.this.phoneField.setText(charSequence);
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
                return null;
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

        public void onItemClick(int i) {
            if (i == -1) {
                NewContactActivity.this.finishFragment();
            } else if (i != 1 || NewContactActivity.this.donePressed != 0) {
            } else {
                Vibrator vibrator;
                if (NewContactActivity.this.firstNameField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                } else if (NewContactActivity.this.codeField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                } else if (NewContactActivity.this.phoneField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                } else {
                    NewContactActivity.this.donePressed = true;
                    NewContactActivity.this.showEditDoneProgress(true, true);
                    i = new TL_contacts_importContacts();
                    final TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
                    tL_inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                    tL_inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(NewContactActivity.this.codeField.getText().toString());
                    stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                    tL_inputPhoneContact.phone = stringBuilder.toString();
                    i.contacts.add(tL_inputPhoneContact);
                    ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(i, new RequestDelegate() {
                        public void run(TLObject tLObject, final TL_error tL_error) {
                            final TL_contacts_importedContacts tL_contacts_importedContacts = (TL_contacts_importedContacts) tLObject;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.ui.NewContactActivity$1$1$1$1 */
                                class C15321 implements DialogInterface.OnClickListener {
                                    C15321() {
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            dialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", tL_inputPhoneContact.phone, null));
                                            dialogInterface.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                                            NewContactActivity.this.getParentActivity().startActivityForResult(dialogInterface, 500);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                }

                                public void run() {
                                    NewContactActivity.this.donePressed = false;
                                    if (tL_contacts_importedContacts == null) {
                                        NewContactActivity.this.showEditDoneProgress(false, true);
                                        AlertsCreator.processError(NewContactActivity.this.currentAccount, tL_error, NewContactActivity.this, i, new Object[0]);
                                    } else if (!tL_contacts_importedContacts.users.isEmpty()) {
                                        MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(tL_contacts_importedContacts.users, false);
                                        MessagesController.openChatOrProfileWith((User) tL_contacts_importedContacts.users.get(0), null, NewContactActivity.this, 1, true);
                                    } else if (NewContactActivity.this.getParentActivity() != null) {
                                        NewContactActivity.this.showEditDoneProgress(false, true);
                                        Builder builder = new Builder(NewContactActivity.this.getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                        builder.setMessage(LocaleController.formatString("ContactNotRegistered", C0446R.string.ContactNotRegistered, ContactsController.formatName(tL_inputPhoneContact.first_name, tL_inputPhoneContact.last_name)));
                                        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                        builder.setPositiveButton(LocaleController.getString("Invite", C0446R.string.Invite), new C15321());
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

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public View createView(Context context) {
        String readLine;
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C22071());
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setInfo(5, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, false);
        this.editDoneItem = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.editDoneItemProgress = new ContextProgressView(context2, 1);
        this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context2);
        View linearLayout = new LinearLayout(context2);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new C15342());
        View frameLayout = new FrameLayout(context2);
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
        this.firstNameField.setHint(LocaleController.getString("FirstName", C0446R.string.FirstName));
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
        this.lastNameField.setHint(LocaleController.getString("LastName", C0446R.string.LastName));
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
        this.countryButton.setBackgroundResource(C0446R.drawable.spinner_states);
        linearLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new C15407());
        this.lineView = new View(context2);
        this.lineView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
        linearLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        frameLayout = new LinearLayout(context2);
        frameLayout.setOrientation(0);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        this.textView = new TextView(context2);
        this.textView.setText("+");
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 18.0f);
        frameLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
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
        frameLayout.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
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
        frameLayout.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
        this.phoneField.addTextChangedListener(new TextWatcher() {
            private int actionPosition;
            private int characterAction = -1;

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == 1) {
                    this.characterAction = 1;
                } else if (i2 != 1 || i3 != 0) {
                    this.characterAction = -1;
                } else if (charSequence.charAt(i) != 32 || i <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = i - 1;
                }
            }

            public void afterTextChanged(Editable editable) {
                if (NewContactActivity.this.ignoreOnPhoneChange == null) {
                    editable = NewContactActivity.this.phoneField.getSelectionStart();
                    String str = "0123456789";
                    String obj = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(obj.substring(0, this.actionPosition));
                        stringBuilder.append(obj.substring(this.actionPosition + 1, obj.length()));
                        obj = stringBuilder.toString();
                        editable--;
                    }
                    CharSequence stringBuilder2 = new StringBuilder(obj.length());
                    int i = 0;
                    while (i < obj.length()) {
                        int i2 = i + 1;
                        Object substring = obj.substring(i, i2);
                        if (str.contains(substring)) {
                            stringBuilder2.append(substring);
                        }
                        i = i2;
                    }
                    NewContactActivity.this.ignoreOnPhoneChange = true;
                    str = NewContactActivity.this.phoneField.getHintText();
                    if (str != null) {
                        int i3 = editable;
                        editable = null;
                        while (editable < stringBuilder2.length()) {
                            if (editable < str.length()) {
                                if (str.charAt(editable) == ' ') {
                                    stringBuilder2.insert(editable, ' ');
                                    editable++;
                                    if (!(i3 != editable || this.characterAction == 2 || this.characterAction == 3)) {
                                        i3++;
                                    }
                                }
                                editable += 1;
                            } else {
                                stringBuilder2.insert(editable, ' ');
                                if (!(i3 != editable + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                    editable = i3 + 1;
                                }
                                editable = i3;
                            }
                        }
                        editable = i3;
                    }
                    NewContactActivity.this.phoneField.setText(stringBuilder2);
                    if (editable >= null) {
                        HintEditText access$300 = NewContactActivity.this.phoneField;
                        if (editable > NewContactActivity.this.phoneField.length()) {
                            editable = NewContactActivity.this.phoneField.length();
                        }
                        access$300.setSelection(editable);
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return null;
                }
                NewContactActivity.this.editDoneItem.performClick();
                return true;
            }
        });
        HashMap hashMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                r1.countriesArray.add(0, split[2]);
                r1.countriesMap.put(split[2], split[0]);
                r1.codesMap.put(split[0], split[2]);
                if (split.length > 3) {
                    r1.phoneFormatMap.put(split[0], split[3]);
                }
                hashMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        Collections.sort(r1.countriesArray, new Comparator<String>() {
            public int compare(String str, String str2) {
                return str.compareTo(str2);
            }
        });
        Object obj = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                obj = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        if (obj != null) {
            readLine = (String) hashMap.get(obj);
            if (!(readLine == null || r1.countriesArray.indexOf(readLine) == -1)) {
                r1.codeField.setText((CharSequence) r1.countriesMap.get(readLine));
                r1.countryState = 0;
            }
        }
        if (r1.codeField.length() == 0) {
            r1.countryButton.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
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

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void selectCountry(String str) {
        if (this.countriesArray.indexOf(str) != -1) {
            this.ignoreOnTextChange = true;
            String str2 = (String) this.countriesMap.get(str);
            this.codeField.setText(str2);
            this.countryButton.setText(str);
            str = (String) this.phoneFormatMap.get(str2);
            this.phoneField.setHintText(str != null ? str.replace('X', '\u2013') : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.ignoreSelection != null) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    private void showEditDoneProgress(final boolean z, boolean z2) {
        if (this.editDoneItemAnimation != null) {
            this.editDoneItemAnimation.cancel();
        }
        if (z2) {
            this.editDoneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (z) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.editDoneItem.getImageView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator) != null) {
                        if (z == null) {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItem.getImageView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator) != null) {
                        NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z) {
            this.editDoneItem.getImageView().setScaleX(0.1f);
            this.editDoneItem.getImageView().setScaleY(0.1f);
            this.editDoneItem.getImageView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(1.0f);
            this.editDoneItemProgress.setScaleY(1.0f);
            this.editDoneItemProgress.setAlpha(1.0f);
            this.editDoneItem.getImageView().setVisibility(4);
            this.editDoneItemProgress.setVisibility(0);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getImageView().setScaleX(1.0f);
            this.editDoneItem.getImageView().setScaleY(1.0f);
            this.editDoneItem.getImageView().setAlpha(1.0f);
            this.editDoneItem.getImageView().setVisibility(0);
            this.editDoneItemProgress.setVisibility(4);
            this.editDoneItem.setEnabled(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r10 = new ThemeDescription[34];
        AnonymousClass14 anonymousClass14 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (NewContactActivity.this.avatarImage != null) {
                    NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
                    NewContactActivity.this.avatarImage.invalidate();
                }
            }
        };
        r10[26] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, anonymousClass14, Theme.key_avatar_text);
        r10[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundRed);
        r10[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundOrange);
        r10[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundViolet);
        r10[30] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundGreen);
        r10[31] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundCyan);
        r10[32] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundBlue);
        r10[33] = new ThemeDescription(null, 0, null, null, null, anonymousClass14, Theme.key_avatar_backgroundPink);
        return r10;
    }
}
