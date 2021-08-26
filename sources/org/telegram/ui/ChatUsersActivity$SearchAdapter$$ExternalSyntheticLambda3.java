package org.telegram.ui;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.ui.ChatUsersActivity;

public final /* synthetic */ class ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatUsersActivity.SearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ SparseArray f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3(ChatUsersActivity.SearchAdapter searchAdapter, ArrayList arrayList, SparseArray sparseArray, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = searchAdapter;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
