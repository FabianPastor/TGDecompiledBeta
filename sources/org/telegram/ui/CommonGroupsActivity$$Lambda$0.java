package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class CommonGroupsActivity$$Lambda$0 implements OnItemClickListener {
    private final CommonGroupsActivity arg$1;

    CommonGroupsActivity$$Lambda$0(CommonGroupsActivity commonGroupsActivity) {
        this.arg$1 = commonGroupsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$CommonGroupsActivity(view, i);
    }
}
