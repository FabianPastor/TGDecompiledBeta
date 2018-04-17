package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogMeUrlCell extends BaseCell {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private int currentAccount = UserConfig.selectedAccount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawVerified;
    private boolean isSelected;
    private StaticLayout messageLayout;
    private int messageLeft;
    private int messageTop = AndroidUtilities.dp(40.0f);
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(RecentMeUrl url) {
        this.recentMeUrl = url;
        requestLayout();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(72.0f) + this.useSeparator);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            buildLayout();
        }
    }

    public void buildLayout() {
        TLObject image;
        StringBuilder stringBuilder;
        CharSequence messageString;
        int nameWidth;
        int w;
        int nameWidth2;
        StaticLayout staticLayout;
        StaticLayout staticLayout2;
        int nameWidth3;
        float f;
        CharSequence messageString2;
        Throwable e;
        int messageWidth;
        StaticLayout staticLayout3;
        TextPaint textPaint;
        float left;
        double widthpx;
        String nameString = TtmlNode.ANONYMOUS_REGION_ID;
        TextPaint currentNamePaint = Theme.dialogs_namePaint;
        TextPaint currentMessagePaint = Theme.dialogs_messagePaint;
        this.drawNameGroup = false;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameBot = false;
        this.drawVerified = false;
        String image2 = null;
        if (this.recentMeUrl instanceof TL_recentMeUrlChat) {
            Chat chat = MessagesController.getInstance(r1.currentAccount).getChat(Integer.valueOf(r1.recentMeUrl.chat_id));
            if (chat.id >= 0) {
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    r1.drawNameGroup = true;
                    r1.nameLockTop = AndroidUtilities.dp(17.5f);
                    r1.drawVerified = chat.verified;
                    if (LocaleController.isRTL) {
                        r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                    } else {
                        r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                        r1.nameLeft = AndroidUtilities.dp(14.0f);
                    }
                    nameString = chat.title;
                    if (chat.photo != null) {
                        image2 = chat.photo.photo_small;
                    }
                    r1.avatarDrawable.setInfo(chat);
                }
            }
            r1.drawNameBroadcast = true;
            r1.nameLockTop = AndroidUtilities.dp(16.5f);
            r1.drawVerified = chat.verified;
            if (LocaleController.isRTL) {
                if (r1.drawNameGroup) {
                }
                r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                if (r1.drawNameGroup) {
                }
                r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
            }
            nameString = chat.title;
            if (chat.photo != null) {
                image2 = chat.photo.photo_small;
            }
            r1.avatarDrawable.setInfo(chat);
        } else if (r1.recentMeUrl instanceof TL_recentMeUrlUser) {
            User user = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(r1.recentMeUrl.user_id));
            if (LocaleController.isRTL) {
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (user != null) {
                if (user.bot) {
                    r1.drawNameBot = true;
                    r1.nameLockTop = AndroidUtilities.dp(16.5f);
                    if (LocaleController.isRTL) {
                        r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - Theme.dialogs_botDrawable.getIntrinsicWidth();
                        r1.nameLeft = AndroidUtilities.dp(14.0f);
                    } else {
                        r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + Theme.dialogs_botDrawable.getIntrinsicWidth();
                    }
                }
                r1.drawVerified = user.verified;
            }
            nameString = UserObject.getUserName(user);
            if (user.photo != null) {
                image2 = user.photo.photo_small;
            }
            r1.avatarDrawable.setInfo(user);
        } else if (r1.recentMeUrl instanceof TL_recentMeUrlStickerSet) {
            int messageWidth2;
            if (LocaleController.isRTL) {
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            nameString = r1.recentMeUrl.set.set.title;
            image = r1.recentMeUrl.set.cover;
            r1.avatarDrawable.setInfo(5, r1.recentMeUrl.set.set.title, null, false);
            image = image;
            stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(r1.currentAccount).linkPrefix);
            stringBuilder.append("/");
            stringBuilder.append(r1.recentMeUrl.url);
            messageString = stringBuilder.toString();
            r1.avatarImage.setImage(image, "50_50", r1.avatarDrawable, null, 0);
            if (TextUtils.isEmpty(nameString)) {
                nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
            }
            if (LocaleController.isRTL) {
                nameWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f);
            } else {
                nameWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (r1.drawNameLock) {
                nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else if (r1.drawNameGroup) {
                nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
            } else if (r1.drawNameBroadcast) {
                nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
            } else if (r1.drawNameBot) {
                nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
            }
            if (r1.drawVerified) {
                w = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                nameWidth -= w;
                if (LocaleController.isRTL) {
                    r1.nameLeft += w;
                }
            }
            nameWidth2 = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
            staticLayout = staticLayout;
            staticLayout2 = staticLayout;
            nameWidth3 = nameWidth2;
            f = 12.0f;
            messageString2 = messageString;
            try {
                staticLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace('\n', ' '), currentNamePaint, (float) (nameWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, null);
                r1.nameLayout = staticLayout2;
            } catch (Throwable e2) {
                e = e2;
                FileLog.m3e(e);
                nameWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
                if (LocaleController.isRTL) {
                    r1.messageLeft = AndroidUtilities.dp(16.0f);
                    w = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 61.0f : 65.0f);
                } else {
                    r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    w = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
                }
                r1.avatarImage.setImageCoords(w, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                messageWidth = Math.max(AndroidUtilities.dp(f), nameWidth);
                staticLayout = staticLayout;
                staticLayout3 = staticLayout;
                messageWidth2 = messageWidth;
                staticLayout = new StaticLayout(TextUtils.ellipsize(messageString2, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, 0);
                r1.messageLayout = staticLayout3;
                if (LocaleController.isRTL) {
                    if (r1.nameLayout != null) {
                    }
                    textPaint = currentMessagePaint;
                    messageWidth = nameWidth3;
                    if (r1.messageLayout != null) {
                    }
                }
                textPaint = currentMessagePaint;
                messageWidth = nameWidth3;
                left = r1.nameLayout.getLineRight(0);
                if (left == ((float) messageWidth)) {
                    widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (widthpx < ((double) messageWidth)) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
                }
                if (r1.messageLayout != null) {
                    return;
                }
                return;
            }
            nameWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                if (AndroidUtilities.isTablet()) {
                }
                w = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
            } else {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                if (AndroidUtilities.isTablet()) {
                }
                w = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 61.0f : 65.0f);
            }
            r1.avatarImage.setImageCoords(w, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            messageWidth = Math.max(AndroidUtilities.dp(f), nameWidth);
            staticLayout = staticLayout;
            staticLayout3 = staticLayout;
            messageWidth2 = messageWidth;
            try {
                staticLayout = new StaticLayout(TextUtils.ellipsize(messageString2, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, 0);
                r1.messageLayout = staticLayout3;
            } catch (Throwable e22) {
                e = e22;
                FileLog.m3e(e);
                if (LocaleController.isRTL) {
                    textPaint = currentMessagePaint;
                    messageWidth = nameWidth3;
                    left = r1.nameLayout.getLineRight(0);
                    if (left == ((float) messageWidth)) {
                        widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                        if (widthpx < ((double) messageWidth)) {
                            r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                        }
                    }
                    if (r1.drawVerified) {
                        r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
                    }
                    if (r1.messageLayout != null) {
                        return;
                    }
                    return;
                }
                if (r1.nameLayout != null) {
                }
                textPaint = currentMessagePaint;
                messageWidth = nameWidth3;
                if (r1.messageLayout != null) {
                }
            }
            if (LocaleController.isRTL) {
                if (r1.nameLayout != null || r1.nameLayout.getLineCount() <= 0) {
                    textPaint = currentMessagePaint;
                    messageWidth = nameWidth3;
                } else {
                    left = r1.nameLayout.getLineLeft(0);
                    double widthpx2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (r1.drawVerified) {
                        messageWidth = nameWidth3;
                        r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) messageWidth) - widthpx2)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    } else {
                        textPaint = currentMessagePaint;
                        messageWidth = nameWidth3;
                    }
                    if (left == 0.0f && widthpx2 < ((double) messageWidth)) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) + (((double) messageWidth) - widthpx2));
                    }
                }
                if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineLeft(0) == 0.0f) {
                    widthpx = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                    if (widthpx < ((double) messageWidth2)) {
                        r1.messageLeft = (int) (((double) r1.messageLeft) + (((double) messageWidth2) - widthpx));
                        return;
                    }
                    return;
                }
                return;
            }
            textPaint = currentMessagePaint;
            messageWidth = nameWidth3;
            if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
                left = r1.nameLayout.getLineRight(0);
                if (left == ((float) messageWidth)) {
                    widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (widthpx < ((double) messageWidth)) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
                }
            }
            if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineRight(0) == ((float) messageWidth2)) {
                widthpx = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                if (widthpx < ((double) messageWidth2)) {
                    r1.messageLeft = (int) (((double) r1.messageLeft) - (((double) messageWidth2) - widthpx));
                    return;
                }
                return;
            }
            return;
        } else if (r1.recentMeUrl instanceof TL_recentMeUrlChatInvite) {
            if (LocaleController.isRTL) {
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (r1.recentMeUrl.chat_invite.chat != null) {
                TLObject image3;
                r1.avatarDrawable.setInfo(r1.recentMeUrl.chat_invite.chat);
                nameString = r1.recentMeUrl.chat_invite.chat.title;
                if (r1.recentMeUrl.chat_invite.chat.photo != null) {
                    image3 = r1.recentMeUrl.chat_invite.chat.photo.photo_small;
                }
                image = image3;
                if (r1.recentMeUrl.chat_invite.chat.id >= 0) {
                    if (!ChatObject.isChannel(r1.recentMeUrl.chat_invite.chat) || r1.recentMeUrl.chat_invite.chat.megagroup) {
                        r1.drawNameGroup = true;
                        r1.nameLockTop = AndroidUtilities.dp(17.5f);
                        r1.drawVerified = r1.recentMeUrl.chat_invite.chat.verified;
                    }
                }
                r1.drawNameBroadcast = true;
                r1.nameLockTop = AndroidUtilities.dp(16.5f);
                r1.drawVerified = r1.recentMeUrl.chat_invite.chat.verified;
            } else {
                nameString = r1.recentMeUrl.chat_invite.title;
                image = r1.recentMeUrl.chat_invite.photo.photo_small;
                r1.avatarDrawable.setInfo(5, r1.recentMeUrl.chat_invite.title, null, false);
                if (!r1.recentMeUrl.chat_invite.broadcast) {
                    if (!r1.recentMeUrl.chat_invite.channel) {
                        r1.drawNameGroup = true;
                        r1.nameLockTop = AndroidUtilities.dp(17.5f);
                    }
                }
                r1.drawNameBroadcast = true;
                r1.nameLockTop = AndroidUtilities.dp(16.5f);
            }
            image2 = image;
            if (LocaleController.isRTL) {
                r1.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                r1.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + (r1.drawNameGroup ? Theme.dialogs_groupDrawable : Theme.dialogs_broadcastDrawable).getIntrinsicWidth();
            }
        } else if (r1.recentMeUrl instanceof TL_recentMeUrlUnknown) {
            if (LocaleController.isRTL) {
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            nameString = "Url";
            image2 = null;
        }
        image = image2;
        stringBuilder = new StringBuilder();
        stringBuilder.append(MessagesController.getInstance(r1.currentAccount).linkPrefix);
        stringBuilder.append("/");
        stringBuilder.append(r1.recentMeUrl.url);
        messageString = stringBuilder.toString();
        r1.avatarImage.setImage(image, "50_50", r1.avatarDrawable, null, 0);
        if (TextUtils.isEmpty(nameString)) {
            nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        if (LocaleController.isRTL) {
            nameWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            nameWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f);
        }
        if (r1.drawNameLock) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (r1.drawNameGroup) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBroadcast) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBot) {
            nameWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        if (r1.drawVerified) {
            w = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            nameWidth -= w;
            if (LocaleController.isRTL) {
                r1.nameLeft += w;
            }
        }
        nameWidth2 = Math.max(AndroidUtilities.dp(12.0f), nameWidth);
        try {
            staticLayout = staticLayout;
            staticLayout2 = staticLayout;
            nameWidth3 = nameWidth2;
            f = 12.0f;
            messageString2 = messageString;
            staticLayout = new StaticLayout(TextUtils.ellipsize(nameString.replace('\n', ' '), currentNamePaint, (float) (nameWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), currentNamePaint, nameWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, null);
            r1.nameLayout = staticLayout2;
        } catch (Throwable e222) {
            nameWidth3 = nameWidth2;
            f = 12.0f;
            messageString2 = messageString;
            e = e222;
            FileLog.m3e(e);
            nameWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                if (AndroidUtilities.isTablet()) {
                }
                w = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
            } else {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                if (AndroidUtilities.isTablet()) {
                }
                w = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 61.0f : 65.0f);
            }
            r1.avatarImage.setImageCoords(w, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            messageWidth = Math.max(AndroidUtilities.dp(f), nameWidth);
            staticLayout = staticLayout;
            staticLayout3 = staticLayout;
            messageWidth2 = messageWidth;
            staticLayout = new StaticLayout(TextUtils.ellipsize(messageString2, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, 0);
            r1.messageLayout = staticLayout3;
            if (LocaleController.isRTL) {
                if (r1.nameLayout != null) {
                }
                textPaint = currentMessagePaint;
                messageWidth = nameWidth3;
                if (r1.messageLayout != null) {
                }
            }
            textPaint = currentMessagePaint;
            messageWidth = nameWidth3;
            left = r1.nameLayout.getLineRight(0);
            if (left == ((float) messageWidth)) {
                widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (widthpx < ((double) messageWidth)) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                }
            }
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        nameWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            r1.messageLeft = AndroidUtilities.dp(16.0f);
            if (AndroidUtilities.isTablet()) {
            }
            w = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 61.0f : 65.0f);
        } else {
            r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            if (AndroidUtilities.isTablet()) {
            }
            w = AndroidUtilities.dp(AndroidUtilities.isTablet() ? NUM : NUM);
        }
        r1.avatarImage.setImageCoords(w, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        messageWidth = Math.max(AndroidUtilities.dp(f), nameWidth);
        try {
            staticLayout = staticLayout;
            staticLayout3 = staticLayout;
            messageWidth2 = messageWidth;
            staticLayout = new StaticLayout(TextUtils.ellipsize(messageString2, currentMessagePaint, (float) (messageWidth - AndroidUtilities.dp(f)), TruncateAt.END), currentMessagePaint, messageWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, 0);
            r1.messageLayout = staticLayout3;
        } catch (Throwable e2222) {
            String str = nameString;
            messageWidth2 = messageWidth;
            e = e2222;
            FileLog.m3e(e);
            if (LocaleController.isRTL) {
                if (r1.nameLayout != null) {
                }
                textPaint = currentMessagePaint;
                messageWidth = nameWidth3;
                if (r1.messageLayout != null) {
                }
            }
            textPaint = currentMessagePaint;
            messageWidth = nameWidth3;
            left = r1.nameLayout.getLineRight(0);
            if (left == ((float) messageWidth)) {
                widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (widthpx < ((double) messageWidth)) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                }
            }
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        if (LocaleController.isRTL) {
            textPaint = currentMessagePaint;
            messageWidth = nameWidth3;
            left = r1.nameLayout.getLineRight(0);
            if (left == ((float) messageWidth)) {
                widthpx = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                if (widthpx < ((double) messageWidth)) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) - (((double) messageWidth) - widthpx));
                }
            }
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + left) + ((float) AndroidUtilities.dp(6.0f)));
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        if (r1.nameLayout != null) {
        }
        textPaint = currentMessagePaint;
        messageWidth = nameWidth3;
        if (r1.messageLayout != null) {
        }
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    protected void onDraw(Canvas canvas) {
        if (this.isSelected) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
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
            canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
            this.nameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.messageLayout != null) {
            canvas.save();
            canvas.translate((float) this.messageLeft, (float) this.messageTop);
            try {
                this.messageLayout.draw(canvas);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            canvas.restore();
        }
        if (this.drawVerified) {
            BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            Theme.dialogs_verifiedDrawable.draw(canvas);
            Theme.dialogs_verifiedCheckDrawable.draw(canvas);
        }
        if (this.useSeparator) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        this.avatarImage.draw(canvas);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
