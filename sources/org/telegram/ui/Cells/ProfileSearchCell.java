package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
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
import org.telegram.ui.NotificationsSettingsActivity;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private TLRPC.Chat chat;
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
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setData(TLObject tLObject, TLRPC.EncryptedChat encryptedChat2, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        this.currentName = charSequence;
        if (tLObject instanceof TLRPC.User) {
            this.user = (TLRPC.User) tLObject;
            this.chat = null;
        } else if (tLObject instanceof TLRPC.Chat) {
            this.chat = (TLRPC.Chat) tLObject;
            this.user = null;
        }
        this.encryptedChat = encryptedChat2;
        this.subLabel = charSequence2;
        this.drawCount = z;
        this.savedMessages = z2;
        update(0);
    }

    public void setException(NotificationsSettingsActivity.NotificationException notificationException, CharSequence charSequence) {
        String str;
        TLRPC.User user2;
        boolean z = notificationException.hasCustom;
        int i = notificationException.notify;
        int i2 = notificationException.muteUntil;
        boolean z2 = false;
        if (i != 3 || i2 == Integer.MAX_VALUE) {
            if (i == 0 || i == 1) {
                z2 = true;
            }
            str = (!z2 || !z) ? z2 ? LocaleController.getString("NotificationsUnmuted", NUM) : LocaleController.getString("NotificationsMuted", NUM) : LocaleController.getString("NotificationsCustom", NUM);
        } else {
            int currentTime = i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (currentTime <= 0) {
                str = z ? LocaleController.getString("NotificationsCustom", NUM) : LocaleController.getString("NotificationsUnmuted", NUM);
            } else if (currentTime < 3600) {
                str = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", currentTime / 60));
            } else if (currentTime < 86400) {
                str = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) currentTime) / 60.0f) / 60.0f))));
            } else {
                str = currentTime < 31536000 ? LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) currentTime) / 60.0f) / 60.0f) / 24.0f)))) : null;
            }
        }
        if (str == null) {
            str = LocaleController.getString("NotificationsOff", NUM);
        }
        String str2 = str;
        long j = notificationException.did;
        int i3 = (int) j;
        int i4 = (int) (j >> 32);
        if (i3 == 0) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i4));
            if (encryptedChat2 != null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id))) != null) {
                setData(user2, encryptedChat2, charSequence, str2, false, false);
            }
        } else if (i3 > 0) {
            TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i3));
            if (user3 != null) {
                setData(user3, (TLRPC.EncryptedChat) null, charSequence, str2, false, false);
            }
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i3));
            if (chat2 != null) {
                setData(chat2, (TLRPC.EncryptedChat) null, charSequence, str2, false, false);
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
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(60.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!(this.user == null && this.chat == null && this.encryptedChat == null) && z) {
            buildLayout();
        }
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    public void setSublabelOffset(int i, int i2) {
        this.sublabelOffsetX = i;
        this.sublabelOffsetY = i2;
    }

    public void buildLayout() {
        TextPaint textPaint;
        int i;
        CharSequence charSequence;
        int i2;
        TLRPC.UserStatus userStatus;
        int i3;
        String str;
        String str2;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        TLRPC.EncryptedChat encryptedChat2 = this.encryptedChat;
        if (encryptedChat2 != null) {
            this.drawNameLock = true;
            this.dialog_id = ((long) encryptedChat2.id) << 32;
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
                this.dialog_id = (long) (-chat2.id);
                if (SharedConfig.drawDialogIcons) {
                    if (!ChatObject.isChannel(chat2) || this.chat.megagroup) {
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
                    this.dialog_id = (long) user2.id;
                    if (!LocaleController.isRTL) {
                        this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    } else {
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                    if (SharedConfig.drawDialogIcons) {
                        TLRPC.User user3 = this.user;
                        if (user3.bot && !MessagesController.isSupportUser(user3)) {
                            this.drawNameBot = true;
                            if (!LocaleController.isRTL) {
                                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                            } else {
                                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                                this.nameLeft = AndroidUtilities.dp(11.0f);
                            }
                            this.nameLockTop = AndroidUtilities.dp(20.5f);
                            this.drawCheck = this.user.verified;
                        }
                    }
                    this.nameLockTop = AndroidUtilities.dp(21.0f);
                    this.drawCheck = this.user.verified;
                }
            }
        }
        CharSequence charSequence2 = this.currentName;
        if (charSequence2 == null) {
            TLRPC.Chat chat3 = this.chat;
            if (chat3 != null) {
                str2 = chat3.title;
            } else {
                TLRPC.User user4 = this.user;
                str2 = user4 != null ? UserObject.getUserName(user4) : "";
            }
            charSequence2 = str2.replace(10, ' ');
        }
        if (charSequence2.length() == 0) {
            TLRPC.User user5 = this.user;
            if (user5 == null || (str = user5.phone) == null || str.length() == 0) {
                charSequence2 = LocaleController.getString("HiddenName", NUM);
            } else {
                charSequence2 = PhoneFormat.getInstance().format("+" + this.user.phone);
            }
        }
        if (this.encryptedChat != null) {
            textPaint = Theme.dialogs_searchNameEncryptedPaint;
        } else {
            textPaint = Theme.dialogs_searchNamePaint;
        }
        TextPaint textPaint2 = textPaint;
        if (!LocaleController.isRTL) {
            i = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f);
            this.nameWidth = i;
        } else {
            i = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            this.nameWidth = i;
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
        int paddingLeft = i - (getPaddingLeft() + getPaddingRight());
        if (this.drawCount) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog == null || (i3 = dialog.unread_count) == 0) {
                this.lastUnreadCount = 0;
                this.countLayout = null;
            } else {
                this.lastUnreadCount = i3;
                String format = String.format("%d", new Object[]{Integer.valueOf(i3)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                this.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int dp = this.countWidth + AndroidUtilities.dp(18.0f);
                this.nameWidth -= dp;
                if (!LocaleController.isRTL) {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
                } else {
                    this.countLeft = AndroidUtilities.dp(19.0f);
                    this.nameLeft += dp;
                }
            }
        } else {
            this.lastUnreadCount = 0;
            this.countLayout = null;
        }
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence2, textPaint2, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint2, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        TextPaint textPaint3 = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC.Chat chat4 = this.chat;
        if (chat4 == null || this.subLabel != null) {
            CharSequence charSequence3 = this.subLabel;
            if (charSequence3 == null) {
                TLRPC.User user6 = this.user;
                if (user6 == null) {
                    charSequence3 = null;
                } else if (MessagesController.isSupportUser(user6)) {
                    charSequence3 = LocaleController.getString("SupportStatus", NUM);
                } else {
                    TLRPC.User user7 = this.user;
                    if (user7.bot) {
                        charSequence3 = LocaleController.getString("Bot", NUM);
                    } else {
                        int i4 = user7.id;
                        if (i4 == 333000 || i4 == 777000) {
                            charSequence3 = LocaleController.getString("ServiceNotifications", NUM);
                        } else {
                            charSequence3 = LocaleController.formatUserStatus(this.currentAccount, user7);
                            TLRPC.User user8 = this.user;
                            if (user8 != null && (user8.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((userStatus = this.user.status) != null && userStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                                textPaint3 = Theme.dialogs_onlinePaint;
                                charSequence3 = LocaleController.getString("Online", NUM);
                            }
                        }
                    }
                }
            }
            if (this.savedMessages) {
                this.nameTop = AndroidUtilities.dp(19.0f);
                charSequence = null;
            }
        } else {
            if (chat4 != null) {
                if (ChatObject.isChannel(chat4)) {
                    TLRPC.Chat chat5 = this.chat;
                    if (!chat5.megagroup) {
                        int i5 = chat5.participants_count;
                        charSequence = i5 != 0 ? LocaleController.formatPluralString("Subscribers", i5) : TextUtils.isEmpty(chat5.username) ? LocaleController.getString("ChannelPrivate", NUM).toLowerCase() : LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                    }
                }
                TLRPC.Chat chat6 = this.chat;
                int i6 = chat6.participants_count;
                if (i6 != 0) {
                    charSequence = LocaleController.formatPluralString("Members", i6);
                } else if (chat6.has_geo) {
                    charSequence = LocaleController.getString("MegaLocation", NUM);
                } else {
                    charSequence = TextUtils.isEmpty(chat6.username) ? LocaleController.getString("MegaPrivate", NUM).toLowerCase() : LocaleController.getString("MegaPublic", NUM).toLowerCase();
                }
            } else {
                charSequence = null;
            }
            this.nameTop = AndroidUtilities.dp(19.0f);
        }
        TextPaint textPaint4 = textPaint3;
        if (!TextUtils.isEmpty(charSequence)) {
            this.statusLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint4, (float) (paddingLeft - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint4, paddingLeft, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.nameTop = AndroidUtilities.dp(9.0f);
            this.nameLockTop -= AndroidUtilities.dp(10.0f);
        } else {
            this.statusLayout = null;
        }
        if (LocaleController.isRTL) {
            i2 = (getMeasuredWidth() - AndroidUtilities.dp(57.0f)) - getPaddingRight();
        } else {
            i2 = AndroidUtilities.dp(11.0f) + getPaddingLeft();
        }
        this.avatarImage.setImageCoords(i2, AndroidUtilities.dp(7.0f), AndroidUtilities.dp(46.0f), AndroidUtilities.dp(46.0f));
        if (LocaleController.isRTL) {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineLeft(0) == 0.0f) {
                double ceil = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i7 = this.nameWidth;
                if (ceil < ((double) i7)) {
                    double d = (double) this.nameLeft;
                    double d2 = (double) i7;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    this.nameLeft = (int) (d + (d2 - ceil));
                }
            }
            StaticLayout staticLayout = this.statusLayout;
            if (staticLayout != null && staticLayout.getLineCount() > 0 && this.statusLayout.getLineLeft(0) == 0.0f) {
                double ceil2 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                double d3 = (double) paddingLeft;
                if (ceil2 < d3) {
                    double d4 = (double) this.statusLeft;
                    Double.isNaN(d3);
                    Double.isNaN(d4);
                    this.statusLeft = (int) (d4 + (d3 - ceil2));
                }
            }
        } else {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineRight(0) == ((float) this.nameWidth)) {
                double ceil3 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i8 = this.nameWidth;
                if (ceil3 < ((double) i8)) {
                    double d5 = (double) this.nameLeft;
                    double d6 = (double) i8;
                    Double.isNaN(d6);
                    Double.isNaN(d5);
                    this.nameLeft = (int) (d5 - (d6 - ceil3));
                }
            }
            StaticLayout staticLayout2 = this.statusLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 0 && this.statusLayout.getLineRight(0) == ((float) paddingLeft)) {
                double ceil4 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                double d7 = (double) paddingLeft;
                if (ceil4 < d7) {
                    double d8 = (double) this.statusLeft;
                    Double.isNaN(d7);
                    Double.isNaN(d8);
                    this.statusLeft = (int) (d8 - (d7 - ceil4));
                }
            }
        }
        this.nameLeft += getPaddingLeft();
        this.statusLeft += getPaddingLeft();
        this.nameLockLeft += getPaddingLeft();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0092, code lost:
        r0 = r12.lastAvatar;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r13) {
        /*
            r12 = this;
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            r1 = 0
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x003e
            org.telegram.ui.Components.AvatarDrawable r4 = r12.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC.User) r0)
            boolean r0 = r12.savedMessages
            if (r0 == 0) goto L_0x0022
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r0.setAvatarType(r2)
            org.telegram.messenger.ImageReceiver r4 = r12.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r12.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x0074
        L_0x0022:
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            if (r0 == 0) goto L_0x002a
            org.telegram.tgnet.TLRPC$FileLocation r1 = r0.photo_small
        L_0x002a:
            org.telegram.messenger.ImageReceiver r4 = r12.avatarImage
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUser(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r7 = r12.avatarDrawable
            r8 = 0
            org.telegram.tgnet.TLRPC$User r9 = r12.user
            r10 = 0
            java.lang.String r6 = "50_50"
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x0074
        L_0x003e:
            org.telegram.tgnet.TLRPC$Chat r0 = r12.chat
            if (r0 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            if (r0 == 0) goto L_0x0048
            org.telegram.tgnet.TLRPC$FileLocation r1 = r0.photo_small
        L_0x0048:
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            org.telegram.tgnet.TLRPC$Chat r4 = r12.chat
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r4)
            org.telegram.messenger.ImageReceiver r5 = r12.avatarImage
            org.telegram.tgnet.TLRPC$Chat r0 = r12.chat
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForChat(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r8 = r12.avatarDrawable
            r9 = 0
            org.telegram.tgnet.TLRPC$Chat r10 = r12.chat
            r11 = 0
            java.lang.String r7 = "50_50"
            r5.setImage(r6, r7, r8, r9, r10, r11)
            goto L_0x0074
        L_0x0063:
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r0.setInfo(r3, r1, r1)
            org.telegram.messenger.ImageReceiver r4 = r12.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r12.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
        L_0x0074:
            if (r13 == 0) goto L_0x0121
            r0 = r13 & 2
            if (r0 == 0) goto L_0x007e
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            if (r0 != 0) goto L_0x0086
        L_0x007e:
            r0 = r13 & 8
            if (r0 == 0) goto L_0x00a8
            org.telegram.tgnet.TLRPC$Chat r0 = r12.chat
            if (r0 == 0) goto L_0x00a8
        L_0x0086:
            org.telegram.tgnet.TLRPC$FileLocation r0 = r12.lastAvatar
            if (r0 == 0) goto L_0x008c
            if (r1 == 0) goto L_0x00a6
        L_0x008c:
            org.telegram.tgnet.TLRPC$FileLocation r0 = r12.lastAvatar
            if (r0 != 0) goto L_0x0092
            if (r1 != 0) goto L_0x00a6
        L_0x0092:
            org.telegram.tgnet.TLRPC$FileLocation r0 = r12.lastAvatar
            if (r0 == 0) goto L_0x00a8
            if (r1 == 0) goto L_0x00a8
            long r4 = r0.volume_id
            long r6 = r1.volume_id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x00a6
            int r0 = r0.local_id
            int r4 = r1.local_id
            if (r0 == r4) goto L_0x00a8
        L_0x00a6:
            r0 = 1
            goto L_0x00a9
        L_0x00a8:
            r0 = 0
        L_0x00a9:
            if (r0 != 0) goto L_0x00c0
            r4 = r13 & 4
            if (r4 == 0) goto L_0x00c0
            org.telegram.tgnet.TLRPC$User r4 = r12.user
            if (r4 == 0) goto L_0x00c0
            org.telegram.tgnet.TLRPC$UserStatus r4 = r4.status
            if (r4 == 0) goto L_0x00ba
            int r4 = r4.expires
            goto L_0x00bb
        L_0x00ba:
            r4 = 0
        L_0x00bb:
            int r5 = r12.lastStatus
            if (r4 == r5) goto L_0x00c0
            r0 = 1
        L_0x00c0:
            if (r0 != 0) goto L_0x00ca
            r4 = r13 & 1
            if (r4 == 0) goto L_0x00ca
            org.telegram.tgnet.TLRPC$User r4 = r12.user
            if (r4 != 0) goto L_0x00d2
        L_0x00ca:
            r4 = r13 & 16
            if (r4 == 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$Chat r4 = r12.chat
            if (r4 == 0) goto L_0x00fb
        L_0x00d2:
            org.telegram.tgnet.TLRPC$User r4 = r12.user
            if (r4 == 0) goto L_0x00ee
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$User r5 = r12.user
            java.lang.String r5 = r5.first_name
            r4.append(r5)
            org.telegram.tgnet.TLRPC$User r5 = r12.user
            java.lang.String r5 = r5.last_name
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            goto L_0x00f2
        L_0x00ee:
            org.telegram.tgnet.TLRPC$Chat r4 = r12.chat
            java.lang.String r4 = r4.title
        L_0x00f2:
            java.lang.String r5 = r12.lastName
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x00fb
            r0 = 1
        L_0x00fb:
            if (r0 != 0) goto L_0x011e
            boolean r4 = r12.drawCount
            if (r4 == 0) goto L_0x011e
            r13 = r13 & 256(0x100, float:3.59E-43)
            if (r13 == 0) goto L_0x011e
            int r13 = r12.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r13 = r13.dialogs_dict
            long r4 = r12.dialog_id
            java.lang.Object r13 = r13.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r13 = (org.telegram.tgnet.TLRPC.Dialog) r13
            if (r13 == 0) goto L_0x011e
            int r13 = r13.unread_count
            int r4 = r12.lastUnreadCount
            if (r13 == r4) goto L_0x011e
            r0 = 1
        L_0x011e:
            if (r0 != 0) goto L_0x0121
            return
        L_0x0121:
            org.telegram.tgnet.TLRPC$User r13 = r12.user
            if (r13 == 0) goto L_0x014a
            org.telegram.tgnet.TLRPC$UserStatus r13 = r13.status
            if (r13 == 0) goto L_0x012e
            int r13 = r13.expires
            r12.lastStatus = r13
            goto L_0x0130
        L_0x012e:
            r12.lastStatus = r3
        L_0x0130:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            java.lang.String r0 = r0.first_name
            r13.append(r0)
            org.telegram.tgnet.TLRPC$User r0 = r12.user
            java.lang.String r0 = r0.last_name
            r13.append(r0)
            java.lang.String r13 = r13.toString()
            r12.lastName = r13
            goto L_0x0152
        L_0x014a:
            org.telegram.tgnet.TLRPC$Chat r13 = r12.chat
            if (r13 == 0) goto L_0x0152
            java.lang.String r13 = r13.title
            r12.lastName = r13
        L_0x0152:
            r12.lastAvatar = r1
            int r13 = r12.getMeasuredWidth()
            if (r13 != 0) goto L_0x0165
            int r13 = r12.getMeasuredHeight()
            if (r13 == 0) goto L_0x0161
            goto L_0x0165
        L_0x0161:
            r12.requestLayout()
            goto L_0x0168
        L_0x0165:
            r12.buildLayout()
        L_0x0168:
            r12.postInvalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ProfileSearchCell.update(int):void");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            if (this.drawNameLock) {
                BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                BaseCell.setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                BaseCell.setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                BaseCell.setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
                Theme.dialogs_botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (!LocaleController.isRTL) {
                        i = (int) (((float) this.nameLeft) + this.nameLayout.getLineRight(0) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        i = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        float lineWidth = this.nameLayout.getLineWidth(0);
                        double d = (double) (this.nameLeft + this.nameWidth);
                        double ceil = Math.ceil((double) lineWidth);
                        Double.isNaN(d);
                        double dp = (double) AndroidUtilities.dp(6.0f);
                        Double.isNaN(dp);
                        double d2 = (d - ceil) - dp;
                        double intrinsicWidth = (double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                        Double.isNaN(intrinsicWidth);
                        i = (int) (d2 - intrinsicWidth);
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, i, this.nameTop + AndroidUtilities.dp(3.0f));
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, i, this.nameTop + AndroidUtilities.dp(3.0f));
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
                int dp2 = this.countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set((float) dp2, (float) this.countTop, (float) (dp2 + this.countWidth + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                RectF rectF = this.rect;
                float f = AndroidUtilities.density;
                canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            this.avatarImage.draw(canvas);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.statusLayout != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(this.statusLayout.getText());
        }
        accessibilityNodeInfo.setText(sb.toString());
    }
}
