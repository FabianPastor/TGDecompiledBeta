package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class TextDetailSettingsCell extends FrameLayout {
    private ImageView imageView;
    private boolean multiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailSettingsCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 35.0f, 21.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(52, 52.0f, (!LocaleController.isRTL ? 3 : i) | 48, 8.0f, 6.0f, 8.0f, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (!this.multiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public void setMultilineDetail(boolean z) {
        this.multiline = z;
        if (z) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
            return;
        }
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
    }

    public void setTextAndValue(String str, CharSequence charSequence, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(charSequence);
        this.needDivider = z;
        this.imageView.setVisibility(8);
        setWillNotDraw(!z);
    }

    public void setTextAndValueAndIcon(String str, CharSequence charSequence, int i, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setText(charSequence);
        this.imageView.setImageResource(i);
        this.imageView.setVisibility(0);
        this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(50.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(50.0f) : 0, 0);
        this.valueTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(50.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(50.0f) : 0, this.multiline ? AndroidUtilities.dp(12.0f) : 0);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setValue(CharSequence charSequence) {
        this.valueTextView.setText(charSequence);
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.textView.invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float dp;
        int i;
        if (!this.needDivider || Theme.dividerPaint == null) {
            return;
        }
        float f = 71.0f;
        if (LocaleController.isRTL) {
            dp = 0.0f;
        } else {
            dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 71.0f : 20.0f);
        }
        float measuredHeight = getMeasuredHeight() - 1;
        int measuredWidth = getMeasuredWidth();
        if (LocaleController.isRTL) {
            if (this.imageView.getVisibility() != 0) {
                f = 20.0f;
            }
            i = AndroidUtilities.dp(f);
        } else {
            i = 0;
        }
        canvas.drawLine(dp, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
    }
}
