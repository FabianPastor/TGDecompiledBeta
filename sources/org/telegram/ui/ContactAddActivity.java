package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ContactAddActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean addContact;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public CheckBoxCell checkBoxCell;
    /* access modifiers changed from: private */
    public ContactAddActivityDelegate delegate;
    private View doneButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    private TextView infoTextView;
    /* access modifiers changed from: private */
    public EditTextBoldCursor lastNameField;
    private TextView nameTextView;
    private boolean needAddException;
    private TextView onlineTextView;
    boolean paused;
    private String phone;
    /* access modifiers changed from: private */
    public int user_id;

    public interface ContactAddActivityDelegate {
        void didAddToContacts();
    }

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
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
        this.needAddException = notificationsSettings.getBoolean("dialog_bar_exception" + this.user_id, false);
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
        String str;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            this.actionBar.setTitle(LocaleController.getString("NewContact", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditName", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ContactAddActivity.this.finishFragment();
                } else if (i == 1 && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                    TLRPC$User user = ContactAddActivity.this.getMessagesController().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                    user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                    user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                    ContactAddActivity.this.getContactsController().addContact(user, ContactAddActivity.this.checkBoxCell != null && ContactAddActivity.this.checkBoxCell.isChecked());
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ContactAddActivity.this.currentAccount).edit();
                    edit.putInt("dialog_bar_vis3" + ContactAddActivity.this.user_id, 3).commit();
                    ContactAddActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1);
                    ContactAddActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) ContactAddActivity.this.user_id));
                    ContactAddActivity.this.finishFragment();
                    if (ContactAddActivity.this.delegate != null) {
                        ContactAddActivity.this.delegate.didAddToContacts();
                    }
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener($$Lambda$ContactAddActivity$xZiAGMz9R4iZiUVWOVvgIBQ7msg.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 24.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(30.0f));
        int i = 3;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 20.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        TextView textView2 = this.nameTextView;
        boolean z = LocaleController.isRTL;
        frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, z ? 0.0f : 80.0f, 3.0f, z ? 80.0f : 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.onlineTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView4 = this.onlineTextView;
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 80.0f, 32.0f, z2 ? 80.0f : 0.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
        this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ContactAddActivity.this.lambda$createView$1$ContactAddActivity(textView, i, keyEvent);
            }
        });
        this.firstNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            boolean focued;

            public void onFocusChange(View view, boolean z) {
                if (!ContactAddActivity.this.paused && !z && this.focued) {
                    FileLog.d("changed");
                }
                this.focued = z;
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
        this.lastNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
        this.lastNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ContactAddActivity.this.lambda$createView$2$ContactAddActivity(textView, i, keyEvent);
            }
        });
        TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
        if (user != null) {
            if (user.phone == null && (str = this.phone) != null) {
                user.phone = PhoneFormat.stripExceptNumbers(str);
            }
            this.firstNameField.setText(user.first_name);
            EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
            this.lastNameField.setText(user.last_name);
        }
        TextView textView5 = new TextView(context2);
        this.infoTextView = textView5;
        textView5.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.infoTextView.setTextSize(1, 14.0f);
        TextView textView6 = this.infoTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        textView6.setGravity(i);
        if (this.addContact) {
            if (!this.needAddException || TextUtils.isEmpty(user.phone)) {
                linearLayout.addView(this.infoTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            }
            if (this.needAddException) {
                CheckBoxCell checkBoxCell2 = new CheckBoxCell(getParentActivity(), 0);
                this.checkBoxCell = checkBoxCell2;
                checkBoxCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.checkBoxCell.setText(LocaleController.formatString("SharePhoneNumberWith", NUM, UserObject.getFirstName(user)), "", true, false);
                this.checkBoxCell.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                this.checkBoxCell.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ContactAddActivity.this.lambda$createView$3$ContactAddActivity(view);
                    }
                });
                linearLayout.addView(this.checkBoxCell, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
            }
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ boolean lambda$createView$1$ContactAddActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ boolean lambda$createView$2$ContactAddActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ContactAddActivity(View view) {
        CheckBoxCell checkBoxCell2 = this.checkBoxCell;
        checkBoxCell2.setChecked(!checkBoxCell2.isChecked(), true);
    }

    public void setDelegate(ContactAddActivityDelegate contactAddActivityDelegate) {
        this.delegate = contactAddActivityDelegate;
    }

    private void updateAvatarLayout() {
        TLRPC$User user;
        if (this.nameTextView != null && (user = getMessagesController().getUser(Integer.valueOf(this.user_id))) != null) {
            if (TextUtils.isEmpty(user.phone)) {
                this.nameTextView.setText(LocaleController.getString("MobileHidden", NUM));
                this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MobileHiddenExceptionInfo", NUM, UserObject.getFirstName(user))));
            } else {
                TextView textView = this.nameTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                textView.setText(instance.format("+" + user.phone));
                if (this.needAddException) {
                    this.infoTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MobileVisibleInfo", NUM, UserObject.getFirstName(user))));
                }
            }
            this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
            BackupImageView backupImageView = this.avatarImage;
            ImageLocation forUser = ImageLocation.getForUser(user, false);
            AvatarDrawable avatarDrawable2 = new AvatarDrawable(user);
            this.avatarDrawable = avatarDrawable2;
            backupImageView.setImage(forUser, "50_50", (Drawable) avatarDrawable2, (Object) user);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            if ((intValue & 2) != 0 || (intValue & 4) != 0) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ContactAddActivity$SOaKXzA4ZHGsNoWooM8mGyCeTs r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ContactAddActivity.this.lambda$getThemeDescriptions$4$ContactAddActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        $$Lambda$ContactAddActivity$SOaKXzA4ZHGsNoWooM8mGyCeTs r8 = r10;
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$4 */
    public /* synthetic */ void lambda$getThemeDescriptions$4$ContactAddActivity() {
        TLRPC$User user;
        if (this.avatarImage != null && (user = getMessagesController().getUser(Integer.valueOf(this.user_id))) != null) {
            this.avatarDrawable.setInfo(user);
            this.avatarImage.invalidate();
        }
    }
}
