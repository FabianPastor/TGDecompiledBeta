package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public RadioButtonCell(Context context, boolean z) {
        super(context);
        Context context2 = context;
        this.radioButton = new RadioButton(context2);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        if (z) {
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        } else {
            this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
        }
        int i = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 20), 10.0f, (float) (!LocaleController.isRTL ? 0 : 20), 0.0f));
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
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 23 : 61), 10.0f, (float) (LocaleController.isRTL ? 61 : 23), 0.0f));
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
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 17 : 61), 35.0f, (float) (LocaleController.isRTL ? 61 : 17), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
    }
}
