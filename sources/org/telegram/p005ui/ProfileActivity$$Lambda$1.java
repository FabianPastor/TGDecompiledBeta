package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListenerExtended;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$1 */
final /* synthetic */ class ProfileActivity$$Lambda$1 implements OnItemClickListenerExtended {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$1(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$4$ProfileActivity(view, i, f, f2);
    }
}
