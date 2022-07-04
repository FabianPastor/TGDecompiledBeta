package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LocationPoweredCell extends FrameLayout {
    private ImageView imageView;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView textView2;

    public LocationPoweredCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView3 = new TextView(context);
        this.textView = textView3;
        textView3.setTextSize(1, 16.0f);
        this.textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.textView.setText("Powered by");
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setImageResource(NUM);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("windowBackgroundWhiteGrayText3"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(35, -2));
        TextView textView4 = new TextView(context);
        this.textView2 = textView4;
        textView4.setTextSize(1, 16.0f);
        this.textView2.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.textView2.setText("Foursquare");
        linearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
