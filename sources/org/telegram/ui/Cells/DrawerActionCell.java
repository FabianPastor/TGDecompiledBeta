package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerActionCell extends FrameLayout {
    private int currentId;
    private RectF rect = new RectF();
    private TextView textView;

    public DrawerActionCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("chats_menuItemText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(29.0f));
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 19.0f, 0.0f, 16.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.currentId == 8) {
            Set<String> suggestions = MessagesController.getInstance(UserConfig.selectedAccount).pendingSuggestions;
            if (suggestions.contains("VALIDATE_PHONE_NUMBER") || suggestions.contains("VALIDATE_PASSWORD")) {
                int countTop = AndroidUtilities.dp(12.5f);
                int countWidth = AndroidUtilities.dp(9.0f);
                int x = ((getMeasuredWidth() - countWidth) - AndroidUtilities.dp(25.0f)) - AndroidUtilities.dp(5.5f);
                this.rect.set((float) x, (float) countTop, (float) (x + countWidth + AndroidUtilities.dp(14.0f)), (float) (AndroidUtilities.dp(23.0f) + countTop));
                Theme.chat_docBackPaint.setColor(Theme.getColor("chats_archiveBackground"));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_docBackPaint);
                int w = Theme.dialogs_errorDrawable.getIntrinsicWidth();
                int h = Theme.dialogs_errorDrawable.getIntrinsicHeight();
                Theme.dialogs_errorDrawable.setBounds((int) (this.rect.centerX() - ((float) (w / 2))), (int) (this.rect.centerY() - ((float) (h / 2))), (int) (this.rect.centerX() + ((float) (w / 2))), (int) (this.rect.centerY() + ((float) (h / 2))));
                Theme.dialogs_errorDrawable.draw(canvas);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
    }

    public void setTextAndIcon(int id, String text, int resId) {
        this.currentId = id;
        try {
            this.textView.setText(text);
            Drawable drawable = getResources().getDrawable(resId).mutate();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_menuItemIcon"), PorterDuff.Mode.MULTIPLY));
            }
            this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
