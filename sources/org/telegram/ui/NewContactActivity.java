package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity;

public class NewContactActivity extends BaseFragment implements AdapterView.OnItemSelectedListener {
    private static final int done_button = 1;
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
    private boolean initialPhoneNumberWithCountryCode;
    /* access modifiers changed from: private */
    public EditTextBoldCursor lastNameField;
    private View lineView;
    /* access modifiers changed from: private */
    public HintEditText phoneField;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap = new HashMap<>();
    private TextView textView;

    public View createView(Context context) {
        boolean needInvalidateAvatar;
        String countryName;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NewContactActivity.this.finishFragment();
                } else if (id == 1 && !NewContactActivity.this.donePressed) {
                    if (NewContactActivity.this.firstNameField.length() == 0) {
                        Vibrator v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                    } else if (NewContactActivity.this.codeField.length() == 0) {
                        Vibrator v2 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v2 != null) {
                            v2.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                    } else if (NewContactActivity.this.phoneField.length() == 0) {
                        Vibrator v3 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v3 != null) {
                            v3.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                    } else {
                        boolean unused = NewContactActivity.this.donePressed = true;
                        NewContactActivity.this.showEditDoneProgress(true, true);
                        TLRPC.TL_contacts_importContacts req = new TLRPC.TL_contacts_importContacts();
                        TLRPC.TL_inputPhoneContact inputPhoneContact = new TLRPC.TL_inputPhoneContact();
                        inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                        inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                        inputPhoneContact.phone = "+" + NewContactActivity.this.codeField.getText().toString() + NewContactActivity.this.phoneField.getText().toString();
                        req.contacts.add(inputPhoneContact);
                        ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(req, new NewContactActivity$1$$ExternalSyntheticLambda2(this, inputPhoneContact, req), 2), NewContactActivity.this.classGuid);
                    }
                }
            }

            /* renamed from: lambda$onItemClick$2$org-telegram-ui-NewContactActivity$1  reason: not valid java name */
            public /* synthetic */ void m3322lambda$onItemClick$2$orgtelegramuiNewContactActivity$1(TLRPC.TL_inputPhoneContact inputPhoneContact, TLRPC.TL_contacts_importContacts req, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new NewContactActivity$1$$ExternalSyntheticLambda1(this, (TLRPC.TL_contacts_importedContacts) response, inputPhoneContact, error, req));
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-NewContactActivity$1  reason: not valid java name */
            public /* synthetic */ void m3321lambda$onItemClick$1$orgtelegramuiNewContactActivity$1(TLRPC.TL_contacts_importedContacts res, TLRPC.TL_inputPhoneContact inputPhoneContact, TLRPC.TL_error error, TLRPC.TL_contacts_importContacts req) {
                boolean unused = NewContactActivity.this.donePressed = false;
                if (res == null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    AlertsCreator.processError(NewContactActivity.this.currentAccount, error, NewContactActivity.this, req, new Object[0]);
                } else if (!res.users.isEmpty()) {
                    MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(res.users, false);
                    MessagesController.openChatOrProfileWith(res.users.get(0), (TLRPC.Chat) null, NewContactActivity.this, 1, true);
                } else if (NewContactActivity.this.getParentActivity() != null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) NewContactActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ContactNotRegisteredTitle", NUM));
                    builder.setMessage(LocaleController.formatString("ContactNotRegistered", NUM, ContactsController.formatName(inputPhoneContact.first_name, inputPhoneContact.last_name)));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.setPositiveButton(LocaleController.getString("Invite", NUM), new NewContactActivity$1$$ExternalSyntheticLambda0(this, inputPhoneContact));
                    NewContactActivity.this.showDialog(builder.create());
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-NewContactActivity$1  reason: not valid java name */
            public /* synthetic */ void m3320lambda$onItemClick$0$orgtelegramuiNewContactActivity$1(TLRPC.TL_inputPhoneContact inputPhoneContact, DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", inputPhoneContact.phone, (String) null));
                    intent.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                    NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        });
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setInfo(5, "", "");
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.editDoneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", NUM));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.editDoneItemProgress = contextProgressView;
        this.editDoneItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        this.contentLayout = linearLayout;
        int i = 0;
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        this.contentLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(this.contentLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.contentLayout.setOnTouchListener(NewContactActivity$$ExternalSyntheticLambda2.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.contentLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60.0f, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        boolean needInvalidateAvatar2 = false;
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
        this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        String str = this.initialFirstName;
        if (str != null) {
            this.firstNameField.setText(str);
            this.initialFirstName = null;
            needInvalidateAvatar2 = true;
        }
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new NewContactActivity$$ExternalSyntheticLambda3(this));
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.invalidateAvatar();
            }
        });
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.lastNameField = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
        this.lastNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        String str2 = this.initialLastName;
        if (str2 != null) {
            this.lastNameField.setText(str2);
            this.initialLastName = null;
            needInvalidateAvatar = true;
        } else {
            needInvalidateAvatar = needInvalidateAvatar2;
        }
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new NewContactActivity$$ExternalSyntheticLambda4(this));
        this.lastNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.invalidateAvatar();
            }
        });
        if (needInvalidateAvatar) {
            invalidateAvatar();
        }
        TextView textView2 = new TextView(context2);
        this.countryButton = textView2;
        textView2.setTextSize(1, 18.0f);
        this.countryButton.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        this.countryButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.countryButton.setMaxLines(1);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackground(Theme.getSelectorDrawable(true));
        this.contentLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new NewContactActivity$$ExternalSyntheticLambda0(this));
        View view = new View(context2);
        this.lineView = view;
        view.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
        this.contentLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        this.contentLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setText("+");
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 18.0f);
        this.textView.setImportantForAccessibility(2);
        linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context2);
        this.codeField = editTextBoldCursor3;
        editTextBoldCursor3.setInputType(3);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.codeField.setCursorWidth(1.5f);
        this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0f);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(NUM);
        linearLayout2.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
        this.codeField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (!NewContactActivity.this.ignoreOnTextChange) {
                    boolean unused = NewContactActivity.this.ignoreOnTextChange = true;
                    String text = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                    NewContactActivity.this.codeField.setText(text);
                    String str = null;
                    if (text.length() == 0) {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                        NewContactActivity.this.phoneField.setHintText((String) null);
                        int unused2 = NewContactActivity.this.countryState = 1;
                    } else {
                        boolean ok = false;
                        String textToSet = null;
                        if (text.length() > 4) {
                            boolean unused3 = NewContactActivity.this.ignoreOnTextChange = true;
                            int a = 4;
                            while (true) {
                                if (a < 1) {
                                    break;
                                }
                                String sub = text.substring(0, a);
                                if (((String) NewContactActivity.this.codesMap.get(sub)) != null) {
                                    ok = true;
                                    textToSet = text.substring(a) + NewContactActivity.this.phoneField.getText().toString();
                                    text = sub;
                                    NewContactActivity.this.codeField.setText(sub);
                                    break;
                                }
                                a--;
                            }
                            if (!ok) {
                                boolean unused4 = NewContactActivity.this.ignoreOnTextChange = true;
                                textToSet = text.substring(1) + NewContactActivity.this.phoneField.getText().toString();
                                EditTextBoldCursor access$200 = NewContactActivity.this.codeField;
                                String substring = text.substring(0, 1);
                                text = substring;
                                access$200.setText(substring);
                            }
                        }
                        String country = (String) NewContactActivity.this.codesMap.get(text);
                        if (country != null) {
                            int index = NewContactActivity.this.countriesArray.indexOf(country);
                            if (index != -1) {
                                boolean unused5 = NewContactActivity.this.ignoreSelection = true;
                                NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(index));
                                String hint = (String) NewContactActivity.this.phoneFormatMap.get(text);
                                HintEditText access$300 = NewContactActivity.this.phoneField;
                                if (hint != null) {
                                    str = hint.replace('X', 8211);
                                }
                                access$300.setHintText(str);
                                int unused6 = NewContactActivity.this.countryState = 0;
                            } else {
                                NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
                                NewContactActivity.this.phoneField.setHintText((String) null);
                                int unused7 = NewContactActivity.this.countryState = 2;
                            }
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
                            NewContactActivity.this.phoneField.setHintText((String) null);
                            int unused8 = NewContactActivity.this.countryState = 2;
                        }
                        if (!ok) {
                            NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                        }
                        if (textToSet != null) {
                            if (NewContactActivity.this.initialPhoneNumber == null) {
                                NewContactActivity.this.phoneField.requestFocus();
                            }
                            NewContactActivity.this.phoneField.setText(textToSet);
                            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
                        }
                    }
                    boolean unused9 = NewContactActivity.this.ignoreOnTextChange = false;
                }
            }
        });
        this.codeField.setOnEditorActionListener(new NewContactActivity$$ExternalSyntheticLambda5(this));
        HintEditText hintEditText = new HintEditText(context2);
        this.phoneField = hintEditText;
        hintEditText.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.phoneField.setCursorWidth(1.5f);
        this.phoneField.setTextSize(1, 18.0f);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(NUM);
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
                int i;
                int i2;
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    int start = NewContactActivity.this.phoneField.getSelectionStart();
                    String str = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                        start--;
                    }
                    StringBuilder builder = new StringBuilder(str.length());
                    for (int a = 0; a < str.length(); a++) {
                        String ch = str.substring(a, a + 1);
                        if ("NUM".contains(ch)) {
                            builder.append(ch);
                        }
                    }
                    boolean unused = NewContactActivity.this.ignoreOnPhoneChange = true;
                    String hint = NewContactActivity.this.phoneField.getHintText();
                    if (hint != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= builder.length()) {
                                break;
                            } else if (a2 < hint.length()) {
                                if (hint.charAt(a2) == ' ') {
                                    builder.insert(a2, ' ');
                                    a2++;
                                    if (!(start != a2 || (i2 = this.characterAction) == 2 || i2 == 3)) {
                                        start++;
                                    }
                                }
                                a2++;
                            } else {
                                builder.insert(a2, ' ');
                                if (start == a2 + 1 && (i = this.characterAction) != 2 && i != 3) {
                                    start++;
                                }
                            }
                        }
                    }
                    NewContactActivity.this.phoneField.setText(builder);
                    if (start >= 0) {
                        NewContactActivity.this.phoneField.setSelection(Math.min(start, NewContactActivity.this.phoneField.length()));
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    boolean unused2 = NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new NewContactActivity$$ExternalSyntheticLambda6(this));
        this.phoneField.setOnKeyListener(new NewContactActivity$$ExternalSyntheticLambda1(this));
        HashMap hashMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                this.countriesArray.add(i, args[2]);
                this.countriesMap.put(args[2], args[i]);
                this.codesMap.put(args[i], args[2]);
                if (args.length > 3) {
                    this.phoneFormatMap.put(args[0], args[3]);
                }
                hashMap.put(args[1], args[2]);
                i = 0;
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Collections.sort(this.countriesArray, ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6.INSTANCE);
        if (!TextUtils.isEmpty(this.initialPhoneNumber)) {
            TLRPC.User user = getUserConfig().getCurrentUser();
            if (this.initialPhoneNumber.startsWith("+")) {
                this.codeField.setText(this.initialPhoneNumber.substring(1));
            } else if (this.initialPhoneNumberWithCountryCode || user == null || TextUtils.isEmpty(user.phone)) {
                this.codeField.setText(this.initialPhoneNumber);
            } else {
                String phone = user.phone;
                int a = 4;
                while (true) {
                    if (a < 1) {
                        break;
                    }
                    String sub = phone.substring(0, a);
                    if (this.codesMap.get(sub) != null) {
                        this.codeField.setText(sub);
                        break;
                    }
                    a--;
                }
                this.phoneField.setText(this.initialPhoneNumber);
            }
            this.initialPhoneNumber = null;
        } else {
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (!(country == null || (countryName = (String) hashMap.get(country)) == null || this.countriesArray.indexOf(countryName) == -1)) {
                this.codeField.setText(this.countriesMap.get(countryName));
                this.countryState = 0;
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                this.phoneField.setHintText((String) null);
                this.countryState = 1;
            }
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ boolean m3312lambda$createView$1$orgtelegramuiNewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ boolean m3313lambda$createView$2$orgtelegramuiNewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ void m3315lambda$createView$4$orgtelegramuiNewContactActivity(View view) {
        CountrySelectActivity fragment = new CountrySelectActivity(true);
        fragment.setCountrySelectActivityDelegate(new NewContactActivity$$ExternalSyntheticLambda8(this));
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ void m3314lambda$createView$3$orgtelegramuiNewContactActivity(CountrySelectActivity.Country country) {
        selectCountry(country.name);
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ boolean m3316lambda$createView$5$orgtelegramuiNewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ boolean m3317lambda$createView$6$orgtelegramuiNewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.editDoneItem.performClick();
        return true;
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ boolean m3318lambda$createView$7$orgtelegramuiNewContactActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode != 67 || this.phoneField.length() != 0) {
            return false;
        }
        this.codeField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.codeField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.codeField.dispatchKeyEvent(event);
        return true;
    }

    public static String getPhoneNumber(Context context, TLRPC.User user, String number, boolean withCoutryCode) {
        HashMap<String, String> codesMap2 = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                codesMap2.put(args[0], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (number.startsWith("+")) {
            return number;
        }
        if (withCoutryCode || user == null || TextUtils.isEmpty(user.phone)) {
            return "+" + number;
        }
        String phone = user.phone;
        for (int a = 4; a >= 1; a--) {
            String sub = phone.substring(0, a);
            if (codesMap2.get(sub) != null) {
                return "+" + sub + number;
            }
        }
        return number;
    }

    /* access modifiers changed from: private */
    public void invalidateAvatar() {
        this.avatarDrawable.setInfo(5, this.firstNameField.getText().toString(), this.lastNameField.getText().toString());
        this.avatarImage.invalidate();
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            View focusedView = this.contentLayout.findFocus();
            if (focusedView == null) {
                this.firstNameField.requestFocus();
                focusedView = this.firstNameField;
            }
            AndroidUtilities.showKeyboard(focusedView);
        }
    }

    public void setInitialPhoneNumber(String value, boolean withCoutryCode) {
        this.initialPhoneNumber = value;
        this.initialPhoneNumberWithCountryCode = withCoutryCode;
    }

    public void setInitialName(String firstName, String lastName) {
        this.initialFirstName = firstName;
        this.initialLastName = lastName;
    }

    public void selectCountry(String name) {
        if (this.countriesArray.indexOf(name) != -1) {
            this.ignoreOnTextChange = true;
            String code = this.countriesMap.get(name);
            this.codeField.setText(code);
            this.countryButton.setText(name);
            String hint = this.phoneFormatMap.get(code);
            this.phoneField.setHintText(hint != null ? hint.replace('X', 8211) : null);
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
        this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        AnimatorSet animatorSet = this.editDoneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.editDoneItemAnimation = new AnimatorSet();
            if (z) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{1.0f})});
            } else {
                this.editDoneItem.getContentView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        if (!z) {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        AnimatorSet unused = NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z) {
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
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new NewContactActivity$$ExternalSyntheticLambda7(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayLine"));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.countryButton, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        themeDescriptions.add(new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, cellDelegate, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$8$org-telegram-ui-NewContactActivity  reason: not valid java name */
    public /* synthetic */ void m3319lambda$getThemeDescriptions$8$orgtelegramuiNewContactActivity() {
        if (this.avatarImage != null) {
            invalidateAvatar();
        }
    }
}
