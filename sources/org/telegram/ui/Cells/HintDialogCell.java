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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
    }

    public void checkUnreadCounter(int mask) {
        if (mask == 0 || (mask & 256) != 0 || (mask & 2048) != 0) {
            TL_dialog dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog == null || dialog.unread_count == 0) {
                if (this.countLayout != null) {
                    if (mask != 0) {
                        invalidate();
                    }
                    this.lastUnreadCount = 0;
                    this.countLayout = null;
                }
            } else if (this.lastUnreadCount != dialog.unread_count) {
                this.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", new Object[]{Integer.valueOf(dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (mask != 0) {
                    invalidate();
                }
            }
        }
    }

    public void update() {
        int uid = (int) this.dialog_id;
        if (uid > 0) {
            this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid)));
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid)));
    }

    public void setDialog(int uid, boolean counter, CharSequence name) {
        this.dialog_id = (long) uid;
        TLObject photo = null;
        if (uid > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(user);
            if (!(user == null || user.photo == null)) {
                photo = user.photo.photo_small;
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(chat);
            if (!(chat == null || chat.photo == null)) {
                photo = chat.photo.photo_small;
            }
        }
        this.imageView.setImage(photo, "50_50", this.avatarDrawable);
        if (counter) {
            checkUnreadCounter(0);
        } else {
            this.countLayout = null;
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && this.countLayout != null) {
            int top = AndroidUtilities.dp(NUM);
            int left = AndroidUtilities.dp(NUM);
            int x = left - AndroidUtilities.dp(5.5f);
            this.rect.set((float) x, (float) top, (float) ((this.countWidth + x) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + top));
            canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, 11.5f * AndroidUtilities.density, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
            canvas.save();
            canvas.translate((float) left, (float) (AndroidUtilities.dp(4.0f) + top));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
        return result;
    }
}
