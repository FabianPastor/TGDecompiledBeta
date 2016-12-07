package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.Components.LayoutHelper;

public class TextSettingsCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;
    private TextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingsCell(Context context) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-13660983);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        textView.setGravity(i2 | 16);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueImageView = new ImageView(context);
        this.valueImageView.setScaleType(ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        view = this.valueImageView;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3 | 16, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int width = availableWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            width = (availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        } else {
            width = availableWidth;
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
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
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueImageView.setVisibility(4);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
        requestLayout();
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        boolean z = false;
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        if (resId != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(resId);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = divider;
        if (!divider) {
            z = true;
        }
        setWillNotDraw(z);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (animators != null) {
            TextView textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = value ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            if (this.valueTextView.getVisibility() == 0) {
                textView = this.valueTextView;
                str = "alpha";
                fArr = new float[1];
                fArr[0] = value ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.5f;
                animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            }
            if (this.valueImageView.getVisibility() == 0) {
                ImageView imageView = this.valueImageView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                if (!value) {
                    f = 0.5f;
                }
                fArr2[0] = f;
                animators.add(ObjectAnimator.ofFloat(imageView, str2, fArr2));
                return;
            }
            return;
        }
        this.textView.setAlpha(value ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha(value ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            imageView = this.valueImageView;
            if (!value) {
                f = 0.5f;
            }
            imageView.setAlpha(f);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }
}
