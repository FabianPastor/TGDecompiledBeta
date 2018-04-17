package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorThemeCell extends FrameLayout {
    private static Paint colorPaint;
    private float alpha = 1.0f;
    private int currentColor;
    private boolean needDivider;
    private TextView textView;

    public TextColorThemeCell(Context context) {
        super(context);
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        int i2 = i | 48;
        i = 53;
        float f = (float) (LocaleController.isRTL ? 17 : 53);
        if (!LocaleController.isRTL) {
            i = 17;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i, 0.0f));
    }

    public void setAlpha(float value) {
        this.alpha = value;
        invalidate();
    }

    public float getAlpha() {
        return this.alpha;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
    }

    public void setTextAndColor(String text, int color) {
        this.textView.setText(text);
        this.currentColor = color;
        boolean z = !this.needDivider && this.currentColor == 0;
        setWillNotDraw(z);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentColor != 0) {
            colorPaint.setColor(this.currentColor);
            colorPaint.setAlpha((int) (255.0f * this.alpha));
            canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
