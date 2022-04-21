package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class FiltersSetupActivity$$ExternalSyntheticLambda1 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ FiltersSetupActivity f$0;

    public /* synthetic */ FiltersSetupActivity$$ExternalSyntheticLambda1(FiltersSetupActivity filtersSetupActivity) {
        this.f$0 = filtersSetupActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m2175lambda$createView$1$orgtelegramuiFiltersSetupActivity(view, i, f, f2);
    }
}
