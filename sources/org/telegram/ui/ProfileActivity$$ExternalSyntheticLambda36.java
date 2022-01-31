package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda36 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda36(ProfileActivity profileActivity, long j) {
        this.f$0 = profileActivity;
        this.f$1 = j;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.lambda$createView$5(this.f$1, view, i, f, f2);
    }
}
