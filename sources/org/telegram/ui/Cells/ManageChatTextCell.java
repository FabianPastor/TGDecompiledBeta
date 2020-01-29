package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class ManageChatTextCell extends FrameLayout {
    private boolean divider;
    private ImageView imageView;
    private SimpleTextView textView;
    private SimpleTextView valueTextView;

    public ManageChatTextCell(Context context) {
        super(context);
        this.textView = new SimpleTextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(16);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        this.valueTextView = new SimpleTextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
        this.valueTextView.setTextSize(16);
        this.valueTextView.setGravity(LocaleController.isRTL ? 3 : i);
        addView(this.valueTextView);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(size, AndroidUtilities.dp(56.0f) + (this.divider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int i6 = i3 - i;
        int textHeight = (i5 - this.valueTextView.getTextHeight()) / 2;
        int dp = LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.valueTextView.getMeasuredHeight() + textHeight);
        int textHeight2 = (i5 - this.textView.getTextHeight()) / 2;
        int dp2 = !LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : AndroidUtilities.dp(24.0f);
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(dp2, textHeight2, simpleTextView2.getMeasuredWidth() + dp2, this.textView.getMeasuredHeight() + textHeight2);
        int dp3 = AndroidUtilities.dp(9.0f);
        int dp4 = !LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(21.0f);
        ImageView imageView2 = this.imageView;
        imageView2.layout(dp4, dp3, imageView2.getMeasuredWidth() + dp4, this.imageView.getMeasuredHeight() + dp3);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setColors(String str, String str2) {
        this.textView.setTextColor(Theme.getColor(str2));
        this.textView.setTag(str2);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
        this.imageView.setTag(str);
    }

    public void setText(String str, String str2, int i, boolean z) {
        this.textView.setText(str);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.imageView.setPadding(0, AndroidUtilities.dp(5.0f), 0, 0);
        this.imageView.setImageResource(i);
        this.divider = z;
        setWillNotDraw(!this.divider);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.divider) {
            canvas.drawLine((float) AndroidUtilities.dp(71.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
