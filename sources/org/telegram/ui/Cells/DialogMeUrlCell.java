package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.tgnet.TLRPC$TL_recentMeUrlChat;
import org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite;
import org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet;
import org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown;
import org.telegram.tgnet.TLRPC$TL_recentMeUrlUser;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogMeUrlCell extends BaseCell {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private int currentAccount = UserConfig.selectedAccount;
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
    private TLRPC$RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(TLRPC$RecentMeUrl tLRPC$RecentMeUrl) {
        this.recentMeUrl = tLRPC$RecentMeUrl;
        requestLayout();
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
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(72.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            buildLayout();
        }
    }

    public void buildLayout() {
        String str;
        int i;
        int i2;
        int i3;
        TextPaint textPaint = Theme.dialogs_namePaint[0];
        TextPaint textPaint2 = Theme.dialogs_messagePaint[0];
        this.drawNameLock = false;
        this.drawVerified = false;
        TLRPC$RecentMeUrl tLRPC$RecentMeUrl = this.recentMeUrl;
        if (tLRPC$RecentMeUrl instanceof TLRPC$TL_recentMeUrlChat) {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.recentMeUrl.chat_id));
            this.drawVerified = chat.verified;
            if (!LocaleController.isRTL) {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
            } else {
                this.nameLockLeft = getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
            str = chat.title;
            this.avatarDrawable.setInfo(chat);
            this.avatarImage.setForUserOrChat(chat, this.avatarDrawable, this.recentMeUrl);
        } else if (tLRPC$RecentMeUrl instanceof TLRPC$TL_recentMeUrlUser) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.recentMeUrl.user_id));
            if (!LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
            if (user != null) {
                if (user.bot) {
                    this.nameLockTop = AndroidUtilities.dp(16.5f);
                    if (!LocaleController.isRTL) {
                        this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
                    } else {
                        this.nameLockLeft = getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = AndroidUtilities.dp(14.0f);
                    }
                }
                this.drawVerified = user.verified;
            }
            str = UserObject.getUserName(user);
            this.avatarDrawable.setInfo(user);
            this.avatarImage.setForUserOrChat(user, this.avatarDrawable, this.recentMeUrl);
        } else if (tLRPC$RecentMeUrl instanceof TLRPC$TL_recentMeUrlStickerSet) {
            if (!LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
            str = this.recentMeUrl.set.set.title;
            this.avatarDrawable.setInfo(5, str, (String) null);
            this.avatarImage.setImage(ImageLocation.getForDocument(this.recentMeUrl.set.cover), (String) null, this.avatarDrawable, (String) null, this.recentMeUrl, 0);
        } else if (tLRPC$RecentMeUrl instanceof TLRPC$TL_recentMeUrlChatInvite) {
            if (!LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
            TLRPC$ChatInvite tLRPC$ChatInvite = this.recentMeUrl.chat_invite;
            TLRPC$Chat tLRPC$Chat = tLRPC$ChatInvite.chat;
            if (tLRPC$Chat != null) {
                this.avatarDrawable.setInfo(tLRPC$Chat);
                TLRPC$RecentMeUrl tLRPC$RecentMeUrl2 = this.recentMeUrl;
                TLRPC$Chat tLRPC$Chat2 = tLRPC$RecentMeUrl2.chat_invite.chat;
                String str2 = tLRPC$Chat2.title;
                this.drawVerified = tLRPC$Chat2.verified;
                this.avatarImage.setForUserOrChat(tLRPC$Chat2, this.avatarDrawable, tLRPC$RecentMeUrl2);
                str = str2;
            } else {
                String str3 = tLRPC$ChatInvite.title;
                this.avatarDrawable.setInfo(5, str3, (String) null);
                this.avatarImage.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(this.recentMeUrl.chat_invite.photo.sizes, 50), this.recentMeUrl.chat_invite.photo), "50_50", this.avatarDrawable, (String) null, this.recentMeUrl, 0);
                str = str3;
            }
            if (!LocaleController.isRTL) {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
            } else {
                this.nameLockLeft = getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
        } else if (tLRPC$RecentMeUrl instanceof TLRPC$TL_recentMeUrlUnknown) {
            if (!LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            } else {
                this.nameLeft = AndroidUtilities.dp(14.0f);
            }
            this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.recentMeUrl, 0);
            str = "Url";
        } else {
            this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, tLRPC$RecentMeUrl, 0);
            str = "";
        }
        String str4 = MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.recentMeUrl.url;
        if (TextUtils.isEmpty(str)) {
            str = LocaleController.getString("HiddenName", NUM);
        }
        if (!LocaleController.isRTL) {
            i2 = getMeasuredWidth() - this.nameLeft;
            i = AndroidUtilities.dp(14.0f);
        } else {
            i2 = getMeasuredWidth() - this.nameLeft;
            i = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        }
        int i4 = i2 - i;
        if (this.drawNameLock) {
            i4 -= AndroidUtilities.dp(4.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        }
        if (this.drawVerified) {
            int dp = AndroidUtilities.dp(6.0f) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            i4 -= dp;
            if (LocaleController.isRTL) {
                this.nameLeft += dp;
            }
        }
        int max = Math.max(AndroidUtilities.dp(12.0f), i4);
        try {
            this.nameLayout = new StaticLayout(TextUtils.ellipsize(str.replace(10, ' '), textPaint, (float) (max - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint, max, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (!LocaleController.isRTL) {
            this.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            i3 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        } else {
            this.messageLeft = AndroidUtilities.dp(16.0f);
            i3 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        }
        this.avatarImage.setImageCoords((float) i3, (float) this.avatarTop, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(52.0f));
        int max2 = Math.max(AndroidUtilities.dp(12.0f), measuredWidth);
        try {
            this.messageLayout = new StaticLayout(TextUtils.ellipsize(str4, textPaint2, (float) (max2 - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), textPaint2, max2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        if (LocaleController.isRTL) {
            StaticLayout staticLayout = this.nameLayout;
            if (staticLayout != null && staticLayout.getLineCount() > 0) {
                float lineLeft = this.nameLayout.getLineLeft(0);
                double ceil = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (this.drawVerified) {
                    double d = (double) this.nameLeft;
                    double d2 = (double) max;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    double d3 = d + (d2 - ceil);
                    double dp2 = (double) AndroidUtilities.dp(6.0f);
                    Double.isNaN(dp2);
                    double d4 = d3 - dp2;
                    double intrinsicWidth = (double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    Double.isNaN(intrinsicWidth);
                    this.nameMuteLeft = (int) (d4 - intrinsicWidth);
                }
                if (lineLeft == 0.0f) {
                    double d5 = (double) max;
                    if (ceil < d5) {
                        double d6 = (double) this.nameLeft;
                        Double.isNaN(d5);
                        Double.isNaN(d6);
                        this.nameLeft = (int) (d6 + (d5 - ceil));
                    }
                }
            }
            StaticLayout staticLayout2 = this.messageLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 0 && this.messageLayout.getLineLeft(0) == 0.0f) {
                double ceil2 = Math.ceil((double) this.messageLayout.getLineWidth(0));
                double d7 = (double) max2;
                if (ceil2 < d7) {
                    double d8 = (double) this.messageLeft;
                    Double.isNaN(d7);
                    Double.isNaN(d8);
                    this.messageLeft = (int) (d8 + (d7 - ceil2));
                    return;
                }
                return;
            }
            return;
        }
        StaticLayout staticLayout3 = this.nameLayout;
        if (staticLayout3 != null && staticLayout3.getLineCount() > 0) {
            float lineRight = this.nameLayout.getLineRight(0);
            if (lineRight == ((float) max)) {
                double ceil3 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                double d9 = (double) max;
                if (ceil3 < d9) {
                    double d10 = (double) this.nameLeft;
                    Double.isNaN(d9);
                    Double.isNaN(d10);
                    this.nameLeft = (int) (d10 - (d9 - ceil3));
                }
            }
            if (this.drawVerified) {
                this.nameMuteLeft = (int) (((float) this.nameLeft) + lineRight + ((float) AndroidUtilities.dp(6.0f)));
            }
        }
        StaticLayout staticLayout4 = this.messageLayout;
        if (staticLayout4 != null && staticLayout4.getLineCount() > 0 && this.messageLayout.getLineRight(0) == ((float) max2)) {
            double ceil4 = Math.ceil((double) this.messageLayout.getLineWidth(0));
            double d11 = (double) max2;
            if (ceil4 < d11) {
                double d12 = (double) this.messageLeft;
                Double.isNaN(d11);
                Double.isNaN(d12);
                this.messageLeft = (int) (d12 - (d11 - ceil4));
            }
        }
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isSelected) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
        }
        if (this.drawNameLock) {
            BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas);
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
