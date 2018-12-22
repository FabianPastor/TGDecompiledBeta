package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.DataUsageActivity$$Lambda$0 */
final /* synthetic */ class DataUsageActivity$$Lambda$0 implements OnItemClickListener {
    private final DataUsageActivity arg$1;

    DataUsageActivity$$Lambda$0(DataUsageActivity dataUsageActivity) {
        this.arg$1 = dataUsageActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$DataUsageActivity(view, i);
    }
}
