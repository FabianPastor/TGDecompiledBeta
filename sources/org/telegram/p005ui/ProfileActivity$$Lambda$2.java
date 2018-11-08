package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$2 */
final /* synthetic */ class ProfileActivity$$Lambda$2 implements OnItemClickListener {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$2(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$5$ProfileActivity(view, i);
    }
}
