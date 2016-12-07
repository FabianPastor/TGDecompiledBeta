package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.C;
import org.telegram.ui.Components.LayoutHelper;

public class LoadingCell extends FrameLayout {
    public LoadingCell(Context context) {
        super(context);
        addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), C.ENCODING_PCM_32BIT));
    }
}
