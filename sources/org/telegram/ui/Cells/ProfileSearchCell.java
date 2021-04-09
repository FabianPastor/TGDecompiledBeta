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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private TLRPC$Chat chat;
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
    private TLRPC$EncryptedChat encryptedChat;
    private TLRPC$FileLocation lastAvatar;
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
    private TLRPC$User user;

    public ProfileSearchCell(Context context) {
        super(context);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setData(TLObject tLObject, TLRPC$EncryptedChat tLRPC$EncryptedChat, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        this.currentName = charSequence;
        if (tLObject instanceof TLRPC$User) {
            this.user = (TLRPC$User) tLObject;
            this.chat = null;
        } else if (tLObject instanceof TLRPC$Chat) {
            this.chat = (TLRPC$Chat) tLObject;
            this.user = null;
        }
        this.encryptedChat = tLRPC$EncryptedChat;
        this.subLabel = charSequence2;
        this.drawCount = z;
        this.savedMessages = z2;
        update(0);
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

    public TLRPC$User getUser() {
        return this.user;
    }

    public TLRPC$Chat getChat() {
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
        TLRPC$UserStatus tLRPC$UserStatus;
        int i2;
        int i3;
        String str;
        String str2;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        TLRPC$EncryptedChat tLRPC$EncryptedChat = this.encryptedChat;
        if (tLRPC$EncryptedChat != null) {
            this.drawNameLock = true;
            this.dialog_id = ((long) tLRPC$EncryptedChat.id) << 32;
            if (!LocaleController.isRTL) {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(11.0f);
            }
            this.nameLockTop = AndroidUtilities.dp(22.0f);
        } else {
            TLRPC$Chat tLRPC$Chat = this.chat;
            if (tLRPC$Chat != null) {
                this.dialog_id = (long) (-tLRPC$Chat.id);
                if (SharedConfig.drawDialogIcons) {
                    if (!ChatObject.isChannel(tLRPC$Chat) || this.chat.megagroup) {
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
                TLRPC$User tLRPC$User = this.user;
                if (tLRPC$User != null) {
                    this.dialog_id = (long) tLRPC$User.id;
                    if (!LocaleController.isRTL) {
                        this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    } else {
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                    if (SharedConfig.drawDialogIcons) {
                        TLRPC$User tLRPC$User2 = this.user;
                        if (tLRPC$User2.bot && !MessagesController.isSupportUser(tLRPC$User2)) {
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
            TLRPC$Chat tLRPC$Chat2 = this.chat;
            if (tLRPC$Chat2 != null) {
                str2 = tLRPC$Chat2.title;
            } else {
                TLRPC$User tLRPC$User3 = this.user;
                str2 = tLRPC$User3 != null ? UserObject.getUserName(tLRPC$User3) : "";
            }
            charSequence2 = str2.replace(10, ' ');
        }
        if (charSequence2.length() == 0) {
            TLRPC$User tLRPC$User4 = this.user;
            if (tLRPC$User4 == null || (str = tLRPC$User4.phone) == null || str.length() == 0) {
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
            TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tLRPC$Dialog == null || (i3 = tLRPC$Dialog.unread_count) == 0) {
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
        if (this.nameWidth < 0) {
            this.nameWidth = 0;
        }
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence2, textPaint2, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint2, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        TextPaint textPaint3 = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC$Chat tLRPC$Chat3 = this.chat;
        if (tLRPC$Chat3 == null || this.subLabel != null) {
            CharSequence charSequence3 = this.subLabel;
            if (charSequence3 == null) {
                TLRPC$User tLRPC$User5 = this.user;
                if (tLRPC$User5 == null) {
                    charSequence3 = null;
                } else if (MessagesController.isSupportUser(tLRPC$User5)) {
                    charSequence3 = LocaleController.getString("SupportStatus", NUM);
                } else {
                    TLRPC$User tLRPC$User6 = this.user;
                    if (tLRPC$User6.bot) {
                        charSequence3 = LocaleController.getString("Bot", NUM);
                    } else {
                        int i4 = tLRPC$User6.id;
                        if (i4 == 333000 || i4 == 777000) {
                            charSequence3 = LocaleController.getString("ServiceNotifications", NUM);
                        } else {
                            charSequence3 = LocaleController.formatUserStatus(this.currentAccount, tLRPC$User6);
                            TLRPC$User tLRPC$User7 = this.user;
                            if (tLRPC$User7 != null && (tLRPC$User7.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((tLRPC$UserStatus = this.user.status) != null && tLRPC$UserStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                                textPaint3 = Theme.dialogs_onlinePaint;
                                charSequence3 = LocaleController.getString("Online", NUM);
                            }
                        }
                    }
                }
            }
            if (this.savedMessages || UserObject.isReplyUser(this.user)) {
                this.nameTop = AndroidUtilities.dp(20.0f);
                charSequence = null;
            }
        } else {
            if (tLRPC$Chat3 != null) {
                if (ChatObject.isChannel(tLRPC$Chat3)) {
                    TLRPC$Chat tLRPC$Chat4 = this.chat;
                    if (!tLRPC$Chat4.megagroup) {
                        int i5 = tLRPC$Chat4.participants_count;
                        charSequence = i5 != 0 ? LocaleController.formatPluralString("Subscribers", i5) : TextUtils.isEmpty(tLRPC$Chat4.username) ? LocaleController.getString("ChannelPrivate", NUM).toLowerCase() : LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                    }
                }
                TLRPC$Chat tLRPC$Chat5 = this.chat;
                int i6 = tLRPC$Chat5.participants_count;
                if (i6 != 0) {
                    charSequence = LocaleController.formatPluralString("Members", i6);
                } else if (tLRPC$Chat5.has_geo) {
                    charSequence = LocaleController.getString("MegaLocation", NUM);
                } else {
                    charSequence = TextUtils.isEmpty(tLRPC$Chat5.username) ? LocaleController.getString("MegaPrivate", NUM).toLowerCase() : LocaleController.getString("MegaPublic", NUM).toLowerCase();
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
        this.avatarImage.setImageCoords((float) i2, (float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
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

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00ad, code lost:
        r0 = r14.lastAvatar;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r15) {
        /*
            r14 = this;
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            r1 = 2
            r2 = 0
            r3 = 0
            r4 = 1
            if (r0 == 0) goto L_0x0060
            org.telegram.ui.Components.AvatarDrawable r5 = r14.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r0 == 0) goto L_0x0029
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r1 = 12
            r0.setAvatarType(r1)
            org.telegram.messenger.ImageReceiver r5 = r14.avatarImage
            r6 = 0
            r7 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r14.avatarDrawable
            r9 = 0
            r10 = 0
            r11 = 0
            r5.setImage(r6, r7, r8, r9, r10, r11)
            goto L_0x009b
        L_0x0029:
            boolean r0 = r14.savedMessages
            if (r0 == 0) goto L_0x003f
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r0.setAvatarType(r4)
            org.telegram.messenger.ImageReceiver r5 = r14.avatarImage
            r6 = 0
            r7 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r14.avatarDrawable
            r9 = 0
            r10 = 0
            r11 = 0
            r5.setImage(r6, r7, r8, r9, r10, r11)
            goto L_0x009b
        L_0x003f:
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo
            if (r5 == 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$FileLocation r2 = r5.photo_small
        L_0x0047:
            org.telegram.messenger.ImageReceiver r5 = r14.avatarImage
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.ui.Components.AvatarDrawable r10 = r14.avatarDrawable
            org.telegram.tgnet.TLRPC$User r11 = r14.user
            r12 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r9 = "50_50"
            r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11, (int) r12)
            goto L_0x009b
        L_0x0060:
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            if (r0 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            if (r5 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$FileLocation r2 = r5.photo_small
        L_0x006a:
            org.telegram.ui.Components.AvatarDrawable r5 = r14.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            org.telegram.messenger.ImageReceiver r6 = r14.avatarImage
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.ui.Components.AvatarDrawable r11 = r14.avatarDrawable
            org.telegram.tgnet.TLRPC$Chat r12 = r14.chat
            r13 = 0
            java.lang.String r8 = "50_50"
            java.lang.String r10 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12, (int) r13)
            goto L_0x009b
        L_0x008a:
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r0.setInfo(r3, r2, r2)
            org.telegram.messenger.ImageReceiver r5 = r14.avatarImage
            r6 = 0
            r7 = 0
            org.telegram.ui.Components.AvatarDrawable r8 = r14.avatarDrawable
            r9 = 0
            r10 = 0
            r11 = 0
            r5.setImage(r6, r7, r8, r9, r10, r11)
        L_0x009b:
            if (r15 == 0) goto L_0x0145
            r0 = r15 & 2
            if (r0 == 0) goto L_0x00a5
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            if (r0 != 0) goto L_0x00ad
        L_0x00a5:
            r0 = r15 & 8
            if (r0 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            if (r0 == 0) goto L_0x00cb
        L_0x00ad:
            org.telegram.tgnet.TLRPC$FileLocation r0 = r14.lastAvatar
            if (r0 == 0) goto L_0x00b3
            if (r2 == 0) goto L_0x00c9
        L_0x00b3:
            if (r0 != 0) goto L_0x00b7
            if (r2 != 0) goto L_0x00c9
        L_0x00b7:
            if (r0 == 0) goto L_0x00cb
            if (r2 == 0) goto L_0x00cb
            long r5 = r0.volume_id
            long r7 = r2.volume_id
            int r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x00c9
            int r0 = r0.local_id
            int r1 = r2.local_id
            if (r0 == r1) goto L_0x00cb
        L_0x00c9:
            r0 = 1
            goto L_0x00cc
        L_0x00cb:
            r0 = 0
        L_0x00cc:
            if (r0 != 0) goto L_0x00e3
            r1 = r15 & 4
            if (r1 == 0) goto L_0x00e3
            org.telegram.tgnet.TLRPC$User r1 = r14.user
            if (r1 == 0) goto L_0x00e3
            org.telegram.tgnet.TLRPC$UserStatus r1 = r1.status
            if (r1 == 0) goto L_0x00dd
            int r1 = r1.expires
            goto L_0x00de
        L_0x00dd:
            r1 = 0
        L_0x00de:
            int r5 = r14.lastStatus
            if (r1 == r5) goto L_0x00e3
            r0 = 1
        L_0x00e3:
            if (r0 != 0) goto L_0x00ed
            r1 = r15 & 1
            if (r1 == 0) goto L_0x00ed
            org.telegram.tgnet.TLRPC$User r1 = r14.user
            if (r1 != 0) goto L_0x00f5
        L_0x00ed:
            r1 = r15 & 16
            if (r1 == 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$Chat r1 = r14.chat
            if (r1 == 0) goto L_0x011e
        L_0x00f5:
            org.telegram.tgnet.TLRPC$User r1 = r14.user
            if (r1 == 0) goto L_0x0111
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.tgnet.TLRPC$User r5 = r14.user
            java.lang.String r5 = r5.first_name
            r1.append(r5)
            org.telegram.tgnet.TLRPC$User r5 = r14.user
            java.lang.String r5 = r5.last_name
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            goto L_0x0115
        L_0x0111:
            org.telegram.tgnet.TLRPC$Chat r1 = r14.chat
            java.lang.String r1 = r1.title
        L_0x0115:
            java.lang.String r5 = r14.lastName
            boolean r1 = r1.equals(r5)
            if (r1 != 0) goto L_0x011e
            r0 = 1
        L_0x011e:
            if (r0 != 0) goto L_0x0141
            boolean r1 = r14.drawCount
            if (r1 == 0) goto L_0x0141
            r15 = r15 & 256(0x100, float:3.59E-43)
            if (r15 == 0) goto L_0x0141
            int r15 = r14.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r15 = r15.dialogs_dict
            long r5 = r14.dialog_id
            java.lang.Object r15 = r15.get(r5)
            org.telegram.tgnet.TLRPC$Dialog r15 = (org.telegram.tgnet.TLRPC$Dialog) r15
            if (r15 == 0) goto L_0x0141
            int r15 = r15.unread_count
            int r1 = r14.lastUnreadCount
            if (r15 == r1) goto L_0x0141
            goto L_0x0142
        L_0x0141:
            r4 = r0
        L_0x0142:
            if (r4 != 0) goto L_0x0145
            return
        L_0x0145:
            org.telegram.tgnet.TLRPC$User r15 = r14.user
            if (r15 == 0) goto L_0x016e
            org.telegram.tgnet.TLRPC$UserStatus r15 = r15.status
            if (r15 == 0) goto L_0x0152
            int r15 = r15.expires
            r14.lastStatus = r15
            goto L_0x0154
        L_0x0152:
            r14.lastStatus = r3
        L_0x0154:
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            java.lang.String r0 = r0.first_name
            r15.append(r0)
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            java.lang.String r0 = r0.last_name
            r15.append(r0)
            java.lang.String r15 = r15.toString()
            r14.lastName = r15
            goto L_0x0176
        L_0x016e:
            org.telegram.tgnet.TLRPC$Chat r15 = r14.chat
            if (r15 == 0) goto L_0x0176
            java.lang.String r15 = r15.title
            r14.lastName = r15
        L_0x0176:
            r14.lastAvatar = r2
            int r15 = r14.getMeasuredWidth()
            if (r15 != 0) goto L_0x0189
            int r15 = r14.getMeasuredHeight()
            if (r15 == 0) goto L_0x0185
            goto L_0x0189
        L_0x0185:
            r14.requestLayout()
            goto L_0x018c
        L_0x0189:
            r14.buildLayout()
        L_0x018c:
            r14.postInvalidate()
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
