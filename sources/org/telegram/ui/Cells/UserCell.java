package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0431R;
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
    private int currentId;
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
        float f;
        float f2;
        super(context);
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 8.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = this.nameTextView;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = (float) ((checkbox == 2 ? 18 : 0) + 28);
        } else {
            f = (float) (padding + 68);
        }
        if (LocaleController.isRTL) {
            f2 = (float) (padding + 68);
        } else {
            f2 = (float) ((checkbox == 2 ? 18 : 0) + 28);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i, f, 11.5f, f2, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (padding + 68), 34.5f, LocaleController.isRTL ? (float) (padding + 68) : 28.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (checkbox == 2) {
            this.checkBoxBig = new CheckBoxSquare(context, false);
            addView(this.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (checkbox == 1) {
            this.checkBox = new CheckBox(context, C0431R.drawable.round_check2);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 38.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
        if (admin) {
            this.adminImage = new ImageView(context);
            this.adminImage.setImageResource(C0431R.drawable.admin_star);
            addView(this.adminImage, LayoutHelper.createFrame(16, 16.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 24.0f : 0.0f, 13.5f, LocaleController.isRTL ? 0.0f : 24.0f, 0.0f));
        }
    }

    public void setIsAdmin(int value) {
        if (this.adminImage != null) {
            int i;
            this.adminImage.setVisibility(value != 0 ? 0 : 8);
            SimpleTextView simpleTextView = this.nameTextView;
            if (!LocaleController.isRTL || value == 0) {
                i = 0;
            } else {
                i = AndroidUtilities.dp(16.0f);
            }
            int dp = (LocaleController.isRTL || value == 0) ? 0 : AndroidUtilities.dp(16.0f);
            simpleTextView.setPadding(i, 0, dp, 0);
            if (value == 1) {
                setTag(Theme.key_profile_creatorIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_creatorIcon), Mode.MULTIPLY));
            } else if (value == 2) {
                setTag(Theme.key_profile_adminIcon);
                this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_adminIcon), Mode.MULTIPLY));
            }
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
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
        this.currentObject = object;
        this.currentDrawable = resId;
        update(0);
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int id) {
        this.currentId = id;
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
        TLObject photo = null;
        String newName = null;
        User currentUser = null;
        Chat currentChat = null;
        if (this.currentObject instanceof User) {
            currentUser = this.currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
        } else if (this.currentObject instanceof Chat) {
            currentChat = this.currentObject;
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
        if (currentUser != null) {
            this.avatarDrawable.setInfo(currentUser);
            if (currentUser.status != null) {
                this.lastStatus = currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
        } else if (currentChat != null) {
            this.avatarDrawable.setInfo(currentChat);
        } else if (this.currentName != null) {
            this.avatarDrawable.setInfo(this.currentId, this.currentName.toString(), null, false);
        } else {
            this.avatarDrawable.setInfo(this.currentId, "#", null, false);
        }
        if (this.currentName != null) {
            this.lastName = null;
            this.nameTextView.setText(this.currentName);
        } else {
            if (currentUser != null) {
                String userName;
                if (newName == null) {
                    userName = UserObject.getUserName(currentUser);
                } else {
                    userName = newName;
                }
                this.lastName = userName;
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
                if (currentUser.bot_chat_history || (this.adminImage != null && this.adminImage.getVisibility() == 0)) {
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", C0431R.string.BotStatusRead));
                } else {
                    this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", C0431R.string.BotStatusCantRead));
                }
            } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id)))) {
                this.statusTextView.setTextColor(this.statusOnlineColor);
                this.statusTextView.setText(LocaleController.getString("Online", C0431R.string.Online));
            } else {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            }
        }
        if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
            this.imageView.setVisibility(this.currentDrawable == 0 ? 8 : 0);
            this.imageView.setImageResource(this.currentDrawable);
        }
        this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
