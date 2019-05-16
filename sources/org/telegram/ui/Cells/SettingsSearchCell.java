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

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
            Rect bounds = getDrawable().getBounds();
            if (fontMetricsInt != null) {
                FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
                i = fontMetricsInt2.descent;
                int i3 = fontMetricsInt2.ascent;
                i3 += (i - i3) / 2;
                i2 = (bounds.bottom - bounds.top) / 2;
                fontMetricsInt.ascent = i3 - i2;
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = i3 + i2;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return bounds.right;
        }

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            i2 = fontMetricsInt.descent;
            canvas.translate(f, (float) (((i4 + i2) - ((i2 - fontMetricsInt.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2)));
            if (LocaleController.isRTL) {
                canvas.scale(-1.0f, 1.0f, (float) (drawable.getIntrinsicWidth() / 2), (float) (drawable.getIntrinsicHeight() / 2));
            }
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public SettingsSearchCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 33.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 8.0f, 10.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    public void setTextAndValueAndIcon(CharSequence charSequence, String[] strArr, int i, boolean z) {
        this.textView.setText(charSequence);
        LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
        float f = 16.0f;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        if (strArr != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (i2 != 0) {
                    spannableStringBuilder.append(" > ");
                    Drawable mutate = getContext().getResources().getDrawable(NUM).mutate();
                    mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), Mode.MULTIPLY));
                    spannableStringBuilder.setSpan(new VerticalImageSpan(mutate), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                }
                spannableStringBuilder.append(strArr[i2]);
            }
            this.valueTextView.setText(spannableStringBuilder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            layoutParams = (LayoutParams) this.valueTextView.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
            if (LocaleController.isRTL) {
                f = 71.0f;
            }
            layoutParams.rightMargin = AndroidUtilities.dp(f);
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
        setWillNotDraw(this.needDivider ^ 1);
    }

    public void setTextAndValue(CharSequence charSequence, String[] strArr, boolean z, boolean z2) {
        LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
        String str = " > ";
        SpannableStringBuilder spannableStringBuilder;
        int i;
        Drawable mutate;
        if (z) {
            this.valueTextView.setText(charSequence);
            spannableStringBuilder = new SpannableStringBuilder();
            for (i = 0; i < strArr.length; i++) {
                if (i != 0) {
                    spannableStringBuilder.append(str);
                    mutate = getContext().getResources().getDrawable(NUM).mutate();
                    mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), Mode.MULTIPLY));
                    spannableStringBuilder.setSpan(new VerticalImageSpan(mutate), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                }
                spannableStringBuilder.append(strArr[i]);
            }
            this.textView.setText(spannableStringBuilder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
        } else {
            this.textView.setText(charSequence);
            if (strArr != null) {
                spannableStringBuilder = new SpannableStringBuilder();
                for (i = 0; i < strArr.length; i++) {
                    if (i != 0) {
                        spannableStringBuilder.append(str);
                        mutate = getContext().getResources().getDrawable(NUM).mutate();
                        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
                        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), Mode.MULTIPLY));
                        spannableStringBuilder.setSpan(new VerticalImageSpan(mutate), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                    }
                    spannableStringBuilder.append(strArr[i]);
                }
                this.valueTextView.setText(spannableStringBuilder);
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
        LayoutParams layoutParams2 = (LayoutParams) this.valueTextView.getLayoutParams();
        int dp2 = AndroidUtilities.dp(16.0f);
        layoutParams2.rightMargin = dp2;
        layoutParams2.leftMargin = dp2;
        this.imageView.setVisibility(8);
        this.needDivider = z2;
        setWillNotDraw(this.needDivider ^ 1);
        this.left = 16;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) this.left), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) this.left) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
