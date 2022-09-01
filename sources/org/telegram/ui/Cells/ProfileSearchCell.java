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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.PremiumGradient;

public class ProfileSearchCell extends BaseCell implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private TLRPC$Chat chat;
    CheckBox2 checkBox;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private int currentAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameLock;
    private TLRPC$EncryptedChat encryptedChat;
    private boolean[] isOnline;
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
    private RectF rect;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean savedMessages;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    private StaticLayout statusLayout;
    private int statusLeft;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    public boolean useSeparator;
    private TLRPC$User user;

    public ProfileSearchCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public ProfileSearchCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.countTop = AndroidUtilities.dp(19.0f);
        this.rect = new RectF();
        this.resourcesProvider = resourcesProvider2;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider2);
        this.checkBox = checkBox2;
        checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox);
        this.statusDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(20.0f));
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            invalidate();
        }
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
        CharSequence charSequence;
        int i2;
        CharSequence charSequence2;
        TLRPC$UserStatus tLRPC$UserStatus;
        int i3;
        String str;
        String str2;
        this.drawNameLock = false;
        this.drawCheck = false;
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
            updateStatus(false, (TLRPC$User) null, false);
        } else {
            TLRPC$Chat tLRPC$Chat = this.chat;
            if (tLRPC$Chat != null) {
                this.dialog_id = -tLRPC$Chat.id;
                this.drawCheck = tLRPC$Chat.verified;
                if (!LocaleController.isRTL) {
                    this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                } else {
                    this.nameLeft = AndroidUtilities.dp(11.0f);
                }
                updateStatus(this.drawCheck, (TLRPC$User) null, false);
            } else {
                TLRPC$User tLRPC$User = this.user;
                if (tLRPC$User != null) {
                    this.dialog_id = tLRPC$User.id;
                    if (!LocaleController.isRTL) {
                        this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    } else {
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                    this.nameLockTop = AndroidUtilities.dp(21.0f);
                    TLRPC$User tLRPC$User2 = this.user;
                    this.drawCheck = tLRPC$User2.verified;
                    if (!tLRPC$User2.self) {
                        boolean isPremiumUser = MessagesController.getInstance(this.currentAccount).isPremiumUser(this.user);
                    }
                    updateStatus(this.drawCheck, this.user, false);
                }
            }
        }
        CharSequence charSequence3 = this.currentName;
        if (charSequence3 == null) {
            TLRPC$Chat tLRPC$Chat2 = this.chat;
            if (tLRPC$Chat2 != null) {
                str2 = tLRPC$Chat2.title;
            } else {
                TLRPC$User tLRPC$User3 = this.user;
                str2 = tLRPC$User3 != null ? UserObject.getUserName(tLRPC$User3) : "";
            }
            charSequence3 = str2.replace(10, ' ');
        }
        if (charSequence3.length() == 0) {
            TLRPC$User tLRPC$User4 = this.user;
            if (tLRPC$User4 == null || (str = tLRPC$User4.phone) == null || str.length() == 0) {
                charSequence3 = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                charSequence3 = PhoneFormat.getInstance().format("+" + this.user.phone);
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
                paddingLeft -= dp;
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
        CharSequence ellipsize = TextUtils.ellipsize(charSequence3, textPaint2, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END);
        if (ellipsize != null) {
            ellipsize = Emoji.replaceEmoji(ellipsize, textPaint2.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        }
        this.nameLayout = new StaticLayout(ellipsize, textPaint2, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        TextPaint textPaint3 = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC$Chat tLRPC$Chat3 = this.chat;
        if (tLRPC$Chat3 == null || this.subLabel != null) {
            CharSequence charSequence4 = this.subLabel;
            if (charSequence4 != null) {
                charSequence2 = charSequence4;
            } else {
                TLRPC$User tLRPC$User5 = this.user;
                if (tLRPC$User5 == null) {
                    charSequence2 = null;
                } else if (MessagesController.isSupportUser(tLRPC$User5)) {
                    charSequence2 = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                } else {
                    TLRPC$User tLRPC$User6 = this.user;
                    if (tLRPC$User6.bot) {
                        charSequence2 = LocaleController.getString("Bot", R.string.Bot);
                    } else {
                        long j = tLRPC$User6.id;
                        if (j == 333000 || j == 777000) {
                            charSequence2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                        } else {
                            if (this.isOnline == null) {
                                this.isOnline = new boolean[1];
                            }
                            boolean[] zArr = this.isOnline;
                            zArr[0] = false;
                            charSequence2 = LocaleController.formatUserStatus(this.currentAccount, tLRPC$User6, zArr);
                            if (this.isOnline[0]) {
                                textPaint3 = Theme.dialogs_onlinePaint;
                            }
                            TLRPC$User tLRPC$User7 = this.user;
                            if (tLRPC$User7 != null && (tLRPC$User7.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((tLRPC$UserStatus = this.user.status) != null && tLRPC$UserStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                                textPaint3 = Theme.dialogs_onlinePaint;
                                charSequence2 = LocaleController.getString("Online", R.string.Online);
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
            if (ChatObject.isChannel(tLRPC$Chat3)) {
                TLRPC$Chat tLRPC$Chat4 = this.chat;
                if (!tLRPC$Chat4.megagroup) {
                    int i4 = tLRPC$Chat4.participants_count;
                    if (i4 != 0) {
                        charSequence = LocaleController.formatPluralString("Subscribers", i4, new Object[0]);
                    } else if (TextUtils.isEmpty(tLRPC$Chat4.username)) {
                        charSequence = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                    } else {
                        charSequence = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                    }
                    this.nameTop = AndroidUtilities.dp(19.0f);
                }
            }
            TLRPC$Chat tLRPC$Chat5 = this.chat;
            int i5 = tLRPC$Chat5.participants_count;
            if (i5 != 0) {
                charSequence = LocaleController.formatPluralString("Members", i5, new Object[0]);
            } else if (tLRPC$Chat5.has_geo) {
                charSequence = LocaleController.getString("MegaLocation", R.string.MegaLocation);
            } else if (TextUtils.isEmpty(tLRPC$Chat5.username)) {
                charSequence = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
            } else {
                charSequence = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
            }
            this.nameTop = AndroidUtilities.dp(19.0f);
        }
        if (!TextUtils.isEmpty(charSequence)) {
            this.statusLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint3, (float) (paddingLeft - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint3, paddingLeft, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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

    public void updateStatus(boolean z, TLRPC$User tLRPC$User, boolean z2) {
        if (z) {
            this.statusDrawable.set((Drawable) new CombinedDrawable(Theme.dialogs_verifiedDrawable, Theme.dialogs_verifiedCheckDrawable, 0, 0), z2);
            this.statusDrawable.setColor((Integer) null);
            return;
        }
        if (tLRPC$User != null && !tLRPC$User.self) {
            TLRPC$EmojiStatus tLRPC$EmojiStatus = tLRPC$User.emoji_status;
            if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatus) {
                this.statusDrawable.set(((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus).document_id, z2);
                this.statusDrawable.setColor(Integer.valueOf(Theme.getColor("chats_verifiedBackground", this.resourcesProvider)));
                return;
            }
        }
        if (tLRPC$User == null || tLRPC$User.self || !MessagesController.getInstance(this.currentAccount).isPremiumUser(tLRPC$User)) {
            this.statusDrawable.set((Drawable) null, z2);
            this.statusDrawable.setColor(Integer.valueOf(Theme.getColor("chats_verifiedBackground", this.resourcesProvider)));
            return;
        }
        this.statusDrawable.set(PremiumGradient.getInstance().premiumStarDrawableMini, z2);
        this.statusDrawable.setColor(Integer.valueOf(Theme.getColor("chats_verifiedBackground", this.resourcesProvider)));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bf, code lost:
        r1 = r14.lastAvatar;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r15) {
        /*
            r14 = this;
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0068
            org.telegram.ui.Components.AvatarDrawable r4 = r14.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r0 == 0) goto L_0x0029
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r1 = 12
            r0.setAvatarType(r1)
            org.telegram.messenger.ImageReceiver r4 = r14.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r14.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x00aa
        L_0x0029:
            boolean r0 = r14.savedMessages
            if (r0 == 0) goto L_0x0040
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r0.setAvatarType(r3)
            org.telegram.messenger.ImageReceiver r4 = r14.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r14.avatarDrawable
            r8 = 0
            r9 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x00aa
        L_0x0040:
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            org.telegram.tgnet.TLRPC$User r4 = r14.user
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r4.photo
            if (r5 == 0) goto L_0x0050
            org.telegram.tgnet.TLRPC$FileLocation r2 = r5.photo_small
            android.graphics.drawable.BitmapDrawable r5 = r5.strippedBitmap
            if (r5 == 0) goto L_0x0050
            r11 = r5
            goto L_0x0051
        L_0x0050:
            r11 = r0
        L_0x0051:
            org.telegram.messenger.ImageReceiver r6 = r14.avatarImage
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUserOrChat(r4, r3)
            org.telegram.tgnet.TLRPC$User r0 = r14.user
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.tgnet.TLRPC$User r12 = r14.user
            r13 = 0
            java.lang.String r8 = "50_50"
            java.lang.String r10 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12, (int) r13)
            goto L_0x00aa
        L_0x0068:
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            if (r0 == 0) goto L_0x0097
            org.telegram.ui.Components.AvatarDrawable r4 = r14.avatarDrawable
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            if (r5 == 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$FileLocation r2 = r5.photo_small
            android.graphics.drawable.BitmapDrawable r5 = r5.strippedBitmap
            if (r5 == 0) goto L_0x007a
            r10 = r5
            goto L_0x007b
        L_0x007a:
            r10 = r4
        L_0x007b:
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            org.telegram.messenger.ImageReceiver r5 = r14.avatarImage
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r14.chat
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            org.telegram.tgnet.TLRPC$Chat r11 = r14.chat
            r12 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r9 = "50_50"
            r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (org.telegram.messenger.ImageLocation) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11, (int) r12)
            goto L_0x00aa
        L_0x0097:
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r4 = 0
            r0.setInfo(r4, r2, r2)
            org.telegram.messenger.ImageReceiver r6 = r14.avatarImage
            r7 = 0
            r8 = 0
            org.telegram.ui.Components.AvatarDrawable r9 = r14.avatarDrawable
            r10 = 0
            r11 = 0
            r12 = 0
            r6.setImage(r7, r8, r9, r10, r11, r12)
        L_0x00aa:
            r0 = 0
            if (r15 == 0) goto L_0x0169
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r1 = r1 & r15
            if (r1 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$User r1 = r14.user
            if (r1 != 0) goto L_0x00bf
        L_0x00b6:
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_AVATAR
            r1 = r1 & r15
            if (r1 == 0) goto L_0x00db
            org.telegram.tgnet.TLRPC$Chat r1 = r14.chat
            if (r1 == 0) goto L_0x00db
        L_0x00bf:
            org.telegram.tgnet.TLRPC$FileLocation r1 = r14.lastAvatar
            if (r1 == 0) goto L_0x00c5
            if (r2 == 0) goto L_0x00d9
        L_0x00c5:
            if (r1 != 0) goto L_0x00c9
            if (r2 != 0) goto L_0x00d9
        L_0x00c9:
            if (r1 == 0) goto L_0x00db
            long r4 = r1.volume_id
            long r6 = r2.volume_id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x00d9
            int r1 = r1.local_id
            int r4 = r2.local_id
            if (r1 == r4) goto L_0x00db
        L_0x00d9:
            r1 = 1
            goto L_0x00dc
        L_0x00db:
            r1 = 0
        L_0x00dc:
            if (r1 != 0) goto L_0x00f4
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r4 = r4 & r15
            if (r4 == 0) goto L_0x00f4
            org.telegram.tgnet.TLRPC$User r4 = r14.user
            if (r4 == 0) goto L_0x00f4
            org.telegram.tgnet.TLRPC$UserStatus r4 = r4.status
            if (r4 == 0) goto L_0x00ee
            int r4 = r4.expires
            goto L_0x00ef
        L_0x00ee:
            r4 = 0
        L_0x00ef:
            int r5 = r14.lastStatus
            if (r4 == r5) goto L_0x00f4
            r1 = 1
        L_0x00f4:
            if (r1 != 0) goto L_0x0104
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_EMOJI_STATUS
            r4 = r4 & r15
            if (r4 == 0) goto L_0x0104
            org.telegram.tgnet.TLRPC$User r4 = r14.user
            if (r4 == 0) goto L_0x0104
            boolean r5 = r4.verified
            r14.updateStatus(r5, r4, r3)
        L_0x0104:
            if (r1 != 0) goto L_0x010f
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r4 = r4 & r15
            if (r4 == 0) goto L_0x010f
            org.telegram.tgnet.TLRPC$User r4 = r14.user
            if (r4 != 0) goto L_0x0118
        L_0x010f:
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_CHAT_NAME
            r4 = r4 & r15
            if (r4 == 0) goto L_0x0141
            org.telegram.tgnet.TLRPC$Chat r4 = r14.chat
            if (r4 == 0) goto L_0x0141
        L_0x0118:
            org.telegram.tgnet.TLRPC$User r4 = r14.user
            if (r4 == 0) goto L_0x0134
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$User r5 = r14.user
            java.lang.String r5 = r5.first_name
            r4.append(r5)
            org.telegram.tgnet.TLRPC$User r5 = r14.user
            java.lang.String r5 = r5.last_name
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            goto L_0x0138
        L_0x0134:
            org.telegram.tgnet.TLRPC$Chat r4 = r14.chat
            java.lang.String r4 = r4.title
        L_0x0138:
            java.lang.String r5 = r14.lastName
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0141
            r1 = 1
        L_0x0141:
            if (r1 != 0) goto L_0x0165
            boolean r4 = r14.drawCount
            if (r4 == 0) goto L_0x0165
            int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE
            r15 = r15 & r4
            if (r15 == 0) goto L_0x0165
            int r15 = r14.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r15 = r15.dialogs_dict
            long r4 = r14.dialog_id
            java.lang.Object r15 = r15.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r15 = (org.telegram.tgnet.TLRPC$Dialog) r15
            if (r15 == 0) goto L_0x0165
            int r15 = r15.unread_count
            int r4 = r14.lastUnreadCount
            if (r15 == r4) goto L_0x0165
            goto L_0x0166
        L_0x0165:
            r3 = r1
        L_0x0166:
            if (r3 != 0) goto L_0x0169
            return
        L_0x0169:
            org.telegram.tgnet.TLRPC$User r15 = r14.user
            if (r15 == 0) goto L_0x0192
            org.telegram.tgnet.TLRPC$UserStatus r15 = r15.status
            if (r15 == 0) goto L_0x0176
            int r15 = r15.expires
            r14.lastStatus = r15
            goto L_0x0178
        L_0x0176:
            r14.lastStatus = r0
        L_0x0178:
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
            goto L_0x019a
        L_0x0192:
            org.telegram.tgnet.TLRPC$Chat r15 = r14.chat
            if (r15 == 0) goto L_0x019a
            java.lang.String r15 = r15.title
            r14.lastName = r15
        L_0x019a:
            r14.lastAvatar = r2
            int r15 = r14.getMeasuredWidth()
            if (r15 != 0) goto L_0x01ad
            int r15 = r14.getMeasuredHeight()
            if (r15 == 0) goto L_0x01a9
            goto L_0x01ad
        L_0x01a9:
            r14.requestLayout()
            goto L_0x01b0
        L_0x01ad:
            r14.buildLayout()
        L_0x01b0:
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
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (!LocaleController.isRTL) {
                    i = (int) (((float) this.nameLeft) + this.nameLayout.getLineRight(0) + ((float) AndroidUtilities.dp(6.0f)));
                } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                    i = (this.nameLeft - AndroidUtilities.dp(6.0f)) - this.statusDrawable.getIntrinsicWidth();
                } else {
                    float lineWidth = this.nameLayout.getLineWidth(0);
                    double d = (double) (this.nameLeft + this.nameWidth);
                    double ceil = Math.ceil((double) lineWidth);
                    Double.isNaN(d);
                    double dp = (double) AndroidUtilities.dp(6.0f);
                    Double.isNaN(dp);
                    double d2 = (d - ceil) - dp;
                    double intrinsicWidth = (double) this.statusDrawable.getIntrinsicWidth();
                    Double.isNaN(intrinsicWidth);
                    i = (int) (d2 - intrinsicWidth);
                }
                BaseCell.setDrawableBounds((Drawable) this.statusDrawable, (float) i, ((float) this.nameTop) + (((float) (this.nameLayout.getHeight() - this.statusDrawable.getIntrinsicHeight())) / 2.0f));
                this.statusDrawable.draw(canvas);
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
        if (this.drawCheck) {
            sb.append(", ");
            sb.append(LocaleController.getString("AccDescrVerified", R.string.AccDescrVerified));
            sb.append("\n");
        }
        if (this.statusLayout != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(this.statusLayout.getText());
        }
        accessibilityNodeInfo.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
            accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        }
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
