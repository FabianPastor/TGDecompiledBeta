package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadialProgressView;

/* renamed from: org.telegram.ui.Cells.LoadingCell */
public class LoadingCell extends FrameLayout {
    private RadialProgressView progressBar;

    public LoadingCell(Context context) {
        super(context);
        this.progressBar = new RadialProgressView(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(54.0f), NUM));
    }
}
