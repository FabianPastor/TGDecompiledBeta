package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ImageSpan;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class SettingsSearchCell extends FrameLayout {
    private ImageView imageView;
    private int left;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public class VerticalImageSpan extends ImageSpan {
        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fontMetricsInt) {
            Rect rect = getDrawable().getBounds();
            if (fontMetricsInt != null) {
                FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + ((fmPaint.descent - fmPaint.ascent) / 2);
                fontMetricsInt.ascent = centerY - (drHeight / 2);
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = (drHeight / 2) + centerY;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            FontMetricsInt fmPaint = paint.getFontMetricsInt();
            canvas.translate(x, (float) (((fmPaint.descent + y) - ((fmPaint.descent - fmPaint.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2)));
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public SettingsSearchCell(Context context) {
        int i;
        int i2;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        TextView textView2 = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView2.setGravity(i2);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 16.0f : 71.0f, 33.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
        ImageView imageView = this.imageView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(imageView, LayoutHelper.createFrame(48, 48.0f, i, 10.0f, 8.0f, 10.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), NUM));
    }

    public void setTextAndValueAndIcon(CharSequence text, String[] value, int icon, boolean divider) {
        this.textView.setText(text);
        LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        if (value != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int a = 0; a < value.length; a++) {
                if (a != 0) {
                    builder.append(" > ");
                    Drawable drawable = getContext().getResources().getDrawable(NUM).mutate();
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), Mode.MULTIPLY));
                    builder.setSpan(new VerticalImageSpan(drawable), builder.length() - 2, builder.length() - 1, 33);
                }
                builder.append(value[a]);
            }
            this.valueTextView.setText(builder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            layoutParams = (LayoutParams) this.valueTextView.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        } else {
            layoutParams.topMargin = AndroidUtilities.dp(21.0f);
            this.valueTextView.setVisibility(8);
        }
        if (icon != 0) {
            this.imageView.setImageResource(icon);
            this.imageView.setVisibility(0);
        } else {
            this.imageView.setVisibility(8);
        }
        this.left = 69;
        this.needDivider = divider;
        setWillNotDraw(!this.needDivider);
    }

    public void setTextAndValue(CharSequence text, String[] value, boolean faq, boolean divider) {
        LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
        SpannableStringBuilder builder;
        int a;
        Drawable drawable;
        if (faq) {
            this.valueTextView.setText(text);
            builder = new SpannableStringBuilder();
            for (a = 0; a < value.length; a++) {
                if (a != 0) {
                    builder.append(" > ");
                    drawable = getContext().getResources().getDrawable(NUM).mutate();
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), Mode.MULTIPLY));
                    builder.setSpan(new VerticalImageSpan(drawable), builder.length() - 2, builder.length() - 1, 33);
                }
                builder.append(value[a]);
            }
            this.textView.setText(builder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
        } else {
            this.textView.setText(text);
            if (value != null) {
                builder = new SpannableStringBuilder();
                for (a = 0; a < value.length; a++) {
                    if (a != 0) {
                        builder.append(" > ");
                        drawable = getContext().getResources().getDrawable(NUM).mutate();
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), Mode.MULTIPLY));
                        builder.setSpan(new VerticalImageSpan(drawable), builder.length() - 2, builder.length() - 1, 33);
                    }
                    builder.append(value[a]);
                }
                this.valueTextView.setText(builder);
                this.valueTextView.setVisibility(0);
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            } else {
                layoutParams.topMargin = AndroidUtilities.dp(21.0f);
                this.valueTextView.setVisibility(8);
            }
        }
        int dp = AndroidUtilities.dp(16.0f);
        layoutParams.rightMargin = dp;
        layoutParams.leftMargin = dp;
        layoutParams = (LayoutParams) this.valueTextView.getLayoutParams();
        dp = AndroidUtilities.dp(16.0f);
        layoutParams.rightMargin = dp;
        layoutParams.leftMargin = dp;
        this.imageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!this.needDivider);
        this.left = 16;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) this.left), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) this.left) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
