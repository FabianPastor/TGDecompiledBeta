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

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setAccount(int i) {
        this.accountNumber = i;
        User currentUser = UserConfig.getInstance(this.accountNumber).getCurrentUser();
        if (currentUser != null) {
            this.avatarDrawable.setInfo(currentUser);
            this.textView.setText(ContactsController.formatName(currentUser.first_name, currentUser.last_name));
            TLObject tLObject = (currentUser.photo == null || currentUser.photo.photo_small == null || currentUser.photo.photo_small.volume_id == 0 || currentUser.photo.photo_small.local_id == 0) ? null : currentUser.photo.photo_small;
            this.imageView.getImageReceiver().setCurrentAccount(i);
            this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
            this.checkBox.setVisibility(i == UserConfig.selectedAccount ? 0 : 4);
        }
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    protected void onDraw(Canvas canvas) {
        if (UserConfig.getActivatedAccountsCount() > 1) {
            if (NotificationsController.getInstance(this.accountNumber).showBadgeNumber) {
                if (NotificationsController.getInstance(this.accountNumber).getTotalUnreadCount() > 0) {
                    String format = String.format("%d", new Object[]{Integer.valueOf(NotificationsController.getInstance(this.accountNumber).getTotalUnreadCount())});
                    int dp = AndroidUtilities.dp(12.5f);
                    int ceil = (int) Math.ceil((double) Theme.chat_livePaint.measureText(format));
                    int max = Math.max(AndroidUtilities.dp(12.0f), ceil);
                    int measuredWidth = ((getMeasuredWidth() - max) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
                    this.rect.set((float) measuredWidth, (float) dp, (float) ((measuredWidth + max) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + dp));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, Theme.dialogs_countPaint);
                    canvas.drawText(format, (this.rect.left + ((this.rect.width() - ((float) ceil)) / 2.0f)) - ((float) AndroidUtilities.dp(0.5f)), (float) (dp + AndroidUtilities.dp(16.0f)), Theme.dialogs_countTextPaint);
                }
            }
        }
    }
}
