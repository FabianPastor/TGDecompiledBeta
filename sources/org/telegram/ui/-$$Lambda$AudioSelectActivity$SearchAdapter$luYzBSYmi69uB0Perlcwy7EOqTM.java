package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.AudioSelectActivity.SearchAdapter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioSelectActivity$SearchAdapter$luYzBSYmi69uB0Perlcwy7EOqTM implements Runnable {
    private final /* synthetic */ SearchAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$AudioSelectActivity$SearchAdapter$luYzBSYmi69uB0Perlcwy7EOqTM(SearchAdapter searchAdapter, String str, ArrayList arrayList) {
        this.f$0 = searchAdapter;
        this.f$1 = str;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2$AudioSelectActivity$SearchAdapter(this.f$1, this.f$2);
    }
}
