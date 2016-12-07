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
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public RadioButtonCell(Context context) {
        int i;
        int i2;
        int i3 = 17;
        int i4 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.SHARE_SHEET_SEND_DISABLED_TEXT_COLOR, -13129232);
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 18), 10.0f, (float) (LocaleController.isRTL ? 18 : 0), 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, (float) (LocaleController.isRTL ? 17 : 51), 10.0f, (float) (LocaleController.isRTL ? 51 : 17), 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        View view2 = this.valueTextView;
        if (!LocaleController.isRTL) {
            i4 = 3;
        }
        i2 = i4 | 48;
        float f = (float) (LocaleController.isRTL ? 17 : 51);
        if (LocaleController.isRTL) {
            i3 = 51;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i2, f, 35.0f, (float) i3, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextAndValue(String text, String value, boolean checked, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        this.radioButton.setChecked(checked, false);
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }
}
