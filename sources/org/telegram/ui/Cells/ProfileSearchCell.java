package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.NotificationsSettingsActivity;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private TLRPC.Chat chat;
    CheckBox2 checkBox;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(19.0f);
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private TLRPC.EncryptedChat encryptedChat;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameTop;
    private int nameWidth;
    private RectF rect = new RectF();
    private boolean savedMessages;
    private StaticLayout statusLayout;
    private int statusLeft;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    public boolean useSeparator;
    private TLRPC.User user;

    public ProfileSearchCell(Context context) {
        super(context);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox);
    }

    public void setData(TLObject object, TLRPC.EncryptedChat ec, CharSequence n, CharSequence s, boolean needCount, boolean saved) {
        this.currentName = n;
        if (object instanceof TLRPC.User) {
            this.user = (TLRPC.User) object;
            this.chat = null;
        } else if (object instanceof TLRPC.Chat) {
            this.chat = (TLRPC.Chat) object;
            this.user = null;
        }
        this.encryptedChat = ec;
        this.subLabel = s;
        this.drawCount = needCount;
        this.savedMessages = saved;
        update(0);
    }

    public void setException(NotificationsSettingsActivity.NotificationException exception, CharSequence name) {
        String text;
        TLRPC.User user2;
        boolean enabled;
        boolean custom = exception.hasCustom;
        int value = exception.notify;
        int delta = exception.muteUntil;
        if (value != 3 || delta == Integer.MAX_VALUE) {
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (!enabled || !custom) {
                text = enabled ? LocaleController.getString("NotificationsUnmuted", NUM) : LocaleController.getString("NotificationsMuted", NUM);
            } else {
                text = LocaleController.getString("NotificationsCustom", NUM);
            }
        } else {
            int delta2 = delta - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta2 <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", NUM);
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", NUM);
                }
            } else if (delta2 < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", delta2 / 60));
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
            } else if (delta2 < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
            } else {
                text = null;
            }
        }
        if (text == null) {
            text = LocaleController.getString("NotificationsOff", NUM);
        }
        if (DialogObject.isEncryptedDialog(exception.did)) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(exception.did)));
            if (encryptedChat2 != null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat2.user_id))) != null) {
                setData(user2, encryptedChat2, name, text, false, false);
            }
        } else if (DialogObject.isUserDialog(exception.did)) {
            TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(exception.did));
            if (user3 != null) {
                setData(user3, (TLRPC.EncryptedChat) null, name, text, false, false);
            }
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-exception.did));
            if (chat2 != null) {
                setData(chat2, (TLRPC.EncryptedChat) null, name, text, false, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(60.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.checkBox != null) {
                int x = LocaleController.isRTL ? (right - left) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
                int y = AndroidUtilities.dp(36.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(x, y, checkBox2.getMeasuredWidth() + x, this.checkBox.getMeasuredHeight() + y);
            }
            if (changed) {
                buildLayout();
            }
        }
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    public void setSublabelOffset(int x, int y) {
        this.sublabelOffsetX = x;
        this.sublabelOffsetY = y;
    }

    public void buildLayout() {
        CharSequence nameString;
        TextPaint currentNamePaint;
        int statusWidth;
        CharSequence statusString;
        int statusWidth2;
        int avatarLeft;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        TLRPC.EncryptedChat encryptedChat2 = this.encryptedChat;
        if (encryptedChat2 != null) {
            this.drawNameLock = true;
            this.dialog_id = DialogObject.makeEncryptedDialogId((long) encryptedChat2.id);
            if (!LocaleController.isRTL) {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(11.0f);
            }
            this.nameLockTop = AndroidUtilities.dp(22.0f);
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                this.dialog_id = -chat2.id;
                if (SharedConfig.drawDialogIcons) {
                    if (!ChatObject.isChannel(this.chat) || this.chat.megagroup) {
                        this.drawNameGroup = true;
                        this.nameLockTop = AndroidUtilities.dp(24.0f);
                    } else {
                        this.drawNameBroadcast = true;
                        this.nameLockTop = AndroidUtilities.dp(22.5f);
                    }
                }
                this.drawCheck = this.chat.verified;
                if (SharedConfig.drawDialogIcons) {
                    if (!LocaleController.isRTL) {
                        this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (this.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                    } else {
                        this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - (this.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                } else if (!LocaleController.isRTL) {
                    this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                } else {
                    this.nameLeft = AndroidUtilities.dp(11.0f);
                }
            } else {
                TLRPC.User user2 = this.user;
                if (user2 != null) {
                    this.dialog_id = user2.id;
                    if (!LocaleController.isRTL) {
                        this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    } else {
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                    if (!SharedConfig.drawDialogIcons || !this.user.bot || MessagesController.isSupportUser(this.user)) {
                        this.nameLockTop = AndroidUtilities.dp(21.0f);
                    } else {
                        this.drawNameBot = true;
                        if (!LocaleController.isRTL) {
                            this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                            this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                        } else {
                            this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                            this.nameLeft = AndroidUtilities.dp(11.0f);
                        }
                        this.nameLockTop = AndroidUtilities.dp(20.5f);
                    }
                    this.drawCheck = this.user.verified;
                }
            }
        }
        if (this.currentName != null) {
            nameString = this.currentName;
        } else {
            String nameString2 = "";
            TLRPC.Chat chat3 = this.chat;
            if (chat3 != null) {
                nameString2 = chat3.title;
            } else {
                TLRPC.User user3 = this.user;
                if (user3 != null) {
                    nameString2 = UserObject.getUserName(user3);
                }
            }
            nameString = nameString2.replace(10, ' ');
        }
        if (nameString.length() == 0) {
            TLRPC.User user4 = this.user;
            if (user4 == null || user4.phone == null || this.user.phone.length() == 0) {
                nameString = LocaleController.getString("HiddenName", NUM);
            } else {
                nameString = PhoneFormat.getInstance().format("+" + this.user.phone);
            }
        }
        if (this.encryptedChat != null) {
            currentNamePaint = Theme.dialogs_searchNameEncryptedPaint;
        } else {
            currentNamePaint = Theme.dialogs_searchNamePaint;
        }
        if (!LocaleController.isRTL) {
            statusWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f);
            this.nameWidth = statusWidth;
        } else {
            statusWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            this.nameWidth = statusWidth;
        }
        if (this.drawNameLock) {
            this.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (this.drawNameBroadcast) {
            this.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (this.drawNameGroup) {
            this.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (this.drawNameBot) {
            this.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        this.nameWidth -= getPaddingLeft() + getPaddingRight();
        int statusWidth3 = statusWidth - (getPaddingLeft() + getPaddingRight());
        if (this.drawCount != 0) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog == null || dialog.unread_count == 0) {
                this.lastUnreadCount = 0;
                this.countLayout = null;
            } else {
                this.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", new Object[]{Integer.valueOf(dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int w = this.countWidth + AndroidUtilities.dp(18.0f);
                this.nameWidth -= w;
                if (!LocaleController.isRTL) {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
                } else {
                    this.countLeft = AndroidUtilities.dp(19.0f);
                    this.nameLeft += w;
                }
            }
        } else {
            this.lastUnreadCount = 0;
            this.countLayout = null;
        }
        if (this.nameWidth < 0) {
            this.nameWidth = 0;
        }
        StaticLayout staticLayout = r6;
        StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(nameString, currentNamePaint, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), currentNamePaint, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.nameLayout = staticLayout;
        CharSequence statusString2 = null;
        TextPaint currentStatusPaint = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC.Chat chat4 = this.chat;
        if (chat4 == null || this.subLabel != null) {
            if (this.subLabel != null) {
                statusString2 = this.subLabel;
            } else {
                TLRPC.User user5 = this.user;
                if (user5 != null) {
                    if (MessagesController.isSupportUser(user5)) {
                        statusString2 = LocaleController.getString("SupportStatus", NUM);
                    } else if (this.user.bot) {
                        statusString2 = LocaleController.getString("Bot", NUM);
                    } else if (this.user.id == 333000 || this.user.id == 777000) {
                        statusString2 = LocaleController.getString("ServiceNotifications", NUM);
                    } else {
                        statusString2 = LocaleController.formatUserStatus(this.currentAccount, this.user);
                        TLRPC.User user6 = this.user;
                        if (user6 != null && (user6.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                            currentStatusPaint = Theme.dialogs_onlinePaint;
                            statusString2 = LocaleController.getString("Online", NUM);
                        }
                    }
                }
            }
            if (this.savedMessages || UserObject.isReplyUser(this.user)) {
                statusString = null;
                this.nameTop = AndroidUtilities.dp(20.0f);
            }
        } else {
            if (!ChatObject.isChannel(chat4) || this.chat.megagroup) {
                if (this.chat.participants_count != 0) {
                    statusString = LocaleController.formatPluralString("Members", this.chat.participants_count);
                } else if (this.chat.has_geo) {
                    statusString = LocaleController.getString("MegaLocation", NUM);
                } else if (TextUtils.isEmpty(this.chat.username)) {
                    statusString = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                } else {
                    statusString = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                }
            } else if (this.chat.participants_count != 0) {
                statusString = LocaleController.formatPluralString("Subscribers", this.chat.participants_count);
            } else if (TextUtils.isEmpty(this.chat.username)) {
                statusString = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
            } else {
                statusString = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
            }
            this.nameTop = AndroidUtilities.dp(19.0f);
        }
        if (!TextUtils.isEmpty(statusString)) {
            StaticLayout staticLayout3 = r8;
            CharSequence charSequence = statusString;
            statusWidth2 = statusWidth3;
            StaticLayout staticLayout4 = new StaticLayout(TextUtils.ellipsize(statusString, currentStatusPaint, (float) (statusWidth3 - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), currentStatusPaint, statusWidth3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.statusLayout = staticLayout3;
            this.nameTop = AndroidUtilities.dp(9.0f);
            this.nameLockTop -= AndroidUtilities.dp(10.0f);
        } else {
            statusWidth2 = statusWidth3;
            this.nameTop = AndroidUtilities.dp(20.0f);
            this.statusLayout = null;
        }
        if (LocaleController.isRTL) {
            avatarLeft = (getMeasuredWidth() - AndroidUtilities.dp(57.0f)) - getPaddingRight();
        } else {
            avatarLeft = AndroidUtilities.dp(11.0f) + getPaddingLeft();
        }
        this.avatarImage.setImageCoords((float) avatarLeft, (float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
        if (LocaleController.isRTL) {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineLeft(0) == 0.0f) {
                double widthpx = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i = this.nameWidth;
                if (widthpx < ((double) i)) {
                    double d = (double) this.nameLeft;
                    double d2 = (double) i;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    this.nameLeft = (int) (d + (d2 - widthpx));
                }
            }
            StaticLayout staticLayout5 = this.statusLayout;
            if (staticLayout5 != null && staticLayout5.getLineCount() > 0 && this.statusLayout.getLineLeft(0) == 0.0f) {
                double widthpx2 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                if (widthpx2 < ((double) statusWidth2)) {
                    double d3 = (double) this.statusLeft;
                    double d4 = (double) statusWidth2;
                    Double.isNaN(d4);
                    Double.isNaN(d3);
                    this.statusLeft = (int) (d3 + (d4 - widthpx2));
                }
            }
        } else {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineRight(0) == ((float) this.nameWidth)) {
                double widthpx3 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i2 = this.nameWidth;
                if (widthpx3 < ((double) i2)) {
                    double d5 = (double) this.nameLeft;
                    double d6 = (double) i2;
                    Double.isNaN(d6);
                    Double.isNaN(d5);
                    this.nameLeft = (int) (d5 - (d6 - widthpx3));
                }
            }
            StaticLayout staticLayout6 = this.statusLayout;
            if (staticLayout6 != null && staticLayout6.getLineCount() > 0 && this.statusLayout.getLineRight(0) == ((float) statusWidth2)) {
                double widthpx4 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                if (widthpx4 < ((double) statusWidth2)) {
                    double d7 = (double) this.statusLeft;
                    double d8 = (double) statusWidth2;
                    Double.isNaN(d8);
                    Double.isNaN(d7);
                    this.statusLeft = (int) (d7 - (d8 - widthpx4));
                }
            }
        }
        this.nameLeft += getPaddingLeft();
        this.statusLeft += getPaddingLeft();
        this.nameLockLeft += getPaddingLeft();
    }

    public void update(int mask) {
        TLRPC.Dialog dialog;
        String newName;
        TLRPC.User user2;
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation photo = null;
        TLRPC.User user3 = this.user;
        if (user3 != null) {
            this.avatarDrawable.setInfo(user3);
            if (UserObject.isReplyUser(this.user)) {
                this.avatarDrawable.setAvatarType(12);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, (Object) null, 0);
            } else if (this.savedMessages) {
                this.avatarDrawable.setAvatarType(1);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, (Object) null, 0);
            } else {
                if (this.user.photo != null) {
                    photo = this.user.photo.photo_small;
                }
                this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.user, 1), "50_50", ImageLocation.getForUserOrChat(this.user, 2), "50_50", (Drawable) this.avatarDrawable, (Object) this.user, 0);
            }
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                if (chat2.photo != null) {
                    photo = this.chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(this.chat);
                this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.chat, 1), "50_50", ImageLocation.getForUserOrChat(this.chat, 2), "50_50", (Drawable) this.avatarDrawable, (Object) this.chat, 0);
            } else {
                this.avatarDrawable.setInfo(0, (String) null, (String) null);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, (Object) null, 0);
            }
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if (!(((MessagesController.UPDATE_MASK_AVATAR & mask) == 0 || this.user == null) && ((MessagesController.UPDATE_MASK_CHAT_AVATAR & mask) == 0 || this.chat == null)) && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || !(fileLocation == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                continueUpdate = true;
            }
            if (!(continueUpdate || (MessagesController.UPDATE_MASK_STATUS & mask) == 0 || (user2 = this.user) == null)) {
                int newStatus = 0;
                if (user2.status != null) {
                    newStatus = this.user.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!((continueUpdate || (MessagesController.UPDATE_MASK_NAME & mask) == 0 || this.user == null) && ((MessagesController.UPDATE_MASK_CHAT_NAME & mask) == 0 || this.chat == null))) {
                if (this.user != null) {
                    newName = this.user.first_name + this.user.last_name;
                } else {
                    newName = this.chat.title;
                }
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!(continueUpdate || !this.drawCount || (MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE & mask) == 0 || (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id)) == null || dialog.unread_count == this.lastUnreadCount)) {
                continueUpdate = true;
            }
            if (!continueUpdate) {
                return;
            }
        }
        TLRPC.User user4 = this.user;
        if (user4 != null) {
            if (user4.status != null) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            this.lastName = this.user.first_name + this.user.last_name;
        } else {
            TLRPC.Chat chat3 = this.chat;
            if (chat3 != null) {
                this.lastName = chat3.title;
            }
        }
        this.lastAvatar = photo;
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x;
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            if (this.drawNameLock) {
                setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (!LocaleController.isRTL) {
                        x = (int) (((float) this.nameLeft) + this.nameLayout.getLineRight(0) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        x = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        float w = this.nameLayout.getLineWidth(0);
                        double d = (double) (this.nameLeft + this.nameWidth);
                        double ceil = Math.ceil((double) w);
                        Double.isNaN(d);
                        double d2 = d - ceil;
                        double dp = (double) AndroidUtilities.dp(6.0f);
                        Double.isNaN(dp);
                        double d3 = d2 - dp;
                        double intrinsicWidth = (double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                        Double.isNaN(intrinsicWidth);
                        x = (int) (d3 - intrinsicWidth);
                    }
                    setDrawableBounds(Theme.dialogs_verifiedDrawable, x, this.nameTop + AndroidUtilities.dp(3.0f));
                    setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, x, this.nameTop + AndroidUtilities.dp(3.0f));
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                }
            }
            if (this.statusLayout != null) {
                canvas.save();
                canvas.translate((float) (this.statusLeft + this.sublabelOffsetX), (float) (AndroidUtilities.dp(33.0f) + this.sublabelOffsetY));
                this.statusLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countLayout != null) {
                int x2 = this.countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set((float) x2, (float) this.countTop, (float) (this.countWidth + x2 + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            this.avatarImage.draw(canvas);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder builder = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            builder.append(staticLayout.getText());
        }
        if (this.statusLayout != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(this.statusLayout.getText());
        }
        info.setText(builder.toString());
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        }
    }
}
