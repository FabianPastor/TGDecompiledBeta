package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class LogoutActivity$$ExternalSyntheticLambda1 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ LogoutActivity f$0;

    public /* synthetic */ LogoutActivity$$ExternalSyntheticLambda1(LogoutActivity logoutActivity) {
        this.f$0 = logoutActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m3918lambda$createView$0$orgtelegramuiLogoutActivity(view, i, f, f2);
    }
}
