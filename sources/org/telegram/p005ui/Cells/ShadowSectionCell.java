package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.ShadowSectionCell */
public class ShadowSectionCell extends View {
    private int size;

    public ShadowSectionCell(Context context) {
        this(context, 12);
    }

    public ShadowSectionCell(Context context, int s) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.size = s;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp((float) this.size), NUM));
    }
}
