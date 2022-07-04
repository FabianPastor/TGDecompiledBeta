package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ThemePreviewActivity$$ExternalSyntheticLambda17 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ ThemePreviewActivity$$ExternalSyntheticLambda17(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m4678lambda$createView$4$orgtelegramuiThemePreviewActivity(view, i, f, f2);
    }
}
