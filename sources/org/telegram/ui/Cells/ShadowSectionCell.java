package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.ui.ActionBar.Theme;

public class ShadowSectionCell extends View {
    private int size = 12;

    public ShadowSectionCell(Context context) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
    }

    public void setSize(int i) {
        this.size = i;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.size), NUM));
    }
}
