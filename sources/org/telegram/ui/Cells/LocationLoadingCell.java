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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class LocationLoadingCell extends FrameLayout {
    private ImageView imageView;
    private RadialProgressView progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);
        this.progressBar = new RadialProgressView(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(NUM);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 24.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.textView.setGravity(17);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextSize(1, 17.0f);
        this.textView.setText(LocaleController.getString("NoPlacesFound", NUM));
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
}
