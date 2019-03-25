package org.telegram.ui;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView.IntReturnCallback;

final /* synthetic */ class SettingsActivity$SearchAdapter$SearchResult$$Lambda$0 implements IntReturnCallback {
    private final SearchResult arg$1;
    private final BaseFragment arg$2;

    SettingsActivity$SearchAdapter$SearchResult$$Lambda$0(SearchResult searchResult, BaseFragment baseFragment) {
        this.arg$1 = searchResult;
        this.arg$2 = baseFragment;
    }

    public int run() {
        return this.arg$1.lambda$open$0$SettingsActivity$SearchAdapter$SearchResult(this.arg$2);
    }
}
