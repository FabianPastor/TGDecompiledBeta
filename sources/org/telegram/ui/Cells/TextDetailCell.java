package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
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
    private boolean multiline;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        int i = 3;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        context = this.imageView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-2, -2.0f, i | 48, LocaleController.isRTL ? 0.0f : 16.0f, 11.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        if (this.multiline) {
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.textView, i3, 0, i4, 0);
            measureChildWithMargins(this.valueTextView, i3, 0, i4, 0);
            measureChildWithMargins(this.imageView, i3, 0, i4, 0);
            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(64.0f), (this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight()) + AndroidUtilities.dp(20.0f)));
            return;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.multiline) {
            z = this.textView.getMeasuredHeight() + AndroidUtilities.dp(NUM);
            this.valueTextView.layout(this.valueTextView.getLeft(), z, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + z);
        }
    }

    public void setTextAndValue(String str, String str2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.imageView.setVisibility(4);
    }

    public void setTextAndValueAndIcon(String str, String str2, int i, int i2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.imageView.setVisibility(null);
        this.imageView.setImageResource(i);
        str = 3;
        if (i2 == 0) {
            i2 = this.imageView;
            if (LocaleController.isRTL) {
                str = 5;
            }
            i2.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, str | 16, LocaleController.isRTL != null ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL != null ? 16.0f : 0.0f, 0.0f));
            return;
        }
        ImageView imageView = this.imageView;
        if (LocaleController.isRTL) {
            str = 5;
        }
        imageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, str | 48, LocaleController.isRTL != null ? 0.0f : 16.0f, (float) i2, LocaleController.isRTL != null ? 16.0f : 0.0f, 0.0f));
    }

    public void setMultiline(boolean z) {
        this.multiline = z;
        if (this.multiline) {
            this.textView.setSingleLine(false);
            return;
        }
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
    }
}
