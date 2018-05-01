package org.telegram.ui.Cells;

import android.content.Context;
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

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(72.0f);
        this.textView.measure(MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
        this.imageView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(i, AndroidUtilities.dp(72.0f));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i4 -= i2;
        i3 -= i;
        z = (i4 - this.textView.getTextHeight()) / 2;
        i = AndroidUtilities.dp(LocaleController.isRTL == 0 ? NUM : NUM);
        this.textView.layout(i, z, this.textView.getMeasuredWidth() + i, this.textView.getMeasuredHeight() + z);
        i4 = (i4 - this.imageView.getMeasuredHeight()) / 2;
        z = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (i3 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
        this.imageView.layout(z, i4, this.imageView.getMeasuredWidth() + z, this.imageView.getMeasuredHeight() + i4);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextAndIcon(String str, int i) {
        this.textView.setText(str);
        this.imageView.setImageResource(i);
    }
}
