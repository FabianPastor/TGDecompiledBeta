package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$cfae1HOWszm_UVN2RQ8ct-ooT1E implements OnItemLongClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ ThemeAccentsListAdapter f$1;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$cfae1HOWszm_UVN2RQ8ct-ooT1E(ListAdapter listAdapter, ThemeAccentsListAdapter themeAccentsListAdapter) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$onCreateViewHolder$5$ThemeActivity$ListAdapter(this.f$1, view, i);
    }
}
