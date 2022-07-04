package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeActivity;

public final /* synthetic */ class ThemeActivity$ListAdapter$$ExternalSyntheticLambda4 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ThemeActivity.ListAdapter f$0;
    public final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;
    public final /* synthetic */ RecyclerListView f$2;

    public /* synthetic */ ThemeActivity$ListAdapter$$ExternalSyntheticLambda4(ThemeActivity.ListAdapter listAdapter, ThemeActivity.ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
        this.f$2 = recyclerListView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m4663x25CLASSNAMEc2(this.f$1, this.f$2, view, i);
    }
}
