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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
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

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        int i;
        float f;
        Context context2 = context;
        int i2 = checkbox;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i3 = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (7 + padding), 8.0f, LocaleController.isRTL ? (float) (7 + padding) : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = r0.nameTextView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i5 = 18;
        if (LocaleController.isRTL) {
            i = (i2 == 2 ? 18 : 0) + 28;
        } else {
            i = 68 + padding;
        }
        float f2 = (float) i;
        if (LocaleController.isRTL) {
            f = (float) (68 + padding);
        } else {
            if (i2 != 2) {
                i5 = 0;
            }
            f = (float) (28 + i5);
        }
        i = 2;
        boolean z = false;
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i4, f2, 11.5f, f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(14);
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view = r0.statusTextView;
        i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f3 = 28.0f;
        f = LocaleController.isRTL ? 28.0f : (float) (68 + padding);
        if (LocaleController.isRTL) {
            f3 = (float) (68 + padding);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i4, f, 34.5f, f3, 0.0f));
        r0.imageView = new ImageView(context2);
        r0.imageView.setScaleType(ScaleType.CENTER);
        r0.imageView.setVisibility(8);
        addView(r0.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i2 == i) {
            r0.checkBoxBig = new CheckBoxSquare(context2, z);
            addView(r0.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i2 == 1) {
            r0.checkBox = new CheckBox(context2, R.drawable.round_check2);
            r0.checkBox.setVisibility(4);
            r0.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(r0.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (37 + padding), 38.0f, LocaleController.isRTL ? (float) (37 + padding) : 0.0f, 0.0f));
        }
        if (admin) {
            r0.adminImage = new ImageView(context2);
            r0.adminImage.setImageResource(R.drawable.admin_star);
            View view2 = r0.adminImage;
            if (!LocaleController.isRTL) {
                i3 = 5;
            }
            addView(view2, LayoutHelper.createFrame(16, 16.0f, i3 | 48, LocaleController.isRTL ? 24.0f : 0.0f, 13.5f, LocaleController.isRTL ? 0.0f : 24.0f, 0.0f));
        }
    }

    public void setIsAdmin(int value) {
        if (this.adminImage != null) {
            this.adminImage.setVisibility(value != 0 ? 0 : 8);
            SimpleTextView simpleTextView = this.nameTextView;
            int dp = (!LocaleController.isRTL || value == 0) ? 0 : AndroidUtilities.dp(16.0f);
            int dp2 = (LocaleController.isRTL || value == 0) ? 0 : AndroidUtilities.dp(16.0f);
            simpleTextView.setPadding(dp, 0, dp2, 0);
            if (value == 1) {
                setTag(Theme.key_profile_creatorIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_creatorIcon), Mode.MULTIPLY));
            } else if (value == 2) {
                setTag(Theme.key_profile_adminIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_adminIcon), Mode.MULTIPLY));
            }
        }
    }

    public void setData(TLObject user, CharSequence name, CharSequence status, int resId) {
        if (user == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.statusTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = status;
        this.currentName = name;
        this.currentObject = user;
        this.currentDrawable = resId;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox != null) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
        } else if (this.checkBoxBig != null) {
            if (this.checkBoxBig.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        if (this.checkBoxBig != null) {
            this.checkBoxBig.setDisabled(disabled);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void invalidate() {
        super.invalidate();
        if (this.checkBoxBig != null) {
            this.checkBoxBig.invalidate();
        }
    }

    public void update(int mask) {
        if (this.currentObject != null) {
            TLObject photo = null;
            String newName = null;
            User currentUser = null;
            Chat currentChat = null;
            if (this.currentObject instanceof User) {
                currentUser = (User) this.currentObject;
                if (currentUser.photo != null) {
                    photo = currentUser.photo.photo_small;
                }
            } else {
                currentChat = (Chat) this.currentObject;
                if (currentChat.photo != null) {
                    photo = currentChat.photo.photo_small;
                }
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || !(this.lastAvatar != null || photo == null || this.lastAvatar == null || photo == null || (this.lastAvatar.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id)))) {
                    continueUpdate = true;
                }
                if (!(currentUser == null || continueUpdate || (mask & 4) == 0)) {
                    int newStatus = 0;
                    if (currentUser.status != null) {
                        newStatus = currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || this.currentName != null || this.lastName == null || (mask & 1) == 0)) {
                    if (currentUser != null) {
                        newName = UserObject.getUserName(currentUser);
                    } else {
                        newName = currentChat.title;
                    }
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            int i = 0;
            if (currentUser != null) {
                this.avatarDrawable.setInfo(currentUser);
                if (currentUser.status != null) {
                    this.lastStatus = currentUser.status.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                this.avatarDrawable.setInfo(currentChat);
            }
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName);
            } else {
                if (currentUser != null) {
                    this.lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
                } else {
                    this.lastName = newName == null ? currentChat.title : newName;
                }
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != null) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (currentUser != null) {
                if (currentUser.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (!currentUser.bot_chat_history) {
                        if (this.adminImage == null || this.adminImage.getVisibility() != 0) {
                            this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", R.string.BotStatusCantRead));
                        }
                    }
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", R.string.BotStatusRead));
                } else {
                    if (currentUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (currentUser.status == null || currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
                        if (!MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id))) {
                            this.statusTextView.setTextColor(this.statusColor);
                            this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
                        }
                    }
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
                }
            }
            if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
                ImageView imageView = this.imageView;
                if (this.currentDrawable == 0) {
                    i = 8;
                }
                imageView.setVisibility(i);
                this.imageView.setImageResource(this.currentDrawable);
            }
            this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
