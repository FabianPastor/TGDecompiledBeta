package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class CreationTextCell extends FrameLayout {
    boolean divider;
    private ImageView imageView;
    public int startPadding;
    private SimpleTextView textView;

    public CreationTextCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public CreationTextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.startPadding = 70;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2", resourcesProvider));
        this.textView.setTag("windowBackgroundWhiteBlueText2");
        addView(this.textView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int dp = AndroidUtilities.dp(48.0f);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        setMeasuredDimension(width, AndroidUtilities.dp(50.0f));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewLeft;
        int width = right - left;
        int viewTop = ((bottom - top) - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            viewLeft = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? (float) this.startPadding : 25.0f);
        } else {
            viewLeft = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? (float) this.startPadding : 25.0f);
        }
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
        int viewLeft2 = !LocaleController.isRTL ? (AndroidUtilities.dp((float) this.startPadding) - this.imageView.getMeasuredWidth()) / 2 : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(25.0f);
        ImageView imageView2 = this.imageView;
        imageView2.layout(viewLeft2, 0, imageView2.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.divider) {
            canvas.drawLine((float) AndroidUtilities.dp((float) this.startPadding), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() + AndroidUtilities.dp(23.0f)), (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    public void setTextAndIcon(String text, Drawable icon, boolean divider2) {
        this.textView.setText(text);
        this.imageView.setImageDrawable(icon);
        this.divider = divider2;
    }
}
