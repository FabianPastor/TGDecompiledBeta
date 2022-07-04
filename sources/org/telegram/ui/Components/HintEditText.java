package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;
import org.telegram.ui.ActionBar.Theme;

public class HintEditText extends EditTextBoldCursor {
    protected TextPaint hintPaint = new TextPaint(1);
    private String hintText;
    private Rect rect = new Rect();

    public HintEditText(Context context) {
        super(context);
        this.hintPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
    }

    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        this.hintPaint.setTextSize(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setHintText(String value) {
        this.hintText = value;
        onTextChange();
        setText(getText());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onTextChange();
    }

    public void onTextChange() {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float newOffset;
        if (this.hintText != null && length() < this.hintText.length()) {
            float offsetX = 0.0f;
            for (int a = 0; a < this.hintText.length(); a++) {
                if (a < length()) {
                    newOffset = getPaint().measureText(getText(), a, a + 1);
                } else {
                    newOffset = this.hintPaint.measureText(this.hintText, a, a + 1);
                }
                if (shouldDrawBehindText(a) || a >= length()) {
                    int color = this.hintPaint.getColor();
                    canvas.save();
                    TextPaint textPaint = this.hintPaint;
                    String str = this.hintText;
                    textPaint.getTextBounds(str, 0, str.length(), this.rect);
                    float offsetY = ((float) (getHeight() + this.rect.height())) / 2.0f;
                    onPreDrawHintCharacter(a, canvas, offsetX, offsetY);
                    canvas.drawText(this.hintText, a, a + 1, offsetX, offsetY, this.hintPaint);
                    offsetX += newOffset;
                    canvas.restore();
                    this.hintPaint.setColor(color);
                } else {
                    offsetX += newOffset;
                }
            }
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean shouldDrawBehindText(int index) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPreDrawHintCharacter(int index, Canvas canvas, float pivotX, float pivotY) {
    }
}
