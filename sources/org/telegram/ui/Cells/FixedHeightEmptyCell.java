package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class FixedHeightEmptyCell extends View {
    int heightInDp;

    public FixedHeightEmptyCell(Context context, int heightInDp2) {
        super(context);
        this.heightInDp = heightInDp2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.heightInDp), NUM));
    }
}
