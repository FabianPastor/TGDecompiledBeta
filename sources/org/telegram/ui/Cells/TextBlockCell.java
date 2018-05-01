package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextBlockCell extends FrameLayout {
    private boolean needDivider;
    private TextView textView;

    public TextBlockCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        context = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-1, -2.0f, i | 48, 17.0f, 10.0f, 17.0f, 10.0f));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(String str, boolean z) {
        this.textView.setText(str);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), i2);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
