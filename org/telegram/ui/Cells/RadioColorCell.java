package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioColorCell extends FrameLayout {
    private static Paint paint;
    private RadioButton radioButton;
    private TextView textView;

    public RadioColorCell(Context context) {
        int i;
        int i2 = 0;
        int i3 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.SHARE_SHEET_SEND_DISABLED_TEXT_COLOR, -13129232);
        View view = this.radioButton;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = (float) (LocaleController.isRTL ? 0 : 18);
        if (LocaleController.isRTL) {
            i2 = 18;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i4, f, 13.0f, (float) i2, 0.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
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
        view = this.textView;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3 | 48, (float) (LocaleController.isRTL ? 17 : 51), 12.0f, (float) (LocaleController.isRTL ? 51 : 17), 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
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
}
