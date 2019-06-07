package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$p-mG9tNYo33F1pFcBkO4OWkissQ implements Runnable {
    private final /* synthetic */ SearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$p-mG9tNYo33F1pFcBkO4OWkissQ(SearchAdapter searchAdapter, int i, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$0$ThemeEditorView$EditorAlert$SearchAdapter(this.f$1, this.f$2, this.f$3);
    }
}
