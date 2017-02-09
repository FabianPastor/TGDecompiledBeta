package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailCell extends FrameLayout {
    private ImageView imageView;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailCell(Context context) {
        int i;
        int i2;
        int i3 = 5;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 16.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        View view2 = this.imageView;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i3 | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    public void setTextAndValue(String text, String value) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.imageView.setVisibility(4);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(resId);
    }
}
