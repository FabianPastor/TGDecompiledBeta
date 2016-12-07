package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private static Drawable countDrawable;
    private static Drawable countDrawableGrey;
    private static TextPaint countPaint;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private StaticLayout countLayout;
    private int countWidth;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;

    public HintDialogCell(Context context) {
        super(context);
        setBackgroundResource(R.drawable.list_selector);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-14606047);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
        if (countDrawable == null) {
            countDrawable = getResources().getDrawable(R.drawable.dialogs_badge);
            countDrawableGrey = getResources().getDrawable(R.drawable.dialogs_badge2);
            countPaint = new TextPaint(1);
            countPaint.setColor(-1);
            countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
        countPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
            getBackground().setHotspot(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void checkUnreadCounter(int mask) {
        if (mask == 0 || (mask & 256) != 0 || (mask & 2048) != 0) {
            TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
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
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) countPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, countPaint, this.countWidth, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (mask != 0) {
                    invalidate();
                }
            }
        }
    }

    public void setDialog(int uid, boolean counter, CharSequence name) {
        this.dialog_id = (long) uid;
        TLObject photo = null;
        if (uid > 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(user);
            if (!(user == null || user.photo == null)) {
                photo = user.photo.photo_small;
            }
        } else {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-uid));
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
            int top = AndroidUtilities.dp(6.0f);
            int left = AndroidUtilities.dp(54.0f);
            int x = left - AndroidUtilities.dp(5.5f);
            if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                countDrawableGrey.setBounds(x, top, (this.countWidth + x) + AndroidUtilities.dp(11.0f), countDrawableGrey.getIntrinsicHeight() + top);
                countDrawableGrey.draw(canvas);
            } else {
                countDrawable.setBounds(x, top, (this.countWidth + x) + AndroidUtilities.dp(11.0f), countDrawable.getIntrinsicHeight() + top);
                countDrawable.draw(canvas);
            }
            canvas.save();
            canvas.translate((float) left, (float) (AndroidUtilities.dp(4.0f) + top));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
        return result;
    }
}
