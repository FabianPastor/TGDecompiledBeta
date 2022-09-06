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

public class DrawerUserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private GroupCreateCheckBox checkBox;
    private BackupImageView imageView;
    private RectF rect = new RectF();
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable status;
    private SimpleTextView textView;

    public DrawerUserCell(Context context) {
        super(context);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
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
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 19, 72.0f, 0.0f, 60.0f, 0.0f));
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.textView, AndroidUtilities.dp(20.0f));
        this.status = swapAnimatedEmojiDrawable;
        this.textView.setRightDrawable((Drawable) swapAnimatedEmojiDrawable);
        GroupCreateCheckBox groupCreateCheckBox = new GroupCreateCheckBox(context);
        this.checkBox = groupCreateCheckBox;
        groupCreateCheckBox.setChecked(true, false);
        this.checkBox.setCheckScale(0.9f);
        this.checkBox.setInnerRadDiff(AndroidUtilities.dp(1.5f));
        this.checkBox.setColorKeysOverrides("chats_unreadCounterText", "chats_unreadCounter", "chats_menuBackground");
        addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 51, 37.0f, 27.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        if (this.textView.getRightDrawable() instanceof AnimatedEmojiDrawable.WrapSizeDrawable) {
            Drawable drawable = ((AnimatedEmojiDrawable.WrapSizeDrawable) this.textView.getRightDrawable()).getDrawable();
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).removeView((View) this.textView);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            int i3 = this.accountNumber;
            if (i2 == i3) {
                setAccount(i3);
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            this.textView.invalidate();
        } else if (i == NotificationCenter.updateInterfaces && (objArr[0].intValue() & MessagesController.UPDATE_MASK_EMOJI_STATUS) > 0) {
            setAccount(this.accountNumber);
        }
    }

    public void setAccount(int i) {
        this.accountNumber = i;
        TLRPC$User currentUser = UserConfig.getInstance(i).getCurrentUser();
        if (currentUser != null) {
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
            } else if (currentUser.emoji_status instanceof TLRPC$TL_emojiStatus) {
                this.textView.setDrawablePadding(AndroidUtilities.dp(4.0f));
                this.status.set(((TLRPC$TL_emojiStatus) currentUser.emoji_status).document_id, true);
            } else if (MessagesController.getInstance(i).isPremiumUser(currentUser)) {
                this.textView.setDrawablePadding(AndroidUtilities.dp(6.0f));
                this.status.set(PremiumGradient.getInstance().premiumStarDrawableMini, true);
            } else {
                this.status.set((Drawable) null, true);
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
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int mainUnreadCount;
        if (UserConfig.getActivatedAccountsCount() > 1 && NotificationsController.getInstance(this.accountNumber).showBadgeNumber && (mainUnreadCount = MessagesStorage.getInstance(this.accountNumber).getMainUnreadCount()) > 0) {
            String format = String.format("%d", new Object[]{Integer.valueOf(mainUnreadCount)});
            int dp = AndroidUtilities.dp(12.5f);
            int ceil = (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format));
            int max = Math.max(AndroidUtilities.dp(10.0f), ceil);
            int measuredWidth = ((getMeasuredWidth() - max) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
            this.rect.set((float) measuredWidth, (float) dp, (float) (measuredWidth + max + AndroidUtilities.dp(14.0f)), (float) (AndroidUtilities.dp(23.0f) + dp));
            RectF rectF = this.rect;
            float f = AndroidUtilities.density;
            canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, Theme.dialogs_countPaint);
            RectF rectF2 = this.rect;
            canvas.drawText(format, rectF2.left + ((rectF2.width() - ((float) ceil)) / 2.0f), (float) (dp + AndroidUtilities.dp(16.0f)), Theme.dialogs_countTextPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(16);
    }
}
