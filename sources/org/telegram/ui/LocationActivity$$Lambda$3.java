package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class LocationActivity$$Lambda$3 implements OnItemClickListener {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$3(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$6$LocationActivity(view, i);
    }
}
