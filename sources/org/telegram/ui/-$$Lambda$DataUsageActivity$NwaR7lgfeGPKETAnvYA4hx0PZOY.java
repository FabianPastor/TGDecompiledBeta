package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataUsageActivity$NwaR7lgfeGPKETAnvYA4hx0PZOY implements OnItemClickListener {
    private final /* synthetic */ DataUsageActivity f$0;
    private final /* synthetic */ RecyclerListView f$1;

    public /* synthetic */ -$$Lambda$DataUsageActivity$NwaR7lgfeGPKETAnvYA4hx0PZOY(DataUsageActivity dataUsageActivity, RecyclerListView recyclerListView) {
        this.f$0 = dataUsageActivity;
        this.f$1 = recyclerListView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$2$DataUsageActivity(this.f$1, view, i);
    }
}
