package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class LocationLoadingCell extends FrameLayout {
    private ImageView imageView;
    private RadialProgressView progressBar;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public LocationLoadingCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider2);
        this.progressBar = radialProgressView;
        addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setImageResource(R.drawable.location_empty);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 24.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(getThemedColor("dialogEmptyText"));
        this.textView.setGravity(17);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextSize(1, 17.0f);
        this.textView.setText(LocaleController.getString("NoPlacesFound", R.string.NoPlacesFound));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 34.0f, 0.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(56.0f)) * 2.5f), NUM));
    }

    public void setLoading(boolean z) {
        int i = 0;
        this.progressBar.setVisibility(z ? 0 : 4);
        this.textView.setVisibility(z ? 4 : 0);
        ImageView imageView2 = this.imageView;
        if (z) {
            i = 4;
        }
        imageView2.setVisibility(i);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
