package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public GroupCreateUserCell(Context context, boolean z) {
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
        if (z) {
            r0.checkBox = new GroupCreateCheckBox(context2);
            r0.checkBox.setVisibility(0);
            View view = r0.checkBox;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(24, 24.0f, i | 48, LocaleController.isRTL ? 0.0f : 41.0f, 41.0f, LocaleController.isRTL ? 41.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(User user, CharSequence charSequence, CharSequence charSequence2) {
        this.currentUser = user;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        update(null);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public User getUser() {
        return this.currentUser;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int i) {
        if (this.currentUser != null) {
            TLObject tLObject = this.currentUser.photo != null ? this.currentUser.photo.photo_small : null;
            int i2 = 0;
            if (i != 0) {
                boolean z = (i & 2) != 0 && ((this.lastAvatar != null && tLObject == null) || !(this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)));
                if (!(this.currentUser == null || this.currentStatus != null || z || (i & 4) == 0)) {
                    if ((this.currentUser.status != null ? this.currentUser.status.expires : 0) != this.lastStatus) {
                        z = true;
                    }
                }
                if (z || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                    i = 0;
                } else {
                    i = UserObject.getUserName(this.currentUser);
                    if (!i.equals(this.lastName)) {
                        z = true;
                    }
                }
                if (!z) {
                    return;
                }
            }
            i = 0;
            this.avatarDrawable.setInfo(this.currentUser);
            if (this.currentUser.status != null) {
                i2 = this.currentUser.status.expires;
            }
            this.lastStatus = i2;
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName, true);
            } else {
                if (i == 0) {
                    i = UserObject.getUserName(this.currentUser);
                }
                this.lastName = i;
                this.nameTextView.setText(this.lastName);
            }
            if (this.currentStatus != 0) {
                this.statusTextView.setText(this.currentStatus, true);
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
            } else if (this.currentUser.bot != 0) {
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
                this.statusTextView.setText(LocaleController.getString("Bot", C0446R.string.Bot));
            } else {
                if (this.currentUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (this.currentUser.status == 0 || this.currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                    if (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)) == 0) {
                        this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                        this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_offlineText));
                        this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
                    }
                }
                this.statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_groupcreate_onlineText));
                this.statusTextView.setText(LocaleController.getString("Online", C0446R.string.Online));
            }
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
    }
}
