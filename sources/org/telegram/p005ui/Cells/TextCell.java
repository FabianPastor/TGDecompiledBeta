package org.telegram.p005ui.Cells;

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
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.TextCell */
public class TextCell extends FrameLayout {
    private ImageView imageView;
    private boolean needDivider;
    private SimpleTextView textView;
    private ImageView valueImageView;
    private SimpleTextView valueTextView;

    public TextCell(Context context) {
        int i = 3;
        super(context);
        this.textView = new SimpleTextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(16);
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.m9dp(48.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(width - AndroidUtilities.m9dp(23.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(20.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec((width - AndroidUtilities.m9dp(95.0f)) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(20.0f), NUM));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        setMeasuredDimension(width, (this.needDivider ? 1 : 0) + AndroidUtilities.m9dp(50.0f));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float f = 71.0f;
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getTextHeight()) / 2;
        int viewLeft = LocaleController.isRTL ? AndroidUtilities.m9dp(23.0f) : 0;
        this.valueTextView.layout(viewLeft, viewTop, this.valueTextView.getMeasuredWidth() + viewLeft, this.valueTextView.getMeasuredHeight() + viewTop);
        viewTop = (height - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            int measuredWidth = getMeasuredWidth() - this.textView.getMeasuredWidth();
            if (this.imageView.getVisibility() != 0) {
                f = 23.0f;
            }
            viewLeft = measuredWidth - AndroidUtilities.m9dp(f);
        } else {
            if (this.imageView.getVisibility() != 0) {
                f = 23.0f;
            }
            viewLeft = AndroidUtilities.m9dp(f);
        }
        this.textView.layout(viewLeft, viewTop, this.textView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
        if (this.imageView.getVisibility() == 0) {
            viewTop = AndroidUtilities.m9dp(5.0f);
            viewLeft = !LocaleController.isRTL ? AndroidUtilities.m9dp(21.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.m9dp(21.0f);
            this.imageView.layout(viewLeft, viewTop, this.imageView.getMeasuredWidth() + viewLeft, this.imageView.getMeasuredHeight() + viewTop);
        }
        if (this.valueImageView.getVisibility() == 0) {
            viewTop = (height - this.valueImageView.getMeasuredHeight()) / 2;
            viewLeft = LocaleController.isRTL ? AndroidUtilities.m9dp(23.0f) : (width - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.m9dp(23.0f);
            this.valueImageView.layout(viewLeft, viewTop, this.valueImageView.getMeasuredWidth() + viewLeft, this.valueImageView.getMeasuredHeight() + viewTop);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!this.needDivider);
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.m9dp(7.0f), 0, 0);
        this.needDivider = divider;
        if (!this.needDivider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        if (!this.needDivider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.m9dp(7.0f), 0, 0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        if (!this.needDivider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setTextAndValueDrawable(String text, Drawable drawable, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.m9dp(7.0f), 0, 0);
        this.needDivider = divider;
        if (!this.needDivider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    protected void onDraw(Canvas canvas) {
        float f = 68.0f;
        if (this.needDivider) {
            float f2;
            int dp;
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = (float) AndroidUtilities.m9dp(this.imageView.getVisibility() == 0 ? 68.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() != 0) {
                    f = 20.0f;
                }
                dp = AndroidUtilities.m9dp(f);
            } else {
                dp = 0;
            }
            canvas.drawLine(f2, measuredHeight, (float) (measuredWidth - dp), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
