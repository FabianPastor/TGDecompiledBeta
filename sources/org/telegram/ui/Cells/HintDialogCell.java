package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private StaticLayout countLayout;
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private User currentUser;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;
    private RectF rect = new RectF();

    public HintDialogCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
    }

    public void update(int i) {
        if (!((i & 4) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
        if (i == 0 || (i & 256) != 0 || (i & 2048) != 0) {
            Dialog dialog = (Dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog != null) {
                int i2 = dialog.unread_count;
                if (i2 != 0) {
                    if (this.lastUnreadCount != i2) {
                        this.lastUnreadCount = i2;
                        String format = String.format("%d", new Object[]{Integer.valueOf(i2)});
                        this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                        this.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, this.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        if (i != 0) {
                            invalidate();
                        }
                    }
                }
            }
            if (this.countLayout != null) {
                if (i != 0) {
                    invalidate();
                }
                this.lastUnreadCount = 0;
                this.countLayout = null;
            }
        }
    }

    public void update() {
        int i = (int) this.dialog_id;
        if (i > 0) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.avatarDrawable.setInfo(this.currentUser);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i)));
        this.currentUser = null;
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        this.dialog_id = (long) i;
        String str = "50_50";
        String str2 = "";
        if (i > 0) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else {
                User user = this.currentUser;
                if (user != null) {
                    this.nameTextView.setText(UserObject.getFirstName(user));
                } else {
                    this.nameTextView.setText(str2);
                }
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.imageView.setImage(ImageLocation.getForUser(this.currentUser, false), str, this.avatarDrawable, this.currentUser);
        } else {
            Object chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText(str2);
            }
            this.avatarDrawable.setInfo((Chat) chat);
            this.currentUser = null;
            this.imageView.setImage(ImageLocation.getForChat(chat, false), str, this.avatarDrawable, chat);
        }
        if (z) {
            update(0);
        } else {
            this.countLayout = null;
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView) {
            int dp;
            int dp2;
            if (this.countLayout != null) {
                dp = AndroidUtilities.dp(6.0f);
                dp2 = AndroidUtilities.dp(54.0f);
                int dp3 = dp2 - AndroidUtilities.dp(5.5f);
                this.rect.set((float) dp3, (float) dp, (float) ((dp3 + this.countWidth) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + dp));
                RectF rectF = this.rect;
                float f = AndroidUtilities.density;
                canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) dp2, (float) (dp + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            User user = this.currentUser;
            if (user != null) {
                UserStatus userStatus = user.status;
                if ((userStatus != null && userStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))) {
                    dp = AndroidUtilities.dp(53.0f);
                    dp2 = AndroidUtilities.dp(59.0f);
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("windowBackgroundWhite"));
                    float f2 = (float) dp2;
                    float f3 = (float) dp;
                    canvas.drawCircle(f2, f3, (float) AndroidUtilities.dp(7.0f), Theme.dialogs_onlineCirclePaint);
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("chats_onlineCircle"));
                    canvas.drawCircle(f2, f3, (float) AndroidUtilities.dp(5.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
        }
        return drawChild;
    }
}
