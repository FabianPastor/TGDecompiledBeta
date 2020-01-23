package org.telegram.ui;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$5LwIoMVltQ3Jr8T-oc_eUIaWa0Q implements SearchAdapterHelperDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$5LwIoMVltQ3Jr8T-oc_eUIaWa0Q(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public /* synthetic */ boolean canApplySearchResults(int i) {
        return -CC.$default$canApplySearchResults(this, i);
    }

    public /* synthetic */ SparseArray<User> getExcludeUsers() {
        return -CC.$default$getExcludeUsers(this);
    }

    public final void onDataSetChanged(int i) {
        this.f$0.lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter(i);
    }

    public /* synthetic */ void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        -CC.$default$onSetHashtags(this, arrayList, hashMap);
    }
}
