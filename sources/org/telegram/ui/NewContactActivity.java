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
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;

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

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public View createView(Context context) {
        String readLine;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    NewContactActivity.this.finishFragment();
                } else if (i == 1 && !NewContactActivity.this.donePressed) {
                    String str = "vibrator";
                    Vibrator vibrator;
                    if (NewContactActivity.this.firstNameField.length() == 0) {
                        vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService(str);
                        if (vibrator != null) {
                            vibrator.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                    } else if (NewContactActivity.this.codeField.length() == 0) {
                        vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService(str);
                        if (vibrator != null) {
                            vibrator.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                    } else if (NewContactActivity.this.phoneField.length() == 0) {
                        vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService(str);
                        if (vibrator != null) {
                            vibrator.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                    } else {
                        NewContactActivity.this.donePressed = true;
                        NewContactActivity.this.showEditDoneProgress(true, true);
                        TL_contacts_importContacts tL_contacts_importContacts = new TL_contacts_importContacts();
                        TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
                        tL_inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                        tL_inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("+");
                        stringBuilder.append(NewContactActivity.this.codeField.getText().toString());
                        stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                        tL_inputPhoneContact.phone = stringBuilder.toString();
                        tL_contacts_importContacts.contacts.add(tL_inputPhoneContact);
                        ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(tL_contacts_importContacts, new -$$Lambda$NewContactActivity$1$WRq0Ss-PBCngsAibqDEMoSm52R4(this, tL_inputPhoneContact, tL_contacts_importContacts), 2), NewContactActivity.this.classGuid);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$NewContactActivity$1(TL_inputPhoneContact tL_inputPhoneContact, TL_contacts_importContacts tL_contacts_importContacts, TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$NewContactActivity$1$FpIczF_U6R8AHoZNqQVCZxCiBTs(this, (TL_contacts_importedContacts) tLObject, tL_inputPhoneContact, tL_error, tL_contacts_importContacts));
            }

            public /* synthetic */ void lambda$null$1$NewContactActivity$1(TL_contacts_importedContacts tL_contacts_importedContacts, TL_inputPhoneContact tL_inputPhoneContact, TL_error tL_error, TL_contacts_importContacts tL_contacts_importContacts) {
                NewContactActivity.this.donePressed = false;
                if (tL_contacts_importedContacts == null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    AlertsCreator.processError(NewContactActivity.this.currentAccount, tL_error, NewContactActivity.this, tL_contacts_importContacts, new Object[0]);
                } else if (!tL_contacts_importedContacts.users.isEmpty()) {
                    MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(tL_contacts_importedContacts.users, false);
                    MessagesController.openChatOrProfileWith((User) tL_contacts_importedContacts.users.get(0), null, NewContactActivity.this, 1, true);
                } else if (NewContactActivity.this.getParentActivity() != null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    Builder builder = new Builder(NewContactActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.formatString("ContactNotRegistered", NUM, ContactsController.formatName(tL_inputPhoneContact.first_name, tL_inputPhoneContact.last_name)));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    builder.setPositiveButton(LocaleController.getString("Invite", NUM), new -$$Lambda$NewContactActivity$1$D3bcNIiNfpYsfTq2W2DLeJJEU84(this, tL_inputPhoneContact));
                    NewContactActivity.this.showDialog(builder.create());
                }
            }

            public /* synthetic */ void lambda$null$0$NewContactActivity$1(TL_inputPhoneContact tL_inputPhoneContact, DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", tL_inputPhoneContact.phone, null));
                    intent.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                    NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        });
        this.avatarDrawable = new AvatarDrawable();
        String str = "";
        this.avatarDrawable.setInfo(5, str, str, false);
        this.editDoneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.editDoneItem.setContentDescription(LocaleController.getString("Done", NUM));
        this.editDoneItemProgress = new ContextProgressView(context2, 1);
        this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(-$$Lambda$NewContactActivity$dyt1ArQHbLSL06GO-wtQQZkRhQE.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60.0f, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        String str2 = "windowBackgroundWhiteHintText";
        this.firstNameField.setHintTextColor(Theme.getColor(str2));
        String str3 = "windowBackgroundWhiteBlackText";
        this.firstNameField.setTextColor(Theme.getColor(str3));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
        this.firstNameField.setCursorColor(Theme.getColor(str3));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new -$$Lambda$NewContactActivity$OEffd5rsJU1asHgIO5gqt5wMyr4(this));
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
                NewContactActivity.this.avatarImage.invalidate();
            }
        });
        this.lastNameField = new EditTextBoldCursor(context2);
        this.lastNameField.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(str2));
        this.lastNameField.setTextColor(Theme.getColor(str3));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
        this.lastNameField.setCursorColor(Theme.getColor(str3));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new -$$Lambda$NewContactActivity$caCQM7G1cFeQM5WDamEHC5G1jDk(this));
        this.lastNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
                NewContactActivity.this.avatarImage.invalidate();
            }
        });
        this.countryButton = new TextView(context2);
        this.countryButton.setTextSize(1, 18.0f);
        this.countryButton.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f), 0);
        this.countryButton.setTextColor(Theme.getColor(str3));
        this.countryButton.setMaxLines(1);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackgroundResource(NUM);
        linearLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new -$$Lambda$NewContactActivity$PQwWlWssBZjUKNZQJ0dIzeJV9OI(this));
        this.lineView = new View(context2);
        this.lineView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
        linearLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        this.textView = new TextView(context2);
        this.textView.setText("+");
        this.textView.setTextColor(Theme.getColor(str3));
        this.textView.setTextSize(1, 18.0f);
        this.textView.setImportantForAccessibility(2);
        linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditTextBoldCursor(context2);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(Theme.getColor(str3));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.codeField.setCursorColor(Theme.getColor(str3));
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
                    NewContactActivity.this.ignoreOnTextChange = true;
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                    NewContactActivity.this.codeField.setText(stripExceptNumbers);
                    String str = null;
                    if (stripExceptNumbers.length() == 0) {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                        NewContactActivity.this.phoneField.setHintText(null);
                        NewContactActivity.this.countryState = 1;
                    } else {
                        Object substring;
                        CharSequence charSequence;
                        Object obj;
                        int i = 4;
                        if (stripExceptNumbers.length() > 4) {
                            NewContactActivity.this.ignoreOnTextChange = true;
                            while (i >= 1) {
                                substring = stripExceptNumbers.substring(0, i);
                                if (((String) NewContactActivity.this.codesMap.get(substring)) != null) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(stripExceptNumbers.substring(i, stripExceptNumbers.length()));
                                    stringBuilder.append(NewContactActivity.this.phoneField.getText().toString());
                                    stripExceptNumbers = stringBuilder.toString();
                                    NewContactActivity.this.codeField.setText(substring);
                                    charSequence = stripExceptNumbers;
                                    obj = 1;
                                    break;
                                }
                                i--;
                            }
                            substring = stripExceptNumbers;
                            charSequence = null;
                            obj = null;
                            if (obj == null) {
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
                            substring = stripExceptNumbers;
                            charSequence = null;
                            obj = null;
                        }
                        String str2 = (String) NewContactActivity.this.codesMap.get(substring);
                        String str3 = "WrongCountry";
                        if (str2 != null) {
                            int indexOf = NewContactActivity.this.countriesArray.indexOf(str2);
                            if (indexOf != -1) {
                                NewContactActivity.this.ignoreSelection = true;
                                NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(indexOf));
                                String str4 = (String) NewContactActivity.this.phoneFormatMap.get(substring);
                                HintEditText access$300 = NewContactActivity.this.phoneField;
                                if (str4 != null) {
                                    str = str4.replace('X', 8211);
                                }
                                access$300.setHintText(str);
                                NewContactActivity.this.countryState = 0;
                            } else {
                                NewContactActivity.this.countryButton.setText(LocaleController.getString(str3, NUM));
                                NewContactActivity.this.phoneField.setHintText(null);
                                NewContactActivity.this.countryState = 2;
                            }
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString(str3, NUM));
                            NewContactActivity.this.phoneField.setHintText(null);
                            NewContactActivity.this.countryState = 2;
                        }
                        if (obj == null) {
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
        });
        this.codeField.setOnEditorActionListener(new -$$Lambda$NewContactActivity$sAQJMXy-aH9t_IBkPAm16jh4ito(this));
        this.phoneField = new HintEditText(context2);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor(str3));
        this.phoneField.setHintTextColor(Theme.getColor(str2));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor(str3));
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

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == 1) {
                    this.characterAction = 1;
                } else if (i2 != 1 || i3 != 0) {
                    this.characterAction = -1;
                } else if (charSequence.charAt(i) != ' ' || i <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = i - 1;
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    StringBuilder stringBuilder;
                    int i;
                    int selectionStart = NewContactActivity.this.phoneField.getSelectionStart();
                    String obj = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(obj.substring(0, this.actionPosition));
                        stringBuilder.append(obj.substring(this.actionPosition + 1, obj.length()));
                        obj = stringBuilder.toString();
                        selectionStart--;
                    }
                    stringBuilder = new StringBuilder(obj.length());
                    int i2 = 0;
                    while (i2 < obj.length()) {
                        i = i2 + 1;
                        String substring = obj.substring(i2, i);
                        if ("NUM".contains(substring)) {
                            stringBuilder.append(substring);
                        }
                        i2 = i;
                    }
                    NewContactActivity.this.ignoreOnPhoneChange = true;
                    obj = NewContactActivity.this.phoneField.getHintText();
                    if (obj != null) {
                        i2 = selectionStart;
                        selectionStart = 0;
                        while (selectionStart < stringBuilder.length()) {
                            if (selectionStart < obj.length()) {
                                if (obj.charAt(selectionStart) == ' ') {
                                    stringBuilder.insert(selectionStart, ' ');
                                    selectionStart++;
                                    if (i2 == selectionStart) {
                                        i = this.characterAction;
                                        if (!(i == 2 || i == 3)) {
                                            i2++;
                                        }
                                    }
                                }
                                selectionStart++;
                            } else {
                                stringBuilder.insert(selectionStart, ' ');
                                if (i2 == selectionStart + 1) {
                                    selectionStart = this.characterAction;
                                    if (!(selectionStart == 2 || selectionStart == 3)) {
                                        selectionStart = i2 + 1;
                                    }
                                }
                                selectionStart = i2;
                            }
                        }
                        selectionStart = i2;
                    }
                    NewContactActivity.this.phoneField.setText(stringBuilder);
                    if (selectionStart >= 0) {
                        HintEditText access$300 = NewContactActivity.this.phoneField;
                        if (selectionStart > NewContactActivity.this.phoneField.length()) {
                            selectionStart = NewContactActivity.this.phoneField.length();
                        }
                        access$300.setSelection(selectionStart);
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new -$$Lambda$NewContactActivity$6q60KF1tjXtvySPg1IX8F4PNNEY(this));
        this.phoneField.setOnKeyListener(new -$$Lambda$NewContactActivity$5Ca3pvZCNy2Se-fsqStW34es8nQ(this));
        HashMap hashMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.countriesArray.add(0, split[2]);
                this.countriesMap.put(split[2], split[0]);
                this.codesMap.put(split[0], split[2]);
                if (split.length > 3) {
                    this.phoneFormatMap.put(split[0], split[3]);
                }
                hashMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Collections.sort(this.countriesArray, -$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE);
        Object obj = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                obj = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (obj != null) {
            readLine = (String) hashMap.get(obj);
            if (!(readLine == null || this.countriesArray.indexOf(readLine) == -1)) {
                this.codeField.setText((CharSequence) this.countriesMap.get(readLine));
                this.countryState = 0;
            }
        }
        if (this.codeField.length() == 0) {
            this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
            this.phoneField.setHintText(null);
            this.countryState = 1;
        }
        return this.fragmentView;
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
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.editDoneItem.getImageView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str3, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str2, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.editDoneItemProgress, str, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str3, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str2, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr2);
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                        if (z3) {
                            NewContactActivity.this.editDoneItem.getImageView().setVisibility(4);
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
        -$$Lambda$NewContactActivity$-TdFXabn_cMkV_y0uLVuNwOAoYk -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk = new -$$Lambda$NewContactActivity$-TdFXabn_cMkV_y0uLVuNwOAoYk(this);
        r10[26] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, -__lambda_newcontactactivity_-tdfxabn_cmkv_y0ulvunwoaoyk, "avatar_text");
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
            this.avatarDrawable.setInfo(5, this.firstNameField.getText().toString(), this.lastNameField.getText().toString(), false);
            this.avatarImage.invalidate();
        }
    }
}
