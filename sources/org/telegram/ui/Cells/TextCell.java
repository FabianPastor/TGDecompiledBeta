package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class TextCell extends FrameLayout {
    private ImageView imageView;
    private SimpleTextView textView;
    private ImageView valueImageView;
    private SimpleTextView valueTextView;

    public TextCell(Context context) {
        super(context);
        this.textView = new SimpleTextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(16);
        int i = 3;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        this.valueTextView = new SimpleTextView(context);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        this.valueTextView.setTextSize(16);
        SimpleTextView simpleTextView = this.valueTextView;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        simpleTextView.setGravity(i);
        addView(this.valueTextView);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        addView(this.imageView);
        this.valueImageView = new ImageView(context);
        this.valueImageView.setScaleType(ScaleType.CENTER);
        addView(this.valueImageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    public ImageView getValueImageView() {
        return this.valueImageView;
    }

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.valueImageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(i, AndroidUtilities.dp(48.0f));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i4 -= i2;
        i3 -= i;
        z = (i4 - this.valueTextView.getTextHeight()) / 2;
        i = LocaleController.isRTL != 0 ? AndroidUtilities.dp(24.0f) : 0;
        this.valueTextView.layout(i, z, this.valueTextView.getMeasuredWidth() + i, this.valueTextView.getMeasuredHeight() + z);
        z = (i4 - this.textView.getTextHeight()) / 2;
        i = LocaleController.isRTL == 0 ? AndroidUtilities.dp(NUM) : AndroidUtilities.dp(24.0f);
        this.textView.layout(i, z, this.textView.getMeasuredWidth() + i, this.textView.getMeasuredHeight() + z);
        z = AndroidUtilities.dp(true);
        i = LocaleController.isRTL == 0 ? AndroidUtilities.dp(16.0f) : (i3 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
        this.imageView.layout(i, z, this.imageView.getMeasuredWidth() + i, this.imageView.getMeasuredHeight() + z);
        i4 = (i4 - this.valueImageView.getMeasuredHeight()) / 2;
        z = LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : (i3 - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(24.0f);
        this.valueImageView.layout(z, i4, this.valueImageView.getMeasuredWidth() + z, this.valueImageView.getMeasuredHeight() + i4);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(String str) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.imageView.setVisibility(4);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
    }

    public void setTextAndIcon(String str, int i) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
    }

    public void setTextAndValue(String str, String str2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.valueTextView.setVisibility(null);
        this.imageView.setVisibility(4);
        this.valueImageView.setVisibility(4);
    }

    public void setTextAndValueAndIcon(String str, String str2, int i) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(4);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(i);
    }

    public void setTextAndValueDrawable(String str, Drawable drawable) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(4);
        this.imageView.setVisibility(4);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
    }
}
