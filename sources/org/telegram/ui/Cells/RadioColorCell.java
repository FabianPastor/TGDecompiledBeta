package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioColorCell extends FrameLayout {
    private RadioButton radioButton;
    private TextView textView;

    public RadioColorCell(Context context) {
        int i;
        int i2 = 0;
        int i3 = 5;
        super(context);
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        RadioButton radioButton = this.radioButton;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = (float) (LocaleController.isRTL ? 0 : 18);
        if (LocaleController.isRTL) {
            i2 = 18;
        }
        addView(radioButton, LayoutHelper.createFrame(22, 22.0f, i4, f, 14.0f, (float) i2, 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
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
        TextView textView2 = this.textView;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i3 | 48, (float) (LocaleController.isRTL ? 21 : 51), 13.0f, (float) (LocaleController.isRTL ? 51 : 21), 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
    }

    public void setCheckColor(int color1, int color2) {
        this.radioButton.setColor(color1, color2);
    }

    public void setTextAndValue(String text, boolean checked) {
        this.textView.setText(text);
        this.radioButton.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(this.radioButton.isChecked());
    }
}
