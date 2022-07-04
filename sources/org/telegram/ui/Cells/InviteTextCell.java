package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class InviteTextCell extends FrameLayout {
    private ImageView imageView;
    private SimpleTextView textView;

    public InviteTextCell(Context context) {
        super(context);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(17);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.dp(72.0f);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        setMeasuredDimension(width, AndroidUtilities.dp(72.0f));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.textView.getTextHeight()) / 2;
        int viewLeft = AndroidUtilities.dp(!LocaleController.isRTL ? 71.0f : 24.0f);
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.imageView.getMeasuredHeight()) / 2;
        int viewLeft2 = LocaleController.isRTL == 0 ? AndroidUtilities.dp(20.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
        ImageView imageView2 = this.imageView;
        imageView2.layout(viewLeft2, viewTop2, imageView2.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight() + viewTop2);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextAndIcon(String text, int resId) {
        this.textView.setText(text);
        this.imageView.setImageResource(resId);
    }
}
