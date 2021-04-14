package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    private View checkBox;
    private CheckBox2 checkBoxRound;
    private int checkBoxSize;
    private CheckBoxSquare checkBoxSquare;
    private int currentType;
    private boolean isMultiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int i) {
        this(context, i, 17);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CheckBoxCell(Context context, int i, int i2) {
        super(context);
        String str;
        String str2;
        Context context2 = context;
        int i3 = i;
        int i4 = i2;
        this.checkBoxSize = 18;
        this.currentType = i3;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        int i5 = 5;
        boolean z = true;
        textView2.setTextColor(Theme.getColor((i3 == 1 || i3 == 5) ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor((i3 == 1 || i3 == 5) ? "dialogTextLink" : "windowBackgroundWhiteLinkText"));
        this.textView.setTag(Integer.valueOf(Theme.getColor((i3 == 1 || i3 == 5) ? "dialogTextBlack" : str2)));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (i3 == 3) {
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 29.0f, 0.0f, 0.0f, 0.0f));
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        } else {
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            if (i3 == 2) {
                TextView textView3 = this.textView;
                boolean z2 = LocaleController.isRTL;
                addView(textView3, LayoutHelper.createFrame(-1, -1.0f, (z2 ? 5 : 3) | 48, (float) (z2 ? 0 : 29), 0.0f, (float) (z2 ? 29 : 0), 0.0f));
            } else {
                int i6 = i3 == 4 ? 56 : 46;
                TextView textView4 = this.textView;
                boolean z3 = LocaleController.isRTL;
                addView(textView4, LayoutHelper.createFrame(-1, -1.0f, (z3 ? 5 : 3) | 48, (float) (z3 ? i4 : i6 + (i4 - 17)), 0.0f, (float) (z3 ? i6 + (i4 - 17) : i4), 0.0f));
            }
        }
        TextView textView5 = new TextView(context2);
        this.valueTextView = textView5;
        textView5.setTextColor(Theme.getColor((i3 == 1 || i3 == 5) ? "dialogTextBlue" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTag((i3 == 1 || i3 == 5) ? "dialogTextBlue" : str);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        float f = (float) i4;
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, f, 0.0f, f, 0.0f));
        if (i3 == 4) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBoxRound = checkBox2;
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(true);
            this.checkBoxRound.setChecked(true, false);
            this.checkBoxRound.setDrawBackgroundAsArc(10);
            this.checkBoxSize = 21;
            View view = this.checkBox;
            float f2 = (float) 21;
            boolean z4 = LocaleController.isRTL;
            addView(view, LayoutHelper.createFrame(21, f2, (!z4 ? 3 : i5) | 48, (float) (z4 ? 0 : i4), 16.0f, (float) (!z4 ? 0 : i4), 0.0f));
            return;
        }
        if (!(i3 == 1 || i3 == 5)) {
            z = false;
        }
        CheckBoxSquare checkBoxSquare2 = new CheckBoxSquare(context2, z);
        this.checkBoxSquare = checkBoxSquare2;
        this.checkBox = checkBoxSquare2;
        this.checkBoxSize = 18;
        if (i3 == 5) {
            float f3 = (float) 18;
            boolean z5 = LocaleController.isRTL;
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, f3, (!z5 ? 3 : i5) | 16, (float) (z5 ? 0 : i4), 0.0f, (float) (!z5 ? 0 : i4), 0.0f));
        } else if (i3 == 3) {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, 51, 0.0f, 15.0f, 0.0f, 0.0f));
        } else if (i3 == 2) {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, (!LocaleController.isRTL ? 3 : i5) | 48, 0.0f, 15.0f, 0.0f, 0.0f));
        } else {
            float f4 = (float) 18;
            boolean z6 = LocaleController.isRTL;
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, f4, (!z6 ? 3 : i5) | 48, (float) (z6 ? 0 : i4), 16.0f, (float) (!z6 ? 0 : i4), 0.0f));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.currentType == 3) {
            int size = View.MeasureSpec.getSize(i);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(34.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), NUM));
            setMeasuredDimension(this.textView.getMeasuredWidth() + AndroidUtilities.dp(29.0f), AndroidUtilities.dp(50.0f));
        } else if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
            int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec((measuredWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), NUM));
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(CharSequence charSequence, String str, boolean z, boolean z2) {
        this.textView.setText(charSequence);
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, false);
        } else {
            this.checkBoxSquare.setChecked(z, false);
        }
        this.valueTextView.setText(str);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setMultiline(boolean z) {
        this.isMultiline = z;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.textView.setLines(0);
            this.textView.setMaxLines(0);
            this.textView.setSingleLine(false);
            this.textView.setEllipsize((TextUtils.TruncateAt) null);
            if (this.currentType != 5) {
                this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(5.0f));
                layoutParams.height = -2;
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.topMargin = AndroidUtilities.dp(12.0f);
            }
        } else {
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
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
        View view = this.checkBox;
        if (!z) {
            f = 0.5f;
        }
        view.setAlpha(f);
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        } else {
            this.checkBoxSquare.setChecked(z, z2);
        }
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return this.checkBoxSquare.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public View getCheckBoxView() {
        return this.checkBox;
    }

    public void setCheckBoxColor(String str, String str2, String str3) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setColor(str, str, str3);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            int i = this.currentType == 4 ? 50 : 20;
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) i), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) i) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
    }
}
