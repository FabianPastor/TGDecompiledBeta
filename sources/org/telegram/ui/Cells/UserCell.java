package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

public class UserCell extends FrameLayout {
    private TextView adminTextView;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private EncryptedChat encryptedChat;
    private ImageView imageView;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        float f;
        float f2;
        super(context);
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 6.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.nameTextView;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = (float) ((checkbox == 2 ? 18 : 0) + 28);
        } else {
            f = (float) (padding + 64);
        }
        if (LocaleController.isRTL) {
            f2 = (float) (padding + 64);
        } else {
            f2 = (float) ((checkbox == 2 ? 18 : 0) + 28);
        }
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i, f, 10.0f, f2, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (padding + 64), 32.0f, LocaleController.isRTL ? (float) (padding + 64) : 28.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (checkbox == 2) {
            this.checkBoxBig = new CheckBoxSquare(context, false);
            addView(this.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (checkbox == 1) {
            this.checkBox = new CheckBox(context, NUM);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 40.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
        if (admin) {
            this.adminTextView = new TextView(context);
            this.adminTextView.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon"));
            addView(this.adminTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 23.0f : 0.0f, 15.0f, LocaleController.isRTL ? 0.0f : 23.0f, 0.0f));
        }
        setFocusable(true);
    }

    public void setAvatarPadding(int padding) {
        float f;
        int i = 18;
        float f2 = 28.0f;
        float f3 = 0.0f;
        LayoutParams layoutParams = (LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 7));
        if (LocaleController.isRTL) {
            f = (float) (padding + 7);
        } else {
            f = 0.0f;
        }
        layoutParams.rightMargin = AndroidUtilities.dp(f);
        this.avatarImageView.setLayoutParams(layoutParams);
        layoutParams = (LayoutParams) this.nameTextView.getLayoutParams();
        if (LocaleController.isRTL) {
            int i2;
            if (this.checkBoxBig != null) {
                i2 = 18;
            } else {
                i2 = 0;
            }
            f = (float) (i2 + 28);
        } else {
            f = (float) (padding + 64);
        }
        layoutParams.leftMargin = AndroidUtilities.dp(f);
        if (LocaleController.isRTL) {
            f = (float) (padding + 64);
        } else {
            if (this.checkBoxBig == null) {
                i = 0;
            }
            f = (float) (i + 28);
        }
        layoutParams.rightMargin = AndroidUtilities.dp(f);
        layoutParams = (LayoutParams) this.statusTextView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : (float) (padding + 64));
        if (LocaleController.isRTL) {
            f2 = (float) (padding + 64);
        }
        layoutParams.rightMargin = AndroidUtilities.dp(f2);
        if (this.checkBox != null) {
            layoutParams = (LayoutParams) this.checkBox.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 37));
            if (LocaleController.isRTL) {
                f3 = (float) (padding + 37);
            }
            layoutParams.rightMargin = AndroidUtilities.dp(f3);
        }
    }

    public void setIsAdmin(int value) {
        if (this.adminTextView != null) {
            this.adminTextView.setVisibility(value != 0 ? 0 : 8);
            if (value == 1) {
                this.adminTextView.setText(LocaleController.getString("ChannelCreator", NUM));
            } else if (value == 2) {
                this.adminTextView.setText(LocaleController.getString("ChannelAdmin", NUM));
            }
            if (value != 0) {
                int dp;
                int i;
                CharSequence text = this.adminTextView.getText();
                int size = (int) Math.ceil((double) this.adminTextView.getPaint().measureText(text, 0, text.length()));
                SimpleTextView simpleTextView = this.nameTextView;
                if (LocaleController.isRTL) {
                    dp = AndroidUtilities.dp(6.0f) + size;
                } else {
                    dp = 0;
                }
                if (LocaleController.isRTL) {
                    i = 0;
                } else {
                    i = AndroidUtilities.dp(6.0f) + size;
                }
                simpleTextView.setPadding(dp, 0, i, 0);
                return;
            }
            this.nameTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        setData(object, null, name, status, resId, false);
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId, boolean divider) {
        setData(object, null, name, status, resId, divider);
    }

    public void setData(TLObject object, EncryptedChat ec, CharSequence name, CharSequence status, int resId, boolean divider) {
        if (object == null && name == null && status == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        boolean z;
        this.encryptedChat = ec;
        this.currentStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        this.needDivider = divider;
        if (this.needDivider) {
            z = false;
        } else {
            z = true;
        }
        setWillNotDraw(z);
        update(0);
    }

    public void setException(NotificationException exception, CharSequence name, boolean divider) {
        String text;
        boolean custom = exception.hasCustom;
        int value = exception.notify;
        int delta = exception.muteUntil;
        if (value != 3 || delta == Integer.MAX_VALUE) {
            boolean enabled;
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (enabled && custom) {
                text = LocaleController.getString("NotificationsCustom", NUM);
            } else if (enabled) {
                text = LocaleController.getString("NotificationsUnmuted", NUM);
            } else {
                text = LocaleController.getString("NotificationsMuted", NUM);
            }
        } else {
            delta -= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", NUM);
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", NUM);
                }
            } else if (delta < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", delta / 60));
            } else if (delta < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / 60.0f) / 60.0f))));
            } else if (delta < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / 60.0f) / 60.0f) / 24.0f))));
            } else {
                text = null;
            }
        }
        if (text == null) {
            text = LocaleController.getString("NotificationsOff", NUM);
        }
        int lower_id = (int) exception.did;
        int high_id = (int) (exception.did >> 32);
        if (lower_id == 0) {
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat != null) {
                TLObject user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                if (user != null) {
                    setData(user, encryptedChat, name, text, 0, false);
                }
            }
        } else if (lower_id > 0) {
            User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user2 != null) {
                setData(user2, null, name, text, 0, divider);
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                setData(chat, null, name, text, 0, divider);
            }
        }
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(58.0f), NUM));
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
        if (this.currentStatus != null) {
            this.statusTextView.setTextColor(this.statusColor);
            this.statusTextView.setText(this.currentStatus);
        } else if (currentUser != null) {
            if (currentUser.bot) {
                this.statusTextView.setTextColor(this.statusColor);
                if (currentUser.bot_chat_history || (this.adminTextView != null && this.adminTextView.getVisibility() == 0)) {
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
                } else {
                    this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
                }
            } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id)))) {
                this.statusTextView.setTextColor(this.statusOnlineColor);
                this.statusTextView.setText(LocaleController.getString("Online", NUM));
            } else {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            }
        }
        if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
            this.imageView.setVisibility(this.currentDrawable == 0 ? 8 : 0);
            this.imageView.setImageResource(this.currentDrawable);
        }
        this.avatarImageView.setImage(photo, "50_50", this.avatarDrawable, this.currentObject);
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.checkBoxBig != null && this.checkBoxBig.getVisibility() == 0) {
            info.setCheckable(true);
            info.setChecked(this.checkBoxBig.isChecked());
            info.setClassName("android.widget.CheckBox");
        } else if (this.checkBox != null && this.checkBox.getVisibility() == 0) {
            info.setCheckable(true);
            info.setChecked(this.checkBox.isChecked());
            info.setClassName("android.widget.CheckBox");
        }
    }
}
