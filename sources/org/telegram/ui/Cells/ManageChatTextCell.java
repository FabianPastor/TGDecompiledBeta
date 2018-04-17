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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.dp(NUM);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        setMeasuredDimension(width, AndroidUtilities.dp(56.0f) + this.divider);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getTextHeight()) / 2;
        int viewLeft = LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0;
        this.valueTextView.layout(viewLeft, viewTop, this.valueTextView.getMeasuredWidth() + viewLeft, this.valueTextView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        viewTop = !LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : AndroidUtilities.dp(24.0f);
        this.textView.layout(viewTop, viewTop2, this.textView.getMeasuredWidth() + viewTop, this.textView.getMeasuredHeight() + viewTop2);
        viewLeft = AndroidUtilities.dp(NUM);
        viewTop = !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
        this.imageView.layout(viewTop, viewLeft, this.imageView.getMeasuredWidth() + viewTop, this.imageView.getMeasuredHeight() + viewLeft);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(String text, String value, int resId, boolean needDivider) {
        this.textView.setText(text);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(resId);
        this.divider = needDivider;
        setWillNotDraw(this.divider ^ 1);
    }

    protected void onDraw(Canvas canvas) {
        if (this.divider) {
            canvas.drawLine((float) AndroidUtilities.dp(71.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
