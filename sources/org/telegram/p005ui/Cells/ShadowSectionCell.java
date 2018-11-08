package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Cells.ShadowSectionCell */
public class ShadowSectionCell extends View {
    private int size = 12;

    public ShadowSectionCell(Context context) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, C0541R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
    }

    public void setSize(int value) {
        this.size = value;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp((float) this.size), NUM));
    }
}
