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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(RecentMeUrl recentMeUrl) {
        this.recentMeUrl = recentMeUrl;
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

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM) + this.useSeparator);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            buildLayout();
        }
    }

    public void buildLayout() {
        String str;
        TLObject tLObject;
        StringBuilder stringBuilder;
        CharSequence stringBuilder2;
        int measuredWidth;
        int dp;
        int max;
        StaticLayout staticLayout;
        int i;
        int measuredWidth2;
        float lineLeft;
        double ceil;
        double d;
        double ceil2;
        double d2;
        Throwable e;
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        TextPaint textPaint = Theme.dialogs_namePaint;
        TextPaint textPaint2 = Theme.dialogs_messagePaint;
        this.drawNameGroup = false;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameBot = false;
        this.drawVerified = false;
        String str3 = null;
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
                    str = chat.title;
                    if (chat.photo != null) {
                        str3 = chat.photo.photo_small;
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
            str = chat.title;
            if (chat.photo != null) {
                str3 = chat.photo.photo_small;
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
            str = UserObject.getUserName(user);
            if (user.photo != null) {
                str3 = user.photo.photo_small;
            }
            r1.avatarDrawable.setInfo(user);
        } else if (r1.recentMeUrl instanceof TL_recentMeUrlStickerSet) {
            if (LocaleController.isRTL) {
                r1.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            str2 = r1.recentMeUrl.set.set.title;
            TLObject tLObject2 = r1.recentMeUrl.set.cover;
            r1.avatarDrawable.setInfo(5, r1.recentMeUrl.set.set.title, null, false);
            tLObject = tLObject2;
            stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(r1.currentAccount).linkPrefix);
            stringBuilder.append("/");
            stringBuilder.append(r1.recentMeUrl.url);
            stringBuilder2 = stringBuilder.toString();
            r1.avatarImage.setImage(tLObject, "50_50", r1.avatarDrawable, null, 0);
            if (TextUtils.isEmpty(str2)) {
                str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
            }
            if (LocaleController.isRTL) {
                measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f);
            }
            if (!r1.drawNameLock) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else if (!r1.drawNameGroup) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
            } else if (!r1.drawNameBroadcast) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
            } else if (r1.drawNameBot) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
            }
            if (r1.drawVerified) {
                dp = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                measuredWidth -= dp;
                if (LocaleController.isRTL) {
                    r1.nameLeft += dp;
                }
            }
            max = Math.max(AndroidUtilities.dp(12.0f), measuredWidth);
            staticLayout = staticLayout;
            i = max;
            r1.nameLayout = new StaticLayout(TextUtils.ellipsize(str2.replace('\n', ' '), textPaint, (float) (max - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, max, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
            } else {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
            }
            r1.avatarImage.setImageCoords(measuredWidth, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            measuredWidth2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder2, textPaint2, (float) (measuredWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            double ceil3;
            if (LocaleController.isRTL) {
                if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
                    lineLeft = r1.nameLayout.getLineLeft(0);
                    ceil = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (r1.drawVerified) {
                        r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) i) - ceil)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    }
                    if (lineLeft == 0.0f) {
                        d = (double) i;
                        if (ceil < d) {
                            r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil));
                        }
                    }
                }
                if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineLeft(0) == 0.0f) {
                    ceil3 = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                    ceil = (double) measuredWidth2;
                    if (ceil3 < ceil) {
                        r1.messageLeft = (int) (((double) r1.messageLeft) + (ceil - ceil3));
                        return;
                    }
                    return;
                }
                return;
            }
            if (r1.nameLayout != null && r1.nameLayout.getLineCount() > 0) {
                lineLeft = r1.nameLayout.getLineRight(0);
                if (lineLeft == ((float) i)) {
                    ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    d2 = (double) i;
                    if (ceil2 < d2) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
                }
            }
            if (r1.messageLayout != null && r1.messageLayout.getLineCount() > 0 && r1.messageLayout.getLineRight(0) == ((float) measuredWidth2)) {
                ceil3 = Math.ceil((double) r1.messageLayout.getLineWidth(0));
                ceil = (double) measuredWidth2;
                if (ceil3 < ceil) {
                    r1.messageLeft = (int) (((double) r1.messageLeft) - (ceil - ceil3));
                    return;
                }
                return;
            }
            return;
        } else {
            if (r1.recentMeUrl instanceof TL_recentMeUrlChatInvite) {
                if (LocaleController.isRTL) {
                    r1.nameLeft = AndroidUtilities.dp(14.0f);
                } else {
                    r1.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                }
                if (r1.recentMeUrl.chat_invite.chat != null) {
                    r1.avatarDrawable.setInfo(r1.recentMeUrl.chat_invite.chat);
                    str2 = r1.recentMeUrl.chat_invite.chat.title;
                    if (r1.recentMeUrl.chat_invite.chat.photo != null) {
                        str3 = r1.recentMeUrl.chat_invite.chat.photo.photo_small;
                    }
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
                    str2 = r1.recentMeUrl.chat_invite.title;
                    FileLocation fileLocation = r1.recentMeUrl.chat_invite.photo.photo_small;
                    r1.avatarDrawable.setInfo(5, r1.recentMeUrl.chat_invite.title, null, false);
                    if (!r1.recentMeUrl.chat_invite.broadcast) {
                        if (!r1.recentMeUrl.chat_invite.channel) {
                            r1.drawNameGroup = true;
                            r1.nameLockTop = AndroidUtilities.dp(17.5f);
                            str3 = fileLocation;
                        }
                    }
                    r1.drawNameBroadcast = true;
                    r1.nameLockTop = AndroidUtilities.dp(16.5f);
                    str3 = fileLocation;
                }
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
                str2 = "Url";
            }
            tLObject = str3;
            stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(r1.currentAccount).linkPrefix);
            stringBuilder.append("/");
            stringBuilder.append(r1.recentMeUrl.url);
            stringBuilder2 = stringBuilder.toString();
            r1.avatarImage.setImage(tLObject, "50_50", r1.avatarDrawable, null, 0);
            if (TextUtils.isEmpty(str2)) {
                str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
            }
            if (LocaleController.isRTL) {
                measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f);
            }
            if (!r1.drawNameLock) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else if (!r1.drawNameGroup) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
            } else if (!r1.drawNameBroadcast) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
            } else if (r1.drawNameBot) {
                measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
            }
            if (r1.drawVerified) {
                dp = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                measuredWidth -= dp;
                if (LocaleController.isRTL) {
                    r1.nameLeft += dp;
                }
            }
            max = Math.max(AndroidUtilities.dp(12.0f), measuredWidth);
            staticLayout = staticLayout;
            i = max;
            r1.nameLayout = new StaticLayout(TextUtils.ellipsize(str2.replace('\n', ' '), textPaint, (float) (max - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, max, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                if (AndroidUtilities.isTablet()) {
                }
                measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
            } else {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                if (AndroidUtilities.isTablet()) {
                }
                measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
            }
            r1.avatarImage.setImageCoords(measuredWidth, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            measuredWidth2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder2, textPaint2, (float) (measuredWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                lineLeft = r1.nameLayout.getLineRight(0);
                if (lineLeft == ((float) i)) {
                    ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    d2 = (double) i;
                    if (ceil2 < d2) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
                }
                if (r1.messageLayout != null) {
                }
            }
            lineLeft = r1.nameLayout.getLineLeft(0);
            ceil = Math.ceil((double) r1.nameLayout.getLineWidth(0));
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) i) - ceil)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
            }
            if (lineLeft == 0.0f) {
                d = (double) i;
                if (ceil < d) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil));
                }
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        str2 = str;
        tLObject = str3;
        stringBuilder = new StringBuilder();
        stringBuilder.append(MessagesController.getInstance(r1.currentAccount).linkPrefix);
        stringBuilder.append("/");
        stringBuilder.append(r1.recentMeUrl.url);
        stringBuilder2 = stringBuilder.toString();
        r1.avatarImage.setImage(tLObject, "50_50", r1.avatarDrawable, null, 0);
        if (TextUtils.isEmpty(str2)) {
            str2 = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
        }
        if (LocaleController.isRTL) {
            measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp(14.0f);
        } else {
            measuredWidth = (getMeasuredWidth() - r1.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        }
        if (!r1.drawNameLock) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        } else if (!r1.drawNameGroup) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_groupDrawable.getIntrinsicWidth();
        } else if (!r1.drawNameBroadcast) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
        } else if (r1.drawNameBot) {
            measuredWidth -= AndroidUtilities.dp(4.0f) + Theme.dialogs_botDrawable.getIntrinsicWidth();
        }
        if (r1.drawVerified) {
            dp = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            measuredWidth -= dp;
            if (LocaleController.isRTL) {
                r1.nameLeft += dp;
            }
        }
        max = Math.max(AndroidUtilities.dp(12.0f), measuredWidth);
        try {
            staticLayout = staticLayout;
            i = max;
            try {
                r1.nameLayout = new StaticLayout(TextUtils.ellipsize(str2.replace('\n', ' '), textPaint, (float) (max - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, max, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e2) {
                e = e2;
                FileLog.m3e(e);
                measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
                if (LocaleController.isRTL) {
                    r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    if (AndroidUtilities.isTablet()) {
                    }
                    measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
                } else {
                    r1.messageLeft = AndroidUtilities.dp(16.0f);
                    if (AndroidUtilities.isTablet()) {
                    }
                    measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
                }
                r1.avatarImage.setImageCoords(measuredWidth, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
                measuredWidth2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
                r1.messageLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder2, textPaint2, (float) (measuredWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (LocaleController.isRTL) {
                    lineLeft = r1.nameLayout.getLineLeft(0);
                    ceil = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    if (r1.drawVerified) {
                        r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) i) - ceil)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    }
                    if (lineLeft == 0.0f) {
                        d = (double) i;
                        if (ceil < d) {
                            r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil));
                        }
                    }
                    if (r1.messageLayout != null) {
                        return;
                    }
                    return;
                }
                lineLeft = r1.nameLayout.getLineRight(0);
                if (lineLeft == ((float) i)) {
                    ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    d2 = (double) i;
                    if (ceil2 < d2) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
                }
                if (r1.messageLayout != null) {
                }
            }
        } catch (Exception e3) {
            e = e3;
            i = max;
            FileLog.m3e(e);
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
            if (LocaleController.isRTL) {
                r1.messageLeft = AndroidUtilities.dp(16.0f);
                if (AndroidUtilities.isTablet()) {
                }
                measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
            } else {
                r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                if (AndroidUtilities.isTablet()) {
                }
                measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
            }
            r1.avatarImage.setImageCoords(measuredWidth, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
            measuredWidth2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder2, textPaint2, (float) (measuredWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (LocaleController.isRTL) {
                lineLeft = r1.nameLayout.getLineRight(0);
                if (lineLeft == ((float) i)) {
                    ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
                    d2 = (double) i;
                    if (ceil2 < d2) {
                        r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
                    }
                }
                if (r1.drawVerified) {
                    r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
                }
                if (r1.messageLayout != null) {
                }
            }
            lineLeft = r1.nameLayout.getLineLeft(0);
            ceil = Math.ceil((double) r1.nameLayout.getLineWidth(0));
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) i) - ceil)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
            }
            if (lineLeft == 0.0f) {
                d = (double) i;
                if (ceil < d) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil));
                }
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            r1.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            if (AndroidUtilities.isTablet()) {
            }
            measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        } else {
            r1.messageLeft = AndroidUtilities.dp(16.0f);
            if (AndroidUtilities.isTablet()) {
            }
            measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        }
        r1.avatarImage.setImageCoords(measuredWidth, r1.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        measuredWidth2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
        try {
            r1.messageLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder2, textPaint2, (float) (measuredWidth2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, measuredWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e4) {
            FileLog.m3e(e4);
        }
        if (LocaleController.isRTL) {
            lineLeft = r1.nameLayout.getLineLeft(0);
            ceil = Math.ceil((double) r1.nameLayout.getLineWidth(0));
            if (r1.drawVerified) {
                r1.nameMuteLeft = (int) (((((double) r1.nameLeft) + (((double) i) - ceil)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
            }
            if (lineLeft == 0.0f) {
                d = (double) i;
                if (ceil < d) {
                    r1.nameLeft = (int) (((double) r1.nameLeft) + (d - ceil));
                }
            }
            if (r1.messageLayout != null) {
                return;
            }
            return;
        }
        lineLeft = r1.nameLayout.getLineRight(0);
        if (lineLeft == ((float) i)) {
            ceil2 = Math.ceil((double) r1.nameLayout.getLineWidth(0));
            d2 = (double) i;
            if (ceil2 < d2) {
                r1.nameLeft = (int) (((double) r1.nameLeft) - (d2 - ceil2));
            }
        }
        if (r1.drawVerified) {
            r1.nameMuteLeft = (int) ((((float) r1.nameLeft) + lineLeft) + ((float) AndroidUtilities.dp(6.0f)));
        }
        if (r1.messageLayout != null) {
        }
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
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
}
