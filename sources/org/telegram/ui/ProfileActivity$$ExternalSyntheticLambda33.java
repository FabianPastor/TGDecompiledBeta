package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda33 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda33(ProfileActivity profileActivity, long j) {
        this.f$0 = profileActivity;
        this.f$1 = j;
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.lambda$createView$3(this.f$1, view, i, f, f2);
    }
}
