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

    /* access modifiers changed from: protected */
    public void onPreDrawHintCharacter(int i, Canvas canvas, float f, float f2) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldDrawBehindText(int i) {
        return false;
    }

    public HintEditText(Context context) {
        super(context);
        this.hintPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
    }

    public void setTextSize(int i, float f) {
        super.setTextSize(i, f);
        this.hintPaint.setTextSize(TypedValue.applyDimension(i, f, getResources().getDisplayMetrics()));
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setHintText(String str) {
        this.hintText = str;
        onTextChange();
        setText(getText());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        onTextChange();
    }

    public void onTextChange() {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        if (this.hintText != null && length() < this.hintText.length()) {
            float f2 = 0.0f;
            for (int i = 0; i < this.hintText.length(); i++) {
                if (i < length()) {
                    f = getPaint().measureText(getText(), i, i + 1);
                } else {
                    f = this.hintPaint.measureText(this.hintText, i, i + 1);
                }
                float f3 = f;
                if (shouldDrawBehindText(i) || i >= length()) {
                    int color = this.hintPaint.getColor();
                    canvas.save();
                    TextPaint textPaint = this.hintPaint;
                    String str = this.hintText;
                    textPaint.getTextBounds(str, 0, str.length(), this.rect);
                    float height = ((float) (getHeight() + this.rect.height())) / 2.0f;
                    onPreDrawHintCharacter(i, canvas, f2, height);
                    canvas.drawText(this.hintText, i, i + 1, f2, height, this.hintPaint);
                    f2 += f3;
                    canvas.restore();
                    this.hintPaint.setColor(color);
                } else {
                    f2 += f3;
                }
            }
        }
        super.onDraw(canvas);
    }
}
