package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ThemeActivity$$ExternalSyntheticLambda1 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ThemeActivity f$0;

    public /* synthetic */ ThemeActivity$$ExternalSyntheticLambda1(ThemeActivity themeActivity) {
        this.f$0 = themeActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m4656lambda$createView$5$orgtelegramuiThemeActivity(view, i, f, f2);
    }
}
