package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
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
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private boolean drawDivider = false;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCreateUserCell(Context context, boolean z, int i) {
        super(context);
        Context context2 = context;
        int i2 = i;
        this.padding = i2;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i3 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i2 + 13), 6.0f, LocaleController.isRTL ? (float) (i2 + 13) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i4 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + i2), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + i2), 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + i2), 32.0f, (float) ((LocaleController.isRTL ? 72 : i4) + i2), 0.0f));
        if (z) {
            this.checkBox = new CheckBox2(context2, 21);
            this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i3) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 33.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        setObject(tLObject, charSequence, charSequence2);
        this.drawDivider = z;
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = tLObject;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.drawDivider = false;
        update(0);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setCheckBoxEnabled(boolean z) {
        this.checkBox.setEnabled(z);
    }

    public TLObject getObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
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
                    if (user != null && this.currentStatus == null && !z && (i & 4) != 0) {
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
                this.lastStatus = userStatus3 != null ? userStatus3.expires : 0;
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence, true);
                } else {
                    if (str3 == null) {
                        str3 = UserObject.getUserName(user);
                    }
                    this.lastName = str3;
                    this.nameTextView.setText(this.lastName);
                }
                if (this.currentStatus == null) {
                    if (user.bot) {
                        this.statusTextView.setTag("windowBackgroundWhiteGrayText");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                        this.statusTextView.setText(LocaleController.getString("Bot", NUM));
                    } else if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (((userStatus = user.status) != null && userStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                        this.statusTextView.setTag("windowBackgroundWhiteBlueText");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
                        this.statusTextView.setText(LocaleController.getString("Online", NUM));
                    } else {
                        this.statusTextView.setTag("windowBackgroundWhiteGrayText");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                        this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
                    }
                }
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            } else {
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
                    this.nameTextView.setText(charSequence2, true);
                } else {
                    if (str == null) {
                        str = chat.title;
                    }
                    this.lastName = str;
                    this.nameTextView.setText(this.lastName);
                }
                if (this.currentStatus == null) {
                    this.statusTextView.setTag("windowBackgroundWhiteGrayText");
                    this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
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
                this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
            }
            CharSequence charSequence3 = this.currentStatus;
            if (charSequence3 != null) {
                this.statusTextView.setText(charSequence3, true);
                this.statusTextView.setTag("windowBackgroundWhiteGrayText");
                this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            float f = 0.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (this.padding + 72));
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = (float) (this.padding + 72);
            }
            canvas.drawRect((float) dp, (float) (getMeasuredHeight() - 1), (float) (measuredWidth - AndroidUtilities.dp(f)), (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }
}
