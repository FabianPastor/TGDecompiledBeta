package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorCell extends FrameLayout {
    private static Paint colorPaint;
    public static final int[] colors = {-1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925};
    public static final int[] colorsToSave = {-65536, -29183, -256, -16711936, -16711681, -16776961, -2984711, -65281, -1};
    private float alpha;
    private int currentColor;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public TextColorCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public TextColorCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.alpha = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    @Keep
    public void setAlpha(float f) {
        this.alpha = f;
        invalidate();
    }

    @Keep
    public float getAlpha() {
        return this.alpha;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setTextAndColor(String str, int i, boolean z) {
        this.textView.setText(str);
        this.needDivider = z;
        this.currentColor = i;
        setWillNotDraw(!z && i == 0);
        invalidate();
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        super.setEnabled(z);
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView2 = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(textView2, property, fArr));
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(this, property2, fArr2));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        if (!z) {
            f = 0.5f;
        }
        setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int i = this.currentColor;
        if (i != 0) {
            colorPaint.setColor(i);
            colorPaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) (LocaleController.isRTL ? AndroidUtilities.dp(33.0f) : getMeasuredWidth() - AndroidUtilities.dp(33.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
