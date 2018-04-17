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

        public void onClick(View v) {
            ManageChatUserCell.this.delegate.onOptionsButtonCheck(ManageChatUserCell.this, true);
        }
    }

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public ManageChatUserCell(Context context, int padding, boolean needOption) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (7 + padding), 8.0f, LocaleController.isRTL ? (float) (7 + padding) : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = r0.nameTextView;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = 46.0f;
        float f2 = LocaleController.isRTL ? 46.0f : (float) (68 + padding);
        if (LocaleController.isRTL) {
            f = (float) (68 + padding);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i2, f2, 11.5f, f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(14);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view = r0.statusTextView;
        i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        f = 28.0f;
        f2 = LocaleController.isRTL ? 28.0f : (float) (68 + padding);
        if (LocaleController.isRTL) {
            f = (float) (68 + padding);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i2, f2, 34.5f, f, 0.0f));
        if (needOption) {
            r0.optionsButton = new ImageView(context2);
            r0.optionsButton.setFocusable(false);
            r0.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            r0.optionsButton.setImageResource(R.drawable.ic_ab_other);
            r0.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
            r0.optionsButton.setScaleType(ScaleType.CENTER);
            View view2 = r0.optionsButton;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            addView(view2, LayoutHelper.createFrame(48, 64, i | 48));
            r0.optionsButton.setOnClickListener(new C08821());
        }
    }

    public void setData(User user, CharSequence name, CharSequence status) {
        if (user == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentUser = null;
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.statusTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = status;
        this.currentName = name;
        this.currentUser = user;
        if (this.optionsButton != null) {
            this.optionsButton.setVisibility(this.delegate.onOptionsButtonCheck(this, false) ? 0 : 4);
        }
        update(0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void setIsAdmin(boolean value) {
        this.isAdmin = value;
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
                if (!(this.currentUser == null || continueUpdate || (mask & 4) == 0)) {
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
            if (this.currentUser.status != null) {
                this.lastStatus = this.currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName);
            } else {
                this.lastName = newName == null ? UserObject.getUserName(this.currentUser) : newName;
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != null) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (this.currentUser != null) {
                if (this.currentUser.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (!this.currentUser.bot_chat_history) {
                        if (!this.isAdmin) {
                            this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", R.string.BotStatusCantRead));
                        }
                    }
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", R.string.BotStatusRead));
                } else {
                    if (this.currentUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (this.currentUser.status == null || this.currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                        if (!MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))) {
                            this.statusTextView.setTextColor(this.statusColor);
                            this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
                        }
                    }
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
                }
            }
            this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable);
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
