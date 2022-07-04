package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda59 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ GroupCallActivity f$0;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda59(GroupCallActivity groupCallActivity) {
        this.f$0 = groupCallActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m3514lambda$new$12$orgtelegramuiGroupCallActivity(view, i, f, f2);
    }
}
