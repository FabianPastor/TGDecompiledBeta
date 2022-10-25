package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
/* loaded from: classes3.dex */
public class DrawerUserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private GroupCreateCheckBox checkBox;
    private BackupImageView imageView;
    private RectF rect;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable status;
    private SimpleTextView textView;

    public DrawerUserCell(Context context) {
        super(context);
        this.rect = new RectF();
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, 51, 14.0f, 6.0f, 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        this.textView.setTextSize(15);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setMaxLines(1);
        this.textView.setGravity(19);
        this.textView.setEllipsizeByGradient(24);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 19, 72.0f, 0.0f, 14.0f, 0.0f));
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.textView, AndroidUtilities.dp(20.0f));
        this.status = swapAnimatedEmojiDrawable;
        this.textView.setRightDrawable(swapAnimatedEmojiDrawable);
        GroupCreateCheckBox groupCreateCheckBox = new GroupCreateCheckBox(context);
        this.checkBox = groupCreateCheckBox;
        groupCreateCheckBox.setChecked(true, false);
        this.checkBox.setCheckScale(0.9f);
        this.checkBox.setInnerRadDiff(AndroidUtilities.dp(1.5f));
        this.checkBox.setColorKeysOverrides("chats_unreadCounterText", "chats_unreadCounter", "chats_menuBackground");
        addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 51, 37.0f, 27.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        if (this.textView.getRightDrawable() instanceof AnimatedEmojiDrawable.WrapSizeDrawable) {
            Drawable drawable = ((AnimatedEmojiDrawable.WrapSizeDrawable) this.textView.getRightDrawable()).getDrawable();
            if (!(drawable instanceof AnimatedEmojiDrawable)) {
                return;
            }
            ((AnimatedEmojiDrawable) drawable).removeView(this.textView);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            int i3 = this.accountNumber;
            if (i2 != i3) {
                return;
            }
            setAccount(i3);
        } else if (i == NotificationCenter.emojiLoaded) {
            this.textView.invalidate();
        } else if (i != NotificationCenter.updateInterfaces || (((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_EMOJI_STATUS) <= 0) {
        } else {
            setAccount(this.accountNumber);
        }
    }

    public void setAccount(int i) {
        this.accountNumber = i;
        TLRPC$User currentUser = UserConfig.getInstance(i).getCurrentUser();
        if (currentUser == null) {
            return;
        }
        this.avatarDrawable.setInfo(currentUser);
        CharSequence formatName = ContactsController.formatName(currentUser.first_name, currentUser.last_name);
        int i2 = 0;
        try {
            formatName = Emoji.replaceEmoji(formatName, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        } catch (Exception unused) {
        }
        this.textView.setText(formatName);
        TLRPC$EmojiStatus tLRPC$EmojiStatus = currentUser.emoji_status;
        if ((tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) && ((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until > ((int) (System.currentTimeMillis() / 1000))) {
            this.textView.setDrawablePadding(AndroidUtilities.dp(4.0f));
            this.status.set(((TLRPC$TL_emojiStatusUntil) currentUser.emoji_status).document_id, true);
            this.textView.setRightDrawableOutside(true);
        } else if (currentUser.emoji_status instanceof TLRPC$TL_emojiStatus) {
            this.textView.setDrawablePadding(AndroidUtilities.dp(4.0f));
            this.status.set(((TLRPC$TL_emojiStatus) currentUser.emoji_status).document_id, true);
            this.textView.setRightDrawableOutside(true);
        } else if (MessagesController.getInstance(i).isPremiumUser(currentUser)) {
            this.textView.setDrawablePadding(AndroidUtilities.dp(6.0f));
            this.status.set(PremiumGradient.getInstance().premiumStarDrawableMini, true);
            this.textView.setRightDrawableOutside(true);
        } else {
            this.status.set((Drawable) null, true);
            this.textView.setRightDrawableOutside(false);
        }
        this.status.setColor(Integer.valueOf(Theme.getColor("chats_verifiedBackground")));
        this.imageView.getImageReceiver().setCurrentAccount(i);
        this.imageView.setForUserOrChat(currentUser, this.avatarDrawable);
        GroupCreateCheckBox groupCreateCheckBox = this.checkBox;
        if (i != UserConfig.selectedAccount) {
            i2 = 4;
        }
        groupCreateCheckBox.setVisibility(i2);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (UserConfig.getActivatedAccountsCount() <= 1 || !NotificationsController.getInstance(this.accountNumber).showBadgeNumber) {
            this.textView.setRightPadding(0);
            return;
        }
        int mainUnreadCount = MessagesStorage.getInstance(this.accountNumber).getMainUnreadCount();
        if (mainUnreadCount <= 0) {
            this.textView.setRightPadding(0);
            return;
        }
        String format = String.format("%d", Integer.valueOf(mainUnreadCount));
        int dp = AndroidUtilities.dp(12.5f);
        int ceil = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(format));
        int max = Math.max(AndroidUtilities.dp(10.0f), ceil);
        int measuredWidth = ((getMeasuredWidth() - max) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
        this.rect.set(measuredWidth, dp, measuredWidth + max + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(23.0f) + dp);
        RectF rectF = this.rect;
        float f = AndroidUtilities.density;
        canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, Theme.dialogs_countPaint);
        RectF rectF2 = this.rect;
        canvas.drawText(format, rectF2.left + ((rectF2.width() - ceil) / 2.0f), dp + AndroidUtilities.dp(16.0f), Theme.dialogs_countTextPaint);
        this.textView.setRightPadding(max + AndroidUtilities.dp(26.0f));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(16);
    }
}
