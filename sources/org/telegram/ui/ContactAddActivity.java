package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences.Editor;
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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
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
    class C13542 implements OnTouchListener {
        C13542() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$3 */
    class C13553 implements OnEditorActionListener {
        C13553() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$4 */
    class C13564 implements OnEditorActionListener {
        C13564() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            ContactAddActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$1 */
    class C21011 extends ActionBarMenuOnItemClick {
        C21011() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ContactAddActivity.this.finishFragment();
            } else if (id == 1 && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                User user = MessagesController.getInstance(ContactAddActivity.this.currentAccount).getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                ContactsController.getInstance(ContactAddActivity.this.currentAccount).addContact(user);
                ContactAddActivity.this.finishFragment();
                Editor edit = MessagesController.getNotificationsSettings(ContactAddActivity.this.currentAccount).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("spam3_");
                stringBuilder.append(ContactAddActivity.this.user_id);
                edit.putInt(stringBuilder.toString(), 1).commit();
                NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf((long) ContactAddActivity.this.user_id));
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactAddActivity$5 */
    class C21025 implements ThemeDescriptionDelegate {
        C21025() {
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

    public ContactAddActivity(Bundle args) {
        super(args);
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            r0.actionBar.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
        } else {
            r0.actionBar.setTitle(LocaleController.getString("EditName", R.string.EditName));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C21011());
        r0.doneButton = r0.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        r0.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ((ScrollView) r0.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new C13542());
        FrameLayout frameLayout = new FrameLayout(context2);
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
        r0.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        r0.firstNameField.setOnEditorActionListener(new C13553());
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
        r0.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        r0.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        r0.lastNameField.setOnEditorActionListener(new C13564());
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
                TLObject photo = null;
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                }
                Drawable avatarDrawable = new AvatarDrawable(user);
                this.avatarDrawable = avatarDrawable;
                this.avatarImage.setImage(photo, "50_50", avatarDrawable);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 4) != 0) {
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

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C21025();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[23];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[6] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[7] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[8] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[9] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[10] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[11] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[13] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[14] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
