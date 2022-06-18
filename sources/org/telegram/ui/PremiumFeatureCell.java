package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PremiumPreviewFragment;

public class PremiumFeatureCell extends FrameLayout {
    public PremiumPreviewFragment.PremiumFeatureData data;
    private final TextView description;
    boolean drawDivider;
    public ImageView imageView;
    private final TextView title;

    public PremiumFeatureCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 15.0f);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        this.description = textView2;
        textView2.setTextSize(1, 13.0f);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 2, 0, 0));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 62.0f, 8.0f, 48.0f, 9.0f));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(this.imageView, LayoutHelper.createFrame(28, 28.0f, 0, 18.0f, 12.0f, 0.0f, 0.0f));
        ImageView imageView3 = new ImageView(context);
        imageView3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView3.setImageResource(NUM);
        imageView3.setColorFilter(Theme.getColor("switchTrack"));
        addView(imageView3, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 18.0f, 0.0f));
    }

    public void setData(PremiumPreviewFragment.PremiumFeatureData premiumFeatureData, boolean z) {
        this.data = premiumFeatureData;
        this.title.setText(premiumFeatureData.title);
        this.description.setText(premiumFeatureData.description);
        this.imageView.setImageResource(premiumFeatureData.icon);
        this.drawDivider = z;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawDivider) {
            canvas.drawRect((float) AndroidUtilities.dp(62.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }
}
