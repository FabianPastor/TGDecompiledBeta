package org.telegram.ui;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$SearchAdapter$8QJzltWXH8TVGPMYFDo_tdWaX_A implements Runnable {
    private final /* synthetic */ SearchAdapter f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ SparseArray f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$SearchAdapter$8QJzltWXH8TVGPMYFDo_tdWaX_A(SearchAdapter searchAdapter, ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = searchAdapter;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$4$ChatUsersActivity$SearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
