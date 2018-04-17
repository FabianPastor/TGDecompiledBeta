package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextSettingsCell extends FrameLayout {
    private boolean needDivider;
    private TextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingsCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueImageView = new ImageView(context);
        this.valueImageView.setScaleType(ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        View view = this.valueImageView;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 16, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(48.0f) + this.needDivider);
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int width2 = availableWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            width = (availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        } else {
            width = availableWidth;
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextValueColor(int color) {
        this.valueTextView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.needDivider = divider;
        setWillNotDraw(divider ^ 1);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueImageView.setVisibility(4);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(divider ^ 1);
        requestLayout();
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        if (resId != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(resId);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(divider ^ 1);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        setEnabled(value);
        float f = 0.5f;
        if (animators != null) {
            TextView textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            if (this.valueTextView.getVisibility() == 0) {
                textView = this.valueTextView;
                str = "alpha";
                fArr = new float[1];
                fArr[0] = value ? 1.0f : 0.5f;
                animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            }
            if (this.valueImageView.getVisibility() == 0) {
                ImageView imageView = this.valueImageView;
                str = "alpha";
                float[] fArr2 = new float[1];
                if (value) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animators.add(ObjectAnimator.ofFloat(imageView, str, fArr2));
                return;
            }
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha(value ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            imageView = this.valueImageView;
            if (value) {
                f = 1.0f;
            }
            imageView.setAlpha(f);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
