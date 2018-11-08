package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.GroupCreateCheckBox;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Cells.GroupCreateUserCell */
public class GroupCreateUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private GroupCreateCheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private CharSequence currentStatus;
    private User currentUser;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public GroupCreateUserCell(Context context, boolean needCheck) {
        int i;
        int i2;
        int i3 = 5;
        super(context);
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.m10dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(50, 50.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(17);
        SimpleTextView simpleTextView = this.nameTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView.setGravity(i | 48);
        View view = this.nameTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i2 | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(16);
        simpleTextView = this.statusTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView.setGravity(i | 48);
        view = this.statusTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i2 | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (needCheck) {
            this.checkBox = new GroupCreateCheckBox(context);
            this.checkBox.setVisibility(0);
            View view2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(view2, LayoutHelper.createFrame(24, 24.0f, i3 | 48, LocaleController.isRTL ? 0.0f : 41.0f, 41.0f, LocaleController.isRTL ? 41.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(User user, CharSequence name, CharSequence status) {
        this.currentUser = user;
        this.currentStatus = status;
        this.currentName = name;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public User getUser() {
        return this.currentUser;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(72.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int mask) {
        if (this.currentUser != null) {
            TLObject photo = null;
            String newName = null;
            if (this.currentUser.photo != null) {
                photo = this.currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || !(this.lastAvatar != null || photo == null || this.lastAvatar == null || photo == null || (this.lastAvatar.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id)))) {
                    continueUpdate = true;
                }
                if (!(this.currentUser == null || this.currentStatus != null || continueUpdate || (mask & 4) == 0)) {
                    int newStatus = 0;
                    if (this.currentUser.status != null) {
                        newStatus = this.currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || this.currentName != null || this.lastName == null || (mask & 1) == 0)) {
                    newName = UserObject.getUserName(this.currentUser);
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.lastStatus = this.currentUser.status != null ? this.currentUser.status.expires : 0;
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName, true);
            } else {
                if (newName == null) {
                    newName = UserObject.getUserName(this.currentUser);
                }
                this.lastName = newName;
                this.nameTextView.setText(this.lastName);
            }
            if (this.currentStatus != null) {
                this.statusTextView.setText(this.currentStatus, true);
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
            } else if (this.currentUser.bot) {
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
                this.statusTextView.setText(LocaleController.getString("Bot", R.string.Bot));
            } else if (this.currentUser.f177id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((this.currentUser.status != null && this.currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.f177id)))) {
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_onlineText));
                this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
            } else {
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
                this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
            }
            this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
