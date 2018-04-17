package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    private CheckBoxSquare checkBox;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int type) {
        Context context2 = context;
        int i = type;
        super(context);
        this.textView = new TextView(context2);
        boolean z = true;
        this.textView.setTextColor(Theme.getColor(i == 1 ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        r0.textView.setTextSize(1, 16.0f);
        r0.textView.setLines(1);
        r0.textView.setMaxLines(1);
        r0.textView.setSingleLine(true);
        r0.textView.setEllipsize(TruncateAt.END);
        int i2 = 3;
        r0.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        int i3 = 17;
        View view;
        int i4;
        int i5;
        float f;
        if (i == 2) {
            view = r0.textView;
            i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            i5 = 29;
            f = (float) (LocaleController.isRTL ? 0 : 29);
            if (!LocaleController.isRTL) {
                i5 = 0;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i4, f, 0.0f, (float) i5, 0.0f));
        } else {
            view = r0.textView;
            i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            i5 = 46;
            f = (float) (LocaleController.isRTL ? 17 : 46);
            if (!LocaleController.isRTL) {
                i5 = 17;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i4, f, 0.0f, (float) i5, 0.0f));
        }
        r0.valueTextView = new TextView(context2);
        r0.valueTextView.setTextColor(Theme.getColor(i == 1 ? Theme.key_dialogTextBlue : Theme.key_windowBackgroundWhiteValueText));
        r0.valueTextView.setTextSize(1, 16.0f);
        r0.valueTextView.setLines(1);
        r0.valueTextView.setMaxLines(1);
        r0.valueTextView.setSingleLine(true);
        r0.valueTextView.setEllipsize(TruncateAt.END);
        r0.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(r0.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        if (i != 1) {
            z = false;
        }
        r0.checkBox = new CheckBoxSquare(context2, z);
        if (i == 2) {
            View view2 = r0.checkBox;
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view2, LayoutHelper.createFrame(18, 18.0f, i2 | 48, 0.0f, 15.0f, 0.0f, 0.0f));
            return;
        }
        view2 = r0.checkBox;
        if (LocaleController.isRTL) {
            i2 = 5;
        }
        int i6 = i2 | 48;
        float f2 = (float) (LocaleController.isRTL ? 0 : 17);
        if (!LocaleController.isRTL) {
            i3 = 0;
        }
        addView(view2, LayoutHelper.createFrame(18, 18.0f, i6, f2, 15.0f, (float) i3, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(48.0f) + this.needDivider);
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, String value, boolean checked, boolean divider) {
        this.textView.setText(text);
        this.checkBox.setChecked(checked, false);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(divider ^ 1);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        float f = 0.5f;
        this.textView.setAlpha(enabled ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(enabled ? 1.0f : 0.5f);
        CheckBoxSquare checkBoxSquare = this.checkBox;
        if (enabled) {
            f = 1.0f;
        }
        checkBoxSquare.setAlpha(f);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public CheckBoxSquare getCheckBox() {
        return this.checkBox;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
