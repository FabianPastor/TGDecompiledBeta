package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ProfileActivity$$Lambda$2 implements OnItemClickListener {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$2(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$5$ProfileActivity(view, i);
    }
}
