package org.telegram.ui.Adapters;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhonebookSearchAdapter$5E7mSLYtYVP-MgRNvar_lnHW5RXo implements Runnable {
    private final /* synthetic */ PhonebookSearchAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$PhonebookSearchAdapter$5E7mSLYtYVP-MgRNvar_lnHW5RXo(PhonebookSearchAdapter phonebookSearchAdapter, String str, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = phonebookSearchAdapter;
        this.f$1 = str;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2$PhonebookSearchAdapter(this.f$1, this.f$2, this.f$3);
    }
}
