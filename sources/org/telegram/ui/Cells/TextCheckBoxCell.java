package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class TextCheckBoxCell extends FrameLayout {
    private CheckBoxSquare checkBox;
    private boolean needDivider;
    private TextView textView;

    public TextCheckBoxCell(Context context) {
        this(context, false);
    }

    public TextCheckBoxCell(Context context, boolean dialog) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(dialog ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
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
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView2 = this.textView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        addView(textView2, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, LocaleController.isRTL ? 66.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 66.0f, 0.0f));
        this.checkBox = new CheckBoxSquare(context, dialog);
        this.checkBox.setDuplicateParentStateEnabled(false);
        this.checkBox.setFocusable(false);
        this.checkBox.setFocusableInTouchMode(false);
        this.checkBox.setClickable(false);
        CheckBoxSquare checkBoxSquare = this.checkBox;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, i3 | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    public void invalidate() {
        super.invalidate();
        this.checkBox.invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(50.0f), NUM));
    }

    public void setTextAndCheck(String text, boolean checked, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.checkBox.setChecked(checked, false);
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.CheckBox");
        info.setCheckable(true);
        info.setChecked(isChecked());
    }
}
