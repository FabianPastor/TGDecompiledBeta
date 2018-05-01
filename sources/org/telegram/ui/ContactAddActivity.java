package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ContactAddActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private boolean addContact;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private EditTextBoldCursor lastNameField;
    private TextView nameTextView;
    private TextView onlineTextView;
    private String phone = null;
    private int user_id;

    /* renamed from: org.telegram.ui.ContactAddActivity$2 */
    class C13602 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C13602() {
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$3 */
    class C13613 implements OnEditorActionListener {
        C13613() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return null;
            }
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$4 */
    class C13624 implements OnEditorActionListener {
        C13624() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return null;
            }
            ContactAddActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$1 */
    class C21071 extends ActionBarMenuOnItemClick {
        C21071() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ContactAddActivity.this.finishFragment();
            } else if (i == 1 && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                i = MessagesController.getInstance(ContactAddActivity.this.currentAccount).getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                i.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                i.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                ContactsController.getInstance(ContactAddActivity.this.currentAccount).addContact(i);
                ContactAddActivity.this.finishFragment();
                i = MessagesController.getNotificationsSettings(ContactAddActivity.this.currentAccount).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("spam3_");
                stringBuilder.append(ContactAddActivity.this.user_id);
                i.putInt(stringBuilder.toString(), 1).commit();
                NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf((long) ContactAddActivity.this.user_id));
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$5 */
    class C21085 implements ThemeDescriptionDelegate {
        C21085() {
        }

        public void didSetColor() {
            if (ContactAddActivity.this.avatarImage != null) {
                User user = MessagesController.getInstance(ContactAddActivity.this.currentAccount).getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                if (user != null) {
                    ContactAddActivity.this.avatarDrawable.setInfo(user);
                    ContactAddActivity.this.avatarImage.invalidate();
                }
            }
        }
    }

    public ContactAddActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        this.user_id = getArguments().getInt("user_id", 0);
        this.phone = getArguments().getString("phone");
        this.addContact = getArguments().getBoolean("addContact", false);
        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)) == null || !super.onFragmentCreate()) {
            return false;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            r0.actionBar.setTitle(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
        } else {
            r0.actionBar.setTitle(LocaleController.getString("EditName", C0446R.string.EditName));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C21071());
        r0.doneButton = r0.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        r0.fragmentView = new ScrollView(context2);
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ((ScrollView) r0.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new C13602());
        View frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 24.0f, 0.0f));
        r0.avatarImage = new BackupImageView(context2);
        r0.avatarImage.setRoundRadius(AndroidUtilities.dp(30.0f));
        int i = 3;
        frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        r0.nameTextView = new TextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(1, 20.0f);
        r0.nameTextView.setLines(1);
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 80.0f, 3.0f, LocaleController.isRTL ? 80.0f : 0.0f, 0.0f));
        r0.onlineTextView = new TextView(context2);
        r0.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.onlineTextView.setTextSize(1, 14.0f);
        r0.onlineTextView.setLines(1);
        r0.onlineTextView.setMaxLines(1);
        r0.onlineTextView.setSingleLine(true);
        r0.onlineTextView.setEllipsize(TruncateAt.END);
        r0.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(r0.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 80.0f, 32.0f, LocaleController.isRTL ? 80.0f : 0.0f, 0.0f));
        r0.firstNameField = new EditTextBoldCursor(context2);
        r0.firstNameField.setTextSize(1, 18.0f);
        r0.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.firstNameField.setMaxLines(1);
        r0.firstNameField.setLines(1);
        r0.firstNameField.setSingleLine(true);
        r0.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.firstNameField.setInputType(49152);
        r0.firstNameField.setImeOptions(5);
        r0.firstNameField.setHint(LocaleController.getString("FirstName", C0446R.string.FirstName));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        r0.firstNameField.setOnEditorActionListener(new C13613());
        r0.lastNameField = new EditTextBoldCursor(context2);
        r0.lastNameField.setTextSize(1, 18.0f);
        r0.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.lastNameField.setMaxLines(1);
        r0.lastNameField.setLines(1);
        r0.lastNameField.setSingleLine(true);
        EditTextBoldCursor editTextBoldCursor = r0.lastNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor.setGravity(i);
        r0.lastNameField.setInputType(49152);
        r0.lastNameField.setImeOptions(6);
        r0.lastNameField.setHint(LocaleController.getString("LastName", C0446R.string.LastName));
        r0.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        r0.lastNameField.setOnEditorActionListener(new C13624());
        User user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(r0.user_id));
        if (user != null) {
            if (user.phone == null && r0.phone != null) {
                user.phone = PhoneFormat.stripExceptNumbers(r0.phone);
            }
            r0.firstNameField.setText(user.first_name);
            r0.firstNameField.setSelection(r0.firstNameField.length());
            r0.lastNameField.setText(user.last_name);
        }
        return r0.fragmentView;
    }

    private void updateAvatarLayout() {
        if (this.nameTextView != null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                TextView textView = this.nameTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                textView.setText(instance.format(stringBuilder.toString()));
                this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
                TLObject tLObject = null;
                if (user.photo != null) {
                    tLObject = user.photo.photo_small;
                }
                Drawable avatarDrawable = new AvatarDrawable(user);
                this.avatarDrawable = avatarDrawable;
                this.avatarImage.setImage(tLObject, "50_50", avatarDrawable);
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

    public void onResume() {
        super.onResume();
        updateAvatarLayout();
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

    public ThemeDescription[] getThemeDescriptions() {
        r10 = new ThemeDescription[23];
        C21085 c21085 = new C21085();
        r10[15] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, c21085, Theme.key_avatar_text);
        r10[16] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundRed);
        r10[17] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundOrange);
        r10[18] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundViolet);
        r10[19] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundGreen);
        r10[20] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundCyan);
        r10[21] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundBlue);
        r10[22] = new ThemeDescription(null, 0, null, null, null, c21085, Theme.key_avatar_backgroundPink);
        return r10;
    }
}
