package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class FilterTabsView$$ExternalSyntheticLambda1 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ FilterTabsView f$0;

    public /* synthetic */ FilterTabsView$$ExternalSyntheticLambda1(FilterTabsView filterTabsView) {
        this.f$0 = filterTabsView;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.lambda$new$0(view, i, f, f2);
    }
}
