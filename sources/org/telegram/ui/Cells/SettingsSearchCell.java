package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

    public static class VerticalImageSpan extends ImageSpan {
        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            Rect rect = getDrawable().getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + (fontHeight / 2);
                fontMetricsInt.ascent = centerY - (drHeight / 2);
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = (drHeight / 2) + centerY;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Canvas canvas2 = canvas;
            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            float f = x;
            canvas.translate(f, (float) (((y + fmPaint.descent) - ((fmPaint.descent - fmPaint.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2)));
            if (LocaleController.isRTL) {
                canvas.scale(-1.0f, 1.0f, (float) (drawable.getIntrinsicWidth() / 2), (float) (drawable.getIntrinsicHeight() / 2));
            }
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public SettingsSearchCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 33.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 8.0f, 10.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setTextAndValueAndIcon(CharSequence text, String[] value, int icon, boolean divider) {
        String[] strArr = value;
        int i = icon;
        boolean z = divider;
        this.textView.setText(text);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        if (strArr != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int a = 0; a < strArr.length; a++) {
                if (a != 0) {
                    builder.append(" > ");
                    Drawable drawable = getContext().getResources().getDrawable(NUM).mutate();
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), PorterDuff.Mode.MULTIPLY));
                    builder.setSpan(new VerticalImageSpan(drawable), builder.length() - 2, builder.length() - 1, 33);
                }
                builder.append(strArr[a]);
            }
            this.valueTextView.setText(builder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.valueTextView.getLayoutParams();
            layoutParams2.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
            layoutParams2.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        } else {
            layoutParams.topMargin = AndroidUtilities.dp(21.0f);
            this.valueTextView.setVisibility(8);
        }
        if (i != 0) {
            this.imageView.setImageResource(i);
            this.imageView.setVisibility(0);
        } else {
            this.imageView.setVisibility(8);
        }
        this.left = 69;
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndValue(CharSequence text, String[] value, boolean faq, boolean divider) {
        CharSequence charSequence = text;
        String[] strArr = value;
        boolean z = divider;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        if (faq) {
            this.valueTextView.setText(charSequence);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int a = 0; a < strArr.length; a++) {
                if (a != 0) {
                    builder.append(" > ");
                    Drawable drawable = getContext().getResources().getDrawable(NUM).mutate();
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
                    builder.setSpan(new VerticalImageSpan(drawable), builder.length() - 2, builder.length() - 1, 33);
                }
                builder.append(strArr[a]);
            }
            this.textView.setText(builder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
        } else {
            this.textView.setText(charSequence);
            if (strArr != null) {
                SpannableStringBuilder builder2 = new SpannableStringBuilder();
                for (int a2 = 0; a2 < strArr.length; a2++) {
                    if (a2 != 0) {
                        builder2.append(" > ");
                        Drawable drawable2 = getContext().getResources().getDrawable(NUM).mutate();
                        drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), PorterDuff.Mode.MULTIPLY));
                        builder2.setSpan(new VerticalImageSpan(drawable2), builder2.length() - 2, builder2.length() - 1, 33);
                    }
                    builder2.append(strArr[a2]);
                }
                this.valueTextView.setText(builder2);
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
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.valueTextView.getLayoutParams();
        int dp2 = AndroidUtilities.dp(16.0f);
        layoutParams2.rightMargin = dp2;
        layoutParams2.leftMargin = dp2;
        this.imageView.setVisibility(8);
        this.needDivider = z;
        setWillNotDraw(!z);
        this.left = 16;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) this.left), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) this.left) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
