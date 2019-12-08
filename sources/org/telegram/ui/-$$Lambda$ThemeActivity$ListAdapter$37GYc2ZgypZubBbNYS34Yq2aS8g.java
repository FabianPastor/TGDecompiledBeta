package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$37GYc2ZgypZubBbNYS34Yq2aS8g implements OnItemClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ ThemeAccentsListAdapter f$1;
    private final /* synthetic */ RecyclerListView f$2;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$37GYc2ZgypZubBbNYS34Yq2aS8g(ListAdapter listAdapter, ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
        this.f$2 = recyclerListView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(this.f$1, this.f$2, view, i);
    }
}
