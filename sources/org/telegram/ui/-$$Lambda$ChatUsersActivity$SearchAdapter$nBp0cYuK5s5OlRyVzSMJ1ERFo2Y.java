package org.telegram.ui;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$SearchAdapter$nBp0cYuK5s5OlRyVzSMJ1ERFo2Y implements SearchAdapterHelperDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$SearchAdapter$nBp0cYuK5s5OlRyVzSMJ1ERFo2Y(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public /* synthetic */ boolean canApplySearchResults(int i) {
        return -CC.$default$canApplySearchResults(this, i);
    }

    public /* synthetic */ SparseArray<User> getExcludeUsers() {
        return -CC.$default$getExcludeUsers(this);
    }

    public final void onDataSetChanged(int i) {
        this.f$0.lambda$new$0$ChatUsersActivity$SearchAdapter(i);
    }

    public /* synthetic */ void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        -CC.$default$onSetHashtags(this, arrayList, hashMap);
    }
}
