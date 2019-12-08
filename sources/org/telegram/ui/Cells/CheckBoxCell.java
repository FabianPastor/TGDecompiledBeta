package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    private CheckBoxSquare checkBox;
    private int currentType;
    private boolean isMultiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int i) {
        this(context, i, 17);
    }

    public CheckBoxCell(Context context, int i, int i2) {
        Context context2 = context;
        int i3 = i;
        int i4 = i2;
        super(context);
        this.currentType = i3;
        this.textView = new TextView(context2);
        boolean z = true;
        this.textView.setTextColor(Theme.getColor(i3 == 1 ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor(i3 == 1 ? "dialogTextLink" : "windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        if (i3 == 3) {
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 29.0f, 0.0f, 0.0f, 0.0f));
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        } else {
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            if (i3 == 2) {
                TextView textView = this.textView;
                int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
                int i6 = 29;
                float f = (float) (LocaleController.isRTL ? 0 : 29);
                if (!LocaleController.isRTL) {
                    i6 = 0;
                }
                addView(textView, LayoutHelper.createFrame(-1, -1.0f, i5, f, 0.0f, (float) i6, 0.0f));
            } else {
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? i4 : (i4 - 17) + 46), 0.0f, (float) (LocaleController.isRTL ? (i4 - 17) + 46 : i4), 0.0f));
            }
        }
        this.valueTextView = new TextView(context2);
        this.valueTextView.setTextColor(Theme.getColor(i3 == 1 ? "dialogTextBlue" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        float f2 = (float) i4;
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, f2, 0.0f, f2, 0.0f));
        if (i3 != 1) {
            z = false;
        }
        this.checkBox = new CheckBoxSquare(context2, z);
        if (i3 == 3) {
            addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 51, 0.0f, 15.0f, 0.0f, 0.0f));
        } else if (i3 == 2) {
            addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 15.0f, 0.0f, 0.0f));
        } else {
            CheckBoxSquare checkBoxSquare = this.checkBox;
            int i7 = (LocaleController.isRTL ? 5 : 3) | 48;
            float f3 = (float) (LocaleController.isRTL ? 0 : i4);
            if (!LocaleController.isRTL) {
                i4 = 0;
            }
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, i7, f3, 16.0f, (float) i4, 0.0f));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (this.currentType == 3) {
            i = MeasureSpec.getSize(i);
            this.valueTextView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.textView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(34.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), NUM));
            setMeasuredDimension(this.textView.getMeasuredWidth() + AndroidUtilities.dp(29.0f), AndroidUtilities.dp(50.0f));
        } else if (this.isMultiline) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + this.needDivider);
            i = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
            this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i / 2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.textView.measure(MeasureSpec.makeMeasureSpec((i - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), NUM));
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(CharSequence charSequence, String str, boolean z, boolean z2) {
        this.textView.setText(charSequence);
        this.checkBox.setChecked(z, false);
        this.valueTextView.setText(str);
        this.needDivider = z2;
        setWillNotDraw(z2 ^ 1);
    }

    public void setMultiline(boolean z) {
        this.isMultiline = z;
        LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
        LayoutParams layoutParams2 = (LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.textView.setLines(0);
            this.textView.setMaxLines(0);
            this.textView.setSingleLine(false);
            this.textView.setEllipsize(null);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(5.0f));
            layoutParams.height = -2;
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            layoutParams2.topMargin = AndroidUtilities.dp(12.0f);
        } else {
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setPadding(0, 0, 0, 0);
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            layoutParams2.topMargin = AndroidUtilities.dp(15.0f);
        }
        this.textView.setLayoutParams(layoutParams);
        this.checkBox.setLayoutParams(layoutParams2);
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        float f = 1.0f;
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(z ? 1.0f : 0.5f);
        CheckBoxSquare checkBoxSquare = this.checkBox;
        if (!z) {
            f = 0.5f;
        }
        checkBoxSquare.setAlpha(f);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
    }
}
