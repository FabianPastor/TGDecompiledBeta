package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;

public class ShadowSectionCell extends View {
    private int size;

    public ShadowSectionCell(Context context) {
        this(context, 12, (Theme.ResourcesProvider) null);
    }

    public ShadowSectionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 12, resourcesProvider);
    }

    public ShadowSectionCell(Context context, int s) {
        this(context, s, (Theme.ResourcesProvider) null);
    }

    public ShadowSectionCell(Context context, int s, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, Theme.getColor("windowBackgroundGrayShadow", resourcesProvider)));
        this.size = s;
    }

    public ShadowSectionCell(Context context, int s, int backgroundColor) {
        this(context, s, backgroundColor, (Theme.ResourcesProvider) null);
    }

    public ShadowSectionCell(Context context, int s, int backgroundColor, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(backgroundColor), Theme.getThemedDrawable(context, NUM, Theme.getColor("windowBackgroundGrayShadow", resourcesProvider)), 0, 0);
        combinedDrawable.setFullsize(true);
        setBackgroundDrawable(combinedDrawable);
        this.size = s;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.size), NUM));
    }
}
