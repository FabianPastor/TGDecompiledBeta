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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
    }

    public void update(int mask) {
        if (!((mask & 4) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
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
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            this.avatarDrawable.setInfo(this.currentUser);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid)));
        this.currentUser = null;
    }

    public void setDialog(int uid, boolean counter, CharSequence name) {
        Object parentObject;
        this.dialog_id = (long) uid;
        TLObject photo = null;
        if (uid > 0) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (this.currentUser != null) {
                this.nameTextView.setText(UserObject.getFirstName(this.currentUser));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(this.currentUser);
            if (!(this.currentUser == null || this.currentUser.photo == null)) {
                photo = this.currentUser.photo.photo_small;
            }
            parentObject = this.currentUser;
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            if (!(chat == null || chat.photo == null)) {
                photo = chat.photo.photo_small;
            }
            Chat parentObject2 = chat;
            this.currentUser = null;
        }
        this.imageView.setImage(photo, "50_50", this.avatarDrawable, parentObject2);
        if (counter) {
            update(0);
        } else {
            this.countLayout = null;
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView) {
            int top;
            int left;
            if (this.countLayout != null) {
                top = AndroidUtilities.dp(6.0f);
                left = AndroidUtilities.dp(54.0f);
                int x = left - AndroidUtilities.dp(5.5f);
                this.rect.set((float) x, (float) top, (float) ((this.countWidth + x) + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + top));
                canvas.drawRoundRect(this.rect, 11.5f * AndroidUtilities.density, 11.5f * AndroidUtilities.density, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) left, (float) (AndroidUtilities.dp(4.0f) + top));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            if (!(this.currentUser == null || this.currentUser.status == null || this.currentUser.status.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))) {
                top = AndroidUtilities.dp(53.0f);
                left = AndroidUtilities.dp(59.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("windowBackgroundWhite"));
                canvas.drawCircle((float) left, (float) top, (float) AndroidUtilities.dp(7.0f), Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("chats_onlineCircle"));
                canvas.drawCircle((float) left, (float) top, (float) AndroidUtilities.dp(5.0f), Theme.dialogs_onlineCirclePaint);
            }
        }
        return result;
    }
}
