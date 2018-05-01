package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
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
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class UserCell extends FrameLayout {
    private ImageView adminImage;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentDrawable;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ImageView imageView;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
    private int statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public UserCell(Context context, int i, int i2, boolean z) {
        int i3;
        float f;
        Context context2 = context;
        int i4 = i2;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i5 = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (7 + i), 8.0f, LocaleController.isRTL ? (float) (7 + i) : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = r0.nameTextView;
        int i6 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i7 = 18;
        if (LocaleController.isRTL) {
            i3 = (i4 == 2 ? 18 : 0) + 28;
        } else {
            i3 = 68 + i;
        }
        float f2 = (float) i3;
        if (LocaleController.isRTL) {
            f = (float) (68 + i);
        } else {
            if (i4 != 2) {
                i7 = 0;
            }
            f = (float) (28 + i7);
        }
        i3 = 2;
        boolean z2 = false;
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i6, f2, 11.5f, f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(14);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view = r0.statusTextView;
        i6 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f3 = 28.0f;
        f = LocaleController.isRTL ? 28.0f : (float) (68 + i);
        if (LocaleController.isRTL) {
            f3 = (float) (68 + i);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i6, f, 34.5f, f3, 0.0f));
        r0.imageView = new ImageView(context2);
        r0.imageView.setScaleType(ScaleType.CENTER);
        r0.imageView.setVisibility(8);
        addView(r0.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i4 == i3) {
            r0.checkBoxBig = new CheckBoxSquare(context2, z2);
            addView(r0.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i4 == 1) {
            r0.checkBox = new CheckBox(context2, C0446R.drawable.round_check2);
            r0.checkBox.setVisibility(4);
            r0.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(r0.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (37 + i), 38.0f, LocaleController.isRTL ? (float) (37 + i) : 0.0f, 0.0f));
        }
        if (z) {
            r0.adminImage = new ImageView(context2);
            r0.adminImage.setImageResource(C0446R.drawable.admin_star);
            View view2 = r0.adminImage;
            if (!LocaleController.isRTL) {
                i5 = 5;
            }
            addView(view2, LayoutHelper.createFrame(16, 16.0f, i5 | 48, LocaleController.isRTL ? 24.0f : 0.0f, 13.5f, LocaleController.isRTL ? 0.0f : 24.0f, 0.0f));
        }
    }

    public void setIsAdmin(int i) {
        if (this.adminImage != null) {
            this.adminImage.setVisibility(i != 0 ? 0 : 8);
            SimpleTextView simpleTextView = this.nameTextView;
            int dp = (!LocaleController.isRTL || i == 0) ? 0 : AndroidUtilities.dp(16.0f);
            int dp2 = (LocaleController.isRTL || i == 0) ? 0 : AndroidUtilities.dp(16.0f);
            simpleTextView.setPadding(dp, 0, dp2, 0);
            if (i == 1) {
                setTag(Theme.key_profile_creatorIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_creatorIcon), Mode.MULTIPLY));
            } else if (i == 2) {
                setTag(Theme.key_profile_adminIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_adminIcon), Mode.MULTIPLY));
            }
        }
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i) {
        if (tLObject == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.statusTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = tLObject;
        this.currentDrawable = i;
        update(null);
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox != null) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
        } else if (this.checkBoxBig != null) {
            if (this.checkBoxBig.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(z, z2);
        }
    }

    public void setCheckDisabled(boolean z) {
        if (this.checkBoxBig != null) {
            this.checkBoxBig.setDisabled(z);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void invalidate() {
        super.invalidate();
        if (this.checkBoxBig != null) {
            this.checkBoxBig.invalidate();
        }
    }

    public void update(int i) {
        if (this.currentObject != null) {
            User user;
            TLObject tLObject;
            Chat chat;
            if (this.currentObject instanceof User) {
                user = (User) this.currentObject;
                if (user.photo != null) {
                    tLObject = user.photo.photo_small;
                    chat = null;
                } else {
                    tLObject = null;
                    chat = tLObject;
                }
            } else {
                Chat chat2 = (Chat) this.currentObject;
                if (chat2.photo != null) {
                    tLObject = chat2.photo.photo_small;
                    chat = chat2;
                    user = null;
                } else {
                    chat = chat2;
                    user = null;
                    tLObject = user;
                }
            }
            if (i != 0) {
                int i2 = ((i & 2) == 0 || ((this.lastAvatar == null || tLObject != null) && (this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)))) ? 0 : 1;
                if (!(user == null || i2 != 0 || (i & 4) == 0)) {
                    if ((user.status != null ? user.status.expires : 0) != this.lastStatus) {
                        i2 = 1;
                    }
                }
                if (i2 != 0 || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                    i = 0;
                } else {
                    if (user != null) {
                        i = UserObject.getUserName(user);
                    } else {
                        i = chat.title;
                    }
                    if (!i.equals(this.lastName)) {
                        i2 = 1;
                    }
                }
                if (i2 == 0) {
                    return;
                }
            }
            i = 0;
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                if (user.status != null) {
                    this.lastStatus = user.status.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                this.avatarDrawable.setInfo(chat);
            }
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName);
            } else {
                if (user != null) {
                    if (i == 0) {
                        i = UserObject.getUserName(user);
                    }
                    this.lastName = i;
                } else {
                    if (i == 0) {
                        i = chat.title;
                    }
                    this.lastName = i;
                }
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != 0) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (user != null) {
                if (user.bot != 0) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (user.bot_chat_history == 0) {
                        if (this.adminImage == 0 || this.adminImage.getVisibility() != 0) {
                            this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", C0446R.string.BotStatusCantRead));
                        }
                    }
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", C0446R.string.BotStatusRead));
                } else {
                    if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (user.status == 0 || user.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                        if (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id)) == 0) {
                            this.statusTextView.setTextColor(this.statusColor);
                            this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, user));
                        }
                    }
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", C0446R.string.Online));
                }
            }
            int i3 = 8;
            if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
                i = this.imageView;
                if (this.currentDrawable != 0) {
                    i3 = 0;
                }
                i.setVisibility(i3);
                this.imageView.setImageResource(this.currentDrawable);
            }
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
    }
}
