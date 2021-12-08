package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
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
    private Object currentObject;
    private CharSequence currrntStatus;
    private ImageView customImageView;
    private ManageChatUserCellDelegate delegate;
    private String dividerColor;
    private boolean isAdmin;
    private TLRPC.FileLocation lastAvatar;
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
        super(context);
        this.namePadding = nPadding;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (avatarPadding + 7), 8.0f, LocaleController.isRTL ? (float) (avatarPadding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 46.0f : (float) (this.namePadding + 68), 11.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (this.namePadding + 68), 34.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (needOption) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(60, 64, (LocaleController.isRTL ? 3 : i) | 48));
            this.optionsButton.setOnClickListener(new ManageChatUserCell$$ExternalSyntheticLambda0(this));
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ManageChatUserCell  reason: not valid java name */
    public /* synthetic */ void m1542lambda$new$0$orgtelegramuiCellsManageChatUserCell(View v) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setCustomRightImage(int resId) {
        ImageView imageView = new ImageView(getContext());
        this.customImageView = imageView;
        imageView.setImageResource(resId);
        this.customImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.customImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_mutedIconUnscrolled"), PorterDuff.Mode.MULTIPLY));
        addView(this.customImageView, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : 5) | 48));
    }

    public void setCustomImageVisible(boolean visible) {
        ImageView imageView = this.customImageView;
        if (imageView != null) {
            imageView.setVisibility(visible ? 0 : 8);
        }
    }

    public void setData(Object object, CharSequence name, CharSequence status, boolean divider) {
        float f;
        float f2;
        Object obj = object;
        CharSequence charSequence = status;
        boolean z = divider;
        if (obj == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = charSequence;
        this.currentName = name;
        this.currentObject = obj;
        int i = 5;
        int i2 = 28;
        if (this.optionsButton != null) {
            boolean visible = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(visible ? 0 : 4);
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? visible ? 46 : 28 : this.namePadding + 68), (charSequence == null || status.length() > 0) ? 11.5f : 20.5f, (float) (LocaleController.isRTL ? this.namePadding + 68 : visible ? 46 : 28), 0.0f));
            SimpleTextView simpleTextView = this.statusTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i3 = i | 48;
            float f3 = (float) (LocaleController.isRTL ? visible ? 46 : 28 : this.namePadding + 68);
            if (LocaleController.isRTL) {
                f2 = (float) (this.namePadding + 68);
            } else {
                if (visible) {
                    i2 = 46;
                }
                f2 = (float) i2;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i3, f3, 34.5f, f2, 0.0f));
        } else {
            ImageView imageView = this.customImageView;
            if (imageView != null) {
                boolean visible2 = imageView.getVisibility() == 0;
                this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? visible2 ? 54 : 28 : this.namePadding + 68), (charSequence == null || status.length() > 0) ? 11.5f : 20.5f, (float) (LocaleController.isRTL ? this.namePadding + 68 : visible2 ? 54 : 28), 0.0f));
                SimpleTextView simpleTextView2 = this.statusTextView;
                if (!LocaleController.isRTL) {
                    i = 3;
                }
                int i4 = i | 48;
                float f4 = (float) (LocaleController.isRTL ? visible2 ? 54 : 28 : this.namePadding + 68);
                if (LocaleController.isRTL) {
                    f = (float) (this.namePadding + 68);
                } else {
                    if (visible2) {
                        i2 = 54;
                    }
                    f = (float) i2;
                }
                simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i4, f4, 34.5f, f, 0.0f));
            }
        }
        this.needDivider = z;
        setWillNotDraw(!z);
        update(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public long getUserId() {
        Object obj = this.currentObject;
        if (obj instanceof TLRPC.User) {
            return ((TLRPC.User) obj).id;
        }
        return 0;
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void setIsAdmin(boolean value) {
        this.isAdmin = value;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setNameColor(int color) {
        this.nameTextView.setTextColor(color);
    }

    public void setDividerColor(String key) {
        this.dividerColor = key;
    }

    public void update(int mask) {
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation fileLocation2;
        Object obj = this.currentObject;
        if (obj != null) {
            if (obj instanceof TLRPC.User) {
                TLRPC.User currentUser = (TLRPC.User) obj;
                TLRPC.FileLocation photo = null;
                String newName = null;
                if (currentUser.photo != null) {
                    photo = currentUser.photo.photo_small;
                }
                if (mask != 0) {
                    boolean continueUpdate = false;
                    if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation2 = this.lastAvatar) != null && photo == null) || ((fileLocation2 == null && photo != null) || !(fileLocation2 == null || (fileLocation2.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                        continueUpdate = true;
                    }
                    if (!continueUpdate && (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                        int newStatus = 0;
                        if (currentUser.status != null) {
                            newStatus = currentUser.status.expires;
                        }
                        if (newStatus != this.lastStatus) {
                            continueUpdate = true;
                        }
                    }
                    if (!continueUpdate && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                        newName = UserObject.getUserName(currentUser);
                        if (!newName.equals(this.lastName)) {
                            continueUpdate = true;
                        }
                    }
                    if (!continueUpdate) {
                        return;
                    }
                }
                this.avatarDrawable.setInfo(currentUser);
                if (currentUser.status != null) {
                    this.lastStatus = currentUser.status.expires;
                } else {
                    this.lastStatus = 0;
                }
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence);
                } else {
                    String userName = newName == null ? UserObject.getUserName(currentUser) : newName;
                    this.lastName = userName;
                    this.nameTextView.setText(userName);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else if (currentUser.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (currentUser.bot_chat_history || this.isAdmin) {
                        this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
                    }
                } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(currentUser.id)))) {
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", NUM));
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
                }
                this.lastAvatar = photo;
                this.avatarImageView.setForUserOrChat(currentUser, this.avatarDrawable);
            } else if (obj instanceof TLRPC.Chat) {
                TLRPC.Chat currentChat = (TLRPC.Chat) obj;
                TLRPC.FileLocation photo2 = null;
                String newName2 = null;
                if (currentChat.photo != null) {
                    photo2 = currentChat.photo.photo_small;
                }
                if (mask != 0) {
                    boolean continueUpdate2 = false;
                    if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo2 == null) || ((fileLocation == null && photo2 != null) || !(fileLocation == null || (fileLocation.volume_id == photo2.volume_id && this.lastAvatar.local_id == photo2.local_id))))) {
                        continueUpdate2 = true;
                    }
                    if (!continueUpdate2 && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                        newName2 = currentChat.title;
                        if (!newName2.equals(this.lastName)) {
                            continueUpdate2 = true;
                        }
                    }
                    if (!continueUpdate2) {
                        return;
                    }
                }
                this.avatarDrawable.setInfo(currentChat);
                CharSequence charSequence2 = this.currentName;
                if (charSequence2 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence2);
                } else {
                    String str = newName2 == null ? currentChat.title : newName2;
                    this.lastName = str;
                    this.nameTextView.setText(str);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (currentChat.participants_count != 0) {
                        if (!ChatObject.isChannel(currentChat) || currentChat.megagroup) {
                            this.statusTextView.setText(LocaleController.formatPluralString("Members", currentChat.participants_count));
                        } else {
                            this.statusTextView.setText(LocaleController.formatPluralString("Subscribers", currentChat.participants_count));
                        }
                    } else if (currentChat.has_geo) {
                        this.statusTextView.setText(LocaleController.getString("MegaLocation", NUM));
                    } else if (TextUtils.isEmpty(currentChat.username)) {
                        this.statusTextView.setText(LocaleController.getString("MegaPrivate", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("MegaPublic", NUM));
                    }
                }
                this.lastAvatar = photo2;
                this.avatarImageView.setForUserOrChat(currentChat, this.avatarDrawable);
            } else if (obj instanceof Integer) {
                this.nameTextView.setText(this.currentName);
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
                this.avatarDrawable.setAvatarType(3);
                this.avatarImageView.setImage((String) null, "50_50", this.avatarDrawable);
            }
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            if (this.dividerColor != null) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(this.dividerColor));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerColor != null ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }
}
