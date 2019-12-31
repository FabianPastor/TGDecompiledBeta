package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.text.TextPaint;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TooManyCommunitiesHintCell extends FrameLayout {
    private TextView headerTextView;
    private FrameLayout imageLayout;
    private ImageView imageView;
    private TextView messageTextView;

    public TooManyCommunitiesHintCell(Context context) {
        super(context);
        this.imageView = new ImageView(context);
        String str = "chats_nameMessage_threeLines";
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.headerTextView = new TextView(context);
        this.headerTextView.setTextColor(Theme.getColor(str));
        this.headerTextView.setTextSize(1, 20.0f);
        str = "fonts/rmedium.ttf";
        this.headerTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.headerTextView.setGravity(17);
        addView(this.headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 75.0f, 52.0f, 0.0f));
        this.messageTextView = new TextView(context);
        this.messageTextView.setTextColor(Theme.getColor("chats_message"));
        this.messageTextView.setTextSize(1, 14.0f);
        this.messageTextView.setGravity(17);
        addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 36.0f, 110.0f, 36.0f, 0.0f));
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setColor(-1);
        textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.getTypeface(str));
        final Paint paint = new Paint(1);
        final String str2 = "500";
        this.imageLayout = new FrameLayout(context) {
            RectF rect = new RectF();

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setColor(Theme.getColor("windowBackgroundWhiteRedText"));
                canvas.save();
                canvas.translate((((float) getMeasuredWidth()) - textPaint.measureText(str2)) - ((float) AndroidUtilities.dp(8.0f)), AndroidUtilities.dpf2(7.0f));
                this.rect.set(0.0f, 0.0f, textPaint.measureText(str2), textPaint.getTextSize());
                this.rect.inset((float) (-AndroidUtilities.dp(6.0f)), (float) (-AndroidUtilities.dp(3.0f)));
                float textSize = (textPaint.getTextSize() / 2.0f) + ((float) AndroidUtilities.dp(3.0f));
                canvas.drawRoundRect(this.rect, textSize, textSize, paint);
                canvas.drawText(str2, 0.0f, textPaint.getTextSize() - AndroidUtilities.dpf2(2.0f), textPaint);
                canvas.restore();
            }
        };
        this.imageLayout.setWillNotDraw(false);
        this.imageLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2, 1));
        addView(this.imageLayout, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 12.0f, 0.0f, 6.0f));
        this.headerTextView.setText(LocaleController.getString("TooManyCommunities", NUM));
        this.imageView.setImageResource(NUM);
    }

    public void setMessageText(String str) {
        this.messageTextView.setText(str);
    }
}
