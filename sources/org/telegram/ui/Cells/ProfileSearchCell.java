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
import org.telegram.messenger.DialogObject;
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
import org.telegram.ui.Components.CheckBox2;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private TLRPC$Chat chat;
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
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox);
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
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(60.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.checkBox != null) {
                int dp = LocaleController.isRTL ? (i3 - i) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
                int dp2 = AndroidUtilities.dp(36.0f);
                CheckBox2 checkBox2 = this.checkBox;
                checkBox2.layout(dp, dp2, checkBox2.getMeasuredWidth() + dp, this.checkBox.getMeasuredHeight() + dp2);
            }
            if (z) {
                buildLayout();
            }
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
        String str;
        int i2;
        TLRPC$UserStatus tLRPC$UserStatus;
        String str2;
        int i3;
        String str3;
        String str4;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        TLRPC$EncryptedChat tLRPC$EncryptedChat = this.encryptedChat;
        if (tLRPC$EncryptedChat != null) {
            this.drawNameLock = true;
            this.dialog_id = DialogObject.makeEncryptedDialogId((long) tLRPC$EncryptedChat.id);
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
                this.dialog_id = -tLRPC$Chat.id;
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
                    this.dialog_id = tLRPC$User.id;
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
        CharSequence charSequence = this.currentName;
        if (charSequence == null) {
            TLRPC$Chat tLRPC$Chat2 = this.chat;
            if (tLRPC$Chat2 != null) {
                str4 = tLRPC$Chat2.title;
            } else {
                TLRPC$User tLRPC$User3 = this.user;
                str4 = tLRPC$User3 != null ? UserObject.getUserName(tLRPC$User3) : "";
            }
            charSequence = str4.replace(10, ' ');
        }
        if (charSequence.length() == 0) {
            TLRPC$User tLRPC$User4 = this.user;
            if (tLRPC$User4 == null || (str3 = tLRPC$User4.phone) == null || str3.length() == 0) {
                charSequence = LocaleController.getString("HiddenName", NUM);
            } else {
                charSequence = PhoneFormat.getInstance().format("+" + this.user.phone);
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
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint2, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint2, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        TextPaint textPaint3 = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC$Chat tLRPC$Chat3 = this.chat;
        if (tLRPC$Chat3 == null || this.subLabel != null) {
            str = this.subLabel;
            if (str == null) {
                TLRPC$User tLRPC$User5 = this.user;
                if (tLRPC$User5 == null) {
                    str = null;
                } else if (MessagesController.isSupportUser(tLRPC$User5)) {
                    str = LocaleController.getString("SupportStatus", NUM);
                } else {
                    TLRPC$User tLRPC$User6 = this.user;
                    if (tLRPC$User6.bot) {
                        str = LocaleController.getString("Bot", NUM);
                    } else {
                        long j = tLRPC$User6.id;
                        if (j == 333000 || j == 777000) {
                            str = LocaleController.getString("ServiceNotifications", NUM);
                        } else {
                            str = LocaleController.formatUserStatus(this.currentAccount, tLRPC$User6);
                            TLRPC$User tLRPC$User7 = this.user;
                            if (tLRPC$User7 != null && (tLRPC$User7.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((tLRPC$UserStatus = this.user.status) != null && tLRPC$UserStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                                textPaint3 = Theme.dialogs_onlinePaint;
                                str = LocaleController.getString("Online", NUM);
                            }
                        }
                    }
                }
            }
            if (this.savedMessages || UserObject.isReplyUser(this.user)) {
                this.nameTop = AndroidUtilities.dp(20.0f);
                str = null;
            }
        } else {
            if (ChatObject.isChannel(tLRPC$Chat3)) {
                TLRPC$Chat tLRPC$Chat4 = this.chat;
                if (!tLRPC$Chat4.megagroup) {
                    int i4 = tLRPC$Chat4.participants_count;
                    if (i4 != 0) {
                        str2 = LocaleController.formatPluralString("Subscribers", i4);
                    } else if (TextUtils.isEmpty(tLRPC$Chat4.username)) {
                        str2 = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                    } else {
                        str2 = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                    }
                    str = str2;
                    this.nameTop = AndroidUtilities.dp(19.0f);
                }
            }
            TLRPC$Chat tLRPC$Chat5 = this.chat;
            int i5 = tLRPC$Chat5.participants_count;
            if (i5 != 0) {
                str2 = LocaleController.formatPluralString("Members", i5);
            } else if (tLRPC$Chat5.has_geo) {
                str2 = LocaleController.getString("MegaLocation", NUM);
            } else if (TextUtils.isEmpty(tLRPC$Chat5.username)) {
                str2 = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
            } else {
                str2 = LocaleController.getString("MegaPublic", NUM).toLowerCase();
            }
            str = str2;
            this.nameTop = AndroidUtilities.dp(19.0f);
        }
        TextPaint textPaint4 = textPaint3;
        if (!TextUtils.isEmpty(str)) {
            this.statusLayout = new StaticLayout(TextUtils.ellipsize(str, textPaint4, (float) (paddingLeft - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint4, paddingLeft, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.nameTop = AndroidUtilities.dp(9.0f);
            this.nameLockTop -= AndroidUtilities.dp(10.0f);
        } else {
            this.nameTop = AndroidUtilities.dp(20.0f);
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
                int i6 = this.nameWidth;
                if (ceil < ((double) i6)) {
                    double d = (double) this.nameLeft;
                    double d2 = (double) i6;
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
                int i7 = this.nameWidth;
                if (ceil3 < ((double) i7)) {
                    double d5 = (double) this.nameLeft;
                    double d6 = (double) i7;
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

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b2, code lost:
        r1 = r13.lastAvatar;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r14) {
        /*
            r13 = this;
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0060
            org.telegram.ui.Components.AvatarDrawable r4 = r13.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r0 == 0) goto L_0x0029
            org.telegram.ui.Components.AvatarDrawable r0 = r13.avatarDrawable
            r1 = 12
            r0.setAvatarType(r1)
            org.telegram.messenger.ImageReceiver r4 = r13.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r13.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x009d
        L_0x0029:
            boolean r0 = r13.savedMessages
            if (r0 == 0) goto L_0x003f
            org.telegram.ui.Components.AvatarDrawable r0 = r13.avatarDrawable
            r0.setAvatarType(r3)
            org.telegram.messenger.ImageReceiver r4 = r13.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r13.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x009d
        L_0x003f:
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            org.telegram.tgnet.TLRPC$UserProfilePhoto r4 = r0.photo
            if (r4 == 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$FileLocation r2 = r4.photo_small
        L_0x0047:
            org.telegram.messenger.ImageReceiver r4 = r13.avatarImage
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r3)
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.ui.Components.AvatarDrawable r9 = r13.avatarDrawable
            org.telegram.tgnet.TLRPC$User r10 = r13.user
            r11 = 0
            java.lang.String r6 = "50_50"
            java.lang.String r8 = "50_50"
            r4.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10, (int) r11)
            goto L_0x009d
        L_0x0060:
            org.telegram.tgnet.TLRPC$Chat r0 = r13.chat
            if (r0 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r0.photo
            if (r4 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$FileLocation r2 = r4.photo_small
        L_0x006a:
            org.telegram.ui.Components.AvatarDrawable r4 = r13.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            org.telegram.messenger.ImageReceiver r5 = r13.avatarImage
            org.telegram.tgnet.TLRPC$Chat r0 = r13.chat
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r13.chat
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.ui.Components.AvatarDrawable r10 = r13.avatarDrawable
            org.telegram.tgnet.TLRPC$Chat r11 = r13.chat
            r12 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r9 = "50_50"
            r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11, (int) r12)
            goto L_0x009d
        L_0x008a:
            org.telegram.ui.Components.AvatarDrawable r0 = r13.avatarDrawable
            r4 = 0
            r0.setInfo(r4, r2, r2)
            org.telegram.messenger.ImageReceiver r6 = r13.avatarImage
            r7 = 0
            r8 = 0
            org.telegram.ui.Components.AvatarDrawable r9 = r13.avatarDrawable
            r10 = 0
            r11 = 0
            r12 = 0
            r6.setImage(r7, r8, r9, r10, r11, r12)
        L_0x009d:
            r0 = 0
            if (r14 == 0) goto L_0x014c
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r1 = r1 & r14
            if (r1 == 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$User r1 = r13.user
            if (r1 != 0) goto L_0x00b2
        L_0x00a9:
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r1 = r1 & r14
            if (r1 == 0) goto L_0x00ce
            org.telegram.tgnet.TLRPC$Chat r1 = r13.chat
            if (r1 == 0) goto L_0x00ce
        L_0x00b2:
            org.telegram.tgnet.TLRPC$FileLocation r1 = r13.lastAvatar
            if (r1 == 0) goto L_0x00b8
            if (r2 == 0) goto L_0x00cc
        L_0x00b8:
            if (r1 != 0) goto L_0x00bc
            if (r2 != 0) goto L_0x00cc
        L_0x00bc:
            if (r1 == 0) goto L_0x00ce
            long r4 = r1.volume_id
            long r6 = r2.volume_id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x00cc
            int r1 = r1.local_id
            int r4 = r2.local_id
            if (r1 == r4) goto L_0x00ce
        L_0x00cc:
            r1 = 1
            goto L_0x00cf
        L_0x00ce:
            r1 = 0
        L_0x00cf:
            if (r1 != 0) goto L_0x00e7
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r4 = r4 & r14
            if (r4 == 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$User r4 = r13.user
            if (r4 == 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$UserStatus r4 = r4.status
            if (r4 == 0) goto L_0x00e1
            int r4 = r4.expires
            goto L_0x00e2
        L_0x00e1:
            r4 = 0
        L_0x00e2:
            int r5 = r13.lastStatus
            if (r4 == r5) goto L_0x00e7
            r1 = 1
        L_0x00e7:
            if (r1 != 0) goto L_0x00f2
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r4 = r4 & r14
            if (r4 == 0) goto L_0x00f2
            org.telegram.tgnet.TLRPC$User r4 = r13.user
            if (r4 != 0) goto L_0x00fb
        L_0x00f2:
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r4 = r4 & r14
            if (r4 == 0) goto L_0x0124
            org.telegram.tgnet.TLRPC$Chat r4 = r13.chat
            if (r4 == 0) goto L_0x0124
        L_0x00fb:
            org.telegram.tgnet.TLRPC$User r4 = r13.user
            if (r4 == 0) goto L_0x0117
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$User r5 = r13.user
            java.lang.String r5 = r5.first_name
            r4.append(r5)
            org.telegram.tgnet.TLRPC$User r5 = r13.user
            java.lang.String r5 = r5.last_name
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            goto L_0x011b
        L_0x0117:
            org.telegram.tgnet.TLRPC$Chat r4 = r13.chat
            java.lang.String r4 = r4.title
        L_0x011b:
            java.lang.String r5 = r13.lastName
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0124
            r1 = 1
        L_0x0124:
            if (r1 != 0) goto L_0x0148
            boolean r4 = r13.drawCount
            if (r4 == 0) goto L_0x0148
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE
            r14 = r14 & r4
            if (r14 == 0) goto L_0x0148
            int r14 = r13.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r14 = r14.dialogs_dict
            long r4 = r13.dialog_id
            java.lang.Object r14 = r14.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r14 = (org.telegram.tgnet.TLRPC$Dialog) r14
            if (r14 == 0) goto L_0x0148
            int r14 = r14.unread_count
            int r4 = r13.lastUnreadCount
            if (r14 == r4) goto L_0x0148
            goto L_0x0149
        L_0x0148:
            r3 = r1
        L_0x0149:
            if (r3 != 0) goto L_0x014c
            return
        L_0x014c:
            org.telegram.tgnet.TLRPC$User r14 = r13.user
            if (r14 == 0) goto L_0x0175
            org.telegram.tgnet.TLRPC$UserStatus r14 = r14.status
            if (r14 == 0) goto L_0x0159
            int r14 = r14.expires
            r13.lastStatus = r14
            goto L_0x015b
        L_0x0159:
            r13.lastStatus = r0
        L_0x015b:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            java.lang.String r0 = r0.first_name
            r14.append(r0)
            org.telegram.tgnet.TLRPC$User r0 = r13.user
            java.lang.String r0 = r0.last_name
            r14.append(r0)
            java.lang.String r14 = r14.toString()
            r13.lastName = r14
            goto L_0x017d
        L_0x0175:
            org.telegram.tgnet.TLRPC$Chat r14 = r13.chat
            if (r14 == 0) goto L_0x017d
            java.lang.String r14 = r14.title
            r13.lastName = r14
        L_0x017d:
            r13.lastAvatar = r2
            int r14 = r13.getMeasuredWidth()
            if (r14 != 0) goto L_0x0190
            int r14 = r13.getMeasuredHeight()
            if (r14 == 0) goto L_0x018c
            goto L_0x0190
        L_0x018c:
            r13.requestLayout()
            goto L_0x0193
        L_0x0190:
            r13.buildLayout()
        L_0x0193:
            r13.postInvalidate()
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

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        }
    }
}
