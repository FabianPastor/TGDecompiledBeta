package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$0 */
final /* synthetic */ class LocationActivity$$Lambda$0 implements OnItemClickListener {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$0(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$LocationActivity(view, i);
    }
}
