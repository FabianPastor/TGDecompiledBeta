package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
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
    private int leftPadding;
    private boolean needDivider;
    private SimpleTextView textView;
    private ImageView valueImageView;
    private SimpleTextView valueTextView;

    public TextCell(Context context) {
        this(context, 23);
    }

    public TextCell(Context context, int i) {
        super(context);
        this.leftPadding = i;
        this.textView = new SimpleTextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(16);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        this.valueTextView = new SimpleTextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(16);
        SimpleTextView simpleTextView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        }
        simpleTextView.setGravity(i2);
        addView(this.valueTextView);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
        addView(this.imageView);
        this.valueImageView = new ImageView(context);
        this.valueImageView.setScaleType(ScaleType.CENTER);
        addView(this.valueImageView);
        setFocusable(true);
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        i2 = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp((float) this.leftPadding), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((i - AndroidUtilities.dp((float) (this.leftPadding + 71))) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
        }
        setMeasuredDimension(i, AndroidUtilities.dp(50.0f) + this.needDivider);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i4 -= i2;
        i3 -= i;
        int textHeight = (i4 - this.valueTextView.getTextHeight()) / 2;
        i = LocaleController.isRTL ? AndroidUtilities.dp((float) this.leftPadding) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(i, textHeight, simpleTextView.getMeasuredWidth() + i, this.valueTextView.getMeasuredHeight() + textHeight);
        textHeight = (i4 - this.textView.getTextHeight()) / 2;
        float f = 71.0f;
        if (LocaleController.isRTL) {
            i = getMeasuredWidth() - this.textView.getMeasuredWidth();
            if (this.imageView.getVisibility() != 0) {
                f = (float) this.leftPadding;
            }
            i -= AndroidUtilities.dp(f);
        } else {
            if (this.imageView.getVisibility() != 0) {
                f = (float) this.leftPadding;
            }
            i = AndroidUtilities.dp(f);
        }
        simpleTextView = this.textView;
        simpleTextView.layout(i, textHeight, simpleTextView.getMeasuredWidth() + i, this.textView.getMeasuredHeight() + textHeight);
        if (this.imageView.getVisibility() == 0) {
            textHeight = AndroidUtilities.dp(5.0f);
            i = !LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (i3 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(21.0f);
            ImageView imageView = this.imageView;
            imageView.layout(i, textHeight, imageView.getMeasuredWidth() + i, this.imageView.getMeasuredHeight() + textHeight);
        }
        if (this.valueImageView.getVisibility() == 0) {
            i4 = (i4 - this.valueImageView.getMeasuredHeight()) / 2;
            textHeight = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (i3 - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView2 = this.valueImageView;
            imageView2.layout(textHeight, i4, imageView2.getMeasuredWidth() + textHeight, this.valueImageView.getMeasuredHeight() + i4);
        }
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setColors(String str, String str2) {
        this.textView.setTextColor(Theme.getColor(str2));
        this.textView.setTag(str2);
        if (str != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.imageView.setTag(str);
        }
    }

    public void setText(String str, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
    }

    public void setTextAndValueAndIcon(String str, String str2, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(i);
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
    }

    public void setTextAndValueDrawable(String str, Drawable drawable, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            float f;
            int dp;
            float f2 = 68.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = (float) AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 68.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() != 0) {
                    f2 = 20.0f;
                }
                dp = AndroidUtilities.dp(f2);
            } else {
                dp = 0;
            }
            canvas.drawLine(f, measuredHeight, (float) (measuredWidth - dp), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
