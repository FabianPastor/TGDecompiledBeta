package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ProfileActivity$$Lambda$3 implements OnItemLongClickListener {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$3(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$8$ProfileActivity(view, i);
    }
}
