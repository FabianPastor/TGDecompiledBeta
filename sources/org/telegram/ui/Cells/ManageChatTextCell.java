package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        i2 = AndroidUtilities.dp(NUM);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
        setMeasuredDimension(i, AndroidUtilities.dp(NUM) + this.divider);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i4 -= i2;
        i3 -= i;
        z = (i4 - this.valueTextView.getTextHeight()) / 2;
        i = LocaleController.isRTL != 0 ? AndroidUtilities.dp(24.0f) : 0;
        this.valueTextView.layout(i, z, this.valueTextView.getMeasuredWidth() + i, this.valueTextView.getMeasuredHeight() + z);
        i4 = (i4 - this.textView.getTextHeight()) / 2;
        z = !LocaleController.isRTL ? AndroidUtilities.dp(true) : AndroidUtilities.dp(24.0f);
        this.textView.layout(z, i4, this.textView.getMeasuredWidth() + z, this.textView.getMeasuredHeight() + i4);
        z = AndroidUtilities.dp(true);
        i = LocaleController.isRTL == 0 ? AndroidUtilities.dp(16.0f) : (i3 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
        this.imageView.layout(i, z, this.imageView.getMeasuredWidth() + i, this.imageView.getMeasuredHeight() + z);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(String str, String str2, int i, boolean z) {
        this.textView.setText(str);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(i);
        this.divider = z;
        setWillNotDraw(this.divider ^ 1);
    }

    protected void onDraw(Canvas canvas) {
        if (this.divider) {
            canvas.drawLine((float) AndroidUtilities.dp(71.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
