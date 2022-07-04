package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_nameMessage_threeLines"), PorterDuff.Mode.MULTIPLY));
        TextView textView = new TextView(context);
        this.headerTextView = textView;
        textView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.headerTextView.setTextSize(1, 20.0f);
        this.headerTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.headerTextView.setGravity(17);
        addView(this.headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 75.0f, 52.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.messageTextView = textView2;
        textView2.setTextColor(Theme.getColor("chats_message"));
        this.messageTextView.setTextSize(1, 14.0f);
        this.messageTextView.setGravity(17);
        addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 36.0f, 110.0f, 36.0f, 0.0f));
        TextPaint textPaint = new TextPaint(1);
        textPaint.setColor(-1);
        textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        final Paint paint = new Paint(1);
        final TextPaint textPaint2 = textPaint;
        final String str = "500";
        AnonymousClass1 r3 = new FrameLayout(context) {
            RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setColor(Theme.getColor("windowBackgroundWhiteRedText"));
                canvas.save();
                canvas.translate((((float) getMeasuredWidth()) - textPaint2.measureText(str)) - ((float) AndroidUtilities.dp(8.0f)), AndroidUtilities.dpf2(7.0f));
                this.rect.set(0.0f, 0.0f, textPaint2.measureText(str), textPaint2.getTextSize());
                this.rect.inset((float) (-AndroidUtilities.dp(6.0f)), (float) (-AndroidUtilities.dp(3.0f)));
                float r = (textPaint2.getTextSize() / 2.0f) + ((float) AndroidUtilities.dp(3.0f));
                canvas.drawRoundRect(this.rect, r, r, paint);
                canvas.drawText(str, 0.0f, textPaint2.getTextSize() - AndroidUtilities.dpf2(2.0f), textPaint2);
                canvas.restore();
            }
        };
        this.imageLayout = r3;
        r3.setWillNotDraw(false);
        this.imageLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2, 1));
        addView(this.imageLayout, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 12.0f, 0.0f, 6.0f));
        this.headerTextView.setText(LocaleController.getString("TooManyCommunities", NUM));
        this.imageView.setImageResource(NUM);
    }

    public void setMessageText(String message) {
        this.messageTextView.setText(message);
    }
}
