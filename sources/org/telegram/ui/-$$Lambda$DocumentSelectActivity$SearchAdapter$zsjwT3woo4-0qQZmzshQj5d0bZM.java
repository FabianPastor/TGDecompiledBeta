package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DocumentSelectActivity.SearchAdapter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DocumentSelectActivity$SearchAdapter$zsjwT3woo4-0qQZmzshQj5d0bZM implements Runnable {
    private final /* synthetic */ SearchAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$DocumentSelectActivity$SearchAdapter$zsjwT3woo4-0qQZmzshQj5d0bZM(SearchAdapter searchAdapter, String str, ArrayList arrayList) {
        this.f$0 = searchAdapter;
        this.f$1 = str;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2$DocumentSelectActivity$SearchAdapter(this.f$1, this.f$2);
    }
}
