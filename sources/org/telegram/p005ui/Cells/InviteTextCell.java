package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.InviteTextCell */
public class InviteTextCell extends FrameLayout {
    private ImageView imageView;
    private SimpleTextView textView;

    public InviteTextCell(Context context) {
        super(context);
        this.textView = new SimpleTextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(17);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        addView(this.imageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.m9dp(72.0f);
        this.textView.measure(MeasureSpec.makeMeasureSpec(width - AndroidUtilities.m9dp(95.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(20.0f), NUM));
        this.imageView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        setMeasuredDimension(width, AndroidUtilities.m9dp(72.0f));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.textView.getTextHeight()) / 2;
        int viewLeft = !LocaleController.isRTL ? AndroidUtilities.m9dp(71.0f) : AndroidUtilities.m9dp(24.0f);
        this.textView.layout(viewLeft, viewTop, this.textView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
        viewTop = (height - this.imageView.getMeasuredHeight()) / 2;
        viewLeft = !LocaleController.isRTL ? AndroidUtilities.m9dp(20.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.m9dp(20.0f);
        this.imageView.layout(viewLeft, viewTop, this.imageView.getMeasuredWidth() + viewLeft, this.imageView.getMeasuredHeight() + viewTop);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextAndIcon(String text, int resId) {
        this.textView.setText(text);
        this.imageView.setImageResource(resId);
    }
}
