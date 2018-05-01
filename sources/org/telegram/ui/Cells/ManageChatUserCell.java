package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import org.telegram.ui.Components.LayoutHelper;

public class ManageChatUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private User currentUser;
    private CharSequence currrntStatus;
    private ManageChatUserCellDelegate delegate;
    private boolean isAdmin;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private ImageView optionsButton;
    private int statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
    private int statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
    private SimpleTextView statusTextView;

    /* renamed from: org.telegram.ui.Cells.ManageChatUserCell$1 */
    class C08821 implements OnClickListener {
        C08821() {
        }

        public void onClick(View view) {
            ManageChatUserCell.this.delegate.onOptionsButtonCheck(ManageChatUserCell.this, true);
        }
    }

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ManageChatUserCell(Context context, int i, boolean z) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i2 = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (7 + i), 8.0f, LocaleController.isRTL ? (float) (7 + i) : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = r0.nameTextView;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = 46.0f;
        float f2 = LocaleController.isRTL ? 46.0f : (float) (68 + i);
        if (LocaleController.isRTL) {
            f = (float) (68 + i);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i3, f2, 11.5f, f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(14);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (68 + i), 34.5f, LocaleController.isRTL ? (float) (68 + i) : 28.0f, 0.0f));
        if (z) {
            r0.optionsButton = new ImageView(context2);
            r0.optionsButton.setFocusable(false);
            r0.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            r0.optionsButton.setImageResource(C0446R.drawable.ic_ab_other);
            r0.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
            r0.optionsButton.setScaleType(ScaleType.CENTER);
            View view2 = r0.optionsButton;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view2, LayoutHelper.createFrame(48, 64, i2 | 48));
            r0.optionsButton.setOnClickListener(new C08821());
        }
    }

    public void setData(User user, CharSequence charSequence, CharSequence charSequence2) {
        if (user == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentUser = null;
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.statusTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = charSequence2;
        this.currentName = charSequence;
        this.currentUser = user;
        if (this.optionsButton != null) {
            this.optionsButton.setVisibility(this.delegate.onOptionsButtonCheck(this, false) != null ? null : 4);
        }
        update(0);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public void update(int i) {
        if (this.currentUser != null) {
            TLObject tLObject = this.currentUser.photo != null ? this.currentUser.photo.photo_small : null;
            if (i != 0) {
                int i2 = ((i & 2) == 0 || ((this.lastAvatar == null || tLObject != null) && (this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)))) ? 0 : 1;
                if (!(this.currentUser == null || i2 != 0 || (i & 4) == 0)) {
                    if ((this.currentUser.status != null ? this.currentUser.status.expires : 0) != this.lastStatus) {
                        i2 = 1;
                    }
                }
                if (i2 != 0 || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                    i = 0;
                } else {
                    i = UserObject.getUserName(this.currentUser);
                    if (!i.equals(this.lastName)) {
                        i2 = 1;
                    }
                }
                if (i2 == 0) {
                    return;
                }
            }
            i = 0;
            this.avatarDrawable.setInfo(this.currentUser);
            if (this.currentUser.status != null) {
                this.lastStatus = this.currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName);
            } else {
                if (i == 0) {
                    i = UserObject.getUserName(this.currentUser);
                }
                this.lastName = i;
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != 0) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (this.currentUser != 0) {
                if (this.currentUser.bot != 0) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (this.currentUser.bot_chat_history == 0) {
                        if (this.isAdmin == 0) {
                            this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", C0446R.string.BotStatusCantRead));
                        }
                    }
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", C0446R.string.BotStatusRead));
                } else {
                    if (this.currentUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (this.currentUser.status == 0 || this.currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                        if (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)) == 0) {
                            this.statusTextView.setTextColor(this.statusColor);
                            this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
                        }
                    }
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", C0446R.string.Online));
                }
            }
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }
}
