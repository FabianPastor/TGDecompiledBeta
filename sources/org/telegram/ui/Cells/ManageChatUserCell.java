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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
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
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ManageChatUserCellDelegate delegate;
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ManageChatUserCell(Context context, int i, int i2, boolean z) {
        super(context);
        this.namePadding = i2;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i3 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 8.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 46.0f : (float) (this.namePadding + 68), 11.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (this.namePadding + 68), 34.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (z) {
            this.optionsButton = new ImageView(context);
            this.optionsButton.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : i3) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ManageChatUserCell.this.lambda$new$0$ManageChatUserCell(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    public /* synthetic */ void lambda$new$0$ManageChatUserCell(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        float f;
        TLObject tLObject2 = tLObject;
        CharSequence charSequence3 = charSequence2;
        if (tLObject2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = charSequence3;
        this.currentName = charSequence;
        this.currentObject = tLObject2;
        if (this.optionsButton != null) {
            boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
            int i = 5;
            int i2 = 46;
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68), (charSequence3 == null || charSequence2.length() > 0) ? 11.5f : 20.5f, (float) (LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28), 0.0f));
            SimpleTextView simpleTextView = this.statusTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i3 = i | 48;
            float f2 = (float) (LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (LocaleController.isRTL) {
                f = (float) (this.namePadding + 68);
            } else {
                if (!onOptionsButtonCheck) {
                    i2 = 28;
                }
                f = (float) i2;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i3, f2, 34.5f, f, 0.0f));
        }
        this.needDivider = z;
        setWillNotDraw(!this.needDivider);
        update(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public void update(int i) {
        String str;
        String str2;
        TLRPC.FileLocation fileLocation;
        String str3;
        TLRPC.UserStatus userStatus;
        TLRPC.FileLocation fileLocation2;
        TLObject tLObject = this.currentObject;
        if (tLObject != null) {
            if (tLObject instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) tLObject;
                TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                TLRPC.FileLocation fileLocation3 = userProfilePhoto != null ? userProfilePhoto.photo_small : null;
                if (i != 0) {
                    boolean z = (i & 2) != 0 && ((this.lastAvatar != null && fileLocation3 == null) || ((this.lastAvatar == null && fileLocation3 != null) || !((fileLocation2 = this.lastAvatar) == null || fileLocation3 == null || (fileLocation2.volume_id == fileLocation3.volume_id && fileLocation2.local_id == fileLocation3.local_id))));
                    if (!(user == null || z || (i & 4) == 0)) {
                        TLRPC.UserStatus userStatus2 = user.status;
                        if ((userStatus2 != null ? userStatus2.expires : 0) != this.lastStatus) {
                            z = true;
                        }
                    }
                    if (z || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                        str3 = null;
                    } else {
                        str3 = UserObject.getUserName(user);
                        if (!str3.equals(this.lastName)) {
                            z = true;
                        }
                    }
                    if (!z) {
                        return;
                    }
                } else {
                    str3 = null;
                }
                this.avatarDrawable.setInfo(user);
                TLRPC.UserStatus userStatus3 = user.status;
                if (userStatus3 != null) {
                    this.lastStatus = userStatus3.expires;
                } else {
                    this.lastStatus = 0;
                }
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence);
                } else {
                    if (str3 == null) {
                        str3 = UserObject.getUserName(user);
                    }
                    this.lastName = str3;
                    this.nameTextView.setText(this.lastName);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else if (user.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (user.bot_chat_history || this.isAdmin) {
                        this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
                    }
                } else if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (((userStatus = user.status) != null && userStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", NUM));
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
                }
                this.lastAvatar = fileLocation3;
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            } else if (tLObject instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                TLRPC.ChatPhoto chatPhoto = chat.photo;
                TLRPC.FileLocation fileLocation4 = chatPhoto != null ? chatPhoto.photo_small : null;
                if (i != 0) {
                    boolean z2 = (i & 2) != 0 && ((this.lastAvatar != null && fileLocation4 == null) || ((this.lastAvatar == null && fileLocation4 != null) || !((fileLocation = this.lastAvatar) == null || fileLocation4 == null || (fileLocation.volume_id == fileLocation4.volume_id && fileLocation.local_id == fileLocation4.local_id))));
                    if (z2 || this.currentName != null || (str2 = this.lastName) == null || (i & 1) == 0) {
                        str = null;
                    } else {
                        str = chat.title;
                        if (!str.equals(str2)) {
                            z2 = true;
                        }
                    }
                    if (!z2) {
                        return;
                    }
                } else {
                    str = null;
                }
                this.avatarDrawable.setInfo(chat);
                CharSequence charSequence2 = this.currentName;
                if (charSequence2 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence2);
                } else {
                    if (str == null) {
                        str = chat.title;
                    }
                    this.lastName = str;
                    this.nameTextView.setText(this.lastName);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    int i2 = chat.participants_count;
                    if (i2 != 0) {
                        this.statusTextView.setText(LocaleController.formatPluralString("Members", i2));
                    } else if (chat.has_geo) {
                        this.statusTextView.setText(LocaleController.getString("MegaLocation", NUM));
                    } else if (TextUtils.isEmpty(chat.username)) {
                        this.statusTextView.setText(LocaleController.getString("MegaPrivate", NUM));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("MegaPublic", NUM));
                    }
                }
                this.lastAvatar = fileLocation4;
                this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
            }
        }
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public TLObject getCurrentObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
