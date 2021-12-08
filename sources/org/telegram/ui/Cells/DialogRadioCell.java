package org.telegram.ui.Cells;

import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RadioButton;

public class DialogRadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = 0.0f;
            float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f);
            float height = (float) (getHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = 60.0f;
            }
            canvas.drawLine(dp, height, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
