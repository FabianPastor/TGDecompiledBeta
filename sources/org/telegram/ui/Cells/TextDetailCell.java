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
        View view = this.imageView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, LocaleController.isRTL ? 0.0f : 16.0f, 11.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.multiline) {
            int i = widthMeasureSpec;
            int i2 = heightMeasureSpec;
            measureChildWithMargins(this.textView, i, 0, i2, 0);
            measureChildWithMargins(this.valueTextView, i, 0, i2, 0);
            measureChildWithMargins(this.imageView, i, 0, i2, 0);
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(64.0f), (this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight()) + AndroidUtilities.dp(20.0f)));
            return;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.multiline) {
            int y = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            this.valueTextView.layout(this.valueTextView.getLeft(), y, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + y);
        }
    }

    public void setTextAndValue(String text, String value) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.imageView.setVisibility(4);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, int offset) {
        int i = offset;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(resId);
        int i2 = 3;
        if (i == 0) {
            ImageView imageView = r0.imageView;
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            imageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, i2 | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
            return;
        }
        imageView = r0.imageView;
        if (LocaleController.isRTL) {
            i2 = 5;
        }
        imageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, i2 | 48, LocaleController.isRTL ? 0.0f : 16.0f, (float) i, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
    }

    public void setMultiline(boolean value) {
        this.multiline = value;
        if (this.multiline) {
            this.textView.setSingleLine(false);
            return;
        }
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
    }
}
