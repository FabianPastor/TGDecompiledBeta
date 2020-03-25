package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private StaticLayout countLayout;
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC$User currentUser;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;
    private RectF rect = new RectF();

    public HintDialogCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
    }

    public void update(int i) {
        int i2;
        if (!((i & 4) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
        if (i == 0 || (i & 256) != 0 || (i & 2048) != 0) {
            TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tLRPC$Dialog == null || (i2 = tLRPC$Dialog.unread_count) == 0) {
                if (this.countLayout != null) {
                    if (i != 0) {
                        invalidate();
                    }
                    this.lastUnreadCount = 0;
                    this.countLayout = null;
                }
            } else if (this.lastUnreadCount != i2) {
                this.lastUnreadCount = i2;
                String format = String.format("%d", new Object[]{Integer.valueOf(i2)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                this.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (i != 0) {
                    invalidate();
                }
            }
        }
    }

    public void update() {
        int i = (int) this.dialog_id;
        if (i > 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.currentUser = user;
            this.avatarDrawable.setInfo(user);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i)));
        this.currentUser = null;
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        this.dialog_id = (long) i;
        if (i > 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.currentUser = user;
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (user != null) {
                this.nameTextView.setText(UserObject.getFirstName(user));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.imageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.currentUser = null;
            this.imageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
        if (z) {
            update(0);
        } else {
            this.countLayout = null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        TLRPC$UserStatus tLRPC$UserStatus;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView) {
            if (this.countLayout != null) {
                int dp = AndroidUtilities.dp(6.0f);
                int dp2 = AndroidUtilities.dp(54.0f);
                int dp3 = dp2 - AndroidUtilities.dp(5.5f);
                this.rect.set((float) dp3, (float) dp, (float) (dp3 + this.countWidth + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + dp));
                RectF rectF = this.rect;
                float f = AndroidUtilities.density;
                canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) dp2, (float) (dp + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User != null && !tLRPC$User.bot && (((tLRPC$UserStatus = tLRPC$User.status) != null && tLRPC$UserStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)))) {
                int dp4 = AndroidUtilities.dp(53.0f);
                int dp5 = AndroidUtilities.dp(59.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("windowBackgroundWhite"));
                float f2 = (float) dp5;
                float f3 = (float) dp4;
                canvas.drawCircle(f2, f3, (float) AndroidUtilities.dp(7.0f), Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("chats_onlineCircle"));
                canvas.drawCircle(f2, f3, (float) AndroidUtilities.dp(5.0f), Theme.dialogs_onlineCirclePaint);
            }
        }
        return drawChild;
    }
}
