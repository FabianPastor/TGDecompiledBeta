package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeActivity;

public final /* synthetic */ class ThemeActivity$ListAdapter$$ExternalSyntheticLambda5 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ ThemeActivity.ListAdapter f$0;
    public final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;

    public /* synthetic */ ThemeActivity$ListAdapter$$ExternalSyntheticLambda5(ThemeActivity.ListAdapter listAdapter, ThemeActivity.ThemeAccentsListAdapter themeAccentsListAdapter) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$onCreateViewHolder$5(this.f$1, view, i);
    }
}
