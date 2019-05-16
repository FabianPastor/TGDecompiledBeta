package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public RadioButtonCell(Context context) {
        this(context, false);
    }

    public RadioButtonCell(Context context, boolean z) {
        Context context2 = context;
        super(context);
        this.radioButton = new RadioButton(context2);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (z) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
        }
        RadioButton radioButton = this.radioButton;
        int i = 5;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 20;
        float f = (float) (LocaleController.isRTL ? 0 : 20);
        if (!LocaleController.isRTL) {
            i3 = 0;
        }
        addView(radioButton, LayoutHelper.createFrame(22, 22.0f, i2, f, 10.0f, (float) i3, 0.0f));
        this.textView = new TextView(context2);
        if (z) {
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        } else {
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView = this.textView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        i2 = 23;
        float f2 = (float) (LocaleController.isRTL ? 23 : 61);
        if (LocaleController.isRTL) {
            i2 = 61;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i4, f2, 10.0f, (float) i2, 0.0f));
        this.valueTextView = new TextView(context2);
        if (z) {
            this.valueTextView.setTextColor(Theme.getColor("dialogTextGray2"));
        } else {
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        }
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        TextView textView2 = this.valueTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        int i5 = i | 48;
        int i6 = 17;
        float f3 = (float) (LocaleController.isRTL ? 17 : 61);
        if (LocaleController.isRTL) {
            i6 = 61;
        }
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i5, f3, 35.0f, (float) i6, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextAndValue(String str, String str2, boolean z, boolean z2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.radioButton.setChecked(z2, false);
        this.needDivider = z;
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    /* Access modifiers changed, original: protected */
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
    }
}
