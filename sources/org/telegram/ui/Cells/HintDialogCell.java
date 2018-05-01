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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private StaticLayout countLayout;
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
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
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
    }

    public void checkUnreadCounter(int i) {
        if (i == 0 || (i & 256) != 0 || (i & 2048) != 0) {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tL_dialog == null || tL_dialog.unread_count == 0) {
                if (this.countLayout != null) {
                    if (i != 0) {
                        invalidate();
                    }
                    this.lastUnreadCount = 0;
                    this.countLayout = 0;
                }
            } else if (this.lastUnreadCount != tL_dialog.unread_count) {
                this.lastUnreadCount = tL_dialog.unread_count;
                CharSequence format = String.format("%d", new Object[]{Integer.valueOf(tL_dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(format)));
                this.countLayout = new StaticLayout(format, Theme.dialogs_countTextPaint, this.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (i != 0) {
                    invalidate();
                }
            }
        }
    }

    public void update() {
        int i = (int) this.dialog_id;
        if (i > 0) {
            this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)));
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i)));
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        TLObject tLObject;
        this.dialog_id = (long) i;
        if (i > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (user != 0) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(user);
            if (!(user == null || user.photo == null)) {
                tLObject = user.photo.photo_small;
                this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
                if (z) {
                    checkUnreadCounter(0);
                } else {
                    this.countLayout = null;
                }
            }
        }
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
        if (charSequence != null) {
            this.nameTextView.setText(charSequence);
        } else if (chat != 0) {
            this.nameTextView.setText(chat.title);
        } else {
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
        this.avatarDrawable.setInfo(chat);
        if (!(chat == null || chat.photo == null)) {
            tLObject = chat.photo.photo_small;
            this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
            if (z) {
                this.countLayout = null;
            } else {
                checkUnreadCounter(0);
            }
        }
        tLObject = 0;
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
        if (z) {
            checkUnreadCounter(0);
        } else {
            this.countLayout = null;
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        j = super.drawChild(canvas, view, j);
        if (view == this.imageView && this.countLayout != null) {
            view = AndroidUtilities.dp(6.0f);
            int dp = AndroidUtilities.dp(54.0f);
            int dp2 = dp - AndroidUtilities.dp(5.5f);
            this.rect.set((float) dp2, (float) view, (float) ((dp2 + this.countWidth) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + view));
            canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
            canvas.save();
            canvas.translate((float) dp, (float) (view + AndroidUtilities.dp(4.0f)));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
        return j;
    }
}
