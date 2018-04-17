package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorCell extends FrameLayout {
    private static Paint colorPaint;
    public static final int[] colors = new int[]{-1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925};
    public static final int[] colorsToSave = new int[]{-65536, -29183, -256, -16711936, -16711681, -16776961, -2984711, -65281, -1};
    private float alpha = 1.0f;
    private int currentColor;
    private boolean needDivider;
    private TextView textView;

    public TextColorCell(Context context) {
        super(context);
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
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

    public void setTextAndColor(String text, int color, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        this.currentColor = color;
        boolean z = !this.needDivider && this.currentColor == 0;
        setWillNotDraw(z);
        invalidate();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 0.5f;
        if (animators != null) {
            TextView textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            String str2 = "alpha";
            float[] fArr2 = new float[1];
            if (value) {
                f = 1.0f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(this, str2, fArr2));
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (value) {
            f = 1.0f;
        }
        setAlpha(f);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
        if (this.currentColor != 0) {
            colorPaint.setColor(this.currentColor);
            colorPaint.setAlpha((int) (255.0f * this.alpha));
            canvas.drawCircle((float) (LocaleController.isRTL ? AndroidUtilities.dp(29.0f) : getMeasuredWidth() - AndroidUtilities.dp(29.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
