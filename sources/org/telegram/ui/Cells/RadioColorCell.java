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

public class RadioColorCell extends FrameLayout {
    private RadioButton radioButton;
    private TextView textView;

    public RadioColorCell(Context context) {
        super(context);
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        View view = this.radioButton;
        int i = 3;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 18;
        float f = (float) (LocaleController.isRTL ? 0 : 18);
        if (!LocaleController.isRTL) {
            i3 = 0;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i2, f, 13.0f, (float) i3, 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        context = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        i2 = i | 48;
        int i4 = 51;
        f = (float) (LocaleController.isRTL ? 17 : 51);
        if (!LocaleController.isRTL) {
            i4 = 17;
        }
        addView(context, LayoutHelper.createFrame(-2, -2.0f, i2, f, 12.0f, (float) i4, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    public void setCheckColor(int i, int i2) {
        this.radioButton.setColor(i, i2);
    }

    public void setTextAndValue(String str, boolean z) {
        this.textView.setText(str);
        this.radioButton.setChecked(z, false);
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }
}
