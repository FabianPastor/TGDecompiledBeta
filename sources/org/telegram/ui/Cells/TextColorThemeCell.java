package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
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
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        TextView textView3 = this.textView;
        boolean z = LocaleController.isRTL;
        addView(textView3, LayoutHelper.createFrame(-1, -1.0f, (!z ? 3 : i) | 48, (float) (z ? 21 : 57), 0.0f, (float) (z ? 57 : 21), 0.0f));
    }

    public void setAlpha(float f) {
        this.alpha = f;
        invalidate();
    }

    public float getAlpha() {
        return this.alpha;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setTextAndColor(CharSequence charSequence, int i) {
        this.textView.setText(charSequence);
        this.currentColor = i;
        setWillNotDraw(!this.needDivider && i == 0);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.currentColor;
        if (i != 0) {
            colorPaint.setColor(i);
            colorPaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
