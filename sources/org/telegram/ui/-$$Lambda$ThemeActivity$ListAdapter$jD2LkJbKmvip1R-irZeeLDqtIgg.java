package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$jD2LkJbKmvip1R-irZeeLDqtIgg implements OnItemClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ RecyclerListView f$1;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$jD2LkJbKmvip1R-irZeeLDqtIgg(ListAdapter listAdapter, RecyclerListView recyclerListView) {
        this.f$0 = listAdapter;
        this.f$1 = recyclerListView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$onCreateViewHolder$3$ThemeActivity$ListAdapter(this.f$1, view, i);
    }
}
