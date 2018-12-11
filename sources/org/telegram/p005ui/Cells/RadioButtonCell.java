package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadioButton;

/* renamed from: org.telegram.ui.Cells.RadioButtonCell */
public class RadioButtonCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public RadioButtonCell(Context context) {
        this(context, false);
    }

    public RadioButtonCell(Context context, boolean dialog) {
        int i;
        int i2;
        int i3 = 23;
        int i4 = 5;
        super(context);
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.m9dp(20.0f));
        if (dialog) {
            this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        } else {
            this.radioButton.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
        }
        View view = this.radioButton;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i | 48, (float) (LocaleController.isRTL ? 0 : 20), 10.0f, (float) (LocaleController.isRTL ? 20 : 0), 0.0f));
        this.textView = new TextView(context);
        if (dialog) {
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        } else {
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2 | 16);
        view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, (float) (LocaleController.isRTL ? 23 : 61), 10.0f, (float) (LocaleController.isRTL ? 61 : 23), 0.0f));
        this.valueTextView = new TextView(context);
        if (dialog) {
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        } else {
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        }
        this.valueTextView.setTextSize(1, 13.0f);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.m9dp(12.0f));
        View view2 = this.valueTextView;
        if (!LocaleController.isRTL) {
            i4 = 3;
        }
        i = i4 | 48;
        float f = (float) (LocaleController.isRTL ? 23 : 61);
        if (LocaleController.isRTL) {
            i3 = 61;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i, f, 35.0f, (float) i3, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextAndValue(String text, String value, boolean divider, boolean checked) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.radioButton.setChecked(checked, false);
        this.needDivider = divider;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    protected void onDraw(Canvas canvas) {
        float f = 60.0f;
        if (this.needDivider) {
            float f2;
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = 60.0f;
            }
            float dp = (float) AndroidUtilities.m9dp(f2);
            float height = (float) (getHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (!LocaleController.isRTL) {
                f = 0.0f;
            }
            canvas.drawLine(dp, height, (float) (measuredWidth - AndroidUtilities.m9dp(f)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
