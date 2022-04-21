package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;

public final /* synthetic */ class ViewPagerFixed$TabsView$$ExternalSyntheticLambda1 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ViewPagerFixed.TabsView f$0;

    public /* synthetic */ ViewPagerFixed$TabsView$$ExternalSyntheticLambda1(ViewPagerFixed.TabsView tabsView) {
        this.f$0 = tabsView;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m4524lambda$new$0$orgtelegramuiComponentsViewPagerFixed$TabsView(view, i, f, f2);
    }
}
