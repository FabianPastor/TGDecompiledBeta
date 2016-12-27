package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

public class ContactAddActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private boolean addContact;
    private BackupImageView avatarImage;
    private View doneButton;
    private EditText firstNameField;
    private EditText lastNameField;
    private TextView nameTextView;
    private TextView onlineTextView;
    private String phone = null;
    private int user_id;

    public ContactAddActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.user_id = getArguments().getInt("user_id", 0);
        this.phone = getArguments().getString("phone");
        this.addContact = getArguments().getBoolean("addContact", false);
        if (MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)) == null || !super.onFragmentCreate()) {
            return false;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            this.actionBar.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditName", R.string.EditName));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ContactAddActivity.this.finishFragment();
                } else if (id == 1 && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                    user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                    user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                    ContactsController.getInstance().addContact(user);
                    ContactAddActivity.this.finishFragment();
                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("spam3_" + ContactAddActivity.this.user_id, 1).commit();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf((long) ContactAddActivity.this.user_id));
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams2 = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        linearLayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(24.0f);
        layoutParams.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams.width = -1;
        layoutParams.height = -2;
        frameLayout.setLayoutParams(layoutParams);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        frameLayout.addView(this.avatarImage);
        LayoutParams layoutParams3 = (LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        layoutParams3.width = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams3.height = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        this.avatarImage.setLayoutParams(layoutParams3);
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-14606047);
        this.nameTextView.setTextSize(1, 20.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(this.nameTextView);
        layoutParams3 = (LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 80.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 80.0f : 0.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(3.0f);
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.nameTextView.setLayoutParams(layoutParams3);
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(-6710887);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.onlineTextView);
        layoutParams3 = (LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 80.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 80.0f : 0.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(32.0f);
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.onlineTextView.setLayoutParams(layoutParams3);
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(-14606047);
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        linearLayout.addView(this.firstNameField);
        layoutParams = (LinearLayout.LayoutParams) this.firstNameField.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(24.0f);
        layoutParams.height = AndroidUtilities.dp(36.0f);
        layoutParams.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams.width = -1;
        this.firstNameField.setLayoutParams(layoutParams);
        this.firstNameField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                ContactAddActivity.this.lastNameField.requestFocus();
                ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
                return true;
            }
        });
        this.lastNameField = new EditText(context);
        this.lastNameField.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.lastNameField.setTextColor(-14606047);
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        linearLayout.addView(this.lastNameField);
        layoutParams = (LinearLayout.LayoutParams) this.lastNameField.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(16.0f);
        layoutParams.height = AndroidUtilities.dp(36.0f);
        layoutParams.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams.width = -1;
        this.lastNameField.setLayoutParams(layoutParams);
        this.lastNameField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ContactAddActivity.this.doneButton.performClick();
                return true;
            }
        });
        User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
        if (user != null) {
            if (user.phone == null && this.phone != null) {
                user.phone = PhoneFormat.stripExceptNumbers(this.phone);
            }
            this.firstNameField.setText(user.first_name);
            this.firstNameField.setSelection(this.firstNameField.length());
            this.lastNameField.setText(user.last_name);
        }
        return this.fragmentView;
    }

    private void updateAvatarLayout() {
        if (this.nameTextView != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                this.nameTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
                this.onlineTextView.setText(LocaleController.formatUserStatus(user));
                TLObject photo = null;
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                }
                this.avatarImage.setImage(photo, "50_50", new AvatarDrawable(user));
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
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
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
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
}
