package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerUserCell extends FrameLayout {
    private int accountNumber;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private GroupCreateCheckBox checkBox;
    private BackupImageView imageView;
    private RectF rect = new RectF();
    private TextView textView;

    public DrawerUserCell(Context context) {
        super(context);
        this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, 51, 14.0f, 6.0f, 0.0f, 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setEllipsize(TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 72.0f, 0.0f, 60.0f, 0.0f));
        this.checkBox = new GroupCreateCheckBox(context);
        this.checkBox.setChecked(true, false);
        this.checkBox.setCheckScale(0.9f);
        this.checkBox.setInnerRadDiff(AndroidUtilities.dp(1.5f));
        this.checkBox.setColorKeysOverrides(Theme.key_chats_unreadCounterText, Theme.key_chats_unreadCounter, Theme.key_chats_menuBackground);
        addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 51, 37.0f, 27.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setAccount(int account) {
        TLObject avatar;
        this.accountNumber = account;
        User user = UserConfig.getInstance(this.accountNumber).getCurrentUser();
        this.avatarDrawable.setInfo(user);
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        if (user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0) {
            avatar = null;
        } else {
            avatar = user.photo.photo_small;
        }
        this.imageView.getImageReceiver().setCurrentAccount(account);
        this.imageView.setImage(avatar, "50_50", this.avatarDrawable);
        this.checkBox.setVisibility(account == UserConfig.selectedAccount ? 0 : 4);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    protected void onDraw(Canvas canvas) {
        if (UserConfig.getActivatedAccountsCount() > 1 && NotificationsController.getInstance(this.accountNumber).showBadgeNumber) {
            int counter = NotificationsController.getInstance(this.accountNumber).getTotalUnreadCount();
            String text = String.format("%d", new Object[]{Integer.valueOf(counter)});
            int countTop = AndroidUtilities.dp(12.5f);
            int textWidth = (int) Math.ceil((double) Theme.chat_livePaint.measureText(text));
            int countWidth = Math.max(AndroidUtilities.dp(12.0f), textWidth);
            int x = ((getMeasuredWidth() - countWidth) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
            this.rect.set((float) x, (float) countTop, (float) ((x + countWidth) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + countTop));
            canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.dialogs_countPaint);
            canvas.drawText(text, (this.rect.left + ((this.rect.width() - ((float) textWidth)) / 2.0f)) - ((float) AndroidUtilities.dp(0.5f)), (float) (AndroidUtilities.dp(16.0f) + countTop), Theme.dialogs_countTextPaint);
        }
    }
}
