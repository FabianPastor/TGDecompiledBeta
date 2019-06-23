package org.telegram.ui;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$NL6PHs6aDhfzvxPlGBUnXlLRajA implements SearchAdapterHelperDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$NL6PHs6aDhfzvxPlGBUnXlLRajA(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public /* synthetic */ SparseArray<User> getExcludeUsers() {
        return -CC.$default$getExcludeUsers(this);
    }

    public final void onDataSetChanged() {
        this.f$0.lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter();
    }

    public /* synthetic */ void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        -CC.$default$onSetHashtags(this, arrayList, hashMap);
    }
}
