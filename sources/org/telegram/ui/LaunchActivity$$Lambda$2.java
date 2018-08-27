package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class LaunchActivity$$Lambda$2 implements OnItemClickListener {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$2(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$onCreate$2$LaunchActivity(view, i);
    }
}
