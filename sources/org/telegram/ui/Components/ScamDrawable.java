package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class ScamDrawable extends Drawable {
    private int currentType;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    private String text;
    private TextPaint textPaint;
    private int textWidth;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public ScamDrawable(int i, int i2) {
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        this.currentType = i2;
        textPaint2.setTextSize((float) AndroidUtilities.dp((float) i));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        if (i2 == 0) {
            this.text = LocaleController.getString("ScamMessage", NUM);
        } else {
            this.text = LocaleController.getString("FakeMessage", NUM);
        }
        this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(this.text));
    }

    public void checkText() {
        String str;
        if (this.currentType == 0) {
            str = LocaleController.getString("ScamMessage", NUM);
        } else {
            str = LocaleController.getString("FakeMessage", NUM);
        }
        if (!str.equals(this.text)) {
            this.text = str;
            this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(str));
        }
    }

    public void setColor(int i) {
        this.textPaint.setColor(i);
        this.paint.setColor(i);
    }

    public int getIntrinsicWidth() {
        return this.textWidth + AndroidUtilities.dp(10.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(16.0f);
    }

    public void draw(Canvas canvas) {
        this.rect.set(getBounds());
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        canvas.drawText(this.text, this.rect.left + ((float) AndroidUtilities.dp(5.0f)), this.rect.top + ((float) AndroidUtilities.dp(12.0f)), this.textPaint);
    }
}
