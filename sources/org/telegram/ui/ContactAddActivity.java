package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ContactAddActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private boolean addContact;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private CheckBoxCell checkBoxCell;
    private ContactAddActivityDelegate delegate;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView infoTextView;
    private EditTextBoldCursor lastNameField;
    private TextView nameTextView;
    private boolean needAddException;
    private TextView onlineTextView;
    boolean paused;
    private String phone;
    private int user_id;

    public interface ContactAddActivityDelegate {
        void didAddToContacts();
    }

    public ContactAddActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        this.user_id = getArguments().getInt("user_id", 0);
        this.phone = getArguments().getString("phone");
        this.addContact = getArguments().getBoolean("addContact", false);
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dialog_bar_exception");
        stringBuilder.append(this.user_id);
        this.needAddException = notificationsSettings.getBoolean(stringBuilder.toString(), false);
        if (getMessagesController().getUser(Integer.valueOf(this.user_id)) == null || !super.onFragmentCreate()) {
            return false;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            this.actionBar.setTitle(LocaleController.getString("NewContact", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditName", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ContactAddActivity.this.finishFragment();
                } else if (i == 1 && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                    User user = ContactAddActivity.this.getMessagesController().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                    user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                    user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                    ContactsController access$500 = ContactAddActivity.this.getContactsController();
                    boolean z = ContactAddActivity.this.checkBoxCell != null && ContactAddActivity.this.checkBoxCell.isChecked();
                    access$500.addContact(user, z);
                    Editor edit = MessagesController.getNotificationsSettings(ContactAddActivity.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("dialog_bar_vis3");
                    stringBuilder.append(ContactAddActivity.this.user_id);
                    edit.putInt(stringBuilder.toString(), 3).commit();
                    ContactAddActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                    ContactAddActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) ContactAddActivity.this.user_id));
                    ContactAddActivity.this.finishFragment();
                    if (ContactAddActivity.this.delegate != null) {
                        ContactAddActivity.this.delegate.didAddToContacts();
                    }
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, LocaleController.getString("Done", NUM).toUpperCase());
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(-$$Lambda$ContactAddActivity$A7kSn3Cfc-ajr4rigI3HkJXjVCE.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 24.0f, 0.0f));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(30.0f));
        int i = 3;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        this.nameTextView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        this.nameTextView.setTextColor(Theme.getColor(str));
        this.nameTextView.setTextSize(1, 20.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 80.0f, 3.0f, LocaleController.isRTL ? 80.0f : 0.0f, 0.0f));
        this.onlineTextView = new TextView(context2);
        this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 80.0f, 32.0f, LocaleController.isRTL ? 80.0f : 0.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        String str2 = "windowBackgroundWhiteHintText";
        this.firstNameField.setHintTextColor(Theme.getColor(str2));
        this.firstNameField.setTextColor(Theme.getColor(str));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
        this.firstNameField.setCursorColor(Theme.getColor(str));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new -$$Lambda$ContactAddActivity$xQQmG-ikgUejGCdP82NOkH8zIao(this));
        this.firstNameField.setOnFocusChangeListener(new OnFocusChangeListener() {
            boolean focued;

            public void onFocusChange(View view, boolean z) {
                if (!(ContactAddActivity.this.paused || z || !this.focued)) {
                    FileLog.d("changed");
                }
                this.focued = z;
            }
        });
        this.lastNameField = new EditTextBoldCursor(context2);
        this.lastNameField.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(str2));
        this.lastNameField.setTextColor(Theme.getColor(str));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
        this.lastNameField.setCursorColor(Theme.getColor(str));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new -$$Lambda$ContactAddActivity$LmoZEE36adLyzqPJDOoRrL7aQWs(this));
        User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
        if (user != null) {
            if (user.phone == null) {
                String str3 = this.phone;
                if (str3 != null) {
                    user.phone = PhoneFormat.stripExceptNumbers(str3);
                }
            }
            this.firstNameField.setText(user.first_name);
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.lastNameField.setText(user.last_name);
        }
        this.infoTextView = new TextView(context2);
        this.infoTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.infoTextView.setTextSize(1, 14.0f);
        TextView textView = this.infoTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        textView.setGravity(i);
        if (this.addContact) {
            if (!this.needAddException || TextUtils.isEmpty(user.phone)) {
                linearLayout.addView(this.infoTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            }
            if (this.needAddException) {
                this.checkBoxCell = new CheckBoxCell(getParentActivity(), 0);
                this.checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.checkBoxCell.setText(LocaleController.formatString("SharePhoneNumberWith", NUM, UserObject.getFirstName(user)), "", true, false);
                this.checkBoxCell.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                this.checkBoxCell.setOnClickListener(new -$$Lambda$ContactAddActivity$f_vG6dzJwQF_sWyoDFMgXkCQQaA(this));
                linearLayout.addView(this.checkBoxCell, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
            }
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ContactAddActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$ContactAddActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$3$ContactAddActivity(View view) {
        CheckBoxCell checkBoxCell = this.checkBoxCell;
        checkBoxCell.setChecked(checkBoxCell.isChecked() ^ 1, true);
    }

    public void setDelegate(ContactAddActivityDelegate contactAddActivityDelegate) {
        this.delegate = contactAddActivityDelegate;
    }

    private void updateAvatarLayout() {
        if (this.nameTextView != null) {
            Object user = getMessagesController().getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                if (TextUtils.isEmpty(user.phone)) {
                    this.nameTextView.setText(LocaleController.getString("MobileHidden", NUM));
                    this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MobileHiddenExceptionInfo", NUM, UserObject.getFirstName(user))));
                } else {
                    TextView textView = this.nameTextView;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(user.phone);
                    textView.setText(instance.format(stringBuilder.toString()));
                    if (this.needAddException) {
                        this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MobileVisibleInfo", NUM, UserObject.getFirstName(user))));
                    }
                }
                this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
                BackupImageView backupImageView = this.avatarImage;
                ImageLocation forUser = ImageLocation.getForUser(user, false);
                Drawable avatarDrawable = new AvatarDrawable((User) user);
                this.avatarDrawable = avatarDrawable;
                backupImageView.setImage(forUser, "50_50", avatarDrawable, user);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 4) != 0) {
                updateAvatarLayout();
            }
        }
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        updateAvatarLayout();
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r10 = new ThemeDescription[24];
        -$$Lambda$ContactAddActivity$ZD04TKrAmYykSmykZFEW6KtUdbc -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc = new -$$Lambda$ContactAddActivity$ZD04TKrAmYykSmykZFEW6KtUdbc(this);
        r10[16] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_text");
        r10[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundRed");
        r10[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundOrange");
        r10[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundViolet");
        r10[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundGreen");
        r10[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundCyan");
        r10[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundBlue");
        r10[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactaddactivity_zd04tkramyyksmykzfew6ktudbc, "avatar_backgroundPink");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$4$ContactAddActivity() {
        if (this.avatarImage != null) {
            User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.avatarImage.invalidate();
            }
        }
    }
}
