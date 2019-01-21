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
        this(context, type, 17);
    }

    public CheckBoxCell(Context context, int type, int padding) {
        int i;
        View view;
        int i2;
        boolean z;
        int i3 = 5;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(type == 1 ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 16);
        if (type == 2) {
            view = this.textView;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, (float) (LocaleController.isRTL ? 0 : 29), 0.0f, (float) (LocaleController.isRTL ? 29 : 0), 0.0f));
        } else {
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? padding : (padding - 17) + 46), 0.0f, (float) (LocaleController.isRTL ? (padding - 17) + 46 : padding), 0.0f));
        }
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor(type == 1 ? "dialogTextBlue" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        textView.setGravity(i | 16);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -1.0f, i2 | 48, (float) padding, 0.0f, (float) padding, 0.0f));
        if (type == 1) {
            z = true;
        } else {
            z = false;
        }
        this.checkBox = new CheckBoxSquare(context, z);
        if (type == 2) {
            View view2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(view2, LayoutHelper.createFrame(18, 18.0f, i3 | 48, 0.0f, 15.0f, 0.0f, 0.0f));
            return;
        }
        View view3 = this.checkBox;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        i3 |= 48;
        float f = (float) (LocaleController.isRTL ? 0 : padding);
        if (!LocaleController.isRTL) {
            padding = 0;
        }
        addView(view3, LayoutHelper.createFrame(18, 18.0f, i3, f, 16.0f, (float) padding, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + AndroidUtilities.dp(50.0f));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), NUM));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, String value, boolean checked, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.checkBox.setChecked(checked, false);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setEnabled(boolean enabled) {
        float f;
        float f2 = 1.0f;
        super.setEnabled(enabled);
        TextView textView = this.textView;
        if (enabled) {
            f = 1.0f;
        } else {
            f = 0.5f;
        }
        textView.setAlpha(f);
        textView = this.valueTextView;
        if (enabled) {
            f = 1.0f;
        } else {
            f = 0.5f;
        }
        textView.setAlpha(f);
        CheckBoxSquare checkBoxSquare = this.checkBox;
        if (!enabled) {
            f2 = 0.5f;
        }
        checkBoxSquare.setAlpha(f2);
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
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
