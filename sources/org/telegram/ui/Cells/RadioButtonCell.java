package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout {
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public RadioButtonCell(Context context) {
        Context context2 = context;
        super(context);
        this.radioButton = new RadioButton(context2);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
        View view = this.radioButton;
        int i = 3;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 18;
        float f = (float) (LocaleController.isRTL ? 0 : 18);
        if (!LocaleController.isRTL) {
            i3 = 0;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i2, f, 10.0f, (float) i3, 0.0f));
        r0.textView = new TextView(context2);
        r0.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.textView.setTextSize(1, 16.0f);
        r0.textView.setLines(1);
        r0.textView.setMaxLines(1);
        r0.textView.setSingleLine(true);
        r0.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        int i4 = 51;
        addView(r0.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 17 : 51), 10.0f, (float) (LocaleController.isRTL ? 51 : 17), 0.0f));
        r0.valueTextView = new TextView(context2);
        r0.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.valueTextView.setTextSize(1, 13.0f);
        r0.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.valueTextView.setLines(0);
        r0.valueTextView.setMaxLines(0);
        r0.valueTextView.setSingleLine(false);
        r0.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        View view2 = r0.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        int i5 = i | 48;
        float f2 = (float) (LocaleController.isRTL ? 17 : 51);
        if (!LocaleController.isRTL) {
            i4 = 17;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i5, f2, 35.0f, (float) i4, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.radioButton.setChecked(z, null);
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }
}
