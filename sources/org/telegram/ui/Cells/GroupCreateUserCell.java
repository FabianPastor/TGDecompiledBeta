package org.telegram.ui.Cells;

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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

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
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(50, 50.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(16);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (needCheck) {
            r0.checkBox = new GroupCreateCheckBox(context2);
            r0.checkBox.setVisibility(0);
            View view = r0.checkBox;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(24, 24.0f, i | 48, LocaleController.isRTL ? 0.0f : 41.0f, 41.0f, LocaleController.isRTL ? 41.0f : 0.0f, 0.0f));
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
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
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
                this.lastName = newName == null ? UserObject.getUserName(this.currentUser) : newName;
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
            } else {
                if (this.currentUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (this.currentUser.status == null || this.currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                    if (!MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))) {
                        this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                        this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
                        this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
                    }
                }
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_onlineText));
                this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
            }
            this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
