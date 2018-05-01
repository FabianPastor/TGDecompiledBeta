package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class LetterDrawable extends Drawable {
    private static TextPaint namePaint;
    public static Paint paint = new Paint();
    private StringBuilder stringBuilder = new StringBuilder(5);
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    public int getIntrinsicHeight() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public LetterDrawable() {
        if (namePaint == null) {
            paint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholder));
            namePaint = new TextPaint(1);
            namePaint.setColor(Theme.getColor(Theme.key_sharedMedia_linkPlaceholderText));
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(28.0f));
    }

    public void setBackgroundColor(int i) {
        paint.setColor(i);
    }

    public void setColor(int i) {
        namePaint.setColor(i);
    }

    public void setTitle(String str) {
        this.stringBuilder.setLength(0);
        if (str != null && str.length() > 0) {
            this.stringBuilder.append(str.substring(0, 1));
        }
        if (this.stringBuilder.length() > null) {
            try {
                this.textLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), namePaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (this.textLayout.getLineCount() > null) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        this.textLayout = null;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int width = bounds.width();
            canvas.save();
            canvas.drawRect((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, paint);
            if (this.textLayout != null) {
                float f = (float) width;
                canvas.translate((((float) bounds.left) + ((f - this.textWidth) / 2.0f)) - this.textLeft, ((float) bounds.top) + ((f - this.textHeight) / 2.0f));
                this.textLayout.draw(canvas);
            }
            canvas.restore();
        }
    }
}
