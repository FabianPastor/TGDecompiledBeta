package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadialProgressView;

/* renamed from: org.telegram.ui.Cells.LocationLoadingCell */
public class LocationLoadingCell extends FrameLayout {
    private RadialProgressView progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);
        this.progressBar = new RadialProgressView(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setText(LocaleController.getString("NoResult", C0541R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.m10dp(56.0f)) * 2.5f), NUM));
    }

    public void setLoading(boolean value) {
        int i;
        int i2 = 4;
        RadialProgressView radialProgressView = this.progressBar;
        if (value) {
            i = 0;
        } else {
            i = 4;
        }
        radialProgressView.setVisibility(i);
        TextView textView = this.textView;
        if (!value) {
            i2 = 0;
        }
        textView.setVisibility(i2);
    }
}
