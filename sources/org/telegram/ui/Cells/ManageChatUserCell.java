package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
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
    private AvatarDrawable avatarDrawable;
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
    private int namePadding;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private ImageView optionsButton;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public ManageChatUserCell(Context context, int avatarPadding, int nPadding, boolean needOption) {
        int i;
        int i2;
        int i3;
        int i4 = 3;
        super(context);
        this.namePadding = nPadding;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (avatarPadding + 7), 8.0f, LocaleController.isRTL ? (float) (avatarPadding + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        SimpleTextView simpleTextView = this.nameTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView.setGravity(i | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i2 | 48, LocaleController.isRTL ? 46.0f : (float) (this.namePadding + 68), 11.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        SimpleTextView simpleTextView3 = this.statusTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        simpleTextView3.setGravity(i | 48);
        simpleTextView2 = this.statusTextView;
        if (LocaleController.isRTL) {
            i3 = 5;
        } else {
            i3 = 3;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i3 | 48, LocaleController.isRTL ? 28.0f : (float) (this.namePadding + 68), 34.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (needOption) {
            this.optionsButton = new ImageView(context);
            this.optionsButton.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
            this.optionsButton.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.optionsButton;
            if (!LocaleController.isRTL) {
                i4 = 5;
            }
            addView(imageView, LayoutHelper.createFrame(52, 64, i4 | 48));
            this.optionsButton.setOnClickListener(new ManageChatUserCell$$Lambda$0(this));
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$ManageChatUserCell(View v) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setData(User user, CharSequence name, CharSequence status, boolean divider) {
        if (user == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentUser = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = status;
        this.currentName = name;
        this.currentUser = user;
        if (this.optionsButton != null) {
            float f;
            float f2;
            boolean visible = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(visible ? 0 : 4);
            SimpleTextView simpleTextView = this.nameTextView;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = (float) (visible ? 46 : 28);
            } else {
                f = (float) (this.namePadding + 68);
            }
            float f3 = (status == null || status.length() > 0) ? 11.5f : 20.5f;
            if (LocaleController.isRTL) {
                f2 = (float) (this.namePadding + 68);
            } else {
                f2 = (float) (visible ? 46 : 28);
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i, f, f3, f2, 0.0f));
            simpleTextView = this.statusTextView;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = (float) (visible ? 46 : 28);
            } else {
                f = (float) (this.namePadding + 68);
            }
            if (LocaleController.isRTL) {
                f2 = (float) (this.namePadding + 68);
            } else {
                f2 = (float) (visible ? 46 : 28);
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i, f, 34.5f, f2, 0.0f));
        }
        this.needDivider = divider;
        setWillNotDraw(!this.needDivider);
        update(0);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), NUM));
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
                if (newName == null) {
                    newName = UserObject.getUserName(this.currentUser);
                }
                this.lastName = newName;
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != null) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (this.currentUser != null) {
                if (this.currentUser.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (this.currentUser.bot_chat_history || this.isAdmin) {
                        this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
                    }
                } else if (this.currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((this.currentUser.status != null && this.currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)))) {
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", NUM));
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
                }
            }
            this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable, this.currentUser);
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
