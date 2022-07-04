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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
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
        simpleTextView.setTextColor(Theme.getColor("chats_menuItemText"));
        this.textView.setTextSize(15);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setMaxLines(1);
        this.textView.setGravity(19);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 19, 72.0f, 0.0f, 60.0f, 0.0f));
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            int i = this.accountNumber;
            if (account == i) {
                setAccount(i);
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            this.textView.invalidate();
        }
    }

    public void setAccount(int account) {
        this.accountNumber = account;
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        if (user != null) {
            this.avatarDrawable.setInfo(user);
            CharSequence text = ContactsController.formatName(user.first_name, user.last_name);
            int i = 0;
            try {
                text = Emoji.replaceEmoji(text, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } catch (Exception e) {
            }
            this.textView.setText(text);
            if (MessagesController.getInstance(account).isPremiumUser(user)) {
                this.textView.setDrawablePadding(AndroidUtilities.dp(6.0f));
                this.textView.setRightDrawable(PremiumGradient.getInstance().premiumStarDrawableMini);
            } else {
                this.textView.setRightDrawable((Drawable) null);
            }
            this.imageView.getImageReceiver().setCurrentAccount(account);
            this.imageView.setForUserOrChat(user, this.avatarDrawable);
            GroupCreateCheckBox groupCreateCheckBox = this.checkBox;
            if (account != UserConfig.selectedAccount) {
                i = 4;
            }
            groupCreateCheckBox.setVisibility(i);
        }
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int counter;
        if (UserConfig.getActivatedAccountsCount() > 1 && NotificationsController.getInstance(this.accountNumber).showBadgeNumber && (counter = MessagesStorage.getInstance(this.accountNumber).getMainUnreadCount()) > 0) {
            String text = String.format("%d", new Object[]{Integer.valueOf(counter)});
            int countTop = AndroidUtilities.dp(12.5f);
            int textWidth = (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(text));
            int countWidth = Math.max(AndroidUtilities.dp(10.0f), textWidth);
            int x = ((getMeasuredWidth() - countWidth) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
            this.rect.set((float) x, (float) countTop, (float) (x + countWidth + AndroidUtilities.dp(14.0f)), (float) (AndroidUtilities.dp(23.0f) + countTop));
            canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.dialogs_countPaint);
            canvas.drawText(text, this.rect.left + ((this.rect.width() - ((float) textWidth)) / 2.0f), (float) (AndroidUtilities.dp(16.0f) + countTop), Theme.dialogs_countTextPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(16);
    }
}
